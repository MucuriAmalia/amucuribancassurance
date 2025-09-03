package com.brokersystems.brokerapp.workflow.docs;

import com.brokersystems.brokerapp.quotes.model.QuoteTrans;
import com.brokersystems.brokerapp.setup.model.ClientDef;
import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;

/**
 * Created by HP on 7/26/2017.
 */
@Entity
@Table(name = "sys_brk_wf_docs")
public class SysWfDocs {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "bwd_id")
    private Long docId;

    @Column(name = "bwd_doc_type")
    @Enumerated(EnumType.STRING)
    private DocType docType;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="bwd_pol_id")
    private PolicyTrans policyTrans;


    @XmlTransient
    @ManyToOne
    @JoinColumn(name="bwd_quot_id")
    private QuoteTrans quoteTrans;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="bwd_user_id")
    private User userId;

    //introducing client to the model
    @XmlTransient
    @ManyToOne
    @JoinColumn(name="bwd_client_id")
    private ClientDef clientId;

    @Column(name = "bwd_active")
    private boolean active;

    @Column(name = "bwd_doc_active_process")
    private String activeProcess;

    @Column(name = "bwd_task_id",unique = true)
    private String taskId;

    @Column(name = "bwd_medical")
    private String medical;

    @Column(name = "bwd_created_dt")
    private Date createdDate;


    public Long getDocId() {
        return docId;
    }

    public void setDocId(Long docId) {
        this.docId = docId;
    }

    public DocType getDocType() {
        return docType;
    }

    public void setDocType(DocType docType) {
        this.docType = docType;
    }

    public PolicyTrans getPolicyTrans() {
        return policyTrans;
    }

    public void setPolicyTrans(PolicyTrans policyTrans) {
        this.policyTrans = policyTrans;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getActiveProcess() {
        return activeProcess;
    }

    public void setActiveProcess(String activeProcess) {
        this.activeProcess = activeProcess;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getMedical() {
        return medical;
    }

    public void setMedical(String medical) {
        this.medical = medical;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public QuoteTrans getQuoteTrans() {
        return quoteTrans;
    }

    public void setQuoteTrans(QuoteTrans quoteTrans) {
        this.quoteTrans = quoteTrans;
    }

    public ClientDef getClientId() {
        return clientId;
    }

    public void setClientId(ClientDef clientId) {
        this.clientId = clientId;
    }
}
