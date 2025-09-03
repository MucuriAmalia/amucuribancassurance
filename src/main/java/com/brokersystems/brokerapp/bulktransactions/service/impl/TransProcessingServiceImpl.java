package com.brokersystems.brokerapp.bulktransactions.service.impl;

import com.brokersystems.brokerapp.bulktransactions.utils.ExcelReaderUtil;
import com.brokersystems.brokerapp.enums.AccountTypeEnum;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.DateUtilities;
import com.brokersystems.brokerapp.server.utils.TemplateMerger;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.server.utils.ValidatorUtils;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.*;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.brokersystems.brokerapp.trans.model.*;
import com.brokersystems.brokerapp.trans.repository.SystemTransRepo;
import com.brokersystems.brokerapp.trans.repository.SystemTransactionsRepo;
import com.brokersystems.brokerapp.trans.repository.TransMappingRepo;
import com.brokersystems.brokerapp.bulktransactions.dtos.TransProcessingAuthDTO;
import com.brokersystems.brokerapp.bulktransactions.models.*;
import com.brokersystems.brokerapp.bulktransactions.repositories.TransProcessPremItemRepo;
import com.brokersystems.brokerapp.bulktransactions.repositories.TransProcessRisksRepo;
import com.brokersystems.brokerapp.bulktransactions.repositories.TransProcessingRepository;
import com.brokersystems.brokerapp.bulktransactions.service.TransProcessingService;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.model.RiskTrans;
import com.brokersystems.brokerapp.uw.model.SectionTrans;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import com.brokersystems.brokerapp.uw.repository.RiskTransRepo;
import com.brokersystems.brokerapp.uw.repository.SectionTransRepo;
import com.mysema.query.types.Predicate;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.ParseException;
import java.io.IOException;

import static org.apache.poi.ss.usermodel.Cell.*;

@Service
public class TransProcessingServiceImpl implements TransProcessingService {
    private static final Logger log = LoggerFactory.getLogger(TransProcessingServiceImpl.class);

    @Autowired
    private TransProcessingRepository transProcessingRepository;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private ProductsRepo productsRepo;

    @Autowired
    private PolicyTransRepo policyTransRepo;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private DateUtilities dateUtils;

    @Autowired
    private UserBranchesRepository userBranchesRepository;

    @Autowired
    private ParamService paramService;

    @Autowired
    private SequenceRepository sequenceRepo;

    @Autowired
    private TemplateMerger templateMerger;

    @Autowired
    private SystemTransRepo transRepo;

    @Autowired
    private ValidatorUtils validator;

    @Autowired
    private BindersRepo bindersRepo;

    @Autowired
    private SystemTransactionsRepo systemTransactionsRepo;

    @Autowired
    private SubClassRepo subClassRepo;

    @Autowired
    private CoverTypesRepo coverTypesRepo;

    @Autowired
    private RiskTransRepo riskTransRepo;

    @Autowired
    private OrgBranchRepository orgBranchRepository;

    @Autowired
    private ClientTypeRepo clientTypeRepo;

    @Autowired
    private BinderDetRepo binderDetRepo;

    @Autowired
    private TransMappingRepo transMappingRepo;

    @Autowired
    private AccountTypeRepo accountTypeRepo;

//    @Autowired
//    private PolicyAuthorizationImpl policyAuthorizationImpl;

    @Autowired
    private CommRatesRepo commRatesRepo;
    @Autowired
    private TransProcessRisksRepo transProcessRisksRepo;

    @Autowired
    private TransProcessPremItemRepo transProcessPremItemRepo;

    @Autowired
    private SectionRepo setupSectionRepo;

    @Autowired
    private PremRatesRepo premRatesRepo;

    @Autowired
    private SectionTransRepo sectionRepo;


    @Override
    @Transactional
    public Map<String, Object> uploadAndSaveExcelData(MultipartFile file) throws BadRequestException {
        UserBranches userBranches = userBranchesRepository.findByUser(userUtils.getCurrentUser());
        Long branchId = userBranches.getBranch().getObId();
        final OrgBranch userBranch = orgBranchRepository.findOne(QOrgBranch.orgBranch.obId.eq(branchId));
        System.out.println("User branch:: " +userBranch.getObId());
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            throw new BadRequestException("Upload files with .xlsx or .xls extension only");
        }

        Map<String, Object> response = new HashMap<>();
        List<String> invalidRecords = new ArrayList<>();
        List<TransactionProcessing> transactions = new ArrayList<>();
        List<TransProcessRisks> processRisks = new ArrayList<>();
        List<TransProcessPremItems> processPremItems = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);

            Sheet sheetOne = workbook.getSheet("Policy Details");
            if (sheetOne == null) {
                sheetOne = workbook.getSheetAt(0);
            }

            if (sheetOne == null) {
                throw new BadRequestException("Excel file contains no sheets");
            }

            log.info("Processing sheet: {}", sheetOne.getSheetName());

            int startRowIndex = findDataStartRow(sheetOne);
            if (startRowIndex == -1) {
                throw new BadRequestException("Sheet contains no valid data. Expected header row not found.");
            }

            Map<String, TransactionProcessing> transMap = new HashMap<>();
            Map<String, TransProcessRisks> processRiskMap = new HashMap<>();

//            int startRowIndex = findDataStartRow(sheetOne);
//            if (startRowIndex == -1) {
//                throw new BadRequestException("Sheet One contains no data.");
//            }

            String refCode = null;
            Predicate pedSystem = QSystemSequence.systemSequence.transType.eq("LD");
            if (sequenceRepo.count(pedSystem) == 0)
                throw new BadRequestException("Sequence for Loaded Data has not been defined");
            SystemSequence dataSequnce = sequenceRepo.findOne(pedSystem);
            Long sequenceNumber = dataSequnce.getNextNumber();
            String transType = "LD";
            refCode = transType + String.format("%05d", sequenceNumber);
            System.out.println("refCode >>>> " +refCode);
            dataSequnce.setLastNumber(sequenceNumber);
            dataSequnce.setNextNumber(sequenceNumber + 1);
            sequenceRepo.save(dataSequnce);

            for (int rowNum = 2; rowNum <= sheetOne.getLastRowNum(); rowNum++) {
                Row row = sheetOne.getRow(rowNum);
                if (isBlankRow(row)) continue;

                try {
                    TransactionProcessing transaction = new TransactionProcessing();
                    String SerialNo = getCellStringValue(row.getCell(0)); // Batch Number
                    System.out.println("Serial No>>>> " +SerialNo);
                    transaction.setSerialNo(getCellStringValue(row.getCell(0)));
                    transaction.setPolNumber(getCellStringValue(row.getCell(1))); // Policy Number
                    transaction.setPolCode(getCellStringValue(row.getCell(2))); // Policy Code
//                    transaction.setProductName(getCellStringValue(row.getCell(2)));
                    transaction.setCoverDateFrom(getCellDateValue(row.getCell(3)));
                    transaction.setCoverDateTo(getCellDateValue(row.getCell(4)));
                    transaction.setCurrency(getCellStringValue(row.getCell(5)));
                    transaction.setInceptionDate(ExcelReaderUtil.parseFlexibleDate(getCellStringValue(row.getCell(6))));
//                    transaction.setClientIDNo(getCellStringValue(row.getCell(7))); // Proposer_Code
                    transaction.setProposerCode(getCellStringValue(row.getCell(7))); // Proposer Code
                    transaction.setAgentCode(getCellStringValue(row.getCell(8))); // Agent Code
                    String policyStatus = getCellStringValue(row.getCell(9));
                    transaction.setTransProcessed(policyStatus);
//                    transaction.setPolicyStatus(getCellStringValue(row.getCell(9))); // Policy Status
                    String isRenewable = getCellStringValue(row.getCell(10));
                    transaction.setIsRenewable(isRenewable);
                    transaction.setRenewDate(ExcelReaderUtil.parseFlexibleDate(getCellStringValue(row.getCell(11))));
                    transaction.setfClientFname(getCellStringValue(row.getCell(12)));
                    transaction.setClientOtherNames(getCellStringValue(row.getCell(13)));
                    transaction.setRiskId(getCellStringValue(row.getCell(14))); // Risk ID
                    transaction.setSubclassCode(getCellStringValue(row.getCell(15))); // Subclass Code
//                    transaction.setCoverType(getCellStringValue(row.getCell(16)));
                    transaction.setCoverTypeCode(getCellStringValue(row.getCell(16))); // Cover Type Code
                    transaction.setRiskSumAssured(getCellBigDecimalValue(row.getCell(17)) != null ? getCellBigDecimalValue(row.getCell(17)) : BigDecimal.ZERO); // Risk Sum Assured
                    transaction.setAuthorisedBy(getCellStringValue(row.getCell(18)));
                    transaction.setAuthorisedDate(ExcelReaderUtil.parseFlexibleDate(getCellStringValue(row.getCell(19))));
                    transaction.setCoinsuranceFlag(getCellStringValue(row.getCell(20)) != null ? getCellStringValue(row.getCell(20)) : "N");
                    String coinsurancePercentage = getCellStringValue(row.getCell(21));
                    if (coinsurancePercentage != null && !coinsurancePercentage.trim().isEmpty()) {
                        transaction.setCoinsurancePercentage(new BigDecimal(coinsurancePercentage));
                    }
                    transaction.setCoinsuranceLeaderFlag(getCellStringValue(row.getCell(22)) != null ? getCellStringValue(row.getCell(22)) : "N");
                    // Additional codes
                    transaction.setSectionCode(getCellStringValue(row.getCell (23)) != null ? ExcelReaderUtil.getCellValue(row, 23) : "");
                    transaction.setPolicyInsuredCode(getCellStringValue(row.getCell(24)));
                    transaction.setRiskCode(getCellStringValue(row.getCell(25)));





//                    Date wef = ExcelReaderUtil.parseFlexibleDate(getCellValueAsString(row.getCell(3)));
//                    if (wef == null) {
//                        throw new BadRequestException("Invalid Cover Date From in row " + (rowNum + 1));
//                    }
//                    transaction.setCoverDateFrom(wef);
//
//                    Date wet = ExcelReaderUtil.parseFlexibleDate(getCellValueAsString(row.getCell(4)));
//                    transaction.setCoverDateTo(wet);


                    String clientId = getCellStringValue(row.getCell(41));

//                    if (!validator.validateIdNo(clientId)){
//                        throw new BadRequestException("Invalid client ID Number");
//                    }
                    transaction.setClientIDNo(clientId);

//                    String clientPin = ExcelReaderUtil.getCellValue(row, 4) != null ? ExcelReaderUtil.getCellValue(row, 4) : ""; //getCellStringValue(row.getCell(4));

//                    if (!validator.validatePin(clientPin)) {
//                        throw new Exception("Invalid client KRA PIN");
//                    }
//                    transaction.setClientPin(clientPin);

//                    transaction.setClientPhoneNo(getCellValueAsString(row.getCell(50))); // Client Phone Number
//                    transaction.setClientDOB(ExcelReaderUtil.parseFlexibleDate(getCellValueAsString(row.getCell(70))));
//                    transaction.setClientCIF(getCellValueAsString(row.getCell(8)));
//                    transaction.setClientType(getCellValueAsString(row.getCell(50)));
//                    transaction.setUnderwriter(getCellValueAsString(row.getCell(11)));
//                    String frequency = ExcelReaderUtil.getCellValue(row, 40) != null ? ExcelReaderUtil.getCellValue(row, 40) : "";
//                    if (frequency != null) {
//                        if (frequency.equalsIgnoreCase("Daily")) {
//                            transaction.setFrequency("Daily");
//                        } else if (frequency.equalsIgnoreCase("Weekly")) {
//                            transaction.setFrequency("Weekly");
//                        } else if (frequency.equalsIgnoreCase("Monthly")) {
//                            transaction.setFrequency("Monthly");
//                        } else if (frequency.equalsIgnoreCase("Quarterly")) {
//                            transaction.setFrequency("Quarterly");
//                        } else if (frequency.equalsIgnoreCase("Semi-Annually")) {
//                            transaction.setFrequency("Semi-Annually");
//                        } else if (frequency.equalsIgnoreCase("Annually")) {
//                            transaction.setFrequency("Annually");
//                        } else if (frequency.equalsIgnoreCase("Single")) {
//                            transaction.setFrequency("Single");
//                        } else {
//                            throw new BadRequestException("Unknown frequency '" + frequency + "'");
//                        }
//                    }
//                    transaction.setFrequency(frequency);









// Coinsurance details




//                    transaction.setSumInsured(BigDecimal.valueOf(Long.parseLong(getCellValueAsString(row.getCell(19)) != null ? getCellValueAsString(row.getCell(19)) : "0")));
//                    transaction.setGrossPremium(BigDecimal.valueOf(Long.parseLong(getCellValueAsString(row.getCell(21)) != null ? getCellValueAsString(row.getCell(21)) : "0")));
//                    transaction.setBalance(BigDecimal.valueOf(Long.parseLong(getCellValueAsString(row.getCell(22)) != null ? getCellValueAsString(row.getCell(22)) : "0")));
//                    transaction.setPremium(BigDecimal.valueOf(Long.parseLong(getCellValueAsString(row.getCell(23)) != null ? getCellValueAsString(row.getCell(23)) : "0")));
//                    transaction.setNetPremium(BigDecimal.valueOf(Long.parseLong(getCellValueAsString(row.getCell(24)) != null ? getCellValueAsString(row.getCell(24)) : "0")));
//                    transaction.setPaidPremium(BigDecimal.valueOf(Long.parseLong(getCellValueAsString(row.getCell(25)) != null ? getCellValueAsString(row.getCell(25)) : "0")));
//                    transaction.setWhtx(BigDecimal.valueOf(Long.parseLong(getCellValueAsString(row.getCell(26)) != null ? getCellValueAsString(row.getCell(26)) : "0")));
//                    transaction.setPhFund(BigDecimal.valueOf(Long.parseLong(getCellValueAsString(row.getCell(27)) != null ? getCellValueAsString(row.getCell(27)) : "0")));
//                    transaction.setStampDuty(BigDecimal.valueOf(Long.parseLong(getCellValueAsString(row.getCell(28)) != null ? getCellValueAsString(row.getCell(28)) : "0")));
//                    transaction.setTrainingLevy(BigDecimal.valueOf(Long.parseLong(getCellValueAsString(row.getCell(29)) != null ? getCellValueAsString(row.getCell(29)) : "0")));
//                    transaction.setExtras(BigDecimal.valueOf(Long.parseLong(getCellValueAsString(row.getCell(30)) != null ? getCellValueAsString(row.getCell(30)) : "0")));
//                    transaction.setBankComm(BigDecimal.valueOf(Long.parseLong(getCellValueAsString(row.getCell(31)) != null ? getCellValueAsString(row.getCell(31)) : "0")));
//                    transaction.setAgentComm(BigDecimal.valueOf(Long.parseLong(getCellValueAsString(row.getCell(32)) != null ? getCellValueAsString(row.getCell(32)) : "0")));
//                    transaction.setSubAgnetABNo(getCellValueAsString(row.getCell(33)));
//                    transaction.setMarketerABNo(getCellValueAsString(row.getCell(34)));
//                    transaction.setLeadsABNo(getCellValueAsString(row.getCell(35)));
                    transaction.setBranch(userBranch);
                    transaction.setTransProcessed("N");
                    transaction.setTransRefCode(refCode);
                    transaction.setUploadedBy(userUtils.getCurrentUser());
                    transaction.setDateUploaded(new Date());
                    transaction.setTransType(transType);
                    transactions.add(transaction);
                    transMap.put(SerialNo, transaction);
                } catch (Exception e) {
                    invalidRecords.add("Sheet One, Row " + (rowNum + 1) + ": " + e.getMessage());
                }
            }

            if (!transactions.isEmpty()) {
                System.out.println("saving transactions ");
                transProcessingRepository.save(transactions);
            }

