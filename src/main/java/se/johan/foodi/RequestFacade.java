/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.johan.foodi;

import se.johan.foodi.model.RecipeIngredient;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.sql.SQLException;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import se.johan.foodi.model.Category;
import se.johan.foodi.model.Comment;
import se.johan.foodi.model.CommentLike;
import se.johan.foodi.model.Recipe;
import se.johan.foodi.model.Step;
import se.johan.foodi.model.facade.CommentFacade;
import se.johan.foodi.model.facade.CommentLikeFacade;
import se.johan.foodi.model.facade.RecipeFacade;
import se.johan.foodi.test.TestHelper;
import se.johan.foodi.util.ConnectionUtils;

/**
 * Facade for interacting with the database facades as well as formatting JSON
 * results.
 *
 * @author johan
 */
@Stateless
public class RequestFacade {

  @EJB
  private RecipeFacade recipeFacade;

  @EJB
  private CommentFacade commentFacade;

  @EJB
  private CommentLikeFacade commentLikeFacade;

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

  public Comment postComment(String recipeId, String author, String message)
          throws SQLException, IllegalArgumentException {
    return postComment(recipeId, author, message, null);
  }

  public Comment postComment(String recipeId, String author, String message,
          Integer replyToId)
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

    if (replyToId != null) {
      Comment replyToComment = commentFacade.find(replyToId);
      if (replyToComment == null) {
        //  Invalid reply to id
        throw new IllegalArgumentException("Invalid reply-to ID: " + replyToId);
      }
      comment.setReplyToId(replyToComment);
    }

    commentFacade.create(comment);
    
    recipe.getCommentCollection().add(comment);
    
    return comment;
  }

  /**
   * Posts a comment like.
   *
   * @param commentId
   * @param senderIdentifier
   */
  public void likeComment(Integer commentId, String senderIdentifier) throws SQLException, IllegalArgumentException {

    if (senderIdentifier == null) {
      throw new IllegalArgumentException("Invalid sender identifier: " + senderIdentifier);
    }

    Comment comment = commentFacade.find(commentId);

    if (comment == null) {
      throw new IllegalArgumentException("Invalid comment ID: " + commentId);
    }

    String existing = ConnectionUtils.querySingleString(
            "SELECT 1 FROM comment_like WHERE sender_identifier = ?"
              + " AND comment_id = ?",
            senderIdentifier,
            String.valueOf(commentId)
    );

    if (existing != null) {
      throw new IllegalArgumentException("You've already liked this comment");
    }

    CommentLike like = new CommentLike();
    like.setSenderIdentifier(senderIdentifier);
    like.setCommentId(comment);

    commentLikeFacade.create(like);
    
    comment.getCommentLikeCollection().add(like);
  }

  /**
   * Retrieves all recipes objects in a minimal preview format, and formats a
   * JSON.
   */
  public JSONArray getRecipePreviews(RecipeFacade facade) {
    List<Recipe> recipes = facade.findAll();
    JSONArray output = new JSONArray();

    for (Recipe r : recipes) {
      if (r.getName().equalsIgnoreCase(TestHelper.TEST_RECIPE_NAME)) {
        //  is test recipe; don't use
        continue;
      }
      
      JSONObject entry = new JSONObject();
      entry.put("id", r.getId());
      entry.put("imageUrl", r.getImageUrl());
      entry.put("name", r.getName());

      output.add(entry);
    }

    return output;
  }
  
  public Recipe getRecipe(String id) {
    return recipeFacade.find(id);
  }

  /**
   * Retrieves full info about a recipe and formats a JSON.
   *
   * @param senderIdentifier Used to determine whether the current user has
   * liked specific comments.
   */
  public JSONObject getRecipeAsJSON(String id, String senderIdentifier) {
    Recipe recipe = getRecipe(id);
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
      if (comment.getReplyToId() != null) {
        //  prevent replies amongst root nodes
        continue;
      }

      comments.add(comment.toJSONObject(senderIdentifier));
    }
    output.put("comments", comments);

    return output;
  }

  /**
   * Reports a specific comment, unless it's been already reported.
   *
   * @param commentId
   * @return Whether the comment was reported
   */
  public boolean reportComment(Integer commentId) throws IllegalArgumentException {
    Comment comment = commentFacade.find(commentId);
    if (comment == null) {
      throw new IllegalArgumentException("Unknown comment with ID: " + String.valueOf(commentId));
    }

    if (!comment.getReported()) {
      comment.setReported(true);
      commentFacade.edit(comment);
      
      return true;
    }
    
    return false;
  }

}
