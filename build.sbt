val Mac     = config("mac"    ) extend Compile
val Linux   = config("linux"  ) extend Compile
val Windows = config("windows") extend Compile
val Devel   = config("devel"  ) // extend Compile

lazy val crossAssemblySettings = Seq(
  mainClass         in assembly   := Some("de.sciss.swingosc.SwingOSC"),
  assembleArtifact  in packageSrc := false  // no sources
)

lazy val crossExcluded      = Set("jxbrowser_development_license_for_swingosc.jar")
lazy val notSafariExcluded  = Set("jxbrowser_engine-webkit.jar")
lazy val notMozillaExcluded = Set("jxbrowser_engine-gecko.jar", "jxbrowser_MozillaInterfaces.jar",
  "jxbrowser_xulrunner-linux.jar", "jxbrowser_xulrunner-linux64.jar",
  "jxbrowser_xulrunner-mac.jar", "jxbrowser_xulrunner-windows.jar")
lazy val notIEExcluded      = Set("jxbrowser_engine-ie.jar")
lazy val notMacExcluded     = Set("jxbrowser_xulrunner-mac.jar") ++ notSafariExcluded
lazy val notLinuxExcluded   = notMozillaExcluded
lazy val notWindowsExcluded = Set("jxbrowser_winpack-3.8.2.jar", "jxbrowser_xulrunner-windows.jar") ++ notIEExcluded
lazy val macExcluded        = notLinuxExcluded ++ notWindowsExcluded ++ crossExcluded
lazy val linuxExcluded      = notMacExcluded   ++ notWindowsExcluded ++ crossExcluded
lazy val windowsExcluded    = notMacExcluded   ++ notLinuxExcluded   ++ crossExcluded
lazy val develExcluded      = Set("jxbrowser_runtime_license_for_swingosc.jar")

def platformAssembly(config: Configuration, excl: Set[String]) =
  inConfig(config)(baseAssemblySettings ++ crossAssemblySettings ++ inTask(assembly) {
    val s0 = Seq(
      assemblyJarName := {
        val n = name.value
        val suffix = if (config == Devel) "" else s"-${config.name.capitalize}"
        s"$n$suffix.jar"
      },
      assemblyExcludedJars in assembly := {
        val cp = (fullClasspath in assembly).value
        cp.filter { e =>
          val n   = e.data.getName
          val res = excl.contains(n)
          // println(s"contains '$n'? $res") // WTF sbt -- this is never called
          res
        }
      }
    )
    if (config != Devel) s0 else (target := baseDirectory.value) +: s0
  })

lazy val customAssemblySettings =
  platformAssembly(Mac    , macExcluded    ) ++
  platformAssembly(Linux  , linuxExcluded  ) ++
  platformAssembly(Windows, windowsExcluded) ++
  platformAssembly(Devel  , develExcluded  )

lazy val root = Project(id = "root", base = file("."))
  .settings(customAssemblySettings)
  .settings(
    name              := "SwingOSC",
    organization      := "de.sciss",
    version           := "0.71.0-SNAPSHOT",
    homepage          := Some(url("https://github.com/Sciss/SwingOSC")),
    description       := "An OpenSoundControl (OSC) server to dynamically instantiate and control Java objects. " +
      "Its main application is a GUI library for SuperCollider.",
    scalaVersion      := "2.11.7",
    autoScalaLibrary  := false,
    crossPaths        := false,

    // ---- assembly ----

//    // WORK AROUND FOR SBT FAILING TO PICK UP CONFIG SPECIFIC SETTINGS
//    assemblyExcludedJars in assembly <<= (fullClasspath in assembly) map {
//      _.filter(_.data.getName == "jxbrowser_development_license_for_swingosc.jar")
//    },

    // ---- distribution ----
    assemblyMac     <<= assembly in Mac,
    assemblyLinux   <<= assembly in Linux,
    assemblyWindows <<= assembly in Windows,
    assemblyDevel   <<= assembly in Devel,
    packageDist     <<= (assemblyMac, assemblyLinux, assemblyWindows, baseDirectory, name, version, streams) map
      packageDistTask
  )

lazy val packageDist       = TaskKey[Unit]("package-dist")
lazy val assemblyMac       = TaskKey[File]("assembly-mac")
lazy val assemblyLinux     = TaskKey[File]("assembly-linux")
lazy val assemblyWindows   = TaskKey[File]("assembly-windows")
lazy val assemblyDevel     = TaskKey[File]("assembly-devel")

def packageDistTask(macJarFile: File, linuxJarFile: File, windowsJarFile: File, baseDir: File,
                    name: String, version: String, streams: TaskStreams): Unit = {
  def flatten(f: File, res: IndexedSeq[(File, String)] = IndexedSeq.empty): IndexedSeq[(File, String)] = {
    require(f.exists(), s"Expected file not found: $f")
    if (f.isDirectory) {
      f.listFiles().foldLeft(res)((fs, f) => flatten(f, fs))
    } else if (f.getName != ".DS_Store") {
      res :+ (f, IO.relativize(baseDir, f).getOrElse(sys.error(s"Can't relativize path $f")))
    } else res
  }

  val common = Seq(
    baseDir / "application.png",
    baseDir / "jmf",
    baseDir / "licenses",
    baseDir / "OSC-Command-Reference.html",
    baseDir / "PureData",
    baseDir / "README.md",
    baseDir / "CHANGES.md",
    baseDir / "SuperCollider"
  )

  val macOnly = Seq(
    baseDir / "application.icns",
    baseDir / "SwingOSC_TCP.command",
    baseDir / "SwingOSC_UDP.command"
  )

  val linuxOnly = Seq(
    baseDir / "debian",
    baseDir / "INSTALL_LINUX",
    baseDir / "install_linux_local.sh",
    baseDir / "install_linux_system.sh",
    baseDir / "SwingOSC_TCP.sh",
    baseDir / "SwingOSC_UDP.sh"
  )

  val winOnly = Seq(
    baseDir / "SwingOSC_TCP.bat",
    baseDir / "SwingOSC_UDP.bat"
  )

   val distDir = baseDir / "dist"
   distDir.mkdirs()

  def compress(entries: Seq[File], jarFile: File, platCfg: Configuration, format: String = "zip"): Unit = {
    val platName          = platCfg.name.capitalize
    val entriesWithNames  = entries.flatMap(flatten(_)) :+ (jarFile, "SwingOSC.jar")
    val targetFile        = distDir / s"$name-$version-$platName.$format"
    streams.log.info(s"Packaging $targetFile")
    format match {
      case "zip"  => IO.zip(entriesWithNames, targetFile)
      case _      => sys.error(s"Unsupported packaging format '$format'")
    }
  }

  compress(common ++ macOnly  , macJarFile    , Mac    )
  compress(common ++ linuxOnly, linuxJarFile  , Linux  )
  compress(common ++ winOnly  , windowsJarFile, Windows)
}
