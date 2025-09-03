package com.brokersystems.brokerapp.workflow.docs;

import java.util.Date;

/**
 * Created by HP on 8/4/2017.
 */
public class WfTaskHistDTO {

    private String assignee;
    private String id;
    private String name;
    private Date startTime;
    private Date endTime;


    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
