# Introduction

`platedecoder` decodes 2D DataMatrix bar codes present on specimen tubes in a
[n sized well plate](https://en.wikipedia.org/wiki/Microtiter_plate). Images containing the 2D bar
codes can come from the file system or a flatbed scanner.

It can decode different sized plates such as plates containing 96, 81, 100 and 144 wells. The 2D bar
code can be present on the top or bottoms of the tubes.

It can also send the decoded bar codes to an [OpenSpecimen](http://openspecimen.org/) server to link
tubes to patients or positions.
