/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.johan.foodi.admin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ValueChangeEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.johan.foodi.model.Category;
import se.johan.foodi.model.Ingredient;
import se.johan.foodi.model.Recipe;
import se.johan.foodi.model.Step;
import se.johan.foodi.model.facade.CategoryFacade;
import se.johan.foodi.model.facade.IngredientFacade;
import se.johan.foodi.model.facade.RecipeFacade;
import se.johan.foodi.util.ConnectionFactory;
import se.johan.foodi.util.ConnectionUtils;
import se.johan.foodi.util.FileUtils;

/**
 * Bean for managing recipes.
 *
 * @author johan
 */
@ManagedBean
@SessionScoped
public class RecipeBean {

  public static final Path UPLOADED_FILES_TARGET_DIR
          = Paths.get("/srv/slutprojekt/uploaded");

  private static final Logger logger = LoggerFactory.getLogger(RecipeBean.class);

  @EJB
  private RecipeFacade recipeFacade;
  
  @EJB
  private IngredientFacade ingredientFacade;
  
  @EJB
  private CategoryFacade categoryFacade;

  private Recipe selectedRecipe;

  public Recipe getSelectedRecipe() {
    return selectedRecipe;
  }

  public String setSelectedRecipe(Recipe selectedRecipe) {
    this.selectedRecipe = selectedRecipe;
    logger.info("Selecting recipe with id: " + selectedRecipe.getId() + "and step size: " + selectedRecipe.getStepCollection().size());
    return "index";
  }

  public void onSelectedRecipeIngredientsChanged(ValueChangeEvent ev) {
    logger.info("onSelectedRecipeIngredientsChanged invoked");
  }

  public void onSelectedRecipeCategoriesChanged(ValueChangeEvent ev) {
    logger.info("onSelectedRecipeCategoriesChanged invoked");
  }

  public void uploadFile(FileUploadEvent event) throws IOException {
    UploadedFile file = event.getFile();

    try (InputStream stream = file.getInputstream()) {

      String fileName = FileUtils.generateFileIdentifier(
              UPLOADED_FILES_TARGET_DIR
      );

      Files.copy(stream, Paths.get(
              UPLOADED_FILES_TARGET_DIR.toString(), fileName
      ));

      selectedRecipe.setImageUri(fileName);
      recipeFacade.edit(selectedRecipe);

    } catch (IOException ex) {
      logger.error("Error while uploading file", ex);
      throw ex;
    }
  }

  /**
   * Creates a new recipe and adds it to the list of unsaved.
   *
   * @return the target after creating a recipe.
   */
  public String createRecipe() {
    Recipe recipe = new Recipe(null, "", "");

    recipe.setName("Untitled recipe");
    recipe.setDescription("No description provided");
    recipe.setImageUri("placeholder.jpg");

    recipeFacade.create(recipe);

    return "index";
  }

  /**
   * Adds an empty step to the list of steps.
   */
  public String addStep() {
    Recipe recipe = selectedRecipe;
    int nextPosition = recipe.getStepCollection().size() + 1;

    try {
      ConnectionUtils.querySingleString(
              "INSERT INTO step (text, position, recipe_id) VALUES (?, ?, ?)",
              ConnectionUtils.QueryExecutionType.EXECUTE_UPDATE,
              "", String.valueOf(nextPosition), String.valueOf(recipe.getId())
      );
    } catch (SQLException ex) {
      logger.error("Error in addStep", ex);
    }

    //  reload page
    return "index";
  }

  /**
   * Persists the selected recipe changes to the database.
   */
  public String updateRecipe() {
    Recipe recipe = selectedRecipe;
    logger.info("invoked updateRecipe()");

    try {

      persistAll(recipe);

      for (Step s : recipe.getStepCollection()) {
        logger.info("Saving step with text: " + s.getText());
      }
      
      persistPendingCategories(recipe);
      persistPendingIngredients(recipe);

      recipeFacade.edit(recipe);
      
      //  workaround for that the local object does not update
      selectedRecipe = recipeFacade.find(recipe.getId());

    } catch (SQLException sqlE) {
      logger.error("updateRecipe()", sqlE);
    }

    return "index";
  }
  
