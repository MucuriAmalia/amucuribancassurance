package com.brokersystems.brokerapp.setup.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

import javax.persistence.*;

@Entity
@Table(name="sys_brk_products")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductsDef extends AuditBaseEntity{


	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="pr_code")
	private Long proCode;
	@Column(name="pr_sht_desc", nullable=false,unique=true)
	private String proShtDesc;
	@Column(name="pr_desc", nullable=false, unique = true)
	private String proDesc;
	@Column(name="pr_pol_prefix")
	private String proPolPrefix;
	@Column(name="pr_clm_prefix")
	private String proClmPrefix;
	@Column(name="pr_multi_sub_class")
	private boolean multiSubClass;
	@Column(name="pr_renewable")
	private boolean renewable;
	@Column(name="pr_active")
	private boolean active;
	@Column(name="pr_min_prem")
	private BigDecimal minPrem;
	@Column(name="pr_install_allowed")
	private boolean installAllowed;
	@Column(name="pr_motor_product")
	private boolean motorProduct;
	@Column(name="pr_midnit_exp")
	private boolean midnightExp;
	@Column(name="pr_interface_type")
	private String interfaceType;
	@Column(name="pr_age_appli")
	private String ageApplicable;
	@ManyToOne(cascade= CascadeType.ALL)
	@JoinColumn(name="pr_bpg_code",nullable=false)
	private ProductGroupDef proGroup;
	@ManyToOne(cascade= CascadeType.ALL)
	@JoinColumn(name="pr_sap_code")
	private ProductCodes sapCode;
	@ManyToOne
	@JoinColumn(name="report_group")
	private ProductReportGroup productReportGroup;
	@Column(name="pr_wiba_pro")
	private String wibaProduct;

	@Column(name = "pr_policy_doc_url")
	private String productPolicyDocument;

	@Column(name = "pr_poldoc_content_type")
	private String contentType;

	@Transient
	MultipartFile file;

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getProductPolicyDocument() {
		return productPolicyDocument;
	}

	public void setProductPolicyDocument(String productPolicyDocument) {
		this.productPolicyDocument = productPolicyDocument;
	}

	public String getWibaProduct() {
		return wibaProduct;
	}

	public void setWibaProduct(String wibaProduct) {
		this.wibaProduct = wibaProduct;
	}

	public ProductReportGroup getProductReportGroup() {
		return productReportGroup;
	}

	public void setProductReportGroup(ProductReportGroup productReportGroup) {
		this.productReportGroup = productReportGroup;
	}



	@Column(name="pr_risk_note")
	private String riskNote;

	public Long getProCode() {
		return proCode;
	}

	public void setProCode(Long proCode) {
		this.proCode = proCode;
	}

	public String getProShtDesc() {
		return proShtDesc;
	}

	public void setProShtDesc(String proShtDesc) {
		this.proShtDesc = proShtDesc;
	}

	public String getProDesc() {
		return proDesc;
	}

	public void setProDesc(String proDesc) {
		this.proDesc = proDesc;
	}

	public String getProPolPrefix() {
		return proPolPrefix;
	}

	public void setProPolPrefix(String proPolPrefix) {
		this.proPolPrefix = proPolPrefix;
	}

	public String getProClmPrefix() {
		return proClmPrefix;
	}

	public void setProClmPrefix(String proClmPrefix) {
		this.proClmPrefix = proClmPrefix;
	}

	public boolean isMultiSubClass() {
		return multiSubClass;
	}

	public void setMultiSubClass(boolean multiSubClass) {
		this.multiSubClass = multiSubClass;
	}

	public boolean isRenewable() {
		return renewable;
	}

	public void setRenewable(boolean renewable) {
		this.renewable = renewable;
	}

	public ProductGroupDef getProGroup() {
		return proGroup;
	}

	public void setProGroup(ProductGroupDef proGroup) {
		this.proGroup = proGroup;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public BigDecimal getMinPrem() {
		return minPrem;
	}

	public void setMinPrem(BigDecimal minPrem) {
		this.minPrem = minPrem;
	}

	public boolean isInstallAllowed() {
		return installAllowed;
	}

	public void setInstallAllowed(boolean installAllowed) {
		this.installAllowed = installAllowed;
	}

	public boolean isMotorProduct() {
		return motorProduct;
	}

	public void setMotorProduct(boolean motorProduct) {
		this.motorProduct = motorProduct;
	}

	public boolean isMidnightExp() {
		return midnightExp;
	}

	public void setMidnightExp(boolean midnightExp) {
		this.midnightExp = midnightExp;
	}

	public String getInterfaceType() {
		return interfaceType;
	}

	public void setInterfaceType(String interfaceType) {
		this.interfaceType = interfaceType;
	}

	public String getAgeApplicable() {
		return ageApplicable;
	}

	public void setAgeApplicable(String ageApplicable) {
		this.ageApplicable = ageApplicable;
	}

	public String getRiskNote() {
		return riskNote;
	}

	public void setRiskNote(String riskNote) {
		this.riskNote = riskNote;
	}
}
