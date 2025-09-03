package com.brokersystems.brokerapp.quotes.model;

import com.brokersystems.brokerapp.medical.model.MedicalCategory;
import com.brokersystems.brokerapp.setup.model.SubclassClauses;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Created by waititu on 09/08/2017.
 */
@Entity
@Table(name="sys_brk_mquot_clauses")
public class MedQuoteClauses {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="mq_clau_id")
    private Long mqClauId;


    @ManyToOne
    @JoinColumn(name="mq_clau_sub_code",nullable=false)
    private SubclassClauses clause;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="mq_quot_id")
    private QuoteTrans quotation;

    @Column(name="mq_clau_header",nullable=false)
    private String clauHeading;

    @Column(name="mq_clau_editable",nullable=false)
    private boolean editable;

    @Column(name="mq_clau_wording",nullable=false)
    @Lob
    private String clauWording;


    public SubclassClauses getClause() {
        return clause;
    }

    public void setClause(SubclassClauses clause) {
        this.clause = clause;
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

    public Long getMqClauId() {
        return mqClauId;
    }

    public void setMqClauId(Long mqClauId) {
        this.mqClauId = mqClauId;
    }

    public QuoteTrans getQuotation() {
        return quotation;
    }

    public void setQuotation(QuoteTrans quotation) {
        this.quotation = quotation;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((clause == null) ? 0 : clause.hashCode());
        result = prime * result + ((quotation == null) ? 0 : quotation.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MedQuoteClauses other = (MedQuoteClauses) obj;
        if (clause == null) {
            if (other.clause != null)
                return false;
        } else if (!clause.equals(other.clause))
            return false;
        if (quotation == null) {
            if (other.quotation != null)
                return false;
        } else if (!quotation.equals(other.quotation))
            return false;
        return true;
    }
}
