package se.johan.foodi.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import se.johan.foodi.model.Recipe;

@Generated(value="EclipseLink-2.6.1.v20150916-rNA", date="2019-03-10T11:12:57")
@StaticMetamodel(Step.class)
public class Step_ { 

    public static volatile SingularAttribute<Step, Integer> id;
    public static volatile SingularAttribute<Step, String> text;
    public static volatile SingularAttribute<Step, Short> position;
    public static volatile SingularAttribute<Step, Recipe> recipeId;

}