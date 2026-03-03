package tug.tobkul.ontologybrowser.ontology.model.constraint;


import java.util.ArrayList;
import java.util.List;

public class Scenario {
    private String name;
    private String comment = "";
    private List<ConstraintHolder> constraintHolderList = new ArrayList<>();

    public Scenario() {}

    public Scenario(String name){
        this.name = name;
    }

    public Scenario(String name, String comment){
        this.name = name;
        this.comment = comment;
    }

    @Override
    public String toString(){
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment(){
        return comment;
    }

    public void setComment(String comment){
        this.comment = comment;
    }

    public List<ConstraintHolder> getConstraintHolderList() {
        return constraintHolderList;
    }

    public void setConstraintHolderList(List<ConstraintHolder> constraintHolderList) {
        this.constraintHolderList = constraintHolderList;
    }

    public void addConstraint(ConstraintHolder constraintHolder){
        constraintHolderList.add(constraintHolder);
    }
}
