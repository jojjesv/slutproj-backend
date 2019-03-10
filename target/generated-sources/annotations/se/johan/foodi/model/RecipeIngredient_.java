package se.johan.foodi.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import se.johan.foodi.model.Ingredient;
import se.johan.foodi.model.Recipe;
import se.johan.foodi.model.RecipeIngredientPK;

@Generated(value="EclipseLink-2.6.1.v20150916-rNA", date="2019-03-10T11:12:57")
@StaticMetamodel(RecipeIngredient.class)
public class RecipeIngredient_ { 

    public static volatile SingularAttribute<RecipeIngredient, String> quantity;
    public static volatile SingularAttribute<RecipeIngredient, Recipe> recipe;
    public static volatile SingularAttribute<RecipeIngredient, Ingredient> ingredient1;
    public static volatile SingularAttribute<RecipeIngredient, RecipeIngredientPK> recipeIngredientPK;

}