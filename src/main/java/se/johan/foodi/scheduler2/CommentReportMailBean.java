/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.johan.foodi.scheduler2;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.johan.foodi.mail.Mailer;
import se.johan.foodi.model.Comment;
import se.johan.foodi.model.facade.CommentFacade;
import se.johan.foodi.scheduler.job.CommentReportMailTemplateBuilder;

/**
 *
 * @author johan
 */
@Stateless
@LocalBean
public class CommentReportMailBean {

  private static Logger logger = LoggerFactory.getLogger(CommentReportMailBean.class);
  
  @EJB
  private CommentFacade commentFacade;

  @Schedule(hour = "*", minute = "*", second = "*")
  public void schedule() {
    if (true) {
      return;
    }
    List<Comment> pending = getPendingReportedComments();

    if (pending.isEmpty()) {
      //  no pending reported comments
      return;
    }

    String text = new CommentReportMailTemplateBuilder()
      .comments(pending)
      .build();

    try {
      new Mailer().send(getRecipient(), "Comment reports", text);
    } catch (IOException | IllegalStateException ex) {
      logger.error("Error sending mail", ex);
      return;
    }
  }

  public List<Comment> getPendingReportedComments() {
    List<Comment> comments = commentFacade.findAll();
    List<Comment> reportedComments = new LinkedList<>();
    for (Comment c : comments) {
      if (c.getReported()) {
        reportedComments.add(c);
      }
    }
    return comments;
  }

  public String getRecipient() {
    return "jojjedeveloper@gmail.com";
  }
}
