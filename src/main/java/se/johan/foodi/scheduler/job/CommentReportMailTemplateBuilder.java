/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.johan.foodi.scheduler.job;

import java.util.List;
import se.johan.foodi.model.Comment;

/**
 * Builds a template to send to admins regarding reported comments.
 *
 * @author johan
 */
public class CommentReportMailTemplateBuilder {

  private List<Comment> comments;

  public List<Comment> getComments() {
    return comments;
  }

  public CommentReportMailTemplateBuilder comments(List<Comment> comments) {
    this.comments = comments;
    return this;
  }

  public String build() {
    if (comments == null || comments.isEmpty()) {
      throw new IllegalStateException("Must provide some comments with #comments()");
    }

    StringBuilder str = new StringBuilder();

    str.append(
      "Hello,\n"
    );
    str.append(
      String.format(
        "There are %d comment%s which are reported and requires attention.\n\n",
        comments.size(),
        comments.size() > 1 ? "s": ""
      )
    );
    str.append(
      "Here are the comments:\n"
    );
    for (Comment c : comments) {
      str.append(
        String.format(
          "%s wrote on %s: %s\n",
          c.getAuthor(), c.getRecipe().getName(), c.getText()
        )
      );
    }

    return str.toString();
  }
}
