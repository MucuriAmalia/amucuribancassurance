package com.brokersystems.brokerapp.reconciliation.sfi.service.impl;

import com.brokersystems.brokerapp.reconciliation.sfi.service.SFIService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Service
public class SFIServiceImpl implements SFIService {

    private static final String FILE_PATH = "/home/absapov/SFI File.xlsx";


    @Override
    public List<Map<String, String>> readMortgagesData(String sheetName) throws IOException {
        System.out.println("Requested sheet name: " + sheetName);
        List<Map<String, String>> dataList = new ArrayList<>();

        // Map the input sheetName to the actual sheet names
        String actualSheetName;
        switch (sheetName.toLowerCase()) {
            case "debit":
                actualSheetName = "PCI-Debit Card";
                break;
            case "credit":
                actualSheetName = "PCI-Credit Card";
                break;
            case "macra":
                actualSheetName = "PCI-Macra";
                break;
            case "mortgages":
                actualSheetName = "Mortgages";
                break;
            case "raw-file":
                actualSheetName = "SFI-Raw File";
                break;
            default:
                throw new IllegalArgumentException("Invalid sheet name provided: " + sheetName);
        }

        // Open the Excel file and process the corresponding sheet
        try (FileInputStream fileInputStream = new FileInputStream(FILE_PATH)) {
            Workbook workbook;
            if (FILE_PATH.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(fileInputStream);
            } else if (FILE_PATH.endsWith(".xls")) {
                workbook = new HSSFWorkbook(fileInputStream);
            } else {
                throw new IllegalArgumentException("Invalid file type. Only .xls and .xlsx files are supported.");
            }

            // Get the sheet based on the mapped sheet name
            Sheet sheet = workbook.getSheet(actualSheetName);
            if (sheet == null) {
                throw new IOException("Sheet not found: " + actualSheetName);
            }

            // Read the header row
            Row headerRow = sheet.getRow(0);
            List<String> headers = new ArrayList<>();
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                headers.add(cell != null ? cell.getStringCellValue() : "");
            }

            // Read the remaining rows and map the data
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Map<String, String> rowData = new HashMap<>();
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j);
                    String value = (cell != null) ? cell.toString() : "";
                    rowData.put(headers.get(j), value);
                }
                dataList.add(rowData);
            }
        }

        return dataList;
    }

}

