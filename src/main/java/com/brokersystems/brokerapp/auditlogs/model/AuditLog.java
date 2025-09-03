package com.brokersystems.brokerapp.auditlogs.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sys_brk_audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username; // User Identifier
    @Column(nullable = false, columnDefinition = "TEXT")
    private String action; // Action Taken
    @Column(nullable = false)
    private Date timestamp; // Timestamp of the Action
    @Column(nullable = false, columnDefinition = "TEXT")
    private String resource; // Resource Accessed
    @Column(columnDefinition = "TEXT")
    private String details; // Additional Details

    // Standard getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
