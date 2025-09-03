package com.brokersystems.brokerapp.bulktransactions.service.impl;

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
import com.brokersystems.brokerapp.bulktransactions.dtos.WezeshaStockDTO;
import com.brokersystems.brokerapp.bulktransactions.models.*;
import com.brokersystems.brokerapp.bulktransactions.repositories.BulkPolicyCreationRepo;
import com.brokersystems.brokerapp.bulktransactions.repositories.BulkPolicyRiskRepository;
import com.brokersystems.brokerapp.bulktransactions.repositories.WezeshaStockRepo;
import com.brokersystems.brokerapp.bulktransactions.service.WezeshaStockService;
import com.brokersystems.brokerapp.trans.service.PolicyAuthorization;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.uw.repository.*;
import com.brokersystems.brokerapp.uw.service.PremComputeService;
import com.brokersystems.brokerapp.uw.service.impl.PremComputeServiceImpl;
import com.brokersystems.brokerapp.uw.validators.RenewalJobListener;
import com.mysema.query.types.Predicate;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.brokersystems.brokerapp.setup.model.QCurrencies.currencies;

@Slf4j
@Service
public class WezeshaStockServiceImpl implements WezeshaStockService {

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private ValidatorUtils validator;

    @Autowired
    private UserBranchesRepository userBranchesRepository;

    @Autowired
    private OrgBranchRepository orgBranchRepository;

    @Autowired
    private BulkPolicyCreationRepo bulkPolicyCreationRepo;
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ClientTypeRepo clientTypeRepo;
//    @Autowired
//    private sequenceRepo sequenceRepo;
    @Autowired
    private PolicyTransRepo policyTransRepo;
    @Autowired
    private CommRatesRepo commRatesRepo;
    @Autowired
    private RiskTransRepo riskTransRepo;

    @Autowired
    private TaxRatesRepo taxRatesRepo;

    @Autowired
    private PolTaxesRepo polTaxesRepo;

    @Autowired
    private PremComputeService premiumService;

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
    private PaymentModeRepo paymentModeRepo;
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
    private SequenceRepository sequenceRepo;
    @Autowired
    private WezeshaStockRepo wezeshaStockRepo;
    @Autowired
    private PolicyAuthorization authService;
    @Autowired
    private SectionRepo sectionsRepo;
    @Autowired
    private PremComputeService premComputeServiceImpl;
    @Autowired
    private UploadValidatorsUtils uploadValidatorsService;
    @Autowired
    private PolActiveRisksRepo activeRisksRepo;
    @Autowired
    private UserRepository userRepo;

    private static final int BATCH_SIZE = 500;

    @Override
//    @Transactional(readOnly = false)
    public Map<String, Object> uploadWezeshaStock(MultipartFile file) throws BadRequestException {
        UserBranches userBranches = userBranchesRepository.findByUser(userUtils.getCurrentUser());
        Long branchId = userBranches.getBranch().getObId();
        final OrgBranch userBranch = orgBranchRepository.findOne(QOrgBranch.orgBranch.obId.eq(branchId));

        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            throw new BadRequestException("Upload files with .xlsx or .xls extension only");
        }

