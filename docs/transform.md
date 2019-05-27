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

The `rf2 transform` command accepts a single, mandatory file path pointing to a valid, recognizable (by the current `rf2-spec.yml`) RF2 source file as first argument.  (_NOTE: directories are not supported yet_).
If the specified file cannot be recognized by the current `rf2-spec.yml` specification, then the command will just copy the file over to the output directory.

## SCRIPT argument

The `rf2 transform` command accept a single, mandatory expression or file path as second argument. 

Supported scripting languages are:
* Groovy (v2.5.x) 

## Examples

TODO
