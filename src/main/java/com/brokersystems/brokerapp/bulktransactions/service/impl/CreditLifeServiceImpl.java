package com.brokersystems.brokerapp.bulktransactions.service.impl;

import com.brokersystems.brokerapp.bulktransactions.ErrorsCache.ErrorWorkbookCache;
import com.brokersystems.brokerapp.bulktransactions.dtos.CreditLifeDTO;
import com.brokersystems.brokerapp.bulktransactions.models.*;
import com.brokersystems.brokerapp.bulktransactions.repositories.CreditLifeRepository;
import com.brokersystems.brokerapp.bulktransactions.service.CreditLifeService;
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
import com.brokersystems.brokerapp.uw.repository.SectionTransRepo;
import com.brokersystems.brokerapp.uw.validators.RenewalJobListener;
import com.mysema.query.types.Predicate;
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

@Service
public class CreditLifeServiceImpl implements CreditLifeService {

    @Autowired
    private SequenceRepository sequenceRepository;
    @Autowired
    private UserUtils userUtils;
    @Autowired
    private CreditLifeRepository creditLifeRepository;
    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private ClientTypeRepo clientTypeRepo;
    @Autowired
    private OrgBranchRepository orgBranchRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private PolicyTransRepo policyTransRepo;
    @Autowired
    private CommRatesRepo commRatesRepo;
    @Autowired
    private RiskTransRepo riskTransRepo;
    @Autowired
    private PolicyAuthorization authService;
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
    private ProductsRepo productsRepo;
    @Autowired
    private DateUtilities dateUtils;
    @Autowired
    private SystemTransRepo systemTransRepo;
    @Autowired
    private PaymentModeRepo paymentModeRepo;
    @Autowired
    private UploadValidatorsUtils uploadValidatorsUtils;
    @Autowired
    private ErrorWorkbookCache errorWorkbookCache;
    @Autowired
    private UserRepository userRepo;

    private  static final int BATCH_SIZE = 500;

