package com.brokersystems.brokerapp.setup.model;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Entity
//@Cacheable(true)
@Table(name ="sys_brk_users")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class User extends AuditBaseEntity  implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Id
    @GeneratedValue
    @Column(name="user_id")
    private Long id;
    
    @Column(name="user_username")
    private String username;

    @Column(name="user_email")
    @JsonIgnore
    private String email;
    
    @Column(name="user_name")
    private String name;
    
    @Column(name="user_status")
    private String enabled;
    
    @Column(name="user_password")
    @JsonIgnore
    private String password;

    @Column(name = "user_reset_pwd")
    private String resetPass;

    @ManyToOne(fetch =FetchType.EAGER)
    @JoinColumn(name="user_sub_account")
    private AccountTypes subAccountTypes;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="user_sub_agent")
    private AccountDef accountDef;

    @Column(name="user_last_login")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLogin;

    @Column(name="user_last_ip")
    private String lastIP;

    @Getter
    @Setter
    @Column(name = "failed_attempts")
    private Integer failedAttempts = 0;

    @Getter
    @Setter
    @Column(name = "account_locked")
    private Boolean  accountLocked = false;

    @Column(name="user_signature", length = 160)
    private String signature;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "esc_level_id")
    private EscalationLevel escalationLevel;

    @Column(name="user_content_type",length = 20)
    private String signatureContentType;
    @Column(name="user_send_email",length = 5)
    private String sendEmail;

    @Column(name="user_absa_no")
    private String absaNo;

    public User(User user) {
        this.id = user.id;
        this.username = user.username;
        this.name = user.name;       
        this.password = user.password;
        this.enabled=user.enabled;
        this.email = user.email;
        this.lastLogin=new Date();
        this.lastIP = user.lastIP;
        this.absaNo = user.absaNo;
}

    public User() {
    	
	}

    public String getAbsaNo() {
        return absaNo;
    }

    public void setAbsaNo(String absNo) {
        this.absaNo = absNo;
    }

    public AccountTypes getSubAccountTypes() {
        return subAccountTypes;
    }

    public void setSubAccountTypes(AccountTypes subAccountTypes) {
        this.subAccountTypes = subAccountTypes;
    }

    public AccountDef getAccountDef() {
        return accountDef;
    }

    public void setAccountDef(AccountDef accountDef) {
        this.accountDef = accountDef;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}


    public String getResetPass() {
        return resetPass;
    }

    public void setResetPass(String resetPass) {
        this.resetPass = resetPass;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    @Override
	public String toString() {
		return username;
	}

    public String getLastIP() {
        return lastIP;
    }

    public void setLastIP(String lastIP) {
        this.lastIP = lastIP;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSignatureContentType() {
        return signatureContentType;
    }

    public void setSignatureContentType(String signatureContentType) {
        this.signatureContentType = signatureContentType;
    }

    public String getSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(String sendEmail) {
        this.sendEmail = sendEmail;
    }

    public EscalationLevel getEscalationLevel() {
        return escalationLevel;
    }

    public void setEscalationLevel(EscalationLevel escalationLevel) {
        this.escalationLevel = escalationLevel;
    }
}