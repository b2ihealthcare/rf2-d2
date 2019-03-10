# RF2-D2  

[![build status](https://img.shields.io/travis/b2ihealthcare/snow-owl/7.x.svg?style=flat-square)](https://travis-ci.org/b2ihealthcare/snow-owl)
[![GitHub](https://img.shields.io/github/license/b2ihealthcare/snow-owl.svg?style=flat-square)](https://github.com/b2ihealthcare/snow-owl/blob/7.x/LICENSE)

# Introduction

RF2-D2 is an open source SNOMED CT release tool, which allows you to check, create and publish SNOMED CT Extension and Edition releases easily.

Feature include:
* Packaging
    * Packages a single RF2 Release from one or more RF2 files and other sources (WIP) 
* Validation and verification
	* Verifies existing RF2 Release file names and structure
    * Validates SNOMED CT content using a number of validation rules (WIP)
* Filtering
    * Exclude certain parts from existing RF2 files (for example exclude all content from a module, etc.) (WIP)
* Replacements
    * Replaces values in RF2 files using conditions,simple statements and even external scripts (WIP)

# Download (soon!)

# Building from source

RF2-D2 uses Gradle for its build system. In order to create a distribution, simply run the following command in the cloned directory. 

    ./gradlew build runtimeZip

The distribution package can be found in the `build/rf2-d2-<version>.zip` folder, when the build is complete.

# License

This project is licensed under the Apache 2.0 License. See [LICENSE](LICENSE) for details.