package se.johan.foodi.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import se.johan.foodi.model.Recipe;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2019-02-25T16:03:41")
@StaticMetamodel(Step.class)
public class Step_ { 

    public static volatile SingularAttribute<Step, Integer> id;
    public static volatile SingularAttribute<Step, String> text;
    public static volatile SingularAttribute<Step, Short> position;
    public static volatile SingularAttribute<Step, Recipe> recipeId;

}