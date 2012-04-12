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
      target in assembly <<= baseDirectory,
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

   lazy val customAssemblySettings: Seq[Project.Setting[_]] =
       inConfig( Mac )(    inTask( assembly )( jarName <<= name( _ + "-Mac.jar"   ))) ++
       inConfig( Linux )(  inTask( assembly )( jarName <<= name( _ + "-Linux.jar" )))
       inConfig( Windows )(inTask( assembly )( jarName <<= name( _ + "-Windows.jar" )))
       inConfig( Devel )  (inTask( assembly )( jarName <<= name( _ + ".jar" )))

   lazy val root = Project( id = "root", base = file( "." ),
      settings = Defaults.defaultSettings ++ assemblySettings ++ crossAssemblySettings ++ customAssemblySettings ++ Seq(
         name           := "SwingOSC",
         organization   := "de.sciss",
         version        := "0.70-SNAPSHOT",
         homepage       := Some( url( "https://github.com/Sciss/SwingOSC" )),
         description    := "An OpenSoundControl (OSC) server to dynamically instantiate and control Java objects. " +
                           "Its main application is a GUI library for SuperCollider.",
         scalaVersion   := "2.9.1",
         crossPaths     := false,

         // ---- assembly ----

         excludedJars in assembly <<= (fullClasspath in assembly) map {
            _.filter(_.data.getName == "jxbrowser_development_license_for_swingosc.jar")
         },

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

      def flatten( f: File, res: IndexedSeq[ (File, String) ] = IndexedSeq.empty ) : IndexedSeq[ (File, String) ] = {
         require( f.exists(), "Expected file not found: " + f )
         if( f.isDirectory ) {
            f.listFiles().foldLeft( res )( (fs, f) => flatten( f, fs ))
         } else if( f.getName != ".DS_Store" ) {
            res :+ (f, IO.relativize( baseDir, f ).getOrElse( sys.error( "Can't relativize path " + f )))
         } else res
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

      def compress( entries: Seq[ File ], platform: String, format: String = "zip" ) {
         val entriesWithNames = entries.flatMap( flatten( _ ))
         val targetFile = distDir / (name + "-" + version + "-" + platform + "." + format)
         log.info( "Packaging " + targetFile )
         format match {
            case "zip" => IO.zip( entriesWithNames, targetFile )
//            case "gz"  => IO.gzip()
            case _     => sys.error( "Unsupported packaging format '" + format + "'" )
         }
      }

      compress( common ++ macOnly,   "Mac" )
      compress( common ++ linuxOnly, "Linux" /*, "gz" */)
      compress( common ++ winOnly,   "Windows" )
   }
}