package com.brokersystems.brokerapp.bulktransactions.service.impl;

import com.brokersystems.brokerapp.bulktransactions.dtos.CreditShieldDTO;
import com.brokersystems.brokerapp.bulktransactions.models.*;
import com.brokersystems.brokerapp.bulktransactions.models.CreditShield;
import com.brokersystems.brokerapp.bulktransactions.repositories.CreditCardRepository;
import com.brokersystems.brokerapp.bulktransactions.repositories.CreditShieldRepository;
import com.brokersystems.brokerapp.bulktransactions.service.CreditCardService;
import com.brokersystems.brokerapp.bulktransactions.service.CreditShieldService;
import com.brokersystems.brokerapp.bulktransactions.utils.ExcelReaderUtil;
import com.brokersystems.brokerapp.enums.AccountTypeEnum;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.DateUtilities;
import com.brokersystems.brokerapp.server.utils.Streamable;
import com.brokersystems.brokerapp.server.utils.TemplateMerger;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.*;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.brokersystems.brokerapp.trans.model.SystemTrans;
import com.brokersystems.brokerapp.trans.repository.SystemTransRepo;
import com.brokersystems.brokerapp.trans.repository.SystemTransactionsRepo;
import com.brokersystems.brokerapp.trans.repository.TransMappingRepo;
import com.brokersystems.brokerapp.trans.service.PolicyAuthorization;
import com.brokersystems.brokerapp.uw.dtos.PolicyCreateDTO;
import com.brokersystems.brokerapp.uw.model.*;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
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
import java.util.concurrent.TimeUnit;

import static org.apache.commons.collections4.ListUtils.partition;

@Service
public class CreditShieldServiceImpl implements CreditShieldService {

    @Autowired
    private CreditShieldRepository creditShieldRepo;
    @Autowired
    private SequenceRepository sequenceRepository;
    @Autowired
    private UserUtils userUtils;
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
    private PolicyAuthorization policyAuthorization;
    @Autowired
    private PaymentModeRepo paymentModeRepo;
    @Autowired
    private TransMappingRepo transMappingRepo;
    @Autowired
    private SystemTransRepo transRepo;
    @Autowired
    private SystemTransactionsRepo systemTransactionsRepo;
    @Autowired
    private UploadValidatorsUtils uploadValidatorsUtils;
    @Autowired
    private UserRepository userRepo;
    
    
    private  static final int BATCH_SIZE = 500;

