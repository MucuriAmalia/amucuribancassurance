package com.brokersystems.brokerapp.bulktransactions.service.impl;

import com.brokersystems.brokerapp.bulktransactions.dtos.TimizaDTO;
import com.brokersystems.brokerapp.bulktransactions.models.*;
import com.brokersystems.brokerapp.bulktransactions.repositories.TimizaRepository;
import com.brokersystems.brokerapp.bulktransactions.service.TimizaService;
import com.brokersystems.brokerapp.bulktransactions.utils.ExcelReaderUtil;
import com.brokersystems.brokerapp.enums.AccountTypeEnum;
import com.brokersystems.brokerapp.enums.RevenueItems;
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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class TimizaServiceImpl implements TimizaService {

    @Autowired
    private PolicyAuthorization authService;
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private OrgBranchRepository orgBranchRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ClientTypeRepo clientTypeRepo;
    @Autowired
    private SequenceRepository sequenceRepo;
    @Autowired
    private PolicyTransRepo policyTransRepo;
    @Autowired
    private CommRatesRepo commRatesRepo;
    @Autowired
    private DateUtilities dateUtils;
    @Autowired
    private RiskTransRepo riskTransRepo;
    @Autowired
    private ValidatorUtils validator;
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
    private TimizaRepository timizaRepo;
    @Autowired
    private UserUtils userUtils;
    @Autowired
    private UserBranchesRepository userBranchesRepository;
    @Autowired
    private SystemTransRepo systemTransRepo;
    @Autowired
    private SectionTransRepo sectionRepo;
    @Autowired
    private PremRatesRepo premRatesRepo;
    @Autowired
    private SectionRepo sectionsRepo;
    @Autowired
    private UploadValidatorsUtils uploadValidatorsUtils;

    @Autowired
    private  UserRepository userRepo;
    private  static final int BATCH_SIZE = 500;

    @Override
    @Async
    public void uploadTimiza(File file, Long userId) throws BadRequestException {
        User user = userRepo.findOne(userId);


        Map<String, Object> response = new HashMap<>();
        List<String> invalidRecords = new ArrayList<>();
        List<Timiza> timizas = new ArrayList<>();

        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
            Workbook workbook = WorkbookFactory.create(inputStream);


            Sheet sheetOne = workbook.getSheetAt(0);

            int startRowIndex = findDataStartRow(sheetOne);
            System.out.println("startRowIndex" + startRowIndex);
            if (startRowIndex == -1) {
                throw new BadRequestException("Sheet One contains no data.");
            }

            String bulkCode = null;
            Predicate pedSystem = QSystemSequence.systemSequence.transType.eq("TPU");
            if (sequenceRepo.count(pedSystem) == 0)
                throw new BadRequestException("Sequence for Timiza Policy Upload has not been defined");
            String transType = "TPU";

            for (int rowNum = startRowIndex; rowNum <= sheetOne.getLastRowNum(); rowNum++) {
                SystemSequence sequenceSystem = sequenceRepo.findOne(pedSystem);
                Long sequenceNumber = sequenceSystem.getNextNumber();
                bulkCode = transType + String.format("%05d", sequenceNumber);
                sequenceSystem.setLastNumber(sequenceNumber);
                sequenceSystem.setNextNumber(sequenceNumber + 1);
                sequenceRepo.save(sequenceSystem);

                Row row = sheetOne.getRow(rowNum);
                if (isBlankRow(row)) continue;

                try {
                    Timiza timiza = new Timiza();

                    String clntIdNo = ExcelReaderUtil.getCellValue(row, 0); // getCellStringValue(row.getCell(3));
                    if (!validator.validateIdNo(clntIdNo)) {
                        throw new BadRequestException("Invalid client ID Number " + clntIdNo);
                    }
                    timiza.setClientId(clntIdNo);
                    String cif = ExcelReaderUtil.getCellValue(row, 1); //getCellStringValue(row.getCell(4));
                    timiza.setClientCIF(cif); //getCellStringValue(row.getCell(10)));

                    final ClientDef client = clientRepository.findByCifandIdNoIgnoreCase(cif, clntIdNo);
                    if (client != null) {
                        timiza.setClientFname(client.getFname());
                        timiza.setClientOtherNames(client.getOtherNames());
                        timiza.setClientDOB(client.getDob());
                        timiza.setClientPin(client.getPinNo());
                        timiza.setClientEmail(client.getEmailAddress());
                        timiza.setClientPhone(client.getPhoneNo());
                        timiza.setClientType(client.getTenantType());

                    } else {
                        throw new BadRequestException("Client with ID No "+ clntIdNo + " not found.");
                    }

                    timiza.setTransDate(ExcelReaderUtil.parseFlexibleDate((ExcelReaderUtil.getCellValue(row, 2)))); //getCellDateValue(row.getCell(9)));
                    timiza.setTransId(ExcelReaderUtil.getCellValue(row, 3)); //getCellStringValue(row.getCell(10)));
                    timiza.setPaymentMode(ExcelReaderUtil.getCellValue(row, 4)); //getCellStringValue(row.getCell(11)));
                    String branchCode = ExcelReaderUtil.getCellValue(row, 5); //getCellStringValue(row.getCell(12));
                    OrgBranch orgBranch = orgBranchRepository.findOne(QOrgBranch.orgBranch.obShtDesc.eq(branchCode.trim()));
                    if (orgBranch != null) {
                        timiza.setBranch(orgBranch);
                    } else {
                        throw new BadRequestException("Branch code " + branchCode + " in sheet two is not setup in the system, please contact system admin.");
                    }
                    String insurerCode = ExcelReaderUtil.getCellValue(row, 6); //getCellStringValue(row.getCell(13));
                    AccountDef underWriter = accountRepo.findOne(QAccountDef.accountDef.shtDesc.eq(insurerCode.trim()));
                    if (underWriter != null) {
                        timiza.setInsurerCode(underWriter);
                    } else {
                        throw new BadRequestException("Insurer with code " + insurerCode + " is not setup in the system, please contact system admin");
                    }

                    timiza.setProductGroup(ExcelReaderUtil.getCellValue(row, 7)); //getCellStringValue(row.getCell(14)));
                    String productName = (ExcelReaderUtil.getCellValue(row, 8)); //getCellStringValue(row.getCell(15)));
                    String normalizedProductName = productName.trim().toLowerCase().replace(" ", "");

                    String searchCode = "TPU";
                    System.out.println("normalized product name "+normalizedProductName);
                    ProductsDef product = productsRepo.findByNormalizedName(normalizedProductName, searchCode);
                    if (product == null) {
                        throw new BadRequestException("Product " + productName + " not set in the system, please contact system admin.");
                    }
                    BindersDef binder = bindersRepo.findBinderByAccId(timiza.getInsurerCode().getAcctId(), product.getProCode());
                    if (binder == null) {
                        throw new BadRequestException("Binder for product " + productName + " and insurer with code " + timiza.getInsurerCode().getShtDesc()
                                + " not set in the system, please contact system admin.");
                    }
                    String coverType = (ExcelReaderUtil.getCellValue(row, 9)); //getCellStringValue(row.getCell(16)));
                    String normalizedCoverType = coverType.trim().toLowerCase().replace(" ", "");
                    SubClassDef subClassDef = subClassRepo.findBySubClassName(normalizedProductName);
                    if (subClassDef != null){
                        timiza.setProductName(subClassDef.getSubDesc());
                    } else {
                        throw new BadRequestException("Product " + productName + " not set in the system, please contact system admin.");
                    }
                    CoverTypesDef coverTypesDef = coverTypesRepo.findCoverTypesByBindId(binder.getBinId(), normalizedCoverType);
                    if (coverTypesDef != null) {
                        timiza.setCoverType(coverTypesDef.getCovName());
                    }else {
                        throw new BadRequestException("Cover type " + coverType + " not set for insurer with code " + timiza.getInsurerCode().getShtDesc() +" please contact system admin.");
                    }
                    String currency = ExcelReaderUtil.getCellValue(row, 10); //getCellStringValue(row.getCell(17));
                    BigDecimal premium = ExcelReaderUtil.parseBigDecimalSafe(ExcelReaderUtil.getCellValue(row, 12)); //getCellBigDecimalValue(row.getCell(19)));
                    premium = premium.abs();
                    if (currency != null && !currency.trim().isEmpty()) {
                        currency = currency.trim();
                        Currencies currencies = currencyRepository.findOne(QCurrencies.currencies.curIsoCode.eq(currency));
                        if (currencies == null) {
                            throw new BadRequestException("Currency code " + currency + " not setup in the system, please contact system admin");
                        }
                        //ExcelReaderUtil.validateCurrency(currencies.getCurIsoCode(),premium);
                        timiza.setCurrency(currencies.getCurIsoCode());
                    } else {
                        throw new BadRequestException("Currency is required in row " + row.getRowNum() + " in Currency ISO code column");
                    }
                    timiza.setFrequency(ExcelReaderUtil.validateFrequency(ExcelReaderUtil.getCellValue(row, 11), row.getRowNum())); //getCellStringValue(row.getCell(18)), row.getRowNum()));
                    timiza.setPremium(premium);

                    Date wef = ExcelReaderUtil.parseFlexibleDate((ExcelReaderUtil.getCellValue(row, 13))); //getCellDateValue(row.getCell(20));
                    if (wef != null) {
                        timiza.setStartDate(wef);
                    } else {
                        throw new BadRequestException("Cover Start date is required");
                    }

                    Date wet = ExcelReaderUtil.parseFlexibleDate((ExcelReaderUtil.getCellValue(row, 14))); //getCellDateValue(row.getCell(21));
                    if (wet != null) {
                        timiza.setEndDate(wet);
                    } else {
                        throw new BadRequestException("Cover End date is required");
                    }
                    String term = ExcelReaderUtil.getCellValue(row, 15); //getCellStringValue(row.getCell(22));
                    timiza.setPolTerm(Integer.parseInt(term));
                    timiza.setTransProcessed("N");
                    timiza.setTimizaPolType(transType);
                    timiza.setUploadedBy(user);
                    timiza.setUploadedDate(new Date());
                    timiza.setTimizaRefCode(bulkCode);

                    timizas.add(timiza);
                    if(timizas.size() >= BATCH_SIZE){
                        timizaRepo.save(timizas);
                        timizas.clear();
                    }
                } catch (Exception e) {
                    //invalidRecords.add("Sheet One, Row " + (rowNum + 1) + ": " + e.getMessage());
                }
            }

            int successfulPol = timizas.size();
            int invalidPol = invalidRecords.size();
            System.out.println("successfulPol:: " + successfulPol);
            System.out.println("invalidPol:: " + invalidPol);
//            if (!(invalidPol > 0) && successfulPol > 0) {
//                timizaRepo.save(timizas);
//            }
            if(!timizas.isEmpty()){
                timizaRepo.save(timizas);
                timizas.clear();
            }

            response.put("successfulPolicies", successfulPol);
            response.put("failedRecords", invalidPol);
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
    public DataTablesResult<TimizaDTO> findUnprocessedTimiza(DataTablesRequest request) {
        Long currentUserId = userUtils.getCurrentUser().getId();

        List<Object[]> viewTimizaPolicies = timizaRepo.findUnProcessedTimizaPol((request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%", request.getPageNumber(), request.getPageSize(),currentUserId);
        final List<TimizaDTO> timiza= new ArrayList<>();
        long rowCount = 0L;
        if (!viewTimizaPolicies.isEmpty()) rowCount = ((BigInteger) viewTimizaPolicies.get(0)[8]).intValue();
        for (Object[] timizaPol : viewTimizaPolicies) {
            TimizaDTO timizaDTO = new TimizaDTO();
            timizaDTO.setTimizaId(((BigInteger) timizaPol[0]).longValue());
            timizaDTO.setCoverType((String) timizaPol[1]);
            timizaDTO.setClientFname((String) timizaPol[2]);
            timizaDTO.setClientOtherNames((String) timizaPol[3]);
            timizaDTO.setPremium((BigDecimal) timizaPol[4]);
            timizaDTO.setStartDate((Date) timizaPol[5]);
            timizaDTO.setEndDate((Date) timizaPol[6]);
            timizaDTO.setUploadedDate((Date) timizaPol[7]);
            timiza.add(timizaDTO);
        }

        Page<TimizaDTO> page = new PageImpl<>(timiza, request, rowCount);

        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<TimizaDTO> viewUnprocessedTimiza(DataTablesRequest request) {
        Long currentUserId = userUtils.getCurrentUser().getId();

        List<Object[]> UnProcessedTimizaPols = timizaRepo.viewUnprocessedTimizaBulkPol((request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%",
                request.getPageNumber(),
                request.getPageSize(),
                currentUserId);
        final List<TimizaDTO> timizaBulk = new ArrayList<>();
        long rowCount = 0L;
        if (!UnProcessedTimizaPols.isEmpty()) rowCount = ((BigInteger) UnProcessedTimizaPols.get(0)[9]).intValue();
        for (Object[] timizaBulkPol : UnProcessedTimizaPols) {
            TimizaDTO timizaDTO = new TimizaDTO();
            timizaDTO.setPolId(((BigInteger) timizaBulkPol[0]).longValue());
            timizaDTO.setCoverType((String) timizaBulkPol[1]);
            timizaDTO.setPolNo((String) timizaBulkPol[2]);
            timizaDTO.setClientFname((String) timizaBulkPol[3]);
            timizaDTO.setClientOtherNames((String) timizaBulkPol[4]);
            timizaDTO.setStartDate((Date) timizaBulkPol[5]);
            timizaDTO.setEndDate((Date) timizaBulkPol[6]);
            timizaDTO.setPolDate((Date) timizaBulkPol[7]);
            timizaDTO.setPolStatus((String) timizaBulkPol[8]);
            timizaBulk.add(timizaDTO);
        }

        Page<TimizaDTO> page = new PageImpl<>(timizaBulk, request, rowCount);

        return new DataTablesResult<>(request, page);
    }

    @Override
    public PolicyTrans processSingleTimizaPol(Long timizaId,Long userId, boolean isApproved) throws BadRequestException {
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


        //   PolicyTrans savedPol = null;
        Timiza timizaCreation = timizaRepo.findOne(QTimiza.timiza.timizaId.eq(timizaId));
        if (timizaCreation.getTransProcessed().equalsIgnoreCase("Y")) {
            throw new BadRequestException("This transaction is already processed");
        }
        // final PolicyTrans policyTrans = new PolicyTrans();
        Date wef = timizaCreation.getStartDate();
        Date wet = timizaCreation.getEndDate();
        //  policyTrans.setPolCreateddt(new Date());
        //  policyTrans.setAuthStatus("LD");
        //  policyTrans.setCurrentStatus("LD");

        //policyTrans.setCoverFrom(wef);
        policyDto.setWefDate(wef);

        //policyTrans.setCoverTo(wet);

        // policyTrans.setWefDate(wef);
        // policyTrans.setWetDate(wet);

        policyDto.setWetDate(wet);

        //policyTrans.setBasicPrem(timizaCreation.getPremium());

        //  policyTrans.setPolTerm(timizaCreation.getPolTerm());
        policyDto.setPolTerm(timizaCreation.getPolTerm());

        // policyTrans.setTotalInstalments(1);
        policyDto.setTotalInstalments(null);

        String frequency = timizaCreation.getFrequency();

        if (frequency != null && !frequency.isEmpty()) {
            if (frequency.equalsIgnoreCase("Daily")) {
                //  policyTrans.setFrequency("D");
                policyDto.setFrequency("D");
            } else if (frequency.equalsIgnoreCase("Weekly")) {
                //  policyTrans.setFrequency("W");
                policyDto.setFrequency("W");
            } else if (frequency.equalsIgnoreCase("Monthly")) {
                //  policyTrans.setFrequency("M");
                policyDto.setFrequency("M");
            } else if (frequency.equalsIgnoreCase("Quarterly")) {
                // policyTrans.setFrequency("Q");
                policyDto.setFrequency("Q");
            } else if (frequency.equalsIgnoreCase("Semi-Annually")) {
                //  policyTrans.setFrequency("S");
                policyDto.setFrequency("S");
            } else if (frequency.equalsIgnoreCase("Annually")) {
                // policyTrans.setFrequency("A");
                policyDto.setFrequency("A");
            } else if (frequency.equalsIgnoreCase("Single")) {
                // policyTrans.setFrequency("SG");
                policyDto.setFrequency("SG");
            } else {
                throw new BadRequestException("Unknown frequency '" + frequency + "'");
            }
        } else {
            throw new BadRequestException("Payment frequency is not defined for client ID " + timizaCreation.getClientId() + " in frequency column in uploaded excel");
        }
        final String pinSearch = timizaCreation.getClientPin().trim();
        final ClientDef client = clientRepository.findByPinNoIgnoreCase(pinSearch);
        if (client != null) {
            // policyTrans.setClient(client);
            policyDto.setClientId(client.getTenId());
        } else {
            throw new BadRequestException("Client with kra pin " + pinSearch + " not found.");
        }

        // policyTrans.setAgent(timizaCreation.getInsurerCode());
        policyDto.setAgentId(timizaCreation.getInsurerCode().getAcctId());

        String searchCode = "TPU";
        String normalizedPrdName = timizaCreation.getProductName().trim().toLowerCase().replace(" ", "");
        final ProductsDef product = productsRepo.findByNormalizedName(normalizedPrdName, searchCode);
        if (product != null) {
            //policyTrans.setProduct(product);
            policyDto.setProdId(product.getProCode());

            String polBusinessType = product.getProGroup().getPrgType();

            if (!polBusinessType.equalsIgnoreCase("L")) {
                //policyTrans.setBusinessType("N");
                policyDto.setBusinessType("N");
            } else {
                //policyTrans.setBusinessType("L");
                policyDto.setBusinessType("L");
            }
        } else {
            throw new BadRequestException(timizaCreation.getProductName() + " product is not found. Please set up the product.");
        }
        final String currToSearch = timizaCreation.getCurrency().trim();
        final Currencies currency = currencyRepository.findByPinNoIgnoreCase(currToSearch);
        if (currency != null) {
            // policyTrans.setTransCurrency(currency);
            policyDto.setCurrencyId(currency.getCurCode());
        } else {
            throw new BadRequestException(timizaCreation.getCurrency() + " currency is not set up. Please add the currency to the system.");
        }
        UserBranches userBranches = userBranchesRepository.findByUser(userUtils.getCurrentUser());
        Long branchId = userBranches.getBranch().getObId();
        final OrgBranch branches = orgBranchRepository.findOne(QOrgBranch.orgBranch.obId.eq(branchId));
        // policyTrans.setBranch(branches);
        policyDto.setBranchId(timizaCreation.getBranch().getObId());


        // policyTrans.setCreatedUser(userUtils.getCurrentUser());
        // policyTrans.setPreviousTrans(policyTrans);
        BindersDef polBinder = bindersRepo.findBinderByAccId(timizaCreation.getInsurerCode().getAcctId(), product.getProCode());

        //policyTrans.setBinder(polBinder);
        policyDto.setBindCode(polBinder.getBinId());

        PaymentModes paymentModes = paymentModeRepo.findOne(QPaymentModes.paymentModes.pmDesc.eq("CASH"));
        //policyTrans.setPaymentMode(paymentModes);
        policyDto.setPaymentId(paymentModes.getPmId());

        SystemTrans systemTrans = null;
        if (timizaCreation.getTransProcessed().equalsIgnoreCase("N")) {

            String policyNumberFormat = paramService.getParameterString("POLICY_NO_FORMAT");
            String endorsementFormat = paramService.getParameterString("ENDORSE_NO_FORMAT");
            Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("P");
            if (sequenceRepo.count(seqPredicate) == 0)
                throw new BadRequestException("Sequence for New Business Transactions has not been defined");
            SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
            Long seqNumber = sequence.getNextNumber();
            //  final String policyNumber = templateMerger.generateFormat(policyNumberFormat, timizaCreation.getBranch().getObId(), product.getProCode(), policyTrans.getWefDate(), sequence.getSeqPrefix() + String.format("%05d", seqNumber), null);
            //   policyTrans.setPolNo(policyNumber);

            sequence.setLastNumber(seqNumber);
            sequence.setNextNumber(seqNumber + 1);
            //  sequenceRepo.save(sequence);
            Predicate endorsePredicate = QSystemSequence.systemSequence.transType.eq("E");
            if (sequenceRepo.count(endorsePredicate) == 0)
                throw new BadRequestException("Sequence for Endorsement Transactions has not been defined");
            SystemSequence endorseSequence = sequenceRepo.findOne(endorsePredicate);
            Long endosseqNumber = endorseSequence.getNextNumber();
            final String revNumber = endorseSequence.getSeqPrefix() + String.format("%05d", endosseqNumber);
            final String endorseNumber = templateMerger.generateFormat(endorsementFormat, timizaCreation.getBranch().getObId(), product.getProCode(), timizaCreation.getStartDate(), revNumber, null);
            // policyTrans.setPolRevNo(endorseNumber + "/1");
            //  policyTrans.setRevisionFormat(endorseNumber);
            endorseSequence.setLastNumber(endosseqNumber);
            endorseSequence.setNextNumber(endosseqNumber + 1);
            //   sequenceRepo.save(endorseSequence);

        }
//        policyTrans.setRenewable(product.isRenewable());
//        policyTrans.setTransType("BU");
//        policyTrans.setPolRevStatus("LD");
//        policyTrans.setInterfaceType("C");
//        policyTrans.setPolCreateddt(new Date());
        //savedPol = policyTransRepo.save(policyTrans);
////        timizaCreation.setPolicyTrans(savedPol);
//        timizaCreation.setTransProcessed("Y");
//        timizaCreation.setProcessedDate(new Date());
//        timizaCreation.setProcessedBy(userUtils.getCurrentUser());
//        timizaRepo.save(timizaCreation);
//
//        systemTrans = new SystemTrans();
//        systemTrans.setDoneDate(new Date());
//        systemTrans.setDoneBy(userUtils.getCurrentUser());
//        systemTrans.setPolicy(savedPol);
//        systemTrans.setTransLevel("U");
//        systemTrans.setTransCode("BUD");
//        systemTrans.setTransAuthorised("N");
//        systemTransRepo.save(systemTrans);

        //     RiskTrans riskTrans = new RiskTrans();
        if (client != null) {
            //       riskTrans.setInsured(client);
        } else {
            throw new BadRequestException("Client with pin " + pinSearch + " does not exist in the system");
        }
        final BigDecimal basicPrem = timizaCreation.getPremium().abs().multiply(BigDecimal.valueOf(100 / 100.45));

        Integer age1 = dateUtils.getAge(timizaCreation.getClientDOB());
        BigDecimal age = BigDecimal.valueOf(age1);

        BindersDef newBinder = bindersRepo.findBinderByAccId(timizaCreation.getInsurerCode().getAcctId(), product.getProCode());
        String normalizedSubClass = timizaCreation.getProductName().trim().toLowerCase().replace(" ", "");
        String normalizedCoverType = timizaCreation.getCoverType().trim().toLowerCase().replace(" ", "");
        final SubClassDef subClassDef = subClassRepo.findBySubClassName(normalizedSubClass);
        final CoverTypesDef coverTypesDef = coverTypesRepo.findCoverTypesByBindId(newBinder.getBinId(), normalizedCoverType);
        System.out.println("Binder id ::" + newBinder.getBinId());
        System.out.println("CoverTypes id:: " + coverTypesDef.getCovId());
        BinderDetails newDetails = binderDetRepo.findOne(QBinderDetails.binderDetails.binder.binId.eq(newBinder.getBinId()).and(QBinderDetails.binderDetails.subCoverTypes.coverTypes.covId.eq(coverTypesDef.getCovId())));
//        riskTrans.setBinder(newBinder);
//        riskTrans.setCovertype(coverTypesDef);
//        riskTrans.setSubclass(subClassDef);
//        riskTrans.setRiskShtDesc(timizaCreation.getTransId());
//        riskTrans.setRiskDesc(timizaCreation.getPaymentMode());
//        riskTrans.setBinderDetails(newDetails);
//        riskTrans.setWefDate(wef);
//        riskTrans.setWetDate(wet);
//        riskTrans.setTransType("BU");
//        riskTrans.setPolicy(savedPol);
//        riskTrans.setAutogenCert("N");
//        riskTrans.setPremium(timizaCreation.getPremium());
//        riskTrans.setButchargePrem(timizaCreation.getPremium());
//        riskTrans.setComputePremium(timizaCreation.getPremium());
//        riskTrans.setNetpremium(timizaCreation.getPremium());
//        riskTrans.setSumInsured(BigDecimal.ZERO);
//        //
//        riskTrans.setCalcPremium(timizaCreation.getPremium());
//        riskTrans.setComputeType("SP");

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
        riskBean.setPremium(timizaCreation.getPremium().abs());
        riskBean.setSumInsured(BigDecimal.ZERO);//timizaCreation.getSumInsured().abs());
        policyDto.setRiskBean(riskBean);
//        policyDto.setNegotiatedPremium(timizaCreation.getPremium());


        //
        String businessType = product.getProGroup().getPrgType();
        if (!businessType.equalsIgnoreCase("L")) {
            // policyTrans.setBusinessType("N");
            // riskTrans.setInstallmentNo(1L);
        } else {
            //  policyTrans.setBusinessType("L");
        }
//
//        CommissionRates commissionRate = commRatesRepo.findOne(QCommissionRates.commissionRates.bindersDef.binId.eq(newBinder.getBinId()));
//        AccountDef accountDef = accountRepo.findOne(QAccountDef.accountDef.accountType.accountType.eq(AccountTypeEnum.INS)
//                .and(QAccountDef.accountDef.acctId.eq(timizaCreation.getInsurerCode().getAcctId())));
//
//        BigDecimal commissionRates = (commissionRate != null) ? commissionRate.getCommRate() : null;
//        BigDecimal accountCommRates = (accountDef != null) ? accountDef.getAccountType().getCommRate() : null;
//
//        if (commissionRates != null && commissionRates.compareTo(BigDecimal.ZERO) > 0) {
//    //        riskTrans.setCommRate(commissionRates);
//        } else if (accountCommRates != null && accountCommRates.compareTo(BigDecimal.ZERO) > 0) {
// //           riskTrans.setCommRate(accountCommRates);
//        } else {
//            throw new BadRequestException("Commission Rates is not setup");
//        }
//        RiskTrans savedTrans = riskTransRepo.save(riskTrans);
        //   long riskIdentifier = Long.valueOf(String.valueOf(dateUtils.getUwYear(policyTrans.getWefDate())) + String.valueOf(riskTrans.getRiskId()));
        //     riskTrans.setRiskIdentifier(riskIdentifier);

        //find a way to make this dynamic
        //save sections
//        if(timizaCreation.getFrequency().equalsIgnoreCase("M")) {
//            //   uploadValidatorsUtils.saveSectionTransaction("SUM ASSURED (MONTHLY)", timizaCreation.getPremium(), savedTrans);
//            List<RiskSectionBean> sections = uploadValidatorsUtils.saveSectionTransaction("SUM ASSURED (MONTHLY)", new BigDecimal("26")); //buildSectionBeans("29226", new BigDecimal("26"), savedRiskTrans);
//            policyDto.setSections(sections);
//        }else {
//            //      uploadValidatorsUtils.saveSectionTransaction("SUM ASSURED (ANNUAL)", timizaCreation.getPremium(), savedTrans);
//            List<RiskSectionBean> sections = uploadValidatorsUtils.saveSectionTransaction("SUM ASSURED (ANNUAL)", new BigDecimal("26")); //buildSectionBeans("29226", new BigDecimal("26"), savedRiskTrans);
//            policyDto.setSections(sections);
//        }

        //save life installments
        // uploadValidatorsUtils.savePolicyInstallments(savedPol, policyTrans.getBasicPrem(),wef, frequency);
        List<RiskSectionBean> sections;
        if (frequency.equalsIgnoreCase("Monthly")) {
            sections = uploadValidatorsUtils.saveSectionTransaction("SAM",newDetails.getDetId(), new BigDecimal("26")); //buildSectionBeans("29226", new BigDecimal("26"), savedRiskTrans);
        }else{
            sections = uploadValidatorsUtils.saveSectionTransaction("SAA",newDetails.getDetId(), new BigDecimal("26")); //buildSectionBeans("29226", new BigDecimal("26"), savedRiskTrans);
        }
        policyDto.setSections(sections);

        // return savedPol;
        PolicyTrans created = uploadValidatorsUtils.saveLifePolicyUpload(policyDto,userId);
        uploadValidatorsUtils.updateBasicNet(timizaCreation.getPremium(), BigDecimal.ZERO, created);

        timizaCreation.setPolicyTrans(created);
        timizaCreation.setTransProcessed("Y");
        timizaCreation.setPolicyAuthorized("N");
        timizaCreation.setProcessedDate(new Date());
        timizaCreation.setProcessedBy(user);
        timizaRepo.save(timizaCreation);

        return  created;
    }

    @Override
    public List<Long> processBulkTimizaPol(BatchCreditBatch batchCreditBatch, boolean isApproved) throws BadRequestException {

        if (batchCreditBatch.getBatchRecords().size() <= 0) {
            throw new BadRequestException("Select At least One Transaction To Process");
        }

        SecurityContext context = SecurityContextHolder.getContext();

        Job job = JobBuilder.aNewJob()
                .reader(new IterableRecordReader(batchCreditBatch.getBatchRecords()))
                .named("timiza_processing" + new SimpleDateFormat("ddMMyyyhhmmss").format(new Date()))
                .processor((RecordProcessor<Record, Record>) renForm -> {
                    SecurityContextHolder.setContext(context);

                    Long policyId = ((BatchRecord) renForm.getPayload()).getPayload().getBatchId();
                    Long userId = ((BatchRecord) renForm.getPayload()).getPayload().getUserId();
                    processSingleTimizaPol(policyId,userId,true);
                    return renForm;
                })
                .pipelineListener(new RenewalJobListener())
                .build();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Future<JobReport> report = executorService.submit(job);

        return  new ArrayList<>();


//        if (timizaIds.size() <= 0) {
//            throw new BadRequestException("Select At least One Transaction To Process");
//        }
//        PolicyTrans savedPol = null;
//        List<Long> processedPolicyIds = new ArrayList<>();
//        for (Long timizaId : timizaIds) {
//            savedPol = processSingleTimizaPol(timizaId, isApproved);
//            processedPolicyIds.add(savedPol.getPolicyId());
//        }
//        return processedPolicyIds;
    }

    @Override
    public String approveSingleTimizaPol(Long policyId) throws BadRequestException {
        authService.authorizeBulkUploadPolicies(policyId);
        Timiza timiza = timizaRepo.findBulkByPolicyId(policyId);
        timiza.setPolicyAuthorized("Y");
        timizaRepo.save(timiza);
        return "Transaction Authorized Successfully";
    }

    @Override
    public String approveBulkTimizaPol(List<Long> policyIds) throws BadRequestException {
        if (policyIds.size() <= 0) {
            throw new BadRequestException("Select At least One Transaction To Process");
        }
        for (Long policyId : policyIds) {
            authService.authorizeBulkUploadPolicies(policyId);
            Timiza timiza = timizaRepo.findBulkByPolicyId(policyId);
            timiza.setPolicyAuthorized("Y");
            timizaRepo.save(timiza);
        }

        return "Transactions Authorized Successfully";
    }

    @Override
    @Transactional
    public String deleteUploadBulkTimizaPols(List<Long> timizaIds) {
        try {
            // Fetch all Timiza records for the given timizaIds at once
            Iterable<Timiza> timizaCreations = timizaRepo.findAll(QTimiza.timiza.timizaId.in(timizaIds));

            for (Timiza timizaCreation : timizaCreations) {
                if (timizaCreation != null && "N".equals(timizaCreation.getTransProcessed())) {
                    timizaRepo.delete(timizaCreation);
                }
            }

            return "Deleted successfully";
        } catch (Exception e) {
            // Log the exception or handle it appropriately
            // Logger.log(e.getMessage(), e);
            return "An error occurred while deleting.";
        }
    }


    @Override
    @Transactional
    public String deleteProcessedTimizaPols(List<Long> timizaIds) throws BadRequestException {

        if (timizaIds.size() <= 0){
            throw new BadRequestException("Select At least One Transaction To Delete");
        }
        for(Long timizaId : timizaIds){
            //authService.authorizeBulkUploadPolicies(policyId)
            //delete the policy from the table
            Timiza timizaCreation = timizaRepo.findBulkByPolicyId(timizaId);
            timizaRepo.delete(timizaCreation);
            //delete the created policy
            uploadValidatorsUtils.deleteprocessedPol(timizaId);
        }

        return "Transactions Deleted Successfully";
    }

}