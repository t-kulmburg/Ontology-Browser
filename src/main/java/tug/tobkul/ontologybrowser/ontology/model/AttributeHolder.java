package tug.tobkul.ontologybrowser.ontology.model;

import tug.tobkul.ontologybrowser.ontology.model.attribute.Attribute;

import java.util.List;

public interface AttributeHolder {
    void addAttribute(Attribute attribute);

    List<Attribute> getAttributes();
}
