#!/bin/sh

where=`dirname $0`
cd ${where}
java -Dapple.laf.useScreenMenuBar=true -Xmx512m -Xdock:icon=application.icns -Xdock:name=SwingOSC -jar SwingOSC.jar -t 57111 -L --nimbus -h 127.0.0.1:57120
