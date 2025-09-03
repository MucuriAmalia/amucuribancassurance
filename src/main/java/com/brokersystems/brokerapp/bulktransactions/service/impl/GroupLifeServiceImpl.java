package com.brokersystems.brokerapp.bulktransactions.service.impl;

import com.brokersystems.brokerapp.bulktransactions.dtos.CreditLifeDTO;
import com.brokersystems.brokerapp.bulktransactions.dtos.GroupLifeDTO;
import com.brokersystems.brokerapp.bulktransactions.models.*;
import com.brokersystems.brokerapp.bulktransactions.repositories.GroupLifeRepository;
import com.brokersystems.brokerapp.bulktransactions.repositories.GroupLifeRisksRepo;
import com.brokersystems.brokerapp.bulktransactions.service.GroupLifeService;
import com.brokersystems.brokerapp.bulktransactions.utils.ExcelReaderUtil;
import com.brokersystems.brokerapp.enums.AccountTypeEnum;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.DateUtilities;
import com.brokersystems.brokerapp.server.utils.TemplateMerger;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.*;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.brokersystems.brokerapp.trans.model.QTransactionMapping;
import com.brokersystems.brokerapp.trans.model.SystemTrans;
import com.brokersystems.brokerapp.trans.model.SystemTransactions;
import com.brokersystems.brokerapp.trans.model.TransactionMapping;
import com.brokersystems.brokerapp.trans.repository.SystemTransRepo;
import com.brokersystems.brokerapp.trans.repository.SystemTransactionsRepo;
import com.brokersystems.brokerapp.trans.repository.TransMappingRepo;
import com.brokersystems.brokerapp.trans.service.PolicyAuthorization;
import com.brokersystems.brokerapp.uw.dtos.PolicyCreateDTO;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.model.RiskSectionBean;
import com.brokersystems.brokerapp.uw.model.RiskTrans;
import com.brokersystems.brokerapp.uw.model.RiskTransBean;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import com.brokersystems.brokerapp.uw.repository.RiskTransRepo;
import com.brokersystems.brokerapp.uw.validators.RenewalJobListener;
import com.mysema.query.types.Predicate;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.easybatch.core.job.Job;
import org.easybatch.core.job.JobBuilder;
import org.easybatch.core.job.JobReport;
import org.easybatch.core.processor.RecordProcessor;
import org.easybatch.core.reader.IterableRecordReader;
import org.easybatch.core.record.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class GroupLifeServiceImpl implements GroupLifeService {

    @Autowired
    private SequenceRepository sequenceRepository;
    @Autowired
    private TemplateMerger templateMerger;
    @Autowired
    private PaymentModeRepo paymentModeRepo;
    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private ClientTypeRepo clientTypeRepo;
    @Autowired
    private OrgBranchRepository orgBranchRepository;
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private UserUtils userUtils;
    @Autowired
    private GroupLifeRepository groupLifeRepo;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private GroupLifeRisksRepo groupLifeRisksRepo;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ProductsRepo productsRepo;
    @Autowired
    private BindersRepo bindersRepo;
    @Autowired
    private SubClassRepo subClassRepo;
    @Autowired
    private CoverTypesRepo coverTypesRepo;
    @Autowired
    private BinderDetRepo binderDetRepo;
    @Autowired
    private ParamService paramService;
    @Autowired
    private PolicyTransRepo policyTransRepo;
    @Autowired
    private CommRatesRepo commRatesRepo;
    @Autowired
    private RiskTransRepo riskTransRepo;
    @Autowired
    private PolicyAuthorization authService;
    @Autowired
    private SystemTransRepo systemTransRepo;
    @Autowired
    private SystemTransactionsRepo systemTransactionsRepo;
    @Autowired
    private DateUtilities dateUtils;
    @Autowired
    private UploadValidatorsUtils uploadValidatorsUtils;

    @Autowired
    private UserRepository userRepo;


    private  static final int BATCH_SIZE = 500;

    @Override
    @Async
    public void  uploadGroupLife(File file, Long userId) throws BadRequestException {
        User user = userRepo.findOne(userId);

        Map<String, Object> response = new HashMap<>();
        List<String> invalidRecords = new ArrayList<>();
        List<GroupLife> groupLifeList = new ArrayList<>();
        List<GroupLifeRisks> groupLifeRisksList = new ArrayList<>();

        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
            Workbook workbook = WorkbookFactory.create(inputStream);

            Sheet sheetOne = workbook.getSheetAt(0);
            Map<String, GroupLife> groupLifeMap = new HashMap<>();
            int startRowIndex = findDataStartRow(sheetOne);
            if (startRowIndex == -1) {
                throw new BadRequestException("Sheet One contains no data.");
            }

            String bulkCode = null;
            String transType = "GLU";
            Predicate pedSystem = QSystemSequence.systemSequence.transType.eq("GLU");
            if (sequenceRepository.count(pedSystem) == 0)
                throw new BadRequestException("Sequence for Group Life Upload has not been defined");


            for (int rowNum = startRowIndex; rowNum <= sheetOne.getLastRowNum(); rowNum++) {
                SystemSequence sequenceSystem = sequenceRepository.findOne(pedSystem);
                Long sequenceNumber = sequenceSystem.getNextNumber();
                bulkCode = transType + String.format("%05d", sequenceNumber);
                sequenceSystem.setLastNumber(sequenceNumber);
                sequenceSystem.setNextNumber(sequenceNumber + 1);
                sequenceRepository.save(sequenceSystem);

                System.out.println("Total Rows in Sheet: " + sheetOne.getLastRowNum());
                Row row = sheetOne.getRow(rowNum);
                System.out.println("Processing row: " + rowNum);
                if (isBlankRow(row)) {
                    System.out.println("Skipping blank row: " + rowNum);
                    continue;
                }

                try {
                    GroupLife groupLife = new GroupLife();

                    String serialNo = ExcelReaderUtil.getCellValue(row, 0);//getCellStringValue(row.getCell(0));
                    groupLife.setSerialNo(serialNo);
                    System.out.println("serialNo" + serialNo);
//                    if(serialNo.isEmpty()){
//                        throw new BadRequestException("Please provide a serial number that will be used to link with the risk details on sheet two");
//                    }

                    String clntIdNo = ExcelReaderUtil.getCellValue(row, 1); // getCellStringValue(row.getCell(3));
                    groupLife.setClientId(clntIdNo);
                    String cif = ExcelReaderUtil.getCellValue(row, 2); //getCellStringValue(row.getCell(4));
                    groupLife.setClientCIF(cif); //getCellStringValue(row.getCell(10)));

                    final ClientDef client = clientRepository.findByCifandIdNoIgnoreCase(cif, clntIdNo);
                    if (client != null) {
                        groupLife.setClientName(client.getFname());
                        groupLife.setClientOtherNames(client.getOtherNames());
                        groupLife.setClientDOB(client.getDob());
                        groupLife.setClientPin(client.getPinNo());
                        groupLife.setClientEmail(client.getEmailAddress());
                        groupLife.setClientPhone(client.getPhoneNo());
                        groupLife.setTenantType(client.getTenantType());
                        groupLife.setCountryCode(client.getCountry());

                    } else {
                        throw new BadRequestException("Client with ID No " + clntIdNo + " not found.");
                    }


                    String branchCode = ExcelReaderUtil.getCellValue(row, 3); //getCellStringValue(row.getCell(11));
                    OrgBranch orgBranch = orgBranchRepository.findOne(QOrgBranch.orgBranch.obShtDesc.eq(branchCode.trim()));
                    if (orgBranch != null) {
                        groupLife.setOrgBranch(orgBranch);
                    } else {
                        throw new BadRequestException("Branch code " + branchCode + " in sheet two is not setup in the system, please contact system admin.");
                    }
                    String insurerCode = ExcelReaderUtil.getCellValue(row, 4); //getCellStringValue(row.getCell(12));
                    AccountDef underWriter = accountRepo.findOne(QAccountDef.accountDef.shtDesc.eq(insurerCode.trim()));
                    if (underWriter != null) {
                        groupLife.setInsurerCode(underWriter);
                    } else {
                        throw new BadRequestException("Insurer with code " + insurerCode + " is not setup in the system, please contact system admin");
                    }
                    groupLife.setProductGroup(ExcelReaderUtil.getCellValue(row, 5)); //getCellStringValue(row.getCell(13)));
                    String productName = (ExcelReaderUtil.getCellValue(row, 6)); //getCellStringValue(row.getCell(14)));

                    //default for all group life
                    String searchCode = "GLPU";
                    String normalizedProductName = productName.trim().toLowerCase().replace(" ", "");
                    ProductsDef product = productsRepo.findByNormalizedName(normalizedProductName, searchCode);
                    if (product == null) {
                        throw new BadRequestException("Product " + normalizedProductName + " not set in the system, please contact system admin.");
                    }
                    String contract = ExcelReaderUtil.getCellValue(row, 13);
                    groupLife.setContractName(contract);
                    System.out.println("finding binders for "+groupLife.getInsurerCode().getAcctId() +" "+ product.getProCode()+" "+contract);
                    List<BindersDef> binders =  bindersRepo.findBinderByAccIdAndBinName(groupLife.getInsurerCode().getAcctId(), product.getProCode()); ///bindersRepo.findBinderByAccId(groupLife.getInsurerCode().getAcctId(), product.getProCode());
                    if (binders.isEmpty()) {
                        throw new BadRequestException("Binder for product " + normalizedProductName + " and insurer with code " + groupLife.getInsurerCode().getShtDesc()
                                + " not set in the system, please contact system admin.");
                    }
                    BindersDef binder = binders.get(0);
                    String coverType = (ExcelReaderUtil.getCellValue(row, 7)); //getCellStringValue(row.getCell(15)));
                    String normalizedCoverType = coverType.trim().toLowerCase().replace(" ", "");
                    SubClassDef subClassDef = subClassRepo.findBySubClassName(normalizedProductName);
                    if (subClassDef != null){
                        groupLife.setProductName(subClassDef.getSubDesc());
                    } else {
                        throw new BadRequestException("Sub class for product " + productName + " not set in the system, please contact system admin.");
                    }
                    CoverTypesDef coverTypesDef = coverTypesRepo.findCoverTypesByBindId(binder.getBinId(), normalizedCoverType);
                    if (coverTypesDef != null) {
                        groupLife.setCoverType(coverTypesDef.getCovName());
                    }else {
                        throw new BadRequestException("Cover type " + coverType + " not set for insurer with code " + groupLife.getInsurerCode().getShtDesc() +" please contact system admin.");
                    }

                    groupLife.setPolTerm(Integer.valueOf(ExcelReaderUtil.getCellValue(row, 8))); //getCellStringValue(row.getCell(16))));
                    groupLife.setFrequency(ExcelReaderUtil.validateFrequency(ExcelReaderUtil.getCellValue(row, 9), row.getRowNum())); //getCellStringValue(row.getCell(17)), row.getRowNum()));
                    String currency = ExcelReaderUtil.getCellValue(row, 10); //getCellStringValue(row.getCell(18));
                    if (currency != null && !currency.trim().isEmpty()) {
                        currency = currency.trim();
                        Currencies currencies = currencyRepository.findOne(QCurrencies.currencies.curIsoCode.eq(currency));
                        if (currencies == null) {
                            throw new BadRequestException("Currency code " + currency + " is not setup in the system, please contact system admin");
                        }
                        groupLife.setCurrencyCode(currencies);
                    } else {
                        throw new BadRequestException("Currency is required in row " + row.getRowNum() + " in Currency ISO code column");
                    }

                    groupLife.setCoverFrom(ExcelReaderUtil.parseFlexibleDate((ExcelReaderUtil.getCellValue(row, 11)))); //getCellDateValue(row.getCell(19)));
                    groupLife.setCoverTo(ExcelReaderUtil.parseFlexibleDate((ExcelReaderUtil.getCellValue(row, 12)))); //getCellDateValue(row.getCell(20)));
                    groupLife.setUploadDate(new Date());
                    groupLife.setUploadedBy(user);
                    groupLife.setTranStatus("N");
                    groupLife.setTransType(transType);
                    groupLife.setRefCode(bulkCode);

                    groupLifeList.add(groupLife);
                    if(groupLifeList.size() >= BATCH_SIZE){
                        groupLifeRepo.save(groupLifeList);
                        groupLifeList.clear();
                    }
                    groupLifeMap.put(serialNo, groupLife);
                } catch (Exception e) {
//                    throw new BadRequestException("Sheet One " + e.getMessage());
                }
            }

            int successfulPol = groupLifeList.size();
            int invalidPol = invalidRecords.size();
            //if (!(invalidPol > 0) && successfulPol > 0) {
            if(!groupLifeList.isEmpty()){
                groupLifeRepo.save(groupLifeList);
                groupLifeList.clear();
            }


            Sheet sheetTwo = workbook.getSheetAt(1);
            int startRowIndexTwo = findDataStartRow(sheetTwo);
            if (startRowIndexTwo == -1) {
                throw new BadRequestException("Sheet Two contains no data.");
            }


            for (int rowNum = startRowIndexTwo; rowNum <= sheetTwo.getLastRowNum(); rowNum++) {
                Row row = sheetTwo.getRow(rowNum);
                if (isBlankRow(row)) continue;

                try {

                    List<GroupLifeRisks> riskList = Optional.ofNullable(
                            (List<GroupLifeRisks>) groupLifeRisksRepo.findAll(
                                    QGroupLifeRisks.groupLifeRisks.transStatus.eq("Y")
                                            .and(QGroupLifeRisks.groupLifeRisks.transCode.eq(bulkCode))
                            )
                    ).orElse(new ArrayList<>());

                    System.out.println("riskList:: " + riskList);

                    List<GroupLife> bulkPolicyList = (List<GroupLife>) groupLifeRepo.findAll(
                            QGroupLife.groupLife.tranStatus.eq("N")
                                    .and(QGroupLife.groupLife.refCode.eq(bulkCode))
                    );
                    System.out.println("bulkPolicyList:: " + bulkPolicyList);

                    // Convert bulkPolicyList into a Set for faster lookups
                    Set<String> refCodeSet = bulkPolicyList.stream()
                            .map(GroupLife::getRefCode)
                            .collect(Collectors.toSet());

                    for (GroupLifeRisks checkRisk : riskList) {
                        if (refCodeSet.contains(checkRisk.getTransCode())) {
                            // Mismatch found, immediately throw an error
                            throw new BadRequestException(
                                    "Serial No " + checkRisk.getSerialNo() + " in sheet 2 is pending processing, process before re-uploading this file"
                            );
                        }
                    }

                    // Proceed to add new risk if no mismatch was found
                    String serialNo = ExcelReaderUtil.getCellValue(row, 0); //getCellStringValue(row.getCell(0));
                    System.out.println("Serial No sheet two: " + serialNo);
                    GroupLife linkedPolicy = groupLifeMap.get(serialNo);

                    if (linkedPolicy == null) {
                        throw new BadRequestException(
                                "Row " + (rowNum + 1) + ": Serial number '" + serialNo + "' not found in Sheet One."
                        );
                    }
                    GroupLifeRisks lifeRisks = new GroupLifeRisks();

                    lifeRisks.setSerialNo(serialNo);
                    String clntIdNo = ExcelReaderUtil.getCellValue(row, 1); //getCellStringValue(row.getCell(4));
                    String cif = ExcelReaderUtil.getCellValue(row, 2); //getCellStringValue(row.getCell(4));

                    lifeRisks.setInsuredCIF(cif); //getCellStringValue(row.getCell(10)));
                    lifeRisks.setInsuredID(clntIdNo); //getCellStringValue(row.getCell(4)));
                    final ClientDef client = clientRepository.findByCifandIdNoIgnoreCase(cif, clntIdNo);
                    if (client != null) {
                        lifeRisks.setInsuredFName(client.getFname());
                        lifeRisks.setInsuredOtherNames(client.getOtherNames());
                        lifeRisks.setInsuredDOB(client.getDob());
                        lifeRisks.setInsuredPin(client.getPinNo());
                        lifeRisks.setInsuredEmail(client.getEmailAddress());
                        lifeRisks.setInsuredPhone(client.getPhoneNo());
                        lifeRisks.setInsuredType(client.getTenantType());

                    } else {
                        throw new BadRequestException("Client with ID No "+ clntIdNo + " not found.");
                    }
//                    lifeRisks.setInsuredFName(ExcelReaderUtil.getCellValue(row, 1)); //getCellStringValue(row.getCell(1)));
//                    lifeRisks.setInsuredOtherNames(ExcelReaderUtil.getCellValue(row, 2)); //getCellStringValue(row.getCell(2)));
//                    lifeRisks.setInsuredDOB(ExcelReaderUtil.parseFlexibleDate((ExcelReaderUtil.getCellValue(row, 3)))); //getCellDateValue(row.getCell(3)));
//                    lifeRisks.setInsuredID(ExcelReaderUtil.getCellValue(row, 4)); //getCellStringValue(row.getCell(4)));
//                    lifeRisks.setInsuredPin(ExcelReaderUtil.getCellValue(row, 5)); //getCellStringValue(row.getCell(5)));
//                    lifeRisks.setInsuredEmail(ExcelReaderUtil.getCellValue(row, 6)); //getCellStringValue(row.getCell(6)));
//                    lifeRisks.setInsuredPhone(ExcelReaderUtil.getCellValue(row, 7)); //getCellStringValue(row.getCell(7)));
//                    lifeRisks.setInsuredCIF(ExcelReaderUtil.getCellValue(row, 8)); //getCellStringValue(row.getCell(8)));
                    lifeRisks.setEmployeeCode(ExcelReaderUtil.getCellValue(row, 3)); //getCellStringValue(row.getCell(9)));
//                    String clientType = ExcelReaderUtil.getCellValue(row, 10); //getCellStringValue(row.getCell(10));
//                    String normalizedClientType = clientType.trim().toLowerCase().replace(" ", "");
//                    ClientTypes clientTypes = clientTypeRepo.findByNormalizeType(normalizedClientType);
//                    if (clientTypes != null) {
//                        lifeRisks.setInsuredType(clientTypes);
//                    } else {
//                        throw new BadRequestException("Client type " + clientType + " is not setup, please contact system admin");
//                    }
                    String branchCode = ExcelReaderUtil.getCellValue(row, 4); //getCellStringValue(row.getCell(11));
                    OrgBranch orgBranch = orgBranchRepository.findOne(QOrgBranch.orgBranch.obShtDesc.eq(branchCode.trim()));
                    if (orgBranch != null) {
                        lifeRisks.setInsuredBranch(orgBranch);
                    } else {
                        throw new BadRequestException("Branch code " + branchCode + " in sheet two is not setup in the system, please contact system admin.");
                    }

                    String insurerCode = ExcelReaderUtil.getCellValue(row, 5); //getCellStringValue(row.getCell(12));
                    AccountDef underWriter = accountRepo.findOne(QAccountDef.accountDef.shtDesc.eq(insurerCode.trim()));
                    if (underWriter != null) {
                        lifeRisks.setInsurerCode(underWriter);
                    } else {
                        throw new BadRequestException("Insurer with code " + insurerCode + " is not setup in the system, please contact system admin");
                    }
                    BigDecimal sumInsured  =ExcelReaderUtil.parseBigDecimalSafe(ExcelReaderUtil.getCellValue(row, 6)); //getCellBigDecimalValue(row.getCell(13)));
                    sumInsured = sumInsured.abs();
                    lifeRisks.setSumInsured(sumInsured);
                    BigDecimal premium = ExcelReaderUtil.parseBigDecimalSafe(ExcelReaderUtil.getCellValue(row, 7)); //getCellBigDecimalValue(row.getCell(14)));
                    premium = premium.abs();
                    lifeRisks.setRisksPremium(premium);
                    lifeRisks.setTransDate(ExcelReaderUtil.parseFlexibleDate((ExcelReaderUtil.getCellValue(row, 8)))); //getCellDateValue(row.getCell(15)));
                    lifeRisks.setGroupLife(linkedPolicy);
                    lifeRisks.setUploadedDate(new Date());
                    lifeRisks.setUploadedBy(user);
                    lifeRisks.setTransStatus("Y");
                    lifeRisks.setTransType(transType);
                    lifeRisks.setTransCode(bulkCode);

                    groupLifeRisksList.add(lifeRisks);
                    if (groupLifeRisksList.size() >= BATCH_SIZE) {
                        groupLifeRisksRepo.save(groupLifeRisksList);
                        groupLifeRisksList.clear();
                    }
                } catch (Exception e) {
//                    throw new BadRequestException("Sheet Two " + e.getMessage());
                }
            }

            int successfulRisks = groupLifeRisksList.size();
            int invalidRisks = invalidRecords.size();

            if (!groupLifeRisksList.isEmpty()) {
                groupLifeRisksRepo.save(groupLifeRisksList);
                groupLifeRisksList.clear();
            }

            response.put("successfulPolicies", groupLifeList.size());
            response.put("successfulRisks", groupLifeRisksList.size());
            response.put("failedRecords", invalidRecords.size());
            response.put("invalidRecords", invalidRecords);

//            return response;

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
                if (cellValue.contains("policy") ||
                        cellValue.contains("number") ||
                        cellValue.contains("name") ||
                        cellValue.contains("code") ||
                        cellValue.contains("amount") ||
                        cellValue.contains("serial no") ||
                        cellValue.contains("insured") ||
                        cellValue.contains("client name") ||
                        cellValue.contains("term") ||
                        cellValue.contains("type")) {
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
                return rowNum + 1; // Start after the header row
            }
        }
        return -1;
    }

    private Double getCellDoubleValue(Cell cell) {
        return (cell != null && cell.getCellType() == Cell.CELL_TYPE_NUMERIC) ? cell.getNumericCellValue() : null;
    }

    private static final List<String> VALID_FREQUENCIES = Arrays.asList(
            "Daily", "Weekly", "Monthly", "Quarterly",
            "Semi-Annually", "Annually", "Single"
    );

    private String validateFrequency(String frequency, int rowIndex) throws BadRequestException {
        if (frequency == null || frequency.trim().isEmpty()) {
            throw new BadRequestException("Payment frequency is required at Row " + (rowIndex + 1) + " in Payment frequency column");
        }

        frequency = frequency.trim();
        if (!VALID_FREQUENCIES.contains(frequency)) {
            throw new BadRequestException("Invalid frequency: '" + frequency + "' at Row " + (rowIndex + 1) + " in Payment frequency column");
        }

        return frequency;
    }

    @Override
    public DataTablesResult<GroupLifeDTO> findUnprocessedGroupLife(DataTablesRequest request) {
        Long currentUserId = userUtils.getCurrentUser().getId();

        List<Object[]> unprocessedGroupLife = groupLifeRepo.findUnprocessedGroupLife((request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%", request.getPageNumber(), request.getPageSize(), currentUserId);
        final List<GroupLifeDTO> groupLifeDTOs = new ArrayList<>();
        long rowCount = 0L;
        if (!unprocessedGroupLife.isEmpty())
            rowCount = ((BigInteger) unprocessedGroupLife.get(0)[9]).intValue();
        for (Object[] unprocessLife : unprocessedGroupLife) {
            GroupLifeDTO groupLifeDTO = new GroupLifeDTO();
            groupLifeDTO.setGroupLifeId(((BigInteger) unprocessLife[0]).longValue());
            groupLifeDTO.setCoverType((String) unprocessLife[1]);
            groupLifeDTO.setProductName((String) unprocessLife[2]);
            groupLifeDTO.setInsurerName((String) unprocessLife[3]);
            groupLifeDTO.setClientFName((String) unprocessLife[4]);
            groupLifeDTO.setClientOtherNames((String) unprocessLife[5]);
            groupLifeDTO.setCoverFrom((Date) unprocessLife[6]);
            groupLifeDTO.setCoverTo((Date) unprocessLife[7]);
            groupLifeDTO.setUploadDate((Date) unprocessLife[8]);

            groupLifeDTOs.add(groupLifeDTO);
        }
        Page<GroupLifeDTO> page = new PageImpl<>(groupLifeDTOs, request, rowCount);

        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<GroupLifeDTO> viewUnprocessedGroupLife(DataTablesRequest request) {
        Long currentUserId = userUtils.getCurrentUser().getId();

        List<Object[]> viewGroupLife = groupLifeRepo.viewUnprocessedGroupLife((request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%", request.getPageNumber(), request.getPageSize(), currentUserId);
        final List<GroupLifeDTO> groupLifeViews = new ArrayList<>();
        long rowCount = 0L;
        if (!viewGroupLife.isEmpty()) rowCount = ((BigInteger) viewGroupLife.get(0)[10]).intValue();
        for (Object[] lifeGroup : viewGroupLife) {
            GroupLifeDTO groupLifeView = new GroupLifeDTO();
            groupLifeView.setPolId(((BigInteger) lifeGroup[0]).longValue());
            groupLifeView.setCoverType((String) lifeGroup[1]);
            groupLifeView.setPolNo((String) lifeGroup[2]);
            groupLifeView.setTotPremium((BigDecimal) lifeGroup[3]);
            groupLifeView.setClientFName((String) lifeGroup[4]);
            groupLifeView.setClientOtherNames((String) lifeGroup[5]);
            groupLifeView.setCoverFrom((Date) lifeGroup[6]);
            groupLifeView.setCoverTo((Date) lifeGroup[7]);
            groupLifeView.setProcessedDate((Date) lifeGroup[8]);
            groupLifeView.setTranStatus((String) lifeGroup[9]);

            groupLifeViews.add(groupLifeView);
        }
        Page<GroupLifeDTO> page = new PageImpl<>(groupLifeViews, request, rowCount);

        return new DataTablesResult<>(request, page);
    }

    @Override
    public PolicyTrans processSingleGroupLifePol(Long groupId,Long userId, boolean isApproved) throws BadRequestException {
        User user = userRepo.findOne(userId);

        PolicyCreateDTO policyDto = new PolicyCreateDTO();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        policyDto.setBulkUpload("true");

        policyDto.setPolicyId(null);
        policyDto.setPolNo(null);
        policyDto.setProposalNo(null);
        policyDto.setPaidInsts(null);

        policyDto.setCoinsuranceBusiness(null);
        policyDto.setError(null);
        policyDto.setOldpolNo(null);
        policyDto.setClientPolNo(null);
        policyDto.setPolRevNo(null);
        policyDto.setPolRevStatus(null);

        policyDto.setPolPaidToDate(null);

        policyDto.setCoverFrom(null);
        policyDto.setCoverTo(null);
        policyDto.setRenewalDate(null);
        policyDto.setImportRisks(false);
        policyDto.setAdminFeePolicy(null);

        policyDto.setSubAgentId(null);

        policyDto.setBindCodes(null);
        policyDto.setBindName(null);
        policyDto.setProductName(null);
        policyDto.setCardId(null);
        policyDto.setQuizTaken(null);
        policyDto.setInsuranceCompany(null);

// Optional fields
        policyDto.setCommRate(null);
        policyDto.setSubAgentCommRate(null);
        policyDto.setIntroducerCommRate(null);
        policyDto.setMarketerCommRate(null);

        policyDto.setPrevPolicy(null);
        policyDto.setInterfaceType(null);
        policyDto.setStatus(null);

        policyDto.setTransType(null);

        policyDto.setTopUpFreq(null);
        policyDto.setInvestment(null);
        policyDto.setTopUp(null);
        policyDto.setInvestmentFreq(null);
        policyDto.setInvestmentTerm(null);
        policyDto.setAllowTopUps(false);
        policyDto.setFirstTopUp(null);
        policyDto.setLastTopUp(null);

        policyDto.setIntroducerAgentId(null);
        policyDto.setMarketerAgentId(null);
        policyDto.setLeadsManId(null);
        policyDto.setAbsaNoIntroducer("");
        policyDto.setAbsaNoLeadsMan("");
        policyDto.setAbsaNoSubAgent("");
        policyDto.setAbsaNoMarketer("");

        policyDto.setAccrualInstDate(null);
        policyDto.setSchemePolicy(null);
        policyDto.setCheckerIds(null);
        policyDto.setAccrualPaymentType(null);
        policyDto.setCover_option_fpp(null);
        policyDto.setCover_option_pa(null);
        policyDto.setCover_option_en(null);
        policyDto.setCover_option_up(null);

//// RiskSectionBean setup
//        List<RiskSectionBean> sections = new ArrayList<>();
//        RiskSectionBean section = new RiskSectionBean();
//        section.setSectionId(29226L); // assuming a setter exists like setSectionId(Long)
//        section.setRatesApplicable("Y");
//        section.setRate(new BigDecimal("0.55"));
//        section.setDivFactor(new BigDecimal("100"));
//        section.setFreeLimit(BigDecimal.ZERO);
//        section.setAmount(new BigDecimal("26"));
//        sections.add(section);
//        policyDto.setSections(sections);


// RiskTransBean setup
//        RiskTransBean riskBean = new RiskTransBean();
//        riskBean.setRiskId(null);
//        riskBean.setTransType(null);
//        riskBean.setRiskIdentifier(null);
//        riskBean.setBindCode(35746L);
//        riskBean.setWefDate(sdf.parse("16/06/2025"));
//        riskBean.setWetDate(sdf.parse("15/06/2026"));
//        riskBean.setInsuredCode("50431");
//        riskBean.setSclCode("35739");
//        riskBean.setComputeType("S");
//        riskBean.setWorkingAge(26);
//        riskBean.setCoverCode("35740");
//        riskBean.setBinderDet("35747");
//        riskBean.setPremium(null);
//        riskBean.setSumInsured(new BigDecimal("4322452451.04"));
//        policyDto.setRiskBean(riskBean);

        //PolicyTrans savedPol = null;
        GroupLife groupLife = groupLifeRepo.findOne(groupId);
        Iterable<GroupLifeRisks> insuranceRisks = groupLifeRisksRepo.findAll(
                QGroupLifeRisks.groupLifeRisks.groupLife.groupLifeId.eq(groupId));
        if (groupLife.getTranStatus().equalsIgnoreCase("Y")) {
            throw new BadRequestException("This transaction is already processed");
        }
        //final PolicyTrans policyTrans = new PolicyTrans();
        Date wef = groupLife.getCoverFrom();
        Date wet = groupLife.getCoverTo();
        System.out.println("wet pol" + wet);

        policyDto.setWetDate(wet);
        policyDto.setWefDate(wef);
        policyDto.setPolTerm(groupLife.getPolTerm());

//        policyTrans.setPolCreateddt(new Date());
//        policyTrans.setAuthStatus("LD");
//        policyTrans.setCurrentStatus("LD");
//        policyTrans.setCoverFrom(wef);
//        policyTrans.setCoverTo(wet);
//        policyTrans.setWefDate(wef);
//        policyTrans.setWetDate(wet);
//        policyTrans.setTotalInstalments(1);
//        policyTrans.setPolTerm(1);


        String frequency = groupLife.getFrequency();
        if (frequency != null && !frequency.isEmpty()) {
            if (frequency.equalsIgnoreCase("Daily")) {
                //policyTrans.setFrequency("D");
                policyDto.setFrequency("D");
            } else if (frequency.equalsIgnoreCase("Weekly")) {
                //policyTrans.setFrequency("W");
                policyDto.setFrequency("W");
            } else if (frequency.equalsIgnoreCase("Monthly")) {
                //policyTrans.setFrequency("M");
                policyDto.setFrequency("M");
            } else if (frequency.equalsIgnoreCase("Quarterly")) {
                //policyTrans.setFrequency("Q");
                policyDto.setFrequency("Q");
            } else if (frequency.equalsIgnoreCase("Semi-Annually")) {
                //policyTrans.setFrequency("S");
                policyDto.setFrequency("S");
            } else if (frequency.equalsIgnoreCase("Annually")) {
                //policyTrans.setFrequency("A");
                policyDto.setFrequency("A");
            } else if (frequency.equalsIgnoreCase("Single")) {
                //policyTrans.setFrequency("SG");
                policyDto.setFrequency("SG");
            } else {
                throw new BadRequestException("Unknown frequency '" + frequency + "'");
            }
        } else {
            throw new BadRequestException("Payment frequency is not defined for client ID " + groupLife.getClientId() + " in frequency column in uploaded excel");
        }

        final String pinSearch = groupLife.getClientPin().trim();
        final ClientDef client = clientRepository.findByPinNoIgnoreCase(pinSearch);
        if (client != null) {
            //policyTrans.setClient(client);
            policyDto.setClientId(client.getTenId());
        } else {
            throw new BadRequestException("Client with Kra pin"+pinSearch+" not found");
        }

        //policyTrans.setAgent(creditShield.getInsurerCode());
        policyDto.setAgentId(groupLife.getInsurerCode().getAcctId());

        String searchCode = "GLPU";
        String normalizedPrdName = groupLife.getProductName().trim().toLowerCase().replace(" ", "");
        final ProductsDef product = productsRepo.findByNormalizedName(normalizedPrdName, searchCode);
        if (product != null) {
            //policyTrans.setProduct(product);
            policyDto.setProdId(product.getProCode());

            String polBusinessType = product.getProGroup().getPrgType();
            System.out.println("polBusinessType" + polBusinessType);
            if (!polBusinessType.equalsIgnoreCase("L")) {
                //policyTrans.setBusinessType("N");
                policyDto.setBusinessType("N");
            } else {
                //policyTrans.setBusinessType("L");
                policyDto.setBusinessType("L");
                //policyTrans.setPolTerm(groupLife.getPolTerm());
            }
        } else {
            throw new BadRequestException(groupLife.getProductName() + " product is not found. Please set up the product.");
        }
//        policyTrans.setTransCurrency(groupLife.getCurrencyCode());
//        policyTrans.setBranch(groupLife.getOrgBranch());
//        policyTrans.setUwYear(dateUtils.getUwYear(wef));
//        policyTrans.setCreatedUser(userUtils.getCurrentUser());
//        policyTrans.setPreviousTrans(policyTrans);
        policyDto.setCurrencyId(groupLife.getCurrencyCode().getCurCode());
        policyDto.setBranchId(groupLife.getOrgBranch().getObId());

        List<BindersDef> polBinders = bindersRepo.findBinderByAccIdAndBinName(groupLife.getInsurerCode().getAcctId(), product.getProCode()); // bindersRepo.findBinderByAccId(groupLife.getInsurerCode().getAcctId(), product.getProCode());
        //policyTrans.setBinder(polBinder);
        if (polBinders.isEmpty()) {
            throw new BadRequestException("Default Binder for product " + product.getProDesc() + " and insurer with code " + groupLife.getInsurerCode().getShtDesc()
                    + " not set in the system, please contact system admin.");
        }
        BindersDef polBinder = polBinders.get(0);
        policyDto.setBindCode(polBinder.getBinId());

        PaymentModes paymentModes = paymentModeRepo.findOne(QPaymentModes.paymentModes.pmDesc.eq("CASH"));
        System.out.println("paymentModes id:: " + paymentModes.getPmId());
       // policyTrans.setPaymentMode(paymentModes);
        policyDto.setPaymentId(paymentModes.getPmId());

       // SystemTrans systemTrans = null;
        if (groupLife.getTranStatus().equalsIgnoreCase("N")) {

            String policyNumberFormat = paramService.getParameterString("POLICY_NO_FORMAT");
            String endorsementFormat = paramService.getParameterString("ENDORSE_NO_FORMAT");
            String proposalFormat = paramService.getParameterString("PROPOSAL_NO_FORMAT");
            Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("P");
            if (sequenceRepository.count(seqPredicate) == 0)
                throw new BadRequestException("Sequence for New Business Transactions has not been defined");
            SystemSequence sequence = sequenceRepository.findOne(seqPredicate);
            Long seqNumber = sequence.getNextNumber();
            //final String policyNumber = templateMerger.generateFormat(policyNumberFormat, groupLife.getOrgBranch().getObId(), product.getProCode(), policyTrans.getWefDate(), sequence.getSeqPrefix() + String.format("%05d", seqNumber), null);
           // policyTrans.setPolNo(policyNumber);

            sequence.setLastNumber(seqNumber);
            sequence.setNextNumber(seqNumber + 1);
       //     sequenceRepository.save(sequence);
            Predicate endorsePredicate = QSystemSequence.systemSequence.transType.eq("E");
            if (sequenceRepository.count(endorsePredicate) == 0)
                throw new BadRequestException("Sequence for Endorsement Transactions has not been defined");
            SystemSequence endorseSequence = sequenceRepository.findOne(endorsePredicate);
            Long endosseqNumber = endorseSequence.getNextNumber();
            final String revNumber = endorseSequence.getSeqPrefix() + String.format("%05d", endosseqNumber);
            final String endorseNumber = templateMerger.generateFormat(endorsementFormat, groupLife.getOrgBranch().getObId(), product.getProCode(), groupLife.getCoverFrom(), revNumber, null);
//            policyTrans.setPolRevNo(endorseNumber + "/1");
//            policyTrans.setRevisionFormat(endorseNumber);
//            endorseSequence.setLastNumber(endosseqNumber);
            endorseSequence.setNextNumber(endosseqNumber + 1);
     //       sequenceRepository.save(endorseSequence);

            Predicate propPredicate = QSystemSequence.systemSequence.transType.eq("PR");
            if (sequenceRepository.count(propPredicate) == 0)
                throw new BadRequestException("Sequence for Proposal Transactions has not been defined");
            SystemSequence lifeSequence = sequenceRepository.findOne(propPredicate);
            Long lifeSeqNumber = lifeSequence.getNextNumber();
            final String proposalNo = templateMerger.generateFormat(proposalFormat, groupLife.getOrgBranch().getObId(), product.getProCode(), wef, lifeSequence.getSeqPrefix() + String.format("%05d", lifeSeqNumber), null);
            if (proposalNo == null) {
                throw new BadRequestException("Proposal no has not been generated");
            }
            System.out.println("Proposal No " + proposalNo);
      //      policyTrans.setProposalNo(proposalNo);
        }

//        policyTrans.setRenewable(product.isRenewable());
//        policyTrans.setTransType("BU");
//        policyTrans.setPolRevStatus("LD");
//        policyTrans.setInterfaceType("C");
//        policyTrans.setPolCreateddt(new Date());
//        savedPol = policyTransRepo.save(policyTrans);
//        groupLife.setPolicyTrans(savedPol);
//        groupLife.setTranStatus("Y");
//        groupLife.setProcessedDate(new Date());
//        groupLife.setProcessedBy(userUtils.getCurrentUser());
//        groupLifeRepo.save(groupLife);

//        systemTrans = new SystemTrans();
//        systemTrans.setDoneDate(new Date());
//        systemTrans.setDoneBy(userUtils.getCurrentUser());
//        systemTrans.setPolicy(savedPol);
//        systemTrans.setTransLevel("U");
//        systemTrans.setTransCode("BUD");
//        systemTrans.setTransAuthorised("N");
//        systemTransRepo.save(systemTrans);

        BigDecimal basicPrem = BigDecimal.ZERO;
        for (GroupLifeRisks insuranceRisk : insuranceRisks) {
            basicPrem = insuranceRisk.getRisksPremium();

            RiskTrans riskTrans = new RiskTrans();
            final String pinToSearch = groupLife.getClientPin().trim();
            final ClientDef insured = clientRepository.findByPinNoIgnoreCase(pinToSearch);
            if (insured != null) {
               // riskTrans.setInsured(insured);
            } else {
                throw new BadRequestException("Client with Kra pin "+pinToSearch+" not found");
            }

            List<BindersDef> npolBinders = bindersRepo.findBinderByAccIdAndBinName(groupLife.getInsurerCode().getAcctId(), product.getProCode()); //bindersRepo.findBinderByAccId(groupLife.getInsurerCode().getAcctId(), product.getProCode());
            if (npolBinders.isEmpty()) {
                throw new BadRequestException("Default Binder for product " + product.getProDesc() + " and insurer with code " + groupLife.getInsurerCode().getShtDesc()
                        + " not set in the system, please contact system admin.");
            }
            BindersDef newBinder = npolBinders.get(0);
            String normalizedSubClass = groupLife.getProductName().trim().toLowerCase().replace(" ", "");
            String normalizedCoverType = groupLife.getCoverType().trim().toLowerCase().replace(" ", "");
            final SubClassDef subClassDef = subClassRepo.findBySubClassName(normalizedSubClass);
            final CoverTypesDef coverTypesDef = coverTypesRepo.findCoverTypesByBindId(newBinder.getBinId(), normalizedCoverType);
            System.out.println("Binder id ::" + newBinder.getBinId());
            System.out.println("CoverTypes id:: " + coverTypesDef.getCovId());
            BinderDetails newDetails = binderDetRepo.findOne(QBinderDetails.binderDetails.binder.binId.eq(newBinder.getBinId()).and(QBinderDetails.binderDetails.subCoverTypes.coverTypes.covId.eq(coverTypesDef.getCovId())));
            String businessType = product.getProGroup().getPrgType();
            if (!businessType.equalsIgnoreCase("L")) {
               // policyTrans.setBusinessType("N");
            } else {
              //  policyTrans.setBusinessType("L");
            }
//            riskTrans.setBinder(newBinder);
//            riskTrans.setCovertype(coverTypesDef);
//            riskTrans.setSubclass(subClassDef);
//            riskTrans.setBinderDetails(newDetails);
//            riskTrans.setAutogenCert("N");
//            riskTrans.setPolicy(savedPol);
//            riskTrans.setComputePremium(insuranceRisk.getRisksPremium());
//            riskTrans.setWefDate(wef);
//            riskTrans.setWetDate(wet);
//            riskTrans.setPremium(insuranceRisk.getRisksPremium());
//            riskTrans.setSumInsured(insuranceRisk.getSumInsured());
//            riskTrans.setTransType("BU");

            Integer age = dateUtils.getAge(groupLife.getClientDOB());
            //riskTrans.setWorkingAge(age);

            RiskTransBean riskBean = new RiskTransBean();
            riskBean.setRiskId(null);
            //riskBean.setTransType(null);
            //riskBean.setRiskIdentifier(null);
            riskBean.setBindCode(newBinder.getBinId());
            riskBean.setWefDate(wef);
            riskBean.setWetDate(wet);
            riskBean.setInsuredCode(client.getTenId());
            riskBean.setSclCode(subClassDef.getSubId());
            riskBean.setComputeType("S");
            riskBean.setWorkingAge(age);
            riskBean.setCoverCode(coverTypesDef.getCovId());
            riskBean.setBinderDet(newDetails.getDetId());
            riskBean.setPremium(insuranceRisk.getRisksPremium());
            riskBean.setSumInsured(insuranceRisk.getSumInsured());
            policyDto.setRiskBean(riskBean);
            policyDto.setNegotiatedPremium(insuranceRisk.getRisksPremium()); //to avoid calculator
            List<RiskSectionBean> sections = uploadValidatorsUtils.saveSectionTransaction("SUM ASSURED", newDetails.getDetId(), basicPrem); //buildSectionBeans("29226", new BigDecimal("26"), savedRiskTrans);
            policyDto.setSections(sections);
            break;

//            CommissionRates commissionRate = commRatesRepo.findOne(QCommissionRates.commissionRates.bindersDef.binId.eq(newBinder.getBinId()));
//            AccountDef accountDef = accountRepo.findOne(QAccountDef.accountDef.accountType.accountType.eq(AccountTypeEnum.INS)
//                    .and(QAccountDef.accountDef.acctId.eq(groupLife.getInsurerCode().getAcctId())));
//
//            BigDecimal commissionRates = (commissionRate != null) ? commissionRate.getCommRate() : null;
//            BigDecimal accountCommRates = (accountDef != null) ? accountDef.getAccountType().getCommRate() : null;
//
//            if (commissionRates != null && commissionRates.compareTo(BigDecimal.ZERO) > 0) {
//            //    riskTrans.setCommRate(commissionRates);
//            } else if (accountCommRates != null && accountCommRates.compareTo(BigDecimal.ZERO) > 0) {
//               // riskTrans.setCommRate(accountCommRates);
//            } else {
//                throw new BadRequestException("Commission Rates is not setup");
//            }

          //  riskTransRepo.save(riskTrans);
          //  long riskIdentifier = Long.valueOf(String.valueOf(dateUtils.getUwYear(policyTrans.getWefDate())) + String.valueOf(riskTrans.getRiskId()));
            //riskTrans.setRiskIdentifier(riskIdentifier);
        }



       // uploadValidatorsUtils.savePolicyInstallments(savedPol, policyTrans.getBasicPrem(),wef, frequency);
        PolicyTrans createdPol = uploadValidatorsUtils.saveLifePolicyUpload(policyDto,userId);
        //
        uploadValidatorsUtils.updateBasicNet(basicPrem, BigDecimal.ZERO, createdPol);

        groupLife.setPolicyTrans(createdPol);
        groupLife.setPolicyAuthorized("N");
        groupLife.setTranStatus("Y");
        groupLife.setProcessedDate(new Date());
        groupLife.setProcessedBy(user);
        groupLifeRepo.save(groupLife);

        return createdPol;
    }

    @Override
    public String deleteBulkGroupLifePol(List<Long> groupIds) throws BadRequestException {
        if (groupIds == null || groupIds.size() == 0) {
            throw new BadRequestException("Select At least One Transaction To Process");
        }

        for (Long groupId : groupIds) {
            GroupLife groupLife = groupLifeRepo.findOne(groupId);
            //check if not processed
            if (groupLife.getTranStatus().equalsIgnoreCase("N")) {

                Iterable<GroupLifeRisks> insuranceRisks = groupLifeRisksRepo.findAll(
                        QGroupLifeRisks.groupLifeRisks.groupLife.groupLifeId.eq(groupId));

                groupLifeRisksRepo.delete(insuranceRisks);
            }

            groupLifeRepo.delete(groupLife);
        }
        return  "Group life policy deleted successfully.";
    }

    @Override
    public String bulkDelProcessedPolicies(List<Long> groupIds) throws BadRequestException {
        if (groupIds == null || groupIds.size() == 0) {
            throw new BadRequestException("Select At least One Transaction To Process");
        }

        for (Long groupId : groupIds) {
            GroupLife groupLife = groupLifeRepo.findBulkStockByPolicyId(groupId);

            if (groupLife == null) {
                System.out.println("No BulkPolicyCreation found for ID: " + groupIds);
                continue;
            }

            //check if processed
            if (!"Y".equalsIgnoreCase(groupLife.getTranStatus())) {
                System.out.println("Transaction not marked as processed for ID: " + groupIds);
                continue;
            }

            groupLifeRisksRepo.deleteByPackage(groupLife);
            groupLifeRepo.delete(groupLife);
            uploadValidatorsUtils.deleteprocessedPol(groupId);
        }
        return  "Group life policy deleted successfully.";
    }

    @Override
    public List<Long> processBulkGroupLifePol(BatchCreditBatch batchCreditBatch, boolean isApproved) throws BadRequestException {
        if (batchCreditBatch.getBatchRecords().size() <= 0) {
            throw new BadRequestException("Select At least One Transaction To Process");
        }

        Job job = JobBuilder.aNewJob()
                .reader(new IterableRecordReader(batchCreditBatch.getBatchRecords()))
                .named("group_life_processing" + new SimpleDateFormat("ddMMyyyhhmmss").format(new Date()))
                .processor((RecordProcessor<Record, Record>) renForm -> {
                    Long policyId = ((BatchRecord) renForm.getPayload()).getPayload().getBatchId();
                    Long userId = ((BatchRecord) renForm.getPayload()).getPayload().getUserId();
                    processSingleGroupLifePol(policyId, userId,true);
                    return renForm;
                })
                .pipelineListener(new RenewalJobListener())
                .build();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Future<JobReport> report = executorService.submit(job);

        return  new ArrayList<>();


//        if (groupIds.size() <= 0) {
//            throw new BadRequestException("Select At least One Transaction To Process");
//        }
//        PolicyTrans savedPol = null;
//        List<Long> processedPolicyIds = new ArrayList<>();
//        for (Long groupId : groupIds) {
//            savedPol = processSingleGroupLifePol(groupId, isApproved);
//            processedPolicyIds.add(savedPol.getPolicyId());
//        }
//        return processedPolicyIds;
    }

    @Override
    public String approveSingleGroupLifePol(Long policyId) throws BadRequestException {
        authService.authorizeBulkUploadPolicies(policyId);
        GroupLife groupLife = groupLifeRepo.findBulkStockByPolicyId(policyId);
        groupLife.setPolicyAuthorized("Y");
        groupLifeRepo.save(groupLife);
        return "Transaction Authorized Successfully";
    }

    @Override
    public String approveBulkGroupLifePol(List<Long> policyIds) throws BadRequestException {

        if (policyIds.size() <= 0) {
            throw new BadRequestException("Select At least One Transaction To Process");
        }
        for (Long policyId : policyIds) {
            authService.authorizeBulkUploadPolicies(policyId);
            GroupLife groupLife = groupLifeRepo.findBulkStockByPolicyId(policyId);
            groupLife.setPolicyAuthorized("Y");
            groupLifeRepo.save(groupLife);
        }

        return "Transactions Authorized Successfully";
    }
}
