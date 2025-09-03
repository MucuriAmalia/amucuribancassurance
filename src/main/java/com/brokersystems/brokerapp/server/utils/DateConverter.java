package com.brokersystems.brokerapp.server.utils;

import com.brokersystems.brokerapp.server.exception.BadRequestException;
import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter implements Converter<String, Date> {

    @Override
    public Date convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }

        // Try parsing with multiple formats
        String[] dateFormats = {"dd/MM/yyyy", "yyyy-MM-dd'T'HH:mm:ss"};
        for (String format : dateFormats) {
            try {
                return new SimpleDateFormat(format).parse(source);
            } catch (ParseException ignored) {
                // Ignore and try the next format
            }
        }

        // If no format matches, throw an exception
        throw new RuntimeException("Invalid Date: " + source);
    }
}
