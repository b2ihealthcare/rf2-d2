RF2-D2 is an open source SNOMED CT release tool, which allows you to check, create and publish SNOMED CT Extension and Edition releases with care.

[![build status](https://img.shields.io/travis/b2ihealthcare/rf2-d2/master.svg?style=flat-square)](https://travis-ci.org/b2ihealthcare/rf2-d2)
[![latest release](https://img.shields.io/github/tag/b2ihealthcare/rf2-d2.svg?style=flat-square)](https://github.com/b2ihealthcare/rf2-d2/releases/tag/v0.3.4)
[![downloads](https://img.shields.io/github/downloads/b2ihealthcare/rf2-d2/total.svg?style=flat-square)](https://github.com/b2ihealthcare/rf2-d2/releases/)
[![GitHub](https://img.shields.io/github/license/b2ihealthcare/rf2-d2.svg?style=flat-square)](https://github.com/b2ihealthcare/rf2-d2/blob/master/LICENSE)

Feature include:
* Validation and verification ([rf2 check](docs/check.md))
	* Verifies existing RF2 Release file names and structure
    * Validates SNOMED CT content using a number of validation rules (WIP)
* Packaging ([rf2 create](docs/create.md))
    * Packages a single RF2 Release from one or more RF2 files and other sources 
    * Configure and customize the name of the RF2 Release and RF2 files
    * Configure release structure and add additional content like custom files, documentation and resources
* Transformation ([rf2 transform](docs/transform.md))
    * Filtering out certain parts from existing RF2 files (for example exclude all content from a module, etc.)
    * Replaces values in RF2 files using conditions, simple statements and even external scripts 
* Diffing ([rf2 diff](docs/diff.md))
    * Diff RF2 text files and releases (WIP)

# Get RF2-D2

* [WINDOWS](https://github.com/b2ihealthcare/rf2-d2/releases/download/v0.3.4/rf2-d2-0.3.4-win-x64.zip)
* [LINUX](https://github.com/b2ihealthcare/rf2-d2/releases/download/v0.3.4/rf2-d2-0.3.4-linux-x64.zip)
* [MACOS](https://github.com/b2ihealthcare/rf2-d2/releases/download/v0.3.4/rf2-d2-0.3.4-osx-x64.zip)

# First steps

After successfully downloading the appropriate RF2-D2 release package for your operating system, run:

    unzip rf2-d2-0.3.4-linux-64.zip
    cd rf2-d2-linux-64/bin
    ./rf2 -v

It will return with the version of RF2-D2 and the supported RF2 version:

    RF2-D2 v0.3.4 @Copyright 2019 B2i Healthcare
    Supported RF2 Version: 20190131

Run the following command to get the supported list of subcommands and their descriptions:

    ./rf2 -h

Run the following command to get help about a specific command:

    ./rf2 help check

You can add the `rf2-d2-<version>/bin` folder to your PATH, so it will be available globally and can be invoked with command `rf2`.
Head over to the [docs](https://docs.b2ihealthcare.com/rf2-d2) to learn more about what is possible with RF2-D2.
If you have questions or feedback, feel free to create an issue [here](https://github.com/b2ihealthcare/rf2-d2/issues/new) or [contact us](mailto:info@b2ihealthcare.com).

# Building from source

RF2-D2 uses Gradle for its build system. In order to create a distribution, simply run the following command in the cloned directory. 

    ./gradlew build runtimeZip

The distribution package can be found in the `build/rf2-d2-<version>.zip` folder, when the build is complete.

# Contributing

Please see [CONTRIBUTING](CONTRIBUTING.md) for details.

# Versioning

Our [releases](https://github.com/b2ihealthcare/rf2-d2/releases) use [semantic versioning](http://semver.org). You can find a chronologically ordered list of notable changes and previous releases on the [releases](https://github.com/b2ihealthcare/rf2-d2/releases) page.

# License

This project is licensed under the Apache 2.0 License. See [LICENSE](LICENSE) for details.