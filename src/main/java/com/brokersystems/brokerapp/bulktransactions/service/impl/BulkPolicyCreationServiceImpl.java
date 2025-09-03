package com.brokersystems.brokerapp.bulktransactions.service.impl;

import com.brokersystems.brokerapp.bulktransactions.ErrorsCache.ErrorWorkbookCache;
import com.brokersystems.brokerapp.bulktransactions.models.*;
import com.brokersystems.brokerapp.bulktransactions.utils.BulkBatchSaveService;
import com.brokersystems.brokerapp.bulktransactions.utils.ExcelReaderUtil;
import com.brokersystems.brokerapp.enums.AccountTypeEnum;
import com.brokersystems.brokerapp.enums.RevenueItems;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.*;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.*;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.brokersystems.brokerapp.trans.model.SystemTrans;
import com.brokersystems.brokerapp.trans.repository.SystemTransRepo;
import com.brokersystems.brokerapp.trans.repository.TransMappingRepo;
import com.brokersystems.brokerapp.bulktransactions.dtos.BulkPolicyCreationDTO;
import com.brokersystems.brokerapp.bulktransactions.repositories.BulkPolicyCreationRepo;
import com.brokersystems.brokerapp.bulktransactions.repositories.BulkPolicyRiskRepository;
import com.brokersystems.brokerapp.bulktransactions.service.BulkPolicyCreationService;
import com.brokersystems.brokerapp.trans.service.PolicyAuthorization;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.uw.repository.*;
import com.brokersystems.brokerapp.uw.service.PremComputeService;
import com.mysema.query.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BulkPolicyCreationServiceImpl implements BulkPolicyCreationService {

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private ValidatorUtils validator;

    @Autowired
    private UserBranchesRepository userBranchesRepository;

    @Autowired
    private OrgBranchRepository orgBranchRepository;

    @Autowired
    private PremComputeService premiumService;

    @Autowired
    private BulkPolicyCreationRepo bulkPolicyCreationRepo;
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ClientTypeRepo clientTypeRepo;
    @Autowired
    private SequenceRepository sequenceRepository;
    @Autowired
    private PolicyTransRepo policyTransRepo;
    @Autowired
    private CommRatesRepo commRatesRepo;
    @Autowired
    private RiskTransRepo riskTransRepo;
    @Autowired
    private DateUtilities dateUtilities;
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
    private TemplateMerger templateMerger;
    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private ProductsRepo productsRepo;
    @Autowired
    private BulkPolicyRiskRepository bulkPolicyRiskRepository;
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
    private TransMappingRepo transMappingRepo;
    @Autowired
    private UploadValidatorsUtils uploadValidatorsService;
    @Autowired
    private PolicyAuthorization authService;
    @Autowired
    private PremComputeService premComputeServiceImpl;
    @Autowired
    private PolTaxesRepo polTaxesRepo;
    @Autowired
    private TaxRatesRepo taxRatesRepo;
    @Autowired
    private PolActiveRisksRepo activeRisksRepo;
    @Autowired
    private ErrorWorkbookCache errorWorkbookCache;
    @Autowired
    private BulkBatchSaveService bulkBatchSaveService;

    @Override
    public Map<String, Object> uploadBulkPolicy(MultipartFile file) throws BadRequestException {

        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            throw new BadRequestException("Upload files with .xlsx or .xls extension only");
        }

        Map<String, Object> response = new HashMap<>();
        List<String> invalidRecords = new ArrayList<>();
        List<BulkPolicyCreation> bulkPolicyCreations = new ArrayList<>();
        List<BulkPolicyRisk> bulkPolicyRisks = new ArrayList<>();

        String batchId = UUID.randomUUID().toString();
        int batchSize = 1000;
        int totalSuccessCount = 0;
        int totalRiskSuccessCount = 0;
        int totalFailedRiskCount = 0;
        int totalFailedPolicyCount = 0;

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);

            // Validate template format
            List<String> validationErrors = ExcelReaderUtil.validateBulkPolicyTemplate(workbook);
            if (!validationErrors.isEmpty()) {
                StringBuilder errorMessage = new StringBuilder("Template validation failed: ");
                for (String error : validationErrors) {
                    errorMessage.append(error).append("; ");
                }
                throw new BadRequestException(errorMessage.toString());
            }

            Sheet sheetOne = workbook.getSheetAt(0);
            Map<String, BulkPolicyCreation> policyMap = new HashMap<>(); // Maps serialNo -> BulkPolicyCreation

            int startRowIndex = findDataStartRow(sheetOne);
            if (startRowIndex == -1) {
                throw new BadRequestException("Sheet One contains no data.");
            }
            int errorColumnIndex = sheetOne.getRow(startRowIndex).getLastCellNum();

            String bulkCode = null;
            Predicate pedSystem = QSystemSequence.systemSequence.transType.eq("BPU");
            if (sequenceRepository.count(pedSystem) == 0)
                throw new BadRequestException("Sequence for Bulk Policy Upload has not been defined");
            String transType = "BPU";

            for (int rowNum = startRowIndex; rowNum <= sheetOne.getLastRowNum(); rowNum++) {
                SystemSequence sequenceSystem = sequenceRepository.findOne(pedSystem);
                Long sequenceNumber = sequenceSystem.getNextNumber();
                bulkCode = transType + String.format("%05d", sequenceNumber);
                sequenceSystem.setLastNumber(sequenceNumber);
                sequenceSystem.setNextNumber(sequenceNumber + 1);
                sequenceRepository.save(sequenceSystem);

                Row row = sheetOne.getRow(rowNum);
                if (isBlankRow(row)) continue;

                try {
                    BulkPolicyCreation bulkPolicyCreation = new BulkPolicyCreation();
                    String serialNo = ExcelReaderUtil.getCellValue(row, 0); //getCellStringValue(row.getCell(0));
                    System.out.println("serialNo" + serialNo);
                    bulkPolicyCreation.setSerialNo(serialNo);

                    String clntIdNo = ExcelReaderUtil.getCellValue(row, 1); //getCellStringValue(row.getCell(3));
                    String cif = ExcelReaderUtil.getCellValue(row, 2); //getCellStringValue(row.getCell(4));
                    final ClientDef client = clientRepository.findByCifandIdNoIgnoreCase(cif,clntIdNo);
                    if (client != null) {
                        bulkPolicyCreation.setClientFname(client.getFname());
                        bulkPolicyCreation.setClientOtherNames(client.getOtherNames());
                        bulkPolicyCreation.setClientIdNo(client.getIdNo());
                        bulkPolicyCreation.setClientPin(client.getPinNo());
                        bulkPolicyCreation.setInsuredFName(client.getFname());
                        bulkPolicyCreation.setInsuredOtherNames(client.getOtherNames());
                        bulkPolicyCreation.setInsuredIdNo(client.getIdNo());
                        bulkPolicyCreation.setInsuredPin(client.getPinNo());
                        bulkPolicyCreation.setInsuredFName(client.getFname());
                        bulkPolicyCreation.setInsuredOtherNames(client.getOtherNames());
                        bulkPolicyCreation.setInsuredIdNo(client.getIdNo());
                        bulkPolicyCreation.setInsuredPin(client.getPinNo());
                        bulkPolicyCreation.setPhoneNumber(client.getPhoneNo());

                    } else {
                        throw new BadRequestException("Client with KRA pin " + cif + clntIdNo+ " is not onboarded in the system");
                    }
                    bulkPolicyCreation.setClientDef(client);
                    bulkPolicyCreation.setSalesAgent(ExcelReaderUtil.getCellValue(row, 3));
                    String subagentCode = ExcelReaderUtil.getCellValue(row, 4).trim();
                    if (!subagentCode.isEmpty()) {
                        System.out.println("subagent " + subagentCode);
                        AccountDef subAgent = accountRepo.findBulkSubAgentBySubAgentABNo(subagentCode);

                        if (subAgent == null) {
                            bulkPolicyCreation.setSalesCode(null);
                        } else {
                            System.out.println("subagent " + subAgent.getAcctId());
                            bulkPolicyCreation.setSalesCode(subAgent.getAcctId());
                            bulkPolicyCreation.setSalesAgentId(subAgent);
                        }
                    }


                    String insurerCode = ExcelReaderUtil.getCellValue(row, 5); //getCellStringValue(row.getCell(10));
                    AccountDef underWriter = accountRepo.findOne(QAccountDef.accountDef.shtDesc.eq(insurerCode.trim()));
                    if (underWriter != null) {
                        bulkPolicyCreation.setInsurerCode(underWriter);
                    } else {
                        throw new BadRequestException("Insurer with code " + insurerCode + " is not setup in the system, please contact system admin");
                    }
                    bulkPolicyCreation.setProductGroup(ExcelReaderUtil.getCellValue(row, 6)); //getCellStringValue(row.getCell(11)));
                    String productName = (ExcelReaderUtil.getCellValue(row, 7)); //getCellStringValue(row.getCell(12)));
                    String normalizedProductName = productName.trim().toLowerCase().replace(" ", "");
                    ProductsDef product = productsRepo.findByNormalizedPName(normalizedProductName);
                    if (product == null) {
                        throw new BadRequestException("Product " + productName + " not set in the system, please contact system admin.");
                    }
                    bulkPolicyCreation.setProductDefId(product);
                    String contract = (ExcelReaderUtil.getCellValue(row, 8));
                    contract = contract.replaceAll("\\s+", " ").trim();
                    bulkPolicyCreation.setContractName(contract);
                    if(contract.isEmpty()){
                        throw new BadRequestException("Please fill Contract is missing for "+productName + "underwriter "+underWriter.getName());
                    }
                    System.out.println("binder info "+ product.getProCode() + " binder"+ bulkPolicyCreation.getInsurerCode().getAcctId());
                    BindersDef binder = bindersRepo.findBinderByAccIdProDCodeActive(bulkPolicyCreation.getInsurerCode().getAcctId(), product.getProCode(), contract);
                    if (binder != null) {
                        throw new BadRequestException("Binder for product " + productName + " and insurer with code " + bulkPolicyCreation.getInsurerCode().getShtDesc()
                                + " not set in the system, please contact system admin.");
                    }

                    bulkPolicyCreation.setBinderTypeId(binder.getBinId());
                    bulkPolicyCreation.setBindersDefId(binder);

                    String coverType = (ExcelReaderUtil.getCellValue(row, 9)); //getCellStringValue(row.getCell(14)));
                    String normalizedCoverType = coverType.trim().toLowerCase().replace(" ", "");
                    SubClassDef subClassDef = subClassRepo.findBySubClassName(normalizedProductName);
                    if (subClassDef != null){
                        bulkPolicyCreation.setProductName(subClassDef.getSubDesc());
                        bulkPolicyCreation.setSubClassDef(subClassDef);
                    } else {
                        throw new BadRequestException("Product " + productName + " not set in the system, please contact system admin.");
                    }
                    CoverTypesDef coverTypesDef = coverTypesRepo.findCoverTypesByBindId(binder.getBinId(), normalizedCoverType);
                    if (coverTypesDef != null) {
                        bulkPolicyCreation.setCoverType(coverTypesDef.getCovName());
                        bulkPolicyCreation.setCoverTypesDef(coverTypesDef);
                    }else {
                        throw new BadRequestException("Cover type " + coverType + " not set for insurer with code " + bulkPolicyCreation.getInsurerCode().getShtDesc() +" please contact system admin.");
                    }

                    String branchCode = ExcelReaderUtil.getCellValue(row, 10).trim(); //getCellStringValue(row.getCell(15));
                    bulkPolicyCreation.setFrequency(ExcelReaderUtil.validateFrequency(ExcelReaderUtil.getCellValue(row,11), row.getRowNum()));
                    OrgBranch orgBranch =  orgBranchRepository.findOne(QOrgBranch.orgBranch.obShtDesc.eq(branchCode));
                    if (orgBranch != null) {
                        bulkPolicyCreation.setBranch(orgBranch);
                    } else {
                        throw new BadRequestException("Branch code " + branchCode + " in sheet two is not setup in the system, please contact system admin.");
                    }
                    Date wef = ExcelReaderUtil.parseFlexibleDate(ExcelReaderUtil.getCellValue(row, 12)); // getCellDateValue(row.getCell(17));

                    if (wef != null) {
                        bulkPolicyCreation.setCoverDateFrom(wef);
                    } else {
                        throw new BadRequestException("Cover Start date is required");
                    }

                    String currency =  ExcelReaderUtil.getCellValue(row, 13);// getCellStringValue(row.getCell(18));
                    if (currency != null && !currency.trim().isEmpty()) {
                        currency = currency.trim();
                        Currencies validatedCurrency = UploadValidatorsUtils.validateAndGetCurrencyCode(currency, currencyRepository);
                        bulkPolicyCreation.setCurrency(validatedCurrency.getCurIsoCode());
                        bulkPolicyCreation.setCurrencyId(validatedCurrency);
                    } else {
                        throw new BadRequestException("Currency is required in row " + row.getRowNum() + " in Currency ISO code column");
                    }

                    bulkPolicyCreation.setRiskId(ExcelReaderUtil.getCellValue(row, 14));  // getCellStringValue(row.getCell(19)));
                    bulkPolicyCreation.setRiskDesc(ExcelReaderUtil.getCellValue(row, 15));  //getCellStringValue(row.getCell(20)));
                    bulkPolicyCreation.setSumInsured(ExcelReaderUtil.parseBigDecimalSafe(ExcelReaderUtil.getCellValue(row, 16)));  //getCellBigDecimalValue(row.getCell(21)));
                    bulkPolicyCreation.setPremium(ExcelReaderUtil.parseBigDecimalSafe(ExcelReaderUtil.getCellValue(row, 17)));  //getCellBigDecimalValue(row.getCell(22)));

                    String paymentType = ExcelReaderUtil.getCellValue(row, 18);
                    if(!paymentType.isEmpty()){
                        if(paymentType.equalsIgnoreCase("Accrual")){
                            Date instDate = ExcelReaderUtil.parseFlexibleDate(ExcelReaderUtil.getCellValue(row, 19)); //getCellDateValue(row.getCell(20)));
                            if(instDate == null){
                                throw  new BadRequestException("Accrual installment date is required.");
                            }
                            bulkPolicyCreation.setAccrualInstDate(instDate);

                            String ApaymentType = ExcelReaderUtil.getCellValue(row, 20).trim(); //getCellDateValue(row.getCell(20)));
                            if (ApaymentType == null ||
                                    (!ApaymentType.equalsIgnoreCase("Dispensation") && !ApaymentType.equalsIgnoreCase("IPF"))) {
                                throw new BadRequestException("Accrual payment is required. Either IPF or Dispensation");
                            }

                            bulkPolicyCreation.setAccrualPaymentType(ApaymentType);
                            bulkPolicyCreation.setAccrualPaymentType("A");
                        }else if(paymentType.equalsIgnoreCase("Cash")){
                            bulkPolicyCreation.setAccrualPaymentType("C"); //getCellDateValue(row.getCell(20)));
                        } else {
                            throw new BadRequestException("Please fill with Cash or Accrual");
                        }
                    }else{
                        throw new BadRequestException("The payment type field is required.");
                    }

                    bulkPolicyCreation.setTransProcessed("N");
                    bulkPolicyCreation.setPolicyType(transType);
                    bulkPolicyCreation.setUploadedBy(userUtils.getCurrentUser());
                    bulkPolicyCreation.setDateUploaded(new Date());
                    bulkPolicyCreation.setRefCode(bulkCode);

                    bulkPolicyCreations.add(bulkPolicyCreation);
                    policyMap.put(serialNo, bulkPolicyCreation);


                    if(bulkPolicyCreations.size() >= batchSize) {
                        bulkBatchSaveService.saveBulkDSCBatch(bulkPolicyCreations);
                        totalSuccessCount += bulkPolicyCreations.size();
                        bulkPolicyCreations.clear();
                    }
                } catch (Exception e) {
                    //throw new BadRequestException("Sheet One, Row " + (rowNum + 1) + ": " + e.getMessage());
                    Cell errorCell = row.createCell(errorColumnIndex);
                    errorCell.setCellValue("Error: " + e.getMessage());
                    invalidRecords.add("Sheet One, Row " + (rowNum + 1) + ": " + e.getMessage());
                    totalFailedPolicyCount++;
                }
            }
            int  successfulPolicy = bulkPolicyCreations.size();
            int failedRecords = invalidRecords.size();

