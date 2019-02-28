package se.johan.foodi;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import se.johan.foodi.RecipeIngredientPK;
import se.johan.foodi.model.Ingredient;
import se.johan.foodi.model.Recipe;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2019-02-28T19:34:56")
@StaticMetamodel(RecipeIngredient.class)
public class RecipeIngredient_ { 

    public static volatile SingularAttribute<RecipeIngredient, String> quantity;
    public static volatile SingularAttribute<RecipeIngredient, Recipe> recipe;
    public static volatile SingularAttribute<RecipeIngredient, Ingredient> ingredient1;
    public static volatile SingularAttribute<RecipeIngredient, RecipeIngredientPK> recipeIngredientPK;

}