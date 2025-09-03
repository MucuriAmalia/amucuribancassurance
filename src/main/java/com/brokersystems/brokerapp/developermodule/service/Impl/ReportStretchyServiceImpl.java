package com.brokersystems.brokerapp.developermodule.service.Impl;

import com.brokersystems.brokerapp.customscreens.model.GenericResultsetData;
import com.brokersystems.brokerapp.customscreens.model.ResultsetColumnHeaderData;
import com.brokersystems.brokerapp.customscreens.model.ResultsetRowData;
import com.brokersystems.brokerapp.developermodule.dto.ReportParameterJoinData;
import com.brokersystems.brokerapp.developermodule.repository.StretchyReportRepository;
import com.brokersystems.brokerapp.developermodule.service.ReportStretchyService;
import com.brokersystems.brokerapp.developermodule.service.StretchyReportService;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.exception.ReportNotFoundException;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ReportStretchyServiceImpl implements ReportStretchyService {

    private final JdbcTemplate jdbcTemplate;
    private final StretchyReportRepository stretchyReportRepository;
    private final StretchyReportService stretchyReportService;

    @Autowired
    public ReportStretchyServiceImpl(JdbcTemplate jdbcTemplate, StretchyReportRepository stretchyReportRepository, StretchyReportService stretchyReportService) {
        this.jdbcTemplate = jdbcTemplate;
        this.stretchyReportRepository = stretchyReportRepository;
        this.stretchyReportService = stretchyReportService;
    }

    @Override
    public String replace(final String str, final String pattern, final String replace) {
        return str.replace(pattern, replace);
    }

    @Override
    public String wrapSQL(final String sql) {
        String trimmedSql = sql.trim().toUpperCase();
        if (trimmedSql.startsWith("SELECT")) {
            return sql;
        }
        return "SELECT x.* FROM (" + sql + ") x";
    }

    @Override
    public GenericResultsetData executeReport(String strTemplateName, Map<String, String> parameters) throws BadRequestException, ReportNotFoundException, IllegalAccessException {
        ReportParameterJoinData reportData = fetchReportDataByCode(strTemplateName);
        String reportSql = constructSqlQueryWithParameters(reportData.getReportSql(), parameters);
        validateSQL(reportSql, parameters);
        GenericResultsetData data =  fillGenericResultSet(reportSql);
        return data;
    }

    private ReportParameterJoinData fetchReportDataByCode(String strTemplateName) throws ReportNotFoundException, IllegalAccessException {
        List<ReportParameterJoinData> reportDataList = stretchyReportService.findStretchyReportQuery(strTemplateName);
        if (reportDataList.isEmpty()) {
            throw new ReportNotFoundException("Report not found with name: " + strTemplateName);
        }
        return reportDataList.get(0);
    }
    
    private String constructSqlQueryWithParameters(String reportSql, Map<String, String> parameters) {
        if (reportSql == null) {
            throw new IllegalArgumentException("Report SQL cannot be null");
        }

        // Replace non-breaking spaces with regular spaces
        reportSql = reportSql.replace("\u00A0", " ");

        // If no parameters are provided, return the query as-is
        if (parameters == null || parameters.isEmpty()) {
            return wrapSQL(reportSql);
        }

        // Replace placeholders with parameter values or NULL for missing ones
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String paramPlaceholder = ":" + entry.getKey();
            String paramValue = entry.getValue() != null ? entry.getValue().trim() : "NULL";

            // If parameter is empty, set it to NULL
            if (paramValue.isEmpty()) {
                paramValue = "NULL";
            } else if (entry.getKey().toLowerCase().contains("date")) {
                // Handle date parameters
                paramValue = "'" + paramValue + "'";
            }

            reportSql = reportSql.replace(paramPlaceholder, paramValue);
        }

        return wrapSQL(reportSql);
    }


    private void validateSQL(String sql, Map<String, String> parameters) throws BadRequestException {
        try {
            jdbcTemplate.queryForRowSet(sql);
        } catch (Exception e) {
            throw new BadRequestException("Invalid SQL query");
        }
    }

    @Override
    public void generateCSVFile(String strTemplateName, Map<String, String> parameters) throws Exception {
        GenericResultsetData result = executeReport(strTemplateName, parameters);

        String outputPath = "customReport.csv";

        generateCsvFromGenericResultsetData(result, outputPath);

    }

    @Override
    public GenericResultsetData fillGenericResultSet(final String sql) throws BadRequestException {
        final SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql);
        final List<ResultsetColumnHeaderData> columnHeaders = new ArrayList<>();
        final List<ResultsetRowData> resultsetDataRows = new ArrayList<>();

        final SqlRowSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();

        // Extract column headers
        for (int i = 1; i <= columnCount; i++) {
            final String columnName = rsmd.getColumnName(i);
            final String columnType = rsmd.getColumnTypeName(i);
            columnHeaders.add(ResultsetColumnHeaderData.basic(columnName, columnType));
        }

        // Extract column data and handle nulls
        while (rs.next()) {
            final List<String> columnValues = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                columnValues.add(rs.getString(i));
            }
            resultsetDataRows.add(ResultsetRowData.create(columnValues));
        }
        return new GenericResultsetData(columnHeaders, resultsetDataRows);
    }

    @Override
    public void generateCsvFromGenericResultsetData(GenericResultsetData result, String outputPath) throws IOException {
        StringBuilder csvContent = new StringBuilder();

        // Write the header
        List<ResultsetColumnHeaderData> columnHeaders = result.getColumnHeaders();
        List<String> headerNames = new ArrayList<>();
        for (ResultsetColumnHeaderData header : columnHeaders) {
            headerNames.add(header.getColumnName());
        }
        csvContent.append(String.join(",", headerNames)).append("\n");

        // Write the data rows
        for (ResultsetRowData rowData : result.getData()) {
            List<String> rowValues = new ArrayList<>();
            for (String value : rowData.getRow()) {
                // Handle null values by replacing them with an empty string
                rowValues.add(value != null ? value.replace("\"", "\"\"") : "NA");
            }
            csvContent.append(String.join(",", rowValues)).append("\n");
        }

        FileUtils.writeStringToFile(new File(outputPath), csvContent.toString(), StandardCharsets.UTF_8);
    }

    @Override
    public void generatePDFFile(String strTemplateName, Map<String, String> parameters, HttpServletResponse response) throws Exception {
        GenericResultsetData result = executeReport(strTemplateName, parameters);

        // Set response content type and headers
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=customReport.pdf"); // Display in browser

        // Generate PDF and write to response output stream
        generatePdfFromGenericResultsetData(result, response.getOutputStream());
    }

    private void generatePdfFromGenericResultsetData(GenericResultsetData result, OutputStream outputStream) throws IOException, DocumentException {
        // Create PDF document with A4 size (landscape mode for more space)
        Document document = new Document(PageSize.A0.rotate(), 10, 10, 10, 10);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        // Debug: Add borders to visualize the header layout and check if the logo loads correctly
        PdfPTable headerTable = new PdfPTable(2);  // 2 columns: one for logo, one for company details
        headerTable.setWidthPercentage(100);       // Full width of the page
        headerTable.setWidths(new float[] {1f, 3f}); // Set column widths
        // Add header table to the document
        document.add(headerTable);
        // Write the header as a title
        Paragraph title = new Paragraph("Custom Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        // Create table with dynamic number of columns
        PdfPTable table = new PdfPTable(result.getColumnHeaders().size());
        table.setWidthPercentage(100);  // Table width as a percentage of page width
        table.setSpacingBefore(10f);    // Space before the table
        table.setSpacingAfter(10f);     // Space after the table

        // Set dynamic column widths (proportional)
        float[] columnWidths = new float[result.getColumnHeaders().size()];
        for (int i = 0; i < columnWidths.length; i++) {
            columnWidths[i] = 1f;  // Adjust based on content if necessary
        }
        table.setWidths(columnWidths);

        // Write the table header
        List<ResultsetColumnHeaderData> columnHeaders = result.getColumnHeaders();
        for (ResultsetColumnHeaderData header : columnHeaders) {
            PdfPCell cell = new PdfPCell(new Phrase(header.getColumnName(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5f);  // Padding for header cells
            table.addCell(cell);
        }

        // Write the data rows with styling
        for (ResultsetRowData rowData : result.getData()) {
            for (String value : rowData.getRow()) {
                PdfPCell cell = new PdfPCell(new Phrase(value != null ? value : "NA", FontFactory.getFont(FontFactory.HELVETICA, 10)));
                cell.setPadding(5f);  // Padding for data cells
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setNoWrap(false);  // Allow text wrapping within cells
                table.addCell(cell);
            }
        }

        // Add table to the document
        document.add(table);

        // Close document
        document.close();
    }

}
