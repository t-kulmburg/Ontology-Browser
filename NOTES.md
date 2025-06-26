# TODO

- write user documentation / programm dokumentation (für github)
- constraint nur berechnen wenn alle werte != epsilon sind beim input model
- Relations can also have Attributes. Currently they are not used for anything in the input model creation.

- 30-50


## DONE

- fix constraint generation
- constraint comparators like > should only work for numbers
- EPSILON for integers is not working, we need a number that never is used (arithmetic)
  - Dont use enum for all data in input model -> breaks arithmetic
- make jar runnable only as input model generator
- track and print runtime of input model generation
- graphen convertieren und checken
- drucken von library/entity/etc oder als pdf speichern etc

# NOTES

I found another possible Issue:

If we for example have a "Robot" entity where we have 4 instances (example via 1:4 relation Env:Robot)-
I think it is currently not possible to construct a constraint that all 4 robots cannot be in the same position,
without also creating constraints like "Robot_1_Position_x != Robot_1_Position_x" that are always false.




java -jar target/OntologyBrowser-1.5.jar data/MobileRobot.json "Mobile Robot" "Mobile Robot" "RobotEnvironment" data/inputModel.txt
java -jar target/OntologyBrowser-1.5.jar data/robot_ontology_ex.json "Publication Ontologies" "QRS 2025" data/inputModel.txt