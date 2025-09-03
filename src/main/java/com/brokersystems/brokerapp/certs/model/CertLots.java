package com.brokersystems.brokerapp.certs.model;

import javax.persistence.*;

import com.brokersystems.brokerapp.setup.model.AccountDef;
import com.brokersystems.brokerapp.setup.model.SubClassDef;
import com.brokersystems.brokerapp.setup.model.User;

@Entity
@Table(name="sys_brk_certs")
public class CertLots {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="cer_id")
	private Long cerId;
	
	@ManyToOne
	@JoinColumn(name="cer_acct_code",nullable=false)
	private AccountDef underwriter;
	
	@ManyToOne
	@JoinColumn(name="cer_sub_id",nullable=false)
	private SubClassDef subclass;

	@ManyToOne
	@JoinColumn(name="cer_sbccert_id",nullable=true)
	private SubclassCertTypes subclassCertType;
	
	@Column(name="cer_no_from",nullable=false)
	private Long noFrom;

	@Column(name="cer_avail_no_from")
	private Long availableNoFrom;
	
	@Column(name="cer_no_to",nullable=false)
	private Long noTo;
	
	@ManyToOne
	@JoinColumn(name="cer_cert_code",nullable=false)
	private CertTypes certTypes;

	@Column(name="cer_count")
	private Long certCount;

	@Transient
	private String certLotDesc;


	public Long getCerId() {
		return cerId;
	}

	public void setCerId(Long cerId) {
		this.cerId = cerId;
	}

	public AccountDef getUnderwriter() {
		return underwriter;
	}

	public void setUnderwriter(AccountDef underwriter) {
		this.underwriter = underwriter;
	}

	public SubClassDef getSubclass() {
		return subclass;
	}

	public void setSubclass(SubClassDef subclass) {
		this.subclass = subclass;
	}

	public Long getNoFrom() {
		return noFrom;
	}

	public void setNoFrom(Long noFrom) {
		this.noFrom = noFrom;
	}

	public Long getNoTo() {
		return noTo;
	}

	public void setNoTo(Long noTo) {
		this.noTo = noTo;
	}

	public CertTypes getCertTypes() {
		return certTypes;
	}

	public void setCertTypes(CertTypes certTypes) {
		this.certTypes = certTypes;
	}


	public Long getCertCount() {
		return certCount;
	}

	public void setCertCount(Long certCount) {
		this.certCount = certCount;
	}

	public Long getAvailableNoFrom() {
		return availableNoFrom;
	}

	public void setAvailableNoFrom(Long availableNoFrom) {
		this.availableNoFrom = availableNoFrom;
	}

	public String getCertLotDesc() {
		return certLotDesc;
	}

	public void setCertLotDesc(String certLotDesc) {
		this.certLotDesc = certLotDesc;
	}

	public SubclassCertTypes getSubclassCertType() {
		return subclassCertType;
	}

	public void setSubclassCertType(SubclassCertTypes subclassCertType) {
		this.subclassCertType = subclassCertType;
	}
}
