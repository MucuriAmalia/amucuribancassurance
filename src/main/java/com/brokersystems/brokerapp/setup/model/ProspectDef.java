package com.brokersystems.brokerapp.setup.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by peter on 4/8/2017.
 */
@Entity
@Table(name="sys_brk_prospects")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class ProspectDef {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="prs_id")
    private Long tenId;

    @Column(name="prs_fname",nullable=false)
    private String fname;

    @Column(name="prs_sht_desc",nullable=false,unique=true)
    private String prospShtDesc;

    @Column(name="prs_onames",nullable=false)
    private String otherNames;

    @Column(name="prs_phone")
    private String phoneNo;

    @Column(name="prs_pin")
    private String pinNo;

    @Column(name="prs_idno")
    private String idNo;

    @Column(name="prs_passport_no")
    private String passportNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="prs_clnt_type",nullable=false)
    private ClientTypes tenantType;

    @Column(name="prs_status",nullable=false)
    private String status;

    @Column(name="prs_address")
    private String address;

    @Column(name="prs_email")
    private String emailAddress;

    @Column(name="prs_gender")
    private String gender;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="prs_clnt_code")
    private ClientDef convertedReference;

    @Column(name="prs_datereg")
    private Date dateregistered;

    @Column(name="prs_datetermin")
    private Date dateterminated;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="prs_brn_code")
    private OrgBranch registeredbrn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="prs_cou_code")
    private Country country;

    @Column(name="prs_sms_number")
    private String smsNumber;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="prs_sms_prefix")
    private MobilePrefixDef smsPrefix;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="prs_phone_prefix")
    private MobilePrefixDef phonePrefix;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="prs_title_id")
    private ClientTitle clientTitle;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="prs_sector_id")
    private SectorDef clientSector;

    @Column(name="client_tel_number")
    private String officeTel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="prs_occup_id")
    private Occupation occupation ;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    @Column(name="prs_dob")
    private Date dob;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="client_town_code")
    private Town town;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="client_post_code")
    private PostalCodesDef postalCodesDef;

    @Transient
    private Long prodId;

    @Transient
    private Long quotationId;

    @Column(name = "prs_comment")
    private String comment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "prs_branch")
    private OrgBranch branch;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "prs_sub_agent")
    private AccountDef acc;

    @Column(name = "prs_category")
    private String category;

    @ManyToOne
    @JoinColumn(name ="prs_created_by")
    private User createdBy;

    @Column(name = "prs_resident_status")
    private String residentStatus;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="client_segment_code")
    private Segments segments;

    public Segments getSegments() {
        return segments;
    }

    public void setSegments(Segments segments) {
        this.segments = segments;
    }

    public String getResidentStatus() {
        return residentStatus;
    }

    public void setResidentStatus(String residentStatus) {
        this.residentStatus = residentStatus;
    }

    public Long getTenId() {
        return tenId;
    }

    public void setTenId(Long tenId) {
        this.tenId = tenId;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getOtherNames() {
        return otherNames;
    }

    public void setOtherNames(String otherNames) {
        this.otherNames = otherNames;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getPinNo() {
        return pinNo;
    }

    public void setPinNo(String pinNo) {
        this.pinNo = pinNo;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public ClientTypes getTenantType() {
        return tenantType;
    }

    public void setTenantType(ClientTypes tenantType) {
        this.tenantType = tenantType;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProspShtDesc() {
        return prospShtDesc;
    }

    public void setProspShtDesc(String prospShtDesc) {
        this.prospShtDesc = prospShtDesc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ClientDef getConvertedReference() {
        return convertedReference;
    }

    public void setConvertedReference(ClientDef convertedReference) {
        this.convertedReference = convertedReference;
    }

    public Long getProdId() {
        return prodId;
    }

    public void setProdId(Long prodId) {
        this.prodId = prodId;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Date getDateregistered() {
        return dateregistered;
    }

    public void setDateregistered(Date dateregistered) {
        this.dateregistered = dateregistered;
    }

    public Date getDateterminated() {
        return dateterminated;
    }

    public void setDateterminated(Date dateterminated) {
        this.dateterminated = dateterminated;
    }

    public OrgBranch getRegisteredbrn() {
        return registeredbrn;
    }

    public void setRegisteredbrn(OrgBranch registeredbrn) {
        this.registeredbrn = registeredbrn;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getSmsNumber() {
        return smsNumber;
    }

    public void setSmsNumber(String smsNumber) {
        this.smsNumber = smsNumber;
    }

    public MobilePrefixDef getSmsPrefix() {
        return smsPrefix;
    }

    public void setSmsPrefix(MobilePrefixDef smsPrefix) {
        this.smsPrefix = smsPrefix;
    }

    public MobilePrefixDef getPhonePrefix() {
        return phonePrefix;
    }

    public void setPhonePrefix(MobilePrefixDef phonePrefix) {
        this.phonePrefix = phonePrefix;
    }

    public ClientTitle getClientTitle() {
        return clientTitle;
    }

    public void setClientTitle(ClientTitle clientTitle) {
        this.clientTitle = clientTitle;
    }

    public SectorDef getClientSector() {
        return clientSector;
    }

    public void setClientSector(SectorDef clientSector) {
        this.clientSector = clientSector;
    }

    public Occupation getOccupation() {
        return occupation;
    }

    public void setOccupation(Occupation occupation) {
        this.occupation = occupation;
    }

    public String getPassportNo() {
        return passportNo;
    }

    public void setPassportNo(String passportNo) {
        this.passportNo = passportNo;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public Long getQuotationId() {
        return quotationId;
    }

    public void setQuotationId(Long quotationId) {
        this.quotationId = quotationId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOfficeTel() {
        return officeTel;
    }

    public void setOfficeTel(String officeTel) {
        this.officeTel = officeTel;
    }

    public Town getTown() {
        return town;
    }

    public void setTown(Town town) {
        this.town = town;
    }

    public PostalCodesDef getPostalCodesDef() {
        return postalCodesDef;
    }

    public void setPostalCodesDef(PostalCodesDef postalCodesDef) {
        this.postalCodesDef = postalCodesDef;
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public OrgBranch getBranch() {
        return branch;
    }

    public void setBranch(OrgBranch branch) {
        this.branch = branch;
    }

    public AccountDef getAcc() {
        return acc;
    }

    public void setAcc(AccountDef acc) {
        this.acc = acc;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
}