  private void persistPendingCategories(Recipe target) {
    List<Category> categoryList = new ArrayList<Category>();
    
    for (String cat : target.getPendingCategories()) {
      Category jpgCat = categoryFacade.find(cat);
      categoryList.add(jpgCat);
    }
    
    target.setCategoryList(categoryList);
  }
  
  private void persistPendingIngredients(Recipe target) {
    List<Ingredient> ingredientList = new ArrayList<Ingredient>();
    
    for (String ingr : target.getPendingIngredients()) {
      Ingredient jpaIngr = ingredientFacade.find(ingr);
      ingredientList.add(jpaIngr);
    }
    
    target.setIngredientList(ingredientList);
  }

  /**
   * Inserts categories and ingredients unless already exists. Also inserts
   * relation between the recipe and inserted item, again unless already exists.
   */
  public void persistAll(Recipe target) throws SQLException {
    try (Connection conn = ConnectionFactory.getConnection()) {

      List<String> cats = target.getPendingCategories();
      List<String> ingreds = target.getPendingIngredients();

      persistList(cats, "category", "name", conn);
      persistList(ingreds, "ingredient", "name", conn);
      persistListRelations(cats, "recipe_category", "category", "recipe_id", target, conn);
      persistListRelations(ingreds, "recipe_ingredient", "ingredient", "recipe_id", target, conn);

    } catch (SQLException sqlE) {
      throw sqlE;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Inserts an item if it does not already exist.
   */
  public void persistList(List<String> list, String tableName, String nameColumnName, Connection connection) throws SQLException {
    for (String item : list) {
      if (item == null || item.trim().equals("")) {
        continue;
      }

      String sql = String.format(
              "SELECT 1 FROM %s WHERE %s = ?",
              tableName, nameColumnName
      );
      PreparedStatement findStmt = connection.prepareStatement(
              sql
      );
      logger.info("item param: " + item + " (" + item.length() + ")");
      findStmt.setString(1, item);

      if (!findStmt.executeQuery().next()) {
        //  Insert it
        PreparedStatement stmt = connection.prepareStatement(
                String.format(
                        "INSERT INTO %s (%s) VALUES (?)",
                        tableName,
                        nameColumnName
                )
        );
        stmt.setString(1, item.trim());
        stmt.execute();
      }
    }
  }

  /**
   * Inserts all relation for an item.
   */
  public void persistListRelations(List<String> list,
          String tableName, String idColumnName, String recipeIdColumnName,
          Recipe recipe, Connection connection) throws SQLException {

    String recipeId = recipe.getId();

    //  remove all and overwrite relations
    ConnectionUtils.querySingleString(
            String.format(
                    "DELETE FROM %s WHERE %s = ?",
                    tableName, recipeIdColumnName
            ),
            ConnectionUtils.QueryExecutionType.EXECUTE_UPDATE,
            recipeId
    );

    for (String item : list) {
      if (item == null || item.equals("")) {
        continue;
      }

      //  Insert it
      ConnectionUtils.querySingleString(
              String.format(
                      "INSERT INTO %s (%s, %s) VALUES (?, ?)",
                      tableName, idColumnName, recipeIdColumnName
              ),
              ConnectionUtils.QueryExecutionType.EXECUTE_UPDATE,
              item, recipeId
      );
    }
  }

  public List<Recipe> getRecipes() {
    List<Recipe> recipes = recipeFacade.findAll();
    //recipes.addAll(unsavedRecipes);
    logger.info("Getting some recipes: " + recipes.size());
    return recipes;
  }

  private UploadedFile uploadedFile;

  public UploadedFile getUploadedFile() {
    return uploadedFile;
  }

  public void setUploadedFile(UploadedFile uploadedFile) {
    this.uploadedFile = uploadedFile;
  }
}
