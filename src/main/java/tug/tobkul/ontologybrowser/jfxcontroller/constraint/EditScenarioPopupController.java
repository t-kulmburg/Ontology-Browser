package tug.tobkul.ontologybrowser.jfxcontroller.constraint;

import tug.tobkul.ontologybrowser.ontology.model.Library;
import tug.tobkul.ontologybrowser.ontology.model.constraint.Scenario;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

public class EditScenarioPopupController extends ScenarioPopupController {
    private Scenario scenario;

    public void setLibrarySystemScenario(Library library, oSystem system, Scenario scenario) {
        libraryChoiceBox.getItems().setAll(library);
        libraryChoiceBox.getSelectionModel().select(library);
        libraryChoiceBox.setDisable(true);

        systemChoiceBox.getItems().setAll(system);
        systemChoiceBox.getSelectionModel().select(system);
        systemChoiceBox.setDisable(true);

        this.scenario = scenario;
        nameField.setText(scenario.getName());
        commentField.setText(scenario.getComment());
    }

    public void onConfirm() {
        if (!checkComboBoxes()) {
            return;
        }
        if (systemChoiceBox.getValue().getScenarios().stream()
                .noneMatch(scenario -> scenario.getName().equals(nameField.getText())) ||
                scenario.getName().equals(nameField.getText())) {

            scenario.setName(nameField.getText());
            scenario.setComment(commentField.getText());
            nameField.getScene().getWindow().hide();
        } else {
            invalidInputLabel.setText("Name must be unique!");
            setInvalidInputLabelVisibleAndFormat();
        }
    }
}