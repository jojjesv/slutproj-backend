package se.johan.foodi.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import se.johan.foodi.model.Category;
import se.johan.foodi.model.Comment;
import se.johan.foodi.model.Ingredient;
import se.johan.foodi.model.RecipeIngredient;
import se.johan.foodi.model.Step;

@Generated(value="EclipseLink-2.6.1.v20150916-rNA", date="2019-03-15T12:07:12")
@StaticMetamodel(Recipe.class)
public class Recipe_ { 

    public static volatile SingularAttribute<Recipe, String> imageUri;
    public static volatile CollectionAttribute<Recipe, Comment> comments;
    public static volatile SingularAttribute<Recipe, String> name;
    public static volatile CollectionAttribute<Recipe, RecipeIngredient> ingredientRelations;
    public static volatile SingularAttribute<Recipe, String> description;
    public static volatile ListAttribute<Recipe, Ingredient> ingredients;
    public static volatile SingularAttribute<Recipe, String> id;
    public static volatile ListAttribute<Recipe, Category> categories;
    public static volatile CollectionAttribute<Recipe, Step> steps;

}