//            Sheet sheetTwo = workbook.getSheet("Risk Details");
//            int startRowIndexTwo = findDataStartRow(sheetTwo);
//            if (startRowIndexTwo == -1) {
//                throw new BadRequestException("Sheet Two contains no data.");
//            }
//
//            for (int rowNum = startRowIndexTwo; rowNum <= sheetTwo.getLastRowNum(); rowNum++) {
//                Row row = sheetTwo.getRow(rowNum);
//                if (isBlankRow(row)) continue;
//                for (int i = 0; i < row.getLastCellNum(); i++) {
//                    System.out.println("Cell " + i + ": " + getCellStringValue(row.getCell(i)));
//                }
//
//                try {
//
//                    List<TransProcessRisks> riskProList = Optional.ofNullable(
//                            (List<TransProcessRisks>) transProcessRisksRepo.findAll(
//                                    QTransProcessRisks.transProcessRisks.riskStatus.eq("Y")
//                                            .and(QTransProcessRisks.transProcessRisks.riskRefCode.eq(refCode))
//                            )
//                    ).orElse(new ArrayList<>());
//
//                    System.out.println("riskList:: " +riskProList);
//
//                    List<TransactionProcessing> processingsList = (List<TransactionProcessing>) transProcessingRepository.findAll(
//                            QTransactionProcessing.transactionProcessing.transProcessed.eq("N")
//                                    .and(QTransactionProcessing.transactionProcessing.transRefCode.eq(refCode))
//                    );
//                    System.out.println("processings:: " +processingsList.toString());
//
//                    // Convert TransactionProcessingList into a Set for faster lookups
//                    Set<String> refCodeSet = processingsList.stream()
//                            .map(TransactionProcessing::getTransRefCode)
//                            .collect(Collectors.toSet());
//
//                    for (TransProcessRisks checkRisk : riskProList) {
//                        if (refCodeSet.contains(checkRisk.getRiskRefCode())) {
//                            // Mismatch found, immediately throw an error
//                            throw new BadRequestException(
//                                    "Serial No " + checkRisk.getRiskSerialNo() + " in risk details is pending processing, process before re-uploading this file"
//                            );
//                        }
//                    }
//
//                    // Proceed to add new risk if no mismatch was found
//                    String serialNo = ExcelReaderUtil.getCellValue(row, 0); //getCellStringValue(row.getCell(0));
//                    TransactionProcessing linkedPolicy = transMap.get(serialNo);
//
//                    if (linkedPolicy == null) {
//                        throw new BadRequestException(
//                                "Sheet Two, Row " + (rowNum + 1) + ": Serial number '" + serialNo + "' not found in Sheet One."
//                        );
//                    }
//
//                    TransProcessRisks transProcessRisks = new TransProcessRisks();
//                    transProcessRisks.setRiskSerialNo(serialNo);
//
//                    transProcessRisks.setInsuredFName(ExcelReaderUtil.getCellValue(row, 1)); //getCellStringValue(row.getCell(1)));
//                    transProcessRisks.setInsuredOtherNames(ExcelReaderUtil.getCellValue(row, 2)); //getCellStringValue(row.getCell(2)));
//                    transProcessRisks.setInsuredIdNo(ExcelReaderUtil.getCellValue(row, 3)); //getCellStringValue(row.getCell(3)));
//                    transProcessRisks.setInsuredPin(ExcelReaderUtil.getCellValue(row, 4)); //getCellStringValue(row.getCell(4)));
//                    transProcessRisks.setInsuredPhoneNo(ExcelReaderUtil.getCellValue(row, 5)); //getCellStringValue(row.getCell(5)));
//                    transProcessRisks.setInsuredEmail(ExcelReaderUtil.getCellValue(row, 6)); //getCellStringValue(row.getCell(6)));
//                    transProcessRisks.setInsuredDOB(ExcelReaderUtil.parseFlexibleDate((ExcelReaderUtil.getCellValue(row, 7)))); //getCellDateValue(row.getCell(7)));
//                    transProcessRisks.setInsuredCIF(ExcelReaderUtil.getCellValue(row, 8)); //getCellStringValue(row.getCell(8)));
//                    transProcessRisks.setInsuredType(ExcelReaderUtil.getCellValue(row, 9)); //getCellStringValue(row.getCell(9)));
//                    transProcessRisks.setRiskShtDesc(ExcelReaderUtil.getCellValue(row, 10)); //getCellStringValue(row.getCell(10)));
//                    transProcessRisks.setRiskDesc(ExcelReaderUtil.getCellValue(row, 11)); //getCellStringValue(row.getCell(11)));
//                    transProcessRisks.setNegotiatedPrem(BigDecimal.valueOf(Long.parseLong(ExcelReaderUtil.getCellValue(row, 12)))); //getCellBigDecimalValue(row.getCell(12)));
//                    transProcessRisks.setRiskTotalInsts(Integer.valueOf(ExcelReaderUtil.getCellValue(row, 13))); //getCellStringValue(row.getCell(13))));
//                    transProcessRisks.setRiskRefCode(refCode);
//                    transProcessRisks.setRiskStatus("Y");
//                    transProcessRisks.setRiskUploadedBy(userUtils.getCurrentUser());
//                    transProcessRisks.setRiskUploadDate(new Date());
//                    transProcessRisks.setRiskWet(linkedPolicy.getCoverDateFrom());
//                    transProcessRisks.setRiskWef(linkedPolicy.getCoverDateTo());
//                    transProcessRisks.setTransProcessing(linkedPolicy);
//
//                    processRisks.add(transProcessRisks);
//                    processRiskMap.put(serialNo, transProcessRisks);
//
//                } catch (Exception e) {
//                    invalidRecords.add("Sheet Two, Row " + (rowNum + 1) + ": " + e.getMessage());
//                }
//            }
//            if(!processRisks.isEmpty()) {
//                System.out.println("saving processRisks ");
//                transProcessRisksRepo.save(processRisks);
//            }

