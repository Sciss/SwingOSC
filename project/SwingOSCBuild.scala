import annotation.tailrec
import sbt._
import Keys._
import sbtassembly.Plugin._
import AssemblyKeys._

object SwingOSCBuild extends Build {
   val Mac     = config( "mac" )     extend( Compile )
   val Linux   = config( "linux" )   extend( Compile )
   val Windows = config( "windows" ) extend( Compile )
   val Devel   = config( "devel" )   extend( Compile )

   lazy val crossAssemblySettings: Seq[Project.Setting[_]] = baseAssemblySettings ++ Seq(
//      target in assembly <<= baseDirectory,
//      jarName in assembly <<= name( _ + ".jar" ),
      mainClass in assembly := Some( "de.sciss.swingosc.SwingOSC" ),
      excludedFiles in assembly := {
         (bases: Seq[File]) =>
            bases.flatMap {
               base =>
                  (base / "META-INF" * "*").get collect {
                     case f if f.getName.toLowerCase.endsWith(".sf") => f
                     case f if f.getName.toLowerCase == "manifest.mf" => f
                  }
            }
      },
      assembleArtifact in packageScala := false, // no scala-library.jar
      assembleArtifact in packageSrc   := false  // no sources
   )

   private lazy val crossExcluded      = Set( "jxbrowser_development_license_for_swingosc.jar" )
   private lazy val notSafariExcluded  = Set( "jxbrowser_engine-webkit.jar" )
   private lazy val notMozillaExcluded = Set( "jxbrowser_engine-gecko.jar", "jxbrowser_MozillaInterfaces.jar",
                                              "jxbrowser_xulrunner-linux.jar", "jxbrowser_xulrunner-linux64.jar",
                                              "jxbrowser_xulrunner-mac.jar", "jxbrowser_xulrunner-windows.jar" )
   private lazy val notIEExcluded      = Set( "jxbrowser_engine-ie.jar" )
   private lazy val notMacExcluded     = Set( "jxbrowser_xulrunner-mac.jar" ) ++ notSafariExcluded
   private lazy val notLinuxExcluded   = notMozillaExcluded
   private lazy val notWindowsExcluded = Set( "jxbrowser_winpack-3.8.2.jar", "jxbrowser_xulrunner-windows.jar" ) ++ notIEExcluded
   private lazy val macExcluded        = notLinuxExcluded ++ notWindowsExcluded
   private lazy val linuxExcluded      = notMacExcluded ++ notWindowsExcluded
   private lazy val windowsExcluded    = notMacExcluded ++ notLinuxExcluded

   private def platformAssembly( config: Configuration, excl: Set[ String ] = Set.empty ) : Seq[sbt.Project.Setting[_]] = {
      val excl1 = excl ++ crossExcluded
      inConfig( config )( crossAssemblySettings ++ inTask( assembly ) {
         val s0 = Seq[Project.Setting[_]](
            jarName <<= name( _ + (if( config == Devel ) "" else "-" + config.name.capitalize) + ".jar" ),
            excludedJars <<= (fullClasspath in assembly) map { _.filter( e => excl1.contains( e.data.getName ))}
         )
         if( config == Devel ) {
            (target <<= baseDirectory) +: s0
         } else s0
      })
   }

   lazy val customAssemblySettings: Seq[Project.Setting[_]] =
      platformAssembly( Mac, macExcluded ) ++
      platformAssembly( Linux, linuxExcluded ) ++
      platformAssembly( Windows, windowsExcluded ) ++
      platformAssembly( Devel )

