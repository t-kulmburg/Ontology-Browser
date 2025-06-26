package tug.tobkul.ontologybrowser;

import tug.tobkul.ontologybrowser.cli.CommandLineInterface;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        if(args.length == 0) {
            OntologyBrowserApplication.main();
            return;
        }
        if(args.length != 4){
            System.out.println("Invalid number of arguments");
            printHelp();
            return;
        }
        CommandLineInterface.run(args);
    }

    private static void printHelp(){
        System.out.println("Usage:");
        System.out.println("\tjava -jar ontologybrowser.jar <input> <library> <system> <output>");
        System.out.println();
        System.out.println("\tRunning without arguments will open the GUI of the Ontology Browser");
        System.out.println();
        System.out.println("\tRunning with arguments will directly create an input model:");
        System.out.println("\t\t<input> \tpath to the ontology json");
        System.out.println("\t\t<library>\tlibrary name");
        System.out.println("\t\t<system>\tsystem name");
        System.out.println("\t\t<output>\tpath to output txt file");
    }
}
