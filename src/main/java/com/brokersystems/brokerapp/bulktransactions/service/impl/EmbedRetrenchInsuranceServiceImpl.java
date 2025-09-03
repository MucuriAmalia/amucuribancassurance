package com.brokersystems.brokerapp.bulktransactions.service.impl;

import com.brokersystems.brokerapp.bulktransactions.dtos.EmbedPackageInsuranceDTO;
import com.brokersystems.brokerapp.bulktransactions.dtos.EmbedRetrenchInsuranceDTO;
import com.brokersystems.brokerapp.bulktransactions.models.*;
import com.brokersystems.brokerapp.bulktransactions.repositories.EmbedPackageInsuranceRiskRepo;
import com.brokersystems.brokerapp.bulktransactions.repositories.EmbedRetrenchInsuranceRepo;
import com.brokersystems.brokerapp.bulktransactions.repositories.EmbedRetrenchInsuranceRisksRepo;
import com.brokersystems.brokerapp.bulktransactions.service.EmbedRetrenchInsuranceService;
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
import com.brokersystems.brokerapp.trans.model.SystemTrans;
import com.brokersystems.brokerapp.trans.repository.SystemTransRepo;
import com.brokersystems.brokerapp.trans.service.PolicyAuthorization;
import com.brokersystems.brokerapp.uw.dtos.PolicyCreateDTO;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.uw.repository.PolActiveRisksRepo;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import com.brokersystems.brokerapp.uw.repository.RiskTransRepo;
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
import java.util.stream.Collectors;

@Service
public class EmbedRetrenchInsuranceServiceImpl implements EmbedRetrenchInsuranceService {

    @Autowired
    private EmbedRetrenchInsuranceRepo embedRetrenchInsuranceRepo;
    @Autowired
    private PolicyAuthorization authService;
    @Autowired
    private SequenceRepository sequenceRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private ClientTypeRepo clientTypeRepo;
    @Autowired
    private OrgBranchRepository orgBranchRepository;
    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private UserUtils userUtils;
    @Autowired
    private DateUtilities dateUtils;
    @Autowired
    private ProductsRepo productsRepo;
    @Autowired
    private BindersRepo bindersRepo;
    @Autowired
    private SubClassRepo subClassRepo;
    @Autowired
    private CoverTypesRepo coverTypesRepo;
    @Autowired
    private RiskTransRepo riskTransRepo;
    @Autowired
    private PolActiveRisksRepo activeRisksRepo;
    @Autowired
    private ParamService paramService;
    @Autowired
    private PolicyTransRepo policyTransRepo;
    @Autowired
    private CommRatesRepo commRatesRepo;
    @Autowired
    private BinderDetRepo binderDetRepo;
    @Autowired
    private TemplateMerger templateMerger;
    @Autowired
    private PaymentModeRepo paymentModeRepo;
    @Autowired
    private SystemTransRepo systemTransRepo;
    @Autowired
    private EmbedRetrenchInsuranceRisksRepo embedRetrenchInsuranceRisksRepo;
    @Autowired
    private UploadValidatorsUtils uploadValidatorsUtils;
    @Autowired
    private UserRepository userRepo;

    private static final int BATCH_SIZE = 500;

