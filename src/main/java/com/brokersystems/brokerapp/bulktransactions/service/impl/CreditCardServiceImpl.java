package com.brokersystems.brokerapp.bulktransactions.service.impl;

import com.brokersystems.brokerapp.bulktransactions.dtos.CreditCardDTO;
import com.brokersystems.brokerapp.bulktransactions.models.*;
import com.brokersystems.brokerapp.bulktransactions.repositories.CreditCardRepository;
import com.brokersystems.brokerapp.bulktransactions.service.CreditCardService;
import com.brokersystems.brokerapp.bulktransactions.utils.ExcelReaderUtil;
import com.brokersystems.brokerapp.enums.AccountTypeEnum;
import com.brokersystems.brokerapp.enums.RevenueItems;
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
import com.brokersystems.brokerapp.uw.repository.*;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import com.brokersystems.brokerapp.uw.service.PremComputeService;
import com.mysema.query.types.Predicate;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CreditCardServiceImpl implements CreditCardService {

    @Autowired
    private CreditCardRepository creditCradRepo;
    @Autowired
    private SequenceRepository sequenceRepository;
    @Autowired
    private UserUtils userUtils;
    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private UploadValidatorsUtils uploadValidatorsService;
    @Autowired
    private PremComputeService premComputeServiceImpl;
    @Autowired
    private PremRatesRepo premRatesRepo;
    @Autowired
    private PremComputeService premiumService;
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private TaxRatesRepo taxRatesRepo;
    @Autowired
    private PolTaxesRepo polTaxesRepo;

    @Autowired
    private UserBranchesRepository userBranchesRepository;
    @Autowired
    private ClientTypeRepo clientTypeRepo;
    @Autowired
    private SequenceRepository sequenceRepo;
    @Autowired
    private OrgBranchRepository orgBranchRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private PolicyTransRepo policyTransRepo;
    @Autowired
    private SectionRepo sectionsRepo;

    @Autowired
    private SectionTransRepo sectionTransRepo;
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
    private PolActiveRisksRepo activeRisksRepo;

    @Override
    public Map<String, Object> uploadCreditCard(MultipartFile file) throws BadRequestException {
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            throw new BadRequestException("Upload files with .xlsx or .xls extension only");
        }

        Map<String, Object> response = new HashMap<>();
        List<String> invalidRecords = new ArrayList<>();
        List<CreditCard> creditCardList = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);

            // Validate template format
            List<String> validationErrors = ExcelReaderUtil.validateCreditCardPolicyTemplate(workbook);
            if (!validationErrors.isEmpty()) {
                StringBuilder errorMessage = new StringBuilder("Template validation failed: ");
                for (String error : validationErrors) {
                    errorMessage.append(error).append("; ");
                }
                throw new BadRequestException(errorMessage.toString());
            }

            Sheet sheetOne = workbook.getSheetAt(0);
            int startRowIndex = findDataStartRow(sheetOne);
            if (startRowIndex == -1) {
                throw new BadRequestException("Sheet One contains no data.");
            }

            String bulkCode = null;
            String transType = "CCU";
            Predicate pedSystem = QSystemSequence.systemSequence.transType.eq("CCU");
            if (sequenceRepository.count(pedSystem) == 0)
                throw new BadRequestException("Sequence for Credit Card Upload has not been defined");

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
                    CreditCard creditCard = new CreditCard();
                    String clntIdNo = ExcelReaderUtil.getCellValue(row, 0); //getCellStringValue(row.getCell(4));
                    String cif = ExcelReaderUtil.getCellValue(row, 1); //getCellStringValue(row.getCell(4));
                    creditCard.setClientCIF(cif); //getCellStringValue(row.getCell(10)));
                    creditCard.setClientID(clntIdNo); //getCellStringValue(row.getCell(4)));
                    final ClientDef client = clientRepository.findByCifandIdNoIgnoreCase(cif, clntIdNo);
                    if (client != null) {
                        creditCard.setClientFName(client.getFname());
                        creditCard.setClientOtherNames(client.getOtherNames());
                        creditCard.setClientDOB(client.getDob());
                        creditCard.setClientPin(client.getPinNo());
                        creditCard.setClientEmail(client.getEmailAddress());
                        creditCard.setClientPhone(client.getPhoneNo());
                        creditCard.setClientType(client.getTenantType());

                    } else {
                        throw new BadRequestException("Client with ID No "+ clntIdNo + " not found.");
                    }