//            Sheet sheetThree = workbook.getSheet("Premium Items");
//            int startRowIndexThree = findDataStartRow(sheetThree);
//            if (startRowIndexThree == -1) {
//                throw new BadRequestException("Sheet Three contains no data.");
//            }
//
//
//            for (int rowNum = startRowIndexThree; rowNum <= sheetThree.getLastRowNum(); rowNum++) {
//                Row row = sheetThree.getRow(rowNum);
//                if (isBlankRow(row)) continue;
//
//                try {
//
//                    List<TransProcessPremItems> premItemsList = Optional.ofNullable(
//                            (List<TransProcessPremItems>) transProcessPremItemRepo.findAll(
//                                    QTransProcessPremItems.transProcessPremItems.premRiskStatus.eq("Y")
//                                            .and(QTransProcessPremItems.transProcessPremItems.premRefCode.eq(refCode))
//                            )
//                    ).orElse(new ArrayList<>());
//
//                    System.out.println("premItemsList:: " +premItemsList);
//
//                    List<TransProcessRisks> transProcessRisksList = (List<TransProcessRisks>) transProcessRisksRepo.findAll(
//                            QTransProcessRisks.transProcessRisks.riskStatus.eq("Y")
//                                    .and(QTransProcessRisks.transProcessRisks.riskRefCode.eq(refCode)));
//                    System.out.println("transProcessRisksList:: " +transProcessRisksList);
//                    List<TransactionProcessing> creationList = (List<TransactionProcessing>) transProcessingRepository.findAll(
//                            QTransactionProcessing.transactionProcessing.transProcessed.eq("N")
//                                    .and(QTransactionProcessing.transactionProcessing.transRefCode.eq(refCode))
//                    );
//                    System.out.println("creationList:: " +creationList);
//
//                    // Convert TransactionProcessing into a Set for faster lookups
//                    Set<String> refCodeSet = creationList.stream()
//                            .map(TransactionProcessing::getTransRefCode)
//                            .collect(Collectors.toSet());
//
//                    Set<String> riskCodeSet = transProcessRisksList.stream()
//                            .map(TransProcessRisks::getRiskRefCode)
//                            .collect(Collectors.toSet());
//
//                    for (TransProcessPremItems checkPremItems : premItemsList) {
//                        if (refCodeSet.contains(checkPremItems.getPremRefCode()) && riskCodeSet.contains(checkPremItems.getPremRefCode())) {
//                            // Mismatch found, immediately throw an error
//                            throw new BadRequestException(
//                                    "Serial No " + checkPremItems.getPremSerialNo() + " in premium item is pending processing, process before re-uploading this file"
//                            );
//                        }
//                    }
//
//                    // Proceed to add new risk if no mismatch was found
//                    String serialNo = ExcelReaderUtil.getCellValue(row, 0); // getCellStringValue(row.getCell(0));
//                    System.out.println("Serial No sheet three: " + serialNo);
//                    TransactionProcessing linkedPolicies = transMap.get(serialNo);
//                    TransProcessRisks linkedRisk = processRiskMap.get(serialNo);
//
//                    if (linkedPolicies == null) {
//                        throw new BadRequestException(
//                                "Sheet Three, Row " + (rowNum + 1) + ": Serial number '" + serialNo + "' not found in Sheet One."
//                        );
//                    }
//                    if (linkedRisk == null) {
//                        throw new BadRequestException(
//                                "Sheet Three, Row " + (rowNum + 1) + ": Serial number '" + serialNo + "' not found in Sheet Two."
//                        );
//                    }
//
//                    TransProcessPremItems premItems = new TransProcessPremItems();
//                    premItems.setPremSerialNo(serialNo);
//                    String sections = ExcelReaderUtil.getCellValue(row, 1); //getCellStringValue(row.getCell(1));
//                    premItems.setPremSections(sections != null ? sections.trim() : null);
//                    premItems.setPremLimit(BigDecimal.valueOf(Long.parseLong(ExcelReaderUtil.getCellValue(row, 2)))); //getCellBigDecimalValue(row.getCell(2)));
//                    premItems.setTransProcessing(linkedPolicies);
//                    premItems.setTransProcessRisks(linkedRisk);
//                    premItems.setPremRiskStatus("Y");
//                    premItems.setPremRefCode(refCode);
//                    premItems.setPremUploadedBy(userUtils.getCurrentUser());
//                    premItems.setPremUploadDate(new Date());
//
//                    processPremItems.add(premItems);
//
//                } catch (Exception e) {
//                    invalidRecords.add("Sheet Three, Row " + (rowNum + 1) + ": " + e.getMessage());
//                }
//            }
//
//            if(!processPremItems.isEmpty()) {
//                System.out.println("saving processPremItems ");
//                transProcessPremItemRepo.save(processPremItems);
//            }

            response.put("successfulPolicies", transactions.size());
            response.put("successfulRisks", 0);
            response.put("successfulPremiumItems", 0);
            response.put("failedRecords", invalidRecords.size());
            response.put("invalidRecords", invalidRecords);

            return response;

        } catch (IOException e) {
            throw new BadRequestException("Failed to read Excel file: " + e.getMessage());
        } catch (IllegalStateException e) {
            throw new BadRequestException("Invalid Excel file format: " + e.getMessage());
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid number format in Excel file: " + e.getMessage());
        } catch (NullPointerException e) {
            log.error("Missing required data", e);
            throw new BadRequestException("Required data missing in Excel file: " + e.getMessage());
        } catch (Exception e) {
            // Log the full stack trace for debugging
            log.error("Unexpected error while processing Excel file", e);
            throw new BadRequestException("Unexpected error while processing Excel file. Please check the format and try again.");
        }
    }


    private boolean isHeaderRow(Row row) {
        if (row == null) return false;
        Cell firstCell = row.getCell(0);
        if (firstCell == null) return false;

        String cellValue = getCellStringValue(firstCell);
        if (cellValue == null) return false;

        cellValue = cellValue.toLowerCase().trim();

        // Match the exact header from your template
        return  cellValue.equals("column1.1") ||
                cellValue.equals("column1.2") ||
                cellValue.equals("column1.3") ||
                cellValue.equals("column1.4") ||
                cellValue.equals("column1.5") ||
                cellValue.equals("column1.6") ||
                cellValue.equals("column1.7") ||
                cellValue.equals("column1.8") ||
                cellValue.equals("column1.9") ||
                cellValue.equals("column1.10") ||
                cellValue.equals("column1.11") ||
                cellValue.equals("column1.12") ||
                cellValue.equals("column1.13") ||
                cellValue.equals("column1.14") ||
                cellValue.equals("column1.15") ||
                cellValue.equals("column1.16") ||
                cellValue.equals("column1.17") ||
                cellValue.equals("column1.18") ||
                cellValue.equals("column1.19") ||
                cellValue.equals("column1.20") ||
                cellValue.equals("column1.21") ||
                cellValue.equals("column1.22") ||
                cellValue.equals("column1.23") ||
                cellValue.equals("column1.24") ||
                cellValue.equals("column1.25") ||
                cellValue.equals("column1.26");
    }

    private boolean isBlankRow(Row row) {
        if (row == null) return true;

        for (int cellNum = 0; cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                String value = getCellStringValue(cell);
                if (value != null && !value.trim().isEmpty()) {
                    return false;
                }
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

        try {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue();
                    }
                    return null;
                case Cell.CELL_TYPE_STRING:
                    String dateStr = cell.getStringCellValue().trim();
                    if (dateStr.isEmpty()) return null;

                    // Try multiple date formats
                    SimpleDateFormat[] formats = {
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),  // "2018-12-11 00:00:00"
                            new SimpleDateFormat("yyyy-MM-dd"),           // "2019-02-25"
                            new SimpleDateFormat("dd/MM/yyyy"),
                            new SimpleDateFormat("MM/dd/yyyy")
                    };

                    for (SimpleDateFormat format : formats) {
                        try {
                            format.setLenient(false);
                            return format.parse(dateStr);
                        } catch (ParseException e) {
                            continue;
                        }
                    }

                    log.error("Could not parse date string: '{}'", dateStr);
                    return null;
                default:
                    return null;
            }
        } catch (Exception e) {
            log.error("Error parsing date from cell: " + e.getMessage(), e);
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
        if (sheet == null) {
            log.error("Sheet is null");
            return -1;
        }

        // Check first few rows
        for (int i = 0; i <= Math.min(sheet.getLastRowNum(), 5); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                String firstCellValue = getCellStringValue(row.getCell(0));
                log.debug("Row {}, first cell: {}", i, firstCellValue);

                if (isHeaderRow(row)) {
                    log.info("Found header row at index {}", i);
                    return i + 1;
                }
            }
        }

        log.error("No header row found in sheet {}", sheet.getSheetName());
        return -1;
    }

