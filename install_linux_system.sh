#!/bin/sh

mkdir -p $1/share/SuperCollider/Extensions
cp -a SuperCollider/SwingOSC $1/share/SuperCollider/Extensions
mkdir $1/share/SuperCollider/SwingOSC
cp -a SuperCollider/examples $1/share/SuperCollider/SwingOSC

cp SwingOSC.jar $1/bin/
