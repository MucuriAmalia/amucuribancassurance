package com.brokersystems.brokerapp.medical.model;

import javax.persistence.*;

/**
 * Created by peter on 5/12/2017.
 */
@Entity
@Table(name = "sys_brk_med_events")
public class MedicalEvents {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "event_sht_desc",nullable = false,unique = true)
    private String eventShtDesc;

    @Column(name = "event_desc",nullable = false)
    private String eventDesc;

    @Column(name = "event_type")
    private String type;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventShtDesc() {
        return eventShtDesc;
    }

    public void setEventShtDesc(String eventShtDesc) {
        this.eventShtDesc = eventShtDesc;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
