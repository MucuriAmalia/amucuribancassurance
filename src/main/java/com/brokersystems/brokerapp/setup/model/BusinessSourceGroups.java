package com.brokersystems.brokerapp.setup.model;

import javax.persistence.*;

/**
 * Created by waititu on 02/10/2017.
 */
@Entity
@Table(name = "sys_brk_business_src_grp")
public class BusinessSourceGroups {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "bsg_id")
    private Long srcGroupId;

    @Column(name = "bsg_sht_desc")
    private String shtDesc;

    @Column(name = "bsg_desc")
    private String desc;

    public Long getSrcGroupId() {
        return srcGroupId;
    }

    public void setSrcGroupId(Long srcGroupId) {
        this.srcGroupId = srcGroupId;
    }

    public String getShtDesc() {
        return shtDesc;
    }

    public void setShtDesc(String shtDesc) {
        this.shtDesc = shtDesc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
