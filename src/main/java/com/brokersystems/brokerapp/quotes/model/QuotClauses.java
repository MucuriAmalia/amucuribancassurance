package com.brokersystems.brokerapp.quotes.model;

import com.brokersystems.brokerapp.setup.model.SubclassClauses;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Created by peter on 3/15/2017.
 */
@Entity
@Table(name="sys_brk_quot_clauses")
public class QuotClauses {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="qp_clau_id")
    private Long qpClauId;


    @ManyToOne
    @JoinColumn(name="qp_clau_sub_code",nullable=false)
    private SubclassClauses clause;


    @XmlTransient
    @ManyToOne
    @JoinColumn(name="qp_clau_pr_id")
    private QuoteProTrans proTrans;

    @Column(name="qp_clau_header",nullable=false)
    private String clauHeading;

    @Column(name="qp_clau_editable",nullable=false)
    private boolean editable;

    @Column(name="qp_clau_wording",nullable=false)
    @Lob
    private String clauWording;

    public Long getQpClauId() {
        return qpClauId;
    }

    public void setQpClauId(Long qpClauId) {
        this.qpClauId = qpClauId;
    }

    public SubclassClauses getClause() {
        return clause;
    }

    public void setClause(SubclassClauses clause) {
        this.clause = clause;
    }

    public QuoteProTrans getProTrans() {
        return proTrans;
    }

    public void setProTrans(QuoteProTrans proTrans) {
        this.proTrans = proTrans;
    }

    public String getClauHeading() {
        return clauHeading;
    }

    public void setClauHeading(String clauHeading) {
        this.clauHeading = clauHeading;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getClauWording() {
        return clauWording;
    }

    public void setClauWording(String clauWording) {
        this.clauWording = clauWording;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QuotClauses that = (QuotClauses) o;

        if (clause != null ? !clause.equals(that.clause) : that.clause != null) return false;
        return proTrans != null ? proTrans.equals(that.proTrans) : that.proTrans == null;

    }

    @Override
    public int hashCode() {
        int result = clause != null ? clause.hashCode() : 0;
        result = 31 * result + (proTrans != null ? proTrans.hashCode() : 0);
        return result;
    }
}
