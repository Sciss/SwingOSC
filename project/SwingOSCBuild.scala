import annotation.tailrec
import sbt._
import Keys._
import sbtassembly.Plugin._
import AssemblyKeys._

object SwingOSCBuild extends Build {
   lazy val root = Project(id = "root", base = file("."),
      settings = Defaults.defaultSettings ++ assemblySettings ++ Seq(
         name := "SwingOSC",
         organization := "de.sciss",
         version := "0.70-SNAPSHOT",
         homepage := Some(url("https://github.com/Sciss/SwingOSC")),
         description := "An OpenSoundControl (OSC) server to dynamically instantiate and control Java objects. " +
            "Its main application is a GUI library for SuperCollider.",
         scalaVersion := "2.9.1",
         crossPaths := false,
         //         resolvers       += Resolver.url( "sbt-plugin-releases",
         //            url( "http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/" ))( Resolver.ivyStylePatterns ),

         // ---- assembly ----

         target in assembly <<= baseDirectory,
         jarName in assembly <<= name( _ + ".jar" ), // i.e. output to baseDir/SwingOSC.jar
         assembleArtifact in packageScala := false, // no scala-library.jar
         assembleArtifact in packageSrc := false, // no sources
         mainClass in assembly := Some("de.sciss.swingosc.SwingOSC"),
         excludedJars in assembly <<= (fullClasspath in assembly) map {
            _.filter(_.data.getName == "jxbrowser_development_license_for_swingosc.jar")
         },
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

         // ---- distribution ----

         packageDist <<= (assembly, baseDirectory, name, version, streams) map packageDistTask
      )
   )

   lazy val packageDist = TaskKey[Unit]("package-dist")

   private def packageDistTask( jarFile: File, baseDir: File, name: String, version: String, streams: TaskStreams ) {
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
         baseDir / "SuperCollider",
         jarFile
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