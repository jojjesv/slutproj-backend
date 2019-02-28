/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.johan.foodi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.sql.SQLException;
import java.util.List;
import javax.ejb.EJB;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import se.johan.foodi.model.Recipe;
import se.johan.foodi.model.facade.RecipeFacade;
import se.johan.foodi.util.RequestUtils;

/**
 * Main request handler.
 *
 * @author johan
 */
@Path("")
public class RequestHandler {

  @EJB
  RequestFacade requestFacade;

  @EJB
  RecipeFacade recipeFacade;

  @POST
  @Path("recipes")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response addRecipe(String body) {
    try {
      JSONObject obj = JSON.parseObject(body);

      return Response.status(201).entity(body).build();
    } catch (Exception e) {
      return Response.serverError().build();
    }
  }

  /**
   * Outputs all comments for a recipe.
   *
   * @return
   */
  @GET
  @Path("recipes")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getRecipes() {
    JSONArray items = requestFacade.getRecipePreviews(recipeFacade);
    System.out.println("JSON string: " + items.toJSONString());
    return Response.ok(items.toString()).build();
  }

  /**
   * Outputs all comments for a recipe.
   *
   * @return
   */
  @GET
  @Path("recipes/{recipeId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getRecipe(@PathParam("recipeId") String recipeId,
          @QueryParam("sid") String senderIdentifier) {
    JSONObject data;
    data = requestFacade.getRecipe(recipeId, senderIdentifier);
    if (data.containsKey("error")) {
      return Response.status(Response.Status.BAD_REQUEST)
              .entity(data.toJSONString()).build();
    }
    return Response.ok(data.toJSONString()).build();
  }

  /**
   * POSTs a comment.
   *
   * @return
   */
  @POST
  @Path("recipes/{recipeId}/comment")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response postComment(String body, @PathParam("recipeId") String recipeId) {

    try {
      JSONObject obj = null;
      try {
        obj = JSON.parseObject(body);
      } catch (Exception ex) {
        ex.printStackTrace();
      }

      JSONObject validationResult = RequestUtils.validateJsonHasProps(
              obj, "author", "message"
      );
      if (validationResult != null) {
        return Response.status(400).entity(validationResult.toJSONString()).build();
      }

      requestFacade.postComment(recipeId, obj.getString("author"), obj.getString("message"));

      return Response.status(201).entity(body).build();
    } catch (IllegalArgumentException e) {

      JSONObject out = new JSONObject();
      out.put("message", e.getMessage());
      return Response.status(400).entity(out.toJSONString()).build();

    } catch (SQLException e) {

      e.printStackTrace();
      return Response.serverError().build();

    } catch (Exception e) {

      e.printStackTrace();
      return Response.serverError().build();

    }

  }

  /**
   * POSTs a comment like.
   *
   * @return
   */
  @POST
  @Path("comment/:commentId/like")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response postCommentLike(String body, @PathParam("commentId") String commentId) {

    try {
      JSONObject obj = JSON.parseObject(body);

      JSONObject validationResult = RequestUtils.validateJsonHasProps(
              obj, "senderIdentifier"
      );
      if (validationResult != null) {
        return Response.status(400).entity(validationResult).build();
      }

      requestFacade.likeComment(commentId, obj.getString("senderIdentifier"));

      return Response.status(201).build();
    } catch (IllegalArgumentException e) {

      JSONObject out = new JSONObject();
      out.put("message", e.getMessage());
      return Response.status(400).entity(out).build();

    } catch (SQLException e) {

      e.printStackTrace();
      return Response.serverError().build();

    } catch (Exception e) {

      e.printStackTrace();
      return Response.serverError().build();

    }

  }
}
