package com.brokersystems.brokerapp.bulktransactions.service.impl;

import com.brokersystems.brokerapp.bulktransactions.models.BulkReceipt;
import com.brokersystems.brokerapp.bulktransactions.models.QBulkReceipt;
import com.brokersystems.brokerapp.bulktransactions.repositories.BulkPolicyCreationRepo;
import com.brokersystems.brokerapp.bulktransactions.repositories.BulkReceiptRepository;
import com.brokersystems.brokerapp.certs.model.RiskCertForm;
import com.brokersystems.brokerapp.certs.repository.PolicyCertsRepo;
import com.brokersystems.brokerapp.certs.repository.PrintQueueRepo;
import com.brokersystems.brokerapp.certs.repository.SubclassCertTypesRepo;
import com.brokersystems.brokerapp.claims.dtos.ClaimDetailsDTO;
import com.brokersystems.brokerapp.claims.model.ClaimBookings;
import com.brokersystems.brokerapp.claims.repository.ClaimPerilsRepo;
import com.brokersystems.brokerapp.claims.repository.ClaimsBookingRepo;
import com.brokersystems.brokerapp.customscreens.repository.RegisteredTableModelRepo;
import com.brokersystems.brokerapp.dms.model.QSybrinCases;
import com.brokersystems.brokerapp.dms.model.SybrinCases;
import com.brokersystems.brokerapp.dms.repo.SybrinCasesRepo;
import com.brokersystems.brokerapp.enums.AccountTypeEnum;
import com.brokersystems.brokerapp.kie.rules.GeneralTransRulesExecutor;
import com.brokersystems.brokerapp.life.model.*;
import com.brokersystems.brokerapp.life.repository.LifeReceiptsRepo;
import com.brokersystems.brokerapp.life.repository.PolicyAccrualPayRepo;
import com.brokersystems.brokerapp.life.repository.PolicyBeneficiariesRepo;
import com.brokersystems.brokerapp.life.repository.PolicyInstallmentsRepo;
import com.brokersystems.brokerapp.life.service.LifeService;
import com.brokersystems.brokerapp.medical.repository.*;
import com.brokersystems.brokerapp.schedules.repository.ScheduleMappingRepo;
import com.brokersystems.brokerapp.schedules.repository.ScheduleTransRepo;
import com.brokersystems.brokerapp.schedules.service.ScheduleService;
import com.brokersystems.brokerapp.security.CheckAuthLimits;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.*;
import com.brokersystems.brokerapp.setup.dto.UserDTO;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.*;
import com.brokersystems.brokerapp.setup.service.OrganizationService;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.brokersystems.brokerapp.trans.model.*;
import com.brokersystems.brokerapp.trans.repository.*;
import com.brokersystems.brokerapp.trans.service.ReceiptService;
import com.brokersystems.brokerapp.users.dto.MakerCheckDTO;
import com.brokersystems.brokerapp.users.model.MakerChecker;
import com.brokersystems.brokerapp.users.model.QMakerChecker;
import com.brokersystems.brokerapp.users.repository.MakerCheckerRepo;
import com.brokersystems.brokerapp.users.service.MakerCheckerService;
import com.brokersystems.brokerapp.uw.dtos.PolicyCreateDTO;
import com.brokersystems.brokerapp.uw.dtos.ReceiptsDTO;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.uw.repository.*;
import com.brokersystems.brokerapp.uw.service.EndorseService;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import com.brokersystems.brokerapp.uw.service.PremComputeService;
import com.brokersystems.brokerapp.workflow.docs.DocType;
import com.brokersystems.brokerapp.workflow.docs.SysWfDocs;
import com.brokersystems.brokerapp.workflow.repository.SysWfDocsRepo;
import com.brokersystems.brokerapp.workflow.utils.WorkflowService;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mysema.query.types.Predicate;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class UploadValidatorsUtils {
    @Autowired
    private SectionRepo sectionsRepo;
    @Autowired
    private PremRatesRepo premRatesRepo;
    @Autowired
    private SectionTransRepo sectionRepo;
    @Autowired
    private PolTaxesRepo polTaxesRepo;
    @Autowired
    private TaxRatesRepo taxRatesRepo;
    @Autowired
    private PremComputeService premiumService;
    @Autowired
    private PolicyInstallmentsRepo policyInstallmentsRepo;
    @Autowired
    private MakerCheckerRepo makerCheckerRepo;
    @Autowired
    private EscalationActivityLoggerRepository activityLoggerRepository;
    @Autowired
    private MakerCheckerService makerCheckerService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PolicyTransService policyService;
    @Autowired
    private PolicyTransRepo policyTransRepo;
    @Autowired
    private PolicyTransRepo policyRepo;
    @Autowired
    private AdminFeeSetUpRepo adminFeeSetUpRepo;
    @Autowired
    private DateUtilities dateUtils;
    @Autowired
    private PolActiveRisksRepo activeRisksRepo;
    @Autowired
    private RiskTransRepo riskRepo;
    @Autowired
    private SectionRepo setupSectionRepo;
    @Autowired
    private BindersRepo binderRepo;
    @Autowired
    private ClientRepository clientRepo;
    @Autowired
    private CoverTypesRepo coverRepo;
    @Autowired
    private BinderDetRepo binderDetRepo;
    @Autowired
    private SubClassRepo subclassRepo;
    @Autowired
    private SystemTransactionsTempRepo systemTransactionsTempRepo;
    @Autowired
    private TransMappingRepo transMappingRepo;
    @Autowired
    private CheckAuthLimits authLimits;
    //
    @Autowired
    private BulkReceiptRepository bulkReceiptRepository;


    @Autowired
    private UserUtils userUtils;

    @Autowired
    private CurrencyRepository currencyRepo;

    @Autowired
    private PaymentModeRepo paymentModeRepo;

    @Autowired
    private OrgBranchRepository branchRepo;
    @Autowired
    private SubclassCertTypesRepo subclassCertTypesRepo;

    @Autowired
    private SequenceRepository sequenceRepo;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private ProductsRepo productRepo;


    @Autowired
    private TemplateMerger templateMerger;

    @Autowired
    private com.brokersystems.brokerapp.certs.service.CertService certService;

    @Autowired
    private ParamService paramService;

    @Autowired
    private WorkflowService workflowService;


    @Autowired
    private RiskDocsRepo riskDocsRepo;

    @Autowired
    private BinderReqrdDocsRepo reqrdDocsRepo;

    @Autowired
    private TransChecksRepo transChecksRepo;

    @Autowired
    private BindersRepo bindersRepo;
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private GeneralTransRulesExecutor generalTransRulesExecutor;
    @Autowired
    private PremComputeService premComputeService;

    @Autowired
    private EndorseService endorseService;
    @Autowired
    private ReceiptService receiptService;
    @Autowired
    private ReceiptRepository receiptRepository;
    @Autowired
    private EscalationRecordRepository escalationRecordRepository;
    private static final int BATCH_SIZE = 500;
    //

    public static Currencies validateAndGetCurrencyCode(String currency, CurrencyRepository currencyRepository) throws BadRequestException {
        Currencies currencies = currencyRepository.findOne(QCurrencies.currencies.curIsoCode.eq(currency));
        if (currencies == null) {
            throw new BadRequestException("Currency code " + currency + " not setup in the system, please contact system admin");
        }
        return currencies;
    }


    public List<RiskSectionBean> saveSectionTransaction(String sectionCode,Long binderDet, BigDecimal amount) {
        List<RiskSectionBean> sectionBeans = new ArrayList<>();

        SectionsDef sectionsDef = sectionsRepo.findOne(QSectionsDef.sectionsDef.shtDesc.eq(sectionCode));
        if (sectionsDef == null) {
            System.out.println("Section not found for: " + sectionCode);
            return sectionBeans;
        }

//        SectionTrans sectionTrans = new SectionTrans();
//        sectionTrans.setAmount(amount);
//        sectionTrans.setSection(sectionsDef);
//        sectionTrans.setCompute(true);
//        sectionTrans.setDivFactor(BigDecimal.valueOf(100));

        Iterable<PremRatesDef> premRatesDefs = premRatesRepo.findAll(QPremRatesDef.premRatesDef.section.shtDesc.eq(sectionCode).and(QPremRatesDef.premRatesDef.binderDet.detId.eq(binderDet)));
        PremRatesDef premRatesDef = null;
        if (premRatesDefs.iterator().hasNext()) {
            premRatesDef = premRatesDefs.iterator().next();
        }

        if (premRatesDef != null) {
            RiskSectionBean bean = new RiskSectionBean();
//            sectionTrans.setPremRates(premRatesDef);
//            sectionTrans.setRate(premRatesDef.getRate());
//            sectionTrans.setRisk(savedTrans);
//            sectionTrans.setFreeLimit(BigDecimal.ZERO);
//            sectionRepo.save(sectionTrans);
            System.out.println("rates" + premRatesDef.getRate() + " amount" + amount);
            bean.setSection(sectionsDef.getId());
            bean.setRate(premRatesDef.getRate());
            bean.setRatesApplicable("Y");
            bean.setDivFactor(premRatesDef.getDivFactor());
            bean.setFreeLimit(BigDecimal.ZERO);
            bean.setAmount(amount);
            sectionBeans.add(bean);
            System.out.println("Saved section transaction for: " + sectionCode);
        } else {
            System.out.println("No premium rate found for: " + sectionCode);
        }
        System.out.println("section beans {}" + sectionBeans.size());
        return sectionBeans;
    }

    //if month add the next month if year add the next year date
    public Date getInstallDueDate(Date wef, String frequency) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(wef);

        if (frequency.equalsIgnoreCase("Daily")) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        } else if (frequency.equalsIgnoreCase("Weekly")) {
            cal.add(Calendar.WEEK_OF_YEAR, 1);
        } else if (frequency.equalsIgnoreCase("Monthly")) {
            cal.add(Calendar.MONTH, 1);
        } else if (frequency.equalsIgnoreCase("Quarterly")) {
            cal.add(Calendar.MONTH, 3);
        } else if (frequency.equalsIgnoreCase("Semi-Annually")) {
            cal.add(Calendar.MONTH, 6);
        } else if (frequency.equalsIgnoreCase("Annually")) {
            cal.add(Calendar.YEAR, 1);
        }

        System.out.println(cal.getTime() + "is the next installments");
        return cal.getTime();
    }

    public void savePolicyInstallments(PolicyTrans policy, BigDecimal payablePrem, Date wef, String frequency) {

        Iterable<PolicyInstallments> installments = policyInstallmentsRepo
                .findAll(QPolicyInstallments.policyInstallments.policyTrans.policyId.eq(policy.getPolicyId()));
        policyInstallmentsRepo.delete(installments);

//        final BigDecimal payablePrem = (totalPrem.add(polextras).add(polstampDuty).add(polphfFund).add(polTl)
//                .add(polwhtxAmt).add(totalCommission)).setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN);

        final List<PolicyInstallments> installmentsList = new ArrayList<>();
        int j = 0;

        int installament;
        if (policy.getFrequency().toUpperCase().contains("SG")) {
            installament = 1;
        } else {
            installament = policy.getTotalInstalments();
        }

        Map<String, Integer> frequencyMap = new HashMap<>();
        frequencyMap.put("D", 365);
        frequencyMap.put("W", 52);
        frequencyMap.put("M", 12);
        frequencyMap.put("Q", 4);
        frequencyMap.put("S", 2);
        frequencyMap.put("A", 1);
        int paymentsPerYear = frequencyMap.getOrDefault(policy.getFrequency(), 12);

//        LifeSubAgentCommissionRates lifeSubAgentCommissionRates = null;

////        final PolicyInstallments policyInstallments = new PolicyInstallments();
////        policyInstallments.setInstallPaid("N");//policyInstallmentsOptional.get().getInstallPaid());
////        policyInstallments.setPolicyTrans(savedPol);
////        policyInstallments.setPaidDate(null);//"policyInstallmentsOptional.get().getPaidDate());
////        policyInstallments.setInstallmentNo(1L);//policyInstallmentsOptional.get().getInstallmentNo());
////        policyInstallments.setInstallPrem(premium);//policyInstallmentsOptional.get().getInstallPrem().multiply(BigDecimal.valueOf(-1))));
////        //policyInstallments.setExpectedCommission((policyInstallmentsOptional.get().getExpectedCommission().multiply(BigDecimal.valueOf(-1))));
////        //if(policyInstallmentsOptional.get().getExpectedSubAgentCommission()!=null)
////        //policyInstallments.setExpectedSubAgentCommission((policyInstallmentsOptional.get().getExpectedSubAgentCommission().multiply(BigDecimal.valueOf(-1))));
////        //if(policyInstallmentsOptional.get().getExpectedMarkerterCommission()!=null)
////        //policyInstallments.setExpectedMarkerterCommission((policyInstallmentsOptional.get().getExpectedMarkerterCommission().multiply(BigDecimal.valueOf(-1))));
////        policyInstallments.setDueDate(getInstallDueDate(wef, frequency)); //policyInstallmentsOptional.get().getDueDate()); compute wef + 1
////        policyInstallments.setNotificationSent(true);
////        policyInstallments.setInstallmentNo(1L);
////        //System.out.println("Install no .."+policyInstallments.getInstallmentNo()+" id..."+policyInstallmentsOptional.get().getInstallmentNo());
////        policyInstallmentsRepo.save(policyInstallments);

//        if(policy.getSubAgent()!=null) {
//            if (lifeSubAgentCommissionRatesRepo.count(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.binderDef.eq(policy.getBinder())
//                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.accountTypes.accountType.eq(AccountTypeEnum.SUB))
//                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermFrom.loe(policy.getPolTerm()))
//                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermTo.goe(policy.getPolTerm()))
//                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wefDate.loe(policy.getCoverFrom()))
//                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.goe(policy.getCoverFrom()).or(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.isNull()))
//                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.frequency.equalsIgnoreCase(policy.getFrequency()))
//            ) <= 0) {
//                throw new BadRequestException("No SubAgent commission rate defined...." + policy.getPolTerm() + "; frequency: " + policy.getFrequency());
//
//            }
//            if (lifeSubAgentCommissionRatesRepo.count(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.binderDef.eq(policy.getBinder())
//                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.accountTypes.accountType.eq(AccountTypeEnum.SUB))
//                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermFrom.loe(policy.getPolTerm()))
//                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermTo.goe(policy.getPolTerm()))
//                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wefDate.loe(policy.getCoverFrom()))
//                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.goe(policy.getCoverFrom()).or(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.isNull()))
//                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.frequency.equalsIgnoreCase(policy.getFrequency()))
//            ) > 1) {
//                throw new BadRequestException("More than one Sub-Agent commission rate exists....");
//            }
//
//            lifeSubAgentCommissionRates = lifeSubAgentCommissionRatesRepo.findOne(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.binderDef.eq(policy.getBinder())
//                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.accountTypes.accountType.eq(AccountTypeEnum.SUB))
//                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermFrom.loe(policy.getPolTerm()))
//                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermTo.goe(policy.getPolTerm()))
//                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wefDate.loe(policy.getCoverFrom()))
//                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.goe(policy.getCoverFrom()).or(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.isNull()))
//                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.frequency.equalsIgnoreCase(policy.getFrequency())));
//        }
//
//        LifeSubAgentCommissionRates lifeMarketerCommissionRates = null;
//
//        if(policy.getMarketerAgent()!=null) {
//            if (lifeSubAgentCommissionRatesRepo.count(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.binderDef.eq(policy.getBinder())
//                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.accountTypes.accountType.eq(AccountTypeEnum.MRK))
//                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermFrom.loe(policy.getPolTerm()))
//                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermTo.goe(policy.getPolTerm()))
//                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wefDate.loe(policy.getCoverFrom()))
//                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.goe(policy.getCoverFrom()).or(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.isNull()))
//                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.frequency.equalsIgnoreCase(policy.getFrequency()))
//            ) <= 0) {
//                throw new BadRequestException("No Marketer commission rate defined...." + policy.getPolTerm() + "; frequency: " + policy.getFrequency());
//
//            }
//            if (lifeSubAgentCommissionRatesRepo.count(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.binderDef.eq(policy.getBinder())
//                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.accountTypes.accountType.eq(AccountTypeEnum.MRK))
//                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermFrom.loe(policy.getPolTerm()))
//                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermTo.goe(policy.getPolTerm()))
//                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.frequency.equalsIgnoreCase(policy.getFrequency()))
//            ) > 1) {
//                throw new BadRequestException("More than one Marketer commission rate exists....");
//            }
//
//            lifeMarketerCommissionRates = lifeSubAgentCommissionRatesRepo.findOne(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.binderDef.eq(policy.getBinder())
//                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.accountTypes.accountType.eq(AccountTypeEnum.MRK))
//                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermFrom.loe(policy.getPolTerm()))
//                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermTo.goe(policy.getPolTerm()))
//                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.frequency.equalsIgnoreCase(policy.getFrequency())));
//        }

//        BigDecimal totalSubAgentCom = BigDecimal.ZERO;
//        BigDecimal totalMarketerAgentCom = BigDecimal.ZERO;
        int annualInstForAnnualFreq = 0;
        for (int i = 1; i <= installament; i++) {
            double paidYears = Math.ceil(((double) i/paymentsPerYear));
//            if (lifeCommissionRatesRepo.count(QLifeCommissionRates.lifeCommissionRates.binderDef.eq(policy.getBinder())
//                    .and(QLifeCommissionRates.lifeCommissionRates.commTermFrom.loe(policy.getPolTerm()))
//                    .and(QLifeCommissionRates.lifeCommissionRates.commTermTo.goe(policy.getPolTerm()))
//                    .and(QLifeCommissionRates.lifeCommissionRates.wefDate.loe(policy.getCoverFrom()))
//                    .and(QLifeCommissionRates.lifeCommissionRates.wetDate.goe(policy.getCoverFrom()).or(QLifeCommissionRates.lifeCommissionRates.wetDate.isNull()))
//                    .and(QLifeCommissionRates.lifeCommissionRates.commYearFrom.loe(paidYears))
//                    .and(QLifeCommissionRates.lifeCommissionRates.frequency.equalsIgnoreCase(policy.getFrequency()))
//                    .and(QLifeCommissionRates.lifeCommissionRates.commYearTo.goe(paidYears))) <= 0) {
//                throw new BadRequestException("No commission rate defined....for term " + policy.getPolTerm() + "; Effective From " + formatDate(policy.getCoverFrom()) + ";To date " + formatDate(policy.getCoverFrom()) + "; Commission Year .." + paidYears + ";Frequesncy: " + policy.getFrequency());
//
//            }
//            if (lifeCommissionRatesRepo.count(QLifeCommissionRates.lifeCommissionRates.binderDef.eq(policy.getBinder())
//                    .and(QLifeCommissionRates.lifeCommissionRates.commTermFrom.loe(policy.getPolTerm()))
//                    .and(QLifeCommissionRates.lifeCommissionRates.commTermTo.goe(policy.getPolTerm()))
//                    .and(QLifeCommissionRates.lifeCommissionRates.wefDate.loe(policy.getCoverFrom()))
//                    .and(QLifeCommissionRates.lifeCommissionRates.wetDate.goe(policy.getCoverFrom()).or(QLifeCommissionRates.lifeCommissionRates.wetDate.isNull()))
//                    .and(QLifeCommissionRates.lifeCommissionRates.commYearFrom.loe(paidYears))
//                    .and(QLifeCommissionRates.lifeCommissionRates.frequency.equalsIgnoreCase(policy.getFrequency()))
//                    .and(QLifeCommissionRates.lifeCommissionRates.commYearTo.goe(paidYears))) > 1) {
//                throw new BadRequestException("More than one commission rate exists....");
//            }
//            LifeCommissionRates commissionRates = lifeCommissionRatesRepo.findOne(QLifeCommissionRates.lifeCommissionRates.binderDef.eq(policy.getBinder())
//                    .and(QLifeCommissionRates.lifeCommissionRates.commTermFrom.loe(policy.getPolTerm()))
//                    .and(QLifeCommissionRates.lifeCommissionRates.commTermTo.goe(policy.getPolTerm()))
//                    .and(QLifeCommissionRates.lifeCommissionRates.wefDate.loe(policy.getCoverFrom()))
//                    .and(QLifeCommissionRates.lifeCommissionRates.wetDate.goe(policy.getCoverFrom()).or(QLifeCommissionRates.lifeCommissionRates.wetDate.isNull()))
//                    .and(QLifeCommissionRates.lifeCommissionRates.commYearFrom.loe(paidYears))
//                    .and(QLifeCommissionRates.lifeCommissionRates.frequency.equalsIgnoreCase(policy.getFrequency()))
//                    .and(QLifeCommissionRates.lifeCommissionRates.commYearTo.goe(paidYears)));



            final PolicyInstallments policyInstallments = new PolicyInstallments();
            policyInstallments.setInstallPaid("N");
            policyInstallments.setNotificationSent(false);
            if (policy.getNegotiatedPremium() != null) {
                policyInstallments.setInstallPrem(policy.getNegotiatedPremium());
            } else {
                policyInstallments.setInstallPrem(payablePrem);
            }
//            if (commissionRates != null) {
//                BigDecimal commRate = commissionRates.getCommRate();
//                BigDecimal comm = (commRate.multiply(policyInstallments.getInstallPrem()).divide(commissionRates.getCommDivFactor())).setScale(2, BigDecimal.ROUND_HALF_EVEN);
//                policyInstallments.setExpectedCommission(comm);
//                if(paidYears <=1) {
//                    if (lifeSubAgentCommissionRates != null) {
//                        BigDecimal subAgentComm = (lifeSubAgentCommissionRates.getCommRate().multiply(comm).divide(lifeSubAgentCommissionRates.getCommDivFactor())).setScale(2, BigDecimal.ROUND_HALF_EVEN);
//                        policyInstallments.setExpectedSubAgentCommission(subAgentComm);
//                    }
//                    if (lifeMarketerCommissionRates != null) {
//                        BigDecimal mkrAgentComm  = (lifeMarketerCommissionRates.getCommRate().multiply(comm).divide(lifeMarketerCommissionRates.getCommDivFactor())).setScale(2, BigDecimal.ROUND_HALF_EVEN);
//                        policyInstallments.setExpectedMarkerterCommission(mkrAgentComm);
//                    }
//                }
//            }
            policyInstallments.setInstallmentNo((long) i);
            policyInstallments.setPolicyTrans(policy);
            if (policy.getFrequency().equalsIgnoreCase("M")) {
                policyInstallments.setDueDate(Date.from(
                        LocalDate.now().plus(j, ChronoUnit.MONTHS).atStartOfDay(ZoneId.systemDefault()).toInstant()));
                j++;
            } else if (policy.getFrequency().equalsIgnoreCase("Q")) {
                policyInstallments.setDueDate(Date.from(LocalDate.now().plus(i * 3, ChronoUnit.MONTHS)
                        .atStartOfDay(ZoneId.systemDefault()).toInstant()));
            } else if (policy.getFrequency().equalsIgnoreCase("S")) {
                policyInstallments.setDueDate(Date.from(LocalDate.now().plus(i * 6, ChronoUnit.MONTHS)
                        .atStartOfDay(ZoneId.systemDefault()).toInstant()));
            } else if (policy.getFrequency().equalsIgnoreCase("A")) {
                policyInstallments.setDueDate(Date.from(
                        LocalDate.now().plus(annualInstForAnnualFreq, ChronoUnit.YEARS).atStartOfDay(ZoneId.systemDefault()).toInstant()));
                annualInstForAnnualFreq++;
            }
  //          installmentsList.add(policyInstallments);
            policyInstallmentsRepo.save(policyInstallments);
        }
//        policyInstallmentsRepo.save(installmentsList);
    }


    public String maskCardNumber(String cardNumber) {
        // Remove spaces or dashes if any
        cardNumber = cardNumber.replaceAll("[^\\d]", "");
        int length = cardNumber.length();

        if (length <= 4) return cardNumber;

        StringBuilder masked = new StringBuilder();
        for (int i = 0; i < length - 4; i++) {
            masked.append("*");
        }
        masked.append(cardNumber.substring(length - 4));

        // Insert spaces every 4 characters
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < masked.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                formatted.append(" ");
            }
            formatted.append(masked.charAt(i));
        }

        return formatted.toString();
    }

    public void saveGeneralPolicyUpload(Long polId, boolean compute) throws BadRequestException {
        //create the policy based on the dto

        //compute the taxes
        if(compute) {
            try {
                premComputeService.computePrem(polId);
            } catch (IOException e) {
                throw new BadRequestException(e.getMessage());
            }
        }

        //make ready the policy
        policyService.makeReady(polId);
       // String ready = makeGeneralUploadReady(polId);
        //set the bulk receipt ref no

        //setup maker checker
        //makerCheckerUpload(polId);
    }


    public String makeGeneralUploadReady(Long polCode) throws BadRequestException {
        log.info("Making policy ready for polCode: {}", polCode);

        PolicyTrans policy = policyRepo.findOne(polCode);

        if (policy == null) {
            log.error("No PolicyTrans found for polCode: {}", polCode);
            throw new BadRequestException("No Policy Transaction to make Ready");
        }

//        if(policy.getBasicPrem() != null || policy.getBasicPrem().compareTo(BigDecimal.ZERO) != 0 ){
//            throw new BadRequestException("Basic Premium can't be null or less than 1");
//        }
        //LIFE PRODUCTS DONT HAVE TAXES
        if (!"L".equalsIgnoreCase(policy.getProduct().getProGroup().getPrgType())) {
            if (policy.getPremium() != null && policy.getPremium().compareTo(BigDecimal.ZERO) != 0) {
                if (policy.getPhcf() == null || policy.getPhcf().compareTo(BigDecimal.ZERO) == 0) {
                    throw new BadRequestException("Please populate taxes to continue");
                }
                if (policy.getTrainingLevy() == null || policy.getTrainingLevy().compareTo(BigDecimal.ZERO) == 0) {
                    throw new BadRequestException("Please populate taxes to continue");
                }
            }
        }

        if (policy.getInterfaceType() == null) {
            log.warn("InterfaceType was null for polCode: {}, setting default to 'A'", polCode);
            policy.setInterfaceType("A"); // or "C", or fetch from a default config
        }
        if (policy.getBusinessType() == null) {
            log.warn("BusinessType was null for polCode: {}, setting default to 'NEW'", polCode);
            policy.setBusinessType("NEW"); // Default business type
        }

        if (!(policy.getInterfaceType().equalsIgnoreCase("A") || policy.getInterfaceType().equalsIgnoreCase("C"))) {
            log.error("Invalid Interface Type '{}' for PolicyTrans with polCode: {}", policy.getInterfaceType(), polCode);
            throw new BadRequestException("Set the policy type to either accrual or cash.");
        }

        if (!authLimits.checkAuthorizationLimits("MAKE_POLICY_READY", policy.getBasicPrem())) {
            log.error("Authorization limits failed for user on polCode: {}", polCode);
            throw new BadRequestException("You have no rights to make ready the transaction...Check your authorization limits..");
        }


        boolean checks = false;// rulesExecutor.handlePolicyChecks(policy);
        long sectCount = sectionRepo.count(QSectionTrans.sectionTrans.risk.policy.policyId.eq(polCode));
        if (sectCount == 0)
            throw new BadRequestException("Cannot Make Ready the Policy without Premium Items..");
        // 3. Adjust policy dates based on transaction type
//        LocalDate originalPolWef = new java.sql.Date(policy.getWefDate().getTime()).toLocalDate();
//        LocalDate originalPolWet = new java.sql.Date(policy.getWetDate().getTime()).toLocalDate();
//
//        LocalDate adjustedPolWef = HolidayUtils.getAdjustedTransactionDate(originalPolWef);
//        LocalDate adjustedPolWet;
//        if ("EN".equalsIgnoreCase(policy.getTransType()) || "CN".equalsIgnoreCase(policy.getTransType())) {
//            // For endorsements - keep original end date
//            adjustedPolWet = originalPolWet;
//        } else {
//            // For new business/renewals - maintain exact duration
//            long policyDurationDays = ChronoUnit.DAYS.between(originalPolWef, originalPolWet);
//            adjustedPolWet = adjustedPolWef.plusDays(policyDurationDays);
//
//        }

        // Update policy dates
//        policy.setWefDate(java.sql.Date.valueOf(adjustedPolWef));
//        policy.setWetDate(java.sql.Date.valueOf(adjustedPolWet));

        // 4. Process and validate risks
        List<Object[]> risks = riskRepo.findPolicyRiskTrans(polCode);
        List<RiskTrans> updatedRisks = new ArrayList<>();

//        if ("EN".equalsIgnoreCase(policy.getTransType()) || "CO".equalsIgnoreCase(policy.getTransType())) {
//            Long remarkCount = policyRemarksRepo.count(QPolicyRemarks.policyRemarks.policy.policyId.eq(polCode));
//            if (remarkCount == 0)
//                throw new BadRequestException("Cannot Make Ready Endorsement Without Endorsement Remarks");
//            PolicyRemarks remarks = policyRemarksRepo.findOne(QPolicyRemarks.policyRemarks.policy.policyId.eq(polCode));
//            if (remarks.getPolRemarks() == null || StringUtils.isBlank(remarks.getPolRemarks()))
//                throw new BadRequestException("Cannot Make Ready Endorsement Without Endorsement Remarks");
//        }

        for (Object[] risk : risks) {
            final Long riskId = ((BigInteger) risk[1]).longValue();
            final Date wef = (Date) risk[7];
            final Date wet = (Date) risk[8];
            final Long binderDetId = ((BigInteger) risk[0]).longValue();

            // Convert and adjust risk dates
            LocalDate originalRiskWef = new java.sql.Date(wef.getTime()).toLocalDate();
            LocalDate originalRiskWet = new java.sql.Date(wet.getTime()).toLocalDate();

//            LocalDate adjustedRiskWef = HolidayUtils.getAdjustedTransactionDate(originalRiskWef);
//            LocalDate adjustedRiskWet;

//            if ("EN".equalsIgnoreCase(policy.getTransType()) || "CN".equalsIgnoreCase(policy.getTransType())) {
//                // For endorsements - keep original risk end date
//                adjustedRiskWet = originalRiskWet;
//            } else {
//                // For new business - maintain exact duration
//                long riskDurationDays = ChronoUnit.DAYS.between(originalRiskWef, originalRiskWet);
//                adjustedRiskWet = adjustedRiskWef.plusDays(riskDurationDays);
//            }
//
//            // Update risk with adjusted dates
//            RiskTrans riskBean = riskRepo.findOne(riskId);
//            riskBean.setWefDate(java.sql.Date.valueOf(adjustedRiskWef));
//            riskBean.setWetDate(java.sql.Date.valueOf(adjustedRiskWet));
//            updatedRisks.add(riskBean);
//            // Validate within policy period
//            if (adjustedRiskWef.isBefore(adjustedPolWef) || adjustedRiskWet.isAfter(adjustedPolWet)) {
//                throw new BadRequestException("Risk Cover Dates outside Policy Cover Periods after adjustments");
//            }

//            if (riskWef.before(polWef) || riskWef.after(polWet)
//                    || riskWet.before(polWef) || riskWet.after(polWet)) {
//                throw new BadRequestException("Risk Cover Dates outside Policy Cover Periods ");
//            }
            final BinderDetails binderDetails = binderDetRepo.findOne(binderDetId);
//            final Long binderId = ((BigInteger)risk[14]).longValue();
            final BindersDef bindersDef = policy.getBinder();

//            Iterable<RiskDocs> riskDocs = riskDocsRepo.findAll(QRiskDocs.riskDocs.risk.riskId.eq(riskBean.getRiskId()));
//            for (RiskDocs riskDoc : riskDocs) {
//                if (riskDoc.getCheckSum() == null || org.apache.commons.lang.StringUtils.isBlank(riskDoc.getCheckSum())) {
//                    throw new BadRequestException(String.format("Cannot authorize policy without uploading documents for Risk %s", riskBean.getRiskShtDesc()));
//                }
//            }

//            Iterable<SubClassReqdDocs> subClassReqdDocs = subclassReqDocRepo.findAll(QSubClassReqdDocs.subClassReqdDocs.subclass.subId.eq(((BigInteger) risk[4]).longValue()));
//            Iterable<RiskDocs> riskDocs = riskDocsRepo.findRiskDocsById(((BigInteger) risk[1]).longValue());
//            if (subClassReqdDocs == null) {
//                throw new BadRequestException("Subclass Required Documents are not set up");
//            }
//            if (riskDocs == null) {
//                throw new BadRequestException("Please upload required documents");
//            }
//            boolean proofOfPayment = false;
//            boolean isAccrualDocFound = false;
//            for (RiskDocs riskDoc : riskDocs) {
//                if (riskDoc.getReqdDocs().getRequiredDoc().getReqDesc().equalsIgnoreCase("PROOF OF PAYMENT")) {
//                    proofOfPayment = true;
//                }
//                if (policy.getInterfaceType().equalsIgnoreCase("A") && riskDoc.getReqdDocs().getRequiredDoc().getAccrualDoc() != null && riskDoc.getReqdDocs().getRequiredDoc().getAccrualDoc().equalsIgnoreCase("Y")) {
//                    isAccrualDocFound = true;
//                }
//            }
//            for (SubClassReqdDocs subClassReqdDoc : subClassReqdDocs) {
//                SybrinCases caseExists = sybrinCasesRepo.findOne(QSybrinCases.sybrinCases.caseRiskId.eq(((BigInteger) risk[1]).longValue()).and(QSybrinCases.sybrinCases.caseRequiredDocId.eq(subClassReqdDoc.getRequiredDoc().getReqId())));
//                String requiredDocDesc = subClassReqdDoc.getRequiredDoc().getReqDesc();
//                if (subClassReqdDoc.isMandatory() && caseExists == null) {
//                    throw new BadRequestException(String.format("%s document is Mandatory", subClassReqdDoc.getRequiredDoc().getReqDesc()));
//                }
//                for (RiskDocs riskDoc : riskDocs) {
//                    if (requiredDocDesc.equals(riskDoc.getReqdDocs().getRequiredDoc().getReqDesc()) && caseExists == null) {
//                        throw new BadRequestException(String.format("%s document upload  is initiated but not completed. Upload document to proceed.", requiredDocDesc));
//                    }
//                }
//            }

            //FOR DOWNWARDS EN DONT ASK FOR PROOF OF PAYMENT
//            boolean downEn = false;
//            List<Object[]> results = policyRemarksRepo.findRemarksByPolicyId(policy.getPolicyId());
//            for (Object[] row : results) {
//                String remarks = (String) row[0];
//                String shortDesc = (String) row[1];
//
//                // Check for "down" (case-insensitive)
//                if ((remarks != null && remarks.toLowerCase().contains("down")) || (shortDesc != null && shortDesc.toLowerCase().contains("down"))) {
//                    System.out.println("down");
//                    downEn = true;
//                    break;
//                }
//            }

            ///----//ask for kyc if the the policy is not from bulk upload
            //  Optional<BulkPolicyCreation> existsFromBulk = bulkPolicyCreationRepo.policyFromUploadCheck(policy.getPolicyId());
//            if (!policy.getTransType().equalsIgnoreCase("BU")) {
//                dont ask for proff of payment for downwards endorsement
//                if (!(policy.getTransType().equalsIgnoreCase("EN") && downEn)) {
//                    for (SubClassReqdDocs reqdDoc : subClassReqdDocs) {
//                        String accrualDocRequired = reqdDoc.getRequiredDoc().getAccrualDoc();
//                        if (policy.getInterfaceType().equalsIgnoreCase("C") && !proofOfPayment) {
//                            throw new BadRequestException("Kindly Attach Proof of Payment");
//                        }
//
//
//                        if (policy.getInterfaceType().equalsIgnoreCase("A") && !isAccrualDocFound) {
//                            log.info(" policy is {}, with transtype {}",policy, policy.getTransType());
//                            if(!policy.getTransType().equalsIgnoreCase("RN")) {
//                                throw new BadRequestException("Kindly Attach Required Accrual Documents");
//                            }
//                        }
//                        for (RiskDocs riskDoc : riskDocs) {
//                            SybrinCases caseExists = sybrinCasesRepo.findOne(QSybrinCases.sybrinCases.caseRiskId.eq(((BigInteger) risk[1]).longValue()).and(QSybrinCases.sybrinCases.caseRequiredDocId.eq(riskDoc.getReqdDocs().getRequiredDoc().getReqId())));
//                            if ((policy.getInterfaceType().equalsIgnoreCase("A") && accrualDocRequired != null && accrualDocRequired.equals(riskDoc.getReqdDocs().getRequiredDoc().getAccrualDoc()) || policy.getInterfaceType().equalsIgnoreCase("C")) &&
//                                    caseExists == null) {
//                                throw new BadRequestException(String.format("%s document upload is initiated but not completed", riskDoc.getReqdDocs().getRequiredDoc().getReqDesc()));
//                            }
//                        }
//                    }
//                }
//            }

            if (binderDetails.getBinder().getBinId() != bindersDef.getBinId()) {
                throw new BadRequestException("Cannot Make Ready...Sub Class and Cover Type Details do not match the binder selected");
            }
        }

        policy.setAuthStatus("R");
        policyRepo.save(policy);
        boolean cashBasis = policy.getInterfaceType() != null && "C".equalsIgnoreCase(policy.getInterfaceType());
        SystemTransactionsTemp trans = systemTransactionsTempRepo.findOne(QSystemTransactionsTemp.systemTransactionsTemp.policy.policyId.eq(policy.getPolicyId()));

        if (!cashBasis && trans != null) {
            systemTransactionsTempRepo.delete(trans);
        }
        if (!checks) {
            if (cashBasis) {
                BigDecimal basicPrem = (policy.getPremium() == null) ? BigDecimal.ZERO : policy.getPremium();
                if (trans == null) {
                    trans = new SystemTransactionsTemp();
                    Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("D");
                    if (sequenceRepo.count(seqPredicate) == 0)
                        throw new BadRequestException("Sequence for Debit Notes has not been defined");
                    SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
                    Long seqNumber = sequence.getNextNumber();
                    String transType = policy.getTransType();
                    if ("NB".equalsIgnoreCase(transType)) {
                        transType = "NB";
                    } else if ("RN".equalsIgnoreCase(transType)) {
                        transType = "RN";
                    } else if ("BU".equalsIgnoreCase(transType)) {
                        transType = "BU";
                    } else if ("LD".equalsIgnoreCase(transType)) {
                        transType = "LD";
                    } else {
                        transType = "EN";
                    }
                    TransactionMapping mapping = transMappingRepo.findOne(QTransactionMapping.transactionMapping.transType.eq(transType));
                    final String refNo = ((basicPrem.compareTo(BigDecimal.ZERO) >= 0) ? mapping.getDebitCode() : mapping.getCreditCode()) + String.format("%05d", seqNumber);
                    final String debitCode = ((basicPrem.compareTo(BigDecimal.ZERO) >= 0) ? mapping.getDebitCode() : mapping.getCreditCode());
                    sequence.setLastNumber(seqNumber);
                    sequence.setNextNumber(seqNumber + 1);
                    sequenceRepo.save(sequence);
                    trans.setRefNo(refNo);
                    trans.setTransType(debitCode);

                    //PROVIDE THE REF NO TO BE USED FOR BULK RECEIPTING
                    //if(transType.equalsIgnoreCase("BU")) {
                        policy.setRefNo(refNo);
                        policyRepo.save(policy);
                    //}
                }


                BigDecimal extras = (policy.getExtras() == null) ? BigDecimal.ZERO : policy.getExtras();
                BigDecimal phcf = (policy.getPhcf() == null) ? BigDecimal.ZERO : policy.getPhcf();
                BigDecimal tl = (policy.getTrainingLevy() == null) ? BigDecimal.ZERO : policy.getTrainingLevy();
                BigDecimal sd = (policy.getStampDuty() == null) ? BigDecimal.ZERO : policy.getStampDuty();
                BigDecimal amountWithTaxes = basicPrem.add(extras).add(phcf).add(tl).add(sd);

                if (basicPrem.compareTo(BigDecimal.ZERO) == 1) {
                    String type = (amountWithTaxes.compareTo(BigDecimal.ZERO) == 1) ? "D" : "C";

                    trans.setAmount(basicPrem.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    trans.setAuthDate(new Date());
                    trans.setAuthorised("Y");
                    trans.setBalance(amountWithTaxes.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    trans.setBranch(policy.getBranch());
                    trans.setClientType("C");
                    trans.setControlAcc(policy.getClient().getTenantNumber());
                    trans.setClient(policy.getClient());
                    trans.setCurrRate(new BigDecimal(1));
                    trans.setCurrency(policy.getTransCurrency());
                    trans.setNarrations("Posting client Debit Note");
                    trans.setNetAmount(amountWithTaxes.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    trans.setOrigin("U");
                    trans.setPhfund(phcf.setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    trans.setPolicy(policy);
                    trans.setSd(sd.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    trans.setTl(tl.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
//                    trans.setTransDate(new Date());
                    trans.setTransDate(java.sql.Date.valueOf(HolidayUtils.getAdjustedTransactionDate(LocalDate.now())));
                    trans.setTransdc((amountWithTaxes.compareTo(BigDecimal.ZERO) == 1) ? "D" : "C");
                    trans.setUserAuth(userUtils.getCurrentUser().getUsername());
                    trans.setWhtx(BigDecimal.ZERO);
                    trans.setExtras(extras.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    trans.setPostedDate(new Date());
                    trans.setPostedUser(userUtils.getCurrentUser());
                    systemTransactionsTempRepo.save(trans);
                }
            }
        }

        //  if ("D".equalsIgnoreCase(policy.getAuthStatus())) {
        boolean medicalProduct = false;
        if (policy.getProduct().getProGroup().getPrgType() == null || !policy.getProduct().getProGroup().getPrgType().equalsIgnoreCase("MD")) {
            medicalProduct = false;
        } else if (policy.getProduct().getProGroup().getPrgType().equalsIgnoreCase("MD")) {
            medicalProduct = true;
        }

        Map<String, Object> processVariables = Maps.newHashMap();
        processVariables.put("canAuthorize", !checks);
        processVariables.put("confirmAuth", true);
        workflowService.completeTask(String.valueOf(polCode), processVariables, policy, DocType.GEN_UW_DOCUMENT, (medicalProduct) ? "Y" : "N", null, null, null, null);
        return (!checks) ? "Y" : "N";
        //}
    }

    private BigDecimal sign(String type) {
        return ("C".equalsIgnoreCase(type) ? BigDecimal.ONE.multiply(BigDecimal.valueOf(-1)) : BigDecimal.ONE.multiply(BigDecimal.valueOf(1)));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public PolicyTrans saveLifePolicyUpload(PolicyCreateDTO policy, Long userId) throws BadRequestException {
        //create the policy first
        System.out.println("policy from dto"+policy);
        PolicyTrans created = createBulkLifePolicy(policy,userId, true);
        policyService.createPolicyAddonsInfo(created, policy);

        //save the installments for upload life
        savePolicyInstallments(created, created.getPremium(), created.getWefDate(), created.getFrequency());
        //save the sections prem
        saveUploadDraftSectionTransaction("SUM ASSURED", created.getPremium(), created, policy);

        //compute the premium
        if ("NB".equalsIgnoreCase(created.getTransType())
                || "SP".equalsIgnoreCase(created.getTransType())
                || "EX".equalsIgnoreCase(created.getTransType())
                || "RN".equalsIgnoreCase(created.getTransType())) {
            try {
                System.out.println(created.getTransType());
                System.out.println("Computing life policy....");
                premiumService.computeLifePrem(created.getPolicyId());
            } catch (IOException e) {
                e.printStackTrace();
                throw new BadRequestException(e.getMessage());
            }

            policyRepo.save(created);
        }

        //MAKE THE POLICY READY
        System.out.println("make life coverting ");
        policyService.makeBulkLifeReady(created.getPolicyId(), userId);
        //CONVERT THE POLICY
        System.out.println("make life coverting ");
        convertLifePolicy(created.getPolicyId(),userId);
        System.out.println("make life coverting ");
        return created;
    }

    public void convertLifePolicy(Long polCode,Long userId) throws BadRequestException {
        //policy at ready convert it next
        //policyService.convertPropToPolicy(polCode);
        policyService.convertBulkPropToPolicy(polCode);

        User user = userRepo.findOne(userId);
        if(user!=null) {
            //maker checker service
            PolicyTrans policy = policyTransRepo.findOne(polCode);
            final Long hashCode = Long.parseLong(String.valueOf(policy.hashCode()));
            List<UserDTO> eligibleCheckers = makerCheckerService.findEligibleCheckers("AUTHORIZE_POLICY", null);
            List<Long> checkerIds = new ArrayList<Long>();
            if (!makerCheckerRepo.exists(QMakerChecker.makerChecker.taskType.equalsIgnoreCase("ALP").and(QMakerChecker.makerChecker.policyId.eq(polCode)))) {
                for (UserDTO eligibleChecker : eligibleCheckers) {
                    checkerIds.add(eligibleChecker.getId());
                }
                MakerCheckDTO makerCheckDTO = new MakerCheckDTO();
                makerCheckDTO.setStatus("N");
//            makerCheckDTO.setTaskName(String.format("Life Policy %s ", policy.getPolNo()));
                makerCheckDTO.setTaskName(String.format(" %s", policy.getPolNo()));
                makerCheckDTO.setTaskType("ALP");
                makerCheckDTO.setJson("{}");
                makerCheckDTO.setTaskCode(hashCode);
                makerCheckDTO.setMakerId(user.getId());
                makerCheckDTO.setAssignedCheckers(checkerIds.toString());
                makerCheckDTO.setPolicyId(policy.getPolicyId());
                makerCheckerService.checkExists(makerCheckDTO);
                //makerCheckerService.createMakerChecker(makerCheckDTO);
                makerCheckerService.createBulkMakerChecker(makerCheckDTO);
            } else {
                MakerChecker makerChecker = makerCheckerRepo.findOne(QMakerChecker.makerChecker.taskType.equalsIgnoreCase("ALP").and(QMakerChecker.makerChecker.policyId.eq(polCode)));
                makerCheckerService.resubmitTask(makerChecker.getId(), "ALP", policy, checkerIds);
            }
        }

    }

    public void convertGeneralPolicy() {
        //receipts.js

    }

    public void approvBulkSingleReceipting(Long taskId) throws BadRequestException {
            makerCheckerService.approveTask(taskId);

            SysWfDocs sysWfDocs = new SysWfDocs();
            sysWfDocs.setDocId(taskId);
            System.out.println(sysWfDocs.getClientId());
            System.out.println(taskId);

    }

    public void approveBulkReceipting(List<Long> taskIds) throws BadRequestException {
        for (Long taskId : taskIds) {
            makerCheckerService.approveTask(taskId);

            SysWfDocs sysWfDocs = new SysWfDocs();
            sysWfDocs.setDocId(taskId);
            System.out.println(sysWfDocs.getClientId());
            System.out.println(taskId);
        }
    }


//    public void approveBulkReceipting(List<Long> taskIds) throws BadRequestException {
////        Iterable<MakerChecker> makerCheckers = makerCheckerRepo.findAll(taskIds);
////        User checkerId = userUtils.getCurrentUser();
////        ArrayList<MakerChecker> makerCheckersRecords = new ArrayList<>();
////        ArrayList<EscalationRecord> existingRecords = new ArrayList<>();
////        ArrayList<EscalationRecord> newRecords = new ArrayList<>();
////        ArrayList<EscalationActivityLogger> escalationActivityLoggers = new ArrayList<>();
////
////        // Rest of your escalation and activity logging code...
////        //List<EscalationRecord> checkerRecords = escalationRecordRepository.findAll(makerChecker);
////        Iterable<EscalationRecord> checkerRecords = escalationRecordRepository.findByTaskIn((List<MakerChecker>) makerCheckers);
////        Map<Long, EscalationRecord> taskToEscalationMap = StreamSupport.stream(checkerRecords.spliterator(), false)
////                .collect(Collectors.toMap(er -> er.getTask().getId(), Function.identity()));
////
////
////        for (MakerChecker makerChecker : makerCheckers) {
////            //makerCheckerService.approveTask(taskId);
////            if (Objects.equals(makerChecker.getStatus(), "N")) {
////                makerChecker.setStatus("A");
////                if (makerChecker.getMakerId().equals(checkerId)) {
////                    throw new BadRequestException("You can't approve a task you've initiated");
////                }
////                makerChecker.setCheckerId(checkerId);
////                makerChecker.setCheckDate(new Date());
////                switch (makerChecker.getTaskType().toUpperCase()) {
////                    case "RC":
////                        // Retrieve policy using makerChecker's policyId
////                        // Deserialize the task JSON
////                        Gson gson = new GsonBuilder()
////                                .setDateFormat("dd/MM/yyyy")
////                                .create();
////
////                        // Check if this is a cancellation task
////                        if (makerChecker.getTaskName() != null && makerChecker.getTaskName().contains("Cancel Receipt")) {
////                            ReceiptsDTO receiptDTO = gson.fromJson(makerChecker.getTaskJson(), ReceiptsDTO.class);
////
////                            ReceiptTrans receipt = receiptRepository.findOne(
////                                    QReceiptTrans.receiptTrans.receiptNo.eq(receiptDTO.getReceiptNo())
////                            );
////
////                            if (receipt == null) {
////                                throw new BadRequestException("Receipt not found with number: " + receiptDTO.getReceiptNo());
////                            }
////
////                            if ("Y".equals(receipt.getCancelApproved())) {
////                                throw new BadRequestException("Receipt cancellation has already been approved.");
////                            }
////
////                            // Create CancelData object
////                            CancelData cancelData = new CancelData();
////                            cancelData.setReceiptId(receipt.getReceiptId());
////                            cancelData.setCommentl(receiptDTO.getCancelComment());
////
////                            List<CancelData> cancelDataList = Collections.singletonList(cancelData);
////
////                            // Call cancellation with approval flag
////                            receiptService.cancelReceipts(cancelDataList, true);
////
////                            // Update final approval metadata
////                            User currentUser = userUtils.getCurrentUser();
////                            Date now = new Date();
////
////
////                            receipt.setCancelApproved("Y");
////                            receipt.setCancelled("Y");
////                            receipt.setCancelledBy(currentUser);
////                            receipt.setCancelledDate(now);
////                            receipt.setCancelApprovedBy(currentUser);
////                            receipt.setCancelApprovedDate(now);
////
////                            receiptRepository.save(receipt);
////
////                        } else {
////
////                            // This is a regular receipt creation approval
////                            ReceiptTrans receiptTrans = gson.fromJson(makerChecker.getTaskJson(), ReceiptTrans.class);
////                            receiptService.createReceipt(receiptTrans, true);
////                            ReceiptTrans created = receiptRepository.findOne(receiptTrans.getReceiptId());
////                            receiptService.markReceiptPrinted(created.getReceiptId(), checkerId);
////
////                        }
////                        break;
////                    default:
////                        throw new BadRequestException("Invalid task type.");
////                }
////
////                makerCheckersRecords.add(makerChecker);
////                //makerCheckerRepo.save(makerChecker);
////
//////                if (!checkerRecords.isEmpty()) {
//////                    EscalationRecord existingRecord = checkerRecords.get(0);
////                EscalationRecord existingRecord = taskToEscalationMap.get(makerChecker.getId());
////                if (existingRecord != null) {
////                    BeanUtils.copyProperties(makerChecker, existingRecord, "escId", "task");
////                    existingRecord.setCheckTime(makerChecker.getCheckDate());
////                    existingRecord.setVerifier(makerChecker.getCheckerId());
////                    existingRecords.add(existingRecord);
////                    //escalationRecordRepository.save(existingRecord);
////                } else {
////                    EscalationRecord newRecord = new EscalationRecord();
////                    newRecord.setCheckTime(makerChecker.getCheckDate());
////                    newRecord.setVerifier(makerChecker.getCheckerId());
////                    newRecord.setCurrentEscalationLevel(makerChecker.getCheckerId().getEscalationLevel());
////                    newRecord.setOwner(null);
////                    newRecord.setTask(makerChecker);
////                    newRecords.add(newRecord);
////                    //escalationRecordRepository.save(newRecord);
////                }
////
////                User checker = makerChecker.getCheckerId();
////                if (checker != null) {
////                    EscalationActivityLogger escalationActivityLogger = new EscalationActivityLogger();
////                    escalationActivityLogger.setEscalationLevel(checker.getEscalationLevel());
////                    escalationActivityLogger.setTask(makerChecker);
////                    escalationActivityLogger.setUser(checker);
////                    escalationActivityLogger.setActivityTime(makerChecker.getCheckDate());
////                    escalationActivityLogger.setSystemAction("Record Approval");
////                    escalationActivityLoggers.add(escalationActivityLogger);
////                    //activityLoggerRepository.save(escalationActivityLogger);
////                }
////
////                SysWfDocs sysWfDocs = new SysWfDocs();
////                sysWfDocs.setDocId(makerChecker.getId());
////                System.out.println(sysWfDocs.getClientId());
////                System.out.println(makerChecker.getId());
////
////                //SAVE THE BATCH SIZE READY
////                if(makerCheckersRecords.size() >= BATCH_SIZE){
////                    makerCheckerRepo.save(makerCheckersRecords);
////                    makerCheckersRecords.clear();
////                }
////
////                if (escalationActivityLoggers.size() >= BATCH_SIZE) {
////                    activityLoggerRepository.save(escalationActivityLoggers);
////                    escalationActivityLoggers.clear();
////                }
////                if (existingRecords.size() >= BATCH_SIZE) {
////                    escalationRecordRepository.save(existingRecords);
////                    existingRecords.clear();
////                }
////                if (newRecords.size() >= BATCH_SIZE) {
////                    escalationRecordRepository.save(newRecords);
////                    newRecords.clear();
////                }
////
////            }
////
////        }
////
////
////        //save the remainig logs records
////        if(!makerCheckersRecords.isEmpty()){
////            makerCheckerRepo.save(makerCheckersRecords);
////            makerCheckersRecords.clear();
////        }
////
////        if(!escalationActivityLoggers.isEmpty()){
////            activityLoggerRepository.save(escalationActivityLoggers);
////            escalationActivityLoggers.clear();
////        }
////        if(!existingRecords.isEmpty()){
////            escalationRecordRepository.save(existingRecords);
////            existingRecords.clear();
////        }
////        if(!newRecords.isEmpty()){
////            escalationRecordRepository.save(newRecords);
////            newRecords.clear();
////        }
//    }

    @Transactional
    public void rejectBulkReceipting(Long taskId, Long reasonId, String rejectResons) throws BadRequestException {
            rejectBulkPolicies(taskId, reasonId, rejectResons);
    }

    @Transactional
    public void rejectBulkReceipting(List<Long> taskIds, Long reasonId, String rejectResons) throws BadRequestException {
        for (Long taskId : taskIds) {
            rejectBulkPolicies(taskId, reasonId, rejectResons);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveUploadDraftSectionTransaction(String sectionCode, BigDecimal amount, PolicyTrans polCode, PolicyCreateDTO policydto) throws BadRequestException {
        //if sections exists
        RiskTransBean riskBean = policydto.getRiskBean();
        RiskTrans risk = new RiskTrans();
        long sectCount = sectionRepo.count(QSectionTrans.sectionTrans.risk.policy.policyId.eq(polCode.getPolicyId()));
        if (sectCount == 0)
            //throw new BadRequestException("Cannot Make Ready the Policy without Premium Items..");
            System.out.println("am having no sections saved thus creating them again");

        if (riskBean.getBindCode() == null) throw new BadRequestException("Binder is Mandatory");
        if (riskBean.getSclCode() == null) throw new BadRequestException("Sub Class is Mandatory");
        if (riskBean.getCoverCode() == null) throw new BadRequestException("Cover Type is Mandatory");
        if (riskBean.getInsuredCode() == null) throw new BadRequestException("Life assured is Mandatory");
        if (riskBean.getWorkingAge() == null) throw new BadRequestException("Life assured age is Mandatory");
        SubClassDef subClassDef = subclassRepo.findOne(riskBean.getSclCode());
        risk.setBinder(binderRepo.findOne(riskBean.getBindCode()));
        risk.setCovertype(coverRepo.findOne(riskBean.getCoverCode()));
        risk.setSubclass(subClassDef);
        risk.setInsured(clientRepo.findOne(riskBean.getInsuredCode()));
        risk.setWorkingAge(riskBean.getWorkingAge());
        risk.setBinderDetails(binderDetRepo.findOne(riskBean.getBinderDet()));
        risk.setCommRate(riskBean.getCommRate());
        risk.setAutogenCert("N");
        risk.setPolicy(polCode);
        risk.setComputePremium(riskBean.getPremium());
        risk.setProrata(riskBean.getProrata());
        risk.setRiskDesc(riskBean.getRiskDesc());
        risk.setRiskShtDesc(riskBean.getRiskShtDesc());
        risk.setWefDate(riskBean.getWefDate());
        risk.setWetDate(riskBean.getWetDate());
        risk.setPremium(riskBean.getPremium());
        risk.setSumInsured(riskBean.getSumInsured());
        risk.setInvestment(policydto.getInvestment());
        risk.setTopUp(policydto.getTopUp());
        risk.setComputeType(riskBean.getComputeType());
        risk.setTransType("BU");

        RiskTrans savedRisk = riskRepo.save(risk);

        List<SectionTrans> sectionTransactions = policydto.getSections().stream().map(sectionbean -> {
            SectionsDef sectiondef = setupSectionRepo.findOne(sectionbean.getSection());
            SectionTrans section = new SectionTrans();
            section.setAmount((sectionbean.getAmount() == null) ? BigDecimal.ZERO : sectionbean.getAmount());
            section.setCompute(sectionbean.isCompute());
            section.setDivFactor(sectionbean.getDivFactor());
            section.setFreeLimit(sectionbean.getFreeLimit());
            if (sectionbean.getRatesApplicable() != null && "Y".equalsIgnoreCase(sectionbean.getRatesApplicable())) {
                List<PremRatesDef> premRates = premRatesRepo.getSectPremiumRates(riskBean.getBinderDet(), sectionbean.getAmount(), sectiondef.getId());
                if (premRates.size() == 1) {
                    section.setPremRates(premRates.get(0));
                    section.setRate(premRates.get(0).getRate());
                }
            } else {
                List<PremRatesDef> premRates = premRatesRepo.getSectPremiumRates(riskBean.getBinderDet(), sectiondef.getId());
                if (premRates.size() == 1) {
                    section.setPremRates(premRates.get(0));
                    section.setRate(sectionbean.getRate());
                }
            }
            log.debug(" the risk section  {} {}", savedRisk, savedRisk.getRiskId());
            section.setCompute(true);
            section.setSection(sectiondef);
            section.setRisk(savedRisk);
            return section;
        }).collect(Collectors.toList());
        System.out.println(sectionTransactions.size() + " is the premium items size");
        sectionRepo.save(sectionTransactions);

        long riskIdentifier = Long.valueOf(String.valueOf(dateUtils.getUwYear(polCode.getWefDate())) + String.valueOf(savedRisk.getRiskId()));
        PolicyActiveRisks activeRisk = new PolicyActiveRisks();
        activeRisk.setPolicy(polCode);
        activeRisk.setRisk(savedRisk);
        activeRisk.setRiskIdentifier(riskIdentifier);
        activeRisksRepo.save(activeRisk);
        savedRisk.setRiskIdentifier(riskIdentifier);
        riskRepo.save(savedRisk);
    }

    public void updateBasicNet(BigDecimal basicPrem, BigDecimal sumInsured, PolicyTrans policyTrans) {

        policyTrans.setBasicPrem(basicPrem);
        policyTrans.setSumInsured(sumInsured);
        policyTrans.setNetPrem(basicPrem);

        policyRepo.save(policyTrans);
    }

    public PolicyTrans createBulkLifePolicy(PolicyCreateDTO policydto,Long userid, boolean isApproved) throws BadRequestException {
        System.out.println(policydto);
//        // First get the transaction type from the DTO
        String transType = policydto.getTransType();
        boolean iSBulkUpload = false;
        if (policydto.getBulkUpload() != null && !policydto.getBulkUpload().isEmpty() && "true".equalsIgnoreCase(policydto.getBulkUpload())) {
            iSBulkUpload = true;
            //transType = "BU";
        }
//
//        // Then call adjustPolicyDates with both parameters
//        adjustPolicyDates(policydto, transType);
//        if ("EN".equalsIgnoreCase(transType) || "CN".equalsIgnoreCase(transType)) {
//            // Adjust risk dates proportionally for endorsements
//            adjustRiskDatesForEndorsement(policydto.getRiskBean(),
//                    policydto.getWefDate(),
//                    policydto.getWetDate());
//        }
        final Long hashCode = Long.parseLong(String.valueOf(policydto.hashCode()));
        final PolicyTrans policy = new PolicyTrans();
        org.springframework.beans.BeanUtils.copyProperties(policydto, policy);
        System.out.println("after copy"+policy);

        if (policydto.getBindCode() == null) {
            throw new BadRequestException("Policy Product is mandatory...");
        }
        final BindersDef bindersDef = bindersRepo.findOne(policydto.getBindCode());
        if ("S".equalsIgnoreCase(bindersDef.getBinType())) {
            if (binderRepo.count(QBindersDef.bindersDef.fundBinder.equalsIgnoreCase("Y")) != 1) {
                throw new BadRequestException("A Fund Binder has not been set up ..Check your configuration");
            }
//            BindersDef fundBinder = binderRepo.findOne(QBindersDef.bindersDef.fundBinder.equalsIgnoreCase("Y"));
//            policy.setBindCode(fundBinder.getBinId());
//            policy.setProdId(fundBinder.getProduct().getProCode());
//            policy.setAgentId(fundBinder.getAccount().getAcctId());
        }
        PolicyTrans editPolicy = new PolicyTrans();
        if (policy.getPolicyId() != null) {
            editPolicy = policyRepo.findOne(policy.getPolicyId());
        }
        if (policydto.getClientId() == null) throw new BadRequestException("Client is Mandatory");
        if (policydto.getBindCode() == null) throw new BadRequestException("Binder is Mandatory ");
        if (policydto.getAgentId() == null) throw new BadRequestException("Intermediary is Mandatory");
        if (policydto.getProdId() == null) throw new BadRequestException("Product is Mandatory");
        if (policydto.getPaymentId() == null) throw new BadRequestException("Payment Mode is Mandatory");
        if (policydto.getBranchId() == null) throw new BadRequestException("Branch is Mandatory");
        if (policydto.getCurrencyId() == null) throw new BadRequestException("Currency is Mandatory");
        if (policy.getWefDate() == null) throw new BadRequestException("Policy Wef Date From is Mandatory");
        if (policy.getWetDate() == null) throw new BadRequestException("Policy Wet Date From is Mandatory");
        if (policy.getWefDate().after(policy.getWetDate()))
            throw new BadRequestException("Wef Date cannot be greater than Wet Date");
        System.out.println(policy.getPolTerm());
        if (policydto.getNegotiatedPremium() != null) {
            policy.setNegotiatedPremium(policydto.getNegotiatedPremium());
        }
//        Date polWetDate = dateUtils.removeTime(policy.getWetDate());
        User user = (userUtils.getCurrentUser()!=null)?userUtils.getCurrentUser():userRepo.findOne(userid);
        if (policy.getBusinessType() == null) throw new BadRequestException("Select Business Type");
        if ("N".equalsIgnoreCase(policy.getBusinessType())) {
            if (policy.getTransType() == null || "NB".equalsIgnoreCase(policy.getTransType())) {
//                if (policy.getWetDate().after(polWetDate) || policy.getWetDate().before(polWetDate))
//                    throw new BadRequestException("The Policy Cover Period Cannot be more than a year");
            } else if ("EN".equalsIgnoreCase(policy.getTransType()) || "CN".equalsIgnoreCase(policy.getTransType())) {
                PolicyTrans currentTrans = policyRepo.findOne(policy.getPolicyId());
                PolicyTrans prevTrans = currentTrans.getPreviousTrans();
                Date currentWet = dateUtils.removeTime(policy.getWetDate());
                Date prevWet = dateUtils.removeTime(prevTrans.getWetDate());
                if (currentWet.after(prevWet) || currentWet.before(prevWet))
                    throw new BadRequestException("Policy WET Date Cannot change for endorsements...");
            }

        }

//        if ("S".equalsIgnoreCase(policy.getBusinessType()))
//            if (policy.getWetDate().after(polWetDate))
//                throw new BadRequestException("The Short Period Policy Cover Period Cannot be more than a year");

        boolean newTrans = false;
        Iterable<BinderReqrdDocs> reqdDocs = new ArrayList<>();

        //DONT CHECK FOR BULK UPLOAD REQ DOCUMENTS
        if (policydto.getRiskBean() != null && !iSBulkUpload)
            if (policydto.getRiskBean().getSclCode() != null) {
//                String transType = "";
                if (policy.getTransType() == null || StringUtils.isBlank(policy.getTransType())) {
                    transType = "NB";
                } else
                    transType = policy.getTransType();
                if ("CO".equalsIgnoreCase(policy.getTransType())) {
                    transType = policy.getPreviousTrans().getTransType();
                }
                if ("NB".equalsIgnoreCase(transType)) {
                    transType = "NB";
                } else if ("RN".equalsIgnoreCase(transType)) {
                    transType = "RN";
                } else {
                    transType = "EN";
                }
                if ("NB".equalsIgnoreCase(transType)) {
                    reqdDocs = reqrdDocsRepo.findAll(QBinderReqrdDocs.binderReqrdDocs.binderDetail.detId.eq(policydto.getRiskBean().getBinderDet())
                            .and(QBinderReqrdDocs.binderReqrdDocs.mandatory.eq(true))
                            .and(QBinderReqrdDocs.binderReqrdDocs.requiredDocs.requiredDoc.appliesNewBusiness.eq(true)));
                } else if ("EN".equalsIgnoreCase(transType)) {
                    reqdDocs = reqrdDocsRepo.findAll(QBinderReqrdDocs.binderReqrdDocs.binderDetail.detId.eq(policydto.getRiskBean().getBinderDet())
                            .and(QBinderReqrdDocs.binderReqrdDocs.mandatory.eq(true))
                            .and(QBinderReqrdDocs.binderReqrdDocs.requiredDocs.requiredDoc.appliesEndorsement.eq(true)));
                } else if ("RN".equalsIgnoreCase(transType)) {
                    reqdDocs = reqrdDocsRepo.findAll(QBinderReqrdDocs.binderReqrdDocs.binderDetail.detId.eq(policydto.getRiskBean().getBinderDet())
                            .and(QBinderReqrdDocs.binderReqrdDocs.mandatory.eq(true))
                            .and(QBinderReqrdDocs.binderReqrdDocs.requiredDocs.requiredDoc.appliesRenewal.eq(true)));
                }

            }
        ProductsDef policyProduct = productRepo.findOne(policydto.getProdId());
        policy.setCurrentStatus("D");
        policy.setAuthStatus("D");

        if ((bindersDef.getAdminFeeActiveStatus() != null) && (bindersDef.getAdminFeeActiveStatus().equalsIgnoreCase("Y"))) {
            policy.setAdminFeeApplicable("Y");
        } else {
            policy.setAdminFeeApplicable("N");
        }

        //if(policydto.getAdminFeePolicy()!=null && "true".equalsIgnoreCase(policydto.getAdminFeePolicy())){
        //  policy.setAdminFeeApplicable("Y");
        //}
        //else policy.setAdminFeeApplicable("N");
//        if(!isApproved && !makerCheckerRepo.exists(hashCode)) {
//            System.out.println(new Gson().toJson(policydto));
//            MakerCheckDTO makerCheckDTO = new MakerCheckDTO();
//            makerCheckDTO.setJson(new Gson().toJson(policydto));
//            makerCheckDTO.setStatus("N");
//            ClientDef clientDef = clientRepo.findOne(policydto.getClientId());
//            makerCheckDTO.setTaskName("Created Policy:Client..." + clientDef.getFname() + " " + clientDef.getOtherNames() + " Policy No " + policy.getPolNo());
//            makerCheckDTO.setTaskType("LP");
//            makerCheckDTO.setTaskCode(hashCode);
//            makerCheckerService.checkExists(makerCheckDTO);
//            makerCheckerService.createMakerChecker(makerCheckDTO);
//            return policy;
//        }
//        if(!isApproved ) {
//            System.out.println(new Gson().toJson(policydto));
//            MakerCheckDTO makerCheckDTO = new MakerCheckDTO();
//            makerCheckDTO.setJson(new Gson().toJson(policydto));
//            makerCheckDTO.setStatus("N");
//            ClientDef clientDef = clientRepo.findOne(policydto.getClientId());
//            makerCheckDTO.setTaskName("Created Policy:Client..." + clientDef.getFname() + " " + clientDef.getOtherNames() + " Policy No " + policy.getPolNo());
//            makerCheckDTO.setTaskType("LP");
//            makerCheckDTO.setTaskCode(hashCode);
//            makerCheckerService.checkExists(makerCheckDTO);
//            makerCheckerService.createMakerChecker(makerCheckDTO);
//            return policy;
//        }
        // SystemTrans transaction = null;
        boolean medicalProduct = false;
        boolean lifeProduct = false;
        boolean investmentProduct = false;
        boolean motorProduct = policyProduct.isMotorProduct();
        if (policyProduct.getProGroup().getPrgType() == null || !policyProduct.getProGroup().getPrgType().equalsIgnoreCase("N")) {
            medicalProduct = false;
            lifeProduct = false;
            investmentProduct = false;
        } else if (policyProduct.getProGroup().getPrgType().equalsIgnoreCase("MD")) {
            medicalProduct = true;
            lifeProduct = false;
            investmentProduct = false;

        } else if (policyProduct.getProGroup().getPrgType().equalsIgnoreCase("L")) {
            medicalProduct = false;
            lifeProduct = true;
            investmentProduct = false;
        } else if (policyProduct.getProGroup().getPrgType().equalsIgnoreCase("IN")) {
            medicalProduct = false;
            lifeProduct = false;
            investmentProduct = true;

        }
        if (policy.getPolicyId() == null) {

            newTrans = true;
            String policyNumberFormat = paramService.getParameterString("POLICY_NO_FORMAT");
            String endorsementFormat = paramService.getParameterString("ENDORSE_NO_FORMAT");
            String proposalFormat = paramService.getParameterString("PROPOSAL_NO_FORMAT");
            //Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("P");
            Predicate propPredicate = QSystemSequence.systemSequence.transType.eq("PR");
//            if (sequenceRepo.count(seqPredicate) == 0)
//                throw new BadRequestException("Sequence for New Business Transactions has not been defined");
            if (sequenceRepo.count(propPredicate) == 0)
                throw new BadRequestException("Sequence for Proposal Transactions has not been defined");
//            SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
//            Long seqNumber = sequence.getNextNumber();
//            final String policyNumber = templateMerger.generateFormat(policyNumberFormat, policy.getBranchId(), policy.getProdId(), policy.getWefDate(), sequence.getSeqPrefix() + String.format("%05d", seqNumber));
//            policy.setPolNo(policyNumber);
//            sequence.setLastNumber(seqNumber);
//            sequence.setNextNumber(seqNumber + 1);
//            sequenceRepo.save(sequence);
            SystemSequence lifeSequence = sequenceRepo.findOne(propPredicate);
            Long lifeSeqNumber = lifeSequence.getNextNumber();
            final String proposalNo = templateMerger.generateFormat(proposalFormat, policydto.getBranchId(), policydto.getProdId(), policy.getWefDate(), lifeSequence.getSeqPrefix() + String.format("%05d", lifeSeqNumber), null);
            if (proposalNo == null) {
                throw new BadRequestException("Proposal no has not been generated");
            }
            System.out.println("Proposal No " + proposalNo);
            policy.setProposalNo(proposalNo);
            lifeSequence.setLastNumber(lifeSeqNumber);
            lifeSequence.setNextNumber(lifeSeqNumber + 1);
            sequenceRepo.save(lifeSequence);
            Predicate endorsePredicate = QSystemSequence.systemSequence.transType.eq("E");
            if (sequenceRepo.count(endorsePredicate) == 0)
                throw new BadRequestException("Sequence for Endorsement Transactions has not been defined");
            SystemSequence endorseSequence = sequenceRepo.findOne(endorsePredicate);
            Long endosseqNumber = endorseSequence.getNextNumber();
            final String revNumber = endorseSequence.getSeqPrefix() + String.format("%05d", endosseqNumber);
            final String endorseNumber = templateMerger.generateFormat(endorsementFormat, policydto.getBranchId(), policydto.getProdId(), policy.getWefDate(), revNumber, null);
            policy.setPolRevNo(endorseNumber + "/1");
            policy.setRevisionFormat(endorseNumber);
            endorseSequence.setLastNumber(endosseqNumber);
            endorseSequence.setNextNumber(endosseqNumber + 1);
            sequenceRepo.save(endorseSequence);
//            transaction = new SystemTrans();
//            transaction.setDoneDate(new Date());
//            transaction.setDoneBy(userUtils.getCurrentUser());
//            transaction.setPolicy(policy);
//            transaction.setTransLevel("U");
//            transaction.setTransCode("NBD"); //A way to setup and look up for transaction transcode
//            transaction.setTransAuthorised("N");


            if ("S".equalsIgnoreCase(policy.getBusinessType())) {

                policy.setTransType("SP");
                policy.setPolRevStatus("SP");
            } else {
                if (iSBulkUpload) {
                    policy.setTransType("BU");
                } else {
                    policy.setTransType("NB");
                }
                policy.setPolRevStatus("NB");
            }
            policy.setCurrentStatus("D");
            policy.setAuthStatus("D");
        } else {
            final PolicyTrans existing = policyRepo.findOne(policy.getPolicyId());
            policy.setPolTerm(existing.getPolTerm());
            policy.setProposalNo(existing.getProposalNo());
        }


        if (policydto.getPrevPolicy() == null) {
            policy.setPreviousTrans(policy);
        } else {
            policy.setPreviousTrans(policyRepo.findOne(policydto.getPrevPolicy()));
        }
        policy.setPolTerm(policy.getPolTerm());
        policy.setInvestmentTerm(policy.getInvestmentTerm());

        if (policy.getPolicyId() != null) {
            policy.setRevisionFormat(editPolicy.getRevisionFormat());
        }
        policy.setCommAllowed(true);
        if (policydto.getSubAgentId() != null) {
            policy.setSubAgent(accountRepo.findOne(policydto.getSubAgentId()));
        }
        if (policydto.getMarketerAgentId() != null) {
            policy.setMarketerAgent(accountRepo.findOne(policydto.getMarketerAgentId()));
        }
        if (policydto.getIntroducerAgentId() != null) {
            policy.setIntroducerAgent(accountRepo.findOne(policydto.getIntroducerAgentId()));
        }
        if (policydto.getLeadsManId() != null) {
            policy.setLeadsMan(userRepo.findOne(policydto.getLeadsManId()));
        }
        policy.setAbsaNoLeadsMan(policydto.getAbsaNoLeadsMan());
        policy.setAbsaNoMarketer(policydto.getAbsaNoMarketer());
        policy.setAbsaNoIntroducer(policydto.getAbsaNoIntroducer());
        policy.setAbsaNoSubAgent(policydto.getAbsaNoSubAgent());
        policy.setAgent(accountRepo.findOne(policydto.getAgentId()));
        policy.setBinder(binderRepo.findOne(policydto.getBindCode()));
        policy.setBranch(branchRepo.findOne(policydto.getBranchId()));
        policy.setClient(clientRepo.findOne(policydto.getClientId()));
        if (policydto.getNegotiatedPremium() != null) {
            policy.setNegotiatedPremium(policydto.getNegotiatedPremium());
        }
        System.out.println(policy);
        policy.setPaymentMode(paymentModeRepo.findOne(policydto.getPaymentId()));
        policy.setProduct(policyProduct);
        policy.setCreatedUser(user);
        policy.setTransCurrency(currencyRepo.findOne(policydto.getCurrencyId()));
        policy.setPolCreateddt(new Date());
        if ("N".equalsIgnoreCase(policy.getBusinessType()))
            policy.setRenewable(policyProduct.isRenewable());
        else
            policy.setRenewable(false);
        policy.setUwYear(dateUtils.getUwYear(policy.getWefDate()));
        if (policyProduct.isRenewable() && "N".equalsIgnoreCase(policy.getBusinessType())) {
            policy.setRenewalDate(DateUtils.addDays(policy.getWetDate(), 1));
        } else
            policy.setRenewalDate(null);
        if (policy.getTransType().equalsIgnoreCase("NB") || policy.getTransType().equalsIgnoreCase("RN") || policy.getTransType().equalsIgnoreCase("BU")) {
            policy.setCoverFrom(policy.getWefDate());
            policy.setCoverTo(policy.getWetDate());
        }
        System.out.println("frequency=" + policy.getFrequency() + ";term=" + policy.getPolTerm());
        if(iSBulkUpload && policy.getFrequency().equalsIgnoreCase("M")){
            policy.setTotalInstalments(policy.getPolTerm());
        }else {
            if ("M".equalsIgnoreCase(policy.getFrequency()))
                policy.setTotalInstalments(12 * policy.getPolTerm());
            if ("A".equalsIgnoreCase(policy.getFrequency()))
                policy.setTotalInstalments(policy.getPolTerm());
            if ("S".equalsIgnoreCase(policy.getFrequency()))
                policy.setTotalInstalments(6 * policy.getPolTerm());
            if ("Q".equalsIgnoreCase(policy.getFrequency()))
                policy.setTotalInstalments(4 * policy.getPolTerm());
        }
        if (policy.getPolicyId() != null) {
            if (editPolicy == null)
                throw new BadRequestException("The Policy does not exist. Cannot Authorize");
            if ("A".equalsIgnoreCase(editPolicy.getAuthStatus())) {
                throw new BadRequestException("Cannot Save..Policy already authorized");
            }

            if (policy.getPolRevStatus() == null || StringUtils.isBlank(policy.getPolRevStatus())) {
                policy.setPolRevStatus(editPolicy.getPolRevStatus());
            }

            BindersDef binder = editPolicy.getBinder();
            BindersDef newBinder = binderRepo.findOne(policydto.getBindCode());
            if (binder.getBinId() != newBinder.getBinId()) {
                Iterable<RiskTrans> binderRisks = riskRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(policy.getPolicyId()));
                List<RiskTrans> newRisks = new ArrayList<>();
                for (RiskTrans risk : binderRisks) {
                    risk.setBinder(newBinder);
                    newRisks.add(risk);
                    Iterable<SectionTrans> sections = sectionRepo.findAll(QSectionTrans.sectionTrans.risk.riskId.eq(risk.getRiskId()));
                    sectionRepo.delete(sections);
                }
                riskRepo.save(newRisks);
            }


        }

        RiskTransBean riskBean = policydto.getRiskBean();
        System.out.println("INVESTMENT: " + policydto.getInvestment());
        PolicyTrans savedTrans = policyRepo.save(policy);
        if (policy.getPolicyId() == null && !medicalProduct && !policydto.isImportRisks()) {
//
//            // Convert policy dates to LocalDate for adjustment
//            LocalDate policyWef = policydto.getWefDate().toInstant()
//                    .atZone(ZoneId.systemDefault())
//                    .toLocalDate();
//            LocalDate policyWet = policydto.getWetDate().toInstant()
//                    .atZone(ZoneId.systemDefault())
//                    .toLocalDate();
//            adjustLifeRiskDates(policydto.getRiskBean(), policyWef, policyWet);
            Date polWef = dateUtils.removeTime(policy.getWefDate());
            Date polWet = dateUtils.removeTime(policy.getWetDate());
            Date riskWef = dateUtils.removeTime(riskBean.getWefDate());
            Date riskWet = dateUtils.removeTime(riskBean.getWetDate());
            if (riskWef.before(polWef) || riskWef.after(polWet)
                    || riskWet.before(polWef) || riskWet.after(polWet)) {
                throw new BadRequestException("Risk Cover Dates outside Policy Cover Periods ");
            }
            RiskTrans risk = new RiskTrans();
            if (riskBean.getBindCode() == null) throw new BadRequestException("Binder is Mandatory");
            if (riskBean.getSclCode() == null) throw new BadRequestException("Sub Class is Mandatory");
            if (riskBean.getCoverCode() == null) throw new BadRequestException("Cover Type is Mandatory");
            if (riskBean.getInsuredCode() == null) throw new BadRequestException("Life assured is Mandatory");
            if (riskBean.getWorkingAge() == null) throw new BadRequestException("Life assured age is Mandatory");
            SubClassDef subClassDef = subclassRepo.findOne(riskBean.getSclCode());
            risk.setBinder(binderRepo.findOne(riskBean.getBindCode()));
            risk.setCovertype(coverRepo.findOne(riskBean.getCoverCode()));
            risk.setSubclass(subClassDef);
            risk.setInsured(clientRepo.findOne(riskBean.getInsuredCode()));
            risk.setWorkingAge(riskBean.getWorkingAge());
            risk.setBinderDetails(binderDetRepo.findOne(riskBean.getBinderDet()));
            risk.setCommRate(riskBean.getCommRate());
            risk.setAutogenCert("N");
            risk.setPolicy(savedTrans);
            risk.setComputePremium(riskBean.getPremium());
            risk.setProrata(riskBean.getProrata());
            risk.setRiskDesc(riskBean.getRiskDesc());
            risk.setRiskShtDesc(riskBean.getRiskShtDesc());
            risk.setWefDate(riskBean.getWefDate());
            risk.setWetDate(riskBean.getWetDate());
            risk.setPremium(riskBean.getPremium());
            risk.setSumInsured(riskBean.getSumInsured());
            risk.setInvestment(policydto.getInvestment());
            risk.setTopUp(policydto.getTopUp());
            risk.setComputeType(riskBean.getComputeType());
            if (iSBulkUpload) {
                risk.setTransType("BU");
            } else {
                risk.setTransType("NB");
            }
            RiskTrans savedRisk = riskRepo.save(risk);

            List<SectionTrans> sectionTransactions = policydto.getSections().stream().map(sectionbean -> {
                SectionsDef sectiondef = setupSectionRepo.findOne(sectionbean.getSection());
                SectionTrans section = new SectionTrans();
                section.setAmount((sectionbean.getAmount() == null) ? BigDecimal.ZERO : sectionbean.getAmount());
                section.setCompute(sectionbean.isCompute());
                section.setDivFactor(sectionbean.getDivFactor());
                section.setFreeLimit(sectionbean.getFreeLimit());
                if (sectionbean.getRatesApplicable() != null && "Y".equalsIgnoreCase(sectionbean.getRatesApplicable())) {
                    List<PremRatesDef> premRates = premRatesRepo.getSectPremiumRates(riskBean.getBinderDet(), sectionbean.getAmount(), sectiondef.getId());
                    if (premRates.size() == 1) {
                        section.setPremRates(premRates.get(0));
                        section.setRate(premRates.get(0).getRate());
                    }
                } else {
                    List<PremRatesDef> premRates = premRatesRepo.getSectPremiumRates(riskBean.getBinderDet(), sectiondef.getId());
                    if (premRates.size() == 1) {
                        section.setPremRates(premRates.get(0));
                        section.setRate(sectionbean.getRate());
                    }
                }
                log.debug(" the risk section  {} {}", savedRisk, savedRisk.getRiskId());
                section.setCompute(true);
                section.setSection(sectiondef);
                section.setRisk(savedRisk);
                return section;
            }).collect(Collectors.toList());
            System.out.println(sectionTransactions.size() + " is the premium items size");
            sectionRepo.save(sectionTransactions);

            long riskIdentifier = Long.valueOf(String.valueOf(dateUtils.getUwYear(policy.getWefDate())) + String.valueOf(savedRisk.getRiskId()));
            PolicyActiveRisks activeRisk = new PolicyActiveRisks();
            activeRisk.setPolicy(savedTrans);
            activeRisk.setRisk(savedRisk);
            activeRisk.setRiskIdentifier(riskIdentifier);
            activeRisksRepo.save(activeRisk);
            savedRisk.setRiskIdentifier(riskIdentifier);
            riskRepo.save(savedRisk);
        }

//        if (newTrans)
//            transRepo.save(transaction); edituwpolicy
        savedTrans = policyRepo.save(policy);
        Set<TransChecks> transChecks = generalTransRulesExecutor.createLifeChecks(savedTrans);
        transChecksRepo.save(transChecks);
        System.out.println(savedTrans);
        if (newTrans && userUtils.getCurrentUser()!=null) {
            workflowService.startNewWorkFlow(DocType.GEN_UW_DOCUMENT, String.valueOf(savedTrans.getPolicyId()), policy, (medicalProduct) ? "Y" : "N", null, null, null, null);
        }
        Iterable<RiskTrans> risks = riskRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(savedTrans.getPolicyId()));
        if (!medicalProduct && !lifeProduct && !investmentProduct) {
            for (RiskTrans risk : risks) {
                if (motorProduct) {
                    final List<BigInteger> codes = subclassCertTypesRepo.getCertificateCode(risk.getSubclass().getSubId());
                    if (codes.size() == 1) {
                        final RiskCertForm riskCertForm = new RiskCertForm();
                        riskCertForm.setSubclasscertId(((BigInteger) codes.get(0)).longValue());
                        riskCertForm.setWefDate(risk.getWefDate());
                        riskCertForm.setWetDate(risk.getWetDate());
                        riskCertForm.setRiskId(risk.getRiskId());
                        try {
                            certService.createRiskCert(riskCertForm);
                        } catch (BadRequestException ex) {
                            System.out.println("Error creating cert....");
                        }
                    }
                }
                List<RiskDocs> riskDocs = Streamable.streamOf(reqdDocs)
                        .filter(reqdDoc -> riskDocsRepo.count(QRiskDocs.riskDocs.risk.riskIdentifier.eq(risk.getRiskIdentifier()).and(QRiskDocs.riskDocs.reqdDocs.sclReqrdId.eq(reqdDoc.getRequiredDocs().getSclReqrdId()))) == 0)
                        .map(reqdDoc -> {
                            RiskDocs riskDoc = new RiskDocs();
                            riskDoc.setReqdDocs(reqdDoc.getRequiredDocs());
                            riskDoc.setRisk(risk);
                            return riskDoc;
                        }).collect(Collectors.toList());

                riskDocsRepo.save(riskDocs);
            }
        }
        return savedTrans;
    }


    public void saveSectionTransaction(String sectionCode, BigDecimal amount, RiskTrans savedTrans) {
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
            sectionTrans.setRisk(savedTrans);
            sectionTrans.setFreeLimit(BigDecimal.ZERO);
            sectionRepo.save(sectionTrans);
            System.out.println("Saved section transaction for: " + sectionCode);
        } else {
            System.out.println("No premium rate found for: " + sectionCode);
        }
    }


    private void rejectBulkPolicies(Long taskId, Long reasonId, String reason) throws BadRequestException {
        if (!makerCheckerRepo.exists(taskId)) {
            throw new BadRequestException("The Task with this ID does not exist..Cannot continue..");
        }
        User checkerId = userUtils.getCurrentUser();
        MakerChecker rejectedTask = makerCheckerRepo.findOne(taskId);
        if (rejectedTask.getMakerId() == checkerId) {
            throw new BadRequestException("You can't reject a task you've initiated");
        }
        String status = "R";

        if ((reasonId != null) && (reason != null)) {
            status = "I";
        } else if (reason == null) {
            throw new BadRequestException("Provide a comment to guide on the action to be done.");
        }

        makerCheckerRepo.updateRejectedTask(taskId, reasonId, reason, checkerId, status);
        EscalationActivityLogger rejectedTaskActivity = new EscalationActivityLogger();
        rejectedTaskActivity.setTask(rejectedTask);
        rejectedTaskActivity.setUser(checkerId);
        rejectedTaskActivity.setActivityTime(new Date());
        rejectedTaskActivity.setEscalationLevel(checkerId.getEscalationLevel());
        rejectedTaskActivity.setComments(rejectedTask.getRejectedReason());
        rejectedTaskActivity.setSystemAction("Record rejected by checker");
        activityLoggerRepository.save(rejectedTaskActivity);
    }

    public void uploadsErrorLogger(){

    }


    public void saveUploadPolicyBasicInfo(Currencies currencies,
            BigDecimal totalPrem,         BigDecimal totalCommission,
            BigDecimal totalSubAgentComm, BigDecimal totalMarketerComm,
            BigDecimal polphfFund,        BigDecimal polextras,
            BigDecimal polstampDuty,      BigDecimal polTl,
            BigDecimal polwhtxAmt,        BigDecimal totSuminsured,
            BigDecimal futurePrem,        BigDecimal futureTotalTax,
            BigDecimal totalPaidAmt,      PolicyTrans policy

    ){
        policy.setPremium(totalPrem);
        policy.setBasicPrem((totalPrem.add(polextras).add(polstampDuty).add(polphfFund).add(polTl))
                .setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setEndosbasicPremium(totalPrem);
        policy.setEndosgrossPremium((totalPrem.add(polextras).add(polstampDuty).add(polphfFund).add(polTl))
                .setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setPaidPremium(totalPaidAmt);
        policy.setEndosCommissions(totalCommission);
        policy.setCommAmt(totalCommission);
        policy.setSubAgentComm(totalSubAgentComm);
        policy.setMarketerAgentComm(totalMarketerComm);
        policy.setExtras(polextras.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setWhtx(polwhtxAmt.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setPhcf(polphfFund.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setTrainingLevy(polTl.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setTotTrainingLevy(polTl.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setTotPhcf(polphfFund.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setPolTotComm(totalCommission);
        policy.setPolTotExtras(polextras.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setPolTotPrem(totalPrem);
        policy.setPolTotSD(polstampDuty.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setPolTotSI(totSuminsured);
        policy.setPolTotWhtx(polwhtxAmt.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setStampDuty(polstampDuty.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setSumInsured(totSuminsured);
        policy.setNetPrem((totalPrem.add(polextras).add(polstampDuty).add(polphfFund).add(polTl).add(polwhtxAmt)
                .add(totalCommission)).setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        if (policy.isRenewable())
            policy.setFuturePrem(
                    futurePrem.add(futureTotalTax).setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        else
            policy.setFuturePrem(BigDecimal.ZERO);
        policyRepo.save(policy);
    }

    public  void deleteprocessedPol(Long policyId) throws BadRequestException {
        //delete the bulk upload receipts
        Iterable<BulkReceipt> bulkReceipts = bulkReceiptRepository.findAll(QBulkReceipt.bulkReceipt.policy.policyId.eq(policyId));
        bulkReceiptRepository.delete(bulkReceipts);
        try {
            endorseService.deletePolicyRecord(policyId,false);//
        } catch (BadRequestException e) {
            throw new RuntimeException(e);
        }
    }

}