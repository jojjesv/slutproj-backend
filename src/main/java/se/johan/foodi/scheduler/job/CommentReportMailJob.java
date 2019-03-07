/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.johan.foodi.scheduler.job;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.jobs.ee.ejb.EJBInvokerJob;
import se.johan.foodi.mail.Mailer;
import se.johan.foodi.model.Comment;
import se.johan.foodi.model.facade.CommentFacade;

/**
 *
 * @author johan
 */
@Stateless
public class CommentReportMailJob extends EJBInvokerJob {

  @EJB
  private CommentFacade commentFacade;

  @Override
  public void execute(JobExecutionContext jec) throws JobExecutionException {
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
      throw new JobExecutionException(ex);
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
