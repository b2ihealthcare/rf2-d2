# Introduction

RF2-D2 is an open source SNOMED CT release tool, which allows you to check, create and publish SNOMED CT Extension and Edition releases with care.

[![build status](https://img.shields.io/travis/b2ihealthcare/rf2-d2/master.svg?style=flat-square)](https://travis-ci.org/b2ihealthcare/rf2-d2)
[![latest release](https://img.shields.io/github/tag/b2ihealthcare/rf2-d2.svg?style=flat-square)](https://github.com/b2ihealthcare/rf2-d2/releases/tag/v0.3.3)
[![downloads](https://img.shields.io/github/downloads/b2ihealthcare/rf2-d2/total.svg?style=flat-square)](https://github.com/b2ihealthcare/rf2-d2/releases/)
[![GitHub](https://img.shields.io/github/license/b2ihealthcare/rf2-d2.svg?style=flat-square)](https://github.com/b2ihealthcare/rf2-d2/blob/master/LICENSE)

Feature include:
* Packaging (see `create` command)
    * Packages a single RF2 Release from one or more RF2 files and other sources 
    * Configure and customize the name of the RF2 Release and RF2 files
    * Configure release structure and add additional content like custom files, documentation and resources
* Validation and verification (see `check` command)
	* Verifies existing RF2 Release file names and structure
    * Validates SNOMED CT content using a number of validation rules (WIP)
* Filtering (see `transform` command)
    * Exclude certain parts from existing RF2 files (for example exclude all content from a module, etc.)
* Replacements (see `transform` command)
    * Replaces values in RF2 files using conditions, simple statements and even external scripts
* Diffing
    * Diff RF2 text files and releases (WIP)

# Download

* [WINDOWS](https://github.com/b2ihealthcare/rf2-d2/releases/download/v0.3.3/rf2-d2-0.3.3-win-x64.zip)
* [LINUX](https://github.com/b2ihealthcare/rf2-d2/releases/download/v0.3.3/rf2-d2-0.3.3-linux-x64.zip)
* [MACOS](https://github.com/b2ihealthcare/rf2-d2/releases/download/v0.3.3/rf2-d2-0.3.3-osx-x64.zip)

# First steps

After successfully downloading the appropriate RF2-D2 release package for your operating system, run:

    unzip rf2-d2-0.3.3-linux-64.zip
    cd rf2-d2-linux-64/bin
    ./rf2 -v

It will return with the version of RF2-D2 and the supported RF2 version:

    RF2-D2 v0.3.3 @Copyright 2019 B2i Healthcare
    Supported RF2 Version: 20190131

Run the following command to get the supported list of subcommands and their descriptions:

    ./rf2 -h

Run the following command to get help about a specific command:

    ./rf2 help check

You can add the `rf2-d2-<version>/bin` folder to your PATH, so it will be available globally and can be invoked with command `rf2`. 
If you have questions or feedback, feel free to create an issue [here](https://github.com/b2ihealthcare/rf2-d2/issues/new) or [contact us](mailto:info@b2i.sg).

# Examples

Verify RF2 release with the following command:

    rf2 check SnomedCT_RF2_PRODUCTION_20190131T120000Z.zip
    
It will print the available release file structure in tree form along with their properties and issues.  

Generate empty RF2 Release with official SNOMED International file structure and naming (the output file will be placed in the default output directory `target`, relative to the current working directory):

    rf2 create

Generate a new RF2 Release based on the previous RF2 Release and a new Delta (or Snapshot) to a specified `OUTDIR_PATH`:

    rf2 create -d 20190131 -o OUTDIR_PATH SnomedCT_PreviousRF2_PRODUCTION_20180731T120000Z.zip SnomedCT_NewDeltaRF2_PRODUCTION_20190131T120000Z.zip

_NOTE: the `-d 20190131` defines the `releaseDate` of the RF2 Release and it is also being used to select the content for the resulting Delta RF2 files._

Generate an RF2 Delta Release from an RF2 Full Release:

    rf2 create -d 20190131 -o OUTDIR_PATH -C Delta SnomedCT_RF2_PRODUCTION_20190131T120000Z.zip
    
Generate an RF2 Release from multiple RF2 sources:

    rf2 create -d 20190201 -o OUTDIR_PATH SnomedCT_InternationalRF2_PRODUCTION_20190131T120000Z.zip SnomedCT_Extension1RF2_PRODUCTION_20190201T120000Z.zip SnomedCT_Extension2DeltaRF2_PRODUCTION_20190201T120000Z.zip
    
# Building from source

RF2-D2 uses Gradle for its build system. In order to create a distribution, simply run the following command in the cloned directory. 

    ./gradlew build runtimeZip

The distribution package can be found in the `build/rf2-d2-<version>.zip` folder, when the build is complete.

# Contributing

Please see [CONTRIBUTING.md](CONTRIBUTING.md) for details.

# Versioning

Our [releases](https://github.com/b2ihealthcare/rf2-d2/releases) use [semantic versioning](http://semver.org). You can find a chronologically ordered list of notable changes in [CHANGELOG.md](CHANGELOG.md).

# License

This project is licensed under the Apache 2.0 License. See [LICENSE](LICENSE) for details.