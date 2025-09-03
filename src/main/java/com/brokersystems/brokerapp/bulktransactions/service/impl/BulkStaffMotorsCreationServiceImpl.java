package com.brokersystems.brokerapp.bulktransactions.service.impl;

import com.brokersystems.brokerapp.bulktransactions.dtos.StaffMotorsCreationDTO;
import com.brokersystems.brokerapp.bulktransactions.models.*;
import com.brokersystems.brokerapp.bulktransactions.repositories.BulkPolicyCreationRepo;
import com.brokersystems.brokerapp.bulktransactions.repositories.BulkPolicyRiskRepository;
import com.brokersystems.brokerapp.bulktransactions.repositories.BulkStaffMotorsCreationRepo;
import com.brokersystems.brokerapp.bulktransactions.service.BulkStaffMotorsCreationService;
import com.brokersystems.brokerapp.bulktransactions.utils.ExcelReaderUtil;
import com.brokersystems.brokerapp.customscreens.model.ColumnForm;
import com.brokersystems.brokerapp.customscreens.model.TableForm;
import com.brokersystems.brokerapp.enums.AccountTypeEnum;
import com.brokersystems.brokerapp.enums.RevenueItems;
import com.brokersystems.brokerapp.schedules.model.ScheduleBean;
import com.brokersystems.brokerapp.schedules.service.ScheduleService;
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
import com.brokersystems.brokerapp.trans.service.PolicyAuthorization;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.uw.repository.*;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import com.brokersystems.brokerapp.uw.service.PremComputeService;
import com.brokersystems.brokerapp.webservices.model.VehicleDetails;
import com.mysema.query.types.Predicate;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class BulkStaffMotorsCreationServiceImpl implements BulkStaffMotorsCreationService {

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private ValidatorUtils validator;

    @Autowired
    private UserBranchesRepository userBranchesRepository;

    @Autowired
    private OrgBranchRepository orgBranchRepository;

    @Autowired
    private BulkStaffMotorsCreationRepo bulkStaffMotorsCreationRepo;
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
    private PolicyTransService policyTransService;

    @Autowired
    private ScheduleService scheduleService;

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private PremComputeService premiumService;
    @Autowired
    private PremComputeService premComputeServiceImpl;
    @Autowired
    private PolTaxesRepo polTaxesRepo;
    @Autowired
    private TaxRatesRepo taxRatesRepo;
    @Autowired
    private SectionRepo sectionsRepo;
    @Autowired
    private PolicyAuthorization authService;
    @Autowired
    private UploadValidatorsUtils uploadValidatorsUtils;
    @Autowired
    private BulkPolicyCreationRepo bulkPolicyCreationRepo;
    @Autowired
    private UploadValidatorsUtils uploadValidatorsService;

    @Autowired
    private PolActiveRisksRepo activeRisksRepo;

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> uploadBulkPolicy(MultipartFile file) throws BadRequestException {

        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            throw new BadRequestException("Upload files with .xlsx or .xls extension only");
        }

        Map<String, Object> response = new HashMap<>();
        List<String> invalidRecords = new ArrayList<>();
        List<BulkStaffMotorsCreation> bulkPolicyCreations = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);

            // Validate template format
            List<String> validationErrors = ExcelReaderUtil.validateAbsaStaffMotorsTemplate(workbook);
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
            String transType = "BPU";
            Predicate pedSystem = QSystemSequence.systemSequence.transType.eq("BPU");
            if (sequenceRepository.count(pedSystem) == 0)
                throw new BadRequestException("Sequence for Policy Upload has not been defined");


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
                    BulkStaffMotorsCreation bulkPolicyCreation = new BulkStaffMotorsCreation();
                    String coverId = ExcelReaderUtil.getCellValue(row, 0);// getCellStringValue(row.getCell(0));
                    System.out.println("coverId" + coverId);
                    bulkPolicyCreation.setCoverId(coverId);
                    bulkPolicyCreation.setInsuredName(ExcelReaderUtil.getCellValue(row, 1));//getCellStringValue(row.getCell(1)));

                    String staffAbNumber = ExcelReaderUtil.getCellValue(row, 2);//getCellStringValue(row.getCell(2));
                    bulkPolicyCreation.setABNumber(staffAbNumber);

                    String clntIdNo = ExcelReaderUtil.getCellValue(row, 3);// getCellStringValue(row.getCell(3));
                    if (!validator.validateIdNo(clntIdNo)) {
                        throw new BadRequestException("Invalid client ID Number " + clntIdNo);
                    }
                    bulkPolicyCreation.setClientIdNo(clntIdNo);

                    String clientKraPin = ExcelReaderUtil.getCellValue(row, 4);// getCellStringValue(row.getCell(4));
                    if (!validator.validatePin(clientKraPin)) {
                        throw new Exception("Invalid client PIN " + clientKraPin);
                    }
                    final ClientDef client = clientRepository.findByPinNoIgnoreCase(clientKraPin);
                    if (client != null) {
                        bulkPolicyCreation.setClientKraPin(clientKraPin);
                    } else {
                        throw new BadRequestException("Client with KRA Pin "+clientKraPin+" not found.");
                    }

                    bulkPolicyCreation.setAccountNo(ExcelReaderUtil.getCellValue(row, 5));//getCellStringValue(row.getCell(5)));
                    bulkPolicyCreation.setClientEmail(ExcelReaderUtil.getCellValue(row, 6));//getCellStringValue(row.getCell(6)));
                    bulkPolicyCreation.setPhoneNumber(ExcelReaderUtil.getCellValue(row, 7));//String.valueOf(Integer.parseInt(getCellStringValue(row.getCell(7)))));
                    //risk info
                    bulkPolicyCreation.setMotorBodyType(ExcelReaderUtil.getCellValue(row, 8));//getCellStringValue(row.getCell(8)));
                    bulkPolicyCreation.setMotorColor(ExcelReaderUtil.getCellValue(row, 9));//getCellStringValue(row.getCell(9)));
                    bulkPolicyCreation.setMotorLoadCapacity(BigDecimal.valueOf(Double.parseDouble(ExcelReaderUtil.getCellValue(row, 10))));//getCellStringValue(row.getCell(10)))));
                    bulkPolicyCreation.setTare(ExcelReaderUtil.getCellValue(row, 11));//getCellStringValue(row.getCell(11)));
                    bulkPolicyCreation.setNumberOfPassengers(Double.parseDouble(ExcelReaderUtil.getCellValue(row, 12)));//getCellStringValue(row.getCell(12))));
                    bulkPolicyCreation.setRegistrationNo(ExcelReaderUtil.getCellValue(row, 13));//getCellStringValue(row.getCell(13)));
                    bulkPolicyCreation.setMakeModel(ExcelReaderUtil.getCellValue(row, 14));//getCellStringValue(row.getCell(14)));
                    bulkPolicyCreation.setYom(ExcelReaderUtil.getCellValue(row, 15));//getCellStringValue(row.getCell(15)));
                    bulkPolicyCreation.setRating(Double.parseDouble(ExcelReaderUtil.getCellValue(row, 16)));//getCellStringValue(row.getCell(16))));
                    bulkPolicyCreation.setSumInsured(ExcelReaderUtil.parseBigDecimalSafe(ExcelReaderUtil.getCellValue(row, 17)));//getCellBigDecimalValue(row.getCell(17)));
                    Date wef = ExcelReaderUtil.parseFlexibleDate(ExcelReaderUtil.getCellValue(row, 18)); //getCellDateValue(row.getCell(18));
                    if (wef != null) {
                        bulkPolicyCreation.setCoverDateFrom(wef);
                    } else {
                        throw new BadRequestException("Cover Start date is required");
                    }
                    bulkPolicyCreation.setChasisNo(ExcelReaderUtil.getCellValue(row, 19));//getCellStringValue(row.getCell(19)));
                    bulkPolicyCreation.setEngineNo(ExcelReaderUtil.getCellValue(row, 20));//getCellStringValue(row.getCell(20)));

                    String insurerCode = ExcelReaderUtil.getCellValue(row, 21);//getCellStringValue(row.getCell(21));
                    AccountDef underWriter = accountRepo.findOne(QAccountDef.accountDef.shtDesc.eq(insurerCode.trim()));
                    if (underWriter != null) {
                        bulkPolicyCreation.setInsurerCode(underWriter);
                    } else {
                        throw new BadRequestException("Insurer with code " + insurerCode + " is not setup in the system, please contact system admin");
                    }

                    bulkPolicyCreation.setProductGroup(ExcelReaderUtil.getCellValue(row, 45));//getCellStringValue(row.getCell(45)));
                    String productName = ExcelReaderUtil.getCellValue(row, 44);// getCellStringValue(row.getCell(44));
                    String normalizedProductName = productName.trim().toLowerCase().replaceAll("\\s+", " ");
                    ProductsDef product = productsRepo.findByNormalizedPName(normalizedProductName);
                    if (product == null) {
                        throw new BadRequestException("Product " + productName + " not set in the system, please contact system admin.");
                    }
                    System.out.println("getting binder "+ bulkPolicyCreation.getInsurerCode().getAcctId() + " "+ product.getProCode() );
                    String contract = (ExcelReaderUtil.getCellValue(row, 46));
                    contract = contract.replaceAll("\\s+", " ").trim();
                    bulkPolicyCreation.setContractName(contract);
                    System.out.println("binder info "+ product.getProCode() + " binder"+ bulkPolicyCreation.getInsurerCode().getAcctId());
                    List<BindersDef> binders = bindersRepo.findBinderByAccIdAndBinName(bulkPolicyCreation.getInsurerCode().getAcctId(), product.getProCode());

                    if (binders.isEmpty()) {
                        throw new BadRequestException("Binder for product " + productName + " and insurer with code " + bulkPolicyCreation.getInsurerCode().getShtDesc()
                                + " not set in the system, please contact system admin.");
                    }
                    BindersDef binder = binders.get(0);
                    if(!binder.getProduct().isActive()){
                        throw new BadRequestException("The policy contract is not active. Please authorise the contract to continue...");
                    }

                    SubClassDef subClassDef = subClassRepo.findBySubClassName(normalizedProductName);
                    if (subClassDef != null){
                        bulkPolicyCreation.setProductName(subClassDef.getSubDesc());
                    } else {
                        throw new BadRequestException("Product " + normalizedProductName + " subclass not set in the system, please contact system admin.");
                    }

                    String coverType = (ExcelReaderUtil.getCellValue(row, 22));//getCellStringValue(row.getCell(22)));
                    String normalizedCoverType = coverType.trim().toLowerCase().replace(" ", "");
                    CoverTypesDef coverTypesDef = coverTypesRepo.findCoverTypesByBindId(binder.getBinId(), normalizedCoverType);
                    if (coverTypesDef != null) {
                        bulkPolicyCreation.setCoverType(coverTypesDef.getCovName());
                    }else {
                        throw new BadRequestException("Cover type " + coverType + " not set for insurer with code " + bulkPolicyCreation.getInsurerCode().getShtDesc() +" please contact system admin.");
                    }

                    bulkPolicyCreation.setCoverOption(ExcelReaderUtil.getCellValue(row, 23));//getCellStringValue(row.getCell(23)));
                    bulkPolicyCreation.setBuyBackOption(ExcelReaderUtil.getCellValue(row, 24));//getCellStringValue(row.getCell(24)));
                    bulkPolicyCreation.setBuyBack(ExcelReaderUtil.getCellValue(row, 25));//getCellStringValue(row.getCell(25)));
                    bulkPolicyCreation.setCoutseyCarsOption(ExcelReaderUtil.getCellValue(row, 26));//getCellStringValue(row.getCell(26)));
                    bulkPolicyCreation.setLossOfUse(ExcelReaderUtil.parseBigDecimalSafe(ExcelReaderUtil.getCellValue(row, 27)));//getCellStringValue(row.getCell(27)));
                    bulkPolicyCreation.setAaServiceOption(ExcelReaderUtil.getCellValue(row, 28));//getCellStringValue(row.getCell(28)));
                    bulkPolicyCreation.setAaService(ExcelReaderUtil.parseBigDecimalSafe(ExcelReaderUtil.getCellValue(row, 29)));//getCellStringValue(row.getCell(29)));
                    bulkPolicyCreation.setAmrefOption(ExcelReaderUtil.getCellValue(row, 30));//getCellStringValue(row.getCell(30)));
                    bulkPolicyCreation.setAmref(ExcelReaderUtil.parseBigDecimalSafe(ExcelReaderUtil.getCellValue(row, 31))); //getCellStringValue(row.getCell(31)));

                    bulkPolicyCreation.setBasicPrem(ExcelReaderUtil.parseBigDecimalSafe(ExcelReaderUtil.getCellValue(row, 32)));//getCellBigDecimalValue(row.getCell(32)));
                    bulkPolicyCreation.setExcessProtector(ExcelReaderUtil.parseBigDecimalSafe(ExcelReaderUtil.getCellValue(row, 33)));//getCellBigDecimalValue(row.getCell(33)));
                    bulkPolicyCreation.setPvt(ExcelReaderUtil.parseBigDecimalSafe(ExcelReaderUtil.getCellValue(row, 34)));//getCellBigDecimalValue(row.getCell(34)));
                    bulkPolicyCreation.setTax(ExcelReaderUtil.parseBigDecimalSafe(ExcelReaderUtil.getCellValue(row, 35)));//getCellBigDecimalValue(row.getCell(35)));
                    bulkPolicyCreation.setPll(ExcelReaderUtil.parseBigDecimalSafe(ExcelReaderUtil.getCellValue(row, 36)));//getCellBigDecimalValue(row.getCell(36)));
                    bulkPolicyCreation.setTotalPrem(ExcelReaderUtil.parseBigDecimalSafe(ExcelReaderUtil.getCellValue(row, 37)));//getCellBigDecimalValue(row.getCell(37)));
                    String coverStatus = ExcelReaderUtil.getCellValue(row, 38);
                    int coverStat;
                    if(coverStatus.equalsIgnoreCase("complete")){
                        coverStat = 1;
                    } else if(coverStatus.equalsIgnoreCase("incomplete")){
                        coverStat = 2;
                    } else{
                        throw new RuntimeException("Cover status can be complete or incomplete only.");
                    }

                    bulkPolicyCreation.setCoverStatus(coverStat);//getCellStringValue(row.getCell(38))));
                    bulkPolicyCreation.setApplicationType(ExcelReaderUtil.getCellValue(row, 39));//getCellStringValue(row.getCell(39)));
                    bulkPolicyCreation.setDateSubmitted(ExcelReaderUtil.getCellValue(row, 40));//getCellStringValue(row.getCell(40)));

                    String branchCode = ExcelReaderUtil.getCellValue(row, 41).trim();//getCellStringValue(row.getCell(41));
                    bulkPolicyCreation.setFrequency(ExcelReaderUtil.validateFrequency(ExcelReaderUtil.getCellValue(row, 42), row.getRowNum())); ////getCellStringValue(row.getCell(42)), row.getRowNum()));
                    OrgBranch orgBranch = orgBranchRepository.findOne(QOrgBranch.orgBranch.obShtDesc.eq(branchCode));
                    if (orgBranch != null) {
                        bulkPolicyCreation.setBranch(orgBranch);
                    } else {
                        throw new BadRequestException("Branch code " + branchCode + " in sheet two is not setup in the system, please contact system admin.");
                    }


                    String currency = ExcelReaderUtil.getCellValue(row, 43);//getCellStringValue(row.getCell(43));
                    if (currency != null && !currency.trim().isEmpty()) {
                        currency = currency.trim();
                        Currencies currencies = currencyRepository.findOne(QCurrencies.currencies.curIsoCode.eq(currency));
                        if (currencies == null) {
                            throw new BadRequestException("Currency code " + currency + " not setup in the system, please contact system admin");
                        }
                        bulkPolicyCreation.setCurrency(currencies.getCurIsoCode());
                    } else {
                        throw new BadRequestException("Currency is required in row " + row.getRowNum() + " in Currency ISO code column");
                    }

                    String paymentType = ExcelReaderUtil.getCellValue(row, 47);
                    if(!paymentType.isEmpty()){
                        if(paymentType.equalsIgnoreCase("Accrual")){
                            Date instDate = ExcelReaderUtil.parseFlexibleDate(ExcelReaderUtil.getCellValue(row, 48)); //getCellDateValue(row.getCell(20)));
                            if(instDate == null){
                                throw  new BadRequestException("Accrual installment date is required.");
                            }
                            bulkPolicyCreation.setAccrualInstDate(instDate);

                            String ApaymentType = ExcelReaderUtil.getCellValue(row, 49); //getCellDateValue(row.getCell(20)));
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
                } catch (Exception e) {
                    throw new BadRequestException("Sheet One, Row " + (rowNum + 1) + ": " + e.getMessage());
                }
            }
            int  successfulPolicy = bulkPolicyCreations.size();
            int failedRecords = invalidRecords.size();

            if (!(failedRecords > 0) && successfulPolicy > 0) {
                bulkStaffMotorsCreationRepo.save(bulkPolicyCreations);
            }

            response.put("successfulAbsaStaffMotorsPolicies", bulkPolicyCreations.size());
            response.put("failedRecords", invalidRecords.size());
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
                if (cellValue.contains("ab number") ||
                        cellValue.contains("sum insured") ||
                        cellValue.contains("name") ||
                        cellValue.contains("underwriter") ||
                        cellValue.contains("basic premium") ||
                        cellValue.contains("total premium") ||
                        cellValue.contains("mobile number") ||
                        cellValue.contains("registration no")) {
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
            if (row != null && !isHeaderRow(row)) {
                return rowNum;
            }
        }
        return -1;
    }

    private Double getCellDoubleValue(Cell cell) {
        return (cell != null && cell.getCellType() == Cell.CELL_TYPE_NUMERIC) ? cell.getNumericCellValue() : null;
    }


    @Override
    public DataTablesResult<StaffMotorsCreationDTO> findUnProcessedBulkPol(DataTablesRequest request) {
        Long currentUserId = userUtils.getCurrentUser().getId();

        List<Object[]> unProcessedBulkPols = bulkStaffMotorsCreationRepo.findUnprocessedBulkPol((request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%", request.getPageNumber(), request.getPageSize(), currentUserId);
        final List<StaffMotorsCreationDTO> unProcessed = new ArrayList<>();
        long rowCount = 0L;
        if (!unProcessedBulkPols.isEmpty()) rowCount = ((BigInteger) unProcessedBulkPols.get(0)[8]).intValue();

        for (Object[] unProcessedBulkPol : unProcessedBulkPols) {
            StaffMotorsCreationDTO staffMotorsCreationDTO = new StaffMotorsCreationDTO();
            staffMotorsCreationDTO.setCoverType((String) unProcessedBulkPol[0]);
            staffMotorsCreationDTO.setClientFname((String) unProcessedBulkPol[1]);
            staffMotorsCreationDTO.setMotorRegistrationNo((String) unProcessedBulkPol[2]);
            staffMotorsCreationDTO.setMotorModel((String) unProcessedBulkPol[3]);
            staffMotorsCreationDTO.setYOM((String) unProcessedBulkPol[4]);
            staffMotorsCreationDTO.setCoverDateFrom((Date) unProcessedBulkPol[5]);
            staffMotorsCreationDTO.setDateUploaded((Date) unProcessedBulkPol[6]);
            staffMotorsCreationDTO.setBulkPolicyId(((BigInteger) unProcessedBulkPol[7]).longValue());
            staffMotorsCreationDTO.setSumInsured(unProcessedBulkPol[9] != null ? (BigDecimal) unProcessedBulkPol[9] : null);

            unProcessed.add(staffMotorsCreationDTO);
        }
        Page<StaffMotorsCreationDTO> page = new PageImpl<>(unProcessed, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<StaffMotorsCreationDTO> viewBulkPolicies(DataTablesRequest request) {
        Long currentUserId = userUtils.getCurrentUser().getId();

        List<Object[]> viewBulkPolicies = bulkStaffMotorsCreationRepo.viewUnprocessedBulkPol((request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%", request.getPageNumber(), request.getPageSize(), currentUserId);
        final List<StaffMotorsCreationDTO> unProcessed = new ArrayList<>();
        long rowCount = 0L;
        if (!viewBulkPolicies.isEmpty()) rowCount = ((BigInteger) viewBulkPolicies.get(0)[10]).intValue();

        for (Object[] viewBulkPolicy : viewBulkPolicies) {
            StaffMotorsCreationDTO bulkPolicies = new StaffMotorsCreationDTO();
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
        Page<StaffMotorsCreationDTO> page = new PageImpl<>(unProcessed, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = false)
    public PolicyTrans processSingleBulkPolicy(Long bulkId, boolean isApproved) throws BadRequestException {
        PolicyTrans savedPol = null;
        BulkStaffMotorsCreation bulkPolicyCreations = bulkStaffMotorsCreationRepo.findOne(QBulkStaffMotorsCreation.bulkStaffMotorsCreation.bulkPolicyId.eq(bulkId));
        if (bulkPolicyCreations.getTransProcessed().equalsIgnoreCase("Y")) {
            throw new BadRequestException("This transaction is already processed");
        }

        final PolicyTrans policy = new PolicyTrans();
        Date wef = bulkPolicyCreations.getCoverDateFrom();
        final Date wet = dateUtils.getWetDate(wef);
        policy.setWefDate(wef);
        policy.setWetDate(wet);
        policy.setPolCreateddt(new Date());
        policy.setAuthStatus("LD");
        policy.setCurrentStatus("LD");
        policy.setCoverFrom(wef);
        policy.setCoverTo(wet);
        policy.setSumInsured(bulkPolicyCreations.getSumInsured());

        if(bulkPolicyCreations.getAmref() != null) {
            policy.setExtras(bulkPolicyCreations.getAmref());
        }

        String frequency = bulkPolicyCreations.getFrequency();

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
            throw new BadRequestException("Payment frequency is not defined for client ID " + bulkPolicyCreations.getClientIdNo() + " in frequency column in uploaded excel");
        }

        final String pinToSearch = bulkPolicyCreations.getClientKraPin().trim();
        final ClientDef client = clientRepository.findByPinNoIgnoreCase(pinToSearch);
        if (client != null) {
            policy.setClient(client);
        } else {
            throw new BadRequestException("Client with KRA pin " + pinToSearch + " is not onboarded in the system");
        }

        policy.setAgent(bulkPolicyCreations.getInsurerCode());

        String normalizedPrdName = bulkPolicyCreations.getProductName().trim().toLowerCase().replaceAll("\\s+", " ");
        final ProductsDef product = productsRepo.findByNormalizedPName(normalizedPrdName);
        if (product != null) {
            policy.setProduct(product);
            String businessType = product.getProGroup().getPrgType();
            if (!businessType.equalsIgnoreCase("L")) {
                policy.setBusinessType("N");
            } else {
                policy.setBusinessType("L");
            }
        } else {
            throw new BadRequestException(bulkPolicyCreations.getProductName() + " product is not found. Please set up the product.");
        }
        final String currToSearch = bulkPolicyCreations.getCurrency().trim();
        final Currencies currency = currencyRepository.findByPinNoIgnoreCase(currToSearch);
        if (currency != null) {
            policy.setTransCurrency(currency);
        } else {
            throw new BadRequestException(bulkPolicyCreations.getCurrency() + " currency is not set up. Please add the currency to the system.");
        }
        UserBranches userBranches = userBranchesRepository.findByUser(userUtils.getCurrentUser());
        Long branchId = userBranches.getBranch().getObId();
        final OrgBranch branches = orgBranchRepository.findOne(QOrgBranch.orgBranch.obId.eq(branchId));
        policy.setBranch(branches);
        policy.setCreatedUser(userUtils.getCurrentUser());
        policy.setPreviousTrans(policy);
     //   System.out.println("am looking for binders"+ bulkPolicyCreations.getInsurerCode().getAcctId() + " "+ product.getProCode());
//        List<BindersDef> polBinder = bindersRepo.findBinderByAccIdAndBinName(bulkPolicyCreations.getInsurerCode().getAcctId(), product.getProCode(),bulkPolicyCreations.getContractName());  // bindersRepo.findBinderByAccId(policyCreation.getInsurerCode().getAcctId(), product.getProCode());
//        if(polBinder == null){
//            throw new BadRequestException("BINDER NOT FOUND");
//        }
//        policy.setBinder(polBinder);
//        if((polBinder.getAdminFeeActiveStatus() != null) && (polBinder.getAdminFeeActiveStatus().equalsIgnoreCase("Y"))) {
//            policy.setAdminFeeApplicable("Y");
//        } else {
//            policy.setAdminFeeApplicable("N");
//        }

        List<BindersDef> polBinders =  bindersRepo.findBinderByAccIdAndBinName(bulkPolicyCreations.getInsurerCode().getAcctId(), product.getProCode());  // bindersRepo.findBinderByAccId(policyCreation.getInsurerCode().getAcctId(), product.getProCode());
        if (polBinders.isEmpty()) {
            throw new BadRequestException("Default Binder for product " + product.getProDesc() + " and insurer with code " + bulkPolicyCreations.getInsurerCode().getShtDesc()
                    + " not set in the system, please contact system admin.");
        }
        BindersDef polBinder = polBinders.get(0);
        policy.setBinder(polBinder);
        if((polBinder.getAdminFeeActiveStatus() != null) && (polBinder.getAdminFeeActiveStatus().equalsIgnoreCase("Y"))) {
            policy.setAdminFeeApplicable("Y");
        } else {
            policy.setAdminFeeApplicable("N");
        }

        SystemTrans systemTrans = null;
        if (bulkPolicyCreations.getTransProcessed().equalsIgnoreCase("N")) {

            String policyNumberFormat = paramService.getParameterString("POLICY_NO_FORMAT");
            String endorsementFormat = paramService.getParameterString("ENDORSE_NO_FORMAT");
            Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("P");
            if (sequenceRepository.count(seqPredicate) == 0)
                throw new BadRequestException("Sequence for New Business Transactions has not been defined");
            SystemSequence sequence = sequenceRepository.findOne(seqPredicate);
            Long seqNumber = sequence.getNextNumber();
            final String policyNumber = templateMerger.generateFormat(policyNumberFormat, bulkPolicyCreations.getBranch().getObId(), product.getProCode(), policy.getWefDate(), sequence.getSeqPrefix() + String.format("%05d", seqNumber), null);
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
            final String endorseNumber = templateMerger.generateFormat(endorsementFormat, bulkPolicyCreations.getBranch().getObId(), product.getProCode(), bulkPolicyCreations.getCoverDateFrom(), revNumber, null);
            policy.setPolRevNo(endorseNumber + "/1");
            policy.setRevisionFormat(endorseNumber);
            endorseSequence.setLastNumber(endosseqNumber);
            endorseSequence.setNextNumber(endosseqNumber + 1);
            sequenceRepository.save(endorseSequence);

        }
        if (product.isRenewable())
            policy.setRenewable(product.isRenewable());
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
     //   policy.setBasicPrem(bulkPolicyCreations.getTotalPrem()); //gross amount
       // policy.setPremium(bulkPolicyCreations.getBasicPrem()); //basic  pol_
        //    policy.setNetPrem(bulkPolicyCreations.()); ///

        if(bulkPolicyCreations.getAccrualPaymentType() != null && bulkPolicyCreations.getAccrualPaymentType().equalsIgnoreCase("A")) {
            policy.setInterfaceType("A");
            policy.setAccrualInstDate(bulkPolicyCreations.getAccrualInstDate());
            policy.setAccrualPaymentType(bulkPolicyCreations.getAccrualPaymentType());
        }else{
            policy.setInterfaceType("C");
        }

        savedPol = policyTransRepo.save(policy);

        systemTrans = new SystemTrans();
        systemTrans.setDoneDate(new Date());
        systemTrans.setDoneBy(userUtils.getCurrentUser());
        systemTrans.setPolicy(savedPol);
        systemTrans.setTransLevel("U");
        systemTrans.setTransCode("BUD");
        systemTrans.setTransAuthorised("N");
        systemTransRepo.save(systemTrans);

        final String insuredPinSerach = bulkPolicyCreations.getClientKraPin().trim();
        final ClientDef insured = clientRepository.findByPinNoIgnoreCase(insuredPinSerach);
        RiskTrans riskTrans = new RiskTrans();
        if (insured != null) {
            riskTrans.setInsured(insured);
        } else {
            throw new BadRequestException("Insured is with KRA pin " + insuredPinSerach + " is not onboarded in the system");
        }
        final BigDecimal excessPrem = (bulkPolicyCreations.getExcessProtector()!=null)?bulkPolicyCreations.getExcessProtector():BigDecimal.ZERO;
        final BigDecimal pvtPrem = (bulkPolicyCreations.getPvt()!=null)?bulkPolicyCreations.getPvt():BigDecimal.ZERO;
        final BigDecimal lossOfUsePrem = (bulkPolicyCreations.getLossOfUse()!=null)?bulkPolicyCreations.getLossOfUse():BigDecimal.ZERO;
        final BigDecimal basicPrem = bulkPolicyCreations.getBasicPrem().add(excessPrem).add(pvtPrem).add(lossOfUsePrem);

        System.out.println("am looking for new binders"+ bulkPolicyCreations.getInsurerCode().getAcctId() + " "+ product.getProCode());
        List<BindersDef> newBinders = bindersRepo.findBinderByAccIdAndBinName(bulkPolicyCreations.getInsurerCode().getAcctId(), product.getProCode());  // bindersRepo.findBinderByAccId(policyCreation.getInsurerCode().getAcctId(), product.getProCode());
        if (newBinders.isEmpty()) {
            throw new BadRequestException("Default Binder for product " + product.getProDesc() + " and insurer with code " + bulkPolicyCreations.getInsurerCode().getShtDesc()
                    + " not set in the system, please contact system admin.");
        }
        BindersDef newBinder = newBinders.get(0);
        riskTrans.setBinder(newBinder);
        String normalizedSubClass = bulkPolicyCreations.getProductName().trim().toLowerCase().replaceAll("\\s+", " ");
        String normalizedCoverType = bulkPolicyCreations.getCoverType().trim().toLowerCase().replaceAll("\\s+", " ");
        final SubClassDef subClassDef = subClassRepo.findBySubClassName(normalizedSubClass);
        final CoverTypesDef coverTypesDef = coverTypesRepo.findCoverTypesByBindId(newBinder.getBinId(),normalizedCoverType);
      //  System.out.println("cover binder "+newBinder.getBinName()+" "+coverTypesDef.getCovId());
        BinderDetails newDetails = binderDetRepo.findOne(QBinderDetails.binderDetails.binder.binId.eq(newBinder.getBinId()).and(QBinderDetails.binderDetails.subCoverTypes.coverTypes.covId.eq(coverTypesDef.getCovId())));
        riskTrans.setSubclass(subClassDef);
        riskTrans.setCovertype(coverTypesDef);
        riskTrans.setRiskShtDesc(bulkPolicyCreations.getRegistrationNo());
        riskTrans.setRiskDesc(bulkPolicyCreations.getMakeModel());
        riskTrans.setBinderDetails(newDetails);
        riskTrans.setWefDate(wef);
        riskTrans.setWetDate(wet);
        riskTrans.setTransType("BU");
        riskTrans.setPolicy(savedPol);
        riskTrans.setButchargePrem(basicPrem);
        riskTrans.setAutogenCert("N");
        riskTrans.setComputeType("S");
        riskTrans.setSumInsured(bulkPolicyCreations.getSumInsured());
        riskTrans.setPremium(bulkPolicyCreations.getBasicPrem());
        riskTrans.setComputePremium(bulkPolicyCreations.getBasicPrem());
        riskTrans.setNetpremium(bulkPolicyCreations.getBasicPrem());
        policy.setPremium(bulkPolicyCreations.getBasicPrem());

        String businessType = product.getProGroup().getPrgType();
        if (!businessType.equalsIgnoreCase("L")) {
            policy.setBusinessType("N");
            riskTrans.setInstallmentNo(1l);
        } else {
            policy.setBusinessType("L");
        }
//
//        CommissionRates commissionRate = commRatesRepo.findOne(QCommissionRates.commissionRates.bindersDef.binId.eq(newBinder.getBinId()));
//        AccountDef accountDef = accountRepo.findOne(QAccountDef.accountDef.accountType.accountType.eq(AccountTypeEnum.INS)
//                .and(QAccountDef.accountDef.acctId.eq(savedPol.getAgent().getAcctId())));
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
//
        // get nb commm rates only

        Iterable<CommissionRates> commissionRatess = commRatesRepo.findAll(QCommissionRates.commissionRates.bindersDef.binId.eq(newBinder.getBinId())
                .and(QCommissionRates.commissionRates.applicableAt.eq("NB")));
        if(commissionRatess.spliterator().getExactSizeIfKnown()==0){
            throw new BadRequestException("Commissio Rates is not setup");
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

        System.out.println("Am here at commisisons "+ accountCommRates +" "+commissionRates );
        riskTrans = riskTransRepo.save(riskTrans);
        long riskIdentifier = Long.valueOf(String.valueOf(dateUtils.getUwYear(policy.getWefDate())) + String.valueOf(riskTrans.getRiskId()));
        riskTrans.setRiskIdentifier(riskIdentifier);
        RiskTrans savedTrans = riskTransRepo.save(riskTrans);

        //RiskTrans savedRisk = riskRepo.save(risk);
        //long riskIdentifier = Long.valueOf(String.valueOf(dateUtils.getUwYear(policy.getWefDate())) + String.valueOf(savedRisk.getRiskId()));
        PolicyActiveRisks activeRisk = new PolicyActiveRisks();
        activeRisk.setPolicy(policy);
        activeRisk.setRisk(riskTrans);
        activeRisk.setRiskIdentifier(riskIdentifier);
        activeRisksRepo.save(activeRisk);
        savedTrans.setRiskIdentifier(riskIdentifier);
        riskTransRepo.save(savedTrans);

        //sections and tax compute area
        saveSectionTransaction("VALUE", bulkPolicyCreations.getSumInsured(), savedTrans);
        saveSectionTransaction("PVT", bulkPolicyCreations.getPvt(), savedTrans);
        saveSectionTransaction("EXCESS", bulkPolicyCreations.getExcessProtector(), savedTrans);
        saveSectionTransaction("PLLI", bulkPolicyCreations.getPll(), savedTrans);
        saveSectionTransaction("AA RESCUE", bulkPolicyCreations.getAaService(), savedTrans);
        saveSectionTransaction("LOSS OF USE", bulkPolicyCreations.getLossOfUse(), savedTrans);
        saveSectionTransaction("AMREF", bulkPolicyCreations.getAmref(), savedTrans);


        Iterable<TaxRates> taxRates = taxRatesRepo.findAll((QTaxRates.taxRates.active.eq(true).and(QTaxRates.taxRates.mandatory.eq(Boolean.TRUE))).and(QTaxRates.taxRates.subclass.subId.eq(savedTrans.getSubclass().getSubId()))
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
                polstampDuty = polstampDuty.add(polstampDuty);
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
        savedPol.setStampDuty(BigDecimal.ZERO); //polstampDuty); //for bulk upload stampduty is zero
        savedPol.setFuturePrem(BigDecimal.ZERO);
        savedPol.setSumInsured(bulkPolicyCreations.getSumInsured());

        policyTransRepo.save(savedPol);

       /// uploadValidatorsUtils.computeAdmimFee(savedPol, newBinder, basicPrem);
        uploadValidatorsService.saveGeneralPolicyUpload(savedPol.getPolicyId(), false);

        bulkPolicyCreations.setPolicyTrans(savedPol);
        bulkPolicyCreations.setTransProcessed("Y");
        bulkPolicyCreations.setPolicyAuthorized("N");
        bulkPolicyCreations.setProcessedBy(userUtils.getCurrentUser());
        bulkPolicyCreations.setDateProcessed(new Date());
        bulkStaffMotorsCreationRepo.save(bulkPolicyCreations);
        return savedPol;
    }

    @Override
    @Transactional
    public void bulkDelUploadPolicies(List<Long> bulkIds) throws BadRequestException {
        if (bulkIds.size() <= 0) {
            throw new BadRequestException("Select At least One Transaction To Process");
        }

        for (Long bulkId : bulkIds) {
            //get bulk policy uploaded
            BulkStaffMotorsCreation bulkPolicyCreation = bulkStaffMotorsCreationRepo.findOne(QBulkStaffMotorsCreation.bulkStaffMotorsCreation.bulkPolicyId.eq(bulkId));
            bulkStaffMotorsCreationRepo.delete(bulkPolicyCreation);
        }
    }

    @Override
    @Transactional
    public String bulkDelProcessedPolicies(List<Long> bulkIds) throws BadRequestException {
        if (bulkIds.size() <= 0) {
            throw new BadRequestException("Select At least One Transaction To Process");
        }

        for (Long bulkId : bulkIds) {
            //get bulk policy uploaded
            BulkStaffMotorsCreation bulkPolicyCreation = bulkStaffMotorsCreationRepo.findBulkStockByPolicyId(bulkId);
            if (bulkPolicyCreation == null) {
                System.out.println("No BulkPolicyCreation found for ID: " + bulkId);
                continue;
            }

            if ("Y".equalsIgnoreCase(bulkPolicyCreation.getTransProcessed())){
                RiskTrans riskTrans = riskTransRepo.findByBulkPolicyId(bulkId);
                riskTransRepo.deleteFromMotorPrivateScheduleByBulkId(bulkId);
                riskTransRepo.deleteActRisksByBulkId(bulkId);
                riskTransRepo.deleteLimitsByBulkId(bulkId);
                riskTransRepo.delete(riskTrans);

                continue;
            }

            uploadValidatorsService.deleteprocessedPol(bulkId);
            bulkStaffMotorsCreationRepo.delete(bulkPolicyCreation);
            //delete the created policy

        }

        return "Transactions Deleted Successfully";
    }


    @Override
    @Transactional
    public List<Long> bulkProcessPolicies(List<Long> bulkIds, boolean isApproved) throws BadRequestException {
        if (bulkIds.size() <= 0) {
            throw new BadRequestException("Select At least One Transaction To Process");
        }
        PolicyTrans savedPol = null;
        List<Long> processedPolicyIds = new ArrayList<>();
        for (Long bulkId : bulkIds) {
            savedPol = processSingleBulkPolicy(bulkId,true);
            processedPolicyIds.add(savedPol.getPolicyId());
        }
        return processedPolicyIds;
    }

    @Override
    public void processRisks(PolicyTrans savedPolicy) throws BadRequestException {
        if (savedPolicy == null) {
            throw new BadRequestException("Policy is null");
        }

        // Fetch the BulkStaffMotorsCreation entry tied to this policy
        BulkStaffMotorsCreation bulkPolicyCreations = bulkStaffMotorsCreationRepo
                .findOne(QBulkStaffMotorsCreation.bulkStaffMotorsCreation.policyTrans.eq(savedPolicy));

        if (bulkPolicyCreations == null) {
            throw new BadRequestException("BulkStaffMotorsCreation record not found for policy");
        }

        // Get the first available risk
        RiskTrans savedRisk = riskTransRepo.findByPolicyId(savedPolicy.getPolicyId());
        if (savedRisk == null) {
            throw new BadRequestException("No RiskTrans records associated with this policy");
        }

        // Get schedule table metadata
        Map<String, Object> tableStructure = policyTransService.getRiskSchedules(savedRisk.getRiskId());
        System.out.println("Table structure: " + tableStructure);

        TableForm tableForm = (TableForm) tableStructure.get("tableForm");
        if (tableForm == null) {
            throw new BadRequestException("Risk ID to schedule mapping not found");
        }

        String tableName = tableForm.getDatatableName();
        List<ColumnForm> columnFormList = tableForm.getColumnFormList();

        Map<String, Object> scheduleData = new HashMap<>();

        for (ColumnForm column : columnFormList) {
            String colName = column.getName();
            String lowerColName = colName.toLowerCase();

            if (lowerColName.contains("s_brk_risks_id")) {
                scheduleData.put(colName, savedRisk.getRiskId());
            } else if (lowerColName.contains("pri_code")) {
                scheduleData.put(colName, -2000); // Example hardcoded value
            } else if (lowerColName.contains("yom") || lowerColName.contains("year of manufacture")) {
                scheduleData.put(colName, Long.parseLong(bulkPolicyCreations.getYom()));
            } else if (lowerColName.contains("reg.mark")) {
                scheduleData.put(colName, bulkPolicyCreations.getRegistrationNo());
            } else if (lowerColName.contains("make/model")) {
                scheduleData.put(colName, bulkPolicyCreations.getMakeModel());
            } else if (lowerColName.contains("type of body")) {
                scheduleData.put(colName, bulkPolicyCreations.getMotorBodyType());
            } else if (lowerColName.contains("tonnage")) {
                scheduleData.put(colName, bulkPolicyCreations.getMotorLoadCapacity());
            } else if (lowerColName.contains("chasis number")) {
                scheduleData.put(colName, bulkPolicyCreations.getChasisNo());
            } else if (lowerColName.contains("engine number")) {
                scheduleData.put(colName, bulkPolicyCreations.getEngineNo());
            }

            // Optional: print column metadata for debugging
            System.out.println("Column Name: " + colName);
            System.out.println("Type: " + column.getType());
            System.out.println("Length: " + column.getLength());
            System.out.println("Mandatory: " + column.getMandatory());
            System.out.println("---");
        }

        System.out.println("Table is: " + tableName + ", Data: " + scheduleData);

        // Persist the data into the dynamic table
        policyTransService.insertOrUpdateDataIntoCustomTable(tableName, scheduleData);
    }
    private void saveSectionTransaction(String sectionCode, BigDecimal amount, RiskTrans savedTrans) {
        SectionsDef sectionsDef = sectionsRepo.findOne(QSectionsDef.sectionsDef.shtDesc.eq(sectionCode));
        if (sectionsDef == null) {
            System.out.println("Section not found for: " + sectionCode);
            return;
        }

        SectionTrans sectionTrans = new SectionTrans();
        sectionTrans.setAmount(amount);
        sectionTrans.setSection(sectionsDef);
        sectionTrans.setCompute(true);
        sectionTrans.setDivFactor(BigDecimal.valueOf(100));

        Iterable<PremRatesDef> premRatesDefs = premRatesRepo.findAll(QPremRatesDef.premRatesDef.section.shtDesc.eq(sectionCode));
        PremRatesDef premRatesDef = null;
        if (premRatesDefs.iterator().hasNext()) {
            premRatesDef = premRatesDefs.iterator().next();
        }

        if (premRatesDef != null) {
            sectionTrans.setPremRates(premRatesDef);
            sectionTrans.setRate(premRatesDef.getRate());
            if(sectionCode!=null && (sectionCode.equalsIgnoreCase("AA RESCUE")||sectionCode.equalsIgnoreCase("AMREF"))) {
                sectionTrans.setRate(amount);
            }
            sectionTrans.setRisk(savedTrans);
            sectionTrans.setFreeLimit(BigDecimal.ZERO);
            sectionTrans.setDivFactor(premRatesDef.getDivFactor());
            sectionRepo.save(sectionTrans);
            System.out.println("Saved section transaction for: " + sectionCode);
        } else {
            System.out.println("No premium rate found for: " + sectionCode);
        }
    }


    @Override
    public String approveSingleMortgageLifePol(Long policyId) throws BadRequestException {
        authService.authorizeBulkUploadPolicies(policyId);

        BulkStaffMotorsCreation bulkPolicyCreation = bulkStaffMotorsCreationRepo.findBulkStockByPolicyId(policyId);
        bulkPolicyCreation.setPolicyAuthorized("Y");
        bulkStaffMotorsCreationRepo.save(bulkPolicyCreation);
        return "Transaction Authorized Successfully";
    }

    @Override
    public String approveBulkMortgageLifePol(List<Long> policyIds) throws BadRequestException {
        if (policyIds.size() <= 0){
            throw new BadRequestException("Select At least One Transaction To Process");
        }
        for(Long policyId : policyIds){
            authService.authorizeBulkUploadPolicies(policyId);
            BulkStaffMotorsCreation policyCreation = bulkStaffMotorsCreationRepo.findBulkStockByPolicyId(policyId);
            policyCreation.setPolicyAuthorized("Y");
            bulkStaffMotorsCreationRepo.save(policyCreation);
        }

        return "Transactions Authorized Successfully";
    }


    private  void staffMotorsComputePrem(){
        //total amount without tax = EXCESS BUY + LOSS + AA + AMREF + BP + EXCESS  PROTECTOR + PVT + PLL
        BigDecimal TAWT = BigDecimal.ZERO;
        //total amt without tax less aa and amref //  TAWTLAA = TAWT - AA - AMREF
        BigDecimal TAWTLAA = BigDecimal.ZERO;
        //tax = TAWTLAA * 0.0045
        BigDecimal tax = BigDecimal.ZERO;
        //bp = si * 0.035
        //if bp < 20000 bp = 20000 else bp = bp
        BigDecimal basicPrem = BigDecimal.ZERO;
        //gp = TAWTLAA + tax + amref + aa
        BigDecimal grossPrem = BigDecimal.ZERO;
        //comm = 0.4 * TAWTLAA




    }
}