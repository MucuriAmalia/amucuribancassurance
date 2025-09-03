//package com.brokersystems.brokerapp.trans.service.impl;
//
//import com.brokersystems.brokerapp.accounts.model.AccountsTrialBalances;
//import com.brokersystems.brokerapp.server.exception.BadRequestException;
//import com.brokersystems.brokerapp.server.utils.sftp.SftpUtil;
//import com.brokersystems.brokerapp.setup.service.ParamService;
//import com.brokersystems.brokerapp.trans.model.GlTransactions;
//import com.brokersystems.brokerapp.trans.repository.GlTransRepo;
//import com.brokersystems.brokerapp.trans.service.GlBatchExportService;
//import com.jcraft.jsch.JSchException;
//import com.jcraft.jsch.SftpException;
//import org.apache.poi.ss.util.CellRangeAddress;
//import org.apache.poi.xssf.usermodel.XSSFCellStyle;
//import org.apache.poi.xssf.usermodel.XSSFColor;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.apache.poi.ss.usermodel.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.awt.Color;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//@Service
//public class GlBatchExportServiceImpl implements GlBatchExportService {
//
//    private static final Logger logger = LoggerFactory.getLogger(GlBatchExportServiceImpl.class);
//
//    private final GlTransRepo glTransRepo;
//
//    @Autowired
//    private SftpUtil sftpUtil;
//
//    @Autowired
//    public GlBatchExportServiceImpl(GlTransRepo glTransRepo) {
//        this.glTransRepo = glTransRepo;
//    }
//
//    @Override
//    public void exportAllBatchesToExcel(String folderPath) {
//        XSSFWorkbook workbook = null;
//        FileOutputStream fileOut = null;
//
//        try {
//            workbook = new XSSFWorkbook();
//            List<Object[]> allTransactions = glTransRepo.findBatchDetails();
//            if (allTransactions.isEmpty()) {
//                throw new RuntimeException("No transactions found for export");
//            }
//
//            Sheet sheet = workbook.createSheet("SAP FILE");
//
//            // Create header rows
//            Row headerRow1 = sheet.createRow(0);
//            headerRow1.createCell(0).setCellValue("Upload General Journal Entry");
//
//            Row headerRow2 = sheet.createRow(1);
//            headerRow2.createCell(0).setCellValue("// To add field columns to the template, please add technical names.");
//
//            Row headerRow3 = sheet.createRow(2);
//            headerRow3.createCell(0).setCellValue("// For a complete list of field columns and their technical names, choose ? in the right upper corner of the app screen and then view the Browse entry of web assistance.");
//
//            Row batchIdRow = sheet.createRow(3);
//            XSSFCellStyle yellowStyle = (XSSFCellStyle) workbook.createCellStyle();
//            yellowStyle.setFillForegroundColor(new XSSFColor(new Color(255, 255, 0)));
//            yellowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//            Cell batchIdCell = batchIdRow.createCell(0);
//            batchIdCell.setCellValue("Batch ID - " + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy")));
//            batchIdCell.setCellStyle(yellowStyle);
//
//            // Add empty rows
//            sheet.createRow(4);
//            sheet.createRow(5);
//
//            // Add header section
//            Row headerSectionRow = sheet.createRow(6);
//            XSSFCellStyle blueStyle = (XSSFCellStyle) workbook.createCellStyle();
//            blueStyle.setFillForegroundColor(new XSSFColor(new Color(0, 141, 192)));
//            blueStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//
//            // Set cell values first
//            headerSectionRow.createCell(0).setCellValue("1");
//            headerSectionRow.createCell(1).setCellValue("Header");
//
//            // Assuming you have 7 columns in the header section row
//            for (int i = 0; i < 9; i++) {
//                Cell cell = headerSectionRow.getCell(i);
//                if (cell == null) {
//                    cell = headerSectionRow.createCell(i);
//                }
//                cell.setCellStyle(blueStyle);
//            }
//
//            Row headerFieldsRow = sheet.createRow(7);
//            String[] headerFields = {
//                    "BUKRS",
//                    "BLART",
//                    "BLDAT",
//                    "BUDAT",
//                    "MONAT",
//                    "BKTXT",
//                    "WAERS"
//            };
//            XSSFCellStyle lightBlueStyle = (XSSFCellStyle) workbook.createCellStyle();
//            lightBlueStyle.setFillForegroundColor(new XSSFColor(new Color(173, 216, 230))); // Light blue color
//            lightBlueStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//            for (int i = 0; i < headerFields.length; i++) {
//                headerFieldsRow.createCell(i + 1).setCellValue(headerFields[i]);
//                Cell cell = headerFieldsRow.createCell(i + 1);
//                cell.setCellValue(headerFields[i]);
//                cell.setCellStyle(lightBlueStyle);
//            }
//
//            Row headerDescriptionsRow = sheet.createRow(8);
//            String[] headerDescriptions = {
//                    "*Company Code (4)",
//                    "*Journal Entry Type (2)",
//                    "*Journal Entry Date",
//                    "*Posting Date",
//                    "Fiscal period (2)",
//                    "Document Header Text (25)",
//                    "*Transaction Currency (5)"
//            };
//
//            for (int i = 0; i < headerDescriptions.length; i++) {
//                Cell cell = headerDescriptionsRow.createCell(i + 1);
//                cell.setCellValue(headerDescriptions[i]);
//                cell.setCellStyle(lightBlueStyle);
//            }
//
//            // Get current date and format it as YYYYMMDD number
//            Date today = new Date();
//            java.time.LocalDate localDate = today.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
//            String dateStr = localDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
//            double numericDate = Double.parseDouble(dateStr);
//
//            // Create number style
//            CellStyle numberStyle = workbook.createCellStyle();
//            numberStyle.setDataFormat(workbook.createDataFormat().getFormat("0"));
//
//            Row headerDataRow = sheet.createRow(9);
//
//            // Set company code as numeric
//            Cell companyCodeCell = headerDataRow.createCell(1);
//            companyCodeCell.setCellValue(Double.parseDouble(allTransactions.get(0)[0].toString()));
//            companyCodeCell.setCellStyle(numberStyle);
//
//            // Set document type
//            headerDataRow.createCell(2).setCellValue("LR");
//
//            // Set dates as numeric values (YYYYMMDD)
//            Cell dateCell1 = headerDataRow.createCell(3);
//            dateCell1.setCellValue(numericDate);
//            dateCell1.setCellStyle(numberStyle);
//
//            Cell dateCell2 = headerDataRow.createCell(4);
//            dateCell2.setCellValue(numericDate);
//            dateCell2.setCellStyle(numberStyle);
//
//            // Set fiscal period as the current month (1-12)
//            Cell periodCell = headerDataRow.createCell(5);
//            int currentMonth = java.time.LocalDate.now().getMonthValue();
//            periodCell.setCellValue(currentMonth);
//            periodCell.setCellStyle(numberStyle);
//
//            // Set remaining text values
//            headerDataRow.createCell(6).setCellValue("Commission Income accrual");
//            headerDataRow.createCell(7).setCellValue("KES");
//
//            // Add empty rows
//            sheet.createRow(10);
//
//            // Add line items section
//            Row lineItemsHeaderRow = sheet.createRow(11);
//            Cell lineItemsHeaderCell = lineItemsHeaderRow.createCell(1);
//            lineItemsHeaderCell.setCellValue("Line Items");
//            lineItemsHeaderCell.setCellStyle(blueStyle);
//
//            // Transaction Currency
//            Row transactionCurrencyRow = sheet.createRow(12);
//            Cell transactionCurrencyCell = transactionCurrencyRow.createCell(4);
//            transactionCurrencyCell.setCellValue("Transaction Currency");
//            transactionCurrencyCell.setCellStyle(lightBlueStyle);
//            sheet.addMergedRegion(new CellRangeAddress(12, 12, 4, 5));
//
//            Row lineItemsFieldsRow = sheet.createRow(13);
//
//
//            String[] lineItemsFields = {
//                    "BUKRS",
//                    "HKONT",
//                    "SGTXT",
//                    "WRSOL",
//                    "WRHAB",
//                    "MWSKZ",
//                    "KOSTL",
//                    "PRCT",
//                    "SEGMENT",
//                    "MATNR_EXT",
//                    "FKBER",
//                    "VBUND",
//                    "RMVCT",
//            };
//            for (int i = 0; i < lineItemsFields.length; i++) {
//                Cell cell = lineItemsFieldsRow.createCell(i + 1);
//                cell.setCellValue(lineItemsFields[i]);
//                cell.setCellStyle(lightBlueStyle);
//            }
//
//            // Create header row
//            Row headerRow = sheet.createRow(14);
//            String[] columns = {
//                    "Company Code (4)",
//                    "G/L Account (10)",
//                    "Item Text (50)",
//                    "Debit",
//                    "Credit",
//                    "Tax Code (2)",
//                    "Cost Center (10)",
//                    "Profit Center (10)",
//                    "Segment for Segmental Reporting (10)",
//                    "Material Number(C40 Field)",
//                    "Functional Area (16)",
//                    "Trading Patner (6)",
//                    "Financial Transaction Type (3)",
//            };
//
//            for (int i = 0; i < columns.length; i++) {
//                Cell cell = headerRow.createCell(i + 1);
//                cell.setCellValue(columns[i]);
//                cell.setCellStyle(lightBlueStyle);
//            }
//
//            // Populate data rows
//            int rowNum = 15;
//            for (Object[] row : allTransactions) {
//                Row dataRow = sheet.createRow(rowNum++);
//
//                // Map the Object[] to cells in the row with null checks
//                dataRow.createCell(1).setCellValue(row[0] != null ? Double.parseDouble(row[0].toString()) : 0.0); // company code
//                dataRow.createCell(2).setCellValue(row[1] != null ? row[1].toString() : "N/A"); //  gl account no co_code
//                dataRow.createCell(3).setCellValue(row[2] != null ? row[2].toString() : "N/A"); // pr_desc
//                // Debit Amount
//                if (row[3] != null) {
//                    try {
//                        double debitAmt = ((Number) row[3]).doubleValue();
//                        dataRow.createCell(4).setCellValue(debitAmt);
//                    } catch (Exception e) {
//                        dataRow.createCell(4).setCellValue(0.0);
//                        logger.error("Error parsing debit amount: " + row[3], e);
//                    }
//                } else {
//                    dataRow.createCell(4).setCellValue(0.0);
//                }
//
//                // Credit Amount
//                if (row[4] != null) {
//                    try {
//                        double creditAmt = ((Number) row[4]).doubleValue();
//                        dataRow.createCell(5).setCellValue(creditAmt);
//                    } catch (Exception e) {
//                        dataRow.createCell(5).setCellValue(0.0);
//                        logger.error("Error parsing credit amount: " + row[4], e);
//                    }
//                } else {
//                    dataRow.createCell(5).setCellValue(0.0);
//                }
//                dataRow.createCell(6).setCellValue(""); // tax code
//                dataRow.createCell(7).setCellValue(""); // cost center
//                dataRow.createCell(8).setCellValue(row[7] != null ? row[7].toString() : "N/A"); // profit center
//
//
//                dataRow.createCell(9).setCellValue(row[8] != null ? row[8].toString() : "N/A"); // segment
//
//                dataRow.createCell(10).setCellValue(row[9] != null ? row[9].toString() : "N/A"); // material number
//
//                dataRow.createCell(11).setCellValue("KIB99999"); // Functional Area
//
//                dataRow.createCell(12).setCellValue(""); // Trading Partner
//
//                dataRow.createCell(13).setCellValue(""); // Financial Transaction Type
//
//            }
//
//            // Auto-size columns
//            for (int i = 0; i < columns.length; i++) {
//                sheet.autoSizeColumn(i);
//            }
//
//
//            // Ensure folder exists
//            File folder = new File(folderPath);
//            if (!folder.exists() && !folder.mkdirs()) {
//                throw new RuntimeException("Failed to create directory: " + folderPath);
//            }
//
//            // Write to single file
//            String fileName = String.format("%s/GL_Transactions_%s.xlsx", folderPath, System.currentTimeMillis());
//            fileOut = new FileOutputStream(fileName);
//            workbook.write(fileOut);
//
////            sftpUtil.authPassword(SFTP_PASSWORD);
//            logger.info("Attempting SFTP upload...");
////            sftpUtil.authKey(System.getProperty("user.home") + File.separator + "Downloads" + File.separator + "absabanca.pem", "");
////            sftpUtil.uploadFile(folderPath);
////            sftpUtil.uploadFile(fileName);
////            sftpUtil.uploadFile(new File(fileName).getAbsolutePath());
//            logger.info("SFTP upload completed successfully");
//
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to export transactions: " + e.getMessage(), e);
//        }
////        catch (JSchException e) {
////            throw new RuntimeException("SFTP authentication failed: " + e.getMessage(), e);
////        }
////        catch (SftpException e) {
////            throw new RuntimeException("SFTP upload failed: " + e.getMessage(), e);
////        }
//        finally {
//            if (fileOut != null) {
//                try {
//                    fileOut.close();
//                } catch (IOException e) {
//                    logger.error("Error closing file output stream", e);
//                }
//            }
//        }
//    }
//}


