# Create command

This page provides the usage information for the `rf2 create` command.

## Command options overview and help

You can also see this information by running `rf2 help create` from the command line.

```
Usage:

rf2 create [-c=<country>] [-d=<releaseDate>] [-n=<namespace>] [-o=<outDir>] [-p=<product>] [-s=<releaseStatus>]
           [-t=<releaseTime>] [-C=<contentSubTypes>]... [PATH...]

Description:

Creates an RF2 Release from a set of RF2 files and/or archives

Parameters:
      [PATH...]              RF2 source files to use when creating the RF2 Release.

Options:
  -c, --country=<country>    Configure the country value in the [CountryNamespace] part of RF2 Release files. Default value
                               is 'INT'.
  -C, --contentsubtype=<contentSubTypes>
                             Configure the content sub types to be created in the RF2 Release. Default is ['Delta',
                               'Snapshot', 'Full'].
  -d, --date=<releaseDate>   Configure the [ReleaseDate] value in the name of the created RF2 Release. Default value is
                               today's date.
  -n, --namespace=<namespace>
                             Configure the namespace value in the [CountryNamespace] part of RF2 Release files. Default
                               value is empty.
  -o, --outdir=<outDir>      Output directory where the RF2 Release will be created. Default is '<currentDir>/target'.
  -p, --product=<product>    Configure the [Product] value in the name of the created RF2 Release. Default value is empty.
  -s, --status=<releaseStatus>
                             Configure the [ReleaseStatus] value in the name of the created RF2 Release. Default value is
                               'PRODUCTION'.
  -t, --time=<releaseTime>   Configure the [ReleaseTime] value in the name of the created RF2 Release. Default value is the
                               current time.
```

## PATH argument

The `rf2 create` command accepts arbitrary number of RF2 source file paths. All `PATH` arguments must be a valid location to a file accessible by the local file system (_NOTE: directories are not supported yet_).

If a `PATH` argument points to an incorrect or inaccessible location of a file, then the command reports it as an error, for example:

    rf2 create nonexistent.zip

Will report:

    Path 'nonexistent.zip' could not be resolved.

## Output directory

By default, the newly created release package will be placed to the `target` directory inside the current working directory.
It is possible to override the default output directory and redirect all output files to another directory with the `-o` or `--outdir` option key. Example:

    rf2 create -o /home/user/anotherOutputDirectory

## How `rf2 create` works

The command is designed to construct a single well-formed RF2 Release package from multiple input source RF2 files.
The format and shape of the final RF2 package is described by the currently active `rf2-spec.yml` file.
To generate an empty RF2 release package and verify that the proper rf2-spec.yml file is loaded, just execute the `rf2 create` command without any RF2 source files:

    rf2 create

### Source selection

To construct an output RF2 file (eg. the `Concept` file), the `create` command will try to find matching input source files based on the following criteria:
* The source file should have the same matching content type (eg. source files with `Concept` content type)
* RF2 Data files (any file that starts with either `der` or `sct`)
  * The source file should have the same header (`Concept` file header will only match files with exact same header, case sensitive)
* Non-RF2 Data files
  * The source file with the highest `RF2VersionDate` part will be selected as one and only source (for `Documentation`, `Resources`, custom files)

If there is no matching source file for a given file specified in an `rf2-spec.yml` file then an empty (non-RF2 Data files) or header-only (RF2 Data files) file will be created at its destination directory.

_NOTE: ContentSubType part of the file name is intentionally left out from the criteria. This is to be able to construct derivatives (like `Delta` and `Snapshot`) from `Full` RF2 release packages and also provide the ability to fix incorrectly packaged RF2 releases_

### Duplicate line detection

In case of multiple matching source input files the resulting output file will contain all lines from these matching input files without any duplication, but if multiple source files contain the same `id`|`effectiveTime` pair but with different actual line content, then the `rf2 create` command will print out these lines with a warning message.  

### Line filtering

Certain RF2 content file require additional line filtering in order to produce the appropriate output file. This can be configured in the `rf2-spec.yml` for each RF2 Data file with the `include`/`exclude` directives.

By default, 
* `Relationship` and `StatedRelationship` files 
* `Description` and `TextDefinition` files 
have default `include`/`exclude` directives attached to them (see here: ). 

## Examples

Generate a new RF2 Release based on the previous RF2 Release and a new Delta (or Snapshot) to a specified `OUTDIR_PATH`:

    rf2 create -d 20190131 SnomedCT_PreviousRF2_PRODUCTION_20180731T120000Z.zip SnomedCT_NewDeltaRF2_PRODUCTION_20190131T120000Z.zip

_NOTE: the `-d 20190131` defines the `releaseDate` of the RF2 Release and it is also being used to select the content for the resulting Delta RF2 files._

Generate an RF2 Delta Release from an RF2 Full Release:

    rf2 create -d 20190131 -o OUTDIR_PATH -C Delta SnomedCT_RF2_PRODUCTION_20190131T120000Z.zip
    
Generate an RF2 Release from multiple RF2 sources:

    rf2 create -d 20190201 -o OUTDIR_PATH SnomedCT_InternationalRF2_PRODUCTION_20190131T120000Z.zip SnomedCT_Extension1RF2_PRODUCTION_20190201T120000Z.zip SnomedCT_Extension2DeltaRF2_PRODUCTION_20190201T120000Z.zip
