package com.brokersystems.brokerapp.setup.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="sys_schedule_screens")
public class ScheduleScreens {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="scrn_id")
	private Long screenId;
	
	@Column(name="scrn_name")
	private String screenName;
	
	@Column(name="scrn_lvl")
	private String screenLevel;

	public Long getScreenId() {
		return screenId;
	}

	public void setScreenId(Long screenId) {
		this.screenId = screenId;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public String getScreenLevel() {
		return screenLevel;
	}

	public void setScreenLevel(String screenLevel) {
		this.screenLevel = screenLevel;
	}
	
	

}
