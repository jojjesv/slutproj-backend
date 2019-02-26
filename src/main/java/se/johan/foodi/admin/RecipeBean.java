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
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.johan.foodi.model.Recipe;
import se.johan.foodi.model.Step;
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

            recipeFacade.edit(recipe);

        } catch (SQLException sqlE) {
            logger.error("updateRecipe()", sqlE);
        }

        return "index";
    }

    /**
     * Inserts categories and ingredients unless already exists. Also inserts
     * relation between the recipe and inserted item, again unless already
     * exists.
     */
    public void persistAll(Recipe target) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection()) {

            List<String> cats = target.getPendingCategories();
            List<String> ingreds = target.getPendingIngredients();

            persistList(cats, "category", "name", conn);
            persistList(ingreds, "ingredient", "name", conn);
            persistListRelations(cats, "recipe_category", "category", conn);
            persistListRelations(ingreds, "recipe_ingredient", "ingredient", conn);

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
                connection.prepareStatement(
                        String.format(
                                "INSERT INTO %s (%s) VALUES (?)",
                                tableName,
                                nameColumnName
                        ),
                        new String[]{
                            item.trim()
                        }
                ).execute();
            }
        }
    }

    /**
     * Inserts all relation for an item.
     */
    public void persistListRelations(List<String> list, String tableName, String nameColumnName, Connection connection) throws SQLException {
        for (String item : list) {
            if (item == null || item.equals("")) {
                continue;
            }

            PreparedStatement findStmt = connection.prepareStatement(
                    String.format(
                            "SELECT 1 FROM %s WHERE %s = ?",
                            tableName,
                            nameColumnName
                    ),
                    new String[]{
                        item
                    }
            );

            if (!findStmt.executeQuery().next()) {
                //  Insert it
                connection.prepareStatement(
                        String.format(
                                "INSERT INTO %s (%s) VALUES (?)",
                                tableName, nameColumnName
                        ),
                        new String[]{
                            item
                        }
                ).execute();
            }
        }
    }

    public List<Recipe> getRecipes() {
        List<Recipe> recipes = recipeFacade.findAll();
        //recipes.addAll(unsavedRecipes);
        logger.info("Getting some recipes: " + recipes.size());
        return recipes;
    }
}
