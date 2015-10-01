# Introduction

`platedecoder` decodes 2D DataMatrix bar codes present on specimen tubes in a
[n sized well plate](https://en.wikipedia.org/wiki/Microtiter_plate). Images containing the 2D bar
codes can come from the file system or a flatbed scanner.

It can decode different sized plates such as plates containing 96, 81, 100 and 144 wells. The 2D bar
code can be present on the top or bottoms of the tubes.

It can also send the decoded bar codes to an [OpenSpecimen](http://openspecimen.org/) server to link
tubes to patients or positions.


## Development

This project use [Gradle](https://gradle.org/) as the build tool. You will need to intsall it to
build this application.

To build the project type the following at the project's root directory:

```bash
gradle  build
```

To run the application, type:

```bash
gradle  run
```

### Emacs

To build from Emacs type: `TERM="dumb" gradle build`