//                    creditCard.setClientFName(ExcelReaderUtil.getCellValue(row, 0)); //getCellStringValue(row.getCell(0)));
//                    creditCard.setClientOtherNames(ExcelReaderUtil.getCellValue(row, 1)); //getCellStringValue(row.getCell(1)));
//                    creditCard.setClientDOB(ExcelReaderUtil.parseFlexibleDate(ExcelReaderUtil.getCellValue(row, 2))); //ExcelReaderUtil.getCellValue(row, 2)); //getCellDateValue(row.getCell(2)));
//                    creditCard.setClientID(ExcelReaderUtil.getCellValue(row, 3)); //getCellStringValue(row.getCell(3)));
//                    creditCard.setClientPin(ExcelReaderUtil.getCellValue(row, 4)); //getCellStringValue(row.getCell(4)));
//                    creditCard.setClientEmail(ExcelReaderUtil.getCellValue(row, 5)); //getCellStringValue(row.getCell(5)));
//                    creditCard.setClientPhone(ExcelReaderUtil.getCellValue(row, 6)); //getCellStringValue(row.getCell(6)));
//                    creditCard.setClientCIF(ExcelReaderUtil.getCellValue(row, 7)); //getCellStringValue(row.getCell(7)));
//                    String clientType = ExcelReaderUtil.getCellValue(row, 8); //getCellStringValue(row.getCell(8));
//                    String normalizedClientType = clientType.trim().toLowerCase().replace(" ", "");
//                    ClientTypes clientTypes = clientTypeRepo.findByNormalizeType(normalizedClientType);
//                    if (clientTypes != null) {
//                        creditCard.setClientType(clientTypes);
//                    } else {
//                        throw new BadRequestException("Client type " + clientType + " is not setup, please contact system admin");
//                    }

                    creditCard.setSalesAgent(ExcelReaderUtil.getCellValue(row, 2)); //getCellStringValue(row.getCell(9)));
                    creditCard.setSalesManager(ExcelReaderUtil.getCellValue(row, 3)); //getCellStringValue(row.getCell(10)));

                    String subagentCode = ExcelReaderUtil.getCellValue(row, 4).trim();
                    if(!subagentCode.isEmpty()) {
                        System.out.println("subagent "+subagentCode);
                        AccountDef subAgent = accountRepo.findBulkSubAgentBySubAgentABNo(subagentCode);
                        if(subAgent == null){
                            throw new BadRequestException("Sub Agent with "+subagentCode+"not found.");
                        }
                        System.out.println("subagent "+subAgent.getAcctId());
                        creditCard.setSalesCode(subAgent.getAcctId()); //getCellStringValue(row.getCell(11)));
                    }
                    creditCard.setCardType(ExcelReaderUtil.getCellValue(row, 5)); //getCellStringValue(row.getCell(12)));
                    creditCard.setCardAccount(ExcelReaderUtil.getCellValue(row, 6)); //getCellStringValue(row.getCell(13)));


                    String insurerCode = ExcelReaderUtil.getCellValue(row, 7); // getCellStringValue(row.getCell(14));
                    AccountDef underWriter = accountRepo.findOne(QAccountDef.accountDef.shtDesc.eq(insurerCode.trim()));
                    if (underWriter != null) {
                        creditCard.setInsurerCode(underWriter);
                    } else {
                        throw new BadRequestException("Insurer with code " + insurerCode + " is not setup in the system, please contact system admin");
                    }
                    creditCard.setProductGroup(ExcelReaderUtil.getCellValue(row, 8)); //getCellStringValue(row.getCell(15)));
                    String productName = (ExcelReaderUtil.getCellValue(row, 9)); ////getCellStringValue(row.getCell(16)));
                    String searchCode = "ECCPU";
                    String normalizedProductName = productName.trim().replaceAll("\\s+", " ").toLowerCase();
                    System.out.println("finding product "+ normalizedProductName +" "+searchCode);
                    ProductsDef product = productsRepo.findByNormalizedName(normalizedProductName, searchCode);
                    if (product == null) {
                        throw new BadRequestException("Product " + productName + " not set in the system, please contact system admin.");
                    }
                    System.out.println("Getting bidders by "+ creditCard.getInsurerCode().getAcctId() + " "+ product.getProCode());
                    BindersDef binder = bindersRepo.findBinderByAccId(creditCard.getInsurerCode().getAcctId(), product.getProCode());
                    if (binder == null) {
                        throw new BadRequestException("Binder for product " + productName + " and insurer with code " + creditCard.getInsurerCode().getShtDesc()
                                + " not set in the system, please contact system admin.");
                    }
                    String coverType = (ExcelReaderUtil.getCellValue(row, 10)); //getCellStringValue(row.getCell(17)));
                    String normalizedCoverType = coverType.trim().toLowerCase().replaceAll("\\s+", " ");
                    System.out.println("getting Subclassdef "+ normalizedProductName);
                    SubClassDef subClassDef = subClassRepo.findBySubClassName(normalizedProductName);
                    if (subClassDef != null){
                        creditCard.setProductName(subClassDef.getSubDesc());
                    } else {
                        throw new BadRequestException("Product " + productName + " not set in the system, please contact system admin.");
                    }
                    System.out.println("Getting CoverTypesDef "+binder.getBinId() + " "+  normalizedCoverType );
                    CoverTypesDef coverTypesDef = coverTypesRepo.findCoverTypesByBindId(binder.getBinId(), normalizedCoverType);
                    if (coverTypesDef != null) {
                        creditCard.setCoverType(coverTypesDef.getCovName());
                    }else {
                        throw new BadRequestException("Cover type " + coverType + " not set for insurer with code " + creditCard.getInsurerCode().getShtDesc() +" please contact system admin.");
                    }
                    creditCard.setFrequency(ExcelReaderUtil.validateFrequency(ExcelReaderUtil.getCellValue(row, 11), row.getRowNum())); // //getCellStringValue(row.getCell(18)), row.getRowNum()));
                    String currency = ExcelReaderUtil.getCellValue(row, 12); // getCellStringValue(row.getCell(19));
                    if (currency != null && !currency.trim().isEmpty()) {
                        currency = currency.trim();
                        Currencies currencies = currencyRepository.findOne(QCurrencies.currencies.curIsoCode.eq(currency));
                        if (currencies == null) {
                            throw new BadRequestException("Currency code " + currency + " is not setup in the system, please contact system admin");
                        }
                        creditCard.setCurrencyCode(currencies);
                    } else {
                        throw new BadRequestException("Currency is required in row " + row.getRowNum() + " in Currency ISO code column");
                    }

                    String branchCode = ExcelReaderUtil.getCellValue(row, 13); //getCellStringValue(row.getCell(20));
                    OrgBranch orgBranch = orgBranchRepository.findOne(QOrgBranch.orgBranch.obShtDesc.eq(branchCode.trim()));
                    if (orgBranch != null) {
                        creditCard.setBranch(orgBranch);
                    } else {
                        throw new BadRequestException("Branch code " + branchCode + " in sheet two is not setup in the system, please contact system admin.");
                    }
                    creditCard.setCardPremium(ExcelReaderUtil.parseBigDecimalSafe(ExcelReaderUtil.getCellValue(row, 14))); //getCellBigDecimalValue(row.getCell(21)));
                    creditCard.setTransDate(ExcelReaderUtil.parseFlexibleDate(ExcelReaderUtil.getCellValue(row, 15))); //getCellDateValue(row.getCell(22)));
                    creditCard.setCoverFrom(ExcelReaderUtil.parseFlexibleDate(ExcelReaderUtil.getCellValue(row, 16))); //getCellDateValue(row.getCell(23)));
                    creditCard.setCoverTo(ExcelReaderUtil.parseFlexibleDate(ExcelReaderUtil.getCellValue(row, 17))); //getCellDateValue(row.getCell(24)));
                    creditCard.setCardNumber(ExcelReaderUtil.getCellValue(row, 18));  //masking the numbers .replaceAll("\\d(?=\\d{4})
                    creditCard.setCycles(ExcelReaderUtil.getCellValue(row, 19));
                    String paymentType = ExcelReaderUtil.getCellValue(row, 20);
                    if(!paymentType.isEmpty()){
                        if(paymentType.equalsIgnoreCase("Accrual")){
                            Date instDate = ExcelReaderUtil.parseFlexibleDate(ExcelReaderUtil.getCellValue(row, 21)); //getCellDateValue(row.getCell(20)));
                            if(instDate == null){
                                throw  new BadRequestException("Accrual installment date is required.");
                            }
                            creditCard.setAccrualInstDate(instDate);

                            String ApaymentType = ExcelReaderUtil.getCellValue(row, 22); //getCellDateValue(row.getCell(20)));
                            if (ApaymentType == null ||
                                    (!ApaymentType.equalsIgnoreCase("Dispensation") && !ApaymentType.equalsIgnoreCase("IPF"))) {
                                throw new BadRequestException("Accrual payment is required. Either IPF or Dispensation");
                            }

                            creditCard.setAccrualPaymentType(ApaymentType);
                            creditCard.setAccrualPaymentType("A");
                        }else if(paymentType.equalsIgnoreCase("Cash")){
                            creditCard.setAccrualPaymentType("C"); //getCellDateValue(row.getCell(20)));
                        } else {
                            throw new BadRequestException("Please fill with Cash or Accrual");
                        }
                    }else{
                        throw new BadRequestException("The payment type field is required.");
                    }
                    String seq = ExcelReaderUtil.getCellValue(row, 23);
                    if(!seq.isEmpty()) {
                        if(seq.equalsIgnoreCase("RN")){
                            creditCard.setSeqStatus("RN");
                        } else if (seq.equalsIgnoreCase("NB")) {
                            creditCard.setSeqStatus("NB");
                        } else{
                            throw new BadRequestException("Please fill with RN for(Renewal) or NB for(New Business).");
                        }
                    }else{
                        throw new BadRequestException("Please fill the SEQ column.");
                    }


                    creditCard.setUploadedDate(new Date());
                    creditCard.setUploadedBy(userUtils.getCurrentUser());
                    creditCard.setTransStatus("N");
                    creditCard.setTransType(transType);
                    creditCard.setTransCode(bulkCode);

                    creditCardList.add(creditCard);
                } catch (Exception e) {
                    throw new BadRequestException("Sheet One " + e.getMessage());
                }
            }

            int successfulPol = creditCardList.size();
            int invalidPol = invalidRecords.size();
            if (!(invalidPol > 0) && successfulPol > 0) {
                creditCradRepo.save(creditCardList);
            }

            response.put("successfulPolicies", successfulPol);
            response.put("failedRecords", invalidPol);
            response.put("invalidRecords", invalidRecords);

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
    public DataTablesResult<CreditCardDTO> findUnprocessedCreditCard(DataTablesRequest request) {
        Long currentUserId = userUtils.getCurrentUser().getId();

        List<Object[]> credits = creditCradRepo.findUnprocessedCreditCard((request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%", request.getPageNumber(), request.getPageSize(), currentUserId);
        final List<CreditCardDTO> packageInsuranceDTOS = new ArrayList<>();
        long rowCount = 0L;
        if (!credits.isEmpty())
            rowCount = ((BigInteger) credits.get(0)[9]).intValue();
        for (Object[] credit : credits) {
            CreditCardDTO creditCardDTO = new CreditCardDTO();
            creditCardDTO.setCardId(((BigInteger) credit[0]).longValue());
            creditCardDTO.setCoverType((String) credit[1]);
            creditCardDTO.setProductName((String) credit[2]);
            creditCardDTO.setInsurerName((String) credit[3]);
            creditCardDTO.setClientFName((String) credit[4]);
            creditCardDTO.setClientOtherNames((String) credit[5]);
            creditCardDTO.setCoverFrom((Date) credit[6]);
            creditCardDTO.setCoverTo((Date) credit[7]);
            creditCardDTO.setUploadedDate((Date) credit[8]);

            packageInsuranceDTOS.add(creditCardDTO);
        }
        Page<CreditCardDTO> page = new PageImpl<>(packageInsuranceDTOS, request, rowCount);

        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<CreditCardDTO> viewUnprocessedCreditCard(DataTablesRequest request) {
        Long currentUserId = userUtils.getCurrentUser().getId();

        List<Object[]> viewCredit = creditCradRepo.viewUnprocessedCreditCard((request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%", request.getPageNumber(), request.getPageSize(), currentUserId);
        final List<CreditCardDTO> insuranceDTOS = new ArrayList<>();
        long rowCount = 0L;
        if (!viewCredit.isEmpty()) rowCount = ((BigInteger) viewCredit.get(0)[10]).intValue();
        for (Object[] viewCredits : viewCredit) {
            CreditCardDTO creditCardDTO = new CreditCardDTO();
            creditCardDTO.setPolId(((BigInteger) viewCredits[0]).longValue());
            creditCardDTO.setCoverType((String) viewCredits[1]);
            creditCardDTO.setPolNo((String) viewCredits[2]);
            creditCardDTO.setCardPremium((BigDecimal) viewCredits[3]);
            creditCardDTO.setClientFName((String) viewCredits[4]);
            creditCardDTO.setClientOtherNames((String) viewCredits[5]);
            creditCardDTO.setCoverFrom((Date) viewCredits[6]);
            creditCardDTO.setCoverTo((Date) viewCredits[7]);
            creditCardDTO.setProcessedDate((Date) viewCredits[8]);
            creditCardDTO.setTransStatus((String) viewCredits[9]);

            insuranceDTOS.add(creditCardDTO);
        }
        Page<CreditCardDTO> page = new PageImpl<>(insuranceDTOS, request, rowCount);

        return new DataTablesResult<>(request, page);
    }

    @Override
    public PolicyTrans processSingleCreditCardPol(Long cardId, boolean isApproved) throws BadRequestException {
        PolicyTrans savedPol = null;
        CreditCard creditCardCreation = creditCradRepo.findOne(QCreditCard.creditCard.cardId.eq(cardId));
        if(creditCardCreation == null){
            throw new BadRequestException("Payment Card not found.");
        }
        if (creditCardCreation.getTransStatus().equalsIgnoreCase("Y")) {
            throw new BadRequestException("This transaction is already processed");
        }
        final PolicyTrans policyTrans = new PolicyTrans();
        Date wef = creditCardCreation.getCoverFrom();
        Date wet = creditCardCreation.getCoverTo();
        policyTrans.setPolCreateddt(new Date());
        policyTrans.setAuthStatus("LD");
        policyTrans.setCurrentStatus("LD");
        policyTrans.setCoverFrom(wef);
        policyTrans.setCoverTo(wet);
        policyTrans.setWefDate(wef);
        policyTrans.setWetDate(wet);
        String frequency = creditCardCreation.getFrequency();
        policyTrans.setTotalInstalments(1);
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
            throw new BadRequestException("Payment frequency is not defined for client ID " + creditCardCreation.getClientID() + " in frequency column in uploaded excel");
        }
        final String pinSearch = creditCardCreation.getClientPin().trim();
        final ClientDef client = clientRepository.findByPinNoIgnoreCase(pinSearch);
        if (client != null) {
            policyTrans.setClient(client);
        } else {
            throw new BadRequestException("Client with "+ pinSearch +" not found.");
        }

        policyTrans.setAgent(creditCardCreation.getInsurerCode());
        if (creditCardCreation.getSalesAgent() != null) {
            AccountDef subAgent = accountRepo.findByAcctId(creditCardCreation.getSalesCode());
            if (subAgent != null) {
                policyTrans.setSubAgent(subAgent);
            }
        }

        String searchCode = "ECCPU";
        String normalizedPrdName = creditCardCreation.getProductName().trim().toLowerCase().replace(" ", "");
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
            throw new BadRequestException(creditCardCreation.getProductName() + " product is not found. Please set up the product.");
        }
        final String currToSearch = String.valueOf(creditCardCreation.getCurrencyCode().getCurIsoCode());
        final Currencies currency = currencyRepository.findByPinNoIgnoreCase(currToSearch);
        if (currency != null) {
            policyTrans.setTransCurrency(currency);
        } else {
            throw new BadRequestException(creditCardCreation.getCurrencyCode() + " currency is not set up. Please add the currency to the system.");
        }
        UserBranches userBranches = userBranchesRepository.findByUser(userUtils.getCurrentUser());
        Long branchId = userBranches.getBranch().getObId();
        final OrgBranch branches = orgBranchRepository.findOne(QOrgBranch.orgBranch.obId.eq(branchId));
        policyTrans.setBranch(branches);
        policyTrans.setCreatedUser(userUtils.getCurrentUser());
        policyTrans.setPreviousTrans(policyTrans);
        BindersDef polBinder = bindersRepo.findBinderByAccId(creditCardCreation.getInsurerCode().getAcctId(), product.getProCode());
        policyTrans.setBinder(polBinder);
        if((polBinder.getAdminFeeActiveStatus() != null) && (polBinder.getAdminFeeActiveStatus().equalsIgnoreCase("Y"))) {
            policyTrans.setAdminFeeApplicable("Y");
        } else {
            policyTrans.setAdminFeeApplicable("N");
        }
        PaymentModes paymentModes = paymentModeRepo.findOne(QPaymentModes.paymentModes.pmDesc.eq("CASH"));
        policyTrans.setPaymentMode(paymentModes);

        SystemTrans systemTrans = null;
        if (creditCardCreation.getTransStatus().equalsIgnoreCase("N")) {

            String policyNumberFormat = paramService.getParameterString("POLICY_NO_FORMAT");
            String endorsementFormat = paramService.getParameterString("ENDORSE_NO_FORMAT");
            Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("P");
            if (sequenceRepo.count(seqPredicate) == 0)
                throw new BadRequestException("Sequence for New Business Transactions has not been defined");
            SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
            Long seqNumber = sequence.getNextNumber();
            final String policyNumber = templateMerger.generateFormat(policyNumberFormat, creditCardCreation.getBranch().getObId(), product.getProCode(), policyTrans.getWefDate(), sequence.getSeqPrefix() + String.format("%05d", seqNumber), null);
            policyTrans.setPolNo(policyNumber);

            sequence.setLastNumber(seqNumber);
            sequence.setNextNumber(seqNumber + 1);
            sequenceRepo.save(sequence);
            Predicate endorsePredicate = QSystemSequence.systemSequence.transType.eq("E");
            if (sequenceRepo.count(endorsePredicate) == 0)
                throw new BadRequestException("Sequence for Endorsement Transactions has not been defined");
            SystemSequence endorseSequence = sequenceRepo.findOne(endorsePredicate);
            Long endosseqNumber = endorseSequence.getNextNumber();
            final String revNumber = endorseSequence.getSeqPrefix() + String.format("%05d", endosseqNumber);
            final String endorseNumber = templateMerger.generateFormat(endorsementFormat, creditCardCreation.getBranch().getObId(), product.getProCode(), creditCardCreation.getCoverFrom(), revNumber, null);
            policyTrans.setPolRevNo(endorseNumber + "/1");
            policyTrans.setRevisionFormat(endorseNumber);
            //policyTrans.getBasicPrem()
            policyTrans.setBasicPrem(creditCardCreation.getCardPremium());
            endorseSequence.setLastNumber(endosseqNumber);
            endorseSequence.setNextNumber(endosseqNumber + 1);
            sequenceRepo.save(endorseSequence);

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

        if(creditCardCreation.getAccrualPaymentType() != null && creditCardCreation.getAccrualPaymentType().equalsIgnoreCase("A")) {
            policyTrans.setInterfaceType("A");
            policyTrans.setAccrualInstDate(creditCardCreation.getAccrualInstDate());
            policyTrans.setAccrualPaymentType(creditCardCreation.getAccrualPaymentType());
        }else{
            policyTrans.setInterfaceType("C");
        }

        policyTrans.setPolCreateddt(new Date());
        savedPol = policyTransRepo.save(policyTrans);


        systemTrans = new SystemTrans();
        systemTrans.setDoneDate(new Date());
        systemTrans.setDoneBy(userUtils.getCurrentUser());
        systemTrans.setPolicy(savedPol);
        systemTrans.setTransLevel("U");
        systemTrans.setTransCode("BUD");
        systemTrans.setTransAuthorised("N");
        systemTransRepo.save(systemTrans);

        RiskTrans riskTrans = new RiskTrans();
        if (client != null) {
            riskTrans.setInsured(client);
        } else {
            throw new BadRequestException("Client with pin " + pinSearch + " does not exist in the system");
        }
        //final BigDecimal basicPrem = creditCardCreation.getCardPremium().subtract(BigDecimal.valueOf(40)).multiply(BigDecimal.valueOf(100/100.45));
        //final BigDecimal basicPrem = creditCardCreation.getCardPremium().add(BigDecimal.valueOf(40)).multiply(BigDecimal.valueOf(100/100.45));
        final BigDecimal basicPrem = creditCardCreation.getCardPremium().add(BigDecimal.ZERO).multiply(BigDecimal.valueOf(100/100.45));
        BindersDef newBinder = bindersRepo.findBinderByAccId(creditCardCreation.getInsurerCode().getAcctId(), product.getProCode());
        riskTrans.setBinder(newBinder);
        String normalizedSubClass = creditCardCreation.getProductName().trim().toLowerCase().replace(" ", "");
        String normalizedCoverType = creditCardCreation.getCoverType().trim().toLowerCase().replace(" ", "");
        final SubClassDef subClassDef = subClassRepo.findBySubClassName(normalizedSubClass);
        final CoverTypesDef coverTypesDef = coverTypesRepo.findCoverTypesByBindId(newBinder.getBinId(), normalizedCoverType);
        System.out.println("Binder id ::" +newBinder.getBinId());
        System.out.println("CoverTypes id:: " +coverTypesDef.getCovId());
        BinderDetails newDetails = binderDetRepo.findOne(QBinderDetails.binderDetails.binder.binId.eq(newBinder.getBinId()).and(QBinderDetails.binderDetails.subCoverTypes.coverTypes.covId.eq(coverTypesDef.getCovId())));
        riskTrans.setSubclass(subClassDef);
        riskTrans.setCovertype(coverTypesDef);
        riskTrans.setRiskShtDesc(uploadValidatorsUtils.maskCardNumber(creditCardCreation.getCardNumber()));
        riskTrans.setNonMaskedValue(creditCardCreation.getCardNumber());
        riskTrans.setRiskDesc(creditCardCreation.getCardAccount()+" ("+creditCardCreation.getSeqStatus()+")"); //type;
        riskTrans.setBinderDetails(newDetails);
        riskTrans.setWefDate(wef);
        riskTrans.setWetDate(wet);
        riskTrans.setTransType("BU");
        riskTrans.setPolicy(savedPol);
        riskTrans.setButchargePrem(basicPrem);
        riskTrans.setAutogenCert("N");
        riskTrans.setSumInsured(creditCardCreation.getCardPremium());
        riskTrans.setComputePremium(creditCardCreation.getCardPremium());
        riskTrans.setNetpremium(creditCardCreation.getCardPremium());
        String businessType = product.getProGroup().getPrgType();
        if (!businessType.equalsIgnoreCase("L")) {
            policyTrans.setBusinessType("N");
            riskTrans.setInstallmentNo(1L);
        } else {
            policyTrans.setBusinessType("L");
        }

        if(creditCardCreation.getSeqStatus() != null && creditCardCreation.getSeqStatus().equalsIgnoreCase("NB")) {
            // get nb commm rates only
            Iterable<CommissionRates> commissionRatess = commRatesRepo.findAll(QCommissionRates.commissionRates.bindersDef.binId.eq(newBinder.getBinId())
                    .and(QCommissionRates.commissionRates.applicableAt.eq("NB")));
            if (commissionRatess.spliterator().getExactSizeIfKnown() == 0) {
                throw new BadRequestException("Commission Rates is not setup");
            }
            CommissionRates commissionRate = Streamable.streamOf(commissionRatess).findAny().get();
            AccountDef accountDef = accountRepo.findOne(QAccountDef.accountDef.accountType.accountType.eq(AccountTypeEnum.INS)
                    .and(QAccountDef.accountDef.acctId.eq(creditCardCreation.getInsurerCode().getAcctId())));

            BigDecimal commissionRates = (commissionRate != null) ? commissionRate.getCommRate() : null;
            BigDecimal accountCommRates = (accountDef != null) ? accountDef.getAccountType().getCommRate() : null;

            if (commissionRates != null && commissionRates.compareTo(BigDecimal.ZERO) > 0) {
                riskTrans.setCommRate(commissionRates);
            } else if (accountCommRates != null && accountCommRates.compareTo(BigDecimal.ZERO) > 0) {
                riskTrans.setCommRate(accountCommRates);
            } else {
                throw new BadRequestException("New Business Commission Rates is not setup");
            }
        }else if(creditCardCreation.getSeqStatus().equalsIgnoreCase("RN")) {
            // get rn commm rates only
            Iterable<CommissionRates> commissionRatess = commRatesRepo.findAll(QCommissionRates.commissionRates.bindersDef.binId.eq(newBinder.getBinId())
                    .and(QCommissionRates.commissionRates.applicableAt.eq("RN")));
            if (commissionRatess.spliterator().getExactSizeIfKnown() == 0) {
                throw new BadRequestException("Commission Rates is not setup");
            }
            CommissionRates commissionRate = Streamable.streamOf(commissionRatess).findAny().get();
            AccountDef accountDef = accountRepo.findOne(QAccountDef.accountDef.accountType.accountType.eq(AccountTypeEnum.INS)
                    .and(QAccountDef.accountDef.acctId.eq(creditCardCreation.getInsurerCode().getAcctId())));

            BigDecimal commissionRates = (commissionRate != null) ? commissionRate.getCommRate() : null;
            BigDecimal accountCommRates = (accountDef != null) ? accountDef.getAccountType().getCommRate() : null;

            if (commissionRates != null && commissionRates.compareTo(BigDecimal.ZERO) > 0) {
                riskTrans.setCommRate(commissionRates);
            } else if (accountCommRates != null && accountCommRates.compareTo(BigDecimal.ZERO) > 0) {
                riskTrans.setCommRate(accountCommRates);
            } else {
                throw new BadRequestException("Renewal Commission Rates is not setup");
            }
        }else{
            throw new BadRequestException("Payment Card Seq not found.");
        }

        //
//        CommissionRates commissionRate = commRatesRepo.findOne(QCommissionRates.commissionRates.bindersDef.binId.eq(newBinder.getBinId()));
//        AccountDef accountDef = accountRepo.findOne(QAccountDef.accountDef.accountType.accountType.eq(AccountTypeEnum.INS)
//                .and(QAccountDef.accountDef.acctId.eq(wezeshaStockCreation.getInsurerCode().getAcctId())));
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
        riskTrans = riskTransRepo.save(riskTrans);
        long riskIdentifier = Long.valueOf(String.valueOf(dateUtils.getUwYear(policyTrans.getWefDate())) + String.valueOf(riskTrans.getRiskId()));
        riskTrans.setRiskIdentifier(riskIdentifier);
        RiskTrans savedTrans = riskTransRepo.save(riskTrans);

        //RiskTrans savedRisk = riskRepo.save(risk);
        //long riskIdentifier = Long.valueOf(String.valueOf(dateUtils.getUwYear(policy.getWefDate())) + String.valueOf(savedRisk.getRiskId()));
        PolicyActiveRisks activeRisk = new PolicyActiveRisks();
        activeRisk.setPolicy(policyTrans);
        activeRisk.setRisk(riskTrans);
        activeRisk.setRiskIdentifier(riskIdentifier);
        activeRisksRepo.save(activeRisk);
        savedTrans.setRiskIdentifier(riskIdentifier);
        riskTransRepo.save(savedTrans);

        final SectionsDef sectionsDef  = sectionsRepo.findOne(QSectionsDef.sectionsDef.shtDesc.eq("SUM INSURED"));
        SectionTrans sectionTrans = new SectionTrans();
        sectionTrans.setAmount(creditCardCreation.getCardPremium());
        sectionTrans.setSection(sectionsDef);
        sectionTrans.setCompute(true);
        sectionTrans.setDivFactor(BigDecimal.valueOf(100));
        Iterable<PremRatesDef> premRatesDefs = premRatesRepo.findAll(QPremRatesDef.premRatesDef.section.shtDesc.eq("SUM INSURED"));
        System.out.println("Found Sections "+premRatesDefs.spliterator().getExactSizeIfKnown());
        PremRatesDef premRatesDef = null;
        if(premRatesDefs.iterator().hasNext()) {
            premRatesDef = premRatesDefs.iterator().next();
        }
        System.out.println("Prem rate..."+premRatesDef);
        if(premRatesDef!=null){
            sectionTrans.setPremRates(premRatesDef);
            sectionTrans.setRate(premRatesDef.getRate());
            sectionTrans.setRisk(savedTrans);
            sectionTrans.setFreeLimit(BigDecimal.ZERO);
            sectionTransRepo.save(sectionTrans);
        }
        Iterable<TaxRates> taxRates = taxRatesRepo.findAll((QTaxRates.taxRates.active.eq(true).and(QTaxRates.taxRates.mandatory.eq(Boolean.TRUE))).and(QTaxRates.taxRates.subclass.subId.eq(savedTrans.getSubclass().getSubId()))
                .and(QTaxRates.taxRates.productsDef.proCode.eq(policyTrans.getProduct().getProCode())));
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
                polstampDuty = polstampDuty.add(computedTax);
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
        savedPol.setSumInsured(creditCardCreation.getSumInsured());
        policyTransRepo.save(savedPol);

        //make ready the policy
        uploadValidatorsService.saveGeneralPolicyUpload(savedPol.getPolicyId(), false);

        //compute admin fee
        // uploadValidatorsService.computeAdmimFee(savedPol, polBinder, basicPrem);
        creditCardCreation.setPolicyTrans(savedPol);
        creditCardCreation.setTransStatus("Y");
        creditCardCreation.setPolicyAuthorized("N");
        creditCardCreation.setProcessedDate(new Date());
        creditCardCreation.setProcessedBy(userUtils.getCurrentUser());
        creditCradRepo.save(creditCardCreation);

        return savedPol;
    }


        @Override
        public List<Long> processBulkCreditCardPol (List <Long> cardIds,boolean isApproved) throws BadRequestException
        {
            if (cardIds.size() <= 0) {
                throw new BadRequestException("Select At least One Transaction To Process");
            }
            PolicyTrans savedPol = null;
            List<Long> processedPolicyIds = new ArrayList<>();
            for (Long cardId : cardIds) {
                savedPol = processSingleCreditCardPol(cardId, true);
                processedPolicyIds.add(savedPol.getPolicyId());
            }
            return processedPolicyIds;
        }

        @Override
        public String approveSingleCreditCardPol (Long policyId) throws BadRequestException {
            authService.authorizeBulkUploadPolicies(policyId);
            CreditCard policyCreation = creditCradRepo.findBulkStockByPolicyId(policyId);
            policyCreation.setPolicyAuthorized("Y");
            creditCradRepo.save(policyCreation);

            return "Transaction Authorized Successfully";
        }

        @Override
        public String approveBulkCreditCardPol (List < Long > policyIds) throws BadRequestException {

            if (policyIds.size() <= 0) {
                throw new BadRequestException("Select At least One Transaction To Process");
            }
            for (Long policyId : policyIds) {
                authService.authorizeBulkUploadPolicies(policyId);
                CreditCard policyCreation = creditCradRepo.findBulkStockByPolicyId(policyId);
                policyCreation.setPolicyAuthorized("Y");
                creditCradRepo.save(policyCreation);
            }

            return "Transactions Authorized Successfully";
        }

        @Override
        @Transactional
        public String deleteBulkCreditCardPol (List < Long > cardIds) throws BadRequestException {
            if (cardIds.size() <= 0) {
                throw new BadRequestException("Select At least One Transaction To Delete");
            }
            for (Long cardId : cardIds) {
                CreditCard creditCard = creditCradRepo.findOne(cardId);
                if ("N".equalsIgnoreCase(creditCard.getTransStatus())) {
                    creditCradRepo.delete(creditCard);
                }
            }
            return "Transactions Deleted Successfully";
        }

        @Override
        @Transactional
        public String deleteProcessedBulkCreditCardPol(List<Long> cardIds) throws BadRequestException {

            if (cardIds.size() <= 0){
                throw new BadRequestException("Select At least One Transaction To Delete");
            }
            for(Long cardId : cardIds){
                //authService.authorizeBulkUploadPolicies(policyId)
                //delete the policy from the table
                CreditCard creditCard = creditCradRepo.findBulkStockByPolicyId(cardId);
                creditCradRepo.delete(creditCard);
                //delete the created policy
                uploadValidatorsUtils.deleteprocessedPol(cardId);
            }

            return "Transactions Deleted Successfully";
        }
    }