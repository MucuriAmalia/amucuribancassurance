package com.brokersystems.brokerapp.setup.model;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by HP on 3/16/2018.
 */
@Entity
@Table(name="sys_brk_section_limits")
public class SectionLimits {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="sl_id")
    private Long id;


    @ManyToOne
    @JoinColumn(name="sl_prem_id",nullable=false)
    private PremRatesDef premRatesDef;


    @ManyToOne
    @JoinColumn(name="sl_clause_id",nullable=false)
    private SubclassClauses clausesDef;

    @Column(name = "sl_value")
    private BigDecimal value;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PremRatesDef getPremRatesDef() {
        return premRatesDef;
    }

    public void setPremRatesDef(PremRatesDef premRatesDef) {
        this.premRatesDef = premRatesDef;
    }

    public SubclassClauses getClausesDef() {
        return clausesDef;
    }

    public void setClausesDef(SubclassClauses clausesDef) {
        this.clausesDef = clausesDef;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "SectionLimits{" +
                "id=" + id +
                ", premRatesDef=" + premRatesDef +
                ", clausesDef=" + clausesDef +
                '}';
    }
}
