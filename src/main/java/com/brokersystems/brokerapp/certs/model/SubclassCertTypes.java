package com.brokersystems.brokerapp.certs.model;

import com.brokersystems.brokerapp.setup.model.SubClassDef;

import javax.persistence.*;

/**
 * Created by waititu on 27/10/2018.
 */
@Entity
@Table(name="sys_brk_subclass_cert_types")
public class SubclassCertTypes {


    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="sbccert_id")
    private Long subclasscertId;

    @ManyToOne
    @JoinColumn(name="sbccert_cert_id",nullable=false)
    private CertTypes certType;

    @ManyToOne
    @JoinColumn(name="sbccert_sub_id",nullable=false)
    private SubClassDef subclass;


//    @Transient
//    private String subclassCertDesc;

    @Transient
    private String subclassDesc;

    public Long getSubclasscertId() {
        return subclasscertId;
    }

    public void setSubclasscertId(Long subclasscertId) {
        this.subclasscertId = subclasscertId;
    }

    public SubClassDef getSubclass() {
        return subclass;
    }

    public void setSubclass(SubClassDef subclass) {
        this.subclass = subclass;
    }

    public CertTypes getCertType() {
        return certType;
    }

    public void setCertType(CertTypes certType) {
        this.certType = certType;
    }

//    public String getSubclassCertDesc() {
//        return subclassCertDesc;
//    }
//
//    public void setSubclassCertDesc(String subclassCertDesc) {
//        this.subclassCertDesc = subclassCertDesc;
//    }

    public String getSubclassDesc() {
        return subclassDesc;
    }

    public void setSubclassDesc(String subclassDesc) {
        this.subclassDesc = subclassDesc;
    }
}
