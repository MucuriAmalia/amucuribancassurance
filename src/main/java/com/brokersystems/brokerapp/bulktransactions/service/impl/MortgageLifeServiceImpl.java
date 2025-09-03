package com.brokersystems.brokerapp.bulktransactions.service.impl;

import com.brokersystems.brokerapp.bulktransactions.ErrorsCache.ErrorWorkbookCache;
import com.brokersystems.brokerapp.bulktransactions.dtos.MortgageLifeDTO;
import com.brokersystems.brokerapp.bulktransactions.models.*;
import com.brokersystems.brokerapp.bulktransactions.repositories.MortgageLifeRepository;
import com.brokersystems.brokerapp.bulktransactions.service.MortgageLifeService;
import com.brokersystems.brokerapp.bulktransactions.utils.ExcelReaderUtil;
import com.brokersystems.brokerapp.enums.AccountTypeEnum;
import com.brokersystems.brokerapp.enums.RevenueItems;
import com.brokersystems.brokerapp.life.model.PolicyInstallments;
import com.brokersystems.brokerapp.life.repository.PolicyInstallmentsRepo;
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
import com.brokersystems.brokerapp.trans.model.SystemTrans;
import com.brokersystems.brokerapp.trans.repository.SystemTransRepo;
import com.brokersystems.brokerapp.trans.service.PolicyAuthorization;
import com.brokersystems.brokerapp.uw.dtos.PolicyCreateDTO;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.uw.repository.PolTaxesRepo;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import com.brokersystems.brokerapp.uw.repository.RiskTransRepo;
import com.brokersystems.brokerapp.uw.service.PremComputeService;
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

@Service
public class MortgageLifeServiceImpl implements MortgageLifeService {
    @Autowired
    private ValidatorUtils validator;
    @Autowired
    private SequenceRepository sequenceRepository;
    @Autowired
    private UserUtils userUtils;
    @Autowired
    private MortgageLifeRepository mortgageLifeRepo;
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
    private PremComputeService premComputeServiceImpl;
    @Autowired
    private PolTaxesRepo polTaxesRepo;
    @Autowired
    private TaxRatesRepo taxRatesRepo;
    @Autowired
    private PremComputeService premiumService;
    @Autowired
    private PolicyInstallmentsRepo policyInstallmentsRepo;
    @Autowired
    private ErrorWorkbookCache errorWorkbookCache;
    @Autowired
    private UserRepository userRepo;

    private  static final int BATCH_SIZE = 500;

