![logo](http://sciss.de/swingOSC/application.png)

## SwingOSC

### statement

SwingOSC is (C)opyright by 2005-2012 Hanns Holger. All rights reserved. It is released under the [GNU General Public License](http://github.com/Sciss/SwingOSC/blob/master/licenses/SwingOSC-License.txt).

The 'readme' is currently under re-writing. For the time being, also see the old `readme.html` for more information about SwingOSC.

This is a snapshot of version 0.70 which aims to be compatible with SuperCollider 3.5.

In progress:

 - the tablet view classes are currently disabled
 - the webview, which was experimentally based on LoboBrowser, is now driven by [JXBrowser](http://www.teamdev.com/jxbrowser/) from TeamDev who kindly provide free licenses for open source projects.
 - we are switching look-and-feel to Nimbus on all platforms. This means that you should use Oracle's Java 6 on Linux. If you prefer OpenJDK, make sure you use OpenJDK 7, as OpenJDK 6 does _not_ include Nimbus. The OS X port of Java 6 _does_ include Nimbus.
 - the reason for this is to have consistent look for the custom widgets and known focus borders which are accounted for the in SuperCollider classes.

The project now includes all the JXBrowser library components. In `lib` you find two license keys `jxbrowser_development_license_for_swingosc.jar` and `jxbrowser_runtime_license_for_swingosc.jar`. If creating a binary artifact of SwingOSC, only the runtime key may be distributed, and the development key must not be included.

_Note:_ Although JXBrowser claims to be able to use Mozilla on all operating systems, we have found that this does not work properly on OS X (even in 32-bit mode). Thus we use the default browser component on each platform, which is Safari/WebKit on OS X, Mozilla/Gecko on Linux, and Internet Explorer on Windows. The web view can be tested, by executing `GUI.swing; Help.gui` in SuperCollider.

### requirements / installation

SwingOSC now builds with the [Simple Build Tool](https://github.com/harrah/xsbt/wiki). Includes is a little helper bash script `sbt` which will download the Simple Build Tool (sbt) launcher to the SwingOSC directory if not yet present, and then invoke it.

You can also install sbt as explained on the sbt website, that is download the bootstrap jar version 0.11.2 and create a shell script `sbt` in your preferred `PATH`, e.g. as `~/bin/sbt`. You will need to perform this step on Windows.

To compile and assemble SwingOSC, run `./sbt assembly`. The final fully self-contained jar file will be `SwingOSC.jar` in the base directory. Make sure you adjust your `SwingOSC.program` settings in SuperCollider accordingly.

To package distribution zip files in the `dist` directory, run `./sbt package-dist`.

### creating an IntelliJ IDEA project

To develop the Java sources of SwingOSC, you can create an IntelliJ IDEA project file. If you haven't globally installed the sbt-idea plugin yet, create the following contents in `~/.sbt/plugins/build.sbt`:

    resolvers += "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"
    
    addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.0.0")

Then to create the IDEA project, run the following two commands from the xsbt shell:

    > set ideaProjectName := "SwingOSC"
    > gen-idea

### download

The current version can be downloaded from [github.com/Sciss/SwingOSC](http://github.com/Sciss/SwingOSC).
