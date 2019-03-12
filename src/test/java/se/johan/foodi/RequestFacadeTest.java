/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.johan.foodi;

import se.johan.foodi.test.TestHelper;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.sql.SQLException;
import java.util.List;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.NamingException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import se.johan.foodi.model.Comment;
import se.johan.foodi.model.Recipe;
import se.johan.foodi.model.facade.CommentFacade;
import se.johan.foodi.model.facade.RecipeFacade;

/**
 * Runs automated integration tests towards the REST API request facade.
 *
 * @author Johan Svensson
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RequestFacadeTest {
  
  private static State state = new State();

  public RequestFacadeTest() {
  }

  @BeforeClass
  public static void setUpClass() {
    try {

      EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
      RecipeFacade recipeFacadeInstance = (RecipeFacade) container.getContext().lookup("java:global/classes/RequestFacade");
      clearTestRecipe(recipeFacadeInstance);
      setupTestRecipe(recipeFacadeInstance);
      container.close();

    } catch (Exception ex) {
      ex.printStackTrace();
      throw new RuntimeException(ex);
    }
  }

  /**
   * Clears existing test recipe, if exists.
   */
  private static void clearTestRecipe(RecipeFacade recipeFacade) throws SQLException {
    try {

      String testRecipeId = new TestHelper().getTestRecipeId();
      Recipe match = recipeFacade.find(testRecipeId);

      if (match == null) {
        return;
      }

      recipeFacade.remove(match);

    } catch (IllegalStateException ex) {
      //  test recipe doesn't exist; this is fine
    } catch (SQLException ex) {
      throw ex;
    }
  }

  private static void setupTestRecipe(RecipeFacade recipeFacade) throws SQLException {
    String existingTestRecipeId = null;
    try {
      existingTestRecipeId = new TestHelper().getTestRecipeId();
    } catch (IllegalStateException ex) {

    }
    if (existingTestRecipeId != null) {
      throw new IllegalStateException("Must clear test recipe before invoking setupTestRecipe");
    }

    Recipe testRecipe = new Recipe(null, TestHelper.TEST_RECIPE_NAME, "placeholder.jpg");
    recipeFacade.create(testRecipe);
  }

  @AfterClass
  public static void tearDownClass() {
    try (EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer()) {
      RecipeFacade recipeFacadeInstance = (RecipeFacade) container.getContext().lookup("java:global/classes/RecipeFacade");
      clearTestRecipe(recipeFacadeInstance);
      setupTestRecipe(recipeFacadeInstance);
    } catch (NamingException | SQLException ex) {
      throw new RuntimeException("tearDownClass failed", ex);
    }
  }

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {
  }

  @Test
  public void testPostComment_001() throws Exception {
    String recipeId = new TestHelper().getTestRecipeId();
    String author = "sam";
    String message = "Nice recipe, loved it.";
    EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
    RequestFacade requestFacadeInstance = (RequestFacade) container.getContext().lookup("java:global/classes/RequestFacade");
    CommentFacade commentFacadeInstance = (CommentFacade) container.getContext().lookup("java:global/classes/RequestFacade");
    Comment result = requestFacadeInstance.postComment(recipeId, author, message);
    Comment jpaFound = commentFacadeInstance.find(result.getId());
    container.close();

    assertThat(jpaFound.getAuthor(), equalTo(author));
    assertThat(jpaFound.getText(), equalTo(message));
    
    state.insertedCommentId = result.getId();
  }

  /**
   * Test of postComment method, of class RequestFacade.
   */
  @Test
  public void testPostComment_002_asReply() throws Exception {
    String recipeId = new TestHelper().getTestRecipeId();
    String author = "sam";
    String message = "Nice recipe, loved it.";
    Integer replyToId = state.insertedCommentId;
    EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
    RequestFacade instance = (RequestFacade) container.getContext().lookup("java:global/classes/RequestFacade");
    Comment replyComment = instance.postComment(recipeId, author, message, replyToId);
    container.close();
    
    Recipe original = instance.getRecipe(recipeId);
    boolean recipeContainsReply = false;
    
    for (Comment c : original.getCommentCollection()) {
      if (c.getId() == replyComment.getId() && c.getReplyToId().getId() == replyToId) {
        recipeContainsReply = true;
        break;
      }
    }
    
    assertThat("Recipe should have the reply", recipeContainsReply, is(true));
  }

  /**
   * Test of likeComment method, of class RequestFacade.
   */
  @Test
  public void testLikeComment() throws Exception {
    System.out.println("likeComment");
    Integer commentId = null;
    String senderIdentifier = "";
    EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
    RequestFacade instance = (RequestFacade) container.getContext().lookup("java:global/classes/RequestFacade");
    instance.likeComment(commentId, senderIdentifier);
    container.close();
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of getRecipePreviews method, of class RequestFacade.
   */
  @Test
  public void testGetRecipePreviews() throws Exception {
    System.out.println("getRecipePreviews");
    RecipeFacade facade = null;
    EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
    RequestFacade instance = (RequestFacade) container.getContext().lookup("java:global/classes/RequestFacade");

    JSONArray expResult = null;
    JSONArray result = instance.getRecipePreviews(facade);

    assertTrue("There aren't any fetched recipes", result.size() > 0);

    assertEquals(expResult, result);
    container.close();
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of getRecipe method, of class RequestFacade.
   */
  @Test
  public void testGetRecipe() throws Exception {
    System.out.println("getRecipe");
    String id = "";
    String senderIdentifier = "";
    EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
    RequestFacade instance = (RequestFacade) container.getContext().lookup("java:global/classes/RequestFacade");
    JSONObject expResult = null;
    JSONObject result = instance.getRecipeAsJSON(id, senderIdentifier);
    assertEquals(expResult, result);
    container.close();
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of reportComment method, of class RequestFacade.
   */
  @Test
  public void testReportComment() throws Exception {
    System.out.println("reportComment");
    Integer commentId = null;
    EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
    RequestFacade instance = (RequestFacade) container.getContext().lookup("java:global/classes/RequestFacade");
    boolean expResult = false;
    boolean result = instance.reportComment(commentId);
    assertEquals(expResult, result);
    container.close();
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }
  
  private static class State {
    public Integer insertedCommentId;
  }

}
