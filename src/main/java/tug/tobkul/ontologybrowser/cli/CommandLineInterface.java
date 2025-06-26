package tug.tobkul.ontologybrowser.cli;

import tug.tobkul.ontologybrowser.inputmodel.InputModelGenerator;
import tug.tobkul.ontologybrowser.ontology.OntologyManager;
import tug.tobkul.ontologybrowser.ontology.model.Library;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class CommandLineInterface {
    private static final OntologyManager ontologyManager = new OntologyManager();

    public static void run(String[] args) throws IOException {
        String inputFileName = args[0];
        String libraryName = args[1];
        String systemName = args[2];
        String outputFileName = args[3];

        ontologyManager.openFile(new File(inputFileName));
        Library library = ontologyManager.getLibraries().stream().filter(l -> l.getName().equals(libraryName)).findFirst().orElse(null);
        if (library == null) {
            System.out.println("Library not found: " + libraryName);
            return;
        }
        oSystem system = library.getSystems().stream().filter(s -> s.getName().equals(systemName)).findFirst().orElse(null);
        if (system == null) {
            System.out.println("System not found: " + systemName + " in library " + libraryName);
            return;
        }

        InputModelGenerator inputModelGenerator = new InputModelGenerator("InputModel", system, true);
        long startTime = System.nanoTime();
        String model = inputModelGenerator.generate();
        long endTime = System.nanoTime();
        long durationNs = endTime - startTime;
        double durationMs = durationNs / 1000000.0;
        if (model == null) {
            System.out.println(inputModelGenerator.getCliError());
            return;
        }

        try {
            Files.write(Path.of(outputFileName), model.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.out.println("Error writing output file: " + outputFileName);
            return;
        }

        String message = String.format("Runtime: %.2f ms (%d ns)", durationMs, durationNs);
        System.out.println("Input Model generated!");
        System.out.println("Detected rootEntity: " + inputModelGenerator.getRootEntity().getName());
        System.out.println(message);
    }
}
