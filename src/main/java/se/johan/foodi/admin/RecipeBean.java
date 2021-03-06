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
import javax.faces.bean.SessionScoped;
import javax.faces.event.ValueChangeEvent;
import javax.validation.ConstraintViolationException;
import org.eclipse.persistence.indirection.IndirectList;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.johan.foodi.model.RecipeIngredient;
import se.johan.foodi.model.RecipeIngredientPK;
import se.johan.foodi.model.Category;
import se.johan.foodi.model.Comment;
import se.johan.foodi.model.Ingredient;
import se.johan.foodi.model.Recipe;
import se.johan.foodi.model.Step;
import se.johan.foodi.model.facade.CategoryFacade;
import se.johan.foodi.model.facade.CommentFacade;
import se.johan.foodi.model.facade.IngredientFacade;
import se.johan.foodi.model.facade.RecipeFacade;
import se.johan.foodi.model.facade.StepFacade;
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

  @EJB
  StepFacade stepFacade;

  private String errorMessage = null;

  @EJB
  private CommentFacade commentFacade;

  private Recipe selectedRecipe;

  private boolean filterReportedComments;

  private UploadedFile selectedRecipeUploadedFile;

  public UploadedFile getSelectedRecipeUploadedFile() {
    return selectedRecipeUploadedFile;
  }

  public void setSelectedRecipeUploadedFile(UploadedFile selectedRecipeUploadedFile) {
    logger.info("setSelectedRecipeUploadedFile hello");
    this.selectedRecipeUploadedFile = selectedRecipeUploadedFile;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  /**
   * @return The executable error message wrapped as a script tag.
   */
  public String getErrorMessageScript() {
    if (errorMessage == null) {
      return null;
    }

    return String.format(
      "<script>alert('%s');</script>",
      errorMessage
    );
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public Recipe getSelectedRecipe() {
    return selectedRecipe;
  }

  public String setSelectedRecipe(Recipe selectedRecipe) {
    this.selectedRecipe = selectedRecipe;
    logger.info("Selecting recipe with id: " + selectedRecipe.getId() + "and step size: " + selectedRecipe.getSteps().size());
    return "index";
  }

  public void onSelectedRecipeIngredientsChanged(ValueChangeEvent ev) {
    logger.info("onSelectedRecipeIngredientsChanged invoked");
  }

  public void onSelectedRecipeCategoriesChanged(ValueChangeEvent ev) {
    logger.info("onSelectedRecipeCategoriesChanged invoked");
  }

  public void uploadSelectedRecipeFile() throws IOException {
    UploadedFile file = getSelectedRecipeUploadedFile();

    System.out.println("We did it, Houston.");
    logger.info("Uploading file: " + (file == null ? "NADA" : file.getFileName()));

    if (file == null) {
      logger.info("[uploadSelectedRecipeFile] file is null!");
      return;
    }

    try (InputStream stream = file.getInputstream()) {

      String fileName = file.getFileName();
      int fileExtIndex = fileName.indexOf(".");

      String ext = "";
      if (fileExtIndex != -1) {
        ext = fileName.substring(fileExtIndex);
      }

      String uniqueFileName = FileUtils.generateFileIdentifier(
        UPLOADED_FILES_TARGET_DIR,
        ext
      );

      Files.copy(stream, Paths.get(
        UPLOADED_FILES_TARGET_DIR.toString(), uniqueFileName
      ));

      selectedRecipe.setImageUri(uniqueFileName);
      recipeFacade.edit(selectedRecipe);

    } catch (IOException ex) {
      logger.error("Error while uploading file", ex);
      throw ex;
    } finally {
      selectedRecipeUploadedFile = null;
    }
  }

  /**
   * Creates a new recipe and adds it to the list of unsaved.
   *
   * @return the target after creating a recipe.
   */
  public String createRecipe() {
    try (Connection connection = ConnectionFactory.getConnection()) {
      Recipe recipe = new Recipe(Recipe.generateId(connection), "", "");

      recipe.setName("Untitled recipe");
      recipe.setDescription("No description provided");
      recipe.setImageUri("placeholder.jpg");

      recipeFacade.create(recipe);
    } catch (SQLException ex) {
      logger.error("[createRecipe] error creating recipe", ex);
    }

    return "/admin/index.xhtml";
  }

  /**
   * Adds an empty step to the list of steps.
   */
  public String addStep() {
    Recipe recipe = selectedRecipe;
    int nextPosition = recipe.getSteps().size() + 1;

    logger.info("invoked addStep() (nextPosition=" + nextPosition + ")");

    Step step = new Step();
    step.setRecipe(recipe);
    step.setPosition((short) nextPosition);
    step.setText("");

    stepFacade.create(step);

    recipe.getSteps().add(step);

    try {
      recipeFacade.edit(recipe);
    } catch (Exception ex) {
      logger.error("Whoopsie", ex);
    }

    //  reload page
    return "/admin/index.xhtml";
  }

  private void reloadSelectedRecipe() {
    //  clear cache
    recipeFacade.clearCache(selectedRecipe);
    selectedRecipe = recipeFacade.find(selectedRecipe.getId());
  }

  /**
   * Deletes the selected recipe.
   */
  public String deleteRecipe() {
    Recipe recipe = selectedRecipe;
    logger.info("invoked deleteRecipe()");

    recipeFacade.remove(recipe);

    selectedRecipe = null;

    return "index";
  }

  /**
   * Persists the selected recipe changes to the database.
   */
  public String updateRecipe() {
    try {
      uploadSelectedRecipeFile();
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }

    Recipe recipe = selectedRecipe;
    logger.info("invoked updateRecipe()");

    try {

      recipe.arrangeSteps();
      persistAll(recipe);

      for (Step s : recipe.getSteps()) {
        logger.info("Saving step with text: " + s.getText());
      }

      persistPendingCategories(recipe);
      persistPendingIngredients(recipe);

      try {
        recipeFacade.edit(recipe);
      } catch (Exception ex) {
        //  mysql validation likely failed
        //  not sure if this is a ConstraintValidationException/EJBException?? nothing works
        this.errorMessage = "Provide more recipe info.";
        return "index";
      }

    } catch (SQLException sqlE) {
      logger.error("updateRecipe()", sqlE);
    }

    //  workaround for that the local object does not update
    reloadSelectedRecipe();

    return "index";
  }

  /**
   * Ensures that categories are persisted.
   *
   * @param target Recipe of which to read categories from.
   */
  private void persistPendingCategories(Recipe target) {
    List<Category> categoryList = new ArrayList<Category>();

    for (String cat : target.getPendingCategories()) {
      if (cat == null || cat.isEmpty()) {
        continue;
      }

      Category jpgCat = categoryFacade.find(cat);
      categoryList.add(jpgCat);
    }

    target.setCategories(categoryList);
  }

  /**
   * Ensures that ingredients are persisted.
   *
   * @param target Recipe of which to read ingredients from.
   */
  private void persistPendingIngredients(Recipe target) {
    List<Ingredient> ingredientList = new ArrayList<Ingredient>();

    for (String ingr : target.getPendingIngredients()) {
      if (ingr == null || ingr.isEmpty()) {
        continue;
      }
      Ingredient jpaIngr = ingredientFacade.find(ingr);
      ingredientList.add(jpaIngr);
    }

    target.setIngredients(ingredientList);
  }

  /**
   * Inserts categories and ingredients unless already exists. Also inserts
   * relation between the recipe and inserted item, again unless already exists.
   */
  public void persistAll(Recipe target) throws SQLException {
    List<String> cats = target.getPendingCategories();
    List<String> ingreds = target.getPendingIngredients();

    //  clear existing relations
    List<RecipeIngredient> oldRelations = new IndirectList<>();
    oldRelations.addAll(target.getIngredientRelations());
    target.getIngredientRelations().clear();
    target.getCategoryList().clear();

    for (String pendingCatName : cats) {
      if (pendingCatName == null || pendingCatName.isEmpty()) {
        continue;
      }

      //  insert pending
      Category pendingCat = categoryFacade.find(pendingCatName);
      boolean createPendingCat = false;

      if (createPendingCat = pendingCat == null) {
        pendingCat = new Category(pendingCatName);
        pendingCat.setAssociatedRecipes(new IndirectList<Recipe>());
      }
      pendingCat.getAssociatedRecipes().add(target);

      try {
        if (createPendingCat) {
          categoryFacade.create(pendingCat);
        } else {
          categoryFacade.edit(pendingCat);
        }
      } catch (Exception ex) {
        logger.info("ex", ex);
      }

      target.getCategoryList().add(pendingCat);
    }
    for (String pendingIngrName : ingreds) {
      if (pendingIngrName == null || pendingIngrName.isEmpty()) {
        continue;
      }

      //  insert pending
      boolean createPendingIngr = false;
      Ingredient pendingIngr = ingredientFacade.find(pendingIngrName);
      if (createPendingIngr = pendingIngr == null) {
        pendingIngr = new Ingredient(pendingIngrName);
      }

      try {
        if (createPendingIngr) {
          ingredientFacade.create(pendingIngr);
        } else {
          ingredientFacade.edit(pendingIngr);
        }
      } catch (Exception ex) {
        logger.info("ex", ex);
      }

      target.getIngredients().add(pendingIngr);

      RecipeIngredientPK pk = new RecipeIngredientPK();
      pk.setIngredient(pendingIngrName);
      pk.setRecipeId(target.getId());

      RecipeIngredient relation = null;
      for (RecipeIngredient oldRel : oldRelations) {
        if (oldRel.getIngredient().getName().equalsIgnoreCase(pendingIngrName)) {
          relation = oldRel;
          break;
        }
      }

      if (relation == null) {
        relation = new RecipeIngredient();
        relation.setRecipe(target);
        relation.setIngredient(pendingIngr);
        relation.setRecipeIngredientPK(pk);
      }

      try {
        Collection<RecipeIngredient> relations = target.getIngredientRelations();
        while (true) {
          //  we want to break out of this while loop on command
          for (RecipeIngredient ri : relations) {
            Ingredient ingr = ri.getIngredient();
            if (ingr == null) {
              break;  //  don't add
            }

            if (ingr.getName().equals(relation.getIngredient().getName())) {
              //  skip duplicate
              break;
            }
          }
          relations.add(relation);

          break;
        }
      } catch (Exception ex) {
        logger.info("ex", ex);
      }
    }

    try {
      recipeFacade.edit(target);
    } catch (Exception ex) {
      logger.error("[updateRecipe]", ex);
    }

    System.out.println("After updateRecipe");
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

  public boolean isFilterReportedComments() {
    return filterReportedComments;
  }

  public void setFilterReportedComments(boolean filterReportedComments) {
    this.filterReportedComments = filterReportedComments;
  }

  /**
   * Deletes a comment (and persist) from the selected recipe.
   *
   * @return Whether the comment was actually deleted.
   */
  public boolean deleteComment(int commentId) {
    Collection<Comment> comments = selectedRecipe.getComments();

    for (Comment c : comments) {
      if (c.getId() == commentId) {
        comments.remove(c);

        commentFacade.remove(c);
        
        Recipe assocRecipe = c.getRecipe();
        
        if (assocRecipe.getComments().remove(c)) {
          //  update the local state for recipe as well, otherwise comment is still shown in frontend
          recipeFacade.edit(assocRecipe);
        }
        
        return true;
      }
    }

    return false;
  }

  /**
   * To be invoked from command button.
   *
   * @param commentId
   * @return
   */
  public String deleteCommentAndRedirect(int commentId) {
    deleteComment(commentId);
    return "index";
  }
}