    @Override
    @Async
    public void uploadMortgageLife(File file, Long userId) throws BadRequestException {
        User user = userRepo.findOne(userId);

        Map<String, Object> response = new HashMap<>();
        List<String> invalidRecords = new ArrayList<>();
        List<MortgageLife> mortgageList = new ArrayList<>();

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
            String transType = "MLU";
            Predicate pedSystem = QSystemSequence.systemSequence.transType.eq("MLU");
            if (sequenceRepository.count(pedSystem) == 0)
                throw new BadRequestException("Sequence for Mortgage Life Upload has not been defined");

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
                    MortgageLife mortgageLife = new MortgageLife();

                    String clntIdNo = ExcelReaderUtil.getCellValue(row, 0); //getCellStringValue(row.getCell(3)));
                    if (!validator.validateIdNo(clntIdNo)) {
                        throw new BadRequestException("Invalid client ID Number " + clntIdNo);
                    }
                    mortgageLife.setClientID(clntIdNo);

                    String cif = ExcelReaderUtil.getCellValue(row, 1); //getCellStringValue(row.getCell(4));
                    mortgageLife.setClientCIF(cif); //getCellStringValue(row.getCell(10)));

                    final ClientDef client = clientRepository.findByCifandIdNoIgnoreCase(cif, clntIdNo);
                    if (client != null) {
                        mortgageLife.setClientFName(client.getFname());
                        mortgageLife.setClientOtherNames(client.getOtherNames());
                        mortgageLife.setClientDOB(client.getDob());
                        mortgageLife.setClientPin(client.getPinNo());
                        mortgageLife.setClientEmail(client.getEmailAddress());
                        mortgageLife.setClientPhone(client.getPhoneNo());
                        mortgageLife.setClientType(client.getTenantType());

                    } else {
                        throw new BadRequestException("Client with ID No "+ clntIdNo + " not found.");
                    }

                    String insurerCode = ExcelReaderUtil.getCellValue(row, 2); //getCellStringValue(row.getCell(9));
                    System.out.println("insured code"+insurerCode);
                    AccountDef underWriter = accountRepo.findOne(QAccountDef.accountDef.shtDesc.eq(insurerCode.trim()));
                    if (underWriter != null) {
                        mortgageLife.setInsurerCode(underWriter);
                    } else {
                        throw new BadRequestException("Insurer with code " + insurerCode + " is not setup in the system, please contact system admin");
                    }
                    mortgageLife.setProductGroup(ExcelReaderUtil.getCellValue(row, 3)); //getCellStringValue(row.getCell(10)));
                    String productName = (ExcelReaderUtil.getCellValue(row, 4)); //getCellStringValue(row.getCell(11)));

                    String searchCode = "MLRPU";
                    String normalizedProductName = productName.trim().toLowerCase().replace(" ", "");
                    ProductsDef product = productsRepo.findByNormalizedName(normalizedProductName, searchCode);
                    if (product == null) {
                        throw new BadRequestException("Product " + productName + " not set in the system, please contact system admin.");
                    }
                    //BindersDef binder = bindersRepo.findBinderByAccId(mortgageLife.getInsurerCode().getAcctId(), product.getProCode());
//                    List<BindersDef> polBinders =  bindersRepo.findBinderByAccIdAndBinName(mortgageLife.getInsurerCode().getAcctId(), product.getProCode());
//                    if (polBinders.isEmpty()) {
//                        throw new BadRequestException("Binder for product " + productName + " and insurer with code " + mortgageLife.getInsurerCode().getShtDesc()
//                                + " not set in the system, please contact system admin.");
//                    }
//                    BindersDef binder = polBinders.get(0);
//                    if(!binder.getProduct().isActive()){
//                        throw new BadRequestException("The policy contract is not active. Please authorise the contract to continue...");
//                    }
                    String contract = (ExcelReaderUtil.getCellValue(row, 16));
                    contract = contract.replaceAll("\\s+", " ").trim();
                    mortgageLife.setContractName(contract);
                    System.out.println("looking for "+mortgageLife.getInsurerCode().getAcctId() +"uw "+underWriter.getAcctId() +" product" + product.getProCode() + contract);
                    BindersDef binder = bindersRepo.findBinderByAccIdAndContractBinName(product.getProCode(), contract);
                    if (binder == null) {
                        throw new BadRequestException("Binder for product " + productName + " and insurer with code " + mortgageLife.getInsurerCode().getShtDesc()
                                + " not set in the system, please contact system admin.");
                    }
                    if(!binder.getProduct().isActive()){
                        throw new BadRequestException("The policy contract is not active. Please authorise the contract to continue...");
                    }
                    String coverType = (ExcelReaderUtil.getCellValue(row, 5)); //getCellStringValue(row.getCell(12)));
                    String normalizedCoverType = coverType.trim().toLowerCase().replace(" ", "");
                    SubClassDef subClassDef = subClassRepo.findBySubClassName(normalizedProductName);
                    if (subClassDef != null){
                        mortgageLife.setProductName(subClassDef.getSubDesc());
                    } else {
                        throw new BadRequestException("Product " + productName + " not set in the system, please contact system admin.");
                    }
                    CoverTypesDef coverTypesDef = coverTypesRepo.findCoverTypesByBindId(binder.getBinId(), normalizedCoverType);
                    if (coverTypesDef != null) {
                        mortgageLife.setCoverType(coverTypesDef.getCovName());
                    }else {
                        throw new BadRequestException("Cover type " + coverType + " not set for insurer with code " + mortgageLife.getInsurerCode().getShtDesc() +" please contact system admin.");
                    }
                    mortgageLife.setFrequency(ExcelReaderUtil.validateFrequency(ExcelReaderUtil.getCellValue(row, 6), row.getRowNum())); //getCellStringValue(row.getCell(13)), row.getRowNum()));
                    String currency = ExcelReaderUtil.getCellValue(row, 7); // getCellStringValue(row.getCell(14));
                    if (currency != null && !currency.trim().isEmpty()) {
                        currency = currency.trim();
                        Currencies currencies = currencyRepository.findOne(QCurrencies.currencies.curIsoCode.eq(currency));
                        if (currencies == null) {
                            throw new BadRequestException("Currency code " + currency + " is not setup in the system, please contact system admin");
                        }
                        mortgageLife.setCurrencyCode(currencies);
                    } else {
                        throw new BadRequestException("Currency is required in row " + row.getRowNum() + " in Currency ISO code column");
                    }

                    String branchCode = ExcelReaderUtil.getCellValue(row, 8); //getCellStringValue(row.getCell(15));
                    OrgBranch orgBranch = orgBranchRepository.findOne(QOrgBranch.orgBranch.obShtDesc.eq(branchCode.trim()));// orgBranchRepository.findBranch(Long.parseLong(branchCode)); //findOne(QOrgBranch.orgBranch.obShtDesc.eq(branchCode.trim()));
                    if (orgBranch != null) {
                        mortgageLife.setBranch(orgBranch);
                    } else {
                        throw new BadRequestException("Branch code " + branchCode + " in sheet one is not setup in the system, please contact system admin.");
                    }

                    BigDecimal sumInsured = ExcelReaderUtil.parseBigDecimalSafe(ExcelReaderUtil.getCellValue(row, 9)); //getCellBigDecimalValue(row.getCell(16)));
                    sumInsured = sumInsured.abs();
                    mortgageLife.setSumInsured(sumInsured);
                    BigDecimal premium = ExcelReaderUtil.parseBigDecimalSafe(ExcelReaderUtil.getCellValue(row, 10)); //getCellBigDecimalValue(row.getCell(17)));
                    premium = premium.abs();
                    mortgageLife.setMortgagePremium(premium);
                    mortgageLife.setTransDate(ExcelReaderUtil.parseFlexibleDate((ExcelReaderUtil.getCellValue(row, 11)))); //getCellDateValue(row.getCell(18)));
                    mortgageLife.setCoverFrom(ExcelReaderUtil.parseFlexibleDate((ExcelReaderUtil.getCellValue(row, 12)))); //getCellDateValue(row.getCell(19)));
                    mortgageLife.setCoverTo(ExcelReaderUtil.parseFlexibleDate((ExcelReaderUtil.getCellValue(row, 13)))); //getCellDateValue(row.getCell(20)));
                    mortgageLife.setPolTerm(1);
                    mortgageLife.setLoanId(ExcelReaderUtil.getCellValue(row, 14)); //getCellDateValue(row.getCell(21)));
                    mortgageLife.setCasa(ExcelReaderUtil.parseBigDecimalSafe((ExcelReaderUtil.getCellValue(row, 15)))); //getCellDateValue(row.getCell(22)));
                    mortgageLife.setUploadedDate(new Date());
                    mortgageLife.setUploadedBy(user);
                    mortgageLife.setTransStatus("N");
                    mortgageLife.setTransType(transType);
                    mortgageLife.setTransCode(bulkCode);

                    mortgageList.add(mortgageLife);
                    if(mortgageList.size() >= BATCH_SIZE){
                        mortgageLifeRepo.save(mortgageList);
                        mortgageList.clear();
                    }
                } catch (Exception e) {
                    //throw new BadRequestException("Sheet One " + e.getMessage());
//                    Cell errorCell = row.getCell(errorColumnIndex);
//                    if (errorCell == null) errorCell = row.createCell(errorColumnIndex);
//                    errorCell.setCellValue("Error: " + e.getMessage());
                    //invalidRecords.add("Sheet One, Row " + (rowNum + 1) + ": " + e.getMessage());
                }
            }

            int successfulPol = mortgageList.size();
            int invalidPol = invalidRecords.size();
