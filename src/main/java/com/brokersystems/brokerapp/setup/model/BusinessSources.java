package com.brokersystems.brokerapp.setup.model;

import javax.persistence.*;

/**
 * Created by waititu on 02/10/2017.
 */
@Entity
@Table(name = "sys_brk_business_sources")
public class BusinessSources {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "bs_id")
    private Long srcId;

    @Column(name = "bs_sht_desc")
    private String shtDesc;

    @ManyToOne
    @JoinColumn(name = "bs_bsg_id")
    private BusinessSourceGroups businessSourceGroup;


    @Column(name = "bs_desc")
    private String desc;

    public Long getSrcId() {
        return srcId;
    }

    public void setSrcId(Long srcId) {
        this.srcId = srcId;
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

    public BusinessSourceGroups getBusinessSourceGroup() {
        return businessSourceGroup;
    }

    public void setBusinessSourceGroup(BusinessSourceGroups businessSourceGroup) {
        this.businessSourceGroup = businessSourceGroup;
    }
}
