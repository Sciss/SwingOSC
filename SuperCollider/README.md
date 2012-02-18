## SwingOSC <sub>-- SuperCollider Classes --</sub>

### Updating

__Note:__ A common problem is a mismatch of the version of the Java server and the SuperCollider classes, because it was forgotten to remove the files of previous versions. In this case you should recognize a warning statement in SuperCollider's post window that reads `"SwingOSC version mismatch: client is v..., server is v...!"`.

### SuperCollider version

SwingOSC v0.66+ was successfully tested with SuperCollider 3.5 and should, if possible, not be used with older versions of SuperCollider.

### installation

The recommended installation of classes and help files is via a __symbolic link__. I don't know if there are symbolic link on Windows, you might need to move or copy the folders instead.

Assuming you are in a bash (shell/terminal), and your working directory is the main SwingOSC installation folder, the following should do the trick on __OS X__:

    EXT_DIR="${HOME}/Library/Application Support/SuperCollider/Extensions"
    mkdir -p "$EXT_DIR"
    ln -s "${PWD}/SuperCollider/SwingOSC" "$EXT_DIR"

Similarly on __Linux__:

    EXT_DIR="/usr/local/share/SuperCollider/Extensions"
    mkdir -p "$EXT_DIR"
    ln -s "${PWD}/SuperCollider/SwingOSC" "$EXT_DIR"
    
__Note__ that sometimes the extensions directory is `/usr/share/SuperCollider/Extensions`. (It has been reported that this is the case for `apt-get` on Ubuntu).

On Windows, locate the `SCClassLibrary` folder in the SuperCollider installation, and copy the directory `SuperCollider/SwingOSC` into that folder. (?)
    
To be able to __boot SwingOSC__ from within SuperCollider, copy the file `SwingOSC.jar` into the main SuperCollider application folder, or set `SwingOSC.program` in your SC startup file to point to the right install location. Details about this, and also the possibility to select a particular Java VM, are explained in the `SwingOSC` help file, which you can access from within SuperCollider once you have performed the first installation step.

### further readings

To get started, please refer to the file `examples/ReadMe-Examples.html`.

---------------------------------------
lastmod: 18-Feb-12
