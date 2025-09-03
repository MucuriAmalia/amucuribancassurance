package com.brokersystems.brokerapp.server.utils;

import com.google.common.io.Files;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by HP on 9/28/2017.
 */
@Component
public class RiskImportExcelUtils {

    public  RiskSections importRisks(MultipartFile file) throws IOException {
        Map<Integer,String> columns  = new HashMap<>();
        Set<RiskImportBean> risks = new HashSet<>();
        List<SectionImportBean> sections = new ArrayList<>();
        RiskSections riskSections = new RiskSections();
        Set<String> riskIds = new HashSet<>();

        byte [] byteArr=file.getBytes();
        InputStream excelFile = new ByteArrayInputStream(byteArr);
        Workbook workbook = null;
        if (Files.getFileExtension(file.getOriginalFilename()).equalsIgnoreCase("xls")) {
            workbook = new HSSFWorkbook(excelFile);
        } else if (Files.getFileExtension(file.getOriginalFilename()).equalsIgnoreCase("xlsx")) {
            workbook = new XSSFWorkbook(excelFile);
        }
        for(int i=0;i<workbook.getNumberOfSheets();i++){
            Sheet sheet = workbook.getSheetAt(i);
            if(i==0){
                Iterator<Row> iterator = sheet.iterator();
                while (iterator.hasNext()) {
                    Row currentRow = iterator.next();
                    if(currentRow.getRowNum()==0) {
                        Iterator<Cell> cellIterator = currentRow.iterator();
                        while (cellIterator.hasNext()) {
                            Cell currentCell = cellIterator.next();
                            int columnIndex = currentCell.getColumnIndex();
                            switch(columnIndex){
                                case 0:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(0,currentCell.getStringCellValue());
                                    }
                                    break;
                                case 1:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(1,currentCell.getStringCellValue());
                                    }
                                    break;
                                case 2:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(2,currentCell.getStringCellValue());

                                    }
                                    break;

                                case 3:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(3,currentCell.getStringCellValue());

                                    }
                                    break;
                                case 4:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(4,currentCell.getStringCellValue());

                                    }
                                    break;
                                case 5:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(5,currentCell.getStringCellValue());

                                    }
                                    break;
                                case 6:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(6,currentCell.getStringCellValue());

                                    }
                                    break;
                                case 7:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(7,currentCell.getStringCellValue());

                                    }
                                    break;
                                case 8:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(8,currentCell.getStringCellValue());

                                    }
                                    break;
                                case 9:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(9,currentCell.getStringCellValue());

                                    }
                                    break;
                                case 10:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(10,currentCell.getStringCellValue());

                                    }
                                    break;
                                case 11:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(11,currentCell.getStringCellValue());

                                    }
                                    break;
                                case 12:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(12,currentCell.getStringCellValue());

                                    }
                                    break;
                                case 13:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(13,currentCell.getStringCellValue());

                                    }
                                    break;
                                case 14:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(14,currentCell.getStringCellValue());
                                    }
                                    break;
                                case 15:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(15,currentCell.getStringCellValue());
                                    }
                                    break;
                                case 16:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(16,currentCell.getStringCellValue());
                                    }
                                    break;
                                case 17:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(17,currentCell.getStringCellValue());
                                    }
                                    break;
                                case 18:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(18,"Sched_col1");
                                    }
                                    break;
                                case 19:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(19,"Sched_col2");
                                    }
                                    break;
                                case 20:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(20,"Sched_col3");
                                    }
                                    break;
                                case 21:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(21,"Sched_col4");
                                    }
                                    break;
                                case 22:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(22,"Sched_col5");
                                    }
                                    break;
                                case 23:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(23,"Sched_col6");
                                    }
                                    break;
                                case 24:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(24,"Sched_col7");
                                    }
                                    break;
                                case 25:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(25,"Sched_col8");
                                    }
                                    break;
                                case 26:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(26,"Sched_col9");
                                    }
                                    break;
                                case 27:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(27,"Sched_col10");
                                    }
                                    break;
                                case 28:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(28,"Sched_col11");
                                    }
                                    break;
                                case 29:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(29,"Sched_col12");
                                    }
                                    break;
                                case 30:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(30,"Sched_col13");
                                    }
                                    break;
                                case 31:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(31,"Sched_col14");
                                    }
                                    break;
                                case 32:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(32,"Sched_col15");
                                    }
                                    break;
                                case 33:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(33,"Sched_col16");
                                    }
                                    break;
                                case 34:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(34,"Sched_col17");
                                    }
                                    break;
                                case 35:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(35,"Sched_col18");
                                    }
                                    break;
                                case 36:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(36,"Sched_col19");
                                    }
                                    break;
                                case 37:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        columns.put(37,"Sched_col20");
                                    }
                                    break;

                            }
                        }
                    }
                    else{
                        RiskImportBean bean = new RiskImportBean();
                        Iterator<Cell> cellIterator = currentRow.iterator();
                        while (cellIterator.hasNext()) {
                            Cell currentCell = cellIterator.next();
                            int columnIndex = currentCell.getColumnIndex();
                            if(columns!=null && columns.get(columnIndex)!=null){
                                if(columns.get(columnIndex).equalsIgnoreCase("RISK_ID")){
                                    if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                        if (riskIds.contains(String.valueOf(currentCell.getNumericCellValue()))) {
                                            System.out.println("Duplicate RISK_ID: " + currentCell.getNumericCellValue());
                                        } else {
                                            riskIds.add(String.valueOf(currentCell.getNumericCellValue()));
                                            bean.setRiskId(String.valueOf(currentCell.getNumericCellValue()));
                                        }
                                    } else if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                        if (riskIds.contains(String.valueOf(currentCell.getStringCellValue()))) {
                                            System.out.println("Duplicate RISK_ID: " + currentCell.getStringCellValue());
                                        } else {
                                            riskIds.add(String.valueOf(currentCell.getStringCellValue()));
                                            bean.setRiskId(String.valueOf(currentCell.getStringCellValue()));
                                        }
                                    }
                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("INSURED_NAME")){
                                    bean.setInsuredName(currentCell.getStringCellValue());
                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("NATIONAL_ID")){
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        bean.setIdNo(currentCell.getStringCellValue());
                                    }
                                    else if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                                        bean.setIdNo(BigDecimal.valueOf(currentCell.getNumericCellValue()).toPlainString());
                                    }
                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("PIN")){
                                    bean.setPinNo(currentCell.getStringCellValue());
                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("POSTAL_ADDRS")){
                                    bean.setPostalAddress(currentCell.getStringCellValue());
                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("POSTAL_TOWN")){
                                    bean.setPostalTown(currentCell.getStringCellValue());
                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("POSTAL_CODE")){
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                                        bean.setPostalCode(currentCell.getStringCellValue());
                                    }
                                    else if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC)
                                        bean.setPostalCode(String.valueOf(currentCell.getNumericCellValue()));

                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("MOBILE_NUMBER")){
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING)
                                        bean.setMobileNumber(currentCell.getStringCellValue());
                                    else if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC)
                                        bean.setMobileNumber(BigDecimal.valueOf(currentCell.getNumericCellValue()).toPlainString());
                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("COUNTRY")){
                                    bean.setCountry(currentCell.getStringCellValue());
                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("EFF_DATE")){
                                    if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                        bean.setEffDate(currentCell.getDateCellValue());
                                    } else if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                        try {
                                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");
                                            bean.setEffDate(sdf.parse(currentCell.getStringCellValue()));
                                        } catch (Exception e) {
                                            System.out.println("Error parsing EFF_DATE: " + currentCell.getStringCellValue());
                                            bean.setEffDate(null);
                                        }
                                    }
                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("EXP_DATE")){
                                    if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                        bean.setExpDate(currentCell.getDateCellValue());
                                    } else if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                        try {
                                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");
                                            bean.setExpDate(sdf.parse(currentCell.getStringCellValue()));
                                        } catch (Exception e) {
                                            System.out.println("Error parsing EXP_DATE: " + currentCell.getStringCellValue());
                                            bean.setExpDate(null);
                                        }
                                    }
                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("Cover_Type")){
                                    bean.setCovtType(currentCell.getStringCellValue());
                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("BUT_CHARGE PREMIUM")){
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                        bean.setButCharge(BigDecimal.valueOf(currentCell.getNumericCellValue()));
                                    } else if(currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                        try {
                                            bean.setButCharge(new BigDecimal(currentCell.getStringCellValue().trim()));
                                        } catch (NumberFormatException e) {
                                            System.out.println("Error parsing BUT_CHARGE PREMIUM: " + currentCell.getStringCellValue());
                                            bean.setButCharge(BigDecimal.ZERO);
                                        }
                                    }
                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("Gender")){
                                    bean.setGender(currentCell.getStringCellValue());
                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("Dob")){
                                    if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                        bean.setDateOfBirth(currentCell.getDateCellValue());
                                    } else if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                        try {
                                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");
                                            bean.setDateOfBirth(sdf.parse(currentCell.getStringCellValue()));
                                        } catch (Exception e) {
                                            System.out.println("Error parsing DOB: " + currentCell.getStringCellValue());
                                            bean.setDateOfBirth(null);
                                        }
                                    }
                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("RISK_SHT_DEC")){
                                    bean.setRegno(currentCell.getStringCellValue());
                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("Risk_Desc")){
                                    bean.setRiskdesc(currentCell.getStringCellValue());
                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("Sched_col1")){
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING)
                                        bean.setColumn1(currentCell.getStringCellValue());
                                    else if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC)
                                        bean.setColumn1(String.valueOf(currentCell.getNumericCellValue()));

                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("Sched_col2")){
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING)
                                        bean.setColumn2(currentCell.getStringCellValue());
                                    else if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC)
                                        bean.setColumn2(String.valueOf(currentCell.getNumericCellValue()));
                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("Sched_col3")){
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING)
                                        bean.setColumn3(currentCell.getStringCellValue());
                                    else if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC)
                                        bean.setColumn3(String.valueOf(currentCell.getNumericCellValue()));
                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("Sched_col4")){
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING)
                                        bean.setColumn4(currentCell.getStringCellValue());
                                    else if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC)
                                        bean.setColumn4(String.valueOf(currentCell.getNumericCellValue()));
                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("Sched_col5")){
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING)
                                        bean.setColumn5(currentCell.getStringCellValue());
                                    else if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC)
                                        bean.setColumn5(String.valueOf(currentCell.getNumericCellValue()));
                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("Sched_col6")){
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING)
                                        bean.setColumn6(currentCell.getStringCellValue());
                                    else if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC)
                                        bean.setColumn6(String.valueOf(currentCell.getNumericCellValue()));
                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("Sched_col7")){
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING)
                                        bean.setColumn7(currentCell.getStringCellValue());
                                    else if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC)
                                        bean.setColumn7(String.valueOf(currentCell.getNumericCellValue()));
                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("Sched_col8")){
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING)
                                        bean.setColumn8(currentCell.getStringCellValue());
                                    else if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC)
                                        bean.setColumn8(String.valueOf(currentCell.getNumericCellValue()));
                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("Sched_col9")){
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING)
                                        bean.setColumn9(currentCell.getStringCellValue());
                                    else if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC)
                                        bean.setColumn9(String.valueOf(currentCell.getNumericCellValue()));
                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("Sched_col10")){
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING)
                                        bean.setColumn10(currentCell.getStringCellValue());
                                    else if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC)
                                        bean.setColumn10(String.valueOf(currentCell.getNumericCellValue()));
                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("Sched_col11")){
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING)
                                        bean.setColumn11(currentCell.getStringCellValue());
                                    else if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC)
                                        bean.setColumn11(String.valueOf(currentCell.getNumericCellValue()));
                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("Sched_col12")){
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING)
                                        bean.setColumn12(currentCell.getStringCellValue());
                                    else if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC)
                                        bean.setColumn12(String.valueOf(currentCell.getNumericCellValue()));
                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("Sched_col13")){
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING)
                                        bean.setColumn13(currentCell.getStringCellValue());
                                    else if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC)
                                        bean.setColumn13(String.valueOf(currentCell.getNumericCellValue()));
                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("Sched_col14")){
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING)
                                        bean.setColumn14(currentCell.getStringCellValue());
                                    else if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC)
                                        bean.setColumn14(String.valueOf(currentCell.getNumericCellValue()));
                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("Sched_col15")){
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING)
                                        bean.setColumn15(currentCell.getStringCellValue());
                                    else if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC)
                                        bean.setColumn15(String.valueOf(currentCell.getNumericCellValue()));
                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("Sched_col16")){
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING)
                                        bean.setColumn16(currentCell.getStringCellValue());
                                    else if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC)
                                        bean.setColumn16(String.valueOf(currentCell.getNumericCellValue()));
                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("Sched_col17")){
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING)
                                        bean.setColumn17(currentCell.getStringCellValue());
                                    else if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC)
                                        bean.setColumn17(String.valueOf(currentCell.getNumericCellValue()));
                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("Sched_col18")){
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING)
                                        bean.setColumn18(currentCell.getStringCellValue());
                                    else if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC)
                                        bean.setColumn18(String.valueOf(currentCell.getNumericCellValue()));
                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("Sched_col19")){
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING)
                                        bean.setColumn19(currentCell.getStringCellValue());
                                    else if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC)
                                        bean.setColumn19(String.valueOf(currentCell.getNumericCellValue()));
                                }
                                else if(columns.get(columnIndex).equalsIgnoreCase("Sched_col20")){
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING)
                                        bean.setColumn20(currentCell.getStringCellValue());
                                    else if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC)
                                        bean.setColumn20(String.valueOf(currentCell.getNumericCellValue()));
                                }
                            }
                        }
                        risks.add(bean);
                    }



                }
            }
            else{
                Iterator<Row> iterator = sheet.iterator();
                while (iterator.hasNext()) {
                    Row currentRow = iterator.next();
                    if(currentRow.getRowNum()!=0) {
                        SectionImportBean section  = new SectionImportBean();
                        Iterator<Cell> cellIterator = currentRow.iterator();
                        while (cellIterator.hasNext()) {
                            Cell currentCell = cellIterator.next();
                            int columnIndex = currentCell.getColumnIndex();
                            switch(columnIndex){
                                case 0:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                                        section.setRiskId(String.valueOf(currentCell.getNumericCellValue()));
                                    }
                                    else if(currentCell.getCellType() == Cell.CELL_TYPE_STRING)
                                        section.setRiskId(String.valueOf(currentCell.getStringCellValue()));
                                    break;
                                case 1:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_STRING)
                                        section.setSectionId(String.valueOf(currentCell.getStringCellValue()));
                                    break;
                                case 2:
                                    if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                                        section.setLimit(BigDecimal.valueOf(currentCell.getNumericCellValue()));
                                    }
                                    break;
//                                case 3:
//                                    if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
//                                        section.setRate(currentCell.getNumericCellValue());
//                                    }
//                                    break;
//                                case 4:
//                                    if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
//                                        section.setDivFactor(currentCell.getNumericCellValue());
//                                    }
//                                    break;
                            }
                        }
                        sections.add(section);
                    }
                }
            }
        }

        riskSections.setRisks(risks);
        riskSections.setSections(sections);
        return riskSections;
    }
}
