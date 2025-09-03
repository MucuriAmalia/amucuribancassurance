package com.brokersystems.brokerapp.setup.model;

import javax.persistence.*;

@Entity
@Table(name="sys_brk_escalation_config")
public class TaskEscalationConfig {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="esc_config_id")
    private Long escConfigId;

    @Column(name="esc_config_type")
    private String taskType;

    @Column(name="esc_config_time_per_user")
    private Integer timeLimitPerUser;

    @Column(name="esc_config_time_per_transaction")
    private Integer timeLimitPerTransaction;

    public Long getEscConfigId() {
        return escConfigId;
    }

    public void setEscConfigId(Long escConfigId) {
        this.escConfigId = escConfigId;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public Integer getTimeLimitPerUser() {
        return timeLimitPerUser;
    }

    public void setTimeLimitPerUser(Integer timeLimitPerUser) {
        this.timeLimitPerUser = timeLimitPerUser;
    }

    public Integer getTimeLimitPerTransaction() {
        return timeLimitPerTransaction;
    }

    public void setTimeLimitPerTransaction(Integer timeLimitPerTransaction) {
        this.timeLimitPerTransaction = timeLimitPerTransaction;
    }
}
