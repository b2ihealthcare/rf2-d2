# RF2-D2  

[![build status](https://img.shields.io/travis/b2ihealthcare/snow-owl/7.x.svg?style=flat-square)](https://travis-ci.org/b2ihealthcare/snow-owl)
[![GitHub](https://img.shields.io/github/license/b2ihealthcare/snow-owl.svg?style=flat-square)](https://github.com/b2ihealthcare/snow-owl/blob/7.x/LICENSE)

# Introduction

RF2-D2 is an open source SNOMED CT release tool, which allows you to check, create and publish SNOMED CT Extension and Edition releases easily.

Feature include:
* Packaging
    * Packages a single RF2 Release from one or more RF2 files and other sources 
    * Configure and customize the name of the RF2 Release and RF2 files
    * Configure release structure and add additional content like custom files, documentation and resources (WIP) 
* Validation and verification
	* Verifies existing RF2 Release file names and structure
    * Validates SNOMED CT content using a number of validation rules (WIP)
* Diffing
    * Diff RF2 text files and releases (WIP)
* Filtering
    * Exclude certain parts from existing RF2 files (for example exclude all content from a module, etc.) (WIP)
* Replacements
    * Replaces values in RF2 files using conditions, simple statements and even external scripts (WIP)

# Download (soon!)

# First steps

After successfully downloading the RF2-D2 release package, run:

    unzip rf2-d2-0.1.0.zip
    cd rf2-d2-<version>
    bin/rf2 -v

It will return with the version of RF2-D2 and the supported RF2 version:

    RF2-D2 v0.1.0 @Copyright 2019 B2i Healthcare
    Supported RF2 Version: 20190131

Run the following command to get the supported list of subcommands and their descriptions:

    bin/rf2 -h

Run the following command to get help about a specific command:

    bin/rf2 help check

If you have questions or feedback, feel free to create an issue [here](https://github.com/b2ihealthcare/rf2-d2/issues/new) or [contact us](mailto:info@b2i.sg).
    
# Building from source

RF2-D2 uses Gradle for its build system. In order to create a distribution, simply run the following command in the cloned directory. 

    ./gradlew build runtimeZip

The distribution package can be found in the `build/rf2-d2-<version>.zip` folder, when the build is complete.

# License

This project is licensed under the Apache 2.0 License. See [LICENSE](LICENSE) for details.