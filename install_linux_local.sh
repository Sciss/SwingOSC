#!/bin/sh

mkdir -p ~/share/SuperCollider/Extensions
cp -a SuperCollider/SwingOSC ~/share/SuperCollider/Extensions
mkdir ~/share/SuperCollider/SwingOSC
cp -a SuperCollider/examples ~/share/SuperCollider/SwingOSC

mkdir -p ~/share/bin/
cp SwingOSC.jar ~/share/bin/
