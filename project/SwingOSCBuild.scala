import sbt._
import Keys._
import sbtassembly.Plugin._
import AssemblyKeys._

object SwingOSCBuild extends Build {
   lazy val swingosc = Project(
      id           = "swingosc",
      base         = file( "." ),
      settings     = Defaults.defaultSettings ++ assemblySettings ++ Seq(
         name            := "SwingOSC",
         organization    := "de.sciss",
         version         := "0.70-SNAPSHOT",
         homepage        := Some( url( "https://github.com/Sciss/SwingOSC" )),
         description     := "An OpenSoundControl (OSC) server to dynamically instantiate and control Java objects. " +
                            "Its main application is a GUI library for SuperCollider.",
         scalaVersion    := "2.9.1",
         crossPaths      := false,
//         resolvers       += Resolver.url( "sbt-plugin-releases",
//            url( "http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/" ))( Resolver.ivyStylePatterns ),

         // ---- assembly ----

         jarName in assembly <<= name( _ + ".jar" ),  // i.e. output to target/SwingOSC.jar
         assembleArtifact in packageScala := false,   // no scala-library.jar
         assembleArtifact in packageSrc := false,     // no sources
         mainClass in assembly := Some( "de.sciss.swingosc.SwingOSC" ),
         excludedJars in assembly <<= (fullClasspath in assembly) map {
            _.filter( _.data.getName == "jxbrowser_development_license_for_swingosc.jar" )
         },
         excludedFiles in assembly := { (bases: Seq[File]) =>
            bases.flatMap { base =>
               (base / "META-INF" * "*").get collect {
                  case f if f.getName.toLowerCase.endsWith( ".sf" ) => f
                  case f if f.getName.toLowerCase == "manifest.mf"  => f
               }
            }
         }
      )
   )
}