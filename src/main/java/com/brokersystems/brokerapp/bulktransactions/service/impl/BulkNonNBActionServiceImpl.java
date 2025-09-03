package com.brokersystems.brokerapp.bulktransactions.service.impl;

import com.brokersystems.brokerapp.accounts.service.AccountsService;
import com.brokersystems.brokerapp.bulktransactions.ErrorsCache.ErrorWorkbookCache;
import com.brokersystems.brokerapp.bulktransactions.dtos.BulkPolicyCreationDTO;
import com.brokersystems.brokerapp.bulktransactions.models.*;
import com.brokersystems.brokerapp.bulktransactions.repositories.*;
import com.brokersystems.brokerapp.bulktransactions.service.BulkNonNBActionService;
import com.brokersystems.brokerapp.bulktransactions.utils.BulkBatchSaveService;
import com.brokersystems.brokerapp.bulktransactions.utils.ExcelReaderUtil;
import com.brokersystems.brokerapp.certs.repository.PolicyCertsRepo;
import com.brokersystems.brokerapp.certs.repository.PrintQueueRepo;
import com.brokersystems.brokerapp.enums.RevenueItems;
import com.brokersystems.brokerapp.life.repository.PolicyAccrualPayRepo;
import com.brokersystems.brokerapp.life.repository.PolicyBeneficiariesRepo;
import com.brokersystems.brokerapp.life.repository.PolicyBenefitsDistributionRepo;
import com.brokersystems.brokerapp.life.repository.PolicyInstallmentsRepo;
import com.brokersystems.brokerapp.life.service.LifeEndorseService;
import com.brokersystems.brokerapp.medical.model.*;
import com.brokersystems.brokerapp.medical.repository.*;
import com.brokersystems.brokerapp.quotes.repository.QuotProductsRepo;
import com.brokersystems.brokerapp.schedules.repository.ScheduleTransRepo;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.exception.EndorsementsException;
import com.brokersystems.brokerapp.server.utils.DateUtilities;
import com.brokersystems.brokerapp.server.utils.Streamable;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.dto.UserDTO;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.*;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.brokersystems.brokerapp.trans.model.SystemTrans;
import com.brokersystems.brokerapp.trans.model.SystemTransactions;
import com.brokersystems.brokerapp.trans.repository.SystemTransRepo;
import com.brokersystems.brokerapp.trans.repository.SystemTransactionsTempRepo;
import com.brokersystems.brokerapp.trans.repository.TransChecksRepo;
import com.brokersystems.brokerapp.trans.service.PolicyAuthorization;
import com.brokersystems.brokerapp.users.dto.MakerCheckDTO;
import com.brokersystems.brokerapp.users.model.MakerChecker;
import com.brokersystems.brokerapp.users.model.QMakerChecker;
import com.brokersystems.brokerapp.users.repository.MakerCheckerRepo;
import com.brokersystems.brokerapp.users.service.MakerCheckerService;
import com.brokersystems.brokerapp.uw.dtos.*;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.uw.repository.*;
import com.brokersystems.brokerapp.uw.service.EndorseService;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import com.brokersystems.brokerapp.uw.service.PremComputeService;
import com.brokersystems.brokerapp.workflow.docs.DocType;
import com.brokersystems.brokerapp.workflow.docs.SysWfDocs;
import com.brokersystems.brokerapp.workflow.repository.SysWfDocsRepo;
import com.brokersystems.brokerapp.workflow.utils.WorkflowService;
import com.google.gson.Gson;
import com.mysema.query.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RuntimeService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.history.Revision;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.brokersystems.brokerapp.bulktransactions.utils.ExcelReaderUtil.findDataStartRow;
import static com.brokersystems.brokerapp.bulktransactions.utils.ExcelReaderUtil.isBlankRow;

@Service
@Slf4j
public class BulkNonNBActionServiceImpl implements BulkNonNBActionService {
    @Autowired
    private PolicyTransRepo policyTransRepo;

    @Autowired
    private UserUtils userUtils;


    @Autowired
    private PolTaxesRepo polTaxesRepo;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private PolClausesRepo polClausesRepo;

    @Autowired
    private PolTaxesRepo policyRepo;

    @Autowired
    private PolicyInstallmentsRepo policyInstallmentsRepo;

    @Autowired
    private EndorseService endorseService;


    @Autowired
    private PolActiveRisksRepo activeRisksRepo;

    @Autowired
    private RiskTransRepo riskTransRepo;

    @Autowired
    private SectionTransRepo sectionTransRepo;

    @Autowired
    private SystemTransRepo transRepo;

    @Autowired
    private ParamService paramService;

    @Autowired
    private DateUtilities dateUtils;

    @Autowired
    private PolicyAccrualPayRepo policyAccrualPayRepo;

    @Autowired
    private PremComputeService premComputeService;

    @Autowired
    private PolicyAuthorization polAuthService;

    @Autowired
    private PolicyTransService polTransService;

    @Autowired
    private PolicyRemarksRepo policyRemarksRepo;

    @Autowired
    private PrintQueueRepo queueRepo;
    @Autowired
    private PolicyCertsRepo certsRepo;

    @Autowired
    private ProductsRepo productRepo;

    @Autowired
    private MedicalCategoryRepo categoryRepo;

    @Autowired
    private CategoryMembersRepo memberRepo;

    @Autowired
    private SelfFundParamsRepo selfFundParamsRepo;

    @Autowired
    private CategoryRulesRepo rulesRepo;
    @Autowired
    private CategoryBenefitRepo benefitRepo;

    @Autowired
    private CatExclusionsRepo exclusionsRepo;

    @Autowired
    private CatLoadingRepo loadingRepo;

    @Autowired
    private CatProvidersRepo providersRepo;

    @Autowired
    private MedicalCardsCrudRepo cardsRepo;

    @Autowired
    private CategoryClausesRepo categoryClausesRepo;

    @Autowired
    private ScheduleTransRepo scheduleTransRepo;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private SysWfDocsRepo sysWfDocsRepo;

    @Autowired
    private RiskDocsRepo riskDocsRepo;

    @Autowired
    private TransChecksRepo transChecksRepo;

    @Autowired
    private BinderReqrdDocsRepo reqrdDocsRepo;

    @Autowired
    private SubclassReqDocRepo subclassReqDocRepo;

    @Autowired
    private RiskIntPartiesRepo intPartiesRepo;

    @Autowired
    private PolicyBeneficiariesRepo beneficiariesRepo;

    @Autowired
    private MakerCheckerRepo makerCheckerRepo;

    @Autowired
    private QuotProductsRepo quotProductsRepo;

    @Autowired
    private CategoryMemberBenefitsRepo memberBenefitsRepo;

    @Autowired
    private PremComputeService premiumService;

    @Autowired
    private PolicyBindersRepo policyBindersRepo;

    @Autowired
    private DataSource dataSource;
    @Autowired
    private PolicyBenefitsDistributionRepo policyBenefitsDistributionRepo;

    @Autowired
    private EscalationRecordRepository escalationRecordRepository;

    @Autowired
    private EscalationActivityLoggerRepository escalationActivityLoggerRepository;

    @Autowired
    private SystemTransactionsTempRepo systemTransactionsTempRepo;

    @Autowired
    private BulkPolicyEnCreationRepo bulkPolicyEnCreationRepo;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private BulkPolicyRnCreationRepo bulkRenewalRepo;
    @Autowired
    private BulkPolicyCnCreationRepo bulkPolicyCnCreationRepo;
    @Autowired
    private PolicyTransService policyService;
    @Autowired
    private PolicyAuthorization authService;
    @Autowired
    private BulkRenewalErrorRepo bulkRenewalErrorRepo;
    @Autowired
    private BulkBatchSaveService bulkBatchSaveService;
    @Autowired
    private MakerCheckerService makerCheckerService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UploadValidatorsUtils uploadValidatorsUtils;
    @Autowired
    private RiskTransRepo riskRepo;
    @Autowired
    private SectionTransRepo sectionRepo;
    @Autowired
    private BulkRefundsCreationRepo bulkRefundsCreationRepo;
    @Autowired
    private  BulkPolicyEnRiskRepository bulkPolicyEnRiskRepository;

    @Autowired
    private AccountsService accountsService;

    @Autowired
    private LifeEndorseService lifeEndorseService;

    @Autowired
    private ErrorWorkbookCache errorWorkbookCache;


@Override
@Transactional
public Map<String, Object> uploadBulkENPolExcel(MultipartFile file) throws BadRequestException {
    String fileName = file.getOriginalFilename();
    if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
        throw new BadRequestException("Upload files with .xlsx or .xls extension only");
    }

    Map<String, Object> response = new HashMap<>();
    List<String> invalidRecords = new ArrayList<>();

    // Maps to track successful entries
    Map<String, BulkPolEnCreation> policyMap = new HashMap<>();
    Map<String, BulkPolEnCreation> finalPoliciesToSave = new HashMap<>();
    Map<String, List<BulkPolicyENRisk>> riskGroupMap = new HashMap<>();

    String batchId = UUID.randomUUID().toString();

