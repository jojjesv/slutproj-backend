/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.johan.foodi;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import se.johan.foodi.model.Category;
import se.johan.foodi.model.Comment;
import se.johan.foodi.model.CommentLike;
import se.johan.foodi.model.Ingredient;
import se.johan.foodi.model.Recipe;
import se.johan.foodi.model.Step;
import se.johan.foodi.model.facade.CommentFacade;
import se.johan.foodi.model.facade.CommentLikeFacade;
import se.johan.foodi.model.facade.RecipeFacade;
import se.johan.foodi.util.ConnectionUtils;

/**
 * Facade for interacting with the database facades as well as formatting
 * JSON results.
 * @author johan
 */
@Stateless
public class RequestFacade {
  
  @EJB
  private RecipeFacade recipeFacade;
  
  @EJB
  private CommentFacade commentFacade;

  /**
   * @return all comments for a specific recipe.
   */
  public List<Comment> getComments(String recipeId) throws SQLException, IllegalArgumentException {
    if (recipeId.length() < 1) {
      throw new IllegalArgumentException("Provide a recipe ID");
    }

    List<Comment> comments = new CommentFacade().findAll(recipeId);

    return comments;
  }

  public void postComment(String recipeId, String author, String message)
          throws SQLException, IllegalArgumentException {
    if (message.length() < 1) {
      throw new IllegalArgumentException("Must provide a message");
    }

    if (author.length() < 1) {
      throw new IllegalArgumentException("Must provide an author");
    }

    if (recipeId.length() < 1) {
      throw new IllegalArgumentException("Must provide a recipe ID");
    }

    Recipe recipe = recipeFacade.find(recipeId);

    if (recipe == null) {
      //  Invalid recipe
      throw new IllegalArgumentException("Invalid recipe ID: " + recipeId);
    }

    Comment comment = new Comment();
    comment.setAuthor(author);
    comment.setText(message);
    comment.setRecipeId(recipe);

    commentFacade.create(comment);
  }

  /**
   * Posts a comment like.
   *
   * @param commentId
   * @param senderIdentifier
   */
  public void likeComment(String commentId, String senderIdentifier) throws SQLException, IllegalArgumentException {

    if (senderIdentifier == null) {
      throw new IllegalArgumentException("Invalid sender identifier: " + senderIdentifier);
    }

    Comment comment = new CommentFacade().find(commentId);

    if (comment == null) {
      throw new IllegalArgumentException("Invalid comment ID: " + commentId);
    }

    String existing = ConnectionUtils.querySingleString(
            "SELECT 1 FROM comment_like WHERE sender_identifier = ?",
            senderIdentifier
    );

    if (existing != null) {
      throw new IllegalArgumentException("You've already liked this comment");
    }

    CommentLike like = new CommentLike();
    like.setCommentId(comment);

    new CommentLikeFacade().create(like);
  }

  /**
   * Retrieves all recipes objects in a minimal preview format, and formats a
   * JSON.
   */
  public JSONArray getRecipePreviews(RecipeFacade facade) {
    List<Recipe> recipes = facade.findAll();
    JSONArray output = new JSONArray();

    for (Recipe r : recipes) {
      JSONObject entry = new JSONObject();
      entry.put("id", r.getId());
      entry.put("imageUrl", r.getImageUrl());
      entry.put("name", r.getName());

      output.add(entry);
    }

    return output;
  }

  /**
   * Retrieves full info about a recipe and formats a JSON.
   * @param senderIdentifier Used to determine whether the current user has liked specific comments.
   */
  public JSONObject getRecipe(String id, String senderIdentifier) {
    Recipe recipe = recipeFacade.find(id);
    JSONObject output = new JSONObject();
    
    if (senderIdentifier == null) {
      output.put("error", "badSenderIdentifier");
      output.put("message", "Bad sender identifier: " + senderIdentifier);
      return output;
    }

    if (recipe == null) {
      output.put("error", "unknownRecipe");
      output.put("message", "Unknown recipe with id: " + id);
      return output;
    }

    output.put("id", recipe.getId());
    output.put("imageUrl", recipe.getImageUrl());
    output.put("name", recipe.getName());
    output.put("description", recipe.getDescription());

    JSONArray categories = new JSONArray();
    for (Category cat : recipe.getCategoryList()) {
      categories.add(cat.getName());
    }
    output.put("categories", categories);

    JSONArray ingredients = new JSONArray();
    for (RecipeIngredient ingr : recipe.getRecipeIngredientCollection()) {
      JSONObject obj = new JSONObject();
      obj.put("quantity", ingr.getQuantity());
      obj.put("name", ingr.getIngredient1().getName());
      ingredients.add(obj);
    }
    output.put("ingredients", ingredients);

    JSONArray steps = new JSONArray();
    for (Step step : recipe.getStepCollection()) {
      JSONObject obj = new JSONObject();
      obj.put("position", step.getPosition());
      obj.put("text", step.getText());
      steps.add(obj);
    }
    output.put("steps", steps);

    JSONArray comments = new JSONArray();
    for (Comment comment : recipe.getCommentCollection()) {
      JSONObject obj = new JSONObject();
      obj.put("id", comment.getId());
      obj.put("author", comment.getAuthor());
      obj.put("message", comment.getText());
      obj.put("likeCount", comment.getCommentLikeCollection().size());
      
      boolean currentUserLiked = false;
      for (CommentLike like : comment.getCommentLikeCollection()) {
        if (like.getSenderIdentifier().equals(senderIdentifier)) {
          currentUserLiked = true;
          break;
        }
      }
      obj.put("currentUserLiked", currentUserLiked);
      steps.add(obj);
    }
    output.put("comments", comments);

    return output;
  }

}
