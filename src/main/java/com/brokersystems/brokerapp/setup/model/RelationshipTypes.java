package com.brokersystems.brokerapp.setup.model;

import javax.persistence.*;

/**
 * Created by waititu on 25/04/2019.
 */
@Entity
@Table(name="sys_brk_relationship_types")
public class RelationshipTypes {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="rt_type_id")
    private Long typeId;

    @Column(name="rt_type",nullable=false)
    private String relationType;

    @Column(name="rt_type_desc",nullable=false)
    private String relationDesc;

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public String getRelationDesc() {
        return relationDesc;
    }

    public void setRelationDesc(String relationDesc) {
        this.relationDesc = relationDesc;
    }
}
