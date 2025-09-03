package com.brokersystems.brokerapp.setup.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlTransient;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditBaseEntity {
	
	    @Column(name = "created_date")
	    @Temporal(TemporalType.TIMESTAMP)
	    @CreatedDate
	    @JsonIgnore
	    @XmlTransient
	    private Date createdDate;
	 
	    @Column(name = "modified_date")
	    @Temporal(TemporalType.TIMESTAMP)
	    @LastModifiedDate
	    @JsonIgnore
	    @XmlTransient
	    private Date modifiedDate;
	    
	    @Column(name = "created_by")
	    @CreatedBy
	    private String createdBy;
	 
	    @Column(name = "modified_by")
	    @LastModifiedBy
	    private String modifiedBy;

		public Date getCreatedDate() {
			return createdDate;
		}

		public void setCreatedDate(Date createdDate) {
			this.createdDate = createdDate;
		}

		public Date getModifiedDate() {
			return modifiedDate;
		}

		public void setModifiedDate(Date modifiedDate) {
			this.modifiedDate = modifiedDate;
		}

		public String getCreatedBy() {
			return createdBy;
		}

		public void setCreatedBy(String createdBy) {
			this.createdBy = createdBy;
		}

		public String getModifiedBy() {
			return modifiedBy;
		}

		public void setModifiedBy(String modifiedBy) {
			this.modifiedBy = modifiedBy;
		}
	    
	   

	    
	    
	    

}
