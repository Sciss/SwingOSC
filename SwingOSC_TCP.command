#!/bin/sh

where=`dirname $0`
cd ${where}
java -Dswing.defaultlaf=com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel -Dapple.laf.useScreenMenuBar=true -Xmx512m -Xdock:icon=application.icns -Xdock:name=SwingOSC -jar target/SwingOSC.jar -t 57111 -L -h 127.0.0.1:57120
