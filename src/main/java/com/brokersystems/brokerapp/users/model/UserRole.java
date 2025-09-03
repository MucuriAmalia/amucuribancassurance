package com.brokersystems.brokerapp.users.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

import com.brokersystems.brokerapp.setup.model.AuditBaseEntity;
import com.brokersystems.brokerapp.setup.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;


@Entity
//@Cacheable(value = true)
@Table(name ="sys_brk_user_roles")
public class UserRole extends AuditBaseEntity implements Serializable {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)    
    @Column(name="user_role_id")
	private Long userroleid;


	@ManyToOne
	@JoinColumn(name="role_name")
	private RolesDef roles;

	@ManyToOne
	@JoinColumn(name="userid")
	private User user;

	public RolesDef getRoles() {
		return roles;
	}

	public void setRoles(RolesDef roles) {
		this.roles = roles;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Long getUserroleid() {
		return userroleid;
	}

	public void setUserroleid(Long userroleid) {
		this.userroleid = userroleid;
	}	
	

}
