/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.johan.foodi.mail;

import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Module for sending mail.
 *
 * @author johan
 */
public class Mailer {

  private static Logger logger
    = LoggerFactory.getLogger(Mailer.class);

  public void send(String recipient, String subject, String text) throws IOException, IllegalStateException {
    HttpClient client = new HttpClient();
    PostMethod post = new PostMethod(
      "http://140.82.38.80:3000/mail?key=zrpWWMJ4kyEp59r8"
    );

    JSONObject jsonPayload = new JSONObject();
    jsonPayload.put("to", recipient);
    jsonPayload.put("subject", subject);
    jsonPayload.put("message", text);

    try {
      post.setRequestEntity(
        new StringRequestEntity(
          jsonPayload.toJSONString(), "application/json", "UTF-8"
        )
      );
    } catch (UnsupportedEncodingException ex) {
      logger.error("Error sending mail", ex);
      throw new RuntimeException(ex);
    }

    int statusCode = client.executeMethod(post);

    if (statusCode != 200) {
      throw new IllegalStateException("Mail failed with status code: " + statusCode);
    }
  }
}
