package com.brokersystems.brokerapp.medical.model;

import com.brokersystems.brokerapp.setup.model.SubclassClauses;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Created by peter on 5/1/2017.
 */
@Entity
@Table(name = "sys_brk_cat_clauses")
public class CategoryClauses {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "cc_id")
    private Long ccId;

    @ManyToOne
    @JoinColumn(name="pol_clau_sub_code",nullable=false)
    private SubclassClauses clause;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="fs_cat_id",nullable = false)
    private MedicalCategory category;

    @Column(name="pol_clau_new",length=1)
    private String newClause;

    @Column(name="pol_clau_header",nullable=false)
    private String clauHeading;

    @Column(name="pol_clau_editable",nullable=false)
    private boolean editable;

    @Column(name="pol_clau_wording",nullable=false)
    @Lob
    private String clauWording;

    public Long getCcId() {
        return ccId;
    }

    public void setCcId(Long ccId) {
        this.ccId = ccId;
    }

    public SubclassClauses getClause() {
        return clause;
    }

    public void setClause(SubclassClauses clause) {
        this.clause = clause;
    }

    public MedicalCategory getCategory() {
        return category;
    }

    public void setCategory(MedicalCategory category) {
        this.category = category;
    }

    public String getNewClause() {
        return newClause;
    }

    public void setNewClause(String newClause) {
        this.newClause = newClause;
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

        CategoryClauses that = (CategoryClauses) o;

        if (!clause.equals(that.clause)) return false;
        return category.equals(that.category);

    }

    @Override
    public int hashCode() {
        int result = clause.hashCode();
        result = 31 * result + category.hashCode();
        return result;
    }
}
