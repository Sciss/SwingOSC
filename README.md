![logo](http://sciss.de/swingOSC/application.png)

## SwingOSC

### statement

SwingOSC is an OpenSoundControl (OSC) server intended for scripting Java(tm), such as to create graphical user interfaces with AWT or Swing classes. It uses the reflection and beans mechanism to dynamically create instances of java classes and control them. A separate set of SuperCollider language classes is included to allow the building of GUIs from within sclang.

SwingOSC is (C)opyright by 2005-2012 Hanns Holger. All rights reserved. It is released under the [GNU General Public License](http://github.com/Sciss/SwingOSC/blob/master/licenses/SwingOSC-License.txt).

SwingOSC version 0.70 is intended to be used with SuperCollider 3.5.1 or higher.

### security note

SwingOSC uses UDP and TCP network protocols. It allows to create and execute almost any kind of java code on your machine. Therefore, running SwingOSC in a network that can be accessed from outside is a __severe security problem__, allowing hijacking, information retrieval and massive damage on your machine. You have been warned!

It is strongly advised to launch SwingOSC with the __-L option__ which limits communication to the local computer. Alternatively, make sure your firewall settings are appropriate.

### installation

SwingOSC is written in Java and requires a Java runtime environment (JRE) version 1.6 or better. On Mac OS X you already have this runtime. On other platforms you may need to download and install a recent runtime. SwingOSC should work with [Oracle Java](http://java.com) or [OpenJDK](http://openjdk.java.net/). You can verify your current Java version by opening a terminal and executing the command `java -version`.

__Linux note:__ There are other free implementations for the Java standard platform, like Apache Harmony and GNU Classpath. However, most of them are not suitable for GUI applications since their AWT/Swing is incomplete or buggy. Note that you can have more than one java runtime installed (see [nescivi's posting](www.nabble.com/swingOSC-installation-on-Linux-Ubuntu-7.04-t4638827.html) for details).

__Installing SuperCollider classes:__ please refer to the separate file `SuperCollider/README.md`.

### launching the server

__Note__: Typically you will launch SwingOSC from sclang, using `SwingOSC.default.boot`. The following section describes how to launch SwingOSC from a terminal and the types of switches accepted.

Open a terminal and `cd` into the SwingOSC folder. Either run the default script by typing `sh SwingOSC_TCP.ext` (where `ext` is `.command` on OS X, `.sh` on Linux, and `.bat` on Windows), or launch with custom options:

    > java [java-VM-options] -jar SwingOSC.jar [swing-osc-options]

where the VM options are:

 - `-Dswing.defaultlaf=<LookAndFeelClassName>` (not recommended): specifies a custom look-and-feel class. e.g. `com.birosoft.liquid.LiquidLookAndFeel`. Note that the widgets are now optimised for Nimbus, so it is recommended to just use the `-nimbus` switch instead.
 - `-Xdock:icon=application.icns` : (Mac OS only) uses a prettier icon for display in the dock and programme switching screen.
 - -`Xdock:name=SwingOSC` : (Mac OS only) uses an alternative name in the screen bar and dock

and the SwingOSC options are:

 - `-t <port>` (recommended) : uses the given TCP port for message reception. The SuperCollider classes assume that you use __port 57111__!
 - `-u <port>` : uses the given UDP port for message reception. if absent, an arbitrary free port will be picked.
 - `-L` (recommended) : uses loopback address ("127.0.0.1") for communication. if absent, the local host's IP address is used. when your computer is connected to a network and SwingOSC needs only be accessed from the local computer, make sure you use this option to minimize the security issue mentioned above.
 - `-i` : runs Swing initialization upon startup. On Mac OS, a terminal app becomes a GUI app with screen menu bar and icon in the Dock, as soon as an AWT or Swing component is created. This initialization can be enforced by using this option.
 - `-h <host:port>` (recommended) : sends a `[ /swing, "hello", <swingHost>, <swingPort>, <swingProtocol> ]` message to the given UDP socket. this kind of manual "bonjour" is used by the SuperCollider classes to detect the startup of the SwingOSC server.
 - `-nimbus` (recommended) : enforces the use of the Nimbus look and feel. Requires Apple Java 6, Oracle Java 6, or OpenJDK 7.

If you wish to include custom java classes or libraries, you can either

 - use the `/classes` OSC command (see OSC reference for details).
 - include them in the `SwingOSC.jar` file
 - add them to the java class path, as shown in the following example which adds the freetts speech libraries (assuming they have been copied to the `lib` folder):

    > java -cp SwingOSC.jar:lib/freetts.jar:lib/jsapi.jar de.sciss.swingosc.SwingOSC [swing-osc-options]

### compilation from source

SwingOSC now builds with the [Simple Build Tool](https://github.com/harrah/xsbt/wiki). Includes is a little helper bash script `sbt` which will download the Simple Build Tool (sbt) launcher to the SwingOSC directory if not yet present, and then invoke it.

You can also install sbt as explained on the sbt website, that is download the bootstrap jar version 0.11.2 and create a shell script `sbt` in your preferred `PATH`, e.g. as `~/bin/sbt`. You will need to perform this step on Windows.

To compile and assemble SwingOSC, run `./sbt assembly-devel`. The final fully self-contained jar file will be `SwingOSC.jar` in the base directory. Make sure you adjust your `SwingOSC.program` settings in SuperCollider accordingly.

To package distribution zip files in the `dist` directory, run `./sbt package-dist`.

The project now includes all the JXBrowser library components. In `lib` you find two license keys `jxbrowser_development_license_for_swingosc.jar` and `jxbrowser_runtime_license_for_swingosc.jar`. If creating a binary artifact of SwingOSC, only the runtime key may be distributed, and the development key must not be included.

### creating an IntelliJ IDEA project

To develop the Java sources of SwingOSC, you can create an IntelliJ IDEA project file. If you haven't globally installed the sbt-idea plugin yet, create the following contents in `~/.sbt/plugins/build.sbt`:

    resolvers += "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"
    
    addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.0.0")

Then to create the IDEA project, run the following two commands from the xsbt shell:

    > set ideaProjectName := "SwingOSC"
    > gen-idea

### download

 - The binaries can be downloaded from [sourceforge.net/projects/swingosc](http://sourceforge.net/projects/swingosc/).
 - The source code and issue tracker can be found at [github.com/Sciss/SwingOSC](http://github.com/Sciss/SwingOSC).
