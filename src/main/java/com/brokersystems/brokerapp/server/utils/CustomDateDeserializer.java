package com.brokersystems.brokerapp.server.utils;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomDateDeserializer extends JsonDeserializer<Date> {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");


    @Override
    public Date deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException, JacksonException {
        String date = p.getText();
        if ("Invalid date".equalsIgnoreCase(date) || date == null || date.trim().isEmpty()) {
            return null; // or throw custom exception
        }
        try {
            return formatter.parse(date);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse date: " + date);
        }
    }
}