    @Async
    public void uploadEmbedRetrench(File file,  Long userId) throws BadRequestException {
        User user = userRepo.findOne(userId);
        Map<String, Object> response = new HashMap<>();
        List<String> invalidRecords = new ArrayList<>();
        List<EmbedRetrenchInsurance> embedRetrenchment = new ArrayList<>();
        List<EmbedRetrenchInsuranceRisks> retrenchRisks = new ArrayList<>();

        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
            Workbook workbook = WorkbookFactory.create(inputStream);


            Sheet sheetOne = workbook.getSheetAt(0);
            Map<String, EmbedRetrenchInsurance> retrenchMap = new HashMap<>();

            int startRowIndex = findDataStartRow(sheetOne);
            if (startRowIndex == -1) {
                throw new BadRequestException("Sheet One contains no data.");
            }

            String bulkCode = null;
            String transType = "ERIU";
            Predicate pedSystem = QSystemSequence.systemSequence.transType.eq("ERIU");
            if (sequenceRepository.count(pedSystem) == 0)
                throw new BadRequestException("Sequence for Embedded Retrenchment Insurance Upload has not been defined");


            String validatedCurrency = "";
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
                    EmbedRetrenchInsurance embedRetrenchInsurance = new EmbedRetrenchInsurance();
                    String serialNo = ExcelReaderUtil.getCellValue(row, 0); //getCellStringValue(row.getCell(0));
                    System.out.println("serialNo" + serialNo);
                    if(serialNo.isEmpty()){
                        throw new BadRequestException("Please provide a serial number that will be used to link with the risk details on sheet two");
                    }
                    embedRetrenchInsurance.setSerialNo(serialNo);

                    String clntIdNo = ExcelReaderUtil.getCellValue(row, 1); // getCellStringValue(row.getCell(3));
                    embedRetrenchInsurance.setClientId(clntIdNo);
                    String cif = ExcelReaderUtil.getCellValue(row, 2); //getCellStringValue(row.getCell(4));
                    embedRetrenchInsurance.setClientCIF(cif); //getCellStringValue(row.getCell(10)));

                    final ClientDef client = clientRepository.findByCifandIdNoIgnoreCase(cif, clntIdNo);
                    if (client != null) {
                        embedRetrenchInsurance.setClientName(client.getFname());
                        embedRetrenchInsurance.setClientOtherNames(client.getOtherNames());
                        embedRetrenchInsurance.setClientDOB(client.getDob());
                        embedRetrenchInsurance.setClientPin(client.getPinNo());
                        embedRetrenchInsurance.setClientEmail(client.getEmailAddress());
                        embedRetrenchInsurance.setClientPhone(client.getPhoneNo());
                        embedRetrenchInsurance.setTenantType(client.getTenantType());

                    } else {
                        throw new BadRequestException("Client with ID No "+ clntIdNo + " not found.");
                    }
//                    embedRetrenchInsurance.setClientName(ExcelReaderUtil.getCellValue(row, 1)); //getCellStringValue(row.getCell(1)));
//                    embedRetrenchInsurance.setClientOtherNames(ExcelReaderUtil.getCellValue(row, 2)); //getCellStringValue(row.getCell(2)));
//                    embedRetrenchInsurance.setClientDOB(ExcelReaderUtil.parseFlexibleDate((ExcelReaderUtil.getCellValue(row, 3)))); //getCellDateValue(row.getCell(3)));
//                    embedRetrenchInsurance.setClientId(ExcelReaderUtil.getCellValue(row, 4)); //getCellStringValue(row.getCell(4)));
//                    embedRetrenchInsurance.setClientPin(ExcelReaderUtil.getCellValue(row, 5)); //getCellStringValue(row.getCell(5)));
//                    embedRetrenchInsurance.setClientEmail(ExcelReaderUtil.getCellValue(row, 6)); //getCellStringValue(row.getCell(6)));
//                    embedRetrenchInsurance.setClientPhone(ExcelReaderUtil.getCellValue(row, 7)); //getCellStringValue(row.getCell(7)));

//                    String code = ExcelReaderUtil.getCellValue(row, 8); //row.getCell(8));
//                    Country country = countryRepository.findOne(QCountry.country.prefix.eq(code));
//                    if (country != null) {
//                        embedRetrenchInsurance.setCountryCode(country);
//                    } else {
//                        throw new BadRequestException("Country code " + code + " is not setup, please contact system admin");
//                    }

//                    String clientType = ExcelReaderUtil.getCellValue(row, 9); //getCellStringValue(row.getCell(9));
//                    String normalizedClientType = clientType.trim().toLowerCase().replace(" ", "");
//                    ClientTypes clientTypes = clientTypeRepo.findByNormalizeType(normalizedClientType);
//                    if (clientTypes != null) {
//                        embedRetrenchInsurance.setTenantType(clientTypes);
//                    } else {
//                        throw new BadRequestException("Client type " + clientType + " is not setup, please contact system admin");
//                    }
//
//                    embedRetrenchInsurance.setClientCIF(ExcelReaderUtil.getCellValue(row, 10)); //getCellStringValue(row.getCell(10)));
                    String branchCode = ExcelReaderUtil.getCellValue(row, 3); //getCellStringValue(row.getCell(11));
                    OrgBranch orgBranch = orgBranchRepository.findOne(QOrgBranch.orgBranch.obShtDesc.eq(branchCode.trim()));
                    if (orgBranch != null) {
                        embedRetrenchInsurance.setOrgBranch(orgBranch);
                    } else {
                        throw new BadRequestException("Branch code " + branchCode + " in sheet two is not setup in the system, please contact system admin.");
                    }


                    String insurerCode = ExcelReaderUtil.getCellValue(row, 4); //getCellStringValue(row.getCell(12));
                    AccountDef underWriter = accountRepo.findOne(QAccountDef.accountDef.shtDesc.eq(insurerCode.trim()));
                    if (underWriter != null) {
                        embedRetrenchInsurance.setInsurerCode(underWriter);
                    } else {
                        throw new BadRequestException("Insurer with code " + insurerCode + " is not setup in the system, please contact system admin");
                    }
                    embedRetrenchInsurance.setProductGroup(ExcelReaderUtil.getCellValue(row, 5)); //getCellStringValue(row.getCell(13)));
                    String productName = (ExcelReaderUtil.getCellValue(row, 6)); //getCellStringValue(row.getCell(14)));

                    String searchCode = "ERLPU";
                    String normalizedProductName = productName.trim().replaceAll("\\s+", " ").toLowerCase();
                    ProductsDef product = productsRepo.findByNormalizedName(normalizedProductName, searchCode);
                    if (product == null) {
                        throw new BadRequestException("Product " + productName + " not set in the system, please contact system admin.");
                    }
                    //BindersDef binder = bindersRepo.findBinderByAccIdANDBinName(embedRetrenchInsurance.getInsurerCode().getAcctId(), product.getProCode(), binName);
                    BindersDef binder = bindersRepo.findBinderByAccId(embedRetrenchInsurance.getInsurerCode().getAcctId(), product.getProCode());
                    if (binder == null) {
                        throw new BadRequestException("Binder for product " + productName + " and insurer with code " + embedRetrenchInsurance.getInsurerCode().getShtDesc()
                                + " not set in the system, please contact system admin.");
                    }

                    String coverType = (ExcelReaderUtil.getCellValue(row, 7)); //getCellStringValue(row.getCell(15)));
                    String normalizedCoverType = coverType.trim().toLowerCase().replaceAll("\\s+", " ");
//                    String subClassDefName = "EMBEDDED RETRENCHMENT";
//                    SubClassDef subClassDef = subClassRepo.findBySubClassName(subClassDefName);
                    SubClassDef subClassDef = subClassRepo.findBySubClassName(normalizedProductName);
                    System.out.println("am here at subclassdeff >> "+normalizedCoverType);
                    if (subClassDef != null){
                        embedRetrenchInsurance.setProductName(subClassDef.getSubDesc());
                    } else {
                        throw new BadRequestException("Sub class for Product " + productName + " by name "+ normalizedProductName+" not set in the system, please contact system admin.");
                    }
                    CoverTypesDef coverTypesDef = coverTypesRepo.findCoverTypesByBindId(binder.getBinId(), normalizedCoverType);
                    System.out.println("am here at covertypesdef "+binder.getBinId()+" "+ normalizedCoverType);
                    if (coverTypesDef != null) {
                        embedRetrenchInsurance.setCoverType(coverTypesDef.getCovName());
                    }else {
                        throw new BadRequestException("Cover type " + coverType + " not set for insurer with code " + embedRetrenchInsurance.getInsurerCode().getShtDesc() +" please contact system admin.");
                    }

                    String frequency = ExcelReaderUtil.getCellValue(row, 8); // row.getRowNum())); //getCellStringValue(row.getCell(16)), row.getRowNum()));
                    ExcelReaderUtil.validateFrequency(frequency, row.getRowNum());
                    embedRetrenchInsurance.setFrequency(frequency);
                    String currency = ExcelReaderUtil.getCellValue(row, 9); //getCellStringValue(row.getCell(17));

                    if (currency != null && !currency.trim().isEmpty()) {
                        currency = currency.trim();
                        Currencies currencies = currencyRepository.findOne(QCurrencies.currencies.curIsoCode.eq(currency));
                        if (currencies == null) {
                            throw new BadRequestException("Currency code " + currency + " is not setup in the system, please contact system admin");
                        }
                        validatedCurrency = currencies.getCurIsoCode();
                        embedRetrenchInsurance.setCurrencyCode(currencies);
                    } else {
                        throw new BadRequestException("Currency is required in row " + row.getRowNum() + " in Currency ISO code column");
                    }

                    embedRetrenchInsurance.setCoverFrom(ExcelReaderUtil.parseFlexibleDate((ExcelReaderUtil.getCellValue(row, 10)))); //getCellDateValue(row.getCell(18)));
                    embedRetrenchInsurance.setCoverTo(ExcelReaderUtil.parseFlexibleDate((ExcelReaderUtil.getCellValue(row, 11)))); //getCellDateValue(row.getCell(19)));
                    embedRetrenchInsurance.setUploadDate(new Date());
                    embedRetrenchInsurance.setUploadedBy(user);
                    embedRetrenchInsurance.setTranStatus("N");
                    embedRetrenchInsurance.setTransType(transType);
                    embedRetrenchInsurance.setRefCode(bulkCode);

                    embedRetrenchment.add(embedRetrenchInsurance);
                    retrenchMap.put(serialNo, embedRetrenchInsurance);
                    if (embedRetrenchment.size() >= BATCH_SIZE) {
                        embedRetrenchInsuranceRepo.save(embedRetrenchment);
                        embedRetrenchment.clear();
                    }
                    System.out.println("am here saving the retrench insurance");
                } catch (Exception e) {
                    throw new BadRequestException("Sheet One " + e.getMessage());
                }
            }

            int successfulPol = embedRetrenchment.size();
            int invalidPol = invalidRecords.size();
            System.out.println("successfulPol>:: " + successfulPol);
            System.out.println("invalidPol>:: " + invalidRecords.toString());
