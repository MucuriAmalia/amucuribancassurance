/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.brokersystems.brokerapp.jobs.jobs.domain;

import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Table(name = "broker_jobs")
public class ScheduledJobDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String jobName;

    @Column(name = "display_name")
    private String jobDisplayName;

    @Column(name = "cron_expression")
    private String cronExpression;

    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Column(name = "task_priority")
    private Short taskPriority;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "previous_run_start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date previousRunStartTime;

    @Column(name = "next_run_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date nextRunTime;

    @Column(name = "job_key")
    private String jobKey;

    @Column(name = "initializing_errorlog")
    private String errorLog;

    @Column(name = "is_active")
    private boolean activeSchedular;

    @Column(name = "currently_running")
    private boolean currentlyRunning;

    @Column(name = "updates_allowed")
    private boolean updatesAllowed;

    @Column(name = "scheduler_group")
    private Short schedulerGroup;

    @Column(name = "is_misfired")
    private boolean triggerMisfired;

    protected ScheduledJobDetail() {

    }

    public String getJobName() {
        return this.jobName;
    }

    public String getCronExpression() {
        return this.cronExpression;
    }

    public Short getTaskPriority() {
        return this.taskPriority;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public String getJobKey() {
        return this.jobKey;
    }

    public Short getSchedulerGroup() {
        return this.schedulerGroup;
    }

    public boolean isActiveSchedular() {
        return this.activeSchedular;
    }

    public void updateCronExpression(final String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public void updatePreviousRunStartTime(final Date previousRunStartTime) {
        this.previousRunStartTime = previousRunStartTime;
    }

    public Date getNextRunTime() {
        return this.nextRunTime;
    }

    public void updateNextRunTime(final Date nextRunTime) {
        this.nextRunTime = nextRunTime;
    }

    public void updateJobKey(final String jobKey) {
        this.jobKey = jobKey;
    }

    public void updateErrorLog(final String errorLog) {
        this.errorLog = errorLog;
    }

    public boolean isCurrentlyRunning() {
        return this.currentlyRunning;
    }

    public void updateCurrentlyRunningStatus(final boolean currentlyRunning) {
        this.currentlyRunning = currentlyRunning;
    }

    public Long getId() {
        return id;
    }

    public boolean isTriggerMisfired() {
        return this.triggerMisfired;
    }

    public void updateTriggerMisfired(final boolean triggerMisfired) {
        this.triggerMisfired = triggerMisfired;
    }

}
