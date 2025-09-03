package com.brokersystems.brokerapp.jobs;

import org.springframework.scheduling.quartz.QuartzJobBean;

public abstract class AbstractJob extends QuartzJobBean
{
    public abstract String getCronExpression ();
}