//            if (!(invalidPol > 0) && successfulPol > 0) {
//                embedRetrenchInsuranceRepo.save(embedRetrenchment);
//            }
            if (!embedRetrenchment.isEmpty()) {
                embedRetrenchInsuranceRepo.save(embedRetrenchment);
                embedRetrenchment.clear();
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

                    List<EmbedRetrenchInsuranceRisks> riskList = Optional.ofNullable(
                            (List<EmbedRetrenchInsuranceRisks>) embedRetrenchInsuranceRisksRepo.findAll(
                                    QEmbedRetrenchInsuranceRisks.embedRetrenchInsuranceRisks.retrenchStatus.eq("Y")
                                            .and(QEmbedRetrenchInsuranceRisks.embedRetrenchInsuranceRisks.retrenchCode.eq(bulkCode))
                            )
                    ).orElse(new ArrayList<>());

                    System.out.println("riskList:: " + riskList);

                    List<EmbedRetrenchInsurance> bulkPolicyList = (List<EmbedRetrenchInsurance>) embedRetrenchInsuranceRepo.findAll(
                            QEmbedRetrenchInsurance.embedRetrenchInsurance.tranStatus.eq("N")
                                    .and(QEmbedRetrenchInsurance.embedRetrenchInsurance.refCode.eq(bulkCode))
                    );
                    System.out.println("bulkPolicyList:: " + bulkPolicyList);

                    // Convert bulkPolicyList into a Set for faster lookups
                    Set<String> refCodeSet = bulkPolicyList.stream()
                            .map(EmbedRetrenchInsurance::getRefCode)
                            .collect(Collectors.toSet());

                    for (EmbedRetrenchInsuranceRisks checkRisk : riskList) {
                        if (refCodeSet.contains(checkRisk.getRetrenchCode())) {
                            // Mismatch found, immediately throw an error
                            throw new BadRequestException(
                                    "Serial No " + checkRisk.getSerialNo() + " in sheet 2 is pending processing, process before re-uploading this file"
                            );
                        }
                    }

                    // Proceed to add new risk if no mismatch was found
                    String serialNo = ExcelReaderUtil.getCellValue(row, 0); //getCellStringValue(row.getCell(0));
                    System.out.println("Serial No sheet two: " + serialNo);
                    EmbedRetrenchInsurance linkedPolicy = retrenchMap.get(serialNo);

                    if (linkedPolicy == null) {
                        throw new BadRequestException(
                                "Row " + (rowNum + 1) + ": Serial number '" + serialNo + "' not found in Sheet One."
                        );
                    }

                    EmbedRetrenchInsuranceRisks embedRetrenchRisk = new EmbedRetrenchInsuranceRisks();

                    embedRetrenchRisk.setSerialNo(serialNo);
                    String clntIdNo = ExcelReaderUtil.getCellValue(row, 1); //getCellStringValue(row.getCell(4));
                    String cif = ExcelReaderUtil.getCellValue(row, 2); //getCellStringValue(row.getCell(4));

                    embedRetrenchRisk.setInsuredCIF(cif); //getCellStringValue(row.getCell(10)));
                    embedRetrenchRisk.setInsuredID(clntIdNo); //getCellStringValue(row.getCell(4)));
                    final ClientDef client = clientRepository.findByCifandIdNoIgnoreCase(cif, clntIdNo);
                    if (client != null) {
                        embedRetrenchRisk.setInsuredFname(client.getFname());
                        embedRetrenchRisk.setInsuredOthernames(client.getOtherNames());
                        embedRetrenchRisk.setInsuredDOB(client.getDob());
                        embedRetrenchRisk.setInsuredPin(client.getPinNo());
                        embedRetrenchRisk.setInsuredEmail(client.getEmailAddress());
                        embedRetrenchRisk.setInsuredPhone(client.getPhoneNo());
                        embedRetrenchRisk.setTenantType(client.getTenantType());

                    } else {
                        throw new BadRequestException("Client with ID No "+ clntIdNo + " not found.");
                    }

                    embedRetrenchRisk.setInsuredAcc(ExcelReaderUtil.getCellValue(row, 3)); //getCellStringValue(row.getCell(10)));
                    embedRetrenchRisk.setAccOpenDate(ExcelReaderUtil.parseFlexibleDate((ExcelReaderUtil.getCellValue(row, 4)))); //getCellDateValue(row.getCell(11)));
                    embedRetrenchRisk.setTransCode(ExcelReaderUtil.getCellValue(row, 5)); //getCellStringValue(row.getCell(12)));
                    embedRetrenchRisk.setTransDate(ExcelReaderUtil.parseFlexibleDate((ExcelReaderUtil.getCellValue(row, 6)))); //getCellDateValue(row.getCell(13)));
                    String branch = ExcelReaderUtil.getCellValue(row, 7); //getCellStringValue(row.getCell(14));
                    OrgBranch orgBranch = orgBranchRepository.findOne(QOrgBranch.orgBranch.obShtDesc.eq(branch.trim()));
                    if (orgBranch != null) {
                        embedRetrenchRisk.setInsuredBranch(orgBranch);
                    } else {
                        throw new BadRequestException("Branch code " + branch + " in sheet two is not setup in the system, please contact system admin.");
                    }
                    BigDecimal premium = ExcelReaderUtil.parseBigDecimalSafe(ExcelReaderUtil.getCellValue(row, 8)); //getCellBigDecimalValue(row.getCell(15)));
                    premium = premium.abs();
                    //ExcelReaderUtil.validateCurrency(validatedCurrency,premium);
                    embedRetrenchRisk.setRetrenchPremium(premium);
                    embedRetrenchRisk.setRetrenchInsurance(linkedPolicy);
                    embedRetrenchRisk.setUploadedBy(user);
                    embedRetrenchRisk.setUploadDate(new Date());
                    embedRetrenchRisk.setRetrenchStatus("N");
                    embedRetrenchRisk.setRetrenchCode(bulkCode);

                    retrenchRisks.add(embedRetrenchRisk);
                    if (retrenchRisks.size() >= BATCH_SIZE) {
                        embedRetrenchInsuranceRisksRepo.save(retrenchRisks);
                        retrenchRisks.clear();
                    }
                } catch (Exception e) {
                    throw new BadRequestException("Sheet Two " + e.getMessage());
                }
            }

            int successfulRisks = retrenchRisks.size();
            int invalidRisks = invalidRecords.size();

