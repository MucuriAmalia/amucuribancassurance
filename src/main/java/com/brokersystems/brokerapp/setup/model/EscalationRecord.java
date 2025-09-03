package com.brokersystems.brokerapp.setup.model;

import com.brokersystems.brokerapp.users.model.MakerChecker;
import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name="sys_brk_escalation_record")
public class EscalationRecord {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="esc_id")
    private Long escId;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mck_id")
    private MakerChecker task;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "esc_level_id")
    private EscalationLevel currentEscalationLevel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "verifier_id")
    private User verifier;

    @Column(name = "made_time")
    private Date madeTime;

    @Column(name = "checked_time")
    private Date checkTime;

    @Column(name = "task_initiation_time")
    private Date taskInitiationTime;

    @Type(type = "json")
    @Column(name = "previous_levels", columnDefinition = "json")
    private String previousLevels;


}
