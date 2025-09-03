package com.brokersystems.brokerapp.setup.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="sys_brk_client_titles")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class ClientTitle {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="cnt_title_id")
	private Long titleId;
	
	@Column(name="cnt_title",nullable=false,unique=true)
	private String titleName;

	public Long getTitleId() {
		return titleId;
	}

	public void setTitleId(Long titleId) {
		this.titleId = titleId;
	}

	public String getTitleName() {
		return titleName;
	}

	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}
	
	

}
