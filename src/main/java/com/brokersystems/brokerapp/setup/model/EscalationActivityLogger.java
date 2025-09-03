package com.brokersystems.brokerapp.setup.model;

import com.brokersystems.brokerapp.users.model.MakerChecker;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "sys_brk_escalation_activity")
@Data
public class EscalationActivityLogger {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "esc_activity_id")
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "esc_activity_task")
    private MakerChecker task;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "esc_activity_level")
    private EscalationLevel escalationLevel;
    @Column(name = "activity_time")
    private Date activityTime;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "comments")
    private String comments;
    @Column(name = "esc_system_action")
    private String systemAction;

}
