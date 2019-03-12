package se.johan.foodi.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import se.johan.foodi.util.ConnectionFactory;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author johan
 */
public class TestHelper {
  public static final String TEST_RECIPE_NAME = "__test";
  public static final String TEST_SENDER_IDENTIFIER = "__sid";
  
  public String getTestRecipeId() throws IllegalStateException, SQLException {
    try (Connection c = ConnectionFactory.getConnection()) {
      ResultSet match = c.prepareStatement(
        String.format("SELECT id FROM recipe WHERE name = '%s'", TEST_RECIPE_NAME)
      ).executeQuery();
      
      if (!match.next()) {
        throw new IllegalStateException("No test recipe found!");
      }
      
      return match.getString(1);
    } catch (SQLException ex) {
      throw ex;
    }
  }
}
