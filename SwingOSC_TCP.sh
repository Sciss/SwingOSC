#!/bin/sh

where=`dirname $0`
cd ${where}
java -Xmx512m -jar SwingOSC.jar -t 57111 -L --nimbus -h 127.0.0.1:57120
