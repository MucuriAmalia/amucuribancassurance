package com.brokersystems.brokerapp.bulktransactions.service.impl;

import com.brokersystems.brokerapp.bulktransactions.ErrorsCache.ErrorWorkbookCache;
import com.brokersystems.brokerapp.bulktransactions.dtos.EmbedPackageInsuranceDTO;
import com.brokersystems.brokerapp.bulktransactions.repositories.*;
import com.brokersystems.brokerapp.bulktransactions.utils.BulkBatchSaveService;
import com.brokersystems.brokerapp.bulktransactions.utils.ExcelReaderUtil;
import com.brokersystems.brokerapp.enums.AccountTypeEnum;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.*;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.*;
import com.brokersystems.brokerapp.bulktransactions.models.*;
import com.brokersystems.brokerapp.bulktransactions.service.EmbedPackageInsuranceService;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.brokersystems.brokerapp.trans.model.SystemTrans;
import com.brokersystems.brokerapp.trans.repository.SystemTransRepo;
import com.brokersystems.brokerapp.trans.service.PolicyAuthorization;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.uw.repository.PolActiveRisksRepo;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import com.brokersystems.brokerapp.uw.repository.RiskTransRepo;
import com.brokersystems.brokerapp.uw.repository.SectionTransRepo;
import com.brokersystems.brokerapp.uw.validators.RenewalJobListener;
import com.mysema.query.types.Predicate;
import org.apache.commons.lang.time.DateUtils;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
public class EmbedPackageInsuranceServiceImpl implements EmbedPackageInsuranceService {

    @Autowired
    private SequenceRepository sequenceRepository;
    @Autowired
    private UserUtils userUtils;
    @Autowired
    private ValidatorUtils validator;
    @Autowired
    private EmbedPackageInsuranceRepo embedPackageInsuranceRepo;
    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private ClientTypeRepo clientTypeRepo;
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private OrgBranchRepository orgBranchRepository;
    @Autowired
    private EmbedPackageInsuranceRiskRepo embedPackageInsuranceRiskRepo;
    @Autowired
    private UserBranchesRepository userBranchesRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private PolicyTransRepo policyTransRepo;
    @Autowired
    private CommRatesRepo commRatesRepo;
    @Autowired
    private RiskTransRepo riskTransRepo;
    @Autowired
    private PolActiveRisksRepo activeRisksRepo;
    @Autowired
    private PolicyAuthorization authService;
    @Autowired
    private BindersRepo bindersRepo;
    @Autowired
    private PaymentModeRepo paymentModeRepo;
    @Autowired
    private SubClassRepo subClassRepo;
    @Autowired
    private CoverTypesRepo coverTypesRepo;
    @Autowired
    private BinderDetRepo binderDetRepo;
    @Autowired
    private ParamService paramService;
    @Autowired
    private TemplateMerger templateMerger;
    @Autowired
    private ProductsRepo productsRepo;
    @Autowired
    private SectionTransRepo sectionRepo;
    @Autowired
    private PremRatesRepo premRatesRepo;
    @Autowired
    private DateUtilities dateUtils;
    @Autowired
    private SectionRepo setupSectionRepo;
    @Autowired
    private SystemTransRepo systemTransRepo;
    @Autowired
    private SequenceRepository sequenceRepo;
    @Autowired
    private UploadValidatorsUtils uploadValidatorsService;
    @Autowired
    private BulkBatchSaveService bulkBatchSaveService;
    @Autowired
    private ErrorWorkbookCache errorWorkbookCache;
    @Autowired
    private UserRepository userRepo;


