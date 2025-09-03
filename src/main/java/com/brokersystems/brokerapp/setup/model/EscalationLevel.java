package com.brokersystems.brokerapp.setup.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="sys_brk_escalation_level")
public class EscalationLevel {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="esc_level_id")
    private Long escMatrixId;

    @Column(name="esc_level_role_name")
    private String roleName;

    @Column(name="esc_hierarchy_level")
    private Integer hierarchyLevel;
}
