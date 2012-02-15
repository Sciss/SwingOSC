## SwingOSC

### statement

SwingOSC is (C)opyright by 2015-2012 Hanns Holger. All rights reserved. It is released under the [GNU General Public License](http://github.com/Sciss/SwingOSC/blob/master/licenses/SwingOSC-License.txt). 

This is an __experimental__ v0.70-SNAPSHOT 'revamp' branch. See the old `readme.html` for more information about SwingOSC.

In progress:

 - the tablet view classes are currently disabled
 - the webview was experimentally based on LoboBrowser, but that doesn't fully work with the DOM demands of the new SC help system
 - we are planning to replace LoboBrowser by JXBrowser from TeamDev who kindly provide free licenses for open source projects
 - we are switching look-and-feel to Nimbus on all platforms. This means that you should use Oracle's Java 6 on Linux. If you prefer OpenJDK, make sure you use OpenJDK 7, as OpenJDK 6 does _not_ include Nimbus. The OS X port of Java 6 _does_ include Nimbus.
 - the reason for this is to have consistent look for the custom widgets and known focus borders which are accounted for the in SuperCollider classes.

The project now includes all the JXBrowser library components. The license keys are _not yet checked in_, because I need to find out if both development and runtime keys should go into git or not. While you can compile the project without problems, to use the web view component, you must downlod the [JXBrowser for evaluation](http://www.teamdev.com/jxbrowser/) and put the 30-days evaluation key (`lib/license.jar`) on the classpath, until this is sorted out.

### requirements / installation

SwingOSC now builds with the [Simple Build Tool](https://github.com/harrah/xsbt/wiki). Download the bootstrap jar version 0.11.2 from the sbt website, and create a shell script `sbt` in your preferred `PATH`, e.g. as `~/bin/sbt`.

`sbt assembly` should compile the project and create the fully self contained jar file in `target/SwingOSC-assembly-0.70-SNAPSHOT.jar`.

### creating an IntelliJ IDEA project

To develop the Java sources of SwingOSC, you can create an IntelliJ IDEA project file. If you haven't globally installed the sbt-idea plugin yet, create the following contents in `~/.sbt/plugins/build.sbt`:

    resolvers += "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"
    
    addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.0.0")

Then to create the IDEA project, run the following two commands from the xsbt shell:

    > set ideaProjectName := "SwingOSC"
    > gen-idea

### download

The current version can be downloaded from [github.com/Sciss/SwingOSC](http://github.com/Sciss/SwingOSC).