//            if (!(failedRecords > 0) && successfulPolicy > 0) {
//                bulkPolicyCreationRepo.save(bulkPolicyCreations);
//            }

            if (!bulkPolicyCreations.isEmpty()) {
                bulkBatchSaveService.saveBulkDSCBatch(bulkPolicyCreations);
                totalSuccessCount += bulkPolicyCreations.size();
                bulkPolicyCreations.clear();
            }


            Sheet sheetTwo = workbook.getSheetAt(1);
            int startRowIndexTwo = findDataStartRow(sheetTwo);
            if (startRowIndexTwo == -1) {
                throw new BadRequestException("Sheet Two contains no data.");
            }
            int errorColumnIndex2 = sheetTwo.getRow(startRowIndexTwo).getLastCellNum();

            for (int rowNum = startRowIndexTwo; rowNum <= sheetTwo.getLastRowNum(); rowNum++) {
                Row row = sheetTwo.getRow(rowNum);
                if (isBlankRow(row)) continue;

                try {

                    List<BulkPolicyRisk> riskList = Optional.ofNullable(
                            (List<BulkPolicyRisk>) bulkPolicyRiskRepository.findAll(
                                    QBulkPolicyRisk.bulkPolicyRisk.riskStatus.eq("Y")
                                            .and(QBulkPolicyRisk.bulkPolicyRisk.riskCode.eq(bulkCode))
                            )
                    ).orElse(new ArrayList<>());

                    System.out.println("riskList:: " +riskList);

                    List<BulkPolicyCreation> bulkPolicyList = (List<BulkPolicyCreation>) bulkPolicyCreationRepo.findAll(
                            QBulkPolicyCreation.bulkPolicyCreation.transProcessed.eq("N")
                                    .and(QBulkPolicyCreation.bulkPolicyCreation.refCode.eq(bulkCode))
                    );
                    System.out.println("bulkPolicyList:: " +bulkPolicyList);

                    // Convert bulkPolicyList into a Set for faster lookups
                    Set<String> refCodeSet = bulkPolicyList.stream()
                            .map(BulkPolicyCreation::getRefCode)
                            .collect(Collectors.toSet());

                    for (BulkPolicyRisk checkRisk : riskList) {
                        if (refCodeSet.contains(checkRisk.getRiskCode())) {
                            // Mismatch found, immediately throw an error
                            throw new BadRequestException(
                                    "Serial No " + checkRisk.getSerialNo() + " in premium item is pending processing, process before re-uploading this file"
                            );
                        }
                    }

                    // Proceed to add new risk if no mismatch was found
                    String serialNo = ExcelReaderUtil.getCellValue(row, 0); //getCellStringValue(row.getCell(0));
                    System.out.println("Serial No sheet two: " + serialNo);
                    BulkPolicyCreation linkedPolicy = policyMap.get(serialNo);

                    if (linkedPolicy == null) {
                        throw new BadRequestException(
                                "Sheet Two, Row " + (rowNum + 1) + ": Serial number '" + serialNo + "' not found in Sheet One."
                        );
                    }

                    BulkPolicyRisk risk = new BulkPolicyRisk();
                    risk.setSerialNo(serialNo);
                    String sections = ExcelReaderUtil.getCellValue(row, 1); // getCellStringValue(row.getCell(1));
                    risk.setSections(sections != null ? sections.trim() : null);
                    risk.setLimit(ExcelReaderUtil.parseBigDecimalSafe(ExcelReaderUtil.getCellValue(row, 2))); // getCellBigDecimalValue(row.getCell(2)));
                    risk.setBulkPolicy(linkedPolicy);
                    risk.setRiskStatus("Y");
                    risk.setRiskCode(bulkCode);

                    bulkPolicyRisks.add(risk);

                    if (bulkPolicyRisks.size() == batchSize) {
                        bulkBatchSaveService.saveBulkDSCRisksBatch(bulkPolicyRisks);
                        totalRiskSuccessCount += bulkPolicyRisks.size();
                        bulkPolicyRisks.clear();
                    }

                } catch (Exception e) {
                    //throw new BadRequestException("Sheet Two, Row " + (rowNum + 1) + ": " + e.getMessage());
                    Cell errorCell = row.createCell(errorColumnIndex2);
                    errorCell.setCellValue("Error: " + e.getMessage());
                    invalidRecords.add("Sheet Two, Row " + (rowNum + 1) + ": " + e.getMessage());
                    totalFailedRiskCount++;

                }
            }

            int  successfulRisks = bulkPolicyRisks.size();