    try (InputStream inputStream = file.getInputStream()) {
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheetOne = workbook.getSheetAt(0);
        Sheet sheetTwo = workbook.getSheetAt(1);

        int startRowIndex = findDataStartRow(sheetOne);
        if (startRowIndex == -1) throw new BadRequestException("Sheet One contains no data.");

        int errorColumnIndex = sheetOne.getRow(startRowIndex).getLastCellNum();

        // Read Sheet One (Policies)
        for (int rowNum = startRowIndex; rowNum <= sheetOne.getLastRowNum(); rowNum++) {
            Row row = sheetOne.getRow(rowNum);
            if (isBlankRow(row)) continue;

            try {
                String polNo = ExcelReaderUtil.getCellValue(row, 0).trim();
                String serialNo = polNo; // used as unique key
                BigDecimal sumInsured = ExcelReaderUtil.parseBigDecimalSafe(ExcelReaderUtil.getCellValue(row, 1));
                BigDecimal premium = ExcelReaderUtil.parseBigDecimalSafe(ExcelReaderUtil.getCellValue(row, 2));
                String polRemarks = ExcelReaderUtil.getCellValue(row, 3);
                Date effectiveDate = ExcelReaderUtil.parseFlexibleDate(ExcelReaderUtil.getCellValue(row, 4));

                // Validate basic data
                if (StringUtils.isEmpty(polRemarks) ||
                        !(polRemarks.equalsIgnoreCase("UPWARDS") || polRemarks.equalsIgnoreCase("DOWNWARDS"))) {
                    throw new BadRequestException("Endorsement remarks must be UPWARDS or DOWNWARDS.");
                }

                PolicyTrans policyTrans = policyTransRepo.findActiveNonNNNBulkProcess(polNo);
                if (policyTrans == null) throw new BadRequestException("Policy " + polNo + " not found.");
                if (effectiveDate == null) throw new BadRequestException("Effective date is missing.");
                if (effectiveDate.before(policyTrans.getCoverFrom())) {
                    throw new BadRequestException("Effective Date cannot be before policy cover start date.");
                }

                String bulkCode = UUID.randomUUID().toString();

                BulkPolEnCreation creation = new BulkPolEnCreation();
                creation.setProductGroup(policyTrans.getProduct().getProGroup().getPrgType());
                creation.setPolicyId(policyTrans.getPolicyId());
                creation.setPolProposalNo(policyTrans.getProposalNo());
                creation.setPolNo(policyTrans.getPolNo());
                creation.setClientCif(policyTrans.getClient().getClientCIF());
                creation.setClientPin(policyTrans.getClient().getPinNo());
                creation.setClientFname(policyTrans.getTransType()); //settranstype
                creation.setClientOtherNames(policyTrans.getClient().getOtherNames());
                creation.setEffectiveDate(effectiveDate);
                creation.setSumInsured(sumInsured.abs());
                creation.setPremium(premium.abs());
                creation.setTransProcessed("N");
                creation.setTransAuthorized("N");
                creation.setUploadedBy(userUtils.getCurrentUser());
                creation.setRefCode(bulkCode);
                creation.setDateUploaded(new Date());
                creation.setCoverDateFrom(policyTrans.getCoverFrom());
                creation.setDirection(polRemarks.toUpperCase().contains("UP") ? "UP" : "DOWN");
                creation.setEnRemarks(creation.getDirection().equals("UP") ? "Upward revision" : "ENDDOWN");

                policyMap.put(serialNo, creation);
            } catch (Exception e) {
                Cell errorCell = row.createCell(errorColumnIndex);
                errorCell.setCellValue("Error: " + e.getMessage());
                invalidRecords.add("Sheet One, Row " + (rowNum + 1) + ": " + e.getMessage());
            }
        }

        // Read Sheet Two (Risks)
        int startRowIndex2 = findDataStartRow(sheetTwo);
        //if (startRowIndex2 == -1) throw new BadRequestException("Sheet Two contains no data.");
        //int errorColumnIndex2 = sheetTwo.getRow(startRowIndex2).getLastCellNum();
        boolean sheetTwoHasData = startRowIndex2 != -1;
        int errorColumnIndex2 = sheetTwoHasData ? sheetTwo.getRow(startRowIndex2).getLastCellNum() : -1;
        if (sheetTwoHasData) {

        }
        for (int rowNum = startRowIndex2; rowNum <= sheetTwo.getLastRowNum(); rowNum++) {
            Row row = sheetTwo.getRow(rowNum);
            if (isBlankRow(row)) continue;

            try {
                String serialNo = ExcelReaderUtil.getCellValue(row, 0).trim();
                String sections = ExcelReaderUtil.getCellValue(row, 1);
                BigDecimal amount = ExcelReaderUtil.parseBigDecimalSafe(ExcelReaderUtil.getCellValue(row, 2));

                BulkPolEnCreation policy = policyMap.get(serialNo);
                if (policy == null) {
                    throw new BadRequestException("Serial No " + serialNo + " not found in Sheet One.");
                }

                BulkPolicyENRisk risk = new BulkPolicyENRisk();
                risk.setSerialNo(serialNo);
                risk.setSections(sections);
                risk.setLimit(amount);
                risk.setBulkPolicy(policy);
                risk.setRiskStatus("Y");
                risk.setRiskCode(policy.getRefCode());

                riskGroupMap.computeIfAbsent(serialNo, k -> new ArrayList<>()).add(risk);
            } catch (Exception e) {
                Cell errorCell = row.createCell(errorColumnIndex2);
                errorCell.setCellValue("Error: " + e.getMessage());
                invalidRecords.add("Sheet Two, Row " + (rowNum + 1) + ": " + e.getMessage());
            }
        }

        // Only keep policies that have at least one valid risk
        for (String serialNo : policyMap.keySet()) {
            if (riskGroupMap.containsKey(serialNo)) {
                finalPoliciesToSave.put(serialNo, policyMap.get(serialNo));
            }
        }

        List<BulkPolEnCreation> finalPolicies = new ArrayList<>(finalPoliciesToSave.values());
        List<BulkPolicyENRisk> finalRisks = finalPolicies.stream()
                .flatMap(p -> riskGroupMap.get(p.getPolNo()).stream())
                .collect(Collectors.toList());

        // Save valid policies and their risks
        if (!finalPolicies.isEmpty()) {
            bulkPolicyEnCreationRepo.save(finalPolicies);
        }
        if (!finalRisks.isEmpty()) {
            bulkPolicyEnRiskRepository.save(finalRisks);
        }

        // Save error workbook
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        errorWorkbookCache.saveWorkbook(batchId, bos);

        response.put("errorFileId", batchId);
        response.put("failedRecords", invalidRecords.size());
        response.put("successfulPolicies", finalPolicies.size());
        response.put("successfulRisks", finalRisks.size());

        return response;

    } catch (Exception e) {
        throw new BadRequestException("Error processing the Excel file: " + e.getMessage());
    }
}


    @Override
    public DataTablesResult<BulkPolicyCreationDTO> unProcessedBulkEnPol(DataTablesRequest request) {
        List<Object[]> unProcessedBulkPols = bulkPolicyEnCreationRepo.findUnprocessedBulkPol((request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%", request.getPageNumber(), request.getPageSize());
        final List<BulkPolicyCreationDTO> unProcessed = new ArrayList<>();
        long rowCount = 0L;
        if (!unProcessedBulkPols.isEmpty()) rowCount = ((BigInteger) unProcessedBulkPols.get(0)[11]).intValue();

        for (Object[] unProcessedBulkPol : unProcessedBulkPols) {
            BulkPolicyCreationDTO bulkPolicyCreationDTO = new BulkPolicyCreationDTO();
            bulkPolicyCreationDTO.setCoverType((String) unProcessedBulkPol[0]);
            bulkPolicyCreationDTO.setClientFname((String) unProcessedBulkPol[1]);
            bulkPolicyCreationDTO.setClientOtherNames((String) unProcessedBulkPol[2]);
            bulkPolicyCreationDTO.setSumInsured(unProcessedBulkPol[3] != null ? (BigDecimal) unProcessedBulkPol[3] : null);
            bulkPolicyCreationDTO.setPremium((BigDecimal) unProcessedBulkPol[4]);
            bulkPolicyCreationDTO.setEnRemarks((String) unProcessedBulkPol[5]);
            bulkPolicyCreationDTO.setEffectiveDate((Date) unProcessedBulkPol[6]);
            bulkPolicyCreationDTO.setCoverDateFrom((Date) unProcessedBulkPol[7]);
            bulkPolicyCreationDTO.setDateUploaded((Date) unProcessedBulkPol[8]);
            bulkPolicyCreationDTO.setBulkPolicyId(((BigInteger) unProcessedBulkPol[9]).longValue());
            bulkPolicyCreationDTO.setPolNumber((String) unProcessedBulkPol[10]);

            unProcessed.add(bulkPolicyCreationDTO);
        }
        Page<BulkPolicyCreationDTO> page = new PageImpl<>(unProcessed, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<BulkPolicyCreationDTO> viewBulkEnPolicies(DataTablesRequest request) {
        List<Object[]> viewBulkPolicies = bulkPolicyEnCreationRepo.viewUnprocessedBulkPol((request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%", request.getPageNumber(), request.getPageSize());
        final List<BulkPolicyCreationDTO> unProcessed = new ArrayList<>();
        long rowCount = 0L;
        if (!viewBulkPolicies.isEmpty()) rowCount = ((BigInteger) viewBulkPolicies.get(0)[11]).intValue();

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
            bulkPolicies.setPolicyStatus((String) viewBulkPolicy[10]);
            unProcessed.add(bulkPolicies);
        }
        Page<BulkPolicyCreationDTO> page = new PageImpl<>(unProcessed, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
//    @Transactional(propagation = Propagation.REQUIRED)
    public List<Long> bulkProcessENPolicies(List<Long> bulkIds, boolean isApproved) throws BadRequestException, EndorsementsException, IOException, IllegalAccessException {
        List<Long> processedPolicyIds = new ArrayList<>();
        for(Long bulkId : bulkIds){
            BulkPolEnCreation policyCreation = bulkPolicyEnCreationRepo.findOne(QBulkPolEnCreation.bulkPolEnCreation.bulkPolicyId.eq(bulkId));


            RevisionForm revisionForm = new RevisionForm();
            revisionForm.setRevisionType("EN");
            revisionForm.setEffectiveDate(policyCreation.getEffectiveDate());
            revisionForm.setPolicyId(policyCreation.getPolicyId());
            revisionForm.setRemarks(policyCreation.getEnRemarks());
            //revisionForm.setAmount(policyCreation.getSumInsured());

            BigDecimal newSumInsured = BigDecimal.ZERO;
            BigDecimal newPremium = BigDecimal.ZERO;

            if(policyCreation.getSumInsured() != null){
                newSumInsured = policyCreation.getSumInsured();
            }
            if(policyCreation.getPremium() != null){ //get the to endorse premium
                if (policyCreation.getDirection() != null && policyCreation.getDirection().equalsIgnoreCase("UP")) {
                    newPremium = policyCreation.getPremium(); // +ve new premium
                } else {
                    newPremium = policyCreation.getPremium().negate(); // -ve new premium
                }
            }

            Long pendingTransaction = endorseService.countUnauthTransactions(policyCreation.getPolNo());
            System.out.println("pending transaction"+pendingTransaction);
            if(pendingTransaction != null && pendingTransaction > 0) {
                System.out.println("Deleting existing pending transactions.");
                List<Object[]> policiesList = policyTransRepo.findPendingTransactions(policyCreation.getPolNo(), 0, 100000);
                for (Object[] policy : policiesList) {
                    Long policyId = (((BigInteger) policy[5]).longValue());
                    System.out.println("Starting Deleting polid "+policyId);
                    endorseService.deletePolicyRecord(policyId,true);//
                    System.out.println("Completed Deleting polid "+policyId);
                }
            }
                Long prevPol = revisionForm.getPolicyId();

               Long newPolicyCode = endorseService.reviseTransaction(revisionForm);

                //insured client id
                Iterable<PolicyActiveRisks> endorsedRisks = activeRisksRepo.findAll(QPolicyActiveRisks.policyActiveRisks.policy.policyId.eq(newPolicyCode));

                for (PolicyActiveRisks activeRisk : endorsedRisks) {
                    String endorseType = "R";
                    Long countRisks = endorseService.findRiskExpiredSections(newPolicyCode);

                    if(countRisks>0 && !"S".equalsIgnoreCase(endorseType)){
                        throw new BadRequestException("The Risk Being Endorsed Has Expired Sections That need to Be Reinstated. Reinstate the sections first before proceeding.");
                    } if(countRisks<1 && "S".equalsIgnoreCase(endorseType) ){
                        throw new BadRequestException("The Risk Being Reinstated Has No Expired Sections... ");
                    }
                    endorseService.endorseRisk(activeRisk.getArId(),endorseType,newPremium);
                }



                List<Object[]> riskTransList = riskTransRepo.findPolicyRiskTrans(newPolicyCode);
                int countRisk = 0;
                for(Object[] riskTrans : riskTransList) {
                    final Long riskId = ((BigInteger) riskTrans[1]).longValue();
                    if(countRisk == 0 ) {
                        System.out.println("overiding risk prem but to new prem"+newPremium+"for riskid"+riskId);
                        //riskRepo.updateRiskOverridePrem(newPremium, riskId);
                    }
                    countRisk++;

                    final List<Object[]> sectionsTrans = sectionTransRepo.findRiskSectionTrans(riskId);
                    int count = 0;
                    for(Object[] sectionTrans : sectionsTrans) {
                        if(count > 0) break;
                        final Long sectionId = ((BigInteger) sectionTrans[0]).longValue();
                        final String sectType = ((String) sectionTrans[13]);
                        if(sectType.equalsIgnoreCase("SI")){
                            if(newSumInsured!=null) {
                                //sectionTransRepo.updateSectionSumAssured(newSumInsured, sectionId);
                                count++;
                            }
                        }
                    }
                }

                try {
                    premiumService.computeEndorsePremium(newPolicyCode);
                } catch (BadRequestException e) {
                    //throw new RuntimeException(e);
                    log.info("runtime {}, {}", e, e.getMessage());
                }


                PolicyTrans rnPolicy = policyTransRepo.findOne(newPolicyCode);

                //make the policy ready
                PolicyCreateDTO policy = new PolicyCreateDTO();
                policy.setPolicyId(newPolicyCode);
                policy.setTransType("EN");
                policy.setPrevPolicy(prevPol);
                policy.setClientId(rnPolicy.getClient().getTenId());
                policy.setBusinessType(rnPolicy.getBusinessType());
                policy.setProdId(rnPolicy.getProduct().getProCode());
                policy.setAgentId(rnPolicy.getAgent().getAcctId());
                policy.setInterfaceType(rnPolicy.getInterfaceType());
                policy.setAccrualPaymentType("Dispensation");
                policy.setFrequency(rnPolicy.getFrequency());
                policy.setAccrualInstDate(rnPolicy.getWefDate()); // read from the user
                policy.setWefDate(rnPolicy.getWefDate());
                policy.setWetDate(rnPolicy.getWetDate());
                policy.setBranchId(rnPolicy.getBranch().getObId());
                policy.setCurrencyId(rnPolicy.getTransCurrency().getCurCode());

                //subagent
                policy.setSubAgentId(null);
            policy.setPolNo(rnPolicy.getPolNo());
            policy.setPolRevNo(rnPolicy.getPolRevNo());

            if(newPolicyCode!=null) {
                policyCreation.setTransProcessed("Y");
                policyCreation.setTransAuthorized("N");
                policyCreation.setEnPolicyId(newPolicyCode);
                bulkPolicyEnCreationRepo.save(policyCreation);
                processedPolicyIds.add(newPolicyCode);
            }

            createRnPolicyMakeReady(policy, newPolicyCode);
        }
        return processedPolicyIds;
    }

    @Override
    public List<Long> bulkAuthorizeEndorsement(List<Long> bulkIds) throws BadRequestException {
        for(Long bulkId : bulkIds) {
            BulkPolEnCreation bulkPolEnCreation = bulkPolicyEnCreationRepo.findBulkStockByPolicyId(bulkId);
            System.out.println(
                    "bulk id "+ bulkId + "old pol code "+ bulkPolEnCreation.getPolicyId()+" new en pol"+bulkPolEnCreation.getEnPolicyId()
            );
            try {
                Long polCode = bulkPolEnCreation.getEnPolicyId();
                if (bulkPolEnCreation.getProductGroup() != null && bulkPolEnCreation.getProductGroup().equalsIgnoreCase("F")) {
                    //authorize general policy
                    authService.authorizePolicy(polCode, BigDecimal.ZERO, true);
                } else {
                    //authorize life policy
                    authService.authorizeLifePolicy(polCode);
                }
            } catch (Exception e) {
                throw new BadRequestException("Authorization failed for bulk ID " + bulkId + ": " + e.getMessage());
            }

            bulkPolEnCreation.setTransAuthorized("Y");
            bulkPolicyEnCreationRepo.save(bulkPolEnCreation);
        }
        return bulkIds;
    }


    @Override
    public List<Long> bulkDeleteEndorsements(List<Long> bulkIds){
        for(Long bulkId : bulkIds) {
            BulkPolEnCreation bulkPolEnCreation = bulkPolicyEnCreationRepo.policyFromUploadCheck(bulkId);
            List<BulkPolicyENRisk> bulkPolicyENRisks = bulkPolicyEnRiskRepository.findAllByBulkPolicy(bulkPolEnCreation);
            for(BulkPolicyENRisk bulkPolicyENRisk : bulkPolicyENRisks){
                bulkPolicyEnRiskRepository.delete(bulkPolicyENRisk);
            }
            bulkPolicyEnCreationRepo.delete(bulkPolEnCreation);
        }
        return bulkIds;
    }

    @Override
    public List<Long> bulkDelProcessedENPolicies(List<Long> bulkIds) throws BadRequestException{
        for(Long bulkId : bulkIds) {
            BulkPolEnCreation bulkPolEnCreation = bulkPolicyEnCreationRepo.findBulkStockByPolicyId(bulkId);

            if (bulkPolEnCreation == null) {
                System.out.println("No BulkPolicyEnCreation found for ID: " + bulkId);
                continue;
            }
            //check if processed
            if (!"Y".equalsIgnoreCase(bulkPolEnCreation.getTransProcessed())) {
                System.out.println("Transaction not marked as processed for ID: " + bulkId);
                continue;
            }
            List<BulkPolicyENRisk> bulkPolicyENRisks = bulkPolicyEnRiskRepository.findAllByBulkPolicy(bulkPolEnCreation);
            for(BulkPolicyENRisk bulkPolicyENRisk : bulkPolicyENRisks){
                bulkPolicyEnRiskRepository.delete(bulkPolicyENRisk);
            }

            uploadValidatorsUtils.deleteprocessedPol(bulkPolEnCreation.getEnPolicyId());
            bulkPolicyEnCreationRepo.delete(bulkPolEnCreation);

        }
        return bulkIds;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void endorseRisk(Long activeRiskCode, String endorseType, Long polCode) throws BadRequestException{

        Long countRisks = endorseService.findRiskExpiredSections(polCode);

        if(countRisks>0 && !"S".equalsIgnoreCase(endorseType)){
            throw new BadRequestException("The Risk Being Endorsed Has Expired Sections That need to Be Reinstated. Reinstate the sections first before proceeding.");
        } if(countRisks<1 && "S".equalsIgnoreCase(endorseType) ){
            throw new BadRequestException("The Risk Being Reinstated Has No Expired Sections... ");
        }
        endorseService.endorseRisk(activeRiskCode,endorseType,null);
//        try {
//            premiumService.computeEndorsePremium(polCode);
//        } catch (IOException e) {
//            throw new BadRequestException(e.getMessage());
//        }
    }
    private Long findActiveRiskTransaction(Long polCode, Long insuredId) {
        //final String search = (request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue().toLowerCase() + "%" : "%%";
        final String search = "%";

        if (insuredId == null) {
            insuredId = -2000L;
        }
        List<Object[]> risks = riskRepo.findPolicyActiveRisks(search.toLowerCase(), polCode, insuredId, 0, 10);
        final List<RiskTransDTO> riskList = new ArrayList<>();
        long rowCount = 0l;
        if (!risks.isEmpty()) rowCount = ((BigInteger) risks.get(0)[12]).intValue();
        Long riskId = null;
        for (Object[] risk : risks) {
            riskId = (((BigInteger) risk[0]).longValue());
            break;
        }
        return riskId;
    }
    public Long findActiveInsureds(String searchValue, Long polCode) {
        if(searchValue==null) searchValue="%%";
        else searchValue = "%"+searchValue+"%";
        List<Object[]> insureds = activeRisksRepo.findActiveRiskInsured(polCode,searchValue, 0, 10);
        long rowCount = 0L;
        if(!insureds.isEmpty()) rowCount = ((BigInteger)insureds.get(0)[2]).intValue();
        final List<ClientsDto> pageInsureds = new ArrayList<>();
        Long tenId = null;
        for(Object[] insured : insureds) {
            tenId =((BigInteger)insured[0]).longValue();
            break;
        }

        if(tenId == null){
            return  0L;
        }
        return tenId;

    }

    public void createPremRates(SectionTransDTO section, Long polCode) throws IllegalAccessException, IOException, BadRequestException {
        log.info("section {}, {}", section, polCode);
        policyService.createRiskSection(section);
        PolicyTrans policy = policyService.getPolicyDetails(polCode);
        String polBusinessType = policy.getProduct().getProGroup().getPrgType();
        log.info("transType {} ", policy.getTransType());
        if (!polBusinessType.equalsIgnoreCase("L")) {
            if ("NB".equalsIgnoreCase(policy.getTransType()) || "SP".equalsIgnoreCase(policy.getTransType()) || "EX".equalsIgnoreCase(policy.getTransType()) || "RN".equalsIgnoreCase(policy.getTransType()) || "RE".equalsIgnoreCase(policy.getTransType()))
                premiumService.computePrem(polCode);
            else if ("EN".equalsIgnoreCase(policy.getTransType())) {
                premiumService.computeEndorsePremium(polCode);
            }
        }
        else{
            premiumService.computeLifePrem(polCode);
        }
    }
    private SectionTransDTO findRiskSections(Long riskId) {

        List<Object[]> pages = sectionRepo.findRiskSectionTrans(riskId);

        SectionTransDTO sec = new SectionTransDTO();
        for (Object[] sectran : pages) {
            if (sectran[0] instanceof BigInteger) {
                sec.setSectId(((BigInteger) sectran[0]).longValue());
            } else if (sectran[0] instanceof BigDecimal) {
                sec.setSectId(((BigInteger) sectran[0]).longValue());
            }
            sec.setAmount((BigDecimal) sectran[1]);
            sec.setCalcprem((BigDecimal) sectran[2]);
            sec.setDivFactor((BigDecimal) sectran[3]);
            sec.setFreeLimit((BigDecimal) sectran[4]);
            //sec.setMultiRate((BigDecimal) sectran[5]);
            sec.setPrem((BigDecimal) sectran[6]);
            sec.setRate((BigDecimal) sectran[7]);
            if (sectran[8] instanceof BigInteger)
                //sec.setSection(setupSectionRepo.findOne(((BigInteger) sectran[8]).longValue()));
                sec.setSectionSectId(((BigInteger) sectran[8]).longValue());
            else if (sectran[8] instanceof BigDecimal)
                //sec.setSection(setupSectionRepo.findOne(((BigDecimal) sectran[8]).longValue()));
                sec.setSectionSectId((((BigDecimal) sectran[8]).longValue()));
            if (sectran[9] instanceof BigInteger)
                //sec.setPremRates(premRatesRepo.findOne(((BigInteger) sectran[9]).longValue()));
                sec.setPremRatesId(((BigInteger) sectran[9]).longValue());
            else if (sectran[9] instanceof BigDecimal)
                //sec.setPremRates(premRatesRepo.findOne(((BigDecimal) sectran[9]).longValue()));
                sec.setPremRatesId(((BigDecimal) sectran[9]).longValue());
            if (sectran[10] instanceof BigInteger)
                sec.setRiskId(((BigInteger) sectran[10]).longValue());
            else if (sectran[10] instanceof BigDecimal)
                sec.setRiskId(((BigDecimal) sectran[10]).longValue());
        }
        return sec;
    }

    private Long findRiskTransactions(Long polCode, Long bindCode) {
        //final String search = (request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue().toLowerCase() + "%" : "%%";
        final String search = "%";
        if (bindCode == null) {
            bindCode = -2000L;
        }
        List<Object[]> risks = riskRepo.findLifePolicyRisks(search.toLowerCase(), polCode, bindCode, 0, 10);
        final List<RiskTransDTO> riskList = new ArrayList<>();
        long rowCount = 0l;
        Long riskId = null;
        for (Object[] risk : risks) {
            if(riskId != null){
                break;
            }
            riskId = ((BigInteger) risk[0]).longValue();
        }
        return riskId;
    }


    @Override
    public String approveBulkPol(List<Long> policyIds) throws BadRequestException {
        for(Long polCode : policyIds){
            authService.authorizePolicy(polCode,BigDecimal.ZERO, true);
        }
        return "";
    }

    @Override
    public void bulkDelUploadPolicies(List<Long> bulkIds) throws BadRequestException {

    }

    //==========================================
    // bulk upload policy renewals
    //==========================================
    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> uploadBulkRNPolExcel(MultipartFile file) throws BadRequestException, IOException, InvalidFormatException {
        String batchId = UUID.randomUUID().toString();

        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            throw new BadRequestException("Only .xlsx or .xls files are allowed.");
        }

        List<BulkRenewal> validRecords = new ArrayList<>();
        List<BulkRenewalError> errorRecords = new ArrayList<>();
        int batchSize = 100;
        int totalSuccessCount = 0;
        int totalErrorCount = 0;

//        try () {
            InputStream inputStream = file.getInputStream();
            Workbook workbook = WorkbookFactory.create(inputStream);

            // Validate template format
            //            List<String> validationErrors = ExcelReaderUtil.validateBulkPolicyTemplate(workbook);
            //            if (!validationErrors.isEmpty()) {
            //                StringBuilder errorMessage = new StringBuilder("Template validation failed: ");
            //                for (String error : validationErrors) {
            //                    errorMessage.append(error).append("; ");
            //                }
            //                throw new BadRequestException(errorMessage.toString());
            //            }
            Sheet sheetOne = workbook.getSheetAt(0);

            int startRowIndex = findDataStartRow(sheetOne);
            if (startRowIndex == -1) {
                throw new BadRequestException("Sheet One contains no data.");
            }
            for (int rowNum = startRowIndex; rowNum <= sheetOne.getLastRowNum(); rowNum++) {
                Row row = sheetOne.getRow(rowNum);
                if (isBlankRow(row)) continue;

                try {
                    String polNo = ExcelReaderUtil.getCellValue(row, 0); //getCellStringValue(row.getCell(3));
                    //String polProposalNo = ExcelReaderUtil.getCellValue(row, 1); //getCellStringValue(row.getCell(3));
                    //PolicyTrans policyTrans = policyTransRepo.findBulkProcessPolicytransProposalAndPolNo(polNo, polProposalNo);
                    PolicyTrans policyTrans = policyTransRepo.findActiveNonNNNBulkProcess(polNo);

                    if (policyTrans == null) {
                        throw new IllegalArgumentException("Policy " + polNo + "not found");
                    } else if(!policyTrans.isRenewable()){
                        throw new IllegalArgumentException("The policy is not Renewable");
                    }

                    //save the policy upload and validate
                    BulkRenewal bulkPolRnCreation = new BulkRenewal();
                    bulkPolRnCreation.setPolicyId(policyTrans.getPolicyId());
                    bulkPolRnCreation.setPolProposalNo(policyTrans.getProposalNo());
                    bulkPolRnCreation.setPolNo(policyTrans.getPolNo());

                    final ClientDef client = policyTrans.getClient();
                    bulkPolRnCreation.setClientCif(client.getClientCIF());
                    bulkPolRnCreation.setClientPin(client.getPinNo());
                    bulkPolRnCreation.setClientFname(client.getFname());
                    bulkPolRnCreation.setClientOtherNames(client.getOtherNames());
                    bulkPolRnCreation.setOldSumInsured(policyTrans.getSumInsured());
                    bulkPolRnCreation.setOldPrem(policyTrans.getBasicPrem());
                    bulkPolRnCreation.setPolicyProcessed("N");
                    bulkPolRnCreation.setTransProcessed("N");
                    bulkPolRnCreation.setUploadedBy(userUtils.getCurrentUser());
                    bulkPolRnCreation.setBulkPolicyDateUploaded(new Date());
                    bulkPolRnCreation.setProductName(policyTrans.getProduct().getProShtDesc());
                    bulkPolRnCreation.setBulkPolicyWef(policyTrans.getCoverFrom());

                    //new values
                    BigDecimal sumInsured = ExcelReaderUtil.parseBigDecimalSafe(ExcelReaderUtil.getCellValue(row, 1)); //getCellStringValue(row.getCell(4));

                    if(sumInsured != null ) {
                        bulkPolRnCreation.setNewSumInsured(sumInsured);
                    }

                    BigDecimal premium = ExcelReaderUtil.parseBigDecimalSafe(ExcelReaderUtil.getCellValue(row, 2)); //getCellStringValue(row.getCell(4));

                    if(premium != null ) {
                        bulkPolRnCreation.setNewPrem(premium);
                    }
                    String paymentType = ExcelReaderUtil.getCellValue(row, 3).trim();
//
                    if(!paymentType.isEmpty()){
                        if(paymentType.equalsIgnoreCase("Accrual")){
                            Date instDate = ExcelReaderUtil.parseFlexibleDate(ExcelReaderUtil.getCellValue(row, 4)); //getCellDateValue(row.getCell(20)));
                            if(instDate == null){
                                throw  new BadRequestException("Accrual installment date is required.");
                            }
                            bulkPolRnCreation.setAccrualInstDate(instDate);

                            String ApaymentType = ExcelReaderUtil.getCellValue(row, 5); //getCellDateValue(row.getCell(20)));
                            if (ApaymentType == null ||
                                    (!ApaymentType.equalsIgnoreCase("Dispensation") && !ApaymentType.equalsIgnoreCase("IPF"))) {
                                throw new BadRequestException("Accrual payment is required. Either IPF or Dispensation");
                            }

                            bulkPolRnCreation.setAccrualPaymentType(ApaymentType);
                            bulkPolRnCreation.setNewInterface("Accrual");
                        }else if(paymentType.equalsIgnoreCase("Cash")){
                            bulkPolRnCreation.setNewInterface("Cash"); //getCellDateValue(row.getCell(20)));
                        } else {
                            throw new BadRequestException("Please fill with Cash or Accrual");
                        }
                    }else{
                        throw new BadRequestException("The payment type field is required.");
                    }


                    Date renewalDate = ExcelReaderUtil.parseFlexibleDate(ExcelReaderUtil.getCellValue(row, 6));
                    bulkPolRnCreation.setBulkPolicyRenewalDate(renewalDate);


                    validRecords.add(bulkPolRnCreation);

                    if(validRecords.size() >= batchSize) {
                        //bulkRenewalRepo.saveAll(validRecords);
                        bulkBatchSaveService.saveBulkRenewalBatch(validRecords);
                        totalSuccessCount += validRecords.size();
                        validRecords.clear();
                    }

                } catch (Exception ex) {
                    BulkRenewalError error = new BulkRenewalError();
                    error.setRowNumber(rowNum + 1);
                    error.setPolicyNumber(ExcelReaderUtil.getCellValue(row, 0));
                    error.setProposalNumber(ExcelReaderUtil.getCellValue(row, 1));
                    error.setErrorMessage(ex.getMessage());
                    error.setBatchId(batchId);
                    errorRecords.add(error);

                    if (errorRecords.size() >= batchSize) {
                        //bulkRenewalErrorRepo.saveAll(errorRecords);
                        bulkBatchSaveService.saveBulkRenewalErrorsBatch(errorRecords);
                        totalErrorCount += errorRecords.size();
                        errorRecords.clear();
                    }
                   // throw new BadRequestException("Sheet One, Row " + (rowNum + 1) + ": " + e.getMessage());
                }
            }
            if (!validRecords.isEmpty()) {
                //bulkRenewalRepo.saveAll(validRecords);
                bulkBatchSaveService.saveBulkRenewalBatch(validRecords);
                totalSuccessCount += validRecords.size();
            }

            if (!errorRecords.isEmpty()) {
                //bulkRenewalErrorRepo.saveAll(errorRecords);
                bulkBatchSaveService.saveBulkRenewalErrorsBatch(errorRecords);
                totalErrorCount += errorRecords.size();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("status", "completed");
            response.put("successCount", totalSuccessCount);
            response.put("errorCount", totalErrorCount);
            response.put("batchId", batchId);
            response.put("message", "Upload completed."); //successfulPolicies

            return response;

//        } catch (Exception e) {
//            throw new BadRequestException("Error processing the Excel file: " + e.getMessage());
//        }
    }

    @Transactional
    @Override
    public List<Long> bulkProcessRNPolicies(List<Long> bulkIds, boolean isApproved) throws BadRequestException, EndorsementsException {
        int totalSize = bulkIds.size();
        int failedCount = 0;
        int passedCount = 0;
        for(Long bulkId : bulkIds){
            BulkRenewal bulkRenewal = bulkRenewalRepo.findByBulkId(bulkId);

            RenewalsForm renewalForm = new RenewalsForm();
            renewalForm.setPolicyId(bulkRenewal.getPolicyId());
            renewalForm.setPolicyNumber(bulkRenewal.getPolNo());

            log.info("data being sent {}", renewalForm);

            Long pendingTransaction = endorseService.countUnauthTransactions(bulkRenewal.getPolNo());
            if(pendingTransaction != null && pendingTransaction > 0) {
                System.out.println("Deleting existing pending transactions.");
                List<Object[]> policiesList = policyTransRepo.findPendingTransactions(bulkRenewal.getPolNo(), 0, 10);
                for (Object[] policy : policiesList) {
                    Long policyId = (((BigInteger) policy[5]).longValue());
                    endorseService.deletePolicyRecord(policyId,true);//
                }
            }
            //renewal create
            Long newPolCode = policyRenewal(renewalForm, bulkRenewal);
            if(newPolCode != null ) {
                bulkRenewal.setNewPolId(newPolCode);
                bulkRenewal.setPolicyProcessed("Y");
                bulkRenewal.setTransProcessed("N");
                bulkRenewalRepo.save(bulkRenewal);
                passedCount++;
            }else{
                failedCount++;
            }
        }
        return bulkIds;
    }

    @Override
    public DataTablesResult<BulkPolicyCreationDTO> unProcessedBulkRNPol(DataTablesRequest request) {
        List<Object[]> unProcessedBulkPols = bulkRenewalRepo.findUnprocessedBulkPol((request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%", request.getPageNumber(), request.getPageSize());
        final List<BulkPolicyCreationDTO> unProcessed = new ArrayList<>();
        long rowCount = 0L;
        if (!unProcessedBulkPols.isEmpty()) rowCount = ((BigInteger) unProcessedBulkPols.get(0)[12]).intValue();
//        if (!unProcessedBulkPols.isEmpty()) rowCount = ((Number) unProcessedBulkPols.get(0)[12]).longValue();

        for (Object[] unProcessedBulkPol : unProcessedBulkPols) {
            BulkPolicyCreationDTO bulkPolicyCreationDTO = new BulkPolicyCreationDTO();
            bulkPolicyCreationDTO.setBulkPolicyId(((BigInteger) unProcessedBulkPol[0]).longValue());
            bulkPolicyCreationDTO.setPolNumber((String) unProcessedBulkPol[1]);
            bulkPolicyCreationDTO.setClientOtherNames((String) unProcessedBulkPol[2]);
            bulkPolicyCreationDTO.setPolicyStatus((String) unProcessedBulkPol[3]);
//            bulkPolicyCreationDTO.setSumInsured(unProcessedBulkPol[4] != null ? (BigDecimal) unProcessedBulkPol[4] : null);
//            bulkPolicyCreationDTO.setPremium((BigDecimal) unProcessedBulkPol[5]);
            bulkPolicyCreationDTO.setNewSumInsured(unProcessedBulkPol[4] != null ? (BigDecimal) unProcessedBulkPol[4] : null);
            bulkPolicyCreationDTO.setNewPrem((BigDecimal) unProcessedBulkPol[5]);
            bulkPolicyCreationDTO.setCoverDateFrom((Date) unProcessedBulkPol[6]);
            bulkPolicyCreationDTO.setNewInterface((String) unProcessedBulkPol[7]);
            bulkPolicyCreationDTO.setAccrualPaymentType((String) unProcessedBulkPol[8]);

            System.out.println("=== DEBUG FOR RECORD " + unProcessedBulkPol[0] + " ===");
            System.out.println("Raw accrualInstDate: " + unProcessedBulkPol[9]);
            System.out.println("Raw renewalDate: " + unProcessedBulkPol[10]);
            System.out.println("Raw dateUploaded: " + unProcessedBulkPol[11]);
            ;

            bulkPolicyCreationDTO.setAccrualInstDate((Date) unProcessedBulkPol[9]);
            bulkPolicyCreationDTO.setBulkPolicyRenewalDate((Date) unProcessedBulkPol[10]);
            bulkPolicyCreationDTO.setDateUploaded((Date) unProcessedBulkPol[11]);
//            bulkPolicyCreationDTO.setDateUploaded((Date) unProcessedBulkPol[7]);

            unProcessed.add(bulkPolicyCreationDTO);
        }
        Page<BulkPolicyCreationDTO> page = new PageImpl<>(unProcessed, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<BulkPolicyCreationDTO> viewBulkRNPolicies(DataTablesRequest request) {
        List<Object[]> viewBulkPolicies = bulkRenewalRepo.viewUnprocessedBulkPol((request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%", request.getPageNumber(), request.getPageSize());
        final List<BulkPolicyCreationDTO> unProcessed = new ArrayList<>();
        long rowCount = 0L;
        if (!viewBulkPolicies.isEmpty()) rowCount = ((BigInteger) viewBulkPolicies.get(0)[10]).intValue();

        for (Object[] viewBulkPolicy : viewBulkPolicies) {
            BulkPolicyCreationDTO bulkPolicies = new BulkPolicyCreationDTO();
            bulkPolicies.setBulkPolicyId(((BigInteger) viewBulkPolicy[0]).longValue());
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



    public void createRnPolicyMakeReady(PolicyCreateDTO policy, Long polCode) throws BadRequestException {
        log.info("Policy creating with dto {} {}", policy, polCode);
        String transType = policy.getTransType();

        // Adjust dates only if not endorsement or cancellation
        //	if (!"EN".equalsIgnoreCase(transType) && !"CN".equalsIgnoreCase(transType)) {
        //		LocalDate today = LocalDate.now();
        //		if (HolidayUtils.isHolidayOrWeekend(today)) {
        //			adjustPolicyDates(policy, transType);
        //		}
        //	}


        // If no checkers selected, proceed with full policy creation
//        if (policy.getCheckerIds() == null || policy.getCheckerIds().isEmpty()) {
//
//            policyService.createPolicy(policy, true);
//
//
//            PolicyTrans created = policyService.getPolicyDetails(polCode);
//
//            if ("NB".equalsIgnoreCase(created.getTransType()) || "SP".equalsIgnoreCase(created.getTransType())
//                    || "EX".equalsIgnoreCase(created.getTransType()) || "RN".equalsIgnoreCase(created.getTransType())
//                    || "BU".equalsIgnoreCase(created.getTransType()) || "LD".equalsIgnoreCase(created.getTransType())|| "RE".equalsIgnoreCase( created.getTransType())) {
//                try {
//                    premiumService.computePrem(polCode);
//                } catch (IOException e) {
//                    throw new BadRequestException(e.getMessage());
//                }
//            } else if ("EN".equalsIgnoreCase(created.getTransType())) {
//                try {
//                    premiumService.computeEndorsePremium(polCode);
//                } catch (IOException e) {
//                    throw new BadRequestException(e.getMessage());
//                }
//            }
//
//            //String response = policyService.makeReady(polCode);
//            String response = uploadValidatorsUtils.makeGeneralUploadReady(polCode);
//            policy.setStatus(response);
//            System.out.println("policy made ready"+response);
//        }

        uploadValidatorsUtils.makeGeneralUploadReady(polCode);

        //  Get latest policy details regardless (needed for maker-checker creation)
        PolicyTrans created = policyService.getPolicyDetails(polCode);

        final Long hashCode = Long.parseLong(String.valueOf(policy.hashCode()));
        List<UserDTO> eligibleCheckers = makerCheckerService.findEligibleCheckers("AUTHORIZE_POLICY", null);
        List<Long> checkerIds = new ArrayList<Long>();

        if ("CO".equalsIgnoreCase(policy.getTransType())) {
//		final Long hashCode = Long.parseLong(String.valueOf(policy.hashCode()));
//		List<UserDTO> eligibleCheckers = makerCheckerService.findEligibleCheckers("AUTHORIZE_POLICY", null);
//		List<Long> checkerIds = new ArrayList<Long>();

            if (!makerCheckerRepo.exists(QMakerChecker.makerChecker.taskType.equalsIgnoreCase("ANP")
                    .and(QMakerChecker.makerChecker.policyId.eq(polCode)))) {

                for (UserDTO eligibleChecker : eligibleCheckers) {
                    checkerIds.add(eligibleChecker.getId());
                }

                MakerCheckDTO makerCheckDTO = new MakerCheckDTO();
                makerCheckDTO.setStatus("N");
//			makerCheckDTO.setTaskName(String.format("Reverse Policy %s ", policy.getPolNo()));
                makerCheckDTO.setTaskName(String.format(" %s ", policy.getPolNo()));
                makerCheckDTO.setTaskType("ANP");
                makerCheckDTO.setJson(new Gson().toJson(policy));
                makerCheckDTO.setTaskCode(hashCode);
                makerCheckDTO.setAssignedCheckers(checkerIds.toString());
                makerCheckDTO.setPolicyId(policy.getPolicyId());

                makerCheckerService.checkExists(makerCheckDTO);
                //makerCheckerService.createMakerChecker(makerCheckDTO);
                makerCheckerService.createBulkMakerChecker(makerCheckDTO);

                // Return without processing further - wait for checker approval
            }
        }



        if (!makerCheckerRepo.exists(QMakerChecker.makerChecker.taskType.equalsIgnoreCase("ANP")
                .and(QMakerChecker.makerChecker.policyId.eq(polCode)))) {

            for (UserDTO eligibleChecker : eligibleCheckers) {
                checkerIds.add(eligibleChecker.getId());
            }

            MakerCheckDTO makerCheckDTO = new MakerCheckDTO();
            makerCheckDTO.setStatus("N");
//		makerCheckDTO.setTaskName(String.format("Policy %s ", policy.getPolNo()));
            makerCheckDTO.setTaskName(String.format(" %s", policy.getPolNo()));
            makerCheckDTO.setTaskType("ANP");
            makerCheckDTO.setJson(new Gson().toJson(policy));
            makerCheckDTO.setTaskCode(hashCode);
            makerCheckDTO.setAssignedCheckers(checkerIds.toString());
            makerCheckDTO.setPolicyId(created.getPolicyId());

            makerCheckerService.checkExists(makerCheckDTO);

            if (policy.getCheckerIds() != null && !policy.getCheckerIds().isEmpty()) {
                Long selectedMakerId = Long.parseLong(policy.getCheckerIds().get(0));
                makerCheckDTO.setMakerId(selectedMakerId);
            }

            //makerCheckerService.createMakerChecker(makerCheckDTO);
            makerCheckerService.createBulkMakerChecker(makerCheckDTO);

        } else {
            MakerChecker makerChecker = makerCheckerRepo.findOne(QMakerChecker.makerChecker.taskType
                    .equalsIgnoreCase("ANP").and(QMakerChecker.makerChecker.policyId.eq(created.getPolicyId())));
            if(policy.getCheckerIds()!=null) {
                for (String checkerId : policy.getCheckerIds()) {
                    System.out.println("policy id: " + policy.getPolicyId() + " checker id: " + checkerId);
                    Long checker_id = Long.parseLong(checkerId);
                    checkerIds.add(checker_id);
                }
            }
            if (checkerIds != null && !checkerIds.isEmpty()) {
                Long firstCheckerId = checkerIds.get(0);
                User checker = userRepository.findOne(firstCheckerId);
                if (checker != null) {
                    makerChecker.setCheckerId(checker);
                }
            }
            makerCheckerService.resubmitTask(makerChecker.getId(), "ANP", policy, checkerIds);
        }
        log.info("reached the end of making rn policy ready");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Long policyRenewal(RenewalsForm renewalsForm, BulkRenewal bulkRenewal) throws BadRequestException, EndorsementsException {
        Long newPolicyCode = null;
        Long prevPol = renewalsForm.getPolicyId();
        log.info("previous policy {}", prevPol);
//        try {
            Long policyCode = renewPolicy(renewalsForm);
            System.out.println("Endorse Code "+policyCode);
            PolicyTrans rnPolicy = policyTransRepo.findOne(policyCode);
            final boolean updateRiskDates = bulkRenewal.getBulkPolicyRenewalDate()!=null && bulkRenewal.getBulkPolicyRenewalDate().after(rnPolicy.getWefDate());
            if(updateRiskDates){
                rnPolicy.setWefDate(bulkRenewal.getBulkPolicyRenewalDate());
                rnPolicy.setWetDate(dateUtils.getWetDate(bulkRenewal.getBulkPolicyRenewalDate()));
                if(rnPolicy.getProduct().isRenewable()) {
                    rnPolicy.setRenewalDate(org.apache.commons.lang.time.DateUtils.addDays(rnPolicy.getWetDate(), 1));
                }
            }
            if (!StringUtils.equalsIgnoreCase(rnPolicy.getInterfaceType(), bulkRenewal.getNewInterface())) {
                log.info("New interface from bulk renewal: {}", bulkRenewal.getNewInterface());

                // User wants to switch the payment type
                if ("Accrual".equalsIgnoreCase(bulkRenewal.getNewInterface())) {
                    rnPolicy.setInterfaceType("A");
                    // Use the value filled by the user, or fallback to "Dispensation" if blank
                    if(bulkRenewal.getAccrualPaymentType() != null){
                        rnPolicy.setAccrualPaymentType(bulkRenewal.getAccrualPaymentType());
                    }else {
                        rnPolicy.setAccrualPaymentType(StringUtils.defaultIfBlank(
                                rnPolicy.getAccrualPaymentType(), "Dispensation"
                        ));
                    }
                    if(bulkRenewal.getAccrualInstDate() != null){
                        rnPolicy.setAccrualInstDate(bulkRenewal.getAccrualInstDate());
                    }else {
                        rnPolicy.setAccrualInstDate(rnPolicy.getWefDate());
                    }
                } else if ("Cash".equalsIgnoreCase(bulkRenewal.getNewInterface())) {
                    rnPolicy.setInterfaceType("C");
                    rnPolicy.setAccrualPaymentType(null);
                    rnPolicy.setAccrualInstDate(null);
                } else {
                    throw new BadRequestException("Invalid value for payment type: " + bulkRenewal.getNewInterface());
                }
                policyTransRepo.save(rnPolicy);
            }
            List<Object[]> riskTransList = riskTransRepo.findPolicyRiskTrans(policyCode);
            for(Object[] riskTrans : riskTransList) {
                final Long riskId = ((BigInteger) riskTrans[1]).longValue();
                if(bulkRenewal.getNewPrem()!=null) {
                    riskRepo.updateRiskOverridePrem(bulkRenewal.getNewPrem(), riskId);
                }
                if(updateRiskDates){
                    riskRepo.updateRiskDates(rnPolicy.getWefDate(),rnPolicy.getWetDate(),riskId);
                }
                final List<Object[]> sectionsTrans = sectionTransRepo.findRiskSectionTrans(riskId);
                 int count = 0;
                for(Object[] sectionTrans : sectionsTrans) {
                    if(count > 0) break;
                    final Long sectionId = ((BigInteger) sectionTrans[0]).longValue();
                    final String sectType = ((String) sectionTrans[13]);
                    if(sectType.equalsIgnoreCase("SI")){
                        if(bulkRenewal.getNewSumInsured()!=null) {
                            sectionTransRepo.updateSectionSumAssured(bulkRenewal.getNewSumInsured(), sectionId);
                            count++;
                        }
                    }
                }
            }
            log.info("new polcode {}", policyCode);
            try {
                premiumService.computePrem(policyCode);
            } catch (BadRequestException|IOException e) {

            }
//            if(endorseService.getProdutGroup(policyCode)){
//                //return "redirect:/protected/medical/policies/edituwpolicy";
//            } else {
//                //return "redirect:/protected/uw/policies/edituwpolicy";
//            }


            //make the policy ready
            PolicyCreateDTO policy = new PolicyCreateDTO();
            policy.setPolicyId(policyCode);
            policy.setTransType("RN");
            policy.setPrevPolicy(prevPol);
            policy.setClientId(rnPolicy.getClient().getTenId());
            policy.setBusinessType(rnPolicy.getBusinessType());
            policy.setProdId(rnPolicy.getProduct().getProCode());
            policy.setAgentId(rnPolicy.getAgent().getAcctId());
            policy.setFrequency(rnPolicy.getFrequency());
            policy.setWefDate(rnPolicy.getWefDate());
            policy.setWetDate(rnPolicy.getWetDate());
            policy.setBranchId(rnPolicy.getBranch().getObId());
            policy.setCurrencyId(rnPolicy.getTransCurrency().getCurCode());

            //subagent
            policy.setSubAgentId(null);

            policy.setPolNo(rnPolicy.getPolNo());
            policy.setPolRevNo(rnPolicy.getPolRevNo());


            createRnPolicyMakeReady(policy, policyCode);

            newPolicyCode =  policyCode;
//        } catch(EndorsementsException | BadRequestException ex ){
//            throw new BadRequestException(ex.getMessage());
//            //            redirectAttrs.addFlashAttribute("error", ex.getMessage());
//            //            redirectAttrs.addFlashAttribute("renewalsForm", renewalsForm);
//            //            return "redirect:/protected/uw/renewals/singleren";
//        }
        return  newPolicyCode;
    }

    @Transactional(readOnly = true)
    public Long countUnauthTransactions(String policyNumber) {
        if (policyNumber == null) return 0L;
        Predicate pred = QPolicyTrans.policyTrans.polNo.eq(policyNumber)
                .and(QPolicyTrans.policyTrans.currentStatus.in("D","R")
                        .and(QPolicyTrans.policyTrans.businessType.in("N","L")));
        long count = policyTransRepo.count(pred);
        System.out.println("Count..."+count);
        return count;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Long renewPolicy(RenewalsForm renForm) throws EndorsementsException, BadRequestException {
        if (renForm.getPolicyId() == null || renForm.getPolicyId() == null)
            throw new EndorsementsException("Policy to Renew cannot be Empty....");


        PolicyTrans currentTrans = policyTransRepo.findOne(renForm.getPolicyId());

        Predicate activePred = QPolicyTrans.policyTrans.polNo.eq(currentTrans.getPolNo())
                .and(QPolicyTrans.policyTrans.currentStatus.eq("A"));

        Predicate renewalPredicate = QPolicyTrans.policyTrans.polNo.eq(currentTrans.getPolNo())
                .and(QPolicyTrans.policyTrans.transType.eq("RN"))
                .and(QPolicyTrans.policyTrans.authStatus.eq("D").or(QPolicyTrans.policyTrans.authStatus.eq("R")));



        if (countUnauthTransactions(currentTrans.getPolNo()) > 0) {
            throw new EndorsementsException(
                    "The Policy has unfinished Transaction");
        }

        if (policyTransRepo.count(activePred) > 1) {
            throw new EndorsementsException("The current Policy has an active Endorsement. Only One Active Endorsement can be allowed");
        }

        if ("SP".equalsIgnoreCase(currentTrans.getTransType()))
            throw new EndorsementsException("Renewal not Supported on Short Period Policy Transactions");

        if (currentTrans.getRenewalDate() == null)
            throw new EndorsementsException("Cannot Renew the Policy...The policy is configured to be not renewable. Contact System Administrator");

        long endorsementCount = policyTransRepo.count(QPolicyTrans.policyTrans.polNo.eq(currentTrans.getPolNo())) + 1;

        PolicyTrans destination = new PolicyTrans();
        destination.setPolTerm(currentTrans.getPolTerm());
        destination.setTotalInstalments(currentTrans.getTotalInstalments());
        destination.setWefDate(currentTrans.getRenewalDate());
        destination.setWetDate(dateUtils.getWetDate(currentTrans.getRenewalDate()));
        destination.setCoverFrom(currentTrans.getRenewalDate());
        destination.setCoverTo(dateUtils.getWetDate(currentTrans.getRenewalDate()));
        destination.setRenewalDate(DateUtils.addDays(dateUtils.getWetDate(currentTrans.getRenewalDate()), 1));
        destination.setUwYear(dateUtils.getUwYear(currentTrans.getRenewalDate()));
        destination.setPolRevStatus("RN");
        if (currentTrans.getRevisionFormat() == null) {
            destination.setPolRevNo(currentTrans.getPolRevNo().substring(0, currentTrans.getPolRevNo().indexOf("/")) + "/" + endorsementCount);
        } else
            destination.setPolRevNo(currentTrans.getRevisionFormat() + "/" + endorsementCount);
        destination.setPolNo(currentTrans.getPolNo());
        destination.setAgent(currentTrans.getAgent());
        destination.setAuthStatus("D");
        destination.setBinder(currentTrans.getBinder());
        destination.setBranch(currentTrans.getBranch());
        destination.setClient(currentTrans.getClient());
        destination.setClientPolNo(currentTrans.getClientPolNo());
        destination.setCommAllowed(currentTrans.isCommAllowed());
        destination.setCreatedUser(userUtils.getCurrentUser());
        destination.setCurrentStatus("D");
        destination.setFrequency(currentTrans.getFrequency());
        destination.setInterfaceType(currentTrans.getInterfaceType());
        destination.setOldpolNo(currentTrans.getOldpolNo());
        destination.setPaymentMode(currentTrans.getPaymentMode());
        destination.setPolCreateddt(new Date());
        destination.setPreviousTrans(currentTrans);
        destination.setProduct(currentTrans.getProduct());
        destination.setRenewable(currentTrans.isRenewable());
        destination.setBusinessType(currentTrans.getBusinessType());
        destination.setRevisionFormat(currentTrans.getRevisionFormat());
        destination.setSubAgent(currentTrans.getSubAgent());

        destination.setTransCurrency(currentTrans.getTransCurrency());
        destination.setTransType("RN");

        SystemTrans transaction = new SystemTrans();
        transaction.setDoneDate(new Date());
        transaction.setDoneBy(userUtils.getCurrentUser());
        transaction.setPolicy(destination);
        transaction.setTransLevel("U");
        transaction.setTransCode("APD"); //A way to setup and look up for transaction transcode
        transaction.setTransAuthorised("N");
        boolean medicalProduct = false;
        ProductsDef productsDef = currentTrans.getProduct();
        if (productsDef.getProGroup().getPrgType() == null || !productsDef.getProGroup().getPrgType().equalsIgnoreCase("MD")) {
            medicalProduct = false;
        } else if (productsDef.getProGroup().getPrgType().equalsIgnoreCase("MD")) {
            medicalProduct = true;
            destination.setMedicalCoverType(currentTrans.getMedicalCoverType());
            destination.setBinCardType(currentTrans.getBinCardType());

        }
        Iterable<MedicalCategory> categories = categoryRepo.findAll(QMedicalCategory.medicalCategory.policy.policyId.eq(currentTrans.getPolicyId()));
        List<MedicalCategory> newCategories = new ArrayList<>();
        List<CategoryMembers> newMembers = new ArrayList<>();
        List<CategoryMemberBenefits> memBenefits = new ArrayList<>();
        Iterable<SelfFundParams> selfFunds = selfFundParamsRepo.findAll(QSelfFundParams.selfFundParams.policyTrans.policyId.eq(currentTrans.getPolicyId()));
        List<SelfFundParams> newSelfunds = new ArrayList<>();
        Iterable<CategoryRules> rules = rulesRepo.findAll(QCategoryRules.categoryRules.category.policy.policyId.eq(currentTrans.getPolicyId()));
        List<CategoryRules> newRules = new ArrayList<>();
        Iterable<MedCategoryBenefits> benefits = benefitRepo.findAll(QMedCategoryBenefits.medCategoryBenefits.category.policy.policyId.eq(currentTrans.getPolicyId()));
        List<MedCategoryBenefits> newBenefits = new ArrayList<>();

        Iterable<CategoryExclusions> exclusions = exclusionsRepo.findAll(QCategoryExclusions.categoryExclusions.category.policy.policyId.eq(currentTrans.getPolicyId()));
        List<CategoryExclusions> newExclusions = new ArrayList<>();

        Iterable<CategoryLoadings> loadings = loadingRepo.findAll(QCategoryLoadings.categoryLoadings.category.policy.policyId.eq(currentTrans.getPolicyId()));
        List<CategoryLoadings> newLoadings = new ArrayList<>();

        Iterable<CatalogueProviders> providers = providersRepo.findAll(QCatalogueProviders.catalogueProviders.category.policy.policyId.eq(currentTrans.getPolicyId()));
        List<CatalogueProviders> newProviders = new ArrayList<>();
        if (medicalProduct) {

            for (MedicalCategory category : categories) {
                MedicalCategory newCategory = new MedicalCategory();
                newCategory.setBedCost(category.getBedCost());
                newCategory.setBedTypes(category.getBedTypes());
                newCategory.setBinderDetails(category.getBinderDetails());
                newCategory.setDesc(category.getDesc());
                newCategory.setShtDesc(category.getShtDesc());
                newCategory.setLoadingFactor(category.getLoadingFactor());
                newCategory.setLoadingPrem(null);
                newCategory.setPolicy(destination);
                newCategory.setPremium(null);
                newCategories.add(newCategory);
                Iterable<CategoryMembers> categoryMembers = memberRepo.findAll(QCategoryMembers.categoryMembers.category.id.eq(category.getId())
                        .and(QCategoryMembers.categoryMembers.memberStatus.equalsIgnoreCase("A")));
                for (MedCategoryBenefits benefit : benefits) {
                    MedCategoryBenefits newBenefit = new MedCategoryBenefits();
                    newBenefit.setApplicableAt(benefit.getApplicableAt());
                    newBenefit.setWaitPeriod(benefit.getWaitPeriod());
                    newBenefit.setCategory(newCategory);
                    newBenefit.setFundLimit(benefit.getFundLimit());
                    newBenefit.setCover(benefit.getCover());
                    newBenefit.setLimit(benefit.getLimit());
                    newBenefits.add(newBenefit);
                }
                for (CategoryMembers member : categoryMembers) {
                    CategoryMembers newMember = new CategoryMembers();
                    newMember.setCategory(newCategory);
                    newMember.setChildType(member.getChildType());
                    newMember.setClient(member.getClient());
                    newMember.setMainClient(member.getMainClient());
                    newMember.setDependentTypes(member.getDependentTypes());
                    newMember.setMemberShipNo(member.getMemberShipNo());
                    newMember.setWefDate(currentTrans.getRenewalDate());
                    newMember.setWetDate(dateUtils.getWetDate(currentTrans.getRenewalDate()));
                    newMember.setMemberStatus("R");
                    newMember.setPrevsectId(member.getSectId());
                    newMembers.add(newMember);


                }
                for (CategoryRules rule : rules) {
                    CategoryRules newRule = new CategoryRules();
                    newRule.setBinderRules(rule.getBinderRules());
                    newRule.setCategory(newCategory);
                    newRule.setDesc(rule.getDesc());
                    newRule.setShtDesc(rule.getShtDesc());
                    newRule.setValue(rule.getValue());
                    newRules.add(newRule);
                }

                for (CategoryExclusions exclusion : exclusions) {
                    CategoryExclusions newExlusion = new CategoryExclusions();
                    newExlusion.setCategory(newCategory);
                    newExlusion.setAilment(exclusion.getAilment());
                    newExclusions.add(newExlusion);
                }
                for (CategoryLoadings loading : loadings) {
                    CategoryLoadings newloading = new CategoryLoadings();
                    newloading.setAilment(loading.getAilment());
                    newloading.setCategory(newCategory);
                    newloading.setLoadingAmt(loading.getLoadingAmt());
                    newloading.setRate(loading.getRate());
                    newloading.setRateType(loading.getRateType());
                    newLoadings.add(newloading);
                }
                for (CatalogueProviders provider : providers) {
                    CatalogueProviders newProvider = new CatalogueProviders();
                    newProvider.setCategory(newCategory);
                    newProvider.setProviders(provider.getProviders());
                    newProviders.add(newProvider);
                }

            }

            for (SelfFundParams selfFundParam : selfFunds) {
                SelfFundParams newSelfFund = new SelfFundParams();
                newSelfFund.setApplicableLevel(selfFundParam.getApplicableLevel());
                newSelfFund.setApplicableValue(selfFundParam.getApplicableValue());
                newSelfFund.setBillingFrequency(selfFundParam.getBillingFrequency());
                newSelfFund.setFundResetAmount(selfFundParam.getFundResetAmount());
                newSelfFund.setFundDepositAmount(selfFundParam.getFundDepositAmount());
                newSelfFund.setMinDeposit(selfFundParam.getMinDeposit());
                newSelfFund.setPolicyTrans(destination);
                newSelfFund.setCarryForwardBalances(selfFundParam.isCarryForwardBalances());
                newSelfFund.setDeductAdminFeeFromFund(selfFundParam.isDeductAdminFeeFromFund());
                newSelfFund.setPayWhenBenefitExhausted(selfFundParam.isPayWhenBenefitExhausted());
                newSelfFund.setPayWhenFundExhausted(selfFundParam.isPayWhenFundExhausted());
                newSelfFund.setSelfFundBalance(selfFundParam.getSelfFundBalance());
                newSelfunds.add(newSelfFund);

            }


        }
        Iterable<PolicyTaxes> taxes = polTaxesRepo.findAll(QPolicyTaxes.policyTaxes.policy.policyId.eq(currentTrans.getPolicyId()));
        List<PolicyTaxes> newTaxes = new ArrayList<>();
        for (PolicyTaxes tax : taxes) {
            if (tax.getRevenueItems().getItem() == RevenueItems.SD)
                continue;

            PolicyTaxes newTax = new PolicyTaxes();
            newTax.setDivFactor(tax.getDivFactor());
            newTax.setPolicy(destination);
            newTax.setRateType(tax.getRateType());
            newTax.setRevenueItems(tax.getRevenueItems());
            newTax.setSubclass(tax.getSubclass());
            newTax.setTaxLevel(tax.getTaxLevel());
            newTax.setTaxRate(tax.getTaxRate());
            newTaxes.add(newTax);
        }

        Iterable<PolicyClauses> clauses = polClausesRepo.findAll(QPolicyClauses.policyClauses.policy.policyId.eq(currentTrans.getPolicyId()));
        List<PolicyClauses> newClauses = new ArrayList<>();
        for (PolicyClauses clause : clauses) {
            PolicyClauses newClause = new PolicyClauses();
            newClause.setClauHeading(clause.getClauHeading());
            newClause.setClause(clause.getClause());
            newClause.setClauWording(clause.getClauWording());
            newClause.setEditable(clause.isEditable());
            newClause.setNewClause("N");
            newClause.setPolicy(destination);
            newClauses.add(newClause);
        }
        Iterable<PolicyActiveRisks> activeRisks = activeRisksRepo.findAll(QPolicyActiveRisks.policyActiveRisks.policy.policyId.eq(renForm.getPolicyId()));
        List<PolicyActiveRisks> newActiveRisks = new ArrayList<>();
        for (PolicyActiveRisks activeRisk : activeRisks) {
            PolicyActiveRisks newActiveRisk = new PolicyActiveRisks();
            newActiveRisk.setPolicy(destination);
            newActiveRisk.setRisk(activeRisk.getRisk());
            newActiveRisk.setRiskIdentifier(activeRisk.getRiskIdentifier());
            newActiveRisks.add(newActiveRisk);
        }
        currentTrans.setRenewed(true);
        policyTransRepo.save(currentTrans);
        PolicyTrans savedPolicy = policyTransRepo.save(destination);

        polClausesRepo.save(newClauses);
        if (!newCategories.isEmpty()) {
            categoryRepo.save(newCategories);
            Iterable<CategoryMembers> savedMems = new ArrayList<>();
            if (!newMembers.isEmpty()) {
                savedMems = memberRepo.save(newMembers);
            }
            if (!newSelfunds.isEmpty()) {
                selfFundParamsRepo.save(newSelfunds);
            }
            if (!newRules.isEmpty()) {
                rulesRepo.save(newRules);
            }
            if (!newBenefits.isEmpty()) {
                Iterable<MedCategoryBenefits> savedBens = benefitRepo.save(newBenefits);
                for (CategoryMembers newMember : savedMems) {
                    for (MedCategoryBenefits ben : savedBens) {
                        Iterable<CategoryMemberBenefits> prevMemBenefits = memberBenefitsRepo.findAll(QCategoryMemberBenefits.categoryMemberBenefits.member.sectId.eq(newMember.getPrevsectId())
                                .and(QCategoryMemberBenefits.categoryMemberBenefits.benefit.status.notEqualsIgnoreCase("D"))
                                .and(QCategoryMemberBenefits.categoryMemberBenefits.benefit.cover.id.eq(ben.getCover().getId())));
                        for (CategoryMemberBenefits benefit : prevMemBenefits) {
                            CategoryMemberBenefits memBenefit = new CategoryMemberBenefits();
                            memBenefit.setComputedPremium(BigDecimal.ZERO);
                            memBenefit.setMember(newMember);
                            memBenefit.setBenefit(ben);
                            memBenefit.setPrevPremium(BigDecimal.ZERO);
                            memBenefit.setPrevUnitPremium(BigDecimal.ZERO);
                            memBenefit.setPremium(BigDecimal.ZERO);
                            memBenefit.setWefDate(savedPolicy.getWefDate());
                            memBenefit.setWetDate(savedPolicy.getWetDate());
                            memBenefit.setPrevcmbId(benefit.getCmbId());
                            memBenefits.add(memBenefit);
                        }
                    }
                }
            }

            if (!memBenefits.isEmpty()) {
                memberBenefitsRepo.save(memBenefits);
            }
            if (!newExclusions.isEmpty()) {
                exclusionsRepo.save(newExclusions);
            }
            if (!newLoadings.isEmpty()) {
                loadingRepo.save(newLoadings);
            }
            if (!newProviders.isEmpty()) {
                providersRepo.save(newProviders);
            }
        }
        polTaxesRepo.save(newTaxes);
        activeRisksRepo.save(newActiveRisks);
        transRepo.save(transaction);
        Iterable<PolicyActiveRisks> endorsedRisks = activeRisksRepo.findAll(QPolicyActiveRisks.policyActiveRisks.policy.policyId.eq(savedPolicy.getPolicyId()));
        for (PolicyActiveRisks activeRisk : endorsedRisks) {

                endorseRisk(activeRisk.getArId(), "R", renForm.getPolicyId());

        }
        workflowService.startNewWorkFlow(DocType.GEN_UW_DOCUMENT, String.valueOf(savedPolicy.getPolicyId()), savedPolicy, "N", null, null, null, null);
        return savedPolicy.getPolicyId();
    }

    @Override
    public List<Long> bulkDeleteRenewals(List<Long> bulkIds){
        for(Long bulkId : bulkIds) {
            BulkRenewal bulkPolrnCreation = bulkRenewalRepo.findByBulkId(bulkId);
            bulkRenewalRepo.delete(bulkPolrnCreation);
        }
        return bulkIds;
    }

    @Override
    public List<Long> bulkDelProcessedRNPolicies(List<Long> bulkIds) throws BadRequestException{
        for(Long bulkId : bulkIds) {
            BulkRenewal bulkPolrnCreation = bulkRenewalRepo.findByBulkId(bulkId);

            if (bulkPolrnCreation == null) {
                System.out.println("No BulkPolicyRnCreation found for ID: " + bulkId);
                continue;
            }
            //check if processed
            if (!"Y".equalsIgnoreCase(bulkPolrnCreation.getPolicyProcessed())) {
                System.out.println("Transaction not marked as processed for ID: " + bulkId);
                continue;
            }
            uploadValidatorsUtils.deleteprocessedPol(bulkPolrnCreation.getNewPolId());
            bulkRenewalRepo.delete(bulkPolrnCreation);

        }
        return bulkIds;
    }

    @Override
    public void bulkAuthorizeRenewals(List<Long> bulkIds) throws BadRequestException {
        for(Long bulkId : bulkIds) {
            BulkRenewal bulkPolrnCreation = bulkRenewalRepo.findByBulkId(bulkId);
            Long polCode = bulkPolrnCreation.getNewPolId();
            BigDecimal refundAmt = BigDecimal.ZERO;

            //start the auth for accrual
            //authService.authorizePolicy(polCode,refundAmt);
            authService.authorizePolicy(polCode, userUtils.getCurrentUser());

            //cleanup auth
            bulkPolrnCreation.setTransProcessed("Y");
            bulkRenewalRepo.save(bulkPolrnCreation);
        }
    }

    @Override
    public void bulkRejectRenewals(List<Long> bulkIds) throws BadRequestException {
        for(Long bulkId : bulkIds) {
            BulkRenewal bulkPolrnCreation = bulkRenewalRepo.findByBulkId(bulkId);
            Long polCode = bulkPolrnCreation.getNewPolId();

            //start the auth for accrual
            //policyService.undoProposalConversion(polCode, reasonId, reason);

            //cleanup auth
            bulkPolrnCreation.setTransProcessed("Y");
            bulkRenewalRepo.save(bulkPolrnCreation);
        }
    }

    //==========================================
    // bulk upload policy CANCELLATIONS
    //==========================================
    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> uploadBulkCNPolExcel(MultipartFile file) throws BadRequestException {
        String batchId = UUID.randomUUID().toString();

        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            throw new BadRequestException("Upload files with .xlsx or .xls extension only");
        }

        Map<String, Object> response = new HashMap<>();
        List<String> invalidRecords = new ArrayList<>();
        List<BulkPolicyCreation> bulkPolicyCreations = new ArrayList<>();
        List<BulkPolicyRisk> bulkPolicyRisks = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);

            // Validate template format
            //            List<String> validationErrors = ExcelReaderUtil.validateBulkPolicyTemplate(workbook);
            //            if (!validationErrors.isEmpty()) {
            //                StringBuilder errorMessage = new StringBuilder("Template validation failed: ");
            //                for (String error : validationErrors) {
            //                    errorMessage.append(error).append("; ");
            //                }
            //                throw new BadRequestException(errorMessage.toString());
            //            }

            Sheet sheetOne = workbook.getSheetAt(0);
            Map<String, BulkPolicyCreation> policyMap = new HashMap<>(); // Maps serialNo -> BulkPolicyCreation

            int startRowIndex = findDataStartRow(sheetOne);
            if (startRowIndex == -1) {
                throw new BadRequestException("Sheet One contains no data.");
            }
            int totalSuccessCount = 0;
            int totalErrorCount = 0;
            for (int rowNum = startRowIndex; rowNum <= sheetOne.getLastRowNum(); rowNum++) {
                Row row = sheetOne.getRow(rowNum);
                if (isBlankRow(row)) continue;

                try {
                    //save the policy upload and validate
                    BulkPolCnCreation bulkPolCnCreation = new BulkPolCnCreation();

                    String polNo = ExcelReaderUtil.getCellValue(row, 0); //getCellStringValue(row.getCell(3));
                    BigDecimal cancelationAmt = ExcelReaderUtil.parseBigDecimalSafe(ExcelReaderUtil.getCellValue(row, 1)); //getCellStringValue(row.getCell(3));
                    Date effectiveDate = ExcelReaderUtil.parseFlexibleDate(ExcelReaderUtil.getCellValue(row, 2)); //getCellStringValue(row.getCell(3));
                    System.out.println(ExcelReaderUtil.getCellValue(row, 2));
                    System.out.println(effectiveDate);
                    //PolicyTrans policyTrans = policyTransRepo.findBulkProcessPolicytransProposalAndPolNo(polNo, polProposalNo);
                    PolicyTrans policyTrans = policyTransRepo.findActiveNonNNNBulkProcess(polNo);
                    if (policyTrans == null) {
                        throw new IllegalArgumentException("Policy " + polNo +"not found");
                    }

                    Date wef = policyTrans.getCoverFrom();
                    Date wet = policyTrans.getCoverTo();
                        bulkPolCnCreation.setBatchId(batchId);
                        bulkPolCnCreation.setPolicyId(policyTrans.getPolicyId());
                        bulkPolCnCreation.setCancelationAmt(cancelationAmt);
                        bulkPolCnCreation.setPolNo(policyTrans.getPolNo());
                        // bulkPolEnCreation.setBulkPolicyId(policyTrans.getRiskTrans());

                        bulkPolCnCreation.setEffectiveDate(effectiveDate);
                        bulkPolCnCreation.setSumInsured(policyTrans.getSumInsured());
                        bulkPolCnCreation.setPremium(policyTrans.getPremium());
                        bulkPolCnCreation.setClientFname(policyTrans.getClient().getFname()+" "+policyTrans.getClient().getOtherNames());
                        bulkPolCnCreation.setPolicyCurrentStatus(policyTrans.getTransType());
                        bulkPolCnCreation.setTransProcessed("N");
                        bulkPolCnCreation.setTransAuthorized("N");
                        bulkPolCnCreation.setUploadedBy(userUtils.getCurrentUser());
                        bulkPolCnCreation.setDateUploaded(new Date());
                        bulkPolCnCreation.setProductGroup(policyTrans.getProduct().getProGroup().getPrgType());

                        bulkPolicyCnCreationRepo.save(bulkPolCnCreation);
                        totalSuccessCount++;
                } catch (Exception e) {
                  //  throw new BadRequestException("Sheet One, Row " + (rowNum + 1) + ": " + e.getMessage());
                    BulkRenewalError error = new BulkRenewalError();
                    error.setRowNumber(rowNum + 1);
                    error.setPolicyNumber(ExcelReaderUtil.getCellValue(row, 0));
                    error.setProposalNumber(ExcelReaderUtil.getCellValue(row, 1));
                    error.setErrorMessage(e.getMessage());
                    error.setBatchId(batchId);
                    bulkRenewalErrorRepo.save(error);
                    totalErrorCount++;
                }
            }


            response.put("status", "completed");
            response.put("successCount", totalSuccessCount);
            response.put("errorCount", totalErrorCount);
            response.put("batchId", batchId);
            response.put("message", "Upload completed."); //successfulPolicies

            return response;

        } catch (Exception e) {
            throw new BadRequestException("Error processing the Excel file: " + e.getMessage());
        }
    }

    private Long policyCancellation (RevisionForm revisionForm, boolean isLife) throws BadRequestException {
        log.warn("revision for cancellation {}", revisionForm);
        Long policyCode = null;
        if(!isLife) { //general
            try {
                System.out.println("canceling general policy ");
                policyCode = endorseService.reviseTransaction(revisionForm);
                if ("CN".equalsIgnoreCase(revisionForm.getRevisionType())) {

                    final Iterable<RiskTrans> riskTrans = riskTransRepo.getRiskDetails(policyCode);

                    int counter = 0;
                    for (RiskTrans cuurRisk : riskTrans) {
                        if (counter > 0) break;
                        cuurRisk.setButchargePrem(BigDecimal.valueOf(revisionForm.getAmount().abs().doubleValue() * -1));
                        riskTransRepo.save(cuurRisk);
                        counter++;
                    }

//                    try {
//                        //premiumService.computeCancelPrem(policyCode);
//                    } catch (BadRequestException e) {
//                        log.info("{}, {}", e, e.getMessage());
//                    }
                }
            } catch (EndorsementsException ex) {
                log.info(" {}, {}", ex, ex.getMessage());
                throw new BadRequestException(" error " + ex.getMessage());
            }
        }else{ //life
            try {
                System.out.println("canceling life policy ");
                policyCode = lifeEndorseService.reviseTransaction(revisionForm);

                if ("CN".equalsIgnoreCase(revisionForm.getRevisionType())) {
//                    try {
//                        //premiumService.computeCancelPrem(policyCode);
//                    } catch (BadRequestException e) {
//                        log.info("{}, {}", e, e.getMessage());
//                    }
                }
            } catch(EndorsementsException ex ){
                log.info(" {}, {}", ex, ex.getMessage());
                throw new BadRequestException(" error " + ex.getMessage());
            }
        }
        return policyCode;
    }


    public void submitPolicyCancellation(PolicyCancellationDTO dto) throws BadRequestException {
        //save the remarks
//        PolicyRemarks remarks = new PolicyRemarks();
//        remarks.setRemarks("CANCELLATION");
//        dto.setRemarks("CANCELLATION");
//        EndorsementRemarks endRemark = new EndorsementRemarks();
//        endRemark.setRemarkId(21809L);
//        remarks.setEndRemarks(endRemark);
//
//        PolicyTrans policy = new PolicyTrans();
//        policy.setPolicyId(dto.getPolicyId());
//        remarks.setPolicy(policy);
//        policyService.saveEndorsementRemarks(remarks);

        //submit the cancellation
        /*
        {"policyId":"6417","remarks":"CANCELLATION","cancelReasonId":"21809"}
         */
        authService.submitPolicyCancellation(dto, false);
    }

    @Override
    public List<Long> bulkProcessCNPolicies(List<Long> bulkIds, boolean isApproved) throws BadRequestException {
        for(Long bulkId : bulkIds){
            System.out.println("Bulk Id..."+bulkId);
            BulkPolCnCreation bulkPolCnCreation = bulkPolicyCnCreationRepo.findByBulkCNId(bulkId);
            RevisionForm revisionForm = new RevisionForm();
            revisionForm.setPolicyId(bulkPolCnCreation.getPolicyId());
            revisionForm.setEffectiveDate(bulkPolCnCreation.getEffectiveDate());
            revisionForm.setRevisionType("CN");
            revisionForm.setRemarks("CANCELLATION");
            revisionForm.setAmount(bulkPolCnCreation.getCancelationAmt());

            log.info("{}", revisionForm);
            //CHECK FOR UNFINISHED TRANSACTIONS
            Long pendingTransaction = endorseService.countUnauthTransactions(bulkPolCnCreation.getPolNo());
            System.out.println("pending transaction"+pendingTransaction);
            if(pendingTransaction != null && pendingTransaction > 0) {
                System.out.println("Deleting existing pending transactions.");
                List<Object[]> policiesList = policyTransRepo.findPendingTransactions(bulkPolCnCreation.getPolNo(), 0, 10);
                for (Object[] policy : policiesList) {
                    Long policyId = (((BigInteger) policy[5]).longValue());
                    System.out.println("Starting Deleting policed "+policyId);
                    endorseService.deletePolicyRecord(policyId,false);//
                    System.out.println("Completed Deleting policed "+policyId);
                }
            }

            //do the cancellation action
            Long cnPolCode = null;
            if(bulkPolCnCreation.getProductGroup() != null && bulkPolCnCreation.getProductGroup().equalsIgnoreCase("F")) {
                //authorize general policy
                cnPolCode = policyCancellation(revisionForm, false);
            }else{
                //life
                cnPolCode = policyCancellation(revisionForm, true);

            }
            if(cnPolCode != null) {
                //submit for authorization
                PolicyCancellationDTO dto = new PolicyCancellationDTO();
                dto.setPolicyId(cnPolCode);
                dto.setRefundAmount(bulkPolCnCreation.getCancelationAmt());
                dto.setRemarks("CANCELLATION");

                //submitPolicyCancellation(dto);
                //authService.submitPolicyCancellation(dto, false);
                authService.submitBulkPolicyCancellation(dto, false);

                bulkPolCnCreation.setBulkCnPolId(cnPolCode);
                bulkPolCnCreation.setTransProcessed("Y");
                bulkPolCnCreation.setTransAuthorized("N");
                bulkPolicyCnCreationRepo.save(bulkPolCnCreation);
            }
        }
        return bulkIds;
    }

    @Override
    public DataTablesResult<BulkPolicyCreationDTO> unProcessedBulkCNPol(DataTablesRequest request) {
        List<Object[]> unProcessedBulkPols = bulkPolicyCnCreationRepo.findUnprocessedBulkPol((request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%", request.getPageNumber(), request.getPageSize());
        final List<BulkPolicyCreationDTO> unProcessed = new ArrayList<>();
        long rowCount = 0L;
        if (!unProcessedBulkPols.isEmpty()) rowCount = ((BigInteger) unProcessedBulkPols.get(0)[10]).intValue();
//        if (!unProcessedBulkPols.isEmpty()) rowCount = ((Number) unProcessedBulkPols.get(0)[10]).longValue();

        for (Object[] unProcessedBulkPol : unProcessedBulkPols) {
            BulkPolicyCreationDTO bulkPolicyCreationDTO = new BulkPolicyCreationDTO();
            bulkPolicyCreationDTO.setBulkPolicyId(((BigInteger) unProcessedBulkPol[0]).longValue());
            bulkPolicyCreationDTO.setPolNumber((String) unProcessedBulkPol[1]);
            bulkPolicyCreationDTO.setClientOtherNames((String) unProcessedBulkPol[2]); //proposal
            bulkPolicyCreationDTO.setPolicyStatus((String) unProcessedBulkPol[3]);
            bulkPolicyCreationDTO.setSumInsured((BigDecimal) unProcessedBulkPol[4]);
            bulkPolicyCreationDTO.setPremium((BigDecimal) unProcessedBulkPol[5]);
            bulkPolicyCreationDTO.setClientFname((String) unProcessedBulkPol[6]);
            bulkPolicyCreationDTO.setCancelationAmt((BigDecimal) unProcessedBulkPol[7]);
            bulkPolicyCreationDTO.setEffectiveDate((Date) unProcessedBulkPol[8]);
            bulkPolicyCreationDTO.setDateUploaded((Date) unProcessedBulkPol[9]);
            unProcessed.add(bulkPolicyCreationDTO);
        }
        Page<BulkPolicyCreationDTO> page = new PageImpl<>(unProcessed, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<BulkPolicyCreationDTO> viewBulkCNPolicies(DataTablesRequest request) {
        List<Object[]> viewBulkPolicies = bulkPolicyCnCreationRepo.viewUnprocessedBulkPol((request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%", request.getPageNumber(), request.getPageSize());
        final List<BulkPolicyCreationDTO> unProcessed = new ArrayList<>();
        long rowCount = 0L;
        if (!viewBulkPolicies.isEmpty()) rowCount = ((BigInteger) viewBulkPolicies.get(0)[12]).intValue();

        for (Object[] unProcessedBulkPol : viewBulkPolicies) {
            BulkPolicyCreationDTO bulkPolicyCreationDTO = new BulkPolicyCreationDTO();
            bulkPolicyCreationDTO.setBulkPolicyId(((BigInteger) unProcessedBulkPol[0]).longValue());
            bulkPolicyCreationDTO.setPolNumber((String) unProcessedBulkPol[1]);
            bulkPolicyCreationDTO.setClientOtherNames((String) unProcessedBulkPol[2]); //proposal
            bulkPolicyCreationDTO.setPolicyStatus((String) unProcessedBulkPol[3]);
            bulkPolicyCreationDTO.setSumInsured((BigDecimal) unProcessedBulkPol[4]);
            bulkPolicyCreationDTO.setPremium((BigDecimal) unProcessedBulkPol[5]);
            bulkPolicyCreationDTO.setClientFname((String) unProcessedBulkPol[6]);
            bulkPolicyCreationDTO.setDateUploaded((Date) unProcessedBulkPol[7]);
            unProcessed.add(bulkPolicyCreationDTO);
        }
        Page<BulkPolicyCreationDTO> page = new PageImpl<>(unProcessed, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public List<Long> bulkAuthorizeCancellation(List<Long> bulkIds) throws BadRequestException {
        for(Long bulkId : bulkIds) {
            BulkPolCnCreation bulkPolCnCreation = bulkPolicyCnCreationRepo.findByBulkCNId(bulkId);
            System.out.println(
                    "bulk id "+ bulkId + "old pol code "+ bulkPolCnCreation.getPolicyId()+" new cn pol"+bulkPolCnCreation.getBulkCnPolId()
            );
            Long polCode = bulkPolCnCreation.getBulkCnPolId();
            if(bulkPolCnCreation.getProductGroup() != null && bulkPolCnCreation.getProductGroup().equalsIgnoreCase("F")) {
                //authorize general policy
                //authService.authorizePolicy(bulkPolCnCreation.getBulkCnPolId(), bulkPolCnCreation.getRefundAmt());
                authService.authorizePolicy(polCode, bulkPolCnCreation.getRefundAmt(), true);
            } else {
                //authorize life policy
                authService.authorizeLifePolicy(polCode);
            }

            bulkPolCnCreation.setTransAuthorized("Y");
            bulkPolicyCnCreationRepo.save(bulkPolCnCreation);
        }
        return bulkIds;
    }


    @Override
    public List<Long> bulkDeleteCancellation(List<Long> bulkIds){
        for(Long bulkId : bulkIds) {
            BulkPolCnCreation bulkPolCnCreation = bulkPolicyCnCreationRepo.findByBulkCNId(bulkId);
            bulkPolicyCnCreationRepo.delete(bulkPolCnCreation);
        }
        return bulkIds;
    }

    @Override
    public List<Long> bulkDelProcessedCNPolicies(List<Long> bulkIds) throws BadRequestException{
        for(Long bulkId : bulkIds) {
            BulkPolCnCreation bulkPolCnCreation = bulkPolicyCnCreationRepo.findByBulkCNId(bulkId);

            if (bulkPolCnCreation == null) {
                System.out.println("No BulkPolicyCnCreation found for ID: " + bulkId);
                continue;
            }

            if (!"Y".equalsIgnoreCase(bulkPolCnCreation.getTransProcessed())) {
                System.out.println("Transaction not marked as processed for ID: " + bulkId);
                continue;
            }
            uploadValidatorsUtils.deleteprocessedPol(bulkPolCnCreation.getBulkCnPolId());

            bulkPolicyCnCreationRepo.delete(bulkPolCnCreation);
//            bulkPolicyCnCreationRepo.deleteByBulkCNId(bulkId);
        }
        return bulkIds;
    }


    //============================
    //refunds
    //===========================
    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> uploadBulkRefundPolExcel(MultipartFile file) throws BadRequestException {
        String batchId = UUID.randomUUID().toString();

        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            throw new BadRequestException("Upload files with .xlsx or .xls extension only");
        }

        Map<String, Object> response = new HashMap<>();
        List<String> invalidRecords = new ArrayList<>();
        List<BulkPolicyCreation> bulkPolicyCreations = new ArrayList<>();
        List<BulkPolicyRisk> bulkPolicyRisks = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);

            // Validate template format
            //            List<String> validationErrors = ExcelReaderUtil.validateBulkPolicyTemplate(workbook);
            //            if (!validationErrors.isEmpty()) {
            //                StringBuilder errorMessage = new StringBuilder("Template validation failed: ");
            //                for (String error : validationErrors) {
            //                    errorMessage.append(error).append("; ");
            //                }
            //                throw new BadRequestException(errorMessage.toString());
            //            }

            Sheet sheetOne = workbook.getSheetAt(0);
            Map<String, BulkPolicyCreation> policyMap = new HashMap<>(); // Maps serialNo -> BulkPolicyCreation

            int startRowIndex = findDataStartRow(sheetOne);
            if (startRowIndex == -1) {
                throw new BadRequestException("Sheet One contains no data.");
            }
            int totalSuccessCount = 0;
            int totalErrorCount = 0;
            for (int rowNum = startRowIndex; rowNum <= sheetOne.getLastRowNum(); rowNum++) {
                Row row = sheetOne.getRow(rowNum);
                if (isBlankRow(row)) continue;

                try {
                    //save the policy upload and validate
                    BulkRefunds bulkRefunds = new BulkRefunds();

                    String polNo = ExcelReaderUtil.getCellValue(row, 0); //getCellStringValue(row.getCell(3));
                    //BigDecimal refundAmt = ExcelReaderUtil.parseBigDecimalSafe(ExcelReaderUtil.getCellValue(row, 1)); //getCellStringValue(row.getCell(3));
                    String polRevNo = ExcelReaderUtil.getCellValue(row, 1); //getCellStringValue(row.getCell(3));

                    //PolicyTrans policyTrans = policyTransRepo.findBulkProcessPolicytransProposalAndPolNo(polNo, polProposalNo);
                    PolicyTrans policyTrans = bulkRefundsCreationRepo.findByPolNo(polNo, polRevNo);
                    if (policyTrans == null) {
                        throw new IllegalArgumentException("Policy " + polNo +"not found");
                    }

                    Date wef = policyTrans.getCoverFrom();
                    Date wet = policyTrans.getCoverTo();
                    bulkRefunds.setBatchId(batchId);
                    //bulkRefunds.setRefundAmount(refundAmt);
                    bulkRefunds.setOPolId(policyTrans.getPolicyId());
                    bulkRefunds.setPolNo(policyTrans.getPolNo());
                    bulkRefunds.setPolProposalNo(policyTrans.getPolRevNo());
                    bulkRefunds.setSumInsured(policyTrans.getSumInsured());
                    bulkRefunds.setBasicPremium(policyTrans.getPremium());
                    bulkRefunds.setPolicyRefundableAmount(policyTrans.getRefundablePremium());
                    bulkRefunds.setBulkPolicyWef(wef);
                    bulkRefunds.setBulkPolicyWet(wet);
                    bulkRefunds.setClientFname(policyTrans.getClient().getFname()+" "+policyTrans.getClient().getOtherNames());
                    bulkRefunds.setTransType(policyTrans.getTransType());
                    bulkRefunds.setPolicyProcessed("N");
                    bulkRefunds.setTransAuthorized("N");
                    bulkRefunds.setUploadedBy(userUtils.getCurrentUser());
                    bulkRefunds.setBulkPolicyDateUploaded(new Date());

                    bulkRefundsCreationRepo.save(bulkRefunds);
                    totalSuccessCount++;
                } catch (Exception e) {
                    //  throw new BadRequestException("Sheet One, Row " + (rowNum + 1) + ": " + e.getMessage());
                    BulkRenewalError error = new BulkRenewalError();
                    error.setRowNumber(rowNum + 1);
                    error.setPolicyNumber(ExcelReaderUtil.getCellValue(row, 0));
                    error.setProposalNumber(ExcelReaderUtil.getCellValue(row, 2));
                    error.setErrorMessage(e.getMessage());
                    error.setBatchId(batchId);
                    bulkRenewalErrorRepo.save(error);
                    totalErrorCount++;
                }
            }


            response.put("status", "completed");
            response.put("successCount", totalSuccessCount);
            response.put("errorCount", totalErrorCount);
            response.put("batchId", batchId);
            response.put("message", "Upload completed."); //successfulPolicies

            return response;

        } catch (Exception e) {
            throw new BadRequestException("Error processing the Excel file: " + e.getMessage());
        }
    }


    @Override
    public DataTablesResult<BulkPolicyCreationDTO> unProcessedBulkRefundPol(DataTablesRequest request) {
        List<Object[]> unProcessedBulkPols = bulkRefundsCreationRepo.findUnprocessedBulkPol((request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%", request.getPageNumber(), request.getPageSize());
        final List<BulkPolicyCreationDTO> unProcessed = new ArrayList<>();
        long rowCount = 0L;
        if (!unProcessedBulkPols.isEmpty()) rowCount = ((BigInteger) unProcessedBulkPols.get(0)[12]).intValue();

        for (Object[] unProcessedBulkPol : unProcessedBulkPols) {
            BulkPolicyCreationDTO bulkPolicyCreationDTO = new BulkPolicyCreationDTO();
            bulkPolicyCreationDTO.setBulkPolicyId(((BigInteger) unProcessedBulkPol[0]).longValue());
            bulkPolicyCreationDTO.setPolNumber((String) unProcessedBulkPol[1]);
            bulkPolicyCreationDTO.setPolProposalNo((String) unProcessedBulkPol[2]);
            bulkPolicyCreationDTO.setPolicyStatus((String) unProcessedBulkPol[3]);
            bulkPolicyCreationDTO.setSumInsured((BigDecimal) unProcessedBulkPol[4]);
            bulkPolicyCreationDTO.setPolBasicPrem((BigDecimal) unProcessedBulkPol[5]);
            bulkPolicyCreationDTO.setRefundablePremium((BigDecimal) unProcessedBulkPol[6]);
            bulkPolicyCreationDTO.setRefundAmount((BigDecimal) unProcessedBulkPol[7]);
            bulkPolicyCreationDTO.setCoverDateFrom((Date) unProcessedBulkPol[8]);
            bulkPolicyCreationDTO.setCoverDateTo((Date) unProcessedBulkPol[9]);
            bulkPolicyCreationDTO.setDateUploaded((Date) unProcessedBulkPol[10]);
            bulkPolicyCreationDTO.setClientFname((String) unProcessedBulkPol[11]);
            unProcessed.add(bulkPolicyCreationDTO);
        }
        Page<BulkPolicyCreationDTO> page = new PageImpl<>(unProcessed, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<BulkPolicyCreationDTO> viewBulkRefundPolicies(DataTablesRequest request) {
        List<Object[]> viewBulkPolicies = bulkRefundsCreationRepo.viewUnprocessedBulkPol((request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%", request.getPageNumber(), request.getPageSize());
        final List<BulkPolicyCreationDTO> unProcessed = new ArrayList<>();
        long rowCount = 0L;
        if (!viewBulkPolicies.isEmpty()) rowCount = ((BigInteger) viewBulkPolicies.get(0)[2]).intValue();

        for (Object[] unProcessedBulkPol : viewBulkPolicies) {
            BulkPolicyCreationDTO bulkPolicyCreationDTO = new BulkPolicyCreationDTO();
            bulkPolicyCreationDTO.setBulkPolicyId(((BigInteger) unProcessedBulkPol[0]).longValue());
            bulkPolicyCreationDTO.setPolNumber((String) unProcessedBulkPol[1]);
            unProcessed.add(bulkPolicyCreationDTO);
        }
        Page<BulkPolicyCreationDTO> page = new PageImpl<>(unProcessed, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public List<Long> bulkProcessRefundPolicies(List<Long> bulkIds, boolean isApproved) throws BadRequestException, IllegalAccessException {
        for(Long bulkId : bulkIds){
            BulkRefunds bulkRefunds = bulkRefundsCreationRepo.findByBulkId(bulkId);

            if (bulkRefunds == null) {
                throw new BadRequestException("Bulk ID not found: " + bulkId);
            }

            Long polId = bulkRefunds.getOPolId();
            List<Long> transactions = bulkRefundsCreationRepo.findRefundableTransactionIds(polId).stream()
                    .map(BigInteger::longValue)
                    .collect(Collectors.toList());

            System.out.println("size "+ transactions.size());
            if (!transactions.isEmpty()) {
                Map<String, Object> result = accountsService.processRefunds(transactions,null);

                Boolean success = (Boolean) result.get("success");
                String message = (String) result.get("message");
                List<Long> processed = (List<Long>) result.get("processedTransactions");
                List<Long> processedRefundPolID = (List<Long>) result.get("refundPolId");
                List<String> errors = (List<String>) result.get("errors");

                if (Boolean.TRUE.equals(success)) {
                    System.out.println("Refunds processed: " + processed);
                    bulkRefunds.setRPolId(processedRefundPolID.get(0));
                } else {
                    System.out.println("Refund processing failed: " + message);
                }

                if (errors != null && !errors.isEmpty()) {
                    errors.forEach(System.out::println);
                }

                log.info("results of refund {}", result);
                bulkRefunds.setPolicyProcessed("Y");
                bulkRefunds.setTransAuthorized("N");
                bulkRefundsCreationRepo.save(bulkRefunds);
            }else{
                //no transaction found to refund
                log.warn("No refundable transactions found for policy ID: {}", bulkRefunds.getOPolId());
            }
        }
        return bulkIds;
    }

    @Override
    public List<Long> bulkAuthorizeRefunds(List<Long> bulkIds) throws BadRequestException {

        for(Long taskId : bulkIds) {
//            BulkRefunds bulkRefunds = bulkRefundsCreationRepo.findByBulkId(taskId);

            //authService.authorizePolicy(bulkPolCnCreation.getBulkCnPolId(), bulkPolCnCreation.getRefundAmt());
            //authService.authorizePolicy(bulkPolCnCreation.getBulkCnPolId(), bulkPolCnCreation.getRefundAmt(), true);
            //
//            BigInteger mckIdBig = bulkRefundsCreationRepo.findRefundableTransactionMakerCheckerIds(bulkRefunds.getRPolId());
//            Long mckId = (mckIdBig != null) ? mckIdBig.longValue() : null;

//            if (mckId != null) {
                makerCheckerService.approveTask(taskId);

                SysWfDocs sysWfDocs = new SysWfDocs();
                sysWfDocs.setDocId(taskId);
                System.out.println(sysWfDocs.getClientId());
                System.out.println(taskId);

//                bulkRefunds.setTransAuthorized("Y");
//                bulkRefundsCreationRepo.save(bulkRefunds);
//            } else {
//                throw new BadRequestException("No maker-checker task found for refund policy ID: " + bulkRefunds.getRPolId());
//            }
        }
        return bulkIds;
    }

    @Override
    public String bulkARejectRefunds(List<Long> taskIds, Long reasonId, String reason) throws BadRequestException {
        if (taskIds.size() <= 0) {
            throw new BadRequestException("Select At least One Receipt To Process");
        }

        uploadValidatorsUtils.rejectBulkReceipting(taskIds, reasonId, reason);

        return "Bulk receipt approvals done successfully.";
    }


    @Override
    public List<Long> bulkDeleteRefunds(List<Long> bulkIds){
        for(Long bulkId : bulkIds) {
            BulkRefunds bulkRefunds = bulkRefundsCreationRepo.findByBulkId(bulkId);
            bulkRefundsCreationRepo.delete(bulkRefunds);
        }
        return bulkIds;
    }
}