        Map<String, Object> response = new HashMap<>();
        List<String> invalidRecords = new ArrayList<>();
        List<WezeshaStockCreation> wezeshaStockCreations = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);

            // Validate template format
            List<String> validationErrors = ExcelReaderUtil.validateWezeshaStockPolicyTemplate(workbook);
            if (!validationErrors.isEmpty()) {
                StringBuilder errorMessage = new StringBuilder("Template validation failed: ");
                for (String error : validationErrors) {
                    errorMessage.append(error).append("; ");
                }
                throw new BadRequestException(errorMessage.toString());
            }

            Sheet sheetOne = workbook.getSheetAt(0);

            int startRowIndex = findDataStartRow(sheetOne);
            System.out.println("startRowIndex" + startRowIndex);
            if (startRowIndex == -1) {
                throw new BadRequestException("Sheet One contains no data.");
            }

            String wezeshaCode = null;
            Predicate pedSystem = QSystemSequence.systemSequence.transType.eq("WPU");
            if (sequenceRepo.count(pedSystem) == 0)
                throw new BadRequestException("Sequence for Wezesha Policy Upload has not been defined");
            String transType = "WPU";

            for (int rowNum = startRowIndex; rowNum <= sheetOne.getLastRowNum(); rowNum++) {
                SystemSequence sequenceSystem = sequenceRepo.findOne(pedSystem);
                Long sequenceNumber = sequenceSystem.getNextNumber();
                wezeshaCode = transType + String.format("%05d", sequenceNumber);
                sequenceSystem.setLastNumber(sequenceNumber);
                sequenceSystem.setNextNumber(sequenceNumber + 1);
                sequenceRepo.save(sequenceSystem);

                Row row = sheetOne.getRow(rowNum);
                if (isBlankRow(row)) continue;

                try {
                    WezeshaStockCreation wezeshaStockCreation = new WezeshaStockCreation();
                    String loanId = ExcelReaderUtil.getCellValue(row, 0); //getCellStringValue(row.getCell(0));
                    System.out.println("loanId:: " + loanId);
                    wezeshaStockCreation.setLoanId(loanId);
//                    wezeshaStockCreation.setClientFname(ExcelReaderUtil.getCellValue(row, 1)); //getCellStringValue(row.getCell(1)));
//                    wezeshaStockCreation.setClientOtherNames(ExcelReaderUtil.getCellValue(row, 2)); //getCellStringValue(row.getCell(2)));
//                    wezeshaStockCreation.setClientDOB(ExcelReaderUtil.parseFlexibleDate(ExcelReaderUtil.getCellValue(row, 3))); //getCellDateValue(row.getCell(3)));

                    String clntIdNo = ExcelReaderUtil.getCellValue(row, 1); //getCellStringValue(row.getCell(4));
                    if (!validator.validateIdNo(clntIdNo)) {
                        throw new BadRequestException("Invalid client ID Number " + clntIdNo);
                    }

                    wezeshaStockCreation.setClientId(clntIdNo);

//                    String clientPin = ExcelReaderUtil.getCellValue(row, 2); // getCellStringValue(row.getCell(5));
//                    if (!validator.validatePin(clientPin)) {
//                        throw new Exception("Invalid client PIN " + clientPin);
//                    }

                    String cif = ExcelReaderUtil.getCellValue(row, 2); //getCellStringValue(row.getCell(4));
                    wezeshaStockCreation.setClientCIF(cif); //getCellStringValue(row.getCell(8)));
                    final ClientDef client = clientRepository.findByCifandIdNoIgnoreCase(cif, clntIdNo);
                    if (client != null) {
                        wezeshaStockCreation.setClientPin(client.getPinNo());
                        wezeshaStockCreation.setClientFname(client.getFname());
                        wezeshaStockCreation.setClientOtherNames(client.getOtherNames());
                        wezeshaStockCreation.setClientDOB(client.getDob());
                        wezeshaStockCreation.setClientEmail(client.getEmailAddress());
                        wezeshaStockCreation.setClientPhone(client.getPhoneNo());
                        wezeshaStockCreation.setClientType(client.getTenantType());
                    } else {
                        throw new BadRequestException("Client with ID No:" + clntIdNo + " not found");
                    }
//
//                    wezeshaStockCreation.setClientEmail(ExcelReaderUtil.getCellValue(row, 6)); //getCellStringValue(row.getCell(6)));
//                    wezeshaStockCreation.setClientPhone(ExcelReaderUtil.getCellValue(row, 7)); //getCellStringValue(row.getCell(7)));
//                    String clientType = ExcelReaderUtil.getCellValue(row, 3); //getCellStringValue(row.getCell(9));
//                    String normalizedClientType = clientType.trim().toLowerCase().replace(" ", "");
//                    ClientTypes clientTypes = clientTypeRepo.findByNormalizeType(normalizedClientType);
//                    if (clientTypes != null) {
//                        wezeshaStockCreation.setClientType(clientTypes);
//                    } else {
//                        throw new BadRequestException("Client type " + clientType + " is not setup, please contact system admin");
//                    }
                    wezeshaStockCreation.setBusinessLocation(ExcelReaderUtil.getCellValue(row, 3)); //getCellStringValue(row.getCell(10)));
                    String businessType = ExcelReaderUtil.getCellValue(row, 4); //getCellStringValue(row.getCell(11));
                    wezeshaStockCreation.setBusinessType(businessType);
                    wezeshaStockCreation.setTransDate(ExcelReaderUtil.parseFlexibleDate((ExcelReaderUtil.getCellValue(row, 5)))); //getCellDateValue(row.getCell(12)));
                    String branchCode = ExcelReaderUtil.getCellValue(row, 6); //getCellStringValue(row.getCell(13));
                    OrgBranch orgBranch = orgBranchRepository.findOne(QOrgBranch.orgBranch.obShtDesc.eq(branchCode.replaceAll("\\s+", "")));
                    if (orgBranch != null) {
                        wezeshaStockCreation.setBranch(orgBranch);
                    } else {
                        throw new BadRequestException("Branch code " + branchCode + " in sheet two is not setup in the system, please contact system admin.");
                    }
                    String insurerCode = ExcelReaderUtil.getCellValue(row, 7); //getCellStringValue(row.getCell(14));
                    AccountDef underWriter = accountRepo.findOne(QAccountDef.accountDef.shtDesc.eq(insurerCode.replaceAll("\\s+", "")));
                    if (underWriter != null) {
                        wezeshaStockCreation.setInsurerCode(underWriter);
                    } else {
                        throw new BadRequestException("Insurer with code " + insurerCode + " is not setup in the system, please contact system admin");
                    }
                    wezeshaStockCreation.setProductGroup(ExcelReaderUtil.getCellValue(row, 8)); //getCellStringValue(row.getCell(15)));
                    String searchCode = "WSPU";
                    String productName = (ExcelReaderUtil.getCellValue(row, 9)); //getCellStringValue(row.getCell(16)));
                    String normalizedProductName = productName.replaceAll("\\s+", "");
                    ProductsDef product = productsRepo.findByNormalizedName(normalizedProductName, searchCode);
                    if (product == null) {
                        throw new BadRequestException("Product " + productName + " not set in the system, please contact system admin.");
                    }
                    BindersDef binder = bindersRepo.findBinderByAccId(wezeshaStockCreation.getInsurerCode().getAcctId(), product.getProCode());
                    if (binder == null) {
                        throw new BadRequestException("Binder for product " + productName + " and insurer with code " + wezeshaStockCreation.getInsurerCode().getShtDesc()
                                + " not set in the system, please contact system admin.");
                    }
                    String coverType = (ExcelReaderUtil.getCellValue(row, 10)); //getCellStringValue(row.getCell(17)));
                    String normalizedCoverType = coverType.replaceAll("\\s+", "");
                    SubClassDef subClassDef = subClassRepo.findBySubClassName(normalizedProductName);
                    if (subClassDef != null){
                        wezeshaStockCreation.setProductName(subClassDef.getSubDesc());
                    } else {
                        throw new BadRequestException("Product " + productName + " not set in the system, please contact system admin.");
                    }
                    System.out.println("Binder Id "+binder.getBinId());
                    CoverTypesDef coverTypesDef = coverTypesRepo.findCoverTypesByBindId(binder.getBinId(), normalizedCoverType);
                    if (coverTypesDef != null) {
                        wezeshaStockCreation.setCoverType(coverTypesDef.getCovName());
                    }else {
                        throw new BadRequestException("Cover type " + coverType + " not set for insurer with code " + wezeshaStockCreation.getInsurerCode().getShtDesc() +" please contact system admin.");
                    }
                    String currency = ExcelReaderUtil.getCellValue(row, 11); //getCellStringValue(row.getCell(18));
                    if (currency != null && !currency.trim().isEmpty()) {
                        currency = currency.trim();
                        Currencies currencies = currencyRepository.findOne(QCurrencies.currencies.curIsoCode.eq(currency));
                        if (currencies == null) {
                            throw new BadRequestException("Currency code " + currency + " not setup in the system, please contact system admin");
                        }
                        wezeshaStockCreation.setCurrency(currencies.getCurIsoCode());
                    } else {
                        throw new BadRequestException("Currency is required in row " + row.getRowNum() + " in Currency ISO code column");
                    }
                    wezeshaStockCreation.setFrequency(validateFrequency(ExcelReaderUtil.getCellValue(row, 12),row.getRowNum())); //getCellStringValue(row.getCell(19)), row.getRowNum()));
                    wezeshaStockCreation.setStockSumInsured(ExcelReaderUtil.parseBigDecimalSafe(ExcelReaderUtil.getCellValue(row, 13))); //getCellBigDecimalValue(row.getCell(20)));
                    wezeshaStockCreation.setStockPremium(ExcelReaderUtil.parseBigDecimalSafe(ExcelReaderUtil.getCellValue(row, 14))); //getCellBigDecimalValue(row.getCell(21)));

                    Date wef = ExcelReaderUtil.parseFlexibleDate(ExcelReaderUtil.getCellValue(row, 15)); //getCellDateValue(row.getCell(22));
                    if (wef != null) {
                        wezeshaStockCreation.setStartDate(wef);
                    } else {
                        throw new BadRequestException("Cover Start date is required");
                    }

                    Date wet = (ExcelReaderUtil.parseFlexibleDate(ExcelReaderUtil.getCellValue(row, 16))); //getCellDateValue(row.getCell(23)));
                    if (wet != null) {
                        wezeshaStockCreation.setEndDate(wet);
                    } else {
                        throw new BadRequestException("Cover End date is required");
                    }
                    String paymentType = ExcelReaderUtil.getCellValue(row, 17);
                    if(!paymentType.isEmpty()){
                        if(paymentType.equalsIgnoreCase("Accrual")){
                            Date instDate = ExcelReaderUtil.parseFlexibleDate(ExcelReaderUtil.getCellValue(row, 18)); //getCellDateValue(row.getCell(20)));
                            if(instDate == null){
                                throw  new BadRequestException("Accrual installment date is required.");
                            }
                            wezeshaStockCreation.setAccrualInstDate(instDate);

                            String ApaymentType = ExcelReaderUtil.getCellValue(row, 19); //getCellDateValue(row.getCell(20)));
                            if (ApaymentType == null ||
                                    (!ApaymentType.equalsIgnoreCase("Dispensation") && !ApaymentType.equalsIgnoreCase("IPF"))) {
                                throw new BadRequestException("Accrual payment is required. Either IPF or Dispensation");
                            }

                            wezeshaStockCreation.setAccrualPaymentType(ApaymentType);
                            wezeshaStockCreation.setAccrualPaymentType("A");
                        }else if(paymentType.equalsIgnoreCase("Cash")){
                            wezeshaStockCreation.setAccrualPaymentType("C"); //getCellDateValue(row.getCell(20)));
                        } else {
                            throw new BadRequestException("Please fill with Cash or Accrual");
                        }
                    }else{
                        throw new BadRequestException("The payment type field is required.");
                    }
                    wezeshaStockCreation.setRiskId(loanId);
                    wezeshaStockCreation.setRiskDesc(businessType);
                    wezeshaStockCreation.setTransProcessed("N");
                    wezeshaStockCreation.setWezeshaPolType(transType);
                    wezeshaStockCreation.setUploadedBy(userUtils.getCurrentUser());
                    wezeshaStockCreation.setUploadedDate(new Date());
                    wezeshaStockCreation.setWezeshaRefCode(wezeshaCode);

                    wezeshaStockCreations.add(wezeshaStockCreation);

                    if (wezeshaStockCreations.size() >= BATCH_SIZE ) {
                        wezeshaStockRepo.save(wezeshaStockCreations);
                        wezeshaStockCreations.clear();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    invalidRecords.add("Sheet One, Row " + (rowNum + 1) + ": " + e.getMessage());
                }
            }

            int successfulPol = wezeshaStockCreations.size();
            int invalidPol = invalidRecords.size();
            System.out.println("successfulPol:: " + successfulPol);
            System.out.println("invalidPol:: " + invalidPol);
//            if (!(invalidPol > 0) && successfulPol > 0) {
//                wezeshaStockRepo.save(wezeshaStockCreations);
//            }

            if (!wezeshaStockCreations.isEmpty()) {
                wezeshaStockRepo.save(wezeshaStockCreations);
                wezeshaStockCreations.clear();
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
                if (cellValue.contains("loan id") ||
                        cellValue.contains("client") ||
                        cellValue.contains("name") ||
                        cellValue.contains("code") ||
                        cellValue.contains("business") ||
                        cellValue.contains("product") ||
                        cellValue.contains("premium") ||
                        cellValue.contains("cover type")) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isBlankRow(Row row) {
        if (row == null) {
            System.out.println("Blank row detected (null).");
            return true;
        }

        boolean allBlank = true;
        for (int cellNum = 0; cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                allBlank = false;
                break;
            }
        }

        if (allBlank) {
            System.out.println("Blank row detected at index: " + row.getRowNum());
        }

        return allBlank;
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
                return cell.getDateCellValue();
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

//    private String validateFrequency(String frequency, int rowIndex) throws BadRequestException {
//        if (frequency == null || frequency.trim().isEmpty()) {
//            throw new BadRequestException("Payment frequency is required at Row " + (rowIndex + 1) + " in Payment frequency column");
//        }
//
//        frequency = frequency.trim();
//        if (!VALID_FREQUENCIES.contains(frequency)) {
//            throw new BadRequestException("Invalid frequency: '" + frequency + "' at Row " + (rowIndex + 1) + " in Payment frequency column");
//        }
//
//        return frequency;
//    }
    private String validateFrequency(String frequency, int rowIndex) throws BadRequestException {
        if (frequency == null || frequency.trim().isEmpty()) {
            throw new BadRequestException("Payment frequency is required at Row " + (rowIndex + 1) + " in Payment frequency column");
        }

        frequency = frequency.trim();
        frequency = capitalizeFully(frequency); // Add this line

        if (!VALID_FREQUENCIES.contains(frequency)) {
            throw new BadRequestException("Invalid frequency: '" + frequency + "' at Row " + (rowIndex + 1) + " in Payment frequency column");
        }

        return frequency;
    }

    // Helper method
    private String capitalizeFully(String str) {
        if (str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    @Override
    public DataTablesResult<WezeshaStockDTO> findUnProcessedWezeshaPol(DataTablesRequest request) {
        Long currentUserId = userUtils.getCurrentUser().getId();

        List<Object[]> viewWezeshaPolicies = wezeshaStockRepo.findUnProcessedWezeshaPol((request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%", request.getPageNumber(), request.getPageSize(), currentUserId);
        final List<WezeshaStockDTO> wezesha= new ArrayList<>();
        long rowCount = 0L;
        if (!viewWezeshaPolicies.isEmpty()) rowCount = ((BigInteger) viewWezeshaPolicies.get(0)[11]).intValue();
        for (Object[] wezeshaPol : viewWezeshaPolicies) {
            WezeshaStockDTO wezeshaStockDTO = new WezeshaStockDTO();
            wezeshaStockDTO.setWezeshaStockId(((BigInteger) wezeshaPol[0]).longValue());
            wezeshaStockDTO.setLoanId((String) wezeshaPol[1]);
            wezeshaStockDTO.setCoverType((String) wezeshaPol[2]);
            wezeshaStockDTO.setClientFname((String) wezeshaPol[3]);
            wezeshaStockDTO.setClientOtherNames((String) wezeshaPol[4]);
            wezeshaStockDTO.setBusinessType((String) wezeshaPol[5]);
            wezeshaStockDTO.setStockSumInsured((BigDecimal) wezeshaPol[6]);
            wezeshaStockDTO.setStockPremium((BigDecimal) wezeshaPol[7]);
            wezeshaStockDTO.setStartDate((Date) wezeshaPol[8]);
            wezeshaStockDTO.setEndDate((Date) wezeshaPol[9]);
            wezeshaStockDTO.setUploadedDate((Date) wezeshaPol[10]);
            wezesha.add(wezeshaStockDTO);
        }

        Page<WezeshaStockDTO> page = new PageImpl<>(wezesha, request, rowCount);

        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<WezeshaStockDTO> viewWezeshaPolicies(DataTablesRequest request) {
        System.out.println("Wezesha passing....");
        Long currentUserId = userUtils.getCurrentUser().getId();

        List<Object[]> UnProcessedWezeshaPols = wezeshaStockRepo.viewUnprocessedwezeshaBulkPol((request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%", request.getPageNumber(), request.getPageSize(), currentUserId);
        final List<WezeshaStockDTO> wezeshaBulk = new ArrayList<>();
        long rowCount = 0L;
        if (!UnProcessedWezeshaPols.isEmpty()) rowCount = ((BigInteger) UnProcessedWezeshaPols.get(0)[11]).intValue();
        for (Object[] wezeshaBulkPol : UnProcessedWezeshaPols) {
            WezeshaStockDTO wezeshaStockDTO = new WezeshaStockDTO();
            wezeshaStockDTO.setPolId(((BigInteger) wezeshaBulkPol[0]).longValue());
            wezeshaStockDTO.setCoverType((String) wezeshaBulkPol[1]);
            wezeshaStockDTO.setLoanId((String) wezeshaBulkPol[2]);
            wezeshaStockDTO.setPolNo((String) wezeshaBulkPol[3]);
            wezeshaStockDTO.setClientFname((String) wezeshaBulkPol[4]);
            wezeshaStockDTO.setClientOtherNames((String) wezeshaBulkPol[5]);
            wezeshaStockDTO.setBusinessType((String) wezeshaBulkPol[6]);
            wezeshaStockDTO.setStartDate((Date) wezeshaBulkPol[7]);
            wezeshaStockDTO.setEndDate((Date) wezeshaBulkPol[8]);
            wezeshaStockDTO.setPolDate((Date) wezeshaBulkPol[9]);
            wezeshaStockDTO.setPolStatus((String) wezeshaBulkPol[10]);
            wezeshaBulk.add(wezeshaStockDTO);
        }

        Page<WezeshaStockDTO> page = new PageImpl<>(wezeshaBulk, request, rowCount);

        return new DataTablesResult<>(request, page);
    }

    @Override
    public PolicyTrans processSingleWezeshPol(Long wezeshaId, Long userId, boolean isApproved) throws BadRequestException {
        PolicyTrans savedPol = null;
        WezeshaStockCreation wezeshaStockCreation = wezeshaStockRepo.findOne(QWezeshaStockCreation.wezeshaStockCreation.wezeshaStockId.eq(wezeshaId));
        if (wezeshaStockCreation.getTransProcessed().equalsIgnoreCase("Y")) {
            throw new BadRequestException("This transaction is already processed");
        }
        final PolicyTrans policyTrans = new PolicyTrans();
//        Date wef = wezeshaStockCreation.getStartDate();
//        Date wet = wezeshaStockCreation.getEndDate();
        policyTrans.setPolCreateddt(new Date());
        policyTrans.setAuthStatus("LD");
        policyTrans.setCurrentStatus("LD");


        Date wef = wezeshaStockCreation.getStartDate();
        final Date wet = dateUtils.getWetDate(wef);
        policyTrans.setWefDate(wef);
        policyTrans.setWetDate(wet);
        policyTrans.setCoverFrom(wef);
        policyTrans.setCoverTo(wet);
//        policyTrans.setCoverFrom(wef);
//        policyTrans.setCoverTo(wet);
//        policyTrans.setWefDate(wef);
//        policyTrans.setWetDate(wet);
        String frequency = wezeshaStockCreation.getFrequency();
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
            throw new BadRequestException("Payment frequency is not defined for client ID " + wezeshaStockCreation.getClientId() + " in frequency column in uploaded excel");
        }
        final String pinSearch = wezeshaStockCreation.getClientPin().trim();
        final ClientDef client = clientRepository.findByPinNoIgnoreCase(pinSearch);
        if (client != null) {
            policyTrans.setClient(client);
        } else {
         throw new BadRequestException("Client with "+ pinSearch +" not found.");
        }

        policyTrans.setAgent(wezeshaStockCreation.getInsurerCode());

        String searchCode = "WSPU";
        String normalizedPrdName = wezeshaStockCreation.getProductName().trim().toLowerCase().replace(" ", "");
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
            throw new BadRequestException(wezeshaStockCreation.getProductName() + " product is not found. Please set up the product.");
        }
        final String currToSearch = wezeshaStockCreation.getCurrency().trim();
        final Currencies currency = currencyRepository.findByPinNoIgnoreCase(currToSearch);
        if (currency != null) {
            policyTrans.setTransCurrency(currency);
        } else {
            throw new BadRequestException(wezeshaStockCreation.getCurrency() + " currency is not set up. Please add the currency to the system.");
        }
        UserBranches userBranches = userBranchesRepository.findByUser(userUtils.getCurrentUser());
        Long branchId = userBranches.getBranch().getObId();
        final OrgBranch branches = orgBranchRepository.findOne(QOrgBranch.orgBranch.obId.eq(branchId));
        policyTrans.setBranch(branches);
        policyTrans.setCreatedUser(userUtils.getCurrentUser());
        policyTrans.setPreviousTrans(policyTrans);
        BindersDef polBinder = bindersRepo.findBinderByAccId(wezeshaStockCreation.getInsurerCode().getAcctId(), product.getProCode());
        policyTrans.setBinder(polBinder);
        if((polBinder.getAdminFeeActiveStatus() != null) && (polBinder.getAdminFeeActiveStatus().equalsIgnoreCase("Y"))) {
            policyTrans.setAdminFeeApplicable("Y");
        } else {
            policyTrans.setAdminFeeApplicable("N");
        }
        PaymentModes paymentModes = paymentModeRepo.findOne(QPaymentModes.paymentModes.pmDesc.eq("CASH"));
        policyTrans.setPaymentMode(paymentModes);

        SystemTrans systemTrans = null;
        if (wezeshaStockCreation.getTransProcessed().equalsIgnoreCase("N")) {

            String policyNumberFormat = paramService.getParameterString("POLICY_NO_FORMAT");
            String endorsementFormat = paramService.getParameterString("ENDORSE_NO_FORMAT");
            Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("P");
            if (sequenceRepo.count(seqPredicate) == 0)
                throw new BadRequestException("Sequence for New Business Transactions has not been defined");
            SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
            Long seqNumber = sequence.getNextNumber();
            final String policyNumber = templateMerger.generateFormat(policyNumberFormat, wezeshaStockCreation.getBranch().getObId(), product.getProCode(), policyTrans.getWefDate(), sequence.getSeqPrefix() + String.format("%05d", seqNumber), null);
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
            final String endorseNumber = templateMerger.generateFormat(endorsementFormat, wezeshaStockCreation.getBranch().getObId(), product.getProCode(), wezeshaStockCreation.getStartDate(), revNumber, null);
            policyTrans.setPolRevNo(endorseNumber + "/1");
            policyTrans.setRevisionFormat(endorseNumber);
            //policyTrans.getBasicPrem()
            policyTrans.setBasicPrem(wezeshaStockCreation.getStockPremium());
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

        if(wezeshaStockCreation.getAccrualPaymentType() != null && wezeshaStockCreation.getAccrualPaymentType().equalsIgnoreCase("A")) {
            policyTrans.setInterfaceType("A");
            policyTrans.setAccrualInstDate(wezeshaStockCreation.getAccrualInstDate());
            policyTrans.setAccrualPaymentType(wezeshaStockCreation.getAccrualPaymentType());
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
        final BigDecimal basicPrem = wezeshaStockCreation.getStockPremium().multiply(BigDecimal.valueOf(100/100.45));
        BindersDef newBinder = bindersRepo.findBinderByAccId(wezeshaStockCreation.getInsurerCode().getAcctId(), product.getProCode());
        riskTrans.setBinder(newBinder);
        String normalizedSubClass = wezeshaStockCreation.getProductName().trim().toLowerCase().replace(" ", "");
        String normalizedCoverType = wezeshaStockCreation.getCoverType().trim().toLowerCase().replace(" ", "");
        final SubClassDef subClassDef = subClassRepo.findBySubClassName(normalizedSubClass);
        final CoverTypesDef coverTypesDef = coverTypesRepo.findCoverTypesByBindId(newBinder.getBinId(), normalizedCoverType);
        System.out.println("Binder id ::" +newBinder.getBinId());
        System.out.println("CoverTypes id:: " +coverTypesDef.getCovId());
        BinderDetails newDetails = binderDetRepo.findOne(QBinderDetails.binderDetails.binder.binId.eq(newBinder.getBinId()).and(QBinderDetails.binderDetails.subCoverTypes.coverTypes.covId.eq(coverTypesDef.getCovId())));
        riskTrans.setSubclass(subClassDef);
        riskTrans.setCovertype(coverTypesDef);
        riskTrans.setRiskShtDesc(wezeshaStockCreation.getRiskId());
        riskTrans.setRiskDesc(wezeshaStockCreation.getRiskDesc());
        riskTrans.setBinderDetails(newDetails);
        riskTrans.setWefDate(wef);
        riskTrans.setWetDate(wet);
        riskTrans.setTransType("BU");
        riskTrans.setPolicy(savedPol);
        riskTrans.setButchargePrem(basicPrem);
        riskTrans.setAutogenCert("N");
        riskTrans.setSumInsured(wezeshaStockCreation.getStockSumInsured());
        riskTrans.setComputePremium(wezeshaStockCreation.getStockPremium());
        riskTrans.setNetpremium(wezeshaStockCreation.getStockPremium());
        String businessType = product.getProGroup().getPrgType();
        if (!businessType.equalsIgnoreCase("L")) {
            policyTrans.setBusinessType("N");
            riskTrans.setInstallmentNo(1L);
        } else {
            policyTrans.setBusinessType("L");
        }
        // get nb commm rates only
        Iterable<CommissionRates> commissionRatess = commRatesRepo.findAll(QCommissionRates.commissionRates.bindersDef.binId.eq(newBinder.getBinId())
                .and(QCommissionRates.commissionRates.applicableAt.eq("NB")));
        if(commissionRatess.spliterator().getExactSizeIfKnown()==0){
            throw new BadRequestException("Commission Rates is not setup");
        }
        CommissionRates commissionRate = Streamable.streamOf(commissionRatess).findAny().get();
        AccountDef accountDef = accountRepo.findOne(QAccountDef.accountDef.accountType.accountType.eq(AccountTypeEnum.INS)
                .and(QAccountDef.accountDef.acctId.eq(wezeshaStockCreation.getInsurerCode().getAcctId())));

        BigDecimal commissionRates = (commissionRate != null) ? commissionRate.getCommRate() : null;
        BigDecimal accountCommRates = (accountDef != null) ? accountDef.getAccountType().getCommRate() : null;

        if (commissionRates != null && commissionRates.compareTo(BigDecimal.ZERO) > 0) {
            riskTrans.setCommRate(commissionRates);
        } else if (accountCommRates != null && accountCommRates.compareTo(BigDecimal.ZERO) > 0) {
            riskTrans.setCommRate(accountCommRates);
        } else {
            throw new BadRequestException("Commission Rates is not setup");
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
        sectionTrans.setAmount(wezeshaStockCreation.getStockSumInsured());
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
            sectionRepo.save(sectionTrans);
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
        savedPol.setSumInsured(wezeshaStockCreation.getStockSumInsured());
        policyTransRepo.save(savedPol);

        //make ready the policy
        uploadValidatorsService.saveGeneralPolicyUpload(savedPol.getPolicyId(), false);

        //compute admin fee
       // uploadValidatorsService.computeAdmimFee(savedPol, polBinder, basicPrem);
        wezeshaStockCreation.setPolicyTrans(savedPol);
        wezeshaStockCreation.setTransProcessed("Y");
        wezeshaStockCreation.setAuthorized("N");
        wezeshaStockCreation.setProcessedDate(new Date());
        User user =  (userUtils.getCurrentUser()!=null)?userUtils.getCurrentUser():userRepo.findOne(userId);
        wezeshaStockCreation.setProcessedBy(user);//userUtils.getCurrentUser());
        wezeshaStockRepo.save(wezeshaStockCreation);

        return savedPol;
    }

    @Override
    public List<Long> bulkProcessWezeshaPolicies(BatchCreditBatch batchCreditBatch, boolean isApproved) throws BadRequestException {
        if (batchCreditBatch.getBatchRecords().size() <= 0) {
            throw new BadRequestException("Select At least One Transaction To Process");
        }

        Job job = JobBuilder.aNewJob()
                .reader(new IterableRecordReader(batchCreditBatch.getBatchRecords()))
                .named("wezesha_processing" + new SimpleDateFormat("ddMMyyyhhmmss").format(new Date()))
                .processor((RecordProcessor<Record, Record>) renForm -> {
                    Long policyId = ((BatchRecord) renForm.getPayload()).getPayload().getBatchId();
                    Long userId = ((BatchRecord) renForm.getPayload()).getPayload().getUserId();
                    processSingleWezeshPol(policyId, userId,true);
                    return renForm;
                })
                .pipelineListener(new RenewalJobListener())
                .build();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Future<JobReport> report = executorService.submit(job);

        return  new ArrayList<>();

//        if (wezeshaIds.size() <= 0) {
//            throw new BadRequestException("Select At least One Transaction To Process");
//        }
//        PolicyTrans savedPol = null;
//        List<Long> processedPolicyIds = new ArrayList<>();
//        for (Long wezeshaId : wezeshaIds) {
//           savedPol = processSingleWezeshPol(wezeshaId, true);
//           processedPolicyIds.add(savedPol.getPolicyId());
//        };
//        return processedPolicyIds;
    }

    @Override
    public String approveSingleWezeshaPol(Long policyId) throws BadRequestException {
        authService.authorizeBulkUploadPolicies(policyId);
        WezeshaStockCreation wezeshaStockCreation = wezeshaStockRepo.findWezeshaStockByPolicyId(policyId);
        wezeshaStockCreation.setAuthorized("Y");
        wezeshaStockRepo.save(wezeshaStockCreation);

        return "Transaction Authorized Successfully";
    }

    @Override
    public String approveBulkWezeshaPol(List<Long> policyIds) throws BadRequestException {

        if (policyIds.size() <= 0) {
            throw new BadRequestException("Select At least One Transaction To Process");
        }
        for (Long policyId : policyIds) {
            authService.authorizeBulkUploadPolicies(policyId);
            WezeshaStockCreation wezeshaStockCreation = wezeshaStockRepo.findWezeshaStockByPolicyId(policyId);
            wezeshaStockCreation.setAuthorized("Y");
            wezeshaStockRepo.save(wezeshaStockCreation);
        }

        return "Transactions Authorized Successfully";
    }

    @Override
    @Transactional
    public String deleteUploadWezeshaStock(List<Long> wezeshaIds) {
        System.out.println("Deleting wezeshaIds " + wezeshaIds);

        for (Long wezeshaId : wezeshaIds) {
            // Retrieve the WezeshaStockCreation record
            WezeshaStockCreation wezeshaStockCreation = wezeshaStockRepo.findOne(
                    QWezeshaStockCreation.wezeshaStockCreation.wezeshaStockId.eq(wezeshaId)
            );

            // Check if the record exists and if it is unprocessed (transProcessed == "N")
            if (wezeshaStockCreation != null && "N".equals(wezeshaStockCreation.getTransProcessed())) {
                wezeshaStockRepo.delete(wezeshaStockCreation);
                System.out.println("Deleted WezeshaStockCreation with ID: " + wezeshaId);
            } else {
                System.out.println("WezeshaStockCreation with ID " + wezeshaId + " is either null or already processed.");
                return  "Deletion failed, it's not found or has been processed.";
            }
        }
        return  "Deleted was successful";
    }
    @Override
    @Transactional
    public String deleteProcessedWezeshaStock(List<Long> wezeshaIds) throws BadRequestException{
        System.out.println("Deleting wezeshaIds " + wezeshaIds);

        for (Long wezeshaId : wezeshaIds) {
            // Retrieve the WezeshaStockCreation record
            WezeshaStockCreation wezeshaStockCreation = wezeshaStockRepo.findWithPolicyTransByWezeshaId(wezeshaId);


            if (wezeshaStockCreation == null) {
                System.out.println("No BulkPolicyCreation found for ID: " + wezeshaId);
                continue;
            }

            if (!"Y".equalsIgnoreCase(wezeshaStockCreation.getTransProcessed())) {
                System.out.println("Transaction not marked as processed for ID: " + wezeshaId);
                continue;
            }

            wezeshaStockRepo.delete(wezeshaStockCreation);
            uploadValidatorsService.deleteprocessedPol(wezeshaId);
        }
        return  "Transactions Deleted Successfully";
    }

}

