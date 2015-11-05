# Introduction

`platedecoder` decodes 2D DataMatrix bar codes present on specimen tubes in a
[n sized well plate](https://en.wikipedia.org/wiki/Microtiter_plate). Images containing the 2D bar
codes can come from the file system or a flatbed scanner.

It can decode different sized plates such as plates containing 96, 81, 100 and 144 wells. The 2D bar
code can be present on the top or bottoms of the tubes.

It can also send the decoded bar codes to an [OpenSpecimen](http://openspecimen.org/) server to link
tubes to patients or positions.

## Run

To run a fat jar, use the following command:

```bash
java -Djava.library.path=./lib -cp platedecoder-all-0.1-SNAPSHOT.jar org.biobank.platedecoder.ui.PlateDecoder
```

Note that the `lib` folder must contain the scanning library DLL (or shared library for Linux).
These libraries can be downloaded from
[here](http://aicml-med.cs.ualberta.ca/CBSR/plate_decoders_libs/).

## Development

### Build Environment

This project uses [Gradle](https://gradle.org/) as the build tool. You will need to intsall it to
build this application.

To build the project type the following at the project's root directory:

```bash
gradle build
```

To run the application, type:

```bash
gradle run
```

To generate the Javadoc.

```bash
gradle -q --console=plain javadoc
```

To build an installation, type:

```bash
gradle installDist
```

Then copy the DLLs (or Linux shared libraries) to `build/install/platedecoder/lib`.
The `build/install/platedecoder` .

The fat jar is placed in the `build/libs` directory.

See also `gradle distZip`.

### Windows executable

Use `gradle installDist` to create an installation. The `dmscanlib.dll` file goes in the `lib`
folder. The other DLLs go in the root folder. Required DLLs are:

* libglog.dll
* opencv_core248.dll
* opencv_highgui248.dll
* opencv_imgproc248.dll
* OpenThreadsWin32.dll

### Linux required libraries

Use the following command to install the required libraries to run
this application on Linux.

```bash
sudo apt-get install libdmtx-dev libopenthreads-dev libgoogle-glog-dev \
libgtest-dev libgflags-dev libopencv-dev libconfig++-dev
```

#### Future

See [launch4j](http://launch4j.sourceforge.net/docs.html) for how to generate a Windows EXE file.

### Emacs

To build from Emacs type: `TERM="dumb" gradle build` or `gradle --console=plain build`.

#### Eclim

Use the [Graphical Installer](http://eclim.org/install.html#installer) to install Eclim.

For tests to run, the native library folder has to be added to the JVM parameters as follows:

```
<path_to_eclim>/eclim -command project_setting -p platedecoder -s org.eclim.java.junit.jvmargs -v \[\"-Djava.library.path=./lib\"\]
```

The path to Eclim for me is:
```
/home/nelson/.eclipse/org.eclipse.platform_4.5.0_1473617060_linux_gtk_x86_64
```