    @Override
    @Async
    public void uploadEmbedFirstAss(File file, Long userId) throws BadRequestException {
        User user = userRepo.findOne(userId);


        Map<String, Object> response = new HashMap<>();
        List<String> invalidRecords = new ArrayList<>();
        List<EmbedPackageInsurance> firstAssurances = new ArrayList<>();
        List<EmbedPackageInsuranceRisks> firstAssRisks = new ArrayList<>();

        int batchSize = 500;
        int totalSuccessCount = 0;
        int totalErrorCount = 0;
        String batchId = UUID.randomUUID().toString();

        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
            Workbook workbook = WorkbookFactory.create(inputStream);



            Sheet sheetOne = workbook.getSheetAt(0);
            Map<String, EmbedPackageInsurance> firstAssuranceMap = new HashMap<>();

            int startRowIndex = findDataStartRow(sheetOne);
            if (startRowIndex == -1) {
                throw new BadRequestException("Sheet One contains no data.");
            }
            int errorColumnIndex = sheetOne.getRow(startRowIndex).getLastCellNum();

            String bulkCode = null;
            String transType = "EPIU";
            Predicate pedSystem = QSystemSequence.systemSequence.transType.eq("EPIU");
            if (sequenceRepository.count(pedSystem) == 0)
                throw new BadRequestException("Sequence for Embedded Packaged Insurance Upload has not been defined");


            for (int rowNum = startRowIndex; rowNum <= sheetOne.getLastRowNum(); rowNum++) {
                SystemSequence sequenceSystem = sequenceRepository.findOne(pedSystem);
                Long sequenceNumber = sequenceSystem.getNextNumber();
                bulkCode = transType + String.format("%05d", sequenceNumber);
                sequenceSystem.setLastNumber(sequenceNumber);
                sequenceSystem.setNextNumber(sequenceNumber + 1);
                sequenceRepository.save(sequenceSystem);

                //System.out.println("Total Rows in Sheet: " + sheetOne.getLastRowNum());
                Row row = sheetOne.getRow(rowNum);
                //System.out.println("Processing row: " + rowNum);
                if (isBlankRow(row)) {
                    //System.out.println("Skipping blank row: " + rowNum);
                    continue;
                }

                try {
                    EmbedPackageInsurance embedPackageInsurance = new EmbedPackageInsurance();
                    String serialNo = ExcelReaderUtil.getCellValue(row, 0); //getCellStringValue(row.getCell(0));
                    System.out.println("serialNo" + serialNo);
                    embedPackageInsurance.setSerialNo(serialNo);

                    String clntIdNo = ExcelReaderUtil.getCellValue(row, 1); //getCellStringValue(row.getCell(4));
                    String cif = ExcelReaderUtil.getCellValue(row, 2); //getCellStringValue(row.getCell(4));

                    embedPackageInsurance.setClientCIF(cif); //getCellStringValue(row.getCell(10)));
                    embedPackageInsurance.setClientId(clntIdNo); //getCellStringValue(row.getCell(4)));
                    final ClientDef client = clientRepository.findByCifandIdNoIgnoreCase(cif, clntIdNo);
                    if (client != null) {
                        embedPackageInsurance.setClientName(client.getFname());
                        embedPackageInsurance.setClientOtherNames(client.getOtherNames());
                        embedPackageInsurance.setClientDOB(client.getDob());
                        embedPackageInsurance.setClientPin(client.getPinNo());
                        embedPackageInsurance.setClientEmail(client.getEmailAddress());
                        embedPackageInsurance.setClientPhone(client.getPhoneNo());
                        embedPackageInsurance.setTenantType(client.getTenantType());
                        embedPackageInsurance.setCountryCode(client.getCountry());

                    } else {
                        throw new BadRequestException("Client with ID No "+ clntIdNo + " not found.");
                    }

                    String branchCode = ExcelReaderUtil.getCellValue(row, 3); //getCellStringValue(row.getCell(11));
                    OrgBranch orgBranch = orgBranchRepository.findOne(QOrgBranch.orgBranch.obShtDesc.eq(branchCode.trim()));
                    if (orgBranch != null) {
                        embedPackageInsurance.setOrgBranch(orgBranch);
                    } else {
                        throw new BadRequestException("Branch code " + branchCode + " in sheet two is not setup in the system, please contact system admin.");
                    }

                    embedPackageInsurance.setTransDate(ExcelReaderUtil.parseFlexibleDate(ExcelReaderUtil.getCellValue(row, 4))); //getCellDateValue(row.getCell(12)));

                    String insurerCode = ExcelReaderUtil.getCellValue(row, 5); //getCellStringValue(row.getCell(13));
                    AccountDef underWriter = accountRepo.findOne(QAccountDef.accountDef.shtDesc.eq(insurerCode.trim()));
                    if (underWriter != null) {
                        embedPackageInsurance.setInsurerCode(underWriter);
                    } else {
                        throw new BadRequestException("Insurer with code " + insurerCode + " is not setup in the system, please contact system admin");
                    }
                    embedPackageInsurance.setProductGroup(ExcelReaderUtil.getCellValue(row, 6)); //getCellStringValue(row.getCell(14)));
                    String productName = (ExcelReaderUtil.getCellValue(row, 7)); //getCellStringValue(row.getCell(15)));

                    String searchCode = "EPPU";
                    String normalizedProductName = productName.trim().toLowerCase().replace(" ", "");
                    ProductsDef product = productsRepo.findByNormalizedName(normalizedProductName, searchCode);
                    if (product == null) {
                        throw new BadRequestException("Product " + productName + " not set in the system, please contact system admin.");
                    }
                    BindersDef binder = bindersRepo.findBinderByAccId(embedPackageInsurance.getInsurerCode().getAcctId(), product.getProCode());
                    if (binder == null) {
                        throw new BadRequestException("Binder for product " + productName + " and insurer with code " + embedPackageInsurance.getInsurerCode().getShtDesc()
                                + " not set in the system, please contact system admin.");
                    }
                    String coverType = (ExcelReaderUtil.getCellValue(row, 8)); //getCellStringValue(row.getCell(16)));
                    String normalizedCoverType = coverType.trim().toLowerCase().replaceAll("\\s+", " ");
                    SubClassDef subClassDef = subClassRepo.findBySubClassName(normalizedProductName);
                    if (subClassDef != null){
                        embedPackageInsurance.setProductName(subClassDef.getSubDesc());
                    } else {
                        throw new BadRequestException("Product " + productName + " not set in the system, please contact system admin.");
                    }
                    CoverTypesDef coverTypesDef = coverTypesRepo.findCoverTypesByBindId(binder.getBinId(), normalizedCoverType);
                    if (coverTypesDef != null) {
                        embedPackageInsurance.setCoverType(coverTypesDef.getCovName());
                    }else {
                        throw new BadRequestException("Cover type " + coverType + " not set for insurer with code " + embedPackageInsurance.getInsurerCode().getShtDesc() +" please contact system admin.");
                    }
                    embedPackageInsurance.setFrequency(validateFrequency(ExcelReaderUtil.getCellValue(row, 9), row.getRowNum())); //getCellStringValue(row.getCell(17)), row.getRowNum()));
                    String currency = ExcelReaderUtil.getCellValue(row, 10); //getCellStringValue(row.getCell(18));
                    if (currency != null && !currency.trim().isEmpty()) {
                        currency = currency.trim();
                        Currencies currencies = currencyRepository.findOne(QCurrencies.currencies.curIsoCode.eq(currency));
                        if (currencies == null) {
                            throw new BadRequestException("Currency code " + currency + " is not setup in the system, please contact system admin");
                        }
                        embedPackageInsurance.setCurrencyCode(currencies);
                    } else {
                        throw new BadRequestException("Currency is required in row " + row.getRowNum() + " in Currency ISO code column");
                    }

                    embedPackageInsurance.setCoverFrom(ExcelReaderUtil.parseFlexibleDate(ExcelReaderUtil.getCellValue(row, 11))); //getCellDateValue(row.getCell(19)));
                    embedPackageInsurance.setCoverTo(ExcelReaderUtil.parseFlexibleDate(ExcelReaderUtil.getCellValue(row, 12))); //getCellDateValue(row.getCell(20)));

                    String paymentType = ExcelReaderUtil.getCellValue(row, 13);
                    if(!paymentType.isEmpty()){
                        if(paymentType.equalsIgnoreCase("Accrual")){
                            Date instDate = ExcelReaderUtil.parseFlexibleDate(ExcelReaderUtil.getCellValue(row, 14)); //getCellDateValue(row.getCell(20)));
                            if(instDate == null){
                                throw  new BadRequestException("Accrual installment date is required.");
                            }
                            embedPackageInsurance.setAccrualInstDate(instDate);

                            String ApaymentType = ExcelReaderUtil.getCellValue(row, 15); //getCellDateValue(row.getCell(20)));
                            if (ApaymentType == null ||
                                    (!ApaymentType.equalsIgnoreCase("Dispensation") && !ApaymentType.equalsIgnoreCase("IPF"))) {
                                throw new BadRequestException("Accrual payment is required. Either IPF or Dispensation");
                            }

                            embedPackageInsurance.setAccrualPaymentType(ApaymentType);
                            embedPackageInsurance.setAccrualPaymentType("A");
                        }else if(paymentType.equalsIgnoreCase("Cash")){
                            embedPackageInsurance.setAccrualPaymentType("C"); //getCellDateValue(row.getCell(20)));
                        } else {
                            throw new BadRequestException("Please fill with Cash or Accrual");
                        }
                    }else{
                        throw new BadRequestException("The payment type field is required.");
                    }
                    embedPackageInsurance.setUploadDate(new Date());
                    embedPackageInsurance.setUploadedBy(user);
                    embedPackageInsurance.setTranStatus("N");
                    embedPackageInsurance.setTransType(transType);
                    embedPackageInsurance.setRefCode(bulkCode);

                    firstAssurances.add(embedPackageInsurance);
                    firstAssuranceMap.put(serialNo, embedPackageInsurance);

                    if (firstAssurances.size() >= batchSize) {
                        bulkBatchSaveService.saveEmbedPackageInsuranceBatch(firstAssurances);
                        firstAssurances.clear();
                    }

                } catch (Exception e) {
                    //throw new BadRequestException("Sheet One " + e.getMessage());
//                    Cell errorCell = row.createCell(errorColumnIndex);
//                    errorCell.setCellValue("Error: " + e.getMessage());
//                    invalidRecords.add("Sheet One, Row " + (rowNum + 1) + ": " + e.getMessage());
                }
            }

            int successfulPol = firstAssurances.size();
            int invalidPol = invalidRecords.size();
            //System.out.println("successfulPol>:: " + successfulPol);
            //System.out.println("invalidPol>:: " + invalidRecords.toString());
            //save remaining items less than batch size
            if (!firstAssurances.isEmpty()) {
                bulkBatchSaveService.saveEmbedPackageInsuranceBatch(firstAssurances);
                firstAssurances.clear();
            }

            Sheet sheetTwo = workbook.getSheetAt(1);
            int startRowIndexTwo = findDataStartRow(sheetTwo);
            if (startRowIndexTwo == -1) {
                throw new BadRequestException("Sheet Two contains no data.");
            }

            int errorColumnIndex2 =  sheetTwo.getRow(startRowIndexTwo).getLastCellNum();

            for (int rowNum = startRowIndexTwo; rowNum <= sheetTwo.getLastRowNum(); rowNum++) {
                Row row = sheetTwo.getRow(rowNum);
                if (isBlankRow(row)) continue;

                try {

                    List<EmbedPackageInsuranceRisks> riskList = Optional.ofNullable(
                            (List<EmbedPackageInsuranceRisks>) embedPackageInsuranceRiskRepo.findAll(
                                    QEmbedPackageInsuranceRisks.embedPackageInsuranceRisks.riskStatus.eq("Y")
                                            .and(QEmbedPackageInsuranceRisks.embedPackageInsuranceRisks.riskCode.eq(bulkCode))
                            )
                    ).orElse(new ArrayList<>());

                    //System.out.println("riskList:: " + riskList);

                    List<EmbedPackageInsurance> bulkPolicyList = (List<EmbedPackageInsurance>) embedPackageInsuranceRepo.findAll(
                            QEmbedPackageInsurance.embedPackageInsurance.tranStatus.eq("N")
                                    .and(QEmbedPackageInsurance.embedPackageInsurance.refCode.eq(bulkCode))
                    );
                    //System.out.println("bulkPolicyList:: " + bulkPolicyList);

                    // Convert bulkPolicyList into a Set for faster lookups
                    Set<String> refCodeSet = bulkPolicyList.stream()
                            .map(EmbedPackageInsurance::getRefCode)
                            .collect(Collectors.toSet());

                    for (EmbedPackageInsuranceRisks checkRisk : riskList) {
                        if (refCodeSet.contains(checkRisk.getRiskCode())) {
                            // Mismatch found, immediately throw an error
                            throw new BadRequestException(
                                    "Serial No " + checkRisk.getSerialNo() + " in sheet 2 is pending processing, process before re-uploading this file"
                            );
                        }
                    }

                    // Proceed to add new risk if no mismatch was found
                    String serialNo = ExcelReaderUtil.getCellValue(row, 0); //getCellStringValue(row.getCell(0));
                    //System.out.println("Serial No sheet two: " + serialNo);
                    EmbedPackageInsurance linkedPolicy = firstAssuranceMap.get(serialNo);

                    if (linkedPolicy == null) {
                        throw new BadRequestException(
                                "Row " + (rowNum + 1) + ": Serial number '" + serialNo + "' not found in Sheet One."
                        );
                    }

                    EmbedPackageInsuranceRisks embedRisk = new EmbedPackageInsuranceRisks();

                    embedRisk.setSerialNo(serialNo);
                    String clntIdNo = ExcelReaderUtil.getCellValue(row, 1); //getCellStringValue(row.getCell(4));
                    String cif = ExcelReaderUtil.getCellValue(row, 2); //getCellStringValue(row.getCell(4));

                    embedRisk.setInsuredCIF(cif); //getCellStringValue(row.getCell(10)));
                    embedRisk.setInsuredID(clntIdNo); //getCellStringValue(row.getCell(4)));
                    final ClientDef client = clientRepository.findByCifandIdNoIgnoreCase(cif, clntIdNo);
                    if (client != null) {
                        embedRisk.setInsuredFname(client.getFname());
                        embedRisk.setInsuredOthernames(client.getOtherNames());
                        embedRisk.setInsuredDOB(client.getDob());
                        embedRisk.setInsuredPin(client.getPinNo());
                        embedRisk.setInsuredEmail(client.getEmailAddress());
                        embedRisk.setInsuredPhone(client.getPhoneNo());
                        embedRisk.setTenantType(client.getTenantType());

                    } else {
                        throw new BadRequestException("Client with ID No "+ clntIdNo + " not found.");
                    }


                    embedRisk.setInsuredAcc(ExcelReaderUtil.getCellValue(row, 3)); //getCellStringValue(row.getCell(10)));
                    embedRisk.setAccOpenDate(ExcelReaderUtil.parseFlexibleDate(ExcelReaderUtil.getCellValue(row, 4))); //getCellDateValue(row.getCell(11)));
                    String branch = ExcelReaderUtil.getCellValue(row, 5); //getCellStringValue(row.getCell(12));
                    OrgBranch orgBranch = orgBranchRepository.findOne(QOrgBranch.orgBranch.obShtDesc.eq(branch.trim()));
                    if (orgBranch != null) {
                        embedRisk.setInsuredBranch(orgBranch);
                    } else {
                        throw new BadRequestException("Branch code " + branch + " in sheet two is not setup in the system, please contact system admin.");
                    }

                    embedRisk.setInsuredCategory(ExcelReaderUtil.getCellValue(row, 6)); //getCellStringValue(row.getCell(13)));
                    embedRisk.setRiskMonth(ExcelReaderUtil.getCellValue(row, 7)); //getCellStringValue(row.getCell(14)));
                    BigDecimal premium = ExcelReaderUtil.parseBigDecimalSafe(ExcelReaderUtil.getCellValue(row, 8)); //getCellBigDecimalValue(row.getCell(15)));
                    premium = premium.abs();
                    embedRisk.setRiskPremium(premium);
                    embedRisk.setEmbedPackageFirstAssurance(linkedPolicy);
                    embedRisk.setUploadedBy(user);
                    embedRisk.setUploadDate(new Date());
                    embedRisk.setRiskStatus("Y");
                    embedRisk.setRiskCode(bulkCode);

                    firstAssRisks.add(embedRisk);

                    if (firstAssRisks.size() >= batchSize) {
                        bulkBatchSaveService.saveEmbedPackageInsuranceRisksBatch(firstAssRisks);
                        firstAssRisks.clear();
                    }

                } catch (Exception e) {
                    //throw new BadRequestException("Sheet Two " + e.getMessage());
//                    Cell errorCell = row.createCell(errorColumnIndex2);
//                    errorCell.setCellValue("Error: " + e.getMessage());
//                    invalidRecords.add("Sheet Two, Row " + (rowNum + 1) + ": " + e.getMessage());
                }
            }

            int successfulRisks = firstAssRisks.size();
            int invalidRisks = invalidRecords.size();

            //save the remaining risks less than bach size
            if (!firstAssRisks.isEmpty()) {
                bulkBatchSaveService.saveEmbedPackageInsuranceRisksBatch(firstAssRisks);
                firstAssRisks.clear();
            }

            // Save error workbook
            //File tempFile = File.createTempFile("errorWorkbook_", ".xlsx");
//            File tempFile = File.createTempFile("errorWorkbook_", ".xlsx", new File(System.getProperty("java.io.tmpdir")));
//            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
//                workbook.write(fos);
//            }
//
//            errorWorkbookCache.saveExcelWorkbook(batchId, tempFile);

            response.put("errorFileId", batchId);
            response.put("successfulPolicies", firstAssurances.size());
            response.put("successfulRisks", firstAssRisks.size());
            response.put("failedRecords", invalidRecords.size());
            response.put("invalidRecords", invalidRecords);

            //return response;

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
    public DataTablesResult<EmbedPackageInsuranceDTO> findUnprocessedPackagedInsur(DataTablesRequest request) {
        Long currentUserId = userUtils.getCurrentUser().getId();

        List<Object[]> unprocessedPackagedInsur = embedPackageInsuranceRepo.findUnprocessedPackagedInsur((request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%", request.getPageNumber(), request.getPageSize(), currentUserId);
        final List<EmbedPackageInsuranceDTO> packageInsuranceDTOS = new ArrayList<>();
        long rowCount = 0L;
        if (!unprocessedPackagedInsur.isEmpty())
            rowCount = ((BigInteger) unprocessedPackagedInsur.get(0)[9]).intValue();
        for (Object[] embedPackage : unprocessedPackagedInsur) {
            EmbedPackageInsuranceDTO packageInsuranceDTO = new EmbedPackageInsuranceDTO();
            packageInsuranceDTO.setEmbedPackageId(((BigInteger) embedPackage[0]).longValue());
            packageInsuranceDTO.setCoverType((String) embedPackage[1]);
            packageInsuranceDTO.setProductName((String) embedPackage[2]);
            packageInsuranceDTO.setInsurerName((String) embedPackage[3]);
            packageInsuranceDTO.setClientFName((String) embedPackage[4]);
            packageInsuranceDTO.setClientOtherNames((String) embedPackage[5]);
            packageInsuranceDTO.setCoverFrom((Date) embedPackage[6]);
            packageInsuranceDTO.setCoverTo((Date) embedPackage[7]);
            packageInsuranceDTO.setUploadedDate((Date) embedPackage[8]);

            packageInsuranceDTOS.add(packageInsuranceDTO);
        }
        Page<EmbedPackageInsuranceDTO> page = new PageImpl<>(packageInsuranceDTOS, request, rowCount);

        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<EmbedPackageInsuranceDTO> viewUnprocessedEmbedPackageInsur(DataTablesRequest request) {
        Long currentUserId = userUtils.getCurrentUser().getId();

        List<Object[]> viewUnprocessedEmbed = embedPackageInsuranceRepo.viewUnprocessedEmbedPackageInsur((request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%", request.getPageNumber(), request.getPageSize(), currentUserId);
        final List<EmbedPackageInsuranceDTO> insuranceDTOS = new ArrayList<>();
        long rowCount = 0L;
        if (!viewUnprocessedEmbed.isEmpty()) rowCount = ((BigInteger) viewUnprocessedEmbed.get(0)[10]).intValue();
        for (Object[] viewEmbedPackage : viewUnprocessedEmbed) {
            EmbedPackageInsuranceDTO embedInsuranceDTO = new EmbedPackageInsuranceDTO();
            embedInsuranceDTO.setPolId(((BigInteger) viewEmbedPackage[0]).longValue());
            embedInsuranceDTO.setCoverType((String) viewEmbedPackage[1]);
            embedInsuranceDTO.setPolNo((String) viewEmbedPackage[2]);
            embedInsuranceDTO.setPolBasicPrem((BigDecimal) viewEmbedPackage[3]);
            embedInsuranceDTO.setInsuredFName((String) viewEmbedPackage[4]);
            embedInsuranceDTO.setInsuredOtherNames((String) viewEmbedPackage[5]);
            embedInsuranceDTO.setPolWef((Date) viewEmbedPackage[6]);
            embedInsuranceDTO.setPolWet((Date) viewEmbedPackage[7]);
            embedInsuranceDTO.setProcessedDate((Date) viewEmbedPackage[8]);
            embedInsuranceDTO.setPolicyStatus((String) viewEmbedPackage[9]);

            insuranceDTOS.add(embedInsuranceDTO);
        }
        Page<EmbedPackageInsuranceDTO> page = new PageImpl<>(insuranceDTOS, request, rowCount);

        return new DataTablesResult<>(request, page);
    }

    @Override
    public PolicyTrans processSinglePackagedInsurPol(Long embedId,Long userId, boolean isApproved) throws BadRequestException {
        User user = (userUtils.getCurrentUser()!=null)?userUtils.getCurrentUser():userRepo.findOne(userId);
        PolicyTrans savedPol = null;
        EmbedPackageInsurance embedPackageInsurance = embedPackageInsuranceRepo.findOne(QEmbedPackageInsurance.embedPackageInsurance.embedPackageId.eq(embedId));
        Iterable<EmbedPackageInsuranceRisks> insuranceRisks = embedPackageInsuranceRiskRepo.findAll(
                QEmbedPackageInsuranceRisks.embedPackageInsuranceRisks.embedPackageInsurance.embedPackageId.eq(embedId)
        );
        System.out.println("insuranceRisks:: " + insuranceRisks.toString());
        if (embedPackageInsurance.getTranStatus().equalsIgnoreCase("Y")) {
            throw new BadRequestException("This transaction is already processed");
        }
        final PolicyTrans policyTrans = new PolicyTrans();
        Date wef = embedPackageInsurance.getCoverFrom();
        Date wet = embedPackageInsurance.getCoverTo();
        System.out.println("wet pol" + wet);
        policyTrans.setPolCreateddt(new Date());
        policyTrans.setAuthStatus("LD");
        policyTrans.setCurrentStatus("LD");
        policyTrans.setCoverFrom(wef);
        policyTrans.setCoverTo(wet);
        policyTrans.setWefDate(wef);
        policyTrans.setWetDate(wet);
        policyTrans.setTotalInstalments(1);
        policyTrans.setPolTerm(1);
        String frequency = embedPackageInsurance.getFrequency();
        if (frequency != null && !frequency.isEmpty()) {
            if (frequency.equalsIgnoreCase("Daily")) {
                policyTrans.setFrequency("D");
            } else if (frequency.equalsIgnoreCase("Weekly")) {
                policyTrans.setFrequency("W");
            } else if (frequency.equalsIgnoreCase("Monthly")) {
                policyTrans.setFrequency("M");
            } else if (frequency.equalsIgnoreCase("Quarterly")) {
                policyTrans.setFrequency("Q");
            } else if (frequency.equalsIgnoreCase("Semi-Annually")) {
                policyTrans.setFrequency("S");
            } else if (frequency.equalsIgnoreCase("Annually")) {
                policyTrans.setFrequency("A");
            } else if (frequency.equalsIgnoreCase("Single")) {
                policyTrans.setFrequency("SG");
            } else {
                throw new BadRequestException("Unknown frequency '" + frequency + "'");
            }
        } else {
            throw new BadRequestException("Payment frequency is not defined for client ID " + embedPackageInsurance.getClientId() + " in frequency column in uploaded excel");
        }
        final String pinSearch = embedPackageInsurance.getClientPin().trim();
        final ClientDef client = clientRepository.findByPinNoIgnoreCase(pinSearch);
        if (client != null) {
            policyTrans.setClient(client);
        } else {
          throw new BadRequestException("Client with Kra Pin"+pinSearch+ " not found.");
        }

        policyTrans.setAgent(embedPackageInsurance.getInsurerCode());

        String searchCode = "EPPU";
        String normalizedPrdName = embedPackageInsurance.getProductName().trim().toLowerCase().replace(" ", "");
        final ProductsDef product = productsRepo.findByNormalizedName(normalizedPrdName, searchCode);
        if (product != null) {
            policyTrans.setProduct(product);
            String polBusinessType = product.getProGroup().getPrgType();
            if (!polBusinessType.equalsIgnoreCase("L")) {
                policyTrans.setBusinessType("N");
            } else {
                policyTrans.setBusinessType("L");
            }
        } else {
            throw new BadRequestException(embedPackageInsurance.getProductName() + " product is not found. Please set up the product.");
        }

        policyTrans.setTransCurrency(embedPackageInsurance.getCurrencyCode());
        policyTrans.setBranch(embedPackageInsurance.getOrgBranch());
        policyTrans.setUwYear(dateUtils.getUwYear(wef));
        policyTrans.setCreatedUser(user);//userUtils.getCurrentUser());
        policyTrans.setPreviousTrans(policyTrans);
        BindersDef polBinder = bindersRepo.findBinderByAccId(embedPackageInsurance.getInsurerCode().getAcctId(), product.getProCode());

        if((polBinder.getAdminFeeActiveStatus() != null) && (polBinder.getAdminFeeActiveStatus().equalsIgnoreCase("Y"))) {
            policyTrans.setAdminFeeApplicable("Y");
        } else {
            policyTrans.setAdminFeeApplicable("N");
        }

        policyTrans.setBinder(polBinder);
        PaymentModes paymentModes = paymentModeRepo.findOne(QPaymentModes.paymentModes.pmDesc.eq("CASH"));
        policyTrans.setPaymentMode(paymentModes);

        SystemTrans systemTrans = null;
        if (embedPackageInsurance.getTranStatus().equalsIgnoreCase("N")) {

            String policyNumberFormat = paramService.getParameterString("POLICY_NO_FORMAT");
            String endorsementFormat = paramService.getParameterString("ENDORSE_NO_FORMAT");
            Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("P");
            if (sequenceRepository.count(seqPredicate) == 0)
                throw new BadRequestException("Sequence for New Business Transactions has not been defined");
            SystemSequence sequence = sequenceRepository.findOne(seqPredicate);
            Long seqNumber = sequence.getNextNumber();
            final String policyNumber = templateMerger.generateFormat(policyNumberFormat, embedPackageInsurance.getOrgBranch().getObId(), product.getProCode(), policyTrans.getWefDate(), sequence.getSeqPrefix() + String.format("%05d", seqNumber), null);
            policyTrans.setPolNo(policyNumber);

            sequence.setLastNumber(seqNumber);
            sequence.setNextNumber(seqNumber + 1);
            sequenceRepository.save(sequence);
            Predicate endorsePredicate = QSystemSequence.systemSequence.transType.eq("E");
            if (sequenceRepository.count(endorsePredicate) == 0)
                throw new BadRequestException("Sequence for Endorsement Transactions has not been defined");
            SystemSequence endorseSequence = sequenceRepository.findOne(endorsePredicate);
            Long endosseqNumber = endorseSequence.getNextNumber();
            final String revNumber = endorseSequence.getSeqPrefix() + String.format("%05d", endosseqNumber);
            final String endorseNumber = templateMerger.generateFormat(endorsementFormat, embedPackageInsurance.getOrgBranch().getObId(), product.getProCode(), embedPackageInsurance.getCoverFrom(), revNumber, null);
            policyTrans.setPolRevNo(endorseNumber + "/1");
            policyTrans.setRevisionFormat(endorseNumber);
            endorseSequence.setLastNumber(endosseqNumber);
            endorseSequence.setNextNumber(endosseqNumber + 1);
            sequenceRepository.save(endorseSequence);

        }

        if (product.isRenewable())
            policyTrans.setRenewable(product.isRenewable());
        else
            policyTrans.setRenewable(false);
        policyTrans.setUwYear(dateUtils.getUwYear(policyTrans.getWefDate()));
        if (product.isRenewable()) {
            policyTrans.setRenewalDate(DateUtils.addDays(policyTrans.getWetDate(), 1));
            policyTrans.setNotificationSent(false);
        } else
            policyTrans.setRenewalDate(null);
        policyTrans.setTransType("BU");
        policyTrans.setPolRevStatus("LD");
        if(embedPackageInsurance.getAccrualPaymentType() != null && embedPackageInsurance.getAccrualPaymentType().equalsIgnoreCase("A")) {
            policyTrans.setInterfaceType("A");
            policyTrans.setAccrualInstDate(embedPackageInsurance.getAccrualInstDate());
            policyTrans.setAccrualPaymentType(embedPackageInsurance.getAccrualPaymentType());
        }else{
            policyTrans.setInterfaceType("C");
        }
        policyTrans.setPolCreateddt(new Date());
        savedPol = policyTransRepo.save(policyTrans);

        systemTrans = new SystemTrans();
        systemTrans.setDoneDate(new Date());
        systemTrans.setDoneBy(user);//userUtils.getCurrentUser());
        systemTrans.setPolicy(savedPol);
        systemTrans.setTransLevel("U");
        systemTrans.setTransCode("BUD");
        systemTrans.setTransAuthorised("N");
        systemTransRepo.save(systemTrans);

        BigDecimal basicPrem = BigDecimal.ZERO;
        for (EmbedPackageInsuranceRisks insuranceRisk : insuranceRisks) {
            basicPrem = insuranceRisk.getRiskPremium().add(BigDecimal.ZERO).multiply(BigDecimal.valueOf(100/100.45));

            RiskTrans riskTrans = new RiskTrans();
            final String pinToSearch = insuranceRisk.getInsuredPin().trim();
            final ClientDef insured = clientRepository.findByPinNoIgnoreCase(pinToSearch);

            if (insured != null) {
                riskTrans.setInsured(insured);
            } else {
                throw new BadRequestException("Client not found with Kra "+pinToSearch);
            }

            BindersDef newBinder = bindersRepo.findBinderByAccId(embedPackageInsurance.getInsurerCode().getAcctId(), product.getProCode());
            riskTrans.setBinder(newBinder);
            String normalizedSubClass = embedPackageInsurance.getProductName().trim().toLowerCase().replace(" ", "");
            String normalizedCoverType = embedPackageInsurance.getCoverType().trim().toLowerCase().replace(" ", "");
            final SubClassDef subClassDef = subClassRepo.findBySubClassName(normalizedSubClass);
            final CoverTypesDef coverTypesDef = coverTypesRepo.findCoverTypesByBindId(newBinder.getBinId(), normalizedCoverType);
            System.out.println("Binder id ::" + newBinder.getBinId());
            System.out.println("CoverTypes id:: " + coverTypesDef.getCovId());
            BinderDetails newDetails = binderDetRepo.findOne(QBinderDetails.binderDetails.binder.binId.eq(newBinder.getBinId()).and(QBinderDetails.binderDetails.subCoverTypes.coverTypes.covId.eq(coverTypesDef.getCovId())));
            riskTrans.setSubclass(subClassDef);
            riskTrans.setCovertype(coverTypesDef);
            riskTrans.setRiskShtDesc(insuranceRisk.getInsuredAcc());
            riskTrans.setRiskDesc(insuranceRisk.getInsuredCategory());
            riskTrans.setBinderDetails(newDetails);
            riskTrans.setWefDate(wef);
            riskTrans.setWetDate(wet);
            riskTrans.setTransType("BU");
            riskTrans.setPolicy(savedPol);
            riskTrans.setComputeType("B");
            riskTrans.setAutogenCert("N");
            riskTrans.setSumInsured(BigDecimal.ZERO);
            riskTrans.setButchargePrem(basicPrem); //insuranceRisk.getRiskPremium());
            riskTrans.setNetpremium(insuranceRisk.getRiskPremium());
            riskTrans.setComputePremium(insuranceRisk.getRiskPremium());


            // get nb commm rates only
            Iterable<CommissionRates> commissionRatess = commRatesRepo.findAll(QCommissionRates.commissionRates.bindersDef.binId.eq(newBinder.getBinId())
                    .and(QCommissionRates.commissionRates.applicableAt.eq("NB")));
            if(commissionRatess.spliterator().getExactSizeIfKnown()==0){
                throw new BadRequestException("Commission Rates is not setup");
            }
            CommissionRates commissionRate = Streamable.streamOf(commissionRatess).findAny().get();
            AccountDef accountDef = accountRepo.findOne(QAccountDef.accountDef.accountType.accountType.eq(AccountTypeEnum.INS)
                    .and(QAccountDef.accountDef.acctId.eq(savedPol.getAgent().getAcctId())));

            BigDecimal commissionRates = (commissionRate != null) ? commissionRate.getCommRate() : null;
            BigDecimal accountCommRates = (accountDef != null) ? accountDef.getAccountType().getCommRate() : null;

            if (commissionRates != null && commissionRates.compareTo(BigDecimal.ZERO) > 0) {
                riskTrans.setCommRate(commissionRates);
            } else if (accountCommRates != null && accountCommRates.compareTo(BigDecimal.ZERO) > 0) {
                riskTrans.setCommRate(accountCommRates);
            } else {
                throw new BadRequestException("Commission Rates is not setup");
            }


            RiskTrans savedRisk = riskTransRepo.save(riskTrans);
            long riskIdentifier = Long.valueOf(String.valueOf(dateUtils.getUwYear(policyTrans.getWefDate())) + String.valueOf(savedRisk.getRiskId()));
            PolicyActiveRisks activeRisk = new PolicyActiveRisks();
            activeRisk.setPolicy(policyTrans);
            activeRisk.setRisk(riskTrans);
            activeRisk.setRiskIdentifier(riskIdentifier);
            activeRisksRepo.save(activeRisk);
            savedRisk.setRiskIdentifier(riskIdentifier);
            riskTransRepo.save(savedRisk);

            //sections
            //uploadValidatorsService.saveSectionTransaction("SIM INSURED - PREMIER", insuranceRisk.getRiskPremium());
            //uploadValidatorsService.saveSectionTransaction("VALUE", insuranceRisk.getRiskPremium());
            BigDecimal premValue = BigDecimal.ZERO;
            String category = insuranceRisk.getInsuredCategory().toUpperCase();
            if(category.contains("BUSINESS")){
                uploadValidatorsService.saveSectionTransaction("SI BCLUB", premValue, savedRisk);
            } else if (category.contains("ULTIMATE")){
                uploadValidatorsService.saveSectionTransaction("SI ULTIMATE", premValue, savedRisk);
            } else if (category.contains("PRESTIGE")){
                uploadValidatorsService.saveSectionTransaction("SI PRESTIGE", premValue, savedRisk);
            } else if (category.contains("PREMIER")){
                uploadValidatorsService.saveSectionTransaction("SI PREMIER", premValue, savedRisk);
            }else if (category.contains("MRB")){
                uploadValidatorsService.saveSectionTransaction("CASA MRB", premValue, savedRisk);
            }else {
                //no risk was found default to
                uploadValidatorsService.saveSectionTransaction("SI PREMIER", premValue, savedRisk);
            }
            //StrisectionCode, BigDecimal amount, RiskTrans savedTrans)
            //uploadValidatorsService.saveSectionTransaction("SUM INSURED - ULTIMATE", insuranceRisk.getRiskPremium());
            //uploadValidatorsService.saveSectionTransaction("SUM INSURED - BUSINESS CLUB", insuranceRisk.getRiskPremium());
        }

        policyTransRepo.save(savedPol);

        //make ready the policy
        uploadValidatorsService.saveGeneralPolicyUpload(savedPol.getPolicyId(), true);

        embedPackageInsurance.setPolicyTrans(savedPol);
        embedPackageInsurance.setTranStatus("Y");
        embedPackageInsurance.setPolicyAuthorized("N");
        embedPackageInsurance.setProcessedDate(new Date());
        embedPackageInsurance.setProcessedBy(user);//userUtils.getCurrentUser());
        embedPackageInsuranceRepo.save(embedPackageInsurance);

        return savedPol;
    }

    @Override
    public List<Long> processBulkPackagedInsurPol(BatchCreditBatch batchCreditBatch, boolean isApproved) throws BadRequestException {
        if (batchCreditBatch.getBatchRecords().size() <= 0) {
            throw new BadRequestException("Select At least One Transaction To Process");
        }

        Job job = JobBuilder.aNewJob()
                .reader(new IterableRecordReader(batchCreditBatch.getBatchRecords()))
                .named("ebd_pkg_processing" + new SimpleDateFormat("ddMMyyyhhmmss").format(new Date()))
                .processor((RecordProcessor<Record, Record>) renForm -> {
                    Long policyId = ((BatchRecord) renForm.getPayload()).getPayload().getBatchId();
                    Long userId = ((BatchRecord) renForm.getPayload()).getPayload().getUserId();
                    processSinglePackagedInsurPol(policyId, userId,true);
                    return renForm;
                })
                .pipelineListener(new RenewalJobListener())
                .build();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Future<JobReport> report = executorService.submit(job);

        return  new ArrayList<>();



//        if (embedIds.size() <= 0) {
//            throw new BadRequestException("Select At least One Transaction To Process");
//        }
//        PolicyTrans savedPol = null;
//        List<Long> processedPolicyIds = new ArrayList<>();
//        for (Long embedId : embedIds) {
//            savedPol = processSinglePackagedInsurPol(embedId, true);
//            processedPolicyIds.add(savedPol.getPolicyId());
//        }
//        return processedPolicyIds;
    }

    @Override
    public String approveSinglePackagedInsurPol(Long policyId) throws BadRequestException {
        authService.authorizeBulkUploadPolicies(policyId);
        EmbedPackageInsurance embedPackageInsurance = embedPackageInsuranceRepo.findBulkStockByPolicyId(policyId);
        embedPackageInsurance.setPolicyAuthorized("Y");
        embedPackageInsuranceRepo.save(embedPackageInsurance);
        return "Transaction Authorized Successfully";
    }

    @Override
    public String approveBulkPackagedInsurPol(List<Long> policyIds) throws BadRequestException {

        if (policyIds.size() <= 0) {
            throw new BadRequestException("Select At least One Transaction To Process");
        }
        for (Long policyId : policyIds) {
            authService.authorizeBulkUploadPolicies(policyId);
            EmbedPackageInsurance embedPackageInsurance = embedPackageInsuranceRepo.findBulkStockByPolicyId(policyId);
            embedPackageInsurance.setPolicyAuthorized("Y");
            embedPackageInsuranceRepo.save(embedPackageInsurance);
        }

        return "Transactions Authorized Successfully";
    }

    @Override
    @Transactional
    public String deleteBulkPackagedInsurPol(List<Long> embedIds) throws BadRequestException {
        if (embedIds.size() <= 0) {
            throw new BadRequestException("Select At least One Transaction To Process");
        }
        for (Long embedId : embedIds) {
            EmbedPackageInsurance embedPackageInsurance = embedPackageInsuranceRepo.findOne(QEmbedPackageInsurance.embedPackageInsurance.embedPackageId.eq(embedId));
//            List<EmbedPackageInsuranceRisks> insuranceRisks = Streamable.streamOf(embedPackageInsuranceRiskRepo.findAll(
//                    QEmbedPackageInsuranceRisks.embedPackageInsuranceRisks.embedPackageInsurance.embedPackageId.eq(embedId)
//            )).filter(a -> a.getRiskStatus().equalsIgnoreCase("N")).collect(Collectors.toList());
//
//            //delete insurance that is not processed
            if (embedPackageInsurance == null) {
                continue; // or log and skip
            }

            if("N".equalsIgnoreCase(embedPackageInsurance.getTranStatus())){
                embedPackageInsuranceRiskRepo.deleteByPackage(embedPackageInsurance);
//                embedPackageInsuranceRiskRepo.(insuranceRisks);
//                //delete insurance
              embedPackageInsuranceRepo.delete(embedPackageInsurance);
            }
        }
        return "Embed package deleted successfully.";
    }

    @Override
    @Transactional(readOnly = false)
    public String bulkDelProcessedPolicies(List<Long> embedIds) throws BadRequestException {
        if (embedIds == null || embedIds.isEmpty()) {
            throw new BadRequestException("Select at least one transaction to process");
        }

        for (Long embedId : embedIds) {
            EmbedPackageInsurance embedPackageInsurance = embedPackageInsuranceRepo.findWithPolicyTransByEmbedId(embedId);

            if (embedPackageInsurance == null) {
                System.out.println("No BulkPolicyCreation found for ID: " + embedIds);
                continue;
            }

            if (!"Y".equalsIgnoreCase(embedPackageInsurance.getTranStatus())) {
                System.out.println("Transaction not marked as processed for ID: " + embedIds);
                continue;
            }

            embedPackageInsuranceRiskRepo.deleteByPackage(embedPackageInsurance);
            embedPackageInsuranceRepo.delete(embedPackageInsurance);
            //delete the created policy
            uploadValidatorsService.deleteprocessedPol(embedId);

        }

        return "Transactions Deleted Successfully";
    }
}
