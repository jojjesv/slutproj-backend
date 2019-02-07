/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.johan.foodi.util;

import com.alibaba.fastjson.JSONObject;
import com.mysql.jdbc.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author johan
 */
public class ConnectionUtils {

    public static String querySingleString(String sql, String... params) throws SQLException {
        try (Connection connection = ConnectionFactory.getConnection()) {

            PreparedStatement stmt = connection.prepareStatement(sql);

            for (int i = 0; i < params.length; i++) {
                stmt.setString(i + 1, params[i]);
            }
            
            ResultSet result = stmt.executeQuery();
            if (!result.next()) {
                return null;
            }
            
            return result.getString(1);

        } catch (SQLException ex) {
            throw ex;
        }
    }
}
