/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.johan.foodi.admin;

import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.johan.foodi.model.Recipe;

/**
 * Bean for managing recipes.
 * @author johan
 */
@ManagedBean(name = "recipeBean")
@SessionScoped
public class RecipeBean {
    
    private static final Logger logger = LoggerFactory.getLogger(RecipeBean.class);
    
    private List<Recipe> unsavedRecipes = new ArrayList<>();
    
    /**
     * Creates a new recipe and adds it to the list of unsaved.
     * @return the target after creating a recipe.
     */
    public String createRecipe() {
        logger.info("We now have " + unsavedRecipes.size() + " unsaved recipes");
        Recipe recipe = new Recipe();
        
        recipe.setName("VERY GOOD! " + Math.random());
        
        unsavedRecipes.add(recipe);
        return "/index.xhtml";
    }
    
    public void updateRecipe() {
        
    }
    
    public List<Recipe> getRecipes(){
        //List<Recipe> recipes = recipeFacade.findAll();
        //recipes.addAll(unsavedRecipes);
        logger.info("Getting some recipes: " + unsavedRecipes.size());
        return unsavedRecipes;
    }
    
    public String getGreeting() {
        logger.info("Getting some recipes: " + unsavedRecipes.size());
        return "Hello 2";
    }
}
