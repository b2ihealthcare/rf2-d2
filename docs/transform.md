# Transform command

This page provides the usage information for the `rf2 transform` command.

## Command options overview and help

You can also see this information by running `rf2 help transform` from the command line.

```
Usage:

rf2 transform [-o=<outDir>] PATH SCRIPT

Description:

Apply an expression to transform and/or filter RF2 rows in the given RF2 source.

Parameters:
      PATH                RF2 source file to replace column values in.
      SCRIPT              Script Expression or PATH to a .groovy script file to apply to each RF2 line in the specified
                            source file.

Options:
  -o, --outdir=<outDir>   Output directory where the transformed RF2 output file will be created.
```

## PATH argument

The command accepts a single, mandatory file path pointing to a valid RF2 source file as first argument. (_NOTE: directories are not supported yet_).
If the specified file cannot be recognized by the current `rf2-spec.yml` specification, then the command will just copy the file over to the output directory.

## SCRIPT argument

The command accepts a single, mandatory expression or file path as second argument.
If the argument points to a local script file, then the command will read the file and execute it, otherwise it will try to evaluate it as a script.   

Supported scripting languages are:
* Groovy (v2.5.x)

## Output directory

By default, the transformed file will be placed to the `target` directory inside the current working directory.
It is possible to override the default output directory and redirect all output files to another directory with the `-o` or `--outdir` option key. Example:

    rf2 transform -o /home/user/anotherOutputDirectory PATH SCRIPT

## How `transform` works

The command will traverse the RF2 input file and create a copy of it in the output directory with the same name.
During the copy process, if it detects an RF2 Data file, then it will execute the supplied script on every row of that data file.
The script will receive the following arguments on each evaluation:
* `_file` - the current RF2 file that is being processed
* All header properties with their corresponding value from the current row

Consider the following command:

    rf2 transform SnomedCT_RF2_PRODUCTION_20190131T120000Z.zip "effectiveTime = 'Concept'.equals(_file.getType()) ? '20190201' : effectiveTime"

And RF2 Concept File:

```
id	effectiveTime	active	moduleId	definitionStatusId
353008	20190131	0	900000000000207008	900000000000074008
449005	20190131	0	900000000000207008	900000000000074008
503003	20190131	0	900000000000207008	900000000000074008
716000	20190131	0	900000000000207008	900000000000074008
721002	20190131	0	900000000000207008	900000000000074008
```

After execution the output file will be placed in `target` output directory with name `SnomedCT_RF2_PRODUCTION_20190131T120000Z.zip`.
The resulting file will be almost identical to the source RF2 file, the command has only modified the `Concept` file because of the logic in the specified expression.
All `effectiveTime` values have been changed to `20190201` in the `Concept` file.

### Filtering

It is possible to return a `boolean` value from the specified script to decide whether to include or exclude a row from the output RF2 file.

Consider the following command:

    rf2 transform SnomedCT_RF2_PRODUCTION_20190131T120000Z.zip "effectiveTime.equals('20190201')"

It will extract the lines with `effectiveTime` value `20190201` from all files and write them to the output directory. 