//            if (!(invalidRisks > 0) && successfulRisks > 0) {
//                embedRetrenchInsuranceRisksRepo.save(retrenchRisks);
//            }
            if (!retrenchRisks.isEmpty()) {
                embedRetrenchInsuranceRisksRepo.save(retrenchRisks);
                retrenchRisks.clear();
            }

            response.put("successfulPolicies", embedRetrenchment.size());
            response.put("successfulRisks", retrenchRisks.size());
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
    public DataTablesResult<EmbedRetrenchInsuranceDTO> findUnprocessedEmbedRetrench(DataTablesRequest request) {
        Long currentUserId = userUtils.getCurrentUser().getId();

        List<Object[]> unprocessRetrenchInsur = embedRetrenchInsuranceRepo.findUnprocessedEmbedRetrench((request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%", request.getPageNumber(), request.getPageSize(), currentUserId);
        final List<EmbedRetrenchInsuranceDTO> packageInsuranceDTOS = new ArrayList<>();
        long rowCount = 0L;
        if (!unprocessRetrenchInsur.isEmpty())
            rowCount = ((BigInteger) unprocessRetrenchInsur.get(0)[9]).intValue();
        for (Object[] embedRetrench : unprocessRetrenchInsur) {
            EmbedRetrenchInsuranceDTO packageInsuranceDTO = new EmbedRetrenchInsuranceDTO();
            packageInsuranceDTO.setEmbedPackageId(((BigInteger) embedRetrench[0]).longValue());
            packageInsuranceDTO.setCoverType((String) embedRetrench[1]);
            packageInsuranceDTO.setProductName((String) embedRetrench[2]);
            packageInsuranceDTO.setInsurerName((String) embedRetrench[3]);
            packageInsuranceDTO.setClientFName((String) embedRetrench[4]);
            packageInsuranceDTO.setClientOtherNames((String) embedRetrench[5]);
            packageInsuranceDTO.setCoverFrom((Date) embedRetrench[6]);
            packageInsuranceDTO.setCoverTo((Date) embedRetrench[7]);
            packageInsuranceDTO.setUploadedDate((Date) embedRetrench[8]);

            packageInsuranceDTOS.add(packageInsuranceDTO);
        }
        Page<EmbedRetrenchInsuranceDTO> page = new PageImpl<>(packageInsuranceDTOS, request, rowCount);

        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<EmbedRetrenchInsuranceDTO> viewUnprocessedEmbedRetrench(DataTablesRequest request) {
        Long currentUserId = userUtils.getCurrentUser().getId();

        List<Object[]> viewEmbedRetrench = embedRetrenchInsuranceRepo.viewUnprocessedEmbedRetrench((request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%", request.getPageNumber(), request.getPageSize(), currentUserId);
        final List<EmbedRetrenchInsuranceDTO> insuranceDTOS = new ArrayList<>();
        long rowCount = 0L;
        if (!viewEmbedRetrench.isEmpty()) rowCount = ((BigInteger) viewEmbedRetrench.get(0)[10]).intValue();
        for (Object[] viewRetrench : viewEmbedRetrench) {
            EmbedRetrenchInsuranceDTO embedInsuranceDTO = new EmbedRetrenchInsuranceDTO();
            embedInsuranceDTO.setPolId(((BigInteger) viewRetrench[0]).longValue());
            embedInsuranceDTO.setCoverType((String) viewRetrench[1]);
            embedInsuranceDTO.setPolNo((String) viewRetrench[2]);
            embedInsuranceDTO.setPolBasicPrem((BigDecimal) viewRetrench[3]);
            embedInsuranceDTO.setInsuredFName((String) viewRetrench[4]);
            embedInsuranceDTO.setInsuredOtherNames((String) viewRetrench[5]);
            embedInsuranceDTO.setPolWef((Date) viewRetrench[6]);
            embedInsuranceDTO.setPolWet((Date) viewRetrench[7]);
            embedInsuranceDTO.setProcessedDate((Date) viewRetrench[8]);
            embedInsuranceDTO.setPolicyStatus((String) viewRetrench[9]);

            insuranceDTOS.add(embedInsuranceDTO);
        }
        Page<EmbedRetrenchInsuranceDTO> page = new PageImpl<>(insuranceDTOS, request, rowCount);

        return new DataTablesResult<>(request, page);
    }

    @Override
    public PolicyTrans processSingleEmbedRetrenchPol(Long retrenchId, Long userId, boolean isApproved) throws BadRequestException {
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
        EmbedRetrenchInsurance retrenchInsurance = embedRetrenchInsuranceRepo.findOne(retrenchId);
        Iterable<EmbedRetrenchInsuranceRisks> insuranceRisks = embedRetrenchInsuranceRisksRepo.findAll(
                QEmbedRetrenchInsuranceRisks.embedRetrenchInsuranceRisks.retrenchInsurance.retrenchId.eq(retrenchId));
        if (retrenchInsurance.getTranStatus().equalsIgnoreCase("Y")) {
            throw new BadRequestException("This transaction is already processed");
        }
        final PolicyTrans policyTrans = new PolicyTrans();
        Date wef = retrenchInsurance.getCoverFrom();
        Date wet = retrenchInsurance.getCoverTo();

        System.out.println("wet pol" + wet);
        policyTrans.setPolCreateddt(new Date());
        policyTrans.setAuthStatus("LD");
        policyTrans.setCurrentStatus("LD");
//        policyTrans.setCoverFrom(wef);
//        policyTrans.setCoverTo(wet);
        policyDto.setWefDate(wef);
        policyDto.setWetDate(wet);
//        policyTrans.setWefDate(wef);
//        policyTrans.setWetDate(wet);
        String frequency = retrenchInsurance.getFrequency();

        policyDto.setTotalInstalments(null);
        //policyTrans.setPolTerm(1);
        policyDto.setPolTerm(1);


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
            throw new BadRequestException("Payment frequency is not defined for client ID " + retrenchInsurance.getClientId() + " in frequency column in uploaded excel");
        }
        final String pinSearch = retrenchInsurance.getClientPin().trim();
        final ClientDef client = clientRepository.findByPinNoIgnoreCase(pinSearch);
        if (client != null) {
            //policyTrans.setClient(client);
            policyDto.setClientId(client.getTenId());
        } else {
           throw new BadRequestException("Client with Kra Pin "+pinSearch+" not found");
        }
        //policyTrans.setAgent(retrenchInsurance.getInsurerCode());
        policyDto.setAgentId(retrenchInsurance.getInsurerCode().getAcctId());

        String searchCode = "ERLPU";
        String normalizedPrdName = retrenchInsurance.getProductName().trim().toLowerCase().replace(" ", "");
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
            throw new BadRequestException(retrenchInsurance.getProductName() + " product is not found. Please set up the product.");
        }
//        policyTrans.setTransCurrency(retrenchInsurance.getCurrencyCode());
        policyDto.setCurrencyId(retrenchInsurance.getCurrencyCode().getCurCode());

//        policyTrans.setBranch(retrenchInsurance.getOrgBranch());
        policyDto.setBranchId(retrenchInsurance.getOrgBranch().getObId());

//        policyTrans.setUwYear(dateUtils.getUwYear(wef));
//        policyTrans.setCreatedUser(userUtils.getCurrentUser());
//        policyTrans.setPreviousTrans(policyTrans);
//
        BindersDef polBinder = bindersRepo.findBinderByAccId(retrenchInsurance.getInsurerCode().getAcctId(), product.getProCode());
        policyDto.setBindCode(polBinder.getBinId());

        if((polBinder.getAdminFeeActiveStatus() != null) && (polBinder.getAdminFeeActiveStatus().equalsIgnoreCase("Y"))) {
            policyTrans.setAdminFeeApplicable("Y");
        } else {
            policyTrans.setAdminFeeApplicable("N");
        }
        //policyTrans.setBinder(polBinder);
        PaymentModes paymentModes = paymentModeRepo.findOne(QPaymentModes.paymentModes.pmDesc.eq("CASH"));
        System.out.println("paymentModes id:: " + paymentModes.getPmId());
        //policyTrans.setPaymentMode(paymentModes);
        policyDto.setPaymentId(paymentModes.getPmId());

//        SystemTrans systemTrans = null;
//        if (retrenchInsurance.getTranStatus().equalsIgnoreCase("N")) {
//
//            String policyNumberFormat = paramService.getParameterString("POLICY_NO_FORMAT");
//            String endorsementFormat = paramService.getParameterString("ENDORSE_NO_FORMAT");
//            String proposalFormat = paramService.getParameterString("PROPOSAL_NO_FORMAT");
//            Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("P");
//            if (sequenceRepository.count(seqPredicate) == 0)
//                throw new BadRequestException("Sequence for New Business Transactions has not been defined");
//            SystemSequence sequence = sequenceRepository.findOne(seqPredicate);
//            Long seqNumber = sequence.getNextNumber();
//            final String policyNumber = templateMerger.generateFormat(policyNumberFormat, retrenchInsurance.getOrgBranch().getObId(), product.getProCode(), policyTrans.getWefDate(), sequence.getSeqPrefix() + String.format("%05d", seqNumber), null);
//            policyTrans.setPolNo(policyNumber);
//
//            sequence.setLastNumber(seqNumber);
//            sequence.setNextNumber(seqNumber + 1);
//            sequenceRepository.save(sequence);
//            Predicate endorsePredicate = QSystemSequence.systemSequence.transType.eq("E");
//            if (sequenceRepository.count(endorsePredicate) == 0)
//                throw new BadRequestException("Sequence for Endorsement Transactions has not been defined");
//            SystemSequence endorseSequence = sequenceRepository.findOne(endorsePredicate);
//            Long endosseqNumber = endorseSequence.getNextNumber();
//            final String revNumber = endorseSequence.getSeqPrefix() + String.format("%05d", endosseqNumber);
//            final String endorseNumber = templateMerger.generateFormat(endorsementFormat, retrenchInsurance.getOrgBranch().getObId(), product.getProCode(), retrenchInsurance.getCoverFrom(), revNumber, null);
//            policyTrans.setPolRevNo(endorseNumber + "/1");
//            policyTrans.setRevisionFormat(endorseNumber);
//            endorseSequence.setLastNumber(endosseqNumber);
//            endorseSequence.setNextNumber(endosseqNumber + 1);
//            sequenceRepository.save(endorseSequence);
//
//            Predicate propPredicate = QSystemSequence.systemSequence.transType.eq("PR");
//            if (sequenceRepository.count(propPredicate) == 0)
//                throw new BadRequestException("Sequence for Proposal Transactions has not been defined");
//            SystemSequence lifeSequence = sequenceRepository.findOne(propPredicate);
//            Long lifeSeqNumber = lifeSequence.getNextNumber();
//            final String proposalNo = templateMerger.generateFormat(proposalFormat, retrenchInsurance.getOrgBranch().getObId(), product.getProCode(), wef, lifeSequence.getSeqPrefix() + String.format("%05d", lifeSeqNumber), null);
//            if (proposalNo == null) {
//                throw new BadRequestException("Proposal no has not been generated");
//            }
//            System.out.println("Proposal No " + proposalNo);
//            //policyTrans.setProposalNo(proposalNo);
//        }

//        policyTrans.setRenewable(product.isRenewable());
//        policyTrans.setTransType("BU");
//        policyTrans.setPolRevStatus("LD");
//        policyTrans.setInterfaceType("C");
//        policyTrans.setPolCreateddt(new Date());
        //savedPol = policyTransRepo.save(policyTrans);
        //retrenchInsurance.setPolicyTrans(savedPol);

//        systemTrans = new SystemTrans();
//        systemTrans.setDoneDate(new Date());
//        systemTrans.setDoneBy(userUtils.getCurrentUser());
//        systemTrans.setPolicy(savedPol);
//        systemTrans.setTransLevel("U");
//        systemTrans.setTransCode("BUD");
//        systemTrans.setTransAuthorised("N");
//        systemTransRepo.save(systemTrans);

        for (EmbedRetrenchInsuranceRisks insuranceRisk : insuranceRisks) {
            RiskTrans riskTrans = new RiskTrans();
            final String pinToSearch = retrenchInsurance.getClientPin().trim();
            final ClientDef insured = clientRepository.findByPinNoIgnoreCase(pinToSearch);
            if (insured != null) {
//                riskTrans.setInsured(insured);
            } else {
               throw  new BadRequestException("Client with Kra pin"+ pinToSearch +" not found");
            }

            BindersDef newBinder = bindersRepo.findBinderByAccId(retrenchInsurance.getInsurerCode().getAcctId(), product.getProCode());
            String normalizedSubClass = retrenchInsurance.getProductName().trim().toLowerCase().replace(" ", "");
            String normalizedCoverType = retrenchInsurance.getCoverType().trim().toLowerCase().replace(" ", "");
            final SubClassDef subClassDef = subClassRepo.findBySubClassName(normalizedSubClass);
            final CoverTypesDef coverTypesDef = coverTypesRepo.findCoverTypesByBindId(newBinder.getBinId(), normalizedCoverType);
            System.out.println("Binder id ::" + newBinder.getBinId());
            System.out.println("CoverTypes id:: " + coverTypesDef.getCovId());
            BinderDetails newDetails = binderDetRepo.findOne(QBinderDetails.binderDetails.binder.binId.eq(newBinder.getBinId()).and(QBinderDetails.binderDetails.subCoverTypes.coverTypes.covId.eq(coverTypesDef.getCovId())));
            String businessType = product.getProGroup().getPrgType();
            if (!businessType.equalsIgnoreCase("L")) {
//                policyTrans.setBusinessType("N");
            } else {
//                policyTrans.setBusinessType("L");
            }
//            riskTrans.setBinder(newBinder);
//            riskTrans.setCovertype(coverTypesDef);
//            riskTrans.setSubclass(subClassDef);
//            riskTrans.setBinderDetails(newDetails);
//            riskTrans.setAutogenCert("N");
//            riskTrans.setPolicy(savedPol);
//            riskTrans.setComputePremium(insuranceRisk.getRetrenchPremium());
//            riskTrans.setButchargePrem(insuranceRisk.getRetrenchPremium());
//            riskTrans.setWefDate(wef);
//            riskTrans.setWetDate(wet);
//            riskTrans.setPremium(insuranceRisk.getRetrenchPremium());
//            riskTrans.setSumInsured(BigDecimal.ZERO);
//            riskTrans.setTransType("BU");

            Integer age = dateUtils.getAge(retrenchInsurance.getClientDOB());
            riskTrans.setWorkingAge(age);

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
            riskBean.setPremium(insuranceRisk.getRetrenchPremium());
            riskBean.setSumInsured(BigDecimal.ZERO);
            policyDto.setRiskBean(riskBean);
            policyDto.setNegotiatedPremium(insuranceRisk.getRetrenchPremium());
            List<RiskSectionBean> sections = uploadValidatorsUtils.saveSectionTransaction("SUM ASSURED",newDetails.getDetId(), new BigDecimal("26")); //buildSectionBeans("29226", new BigDecimal("26"), savedRiskTrans);
            policyDto.setSections(sections);
            //to avoid calculator
            break;

//            CommissionRates commissionRate = commRatesRepo.findOne(QCommissionRates.commissionRates.bindersDef.binId.eq(newBinder.getBinId()));
//            AccountDef accountDef = accountRepo.findOne(QAccountDef.accountDef.accountType.accountType.eq(AccountTypeEnum.INS)
//                    .and(QAccountDef.accountDef.acctId.eq(retrenchInsurance.getInsurerCode().getAcctId())));
//
//            BigDecimal commissionRates = (commissionRate != null) ? commissionRate.getCommRate() : null;
//            BigDecimal accountCommRates = (accountDef != null) ? accountDef.getAccountType().getCommRate() : null;
//
//            if (commissionRates != null && commissionRates.compareTo(BigDecimal.ZERO) > 0) {
//                riskTrans.setCommRate(commissionRates);
//            } else if (accountCommRates != null && accountCommRates.compareTo(BigDecimal.ZERO) > 0) {
//                riskTrans.setCommRate(accountCommRates);
//            } else {
//                throw new BadRequestException("Commission Rates is not setup");
//            }

//            RiskTrans savedRisk = riskTransRepo.save(riskTrans);
//            long riskIdentifier = Long.valueOf(String.valueOf(dateUtils.getUwYear(policyTrans.getWefDate())) + String.valueOf(savedRisk.getRiskId()));
//            PolicyActiveRisks activeRisk = new PolicyActiveRisks();
//            activeRisk.setPolicy(policyTrans);
//            activeRisk.setRisk(riskTrans);
//            activeRisk.setRiskIdentifier(riskIdentifier);
//            activeRisksRepo.save(activeRisk);
//            savedRisk.setRiskIdentifier(riskIdentifier);
//            riskTransRepo.save(savedRisk);

        }



        //for life only
        PolicyTrans created = uploadValidatorsUtils.saveLifePolicyUpload(policyDto,userId);
        //save the remaining risk items

        retrenchInsurance.setTranStatus("Y");
        retrenchInsurance.setProcessedDate(new Date());
        User user = userRepo.findOne(userId);
        retrenchInsurance.setProcessedBy(user);
        retrenchInsurance.setPolicyAuthorized("N");
        embedRetrenchInsuranceRepo.save(retrenchInsurance);

        //assing the pol id
        retrenchInsurance.setPolicyTrans(created);
        retrenchInsurance.setPolicyAuthorized("N");
        embedRetrenchInsuranceRepo.save(retrenchInsurance);

        return created;
    }

    @Override
    public List<Long> processBulkEmbedRetrenchPol(BatchCreditBatch batchCreditBatch, boolean isApproved) throws BadRequestException {
        if (batchCreditBatch.getBatchRecords().size() <= 0) {
            throw new BadRequestException("Select At least One Transaction To Process");
        }

        Job job = JobBuilder.aNewJob()
                .reader(new IterableRecordReader(batchCreditBatch.getBatchRecords()))
                .named("embeded_pkg_processing" + new SimpleDateFormat("ddMMyyyhhmmss").format(new Date()))
                .processor((RecordProcessor<Record, Record>) renForm -> {
                    Long policyId = ((BatchRecord) renForm.getPayload()).getPayload().getBatchId();
                    Long userId = ((BatchRecord) renForm.getPayload()).getPayload().getUserId();
                    processSingleEmbedRetrenchPol(policyId, userId,true);
                    return renForm;
                })
                .pipelineListener(new RenewalJobListener())
                .build();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Future<JobReport> report = executorService.submit(job);

        return  new ArrayList<>();
//        if (retrenchIds.size() <= 0) {
//            throw new BadRequestException("Select At least One Transaction To Process");
//        }
//        PolicyTrans savedPol = null;
//        List<Long> processedPolicyIds = new ArrayList<>();
//        for (Long retrenchId : retrenchIds) {
//            savedPol = processSingleEmbedRetrenchPol(retrenchId, true);
//            processedPolicyIds.add(savedPol.getPolicyId());
//        }
//        return processedPolicyIds;
    }

    @Override
    public String approveSingleEmbedRetrenchPol(Long policyId) throws BadRequestException {
        authService.authorizeBulkUploadPolicies(policyId);
        EmbedRetrenchInsurance embedPackageInsurance = embedRetrenchInsuranceRepo.findBulkStockByPolicyId(policyId);
        embedPackageInsurance.setPolicyAuthorized("Y");
        embedRetrenchInsuranceRepo.save(embedPackageInsurance);
        return "Transaction Authorized Successfully";
    }

    @Override
    public String approveBulkEmbedRetrenchPol(List<Long> policyIds) throws BadRequestException {
        if (policyIds.size() <= 0) {
            throw new BadRequestException("Select At least One Transaction To Process");
        }
        for (Long policyId : policyIds) {
            authService.authorizeBulkUploadPolicies(policyId);
            EmbedRetrenchInsurance embedPackageInsurance = embedRetrenchInsuranceRepo.findBulkStockByPolicyId(policyId);
            embedPackageInsurance.setPolicyAuthorized("Y");
            embedRetrenchInsuranceRepo.save(embedPackageInsurance);
        }

        return "Transactions Authorized Successfully";
    }

    @Override
    public String deleteBulkEmbedRetrenchPol(List<Long> retrenchIds) throws BadRequestException {
        if (retrenchIds.size() <= 0) {
            throw new BadRequestException("Select At least One Transaction To Process");
        }
        for (Long retrenchId : retrenchIds) {
            EmbedRetrenchInsurance retrenchInsurance = embedRetrenchInsuranceRepo.findOne(retrenchId);

            //check if the insurance is not processed
            if ("N".equalsIgnoreCase(retrenchInsurance.getTranStatus())) {
                List<EmbedRetrenchInsuranceRisks> insuranceRisks = embedRetrenchInsuranceRisksRepo.findAllRiskByRetrenchInsurance(retrenchInsurance.getRetrenchId());
                //(QEmbedRetrenchInsuranceRisks.embedRetrenchInsuranceRisks.retrenchInsurance.retrenchId.eq(retrenchId));

                for (EmbedRetrenchInsuranceRisks insuranceRisk : insuranceRisks) {
                    //check if processed
                    if ("N".equalsIgnoreCase(insuranceRisk.getRetrenchStatus())) {
                        //delete the insurance risks
                        embedRetrenchInsuranceRisksRepo.delete(insuranceRisk);
                    }
                }
                //delete the insurance upload
                embedRetrenchInsuranceRepo.delete(retrenchInsurance);
            } else {
                return  "Approved Transaction cannot be deleted";
            }
        }
        return "Embedded Transaction Deleted successfully.";
    }

    @Override
    @Transactional(readOnly = false)
    public String bulkDelProcessedPolicies(List<Long> retrenchIds) throws BadRequestException {
        if (retrenchIds == null || retrenchIds.isEmpty()) {
            throw new BadRequestException("Select at least one transaction to process");
        }

        for (Long retrenchId : retrenchIds) {
            EmbedRetrenchInsurance retrenchInsurance = embedRetrenchInsuranceRepo.findBulkStockByPolicyId(retrenchId);

            if (retrenchInsurance == null) {
                System.out.println("No BulkPolicyCreation found for ID: " + retrenchId);
                continue;
            }

            if (!"Y".equalsIgnoreCase(retrenchInsurance.getTranStatus())) {
                System.out.println("Transaction not marked as processed for ID: " + retrenchIds);
                continue;
            }

            embedRetrenchInsuranceRisksRepo.deleteByPackage(retrenchInsurance);
            embedRetrenchInsuranceRepo.delete(retrenchInsurance);
            //delete the created policy
            uploadValidatorsUtils.deleteprocessedPol(retrenchId);
        }
        return "Transactions Deleted Successfully";
    }

}
