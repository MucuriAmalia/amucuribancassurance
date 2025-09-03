package com.brokersystems.brokerapp.auditlogs.model;

import java.util.Date;

public class SystemLogs {

    private String username;
    private String hostname;
    private String machinename;
    private String address;
    private String action;
    private Date timetamp;

    public SystemLogs(final String username, final String hostname,final String machinename,final String address, final String action, final Date timetamp) {
        this.username = username;
        this.hostname = hostname;
        this.machinename = machinename;
        this.address = address;
        this.action = action;
        this.timetamp = timetamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getMachinename() {
        return machinename;
    }

    public void setMachinename(String machinename) {
        this.machinename = machinename;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Date getTimetamp() {
        return timetamp;
    }

    public void setTimetamp(Date timetamp) {
        this.timetamp = timetamp;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
