/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.johan.foodi.scheduler;

import org.quartz.CronScheduleBuilder;
import org.quartz.DateBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import se.johan.foodi.scheduler.job.CommentReportMailJob;

/**
 *
 * @author johan
 */
public class CommentReportMailScheduler {

  /**
   * Initializes the scheduler with default interval.
   */
  public void schedule() throws SchedulerException {
    JobDetail detail = JobBuilder.newJob(CommentReportMailJob.class)
      .build();
    
    Trigger trigger = TriggerBuilder.newTrigger()
      .withIdentity("comment_report", "group1")
      .startNow()
      .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(8, 0))
      .build();
    
    
    Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
    scheduler.start();
    scheduler.scheduleJob(detail, trigger);
  }
}