    @Override
    @Async
    public void  uploadCreditShield(File file, Long userId) throws BadRequestException, IOException, InvalidFormatException {


        Map<String, Object> response = new HashMap<>();
        List<String> invalidRecords = new ArrayList<>();
        List<CreditShield> creditShieldList = new ArrayList<>();

        try {
            InputStream inputStream = Files.newInputStream(file.toPath());
            Workbook workbook = WorkbookFactory.create(inputStream);

            Sheet sheetOne = workbook.getSheetAt(0);
            int startRowIndex = findDataStartRow(sheetOne);
            if (startRowIndex == -1) {
                throw new BadRequestException("Sheet One contains no data.");
            }

            String bulkCode = null;
            String transType = "CSU";
            Predicate pedSystem = QSystemSequence.systemSequence.transType.eq("CSU");
            if (sequenceRepository.count(pedSystem) == 0)
                throw new BadRequestException("Sequence for Credit Shield Upload has not been defined");

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
                    CreditShield creditShield = new CreditShield();

                    String clntIdNo = ExcelReaderUtil.getCellValue(row, 0);
                    creditShield.setClientID(clntIdNo);
                    String cif = ExcelReaderUtil.getCellValue(row, 1);
                    creditShield.setClientCIF(cif);

                    final ClientDef client = clientRepository.findByCifandIdNoIgnoreCase(cif, clntIdNo);
                    if (client != null) {
                        creditShield.setClientFName(client.getFname());
                        creditShield.setClientOtherNames(client.getOtherNames());
                        creditShield.setClientDOB(client.getDob());
                        creditShield.setClientPin(client.getPinNo());
                        creditShield.setClientEmail(client.getEmailAddress());
                        creditShield.setClientPhone(client.getPhoneNo());
                        creditShield.setClientType(client.getTenantType());

                    } else {
                        throw new BadRequestException("Client with ID No "+ clntIdNo + " not found.");
                    }

                    creditShield.setSalesAgent(ExcelReaderUtil.getCellValue(row, 2));
                    creditShield.setSalesManager(ExcelReaderUtil.getCellValue(row, 3));

                    String subagentCode = ExcelReaderUtil.getCellValue(row, 4).trim();
                    if (!subagentCode.isEmpty()) {
                        System.out.println("subagent " + subagentCode);
                        AccountDef subAgent = accountRepo.findBulkSubAgentBySubAgentABNo(subagentCode);

                        if (subAgent == null) {
                            creditShield.setSalesCode(null);
                        } else {
                            System.out.println("subagent " + subAgent.getAcctId());
                            creditShield.setSalesCode(String.valueOf(subAgent.getAcctId()));
                        }
                    }

                    creditShield.setCardType(ExcelReaderUtil.getCellValue(row, 5)); //getCellStringValue(row.getCell(12)));
                    creditShield.setCardAccount(ExcelReaderUtil.getCellValue(row, 6)); //getCellStringValue(row.getCell(13)));

                    String insurerCode = ExcelReaderUtil.getCellValue(row, 7); //getCellStringValue(row.getCell(14));
                    AccountDef underWriter = accountRepo.findOne(QAccountDef.accountDef.shtDesc.eq(insurerCode.trim()));
                    if (underWriter != null) {
                        creditShield.setInsurerCode(underWriter);
                    } else {
                        throw new BadRequestException("Insurer with code " + insurerCode + " is not setup in the system, please contact system admin");
                    }
                    creditShield.setProductGroup(ExcelReaderUtil.getCellValue(row, 8)); //getCellStringValue(row.getCell(15)));
                    String productName = (ExcelReaderUtil.getCellValue(row, 9)); //getCellStringValue(row.getCell(16)));
                    String searchCode = "CS";
                    String normalizedProductName = productName.trim().replaceAll("\\s+", " ").toLowerCase();
                    System.out.println("finding product " + normalizedProductName + " " + searchCode);
                    ProductsDef product = productsRepo.findByNormalizedName(normalizedProductName, searchCode);
                    if (product == null) {
                        throw new BadRequestException("Product " + productName + " not set in the system, please contact system admin.");
                    }
                    System.out.println("Getting bidders by " + creditShield.getInsurerCode().getAcctId() + " " + product.getProCode());
                    BindersDef binder = bindersRepo.findBinderByAccId(creditShield.getInsurerCode().getAcctId(), product.getProCode());
                    if (binder == null) {
                        throw new BadRequestException("Binder for product " + productName + " and insurer with code " + creditShield.getInsurerCode().getShtDesc()
                                + " not set in the system, please contact system admin.");
                    }

                    String coverType = (ExcelReaderUtil.getCellValue(row, 10)); //getCellStringValue(row.getCell(17)));
                    String normalizedCoverType = coverType.trim().toLowerCase().replaceAll("\\s+", " ");
                    System.out.println("getting Subclassdef " + normalizedProductName);
                    SubClassDef subClassDef = subClassRepo.findBySubClassName(normalizedProductName);
                    if (subClassDef != null) {
                        creditShield.setProductName(subClassDef.getSubDesc());
                    } else {
                        throw new BadRequestException("Product " + productName + " not set in the system, please contact system admin.");
                    }
                    System.out.println("Getting CoverTypesDef " + binder.getBinId() + " " + normalizedCoverType);
                    CoverTypesDef coverTypesDef = coverTypesRepo.findCoverTypesByBindId(binder.getBinId(), normalizedCoverType);
                    if (coverTypesDef != null) {
                        creditShield.setCoverType(coverTypesDef.getCovName());
                    } else {
                        throw new BadRequestException("Cover type " + coverType + " not set for insurer with code " + creditShield.getInsurerCode().getShtDesc() + " please contact system admin.");
                    }
                    creditShield.setFrequency(ExcelReaderUtil.validateFrequency(ExcelReaderUtil.getCellValue(row, 11), row.getRowNum())); //getCellStringValue(row.getCell(18)), row.getRowNum()));
                    String currency = ExcelReaderUtil.getCellValue(row, 12); //getCellStringValue(row.getCell(19));
                    BigDecimal premium = ExcelReaderUtil.parseBigDecimalSafe(ExcelReaderUtil.getCellValue(row, 14)); //getCellBigDecimalValue(row.getCell(21)));
                    premium = premium.abs();
                    if (currency != null && !currency.trim().isEmpty()) {
                        currency = currency.trim();
                        Currencies currencies = currencyRepository.findOne(QCurrencies.currencies.curIsoCode.eq(currency));
                        if (currencies == null) {
                            throw new BadRequestException("Currency code " + currency + " is not setup in the system, please contact system admin");
                        }
                        //ExcelReaderUtil.validateCurrency(currencies.getCurIsoCode(), premium);
                        creditShield.setCurrencyCode(currencies);
                    } else {
                        throw new BadRequestException("Currency is required in row " + row.getRowNum() + " in Currency ISO code column");
                    }

                    String branchCode = ExcelReaderUtil.getCellValue(row, 13); // getCellStringValue(row.getCell(20));
                    System.out.println("Branch Code.."+branchCode);
                    OrgBranch orgBranch = orgBranchRepository.findOne(QOrgBranch.orgBranch.obShtDesc.eq(branchCode.trim()));
                    if (orgBranch != null) {
                        creditShield.setBranch(orgBranch);
                    } else {
                        throw new BadRequestException("Branch code " + branchCode + " in sheet two is not setup in the system, please contact system admin.");
                    }
                    creditShield.setCardPremium(premium);
                    creditShield.setTransDate(ExcelReaderUtil.parseFlexibleDate(ExcelReaderUtil.getCellValue(row, 15))); //getCellDateValue(row.getCell(22)));
                    creditShield.setCoverFrom(ExcelReaderUtil.parseFlexibleDate(ExcelReaderUtil.getCellValue(row, 16))); //getCellDateValue(row.getCell(23)));
                    creditShield.setCoverTo(ExcelReaderUtil.parseFlexibleDate(ExcelReaderUtil.getCellValue(row, 17))); //getCellDateValue(row.getCell(24)));

                    if (ExcelReaderUtil.parseFlexibleDate(ExcelReaderUtil.getCellValue(row, 16)).after(ExcelReaderUtil.parseFlexibleDate(ExcelReaderUtil.getCellValue(row, 17))))
                        throw new BadRequestException("Wef Date cannot be greater than Wet Date");

                    creditShield.setUploadedDate(new Date());
                    User user = userRepo.findOne(userId);
                    creditShield.setUploadedBy(user);
                    creditShield.setTransStatus("N");
                    creditShield.setTransType(transType);
                    creditShield.setTransCode(bulkCode);

                    creditShieldList.add(creditShield);
                    if(creditShieldList.size() >= BATCH_SIZE) {
                        creditShieldRepo.save(creditShieldList);
                        creditShieldList.clear();
                    }
                }
//                catch (Exception e) {
//                    throw new BadRequestException("Sheet One " + e.getMessage());
//                }
                finally {
                    if (file != null && file.exists()) {
                        boolean deleted = file.delete();
                        if (!deleted) {
                            System.err.println("Warning: Failed to delete temp file: " + file.getAbsolutePath());
                        }
                    }
                }
            }

