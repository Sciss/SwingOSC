import AssemblyKeys._

name := "SwingOSC"

organization := "de.sciss"

version := "0.70-SNAPSHOT"

homepage := Some( url( "https://github.com/Sciss/SwingOSC" ))

description := "An OpenSoundControl (OSC) server to dynamically instantiate and control Java objects. Its main application is a GUI library for SuperCollider."

scalaVersion := "2.9.1"

crossPaths := false   // currently no scala involved, hence remove that from the artifact names

// ---- assembly packaging ----

seq( assemblySettings: _* )

jarName in assembly <<= name( _ + ".jar" )  // i.e. output to target/SwingOSC.jar

assembleArtifact in packageScala := false   // no scala-library.jar

// assembleArtifact in packageBin := false     // no sources

assembleArtifact in packageSrc := false     // no sources

mainClass in assembly := Some( "de.sciss.swingosc.SwingOSC" )

excludedFiles in assembly := { (bases: Seq[File]) =>
   bases flatMap { base =>
      (base / "META-INF" * "*").get collect {
         case f if f.getName.toLowerCase.endsWith( ".sf" ) => f
         case f if f.getName.toLowerCase == "manifest.mf"  => f
      }
   }
}