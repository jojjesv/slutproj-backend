/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.johan.foodi.util;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author johan
 */
public abstract class ConnectionFactory {

  private ConnectionFactory() {

  }

  /**
   * @return a new, established connection to the database.
   */
  public static Connection getConnection() throws SQLException {
    boolean useDockerLink = false;
    //  whether to disable ssl (in docker container)
    boolean disableSsl = useDockerLink;
    
    String host = useDockerLink ? "mysql" : "localhost", username = "root", password = useDockerLink ? "seppo" : "";

    System.out.println("[getConnection] host=" + host);

    String db = "foodi";

    try {
      Class.forName("com.mysql.jdbc.Driver");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return (Connection) DriverManager.getConnection(
      "jdbc:mysql://" + host + "/" + db + (disableSsl
        ? "?useSSL=false&allowPublicKeyRetrieval=true" : ""),
      username, password
    );
  }
}
