package se.johan.foodi.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import se.johan.foodi.model.Category;
import se.johan.foodi.model.Ingredient;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2019-02-08T11:02:43")
@StaticMetamodel(Recipe.class)
public class Recipe_ { 

    public static volatile SingularAttribute<Recipe, String> imageUri;
    public static volatile SingularAttribute<Recipe, String> name;
    public static volatile ListAttribute<Recipe, Category> categoryList;
    public static volatile ListAttribute<Recipe, Ingredient> ingredientList;
    public static volatile SingularAttribute<Recipe, String> description;
    public static volatile SingularAttribute<Recipe, String> id;

}