module tug.tobkul.ontologybrowser {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires java.smartcardio;
    requires org.controlsfx.controls;
    requires java.desktop;
    requires commons.math3;
    requires java.sql;
    requires org.jgrapht.core;
    requires net.sourceforge.plantuml;
    requires org.apache.pdfbox;


    opens tug.tobkul.ontologybrowser to javafx.fxml;
    opens tug.tobkul.ontologybrowser.ontology to com.fasterxml.jackson.databind;
    exports tug.tobkul.ontologybrowser;
    opens tug.tobkul.ontologybrowser.ontology.model to com.fasterxml.jackson.databind;
    opens tug.tobkul.ontologybrowser.ontology.model.attribute to com.fasterxml.jackson.databind;
    opens tug.tobkul.ontologybrowser.ontology.model.constraint to com.fasterxml.jackson.databind;
    exports tug.tobkul.ontologybrowser.ontology.model.constraint.term to com.fasterxml.jackson.databind;
    exports tug.tobkul.ontologybrowser.ontology.model.constraint.parameter to com.fasterxml.jackson.databind;
    exports tug.tobkul.ontologybrowser.jfxcontroller.export;
    opens tug.tobkul.ontologybrowser.jfxcontroller.export to javafx.fxml;
    exports tug.tobkul.ontologybrowser.jfxcontroller;
    opens tug.tobkul.ontologybrowser.jfxcontroller to javafx.fxml;
    opens tug.tobkul.ontologybrowser.ontology.model.constraint.operator to com.fasterxml.jackson.databind;
    opens tug.tobkul.ontologybrowser.ontology.model.constraint.operator.relational to com.fasterxml.jackson.databind;
    exports tug.tobkul.ontologybrowser.jfxcontroller.attribute;
    opens tug.tobkul.ontologybrowser.jfxcontroller.attribute to javafx.fxml;
    exports tug.tobkul.ontologybrowser.jfxcontroller.entity;
    opens tug.tobkul.ontologybrowser.jfxcontroller.entity to javafx.fxml;
    exports tug.tobkul.ontologybrowser.jfxcontroller.library;
    opens tug.tobkul.ontologybrowser.jfxcontroller.library to javafx.fxml;
    exports tug.tobkul.ontologybrowser.jfxcontroller.relation;
    opens tug.tobkul.ontologybrowser.jfxcontroller.relation to javafx.fxml;
    exports tug.tobkul.ontologybrowser.jfxcontroller.system;
    opens tug.tobkul.ontologybrowser.jfxcontroller.system to javafx.fxml;
    exports tug.tobkul.ontologybrowser.jfxcontroller.term;
    opens tug.tobkul.ontologybrowser.jfxcontroller.term to javafx.fxml;
    exports tug.tobkul.ontologybrowser.jfxcontroller.constraint;
    opens tug.tobkul.ontologybrowser.jfxcontroller.constraint to javafx.fxml;
    exports tug.tobkul.ontologybrowser.jfxcontroller.constraint.view;
    opens tug.tobkul.ontologybrowser.jfxcontroller.constraint.view to javafx.fxml;
}