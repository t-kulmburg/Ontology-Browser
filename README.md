# Ontology Browser

This tool provides a graphical user interface that allows the creation of ontologies.

Further, it is able to generate an Input Model from an existing ontology,
that can then be used as input for the combinatorial-testing-tool
[ACTS](https://csrc.nist.rip/groups/SNS/acts/documents/comparison-report.html#acts)

## Compile & Run

The tool is written in java using maven, and utilizing javafx as frontend-library.

To build an executable jar file, run

```bash
 ./mvnw clean install:install-file package
```

The `install:install-file` is required to install the `plantuml-1.2025.3.jar` located in `lib` to the local maven
repository, so that it is available for the build. This is required as the dependency is not available in any public
maven repository.

The jar file supports two ways of execution:

### Running the full OntologyBrowser with graphical interface

Simply execute the jar without additional parameters by running

```bash
java -jar target/OntologyBrowser-1.5.jar
```

### Generate Input Model without GUI

The jar can also be used to directly create an input model from the command line.

Therefore, following 4 parameters need to be passed:

1. Path to the ontology json file
2. Name of the library within the ontology
3. Name of the system within the library
4. Path to the output file

```bash
java -jar ontologybrowser.jar <input> <library> <system> <output>
```

## User Documentation

The detailed documentation is published using GitHub Pages: https://t-kulmburg.github.io/Ontology-Browser/