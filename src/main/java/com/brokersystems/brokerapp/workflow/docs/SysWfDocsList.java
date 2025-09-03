package com.brokersystems.brokerapp.workflow.docs;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Created by HP on 7/26/2017.
 */
@Entity
@Table(name = "sys_brk_wf_doc_list")
public class SysWfDocsList {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "bdl_id")
    private Long bdlId;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="bpd_wf_doc_id")
    private SysWfDocs wfDocs;

    @Column(name = "bpd_user")
    private String assignedUser;

    public Long getBdlId() {
        return bdlId;
    }

    public void setBdlId(Long bdlId) {
        this.bdlId = bdlId;
    }

    public SysWfDocs getWfDocs() {
        return wfDocs;
    }

    public void setWfDocs(SysWfDocs wfDocs) {
        this.wfDocs = wfDocs;
    }

    public String getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(String assignedUser) {
        this.assignedUser = assignedUser;
    }
}
