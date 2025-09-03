package com.brokersystems.brokerapp.auditlogs.service;

import com.brokersystems.brokerapp.auditlogs.model.SystemLogs;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class SystemLogsServiceImpl implements SystemLogsService {

    @Autowired
    private Environment env;

    @Override
    public List<SystemLogs> getSystemLogs(Date startDate, Date endDate) throws BadRequestException {
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        List<SystemLogs> systemLogsList = new ArrayList<>();
        JsonReader jsonReader = null;
        try {
            while (!start.getTime().after(endDate)) {
                String currDate = formatter.format(start.getTime());
                String file = env.getProperty("log.path") + "/broker_" + currDate + ".json";
                File f = new File(file);
//                System.out.println("File Exists: "+ f.exists());
                if (f.exists() && f.length() != 0 ) {
                    jsonReader = new JsonReader(new FileReader(file));
                    jsonReader.setLenient(true);
                    JsonParser parser = new JsonParser();
                    while (jsonReader.peek() != JsonToken.END_DOCUMENT) {
                        JsonElement element = parser.parse(jsonReader);
                        String timestamp = extractStringNamed("@timestamp", element);
                        final String time = timestamp.substring(0, timestamp.indexOf("+"));
                        Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(time);
                        SystemLogs systemLogs = new SystemLogs(extractStringNamed("username", element),
                                extractStringNamed("HOSTNAME", element),
                                extractStringNamed("machinename", element),
                                extractStringNamed("address", element),
                                extractStringNamed("message", element), date);
                        systemLogsList.add(systemLogs);
                    }
                }
                start.add(Calendar.DATE, 1);
            }
        } catch (IOException | ParseException e) {
            throw new BadRequestException("Unable to get logs for the requested  date..Please consult admin");

        }


        return systemLogsList;
    }

    private String extractStringNamed(final String parameterName, final JsonElement element) {
        return extractStringNamed2(parameterName, element, new HashSet<String>());
    }


    private String extractStringNamed2(final String parameterName, final JsonElement element, final Set<String> parametersPassedInRequest) {
        String stringValue = null;
        if (element.isJsonObject()) {
            final JsonObject object = element.getAsJsonObject();
            if (object.has(parameterName) && object.get(parameterName).isJsonPrimitive()) {
                parametersPassedInRequest.add(parameterName);
                final JsonPrimitive primitive = object.get(parameterName).getAsJsonPrimitive();
                final String valueAsString = primitive.getAsString();
                if (StringUtils.isNotBlank(valueAsString)) {
                    stringValue = valueAsString;
                }
            }
        }
        return stringValue;
    }
}
