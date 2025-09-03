package com.brokersystems.brokerapp.setup.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sys_brk_holidays")
public class PublicHolidays {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "holiday_id")
    private Long holidayId;

    @Column(name = "holiday_name", nullable = false)
    private String holidayName;

    @Temporal(TemporalType.DATE)
    @Column(name = "holiday_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date holidayDate;


    @Column(name = "is_recurring", length = 1)
    private String isRecurring = "N"; // Y = every year, N = one-time

    @Column(name = "active_status", length = 1)
    private String activeStatus = "Y"; // Default to active




    public Long getHolidayId() {
        return holidayId;
    }

    public void setHolidayId(Long holidayId) {
        this.holidayId = holidayId;
    }

    public String getHolidayName() {
        return holidayName;
    }

    public void setHolidayName(String holidayName) {
        this.holidayName = holidayName;
    }

    public Date getHolidayDate() {
        return holidayDate;
    }

    public void setHolidayDate(Date holidayDate) {
        this.holidayDate = holidayDate;
    }

    public String getIsRecurring() {
        return isRecurring;
    }

    public void setIsRecurring(String isRecurring) {
        this.isRecurring = isRecurring;
    }

    public String getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(String activeStatus) {
        this.activeStatus = activeStatus;
    }
}