    @Override
    @Async
    public void  uploadCreditLife(File file, Long userId) throws BadRequestException {
        User user = userRepo.findOne(userId);

        Map<String, Object> response = new HashMap<>();
        List<String> invalidRecords = new ArrayList<>();
        List<CreditLife> creditLifeList = new ArrayList<>();

        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
            Workbook workbook = WorkbookFactory.create(inputStream);

            Sheet sheetOne = workbook.getSheetAt(0);
            int startRowIndex = findDataStartRow(sheetOne);
            if (startRowIndex == -1) {
                throw new BadRequestException("Sheet One contains no data.");
            }

            int errorColumnIndex = sheetOne.getRow(startRowIndex).getLastCellNum();
            Row headerRow = sheetOne.getRow(startRowIndex - 1);
            if (headerRow != null && headerRow.getCell(errorColumnIndex) == null) {
                Cell headerCell = headerRow.createCell(errorColumnIndex);
                headerCell.setCellValue("Error");
            }

            String bulkCode = null;
            String transType = "CLU";
            Predicate pedSystem = QSystemSequence.systemSequence.transType.eq("CLU");
            if (sequenceRepository.count(pedSystem) == 0)
                throw new BadRequestException("Sequence for Credit Life Upload has not been defined");

            String batchId = UUID.randomUUID().toString();
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
                    CreditLife creditLife = new CreditLife();

                    String clntIdNo = ExcelReaderUtil.getCellValue(row, 0); // getCellStringValue(row.getCell(3));
                    creditLife.setClientID(clntIdNo);
                    String cif = ExcelReaderUtil.getCellValue(row, 1); //getCellStringValue(row.getCell(4));
                    creditLife.setClientCIF(cif); //getCellStringValue(row.getCell(10)));

                    final ClientDef client = clientRepository.findByCifandIdNoIgnoreCase(cif, clntIdNo);
                    if (client != null) {
                        creditLife.setClientFName(client.getFname());
                        creditLife.setClientOtherNames(client.getOtherNames());
                        creditLife.setClientDOB(client.getDob());
                        creditLife.setClientPin(client.getPinNo());
                        creditLife.setClientEmail(client.getEmailAddress());
                        creditLife.setClientPhone(client.getPhoneNo());
                        creditLife.setClientType(client.getTenantType());

                    } else {
                        throw new BadRequestException("Client with ID No "+ clntIdNo + " not found.");
                    }

                    String insurerCode = ExcelReaderUtil.getCellValue(row, 2); //getCellStringValue(row.getCell(9));
                    AccountDef underWriter = accountRepo.findOne(QAccountDef.accountDef.shtDesc.eq(insurerCode.trim()));
                    if (underWriter != null) {
                        creditLife.setInsurerCode(underWriter);
                    } else {
                        throw new BadRequestException("Insurer with code " + insurerCode + " is not setup in the system, please contact system admin");
                    }
                    creditLife.setProductGroup(ExcelReaderUtil.getCellValue(row, 3)); //getCellStringValue(row.getCell(10)));
                    String productName = (ExcelReaderUtil.getCellValue(row, 4)); //getCellStringValue(row.getCell(11)));

                    String searchCode = "CLPU";
                    String normalizedProductName = productName.trim().toLowerCase().replace(" ", "");
                    System.out.println("looking for productdef "+ normalizedProductName +" search code "+searchCode);
                    ProductsDef product = productsRepo.findByNormalizedName(normalizedProductName, searchCode);
                    if (product == null) {
                        throw new BadRequestException("Product " + productName + " not set in the system, please contact system admin.");
                    }

                    String contract = (ExcelReaderUtil.getCellValue(row, 5));
                    contract = contract.replaceAll("\\s+", " ").trim();
                    creditLife.setContractName(contract);
                    System.out.println("looking for binders "+creditLife.getInsurerCode().getAcctId()+" and product with procode "+ product.getProCode()+" contract"+ contract);
//                    BindersDef binder = bindersRepo.findBinderByAccIdAndBinName(creditLife.getInsurerCode().getAcctId(), product.getProCode(), contract);
//                    if (binder == null) {
//                        throw new BadRequestException("Binder for product " + productName + " and insurer with code " + creditLife.getInsurerCode().getShtDesc()
//                                + " not set in the system, please contact system admin.");
//                    }
                    List<BindersDef> polBinders =  bindersRepo.findBinderByAccIdAndBinName(creditLife.getInsurerCode().getAcctId(), product.getProCode());
                    if (polBinders.isEmpty()) {
                        throw new BadRequestException("Default Binder for product " + product.getProDesc() + " and insurer with code " + creditLife.getInsurerCode().getShtDesc()
                                + " not set in the system, please contact system admin.");
                    }
                    BindersDef binder = polBinders.get(0);
                    if(!binder.getProduct().isActive()){
                        throw new BadRequestException("The policy contract is not active. Please authorise the contract to continue...");
                    }
                    creditLife.setBinderTypeId(binder.getBinId());
                    String coverType = (ExcelReaderUtil.getCellValue(row, 6)); //getCellStringValue(row.getCell(13)));
                    String normalizedCoverType = coverType.trim().toLowerCase().replaceAll("\\s+", " ");
                    System.out.println("Searching by subclassdef "+normalizedProductName);
                    SubClassDef subClassDef = subClassRepo.findBySubClassName(normalizedProductName);
                    if (subClassDef != null){
                        creditLife.setProductName(subClassDef.getSubDesc());
                    } else {
                        throw new BadRequestException("Product " + productName + " not set in the system, please contact system admin.");
                    }
                    System.out.println("Searching for cover types with bin "+binder.getBinId() +" covertype" + normalizedCoverType);
                    CoverTypesDef coverTypesDef = coverTypesRepo.findCoverTypesByBindId(binder.getBinId(), normalizedCoverType);
                    if (coverTypesDef != null) {
                        creditLife.setCoverType(coverTypesDef.getCovName());
                    }else {
                        throw new BadRequestException("Cover type " + coverType + " not set for insurer with code " + creditLife.getInsurerCode().getShtDesc() +" please contact system admin.");
                    }
                    Integer polTerm = Integer.valueOf(ExcelReaderUtil.getCellValue(row, 7));//ExcelReaderUtil.getCellValue(row, 7))); //getCellStringValue(row.getCell(14))));
                    String freqq = ExcelReaderUtil.validateFrequency(ExcelReaderUtil.getCellValue(row, 8), row.getRowNum()); //getCellStringValue(row.getCell(15)), row.getRowNum()));
                    creditLife.setFrequency(freqq);


                        int polTermYears = polTerm / 12;
                        if(polTermYears < 1 ){
                            polTermYears = 1;
                        }

                    creditLife.setCreditTerm(polTermYears);

                    String currency = ExcelReaderUtil.getCellValue(row, 9); //getCellStringValue(row.getCell(16));
                    BigDecimal premium = ExcelReaderUtil.parseBigDecimalSafe(ExcelReaderUtil.getCellValue(row, 12)); //getCellBigDecimalValue(row.getCell(19)));
                    premium = premium.abs();

                    if (currency != null && !currency.trim().isEmpty()) {
                        currency = currency.trim();
                        System.out.println("getting currency "+currency);
                        Currencies currencies = currencyRepository.findOne(QCurrencies.currencies.curIsoCode.eq(currency));
                        if (currencies == null) {
                            throw new BadRequestException("Currency code " + currency + " is not setup in the system, please contact system admin");
                        }
                       // ExcelReaderUtil.validateCurrency(currencies.getCurIsoCode(),premium);
                        creditLife.setCurrencyCode(currencies);
                    } else {
                        throw new BadRequestException("Currency is required in row " + row.getRowNum() + " in Currency ISO code column");
                    }

                    String branchCode = ExcelReaderUtil.getCellValue(row, 10); //getCellStringValue(row.getCell(17));
                    System.out.println("finding branch"+branchCode);
                    OrgBranch orgBranch = orgBranchRepository.findOne(QOrgBranch.orgBranch.obShtDesc.eq(branchCode.trim()));
                    if (orgBranch != null) {
                        creditLife.setBranch(orgBranch);
                    } else {
                        throw new BadRequestException("Branch code " + branchCode + " in sheet two is not setup in the system, please contact system admin.");
                    }

                    BigDecimal sumInsured = ExcelReaderUtil.parseBigDecimalSafe(ExcelReaderUtil.getCellValue(row, 11)); //getCellBigDecimalValue(row.getCell(18)));
                    sumInsured = sumInsured.abs();
                    creditLife.setSumInsured(sumInsured);
                    creditLife.setCreditPremium(premium); //getCellBigDecimalValue(row.getCell(19)));
                    creditLife.setTransDate(ExcelReaderUtil.parseFlexibleDate(ExcelReaderUtil.getCellValue(row, 13))); //getCellDateValue(row.getCell(20)));

                    Date wef = ExcelReaderUtil.parseFlexibleDate(ExcelReaderUtil.getCellValue(row, 14)); //getCellDateValue(row.getCell(21)));
                    creditLife.setCoverFrom(wef);

                    Date wet = dateUtils.getWetMonthDate(wef, polTerm);
                    creditLife.setCoverTo(wet);
                    //creditLife.setCoverTo(ExcelReaderUtil.parseFlexibleDate(ExcelReaderUtil.getCellValue(row, 15))); //getCellDateValue(row.getCell(22)));
                    creditLife.setLoanId(ExcelReaderUtil.getCellValue(row, 15)); //getCellDateValue(row.getCell(22)));
                    creditLife.setUploadedDate(new Date());
                    creditLife.setUploadedBy(user);
                    creditLife.setTransStatus("N");
                    creditLife.setTransType(transType);
                    creditLife.setTransCode(bulkCode);

                    creditLifeList.add(creditLife);
                    if(creditLifeList.size() >= BATCH_SIZE){
                        creditLifeRepository.save(creditLifeList);
                        creditLifeList.clear();
                    }
                } catch (Exception e) {
                    //throw new BadRequestException("Sheet One " + e.getMessage());
                    Cell errorCell = row.getCell(errorColumnIndex);
                    if (errorCell == null) errorCell = row.createCell(errorColumnIndex);
                    errorCell.setCellValue("Error: " + e.getMessage());
                  //  invalidRecords.add("Sheet One, Row " + (rowNum + 1) + ": " + e.getMessage());
                }
            }

            int successfulPol = creditLifeList.size();
            int invalidPol = invalidRecords.size();
