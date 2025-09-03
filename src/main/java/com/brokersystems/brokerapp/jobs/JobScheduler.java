package com.brokersystems.brokerapp.jobs;

import com.brokersystems.brokerapp.config.QuartzJobFactory;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JobScheduler extends SchedulerFactoryBean implements ApplicationContextAware
 {

    @Autowired
    private QuartzJobFactory autowiringJobFactory;

    private ApplicationContext applicationContext;

    private HashMap<Class<? extends AbstractJob>, Trigger> triggers;

    @Override
    public void setApplicationContext (final ApplicationContext context)
    {
        this.applicationContext = context;
    }

    @Override
    public void afterPropertiesSet () throws Exception
    {
        Map<String, AbstractJob> webappBeanNames =
                applicationContext.getBeansOfType (AbstractJob.class);
        triggers = new HashMap<> ();
        for (String webappBeanName : webappBeanNames.keySet ())
        {
            AbstractJob cron = webappBeanNames.get (webappBeanName);
            CronTriggerFactoryBean trigger = new CronTriggerFactoryBean ();
            JobDetail job = JobBuilder.newJob (cron.getClass ()).
                    storeDurably (true).build ();
            trigger.setJobDetail (job);
            trigger.setCronExpression (cron.getCronExpression ());
            trigger.setName (webappBeanName + "Trigger");
            trigger.afterPropertiesSet ();
            triggers.put (cron.getClass(), trigger.getObject ());
        }
        super.setTriggers (triggers.values ().toArray (
                new Trigger[triggers.size ()]));
        super.setJobFactory (autowiringJobFactory);
        super.afterPropertiesSet ();
    }
//
//    public Date getNextSystemCheckJobSchedule () throws SchedulerException
//    {
//        return triggers.get (TransactionsCleanupJob.class).getFireTimeAfter (new Date ());
//    }

}
