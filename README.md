# Introduction

`platedecoder` decodes 2D DataMatrix bar codes present on specimen
tubes in a
[n sized well plate](https://en.wikipedia.org/wiki/Microtiter_plate).
Images containing the 2D bar codes can come from the file system or a
flatbed scanner.

It can decode different sized plates such as plates containing 96, 81,
100 and 144 wells. The 2D bar code can be present on the top or
bottoms of the tubes.

It can also send the decoded bar codes to an
[OpenSpecimen](http://openspecimen.org/) server to link tubes to
patients or positions.

## Run

A distributatble package can be built by using the following command:

```sh
gradle distZip
```

When the command completes, the directory `build/distributions` will
contains ZIP file. Unzip this file to a location where you'd like to
install it and then run it by using the Linux script or the MS Windows
batch file present in the `bin` directory. The ZIP file comes bundled
with the required libraries.

### MS Windows

To use the app in MS Windows, you will first have to install the
[Microsoft Visual C++ 2010 SP1 Redistributable Package (x86)](http://www.microsoft.com/en-us/download/details.aspx?id=8328).
This only needs to be done once per computer.

## Development

### Maven repository

This app uses a custom maven repository to retrieve it's dependant
libraries. This repository is hosted on GitHub at:

```sh
https://github.com/cbsrbiobank/biobank-maven-repo/
```

### Build Environment

This project uses [Gradle](https://gradle.org/) as the build tool. You
will need to intsall it to build this application.

To build the project type the following at the project's root directory:

```sh
gradle build
```

To run the application, type:

```sh
gradle run
```

To generate the Javadoc.

```sh
gradle -q --console=plain javadoc
```

To build an installation, type:

```sh
gradle installDist
```

Then copy the DLLs (or Linux shared libraries) to `build/install/platedecoder/lib`.
The `build/install/platedecoder` .

The fat jar is placed in the `build/libs` directory.

See also `gradle distZip`.

#### Future

See [launch4j](http://launch4j.sourceforge.net/docs.html) for how to generate a Windows EXE file.

### Emacs

To build from Emacs type: `TERM="dumb" gradle build` or `gradle
--console=plain build`.

#### Eclim

Use the [Graphical Installer](http://eclim.org/install.html#installer)
to install Eclim.

For tests to run, the native library folder has to be added to the JVM
parameters as follows:

```
<path_to_eclim>/eclim -command project_setting -p platedecoder -s org.eclim.java.junit.jvmargs -v \[\"-Ddebug=true\",\"-Djava.library.path=./lib\"\]
```

The path to Eclim for me is:
```
/home/nelson/.eclipse/org.eclipse.platform_4.5.0_1473617060_linux_gtk_x86_64
```