//            if (!(invalidPol > 0) && successfulPol > 0) {
//                creditLifeRepository.save(creditLifeList);
//            }
            if (!creditLifeList.isEmpty()) {
                creditLifeRepository.save(creditLifeList);
                creditLifeList.clear();
            }

            // Save workbook with errors
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            workbook.write(bos);
//            errorWorkbookCache.saveWorkbook(batchId, bos);

            response.put("successfulPolicies", successfulPol);
            response.put("failedRecords", invalidPol);
            //response.put("invalidRecords", invalidRecords);
            response.put("errorFileId", batchId);
//
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
    public DataTablesResult<CreditLifeDTO> findUnprocessedCreditLife(DataTablesRequest request) {

        Long currentUserId = userUtils.getCurrentUser().getId();

        List<Object[]> unprocessedPackagedInsur = creditLifeRepository.findUnprocessedCreditLife((request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%", request.getPageNumber(), request.getPageSize(), currentUserId);
        final List<CreditLifeDTO> packageInsuranceDTOS = new ArrayList<>();
        long rowCount = 0L;
        if (!unprocessedPackagedInsur.isEmpty())
            rowCount = ((BigInteger) unprocessedPackagedInsur.get(0)[9]).intValue();
        for (Object[] embedPackage : unprocessedPackagedInsur) {
            CreditLifeDTO packageInsuranceDTO = new CreditLifeDTO();
            packageInsuranceDTO.setCreditLifeId(((BigInteger) embedPackage[0]).longValue());
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
        Page<CreditLifeDTO> page = new PageImpl<>(packageInsuranceDTOS, request, rowCount);

        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<CreditLifeDTO> viewUnprocessedCreditLife(DataTablesRequest request) {
        Long currentUserId = userUtils.getCurrentUser().getId();

        List<Object[]> viewUnprocessedCredit = creditLifeRepository.viewUnprocessedCreditLife((request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%", request.getPageNumber(), request.getPageSize(), currentUserId);
        final List<CreditLifeDTO> insuranceDTOS = new ArrayList<>();
        long rowCount = 0L;
        if (!viewUnprocessedCredit.isEmpty()) rowCount = ((BigInteger) viewUnprocessedCredit.get(0)[10]).intValue();
        for (Object[] viewEmbedPackage : viewUnprocessedCredit) {
            CreditLifeDTO creditLifeDTO = new CreditLifeDTO();
            creditLifeDTO.setPolId(((BigInteger) viewEmbedPackage[0]).longValue());
            creditLifeDTO.setCoverType((String) viewEmbedPackage[1]);
            creditLifeDTO.setPolNo((String) viewEmbedPackage[2]);
            creditLifeDTO.setCreditPremium((BigDecimal) viewEmbedPackage[3]);
            creditLifeDTO.setClientFName((String) viewEmbedPackage[4]);
            creditLifeDTO.setClientOtherNames((String) viewEmbedPackage[5]);
            creditLifeDTO.setCoverFrom((Date) viewEmbedPackage[6]);
            creditLifeDTO.setCoverTo((Date) viewEmbedPackage[7]);
            creditLifeDTO.setProcessedDate((Date) viewEmbedPackage[8]);
            creditLifeDTO.setTransStatus((String) viewEmbedPackage[9]);

            insuranceDTOS.add(creditLifeDTO);
        }
        Page<CreditLifeDTO> page = new PageImpl<>(insuranceDTOS, request, rowCount);

        return new DataTablesResult<>(request, page);
    }

    @Override
    public PolicyTrans processSingleCreditLifePol(Long creditId,Long userId, boolean isApproved) throws BadRequestException {
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




       // PolicyTrans savedPol = null;
        CreditLife creditLife = creditLifeRepository.findOne(creditId);
        if (creditLife.getTransStatus().equalsIgnoreCase("Y")) {
            throw new BadRequestException("This transaction is already processed");
        }
       // final PolicyTrans policyTrans = new PolicyTrans();
        Date wef = creditLife.getCoverFrom();
        Date wet = creditLife.getCoverTo();
        System.out.println("wet pol" + wet);
//        policyTrans.setPolCreateddt(new Date());
//        policyTrans.setAuthStatus("LD");
//        policyTrans.setCurrentStatus("LD");
        //policyTrans.setCoverFrom(wef);
        policyDto.setWefDate(wef);

        //policyTrans.setCoverTo(wet);
        policyDto.setWetDate(wet);

        //policyTrans.setWefDate(wef);

        //policyTrans.setWetDate(wet);

        String frequency = creditLife.getFrequency();
       //policyTrans.setPolTerm(1);
        policyDto.setPolTerm(creditLife.getCreditTerm());

        ///policyTrans.setTotalInstalments(1);
        policyDto.setTotalInstalments(null);

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
            throw new BadRequestException("Payment frequency is not defined for client ID " + creditLife.getClientID() + " in frequency column in uploaded excel");
        }
        final String pinSearch = creditLife.getClientPin().trim();
        final ClientDef client = clientRepository.findByPinNoIgnoreCase(pinSearch);
        if (client != null) {
            //policyTrans.setClient(client);
            policyDto.setClientId(client.getTenId());
        } else {
           throw new BadRequestException("Client with Kra Pin "+pinSearch+" not found");
        }
        //policyTrans.setAgent(creditLife.getInsurerCode());
        policyDto.setAgentId(creditLife.getInsurerCode().getAcctId());

        String searchCode = "CLPU";
        String normalizedPrdName = creditLife.getProductName().trim().toLowerCase().replace(" ", "");
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
            }
        } else {
            throw new BadRequestException(creditLife.getProductName() + " product is not found. Please set up the product.");
        }

        policyDto.setCurrencyId(creditLife.getCurrencyCode().getCurCode());

        policyDto.setBranchId(creditLife.getBranch().getObId());

        System.out.println("getting binder for "+creditLife.getBinderTypeId());
        BindersDef polBinder =  bindersRepo.findByBindersBybinid(creditLife.getBinderTypeId());

        policyDto.setBindCode(polBinder.getBinId());

        PaymentModes paymentModes = paymentModeRepo.findOne(QPaymentModes.paymentModes.pmDesc.eq("CASH"));
        System.out.println("paymentModes id:: " + paymentModes.getPmId());
        policyDto.setPaymentId(paymentModes.getPmId());


        if (creditLife.getTransStatus().equalsIgnoreCase("N")) {

            String policyNumberFormat = paramService.getParameterString("POLICY_NO_FORMAT");
            String endorsementFormat = paramService.getParameterString("ENDORSE_NO_FORMAT");
            String proposalFormat = paramService.getParameterString("PROPOSAL_NO_FORMAT");
            Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("P");
            if (sequenceRepository.count(seqPredicate) == 0)
                throw new BadRequestException("Sequence for New Business Transactions has not been defined");
            SystemSequence sequence = sequenceRepository.findOne(seqPredicate);
            Long seqNumber = sequence.getNextNumber();

            sequence.setLastNumber(seqNumber);
            sequence.setNextNumber(seqNumber + 1);
           // sequenceRepository.save(sequence);
            Predicate endorsePredicate = QSystemSequence.systemSequence.transType.eq("E");
            if (sequenceRepository.count(endorsePredicate) == 0)
                throw new BadRequestException("Sequence for Endorsement Transactions has not been defined");
            SystemSequence endorseSequence = sequenceRepository.findOne(endorsePredicate);
            Long endosseqNumber = endorseSequence.getNextNumber();
            final String revNumber = endorseSequence.getSeqPrefix() + String.format("%05d", endosseqNumber);
            final String endorseNumber = templateMerger.generateFormat(endorsementFormat, creditLife.getBranch().getObId(), product.getProCode(), creditLife.getCoverFrom(), revNumber, null);
         //   policyTrans.setPolRevNo(endorseNumber + "/1");
           // policyTrans.setRevisionFormat(endorseNumber);
            endorseSequence.setLastNumber(endosseqNumber);
            endorseSequence.setNextNumber(endosseqNumber + 1);
            //sequenceRepository.save(endorseSequence);

            Predicate propPredicate = QSystemSequence.systemSequence.transType.eq("PR");
            if (sequenceRepository.count(propPredicate) == 0)
                throw new BadRequestException("Sequence for Proposal Transactions has not been defined");
            SystemSequence lifeSequence = sequenceRepository.findOne(propPredicate);
            Long lifeSeqNumber = lifeSequence.getNextNumber();
            final String proposalNo = templateMerger.generateFormat(proposalFormat, creditLife.getBranch().getObId(), product.getProCode(), wef, lifeSequence.getSeqPrefix() + String.format("%05d", lifeSeqNumber), null);
            if (proposalNo == null) {
                throw new BadRequestException("Proposal no has not been generated");
            }
            System.out.println("Proposal No " + proposalNo);
            //policyTrans.setProposalNo(proposalNo);
        }

//        policyTrans.setRenewable(product.isRenewable());
//        policyTrans.setTransType("BU");
//        policyTrans.setPolRevStatus("LD");
//        policyTrans.setInterfaceType("C");
//        policyTrans.setPolCreateddt(new Date());
        //savedPol = policyTransRepo.save(policyTrans);
        //creditLife.setPolicyTrans(savedPol);
        creditLife.setTransStatus("Y");
        creditLife.setProcessedDate(new Date());
        creditLife.setProcessedBy(user);
        creditLifeRepository.save(creditLife);


        //RiskTrans riskTrans = new RiskTrans();
        final String pinToSearch = creditLife.getClientPin().trim();
        final ClientDef insured = clientRepository.findByPinNoIgnoreCase(pinToSearch);
        if (insured != null) {
          //  riskTrans.setInsured(insured);
        } else {
            throw new BadRequestException("Insured is not registered");
        }
        Integer age1 = dateUtils.getAge(creditLife.getClientDOB());
        BigDecimal age = BigDecimal.valueOf(age1);

        //BindersDef newBinder = bindersRepo.findBinderByAccId(creditLife.getInsurerCode().getAcctId(), product.getProCode());
        BindersDef newBinder =  bindersRepo.findByBindersBybinid(creditLife.getBinderTypeId());
        String normalizedSubClass = creditLife.getProductName().trim().toLowerCase().replace(" ", "");
        String normalizedCoverType = creditLife.getCoverType().trim().toLowerCase().replace(" ", "");
        final SubClassDef subClassDef = subClassRepo.findBySubClassName(normalizedSubClass);
        final CoverTypesDef coverTypesDef = coverTypesRepo.findCoverTypesByBindId(newBinder.getBinId(), normalizedCoverType);
        System.out.println("Binder id ::" + newBinder.getBinId());
        System.out.println("CoverTypes id:: " + coverTypesDef.getCovId());
        BinderDetails newDetails = binderDetRepo.findOne(QBinderDetails.binderDetails.binder.binId.eq(newBinder.getBinId()).and(QBinderDetails.binderDetails.subCoverTypes.coverTypes.covId.eq(coverTypesDef.getCovId())));
        String businessType = product.getProGroup().getPrgType();
        if (!businessType.equalsIgnoreCase("L")) {
            //policyTrans.setBusinessType("N");
        } else {
            //policyTrans.setBusinessType("L");
        }

        RiskTransBean riskBean = new RiskTransBean();
        riskBean.setRiskId(null);
        //riskBean.setTransType(null);
        //riskBean.setRiskIdentifier(null);
        riskBean.setRiskShtDesc(creditLife.getLoanId());
        riskBean.setBindCode(newBinder.getBinId());
        riskBean.setWefDate(wef);
        riskBean.setWetDate(wet);
        riskBean.setInsuredCode(client.getTenId());
        riskBean.setSclCode(subClassDef.getSubId());
        riskBean.setComputeType("S");
        riskBean.setWorkingAge(age1);
        riskBean.setCoverCode(coverTypesDef.getCovId());
        riskBean.setBinderDet(newDetails.getDetId());
        riskBean.setPremium(creditLife.getCreditPremium().abs());
        riskBean.setSumInsured(creditLife.getSumInsured().abs());
        policyDto.setRiskBean(riskBean);
        policyDto.setNegotiatedPremium(creditLife.getCreditPremium().abs()); //to avoid calculator

        System.out.println("age is "+age);
        //RETRENCHMENT
        //Last Expense
        //TOTAL AND PERMANENT DISABILITY
        //DEATH
        List<RiskSectionBean> sections = uploadValidatorsUtils.saveSectionTransaction("SUM ASSURED",newDetails.getDetId(), new BigDecimal("26")); //buildSectionBeans("29226", new BigDecimal("26"), savedRiskTrans);
        policyDto.setSections(sections);


//        Integer age = dateUtils.getAge(creditLife.getClientDOB());
//        riskTrans.setWorkingAge(age);
//
//        CommissionRates commissionRate = commRatesRepo.findOne(QCommissionRates.commissionRates.bindersDef.binId.eq(newBinder.getBinId()));
//        AccountDef accountDef = accountRepo.findOne(QAccountDef.accountDef.accountType.accountType.eq(AccountTypeEnum.INS)
//                .and(QAccountDef.accountDef.acctId.eq(creditLife.getInsurerCode().getAcctId())));
//
//        BigDecimal commissionRates = (commissionRate != null) ? commissionRate.getCommRate() : null;
//        BigDecimal accountCommRates = (accountDef != null) ? accountDef.getAccountType().getCommRate() : null;
//
//        if (commissionRates != null && commissionRates.compareTo(BigDecimal.ZERO) > 0) {
//            riskTrans.setCommRate(commissionRates);
//        } else if (accountCommRates != null && accountCommRates.compareTo(BigDecimal.ZERO) > 0) {
//            riskTrans.setCommRate(accountCommRates);
//        } else {
//            throw new BadRequestException("Commission Rates is not setup");
//        }

     //   riskTransRepo.save(riskTrans);
//        long riskIdentifier = Long.valueOf(String.valueOf(dateUtils.getUwYear(policyTrans.getWefDate())) + String.valueOf(riskTrans.getRiskId()));
//        riskTrans.setRiskIdentifier(riskIdentifier);

        //for life only
        PolicyTrans createdPol = uploadValidatorsUtils.saveLifePolicyUpload(policyDto,userId);

        creditLife.setPolicyTrans(createdPol);
        creditLife.setPolicyAuthorized("N");
        creditLifeRepository.save(creditLife);

        return createdPol;
    }

    @Override
    public List<Long> processBulkCreditLifePol(BatchCreditBatch batchCreditBatch, boolean isApproved) throws BadRequestException {
        if (batchCreditBatch.getBatchRecords().size() <= 0) {
            throw new BadRequestException("Select At least One Transaction To Process");
        }

        Job job = JobBuilder.aNewJob()
                .reader(new IterableRecordReader(batchCreditBatch.getBatchRecords()))
                .named("credit_shield_processing" + new SimpleDateFormat("ddMMyyyhhmmss").format(new Date()))
                .processor((RecordProcessor<Record, Record>) renForm -> {
                    Long policyId = ((BatchRecord) renForm.getPayload()).getPayload().getBatchId();
                    Long userId = ((BatchRecord) renForm.getPayload()).getPayload().getUserId();
                    processSingleCreditLifePol(policyId, userId,true);
                    return renForm;
                })
                .pipelineListener(new RenewalJobListener())
                .build();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Future<JobReport> report = executorService.submit(job);

        return  new ArrayList<>();
//        if (creditIds.size() <= 0) {
//            throw new BadRequestException("Select At least One Transaction To Process");
//        }
//        PolicyTrans savedPol = null;
//        List<Long> processedPolicyIds = new ArrayList<>();
//        for (Long creditId : creditIds) {
//           savedPol = processSingleCreditLifePol(creditId, true);
//           processedPolicyIds.add(savedPol.getPolicyId());
//        }
//        return processedPolicyIds;
    }

    @Override
    public String approveSingleCreditLifePol(Long policyId) throws BadRequestException {
        authService.authorizeBulkUploadPolicies(policyId);
        CreditLife creditLife = creditLifeRepository.findCreditLifeByPolicyId(policyId);
        creditLife.setPolicyAuthorized("Y");
        creditLifeRepository.save(creditLife);

        return "Transaction Authorized Successfully";
    }

    @Override
    public String approveBulkCreditLifePol(List<Long> policyIds) throws BadRequestException {

        if (policyIds.size() <= 0){
            throw new BadRequestException("Select At least One Transaction To Process");
        }
        for(Long policyId : policyIds){
            authService.authorizeBulkUploadPolicies(policyId);
            CreditLife creditLife = creditLifeRepository.findCreditLifeByPolicyId(policyId);
            creditLife.setPolicyAuthorized("Y");
            creditLifeRepository.save(creditLife);
        }

        return "Transactions Authorized Successfully";
    }

    @Override
    @Transactional
    public String deleteUploadCreditLife(List<Long> creditIds) {

        for(Long creditId : creditIds) {
            // Use findById instead of findOne, as findOne is deprecated
            CreditLife creditLife = creditLifeRepository.findOne(creditId);

            // Check if the CreditLife exists and the transaction status is 'N'
            if((creditLife != null) && ("N".equals(creditLife.getTransStatus()))) {
                creditLifeRepository.delete(creditLife);
            }
        }

        return "Deleted successfully.";
    }

    @Override
    @Transactional
    public String deleteProcessedBulkCreditLifePol(List<Long> policyIds) throws BadRequestException {

        if (policyIds.size() <= 0){
            throw new BadRequestException("Select At least One Transaction To Process");
        }
        for(Long policyId : policyIds){
            //authService.authorizeBulkUploadPolicies(policyId)
            //delete the policy from the table
            CreditLife creditLife = creditLifeRepository.findCreditLifeByPolicyId(policyId);
            creditLifeRepository.delete(creditLife);
            //delete the created policy
            uploadValidatorsUtils.deleteprocessedPol(policyId);
        }

        return "Transactions Authorized Successfully";
    }
}
