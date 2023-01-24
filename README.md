# TessTest
Reproducer for Tesseract (in the bytedeco wrapper) crash under Linux OS (CentOS 8 Stream) with glibc version >=2.28-216.el8 when loading several languages and the first language contains `tessedit_load_sublangs` param. See [more details here](https://github.com/bytedeco/javacpp-presets/issues/1314).

##### Launch steps using Maven (assuming Linux OS):
1) Set environment variable TESSDATA_PREFIX:
```
export TESSDATA_PREFIX=<PATH_TO_TRAINEDDATA_DIR>
```
Example: `export TESSDATA_PREFIX=/tmp/tessdata`

2) Run project using the following maven command (assuming mvn is in your PATH):
```
mvn clean install exec:java
```
