# Creating a Constraint

Custom constraints can be added to restrict values of Attributes of Entities of a System.
Currently, constraints can be started from a single simple constraint, and expanded in a binary-tree structure.

When creating a new constraint, the main window looks like the following:

![Add Constraint dialog window](images/addConstraintBase.png)

Here, a new constraint can be started by using the `New Root` button bottom left.


This will open the `Add simple Constraint` popup:

![Add Simple Constraint dialog window](images/addSimpleConstraint.png)

A simple constraint consists of two terms and a relational operator. Attributes used in both terms need to be of the
same attribute type (boolean, enumeration, integer), and the available relational operators depend on the used type.

## Term Types

There are four different types of terms:

####