package com.waylau.spring.cloud.weather.config;

import org.quartz.Trigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.waylau.spring.cloud.weather.job.ScheduledTasks;

/**
 * 定时任务配置类
 */
@Configuration
public class QuartzConfiguration
{
	// 配置定时任务 ScheduledTasks为我们需要执行的任务类以及需要执行方法，刚才定义的类
	@Bean(name = "mfactoryBean")
	public MethodInvokingJobDetailFactoryBean mfactoryBean(ScheduledTasks scheduledTasks)
	{
		MethodInvokingJobDetailFactoryBean md = new MethodInvokingJobDetailFactoryBean();
		// 设置分组
		md.setGroup("group---1");
		// 设置当前监听器的名称
		md.setName("quartz---1");
		// 是否并发执行，就是前一个（次）任务未执行完成，后一个（次）任务是否执行，
		md.setConcurrent(false);
		md.setTargetObject(scheduledTasks);
		// 监听的方法
		md.setTargetMethod("reportCurrentByCron");
		return md;
	}

	// 配置定时任务的触发器，也就是什么时候触发执行定时任务
	@Bean(name = "cfactoryBean")
	public CronTriggerFactoryBean cfactoryBean(MethodInvokingJobDetailFactoryBean mfactoryBean)
	{
		CronTriggerFactoryBean cfb = new CronTriggerFactoryBean();
		cfb.setJobDetail(mfactoryBean.getObject());
		cfb.setCronExpression("*/50 * * * * ?"); // 10秒执行一次
		cfb.setName("quartz---cron");
		return cfb;

	}

  // 定义quartz调度工厂
  @Bean  
  public SchedulerFactoryBean sfactoryBean(Trigger cronJobTrigger){  
    SchedulerFactoryBean sfb=new SchedulerFactoryBean();  
    
    // 延时启动，应用启动1秒后    
    sfb.setStartupDelay(5);    
    sfb.setTriggers(cronJobTrigger);  
    return sfb;  
       
  }
}