/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.johan.foodi.scheduler;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author johan
 */
public class CommentReportMailServletListener implements ServletContextListener {
  private static Logger logger =
    LoggerFactory.getLogger(CommentReportMailServletListener.class);
  
  @Override
  public void contextInitialized(ServletContextEvent sce) {
    try {
      new CommentReportMailScheduler().schedule();
    } catch (SchedulerException ex) {
      logger.error("Error running comment report mail scheduler", ex);
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
  }
}