            int successfulPol = creditShieldList.size();
            int invalidPol = invalidRecords.size();
            //if (!(invalidPol > 0) && successfulPol > 0) {
            if(!creditShieldList.isEmpty()) {
                creditShieldRepo.save(creditShieldList);
                creditShieldList.clear();
            }
            //}

//            response.put("successfulPolicies", successfulPol);
//            response.put("failedRecords", invalidPol);
//            response.put("invalidRecords", invalidRecords);
//
//            return response;

//        } catch (Exception e) {
//            throw new BadRequestException("Error processing the Excel file: " + e.getMessage());
//        }
        }
        finally {

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
    public DataTablesResult<CreditShieldDTO> findUnprocessedCreditShield(DataTablesRequest request) {
        Long currentUserId = userUtils.getCurrentUser().getId();

        List<Object[]> credits = creditShieldRepo.findUnprocessedCreditCard((request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%", request.getPageNumber(), request.getPageSize(), currentUserId);
        final List<CreditShieldDTO> packageInsuranceDTOS = new ArrayList<>();
        long rowCount = 0L;
        if (!credits.isEmpty())
            rowCount = ((BigInteger) credits.get(0)[9]).intValue();
        for (Object[] credit : credits) {
            CreditShieldDTO creditShieldDTO = new CreditShieldDTO();
            creditShieldDTO.setShieldId(((BigInteger) credit[0]).longValue());
            creditShieldDTO.setCoverType((String) credit[1]);
            creditShieldDTO.setProductName((String) credit[2]);
            creditShieldDTO.setInsurerName((String) credit[3]);
            creditShieldDTO.setClientFName((String) credit[4]);
            creditShieldDTO.setClientOtherNames((String) credit[5]);
            creditShieldDTO.setCoverFrom((Date) credit[6]);
            creditShieldDTO.setCoverTo((Date) credit[7]);
            creditShieldDTO.setUploadedDate((Date) credit[8]);

            packageInsuranceDTOS.add(creditShieldDTO);
        }
        Page<CreditShieldDTO> page = new PageImpl<>(packageInsuranceDTOS, request, rowCount);

        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<CreditShieldDTO> viewUnprocessedCreditShield(DataTablesRequest request) {
        Long currentUserId = userUtils.getCurrentUser().getId();

        List<Object[]> viewCredit = creditShieldRepo.viewUnprocessedCreditCard((request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%", request.getPageNumber(), request.getPageSize(), currentUserId);
        final List<CreditShieldDTO> insuranceDTOS = new ArrayList<>();
        long rowCount = 0L;
        if (!viewCredit.isEmpty()) rowCount = ((BigInteger) viewCredit.get(0)[10]).intValue();
        for (Object[] viewCredits : viewCredit) {
            CreditShieldDTO creditShieldDTO = new CreditShieldDTO();
            creditShieldDTO.setShieldId(((BigInteger) viewCredits[0]).longValue());
            creditShieldDTO.setCoverType((String) viewCredits[1]);
            creditShieldDTO.setPolNo((String) viewCredits[2]);
            
            creditShieldDTO.setCardPremium((BigDecimal) viewCredits[3]);
            creditShieldDTO.setClientFName((String) viewCredits[4]);
            creditShieldDTO.setClientOtherNames((String) viewCredits[5]);
            creditShieldDTO.setCoverFrom((Date) viewCredits[6]);
            creditShieldDTO.setCoverTo((Date) viewCredits[7]);
            creditShieldDTO.setProcessedDate((Date) viewCredits[8]);
            creditShieldDTO.setTransStatus((String) viewCredits[9]);

            insuranceDTOS.add(creditShieldDTO);
        }
        Page<CreditShieldDTO> page = new PageImpl<>(insuranceDTOS, request, rowCount);

        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional( propagation = Propagation.REQUIRED)
    public PolicyTrans processSingleCreditShieldPol(Long cardId,Long userId, boolean isApproved) throws BadRequestException {
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

        PolicyTrans savedPol = null;
        CreditShield creditShield = creditShieldRepo.findOne(cardId);
        if (creditShield.getTransStatus().equalsIgnoreCase("Y")) {
            throw new BadRequestException("This transaction is already processed");
        }
        //final PolicyTrans policyTrans = new PolicyTrans();
        Date wef = creditShield.getCoverFrom();
        Date wet = creditShield.getCoverTo();
        System.out.println("wet pol" + wet);
//        policyTrans.setPolCreateddt(new Date());
//        policyTrans.setAuthStatus("LD");
//        policyTrans.setCurrentStatus("LD");

//        policyTrans.setWefDate(wef);
        policyDto.setWetDate(wet);
//
//        policyTrans.setWetDate(wet);
        policyDto.setWefDate(wef);
//
//        policyTrans.setTotalInstalments(1);

        //policyTrans.setPolTerm(1);
        policyDto.setPolTerm(1);

        String frequency = creditShield.getFrequency();
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
            throw new BadRequestException("Payment frequency is not defined for client ID " + creditShield.getClientID() + " in frequency column in uploaded excel");
        }
        final String pinSearch = creditShield.getClientPin().trim();
        final ClientDef client = clientRepository.findByPinNoIgnoreCase(pinSearch);
        if (client != null) {
            //policyTrans.setClient(client);
            policyDto.setClientId(client.getTenId());
        } else {
           throw new BadRequestException("Client with Kra pin"+pinSearch+" not found");
        }
        //policyTrans.setAgent(creditShield.getInsurerCode());
        policyDto.setAgentId(creditShield.getInsurerCode().getAcctId());

        String searchCode = "CS";
        String normalizedPrdName = creditShield.getProductName().trim().toLowerCase().replace(" ", "");
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
            throw new BadRequestException(creditShield.getProductName() + " product is not found. Please set up the product.");
        }
        //policyTrans.setTransCurrency(creditShield.getCurrencyCode());
        policyDto.setCurrencyId(creditShield.getCurrencyCode().getCurCode());

        //policyTrans.setBranch(creditShield.getBranch());
        policyDto.setBranchId(creditShield.getBranch().getObId());

//        policyTrans.setUwYear(dateUtils.getUwYear(wef));
//        policyTrans.setCreatedUser(userUtils.getCurrentUser());
//        policyTrans.setPreviousTrans(policyTrans);
        BindersDef polBinder = bindersRepo.findBinderByAccId(creditShield.getInsurerCode().getAcctId(), product.getProCode());
        //policyTrans.setBinder(polBinder);
        policyDto.setBindCode(polBinder.getBinId());

//        if((polBinder.getAdminFeeActiveStatus() != null) && (polBinder.getAdminFeeActiveStatus().equalsIgnoreCase("Y"))) {
//            policyTrans.setAdminFeeApplicable("Y");
//        } else {
//            policyTrans.setAdminFeeApplicable("N");
//        }
//        if (creditShield.getSalesCode() != null) {
//            policyTrans.setSubAgent(accountRepo.findOne(Long.valueOf(creditShield.getSalesCode())));
//        }
        if(creditShield.getSalesCode() != null) {
            policyDto.setSubAgentId(Long.valueOf(creditShield.getSalesCode()));
            //  policyDto.setAbsaNoSubAgent(creditShield.getSalesCode());
        } else{
            policyDto.setSubAgentId(null);
        }

        PaymentModes paymentModes = paymentModeRepo.findOne(QPaymentModes.paymentModes.pmDesc.eq("CASH"));
        System.out.println("paymentModes id:: " + paymentModes.getPmId());
        //policyTrans.setPaymentMode(paymentModes);
        policyDto.setPaymentId(paymentModes.getPmId());

        //SystemTrans systemTrans = null;
        if (creditShield.getTransStatus().equalsIgnoreCase("N")) {

            String policyNumberFormat = paramService.getParameterString("POLICY_NO_FORMAT");
            String endorsementFormat = paramService.getParameterString("ENDORSE_NO_FORMAT");
            String proposalFormat = paramService.getParameterString("PROPOSAL_NO_FORMAT");
            Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("P");
            if (sequenceRepository.count(seqPredicate) == 0)
                throw new BadRequestException("Sequence for New Business Transactions has not been defined");
            SystemSequence sequence = sequenceRepository.findOne(seqPredicate);
            Long seqNumber = sequence.getNextNumber();
            //final String policyNumber = templateMerger.generateFormat(policyNumberFormat, creditShield.getBranch().getObId(), product.getProCode(), policyTrans.getWefDate(), sequence.getSeqPrefix() + String.format("%05d", seqNumber), null);
          //  policyTrans.setPolNo(policyNumber);

            sequence.setLastNumber(seqNumber);
            sequence.setNextNumber(seqNumber + 1);
            sequenceRepository.save(sequence);
            Predicate endorsePredicate = QSystemSequence.systemSequence.transType.eq("E");
            if (sequenceRepository.count(endorsePredicate) == 0)
                throw new BadRequestException("Sequence for Endorsement Transactions has not been defined");
            SystemSequence endorseSequence = sequenceRepository.findOne(endorsePredicate);
            Long endosseqNumber = endorseSequence.getNextNumber();
            final String revNumber = endorseSequence.getSeqPrefix() + String.format("%05d", endosseqNumber);
            final String endorseNumber = templateMerger.generateFormat(endorsementFormat, creditShield.getBranch().getObId(), product.getProCode(), creditShield.getCoverFrom(), revNumber, null);
//            policyTrans.setPolRevNo(endorseNumber + "/1");
//            policyTrans.setRevisionFormat(endorseNumber);
            endorseSequence.setLastNumber(endosseqNumber);
            endorseSequence.setNextNumber(endosseqNumber + 1);
            sequenceRepository.save(endorseSequence);

            Predicate propPredicate = QSystemSequence.systemSequence.transType.eq("PR");
            if (sequenceRepository.count(propPredicate) == 0)
                throw new BadRequestException("Sequence for Proposal Transactions has not been defined");
            SystemSequence lifeSequence = sequenceRepository.findOne(propPredicate);
            Long lifeSeqNumber = lifeSequence.getNextNumber();
            final String proposalNo = templateMerger.generateFormat(proposalFormat, creditShield.getBranch().getObId(), product.getProCode(), wef, lifeSequence.getSeqPrefix() + String.format("%05d", lifeSeqNumber), null);
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
        //creditShield.setPolicyTrans(savedPol);
        creditShield.setTransStatus("Y");
        creditShield.setProcessedDate(new Date());
        creditShield.setProcessedBy(user);
        creditShieldRepo.save(creditShield);

//        systemTrans = new SystemTrans();
//        systemTrans.setDoneDate(new Date());
//        systemTrans.setDoneBy(userUtils.getCurrentUser());
//        systemTrans.setPolicy(savedPol);
//        systemTrans.setTransLevel("U");
//        systemTrans.setTransCode("BUD");
//        systemTrans.setTransAuthorised("N");
//        systemTransRepo.save(systemTrans);

//        RiskTrans riskTrans = new RiskTrans();
        final String pinToSearch = creditShield.getClientPin().trim();
        final ClientDef insured = clientRepository.findByPinNoIgnoreCase(pinToSearch);
        if (insured != null) {
//            riskTrans.setInsured(insured);
        } else {
            throw new BadRequestException("Insured is not registered");
        }

        Integer age1 = dateUtils.getAge(creditShield.getClientDOB());
        BigDecimal age = BigDecimal.valueOf(age1);

        BindersDef newBinder = bindersRepo.findBinderByAccId(creditShield.getInsurerCode().getAcctId(), product.getProCode());
        String normalizedSubClass = creditShield.getProductName().trim().toLowerCase().replace(" ", "");
        String normalizedCoverType = creditShield.getCoverType().trim().toLowerCase().replace(" ", "");
        final SubClassDef subClassDef = subClassRepo.findBySubClassName(normalizedSubClass);
        final CoverTypesDef coverTypesDef = coverTypesRepo.findCoverTypesByBindId(newBinder.getBinId(), normalizedCoverType);
        System.out.println("Binder id ::" + newBinder.getBinId());
        System.out.println("CoverTypes id:: " + coverTypesDef.getCovId());
        BinderDetails newDetails = binderDetRepo.findOne(QBinderDetails.binderDetails.binder.binId.eq(newBinder.getBinId()).and(QBinderDetails.binderDetails.subCoverTypes.coverTypes.covId.eq(coverTypesDef.getCovId())));
        System.out.println("Binder Details..."+newDetails.getDetId());
        String businessType = product.getProGroup().getPrgType();
        if (!businessType.equalsIgnoreCase("L")) {
            //policyTrans.setBusinessType("N");
        } else {
            //policyTrans.setBusinessType("L");
        }
//        riskTrans.setBinder(newBinder);
//        riskTrans.setCovertype(coverTypesDef);
//        riskTrans.setSubclass(subClassDef);
//        riskTrans.setBinderDetails(newDetails);
//        riskTrans.setAutogenCert("N");
//        riskTrans.setPolicy(savedPol);
//        riskTrans.setComputePremium(creditShield.getCardPremium());
//        riskTrans.setWefDate(wef);
//        riskTrans.setWetDate(wet);
//        riskTrans.setPremium(creditShield.getCardPremium());
//        riskTrans.setSumInsured(BigDecimal.ZERO);
//        riskTrans.setTransType("BU");

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
        riskBean.setWorkingAge(age1);
        riskBean.setCoverCode(coverTypesDef.getCovId());
        riskBean.setBinderDet(newDetails.getDetId());
        riskBean.setPremium(creditShield.getCardPremium());
        riskBean.setSumInsured(BigDecimal.ZERO);
        policyDto.setRiskBean(riskBean);
        policyDto.setNegotiatedPremium(creditShield.getCardPremium()); //to avoid calculator



        //sections
        //card balance above 3M without retrenchment benefit
        //card balance below 3M with retrenchment benefit
        //card balance above 3M with retrenchment benefit
        List<RiskSectionBean> sections = uploadValidatorsUtils.saveSectionTransaction("CBB3M1",newDetails.getDetId(), creditShield.getCardPremium().abs()); //buildSectionBeans("29226", new BigDecimal("26"), savedRiskTrans);
        policyDto.setSections(sections);
//
//        CommissionRates commissionRate = commRatesRepo.findOne(QCommissionRates.commissionRates.bindersDef.binId.eq(newBinder.getBinId()));
//        AccountDef accountDef = accountRepo.findOne(QAccountDef.accountDef.accountType.accountType.eq(AccountTypeEnum.INS)
//                .and(QAccountDef.accountDef.acctId.eq(creditShield.getInsurerCode().getAcctId())));
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

       // riskTransRepo.save(riskTrans);
//        long riskIdentifier = Long.valueOf(String.valueOf(dateUtils.getUwYear(policyTrans.getWefDate())) + String.valueOf(riskTrans.getRiskId()));
//        riskTrans.setRiskIdentifier(riskIdentifier);
        //
        //for life only
        //uploadValidatorsUtils.savePolicyInstallments(savedPol, savedPol.getBasicPrem(), wef, frequency);
        PolicyTrans createdPol = uploadValidatorsUtils.saveLifePolicyUpload(policyDto,userId);

        uploadValidatorsUtils.updateBasicNet(creditShield.getCardPremium(),BigDecimal.ZERO, createdPol);

        creditShield.setPolicyTrans(createdPol);
        creditShield.setPolicyAuthorized("N");
        creditShieldRepo.save(creditShield);

        return createdPol;
    }

    @Override
    public List<Long> processBulkCreditShieldPol(BatchCreditBatch batchCreditBatch, boolean isApproved) throws BadRequestException {
        if (batchCreditBatch.getBatchRecords().size() <= 0) {
            throw new BadRequestException("Select At least One Transaction To Process");
        }

        Job job = JobBuilder.aNewJob()
                .reader(new IterableRecordReader(batchCreditBatch.getBatchRecords()))
                .named("credit_shield_processing" + new SimpleDateFormat("ddMMyyyhhmmss").format(new Date()))
                .processor((RecordProcessor<Record, Record>) renForm -> {
                    Long policyId = ((BatchRecord) renForm.getPayload()).getPayload().getBatchId();
                    Long userId = ((BatchRecord) renForm.getPayload()).getPayload().getUserId();
                    processSingleCreditShieldPol(policyId, userId,true);
                    return renForm;
                })
                .pipelineListener(new RenewalJobListener())
                .build();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Future<JobReport> report = executorService.submit(job);

        return  new ArrayList<>();
//        List<Long> processedPolicyIds = new ArrayList<>();
//        for (Long cardId : cardIds) {
//            PolicyTrans savedPol = processSingleCreditShieldPol(cardId, true);
//            processedPolicyIds.add(savedPol.getPolicyId());
//        }
//        return processedPolicyIds;
        }



    @Override
    public String approveSingleCreditShieldPol(Long policyId) throws BadRequestException {
        authService.authorizeBulkUploadPolicies(policyId);
        CreditShield creditShield = creditShieldRepo.findBulkStockByPolicyId(policyId);
        creditShield.setPolicyAuthorized("Y");
        creditShieldRepo.save(creditShield);
        return "Transaction Authorized Successfully";
    }

    @Override
    public String approveBulkCreditShieldPol(List<Long> policyIds) throws BadRequestException {

        if (policyIds.size() <= 0){
            throw new BadRequestException("Select At least One Transaction To Process");
        }
        for(Long policyId : policyIds){
            authService.authorizeBulkUploadPolicies(policyId);
            CreditShield creditShield = creditShieldRepo.findBulkStockByPolicyId(policyId);
            creditShield.setPolicyAuthorized("Y");
            creditShieldRepo.save(creditShield);
        }

        return "Transactions Authorized Successfully";
    }

    @Override
    @Transactional
    public String deleteBulkCreditShieldPol(List<Long> cardIds) throws BadRequestException {
        if (cardIds.size() <= 0) {
            throw new BadRequestException("Select At least One Transaction To Process");
        }
        for(Long cardId : cardIds){
            CreditShield creditShield = creditShieldRepo.findOne(cardId);
            if("N".equalsIgnoreCase(creditShield.getTransStatus())){
                creditShieldRepo.delete(creditShield);
            }
        }
        return "Transactions Deleted Successfully";
    }

    @Override
    @Transactional
    public String deleteProcessedBulkCreditShieldPol(List<Long> cardIds) throws BadRequestException {

        if (cardIds.size() <= 0){
            throw new BadRequestException("Select At least One Transaction To Process");
        }
        for(Long cardId : cardIds){
            //authService.authorizeBulkUploadPolicies(policyId)
            //delete the policy from the table
            CreditShield creditShield = creditShieldRepo.findBulkStockByPolicyId(cardId);
            creditShieldRepo.delete(creditShield);
            //delete the created policy
            uploadValidatorsUtils.deleteprocessedPol(cardId);
        }

        return "Transactions Authorized Successfully";
    }


    @Override
    public List<Long> getAllCreditShieldIds() {
        return creditShieldRepo.findAllIds();
    }
}
