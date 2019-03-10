package se.johan.foodi.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import se.johan.foodi.model.Recipe;

@Generated(value="EclipseLink-2.6.1.v20150916-rNA", date="2019-03-10T11:12:57")
@StaticMetamodel(Category.class)
public class Category_ { 

    public static volatile ListAttribute<Category, Recipe> recipeList;
    public static volatile SingularAttribute<Category, String> name;

}