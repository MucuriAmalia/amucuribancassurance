package com.brokersystems.brokerapp.developermodule.service;

import com.brokersystems.brokerapp.customscreens.model.GenericResultsetData;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.exception.ReportNotFoundException;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public interface ReportStretchyService {

    String replace(String str, String pattern, String replace);

    String wrapSQL(String sql);

    GenericResultsetData fillGenericResultSet(String sql) throws BadRequestException;

    GenericResultsetData executeReport(String strTemplateName, Map<String, String> parameters) throws BadRequestException, ReportNotFoundException, IllegalAccessException;

    void generateCSVFile(String strTemplateName, Map<String, String> parameters) throws Exception;

    void generateCsvFromGenericResultsetData(GenericResultsetData result, String outputPath) throws IOException;

    void generatePDFFile(String strTemplateName, Map<String, String> parameters, HttpServletResponse response) throws Exception;

//    void generatePDFFile(String strTemplateName, Map<String, String> parameters) throws Exception;

}
