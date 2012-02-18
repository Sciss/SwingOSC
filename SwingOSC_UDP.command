#!/bin/sh

where=`dirname $0`
cd ${where}
java -Dswing.defaultlaf=com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel -Xmx512m -Dapple.laf.useScreenMenuBar=true -Xdock:icon=application.icns -Xdock:name=SwingOSC -jar SwingOSC.jar -u 57111 -L -h 127.0.0.1:57120
