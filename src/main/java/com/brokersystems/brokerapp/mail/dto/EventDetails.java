package com.brokersystems.brokerapp.mail.dto;

import lombok.Data;

import java.util.List;

@Data
public class EventDetails {
    private List<EventDetailList> eventDetailList;
}
