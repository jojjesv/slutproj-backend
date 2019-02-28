/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.johan.foodi.util;

import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author johan
 */
public class RequestUtils {

  /**
   * Validates that a json object has required props.
   *
   * @param props
   * @return A json object to output in case the validation fails.
   */
  public static JSONObject validateJsonHasProps(JSONObject original, String... props) {
    if (original == null) {
      JSONObject out = new JSONObject();
      out.put("error", "invalidJson");
      return out;
    }
    
    List<String> missingProps = new ArrayList<String>();

    for (String prop : props) {
      if (!original.containsKey(prop)) {
        missingProps.add(prop);
      }
    }

    if (!missingProps.isEmpty()) {
      JSONObject out = new JSONObject();
      out.put("error", "missingParams");

      StringBuilder paramsStr = new StringBuilder();
      for (String prop : missingProps) {
        paramsStr.append(prop);
        paramsStr.append(",");
      }

      //  Trim trailing delimiter
      paramsStr.setLength(paramsStr.length() - 1);

      out.put("message", String.format("Missing parameters: %s", paramsStr));

      return out;
    }

    //  Validation passed
    return null;
  }
}
