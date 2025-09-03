package com.brokersystems.brokerapp.setup.model;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="sys_brk_clients")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class ClientDef {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="client_id")
	private Long tenId;

	@Column(name="client_sht_desc",nullable=false,unique=true)
	private String tenantNumber;

	@Column(name="client_fname",nullable=false)
	private String fname;

	@Column(name="client_onames",nullable=false)
	private String otherNames;

	@Column(name="client_address")
	private String address;

	@Column(name="client_cif")
	//@JsonIgnore
	private String clientCIF;


	@Column(name="client_segment")
	private String clientSegment;

	@Column(name="client_email")
	private String emailAddress;

	@Column(name="client_phone",nullable=false)
	private String phoneNo;

	@Column(name="client_dob")
	@JsonFormat(pattern = "dd/MM/yyyy")
	@Temporal(TemporalType.DATE)
	private Date dob;

	@Column(name="client_date_created")
	private Date     dateCreated;

	@Column(name="client_pin", nullable = false)
	//@JsonIgnore
	private String pinNo;

	@Column(name="client_idno", nullable = false)
	//@JsonIgnore
	private String idNo;

	@Column(name="client_passport_no")
	//@JsonIgnore
	private String passportNo;

	@Column(name="client_photo_url")
	private String photoUrl;
	//@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="client_clnt_type",nullable=false)
	private ClientTypes tenantType;

	@Column(name="client_datereg",nullable=false)
	private Date dateregistered;

	@Column(name="client_datetermin")
	private Date dateterminated;

	@Column(name="client_status",nullable=false)
	private String status;

	@Column(name = "comments")
	//@JsonIgnore
	private String comment;

	@Column(name = "client_hash_code")
	@JsonIgnore
	private String clientHash;

	//@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="client_brn_code",nullable=false)
	private OrgBranch registeredbrn;

	//@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="client_town_code")
	private Town town;

	//@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="client_post_code")
	private PostalCodesDef postalCodesDef;

	//@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="client_segment_code")
	private Segments segments;

	public Segments getSegments() {
		return segments;
	}

	public void setSegments(Segments segments) {
		this.segments = segments;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getClientHash() {
		return clientHash;
	}

	public void setClientHash(String clientHash) {
		this.clientHash = clientHash;
	}

	@Column(name="client_gender")
	private String gender;

	@ManyToOne
	@JoinColumn(name="client_cou_code")
	private Country country;

	@Column(name="client_sms_number")
	//@JsonIgnore
	private String smsNumber;


	//@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="client_sms_prefix")
	private MobilePrefixDef smsPrefix;

	//@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="client_phone_prefix")
	private MobilePrefixDef phonePrefix;

	//@JsonIgnore
	@ManyToOne(cascade = {CascadeType.ALL})
	@JoinColumn(name="client_title_id")
	private ClientTitle clientTitle;

	//@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="client_sector_id")
	private SectorDef clientSector;

	//@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="client_occup_id")
	private Occupation occupation ;

	@Column(name="client_tel_number")
	private String officeTel;

	@Column(name="client_authorized")
	private String authStatus;


	@Transient
	private String title;

	@XmlTransient

	@ManyToOne
	@JoinColumn(name="client_auth_user")
	private User authBy;

	@ManyToOne
	@JoinColumn(name="client_created_user")
	private User createdBy;

	@Column(name = "client_cust_ref",length = 50)
	private String clientRef;

	@Column(name = "client_resident_status")
	private String residentStatus;

	@JsonIgnore
	@OneToMany(mappedBy="clientDef")
	private List<ClientDocs> clientDocses;

	@Column(name = "client_date_updated")
	private Date dateUpdated;

	@ManyToOne
	@JoinColumn(name ="client_update_user")
	private User updatedBy;


	@Column(name="receive_marketing_material")
	private Boolean receiveMarketingMaterial;

	@Column(name="preferred_mode_of_communication")
	private String preferredModeOfCommunication;

	@Column(name = "client_pep_type")
	private String pepType;

	@Column(name = "client_screening_status")
	//@JsonIgnore
	private String screeningStatus;

	@Column(name = "client_screening_date")
	private Date screeningDate;

	@Column(name = "client_ecase_ref")
	private String ecasaRefNo;


	public String getScreeningStatus() {
		return screeningStatus;
	}

	public void setScreeningStatus(String screeningStatus) {
		this.screeningStatus = screeningStatus;
	}

	public Date getScreeningDate() {
		return screeningDate;
	}

	public void setScreeningDate(Date screeningDate) {
		this.screeningDate = screeningDate;
	}

	public String getPepType() {
		return pepType;
	}

	public void setPepType(String pepType) {
		this.pepType = pepType;
	}

	public String getClientCIF() {
		return clientCIF;
	}

	public void setClientCIF(String clientCIF) {
		this.clientCIF = clientCIF;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
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

	public String getPassportNo() {
		return passportNo;
	}

	public void setPassportNo(String passportNo) {
		this.passportNo = passportNo;
	}

	public ClientTypes getTenantType() {
		return tenantType;
	}

	public void setTenantType(ClientTypes tenantType) {
		this.tenantType = tenantType;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public OrgBranch getRegisteredbrn() {
		return registeredbrn;
	}

	public void setRegisteredbrn(OrgBranch registeredbrn) {
		this.registeredbrn = registeredbrn;
	}

	public String getTenantNumber() {
		return tenantNumber;
	}

	public void setTenantNumber(String tenantNumber) {
		this.tenantNumber = tenantNumber;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
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

	public String getOfficeTel() {
		return officeTel;
	}

	public void setOfficeTel(String officeTel) {
		this.officeTel = officeTel;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
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

	public String getAuthStatus() {
		return authStatus;
	}

	public void setAuthStatus(String authStatus) {
		this.authStatus = authStatus;
	}

	public User getAuthBy() {
		return authBy;
	}

	public void setAuthBy(User authBy) {
		this.authBy = authBy;
	}

	public List<ClientDocs> getClientDocses() {
		return clientDocses;
	}

	public void setClientDocses(List<ClientDocs> clientDocses) {
		this.clientDocses = clientDocses;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public String getClientRef() {
		return clientRef;
	}

	public void setClientRef(String clientRef) {
		this.clientRef = clientRef;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getResidentStatus() {
		return residentStatus;
	}

	public void setResidentStatus(String residentStatus) {
		this.residentStatus = residentStatus;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public void setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	public User getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Boolean getReceiveMarketingMaterial() {
		return receiveMarketingMaterial;
	}

	public String getClientSegment() {return clientSegment;}

	public void setClientSegment(String clientSegment) {this.clientSegment = clientSegment;}
	public void setReceiveMarketingMaterial(Boolean receiveMarketingMaterial) {
		this.receiveMarketingMaterial = receiveMarketingMaterial;
	}

	public String getPreferredModeOfCommunication() {
		return preferredModeOfCommunication;
	}

	public void setPreferredModeOfCommunication (String preferredModeOfCommunication) {
		this.preferredModeOfCommunication = preferredModeOfCommunication;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ClientDef clientDef = (ClientDef) o;
		return Objects.equals(pinNo, clientDef.pinNo);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pinNo);
	}

    public String getEcasaRefNo() {
        return ecasaRefNo;
    }

    public void setEcasaRefNo(String ecasaRefNo) {
        this.ecasaRefNo = ecasaRefNo;
    }
}