//            if (!(failedRecords > 0) && successfulRisks > 0) {
//                bulkPolicyRiskRepository.save(bulkPolicyRisks);
//            }
            if (!bulkPolicyRisks.isEmpty()) {
                bulkBatchSaveService.saveBulkDSCRisksBatch(bulkPolicyRisks);
                totalRiskSuccessCount += bulkPolicyRisks.size();
                bulkPolicyRisks.clear();
            }

            // Save error workbook
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            errorWorkbookCache.saveWorkbook(batchId, bos);

            response.put("errorFileId", batchId);
            response.put("successfulPolicies", totalSuccessCount);
            response.put("successfulRisks", totalRiskSuccessCount);
            response.put("totalFailedRiskCount", totalFailedRiskCount);
            response.put("totalFailedPolicyCount", totalFailedPolicyCount);

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
                if (cellValue.contains("policy") ||
                        cellValue.contains("number") ||
                        cellValue.contains("name") ||
                        cellValue.contains("code") ||
                        cellValue.contains("amount") ||
                        cellValue.contains("serial no") ||
                        cellValue.contains("sections") ||
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

    private int findDataStartRow(Sheet sheet) {
        for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (row != null && !isHeaderRow(row)) {
                return rowNum;
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
    public DataTablesResult<BulkPolicyCreationDTO> findUnProcessedBulkPol(DataTablesRequest request) {
        Long currentUserId = userUtils.getCurrentUser().getId();

        List<Object[]> unProcessedBulkPols = bulkPolicyCreationRepo.findUnprocessedBulkPol((request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%", request.getPageNumber(), request.getPageSize(),currentUserId);
        final List<BulkPolicyCreationDTO> unProcessed = new ArrayList<>();
        long rowCount = 0L;
        if (!unProcessedBulkPols.isEmpty()) rowCount = ((BigInteger) unProcessedBulkPols.get(0)[8]).intValue();

        for (Object[] unProcessedBulkPol : unProcessedBulkPols) {
            BulkPolicyCreationDTO bulkPolicyCreationDTO = new BulkPolicyCreationDTO();
            bulkPolicyCreationDTO.setCoverType((String) unProcessedBulkPol[0]);
            bulkPolicyCreationDTO.setClientFname((String) unProcessedBulkPol[1]);
            bulkPolicyCreationDTO.setClientOtherNames((String) unProcessedBulkPol[2]);
            bulkPolicyCreationDTO.setInsuredFName((String) unProcessedBulkPol[3]);
            bulkPolicyCreationDTO.setInsuredOtherNames((String) unProcessedBulkPol[4]);
            bulkPolicyCreationDTO.setCoverDateFrom((Date) unProcessedBulkPol[5]);
            bulkPolicyCreationDTO.setDateUploaded((Date) unProcessedBulkPol[6]);
            bulkPolicyCreationDTO.setBulkPolicyId(((BigInteger) unProcessedBulkPol[7]).longValue());
            bulkPolicyCreationDTO.setSumInsured(unProcessedBulkPol[9] != null ? (BigDecimal) unProcessedBulkPol[9] : null);

            unProcessed.add(bulkPolicyCreationDTO);
        }
        Page<BulkPolicyCreationDTO> page = new PageImpl<>(unProcessed, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<BulkPolicyCreationDTO> viewBulkPolicies(DataTablesRequest request) {
        Long currentUserId = userUtils.getCurrentUser().getId();

        List<Object[]> viewBulkPolicies = bulkPolicyCreationRepo.viewUnprocessedBulkPol((request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%", request.getPageNumber(), request.getPageSize(),currentUserId);
        final List<BulkPolicyCreationDTO> unProcessed = new ArrayList<>();
        long rowCount = 0L;
        if (!viewBulkPolicies.isEmpty()) rowCount = ((BigInteger) viewBulkPolicies.get(0)[10]).intValue();

        for (Object[] viewBulkPolicy : viewBulkPolicies) {
            BulkPolicyCreationDTO bulkPolicies = new BulkPolicyCreationDTO();
            bulkPolicies.setPolicyId(((BigInteger) viewBulkPolicy[0]).longValue());
            bulkPolicies.setPolicyCoverType((String) viewBulkPolicy[1]);
            bulkPolicies.setPolNumber((String) viewBulkPolicy[2]);
            bulkPolicies.setPolBasicPrem((BigDecimal) viewBulkPolicy[3]);
            bulkPolicies.setPolClientFname((String) viewBulkPolicy[4]);
            bulkPolicies.setPolClientOtherNames((String) viewBulkPolicy[5]);
            bulkPolicies.setPolWef((Date) viewBulkPolicy[6]);
            bulkPolicies.setPolWet((Date) viewBulkPolicy[7]);
            bulkPolicies.setPolCreationDate((Date) viewBulkPolicy[8]);
            bulkPolicies.setPolicyStatus((String) viewBulkPolicy[9]);
            unProcessed.add(bulkPolicies);
        }
        Page<BulkPolicyCreationDTO> page = new PageImpl<>(unProcessed, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
//    @Transactional(readOnly = false)
    public PolicyTrans processSingleBulkPolicy(Long bulkId, boolean isApproved) throws BadRequestException {
        BulkPolicyCreation policyCreation = bulkPolicyCreationRepo.findOne(QBulkPolicyCreation.bulkPolicyCreation.bulkPolicyId.eq(bulkId));
        if (policyCreation.getTransProcessed().equalsIgnoreCase("Y")) {
            throw new BadRequestException("This transaction is already processed");
        }
        if (QRiskTrans.riskTrans.riskDesc.equals(policyCreation.getRiskId())) {
            throw new BadRequestException("Risk id" + policyCreation.getRiskId() + " already exists in the system");
        }
        final PolicyTrans policy = new PolicyTrans();
        Date wef = policyCreation.getCoverDateFrom();
        final Date wet = dateUtils.getWetDate(wef);
        policy.setWefDate(wef);
        policy.setWetDate(wet);
        policy.setPolCreateddt(new Date());
        policy.setAuthStatus("D");
        policy.setCurrentStatus("D");
        policy.setCoverFrom(wef);
        policy.setInstallmentNo(1L);
        policy.setCoverTo(wet);
        policy.setBasicPrem(policyCreation.getPremium());
        policy.setPremium(policyCreation.getPremium());
        String frequency = policyCreation.getFrequency();

        policy.setTotalInstalments(1);

        if (frequency != null && !frequency.isEmpty()) {
            if (frequency.equalsIgnoreCase("Daily")) {
                policy.setFrequency("D");
            } else if (frequency.equalsIgnoreCase("Weekly")) {
                policy.setFrequency("W");
            } else if (frequency.equalsIgnoreCase("Monthly")) {
                policy.setFrequency("M");
            } else if (frequency.equalsIgnoreCase("Quarterly")) {
                policy.setFrequency("Q");
            } else if (frequency.equalsIgnoreCase("Semi-Annually")) {
                policy.setFrequency("S");
            } else if (frequency.equalsIgnoreCase("Annually")) {
                policy.setFrequency("A");
            } else if (frequency.equalsIgnoreCase("Single")) {
                policy.setFrequency("SG");
            } else {
                throw new BadRequestException("Unknown frequency '" + frequency + "'");
            }
        } else {
            throw new BadRequestException("Payment frequency is not defined for client ID " + policyCreation.getClientIdNo() + " in frequency column in uploaded excel");
        }

        final String pinToSearch = policyCreation.getClientPin().trim();
        final ClientDef client =  policyCreation.getClientDef(); //clientRepository.findByPinNoIgnoreCase(pinToSearch);
        if (client != null) {
            policy.setClient(client);
        } else {
            throw new BadRequestException("Client with KRA pin " + pinToSearch + " is not onboarded in the system");
        }

        policy.setAgent(policyCreation.getInsurerCode());
        if (policyCreation.getSalesAgent() != null) {
            AccountDef subAgent = policyCreation.getSalesAgentId(); //accountRepo.findByAcctId(policyCreation.getSalesCode());
            if (subAgent != null) {
                policy.setSubAgent(subAgent);
            }
        }

        String normalizedPrdName = policyCreation.getProductName().trim().toLowerCase().replace(" ", "");
        final ProductsDef product =  policyCreation.getProductDefId(); //productsRepo.findByNormalizedPName(normalizedPrdName);
        if (product != null) {
            policy.setProduct(product);
            String businessType = product.getProGroup().getPrgType();
            if (!businessType.equalsIgnoreCase("L")) {
                policy.setBusinessType("N");
            } else {
                policy.setBusinessType("L");
            }
        } else {
            throw new BadRequestException(policyCreation.getProductName() + " product is not found. Please set up the product.");
        }
        final String currToSearch = policyCreation.getCurrency().trim();
        final Currencies currency =  policyCreation.getCurrencyId(); //currencyRepository.findByPinNoIgnoreCase(currToSearch);
        if (currency != null) {
            policy.setTransCurrency(currency);
        } else {
            throw new BadRequestException(policyCreation.getCurrency() + " currency is not set up. Please add the currency to the system.");
        }
        UserBranches userBranches = userBranchesRepository.findByUser(userUtils.getCurrentUser());
        Long branchId = userBranches.getBranch().getObId();
        final OrgBranch branches = orgBranchRepository.findOne(QOrgBranch.orgBranch.obId.eq(branchId));
        policy.setBranch(branches);
        policy.setCreatedUser(userUtils.getCurrentUser());
        policy.setPreviousTrans(policy);

        //BindersDef polBinder = polBinders.get(0);
        BindersDef polBinder = policyCreation.getBindersDefId(); //bindersRepo.findByBindersBybinid(policyCreation.getBinderTypeId());
        policy.setBinder(polBinder);
        if((polBinder.getAdminFeeActiveStatus() != null) && (polBinder.getAdminFeeActiveStatus().equalsIgnoreCase("Y"))) {
            policy.setAdminFeeApplicable("Y");
        } else {
            policy.setAdminFeeApplicable("N");
        }
        SystemTrans systemTrans = null;
        if (policyCreation.getTransProcessed().equalsIgnoreCase("N")) {

            String policyNumberFormat = paramService.getParameterString("POLICY_NO_FORMAT");
            String endorsementFormat = paramService.getParameterString("ENDORSE_NO_FORMAT");
            Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("P");
            if (sequenceRepository.count(seqPredicate) == 0)
                throw new BadRequestException("Sequence for New Business Transactions has not been defined");
            SystemSequence sequence = sequenceRepository.findOne(seqPredicate);
            Long seqNumber = sequence.getNextNumber();
            final String policyNumber = templateMerger.generateFormat(policyNumberFormat, policyCreation.getBranch().getObId(), product.getProCode(), policy.getWefDate(), sequence.getSeqPrefix() + String.format("%05d", seqNumber), null);
            policy.setPolNo(policyNumber);

            sequence.setLastNumber(seqNumber);
            sequence.setNextNumber(seqNumber + 1);
            sequenceRepository.save(sequence);
            Predicate endorsePredicate = QSystemSequence.systemSequence.transType.eq("E");
            if (sequenceRepository.count(endorsePredicate) == 0)
                throw new BadRequestException("Sequence for Endorsement Transactions has not been defined");
            SystemSequence endorseSequence = sequenceRepository.findOne(endorsePredicate);
            Long endosseqNumber = endorseSequence.getNextNumber();
            final String revNumber = endorseSequence.getSeqPrefix() + String.format("%05d", endosseqNumber);
            final String endorseNumber = templateMerger.generateFormat(endorsementFormat, policyCreation.getBranch().getObId(), product.getProCode(), policyCreation.getCoverDateFrom(), revNumber, null);
            policy.setPolRevNo(endorseNumber + "/1");
            policy.setRevisionFormat(endorseNumber);
            endorseSequence.setLastNumber(endosseqNumber);
            endorseSequence.setNextNumber(endosseqNumber + 1);
            sequenceRepository.save(endorseSequence);

        }
//        policy.setRenewable(product.isRenewable());
        if (product.isRenewable())
            policy.setRenewable(true);
        else
            policy.setRenewable(false);
        policy.setUwYear(dateUtils.getUwYear(policy.getWefDate()));
        if (product.isRenewable()) {
            policy.setRenewalDate(DateUtils.addDays(policy.getWetDate(), 1));
            policy.setNotificationSent(false);
        } else
            policy.setRenewalDate(null);
        policy.setTransType("BU");
        policy.setPolRevStatus("LD");

        if(policyCreation.getAccrualPaymentType() != null && policyCreation.getAccrualPaymentType().equalsIgnoreCase("A")) {
            policy.setInterfaceType("A");
            policy.setAccrualInstDate(policyCreation.getAccrualInstDate());
            policy.setAccrualPaymentType(policyCreation.getAccrualPaymentType());
        }else{
            policy.setInterfaceType("C");
        }

        policy.setPolCreateddt(new Date());
        PolicyTrans savedPol = policyTransRepo.save(policy);


        systemTrans = new SystemTrans();
        systemTrans.setDoneDate(new Date());
        systemTrans.setDoneBy(userUtils.getCurrentUser());
        systemTrans.setPolicy(savedPol);
        systemTrans.setTransLevel("U");
        systemTrans.setTransCode("BUD");
        systemTrans.setTransAuthorised("N");
        systemTransRepo.save(systemTrans);

        final String insuredPinSerach = policyCreation.getInsuredPin().trim();
        final ClientDef insured = policyCreation.getClientDef();//clientRepository.findByPinNoIgnoreCase(insuredPinSerach);
        RiskTrans riskTrans = new RiskTrans();
        if (insured != null) {
            riskTrans.setInsured(insured);
        } else {
            throw new BadRequestException("Insured is with KRA pin " + insuredPinSerach + " is not onboarded in the system");
        }
        final BigDecimal basicPrem = policyCreation.getPremium().add(BigDecimal.ZERO).multiply(BigDecimal.valueOf(100/100.45));
        //BindersDef newBinder = bindersRepo.findBinderByAccIdAndBinName(policyCreation.getInsurerCode().getAcctId(), product.getProCode(), policyCreation.getContractName()); //bindersRepo.findBinderByAccId(policyCreation.getInsurerCode().getAcctId(), product.getProCode());
//        List<BindersDef> mpolBinders =  bindersRepo.findBinderByAccIdAndBinName(policyCreation.getInsurerCode().getAcctId(), product.getProCode());  // bindersRepo.findBinderByAccId(policyCreation.getInsurerCode().getAcctId(), product.getProCode());
//        if (mpolBinders.isEmpty()) {
//            throw new BadRequestException("Default Binder for product " + product.getProDesc() + " and insurer with code " + policyCreation.getInsurerCode().getShtDesc()
//                    + " not set in the system, please contact system admin.");
//        }
        BindersDef newBinder = policyCreation.getBindersDefId(); //bindersRepo.findByBindersBybinid(policyCreation.getBinderTypeId());
        riskTrans.setBinder(newBinder);
        String normalizedSubClass = policyCreation.getProductName().trim().toLowerCase().replace(" ", "");
        String normalizedCoverType = policyCreation.getCoverType().trim().toLowerCase().replace(" ", "");
        final SubClassDef subClassDef = policyCreation.getSubClassDef(); //subClassRepo.findBySubClassName(normalizedSubClass);
        final CoverTypesDef coverTypesDef = policyCreation.getCoverTypesDef(); //coverTypesRepo.findCoverTypesByBindId(newBinder.getBinId(),normalizedCoverType);
        BinderDetails newDetails = binderDetRepo.findOne(QBinderDetails.binderDetails.binder.binId.eq(newBinder.getBinId()).and(QBinderDetails.binderDetails.subCoverTypes.coverTypes.covId.eq(coverTypesDef.getCovId())));
        riskTrans.setSubclass(subClassDef);
        riskTrans.setCovertype(coverTypesDef);
        riskTrans.setRiskShtDesc(policyCreation.getRiskId());
        riskTrans.setRiskDesc(policyCreation.getRiskDesc());
        riskTrans.setBinderDetails(newDetails);
        riskTrans.setWefDate(wef);
        riskTrans.setWetDate(wet);
        riskTrans.setTransType("BU");
        riskTrans.setPolicy(savedPol);
        riskTrans.setButchargePrem(basicPrem);
        riskTrans.setAutogenCert("N");
        riskTrans.setSumInsured(policyCreation.getSumInsured());
        riskTrans.setPremium(policyCreation.getPremium());
        riskTrans.setComputePremium(policyCreation.getPremium());
        riskTrans.setNetpremium(policyCreation.getPremium());
        policy.setPremium(policyCreation.getPremium());

        String businessType = product.getProGroup().getPrgType();
        if (!businessType.equalsIgnoreCase("L")) {
            policy.setBusinessType("N");
            riskTrans.setInstallmentNo(1l);
        } else {
            policy.setBusinessType("L");
        }

        ///

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
        long riskIdentifier = Long.valueOf(String.valueOf(dateUtils.getUwYear(policy.getWefDate())) + String.valueOf(savedRisk.getRiskId()));
        savedRisk.setRiskIdentifier(riskIdentifier);

        PolicyActiveRisks activeRisk = new PolicyActiveRisks();
        activeRisk.setPolicy(policy);
        activeRisk.setRisk(riskTrans);
        activeRisk.setRiskIdentifier(riskIdentifier);
        activeRisksRepo.save(activeRisk);
        savedRisk.setRiskIdentifier(riskIdentifier);
        riskTransRepo.save(savedRisk);

        List<SectionTrans> sectionTransactions = new ArrayList<>();
        List<BulkPolicyRisk> riskSections = bulkPolicyRiskRepository.findAllByBulkPolicy(policyCreation);
        riskSections.stream().filter(a -> a.getSerialNo().equals(policyCreation.getSerialNo())).forEach(a -> {
            long count = setupSectionRepo.count(QSectionsDef.sectionsDef.desc.eq(a.getSections().trim()));
            if (count == 1) {
                SectionsDef sectiondef = setupSectionRepo.findOne(QSectionsDef.sectionsDef.desc.eq(a.getSections().trim()));
                SectionTrans section = new SectionTrans();
                List<PremRatesDef> premRates = premRatesRepo.getSectPremiumRates(savedRisk.getBinderDetails().getDetId(), sectiondef.getId());
                if (premRates.size() == 1) {
                    section.setPremRates(premRates.get(0));
                    section.setRate(premRates.get(0).getRate());
                    section.setAmount((a.getLimit() != null) ? a.getLimit() : BigDecimal.ZERO);
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

        //taxes
        Iterable<TaxRates> taxRates = taxRatesRepo.findAll((QTaxRates.taxRates.active.eq(true).and(QTaxRates.taxRates.mandatory.eq(Boolean.TRUE))).and(QTaxRates.taxRates.subclass.subId.eq(savedRisk.getSubclass().getSubId()))
                .and(QTaxRates.taxRates.productsDef.proCode.eq(savedPol.getProduct().getProCode())));
        System.out.println("Total Tax Rates size..."+taxRates.spliterator().getExactSizeIfKnown());
        Set<PolicyTaxes> policyTaxes = new HashSet<>();
        for (TaxRates tax : taxRates) {
            PolicyTaxes policyTax = new PolicyTaxes();
            policyTax.setPolicy(savedPol);
            policyTax.setRateType(tax.getRateType());
            policyTax.setRevenueItems(tax.getRevenueItems());
            policyTax.setSubclass(tax.getSubclass());
            policyTax.setTaxLevel(tax.getTaxLevel());
            policyTax.setTaxRate(tax.getTaxRate());
            policyTax.setDivFactor(tax.getDivFactor());
            policyTax.setTaxAmount(premiumService.calculateTax(basicPrem, tax.getTaxRate(), tax.getDivFactor(), tax.getRateType()));
            policyTaxes.add(policyTax);
        }
        polTaxesRepo.save(policyTaxes);
        try {
            premComputeServiceImpl.computePrem(savedPol.getPolicyId());
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }
        BigDecimal polstampDuty = BigDecimal.ZERO;
        BigDecimal polphfFund = BigDecimal.ZERO;
        BigDecimal polTl = BigDecimal.ZERO;
        for (PolicyTaxes policyTax : policyTaxes) {
            BigDecimal computedTax = premiumService.calculateTax(basicPrem, policyTax.getTaxRate(), policyTax.getDivFactor(),
                    policyTax.getRateType());
            policyTax.setTaxAmount(computedTax);
            if (policyTax.getRevenueItems().getItem() == RevenueItems.SD) {
              //  polstampDuty = polstampDuty.add(computedTax);
            } else if (policyTax.getRevenueItems().getItem() == RevenueItems.PHCF) {
                polphfFund = polphfFund.add(computedTax);
            } else if (policyTax.getRevenueItems().getItem() == RevenueItems.TL) {
                polTl = polTl.add(computedTax);

            }
        }
        Currencies currencies = savedPol.getTransCurrency();
        savedPol.setPhcf(polphfFund.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        savedPol.setTrainingLevy(polTl.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        savedPol.setTotTrainingLevy(polTl.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        savedPol.setTotPhcf(polphfFund.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        savedPol.setTotTrainingLevy(polTl.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        savedPol.setStampDuty(BigDecimal.ZERO); //polstampDuty); //for bulk upload stampduty is zero
        savedPol.setFuturePrem(BigDecimal.ZERO);
        savedPol.setSumInsured(policyCreation.getSumInsured());
        //savedPol.setBasicPrem(policyCreation.getPremium());
        policyTransRepo.save(savedPol);

        //make ready the policy
        uploadValidatorsService.saveGeneralPolicyUpload(savedPol.getPolicyId(), false);

        policyCreation.setPolicyTrans(savedPol);
        policyCreation.setTransProcessed("Y");
        policyCreation.setPolicyAuthorized("N");
        policyCreation.setProcessedBy(userUtils.getCurrentUser());
        bulkPolicyCreationRepo.save(policyCreation);

        return savedPol;
    }

    @Override
    @Transactional(readOnly = false)
    public void bulkDelUploadPolicies(List<Long> bulkIds) throws BadRequestException {
        if (bulkIds.size() <= 0) {
            throw new BadRequestException("Select At least One Transaction To Process");
        }
        //bulkpolicycreation and risk have similar serial number
        for (Long bulkId : bulkIds) {
            //get bulk policy uploaded
            BulkPolicyCreation bulkPolicyCreation = bulkPolicyCreationRepo.findOne(QBulkPolicyCreation.bulkPolicyCreation.bulkPolicyId.eq(bulkId));
            //get the policy risk uploaded
            System.out.println("serial number "+bulkPolicyCreation.getSerialNo());
            BulkPolicyRisk bulkPolicyRisk = bulkPolicyRiskRepository.findByBulkPolicy(bulkPolicyCreation.getBulkPolicyId());
            //delete the risk by serial number
            bulkPolicyRiskRepository.delete(bulkPolicyRisk);
            //delete the bulk policy uploaded
            bulkPolicyCreationRepo.delete(bulkPolicyCreation);
        }
    }


    @Override
    @Transactional(readOnly = false)
    public String bulkDelProcessedPolicies(List<Long> bulkIds) throws BadRequestException {
        if (bulkIds == null || bulkIds.isEmpty()) {
            throw new BadRequestException("Select at least one transaction to process");
        }

        for (Long bulkId : bulkIds) {
            BulkPolicyCreation bulkPolicyCreation = bulkPolicyCreationRepo.findWithPolicyTransByBulkId(bulkId);

            if (bulkPolicyCreation == null) {
                System.out.println("No BulkPolicyCreation found for ID: " + bulkId);
                continue;
            }

            if (!"Y".equalsIgnoreCase(bulkPolicyCreation.getTransProcessed())) {
                System.out.println("Transaction not marked as processed for ID: " + bulkId);
                continue;
            }

            BulkPolicyRisk bulkPolicyRisk = bulkPolicyRiskRepository.findByBulkPolicy(bulkPolicyCreation.getBulkPolicyId());

            if (bulkPolicyRisk != null) {
                bulkPolicyRiskRepository.delete(bulkPolicyRisk);
            }

            uploadValidatorsService.deleteprocessedPol(bulkId);
            bulkPolicyCreationRepo.delete(bulkPolicyCreation);
        }

        return "Transactions Deleted Successfully";
    }




    @Override
//    @Transactional(readOnly = false)
    public List<Long> bulkProcessPolicies(List<Long> bulkIds, boolean isApproved) throws BadRequestException {
        if (bulkIds.size() <= 0) {
            throw new BadRequestException("Select At least One Transaction To Process");
        }

        List<Long> processedPolicyIds = new ArrayList<>();
        for (Long bulkId : bulkIds) {
            try {
                PolicyTrans savedPol = processSingleBulkPolicy(bulkId, isApproved);
                processedPolicyIds.add(savedPol.getPolicyId());
            } catch (BadRequestException e) {
                //throw new RuntimeException(e);
                log.error("Failed to process policy ID " + bulkId, e);
            }
        }
        return processedPolicyIds;
    }
    @Override
    public String approveSinglePol(Long policyId) throws BadRequestException {
        authService.authorizeBulkUploadPolicies(policyId);
        BulkPolicyCreation policyCreation = bulkPolicyCreationRepo.findBulkStockByPolicyId(policyId);
        policyCreation.setPolicyAuthorized("Y");
        bulkPolicyCreationRepo.save(policyCreation);
        return "Transaction Authorized Successfully";
    }

    @Override
    public String approveBulkPol(List<Long> policyIds) throws BadRequestException {

        if (policyIds.size() <= 0) {
            throw new BadRequestException("Select At least One Transaction To Process");
        }
        for (Long policyId : policyIds) {
            authService.authorizeBulkUploadPolicies(policyId);
            BulkPolicyCreation policyCreation = bulkPolicyCreationRepo.findBulkStockByPolicyId(policyId);
            policyCreation.setPolicyAuthorized("Y");
            bulkPolicyCreationRepo.save(policyCreation);
        }

        return "Transactions Authorized Successfully";
    }
}