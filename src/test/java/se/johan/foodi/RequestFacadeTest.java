/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.johan.foodi;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.List;
import javax.ejb.embeddable.EJBContainer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import se.johan.foodi.model.Comment;
import se.johan.foodi.model.facade.RecipeFacade;

/**
 *
 * @author johan
 */
public class RequestFacadeTest {
  
  public RequestFacadeTest() {
  }
  
  @BeforeClass
  public static void setUpClass() {
  }
  
  @AfterClass
  public static void tearDownClass() {
  }
  
  @Before
  public void setUp() {
  }
  
  @After
  public void tearDown() {
  }

  /**
   * Test of postComment method, of class RequestFacade.
   */
  @Test
  public void testPostComment_3args() throws Exception {
    System.out.println("postComment");
    String recipeId = "";
    String author = "";
    String message = "";
    EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
    RequestFacade instance = (RequestFacade)container.getContext().lookup("java:global/classes/RequestFacade");
    instance.postComment(recipeId, author, message);
    container.close();
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of postComment method, of class RequestFacade.
   */
  @Test
  public void testPostComment_4args() throws Exception {
    System.out.println("postComment");
    String recipeId = "";
    String author = "";
    String message = "";
    Integer replyToId = null;
    EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
    RequestFacade instance = (RequestFacade)container.getContext().lookup("java:global/classes/RequestFacade");
    instance.postComment(recipeId, author, message, replyToId);
    container.close();
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
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
    RequestFacade instance = (RequestFacade)container.getContext().lookup("java:global/classes/RequestFacade");
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
    RequestFacade instance = (RequestFacade)container.getContext().lookup("java:global/classes/RequestFacade");
    
    JSONArray expResult = null;
    JSONArray result = instance.getRecipePreviews(facade);
    
    assertTrue("There are some fetched recipes", result.size() > 0);
    
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
    RequestFacade instance = (RequestFacade)container.getContext().lookup("java:global/classes/RequestFacade");
    JSONObject expResult = null;
    JSONObject result = instance.getRecipe(id, senderIdentifier);
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
    RequestFacade instance = (RequestFacade)container.getContext().lookup("java:global/classes/RequestFacade");
    boolean expResult = false;
    boolean result = instance.reportComment(commentId);
    assertEquals(expResult, result);
    container.close();
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }
  
}