//    private String getCellValueAsString(Cell cell) {
//        if (cell == null) {
//            return null;
//        }
//
//        switch (cell.getCellType()) {
//            case CELL_TYPE_STRING:
//                return cell.getStringCellValue().trim();
//            case CELL_TYPE_NUMERIC:
//                if (DateUtil.isCellDateFormatted(cell)) {
//                    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cell.getDateCellValue());
//                }
//                // Check if it's a whole number
//                double num = cell.getNumericCellValue();
//                if (num == Math.floor(num)) {
//                    return String.valueOf((long) num);
//                }
//                return String.valueOf(num);
//            case CELL_TYPE_BOOLEAN:
//                return String.valueOf(cell.getBooleanCellValue());
//            case CELL_TYPE_FORMULA:
//                switch (cell.getCachedFormulaResultType()) {
//                    case CELL_TYPE_STRING:
//                        return cell.getStringCellValue();
//                    case CELL_TYPE_NUMERIC:
//                        return String.valueOf(cell.getNumericCellValue());
//                    case CELL_TYPE_BOOLEAN:
//                        return String.valueOf(cell.getBooleanCellValue());
//                    default:
//                        return "";
//                }
//            default:
//                return "";
//        }
//    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<TransactionProcessing> findUnprocessedTrans(DataTablesRequest request) {
        List<Object[]> unProcessedTrans = transProcessingRepository.findUnprocessedTrans((request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%", request.getPageNumber(), request.getPageSize());
        final List<TransactionProcessing> unProcessed = new ArrayList<>();
        long rowCount = 0L;
        if (!unProcessedTrans.isEmpty()) rowCount = ((BigInteger) unProcessedTrans.get(0)[27]).intValue();
        for (Object[] unprocessedTran: unProcessedTrans){
            TransactionProcessing transProcessing = new TransactionProcessing();
            transProcessing.setTransProcessingId(((BigInteger) unprocessedTran[0]).longValue());
            transProcessing.setSerialNo((String) unprocessedTran[1]);
            transProcessing.setPolNumber((String) unprocessedTran[2]);
            transProcessing.setPolCode((String) unprocessedTran[3]);
            transProcessing.setCoverDateFrom((Date) unprocessedTran[4]);
            transProcessing.setCoverDateTo((Date) unprocessedTran[5]);
            transProcessing.setCurrency((String) unprocessedTran[6]);
            transProcessing.setInceptionDate((Date) unprocessedTran[7]);
            transProcessing.setProposerCode((String) unprocessedTran[8]);
            transProcessing.setAgentCode((String) unprocessedTran[9]);
            transProcessing.setTransProcessed((String) unprocessedTran[10]);
            transProcessing.setIsRenewable((String) unprocessedTran[11]);
            transProcessing.setRenewDate((Date) unprocessedTran[12]);
            transProcessing.setfClientFname((String) unprocessedTran[13]);
            transProcessing.setClientOtherNames((String) unprocessedTran[14]);
            transProcessing.setRiskId((String) unprocessedTran[15]);
            transProcessing.setSubclassCode((String) unprocessedTran[16]);
            transProcessing.setCoverTypeCode((String) unprocessedTran[17]);
            transProcessing.setRiskSumAssured((BigDecimal) unprocessedTran[18]);
            transProcessing.setAuthorisedBy((String) unprocessedTran[19]);
            transProcessing.setAuthorisedDate((Date) unprocessedTran[20]);
            transProcessing.setCoinsuranceFlag((String) unprocessedTran[21]);
            transProcessing.setCoinsurancePercentage((BigDecimal) unprocessedTran[22]);
            transProcessing.setCoinsuranceLeaderFlag((String) unprocessedTran[23]);
            transProcessing.setSectionCode((String) unprocessedTran[24]);
            transProcessing.setPolicyInsuredCode((String) unprocessedTran[25]);
            transProcessing.setRiskCode((String) unprocessedTran[26]);
            unProcessed.add(transProcessing);
        }
        Page<TransactionProcessing> page = new PageImpl<>(unProcessed, request,rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<TransProcessingAuthDTO> findUnauthorizedPolicies(DataTablesRequest request) {
        String search = (request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : null;
        List<Object[]> unAthorizedPolicies = policyTransRepo.findUnauthorizedPolicies(search,request.getPageNumber(), request.getPageSize());
        final List<TransProcessingAuthDTO> unAuthorized = new ArrayList<>();
        long rowCount = 0L;
        if(!unAthorizedPolicies.isEmpty()) rowCount = ((BigInteger) unAthorizedPolicies.get(0)[8]).intValue();
        for(Object[] unAuthorizedPolicy: unAthorizedPolicies){
            TransProcessingAuthDTO authDTO = new TransProcessingAuthDTO();
            authDTO.setPolicyId(((BigInteger) unAuthorizedPolicy[0]).longValue());
            authDTO.setBasicPrem((BigDecimal) unAuthorizedPolicy[1]);
            authDTO.setCoverFrom((Date) unAuthorizedPolicy[2]);
            authDTO.setCoverTo((Date) unAuthorizedPolicy[3]);
            authDTO.setPolNo((String) unAuthorizedPolicy[4]);
            authDTO.setSumInsured((BigDecimal) unAuthorizedPolicy[5]);
            authDTO.setLoadedCoverType((String) unAuthorizedPolicy[6]);
            authDTO.setDateProcessed((Date) unAuthorizedPolicy[7]);
            unAuthorized.add(authDTO);
        }
        Page<TransProcessingAuthDTO> page = new PageImpl<>(unAuthorized, request,rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = false)
    public String processSingleTrans(Long transId) throws BadRequestException {
        TransactionProcessing trans = transProcessingRepository.findOne(transId);
        TransProcessRisks transRisks = transProcessRisksRepo.findOne(QTransProcessRisks.transProcessRisks.transProcessing.transProcessingId.eq(transId));
        TransProcessPremItems transPremItems = transProcessPremItemRepo.findOne(QTransProcessPremItems.transProcessPremItems.transProcessing.transProcessingId.eq(transId)
                .and(QTransProcessPremItems.transProcessPremItems.transProcessing.transProcessingId.eq(transId)));
        if (trans.getTransProcessed().equalsIgnoreCase("Y")){
            throw new BadRequestException("This transaction is already processed");
        }
        final PolicyTrans policy = new PolicyTrans();
        policy.setWefDate(trans.getCoverDateFrom());
        policy.setWetDate(trans.getCoverDateTo());
        policy.setPolCreateddt(trans.getInceptionDate());
        policy.setPolNo(trans.getPolNumber());
        policy.setAuthStatus("PL");
        policy.setBasicPrem(trans.getGrossPremium());
        policy.setCommAmt(trans.getBankComm().negate());
        policy.setEndosbasicPremium(trans.getPremium());
        policy.setEndosCommissions(trans.getBankComm().negate());
        policy.setSumInsured(trans.getSumInsured());
        policy.setUwYear(dateUtils.getUwYear(policy.getWefDate()));
        policy.setCurrentStatus("PL");
        policy.setCoverFrom(trans.getCoverDateFrom());
        policy.setCoverTo(trans.getCoverDateTo());
        policy.setEndosgrossPremium(trans.getGrossPremium());
        policy.setRenewalDate(trans.getRenewDate());
        policy.setClientPolNo(trans.getPolNumber());
        policy.setWhtx(trans.getWhtx());
        policy.setPolTotWhtx(trans.getWhtx());
        policy.setPhcf(trans.getPhFund());
        policy.setTotPhcf(trans.getPhFund());
        policy.setStampDuty(trans.getStampDuty());
        policy.setExtras(trans.getExtras());
        policy.setPolTotExtras(trans.getExtras());
        policy.setPremium(trans.getPremium());
        policy.setTrainingLevy(trans.getTrainingLevy());
        policy.setTotTrainingLevy(trans.getTrainingLevy());
        policy.setPolTotComm(trans.getBankComm().negate());
        policy.setPolTotPrem(trans.getPremium());
        policy.setFuturePrem(trans.getGrossPremium());
        policy.setNetPrem(trans.getNetPremium());
        policy.setPolTotSI(trans.getSumInsured());
        if(trans.getPaidPremium() != null){
            policy.setPaidPremium(trans.getPaidPremium());
            policy.setOutstandingPremium(trans.getBalance());
            policy.setPaidPremPct(trans.getPaidPremium().divide(trans.getGrossPremium(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP));
        }
        policy.setFrequency(trans.getFrequency());
        if(trans.getBankComm() != BigDecimal.ZERO){
            policy.setCommAllowed(true);
        }
        if(trans.getMarketerABNo() != null){
            AccountDef marketer = accountRepo.findByMarketerABNo(trans.getMarketerABNo().trim());
            policy.setMarketerAgent(marketer);
            policy.setMarketerAgentComm(trans.getAgentComm());
            policy.setAbsaNoMarketer(trans.getMarketerABNo());
        }
        if (trans.getSubAgnetABNo() != null){
            AccountDef subAgent = accountRepo.findBySubAgentABNo(trans.getSubAgnetABNo().trim());
            policy.setSubAgent(subAgent);
            policy.setSubAgentComm(trans.getAgentComm());
            policy.setAbsaNoSubAgent(trans.getSubAgnetABNo());
        }
        if(trans.getLeadsABNo() !=null){
            User leadsMan = accountRepo.findByLeadsABNO(trans.getLeadsABNo().trim());
            policy.setLeadsMan(leadsMan);
            policy.setAbsaNoLeadsMan(trans.getLeadsABNo());
        }
        final String pinToSearch = trans.getClientPin().trim();
        final ClientDef client = clientRepository.findByPinNoIgnoreCase(pinToSearch);
        final String clientType = trans.getClientType().trim();
        final ClientTypes normalizedClientType = clientTypeRepo.findByNormalizeType(clientType);
        if (client != null) {
            policy.setClient(client);
        } else {
            final ClientDef clientDef = new ClientDef();
            Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("C");
            if (sequenceRepo.count(seqPredicate) == 0)
                throw new BadRequestException("Sequence for Client Definition has not been setup");
            SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
            Long seqNumber = sequence.getNextNumber();
            final String clientNumber = sequence.getSeqPrefix() + String.format("%06d", seqNumber);
            sequence.setLastNumber(seqNumber);
            sequence.setNextNumber(seqNumber + 1);
            sequenceRepo.save(sequence);
            clientDef.setFname(trans.getfClientFname());
            clientDef.setOtherNames(trans.getClientOtherNames());
            clientDef.setEmailAddress(trans.getClientEmail());
            clientDef.setPhoneNo(trans.getClientPhoneNo());
            clientDef.setPinNo(trans.getClientPin());
            clientDef.setIdNo(trans.getClientIDNo());
            clientDef.setDateregistered(new Date());
            clientDef.setDateCreated(new Date());
            clientDef.setAuthBy(userUtils.getCurrentUser());
            clientDef.setAuthStatus("Y");
            clientDef.setCreatedBy(userUtils.getCurrentUser());
            clientDef.setTenantType(normalizedClientType);
            clientDef.setRegisteredbrn(trans.getBranch());
            clientDef.setStatus("A");
            clientDef.setTenantNumber(clientNumber);
            clientDef.setDob(trans.getClientDOB());
            clientDef.setClientCIF(trans.getClientCIF());
            ClientDef savedClient = clientRepository.save(clientDef);
            policy.setClient(savedClient);
        }
        String normalizedUnderwriter = trans.getUnderwriter().trim();
        final AccountDef underwriter = accountRepo.findByNormalizedName(normalizedUnderwriter);
        System.out.println("underwriter:: "+underwriter.getShtDesc());
        if (underwriter != null) {
            policy.setAgent(underwriter);
        } else {
            throw new BadRequestException(trans.getUnderwriter() + " Insurance Company is not set up. Please set up the Underwriter to process this transaction");
        }
        String normalizedPrdName = trans.getProductName().trim().toLowerCase().replace(" ", "");
        final ProductsDef product = productsRepo.findByNormalizedPName(normalizedPrdName);
        if (product != null) {
            policy.setProduct(product);
            String businessType = product.getProGroup().getPrgType();
            if(!businessType.equalsIgnoreCase("L")){
                policy.setBusinessType("N");
            } else {
                policy.setBusinessType("L");
            }
        } else {
            throw new BadRequestException(trans.getProductName() + " product is not found. Please set up the product.");
        }
        final String currToSearch = trans.getCurrency().trim();
        final Currencies currency = currencyRepository.findByPinNoIgnoreCase(currToSearch);
        if (currency != null) {
            policy.setTransCurrency(currency);
        } else {
            throw new BadRequestException(trans.getCurrency() + " currency is not set up. Please add the currency to the system.");
        }
        UserBranches userBranches = userBranchesRepository.findByUser(userUtils.getCurrentUser());
        Long branchId = userBranches.getBranch().getObId();
        final OrgBranch branches = orgBranchRepository.findOne(QOrgBranch.orgBranch.obId.eq(branchId));
        policy.setBranch(branches);
        policy.setCreatedUser(userUtils.getCurrentUser());
        policy.setPreviousTrans(policy);
        BindersDef binder = bindersRepo.findBinderByAccId(underwriter.getAcctId(), product.getProCode());
        policy.setBinder(binder);

        if (trans.getTransProcessed().equalsIgnoreCase("N")) {

            String endorsementFormat = paramService.getParameterString("ENDORSE_NO_FORMAT");
            Predicate endorsePredicate = QSystemSequence.systemSequence.transType.eq("E");
            if (sequenceRepo.count(endorsePredicate) == 0)
                throw new BadRequestException("Sequence for Endorsement Transactions has not been defined");
            SystemSequence endorseSequence = sequenceRepo.findOne(endorsePredicate);
            Long endosseqNumber = endorseSequence.getNextNumber();
            final String revNumber = endorseSequence.getSeqPrefix() + String.format("%05d", endosseqNumber);
            final String endorseNumber = templateMerger.generateFormat(endorsementFormat, branches.getObId(), product.getProCode(), trans.getCoverDateTo(), revNumber, null);
            policy.setPolRevNo(endorseNumber + "/1");
            policy.setRevisionFormat(endorseNumber);
            endorseSequence.setLastNumber(endosseqNumber);
            endorseSequence.setNextNumber(endosseqNumber + 1);
            sequenceRepo.save(endorseSequence);

        }
        policy.setRenewable(product.isRenewable());
        policy.setTransType("LD");
        policy.setPolRevStatus("LD");
        PolicyTrans savedPol= policyTransRepo.save(policy);
        trans.setPolicyTrans(savedPol);
        trans.setTransProcessed("Y");
        trans.setProcessedBy(userUtils.getCurrentUser());
        trans.setDateProcessed(new Date());
        transProcessingRepository.save(trans);

        final String insuredType = transRisks.getInsuredType().trim();
        final ClientTypes normalizedInsuredType = clientTypeRepo.findByNormalizeType(insuredType);
        final String insuredPinSerach = transRisks.getInsuredPin().trim();
        final ClientDef insured = clientRepository.findByPinNoIgnoreCase(insuredPinSerach);
        RiskTrans riskTrans = new RiskTrans();
        if (insured != null) {
            riskTrans.setInsured(insured);
        } else {
            final ClientDef riskInsured = new ClientDef();
            Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("C");
            if (sequenceRepo.count(seqPredicate) == 0)
                throw new BadRequestException("Sequence for Client Definition has not been setup");
            SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
            Long seqNumber = sequence.getNextNumber();
            final String clientNumber = sequence.getSeqPrefix() + String.format("%06d", seqNumber);
            sequence.setLastNumber(seqNumber);
            sequence.setNextNumber(seqNumber + 1);
            sequenceRepo.save(sequence);

            riskInsured.setFname(transRisks.getInsuredFName());
            riskInsured.setOtherNames(transRisks.getInsuredOtherNames());
            riskInsured.setEmailAddress(transRisks.getInsuredEmail());
            riskInsured.setPhoneNo(transRisks.getInsuredPhoneNo());
            riskInsured.setPinNo(transRisks.getInsuredPin());
            riskInsured.setIdNo(transRisks.getInsuredIdNo());
            riskInsured.setDateregistered(new Date());
            riskInsured.setDateCreated(new Date());
            riskInsured.setAuthBy(userUtils.getCurrentUser());
            riskInsured.setAuthStatus("Y");
            riskInsured.setCreatedBy(userUtils.getCurrentUser());
            riskInsured.setTenantType(normalizedInsuredType);
            riskInsured.setRegisteredbrn(trans.getBranch());
            riskInsured.setStatus("A");
            riskInsured.setTenantNumber(clientNumber);
            riskInsured.setDob(transRisks.getInsuredDOB());
            riskInsured.setClientCIF(transRisks.getInsuredCIF());
            ClientDef savedInsured = clientRepository.save(riskInsured);
            riskTrans.setInsured(savedInsured);
        }
        System.out.println("newBinder A:: "+underwriter.getAcctId());
        System.out.println("newBinder B:: "+product.getProCode());
        BindersDef newBinder = bindersRepo.findBinderByAccId(underwriter.getAcctId(), product.getProCode());
        System.out.println("newBinder:: "+newBinder.getBinId());
        riskTrans.setBinder(newBinder);
        String normalizedSubClass = trans.getProductName().trim().toLowerCase().replace(" ", "");
        String normalizedCoverType = trans.getCoverType().trim().toLowerCase().replace(" ", "");
        final SubClassDef subClassDef = subClassRepo.findBySubClassName(normalizedSubClass);
        final CoverTypesDef coverTypesDef = coverTypesRepo.findCoverTypesByBindId(newBinder.getBinId(),normalizedCoverType);
        System.out.println("coverTypesDef:: "+coverTypesDef.getCovName());
        BinderDetails newDetails = binderDetRepo.findOne(QBinderDetails.binderDetails.binder.binId.eq(newBinder.getBinId())
                .and(QBinderDetails.binderDetails.subCoverTypes.coverTypes.covId.eq(coverTypesDef.getCovId())));
        System.out.println("newDetails:: "+newDetails.getBinder());
        riskTrans.setSubclass(subClassDef);
        riskTrans.setCovertype(coverTypesDef);
        riskTrans.setRiskShtDesc(transRisks.getRiskShtDesc());
        riskTrans.setRiskDesc(transRisks.getRiskDesc());
        riskTrans.setPolicy(policy);
        riskTrans.setBinderDetails(newDetails);
        riskTrans.setWefDate(trans.getCoverDateFrom());
        riskTrans.setWetDate(trans.getCoverDateTo());
        riskTrans.setCalcPremium(trans.getPremium());
        riskTrans.setCommAmt(trans.getBankComm().negate());
        riskTrans.setExtras(trans.getExtras());
        riskTrans.setPhfFund(trans.getPhFund());
        riskTrans.setTransType("LD");
        riskTrans.setTrainingLevy(trans.getTrainingLevy());
        riskTrans.setSumInsured(trans.getSumInsured());
        riskTrans.setStampDuty(trans.getStampDuty());
        riskTrans.setPremium(trans.getPremium());
        String businessType = product.getProGroup().getPrgType();
        if(!businessType.equalsIgnoreCase("L")){
            policy.setBusinessType("N");
            riskTrans.setInstallmentNo(1l);
        } else {
            policy.setBusinessType("L");
            riskTrans.setInstallmentNo(trans.getPaidInsts());
        }
        if(trans.getMarketerABNo() != null){
            riskTrans.setMarketerAgentComm(trans.getAgentComm());
        }
        if (trans.getSubAgnetABNo() != null){
            riskTrans.setSubAgentComm(trans.getAgentComm());
        }

        BigDecimal commissionRates = commRatesRepo.findOne(QCommissionRates.commissionRates.bindersDef.binId.eq(newBinder.getBinId())).getCommRate();
        AccountDef accountDef = accountRepo.findOne(QAccountDef.accountDef.accountType.accountType.eq(AccountTypeEnum.INS)
                .and(QAccountDef.accountDef.acctId.eq(savedPol.getAgent().getAcctId())));
        BigDecimal accountCommRates = accountDef.getAccountType().getCommRate();
        if(commissionRates!=null || commissionRates != BigDecimal.ZERO){
            System.out.println("CommissionRates:: "+commissionRates);
            riskTrans.setCommRate(commissionRates);
        } else if (accountCommRates !=null || accountCommRates != BigDecimal.ZERO) {
            System.out.println("accountCommRates::" + accountCommRates);
            riskTrans.setCommRate(accountCommRates);
        } else {
            throw new BadRequestException("Commission Rates are not setup");
        }
        BigDecimal sumRisktaxAmount = trans.getStampDuty().add(trans.getTrainingLevy()).add(trans.getWhtx()).add(trans.getPhFund());
        riskTrans.setNetpremium(trans.getPremium().add(sumRisktaxAmount).subtract(trans.getBankComm().negate()));
        RiskTrans savedRisk = riskTransRepo.save(riskTrans);
        long riskIdentifier = Long.valueOf(String.valueOf(dateUtils.getUwYear(policy.getWefDate())) + String.valueOf(savedRisk.getRiskId()));
        savedRisk.setRiskIdentifier(riskIdentifier);
        riskTransRepo.save(savedRisk);

        List<SectionTrans> sectionTransactions = new ArrayList<>();
        List<TransProcessPremItems> riskSections = transProcessPremItemRepo.findAllByTransProcessing(trans);
        riskSections.stream().filter(a -> a.getPremSerialNo().equals(trans.getSerialNo())).forEach(a -> {
            long count = setupSectionRepo.count(QSectionsDef.sectionsDef.desc.eq(a.getPremSections().trim()));
            if (count == 1) {
                SectionsDef sectiondef = setupSectionRepo.findOne(QSectionsDef.sectionsDef.desc.eq(a.getPremSections().trim()));
                SectionTrans section = new SectionTrans();
                List<PremRatesDef> premRates = premRatesRepo.getSectPremiumRates(savedRisk.getBinderDetails().getDetId(), sectiondef.getId());
                if (premRates.size() == 1) {
                    section.setPremRates(premRates.get(0));
                    section.setRate(premRates.get(0).getRate());
                    section.setAmount((a.getPremLimit() != null) ? a.getPremLimit() : BigDecimal.ZERO);
                    section.setCompute(true);
                    section.setDivFactor(premRates.get(0).getDivFactor());
                    section.setFreeLimit(premRates.get(0).getFreeLimit());
                    section.setSection(sectiondef);
                    section.setRisk(savedRisk);
                    sectionTransactions.add(section);
                }

            }
        });
        sectionRepo.save(sectionTransactions);

        return "Transaction Processed successfully";
    }

    @Override
    @Transactional(readOnly = false)
    public String bulkProcessTrans(List<Long> transactionIds) throws BadRequestException {
        if (transactionIds.size() <= 0){
            throw new BadRequestException("Select At least One Transaction To Process");
        }
        for (Long transId : transactionIds) {
            TransactionProcessing trans = transProcessingRepository.findOne(transId);
            TransProcessRisks transRisks = transProcessRisksRepo.findOne(QTransProcessRisks.transProcessRisks.transProcessing.transProcessingId.eq(transId));
            TransProcessPremItems transPremItems = transProcessPremItemRepo.findOne(QTransProcessPremItems.transProcessPremItems.transProcessing.transProcessingId.eq(transId)
                    .and(QTransProcessPremItems.transProcessPremItems.transProcessing.transProcessingId.eq(transId)));
            if (trans.getTransProcessed().equalsIgnoreCase("Y")){
                throw new BadRequestException("This transaction is already processed");
            }
            final PolicyTrans policy = new PolicyTrans();
            policy.setWefDate(trans.getCoverDateFrom());
            policy.setWetDate(trans.getCoverDateTo());
            policy.setPolCreateddt(trans.getInceptionDate());
            policy.setPolNo(trans.getPolNumber());
            policy.setAuthStatus("PL");
            policy.setBasicPrem(trans.getGrossPremium());
            policy.setCommAmt(trans.getBankComm().negate());
            policy.setEndosbasicPremium(trans.getPremium());
            policy.setEndosCommissions(trans.getAgentComm().negate());
            policy.setSumInsured(trans.getSumInsured());
            policy.setUwYear(dateUtils.getUwYear(policy.getWefDate()));
            policy.setCurrentStatus("PL");
            policy.setCoverFrom(trans.getCoverDateFrom());
            policy.setCoverTo(trans.getCoverDateTo());
            policy.setEndosgrossPremium(trans.getGrossPremium());
            policy.setRenewalDate(trans.getRenewDate());
            policy.setClientPolNo(trans.getPolNumber());
            policy.setWhtx(trans.getWhtx());
            policy.setPolTotWhtx(trans.getWhtx());
            policy.setPhcf(trans.getPhFund());
            policy.setTotPhcf(trans.getPhFund());
            policy.setStampDuty(trans.getStampDuty());
            policy.setExtras(trans.getExtras());
            policy.setPolTotExtras(trans.getExtras());
            policy.setPremium(trans.getPremium());
            policy.setTrainingLevy(trans.getTrainingLevy());
            policy.setTotTrainingLevy(trans.getTrainingLevy());
            policy.setPolTotComm(trans.getBankComm().negate());
            policy.setPolTotPrem(trans.getPremium());
            policy.setFuturePrem(trans.getGrossPremium());
            policy.setNetPrem(trans.getNetPremium());
            policy.setPolTotSI(trans.getSumInsured());
            if(trans.getPaidPremium() != null){
                policy.setPaidPremium(trans.getPaidPremium());
                policy.setOutstandingPremium(trans.getBalance());
                policy.setPaidPremPct(trans.getPaidPremium().divide(trans.getGrossPremium(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP));
            }
            policy.setFrequency(trans.getFrequency());
            if(trans.getBankComm() != BigDecimal.ZERO){
                policy.setCommAllowed(true);
            }
            if(trans.getMarketerABNo() != null){
                AccountDef marketer = accountRepo.findByMarketerABNo(trans.getMarketerABNo().trim());
                policy.setMarketerAgent(marketer);
                policy.setMarketerAgentComm(trans.getAgentComm());
                policy.setAbsaNoMarketer(trans.getMarketerABNo());
            }
            if (trans.getSubAgnetABNo() != null){
                AccountDef subAgent = accountRepo.findBySubAgentABNo(trans.getSubAgnetABNo().trim());
                policy.setSubAgent(subAgent);
                policy.setSubAgentComm(trans.getAgentComm());
                policy.setAbsaNoSubAgent(trans.getSubAgnetABNo());
            }
            final String pinToSearch = trans.getClientPin().trim();
            final ClientDef client = clientRepository.findByPinNoIgnoreCase(pinToSearch);
            final String clientType = trans.getClientType().trim();
            final ClientTypes normalizedClientType = clientTypeRepo.findByNormalizeType(clientType);
            if (client != null) {
                policy.setClient(client);
            } else {
                final ClientDef clientDef = new ClientDef();
                Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("C");
                if (sequenceRepo.count(seqPredicate) == 0)
                    throw new BadRequestException("Sequence for Client Definition has not been setup");
                SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
                Long seqNumber = sequence.getNextNumber();
                final String clientNumber = sequence.getSeqPrefix() + String.format("%06d", seqNumber);
                sequence.setLastNumber(seqNumber);
                sequence.setNextNumber(seqNumber + 1);
                sequenceRepo.save(sequence);
                clientDef.setFname(trans.getfClientFname());
                clientDef.setOtherNames(trans.getClientOtherNames());
                clientDef.setEmailAddress(trans.getClientEmail());
                clientDef.setPhoneNo(trans.getClientPhoneNo());
                clientDef.setPinNo(trans.getClientPin());
                clientDef.setIdNo(trans.getClientIDNo());
                clientDef.setDateregistered(new Date());
                clientDef.setDateCreated(new Date());
                clientDef.setAuthBy(userUtils.getCurrentUser());
                clientDef.setAuthStatus("Y");
                clientDef.setCreatedBy(userUtils.getCurrentUser());
                clientDef.setTenantType(normalizedClientType);
                clientDef.setRegisteredbrn(trans.getBranch());
                clientDef.setStatus("A");
                clientDef.setTenantNumber(clientNumber);
                clientDef.setDob(trans.getClientDOB());
                clientDef.setClientCIF(trans.getClientCIF());
                ClientDef savedClient = clientRepository.save(clientDef);
                policy.setClient(savedClient);
            }
            String normalizedUnderwriter = trans.getUnderwriter().trim();
            final AccountDef underwriter = accountRepo.findByNormalizedName(normalizedUnderwriter);
            System.out.println("underwriter:: "+underwriter.getShtDesc());
            if (underwriter != null) {
                policy.setAgent(underwriter);
            } else {
                throw new BadRequestException(trans.getUnderwriter() + " Insurance Company is not set up. Please set up the Underwriter to process this transaction");
            }
            String normalizedPrdName = trans.getProductName().trim().toLowerCase().replace(" ", "");
            final ProductsDef product = productsRepo.findByNormalizedPName(normalizedPrdName);
            if (product != null) {
                policy.setProduct(product);
                String businessType = product.getProGroup().getPrgType();
                if(!businessType.equalsIgnoreCase("L")){
                    policy.setBusinessType("N");
                } else {
                    policy.setBusinessType("L");
                }
            } else {
                throw new BadRequestException(trans.getProductName() + " product is not found. Please set up the product.");
            }
            final String currToSearch = trans.getCurrency().trim();
            final Currencies currency = currencyRepository.findByPinNoIgnoreCase(currToSearch);
            if (currency != null) {
                policy.setTransCurrency(currency);
            } else {
                throw new BadRequestException(trans.getCurrency() + " currency is not set up. Please add the currency to the system.");
            }
            UserBranches userBranches = userBranchesRepository.findByUser(userUtils.getCurrentUser());
            Long branchId = userBranches.getBranch().getObId();
            final OrgBranch branches = orgBranchRepository.findOne(QOrgBranch.orgBranch.obId.eq(branchId));
            policy.setBranch(branches);
            policy.setCreatedUser(userUtils.getCurrentUser());
            policy.setPreviousTrans(policy);
            BindersDef polBinder = bindersRepo.findBinderByAccId(underwriter.getAcctId(), product.getProCode());
            policy.setBinder(polBinder);

            if (trans.getTransProcessed().equalsIgnoreCase("N")) {

                String endorsementFormat = paramService.getParameterString("ENDORSE_NO_FORMAT");
                Predicate endorsePredicate = QSystemSequence.systemSequence.transType.eq("E");
                if (sequenceRepo.count(endorsePredicate) == 0)
                    throw new BadRequestException("Sequence for Endorsement Transactions has not been defined");
                SystemSequence endorseSequence = sequenceRepo.findOne(endorsePredicate);
                Long endosseqNumber = endorseSequence.getNextNumber();
                final String revNumber = endorseSequence.getSeqPrefix() + String.format("%05d", endosseqNumber);
                final String endorseNumber = templateMerger.generateFormat(endorsementFormat, branches.getObId(), product.getProCode(), trans.getCoverDateTo(), revNumber, null);
                policy.setPolRevNo(endorseNumber + "/1");
                policy.setRevisionFormat(endorseNumber);
                endorseSequence.setLastNumber(endosseqNumber);
                endorseSequence.setNextNumber(endosseqNumber + 1);
                sequenceRepo.save(endorseSequence);

            }
            policy.setRenewable(product.isRenewable());
            policy.setTransType("LD");
            policy.setPolRevStatus("LD");
            PolicyTrans savedPol= policyTransRepo.save(policy);
            trans.setPolicyTrans(savedPol);
            trans.setTransProcessed("Y");
            trans.setProcessedBy(userUtils.getCurrentUser());
            trans.setDateProcessed(new Date());
            transProcessingRepository.save(trans);

            final String insuredType = transRisks.getInsuredType().trim();
            final ClientTypes normalizedInsuredType = clientTypeRepo.findByNormalizeType(insuredType);
            final String insuredPinSerach = transRisks.getInsuredPin().trim();
            final ClientDef insured = clientRepository.findByPinNoIgnoreCase(insuredPinSerach);
            RiskTrans riskTrans = new RiskTrans();
            if (insured != null) {
                riskTrans.setInsured(insured);
            } else {
                final ClientDef riskInsured = new ClientDef();
                Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("C");
                if (sequenceRepo.count(seqPredicate) == 0)
                    throw new BadRequestException("Sequence for Client Definition has not been setup");
                SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
                Long seqNumber = sequence.getNextNumber();
                final String clientNumber = sequence.getSeqPrefix() + String.format("%06d", seqNumber);
                sequence.setLastNumber(seqNumber);
                sequence.setNextNumber(seqNumber + 1);
                sequenceRepo.save(sequence);

                riskInsured.setFname(transRisks.getInsuredFName());
                riskInsured.setOtherNames(transRisks.getInsuredOtherNames());
                riskInsured.setEmailAddress(transRisks.getInsuredEmail());
                riskInsured.setPhoneNo(transRisks.getInsuredPhoneNo());
                riskInsured.setPinNo(transRisks.getInsuredPin());
                riskInsured.setIdNo(transRisks.getInsuredIdNo());
                riskInsured.setDateregistered(new Date());
                riskInsured.setDateCreated(new Date());
                riskInsured.setAuthBy(userUtils.getCurrentUser());
                riskInsured.setAuthStatus("Y");
                riskInsured.setCreatedBy(userUtils.getCurrentUser());
                riskInsured.setTenantType(normalizedInsuredType);
                riskInsured.setRegisteredbrn(trans.getBranch());
                riskInsured.setStatus("A");
                riskInsured.setTenantNumber(clientNumber);
                riskInsured.setDob(transRisks.getInsuredDOB());
                riskInsured.setClientCIF(transRisks.getInsuredCIF());
                ClientDef savedInsured = clientRepository.save(riskInsured);
                riskTrans.setInsured(savedInsured);
            }
            System.out.println("newBinder A:: "+underwriter.getAcctId());
            System.out.println("newBinder B:: "+product.getProCode());
            BindersDef newBinder = bindersRepo.findBinderByAccId(underwriter.getAcctId(), product.getProCode());
            System.out.println("newBinder:: "+newBinder.getBinId());
            riskTrans.setBinder(newBinder);
            String normalizedSubClass = trans.getProductName().trim().toLowerCase().replace(" ", "");
            String normalizedCoverType = trans.getCoverType().trim().toLowerCase().replace(" ", "");
            final SubClassDef subClassDef = subClassRepo.findBySubClassName(normalizedSubClass);
//            final CoverTypesDef coverTypesDef = coverTypesRepo.findByCoverType(normalizedCoverType);
            System.out.println("normalizedCoverType:: "+normalizedCoverType);
            final CoverTypesDef coverTypesDef = coverTypesRepo.findCoverTypesByBindId(newBinder.getBinId(),normalizedCoverType);
            System.out.println("coverTypesDefs:: "+coverTypesDef.getCovName());
            BinderDetails newDetails = binderDetRepo.findOne(QBinderDetails.binderDetails.binder.binId.eq(newBinder.getBinId()).and(QBinderDetails.binderDetails.subCoverTypes.coverTypes.covId.eq(coverTypesDef.getCovId())));
            System.out.println("newDetails:: "+newDetails.getBinder());
            riskTrans.setSubclass(subClassDef);
            riskTrans.setCovertype(coverTypesDef);
            riskTrans.setRiskShtDesc(transRisks.getRiskShtDesc());
            riskTrans.setRiskDesc(transRisks.getRiskDesc());
            riskTrans.setPolicy(policy);
            riskTrans.setBinderDetails(newDetails);
            riskTrans.setWefDate(trans.getCoverDateFrom());
            riskTrans.setWetDate(trans.getCoverDateTo());
            riskTrans.setCalcPremium(trans.getPremium());
            riskTrans.setCommAmt(trans.getBankComm().negate());
            riskTrans.setExtras(trans.getExtras());
            riskTrans.setPhfFund(trans.getPhFund());
            riskTrans.setTransType("LD");
            riskTrans.setTrainingLevy(trans.getTrainingLevy());
            riskTrans.setSumInsured(trans.getSumInsured());
            riskTrans.setStampDuty(trans.getStampDuty());
            riskTrans.setPremium(trans.getPremium());
            String businessType = product.getProGroup().getPrgType();
            if(!businessType.equalsIgnoreCase("L")){
                policy.setBusinessType("N");
                riskTrans.setInstallmentNo(1l);
            } else {
                policy.setBusinessType("L");
                riskTrans.setInstallmentNo(trans.getPaidInsts());
            }
            if(trans.getMarketerABNo() != null){
                riskTrans.setMarketerAgentComm(trans.getAgentComm());
            }
            if (trans.getSubAgnetABNo() != null){
                riskTrans.setSubAgentComm(trans.getAgentComm());
            }

            BigDecimal commissionRates = commRatesRepo.findOne(QCommissionRates.commissionRates.bindersDef.binId.eq(newBinder.getBinId())).getCommRate();
            AccountDef accountDef = accountRepo.findOne(QAccountDef.accountDef.accountType.accountType.eq(AccountTypeEnum.INS)
                    .and(QAccountDef.accountDef.acctId.eq(savedPol.getAgent().getAcctId())));
            BigDecimal accountCommRates = accountDef.getAccountType().getCommRate();
            if(commissionRates!=null || commissionRates != BigDecimal.ZERO){
                System.out.println("CommissionRates:: "+commissionRates);
                riskTrans.setCommRate(commissionRates);
            } else if (accountCommRates !=null || accountCommRates != BigDecimal.ZERO) {
                System.out.println("accountCommRates::" + accountCommRates);
                riskTrans.setCommRate(accountCommRates);
            } else {
                throw new BadRequestException("Commission Rates are not setup");
            }
            BigDecimal sumRisktaxAmount = trans.getStampDuty().add(trans.getTrainingLevy()).add(trans.getWhtx()).add(trans.getPhFund());
            riskTrans.setNetpremium(trans.getPremium().add(sumRisktaxAmount).subtract(trans.getBankComm().negate()));
            RiskTrans savedRisk = riskTransRepo.save(riskTrans);
            long riskIdentifier = Long.valueOf(String.valueOf(dateUtils.getUwYear(policy.getWefDate())) + String.valueOf(savedRisk.getRiskId()));
            savedRisk.setRiskIdentifier(riskIdentifier);
            riskTransRepo.save(savedRisk);

            List<SectionTrans> sectionTransactions = new ArrayList<>();
            List<TransProcessPremItems> riskSections = transProcessPremItemRepo.findAllByTransProcessing(trans);
            riskSections.stream().filter(a -> a.getPremSerialNo().equals(trans.getSerialNo())).forEach(a -> {
                long count = setupSectionRepo.count(QSectionsDef.sectionsDef.desc.eq(a.getPremSections().trim()));
                if (count == 1) {
                    SectionsDef sectiondef = setupSectionRepo.findOne(QSectionsDef.sectionsDef.desc.eq(a.getPremSections().trim()));
                    SectionTrans section = new SectionTrans();
                    List<PremRatesDef> premRates = premRatesRepo.getSectPremiumRates(savedRisk.getBinderDetails().getDetId(), sectiondef.getId());
                    if (premRates.size() == 1) {
                        section.setPremRates(premRates.get(0));
                        section.setRate(premRates.get(0).getRate());
                        section.setAmount((a.getPremLimit() != null) ? a.getPremLimit() : BigDecimal.ZERO);
                        section.setCompute(true);
                        section.setDivFactor(premRates.get(0).getDivFactor());
                        section.setFreeLimit(premRates.get(0).getFreeLimit());
                        section.setSection(sectiondef);
                        section.setRisk(savedRisk);
                        sectionTransactions.add(section);
                    }

                }
            });
            sectionRepo.save(sectionTransactions);
        }
        return "Transactions Processed Successfully";
    }

    private BigDecimal sign(String type) {
        return ("C".equalsIgnoreCase(type) ? BigDecimal.ONE.multiply(BigDecimal.valueOf(-1)) : BigDecimal.ONE.multiply(BigDecimal.valueOf(1)));
    }

    @Override
    public String approveSingleLoadedPolicy(Long policyId) throws BadRequestException {
        final OrgBranch headOffice = orgBranchRepository.findHeadOfficeID();
        PolicyTrans policyTrans = policyTransRepo.findOne(policyId);
        if(policyTrans.getCurrentStatus().equalsIgnoreCase("LD")){
            throw new BadRequestException("This transaction is already approved");
        }

        String refNo = null;
        String debitCode = null;
        BigDecimal prems = (policyTrans.getPremium() == null) ? BigDecimal.ZERO : policyTrans.getPremium();
        Predicate pedSystem = QSystemSequence.systemSequence.transType.eq("D");
        if (sequenceRepo.count(pedSystem) == 0)
            throw new BadRequestException("Sequence for Debit Notes has not been defined");
        SystemSequence sequenceSystem = sequenceRepo.findOne(pedSystem);
        Long sequenceNumber = sequenceSystem.getNextNumber();
        String transType = policyTrans.getTransType();
        if ("CO".equalsIgnoreCase(policyTrans.getTransType())) {
            transType = policyTrans.getPreviousTrans().getTransType();
        }
        if ("NB".equalsIgnoreCase(transType)) {
            transType = "NB";
        } else if ("RN".equalsIgnoreCase(transType)) {
            transType = "RN";
        } else if("LD".equalsIgnoreCase(transType)) {
            transType = "LD";
        }
        else {
            transType = "EN";
        }
        if (transMappingRepo.count(QTransactionMapping.transactionMapping.transType.eq(transType)) == 0)
            throw new BadRequestException("Error getting Transaction Mapping Setups..Contact System Administrator");
        TransactionMapping mapping = transMappingRepo.findOne(QTransactionMapping.transactionMapping.transType.eq(transType));
        refNo = ((prems.compareTo(BigDecimal.ZERO) >= 0) ? mapping.getDebitCode() : mapping.getCreditCode()) + String.format("%05d", sequenceNumber);
        debitCode = ((prems.compareTo(BigDecimal.ZERO) >= 0) ? mapping.getDebitCode() : mapping.getCreditCode());
        sequenceSystem.setLastNumber(sequenceNumber);
        sequenceSystem.setNextNumber(sequenceNumber + 1);
        sequenceRepo.save(sequenceSystem);

        boolean newAuthorization = false;
        SystemTrans transaction = null;
        SystemTransactions trans = null;
        SystemTransactions atrans = null;
        if(policyTrans.getCurrentStatus().equalsIgnoreCase("PL")) {
            final TransactionProcessing transactionProcessing =transProcessingRepository.findOne(QTransactionProcessing.transactionProcessing.policyTrans.policyId.eq(policyTrans.getPolicyId()));
            newAuthorization = true;
            transaction = new SystemTrans();
            transaction.setDoneDate(new Date());
            transaction.setDoneBy(userUtils.getCurrentUser());
            transaction.setPolicy(policyTrans);
            transaction.setTransLevel("U");
            transaction.setTransCode("LD");
            transaction.setTransAuthorised("Y");
            transaction.setAuthBy(userUtils.getCurrentUser());
            transaction.setAuthDate(new Date());

            BigDecimal basicPrem = (policyTrans.getPremium() == null) ? BigDecimal.ZERO : policyTrans.getPremium();
            BigDecimal extras = (policyTrans.getExtras() == null) ? BigDecimal.ZERO : policyTrans.getExtras();
            BigDecimal phcf = (policyTrans.getPhcf() == null) ? BigDecimal.ZERO : policyTrans.getPhcf();
            BigDecimal tl = (policyTrans.getTrainingLevy() == null) ? BigDecimal.ZERO : policyTrans.getTrainingLevy();
            BigDecimal sd = (policyTrans.getStampDuty() == null) ? BigDecimal.ZERO : policyTrans.getStampDuty();
            BigDecimal amountWithTaxes = basicPrem.add(extras).add(phcf).add(tl).add(sd);
            String type = (amountWithTaxes.compareTo(BigDecimal.ZERO) == 1) ? "D" : "C";

            BigDecimal adminFeeAmt = BigDecimal.ZERO;
            if(policyTrans.getAdminFeeApplicable()!=null && "Y".equalsIgnoreCase(policyTrans.getAdminFeeApplicable())){
                if(policyTrans.getAdminFeeAmt()!=null && policyTrans.getAdminFeeVatAmt()!=null){
                    adminFeeAmt = policyTrans.getAdminFeeAmt().subtract(policyTrans.getAdminFeeVatAmt());
                }
                else if(policyTrans.getAdminFeeAmt()!=null && policyTrans.getAdminFeeVatAmt()==null){
                    adminFeeAmt = policyTrans.getAdminFeeAmt();
                }
            }

            final BigDecimal sign = BigDecimal.valueOf(-1);

            BigDecimal commamt = (policyTrans.getCommAmt() == null) ? BigDecimal.ZERO : policyTrans.getCommAmt();
            trans = new SystemTransactions();
            trans.setAmount(policyTrans.getBasicPrem().setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            trans.setAuthDate(new Date());
            trans.setAuthorised("Y");
            trans.setBalance(transactionProcessing.getBalance().setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            trans.setBranch(policyTrans.getBranch());
            trans.setClientType("C");
            trans.setControlAcc(policyTrans.getClient().getTenantNumber());
            trans.setClient(policyTrans.getClient());
            trans.setCurrRate(new BigDecimal(1));
            trans.setCurrency(policyTrans.getTransCurrency());
            trans.setNarrations("Posting client Debit Note");
            trans.setNetAmount(policyTrans.getBasicPrem().setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            trans.setAdminFeeNet(adminFeeAmt.setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            trans.setOrigin("LD");
            trans.setPhfund(policyTrans.getPhcf().setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            trans.setPolicy(policyTrans);
            trans.setRefNo(refNo);
            trans.setSd(policyTrans.getStampDuty().setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            trans.setTl(policyTrans.getTrainingLevy().setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            trans.setTransDate(new Date());
            trans.setTransdc("D");
            trans.setTransType(debitCode); //Should not be hardcorded
            trans.setUserAuth(userUtils.getCurrentUser().getUsername());
            trans.setWhtx(BigDecimal.ZERO);
            trans.setExtras(policyTrans.getExtras().setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            trans.setPostedDate(new Date());
            trans.setPostedUser(userUtils.getCurrentUser());
            trans.setTransType(debitCode);

            final BigDecimal agentSign = BigDecimal.valueOf(-1);
            final BigDecimal commSign = BigDecimal.valueOf(1);
            atrans = new SystemTransactions();
            atrans.setAmount(policyTrans.getBasicPrem().multiply(agentSign).setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            atrans.setAuthDate(new Date());
            atrans.setAuthorised("Y");
            atrans.setBalance(transactionProcessing.getBalance().multiply(agentSign).setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            atrans.setBranch(policyTrans.getBranch());
            atrans.setClientType("A");
            atrans.setControlAcc(policyTrans.getAgent().getShtDesc());
            atrans.setAgent(policyTrans.getAgent());
            atrans.setCommission(policyTrans.getCommAmt().abs().multiply(commSign).setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            atrans.setAdminFeeNet(adminFeeAmt.multiply(agentSign).setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            atrans.setCurrRate(new BigDecimal(1));
            atrans.setCurrency(policyTrans.getTransCurrency());
            atrans.setNarrations("Posting Agent Credit Note");
            atrans.setNetAmount(policyTrans.getNetPrem().multiply(agentSign).setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            atrans.setOrigin("LD");
            atrans.setPhfund(policyTrans.getPhcf().multiply(agentSign).setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            atrans.setPolicy(policyTrans);
            atrans.setRefNo(refNo);
            atrans.setSd(policyTrans.getStampDuty().multiply(agentSign).setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            atrans.setTl(policyTrans.getTrainingLevy().multiply(agentSign).setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            atrans.setTransDate(new Date());
            atrans.setTransdc("C");
            atrans.setTransType(debitCode); //Should not be hardcorded
            atrans.setUserAuth(userUtils.getCurrentUser().getUsername());
            atrans.setWhtx(policyTrans.getWhtx().multiply(agentSign).multiply(agentSign).setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            atrans.setExtras(policyTrans.getExtras().multiply(agentSign).setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            atrans.setPostedDate(new Date());
            atrans.setPostedUser(userUtils.getCurrentUser());
//            policyAuthorizationImpl.postUwTransactions(policyTrans,transaction,commamt );
        }
        if (newAuthorization){
            transRepo.save(transaction);
            trans.setTransaction(transaction);
            atrans.setTransaction(transaction);
            systemTransactionsRepo.save(trans);
            systemTransactionsRepo.save(atrans);
        }
        policyTrans.setAuthStatus("LD");
        policyTrans.setCurrentStatus("LD");
        policyTrans.setAuthDate(new Date());
        policyTrans.setAuthBy(userUtils.getCurrentUser());
        policyTrans.setRefNo(refNo);
        policyTransRepo.save(policyTrans);
        return "Transaction Authorized Successfully";
    }

    @Override
    public String approveBulkLoadedPolicy(List<Long> policyIds) throws BadRequestException {
        final OrgBranch headOffice = orgBranchRepository.findHeadOfficeID();
        if (policyIds.size() <= 0){
            throw new BadRequestException("Select At least One Transaction To Process");
        }
        for(Long policyId : policyIds){
            PolicyTrans policyTrans = policyTransRepo.findOne(policyId);
            if(policyTrans.getCurrentStatus().equalsIgnoreCase("LD")){
                throw new BadRequestException("This transaction is already processed");
            }
            String refNo = null;
            String debitCode = null;
            BigDecimal prems = (policyTrans.getPremium() == null) ? BigDecimal.ZERO : policyTrans.getPremium();
            Predicate pedSystem = QSystemSequence.systemSequence.transType.eq("D");
            if (sequenceRepo.count(pedSystem) == 0)
                throw new BadRequestException("Sequence for Debit Notes has not been defined");
            SystemSequence sequenceSystem = sequenceRepo.findOne(pedSystem);
            Long sequenceNumber = sequenceSystem.getNextNumber();
            String transType = policyTrans.getTransType();
            if ("CO".equalsIgnoreCase(policyTrans.getTransType())) {
                transType = policyTrans.getPreviousTrans().getTransType();
            }
            if ("NB".equalsIgnoreCase(transType)) {
                transType = "NB";
            } else if ("RN".equalsIgnoreCase(transType)) {
                transType = "RN";
            } else if("LD".equalsIgnoreCase(transType)) {
                transType = "LD";
            }
            else {
                transType = "EN";
            }
            if (transMappingRepo.count(QTransactionMapping.transactionMapping.transType.eq(transType)) == 0)
                throw new BadRequestException("Error getting Transaction Mapping Setups..Contact System Administrator");
            TransactionMapping mapping = transMappingRepo.findOne(QTransactionMapping.transactionMapping.transType.eq(transType));
            refNo = ((prems.compareTo(BigDecimal.ZERO) >= 0) ? mapping.getDebitCode() : mapping.getCreditCode()) + String.format("%05d", sequenceNumber);
            debitCode = ((prems.compareTo(BigDecimal.ZERO) >= 0) ? mapping.getDebitCode() : mapping.getCreditCode());
            sequenceSystem.setLastNumber(sequenceNumber);
            sequenceSystem.setNextNumber(sequenceNumber + 1);
            sequenceRepo.save(sequenceSystem);

            boolean newAuthorization = false;
            SystemTrans transaction = null;
            SystemTransactions trans = null;
            SystemTransactions atrans = null;
            if(policyTrans.getCurrentStatus().equalsIgnoreCase("PL")) {
                final TransactionProcessing transactionProcessing =transProcessingRepository.findOne(QTransactionProcessing.transactionProcessing.policyTrans.policyId.eq(policyTrans.getPolicyId()));
                newAuthorization = true;
                transaction = new SystemTrans();
                transaction.setDoneDate(new Date());
                transaction.setDoneBy(userUtils.getCurrentUser());
                transaction.setPolicy(policyTrans);
                transaction.setTransLevel("U");
                transaction.setTransCode("LD");
                transaction.setTransAuthorised("Y");
                transaction.setAuthBy(userUtils.getCurrentUser());
                transaction.setAuthDate(new Date());

                BigDecimal adminFeeAmt = BigDecimal.ZERO;
                if(policyTrans.getAdminFeeApplicable()!=null && "Y".equalsIgnoreCase(policyTrans.getAdminFeeApplicable())){
                    if(policyTrans.getAdminFeeAmt()!=null && policyTrans.getAdminFeeVatAmt()!=null){
                        adminFeeAmt = policyTrans.getAdminFeeAmt().subtract(policyTrans.getAdminFeeVatAmt());
                    }
                    else if(policyTrans.getAdminFeeAmt()!=null && policyTrans.getAdminFeeVatAmt()==null){
                        adminFeeAmt = policyTrans.getAdminFeeAmt();
                    }
                }
                final BigDecimal agentSign = BigDecimal.valueOf(-1);

                BigDecimal commamt = (policyTrans.getCommAmt() == null) ? BigDecimal.ZERO : policyTrans.getCommAmt();
                trans = new SystemTransactions();
                trans.setAmount(policyTrans.getBasicPrem().setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                trans.setAuthDate(new Date());
                trans.setAuthorised("Y");
                trans.setBalance(transactionProcessing.getBalance().setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                trans.setBranch(policyTrans.getBranch());
                trans.setClientType("C");
                trans.setControlAcc(policyTrans.getClient().getTenantNumber());
                trans.setClient(policyTrans.getClient());
                trans.setCurrRate(new BigDecimal(1));
                trans.setCurrency(policyTrans.getTransCurrency());
                trans.setNarrations("Posting client Debit Note");
                trans.setNetAmount(policyTrans.getBasicPrem().setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                trans.setAdminFeeNet(adminFeeAmt.setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                trans.setOrigin("LD");
                trans.setPhfund(policyTrans.getPhcf().setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                trans.setPolicy(policyTrans);
                trans.setRefNo(refNo);
                trans.setSd(policyTrans.getStampDuty().setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                trans.setTl(policyTrans.getTrainingLevy().setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                trans.setTransDate(new Date());
                trans.setTransdc("D");
                trans.setTransType(debitCode); //Should not be hardcorded
                trans.setUserAuth(userUtils.getCurrentUser().getUsername());
                trans.setWhtx(BigDecimal.ZERO);
                trans.setExtras(policyTrans.getExtras().setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                trans.setPostedDate(new Date());
                trans.setPostedUser(userUtils.getCurrentUser());
                trans.setTransType(debitCode);

                final BigDecimal commSign = BigDecimal.valueOf(1);

                atrans = new SystemTransactions();
                atrans.setAmount(policyTrans.getBasicPrem().multiply(agentSign).setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                atrans.setAuthDate(new Date());
                atrans.setAuthorised("Y");
                atrans.setBalance(transactionProcessing.getBalance().multiply(agentSign).setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                atrans.setBranch(policyTrans.getBranch());
                atrans.setClientType("A");
                atrans.setControlAcc(policyTrans.getAgent().getShtDesc());
                atrans.setAgent(policyTrans.getAgent());
                atrans.setCommission(policyTrans.getCommAmt().multiply(commSign).setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                atrans.setCurrRate(new BigDecimal(1));
                atrans.setCurrency(policyTrans.getTransCurrency());
                atrans.setNarrations("Posting Agent Credit Note");
                atrans.setNetAmount(policyTrans.getNetPrem().multiply(agentSign).setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                atrans.setOrigin("LD");
                atrans.setPhfund(policyTrans.getPhcf().multiply(agentSign).setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                atrans.setPolicy(policyTrans);
                atrans.setRefNo(refNo);
                atrans.setSd(policyTrans.getStampDuty().multiply(agentSign).setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                atrans.setTl(policyTrans.getTrainingLevy().multiply(agentSign).setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                atrans.setTransDate(new Date());
                atrans.setTransdc("C");
                atrans.setTransType(debitCode); //Should not be hardcorded
                atrans.setUserAuth(userUtils.getCurrentUser().getUsername());
                atrans.setWhtx(policyTrans.getWhtx().multiply(agentSign).setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                atrans.setExtras(policyTrans.getExtras().multiply(agentSign).setScale(policyTrans.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                atrans.setPostedDate(new Date());
                atrans.setPostedUser(userUtils.getCurrentUser());
//            policyAuthorizationImpl.postUwTransactions(policyTrans,transaction,commamt );
            }
            if (newAuthorization){
                transRepo.save(transaction);
                trans.setTransaction(transaction);
                atrans.setTransaction(transaction);
                systemTransactionsRepo.save(trans);
                systemTransactionsRepo.save(atrans);
            }
            policyTrans.setAuthStatus("LD");
            policyTrans.setCurrentStatus("LD");
            policyTrans.setAuthDate(new Date());
            policyTrans.setAuthBy(userUtils.getCurrentUser());
            policyTrans.setRefNo(refNo);
            policyTransRepo.save(policyTrans);
        }
        return "Transactions Authorized Successfully";
    }

    @Override
    @Transactional
    public String deleteBulkTransProcessing(List<Long> transactionIds) throws BadRequestException {
        if (transactionIds.size() <= 0){
            throw new BadRequestException("Select At least One Transaction To Process");
        }

        for (Long transId : transactionIds) {
            TransactionProcessing trans = transProcessingRepository.findOne(transId);
            TransProcessRisks transRisks = transProcessRisksRepo.findOne(QTransProcessRisks.transProcessRisks.transProcessing.transProcessingId.eq(transId));
            TransProcessPremItems transPremItems = transProcessPremItemRepo.findOne(QTransProcessPremItems.transProcessPremItems.transProcessing.transProcessingId.eq(transId)
                    .and(QTransProcessPremItems.transProcessPremItems.transProcessing.transProcessingId.eq(transId)));
            if (trans.getTransProcessed().equalsIgnoreCase("N")) {
                //delete prem items
                transProcessPremItemRepo.delete(transPremItems);
                //delete risks
                transProcessRisksRepo.delete(transRisks);
                //delete insurance
                transProcessingRepository.delete(trans);
            }
        }
        return "Deleted successfully.";
    }
}