//            if (!(invalidPol > 0) && successfulPol > 0) {
//                mortgageLifeRepo.save(mortgageList);
//            }

            if (!mortgageList.isEmpty()) {
                mortgageLifeRepo.save(mortgageList);
                mortgageList.clear();
            }

            // Save workbook with errors
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            workbook.write(bos);
//            errorWorkbookCache.saveWorkbook(batchId, bos);

            response.put("successfulPolicies", successfulPol);
            response.put("failedRecords", invalidPol);
            response.put("invalidRecords", invalidRecords);
            response.put("errorFileId", batchId);

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

    @Override
    public DataTablesResult<MortgageLifeDTO> findUnprocessedMortgageLife(DataTablesRequest request) {
        Long currentUserId = userUtils.getCurrentUser().getId();

        List<Object[]> unprocessedMortgageLife = mortgageLifeRepo.findUnprocessedMortgageLife((request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%", request.getPageNumber(), request.getPageSize(), currentUserId);
        final List<MortgageLifeDTO> mortgageLifeDTOS = new ArrayList<>();
        long rowCount = 0L;
        if (!unprocessedMortgageLife.isEmpty())
            rowCount = ((BigInteger) unprocessedMortgageLife.get(0)[9]).intValue();
        for (Object[] embedPackage : unprocessedMortgageLife) {
            MortgageLifeDTO mortgageLifeDTO = new MortgageLifeDTO();
            mortgageLifeDTO.setMortgageId(((BigInteger) embedPackage[0]).longValue());
            mortgageLifeDTO.setCoverType((String) embedPackage[1]);
            mortgageLifeDTO.setProductName((String) embedPackage[2]);
            mortgageLifeDTO.setInsurerName((String) embedPackage[3]);
            mortgageLifeDTO.setClientFName((String) embedPackage[4]);
            mortgageLifeDTO.setClientOtherNames((String) embedPackage[5]);
            mortgageLifeDTO.setCoverFrom((Date) embedPackage[6]);
            mortgageLifeDTO.setCoverTo((Date) embedPackage[7]);
            mortgageLifeDTO.setUploadedDate((Date) embedPackage[8]);

            mortgageLifeDTOS.add(mortgageLifeDTO);
        }
        Page<MortgageLifeDTO> page = new PageImpl<>(mortgageLifeDTOS, request, rowCount);

        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<MortgageLifeDTO> viewUnprocessedMortgageLife(DataTablesRequest request) {
        Long currentUserId = userUtils.getCurrentUser().getId();

        List<Object[]> viewUnprocessedMortgage = mortgageLifeRepo.viewUnprocessedMortgageLife((request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%", request.getPageNumber(), request.getPageSize(), currentUserId);
        final List<MortgageLifeDTO> insuranceDTOS = new ArrayList<>();
        long rowCount = 0L;
        if (!viewUnprocessedMortgage.isEmpty()) rowCount = ((BigInteger) viewUnprocessedMortgage.get(0)[10]).intValue();
        for (Object[] viewEmbedPackage : viewUnprocessedMortgage) {
            MortgageLifeDTO mortgageLifeDTO = new MortgageLifeDTO();
            mortgageLifeDTO.setPolId(((BigInteger) viewEmbedPackage[0]).longValue());
            mortgageLifeDTO.setCoverType((String) viewEmbedPackage[1]);
            mortgageLifeDTO.setPolNo((String) viewEmbedPackage[2]);
            mortgageLifeDTO.setMortgagePremium((BigDecimal) viewEmbedPackage[3]);
            mortgageLifeDTO.setClientFName((String) viewEmbedPackage[4]);
            mortgageLifeDTO.setClientOtherNames((String) viewEmbedPackage[5]);
            mortgageLifeDTO.setCoverFrom((Date) viewEmbedPackage[6]);
            mortgageLifeDTO.setCoverTo((Date) viewEmbedPackage[7]);
            mortgageLifeDTO.setProcessedDate((Date) viewEmbedPackage[8]);
            mortgageLifeDTO.setTransStatus((String) viewEmbedPackage[9]);

            insuranceDTOS.add(mortgageLifeDTO);
        }
        Page<MortgageLifeDTO> page = new PageImpl<>(insuranceDTOS, request, rowCount);

        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional
    public PolicyTrans processSingleMortgageLifePol(Long mortgageId, Long userId, boolean isApproved) throws BadRequestException {
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
        MortgageLife mortgageLife = mortgageLifeRepo.findOne(QMortgageLife.mortgageLife.mortgageId.eq(mortgageId));
        if (mortgageLife.getTransStatus().equalsIgnoreCase("Y")) {
            throw new BadRequestException("This transaction is already processed");
        }
       // final PolicyTrans policyTrans = new PolicyTrans();
        Date wef = mortgageLife.getCoverFrom();
        Date wet = mortgageLife.getCoverTo();
        System.out.println("wet pol" + wet);
       // policyTrans.setPolCreateddt(new Date());
    //    policyTrans.setAuthStatus("LD");
   //     policyTrans.setCurrentStatus("LD");

        //policyTrans.setCoverFrom(wef);
        //policyTrans.setCoverTo(wet);

        policyDto.setWefDate(wef);
        //policyTrans.setWefDate(wef);

        policyDto.setWetDate(wet);
        //policyTrans.setWetDate(wet);

        //policyTrans.setBasicPrem(mortgageLife.getMortgagePremium());
     //   policyTrans.setSumInsured(mortgageLife.getSumInsured());

        //policyTrans.setPolTerm(1);
        policyDto.setPolTerm(1); // based on wet and wef

        //policyTrans.setTotalInstalments(1);
        policyDto.setTotalInstalments(null);

        String frequency = mortgageLife.getFrequency();
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
            throw new BadRequestException("Payment frequency is not defined for client ID " + mortgageLife.getClientID() + " in frequency column in uploaded excel");
        }



        final String pinSearch = mortgageLife.getClientPin().trim();
        final ClientDef client = clientRepository.findByPinNoIgnoreCase(pinSearch);
        if (client != null) {
            //policyTrans.setClient(client);
            policyDto.setClientId(client.getTenId());
        } else {
            throw new BadRequestException("Client with KRA pin "+ pinSearch +"Not found.");
        }
        //policyTrans.setAgent(mortgageLife.getInsurerCode());
        policyDto.setAgentId(mortgageLife.getInsurerCode().getAcctId());

        String searchCode = "MLRPU";
        String normalizedPrdName = mortgageLife.getProductName().trim().toLowerCase().replace(" ", "");
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
            throw new BadRequestException(mortgageLife.getProductName() + " product is not found. Please set up the product.");
        }
        //policyTrans.setTransCurrency(mortgageLife.getCurrencyCode());
        policyDto.setCurrencyId(mortgageLife.getCurrencyCode().getCurCode());

        //policyTrans.setBranch(mortgageLife.getBranch());
        policyDto.setBranchId(mortgageLife.getBranch().getObId());

       // policyTrans.setUwYear(dateUtils.getUwYear(wef));
        //policyTrans.setCreatedUser(userUtils.getCurrentUser());
        //policyTrans.setPreviousTrans(policyTrans);
        BindersDef polBinder = bindersRepo.findBinderByAccId(mortgageLife.getInsurerCode().getAcctId(), product.getProCode());
        //policyTrans.setBinder(polBinder);
        policyDto.setBindCode(polBinder.getBinId());

        PaymentModes paymentModes = paymentModeRepo.findOne(QPaymentModes.paymentModes.pmDesc.eq("CASH"));
        System.out.println("paymentModes id:: " + paymentModes.getPmId());
        //policyTrans.setPaymentMode(paymentModes);
        policyDto.setPaymentId(paymentModes.getPmId());

        SystemTrans systemTrans = null;
        if (mortgageLife.getTransStatus().equalsIgnoreCase("N")) {

            String policyNumberFormat = paramService.getParameterString("POLICY_NO_FORMAT");
            String endorsementFormat = paramService.getParameterString("ENDORSE_NO_FORMAT");
            String proposalFormat = paramService.getParameterString("PROPOSAL_NO_FORMAT");
            Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("P");
            if (sequenceRepository.count(seqPredicate) == 0)
                throw new BadRequestException("Sequence for New Business Transactions has not been defined");
            SystemSequence sequence = sequenceRepository.findOne(seqPredicate);
            Long seqNumber = sequence.getNextNumber();
            //final String policyNumber = templateMerger.generateFormat(policyNumberFormat, mortgageLife.getBranch().getObId(), product.getProCode(), policyTrans.getWefDate(), sequence.getSeqPrefix() + String.format("%05d", seqNumber), null);
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
            final String endorseNumber = templateMerger.generateFormat(endorsementFormat, mortgageLife.getBranch().getObId(), product.getProCode(), mortgageLife.getCoverFrom(), revNumber, null);
          //  policyTrans.setPolRevNo(endorseNumber + "/1");
          //  policyTrans.setRevisionFormat(endorseNumber);
            endorseSequence.setLastNumber(endosseqNumber);
            endorseSequence.setNextNumber(endosseqNumber + 1);
            sequenceRepository.save(endorseSequence);

            Predicate propPredicate = QSystemSequence.systemSequence.transType.eq("PR");
            if (sequenceRepository.count(propPredicate) == 0)
                throw new BadRequestException("Sequence for Proposal Transactions has not been defined");
            SystemSequence lifeSequence = sequenceRepository.findOne(propPredicate);
            Long lifeSeqNumber = lifeSequence.getNextNumber();
            final String proposalNo = templateMerger.generateFormat(proposalFormat, mortgageLife.getBranch().getObId(), product.getProCode(), wef, lifeSequence.getSeqPrefix() + String.format("%05d", lifeSeqNumber), null);
            if (proposalNo == null) {
                throw new BadRequestException("Proposal no has not been generated");
            }
            System.out.println("Proposal No " + proposalNo);
          //  policyTrans.setProposalNo(proposalNo);
        }



        RiskTrans riskTrans = new RiskTrans();
        final String pinToSearch = mortgageLife.getClientPin().trim();
        final ClientDef insured = clientRepository.findByPinNoIgnoreCase(pinToSearch);
        if (insured != null) {
            riskTrans.setInsured(insured);//
        } else {
            throw new BadRequestException("Insured is not registered");
        }
        final BigDecimal basicPrem = mortgageLife.getMortgagePremium().subtract(BigDecimal.valueOf(40)).multiply(BigDecimal.valueOf(100/100.45));

        Integer age1 = dateUtils.getAge(mortgageLife.getClientDOB());
        BigDecimal age = BigDecimal.valueOf(age1);

        BindersDef newBinder = bindersRepo.findBinderByAccId(mortgageLife.getInsurerCode().getAcctId(), product.getProCode());
        String normalizedSubClass = mortgageLife.getProductName().trim().toLowerCase().replace(" ", "");
        String normalizedCoverType = mortgageLife.getCoverType().trim().toLowerCase().replace(" ", "");
        final SubClassDef subClassDef = subClassRepo.findBySubClassName(normalizedSubClass);
        final CoverTypesDef coverTypesDef = coverTypesRepo.findCoverTypesByBindId(newBinder.getBinId(), normalizedCoverType);
        System.out.println("Binder id ::" + newBinder.getBinId());
        System.out.println("CoverTypes id:: " + coverTypesDef.getCovId());
        BinderDetails newDetails = binderDetRepo.findOne(QBinderDetails.binderDetails.binder.binId.eq(newBinder.getBinId()).and(QBinderDetails.binderDetails.subCoverTypes.coverTypes.covId.eq(coverTypesDef.getCovId())));
        String businessType = product.getProGroup().getPrgType();
        if (!businessType.equalsIgnoreCase("L")) {
        //    policyTrans.setBusinessType("N");
        } else {
         //   policyTrans.setBusinessType("L");
        }

        RiskTransBean riskBean = new RiskTransBean();
        riskBean.setRiskId(null);
        //riskBean.setTransType(null);
        //riskBean.setRiskIdentifier(null);
        riskBean.setRiskShtDesc(mortgageLife.getLoanId());
        riskBean.setBindCode(newBinder.getBinId());
        riskBean.setWefDate(wef);
        riskBean.setWetDate(wet);
        riskBean.setInsuredCode(client.getTenId());
        riskBean.setSclCode(subClassDef.getSubId());
        riskBean.setComputeType("S");
        riskBean.setWorkingAge(age1);
        riskBean.setCoverCode(coverTypesDef.getCovId());
        riskBean.setBinderDet(newDetails.getDetId());
        riskBean.setPremium(mortgageLife.getMortgagePremium());
        riskBean.setSumInsured(mortgageLife.getSumInsured());
        policyDto.setRiskBean(riskBean);
        policyDto.setNegotiatedPremium(mortgageLife.getMortgagePremium()); //to avoid calculator

        System.out.println("age is "+age);
        List<RiskSectionBean> sections = uploadValidatorsUtils.saveSectionTransaction("SUM ASSURED",newDetails.getDetId(), new BigDecimal("26")); //buildSectionBeans("29226", new BigDecimal("26"), savedRiskTrans);
        policyDto.setSections(sections);

        ///change the create policy
        PolicyTrans created = uploadValidatorsUtils.saveLifePolicyUpload(policyDto,userId);

        uploadValidatorsUtils.updateBasicNet(mortgageLife.getMortgagePremium(),mortgageLife.getSumInsured(), created);

        mortgageLife.setPolicyTrans(created);
        mortgageLife.setPolicyAuthorized("N");
        mortgageLife.setTransStatus("Y");
        mortgageLife.setProcessedDate(new Date());
        mortgageLife.setProcessedBy(user);
        mortgageLifeRepo.save(mortgageLife);

        return  created;
    }

    @Override
    public List<Long> processBulkMortgageLifePol(BatchCreditBatch batchCreditBatch, boolean isApproved) throws BadRequestException {
        if (batchCreditBatch.getBatchRecords().size() <= 0) {
            throw new BadRequestException("Select At least One Transaction To Process");
        }

        Job job = JobBuilder.aNewJob()
                .reader(new IterableRecordReader(batchCreditBatch.getBatchRecords()))
                .named("mortgage_life_processing" + new SimpleDateFormat("ddMMyyyhhmmss").format(new Date()))
                .processor((RecordProcessor<Record, Record>) renForm -> {
                    Long policyId = ((BatchRecord) renForm.getPayload()).getPayload().getBatchId();
                    Long userId = ((BatchRecord) renForm.getPayload()).getPayload().getUserId();
                    processSingleMortgageLifePol(policyId, userId,true);
                    return renForm;
                })
                .pipelineListener(new RenewalJobListener())
                .build();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Future<JobReport> report = executorService.submit(job);

        return  new ArrayList<>();

//        if (mortgageIds.size() <= 0) {
//            throw new BadRequestException("Select At least One Transaction To Process");
//        }
//        PolicyTrans savedPol = null;
//        List<Long> processedPolicyIds = new ArrayList<>();
//        for (Long mortgageId : mortgageIds) {
//            savedPol = processSingleMortgageLifePol(mortgageId, isApproved);
//            //processedPolicyIds.add(savedPol.getPolicyId());
//        }
//        return processedPolicyIds;
    }

    @Override
    public String approveSingleMortgageLifePol(Long policyId) throws BadRequestException {
        authService.authorizeBulkUploadPolicies(policyId);
        MortgageLife mortgageLife = mortgageLifeRepo.findBulkStockByPolicyId(policyId);
        mortgageLife.setPolicyAuthorized("Y");
        mortgageLifeRepo.save(mortgageLife);
        return "Transaction Authorized Successfully";
    }

    @Override
    public String approveBulkMortgageLifePol(List<Long> policyIds) throws BadRequestException {
        if (policyIds.size() <= 0){
            throw new BadRequestException("Select At least One Transaction To Process");
        }
        for(Long policyId : policyIds){
            authService.authorizeBulkUploadPolicies(policyId);
            MortgageLife mortgageLife = mortgageLifeRepo.findBulkStockByPolicyId(policyId);
            mortgageLife.setPolicyAuthorized("Y");
            mortgageLifeRepo.save(mortgageLife);

        }

        return "Transactions Authorized Successfully";
    }

    @Override
    @Transactional
    public String deleteBulkMortgageLifePol(List<Long> mortgageIds) throws BadRequestException {
        if (mortgageIds.size() <= 0){
            throw new BadRequestException("Select At least One Transaction To Process");
        }
        for(Long mortgageId : mortgageIds){
            MortgageLife mortgageLife = mortgageLifeRepo.findOne(QMortgageLife.mortgageLife.mortgageId.eq(mortgageId));
            if (mortgageLife.getTransStatus().equalsIgnoreCase("N")) {
                mortgageLifeRepo.delete(mortgageLife);
            }
        }
        return "Mortgage deleted successfully.";
    }

    @Override
    @Transactional
    public String bulkDelProcessedPolicies(List<Long> mortgageIds) throws BadRequestException {
        if (mortgageIds.size() <= 0){
            throw new BadRequestException("Select At least One Transaction To Process");
        }
        for(Long mortgageId : mortgageIds){
            MortgageLife mortgageLife = mortgageLifeRepo.findBulkStockByPolicyId(mortgageId);
            if (mortgageLife != null && mortgageLife.getTransStatus().equalsIgnoreCase("Y")) {
                mortgageLifeRepo.delete(mortgageLife);
            }
            uploadValidatorsUtils.deleteprocessedPol(mortgageId);
        }
        return "Mortgage deleted successfully.";
    }
}