package com.brokersystems.brokerapp.trans.service.impl;

import com.brokersystems.brokerapp.server.utils.sftp.SftpUtil;
import com.brokersystems.brokerapp.trans.repository.GlTransRepo;
import com.brokersystems.brokerapp.trans.service.GlBatchExportService;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GlBatchExportServiceImpl implements GlBatchExportService {

    private static final Logger logger = LoggerFactory.getLogger(GlBatchExportServiceImpl.class);

    private final GlTransRepo glTransRepo;

    @Autowired
    private SftpUtil sftpUtil;

    @Value("${file.transfer.enabled:true}")
    private boolean fileTransferEnabled;

    @Value("${file.cleanup.after.transfer:false}")
    private boolean cleanupAfterTransfer;

    @Autowired
    public GlBatchExportServiceImpl(GlTransRepo glTransRepo) {
        this.glTransRepo = glTransRepo;
    }

    @Override
    public void exportAllBatchesToExcel(String folderPath) {
        XSSFWorkbook workbook = null;
        FileOutputStream fileOut = null;
        String excelFileName = null;
        String txtFileName = null;

        try {
            List<Object[]> allTransactions = glTransRepo.findBatchDetails();
            if (allTransactions.isEmpty()) {
                throw new RuntimeException("No transactions found for export");
            }

            // Generate both Excel and Text files
            excelFileName = generateExcelFile(folderPath, allTransactions);
            txtFileName = generateSapTextFile(folderPath, allTransactions);

            logger.info("Excel file created successfully: {}", excelFileName);
            logger.info("SAP text file created successfully: {}", txtFileName);

            // Transfer files to EC2 instance using SFTP
            if (fileTransferEnabled) {
                transferFileToEc2(excelFileName);
                transferFileToEc2(txtFileName);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to export transactions: " + e.getMessage(), e);
        } finally {
            closeResources(fileOut, workbook);
        }
    }

    /**
     * Generate Excel file (existing functionality)
     */
    private String generateExcelFile(String folderPath, List<Object[]> allTransactions) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        FileOutputStream fileOut = null;
        String fileName = null;

        try {
            Sheet sheet = workbook.createSheet("SAP FILE");

            // Create header rows
            Row headerRow1 = sheet.createRow(0);
            headerRow1.createCell(0).setCellValue("Upload General Journal Entry");

            Row headerRow2 = sheet.createRow(1);
            headerRow2.createCell(0).setCellValue("// To add field columns to the template, please add technical names.");

            Row headerRow3 = sheet.createRow(2);
            headerRow3.createCell(0).setCellValue("// For a complete list of field columns and their technical names, choose ? in the right upper corner of the app screen and then view the Browse entry of web assistance.");

            Row batchIdRow = sheet.createRow(3);
            XSSFCellStyle yellowStyle = (XSSFCellStyle) workbook.createCellStyle();
            yellowStyle.setFillForegroundColor(new XSSFColor(new Color(255, 255, 0)));
            yellowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Cell batchIdCell = batchIdRow.createCell(0);
            batchIdCell.setCellValue("Batch ID - " + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy")));
            batchIdCell.setCellStyle(yellowStyle);

            // Add empty rows
            sheet.createRow(4);
            sheet.createRow(5);

            // Add header section
            Row headerSectionRow = sheet.createRow(6);
            XSSFCellStyle blueStyle = (XSSFCellStyle) workbook.createCellStyle();
            blueStyle.setFillForegroundColor(new XSSFColor(new Color(0, 141, 192)));
            blueStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Set cell values first
            headerSectionRow.createCell(0).setCellValue("1");
            headerSectionRow.createCell(1).setCellValue("Header");

            // Apply blue style to header section
            for (int i = 0; i < 9; i++) {
                Cell cell = headerSectionRow.getCell(i);
                if (cell == null) {
                    cell = headerSectionRow.createCell(i);
                }
                cell.setCellStyle(blueStyle);
            }

            Row headerFieldsRow = sheet.createRow(7);
            String[] headerFields = {
                    "BUKRS", "BLART", "BLDAT", "BUDAT", "MONAT", "BKTXT", "WAERS"
            };
            XSSFCellStyle lightBlueStyle = (XSSFCellStyle) workbook.createCellStyle();
            lightBlueStyle.setFillForegroundColor(new XSSFColor(new Color(173, 216, 230)));
            lightBlueStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            for (int i = 0; i < headerFields.length; i++) {
                Cell cell = headerFieldsRow.createCell(i + 1);
                cell.setCellValue(headerFields[i]);
                cell.setCellStyle(lightBlueStyle);
            }

            Row headerDescriptionsRow = sheet.createRow(8);
            String[] headerDescriptions = {
                    "*Company Code (4)", "*Journal Entry Type (2)", "*Journal Entry Date",
                    "*Posting Date", "Fiscal period (2)", "Document Header Text (25)", "*Transaction Currency (5)"
            };

            for (int i = 0; i < headerDescriptions.length; i++) {
                Cell cell = headerDescriptionsRow.createCell(i + 1);
                cell.setCellValue(headerDescriptions[i]);
                cell.setCellStyle(lightBlueStyle);
            }

            // Get current date and format it as YYYYMMDD number
            Date today = new Date();
            java.time.LocalDate localDate = today.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            String dateStr = localDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
            double numericDate = Double.parseDouble(dateStr);

            // Create number style
            CellStyle numberStyle = workbook.createCellStyle();
            numberStyle.setDataFormat(workbook.createDataFormat().getFormat("0"));

            Row headerDataRow = sheet.createRow(9);

            // Set company code as numeric
            Cell companyCodeCell = headerDataRow.createCell(1);
            companyCodeCell.setCellValue(Double.parseDouble(allTransactions.get(0)[0].toString()));
            companyCodeCell.setCellStyle(numberStyle);

            // Set document type
            headerDataRow.createCell(2).setCellValue("LR");

            // Set dates as numeric values (YYYYMMDD)
            Cell dateCell1 = headerDataRow.createCell(3);
            dateCell1.setCellValue(numericDate);
            dateCell1.setCellStyle(numberStyle);

            Cell dateCell2 = headerDataRow.createCell(4);
            dateCell2.setCellValue(numericDate);
            dateCell2.setCellStyle(numberStyle);

            // Set fiscal period as the current month (1-12)
            Cell periodCell = headerDataRow.createCell(5);
            int currentMonth = java.time.LocalDate.now().getMonthValue();
            periodCell.setCellValue(currentMonth);
            periodCell.setCellStyle(numberStyle);

            // Set remaining text values
            headerDataRow.createCell(6).setCellValue("Commission Income accrual");
            headerDataRow.createCell(7).setCellValue("KES");

            // Add empty rows
            sheet.createRow(10);

            // Add line items section
            Row lineItemsHeaderRow = sheet.createRow(11);
            Cell lineItemsHeaderCell = lineItemsHeaderRow.createCell(1);
            lineItemsHeaderCell.setCellValue("Line Items");
            lineItemsHeaderCell.setCellStyle(blueStyle);

            // Transaction Currency
            Row transactionCurrencyRow = sheet.createRow(12);
            Cell transactionCurrencyCell = transactionCurrencyRow.createCell(4);
            transactionCurrencyCell.setCellValue("Transaction Currency");
            transactionCurrencyCell.setCellStyle(lightBlueStyle);
            sheet.addMergedRegion(new CellRangeAddress(12, 12, 4, 5));

            Row lineItemsFieldsRow = sheet.createRow(13);
            String[] lineItemsFields = {
                    "BUKRS", "HKONT", "SGTXT", "WRSOL", "WRHAB", "MWSKZ", "KOSTL",
                    "PRCT", "SEGMENT", "MATNR_EXT", "FKBER", "VBUND", "RMVCT"
            };
            for (int i = 0; i < lineItemsFields.length; i++) {
                Cell cell = lineItemsFieldsRow.createCell(i + 1);
                cell.setCellValue(lineItemsFields[i]);
                cell.setCellStyle(lightBlueStyle);
            }

            // Create header row
            Row headerRow = sheet.createRow(14);
            String[] columns = {
                    "Company Code (4)", "G/L Account (10)", "Item Text (50)", "Debit", "Credit",
                    "Tax Code (2)", "Cost Center (10)", "Profit Center (10)",
                    "Segment for Segmental Reporting (10)", "Material Number(C40 Field)",
                    "Functional Area (16)", "Trading Patner (6)", "Financial Transaction Type (3)"
            };

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i + 1);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(lightBlueStyle);
            }

            // Populate data rows - mapping your query results to Excel columns
            int rowNum = 15;
            for (Object[] row : allTransactions) {
                Row dataRow = sheet.createRow(rowNum++);

                // Map the Object[] to cells based on your query structure:
                // [0] sbs.seg_code (Company Code), [1] scs.co_code (G/L Account), [2] sbpr.pr_desc (Item Text)
                // [3] Debit Amount, [4] Credit Amount, [5] Tax Code (empty), [6] Cost Center (empty)
                // [7] sbs.seg_comp_code (Profit Center), [8] sbs.seg_segment (Segment), [9] sbpr.pr_sap_code (Material)
                // [10] KIB99999 (Functional Area), [11] Trading Partner (empty), [12] Transaction Type (empty)

                dataRow.createCell(1).setCellValue(row[0] != null ? Double.parseDouble(row[0].toString()) : 0.0); // Company Code
                dataRow.createCell(2).setCellValue(row[1] != null ? row[1].toString() : ""); // G/L Account
                dataRow.createCell(3).setCellValue(row[2] != null ? row[2].toString() : ""); // Item Text

                // Debit Amount (WRSOL)
                if (row[3] != null) {
                    try {
                        double debitAmt = ((Number) row[3]).doubleValue();
                        dataRow.createCell(4).setCellValue(Math.abs(debitAmt));
                    } catch (Exception e) {
                        dataRow.createCell(4).setCellValue(0.0);
                        logger.error("Error parsing debit amount: " + row[3], e);
                    }
                } else {
                    dataRow.createCell(4).setCellValue(0.0);
                }

                // Credit Amount (WRHAB)
                if (row[4] != null) {
                    try {
                        double creditAmt = ((Number) row[4]).doubleValue();
                        dataRow.createCell(5).setCellValue(Math.abs(creditAmt));
                    } catch (Exception e) {
                        dataRow.createCell(5).setCellValue(0.0);
                        logger.error("Error parsing credit amount: " + row[4], e);
                    }
                } else {
                    dataRow.createCell(5).setCellValue(0.0);
                }

                dataRow.createCell(6).setCellValue(row[5] != null ? row[5].toString() : ""); // Tax Code
                dataRow.createCell(7).setCellValue(row[6] != null ? row[6].toString() : ""); // Cost Center (KOSTL - empty in query)
                dataRow.createCell(8).setCellValue(row[7] != null ? row[7].toString() : ""); // Profit Center (PRCT)
                dataRow.createCell(9).setCellValue(row[8] != null ? row[8].toString() : ""); // Segment (SEGMENT)
                dataRow.createCell(10).setCellValue(row[9] != null ? row[9].toString() : ""); // Material Number (MATNR_EXT)
                dataRow.createCell(11).setCellValue(row[10] != null ? row[10].toString() : "KIB99999"); // Functional Area (FKBER)
                dataRow.createCell(12).setCellValue(row[11] != null ? row[11].toString() : ""); // Trading Partner (VBUND - empty in query)
//                dataRow.createCell(13).setCellValue(row[12] != null ? row[12].toString() : ""); // Financial Transaction Type (RMVCT - empty in query)
                dataRow.createCell(13).setCellValue(""); // Financial Transaction Type (RMVCT - Forced Empty)
            }

            // Auto-size columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Ensure folder exists
            File folder = new File(folderPath);
            if (!folder.exists() && !folder.mkdirs()) {
                throw new RuntimeException("Failed to create directory: " + folderPath);
            }

            // Write to single file
            fileName = String.format("%s/GL_Transactions_%s.xlsx", folderPath, System.currentTimeMillis());
            fileOut = new FileOutputStream(fileName);
            workbook.write(fileOut);
            fileOut.close(); // Close here to ensure file is written before transfer

            return fileName;

        } finally {
            if (fileOut != null) {
                try {
                    fileOut.close();
                } catch (IOException e) {
                    logger.error("Error closing Excel file output stream", e);
                }
            }
        }
    }

    /**
     * Generate SAP text file in the required format
     */
    private String generateSapTextFile(String folderPath, List<Object[]> allTransactions) throws IOException {
        // Ensure folder exists
        File folder = new File(folderPath);
        if (!folder.exists() && !folder.mkdirs()) {
            throw new RuntimeException("Failed to create directory: " + folderPath);
        }

        String fileName = String.format("%s/SAP_GL_Transactions_%s.txt", folderPath, System.currentTimeMillis());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {

            // 1. Write Header Definition Line
            String headerDefinition = "H|SOURCE|DATETIME|H_ID|ENTITY|JNL_TYP|AUTOREV|POST_DATE|PERIOD|DOC_DATE|VER_NR|DOC_TYPE|AVE_M|HEADER_TEXT|SRC_DOC";
            writer.write(headerDefinition);
            writer.newLine();

            // 2. Write Header Data Record
            String headerData = generateHeaderDataRecord(allTransactions);
            writer.write(headerData);
            writer.newLine();

            // 3. Write Line Definition Record
            String lineDefinition = "L|H_ID|COCODE|GLACCT|CUR_FC|AMNT_FC|CUR_LOC|AMNT_LOC|MAT_NR|CCPC|PPC|TP|SGMNT|SRC_CBK|AGE_BUCKET|TTYPE|BRANCH|VENDOR_ID|PAY_TERM|CUST_ID|TAX|ITEM_TEXT|ALLOC_NO";
            writer.write(lineDefinition);
            writer.newLine();

            // 4. Write Data Records for each transaction
            int recordId = 1;
            double totalAmount = 0.0;
            for (Object[] transaction : allTransactions) {
                String dataRecord = generateDataRecord(recordId, transaction);
                writer.write(dataRecord);
                writer.newLine();

                // Calculate total amount for trailer (sum of absolute values)
                double debitAmount = 0.0;
                double creditAmount = 0.0;

                if (transaction[3] != null) {  // WRSOL - Debit
                    try {
                        debitAmount = ((Number) transaction[3]).doubleValue();
                    } catch (Exception e) {
                        logger.error("Error parsing debit amount for trailer: " + transaction[3], e);
                    }
                }

                if (transaction[4] != null) {  // WRHAB - Credit
                    try {
                        creditAmount = ((Number) transaction[4]).doubleValue();
                    } catch (Exception e) {
                        logger.error("Error parsing credit amount for trailer: " + transaction[4], e);
                    }
                }

                // Add absolute values to total (for hash calculation)
                totalAmount += Math.abs(debitAmount) + Math.abs(creditAmount);
                recordId++;
            }

            // 5. Write Trailer Record with fixed value 9
            // Format: 9|Count|LOCHash
            int dataRecordCount = allTransactions.size();
            String trailerRecord = String.format("9|%d|%.2f", dataRecordCount, totalAmount);
            writer.write(trailerRecord);
            writer.newLine();

            writer.flush();
        }

        return fileName;
    }

    /**
     * Generate header data record for SAP text file (starts with 0|)
     * Format: 0|AIC_IDIT|20221020234525|1|AIC_IDIT|0|0|20221020||20221020||Z5||AIC_IDIT GL Posting|482001
     */
    private String generateHeaderDataRecord(List<Object[]> allTransactions) {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String postDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String docDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // Generate source document number
        long sourceDoc = System.currentTimeMillis() % 1000000;

        return String.format("0|Banca|%s|1|Banca|0|0|%s||%s||ZK||Commission Income Accrual|%d",
                timestamp,
                postDate,
                docDate,
                sourceDoc
        );
    }

    /**
     * Generate data record for each transaction
     * Maps your query results to SAP format:
     * [0] sbs.seg_code (BUKRS - Company Code)
     * [1] scs.co_code (HKONT - G/L Account)
     * [2] sbpr.pr_desc (SGTXT - Item Text)
     * [3] Debit Amount (WRSOL)
     * [4] Credit Amount (WRHAB)
     * [5] Tax Code (MWSKZ - empty)
     * [6] Cost Center (KOSTL - empty)
     * [7] sbs.seg_comp_code (PRCT - Profit Center)
     * [8] sbs.seg_segment (SEGMENT)
     * [9] sbpr.pr_sap_code (MATNR_EXT - Material Number)
     * [10] Functional Area (FKBER - KIB99999)
     * [11] Trading Partner (VBUND - empty)
     * [12] Transaction Type (RMVCT - empty)
     */
    private String generateDataRecord(int recordId, Object[] transaction) {
        // Extract transaction data with null checks based on your query structure
        String companyCode = transaction[0] != null ? transaction[0].toString() : "";  // sbs.seg_code
        String glAccount = transaction[1] != null ? transaction[1].toString() : "";        // scs.co_code
        String itemText = transaction[2] != null ? transaction[2].toString() : "";         // sbpr.pr_desc

        // Handle amounts from the query
        double debitAmount = 0.0;
        double creditAmount = 0.0;

        if (transaction[3] != null) {  // WRSOL - Debit
            try {
                debitAmount = ((Number) transaction[3]).doubleValue();
            } catch (Exception e) {
                logger.error("Error parsing debit amount: " + transaction[3], e);
            }
        }

        if (transaction[4] != null) {  // WRHAB - Credit
            try {
                creditAmount = ((Number) transaction[4]).doubleValue();
            } catch (Exception e) {
                logger.error("Error parsing credit amount: " + transaction[4], e);
            }
        }

        // Determine the final amount for SAP (negative for credit, positive for debit)
        double finalAmount;
        String currency = "KES"; // Your default currency

        if (debitAmount != 0) {
            finalAmount = Math.abs(debitAmount);
        } else if (creditAmount != 0) {
            finalAmount = -Math.abs(creditAmount);
        } else {
            finalAmount = 0.0;
        }

        // Extract organizational fields from your query
        String taxCode = transaction[5] != null ? transaction[5].toString() : "";           // MWSKZ (empty in query)
        String costCenter = transaction[6] != null ? transaction[6].toString() : "";        // KOSTL (empty in query)
        String profitCenter = transaction[7] != null ? transaction[7].toString() : "";      // PRCT - sbs.seg_comp_code
        String segment = transaction[8] != null ? transaction[8].toString() : "CS001";      // SEGMENT - sbs.seg_segment
        String materialNumber = transaction[9] != null ? transaction[9].toString() : "";    // MATNR_EXT - sbpr.pr_sap_code
        String functionalArea = transaction[10] != null ? transaction[10].toString() : "KIB99999"; // FKBER
        String tradingPartner = transaction[11] != null ? transaction[11].toString() : "";  // VBUND (empty in query)
        String transactionType = transaction[12] != null ? transaction[12].toString() : ""; // RMVCT - sbgt.gl_trans_type from query

        // Generate allocation number and item text
        String allocationNo = String.format("IDIT %d/%d/null/10075", recordId + 130868, recordId + 130868);
        String fullItemText = itemText + " " + allocationNo;

        // SAP Format: L|H_ID|COCODE|GLACCT|CUR_FC|AMNT_FC|CUR_LOC|AMNT_LOC|MAT_NR|CCPC|PPC|TP|SGMNT|SRC_CBK|AGE_BUCKET|TTYPE|BRANCH|VENDOR_ID|PAY_TERM|CUST_ID|TAX|ITEM_TEXT|ALLOC_NO
        return String.format("1|1|%s|%s|%s|%.2f|%s|%.2f|%s|%s|%s|%s|%s|0000|0000|%s|KIB99999||||%s|%s|%d",
                companyCode,        // COCODE - sbs.seg_code
                glAccount,          // GLACCT - scs.co_code
                currency,           // CUR_FC - KES
                finalAmount,        // AMNT_FC - calculated amount
                currency,           // CUR_LOC - KES
                finalAmount,        // AMNT_LOC - calculated amount
                materialNumber,     // MAT_NR - sbpr.pr_sap_code
                profitCenter,       // PPC - sbs.seg_comp_code
                costCenter,         // CCPC - empty from query
                tradingPartner,     // TP - empty from query
                segment,            // SGMNT - sbs.seg_segment
                transactionType,    // TTYPE - empty from query
                taxCode,            // TAX - empty from query
                fullItemText,       // ITEM_TEXT - combination
                recordId + 130868   // ALLOC_NO - generated
        );
    }

    /**
     * Transfer the generated file to EC2 instance using SFTP
     */
    private void transferFileToEc2(String localFilePath) {
        try {
            logger.info("Starting SFTP file transfer to EC2 instance: {}", localFilePath);

            // Test connection first
            if (!sftpUtil.testConnection()) {
                logger.error("Cannot establish SFTP connection to EC2 instance");
                return;
            }

            logger.info("SFTP connection test successful, proceeding with file transfer...");

            // Authenticate and upload
            sftpUtil.authWithKey();
            sftpUtil.uploadFileToEc2(localFilePath);

            logger.info("File successfully transferred to EC2 instance: {}", localFilePath);

            // Optionally delete local file after successful transfer
            if (cleanupAfterTransfer) {
                deleteLocalFile(localFilePath);
            }

        } catch (JSchException e) {
            logger.error("SFTP authentication failed: {}", e.getMessage(), e);
            throw new RuntimeException("SFTP authentication failed: " + e.getMessage(), e);
        } catch (SftpException e) {
            logger.error("SFTP upload failed: {}", e.getMessage(), e);
            throw new RuntimeException("SFTP upload failed: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error during SFTP transfer: {}", e.getMessage(), e);
            throw new RuntimeException("SFTP transfer failed: " + e.getMessage(), e);
        } finally {
            // Always close SFTP connection
            sftpUtil.close();
        }
    }

    /**
     * Delete local file after successful transfer (optional)
     */
    private void deleteLocalFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists() && file.delete()) {
                logger.info("Local file deleted after successful transfer: {}", filePath);
            } else {
                logger.warn("Could not delete local file: {}", filePath);
            }
        } catch (Exception e) {
            logger.error("Error deleting local file: {}", e.getMessage(), e);
        }
    }

    /**
     * Close resources properly
     */
    private void closeResources(FileOutputStream fileOut, XSSFWorkbook workbook) {
        if (fileOut != null) {
            try {
                fileOut.flush();
                fileOut.close();
            } catch (IOException e) {
                logger.error("Error closing file output stream", e);
            }
        }
    }
}