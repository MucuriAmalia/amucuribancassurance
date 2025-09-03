package com.brokersystems.brokerapp.setup.service.impl;


import com.brokersystems.brokerapp.enums.AccountTypeEnum;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.server.utils.ValidatorUtils;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.AccountRepo;
import com.brokersystems.brokerapp.setup.repository.AccountTypeRepo;
import com.brokersystems.brokerapp.setup.repository.OrgBranchRepository;
import com.brokersystems.brokerapp.setup.service.SubAgentMarketerImportService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SubAgentMarketerImportImpl implements SubAgentMarketerImportService {

    @Autowired
    private AccountTypeRepo accountTypeRepo;

    @Autowired
    private OrgBranchRepository orgBranchRepo;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private ValidatorUtils validatorUtils;

    @Override
    public Map<String, Object> importSubAgentMarketer(MultipartFile file) throws BadRequestException {
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            throw new BadRequestException("Upload files with .xlsx or .xls extension only");
        }

        Map<String, Object> response = new HashMap<>();
        List<String> invalidRecords = new ArrayList<>();
        List<AccountDef> accountDefList = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);

            Sheet sheetOne = workbook.getSheetAt(0);
            int startRowIndex = findDataStartRow(sheetOne);
            if (startRowIndex == -1) {
                throw new BadRequestException("Sheet One contains no data.");
            }

            int totalRows = sheetOne.getLastRowNum();
            int processedRows = 0;

            for (int rowNum = startRowIndex; rowNum <= totalRows; rowNum++) {
                System.out.println("Total Rows in Sheet: " + totalRows);
                Row row = sheetOne.getRow(rowNum);
                System.out.println("Processing row: " + rowNum+1);
                if (isBlankRow(row)) {
                    System.out.println("Skipping blank row: " + rowNum+1);
                    continue;
                }
                    AccountDef accountDef = new AccountDef();
                    String accountType = getCellStringValue(row.getCell(0));
                    if (accountType != null && !accountType.isEmpty()) {
                        accountType = accountType.trim();
                    } else {
                        throw new BadRequestException("Account type is required");
                    }
                    if (accountType.equalsIgnoreCase("MARKETERS")) {
                        accountType = "MRK";
                    } else if (accountType.equalsIgnoreCase("SUB AGENTS")) {
                        accountType = "SUB";
                    } else if (accountType.equalsIgnoreCase("INTRODUCERS")) {
                        accountType = "INT";
                    } else {
                        throw new BadRequestException("Invalid account type value: " + accountType + " in column ACT_ACCOUNT_TYPE at row " + rowNum+1);
                    }
                    AccountTypes accountTypes = accountTypeRepo.findOne(QAccountTypes.accountTypes.accountType.eq(AccountTypeEnum.valueOf(accountType)));
                    accountDef.setAccountType(accountTypes);
                    accountDef.setAbsaNo(getCellStringValue(row.getCell(1)));
                    accountDef.setName(getCellStringValue(row.getCell(2)));
                    String phoneNo = getCellStringValue(row.getCell(3));
                    if (phoneNo != null && !phoneNo.isEmpty()) {
                        phoneNo = phoneNo.trim();
                    } else {
                        throw new BadRequestException("Missing phone number in column AGN_TEL at row " + rowNum+1);
                    }
                    if (!phoneNo.matches("^\\+?\\d{10,}$")) {
                        throw new BadRequestException("Invalid phone number " + phoneNo + " in column AGN_TEL at row " + rowNum+1);
                    }
                    accountDef.setPhoneNo(phoneNo);
                    accountDef.setBankAccount(getCellStringValue(row.getCell(4)));

                    String agentPin = getCellStringValue(row.getCell(5));
                    String pinNo = agentPin.trim();
                    if (!validatorUtils.validatePin(pinNo)) {
                        throw new BadRequestException("Invalid Pin Number " + pinNo);
                    }
                    accountDef.setPinNo(pinNo);

                    String agentStatus = getCellStringValue(row.getCell(6));
                    String accountStatus = agentStatus.trim();
                    if (accountStatus.equalsIgnoreCase("ACTIVE")) {
                        accountStatus = "A";
                    } else if (accountStatus.equalsIgnoreCase("DRAFT")) {
                        accountStatus = "D";
                    } else if (accountStatus.equalsIgnoreCase("SUSPENDED")) {
                        accountStatus = "DA";
                    } else if (accountStatus.equalsIgnoreCase("INACTIVE")) {
                        accountStatus = "I";
                    } else {
                        throw new BadRequestException("Invalid agent status " + agentStatus + " in column AGN_STATUS at row " + rowNum+1);
                    }
                    accountDef.setStatus(accountStatus);
                    accountDef.setCreatedDate(getCellDateValue(row.getCell(7)));
                    accountDef.setCreatedBy(getCellStringValue(row.getCell(8)));
                    String commStatus = getCellStringValue(row.getCell(9));
                    String commissionStatus = commStatus.trim();

                    if (commissionStatus.equalsIgnoreCase("Y")) {
                        commissionStatus = "Yes";
                    } else if (commissionStatus.equalsIgnoreCase("N") || commStatus.isEmpty()) {
                        commissionStatus = "No";
                    } else {
                        throw new BadRequestException("Invalid commission status " + commStatus + " in column AGN_COMM_ALLOWED at row " + rowNum+1);
                    }
                    accountDef.setCommissionEarning(commissionStatus);

                    String branchCode = getCellStringValue(row.getCell(10));
                    System.out.println("branchCode "+branchCode);
                    if (branchCode != null && !branchCode.isEmpty()) {
                        branchCode = branchCode.trim();
                    } else {
                        throw new BadRequestException("Branch code is missing in column AGN_BRN_CODE at row " + rowNum+1);
                    }
                    OrgBranch orgBranch = orgBranchRepo.findOne(QOrgBranch.orgBranch.obShtDesc.eq(branchCode));
                    System.out.println("Branch code" +orgBranch);
                    if (orgBranch == null) {
                        throw new BadRequestException("Branch code " + branchCode + " in column AGN_BRN_CODE at row " + rowNum+1 + " not set in the system, please contact system admin");
                    }
                    accountDef.setBranch(orgBranch);

                    String idNo = getCellStringValue(row.getCell(11));
                    if (idNo != null && !idNo.isEmpty()) {
                        idNo = idNo.trim();
                    } else {
                        throw new BadRequestException("Agent id is missing in column AGN_ID_NO at row " + rowNum+1);
                    }
                    if (!validatorUtils.validateId(idNo)) {
                        throw new BadRequestException("Invalid agent number " + idNo + " in column AGN_ID_NO at row " + rowNum+1);
                    }

                    accountDef.setIdNumber(idNo);
                    accountDef.setEmail(getCellStringValue(row.getCell(12)));
                    accountDef.setCreationMode("DATA_LOADING");
                    accountDef.setModifiedDate(new Date());
                    accountDef.setModifiedBy(String.valueOf(userUtils.getCurrentUser()));
                    accountDefList.add(accountDef);

                    processedRows++;
            }
            int totalProcessed = totalRows - processedRows;
            System.out.println("totalProcessed = " + totalProcessed);
            int successfulAgents = accountDefList.size();
            if (!(totalProcessed > 0) && successfulAgents > 0){
                accountRepo.save(accountDefList);
            }
            response.put("successfulAgents", successfulAgents);
            return response;
        } catch (Exception e) {
            throw new BadRequestException("Error processing the Excel file: " + e.getMessage());
        }

    }

    private boolean isHeaderRow(Row row) {
        if (row == null) return false;

        for (int cellNum = 0; cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell == null) continue;
            String cellValue = getCellStringValue(cell);
            if (cellValue != null) {
                cellValue = cellValue.toLowerCase().trim();
                if (cellValue.contains("act_account_type") ||
                        cellValue.contains("agn_sht_desc") ||
                        cellValue.contains("agn_name") ||
                        cellValue.contains("agn_tel1") ||
                        cellValue.contains("agn_acc_no") ||
                        cellValue.contains("agn_pin") ||
                        cellValue.contains("agn_status") ||
                        cellValue.contains("agn_date_created") ||
                        cellValue.contains("agn_created_by") ||
                        cellValue.contains("agn_comm_allowed")) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isBlankRow(Row row) {
        if (row == null) return true;

        for (int cellNum = 0; cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                return false;
            }
        }
        return true;
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue().trim();
            case Cell.CELL_TYPE_NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case Cell.CELL_TYPE_BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }

    private BigDecimal getCellBigDecimalValue(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:
                return BigDecimal.valueOf(cell.getNumericCellValue());
            case Cell.CELL_TYPE_BOOLEAN:
                try {
                    return new BigDecimal(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return null;
                }
            default:
                return null;
        }
    }

    private Date getCellDateValue(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                }
                return null;

            case Cell.CELL_TYPE_STRING:
                try {
                    String dateStr = cell.getStringCellValue().trim();
                    if (!dateStr.isEmpty()) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        return sdf.parse(dateStr);
                    }
                } catch (ParseException e) {
                    System.out.println("Error parsing date: " + e.getMessage());
                }
                return null;

            case Cell.CELL_TYPE_FORMULA:
                try {
                    return cell.getDateCellValue();
                } catch (Exception e) {
                    System.out.println("Formula error: " + e.getMessage());
                }
                return null;

            default:
                return null;
        }
    }


    private BigInteger getCellBigIntegerValue(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:
                return BigInteger.valueOf((long) cell.getNumericCellValue());
            case Cell.CELL_TYPE_STRING:
                try {
                    return new BigInteger(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return null;
                }
            default:
                return null;
        }
    }


    private int findDataStartRow(Sheet sheet) {
        for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (row != null) {
                System.out.println("Checking row: " + rowNum);
                for (Cell cell : row) {
                    System.out.print(getCellStringValue(cell) + " | ");
                }
                System.out.println();
            }
            if (isHeaderRow(row)) {
                return rowNum + 1;
            }
        }
        return -1;
    }

    private Double getCellDoubleValue(Cell cell) {
        return (cell != null && cell.getCellType() == Cell.CELL_TYPE_NUMERIC) ? cell.getNumericCellValue() : null;
    }
}
