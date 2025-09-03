package com.brokersystems.brokerapp.setup.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="sys_schedule_tables")
public class ScheduleTables {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="sch_id")
	private Long scheduleId;
	
	
	@Column(name="sch_displ_val")
	private String displayValue;
	
	@Column(name="sch_col_name")
	private String columnName;
	
	@Column(name="sch_col_type")
	private String columnType;
	
	@Column(name="sch_col_size")
	private Integer colSize;
	
	@Column(name="sch_default_val")
	private String defaultValue;
	
	
	@XmlTransient
	@ManyToOne
	@JoinColumn(name="sch_scr_code",nullable=false)
	private ScheduleScreens screen;

	public Long getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(Long scheduleId) {
		this.scheduleId = scheduleId;
	}

	public String getDisplayValue() {
		return displayValue;
	}

	public void setDisplayValue(String displayValue) {
		this.displayValue = displayValue;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnType() {
		return columnType;
	}

	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}

	public Integer getColSize() {
		return colSize;
	}

	public void setColSize(Integer colSize) {
		this.colSize = colSize;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public ScheduleScreens getScreen() {
		return screen;
	}

	public void setScreen(ScheduleScreens screen) {
		this.screen = screen;
	}
	
	
	
	
	

}
