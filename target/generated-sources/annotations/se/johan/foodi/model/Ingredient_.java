package se.johan.foodi.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import se.johan.foodi.RecipeIngredient;
import se.johan.foodi.model.Recipe;

@Generated(value="EclipseLink-2.6.1.v20150916-rNA", date="2019-03-08T15:07:41")
@StaticMetamodel(Ingredient.class)
public class Ingredient_ { 

    public static volatile CollectionAttribute<Ingredient, RecipeIngredient> recipeIngredientCollection;
    public static volatile ListAttribute<Ingredient, Recipe> recipeList;
    public static volatile SingularAttribute<Ingredient, String> name;

}