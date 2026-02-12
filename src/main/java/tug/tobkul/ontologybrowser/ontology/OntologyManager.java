package tug.tobkul.ontologybrowser.ontology;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.collections.FXCollections;
import tug.tobkul.ontologybrowser.ontology.model.Library;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class OntologyManager {
    ObjectMapper mapper = new ObjectMapper();
    private List<Library> libraries;
    private File currentFile;

    public OntologyManager() {
        libraries = FXCollections.observableArrayList();
        currentFile = null;
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void openFile(File library_json) throws IOException {
        List<Library> temp = new ArrayList<>(mapper.readValue(library_json, new TypeReference<>() {
        }));
        currentFile = library_json;
        setLibraries(temp);
    }

    public void importFile(File library_json) throws IOException {
        List<Library> temp = new ArrayList<>(mapper.readValue(library_json, new TypeReference<>() {
        }));
        temp.forEach(this::addLibrary);
    }

    public void saveFile() throws IOException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(currentFile, libraries);
    }

    public void saveFileAs(File library_json) throws IOException {
        currentFile = library_json;
        mapper.writerWithDefaultPrettyPrinter().writeValue(currentFile, libraries);
    }

    public void closeFile() {
        currentFile = null;
        libraries.clear();
    }

    public String getCurrentFileName() {
        if (currentFile == null) {
            return "unsaved";
        } else {
            return currentFile.getName();
        }
    }

    public boolean hasUnsavedChanges() throws IOException {
        if (currentFile == null) {
            return !libraries.isEmpty();
        }
        String fileContent = Files.readString(currentFile.toPath());
        JsonNode fileJsonTree = mapper.readTree(fileContent);

        JsonNode currentJsonTree = mapper.valueToTree(libraries);

        return !fileJsonTree.equals(currentJsonTree);
    }

    public File getCurrentFile() {
        return currentFile;
    }

    public List<Library> getLibraries() {
        return libraries;
    }

    public void setLibraries(List<Library> libraries) {
        this.libraries = FXCollections.observableArrayList(libraries);
    }

    public void addLibrary(Library library) {
        this.libraries.add(library);
    }
}
