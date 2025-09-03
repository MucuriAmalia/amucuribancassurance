package com.brokersystems.brokerapp.config;

import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.stereotype.Component;

@Component
public class QuartzJobFactory extends AdaptableJobFactory implements ApplicationContextAware
{
    private transient AutowireCapableBeanFactory beanFactory;

    @Override
    public void setApplicationContext (final ApplicationContext context)
    {
        beanFactory = context.getAutowireCapableBeanFactory ();
    }

    @Override
    public Job newJob (TriggerFiredBundle bundle, Scheduler scheduler)
            throws SchedulerException
    {
        Job job = super.newJob (bundle, scheduler);
        beanFactory.autowireBean (job);
        return job;
    }
}