   lazy val root = Project( id = "root", base = file( "." ),
      settings = Defaults.defaultSettings ++ assemblySettings ++ /* crossAssemblySettings ++ */ customAssemblySettings ++ Seq(
         name           := "SwingOSC",
         organization   := "de.sciss",
         version        := "0.70-SNAPSHOT",
         homepage       := Some( url( "https://github.com/Sciss/SwingOSC" )),
         description    := "An OpenSoundControl (OSC) server to dynamically instantiate and control Java objects. " +
                           "Its main application is a GUI library for SuperCollider.",
         scalaVersion   := "2.9.1",
         crossPaths     := false,

         // ---- assembly ----

//         excludedJars in assembly <<= (fullClasspath in assembly) map {
//            _.filter(_.data.getName == "jxbrowser_development_license_for_swingosc.jar")
//         },

         // ---- distribution ----
         assemblyMac     <<= assembly in Mac,
         assemblyLinux   <<= assembly in Linux,
         assemblyWindows <<= assembly in Windows,
         assemblyDevel   <<= assembly in Devel,
         packageDist     <<= (assemblyMac, assemblyLinux, assemblyWindows, baseDirectory, name, version, streams) map
            packageDistTask
      )
   )

   lazy val packageDist       = TaskKey[Unit]("package-dist")
   lazy val assemblyMac       = TaskKey[File]("assembly-mac")
   lazy val assemblyLinux     = TaskKey[File]("assembly-linux")
   lazy val assemblyWindows   = TaskKey[File]("assembly-windows")
   lazy val assemblyDevel     = TaskKey[File]("assembly-devel")

   private def packageDistTask( macJarFile: File, linuxJarFile: File, windowsJarFile: File, baseDir: File,
                                name: String, version: String, streams: TaskStreams ) {
      import streams.log

      def flatten( platCfg: Configuration, f: File, res: IndexedSeq[ (File, String) ] = IndexedSeq.empty ) : IndexedSeq[ (File, String) ] = {
         require( f.exists(), "Expected file not found: " + f )
         if( f.isDirectory ) {
            f.listFiles().foldLeft( res )( (fs, f) => flatten( platCfg, f, fs ))
         } else {
            val fn = f.getName
            if( fn != ".DS_Store" ) {
               val platName   = "-" + platCfg.name.capitalize;
               val pi         = fn.indexOf( platName )
               val f1 = if( pi < 0 ) f else {   // remove -platName
                  new File( f.getParent, fn.substring( 0, pi ) + fn.substring( pi + platName.length ))
               }
               res :+ (f, IO.relativize( baseDir, f1 ).getOrElse( sys.error( "Can't relativize path " + f )))
            } else res
         }
      }

      val common = Seq(
         baseDir / "application.png",
         baseDir / "jmf",
         baseDir / "licenses",
         baseDir / "OSC-Command-Reference.html",
         baseDir / "PureData",
         baseDir / "readme.html",
         baseDir / "README.md",
         baseDir / "SuperCollider"
      )

      val macOnly = Seq(
         baseDir / "application.icns",
         baseDir / "SwingOSC_TCP.command",
         baseDir / "SwingOSC_UDP.command",
         macJarFile
      )

      val linuxOnly = Seq(
         baseDir / "debian",
         baseDir / "INSTALL_LINUX",
         baseDir / "install_linux_local.sh",
         baseDir / "install_linux_system.sh",
         baseDir / "SwingOSC_TCP.sh",
         baseDir / "SwingOSC_UDP.sh",
         linuxJarFile
      )

      val winOnly = Seq(
         baseDir / "SwingOSC_TCP.bat",
         baseDir / "SwingOSC_UDP.bat",
         windowsJarFile
      )

      val distDir = baseDir / "dist"
      distDir.mkdirs()

      def compress( entries: Seq[ File ], platCfg: Configuration, format: String = "zip" ) {
         val platName = platCfg.name.capitalize
         val entriesWithNames = entries.flatMap( flatten( platCfg, _ ))
         val targetFile = distDir / (name + "-" + version + "-" + platName + "." + format)
         log.info( "Packaging " + targetFile )
         format match {
            case "zip" => IO.zip( entriesWithNames, targetFile )
//            case "gz"  => IO.gzip()
            case _     => sys.error( "Unsupported packaging format '" + format + "'" )
         }
      }

      compress( common ++ macOnly,   Mac )
      compress( common ++ linuxOnly, Linux )
      compress( common ++ winOnly,   Windows )
   }
}