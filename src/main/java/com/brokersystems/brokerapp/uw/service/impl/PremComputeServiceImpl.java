package com.brokersystems.brokerapp.uw.service.impl;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.brokersystems.brokerapp.bulktransactions.repositories.CreditLifeRepository;
import com.brokersystems.brokerapp.enums.AccountTypeEnum;
import com.brokersystems.brokerapp.kie.rules.GeneralTransRulesExecutor;
import com.brokersystems.brokerapp.life.model.*;
import com.brokersystems.brokerapp.life.repository.LifeCommissionRatesRepo;
import com.brokersystems.brokerapp.life.repository.LifeSubAgentCommissionRatesRepo;
import com.brokersystems.brokerapp.life.repository.PolicyBenefitsDistributionRepo;
import com.brokersystems.brokerapp.life.repository.PolicyInstallmentsRepo;
import com.brokersystems.brokerapp.schedules.model.QScheduleTrans;
import com.brokersystems.brokerapp.schedules.model.ScheduleBean;
import com.brokersystems.brokerapp.schedules.model.ScheduleTrans;
import com.brokersystems.brokerapp.schedules.repository.ScheduleTransRepo;
import com.brokersystems.brokerapp.schedules.service.ScheduleService;
import com.brokersystems.brokerapp.server.utils.*;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.*;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.brokersystems.brokerapp.trans.service.ReceiptService;
import com.brokersystems.brokerapp.uw.dtos.RiskBatchDTO;
import com.brokersystems.brokerapp.uw.dtos.SectionTransDTO;
import com.brokersystems.brokerapp.uw.dtos.alak.*;
import com.brokersystems.brokerapp.uw.dtos.alak.kit.GenerateQuoteForEducationPolicyRequest;
import com.brokersystems.brokerapp.uw.dtos.alak.kit.GenerateQuoteForEndowmentPolicyRequest;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.uw.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.brokersystems.brokerapp.enums.RevenueItems;
import com.brokersystems.brokerapp.enums.SectionTypes;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import com.brokersystems.brokerapp.uw.service.PremComputeService;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

@Slf4j
@Service
public class PremComputeServiceImpl implements PremComputeService {

    @Autowired
    private PolicyTransRepo policyRepo;

    @Autowired
    private RiskTransRepo riskRepo;

    @Autowired
    private SectionTransRepo sectionRepo;

    @Autowired
    private PolTaxesRepo polTaxesRepo;

    @Autowired
    private PolicyTransService policyService;

    @Autowired
    private ShortPeriodRepo shortPeriodRepo;

    @Autowired
    private CommRatesRepo commRatesRepo;

    @Autowired
    private PremExcelUtils premExcelUtils;

    @Autowired
    private SubclassCompSheetNamesRepo subclassCompSheetNamesRepo;

    @Autowired
    private PremRatesTableRepo premRatesTableRepo;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ScheduleTransRepo scheduleTransRepo;

    @Autowired
    private ReceiptService receiptService;

    @Autowired
    private ParamService paramService;

    @Autowired
    private PolicyInstallmentsRepo policyInstallmentsRepo;

    @Autowired
    private LifeCommissionRatesRepo lifeCommissionRatesRepo;

    @Autowired
    private LifeSubAgentCommissionRatesRepo lifeSubAgentCommissionRatesRepo;

    @Autowired
    private LifeExcelUtils lifeExcelUtils;

    @Autowired
    private GeneralTransRulesExecutor rulesExecutor;

    @Autowired
    private PolActiveRisksRepo activeRisksRepo;

    @Autowired
    private PolicyBenefitsDistributionRepo maturityRepo;

    @Autowired
    private PolicyBindersRepo policyBindersRepo;

    @Autowired
    private AdminFeeSetUpRepo adminFeeSetUpRepo;

    @Autowired
    private DataSource dataSource;
    @Autowired
    private BinderDetRepo binderDetRepo;
    @Autowired
    private BindersRepo bindersRepo;
    @Autowired
    private PremRatesRepo premRatesRepo;
    @Autowired
    private SubAgentCommRepo subAgentCommRepo;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PolicyBenefitsDistributionRepo policyBenefitsDistributionRepo;
    @Autowired
    private PolicySurrenderValuesRepo policySurrenderValuesRepo;
    @Autowired
    private CreditLifeRepository creditLifeRepository;
    @Autowired
    private PolicyMiscInfoRepo policyMiscInfoRepo;
    @Autowired
    private PolicyDependentsRepo policyDependentsRepo;

    /**
     * Premium Computation at policy level
     */
    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void computePrem(Long polCode) throws BadRequestException, IOException {
        System.out.println("Calls compute prem....");
        PolicyTrans policy = policyRepo.findOne(polCode);
        policyService.populateTaxes(policy);
        ProductsDef product = policy.getProduct();
        Iterable<Object[]> risks = riskRepo.findPolicyRiskTrans(policy.getPolicyId());
        Iterable<PolicyTaxes> policyTaxes = polTaxesRepo
                .findAll(QPolicyTaxes.policyTaxes.policy.policyId.eq(policy.getPolicyId()));
        BigDecimal premium = BigDecimal.ZERO;
        BigDecimal fullpremium = BigDecimal.ZERO;
        BigDecimal sumInsured = BigDecimal.ZERO;
        BigDecimal totalCommission = BigDecimal.ZERO;
        BigDecimal totalCommissionPrem = BigDecimal.ZERO;
        BigDecimal totalSubAgentComm = BigDecimal.ZERO;
        BigDecimal totalMarketerComm = BigDecimal.ZERO;
        BigDecimal polstampDuty = BigDecimal.ZERO;
        BigDecimal polphfFund = BigDecimal.ZERO;
        BigDecimal polwhtxAmt = BigDecimal.ZERO;
        BigDecimal polextras = BigDecimal.ZERO;
        BigDecimal polTl = BigDecimal.ZERO;
        BigDecimal totSuminsured = BigDecimal.ZERO;
        BigDecimal totalPrem = BigDecimal.ZERO;
        BigDecimal totalFapPrem = BigDecimal.ZERO;
        BigDecimal sumPolicytaxAmount = BigDecimal.ZERO;
        AccountTypes accType = (policy.getAgent() != null) ? policy.getAgent().getAccountType() : null;
        boolean multiProduct = policyBindersRepo
                .count(QPolicyBinders.policyBinders.policyTrans.policyId.eq(policy.getPolicyId())) > 0;
        long calDays = TimeUnit.DAYS.convert((policy.getWetDate().getTime() - policy.getWefDate().getTime()),
                TimeUnit.MILLISECONDS) + 1;
        // long totalInstallment = policy.getTotalInstalments();
        boolean cashBasis = policy.getInterfaceType() != null && "C".equalsIgnoreCase(policy.getInterfaceType());
        final List<RiskBatchDTO> riskBatchList = new ArrayList<>();
        BigDecimal futurePrem = BigDecimal.ZERO;
        for (Object[] risk : risks) {
            final RiskBatchDTO batchDTO = new RiskBatchDTO();
            final Long binderDetId = ((BigInteger) risk[0]).longValue();
            final Long riskId = ((BigInteger) risk[1]).longValue();
            batchDTO.setRiskId(riskId);
            final BinderDetails binderDetails = binderDetRepo.findOne(binderDetId);
            BigDecimal riskFuturePrem = BigDecimal.ZERO;
            List<Object[]> sections = sectionRepo.findRiskSectionTrans(riskId);
            BigDecimal riskPrem = BigDecimal.ZERO;
            BigDecimal commissionPrem = BigDecimal.ZERO;
            BigDecimal riskInsured = BigDecimal.ZERO;
            List<PremiumItemsBean> itemsBeen = new ArrayList<>();
            List<PremiumItemsBean> discountItems = new ArrayList<>();
            final String ratesTable = premRatesTableRepo.getRatesLocation(binderDetId);
            if (ratesTable == null)
                throw new BadRequestException("Rates Table for the Binder has not been setup");
            ScheduleBean scheduleBean = scheduleService.generateScheduleColumns(riskId);
            long count = 0;
            if (scheduleBean.getMappings() != null) {
                count = scheduleBean.getMappings().stream()
                        .filter(a -> a.getPremItem() != null && "Y".equalsIgnoreCase(a.getPremItem())).count();
            }
            if (count > 0) {
                if (scheduleTransRepo.count(QScheduleTrans.scheduleTrans.risk.riskId.eq(riskId)) > 1)
                    throw new BadRequestException(
                            "Cannot compute premium. You have entered mutliple schedule details...");
            }
            long counter = count;
            for (Object[] section : sections) {
                List<String> items = new ArrayList<>();
                double minPrem = 0;
                final Long sectId = ((BigInteger) section[0]).longValue();
                final Long sectSectionId = ((BigInteger) section[8]).longValue();
                final Long premRateId = ((BigInteger) section[9]).longValue();
                if (scheduleBean.getMappings() != null) {
                    scheduleBean.getMappings().stream()
                            .filter(a -> a.getPremItem() != null && "Y".equalsIgnoreCase(a.getPremItem()))
                            .filter(a -> a.getPremCode() != null && a.getPremCode() == sectSectionId).forEach(a -> {
                                if (counter > 0) {
                                    ScheduleTrans scheduleTrans = scheduleTransRepo
                                            .findOne(QScheduleTrans.scheduleTrans.risk.riskId.eq(riskId));
                                    if (scheduleTrans != null)
                                        switch (a.getKey()) {
                                            case "1":
                                                if (scheduleTrans.getColumn1() != null)
                                                    items.add(scheduleTrans.getColumn1());
                                                break;
                                            case "2":
                                                if (scheduleTrans.getColumn2() != null)
                                                    items.add(scheduleTrans.getColumn2());
                                                break;
                                            case "3":
                                                if (scheduleTrans.getColumn3() != null)
                                                    items.add(scheduleTrans.getColumn3());
                                                break;
                                            case "4":
                                                if (scheduleTrans.getColumn4() != null)
                                                    items.add(scheduleTrans.getColumn4());
                                                break;
                                            case "5":
                                                if (scheduleTrans.getColumn5() != null)
                                                    items.add(scheduleTrans.getColumn5());
                                                break;
                                            case "6":
                                                if (scheduleTrans.getColumn6() != null)
                                                    items.add(scheduleTrans.getColumn6());
                                                break;
                                            case "7":
                                                if (scheduleTrans.getColumn7() != null)
                                                    items.add(scheduleTrans.getColumn7());
                                                break;
                                            case "8":
                                                if (scheduleTrans.getColumn8() != null)
                                                    items.add(scheduleTrans.getColumn8());
                                                break;
                                            case "9":
                                                if (scheduleTrans.getColumn9() != null)
                                                    items.add(scheduleTrans.getColumn9());
                                                break;
                                            case "10":
                                                if (scheduleTrans.getColumn10() != null)
                                                    items.add(scheduleTrans.getColumn10());
                                                break;
                                            case "11":
                                                if (scheduleTrans.getColumn11() != null)
                                                    items.add(scheduleTrans.getColumn11());
                                                break;
                                            case "12":
                                                if (scheduleTrans.getColumn12() != null)
                                                    items.add(scheduleTrans.getColumn12());
                                                break;
                                            case "13":
                                                if (scheduleTrans.getColumn13() != null)
                                                    items.add(scheduleTrans.getColumn13());
                                                break;
                                            case "14":
                                                if (scheduleTrans.getColumn14() != null)
                                                    items.add(scheduleTrans.getColumn14());
                                                break;
                                            case "15":
                                                if (scheduleTrans.getColumn15() != null)
                                                    items.add(scheduleTrans.getColumn15());
                                                break;
                                        }
                                }

                            });

                    PremRatesDef premRatesDef = premRatesRepo.findOne(premRateId);
                    if (premRatesDef.getMinPremium() != null
                            && premRatesDef.getMinPremium().compareTo(BigDecimal.ZERO) == 1) {
                        minPrem = premRatesDef.getMinPremium().doubleValue();
                    }
                }
                if (sectSectionId == null) {
                    throw new BadRequestException(
                            "Premium Sections have not been mapped correctly to the rates table. Contact Your System Administrator");
                }
                final String sectShtDesc = (String) section[12];
                final String type = (String) section[13];
                final BigDecimal rate = (BigDecimal) section[7];
                final BigDecimal freeLimit = (BigDecimal) section[4];
                final BigDecimal amount = (BigDecimal) section[1];
                final BigDecimal divFactor = (BigDecimal) section[3];
                final SectionTypes sectionTypes = SectionTypes.valueOf(type);
                PremiumItemsBean itemsBean = new PremiumItemsBean(sectShtDesc,
                        (rate != null) ? rate.doubleValue() : 0d,
                        freeLimit != null ? freeLimit.doubleValue() : 0d, minPrem,
                        amount != null ? amount.doubleValue() : 0d,
                        sectId, divFactor != null ? divFactor.doubleValue() : 0d,
                        sectionTypes.getCode(), sectionTypes.getOrder(), items, policy.getPolicyId());
                if (product.getAgeApplicable() != null && "Y".equalsIgnoreCase(product.getAgeApplicable())) {
                    final Date dob = (Date) risk[5];
                    if (dob != null)
                        itemsBean.setAge(DateUtilities.computeAge(dob));
                }
                if (!sectionTypes.equals(SectionTypes.DS)) {
                    itemsBeen.add(itemsBean);
                } else if (sectionTypes.equals(SectionTypes.DS)) {
                    discountItems.add(itemsBean);
                }

            }

            itemsBeen.stream().sorted(Comparator.comparing(PremiumItemsBean::getOrder));
            PremiumResultBean resultBean = premExcelUtils.getPremium(itemsBeen, ratesTable);
            System.out.println("Computed Premium..."+resultBean);
            Optional<PremiumItemsBean> premiumItemsBean = discountItems.stream()
                    .filter(a -> a.getSectType().equalsIgnoreCase("DS")).findFirst();
            riskPrem = BigDecimal.valueOf(resultBean.getPremium());
            commissionPrem = BigDecimal.valueOf(resultBean.getCommissionPremium());
            System.out.println("Commission Premium...."+commissionPrem);
            if (premiumItemsBean.isPresent()) {
                PremiumItemsBean itemsBean = premiumItemsBean.get();
                final SectionTrans section = sectionRepo.findOne(itemsBean.getSectId());
                final BigDecimal discountPrem = BigDecimal.valueOf(itemsBean.getRate()).multiply(riskPrem)
                        .divide(BigDecimal.valueOf(itemsBean.getDivFactor()), 2, RoundingMode.HALF_EVEN);
                final BigDecimal discountCommPrem = BigDecimal.valueOf(itemsBean.getRate()).multiply(riskPrem)
                        .divide(BigDecimal.valueOf(itemsBean.getDivFactor()), 2, RoundingMode.HALF_EVEN);
                section.setCalcprem(discountPrem.negate());
                section.setPrem(discountPrem.negate());
                sectionRepo.save(section);
                riskPrem = riskPrem.subtract(discountPrem);
                commissionPrem = commissionPrem.subtract(discountCommPrem);
            }

            sumInsured = BigDecimal.valueOf(resultBean.getSumInsured());
            riskInsured = sumInsured;
            premium = riskPrem;
            fullpremium = BigDecimal.valueOf(resultBean.getPremiumFull());
            riskFuturePrem = fullpremium;

            String proratedFull = (String) risk[6];
            final Date riskWefDate = (Date) risk[7];
            final Date riskWetDate = (Date) risk[8];
            BinderDetails coverTypes = binderDetails;
            String distribution = coverTypes.getDistribution();
            String firstInstallment = "";

            if (distribution != null && distribution.contains(":")) {
                firstInstallment = distribution.split(":")[0];
            } else
                firstInstallment = distribution;

            if (distribution == null) {
                firstInstallment = "100";
            }
            BigDecimal installPercentage = BigDecimal.ZERO;
            if(firstInstallment!=null){
                try {
                    installPercentage = new BigDecimal(firstInstallment);
                }
                catch (Exception ex){

                }
            }


            if ( installPercentage.compareTo(BigDecimal.ZERO) == 0) {
                if ("F".equalsIgnoreCase(proratedFull)) {
                    riskPrem = riskPrem.multiply(BigDecimal.ONE);
                    premium = premium.multiply(BigDecimal.ONE);
                    commissionPrem = commissionPrem.multiply(BigDecimal.ONE);
                } else if ("S".equalsIgnoreCase(proratedFull)) {
                    long datediff = riskWetDate.getTime() - riskWefDate.getTime();
                    long daysDiff = TimeUnit.DAYS.convert(datediff, TimeUnit.MILLISECONDS) + 1;
                    List<ShortPeriodRates> periodRates = shortPeriodRepo.getShortPeriodRates(daysDiff);
                    if (periodRates.isEmpty())
                        throw new BadRequestException("Short Period Rates For the Cover Period has not been setup");
                    if (periodRates.size() > 1)
                        throw new BadRequestException(
                                "More than one Short Period Rates For the Cover Period have been setup");
                    ShortPeriodRates rates = periodRates.get(0);
                    riskPrem = riskPrem.multiply(rates.getRate().divide(rates.getDivFactor()));
                    commissionPrem = commissionPrem.multiply(rates.getRate().divide(rates.getDivFactor()));
                    premium = premium.multiply(rates.getRate().divide(rates.getDivFactor()));
                } else if ("P".equalsIgnoreCase(proratedFull)) {
                    long datediff = riskWetDate.getTime() - riskWefDate.getTime();
                    long daysDiff = TimeUnit.DAYS.convert(datediff, TimeUnit.MILLISECONDS) + 1;
                    double rata = (double) daysDiff / calDays;
                    BigDecimal prorata = new BigDecimal(rata).setScale(9, BigDecimal.ROUND_HALF_EVEN);
                    riskPrem = riskPrem.multiply(prorata).setScale(2, BigDecimal.ROUND_HALF_EVEN);
                    commissionPrem = commissionPrem.multiply(prorata).setScale(2, BigDecimal.ROUND_HALF_EVEN);
                    premium = premium.multiply(prorata).setScale(2, BigDecimal.ROUND_HALF_EVEN);
                }
            }

            System.out.println("Commission Premium "+commissionPrem);

            riskPrem = riskPrem.add(fullpremium);
            premium = premium.add(fullpremium);


            final BigDecimal butChargePrem = (BigDecimal) risk[9];
            final BigDecimal riskPremium = (BigDecimal) risk[10];
             BigDecimal commissionRate = (BigDecimal) risk[11];
            BigDecimal riskInstallAmt = (BigDecimal) risk[13];
            String installmentPercentage = (String) risk[12];

            final boolean butChargeOrMinPremExpected = ((butChargePrem != null && butChargePrem.compareTo(BigDecimal.ZERO) != 0) ||
                                                        (binderDetails.getMinPrem() != null && binderDetails.getMinPrem().compareTo(riskPrem) > 0));


            if (butChargePrem != null && butChargePrem.compareTo(BigDecimal.ZERO) != 0) {
                riskPrem = butChargePrem.setScale(2, RoundingMode.HALF_EVEN);
                premium = butChargePrem.setScale(2, RoundingMode.HALF_EVEN);
               // commissionPrem = BigDecimal.ZERO;
            }

            if (binderDetails.getMinPrem() != null && binderDetails.getMinPrem().compareTo(riskPrem) > 0) {
                riskPrem = binderDetails.getMinPrem();
                premium = binderDetails.getMinPrem();
            }

            if(butChargeOrMinPremExpected){
                riskPrem = riskPrem.add(commissionPrem);
            }
            BigDecimal riskPremToComputeComm = riskPrem.subtract(commissionPrem);


            System.out.println("Premium Test..."+riskPrem+" But Charged..."+butChargeOrMinPremExpected);

            totSuminsured = totSuminsured.add(riskInsured);
            batchDTO.setRiskPrem(riskPrem);
            batchDTO.setCalcPrem(riskPrem);
            batchDTO.setRiskInsured(riskInsured);
            BigDecimal comm = BigDecimal.ZERO;
            BigDecimal subAgentComm = BigDecimal.ZERO;
            BigDecimal marketerComm = BigDecimal.ZERO;

            if(policy.getTransType()!=null && policy.getTransType().equalsIgnoreCase("RN")){
                Iterable<CommissionRates> commissionRates = commRatesRepo.findAll(QCommissionRates.commissionRates.bindersDef.binId.eq(policy.getBinder().getBinId())
                        .and(QCommissionRates.commissionRates.applicableAt.isNull().or(QCommissionRates.commissionRates.applicableAt.eq("RN"))));
                if(commissionRates.spliterator().getExactSizeIfKnown()!=1){
                    throw new BadRequestException("Commission is not set up for the product...");
                }
                for(CommissionRates rates:commissionRates){
                    commissionRate = rates.getCommRate();
                }
            }
            else  if(policy.getTransType()!=null && policy.getTransType().equalsIgnoreCase("NB")){
                Iterable<CommissionRates> commissionRates = commRatesRepo.findAll(QCommissionRates.commissionRates.bindersDef.binId.eq(policy.getBinder().getBinId())
                        .and(QCommissionRates.commissionRates.applicableAt.isNull().or(QCommissionRates.commissionRates.applicableAt.eq("NB"))));
                if(commissionRates.spliterator().getExactSizeIfKnown()!=1){
                    throw new BadRequestException("Commission is not set up for the product...");
                }
                for(CommissionRates rates:commissionRates){
                    commissionRate = rates.getCommRate();
                }
            }

            if (commissionRate != null) {
                if(riskPremToComputeComm.compareTo(BigDecimal.ZERO) < 0){
                    throw new BadRequestException("Commission Premium cannot be negative...Please check on your premium...");
                }
                comm = riskPremToComputeComm.multiply(commissionRate).divide(BigDecimal.valueOf(100));
//                comm = riskPrem.subtract(commissionPrem).multiply(commissionRate).divide(BigDecimal.valueOf(100));
                if (policy.getSubAgent() != null) {
                    if (policy.getSubAgent().getCommissionEarning() != null
                            && policy.getSubAgent().getCommissionEarning().equalsIgnoreCase("Yes")) {
                        Long accId = policy.getSubAgent().getAccountType().getAccId();
                        Long binderId = policy.getBinder().getBinId();
                        String transType = policy.getTransType().toUpperCase().trim();
                        SubAgentCommissionRates subAgentCommissionRates = subAgentCommRepo
                                .findOne(QSubAgentCommissionRates.subAgentCommissionRates.accountTypes.accId.eq(accId)
                                        .and(QSubAgentCommissionRates.subAgentCommissionRates.bindersDef.binId.eq(binderId))
                                        .and(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.eq(transType)
                                                .or(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.isNull())));
                        if ((policy.getSubAgent().getAccountType().getCommRate() == null
                                || policy.getSubAgent().getAccountType().getCommRate().compareTo(BigDecimal.ZERO) == 0)
                                && (subAgentCommissionRates == null
                                        || subAgentCommissionRates.getCommRate().compareTo(BigDecimal.ZERO) == 0)) {
                            throw new BadRequestException(transType+" Sub Agent Commission Rate not setup");
                        }
                        if (subAgentCommissionRates != null) {
                            BigDecimal subAgentCommRate = subAgentCommissionRates.getCommRate();
                            System.out.println("sub agent comm 1: " + subAgentCommRate);

                            // Use the provided subAgentCommRate
                            subAgentComm = comm.multiply(subAgentCommRate.divide(BigDecimal.valueOf(100)));
                            System.out.println("Sub agent comm amount 1a: " + subAgentComm);
                        } else {
                            // Default to policy's sub Agent account type commission rate
                            subAgentComm = comm.multiply(policy.getSubAgent().getAccountType().getCommRate()
                                    .divide(BigDecimal.valueOf(100)));
                        }
                    } else {
                        subAgentComm = BigDecimal.ZERO;
                    }
                }
            }

            if (commissionRate != null) {
                if(riskPremToComputeComm.compareTo(BigDecimal.ZERO) < 0){
                    throw new BadRequestException("Commission Premium cannot be negative...Please check on your premium...");
                }
                comm = riskPremToComputeComm.multiply(commissionRate).divide(BigDecimal.valueOf(100));
//                comm = riskPrem.subtract(commissionPrem).multiply(commissionRate).divide(BigDecimal.valueOf(100));
                if (policy.getMarketerAgent() != null) {
                    if (policy.getMarketerAgent().getCommissionEarning() != null
                            && policy.getMarketerAgent().getCommissionEarning().equalsIgnoreCase("Yes")) {
                        Long accId = policy.getMarketerAgent().getAccountType().getAccId();
                        Long binderId = policy.getBinder().getBinId();
                        String transType = policy.getTransType().toUpperCase().trim();
                        System.out.println("Marketer Binder Id 1: " + binderId);
                        System.out.println("Marketer AccountId 1: " + accId);
                        SubAgentCommissionRates subAgentCommissionRates = subAgentCommRepo
                                .findOne(QSubAgentCommissionRates.subAgentCommissionRates.accountTypes.accId.eq(accId)
                                        .and(QSubAgentCommissionRates.subAgentCommissionRates.bindersDef.binId.eq(binderId))
                                        .and(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.eq(transType)
                                                .or(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.isNull())));
                        if ((policy.getMarketerAgent().getAccountType().getCommRate() == null || policy
                                .getMarketerAgent().getAccountType().getCommRate().compareTo(BigDecimal.ZERO) == 0)
                                && (subAgentCommissionRates == null
                                        || subAgentCommissionRates.getCommRate().compareTo(BigDecimal.ZERO) == 0)) {
                            throw new BadRequestException(transType+"Marketer Commission Rate not setup");
                        }
                        if (subAgentCommissionRates != null) {
                            BigDecimal marketerCommRate = subAgentCommissionRates.getCommRate();
                            System.out.println("Marketer comm 1: " + marketerCommRate);

                            // Use the provided marketerCommRate
                            marketerComm = comm.multiply(marketerCommRate.divide(BigDecimal.valueOf(100)));
                            System.out.println("Marketer comm amount 1a: " + marketerComm);
                        } else {
                            // Default to policy's marketer account type commission rate
                            marketerComm = comm.multiply(policy.getMarketerAgent().getAccountType().getCommRate()
                                    .divide(BigDecimal.valueOf(100)));
                            System.out.println("Marketer comm amount 1b: " + marketerComm);
                        }
                    } else {
                        marketerComm = BigDecimal.ZERO;
                    }
                }
            }

            BigDecimal sumRisktaxAmount = BigDecimal.ZERO;
            BigDecimal riskstampDuty = BigDecimal.ZERO;
            BigDecimal riskphfFund = BigDecimal.ZERO;
            BigDecimal riskwhtxAmt = BigDecimal.ZERO;
            BigDecimal riskextras = BigDecimal.ZERO;
            BigDecimal riskTl = BigDecimal.ZERO;
            BigDecimal cashBasisPrem = premium;


            System.out.println("First Installment "+firstInstallment);
            if (installmentPercentage != null) {

                // Check if it's a cash payment
                boolean isCashPayment = firstInstallment.equalsIgnoreCase("100") ||
                        installmentPercentage.trim().isEmpty() ||
                        "100".equals(installmentPercentage.trim()) ||
                        "100.0".equals(installmentPercentage.trim());

                if (isCashPayment) {
                    // Handle CASH PAYMENT - set to 100% and skip installment processing
                    System.out.println("Processing cash payment (100% upfront)");

                    policy.setTotalInstalments(1);
                    policy.setInstallmentNo(1L);

                    // Set cash basis premium to full premium
                    cashBasisPrem = premium;
                    riskInstallAmt = premium;

                    batchDTO.setInstallAmt(premium);
                    batchDTO.setInstallmentPercentage("100.0");
                    batchDTO.setTotalPercent(BigDecimal.valueOf(100));

                    if(butChargeOrMinPremExpected){
                        cashBasisPrem = cashBasisPrem.add(commissionPrem);
                    }

                    System.out.println("Cash Basis Prem.1.."+cashBasisPrem);

                    // For cash payment, WET date should be the same as policy end date
                    // No need to adjust WET date for cash payments

                } else {
                    // Handle INSTALLMENT PAYMENT (your existing logic)
                    BigDecimal perc = BigDecimal.ONE;
                    BigDecimal finstalment = BigDecimal.ZERO;

                    try {
                        perc = new BigDecimal(installmentPercentage).divide(BigDecimal.valueOf(100));
                        finstalment = new BigDecimal(firstInstallment).divide(BigDecimal.valueOf(100));
                    } catch (NumberFormatException ex) {
                        throw new BadRequestException("Invalid Instalment Percentage");
                    }

                    Integer installmentNo = coverTypes.getInstallmentsNo();
                    if (installmentNo == null) installmentNo = 1;
                    if (distribution == null) installmentNo = 1;

                    System.out.println("Cash basis.." + cashBasisPrem + " Percentage " + perc);
                    cashBasisPrem = cashBasisPrem.multiply(perc);
                    policy.setTotalInstalments(installmentNo);

                    BigDecimal finstallmentPrem = premium.multiply(finstalment);
                    System.out.println("First installment prem " + finstallmentPrem + " fin installment " + finstalment + " Risk instal amt " + riskInstallAmt);

                    if((riskInstallAmt == null || riskInstallAmt.compareTo(cashBasisPrem) < 0 )){
                        riskInstallAmt = cashBasisPrem;
                    }

                    if (riskInstallAmt == null) {
                        batchDTO.setInstallAmt(cashBasisPrem);
                        riskInstallAmt = cashBasisPrem;
                    }

                    if (riskInstallAmt != null) {
                        cashBasisPrem = riskInstallAmt;
                    }

                    if (cashBasisPrem.compareTo(finstallmentPrem) < 0) {
                        throw new BadRequestException("Instalment Amount is below the required Minimum amount of "
                                + finstallmentPrem + "....." + cashBasisPrem);
                    }

                    if (riskInstallAmt.compareTo(premium) > 0) {
                        throw new BadRequestException(
                                "Installment amount is above the the maximum premium required for this risk of " + premium);
                    }

                    if (riskInstallAmt.compareTo(premium) < 0) {
                        batchDTO.setWetDate(DateUtils.addDays(DateUtils.addMonths(riskWefDate, 1), -1));
                    } else if (riskInstallAmt.compareTo(premium) == 0) {
                        policy.setTotalInstalments(1);
                    }

                    cashBasisPrem = riskInstallAmt;

                    if(butChargeOrMinPremExpected){
                        cashBasisPrem = cashBasisPrem.add(commissionPrem);
                    }

                    System.out.println("Cash Basis Prem.2.."+cashBasisPrem);
                    BigDecimal percentage = cashBasisPrem.divide(premium, 2, RoundingMode.HALF_EVEN)
                            .multiply(BigDecimal.valueOf(100));
                    batchDTO.setInstallmentPercentage(String.valueOf(percentage));
                    policy.setInstallmentNo(1L);
                    batchDTO.setTotalPercent(percentage);
                }

                if (commissionRate != null) {
                    BigDecimal commPremAmount = (policy.getInstallmentNo()!=null && policy.getInstallmentNo()==1l)?commissionPrem:BigDecimal.ZERO;
                    if(((cashBasisPrem.subtract(commPremAmount)).compareTo(BigDecimal.ZERO)) < 0){
                        throw new BadRequestException("Commission Premium cannot be negative...Please check on your premium...");
                    }
                    comm = (cashBasisPrem.subtract(commPremAmount)).multiply(commissionRate).divide(BigDecimal.valueOf(100));
                    if (policy.getSubAgent() != null) {
                        if (policy.getSubAgent().getCommissionEarning() != null
                                && policy.getSubAgent().getCommissionEarning().equalsIgnoreCase("Yes")) {
                            Long accId = policy.getSubAgent().getAccountType().getAccId();
                            Long binderId = policy.getBinder().getBinId();
                            String transType = policy.getTransType().toUpperCase().trim();
                            System.out.println("SubAgent Binder Id 2: " + binderId);
                            System.out.println("SubAgent AccountId 2: " + accId);
                            SubAgentCommissionRates subAgentCommissionRates = subAgentCommRepo.findOne(
                                    QSubAgentCommissionRates.subAgentCommissionRates.accountTypes.accId.eq(accId)
                                            .and(QSubAgentCommissionRates.subAgentCommissionRates.bindersDef.binId
                                                    .eq(binderId))
                                            .and(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.eq(transType)
                                                    .or(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.isNull())));
                            if ((policy.getSubAgent().getAccountType().getCommRate() == null || policy.getSubAgent()
                                    .getAccountType().getCommRate().compareTo(BigDecimal.ZERO) == 0)
                                    && (subAgentCommissionRates == null
                                            || subAgentCommissionRates.getCommRate().compareTo(BigDecimal.ZERO) == 0)) {
                                throw new BadRequestException("Sub Agent Commission Rate not setup");
                            }
                            if (subAgentCommissionRates != null) {
                                BigDecimal subAgentCommRate = subAgentCommissionRates.getCommRate();
                                System.out.println("sub agent comm 2: " + subAgentCommRate);

                                // Use the provided subAgentCommRate
                                subAgentComm = comm.multiply(subAgentCommRate.divide(BigDecimal.valueOf(100)));
                                System.out.println("Sub agent comm amount 2a: " + subAgentComm);
                            } else {
                                // Default to policy's sub Agent account type commission rate
                                subAgentComm = comm.multiply(policy.getSubAgent().getAccountType().getCommRate()
                                        .divide(BigDecimal.valueOf(100)));
                                System.out.println("Sub agent comm amount 2b: " + subAgentComm);
                            }
                        } else {
                            subAgentComm = BigDecimal.ZERO;
                        }
                    }
                }

                if (commissionRate != null) {
                    BigDecimal commPremAmount = (policy.getInstallmentNo()!=null && policy.getInstallmentNo()==1l)?commissionPrem:BigDecimal.ZERO;
                    if(((cashBasisPrem.subtract(commPremAmount)).compareTo(BigDecimal.ZERO)) < 0){
                        throw new BadRequestException("Commission Premium cannot be negative...Please check on your premium...");
                    }
                    comm = (cashBasisPrem.subtract(commPremAmount)).multiply(commissionRate).divide(BigDecimal.valueOf(100));
                    if (policy.getMarketerAgent() != null) {
                        if (policy.getMarketerAgent().getCommissionEarning() != null
                                && policy.getMarketerAgent().getCommissionEarning().equalsIgnoreCase("Yes")) {
                            Long accId = policy.getMarketerAgent().getAccountType().getAccId();
                            Long binderId = policy.getBinder().getBinId();
                            String transType = policy.getTransType().toUpperCase().trim();
                            System.out.println("Marketer Binder Id 2: " + binderId);
                            System.out.println("Marketer AccountId 2:" + accId);
                            SubAgentCommissionRates subAgentCommissionRates = subAgentCommRepo.findOne(
                                    QSubAgentCommissionRates.subAgentCommissionRates.accountTypes.accId.eq(accId)
                                            .and(QSubAgentCommissionRates.subAgentCommissionRates.bindersDef.binId
                                                    .eq(binderId))
                                            .and(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.eq(transType)
                                                    .or(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.isNull())));
                            if ((policy.getMarketerAgent().getAccountType().getCommRate() == null || policy
                                    .getMarketerAgent().getAccountType().getCommRate().compareTo(BigDecimal.ZERO) == 0)
                                    && (subAgentCommissionRates == null
                                            || subAgentCommissionRates.getCommRate().compareTo(BigDecimal.ZERO) == 0)) {
                                throw new BadRequestException("Marketer Commission Rate not setup");
                            }
                            if (subAgentCommissionRates != null) {
                                BigDecimal marketerCommRate = subAgentCommissionRates.getCommRate();
                                System.out.println("Marketer comm 2: " + marketerCommRate);

                                // Use the provided marketerCommRate
                                marketerComm = comm.multiply(marketerCommRate.divide(BigDecimal.valueOf(100)));
                                System.out.println("Marketer comm amount 2a: " + totalMarketerComm);
                            } else {
                                // Default to policy's marketer account type commission rate
                                marketerComm = comm.multiply(policy.getMarketerAgent().getAccountType().getCommRate()
                                        .divide(BigDecimal.valueOf(100)));
                                System.out.println("Marketer comm amount 2b: " + totalMarketerComm);
                            }
                        } else {
                            marketerComm = BigDecimal.ZERO;
                            System.out.println("Marketer comm amount not earning com2b: " + totalMarketerComm);
                        }
                    }
                }
            }
            else{
                if(butChargeOrMinPremExpected){
                    cashBasisPrem = cashBasisPrem.add(commissionPrem);
                }
            }
            if (riskPrem.compareTo(BigDecimal.ZERO) == 1) {
                comm = comm.negate();
                subAgentComm = subAgentComm.abs();
            } else if (riskPrem.compareTo(BigDecimal.ZERO) == -1) {
                comm = comm.abs();
                subAgentComm = subAgentComm.negate();
            }
            batchDTO.setComm(comm);
            batchDTO.setSubAgentComm(subAgentComm);
            batchDTO.setMarketerAgentComm(marketerComm);
            totalCommission = totalCommission.add(comm);
            totalSubAgentComm = totalSubAgentComm.add(subAgentComm);
            totalMarketerComm = totalMarketerComm.add(marketerComm);
            BigDecimal commPremAmount = BigDecimal.ZERO;
            if(cashBasis && policy.getInstallmentNo()!=null) {
                System.out.println("Passed here....");
                commPremAmount = (policy.getInstallmentNo() == 1l) ? commissionPrem : BigDecimal.ZERO;
            }
            else{
                commPremAmount = commissionPrem;
            }
            polextras = polextras.add(commPremAmount);

            System.out.println("Total Extras "+polextras);

            for (PolicyTaxes policyTax : policyTaxes) {
                BigDecimal computedTax = calculateTax((cashBasisPrem.subtract(commPremAmount)), policyTax.getTaxRate(), policyTax.getDivFactor(),
                        policyTax.getRateType());
                policyTax.setTaxAmount(computedTax);
                sumPolicytaxAmount = sumPolicytaxAmount.add(computedTax);
                // if (policyTax.getRevenueItems().getItem() == RevenueItems.EX) {
                // if ("R".equalsIgnoreCase(policyTax.getTaxLevel())) {
                // riskextras = riskextras.add(computedTax);
                // sumRisktaxAmount = sumRisktaxAmount.add(computedTax);
                // }
                // } else
                if (policyTax.getRevenueItems().getItem() == RevenueItems.SD) {
                    if ("R".equalsIgnoreCase(policyTax.getTaxLevel())) {
                        riskstampDuty = riskstampDuty.add(computedTax);
                        sumRisktaxAmount = sumRisktaxAmount.add(computedTax);
                    }
                } else if (policyTax.getRevenueItems().getItem() == RevenueItems.PHCF) {
                    if ("R".equalsIgnoreCase(policyTax.getTaxLevel())) {
                        riskphfFund = riskphfFund.add(computedTax);
                        sumRisktaxAmount = sumRisktaxAmount.add(computedTax);
                    }
                } else if (policyTax.getRevenueItems().getItem() == RevenueItems.WHTX) {
                    if ("R".equalsIgnoreCase(policyTax.getTaxLevel())) {
                        riskwhtxAmt = riskwhtxAmt.add(computedTax);
                        sumRisktaxAmount = sumRisktaxAmount.add(computedTax);
                    }
                } else if (policyTax.getRevenueItems().getItem() == RevenueItems.TL) {
                    if ("R".equalsIgnoreCase(policyTax.getTaxLevel())) {
                        riskTl = riskTl.add(computedTax);
                        sumRisktaxAmount = sumRisktaxAmount.add(computedTax);
                    }
                }
            }

            if (multiProduct) {
                if (accType == null) {
                    final Long binderId = ((BigInteger) risk[14]).longValue();
                    final BindersDef bindersDef = bindersRepo.findOne(binderId);
                    accType = (bindersDef.getAccount().getAccountType() != null)
                            ? bindersDef.getAccount().getAccountType()
                            : null;
                }
            }
            BigDecimal riskWhtx = BigDecimal.ZERO;

            if (accType != null && accType.isWhtxAppl()) {
                if (accType.getWhtaxVal().compareTo(BigDecimal.ZERO) == 1) {
                    riskWhtx = accType.getWhtaxVal().divide(new BigDecimal(100)).multiply(comm);
                    // risk.setWhtax(riskWhtx);
                }
            }

            System.out.println("cashBasisPrem:: " + cashBasisPrem);
            System.out.println("comm:: " + comm);
            System.out.println("sumRisktaxAmount:: " + sumRisktaxAmount);
            batchDTO.setNetPremium(cashBasisPrem.subtract(commPremAmount).add(sumRisktaxAmount).subtract(comm.abs()));
            batchDTO.setRiskextras(commPremAmount);

            batchDTO.setRiskphfFund(riskphfFund);
            batchDTO.setRiskTl(riskTl);
            batchDTO.setRiskstampDuty(riskstampDuty);
            batchDTO.setRiskFuturePrem(riskFuturePrem);
            batchDTO.setInstallAmt(riskInstallAmt);
            totalPrem = totalPrem.add((cashBasisPrem.subtract(commPremAmount)));
            System.out.println("Total Premium..."+totalPrem);
            futurePrem = futurePrem.add(riskFuturePrem);
            polwhtxAmt = polwhtxAmt.add(riskWhtx);
            if (batchDTO.getWetDate() == null) {
                batchDTO.setWetDate(riskWetDate);
            }
            riskBatchList.add(batchDTO);
        }
        BigDecimal policyLevelCommRate = BigDecimal.ZERO;
        if (totalPrem.compareTo(BigDecimal.ZERO) != 0)
            policyLevelCommRate = totalCommission.divide(totalPrem, 2, RoundingMode.HALF_EVEN);

        if (!multiProduct) {
            if (policy.getBinder().getMinPrem() != null) {
                if (policy.getBinder().getMinPrem().compareTo(totalPrem) == 1) {
                    totalPrem = policy.getBinder().getMinPrem();
                    totalCommission = policyLevelCommRate.multiply(totalPrem);
                    if (policy.getSubAgent() != null) {
                        if (policy.getSubAgent().getCommissionEarning() != null
                                && policy.getSubAgent().getCommissionEarning().equalsIgnoreCase("Yes")) {
                            Long accId = policy.getSubAgent().getAccountType().getAccId();
                            Long binderId = policy.getBinder().getBinId();
                            String transType = policy.getTransType().toUpperCase().trim();
                            System.out.println("SubAgent Binder Id 3: " + binderId);
                            System.out.println("SubAgent AccountId 3: " + accId);
                            SubAgentCommissionRates subAgentCommissionRates = subAgentCommRepo.findOne(
                                    QSubAgentCommissionRates.subAgentCommissionRates.accountTypes.accId.eq(accId)
                                            .and(QSubAgentCommissionRates.subAgentCommissionRates.bindersDef.binId
                                                    .eq(binderId))
                                            .and(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.eq(transType)
                                                    .or(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.isNull())));
                            if ((policy.getSubAgent().getAccountType().getCommRate() == null || policy.getSubAgent()
                                    .getAccountType().getCommRate().compareTo(BigDecimal.ZERO) == 0)
                                    && (subAgentCommissionRates == null
                                            || subAgentCommissionRates.getCommRate().compareTo(BigDecimal.ZERO) == 0)) {
                                throw new BadRequestException("Sub Agent Commission Rate not setup");
                            }
                            if (subAgentCommissionRates != null) {
                                BigDecimal subAgentCommRate = subAgentCommissionRates.getCommRate();
                                System.out.println("sub agent comm 3: " + subAgentCommRate);

                                // Use the provided subAgentCommRate
                                totalSubAgentComm = totalCommission
                                        .multiply(subAgentCommRate.divide(BigDecimal.valueOf(100)));
                                System.out.println("Sub agent comm amount 3a: " + totalSubAgentComm);
                            } else {
                                // Default to policy's sub Agent account type commission rate
                                totalSubAgentComm = totalCommission.multiply(policy.getSubAgent().getAccountType()
                                        .getCommRate().divide(BigDecimal.valueOf(100)));
                                System.out.println("Sub agent comm amount 3b: " + totalSubAgentComm);
                            }
                        } else {
                            totalSubAgentComm = BigDecimal.ZERO;
                            System.out.println("Sub agent comm amount not earning com3b: " + totalSubAgentComm);
                        }
                    }

                    if (policy.getMarketerAgent() != null) {
                        if (policy.getMarketerAgent().getCommissionEarning() != null
                                && policy.getMarketerAgent().getCommissionEarning().equalsIgnoreCase("Yes")) {
                            Long accId = policy.getMarketerAgent().getAccountType().getAccId();
                            Long binderId = policy.getBinder().getBinId();
                            String transType = policy.getTransType().toUpperCase().trim();
                            System.out.println("Marketer Binder Id 3: " + binderId);
                            System.out.println("Marketer AccountId 3:" + accId);
                            SubAgentCommissionRates subAgentCommissionRates = subAgentCommRepo.findOne(
                                    QSubAgentCommissionRates.subAgentCommissionRates.accountTypes.accId.eq(accId)
                                            .and(QSubAgentCommissionRates.subAgentCommissionRates.bindersDef.binId
                                                    .eq(binderId))
                                            .and(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.eq(transType)
                                                    .or(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.isNull())));
                            if ((policy.getMarketerAgent().getAccountType().getCommRate() == null || policy
                                    .getMarketerAgent().getAccountType().getCommRate().compareTo(BigDecimal.ZERO) == 0)
                                    && (subAgentCommissionRates == null
                                            || subAgentCommissionRates.getCommRate().compareTo(BigDecimal.ZERO) == 0)) {
                                throw new BadRequestException("Marketer Commission Rate not setup");
                            }
                            if (subAgentCommissionRates != null) {
                                BigDecimal marketerCommRate = subAgentCommissionRates.getCommRate();
                                System.out.println("Marketer comm 3: " + marketerCommRate);

                                // Use the provided marketerCommRate
                                totalMarketerComm = totalCommission
                                        .multiply(marketerCommRate.divide(BigDecimal.valueOf(100)));
                                System.out.println("Marketer comm amount 3a: " + totalMarketerComm);
                            } else {
                                // Default to policy's marketer account type commission rate
                                totalMarketerComm = totalCommission.multiply(policy.getMarketerAgent().getAccountType()
                                        .getCommRate().divide(BigDecimal.valueOf(100)));
                                System.out.println("Marketer comm amount 3b: " + totalMarketerComm);
                            }
                        } else {
                            totalMarketerComm = BigDecimal.ZERO;
                            System.out.println("Marketer comm amount not earning com3b: " + totalMarketerComm);
                        }
                    }
                }
            }

            if (policy.getProduct().getMinPrem() != null) {
                if (policy.getProduct().getMinPrem().compareTo(totalPrem) == 1) {
                    totalPrem = policy.getProduct().getMinPrem();
                    totalCommission = policyLevelCommRate.multiply(totalPrem);
                    if (policy.getSubAgent() != null) {
                        if (policy.getSubAgent().getCommissionEarning() != null
                                && policy.getSubAgent().getCommissionEarning().equalsIgnoreCase("Yes")) {
                            Long accId = policy.getSubAgent().getAccountType().getAccId();
                            Long binderId = policy.getBinder().getBinId();
                            String transType = policy.getTransType().toUpperCase().trim();
                            System.out.println("SubAgent Binder Id 4: " + binderId);
                            System.out.println("SubAgent AccountId 4: " + accId);
                            SubAgentCommissionRates subAgentCommissionRates = subAgentCommRepo.findOne(
                                    QSubAgentCommissionRates.subAgentCommissionRates.accountTypes.accId.eq(accId)
                                            .and(QSubAgentCommissionRates.subAgentCommissionRates.bindersDef.binId
                                                    .eq(binderId))
                                            .and(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.eq(transType)
                                                    .or(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.isNull())));
                            if ((policy.getSubAgent().getAccountType().getCommRate() == null || policy.getSubAgent()
                                    .getAccountType().getCommRate().compareTo(BigDecimal.ZERO) == 0)
                                    && (subAgentCommissionRates == null
                                            || subAgentCommissionRates.getCommRate().compareTo(BigDecimal.ZERO) == 0)) {
                                throw new BadRequestException("Sub Agent Commission Rate not setup");
                            }
                            if (subAgentCommissionRates != null) {
                                BigDecimal subAgentCommRate = subAgentCommissionRates.getCommRate();
                                System.out.println("sub agent comm 4: " + subAgentCommRate);

                                // Use the provided subAgentCommRate
                                totalSubAgentComm = totalCommission
                                        .multiply(subAgentCommRate.divide(BigDecimal.valueOf(100)));
                                System.out.println("Sub agent comm amount 4a: " + totalSubAgentComm);
                            } else {
                                // Default to policy's sub Agent account type commission rate
                                totalSubAgentComm = totalCommission.multiply(policy.getSubAgent().getAccountType()
                                        .getCommRate().divide(BigDecimal.valueOf(100)));
                                System.out.println("Sub agent comm amount 4b: " + totalSubAgentComm);
                            }
                        } else {
                            totalSubAgentComm = BigDecimal.ZERO;
                            System.out.println("Sub agent comm amount not earning com4b: " + totalSubAgentComm);
                        }
                    }

                    if (policy.getMarketerAgent() != null) {
                        if (policy.getMarketerAgent().getCommissionEarning() != null
                                && policy.getMarketerAgent().getCommissionEarning().equalsIgnoreCase("Yes")) {
                            Long accId = policy.getMarketerAgent().getAccountType().getAccId();
                            Long binderId = policy.getBinder().getBinId();
                            String transType = policy.getTransType().toUpperCase().trim();
                            System.out.println("Marketer Binder Id 4: " + binderId);
                            System.out.println("Marketer AccountId 4: " + accId);
                            SubAgentCommissionRates subAgentCommissionRates = subAgentCommRepo.findOne(
                                    QSubAgentCommissionRates.subAgentCommissionRates.accountTypes.accId.eq(accId)
                                            .and(QSubAgentCommissionRates.subAgentCommissionRates.bindersDef.binId
                                                    .eq(binderId))
                                            .and(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.eq(transType)
                                                    .or(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.isNull())));
                            if ((policy.getMarketerAgent().getAccountType().getCommRate() == null || policy
                                    .getMarketerAgent().getAccountType().getCommRate().compareTo(BigDecimal.ZERO) == 0)
                                    && (subAgentCommissionRates == null
                                            || subAgentCommissionRates.getCommRate().compareTo(BigDecimal.ZERO) == 0)) {
                                throw new BadRequestException("Marketer Commission Rate not setup");
                            }
                            if (subAgentCommissionRates != null) {
                                BigDecimal marketerCommRate = subAgentCommissionRates.getCommRate();
                                System.out.println("Marketer comm 4: " + marketerCommRate);

                                // Use the provided marketerCommRate
                                totalMarketerComm = totalCommission
                                        .multiply(marketerCommRate.divide(BigDecimal.valueOf(100)));
                                System.out.println("Marketer comm amount 4a " + totalMarketerComm);
                            } else {
                                // Default to policy's marketer account type commission rate
                                totalMarketerComm = totalCommission.multiply(policy.getMarketerAgent().getAccountType()
                                        .getCommRate().divide(BigDecimal.valueOf(100)));
                                System.out.println("Marketer comm amount 4b " + totalMarketerComm);
                            }
                        } else {
                            totalMarketerComm = BigDecimal.ZERO;
                            System.out.println("Marketer comm amount not earning com4b " + totalMarketerComm);
                        }
                    }

                }
            }
        }

        // polTaxesRepo.save(policyTaxes);
        BigDecimal whtxAmt = BigDecimal.ZERO;
        if (!multiProduct) {
            if (accType != null && accType.isWhtxAppl()) {
                if (accType.getWhtaxVal().compareTo(BigDecimal.ZERO) == 1) {
                    whtxAmt = accType.getWhtaxVal().divide(new BigDecimal(100)).multiply(totalCommission);
                    polwhtxAmt = whtxAmt.negate();
                }
            }
        }

        // if
        // (StringUtils.equalsIgnoreCase(paramService.getParameterString("SUB_AGENT_COMM_PARAM"),
        // "N")) {
        if (policy.getSubAgent() != null) {
            if (policy.getSubAgent().getCommissionEarning() != null
                    && policy.getSubAgent().getCommissionEarning().equalsIgnoreCase("Yes")) {
                Long accId = policy.getSubAgent().getAccountType().getAccId();
                Long binderId = policy.getBinder().getBinId();
                String transType = policy.getTransType().toUpperCase().trim(); // Added to use transType in query
                System.out.println("SubAgent Binder Id 5: " + binderId);
                System.out.println("SubAgent AccountId 5: " + accId);
                SubAgentCommissionRates subAgentCommissionRates = subAgentCommRepo
                        .findOne(QSubAgentCommissionRates.subAgentCommissionRates.accountTypes.accId.eq(accId)
                                .and(QSubAgentCommissionRates.subAgentCommissionRates.bindersDef.binId.eq(binderId))
                                .and(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.eq(transType)
                                        .or(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.isNull())));
                if ((policy.getSubAgent().getAccountType().getCommRate() == null
                        || policy.getSubAgent().getAccountType().getCommRate().compareTo(BigDecimal.ZERO) == 0)
                        && (subAgentCommissionRates == null
                        || subAgentCommissionRates.getCommRate().compareTo(BigDecimal.ZERO) == 0)) {
                    throw new BadRequestException("Sub Agent Commission Rate not setup");
                }
                if (subAgentCommissionRates != null) {
                    BigDecimal subAgentCommRate = subAgentCommissionRates.getCommRate();
                    System.out.println("sub agent comm 5: " + subAgentCommRate);

                    // Use the provided subAgentCommRate
                    totalSubAgentComm = totalCommission.multiply(subAgentCommRate.divide(BigDecimal.valueOf(100)));
                    System.out.println("Sub agent comm amount 5a: " + totalSubAgentComm);
                } else {
                    // Default to policy's sub Agent account type commission rate
                    totalSubAgentComm = totalCommission.multiply(
                            policy.getSubAgent().getAccountType().getCommRate().divide(BigDecimal.valueOf(100)));
                    System.out.println("Sub agent comm amount 5b: " + totalSubAgentComm);
                }
            } else {
                totalSubAgentComm = BigDecimal.ZERO;
                System.out.println("Sub agent comm amount not earning com5b: " + totalSubAgentComm);
            }
        }

        if (policy.getMarketerAgent() != null) {
            if (policy.getMarketerAgent().getCommissionEarning() != null
                    && policy.getMarketerAgent().getCommissionEarning().equalsIgnoreCase("Yes")) {
                Long accId = policy.getMarketerAgent().getAccountType().getAccId();
                Long binderId = policy.getBinder().getBinId();
                String transType = policy.getTransType().toUpperCase().trim(); // Added to use transType in query
                System.out.println("Marketer Binder Id 5: " + binderId);
                System.out.println("Marketer AccountId 5:" + accId);
                SubAgentCommissionRates subAgentCommissionRates = subAgentCommRepo
                        .findOne(QSubAgentCommissionRates.subAgentCommissionRates.accountTypes.accId.eq(accId)
                                .and(QSubAgentCommissionRates.subAgentCommissionRates.bindersDef.binId.eq(binderId))
                                .and(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.eq(transType)
                                        .or(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.isNull())));
                if ((policy.getMarketerAgent().getAccountType().getCommRate() == null
                        || policy.getMarketerAgent().getAccountType().getCommRate().compareTo(BigDecimal.ZERO) == 0)
                        && (subAgentCommissionRates == null
                        || subAgentCommissionRates.getCommRate().compareTo(BigDecimal.ZERO) == 0)) {
                    throw new BadRequestException("Marketer Commission Rate not setup");
                }
                if (subAgentCommissionRates != null) {
                    BigDecimal marketerCommRate = subAgentCommissionRates.getCommRate();
                    System.out.println("Marketer comm 5: " + marketerCommRate);

                    // Use the provided marketerCommRate
                    totalMarketerComm = totalCommission.multiply(marketerCommRate.divide(BigDecimal.valueOf(100)));
                    System.out.println("Marketer comm amount 5a: " + totalMarketerComm);
                } else {
                    // Default to policy's marketer account type commission rate
                    totalMarketerComm = totalCommission.multiply(
                            policy.getMarketerAgent().getAccountType().getCommRate().divide(BigDecimal.valueOf(100)));
                    System.out.println("Marketer comm amount 5b: " + totalMarketerComm);
                }
            } else {
                totalMarketerComm = BigDecimal.ZERO;
                System.out.println("Marketer comm amount not earning com5b: " + totalMarketerComm);
            }
        }

        if (totalPrem.compareTo(BigDecimal.ZERO) == 1) {
            totalCommission = totalCommission.abs().negate();
            totalSubAgentComm = totalSubAgentComm.abs();
            totalMarketerComm = totalMarketerComm.abs();
        } else if (totalPrem.compareTo(BigDecimal.ZERO) == -1) {
            totalCommission = totalCommission.abs();
            totalSubAgentComm = totalSubAgentComm.abs().negate();
            totalMarketerComm = totalMarketerComm.abs().negate();
        }

        sumPolicytaxAmount = sumPolicytaxAmount.add(polwhtxAmt);
        BigDecimal futureTotalTax = BigDecimal.ZERO;
        for (PolicyTaxes policyTax : policyTaxes) {
            BigDecimal computedTax = calculateTax(totalPrem, policyTax.getTaxRate(), policyTax.getDivFactor(),
                    policyTax.getRateType());
            policyTax.setTaxAmount(computedTax);
            BigDecimal computedFutureTax = calculateTax(futurePrem, policyTax.getTaxRate(), policyTax.getDivFactor(),
                    policyTax.getRateType());
            if (policyTax.getRevenueItems().getItem() == RevenueItems.SD) {

                polstampDuty = polstampDuty.add(computedTax);

            } else if (policyTax.getRevenueItems().getItem() == RevenueItems.PHCF) {

                polphfFund = polphfFund.add(computedTax);
                futureTotalTax = futureTotalTax.add(computedFutureTax);

            } else if (policyTax.getRevenueItems().getItem() == RevenueItems.TL) {

                polTl = polTl.add(computedTax);
                futureTotalTax = futureTotalTax.add(computedFutureTax);

            }
        }
        polTaxesRepo.save(policyTaxes);
        for (RiskBatchDTO batchDTO : riskBatchList) {
            riskRepo.updateRiskDetails(batchDTO.getRiskPrem(), batchDTO.getCalcPrem(), batchDTO.getRiskInsured(),
                    (batchDTO.getInstallAmt() != null) ? batchDTO.getInstallAmt() : BigDecimal.ZERO,
                    batchDTO.getWetDate(),
                    (batchDTO.getTotalPercent() != null) ? batchDTO.getTotalPercent() : BigDecimal.ZERO,
                    batchDTO.getComm(),
                    (batchDTO.getSubAgentComm() != null) ? batchDTO.getSubAgentComm() : BigDecimal.ZERO,
                    batchDTO.getNetPremium(),
                    (batchDTO.getRiskextras() != null) ? batchDTO.getRiskextras() : BigDecimal.ZERO,
                    batchDTO.getRiskphfFund(), batchDTO.getRiskTl(), batchDTO.getRiskstampDuty(),
                    batchDTO.getRiskFuturePrem(),
                    batchDTO.getInstallmentPercentage(), batchDTO.getRiskId());
        }
        if (multiProduct) {
            Iterable<PolicyBinders> policyBinders = policyBindersRepo
                    .findAll(QPolicyBinders.policyBinders.policyTrans.policyId.eq(polCode));
            for (PolicyBinders binders : policyBinders) {
                BigDecimal totalBinderRisk = Streamable
                        .streamOf(riskRepo
                                .findAll(QRiskTrans.riskTrans.policyBinders.policyBindId.eq(binders.getPolicyBindId())))
                        .map(a -> a.getCalcPremium()).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal totaltl = Streamable
                        .streamOf(riskRepo
                                .findAll(QRiskTrans.riskTrans.policyBinders.policyBindId.eq(binders.getPolicyBindId())))
                        .map(a -> a.getTrainingLevy()).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal totalPhcf = Streamable
                        .streamOf(riskRepo
                                .findAll(QRiskTrans.riskTrans.policyBinders.policyBindId.eq(binders.getPolicyBindId())))
                        .map(a -> a.getPhfFund()).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal totalComm = Streamable
                        .streamOf(riskRepo
                                .findAll(QRiskTrans.riskTrans.policyBinders.policyBindId.eq(binders.getPolicyBindId())))
                        .map(a -> a.getCommAmt()).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal totalWhtx = Streamable
                        .streamOf(riskRepo
                                .findAll(QRiskTrans.riskTrans.policyBinders.policyBindId.eq(binders.getPolicyBindId())))
                        .map(a -> (a.getWhtax() != null) ? a.getWhtax() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                binders.setBasicPrem(totalBinderRisk);
                binders.setCommission(totalComm);
                binders.setTl(totaltl);
                binders.setPhcf(totalPhcf);
                binders.setWhtx(totalWhtx);
            }
            policyBindersRepo.save(policyBinders);
        }
        Currencies currencies = policy.getTransCurrency();
        totalPrem = totalPrem.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN);
        totalCommission = totalCommission.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN);
        totalSubAgentComm = totalSubAgentComm.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN);
        totalMarketerComm = totalMarketerComm.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN);

        if(policy.getAdminFeeApplicable()!=null && "Y".equalsIgnoreCase(policy.getAdminFeeApplicable())) {
            long count = adminFeeSetUpRepo.count(QAdminFeeSetUp.adminFeeSetUp.binder.binId.eq(policy.getBinder().getBinId()).and(QAdminFeeSetUp.adminFeeSetUp.status.eq("Active")));

            if (count != 1) {
                throw new BadRequestException("Please configure set up for admin fee to continue...");
            }

            AdminFeeSetUp adminFeeSetUp = adminFeeSetUpRepo.findOne(QAdminFeeSetUp.adminFeeSetUp.binder.binId.eq(policy.getBinder().getBinId()).and(QAdminFeeSetUp.adminFeeSetUp.status.eq("Active")));

            //admin fee check
            BigDecimal adminFeeTotal = BigDecimal.ZERO;
            BigDecimal vatTotal = BigDecimal.ZERO;
            if(adminFeeSetUp != null && adminFeeSetUp.getAdminFeeRateType() != null) {
                final double rate = (adminFeeSetUp.getAdminFeeRateType().equals("Percent")) ? 100 : 1;
                if (adminFeeSetUp.getAdminFeeRateType().equals("Percent")) {
                    adminFeeTotal = BigDecimal.valueOf((adminFeeSetUp.getAdminFeeRate().doubleValue() / rate) * totalPrem.doubleValue());
                } else {
                    adminFeeTotal = adminFeeSetUp.getAdminFeeRate();
                }
                final double vatrate = (adminFeeSetUp.getVateRateType().equals("Percent")) ? 100 : 1;
                     vatTotal = BigDecimal.valueOf((adminFeeSetUp.getVatRate().doubleValue() / vatrate) * adminFeeTotal.doubleValue());
                if ((!adminFeeSetUp.getVateRateType().equals("Percent"))) {
                    vatTotal = adminFeeSetUp.getVatRate();
                }
                policy.setAdminFeeAmt(adminFeeTotal);
                policy.setAdminFeeVatAmt(vatTotal);
            }
        }
        else{
            policy.setAdminFeeAmt(BigDecimal.ZERO);
            policy.setAdminFeeVatAmt(BigDecimal.ZERO);
        }

        policy.setPremium(totalPrem);
        policy.setBasicPrem((totalPrem.add(polextras).add(polstampDuty).add(polphfFund).add(polTl))
                .setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setEndosbasicPremium(totalPrem);
        policy.setEndosgrossPremium((totalPrem.add(polextras).add(polstampDuty).add(polphfFund).add(polTl))
                .setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setPaidPremium(receiptService.getPolicyTotalRcptAmount(policy.getPolNo()));
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
        if (policy.isRenewable()) {
            policy.setFuturePrem(futurePrem.add(futureTotalTax).setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            System.out.println("Endose prem...comput..."+policy.getEndosgrossPremium());
            policy.setFuturePrem(policy.getEndosgrossPremium());
        }
        else {
            policy.setFuturePrem(BigDecimal.ZERO);
        }

        policyRepo.save(policy);
        List<Object[]> savedRisks = riskRepo.findRiskTrans(policy.getPolicyId());
        for (Object[] savedRisk : savedRisks) {
            policyService.populateRiskScheduleDetails(((BigInteger) savedRisk[0]).longValue());
        }

    }

    /**
     * Premium Computation at Risk Level
     *
     * @return Risk Premium
     */
    @Override
    public BigDecimal computeRiskPrem(Long riskCode) {
        return null;
    }

    @Override
    public BigDecimal calculateTax(BigDecimal premAmount, BigDecimal rate,
            BigDecimal divFactor, String rateType) {
        if ("P".equalsIgnoreCase(rateType)) {
            if (premAmount == null || premAmount.compareTo(BigDecimal.ZERO) == 0) {
                return BigDecimal.ZERO;
            } else
                return premAmount.multiply(rate).divide(divFactor);
        } else if ("A".equalsIgnoreCase(rateType)) {
            if (premAmount == null || premAmount.compareTo(BigDecimal.ZERO) == 0) {
                return BigDecimal.ZERO;
            } else {
                if (premAmount.compareTo(BigDecimal.ZERO) == 1)
                    return rate;
                else
                    return rate.negate();
            }

        } else
            return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getCommissionRate(long BinderDet, BigDecimal premAmount) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void computeEndorsePremium(Long polCode) throws BadRequestException, IOException {
        PolicyTrans policy = policyRepo.findOne(polCode);
        if (policy == null) {
            throw new BadRequestException("Error getting Policy Details");
        }

        if ("NB".equalsIgnoreCase(policy.getTransType()) || "SP".equalsIgnoreCase(policy.getTransType())) {
            throw new BadRequestException("Error Computing Endorsement Premium for " + policy.getTransType());
        }

        PolicyTrans prevPolicy = null;
        if ("RE".equalsIgnoreCase(policy.getPolRevStatus())) {
            prevPolicy = policy.getReusecontraPolicy().getPreviousTrans();
        } else
            prevPolicy = policy.getPreviousTrans();
        if (prevPolicy == null) {
            throw new BadRequestException("Error getting Previous Policy Transaction Details...");
        }
        policyService.populateTaxes(policy);
        boolean countRenewals = policyRepo.countRenewals(policy.getPolNo()) > 0;
        long calDays = TimeUnit.DAYS.convert((policy.getCoverTo().getTime() - policy.getCoverFrom().getTime()),
                TimeUnit.MILLISECONDS) + 1;
        boolean cashBasis = policy.getInterfaceType() != null && "C".equalsIgnoreCase(policy.getInterfaceType());

        Iterable<RiskTrans> prevRisks = riskRepo
                .findAll(QRiskTrans.riskTrans.policy.policyId.eq(prevPolicy.getPolicyId()));

        Iterable<RiskTrans> currRisks = riskRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(polCode));
        Iterable<PolicyTaxes> policyTaxes = polTaxesRepo.findAll(QPolicyTaxes.policyTaxes.policy.policyId.eq(polCode));
        BigDecimal totalCommission = BigDecimal.ZERO;
        BigDecimal sumPolicytaxAmount = BigDecimal.ZERO;
        BigDecimal polstampDuty = BigDecimal.ZERO;
        BigDecimal polphfFund = BigDecimal.ZERO;
        BigDecimal polwhtxAmt = BigDecimal.ZERO;
        BigDecimal polextras = BigDecimal.ZERO;
        BigDecimal polTl = BigDecimal.ZERO;
        BigDecimal totalEndosPremium = BigDecimal.ZERO;
        BigDecimal sumInsured = BigDecimal.ZERO;
        BigDecimal totalFullPrem = BigDecimal.ZERO;
        BigDecimal totalSubAgentComm = BigDecimal.ZERO;
        BigDecimal totalMarketerComm = BigDecimal.ZERO;
        BigDecimal futurePremium = BigDecimal.ZERO;
        AccountTypes accType = (policy.getAgent() != null) ? policy.getAgent().getAccountType() : null;
        for (RiskTrans currRisk : currRisks) {

            BigDecimal prevriskPrem = BigDecimal.ZERO;
            BigDecimal currRiskPrem = BigDecimal.ZERO;
            BigDecimal riskInsured = BigDecimal.ZERO;
            BigDecimal newRiskPrem = BigDecimal.ZERO;
            BigDecimal futureRiskPrem = BigDecimal.ZERO;
            Iterable<SectionTrans> sections = sectionRepo
                    .findAll(QSectionTrans.sectionTrans.risk.riskId.eq(currRisk.getRiskId()));
            List<PremiumItemsBean> itemsBeen = new ArrayList<>();
            List<PremiumItemsBean> discountItems = new ArrayList<>();
            final String ratesTable = premRatesTableRepo.getRatesLocation(currRisk.getBinderDetails().getDetId());
            if (ratesTable == null)
                throw new BadRequestException("Rates Table for the Binder has not been setup");
            ScheduleBean scheduleBean = scheduleService.generateScheduleColumns(currRisk.getRiskId());
            long count = 0;
            if (scheduleBean.getMappings() != null) {
                count = scheduleBean.getMappings().stream()
                        .filter(a -> a.getPremItem() != null && "Y".equalsIgnoreCase(a.getPremItem())).count();
            }
            if (count > 0) {
                if (scheduleTransRepo.count(QScheduleTrans.scheduleTrans.risk.riskId.eq(currRisk.getRiskId())) > 1)
                    throw new BadRequestException(
                            "Cannot compute premium. You have entered mutliple schedule details...");
            }
            long counter = count;
            for (SectionTrans section : sections) {
                List<String> items = new ArrayList<>();
                double minPrem = 0;
                if (scheduleBean.getMappings() != null) {
                    scheduleBean.getMappings().stream()
                            .filter(a -> a.getPremItem() != null && "Y".equalsIgnoreCase(a.getPremItem()))
                            .filter(a -> a.getPremCode() != null && a.getPremCode() == section.getSection().getId())
                            .forEach(a -> {
                                if (counter > 1) {
                                    ScheduleTrans scheduleTrans = scheduleTransRepo
                                            .findOne(QScheduleTrans.scheduleTrans.risk.riskId.eq(currRisk.getRiskId()));
                                    if (scheduleTrans != null)
                                        switch (a.getKey()) {
                                            case "1":
                                                if (scheduleTrans.getColumn1() != null)
                                                    items.add(scheduleTrans.getColumn1());
                                                break;
                                            case "2":
                                                if (scheduleTrans.getColumn2() != null)
                                                    items.add(scheduleTrans.getColumn2());
                                                break;
                                            case "3":
                                                if (scheduleTrans.getColumn3() != null)
                                                    items.add(scheduleTrans.getColumn3());
                                                break;
                                            case "4":
                                                if (scheduleTrans.getColumn4() != null)
                                                    items.add(scheduleTrans.getColumn4());
                                                break;
                                            case "5":
                                                if (scheduleTrans.getColumn5() != null)
                                                    items.add(scheduleTrans.getColumn5());
                                                break;
                                            case "6":
                                                if (scheduleTrans.getColumn6() != null)
                                                    items.add(scheduleTrans.getColumn6());
                                                break;
                                            case "7":
                                                if (scheduleTrans.getColumn7() != null)
                                                    items.add(scheduleTrans.getColumn7());
                                                break;
                                            case "8":
                                                if (scheduleTrans.getColumn8() != null)
                                                    items.add(scheduleTrans.getColumn8());
                                                break;
                                            case "9":
                                                if (scheduleTrans.getColumn9() != null)
                                                    items.add(scheduleTrans.getColumn9());
                                                break;
                                            case "10":
                                                if (scheduleTrans.getColumn10() != null)
                                                    items.add(scheduleTrans.getColumn10());
                                                break;
                                            case "11":
                                                if (scheduleTrans.getColumn11() != null)
                                                    items.add(scheduleTrans.getColumn11());
                                                break;
                                            case "12":
                                                if (scheduleTrans.getColumn12() != null)
                                                    items.add(scheduleTrans.getColumn12());
                                                break;
                                            case "13":
                                                if (scheduleTrans.getColumn13() != null)
                                                    items.add(scheduleTrans.getColumn13());
                                                break;
                                            case "14":
                                                if (scheduleTrans.getColumn14() != null)
                                                    items.add(scheduleTrans.getColumn14());
                                                break;
                                            case "15":
                                                if (scheduleTrans.getColumn15() != null)
                                                    items.add(scheduleTrans.getColumn15());
                                                break;
                                        }
                                }

                            });

                    if (section.getPremRates() != null) {
                        PremRatesDef premRatesDef = section.getPremRates();
                        if (premRatesDef.getMinPremium() != null
                                && premRatesDef.getMinPremium().compareTo(BigDecimal.ZERO) == 1) {
                            minPrem = premRatesDef.getMinPremium().doubleValue();
                        }
                    }
                }
                if (!section.getSection().getType().equals(SectionTypes.DS)) {
                    itemsBeen.add(
                            new PremiumItemsBean(section.getSection().getShtDesc(), section.getRate().doubleValue(),
                                    section.getFreeLimit().doubleValue(), minPrem,
                                    section.getAmount().doubleValue(), section.getSectId(),
                                    section.getDivFactor().doubleValue(),
                                    section.getSection().getType().getCode(), section.getSection().getType().getOrder(),
                                    items, policy.getPolicyId()));
                } else if (section.getSection().getType().equals(SectionTypes.DS)) {
                    discountItems.add(
                            new PremiumItemsBean(section.getSection().getShtDesc(), section.getRate().doubleValue(),
                                    section.getFreeLimit().doubleValue(), minPrem,
                                    section.getAmount().doubleValue(), section.getSectId(),
                                    section.getDivFactor().doubleValue(),
                                    section.getSection().getType().getCode(), section.getSection().getType().getOrder(),
                                    items, policy.getPolicyId()));
                }
            }

            itemsBeen.stream().sorted(Comparator.comparing(PremiumItemsBean::getOrder));
            PremiumResultBean resultBean = premExcelUtils.getPremium(itemsBeen, ratesTable);
            currRiskPrem = BigDecimal.valueOf(resultBean.getPremium());
            sumInsured = BigDecimal.valueOf(resultBean.getSumInsured());
            riskInsured = sumInsured;
            futureRiskPrem = currRiskPrem;

            System.out.println("Current risk prem.." + currRiskPrem);

            // if(currRisk.getBinderDetails().getMinPrem()!=null &&
            // currRisk.getBinderDetails().getMinPrem().compareTo(currRiskPrem) ==1){
            // currRiskPrem = currRisk.getBinderDetails().getMinPrem();
            // }
            if ("NB".equalsIgnoreCase(currRisk.getTransType())) {
                newRiskPrem = currRiskPrem;
            }
            RiskTrans foundRisk = null;
            Iterator<RiskTrans> riskIterator = prevRisks.iterator();
            while (riskIterator.hasNext()) {
                RiskTrans risk = (RiskTrans) riskIterator.next();
                if (currRisk.getRiskIdentifier().longValue() == risk.getRiskIdentifier().longValue()) {
                    foundRisk = risk;
                    break;
                }
            }
            if (("EN".equalsIgnoreCase(currRisk.getTransType())) && foundRisk == null)
                throw new BadRequestException("Error Occured Populating Endorsements");

            if ("EN".equalsIgnoreCase(currRisk.getTransType()) || "EX".equalsIgnoreCase(currRisk.getTransType())) {
                Iterable<SectionTrans> prevSections = sectionRepo
                        .findAll(QSectionTrans.sectionTrans.risk.riskId.eq(foundRisk.getRiskId()));
                long counter2 = count;
                RiskTrans foundRisk2 = foundRisk;
                itemsBeen = new ArrayList<>();
                for (SectionTrans section : prevSections) {
                    List<String> items = new ArrayList<>();
                    double minPrem = 0;
                    if (scheduleBean.getMappings() != null) {
                        scheduleBean.getMappings().stream()
                                .filter(a -> a.getPremItem() != null && "Y".equalsIgnoreCase(a.getPremItem()))
                                .filter(a -> a.getPremCode() != null && a.getPremCode() == section.getSection().getId())
                                .forEach(a -> {
                                    if (counter2 > 1) {
                                        ScheduleTrans scheduleTrans = scheduleTransRepo.findOne(
                                                QScheduleTrans.scheduleTrans.risk.riskId.eq(foundRisk2.getRiskId()));
                                        if (scheduleTrans != null)
                                            switch (a.getKey()) {
                                                case "1":
                                                    if (scheduleTrans.getColumn1() != null)
                                                        items.add(scheduleTrans.getColumn1());
                                                    break;
                                                case "2":
                                                    if (scheduleTrans.getColumn2() != null)
                                                        items.add(scheduleTrans.getColumn2());
                                                    break;
                                                case "3":
                                                    if (scheduleTrans.getColumn3() != null)
                                                        items.add(scheduleTrans.getColumn3());
                                                    break;
                                                case "4":
                                                    if (scheduleTrans.getColumn4() != null)
                                                        items.add(scheduleTrans.getColumn4());
                                                    break;
                                                case "5":
                                                    if (scheduleTrans.getColumn5() != null)
                                                        items.add(scheduleTrans.getColumn5());
                                                    break;
                                                case "6":
                                                    if (scheduleTrans.getColumn6() != null)
                                                        items.add(scheduleTrans.getColumn6());
                                                    break;
                                                case "7":
                                                    if (scheduleTrans.getColumn7() != null)
                                                        items.add(scheduleTrans.getColumn7());
                                                    break;
                                                case "8":
                                                    if (scheduleTrans.getColumn8() != null)
                                                        items.add(scheduleTrans.getColumn8());
                                                    break;
                                                case "9":
                                                    if (scheduleTrans.getColumn9() != null)
                                                        items.add(scheduleTrans.getColumn9());
                                                    break;
                                                case "10":
                                                    if (scheduleTrans.getColumn10() != null)
                                                        items.add(scheduleTrans.getColumn10());
                                                    break;
                                                case "11":
                                                    if (scheduleTrans.getColumn11() != null)
                                                        items.add(scheduleTrans.getColumn11());
                                                    break;
                                                case "12":
                                                    if (scheduleTrans.getColumn12() != null)
                                                        items.add(scheduleTrans.getColumn12());
                                                    break;
                                                case "13":
                                                    if (scheduleTrans.getColumn13() != null)
                                                        items.add(scheduleTrans.getColumn13());
                                                    break;
                                                case "14":
                                                    if (scheduleTrans.getColumn14() != null)
                                                        items.add(scheduleTrans.getColumn14());
                                                    break;
                                                case "15":
                                                    if (scheduleTrans.getColumn15() != null)
                                                        items.add(scheduleTrans.getColumn15());
                                                    break;
                                            }
                                    }

                                });

                        if (section.getPremRates() != null) {
                            PremRatesDef premRatesDef = section.getPremRates();
                            if (premRatesDef.getMinPremium() != null
                                    && premRatesDef.getMinPremium().compareTo(BigDecimal.ZERO) == 1) {
                                minPrem = premRatesDef.getMinPremium().doubleValue();
                            }
                        }
                    }
                    itemsBeen.add(
                            new PremiumItemsBean(section.getSection().getShtDesc(), section.getRate().doubleValue(),
                                    section.getFreeLimit().doubleValue(), minPrem,
                                    section.getAmount().doubleValue(), section.getSectId(),
                                    section.getDivFactor().doubleValue(),
                                    section.getSection().getType().getCode(), section.getSection().getType().getOrder(),
                                    items, policy.getPolicyId()));
                }

                itemsBeen.stream().sorted(Comparator.comparing(PremiumItemsBean::getOrder));
                resultBean = premExcelUtils.getPremium(itemsBeen, ratesTable);
                prevriskPrem = BigDecimal.valueOf(resultBean.getPremium());

                // if(foundRisk2.getBinderDetails().getMinPrem()!=null &&
                // currRisk.getBinderDetails().getMinPrem().compareTo(prevriskPrem) ==1){
                // prevriskPrem = foundRisk2.getBinderDetails().getMinPrem();
                // }
                // System.out.println("Prev Risk Prem 5"+prevriskPrem);
            }
            long datediff = currRisk.getWetDate().getTime() - currRisk.getWefDate().getTime();
            long daysDiff = TimeUnit.DAYS.convert(datediff, TimeUnit.MILLISECONDS) + 1;

            double rata = (double) daysDiff / calDays;

            BigDecimal prorata = new BigDecimal(rata);
            BigDecimal currprorataPrem = currRiskPrem;
            BigDecimal endosPrem = BigDecimal.ZERO;
            BigDecimal totEndosPrem = BigDecimal.ZERO;
            BigDecimal endosSI = BigDecimal.ZERO;
            System.out.println("Current risk prem again..." + currprorataPrem);
            if (currRisk.getButchargePrem() == null || currRisk.getButchargePrem().compareTo(BigDecimal.ZERO) == 0) {

            } else {
                System.out.println("setting butcharge amount. ");
                currprorataPrem = currRisk.getButchargePrem();
                newRiskPrem = currRisk.getButchargePrem();
            }
            boolean butchargedPrem = (currRisk.getButchargePrem() != null && currRisk.getButchargePrem().compareTo(BigDecimal.ZERO) != 0);
            System.out.println("prorate amt"+currprorataPrem + "new risk perm"+newRiskPrem);

            if ((currRisk.getTransType() != null && "EX".equalsIgnoreCase(currRisk.getTransType()))) {
                if (policy.getTotalInstalments() != null && policy.getTotalInstalments() > 1) {
                    if (newRiskPrem.compareTo(BigDecimal.ZERO) != 0) {
                        endosPrem = newRiskPrem;
                        totEndosPrem = newRiskPrem;
                    } else {
                        endosPrem = currprorataPrem.subtract(prevriskPrem);
                        totEndosPrem = currprorataPrem.subtract(prevriskPrem);

                    }
                    BigDecimal cashBasisPrem = endosPrem;
                    BigDecimal endorseCashBasis = BigDecimal.ZERO;
                    BigDecimal cashPrem = BigDecimal.ZERO;
                    System.out.println("Curr percent..."+currRisk.getTotalPercentage());

                    try {
                        endorseCashBasis = endosPrem
                                .multiply(currRisk.getTotalPercentage().divide(BigDecimal.valueOf(100)));
                        cashBasisPrem = prevriskPrem.multiply(
                                new BigDecimal(currRisk.getInstallmentPerc()).divide(BigDecimal.valueOf(100)));
                        cashPrem = endorseCashBasis.add(cashBasisPrem);

                    } catch (NumberFormatException ex) {

                    }
                    System.out.println("Endorse Cash basis "+endorseCashBasis+" Cash basis prem "+cashBasisPrem+" Cash prem "+cashPrem);

                    BigDecimal installAmount = cashPrem;

                    if (currRisk.getInstallAmount() == null || currRisk.getInstallAmount().compareTo(cashPrem) < 0) {
                         currRisk.setInstallAmount(cashPrem);
                    } else
                        installAmount = currRisk.getInstallAmount();

                    BigDecimal remPercentage = BigDecimal.valueOf(100).subtract(currRisk.getTotalPercentage());
                    BigDecimal remPrem = remPercentage.multiply(currprorataPrem.divide(BigDecimal.valueOf(100)));
                    if (currRisk.getInstallAmount() != null) {
                        if (cashPrem.compareTo(currRisk.getInstallAmount()) > 0) {
                            throw new BadRequestException(
                                    "Instalment Amount is below the required Minimum amount of " + cashPrem);
                        }

                        if (currRisk.getInstallAmount().compareTo(remPrem.add(currRisk.getInstallAmount())) == 1) {
                            throw new BadRequestException(
                                    "Installment amount is above the the maximum premium required for this risk of "
                                            + (remPrem.add(currRisk.getInstallAmount())));
                        }

                    }

                    if (remPrem.compareTo(BigDecimal.ZERO) > 0) {
                        currRisk.setWetDate(DateUtils.addDays(DateUtils.addMonths(currRisk.getWefDate(), 1), -1));
                    } else if (remPrem.compareTo(BigDecimal.ZERO) == 0) {
                        policy.setTotalInstalments(currRisk.getInstallmentNo().intValue());
                        currRisk.setWetDate(policy.getWetDate());
                    }
                    if (currRisk.getInstallAmount() != null) {
                        BigDecimal percentage = (currRisk.getInstallAmount()).divide(currprorataPrem)
                                .multiply(BigDecimal.valueOf(100));
                        currRisk.setPrevPercentage(new BigDecimal(currRisk.getInstallmentPerc()));
                        currRisk.setInstallmentPerc(String.valueOf(percentage));
                        currRisk.setTotalPercentage(
                                currRisk.getTotalPercentage().add(percentage).subtract(currRisk.getPrevPercentage()));
                    }

                    endosPrem = installAmount;
                } else {
                    endosPrem = currprorataPrem;
                    if (currRisk.getButchargePrem() != null
                            && currRisk.getButchargePrem().compareTo(BigDecimal.ZERO) > 0) {
                        endosPrem = currRisk.getButchargePrem();
                    }
                    System.out.println("Current premium " + endosPrem);
                }
            } else if ("RS".equalsIgnoreCase(policy.getPolRevStatus())) {
                if (currprorataPrem.compareTo(BigDecimal.ZERO) <= 0)
                    throw new BadRequestException("Invalid Reinstatement Transaction Premium Amount ");
                endosPrem = currprorataPrem;
                totEndosPrem = currprorataPrem;
                double endPrem = endosPrem.doubleValue();
                endosPrem = BigDecimal.valueOf(endPrem);
            } else {
                System.out.println("risk trans type"+currRisk.getTransType()+" risk id"+currRisk.getRiskId());
                if(!"EN".equalsIgnoreCase(currRisk.getTransType())){
                    newRiskPrem = newRiskPrem.multiply(prorata);
                }
                System.out.println("new risk prem "+newRiskPrem);
                if (newRiskPrem.compareTo(BigDecimal.ZERO) != 0) {
                    endosPrem = newRiskPrem;
                    totEndosPrem = newRiskPrem;
                } else {
                    endosPrem = currprorataPrem.subtract(prevriskPrem).multiply(prorata);
                    totEndosPrem = currprorataPrem.subtract(prevriskPrem);

                }
                double endPrem = endosPrem.doubleValue();
                endosPrem = BigDecimal.valueOf(endPrem);
                System.out.println("Endorsement Prem "+endPrem);
            }
            if(!butchargedPrem) {
                if ((currRisk.getTransType() != null && !"EX".equalsIgnoreCase(currRisk.getTransType()))) {
                    if ("F".equalsIgnoreCase(currRisk.getProrata())) {
                        endosPrem = endosPrem.multiply(BigDecimal.ONE);
                    } else if ("S".equalsIgnoreCase(currRisk.getProrata())) {
                        List<ShortPeriodRates> periodRates = shortPeriodRepo.getShortPeriodRates(daysDiff);
                        if (periodRates.isEmpty())
                            throw new BadRequestException("Short Period Rates For the Cover Period has not been setup");
                        if (periodRates.size() > 1)
                            throw new BadRequestException(
                                    "More than one Short Period Rates For the Cover Period have been setup");
                        ShortPeriodRates rates = periodRates.get(0);
                        endosPrem = endosPrem.multiply(rates.getRate().divide(rates.getDivFactor()));
                    } else if ("P".equalsIgnoreCase(currRisk.getProrata())) {
                        endosPrem = endosPrem.multiply(prorata).setScale(2, BigDecimal.ROUND_HALF_EVEN);
                    }
                }
            }

            Optional<PremiumItemsBean> premiumItemsBean = discountItems.stream()
                    .filter(a -> a.getSectType().equalsIgnoreCase("DS")).findFirst();
            if (premiumItemsBean.isPresent()) {
                PremiumItemsBean itemsBean = premiumItemsBean.get();
                final SectionTrans section = sectionRepo.findOne(itemsBean.getSectId());
                final BigDecimal discountPrem = BigDecimal.valueOf(itemsBean.getRate()).multiply(endosPrem)
                        .divide(BigDecimal.valueOf(itemsBean.getDivFactor()), 2, RoundingMode.HALF_EVEN);
                section.setCalcprem(discountPrem.negate());
                section.setPrem(discountPrem.negate());
                sectionRepo.save(section);
                endosPrem = endosPrem.subtract(discountPrem);
                System.out.println("Premium before discount..." + endosPrem + " Discount " + discountPrem);
            }
            System.out.println("Endos prem..." + endosPrem);

            totalEndosPremium = totalEndosPremium.add(endosPrem);
            totalFullPrem = totalFullPrem.add(totEndosPrem);
            currRisk.setPremium(endosPrem);
            currRisk.setCalcPremium(endosPrem);
            currRisk.setSumInsured(riskInsured);
            BigDecimal comm = BigDecimal.ZERO;
            if (endosPrem.compareTo(BigDecimal.ZERO) != 0) {
                if (currRisk.getCommRate() != null)
                    comm = endosPrem.multiply(currRisk.getCommRate()).divide(new BigDecimal(100));
                if (endosPrem.compareTo(BigDecimal.ZERO) == 1)
                    comm = comm.negate();
                else if (endosPrem.compareTo(BigDecimal.ZERO) == -1)
                    comm = comm.abs();
                currRisk.setCommAmt(comm);
                totalCommission = totalCommission.add(comm);
            }

            BigDecimal subAgentComm = BigDecimal.ZERO;
            BigDecimal marketerComm = BigDecimal.ZERO;
            // if (policy.getSubAgent() != null) {
            // BigDecimal commRate = BigDecimal.ZERO;
            //// if(policy.getSubAgent().getSubAccountTypes().getBindersDef()!=null &&
            // ==policy.getBinder().getBinId() &&
            // policy.getSubAgent().getSubAccountTypes().getCommRate().compareTo(BigDecimal.ZERO)!=0){
            //// commRate = policy.getSubAgent().getSubAccountTypes().getCommRate();
            //// }
            // if (commRate.compareTo(BigDecimal.ZERO) == 0) {
            // if (policy.getSubAgent().getAccountType().getCommRate() != null ||
            // policy.getSubAgent().getAccountType().getCommRate().compareTo(BigDecimal.ZERO)
            // != 0) {
            // commRate = policy.getSubAgent().getAccountType().getCommRate();
            // }
            // }
            // if (commRate.compareTo(BigDecimal.ZERO) == 0) {
            // throw new BadRequestException("Sub Agent Commission Rate not setup");
            // }
            // subAgentComm = comm.multiply(commRate.divide(BigDecimal.valueOf(100)));
            // }
            if (policy.getSubAgent() != null) {
                if (policy.getSubAgent().getCommissionEarning()!=null && policy.getSubAgent().getCommissionEarning().equalsIgnoreCase("Yes")) {
                    Long accId = policy.getSubAgent().getAccountType().getAccId();
                    Long binderId = policy.getBinder().getBinId();
                    SubAgentCommissionRates subAgentCommissionRates = null;
                    if(countRenewals)
                     subAgentCommissionRates = subAgentCommRepo
                            .findOne(QSubAgentCommissionRates.subAgentCommissionRates.accountTypes.accId.eq(accId)
                                    .and(QSubAgentCommissionRates.subAgentCommissionRates.bindersDef.binId
                                            .eq(binderId)).and(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.eq("RN")));
                    else{
                        subAgentCommissionRates = subAgentCommRepo
                                .findOne(QSubAgentCommissionRates.subAgentCommissionRates.accountTypes.accId.eq(accId)
                                        .and(QSubAgentCommissionRates.subAgentCommissionRates.bindersDef.binId
                                                .eq(binderId)).and(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.eq("NB")));
                    }
                    if ((policy.getSubAgent().getAccountType().getCommRate() == null
                            || policy.getSubAgent().getAccountType().getCommRate().compareTo(BigDecimal.ZERO) == 0)
                            && (subAgentCommissionRates == null
                                    || subAgentCommissionRates.getCommRate().compareTo(BigDecimal.ZERO) == 0)) {
                        throw new BadRequestException("Sub Agent Commission Rate not setup");
                    }
                    if (subAgentCommissionRates != null) {
                        BigDecimal subAgentCommRate = subAgentCommissionRates.getCommRate();
                        System.out.println("sub agent endorse comm 1: " + subAgentCommRate);

                        // Use the provided subAgentCommRate
                        subAgentComm = comm.multiply(subAgentCommRate.divide(BigDecimal.valueOf(100)));
                        System.out.println("Sub agent endorse comm amount 1a: " + subAgentComm);
                    } else {
                        // Default to policy's sub Agent account type commission rate
                        subAgentComm = comm.multiply(
                                policy.getSubAgent().getAccountType().getCommRate().divide(BigDecimal.valueOf(100)));
                        System.out.println("Sub agent endorse comm amount 1b: " + subAgentComm);
                    }
                } else {
                    subAgentComm = BigDecimal.ZERO;
                    System.out.println("Sub agent endorse comm amount not earning com1b: " + subAgentComm);
                }
            }

            if (policy.getMarketerAgent() != null) {
                if (policy.getMarketerAgent().getCommissionEarning()!=null && policy.getMarketerAgent().getCommissionEarning().equalsIgnoreCase("Yes")) {
                    Long accId = policy.getMarketerAgent().getAccountType().getAccId();
                    Long binderId = policy.getBinder().getBinId();
                    SubAgentCommissionRates subAgentCommissionRates =null;
                    if(countRenewals)
                     subAgentCommissionRates = subAgentCommRepo
                            .findOne(QSubAgentCommissionRates.subAgentCommissionRates.accountTypes.accId.eq(accId)
                                    .and(QSubAgentCommissionRates.subAgentCommissionRates.bindersDef.binId
                                            .eq(binderId)).and(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.eq("RN")));
                    else
                        subAgentCommissionRates = subAgentCommRepo
                                .findOne(QSubAgentCommissionRates.subAgentCommissionRates.accountTypes.accId.eq(accId)
                                        .and(QSubAgentCommissionRates.subAgentCommissionRates.bindersDef.binId
                                                .eq(binderId)).and(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.eq("RN")));
                    if ((policy.getMarketerAgent().getAccountType().getCommRate() == null
                            || policy.getMarketerAgent().getAccountType().getCommRate().compareTo(BigDecimal.ZERO) == 0)
                            && (subAgentCommissionRates == null
                                    || subAgentCommissionRates.getCommRate().compareTo(BigDecimal.ZERO) == 0)) {
                        throw new BadRequestException("Marketer Commission Rate not setup");
                    }
                    if (subAgentCommissionRates != null) {
                        BigDecimal marketerCommRate = subAgentCommissionRates.getCommRate();
                        System.out.println("Marketer endorse comm 1: " + marketerCommRate);

                        // Use the provided marketerCommRate
                        marketerComm = comm.multiply(marketerCommRate.divide(BigDecimal.valueOf(100)));
                        System.out.println("Marketer endorse comm amount 1a: " + marketerComm);
                    } else {
                        // Default to policy's marketer account type commission rate
                        marketerComm = comm.multiply(policy.getMarketerAgent().getAccountType().getCommRate()
                                .divide(BigDecimal.valueOf(100)));
                        System.out.println("Marketer endorse comm amount 1b: " + marketerComm);
                    }
                } else {
                    marketerComm = BigDecimal.ZERO;
                    System.out.println("Marketer endorse comm amount not earning com1b: " + marketerComm);
                }
            }

            currRisk.setSubAgentComm(subAgentComm);
            currRisk.setMarketerAgentComm(marketerComm);
            totalSubAgentComm = totalSubAgentComm.add(subAgentComm);
            totalMarketerComm = totalMarketerComm.add(marketerComm);
            BigDecimal sumRisktaxAmount = BigDecimal.ZERO;
            BigDecimal riskstampDuty = BigDecimal.ZERO;
            BigDecimal riskphfFund = BigDecimal.ZERO;
            BigDecimal riskwhtxAmt = BigDecimal.ZERO;
            BigDecimal riskextras = BigDecimal.ZERO;
            BigDecimal riskTl = BigDecimal.ZERO;

            for (PolicyTaxes policyTax : policyTaxes) {
                BigDecimal computedTax = calculateTax(endosPrem, policyTax.getTaxRate(), policyTax.getDivFactor(),
                        policyTax.getRateType());
                policyTax.setTaxAmount(computedTax);
                sumPolicytaxAmount = sumPolicytaxAmount.add(computedTax);
                // if (policyTax.getRevenueItems().getItem() == RevenueItems.EX) {
                // if ("R".equalsIgnoreCase(policyTax.getTaxLevel())) {
                // riskextras = riskextras.add(computedTax);
                // sumRisktaxAmount = sumRisktaxAmount.add(computedTax);
                // }
                // }
                // else if(policyTax.getRevenueItems().getItem() == RevenueItems.SD){
                // if("R".equalsIgnoreCase(policyTax.getTaxLevel())){
                // riskstampDuty = riskstampDuty.add(computedTax);
                // sumRisktaxAmount = sumRisktaxAmount.add(computedTax);
                // }
                // }
                // else
                if (policyTax.getRevenueItems().getItem() == RevenueItems.PHCF) {
                    if ("R".equalsIgnoreCase(policyTax.getTaxLevel())) {
                        riskphfFund = riskphfFund.add(computedTax);
                        sumRisktaxAmount = sumRisktaxAmount.add(computedTax);
                    }
                } else if (policyTax.getRevenueItems().getItem() == RevenueItems.WHTX) {
                    if ("R".equalsIgnoreCase(policyTax.getTaxLevel())) {
                        riskwhtxAmt = riskwhtxAmt.add(computedTax);
                        sumRisktaxAmount = sumRisktaxAmount.add(computedTax);
                    }
                } else if (policyTax.getRevenueItems().getItem() == RevenueItems.TL) {
                    if ("R".equalsIgnoreCase(policyTax.getTaxLevel())) {
                        riskTl = riskTl.add(computedTax);
                        sumRisktaxAmount = sumRisktaxAmount.add(computedTax);
                    }
                }
            }

            if (accType != null && accType.isWhtxAppl()) {
                if (accType.getWhtaxVal().compareTo(BigDecimal.ZERO) == 1) {
                    BigDecimal riskWhtx = accType.getWhtaxVal().divide(new BigDecimal(100)).multiply(comm);
                    currRisk.setWhtax(riskWhtx);
                }
            }

            // currRisk.setNetpremium(endosPrem.add(sumRisktaxAmount).subtract(comm));
            currRisk.setExtras(riskextras);
            // currRisk.setWhtax(riskwhtxAmt);
            currRisk.setPhfFund(riskphfFund);
            currRisk.setTrainingLevy(riskTl);
            currRisk.setStampDuty(riskstampDuty);
            currRisk.setFuturePrem(futureRiskPrem);
            futurePremium = futurePremium.add(futureRiskPrem);
        }

        polTaxesRepo.save(policyTaxes);
        BigDecimal whtxAmt = BigDecimal.ZERO;
        if (accType != null && accType.isWhtxAppl()) {
            if (accType.getWhtaxVal().compareTo(BigDecimal.ZERO) == 1) {
                whtxAmt = accType.getWhtaxVal().divide(new BigDecimal(100)).multiply(totalCommission);
                polwhtxAmt = whtxAmt.negate();
            }
        }

        sumPolicytaxAmount = sumPolicytaxAmount.add(polwhtxAmt);
        BigDecimal futureTax = BigDecimal.ZERO;
        for (PolicyTaxes policyTax : policyTaxes) {
            BigDecimal computedTax = calculateTax(totalEndosPremium, policyTax.getTaxRate(), policyTax.getDivFactor(),
                    policyTax.getRateType());
            BigDecimal computedFutureTax = calculateTax(futurePremium, policyTax.getTaxRate(), policyTax.getDivFactor(),
                    policyTax.getRateType());
            // if (policyTax.getRevenueItems().getItem() == RevenueItems.EX) {
            //
            // polextras = polextras.add(computedTax);
            // futureTax = futureTax.add(computedFutureTax);
            // }
            // else if(policyTax.getRevenueItems().getItem() == RevenueItems.SD){
            //
            // polstampDuty = polstampDuty.add(computedTax);
            // }
            // else
            if (policyTax.getRevenueItems().getItem() == RevenueItems.PHCF) {
                futureTax = futureTax.add(computedFutureTax);
                polphfFund = polphfFund.add(computedTax);
            } else if (policyTax.getRevenueItems().getItem() == RevenueItems.WHTX) {
                futureTax = futureTax.add(computedFutureTax);
                polwhtxAmt = polwhtxAmt.add(computedTax);
            } else if (policyTax.getRevenueItems().getItem() == RevenueItems.TL) {
                futureTax = futureTax.add(computedFutureTax);
                polTl = polTl.add(computedTax);
            }
        }
        riskRepo.save(currRisks);
        Currencies currencies = policy.getTransCurrency();
        totalEndosPremium = totalEndosPremium.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN);
        totalCommission = totalCommission.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN);
        if(policy.getAdminFeeApplicable()!=null && "Y".equalsIgnoreCase(policy.getAdminFeeApplicable())) {
            long count = adminFeeSetUpRepo.count(QAdminFeeSetUp.adminFeeSetUp.binder.binId.eq(policy.getBinder().getBinId()).and(QAdminFeeSetUp.adminFeeSetUp.status.eq("Active")));
            if (count != 1) {
                throw new BadRequestException("Please configure set up for admin fee to continue...");
            }
            AdminFeeSetUp adminFeeSetUp = adminFeeSetUpRepo.findOne(QAdminFeeSetUp.adminFeeSetUp.binder.binId.eq(policy.getBinder().getBinId()).and(QAdminFeeSetUp.adminFeeSetUp.status.eq("Active")));
            //admin fee check
            BigDecimal adminFeeTotal = BigDecimal.ZERO;
            BigDecimal vatTotal = BigDecimal.ZERO;
            if(adminFeeSetUp != null && adminFeeSetUp.getAdminFeeRateType() != null) {
                final double rate = (adminFeeSetUp.getAdminFeeRateType().equals("Percent")) ? 100 : 1;
                if (adminFeeSetUp.getAdminFeeRateType().equals("Percent")) {
                    adminFeeTotal = BigDecimal.valueOf((adminFeeSetUp.getAdminFeeRate().doubleValue() / rate) * totalEndosPremium.doubleValue());
                } else {
                    adminFeeTotal = adminFeeSetUp.getAdminFeeRate();
                }
                final double vatrate = (adminFeeSetUp.getVateRateType().equals("Percent")) ? 100 : 1;
                 vatTotal = BigDecimal.valueOf((adminFeeSetUp.getVatRate().doubleValue() / vatrate) * adminFeeTotal.doubleValue());
                if ((!adminFeeSetUp.getVateRateType().equals("Percent"))) {
                    vatTotal = adminFeeSetUp.getVatRate();
                }
                policy.setAdminFeeAmt(adminFeeTotal);
                policy.setAdminFeeVatAmt(vatTotal);
            }
        }
        else{
            policy.setAdminFeeAmt(BigDecimal.ZERO);
            policy.setAdminFeeVatAmt(BigDecimal.ZERO);
        }
        policy.setPremium(totalEndosPremium);
        policy.setBasicPrem((totalEndosPremium.add(polextras).add(polstampDuty).add(polphfFund).add(polTl))
                .setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        if (prevPolicy.getEndosbasicPremium() == null) {
            policy.setEndosbasicPremium(totalEndosPremium);
        } else {
            policy.setEndosbasicPremium(prevPolicy.getEndosbasicPremium().add(totalEndosPremium));
        }
        if (prevPolicy.getEndosgrossPremium() == null) {
            policy.setEndosgrossPremium((totalEndosPremium.add(polextras).add(polstampDuty).add(polphfFund).add(polTl))
                    .setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        } else {
            policy.setEndosgrossPremium(prevPolicy.getEndosgrossPremium()
                    .add((totalEndosPremium.add(polextras).add(polstampDuty).add(polphfFund).add(polTl))
                            .setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN)));
        }

        if (prevPolicy.getEndosCommissions() == null)
            policy.setEndosCommissions(totalCommission);
        else
            policy.setEndosCommissions(prevPolicy.getEndosCommissions().add(totalCommission));

        // if
        // (StringUtils.equalsIgnoreCase(paramService.getParameterString("SUB_AGENT_COMM_PARAM"),
        // "N")) {
        // if (policy.getSubAgent() != null) {
        // BigDecimal commRate = BigDecimal.ZERO;
        //// if(policy.getSubAgent().getSubAccountTypes().getBindersDef()!=null &&
        // policy.getSubAgent().getSubAccountTypes().getBindersDef().getBinId()
        // ==policy.getBinder().getBinId() &&
        // policy.getSubAgent().getSubAccountTypes().getCommRate().compareTo(BigDecimal.ZERO)!=0){
        //// commRate = policy.getSubAgent().getSubAccountTypes().getCommRate();
        //// }
        // if (commRate.compareTo(BigDecimal.ZERO) == 0) {
        // if (policy.getSubAgent().getAccountType().getCommRate() != null ||
        // policy.getSubAgent().getAccountType().getCommRate().compareTo(BigDecimal.ZERO)
        // != 0) {
        // commRate = policy.getSubAgent().getAccountType().getCommRate();
        // }
        // }
        // if (commRate.compareTo(BigDecimal.ZERO) == 0) {
        // throw new BadRequestException("Sub Agent Commission Rate not setup");
        // }
        // totalSubAgentComm =
        // (totalCommission.abs().subtract(whtxAmt.abs())).multiply(commRate.divide(BigDecimal.valueOf(100)));
        // }

        if (policy.getSubAgent() != null) {
            if (policy.getSubAgent().getCommissionEarning()!=null && policy.getSubAgent().getCommissionEarning().equalsIgnoreCase("Yes")) {
                Long accId = policy.getSubAgent().getAccountType().getAccId();
                Long binderId = policy.getBinder().getBinId();
                System.out.println("SubAgent endorse Binder Id 2: " + binderId);
                System.out.println("SubAgent endorse AccountId 2: " + accId);
                SubAgentCommissionRates subAgentCommissionRates = null;
                if(countRenewals)
                         subAgentCommissionRates = subAgentCommRepo
                        .findOne(QSubAgentCommissionRates.subAgentCommissionRates.accountTypes.accId.eq(accId)
                                .and(QSubAgentCommissionRates.subAgentCommissionRates.bindersDef.binId.eq(binderId))
                                .and(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.eq("RN")));
                else
                    subAgentCommissionRates = subAgentCommRepo
                            .findOne(QSubAgentCommissionRates.subAgentCommissionRates.accountTypes.accId.eq(accId)
                                    .and(QSubAgentCommissionRates.subAgentCommissionRates.bindersDef.binId.eq(binderId))
                                    .and(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.eq("NB")));
                if ((policy.getSubAgent().getAccountType().getCommRate() == null
                        || policy.getSubAgent().getAccountType().getCommRate().compareTo(BigDecimal.ZERO) == 0)
                        && (subAgentCommissionRates == null
                                || subAgentCommissionRates.getCommRate().compareTo(BigDecimal.ZERO) == 0)) {
                    throw new BadRequestException("Sub Agent Commission Rate not setup");
                }
                if (subAgentCommissionRates != null) {
                    BigDecimal subAgentCommRate = subAgentCommissionRates.getCommRate();
                    System.out.println("sub agent endorse comm 2: " + subAgentCommRate);

                    // Use the provided subAgentCommRate
                    totalSubAgentComm = (totalCommission.abs().subtract(whtxAmt.abs()))
                            .multiply(subAgentCommRate.divide(BigDecimal.valueOf(100)));
                    System.out.println("Sub agent endorse comm amount 2a: " + totalSubAgentComm);
                } else {
                    // Default to policy's sub Agent account type commission rate

                    totalSubAgentComm = (totalCommission.abs().subtract(whtxAmt.abs())).multiply(
                            policy.getSubAgent().getAccountType().getCommRate().divide(BigDecimal.valueOf(100)));
                    System.out.println("Sub agent endorse comm amount 2b: " + totalSubAgentComm);
                }
            } else {
                totalSubAgentComm = BigDecimal.ZERO;
                System.out.println("Sub agent endorse comm amount not earning com2b: " + totalSubAgentComm);
            }
        }

        if (policy.getMarketerAgent() != null) {
            if (policy.getMarketerAgent().getCommissionEarning()!=null &&  policy.getMarketerAgent().getCommissionEarning().equalsIgnoreCase("Yes")) {
                Long accId = policy.getMarketerAgent().getAccountType().getAccId();
                Long binderId = policy.getBinder().getBinId();
                System.out.println("Marketer endorse Binder Id 2: " + binderId);
                System.out.println("Marketer endorse AccountId 2:" + accId);
                SubAgentCommissionRates subAgentCommissionRates = null;
                if(countRenewals)
                 subAgentCommissionRates = subAgentCommRepo
                        .findOne(QSubAgentCommissionRates.subAgentCommissionRates.accountTypes.accId.eq(accId)
                                .and(QSubAgentCommissionRates.subAgentCommissionRates.bindersDef.binId.eq(binderId))
                                .and(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.eq("RN")));
                else
                    subAgentCommissionRates = subAgentCommRepo
                            .findOne(QSubAgentCommissionRates.subAgentCommissionRates.accountTypes.accId.eq(accId)
                                    .and(QSubAgentCommissionRates.subAgentCommissionRates.bindersDef.binId.eq(binderId))
                                    .and(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.eq("NB")));
                if ((policy.getMarketerAgent().getAccountType().getCommRate() == null
                        || policy.getMarketerAgent().getAccountType().getCommRate().compareTo(BigDecimal.ZERO) == 0)
                        && (subAgentCommissionRates == null
                                || subAgentCommissionRates.getCommRate().compareTo(BigDecimal.ZERO) == 0)) {
                    throw new BadRequestException("Marketer Commission Rate not setup");
                }
                if (subAgentCommissionRates != null) {
                    BigDecimal marketerCommRate = subAgentCommissionRates.getCommRate();
                    System.out.println("Marketer endorse comm 2: " + marketerCommRate);

                    // Use the provided marketerCommRate
                    totalMarketerComm = (totalCommission.abs().subtract(whtxAmt.abs()))
                            .multiply(marketerCommRate.divide(BigDecimal.valueOf(100)));
                    System.out.println("Marketer endorse comm amount 2a: " + totalMarketerComm);
                } else {
                    // Default to policy's marketer account type commission rate
                    totalMarketerComm = (totalCommission.abs().subtract(whtxAmt.abs())).multiply(
                            policy.getMarketerAgent().getAccountType().getCommRate().divide(BigDecimal.valueOf(100)));
                    System.out.println("Marketer endorse comm amount 2b: " + totalMarketerComm);
                }
            } else {
                totalMarketerComm = BigDecimal.ZERO;
                System.out.println("Marketer endorse comm amount not earning com2b: " + totalMarketerComm);
            }
        }
        // }

        System.out.println("Endorsement Premium..."+totalEndosPremium);

        if (totalEndosPremium.compareTo(BigDecimal.ZERO) == 1) {
            totalCommission = totalCommission.abs().negate();
            totalSubAgentComm = totalSubAgentComm.abs();
            totalMarketerComm = totalMarketerComm.abs();
        } else if (totalEndosPremium.compareTo(BigDecimal.ZERO) == -1) {
            totalCommission = totalCommission.abs();
            totalSubAgentComm = totalSubAgentComm.abs().negate();
            totalMarketerComm = totalMarketerComm.abs().negate();
        }

        policy.setCommAmt(totalCommission.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setSubAgentComm(totalSubAgentComm);
        policy.setMarketerAgentComm(totalMarketerComm);
        policy.setExtras(polextras.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setWhtx(polwhtxAmt.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setPhcf(polphfFund.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setTrainingLevy(polTl.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        // policy.setTotTrainingLevy(polTl.setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN));
        // policy.setTotPhcf(polphfFund.setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN));
        policy.setStampDuty(polstampDuty.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setSumInsured(sumInsured.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setNetPrem((totalEndosPremium.add(polextras).add(polstampDuty).add(polphfFund).add(polTl).add(polwhtxAmt)
                .add(totalCommission)).setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));

        Iterable<PolicyActiveRisks> activeRiskses = activeRisksRepo
                .findAll(QPolicyActiveRisks.policyActiveRisks.policy.policyId.eq(policy.getPolicyId())
                        .and(QPolicyActiveRisks.policyActiveRisks.prevRisk.isNull())
                        .and(QPolicyActiveRisks.policyActiveRisks.status.in("NB", "NR")));
        BigDecimal totTl = BigDecimal.ZERO;
        BigDecimal totPhf = BigDecimal.ZERO;
        BigDecimal totComm = BigDecimal.ZERO;
        BigDecimal totExtras = BigDecimal.ZERO;
        BigDecimal totPrem = BigDecimal.ZERO;
        BigDecimal totSi = BigDecimal.ZERO;
        BigDecimal totWhtx = BigDecimal.ZERO;
        for (PolicyActiveRisks activeRisks : activeRiskses) {
            totTl = totTl.add(activeRisks.getRisk().getTrainingLevy());
            totPhf = totPhf.add(activeRisks.getRisk().getPhfFund());
            totComm = totComm.add((activeRisks.getRisk().getCommAmt() != null) ? activeRisks.getRisk().getCommAmt()
                    : BigDecimal.ZERO);
            totExtras = totExtras.add(
                    (activeRisks.getRisk().getExtras() != null) ? activeRisks.getRisk().getExtras() : BigDecimal.ZERO);
            totPrem = totPrem.add(activeRisks.getRisk().getPremium());
            totSi = totSi.add(activeRisks.getRisk().getSumInsured());
            totWhtx = totWhtx.add(
                    (activeRisks.getRisk().getWhtax() != null) ? activeRisks.getRisk().getWhtax() : BigDecimal.ZERO);
        }

        Iterable<PolicyActiveRisks> activeRiskes = activeRisksRepo
                .findAll(QPolicyActiveRisks.policyActiveRisks.policy.policyId.eq(policy.getPolicyId())
                        .and(QPolicyActiveRisks.policyActiveRisks.prevRisk.isNull())
                        .and(QPolicyActiveRisks.policyActiveRisks.status.in("ER")));

        for (PolicyActiveRisks activeRisks : activeRiskes) {
            totTl = totTl.add(activeRisks.getRisk().getTrainingLevy());
            totPhf = totPhf.add(activeRisks.getRisk().getPhfFund());
            totComm = totComm.add((activeRisks.getRisk().getCommAmt() != null) ? activeRisks.getRisk().getCommAmt()
                    : BigDecimal.ZERO);
            totExtras = totExtras.add(
                    (activeRisks.getRisk().getExtras() != null) ? activeRisks.getRisk().getExtras() : BigDecimal.ZERO);
            totPrem = totPrem.add(activeRisks.getRisk().getPremium());
            totSi = totSi.add(activeRisks.getRisk().getSumInsured());
            totWhtx = totWhtx.add(
                    (activeRisks.getRisk().getWhtax() != null) ? activeRisks.getRisk().getWhtax() : BigDecimal.ZERO);
        }

        policy.setTotTrainingLevy(totTl.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setTotPhcf(totPhf.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setPolTotComm(totComm.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setPolTotExtras(totExtras.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setPolTotPrem(totPrem.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setPolTotSD(polstampDuty.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setPolTotSI(totSi.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setPolTotWhtx(totWhtx.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setPaidPremium(receiptService.getPolicyTotalRcptAmount(policy.getPolNo()));
        if (policy.getPaidPremium().compareTo(policy.getEndosgrossPremium()) == 1) {
            BigDecimal refundableAmt = policy.getPaidPremium().subtract(policy.getEndosgrossPremium());
            if (refundableAmt.compareTo(policy.getBasicPrem().abs()) == 1) {
                policy.setRefundablePremium(policy.getBasicPrem().abs());
            } else {
                policy.setRefundablePremium(refundableAmt);
            }

        } else {
            policy.setRefundablePremium(BigDecimal.ZERO);
        }
        if (prevPolicy.getFuturePrem() == null)
            throw new BadRequestException("Error Doing an Endorsement on this policy");
//        System.out.println("Future Prem..."+policy.getEndosgrossPremium());
        policy.setFuturePrem(policy.getEndosgrossPremium());
        PolicyTrans savedPolicy = policyRepo.save(policy);
        Iterable<RiskTrans> savedRisks = riskRepo
                .findAll(QRiskTrans.riskTrans.policy.policyId.eq(savedPolicy.getPolicyId()));
        for (RiskTrans savedRisk : savedRisks) {
            policyService.populateRiskScheduleDetails(savedRisk.getRiskId());
        }

    }

    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class },propagation = Propagation.REQUIRED)
    public void  computeCancelPrem(Long polCode) throws BadRequestException {
        PolicyTrans policy = policyRepo.findOne(polCode);
        if (policy == null) {
            throw new BadRequestException("Error getting Policy Details");
        }

        if ("NB".equalsIgnoreCase(policy.getTransType()) || "SP".equalsIgnoreCase(policy.getTransType())) {
            throw new BadRequestException("Error Computing Cancellation Premium for " + policy.getTransType());
        }

        PolicyTrans prevPolicy = policy.getPreviousTrans();
        if (prevPolicy == null) {
            throw new BadRequestException("Error getting Previous Policy Transaction Details...");
        }
        policyService.populateTaxes(policy);
        long calDays = TimeUnit.DAYS.convert((policy.getCoverTo().getTime() - policy.getCoverFrom().getTime()),
                TimeUnit.MILLISECONDS) + 1;

        long datediff = policy.getWetDate().getTime() - policy.getWefDate().getTime();
        long daysDiff = TimeUnit.DAYS.convert(datediff, TimeUnit.MILLISECONDS) + 1;

        Iterable<PolicyTaxes> policyTaxes = polTaxesRepo.findAll(QPolicyTaxes.policyTaxes.policy.policyId.eq(polCode));


        BigDecimal endorsePrem = prevPolicy.getEndosbasicPremium();
        BigDecimal endorseComm = prevPolicy.getEndosCommissions();
        BigDecimal futurePrem = (policy.getPaidPremium()!=null)?policy.getPaidPremium():endorsePrem;



        System.out.println("endorseComm = " + endorseComm + " en'dorsePrem = " + endorsePrem);
        if (endorsePrem.compareTo(BigDecimal.ZERO) == 0 || endorsePrem.compareTo(BigDecimal.ZERO) == 0)
            throw new BadRequestException(
                    "Error Computing Cancellation Premium. Endorsement Premium cannot be negative or zero");
        double rata = (double) daysDiff / calDays;
        BigDecimal prorata = new BigDecimal(rata);
        BigDecimal totalSubAgentComm =(prevPolicy.getSubAgentComm() != null) ?prevPolicy.getSubAgentComm().negate().multiply(prorata):BigDecimal.ZERO;
        BigDecimal totalMarkerterComm = (prevPolicy.getMarketerAgentComm() != null)
                ? prevPolicy.getMarketerAgentComm().negate().multiply(prorata)
                : BigDecimal.ZERO;
        //BigDecimal totalIntroducerComm = (prevPolicy.getIntroducerAgentComm() != null) ?prevPolicy.getIntroducerAgentComm().negate().multiply(prorata):BigDecimal.ZERO;
        BigDecimal refundPrem = endorsePrem.multiply(prorata)
                .setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN).negate();

        BigDecimal totalCommission = endorseComm.multiply(prorata).negate();
        BigDecimal polstampDuty = BigDecimal.ZERO;
        BigDecimal polphfFund = BigDecimal.ZERO;
        BigDecimal polwhtxAmt = BigDecimal.ZERO;
        BigDecimal polextras = BigDecimal.ZERO;
        BigDecimal polTl = BigDecimal.ZERO;

        BigDecimal totalSubAgentCommBp = BigDecimal.ZERO;
        BigDecimal totalMarkerterCommBp = BigDecimal.ZERO;
        BigDecimal totalCommissionBp = BigDecimal.ZERO;
        BigDecimal refundPremBp = BigDecimal.ZERO;
        Iterable<PolicyActiveRisks> activeRisks = activeRisksRepo
                .findAll(QPolicyActiveRisks.policyActiveRisks.policy.policyId.eq(polCode));
        // AccountTypes accType = policy.getAgent().getAccountType();
        AccountTypes accType = (policy.getAgent() != null) ? policy.getAgent().getAccountType() : null;
        for (PolicyActiveRisks risk : activeRisks) {
            BigDecimal riskstampDuty = BigDecimal.ZERO;
            BigDecimal riskphfFund = BigDecimal.ZERO;
            BigDecimal riskwhtxAmt = BigDecimal.ZERO;
            BigDecimal riskextras = BigDecimal.ZERO;
            BigDecimal riskTl = BigDecimal.ZERO;

            BigDecimal endosPrem = risk.getPrevRisk().getPremium().negate().multiply(prorata);
            BigDecimal comm = risk.getPrevRisk().getCommAmt().negate().multiply(prorata);

            final BigDecimal currentButChargePrem = risk.getRisk().getButchargePrem();
            BigDecimal subAgentComm = BigDecimal.ZERO;
            BigDecimal marketerComm = BigDecimal.ZERO;
            BigDecimal futurePremWithoutTaxes = futurePrem;
            futurePremWithoutTaxes = futurePremWithoutTaxes.setScale(2, RoundingMode.HALF_EVEN);

            BigDecimal totalTaxExpStampDuty = BigDecimal.ZERO;
            System.out.println("current ppol"+polCode + "prev pol"+prevPolicy.getPolicyId());
            Iterable<PolicyTaxes> prevPolicyTaxes = polTaxesRepo.findAll(QPolicyTaxes.policyTaxes.policy.policyId.eq(prevPolicy.getPolicyId()));
            for (PolicyTaxes policyTax : prevPolicyTaxes) {
                if (policyTax.getRevenueItems().getItem() != RevenueItems.SD) {
                    totalTaxExpStampDuty = totalTaxExpStampDuty.add(policyTax.getTaxAmount());
                }
                System.out.println("taxes"+policyTax.getTaxAmount() + " category"+policyTax.getRevenueItems().getItem()+" total tax"+totalTaxExpStampDuty);
            }
            BigDecimal futurePremWithTaxes = futurePrem.add(totalTaxExpStampDuty);
            System.out.println("totalfutureprem"+futurePremWithTaxes);
            if(currentButChargePrem!=null) {
                if ( currentButChargePrem.compareTo(BigDecimal.ZERO) > 0) {
                    throw new BadRequestException("Override Cancellation Premium Cannot be Positive...");
                }


                //if (currentButChargePrem.abs().compareTo(futurePremWithoutTaxes.abs()) > 0) {
                if (currentButChargePrem.abs().compareTo(futurePremWithTaxes.abs()) > 0) {
                    //throw new BadRequestException("Override Cancellation "+currentButChargePrem+" Cannot be greater than Risk premium " + futurePremWithoutTaxes);
                    throw new BadRequestException("Override Cancellation "+currentButChargePrem+" Cannot be greater than Risk premium + taxes without stamp duty" + futurePremWithTaxes);
                }
                endosPrem = currentButChargePrem;
                refundPremBp = refundPremBp.add(currentButChargePrem);
                BigDecimal commRate = risk.getPrevRisk().getCommRate();
                if(commRate!=null) {
                    comm = BigDecimal.valueOf((commRate.doubleValue() * -1 * endosPrem.doubleValue() / 100));
                    totalCommissionBp = totalCommissionBp.add(comm);
                    if (policy.getSubAgent() != null) {
                        if (policy.getSubAgent().getCommissionEarning() != null
                                && policy.getSubAgent().getCommissionEarning().equalsIgnoreCase("Yes")) {
                            Long accId = policy.getSubAgent().getAccountType().getAccId();
                            Long binderId = policy.getBinder().getBinId();
                            String transType = policy.getTransType().toUpperCase().trim();
                            SubAgentCommissionRates subAgentCommissionRates = subAgentCommRepo
                                    .findOne(QSubAgentCommissionRates.subAgentCommissionRates.accountTypes.accId.eq(accId)
                                            .and(QSubAgentCommissionRates.subAgentCommissionRates.bindersDef.binId.eq(binderId))
                                            .and(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.eq(transType)
                                                    .or(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.isNull())));
                            if ((policy.getSubAgent().getAccountType().getCommRate() == null
                                    || policy.getSubAgent().getAccountType().getCommRate().compareTo(BigDecimal.ZERO) == 0)
                                    && (subAgentCommissionRates == null
                                    || subAgentCommissionRates.getCommRate().compareTo(BigDecimal.ZERO) == 0)) {
                                throw new BadRequestException(transType+" Sub Agent Commission Rate not setup");
                            }
                            if (subAgentCommissionRates != null) {
                                BigDecimal subAgentCommRate = subAgentCommissionRates.getCommRate();
                                System.out.println("sub agent comm 1: " + subAgentCommRate);

                                // Use the provided subAgentCommRate
                                subAgentComm = comm.multiply(subAgentCommRate.divide(BigDecimal.valueOf(100)));
                                System.out.println("Sub agent comm amount 1a: " + subAgentComm);
                            } else {
                                // Default to policy's sub Agent account type commission rate
                                subAgentComm = comm.multiply(policy.getSubAgent().getAccountType().getCommRate()
                                        .divide(BigDecimal.valueOf(100)));
                                System.out.println("Sub agent comm amount 1b: " + subAgentComm);
                            }
                        }
                    }
                    if (policy.getMarketerAgent() != null) {
                        if (policy.getMarketerAgent().getCommissionEarning() != null
                                && policy.getMarketerAgent().getCommissionEarning().equalsIgnoreCase("Yes")) {
                            Long accId = policy.getMarketerAgent().getAccountType().getAccId();
                            Long binderId = policy.getBinder().getBinId();
                            String transType = policy.getTransType().toUpperCase().trim();
                            SubAgentCommissionRates subAgentCommissionRates = subAgentCommRepo
                                    .findOne(QSubAgentCommissionRates.subAgentCommissionRates.accountTypes.accId.eq(accId)
                                            .and(QSubAgentCommissionRates.subAgentCommissionRates.bindersDef.binId.eq(binderId))
                                            .and(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.eq(transType)
                                                    .or(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.isNull())));
                            if ((policy.getMarketerAgent().getAccountType().getCommRate() == null || policy
                                    .getMarketerAgent().getAccountType().getCommRate().compareTo(BigDecimal.ZERO) == 0)
                                    && (subAgentCommissionRates == null
                                    || subAgentCommissionRates.getCommRate().compareTo(BigDecimal.ZERO) == 0)) {
                                throw new BadRequestException(transType+"Marketer Commission Rate not setup");
                            }
                            if (subAgentCommissionRates != null) {
                                BigDecimal marketerCommRate = subAgentCommissionRates.getCommRate();
                                System.out.println("Marketer comm 1: " + marketerCommRate);

                                // Use the provided marketerCommRate
                                marketerComm = comm.multiply(marketerCommRate.divide(BigDecimal.valueOf(100)));
                                System.out.println("Marketer comm amount 1a: " + marketerComm);
                            } else {
                                // Default to policy's marketer account type commission rate
                                marketerComm = comm.multiply(policy.getMarketerAgent().getAccountType().getCommRate()
                                        .divide(BigDecimal.valueOf(100)));
                                System.out.println("Marketer comm amount 1b: " + marketerComm);
                            }
                        }
                    }
                    totalSubAgentCommBp = totalSubAgentCommBp.add(subAgentComm);
                    totalMarkerterCommBp = totalMarkerterCommBp.add(marketerComm);
                }


            }



//            totalMarkerterComm = totalMarkerterComm.add(marketerComm);
//            totalIntroducerComm = totalIntroducerComm.add(introducerComm);
            RiskTrans currRisk = risk.getRisk();
            currRisk.setPremium(endosPrem);
            currRisk.setCalcPremium(endosPrem);
            currRisk.setSumInsured(risk.getPrevRisk().getSumInsured());
            currRisk.setCommAmt(comm);
            currRisk.setSubAgentComm(subAgentComm);
            currRisk.setMarketerAgentComm(marketerComm);
            Iterable<SectionTrans> sections = sectionRepo
                    .findAll(QSectionTrans.sectionTrans.risk.riskId.eq(currRisk.getRiskId()));
            for (SectionTrans section : sections) {
                BigDecimal sectPrem = (section.getPrevSection().getPrem() != null)
                        ? section.getPrevSection().getPrem().negate().multiply(prorata)
                        : BigDecimal.ZERO;
                section.setPrem(sectPrem);
                section.setCalcprem(sectPrem);
                sectionRepo.save(section);
            }
            for (PolicyTaxes policyTax : policyTaxes) {
                BigDecimal computedTax = calculateTax(endosPrem, policyTax.getTaxRate(), policyTax.getDivFactor(),
                        policyTax.getRateType());
                // if (policyTax.getRevenueItems().getItem() == RevenueItems.EX) {
                // if ("R".equalsIgnoreCase(policyTax.getTaxLevel())) {
                // riskextras = riskextras.add(computedTax);
                // }
                // } else
                if (policyTax.getRevenueItems().getItem() == RevenueItems.PHCF) {
                    if ("R".equalsIgnoreCase(policyTax.getTaxLevel())) {
                        riskphfFund = riskphfFund.add(computedTax);
                    }
                } else if (policyTax.getRevenueItems().getItem() == RevenueItems.WHTX) {
                    if ("R".equalsIgnoreCase(policyTax.getTaxLevel())) {
                        riskwhtxAmt = riskwhtxAmt.add(computedTax);
                    }
                } else if (policyTax.getRevenueItems().getItem() == RevenueItems.TL) {
                    if ("R".equalsIgnoreCase(policyTax.getTaxLevel())) {
                        riskTl = riskTl.add(computedTax);
                    }
                }
            }
            if (accType != null && accType.isWhtxAppl()) {
                if (accType.getWhtaxVal().compareTo(BigDecimal.ZERO) == 1) {
                    BigDecimal riskWhtx = accType.getWhtaxVal().divide(new BigDecimal(100)).multiply(comm);
                    currRisk.setWhtax(riskWhtx);
                }
            }
            currRisk.setExtras(riskextras);
            currRisk.setPhfFund(riskphfFund);
            currRisk.setTrainingLevy(riskTl);
            currRisk.setStampDuty(riskstampDuty);
            riskRepo.save(currRisk);
        }

        if(refundPremBp.compareTo(BigDecimal.ZERO) != 0){
            refundPrem = refundPremBp;
        }

        if(totalCommissionBp.compareTo(BigDecimal.ZERO)!=0){
            totalCommission = totalCommissionBp;
        }

        for (PolicyTaxes policyTax : policyTaxes) {
            BigDecimal computedTax = calculateTax(refundPrem, policyTax.getTaxRate(), policyTax.getDivFactor(),
                    policyTax.getRateType());
            policyTax.setTaxAmount(
                    computedTax.setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            // if (policyTax.getRevenueItems().getItem() == RevenueItems.EX) {
            //
            // polextras = polextras.add(computedTax);
            // }
            // else if(policyTax.getRevenueItems().getItem() == RevenueItems.SD){
            //
            // polstampDuty = polstampDuty.add(computedTax);
            // }
            // else
            if (policyTax.getRevenueItems().getItem() == RevenueItems.PHCF) {

                polphfFund = polphfFund.add(computedTax);
            } else if (policyTax.getRevenueItems().getItem() == RevenueItems.WHTX) {

                polwhtxAmt = polwhtxAmt.add(computedTax);
            } else if (policyTax.getRevenueItems().getItem() == RevenueItems.TL) {

                polTl = polTl.add(computedTax);
            }
        }

        polTaxesRepo.save(policyTaxes);

        BigDecimal whtxAmt = BigDecimal.ZERO;

        if (accType != null && accType.isWhtxAppl()) {
            if (accType.getWhtaxVal().compareTo(BigDecimal.ZERO) == 1) {
                whtxAmt = accType.getWhtaxVal().divide(new BigDecimal(100)).multiply(totalCommission);
                polwhtxAmt = whtxAmt.negate();
            }
        }
        Currencies currencies = policy.getTransCurrency();
        refundPrem = refundPrem.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN);
        totalCommission = totalCommission.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN);

        if(policy.getAdminFeeApplicable()!=null && "Y".equalsIgnoreCase(policy.getAdminFeeApplicable())) {
            long count = adminFeeSetUpRepo.count(QAdminFeeSetUp.adminFeeSetUp.binder.binId.eq(policy.getBinder().getBinId()).and(QAdminFeeSetUp.adminFeeSetUp.status.eq("Active")));

            if (count != 1) {
                throw new BadRequestException("Please configure set up for admin fee to continue...");
            }

            AdminFeeSetUp adminFeeSetUp = adminFeeSetUpRepo.findOne(QAdminFeeSetUp.adminFeeSetUp.binder.binId.eq(policy.getBinder().getBinId()).and(QAdminFeeSetUp.adminFeeSetUp.status.eq("Active")));

            //admin fee check on cancel prem
            BigDecimal adminFeeTotal = BigDecimal.ZERO;
            BigDecimal vatTotal = BigDecimal.ZERO;
            if(adminFeeSetUp != null &&  adminFeeSetUp.getAdminFeeRateType() != null) {
                final double rate = (adminFeeSetUp.getAdminFeeRateType().equals("Percent")) ? 100 : 1;

                if (adminFeeSetUp.getAdminFeeRateType().equals("Percent")) {
                    adminFeeTotal = BigDecimal.valueOf((adminFeeSetUp.getAdminFeeRate().doubleValue() / rate) * refundPrem.doubleValue());
                } else {
                    adminFeeTotal = adminFeeSetUp.getAdminFeeRate();
                }
                final double vatrate = (adminFeeSetUp.getVateRateType().equals("Percent")) ? 100 : 1;
                vatTotal = BigDecimal.valueOf((adminFeeSetUp.getVatRate().doubleValue() / vatrate) * adminFeeTotal.doubleValue());
                if ((!adminFeeSetUp.getVateRateType().equals("Percent"))) {
                    vatTotal = adminFeeSetUp.getVatRate();
                }
                policy.setAdminFeeAmt(adminFeeTotal);
                policy.setAdminFeeVatAmt(vatTotal);
            }
        }
        else{
            policy.setAdminFeeAmt(BigDecimal.ZERO);
            policy.setAdminFeeVatAmt(BigDecimal.ZERO);
        }

        policy.setPremium(refundPrem);
        policy.setBasicPrem((refundPrem.add(polextras).add(polstampDuty).add(polphfFund).add(polTl))
                .setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setEndosbasicPremium((prevPolicy.getEndosbasicPremium().add(refundPrem))
                .setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setCommAmt(totalCommission);
        policy.setSubAgentComm(totalSubAgentComm);
        policy.setMarketerAgentComm(totalMarkerterComm);
        policy.setExtras(polextras.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setWhtx(polwhtxAmt.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setPhcf(polphfFund.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setTrainingLevy(polTl.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setStampDuty(polstampDuty.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setFuturePrem(prevPolicy.getFuturePrem());
        policy.setSumInsured(prevPolicy.getSumInsured());
        policy.setIntroducerAgentComm(BigDecimal.ZERO);
        policy.setNetPrem(refundPrem
                .add((polextras).add(polstampDuty).add(polphfFund).add(polTl).add(polwhtxAmt).add(totalCommission))
                .setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setTotTrainingLevy(polTl.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setTotPhcf(polphfFund.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));

        policy.setPaidPremium(receiptService.getPolicyTotalRcptAmount(policy.getPolNo()));

        if (prevPolicy.getEndosgrossPremium() == null) {
            policy.setEndosgrossPremium((refundPrem.add(polextras).add(polstampDuty).add(polphfFund).add(polTl))
                    .setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        } else {
            policy.setEndosgrossPremium(prevPolicy.getEndosgrossPremium()
                    .add((refundPrem.add(polextras).add(polstampDuty).add(polphfFund).add(polTl))
                            .setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN)));
        }

        if (policy.getPaidPremium().compareTo(policy.getEndosgrossPremium()) == 1) {
            BigDecimal refundableAmt = policy.getPaidPremium().subtract(policy.getEndosgrossPremium());
            if (refundableAmt.compareTo(policy.getBasicPrem().abs()) == 1) {
                policy.setRefundablePremium(policy.getBasicPrem().abs());
            } else {
                policy.setRefundablePremium(refundableAmt);
            }

        } else {
            policy.setRefundablePremium(BigDecimal.ZERO);
        }
        policyRepo.save(policy);

    }

    @Override
    public void computeCancelLifePrem(Long polCode) throws BadRequestException, IOException {
        PolicyTrans policy = policyRepo.findOne(polCode);
        Iterable<PolicyInstallments> policyInstallmentsList = policyInstallmentsRepo.findAll(QPolicyInstallments.policyInstallments.policyTrans.policyId.eq(polCode));

    }

    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void computeLifePrem(Long polCode) throws BadRequestException, IOException {
        PolicyTrans policy = policyRepo.findOne(polCode);
        System.out.println(polCode);
        policyService.populateClauses(policy);
        policyService.populateTaxes(policy);
        Iterable<RiskTrans> risks = riskRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(policy.getPolicyId()));
        Iterable<PolicyTaxes> policyTaxes = polTaxesRepo
                .findAll(QPolicyTaxes.policyTaxes.policy.policyId.eq(policy.getPolicyId()));
        BigDecimal premium = BigDecimal.ZERO;
        BigDecimal negotiatedPremium = policy.getNegotiatedPremium();
        BigDecimal sumInsured = BigDecimal.ZERO;
        BigDecimal investment = BigDecimal.ZERO;
        BigDecimal totalCommission = BigDecimal.ZERO;
        BigDecimal totalSubAgentComm = BigDecimal.ZERO;
        BigDecimal polstampDuty = BigDecimal.ZERO;
        BigDecimal polphfFund = BigDecimal.ZERO;
        BigDecimal polwhtxAmt = BigDecimal.ZERO;
        BigDecimal polextras = BigDecimal.ZERO;
        BigDecimal polTl = BigDecimal.ZERO;
        BigDecimal totalPrem = BigDecimal.ZERO;
        BigDecimal sumPolicytaxAmount = BigDecimal.ZERO;

        // // Retrieve the top-up related fields
        // String topUpFrequency = policy.getTopUpFreq();
        // boolean allowTopUps = policy.isAllowTopUps();
        // Date firstTopUpDate = policy.getFirstTopUp();
        // Date lastTopUpDate = policy.getLastTopUp();
        // Integer investmentTerm = policy.getInvestmentTerm();
        // BigDecimal investmentAmount = policy.getInvestment();
        // BigDecimal topUpAmount = policy.getTopUpAmount();
        // String investmentFrequency = policy.getInvestmentFreq();
        for (RiskTrans risk : risks) {
            System.out.println("INVAMT: " + risk.getInvestment());
            Iterable<SectionTrans> sections = sectionRepo
                    .findAll(QSectionTrans.sectionTrans.risk.riskId.eq(risk.getRiskId()));
            BigDecimal riskPrem = BigDecimal.ZERO;
            BigDecimal riskInsured = BigDecimal.ZERO;
            List<PremiumItemsBean> itemsBeen = new ArrayList<>();
            String ratesTable = "";
            if (policy.getBinder().getCalculatorType() != null
                    && !policy.getBinder().getCalculatorType().equalsIgnoreCase("I")) {
                ratesTable = premRatesTableRepo.getRatesLocation(risk.getBinderDetails().getDetId());
                System.out.println(" Sum Assured " + risk.getSumInsured() + " Compute Type..." + risk.getComputeType());
                if (ratesTable == null)
                    throw new BadRequestException("Rates Table for the Binder has not been setup");
            }

            for (SectionTrans section : sections) {
                List<String> items = new ArrayList<>();
                double minPrem = 0;
                if (section.getPremRates() != null) {
                    PremRatesDef premRatesDef = section.getPremRates();
                    if (premRatesDef.getMinPremium() != null
                            && premRatesDef.getMinPremium().compareTo(BigDecimal.ZERO) == 1) {
                        minPrem = premRatesDef.getMinPremium().doubleValue();
                    }
                }

                PremiumItemsBean premiumItemsBean = new PremiumItemsBean(section.getSection().getShtDesc(),
                        section.getRate().doubleValue(),
                        section.getFreeLimit().doubleValue(), minPrem,
                        section.getAmount().doubleValue(), section.getSectId(), section.getDivFactor().doubleValue(),
                        section.getSection().getType().getCode(), section.getSection().getType().getOrder(), items,
                        policy.getPolicyId());
                System.out.println("prem items bean >>>>" + premiumItemsBean);
                if (section.getSection().getType() == SectionTypes.SI) {
                    premiumItemsBean.setMainSection(true);
                } else
                    premiumItemsBean.setMainSection(false);
                premiumItemsBean.setAge(risk.getWorkingAge());
                premiumItemsBean.setFrequency(policy.getFrequency());
                premiumItemsBean.setInvestment(risk.getInvestment());
                premiumItemsBean.setInvestmentFreq(policy.getInvestmentFreq());
                premiumItemsBean.setTopUp(risk.getTopUp());
                premiumItemsBean.setTopUpFreq(policy.getTopUpFreq());
                if ("P".equalsIgnoreCase(risk.getComputeType())) {
                    // premium setting
                    premiumItemsBean.setPremium(risk.getComputePremium());
                    premiumItemsBean.setSumAssured(BigDecimal.ZERO);
                } else if ("B".equalsIgnoreCase(risk.getComputeType())) {
                    premiumItemsBean.setSumAssured(risk.getSumInsured());
                    premiumItemsBean.setPremium(risk.getComputePremium());
                } else if ("S".equalsIgnoreCase(risk.getComputeType())) {
                    premiumItemsBean.setSumAssured(risk.getSumInsured());
                    premiumItemsBean.setPremium(BigDecimal.ZERO);
                }
                premiumItemsBean.setTerm(policy.getPolTerm());
                itemsBeen.add(premiumItemsBean);
            }

            itemsBeen.stream().sorted(Comparator.comparing(PremiumItemsBean::getOrder));
            Iterable<PolicyBenefitsDistribution> existingMaturitie = maturityRepo
                    .findAll(QPolicyBenefitsDistribution.policyBenefitsDistribution.policyId.policyId.eq(polCode));
            for (PolicyBenefitsDistribution premat : existingMaturitie) {
                maturityRepo.delete(premat.getMaturityId());
            }
            // Skip workbook calculations if negotiated premium is present
            if (negotiatedPremium != null) {
                riskPrem = negotiatedPremium;
                sumInsured = risk.getSumInsured() != null ? risk.getSumInsured() : BigDecimal.ZERO;
                investment = risk.getInvestment() != null ? risk.getInvestment() : BigDecimal.ZERO;
                //todo checks
                premium = risk.getPremium() != null ? risk.getPremium() : BigDecimal.ZERO;
            } else {
                final List<Object[]> result = riskRepo.getInsuredDate(risk.getRiskId());
                if (result.isEmpty()) {
                    throw new BadRequestException("Error Getting insured details...");
                }
                final Date dob = (Date) result.get(0)[0];
                final String gender = (String) result.get(0)[1];
                String sheetName = null;
                System.out.println(policy.getProduct().getProGroup().getPrgType());

                if (policy.getProduct().getProGroup().getPrgType().equalsIgnoreCase("L")) {
                    Iterable<SubclassComputationSheetNames> sheetNames = subclassCompSheetNamesRepo
                            .findAll(QSubclassComputationSheetNames.subclassComputationSheetNames.binderDetails.detId
                                    .eq(risk.getBinderDetails().getDetId())
                                    .and(QSubclassComputationSheetNames.subclassComputationSheetNames.computeType
                                            .eq(risk.getComputeType())));
                    if (sheetNames.spliterator().getExactSizeIfKnown() > 0) {
                        sheetName = Streamable.streamOf(sheetNames).map(a -> a.getSheetName()).findFirst().get();
                    }
                    if (risk.getComputeType() == null) {
                        throw new BadRequestException("Compute Type cannot be null...");
                    }
                } else {
                    sheetName = "WealthBuilder Quote";
                }
                System.out.println("ITEMS BEAN: " + itemsBeen);
                System.out.println(sheetName);
                PremiumResultBean resultBean = new PremiumResultBean();
                if (policy.getBinder().getCalculatorType() != null
                        && policy.getBinder().getCalculatorType().equalsIgnoreCase("E")) {
                    resultBean = premExcelUtils.getLifePremium(itemsBeen, ratesTable, dob, gender, policy.getPolTerm(),
                            policy.getFrequency(), polCode, sheetName, risk.getComputeType(), policy.getTopUpFreq(),
                            policy.isAllowTopUps(), policy.getFirstTopUp(), policy.getLastTopUp(),
                            policy.getInvestmentFreq());
                }else  if (policy.getBinder().getCalculatorType() != null
                        && policy.getBinder().getCalculatorType().equalsIgnoreCase("N")) {
                    resultBean = premExcelUtils.getPremium(itemsBeen, ratesTable);
                }
                else {
                    String binName = policy.getBinder().getBinName().toUpperCase();

                    ClientDef clientDef = policy.getClient();
                    PolicyMiscInfo policyMiscInfo = policyMiscInfoRepo.findByPolicyId(policy.getPolicyId());


                    // Check for specific ALAK binder names and override premium calculation
                    if(binName.contains("ALAK") && policy.getTransType().equalsIgnoreCase("NB")) {
                        if (binName.contains("ALAK PERSONAL ACCIDENT")) {
                            validateAlakClientDetails(clientDef);

                            resultBean = computeAlakPAPremium(policy, itemsBeen);
                        } else if (binName.contains("ALAK FAMILY PROTECTION PLAN")) {
                            validateAlakClientDetails(clientDef);

                            resultBean = computeAlakFPPremium(policy, itemsBeen);
                        } else if (binName.contains("ALAK ULTIMATE PROTECTOR")) {
                            validateAlakClientDetails(clientDef);

                            resultBean = computeAlakUPPremium(policy, itemsBeen);
                        } else if (binName.contains("ALAK EDUCATION")) {
                            validateAlakClientDetails(clientDef);

                            Date dobDate = clientDef.getDob();
                            if (dobDate != null) {
                                LocalDate dob1 = Instant.ofEpochMilli(dobDate.getTime())
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate();
                                LocalDate today = LocalDate.now();
                                int age = Period.between(dob1, today).getYears();

                                if (age < 18) {
                                    throw new RuntimeException("Client is supposed to be at least 18 years old.");
                                }
                            }

                            if(risk.getComputeType().equals("P")){
                                resultBean = computeAlakEducationPremiumUsingPremiumValue(policy, itemsBeen);
                            }else{
                                resultBean = computeAlakEdPremium(policy, itemsBeen);
                            }

                        } else if (binName.contains("ALAK ENDOWMENT")) {
                            // endowment
                           validateAlakClientDetails(clientDef);

                            Date dobDate = clientDef.getDob();
                            if (dobDate != null) {
                                LocalDate dob1 = Instant.ofEpochMilli(dobDate.getTime())
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate();
                                LocalDate today = LocalDate.now();
                                int age = Period.between(dob1, today).getYears();

                                if (age < 18) {
                                    throw new RuntimeException("Client is supposed to be at least 18 years old.");
                                }
                            }

                            if (policy.getClient().getTenantType().getClientType().equalsIgnoreCase("C")) {
                                throw new BadRequestException("ALAK Endowment is only for Individual Clients");
                            }
                            if(risk.getComputeType().equals("P")){
                                resultBean = computeAlakPremiumUsingPremiumValue(policy, itemsBeen);
                            }else{
                                resultBean = computeAlakPremium(policy, itemsBeen);
                            }

                        } else{
                            //add new integrations here
                            throw new RuntimeException("Integration pending for "+binName+" Reach admin for support.");
                        }
                    } else{
                        //add new integrations here
                      //  throw new RuntimeException("Integration pending for "+binName+" for transaction type "+policy.getTransType()+" Reach admin for support.");
                        // Check if this is an endorsement transaction for integration-based policies
                        if (!policy.getTransType().equalsIgnoreCase("NB")) {
                                throw new BadRequestException("Endorsement transactions are not supported for " + binName + ".");
                                                    }
                    }

                }
                sumInsured = BigDecimal.valueOf(resultBean.getSumInsured());
                investment = BigDecimal.valueOf(resultBean.getInvestment());
                System.out.println("Sum assured..." + sumInsured);
                riskPrem = BigDecimal.valueOf(resultBean.getPremium());
                riskInsured = sumInsured;
                premium = riskPrem;

                risk.setSemiAnnualPremium(BigDecimal.valueOf(resultBean.getSemiAnnualpremium()));
                risk.setQuartelyPremium(BigDecimal.valueOf(resultBean.getQuarterlypremium()));
                risk.setAnnualPremium(BigDecimal.valueOf(resultBean.getAnnualpremium()));
                risk.setSinglePremium(BigDecimal.valueOf(resultBean.getSinglepremium()));
                risk.setTaxRelief(BigDecimal.valueOf(resultBean.getTaxRelief()));
            }

            // if (policy.getNegotiatedPremium() != null) {
            // risk.setCalcPremium(policy.getNegotiatedPremium());
            // } else {
            // risk.setCalcPremium(riskPrem);
            // }
            riskInsured = sumInsured;
            premium = riskPrem;
            risk.setCalcPremium(riskPrem);
            risk.setPremium(riskPrem);
            risk.setSumInsured(sumInsured);
            risk.setInvestment(investment);
            System.out.println("premium" + risk.getPremium());
            System.out.println("Calculated premium" + risk.getCalcPremium());
            System.out.println("Computated premium" + risk.getComputePremium());
            BigDecimal comm = BigDecimal.ZERO;
            BigDecimal subAgentComm = BigDecimal.ZERO;
            // totSuminsured = totSuminsured.add(riskInsured);
            if (risk.getCommRate() != null) {
                comm = riskPrem.multiply(risk.getCommRate()).divide(BigDecimal.valueOf(100));
                if (policy.getSubAgent() != null) {
                    if (policy.getSubAgent().getAccountType().getCommRate() == null
                            || policy.getSubAgent().getAccountType().getCommRate().compareTo(BigDecimal.ZERO) == 0) {
                        throw new BadRequestException("Sub Agent Commission Rate not setup");
                    }
                    subAgentComm = comm.multiply(
                            policy.getSubAgent().getAccountType().getCommRate().divide(BigDecimal.valueOf(100)));

                }
            }

            if (riskPrem.compareTo(BigDecimal.ZERO) == 1) {
                comm = comm.negate();
                subAgentComm = subAgentComm.negate();
            } else if (riskPrem.compareTo(BigDecimal.ZERO) == -1) {
                comm = comm.abs();
                subAgentComm = subAgentComm.abs();
            }
            risk.setCommAmt(comm);
            risk.setSubAgentComm(subAgentComm);
            totalCommission = totalCommission.add(comm);
            totalSubAgentComm = totalSubAgentComm.add(subAgentComm);

            BigDecimal sumRisktaxAmount = BigDecimal.ZERO;
            BigDecimal riskstampDuty = BigDecimal.ZERO;
            BigDecimal riskphfFund = BigDecimal.ZERO;
            BigDecimal riskwhtxAmt = BigDecimal.ZERO;
            BigDecimal riskextras = BigDecimal.ZERO;
            BigDecimal riskTl = BigDecimal.ZERO;

            for (PolicyTaxes policyTax : policyTaxes) {
                BigDecimal computedTax = calculateTax(premium, policyTax.getTaxRate(), policyTax.getDivFactor(),
                        policyTax.getRateType());
                policyTax.setTaxAmount(computedTax);
                sumPolicytaxAmount = sumPolicytaxAmount.add(computedTax);
                // if (policyTax.getRevenueItems().getItem() == RevenueItems.EX) {
                // if ("R".equalsIgnoreCase(policyTax.getTaxLevel())) {
                // riskextras = riskextras.add(computedTax);
                // sumRisktaxAmount = sumRisktaxAmount.add(computedTax);
                // }
                // } else
                if (policyTax.getRevenueItems().getItem() == RevenueItems.SD) {
                    if ("R".equalsIgnoreCase(policyTax.getTaxLevel())) {
                        riskstampDuty = riskstampDuty.add(computedTax);
                        sumRisktaxAmount = sumRisktaxAmount.add(computedTax);
                    }
                } else if (policyTax.getRevenueItems().getItem() == RevenueItems.PHCF) {
                    if ("R".equalsIgnoreCase(policyTax.getTaxLevel())) {
                        riskphfFund = riskphfFund.add(computedTax);
                        sumRisktaxAmount = sumRisktaxAmount.add(computedTax);
                    }
                } else if (policyTax.getRevenueItems().getItem() == RevenueItems.WHTX) {
                    if ("R".equalsIgnoreCase(policyTax.getTaxLevel())) {
                        riskwhtxAmt = riskwhtxAmt.add(computedTax);
                        sumRisktaxAmount = sumRisktaxAmount.add(computedTax);
                    }
                } else if (policyTax.getRevenueItems().getItem() == RevenueItems.TL) {
                    if ("R".equalsIgnoreCase(policyTax.getTaxLevel())) {
                        riskTl = riskTl.add(computedTax);
                        sumRisktaxAmount = sumRisktaxAmount.add(computedTax);
                    }
                }
            }
            if (policy.getNegotiatedPremium() != null) {
                risk.setNetpremium(policy.getNegotiatedPremium());
            } else {
                risk.setNetpremium(premium.add(sumRisktaxAmount).subtract(comm.abs()));
            }

            risk.setExtras(riskextras);
            risk.setWhtax(riskwhtxAmt);
            risk.setPhfFund(riskphfFund);
            risk.setTrainingLevy(riskTl);
            risk.setStampDuty(riskstampDuty);
            risk.setFuturePrem(premium);
            totalPrem = totalPrem.add(premium);
        }
        BigDecimal policyLevelCommRate = BigDecimal.ZERO;
        if (totalPrem.compareTo(BigDecimal.ZERO) != 0)
            policyLevelCommRate = totalCommission.divide(totalPrem, 2, RoundingMode.HALF_EVEN);
        if (policy.getBinder().getMinPrem() != null) {
            if (policy.getBinder().getMinPrem().compareTo(totalPrem) == 1) {
                totalPrem = policy.getBinder().getMinPrem();
                totalCommission = policyLevelCommRate.multiply(totalPrem);
                if (policy.getSubAgent() != null) {
                    if (policy.getSubAgent().getAccountType().getCommRate() == null
                            || policy.getSubAgent().getAccountType().getCommRate().compareTo(BigDecimal.ZERO) == 0) {
                        throw new BadRequestException("Sub Agent Commission Rate not setup");
                    }
                    totalSubAgentComm = totalCommission.multiply(
                            policy.getSubAgent().getAccountType().getCommRate().divide(BigDecimal.valueOf(100)));

                }
            }
        }

        if (policy.getProduct().getMinPrem() != null) {
            if (policy.getProduct().getMinPrem().compareTo(totalPrem) == 1) {
                totalPrem = policy.getProduct().getMinPrem();
                totalCommission = policyLevelCommRate.multiply(totalPrem);
                if (policy.getSubAgent() != null) {
                    if (policy.getSubAgent().getAccountType().getCommRate() == null
                            || policy.getSubAgent().getAccountType().getCommRate().compareTo(BigDecimal.ZERO) == 0) {
                        throw new BadRequestException("Sub Agent Commission Rate not setup");
                    }
                    totalSubAgentComm = totalCommission.multiply(
                            policy.getSubAgent().getAccountType().getCommRate().divide(BigDecimal.valueOf(100)));
                }

            }
        }

        polTaxesRepo.save(policyTaxes);
        BigDecimal whtxAmt = BigDecimal.ZERO;
        AccountTypes accType = policy.getAgent() != null ? policy.getAgent().getAccountType() : null;
        if (accType != null && accType.isWhtxAppl()) {
            if (accType.getWhtaxVal().compareTo(BigDecimal.ZERO) == 1) {
                whtxAmt = accType.getWhtaxVal().divide(new BigDecimal(100)).multiply(totalCommission);
                polwhtxAmt = whtxAmt.negate();
            }
        }

        if (StringUtils.equalsIgnoreCase(paramService.getParameterString("SUB_AGENT_COMM_PARAM"), "N")) {
            if (policy.getSubAgent() != null) {
                totalSubAgentComm = (totalCommission.abs().subtract(whtxAmt.abs()))
                        .multiply(policy.getSubAgent().getAccountType().getCommRate().divide(BigDecimal.valueOf(100)));
            }
        }

        if (totalPrem.compareTo(BigDecimal.ZERO) == 1) {
            totalCommission = totalCommission.abs().negate();
            totalSubAgentComm = totalSubAgentComm.abs().negate();
        } else if (totalPrem.compareTo(BigDecimal.ZERO) == -1) {
            totalCommission = totalCommission.abs();
            totalSubAgentComm = totalSubAgentComm.abs();
        }

        sumPolicytaxAmount = sumPolicytaxAmount.add(polwhtxAmt);

        for (PolicyTaxes policyTax : policyTaxes) {
            BigDecimal computedTax = calculateTax(totalPrem, policyTax.getTaxRate(), policyTax.getDivFactor(),
                    policyTax.getRateType());

            // if (policyTax.getRevenueItems().getItem() == RevenueItems.EX) {
            //
            // polextras = polextras.add(computedTax);
            // } else
            if (policyTax.getRevenueItems().getItem() == RevenueItems.SD) {

                polstampDuty = polstampDuty.add(computedTax);
            } else if (policyTax.getRevenueItems().getItem() == RevenueItems.PHCF) {

                polphfFund = polphfFund.add(computedTax);
            } else if (policyTax.getRevenueItems().getItem() == RevenueItems.TL) {

                polTl = polTl.add(computedTax);
            }
        }
        riskRepo.save(risks);
        Currencies currencies = policy.getTransCurrency();
        totalPrem = totalPrem.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN);
        totalCommission = totalCommission.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN);
        policy.setPremium(totalPrem);
        if (policy.getNegotiatedPremium() != null) {
            policy.setBasicPrem(policy.getNegotiatedPremium());
        } else {
            policy.setBasicPrem((totalPrem.add(polextras).add(polstampDuty).add(polphfFund).add(polTl))
                    .setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        }
        policy.setEndosbasicPremium(totalPrem);
        policy.setEndosCommissions(totalCommission);
        policy.setCommAmt(totalCommission);
        policy.setSubAgentComm(totalSubAgentComm.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setExtras(polextras.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setWhtx(polwhtxAmt.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setPhcf(polphfFund.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setTrainingLevy(polTl.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setTotTrainingLevy(polTl.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setTotPhcf(polphfFund.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setStampDuty(polstampDuty.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setSumInsured(sumInsured);
        policy.setInvestment(investment);
        policy.setNetPrem((totalPrem.add(polextras).add(polstampDuty).add(polphfFund).add(polTl).add(polwhtxAmt)
                .add(totalCommission)).setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        if (policy.isRenewable())
            policy.setFuturePrem((totalPrem.add(polextras).add(polphfFund).add(polTl))
                    .setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        else
            policy.setFuturePrem(BigDecimal.ZERO);
        Iterable<PolicyInstallments> installments = policyInstallmentsRepo
                .findAll(QPolicyInstallments.policyInstallments.policyTrans.policyId.eq(polCode));
        policyInstallmentsRepo.delete(installments);
        final BigDecimal payablePrem = (totalPrem.add(polextras).add(polstampDuty).add(polphfFund).add(polTl)
                .add(polwhtxAmt).add(totalCommission)).setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN);
        LocalDateTime wefDate = new java.util.Date(policy.getCoverFrom().getTime())
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
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

        LifeSubAgentCommissionRates lifeSubAgentCommissionRates = null;

        if(policy.getSubAgent()!=null) {
            if (lifeSubAgentCommissionRatesRepo.count(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.binderDef.eq(policy.getBinder())
                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.accountTypes.accountType.eq(AccountTypeEnum.SUB))
                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermFrom.loe(policy.getPolTerm()))
                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermTo.goe(policy.getPolTerm()))
                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wefDate.loe(policy.getCoverFrom()))
                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.goe(policy.getCoverFrom()).or(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.isNull()))
                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.frequency.equalsIgnoreCase(policy.getFrequency()))
            ) <= 0) {
                throw new BadRequestException("No SubAgent commission rate defined...." + policy.getPolTerm() + "; frequency: " + policy.getFrequency());

            }
            if (lifeSubAgentCommissionRatesRepo.count(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.binderDef.eq(policy.getBinder())
                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.accountTypes.accountType.eq(AccountTypeEnum.SUB))
                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermFrom.loe(policy.getPolTerm()))
                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermTo.goe(policy.getPolTerm()))
                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wefDate.loe(policy.getCoverFrom()))
                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.goe(policy.getCoverFrom()).or(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.isNull()))
                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.frequency.equalsIgnoreCase(policy.getFrequency()))
            ) > 1) {
                throw new BadRequestException("More than one Sub-Agent commission rate exists....");
            }

              lifeSubAgentCommissionRates = lifeSubAgentCommissionRatesRepo.findOne(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.binderDef.eq(policy.getBinder())
                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.accountTypes.accountType.eq(AccountTypeEnum.SUB))
                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermFrom.loe(policy.getPolTerm()))
                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermTo.goe(policy.getPolTerm()))
                      .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wefDate.loe(policy.getCoverFrom()))
                      .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.goe(policy.getCoverFrom()).or(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.isNull()))
                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.frequency.equalsIgnoreCase(policy.getFrequency())));
        }

        LifeSubAgentCommissionRates lifeMarketerCommissionRates = null;

        if(policy.getMarketerAgent()!=null) {
            if (lifeSubAgentCommissionRatesRepo.count(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.binderDef.eq(policy.getBinder())
                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.accountTypes.accountType.eq(AccountTypeEnum.MRK))
                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermFrom.loe(policy.getPolTerm()))
                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermTo.goe(policy.getPolTerm()))
                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wefDate.loe(policy.getCoverFrom()))
                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.goe(policy.getCoverFrom()).or(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.isNull()))
                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.frequency.equalsIgnoreCase(policy.getFrequency()))
            ) <= 0) {
                throw new BadRequestException("No Marketer commission rate defined...." + policy.getPolTerm() + "; frequency: " + policy.getFrequency());

            }
            if (lifeSubAgentCommissionRatesRepo.count(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.binderDef.eq(policy.getBinder())
                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.accountTypes.accountType.eq(AccountTypeEnum.MRK))
                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermFrom.loe(policy.getPolTerm()))
                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermTo.goe(policy.getPolTerm()))
                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.frequency.equalsIgnoreCase(policy.getFrequency()))
            ) > 1) {
                throw new BadRequestException("More than one Marketer commission rate exists....");
            }

            lifeMarketerCommissionRates = lifeSubAgentCommissionRatesRepo.findOne(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.binderDef.eq(policy.getBinder())
                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.accountTypes.accountType.eq(AccountTypeEnum.MRK))
                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermFrom.loe(policy.getPolTerm()))
                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermTo.goe(policy.getPolTerm()))
                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.frequency.equalsIgnoreCase(policy.getFrequency())));
        }

         BigDecimal totalSubAgentCom = BigDecimal.ZERO;
         BigDecimal totalMarketerAgentCom = BigDecimal.ZERO;
         int annualInstForAnnualFreq = 0;
        for (int i = 1; i <= installament; i++) {
            double paidYears = Math.ceil(((double) i/paymentsPerYear));
            if (lifeCommissionRatesRepo.count(QLifeCommissionRates.lifeCommissionRates.binderDef.eq(policy.getBinder())
                    .and(QLifeCommissionRates.lifeCommissionRates.commTermFrom.loe(policy.getPolTerm()))
                    .and(QLifeCommissionRates.lifeCommissionRates.commTermTo.goe(policy.getPolTerm()))
                    .and(QLifeCommissionRates.lifeCommissionRates.wefDate.loe(policy.getCoverFrom()))
                    .and(QLifeCommissionRates.lifeCommissionRates.wetDate.goe(policy.getCoverFrom()).or(QLifeCommissionRates.lifeCommissionRates.wetDate.isNull()))
                    .and(QLifeCommissionRates.lifeCommissionRates.commYearFrom.loe(paidYears))
                    .and(QLifeCommissionRates.lifeCommissionRates.frequency.equalsIgnoreCase(policy.getFrequency()))
                    .and(QLifeCommissionRates.lifeCommissionRates.commYearTo.goe(paidYears))) <= 0) {
                throw new BadRequestException("No commission rate defined....for term " + policy.getPolTerm() + "; Effective From " + formatDate(policy.getCoverFrom()) + ";To date " + formatDate(policy.getCoverFrom()) + "; Commission Year .." + paidYears + ";Frequesncy: " + policy.getFrequency());

            }
            if (lifeCommissionRatesRepo.count(QLifeCommissionRates.lifeCommissionRates.binderDef.eq(policy.getBinder())
                    .and(QLifeCommissionRates.lifeCommissionRates.commTermFrom.loe(policy.getPolTerm()))
                    .and(QLifeCommissionRates.lifeCommissionRates.commTermTo.goe(policy.getPolTerm()))
                    .and(QLifeCommissionRates.lifeCommissionRates.wefDate.loe(policy.getCoverFrom()))
                    .and(QLifeCommissionRates.lifeCommissionRates.wetDate.goe(policy.getCoverFrom()).or(QLifeCommissionRates.lifeCommissionRates.wetDate.isNull()))
                    .and(QLifeCommissionRates.lifeCommissionRates.commYearFrom.loe(paidYears))
                    .and(QLifeCommissionRates.lifeCommissionRates.frequency.equalsIgnoreCase(policy.getFrequency()))
                    .and(QLifeCommissionRates.lifeCommissionRates.commYearTo.goe(paidYears))) > 1) {
                throw new BadRequestException("More than one commission rate exists....");
            }
            LifeCommissionRates commissionRates = lifeCommissionRatesRepo.findOne(QLifeCommissionRates.lifeCommissionRates.binderDef.eq(policy.getBinder())
                    .and(QLifeCommissionRates.lifeCommissionRates.commTermFrom.loe(policy.getPolTerm()))
                    .and(QLifeCommissionRates.lifeCommissionRates.commTermTo.goe(policy.getPolTerm()))
                    .and(QLifeCommissionRates.lifeCommissionRates.wefDate.loe(policy.getCoverFrom()))
                    .and(QLifeCommissionRates.lifeCommissionRates.wetDate.goe(policy.getCoverFrom()).or(QLifeCommissionRates.lifeCommissionRates.wetDate.isNull()))
                    .and(QLifeCommissionRates.lifeCommissionRates.commYearFrom.loe(paidYears))
                    .and(QLifeCommissionRates.lifeCommissionRates.frequency.equalsIgnoreCase(policy.getFrequency()))
                    .and(QLifeCommissionRates.lifeCommissionRates.commYearTo.goe(paidYears)));



            final PolicyInstallments policyInstallments = new PolicyInstallments();
            policyInstallments.setInstallPaid("N");
            policyInstallments.setNotificationSent(false);
            if (policy.getNegotiatedPremium() != null) {
                policyInstallments.setInstallPrem(policy.getNegotiatedPremium());
            } else {
                policyInstallments.setInstallPrem(payablePrem);
            }
            if (commissionRates != null) {
                BigDecimal commRate = commissionRates.getCommRate();
                BigDecimal comm = (commRate.multiply(policyInstallments.getInstallPrem()).divide(commissionRates.getCommDivFactor())).setScale(2, BigDecimal.ROUND_HALF_EVEN);
                policyInstallments.setExpectedCommission(comm);
                if(paidYears <=1) {
                    if (lifeSubAgentCommissionRates != null) {
                        BigDecimal subAgentComm = (lifeSubAgentCommissionRates.getCommRate().multiply(comm).divide(lifeSubAgentCommissionRates.getCommDivFactor())).setScale(2, BigDecimal.ROUND_HALF_EVEN);
                        policyInstallments.setExpectedSubAgentCommission(subAgentComm);
                    }
                    if (lifeMarketerCommissionRates != null) {
                        BigDecimal mkrAgentComm  = (lifeMarketerCommissionRates.getCommRate().multiply(comm).divide(lifeMarketerCommissionRates.getCommDivFactor())).setScale(2, BigDecimal.ROUND_HALF_EVEN);
                        policyInstallments.setExpectedMarkerterCommission(mkrAgentComm);
                    }
                }
            }
            policyInstallments.setInstallmentNo((long) i);
            policyInstallments.setPolicyTrans(policy);


            if (policy.getFrequency().equalsIgnoreCase("M")) {
                policyInstallments.setDueDate(Date.from(
                        wefDate.plusMonths(j)
                                .atZone(ZoneId.systemDefault())
                                .toInstant()));
                j++;
            } else if (policy.getFrequency().equalsIgnoreCase("Q")) {
                policyInstallments.setDueDate(
                        Date.from(
                                wefDate
                                        .plusMonths(i * 3L) // use months directly
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()
                        )
                );
            } else if (policy.getFrequency().equalsIgnoreCase("S")) {
                policyInstallments.setDueDate(
                        Date.from(
                                wefDate
                                        .plusMonths(i * 6L) // use months directly
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()
                        )
                );
            } else if (policy.getFrequency().equalsIgnoreCase("A")) {
                policyInstallments.setDueDate(Date.from(
                        wefDate.plus(annualInstForAnnualFreq, ChronoUnit.YEARS)
                                .atZone(ZoneId.systemDefault())
                                .toInstant()));
                        annualInstForAnnualFreq++;
            }
            installmentsList.add(policyInstallments);
        }
        policyInstallmentsRepo.save(installmentsList);
        if (policy.getSubAgent()!=null && policy.getSubAgent().getCommissionEarning().equalsIgnoreCase("Yes")) {
            policy.setSubAgentComm(totalSubAgentCom);
        }
        if (policy.getMarketerAgent()!=null && policy.getMarketerAgent().getCommissionEarning().equalsIgnoreCase("Yes")) {
            policy.setMarketerAgentComm(totalMarketerAgentCom);
        }
        PolicyTrans savedPolicy = policyRepo.save(policy);
        Iterable<RiskTrans> savedRisks = riskRepo
                .findAll(QRiskTrans.riskTrans.policy.policyId.eq(savedPolicy.getPolicyId()));
        for (RiskTrans savedRisk : savedRisks) {
            policyService.populateRiskScheduleDetails(savedRisk.getRiskId());
        }

    }


    public static String formatDate(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(date);
    }

    /**
     * Computes the premium for an ALAK endowment policy using the premium value.
     *
     * @param policyTrans The policy transaction details.
     * @param itemsBeans  The list of premium items.
     * @return A PremiumResultBean containing the computed premium and related details.
     * @throws BadRequestException If the premium value is invalid or API call fails.
     */
    @Transactional
    public PremiumResultBean computeAlakPremiumUsingPremiumValue(PolicyTrans policyTrans, List<PremiumItemsBean> itemsBeans)
            throws BadRequestException {
        GenerateQuoteForEndowmentPolicyRequest request = new GenerateQuoteForEndowmentPolicyRequest();
        boolean ahb = false;
        PremiumResultBean premiumResultBean = new PremiumResultBean();
        BigDecimal premiumAtEntry = itemsBeans.stream().filter(PremiumItemsBean::isMainSection)
                .map(PremiumItemsBean::getPremium)
                .findAny().orElse(BigDecimal.ZERO);

//        BigDecimal min = new BigDecimal("3670");
//        if (premiumAtEntry.compareTo(min) < 0 ) {
//            throw new BadRequestException("Premium should be equal to or greater than 3670");
//        }

        int frequency = 0;
        String polFrequency = policyTrans.getFrequency();
        if (polFrequency.equalsIgnoreCase("M")) {
            frequency = 1;
        } else if (polFrequency.equalsIgnoreCase("Q")) {
            frequency = 3;
        } else if (polFrequency.equalsIgnoreCase("S")) {
            frequency = 6;
        } else if (polFrequency.equalsIgnoreCase("A")) {
            frequency = 12;
        } else if (polFrequency.equalsIgnoreCase("SG")) {
            frequency = 1;
        }

        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        request.setFrequency(frequency);
//        request.setInceptionDate(LocalDateTime.parse(
//                outputFormat.format(policyTrans.getCoverFrom()),
//                DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        request.setInceptionDate(outputFormat.format(policyTrans.getCoverFrom()));
        request.setPremiumAtEntry(premiumAtEntry.doubleValue());
        request.setInflationPercentage(0);
        request.setPremiumPaymentTerms(policyTrans.getPolTerm());
        request.setMaturityPeriod(1);

        if (premiumAtEntry.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Premium at entry cannot be zero for this calculator...");
        }

        premiumResultBean.setPremium(premiumAtEntry.doubleValue());
        SectionTransDTO section = new SectionTransDTO();
        for (PremiumItemsBean premiumItemsBean : itemsBeans) {
            if (premiumItemsBean.getPremiumId().equalsIgnoreCase("Main Benefit")) {
                List<Object[]> sectionArr = sectionRepo.findSectionTransById(premiumItemsBean.getSectId());
                if (!sectionArr.isEmpty()) {
                    final BigDecimal rate = (BigDecimal) sectionArr.get(0)[7];
                    final BigDecimal freeLimit = (BigDecimal) sectionArr.get(0)[4];
                    final BigDecimal divFactor = (BigDecimal) sectionArr.get(0)[3];
                    final Long sectId = ((BigInteger) sectionArr.get(0)[0]).longValue();
                    final Long sectSectionId = ((BigInteger) sectionArr.get(0)[8]).longValue();
                    final Long premRateId = ((BigInteger) sectionArr.get(0)[9]).longValue();
                    section.setPremRatesId(premRateId);
                    section.setRate(rate);
                    section.setFreeLimit(freeLimit);
                    section.setDivFactor(divFactor);
                    section.setSectId(sectId);
                    section.setSectionSectId(sectSectionId);
                }
            }

            if (premiumItemsBean.getPremiumId().equalsIgnoreCase("Accidental Hospitalization Benefit")) {
                List<Object[]> sectionArr = sectionRepo.findSectionTransById(premiumItemsBean.getSectId());
                if (!sectionArr.isEmpty()) {
                    final BigDecimal rate = (BigDecimal) sectionArr.get(0)[7];
                    final BigDecimal freeLimit = (BigDecimal) sectionArr.get(0)[4];
                    final BigDecimal divFactor = (BigDecimal) sectionArr.get(0)[3];
                    final Long sectId = ((BigInteger) sectionArr.get(0)[0]).longValue();
                    final Long sectSectionId = ((BigInteger) sectionArr.get(0)[8]).longValue();
                    final Long premRateId = ((BigInteger) sectionArr.get(0)[9]).longValue();
                    section.setPremRatesId(premRateId);
                    section.setRate(rate);
                    section.setFreeLimit(freeLimit);
                    section.setDivFactor(divFactor);
                    section.setSectId(sectId);
                    section.setSectionSectId(sectSectionId);
                }
                ahb = true;
            }

            request.setMedicalUWQ1(false);
            request.setMedicalUWQ2(false);
            request.setMedicalUWQ3(false);
            request.setMedicalUWQ4(false);
            request.setAccidentalHospitalisationBenefit(ahb);
//            request.setMainMemberBirthDate(LocalDateTime.parse(
//                    outputFormat.format(policyTrans.getClient().getDob()),
//                    DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            request.setMainMemberBirthDate(outputFormat.format(policyTrans.getClient().getDob()));
            String gender = policyTrans.getClient().getGender();
            int mainMemberGender = gender.equalsIgnoreCase("F") ? 1 : 2;
            request.setMainMemberGender(mainMemberGender);
            boolean staffDiscount = policyTrans.getClient().getTenantType().getTypeDesc().equalsIgnoreCase("STAFF");
            request.setStaffDiscount(staffDiscount);
            System.out.println("ALAK endowment PAYLOAD: " + new Gson().toJson(request));
            try {
                String paramValue = paramService.getParameterString("API_INTEGRATION_URL") + "/kit/api/v1/endowment/quote/calculate-sum-assured-from-premium";
                HttpHeaders headers = new HttpHeaders();
                headers.set("Content-Type", "application/json");
                HttpEntity<GenerateQuoteForEndowmentPolicyRequest> requestEntity = new HttpEntity<>(request, headers);
                ResponseEntity<String> responseEntity = restTemplate.postForEntity(paramValue, requestEntity, String.class);
                String responseBody = responseEntity.getBody();
                JsonNode rootNode = objectMapper.readTree(responseBody);
                JsonNode projectionDetails = rootNode.get("ProjectionDetails");

                if (projectionDetails == null || !projectionDetails.isArray()) {
                    throw new BadRequestException("Invalid or missing ProjectionDetails in API response.");
                }

                List<PolicyBenefitsDistribution> benefitsDistributions = new ArrayList<>();
                List<PolicySurrenderValues> surrenderValuesList = new ArrayList<>();
                BigDecimal premium = BigDecimal.ZERO;
                BigDecimal sumAssured = BigDecimal.ZERO;

                for (JsonNode projectionDetail : projectionDetails) {
                    PolicySurrenderValues surrenderValues = new PolicySurrenderValues();
                    surrenderValues.setPolicyId(policyTrans);
                    surrenderValues.setSurrenderYear(String.format("Year %d", projectionDetail.get("Year").asInt()));
                    surrenderValues.setSurrenderValue(projectionDetail.get("SurrenderValue").decimalValue());
                    surrenderValuesList.add(surrenderValues);
                    premium = projectionDetail.get("Premium").decimalValue();
                    sumAssured = projectionDetail.get("SumAssured").decimalValue();

                    premiumResultBean.setPremium(premium.doubleValue());
                    premiumResultBean.setSumInsured(sumAssured.doubleValue());
                    String frequency1 = policyTrans.getFrequency();
                    if (frequency1.equalsIgnoreCase("Q")) {
                        premiumResultBean.setQuarterlypremium(premium.doubleValue());
                    } else if (frequency1.equalsIgnoreCase("S")) {
                        premiumResultBean.setSemiAnnualpremium(premium.doubleValue());
                    } else if (frequency1.equalsIgnoreCase("A")) {
                        premiumResultBean.setAnnualpremium(premium.doubleValue());
                    }
                    if (premiumItemsBean.getPremiumId().equalsIgnoreCase("Main Benefit")) {
                        section.setPrem(projectionDetail.get("MainBenefitPremium").decimalValue());
                    }
                    if (premiumItemsBean.getPremiumId().equalsIgnoreCase("Accidental Hospitalization Benefit")) {
                        section.setPrem(projectionDetail.get("HospitalisationBenefitPremium").decimalValue());
                    }
                    section.setCalcprem(section.getPrem());
                    sectionRepo.updateSectionSumAssuredDetails(
                            (section.getPrem() != null) ? section.getPrem() : BigDecimal.ZERO,
                            (section.getCalcprem() != null) ? section.getCalcprem() : BigDecimal.ZERO,
                            (section.getAmount() != null) ? section.getAmount() : BigDecimal.ZERO,
                            section.getSectId());
                }

                JsonNode annualMaturityBenefits = rootNode.get("AnnualMaturityBenefit");
                if (annualMaturityBenefits == null || !annualMaturityBenefits.isArray()) {
                    throw new BadRequestException("Invalid or missing AnnualMaturityBenefit in API response.");
                }

                for (JsonNode maturityBenefit : annualMaturityBenefits) {
                    PolicyBenefitsDistribution benefitsDistribution = new PolicyBenefitsDistribution();
                    benefitsDistribution.setPolicyId(policyTrans);
                    Pattern pattern = Pattern.compile("year (\\d+)");
                    Matcher matcher = pattern.matcher(maturityBenefit.get("strText").asText());

                    if (matcher.find()) {
                        int year = Integer.parseInt(matcher.group(1));
                        benefitsDistribution.setMaturityYear(String.format("Year %s", year));
                    }
                    benefitsDistribution.setEstBenefit(maturityBenefit.get("AnnualMaturityBenefit").asDouble());
                    benefitsDistribution.setMaturityExpDate(Date.from(
                            LocalDateTime
                                    .parse(maturityBenefit.get("MaturityDate").asText(),
                                            DateTimeFormatter.ISO_DATE_TIME)
                                    .atZone(ZoneId.systemDefault()).toInstant()));
                    benefitsDistributions.add(benefitsDistribution);
                }

                policyBenefitsDistributionRepo.save(benefitsDistributions);
                policySurrenderValuesRepo.save(surrenderValuesList);
            } catch (JsonProcessingException e) {
                throw new BadRequestException("Error computing premium: " + e.getMessage());
            } catch (HttpServerErrorException ex) {
                throw new BadRequestException("ALAK Integration service has issues...Please contact Support for assistance");
            } catch (RestClientException ex) {
                throw new BadRequestException("ALAK Integration service has issues..." + ex.getMessage());
            }
        }
        return premiumResultBean;
    }


    @Transactional
    public PremiumResultBean computeAlakPremium(PolicyTrans policyTrans, List<PremiumItemsBean> itemsBeans)
            throws BadRequestException {
        CalculatePremiumRequest request = new CalculatePremiumRequest();
        boolean ahb = false;
        PremiumResultBean premiumResultBean = new PremiumResultBean();
        BigDecimal sumAssured = itemsBeans.stream().filter(PremiumItemsBean::isMainSection)
                .map(PremiumItemsBean::getSumAssured).findAny().isPresent()
                        ? itemsBeans.stream().filter(PremiumItemsBean::isMainSection)
                                .map(PremiumItemsBean::getSumAssured).findAny().get()
                        : BigDecimal.ZERO;
//        BigDecimal min = new BigDecimal("1000000");
//        BigDecimal max = new BigDecimal("100000000");
//        if (sumAssured.compareTo(min) < 0 || sumAssured.compareTo(max) > 0) {
//            throw new BadRequestException("Sum Assured at entry should be between KES 1,000,000 and KES 100,000,000");
//        }

        int frequency = 0;
        String polFrequency = policyTrans.getFrequency();
        if (polFrequency.equalsIgnoreCase("M")) {
            frequency = 1;
        } else if (polFrequency.equalsIgnoreCase("Q")) {
            frequency = 3;
        } else if (polFrequency.equalsIgnoreCase("S")) {
            frequency = 6;
        } else if (polFrequency.equalsIgnoreCase("A")) {
            frequency = 12;
        } else if (polFrequency.equalsIgnoreCase("SG")) {
            frequency = 1;
        }

        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        request.setFrequency(frequency);
        request.setInceptionDate(outputFormat.format(policyTrans.getCoverFrom()));
        request.setSumAssuredAtEntry(sumAssured.intValue());
        request.setInflationPercentage(0);
        request.setPremiumPaymentTerms(policyTrans.getPolTerm());
        request.setMaturityPeriod(1);

        if (sumAssured.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Sum Assured cannot be zero for this calculator...");
        }
        premiumResultBean.setSumInsured(sumAssured.doubleValue());
        SectionTransDTO section = new SectionTransDTO();
        for (PremiumItemsBean premiumItemsBean : itemsBeans) {
            if (premiumItemsBean.getPremiumId().equalsIgnoreCase("Main Benefit")) {
                List<Object[]> sectionArr = sectionRepo.findSectionTransById(premiumItemsBean.getSectId());
                if (!sectionArr.isEmpty()) {
                    final BigDecimal rate = (BigDecimal) sectionArr.get(0)[7];
                    final BigDecimal freeLimit = (BigDecimal) sectionArr.get(0)[4];
                    final BigDecimal divFactor = (BigDecimal) sectionArr.get(0)[3];
                    final Long sectId = ((BigInteger) sectionArr.get(0)[0]).longValue();
                    final Long sectSectionId = ((BigInteger) sectionArr.get(0)[8]).longValue();
                    final Long premRateId = ((BigInteger) sectionArr.get(0)[9]).longValue();
                    section.setPremRatesId(premRateId);
                    section.setRate(rate);
                    section.setFreeLimit(freeLimit);
                    section.setDivFactor(divFactor);
                    section.setSectId(sectId);
                    section.setSectionSectId(sectSectionId);
                }

            }

            if (premiumItemsBean.getPremiumId().equalsIgnoreCase("Accidental Hospitalization Benefit")) {
                List<Object[]> sectionArr = sectionRepo.findSectionTransById(premiumItemsBean.getSectId());
                if (!sectionArr.isEmpty()) {
                    final BigDecimal rate = (BigDecimal) sectionArr.get(0)[7];
                    final BigDecimal freeLimit = (BigDecimal) sectionArr.get(0)[4];
                    final BigDecimal divFactor = (BigDecimal) sectionArr.get(0)[3];
                    final Long sectId = ((BigInteger) sectionArr.get(0)[0]).longValue();
                    final Long sectSectionId = ((BigInteger) sectionArr.get(0)[8]).longValue();
                    final Long premRateId = ((BigInteger) sectionArr.get(0)[9]).longValue();
                    section.setPremRatesId(premRateId);
                    section.setRate(rate);
                    section.setFreeLimit(freeLimit);
                    section.setDivFactor(divFactor);
                    section.setSectId(sectId);
                    section.setSectionSectId(sectSectionId);
                }
                ahb = true;
            }

            request.setMedicalUWQ1(false);
            request.setMedicalUWQ2(false);
            request.setMedicalUWQ3(false);
            request.setMedicalUWQ4(false);
            request.setAccidentalHospitalisationBenefit(ahb);
            request.setMainMemberBirthDate(outputFormat.format(policyTrans.getClient().getDob()));
            String gender = policyTrans.getClient().getGender();
            int mainMemberGender = gender.equalsIgnoreCase("F") ? 1 : 2;
            request.setMainMemberGender(mainMemberGender);
            boolean staffDiscount = policyTrans.getClient().getTenantType().getTypeDesc().equalsIgnoreCase("STAFF");
            request.setStaffDiscount(staffDiscount);
            System.out.println("ALAK endowmnet PAYLOAD: " + new Gson().toJson(request));
            try {
                String paramValue = paramService.getParameterString("API_INTEGRATION_URL") + "/alak/calculatePremium";
                HttpHeaders headers = new HttpHeaders();
                headers.set("Content-Type", "application/json");
                HttpEntity<?> requestEntity = new HttpEntity<>(request, headers);
                ResponseEntity<String> responseEntity = restTemplate.postForEntity(paramValue, requestEntity,
                        String.class);
                String responseBody = responseEntity.getBody();
                JsonNode rootNode = objectMapper.readTree(responseBody);
                JsonNode projectionDetails = rootNode.get("ProjectionDetails");

                if (projectionDetails == null || !projectionDetails.isArray()) {
                    throw new BadRequestException("Invalid or missing ProjectionDetails in API response.");
                }

                List<PolicyBenefitsDistribution> benefitsDistributions = new ArrayList<>();
                List<PolicySurrenderValues> surrenderValuesList = new ArrayList<>();
                BigDecimal premium = BigDecimal.ZERO;

                for (JsonNode projectionDetail : projectionDetails) {
                    PolicySurrenderValues surrenderValues = new PolicySurrenderValues();
                    surrenderValues.setPolicyId(policyTrans);
                    surrenderValues.setSurrenderYear(String.format("Year %d", projectionDetail.get("Year").asInt()));
                    surrenderValues.setSurrenderValue(projectionDetail.get("SurrenderValue").decimalValue());
                    surrenderValuesList.add(surrenderValues);
                    premium = projectionDetail.get("Premium").decimalValue();

                    premiumResultBean.setPremium(premium.doubleValue());
                    String frequency1 = policyTrans.getFrequency();
                    if (frequency1.equalsIgnoreCase("Q")) {
                        premiumResultBean.setQuarterlypremium(premium.doubleValue());
                    } else if (frequency1.equalsIgnoreCase("S")) {
                        premiumResultBean.setSemiAnnualpremium(premium.doubleValue());
                    } else if (frequency1.equalsIgnoreCase("A")) {
                        premiumResultBean.setAnnualpremium(premium.doubleValue());
                    }
                    if (premiumItemsBean.getPremiumId().equalsIgnoreCase("Main Benefit")) {
                        section.setPrem(projectionDetail.get("MainBenefitPremium").decimalValue());
                    }
                    if (premiumItemsBean.getPremiumId().equalsIgnoreCase("Accidental Hospitalization Benefit")) {
                        section.setPrem(projectionDetail.get("HospitalisationBenefitPremium").decimalValue());
                    }
                    section.setCalcprem(section.getPrem());
                    sectionRepo.updateSectionSumAssuredDetails(
                            (section.getPrem() != null) ? section.getPrem() : BigDecimal.ZERO,
                            (section.getCalcprem() != null) ? section.getCalcprem() : BigDecimal.ZERO,
                            (section.getAmount() != null) ? section.getAmount() : BigDecimal.ZERO,
                            section.getSectId());

                    // PolicyInstallments installment = new PolicyInstallments();
                    // installment.setInstallPaid("N");
                    // installment.setInstallPrem(projectionDetail.get("Premium").decimalValue());
                    // installment.setInstallmentNo((long) installmentsList.size() + 1);
                    // installment.setPolicyTrans(policyTrans);
                    // installment.setDueDate(Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    // installmentsList.add(installment);
                }
                JsonNode annualMaturityBenefits = rootNode.get("AnnualMaturityBenefit");

                if (annualMaturityBenefits == null || !annualMaturityBenefits.isArray()) {
                    throw new BadRequestException("Invalid or missing ProjectionDetails in API response.");
                }

                for (JsonNode maturityBenefit : annualMaturityBenefits) {
                    PolicyBenefitsDistribution benefitsDistribution = new PolicyBenefitsDistribution();
                    benefitsDistribution.setPolicyId(policyTrans);
                    Pattern pattern = Pattern.compile("year (\\d+)");
                    Matcher matcher = pattern.matcher(maturityBenefit.get("strText").asText());

                    if (matcher.find()) {
                        int year = Integer.parseInt(matcher.group(1));
                        benefitsDistribution.setMaturityYear(String.format("Year %s", year));
                    }
                    benefitsDistribution.setEstBenefit(maturityBenefit.get("AnnualMaturityBenefit").asDouble());
                    benefitsDistribution.setMaturityExpDate(Date.from(
                            LocalDateTime
                                    .parse(maturityBenefit.get("MaturityDate").asText(),
                                            DateTimeFormatter.ISO_DATE_TIME)
                                    .atZone(ZoneId.systemDefault()).toInstant()));
                    benefitsDistributions.add(benefitsDistribution);
                }

                policyBenefitsDistributionRepo.save(benefitsDistributions);
                policySurrenderValuesRepo.save(surrenderValuesList);
            } catch (JsonProcessingException e) {
                throw new BadRequestException("Error computing premium: " + e.getMessage());
            }
            catch (HttpServerErrorException ex) {
                throw new BadRequestException("ELAK Integration service has issues...Please contact Support for assistance");

            } catch (RestClientException ex) {
                throw new BadRequestException("ELAK Integration service has issues..."+ex.getMessage());
            }
        }
        return premiumResultBean;
    }

    @Transactional
    public PremiumResultBean computeAlakPAPremium(PolicyTrans policyTrans, List<PremiumItemsBean> itemsBeans)
            throws BadRequestException {
        CalculatePersonalAccidentPremiumRequest request = new CalculatePersonalAccidentPremiumRequest();

        PolicyMiscInfo policyMiscInfo = policyMiscInfoRepo.findByPolicyId(policyTrans.getPolicyId());

        boolean ahb = false;
        PremiumResultBean premiumResultBean = new PremiumResultBean();
        BigDecimal sumAssured = itemsBeans.stream().filter(PremiumItemsBean::isMainSection)
                .map(PremiumItemsBean::getSumAssured).findAny().isPresent()
                        ? itemsBeans.stream().filter(PremiumItemsBean::isMainSection)
                                .map(PremiumItemsBean::getSumAssured).findAny().get()
                        : BigDecimal.ZERO;
        log.info("all the premioum item beans "+itemsBeans);
        log.info("sum insured"+sumAssured);

//        BigDecimal min = new BigDecimal("500000");
//        BigDecimal max = new BigDecimal("5000000");
//        if (sumAssured.compareTo(min) < 0 || sumAssured.compareTo(max) > 0) {
//            throw new BadRequestException("Sum Assured at entry should be between KES 500,000 and KES 5,000,000");
//        }

        int frequency = 0;
        String polFrequency = policyTrans.getFrequency();
        if (polFrequency.equalsIgnoreCase("M")) {
            frequency = 1;
        } else if (polFrequency.equalsIgnoreCase("Q")) {
            frequency = 3;
        } else if (polFrequency.equalsIgnoreCase("S")) {
            frequency = 6;
        } else if (polFrequency.equalsIgnoreCase("A")) {
            frequency = 12;
        } else if (polFrequency.equalsIgnoreCase("SG")) {
            frequency = 1;
        }

        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        request.setFrequency(frequency);
        request.setInceptionDate(outputFormat.format(policyTrans.getCoverFrom()));
        //request.setCoverOptionId(Integer.parseInt(policyMiscInfo.getCoverOption()));
        request.setCoverOptionId(1);
        request.setSumInsured(sumAssured.intValue());

        premiumResultBean.setSumInsured(sumAssured.doubleValue());
        SectionTransDTO section = new SectionTransDTO();
        for (PremiumItemsBean premiumItemsBean : itemsBeans) {
            if (premiumItemsBean.getPremiumId().equalsIgnoreCase("Main Benefit")) {
                List<Object[]> sectionArr = sectionRepo.findSectionTransById(premiumItemsBean.getSectId());
                if (!sectionArr.isEmpty()) {
                    final BigDecimal rate = (BigDecimal) sectionArr.get(0)[7];
                    final BigDecimal freeLimit = (BigDecimal) sectionArr.get(0)[4];
                    final BigDecimal divFactor = (BigDecimal) sectionArr.get(0)[3];
                    final Long sectId = ((BigInteger) sectionArr.get(0)[0]).longValue();
                    final Long sectSectionId = ((BigInteger) sectionArr.get(0)[8]).longValue();
                    final Long premRateId = ((BigInteger) sectionArr.get(0)[9]).longValue();
                    section.setPremRatesId(premRateId);
                    section.setRate(rate);
                    section.setFreeLimit(freeLimit);
                    section.setDivFactor(divFactor);
                    section.setSectId(sectId);
                    section.setSectionSectId(sectSectionId);
                }

            }

            if (premiumItemsBean.getPremiumId().equalsIgnoreCase("Accidental Hospitalization Benefit")) {
                List<Object[]> sectionArr = sectionRepo.findSectionTransById(premiumItemsBean.getSectId());
                if (!sectionArr.isEmpty()) {
                    final BigDecimal rate = (BigDecimal) sectionArr.get(0)[7];
                    final BigDecimal freeLimit = (BigDecimal) sectionArr.get(0)[4];
                    final BigDecimal divFactor = (BigDecimal) sectionArr.get(0)[3];
                    final Long sectId = ((BigInteger) sectionArr.get(0)[0]).longValue();
                    final Long sectSectionId = ((BigInteger) sectionArr.get(0)[8]).longValue();
                    final Long premRateId = ((BigInteger) sectionArr.get(0)[9]).longValue();
                    section.setPremRatesId(premRateId);
                    section.setRate(rate);
                    section.setFreeLimit(freeLimit);
                    section.setDivFactor(divFactor);
                    section.setSectId(sectId);
                    section.setSectionSectId(sectSectionId);
                }
                ahb = true;
            }
        }
            request.setAccidentalHospitalisationBenefit(ahb);
            request.setMainMemberFullName(policyTrans.getClient().getFname() + " " + policyTrans.getClient().getOtherNames());
            request.setMainMemberBirthDate(outputFormat.format(policyTrans.getClient().getDob()));
            String spouseName = "";
            List<PolicyDependentsInfo> policyTotalDependents = policyDependentsRepo.findByPolicyId(policyTrans.getPolicyId());
            int totalCount = policyTotalDependents.size();
//
//            if(Integer.parseInt(policyMiscInfo.getCoverOption()) == 1){
//                //only policy holder info is accepted
//                List<PolicyDependentsInfo> policyDependentsInfo = policyDependentsRepo.findByPolicyIdAndDeptType(policyTrans.getPolicyId(),"main_member");
//                if(!policyDependentsInfo.isEmpty() || totalCount > 1){
//                    throw  new BadRequestException("Cover option 1, allows only policy holder.");
//                }
//
//            } else if(Integer.parseInt(policyMiscInfo.getCoverOption()) == 2){
//                // Policyholder plus Spouse only (max of 1)
//                List<PolicyDependentsInfo> policySpouseDependentsInfo = policyDependentsRepo.findByPolicyIdAndDeptType(policyTrans.getPolicyId(),"spouse");
//                if(!policySpouseDependentsInfo.isEmpty()) {
//                    if (policySpouseDependentsInfo.size() > 1 || (totalCount > policySpouseDependentsInfo.size()+1)) {
//                        throw new BadRequestException("Cover option 2, allows only policy holder and 1 (ONE) spouse only.");
//                    }
//                    spouseName = policySpouseDependentsInfo.get(0).getFullName();
//                }
//            } else if(Integer.parseInt(policyMiscInfo.getCoverOption()) == 3){
//                //Policyholder plus Children (max of 5)
//
//                List<PolicyDependentsInfo> policyChildDependentsInfo = policyDependentsRepo.findByPolicyIdAndDeptType(policyTrans.getPolicyId(), "child");
//
//                //check if child is empty
//                if(!policyChildDependentsInfo.isEmpty()) {
//                    if (policyChildDependentsInfo.size() > 5 || (totalCount > policyChildDependentsInfo.size()+1)) {
//                        throw new BadRequestException("Cover option 3, allow only 5 (FIVE) children max only.");
//                    }
//
//                    for (int i = 0; i < policyChildDependentsInfo.size(); i++) {
//                        PolicyDependentsInfo childDep = policyChildDependentsInfo.get(i);
//                        CalculatePersonalAccidentPremiumRequest.Child child = new CalculatePersonalAccidentPremiumRequest.Child();
//                        child.setFullName(childDep.getFullName());
//                        child.setBirthDate(childDep.getDOB().toString());
//                        child.setStillBorn(childDep.getStillBorn());
//                        child.setStudent(childDep.getIsStudent());
//
//                        switch (i) {
//                            case 0:
//                                request.setChild1(child);
//                                break;
//                            case 1:
//                                request.setChild2(child);
//                                break;
//                            case 2:
//                                request.setChild3(child);
//                                break;
//                            case 3:
//                                request.setChild4(child);
//                                break;
//                            case 4:
//                                request.setChild5(child);
//                                break;
//                        }
//                    }
//                }
//
//            } else if(Integer.parseInt(policyMiscInfo.getCoverOption()) == 4){
//                //Policyholder, Spouse (max of 1) plus Children (max of 5)
//
//                List<PolicyDependentsInfo> policySpouseDependentsInfo = policyDependentsRepo.findByPolicyIdAndDeptType(policyTrans.getPolicyId(),"spouse");
//                List<PolicyDependentsInfo> policyChildDependentsInfo = policyDependentsRepo.findByPolicyIdAndDeptType(policyTrans.getPolicyId(), "child");
//
//                if(!policySpouseDependentsInfo.isEmpty()) {
//                    if (policySpouseDependentsInfo.size() > 1 || (totalCount > policySpouseDependentsInfo.size() +1)) {
//                        throw new BadRequestException("Cover option 4, allows only 1 (ONE) spouse only.");
//                    }
//                    spouseName = policySpouseDependentsInfo.get(0).getFullName();
//                }
//
//                //check if child is empty
//                if(!policyChildDependentsInfo.isEmpty()) {
//                    if (policyChildDependentsInfo.size() > 5 || (totalCount > policyChildDependentsInfo.size()+1) ) {
//                        throw new BadRequestException("Cover option 4, allow only 5 (FIVE) children max only.");
//                    }
//
//                    for (int i = 0; i < policyChildDependentsInfo.size(); i++) {
//                        PolicyDependentsInfo childDep = policyChildDependentsInfo.get(i);
//                        CalculatePersonalAccidentPremiumRequest.Child child = new CalculatePersonalAccidentPremiumRequest.Child();
//                        child.setFullName(childDep.getFullName());
//                        child.setBirthDate(childDep.getDOB().toString());
//
//                        switch (i) {
//                            case 0:
//                                request.setChild1(child);
//                                break;
//                            case 1:
//                                request.setChild2(child);
//                                break;
//                            case 2:
//                                request.setChild3(child);
//                                break;
//                            case 3:
//                                request.setChild4(child);
//                                break;
//                            case 4:
//                                request.setChild5(child);
//                                break;
//                        }
//                    }
//                }
//
//            }
            request.setSpouseFullName(spouseName);

            System.out.println("ALAK PA PAYLOAD: " + new Gson().toJson(request));
            try {
                String paramValue = paramService.getParameterString("API_INTEGRATION_URL")
                        + "/alak/calculatePersonalAccidentPremium";
                HttpHeaders headers = new HttpHeaders();
                headers.set("Content-Type", "application/json");
                HttpEntity<?> requestEntity = new HttpEntity<>(request, headers);
                ResponseEntity<String> responseEntity = restTemplate.postForEntity(paramValue, requestEntity,
                        String.class);

                log.info("Getting a respose for alak pA {}",responseEntity.getBody());
                if (responseEntity.getStatusCode() != HttpStatus.OK) {
                    // bad request handling
                    JsonNode error = objectMapper.readTree(responseEntity.getBody());
                    String message = Optional.ofNullable(error.get("Message")).map(JsonNode::asText)
                            .orElse("Confirm your input parameters");
                    throw new BadRequestException(message);
                }

                JsonNode rootNode = objectMapper.readTree(responseEntity.getBody());

                BigDecimal premium = BigDecimal.ZERO;
                String currency = rootNode.get("Currency").asText();

                double totalAnnualPremium = rootNode.get("TotalAnnualPremium").asDouble(); // basic prem = installment
                double totalMonthlyPremium = rootNode.get("TotalMonthlyPremium").asDouble();// // prem

                premiumResultBean.setQuarterlypremium(totalMonthlyPremium*4);
                premiumResultBean.setSemiAnnualpremium(totalMonthlyPremium*6);
                premiumResultBean.setSinglepremium(totalMonthlyPremium);
                premiumResultBean.setAnnualpremium(totalAnnualPremium);

                premiumResultBean.setPremium(totalAnnualPremium);


                // Extracting main member information
                JsonNode mainMember = rootNode.get("MainMember");
                if(mainMember != null && !mainMember.isNull()) {
                    String mainMemberName = mainMember.get("Name").asText();
                    double mainMemberSumInsured = mainMember.get("SumInsured").asDouble();
                    // premiumResultBean.setSumInsured(mainMemberSumInsured);
                    double mainMemberAnnualPremium = mainMember.get("AnnualPremium").asDouble();
                    double mainMemberMonthlyPremium = mainMember.get("MonthlyPremium").asDouble();
                    double accidentalHospitalisationPremium = mainMember.get("AccidentalHospitalisationPremium").asDouble();

                    if (ahb) {
                        section.setPrem(BigDecimal.valueOf(accidentalHospitalisationPremium));

                        section.setCalcprem(section.getPrem());
                        // sectionRepo.updateSectionSumAssuredDetails(
                        //         (section.getPrem() != null) ? section.getPrem() : BigDecimal.ZERO,
                        //         (section.getCalcprem() != null) ? section.getCalcprem() : BigDecimal.ZERO,
                        //         (section.getAmount() != null) ? section.getAmount() : BigDecimal.ZERO,
                        //         section.getSectId());
                    }

                    updateAlakDependeciesPrem(mainMemberName, policyTrans.getPolicyId(), "main_member", mainMemberMonthlyPremium, mainMemberAnnualPremium, mainMemberSumInsured, accidentalHospitalisationPremium);
                }
                //spouse
                JsonNode mainMemberSpouse = rootNode.get("Spouse");
                if(mainMemberSpouse != null && !mainMemberSpouse.isNull()) {
                    String mainMemberSpouseName = mainMemberSpouse.get("Name").asText();
                    double mainMemberSpouseSumInsured = mainMemberSpouse.get("SumInsured").asDouble();

                    updateAlakDependeciesPrem(mainMemberSpouseName, policyTrans.getPolicyId(), "spouse", null, null, mainMemberSpouseSumInsured, null);
                }
                List<String> childrenKeys = Arrays.asList("Child1", "Child2", "Child3", "Child4", "Child5");

                for (String childKey : childrenKeys) {
                    JsonNode childNode = rootNode.get(childKey);
                    if (childNode != null && !childNode.isNull()) {
                        String childName = childNode.get("Name").asText();
                        double childSumInsured = childNode.get("SumInsured").asDouble();

                        // Optional: log or store per-dependent premiums
                        log.info("child {} premium: {} name {}", childKey,childSumInsured, childName);
                        updateAlakDependeciesPrem(childName, policyTrans.getPolicyId(),"child",null,null,childSumInsured,null);
                    }
                }

                // JsonNode projectionDetails = rootNode.get("ProjectionDetails");

                // if (projectionDetails == null || !projectionDetails.isArray()) {
                // throw new BadRequestException("Invalid or missing ProjectionDetails in API
                // response.");
                // }

                // List<PolicyBenefitsDistribution> benefitsDistributions = new ArrayList<>();
                // List<PolicySurrenderValues> surrenderValuesList = new ArrayList<>();
                // BigDecimal premium = BigDecimal.ZERO;

                // for (JsonNode projectionDetail : projectionDetails) {
                // PolicySurrenderValues surrenderValues = new PolicySurrenderValues();
                // surrenderValues.setPolicyId(policyTrans);
                // surrenderValues.setSurrenderYear(String.format("Year %d",
                // projectionDetail.get("Year").asInt()));
                // surrenderValues.setSurrenderValue(projectionDetail.get("SurrenderValue").decimalValue());
                // surrenderValuesList.add(surrenderValues);
                // premium = projectionDetail.get("Premium").decimalValue();

                // premiumResultBean.setPremium(premium.doubleValue());
                // String frequency1 = policyTrans.getFrequency();
                // if (frequency1.equalsIgnoreCase("Q")) {
                // premiumResultBean.setQuarterlypremium(premium.doubleValue());
                // } else if (frequency1.equalsIgnoreCase("S")) {
                // premiumResultBean.setSemiAnnualpremium(premium.doubleValue());
                // } else if (frequency1.equalsIgnoreCase("A")) {
                // premiumResultBean.setAnnualpremium(premium.doubleValue());
                // }
                // if (premiumItemsBean.getPremiumId().equalsIgnoreCase("Main Benefit")) {
                // section.setPrem(projectionDetail.get("MainBenefitPremium").decimalValue());
                // }
                // if (premiumItemsBean.getPremiumId().equalsIgnoreCase("Accidental
                // Hospitalization Benefit")) {
                // section.setPrem(projectionDetail.get("HospitalisationBenefitPremium").decimalValue());
                // }
                // section.setCalcprem(section.getPrem());
                // sectionRepo.updateSectionSumAssuredDetails(
                // (section.getPrem() != null) ? section.getPrem() : BigDecimal.ZERO,
                // (section.getCalcprem() != null) ? section.getCalcprem() : BigDecimal.ZERO,
                // (section.getAmount() != null) ? section.getAmount() : BigDecimal.ZERO,
                // section.getSectId());

                // // PolicyInstallments installment = new PolicyInstallments();
                // // installment.setInstallPaid("N");
                // //
                // installment.setInstallPrem(projectionDetail.get("Premium").decimalValue());
                // // installment.setInstallmentNo((long) installmentsList.size() + 1);
                // // installment.setPolicyTrans(policyTrans);
                // //
                // installment.setDueDate(Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                // // installmentsList.add(installment);
                // }
                // JsonNode annualMaturityBenefits = rootNode.get("AnnualMaturityBenefit");

                // if (annualMaturityBenefits == null || !annualMaturityBenefits.isArray()) {
                // throw new BadRequestException("Invalid or missing ProjectionDetails in API
                // response.");
                // }

                // for (JsonNode maturityBenefit : annualMaturityBenefits) {
                // PolicyBenefitsDistribution benefitsDistribution = new
                // PolicyBenefitsDistribution();
                // benefitsDistribution.setPolicyId(policyTrans);
                // Pattern pattern = Pattern.compile("year (\\d+)");
                // Matcher matcher = pattern.matcher(maturityBenefit.get("strText").asText());

                // if (matcher.find()) {
                // int year = Integer.parseInt(matcher.group(1));
                // benefitsDistribution.setMaturityYear(String.format("Year %s", year));
                // }
                // benefitsDistribution.setEstBenefit(maturityBenefit.get("AnnualMaturityBenefit").asDouble());
                // benefitsDistribution.setMaturityExpDate(Date.from(
                // LocalDateTime
                // .parse(maturityBenefit.get("MaturityDate").asText(),
                // DateTimeFormatter.ISO_DATE_TIME)
                // .atZone(ZoneId.systemDefault()).toInstant()));
                // benefitsDistributions.add(benefitsDistribution);
                // }

                // policyBenefitsDistributionRepo.save(benefitsDistributions);
                // policySurrenderValuesRepo.save(surrenderValuesList);
            } catch (JsonProcessingException e) {
                throw new BadRequestException("Error computing premium: " + e.getMessage());
            }
            catch (HttpServerErrorException ex) {
                throw new BadRequestException("ELAK Integration service has issues...Please contact Support for assistance");

            } catch (RestClientException ex) {
                throw new BadRequestException("ELAK Integration service has issues..."+ex.getMessage());
            }

        return premiumResultBean;
    }

    @Transactional
    public PremiumResultBean computeAlakFPPremium(PolicyTrans policyTrans, List<PremiumItemsBean> itemsBeans)
            throws BadRequestException {
        CalculateFamilyProtectionPremiumRequest request = new CalculateFamilyProtectionPremiumRequest();

        PolicyMiscInfo policyMiscInfo = policyMiscInfoRepo.findByPolicyId(policyTrans.getPolicyId());

        boolean ahb = false;
        PremiumResultBean premiumResultBean = new PremiumResultBean();
        BigDecimal sumAssured = itemsBeans.stream().filter(PremiumItemsBean::isMainSection)
                .map(PremiumItemsBean::getSumAssured).findAny().isPresent()
                        ? itemsBeans.stream().filter(PremiumItemsBean::isMainSection)
                                .map(PremiumItemsBean::getSumAssured).findAny().get()
                        : BigDecimal.ZERO;
        BigDecimal min = new BigDecimal("50000");
        BigDecimal max = new BigDecimal("500000");
        log.info("all the premioum item beans "+itemsBeans);
        log.info("sum insured"+sumAssured);

        if (sumAssured.compareTo(min) < 0 || sumAssured.compareTo(max) > 0) {
            throw new BadRequestException("Sum Assured at entry should be between KES 50,000 and KES 500,000");
        }

        int frequency = 0;
        String polFrequency = policyTrans.getFrequency();
        if (polFrequency.equalsIgnoreCase("M")) {
            frequency = 1;
        } else if (polFrequency.equalsIgnoreCase("Q")) {
            frequency = 3;
        } else if (polFrequency.equalsIgnoreCase("S")) {
            frequency = 6;
        } else if (polFrequency.equalsIgnoreCase("A")) {
            frequency = 12;
        } else if (polFrequency.equalsIgnoreCase("SG")) {
            frequency = 1;
        }

        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        request.setFrequency(frequency);
        request.setInceptionDate(outputFormat.format(policyTrans.getCoverFrom()));
        //request.setFuneralCoverOptionId(Integer.parseInt(policyMiscInfo.getCoverOption())); //cover options
        request.setFuneralCoverOptionId(1); //cover options

        request.setSumInsured(sumAssured.intValue());
        request.setMainMemberFullName(policyTrans.getClient().getFname() + " " + policyTrans.getClient().getOtherNames());
        request.setMainMemberBirthDate(policyTrans.getClient().getDob().toString());
        String spouseName = "";

        List<PolicyDependentsInfo> policyTotalDependents = policyDependentsRepo.findByPolicyId(policyTrans.getPolicyId());
        int totalCount = policyTotalDependents.size();
//
//        if(Integer.parseInt(policyMiscInfo.getCoverOption()) == 1){
//            //only policy holder info is accepted
//            List<PolicyDependentsInfo> policyDependentsInfo = policyDependentsRepo.findByPolicyIdAndDeptType(policyTrans.getPolicyId(),"main_member");
//            if(!policyDependentsInfo.isEmpty() || totalCount > 1){
//                throw  new BadRequestException("Cover option 1, allows only policy holder.");
//            }
//
//        } else if(Integer.parseInt(policyMiscInfo.getCoverOption()) == 2){
//           // Policyholder plus Spouse only (max of 1)
//            List<PolicyDependentsInfo> policySpouseDependentsInfo = policyDependentsRepo.findByPolicyIdAndDeptType(policyTrans.getPolicyId(),"spouse");
//            if(!policySpouseDependentsInfo.isEmpty()) {
//                if (policySpouseDependentsInfo.size() > 1 || (totalCount > policySpouseDependentsInfo.size()+1)) {
//                    throw new BadRequestException("Cover option 2, allows only policy holder and 1 (ONE) spouse only.");
//                }
//                spouseName = policySpouseDependentsInfo.get(0).getFullName();
//            }
//        } else if(Integer.parseInt(policyMiscInfo.getCoverOption()) == 3){
//            //Policyholder plus Children (max of 5)
//
//            List<PolicyDependentsInfo> policyChildDependentsInfo = policyDependentsRepo.findByPolicyIdAndDeptType(policyTrans.getPolicyId(), "child");
//
//            //check if child is empty
//            if(!policyChildDependentsInfo.isEmpty()) {
//                if (policyChildDependentsInfo.size() > 5 || (totalCount > policyChildDependentsInfo.size()+1)) {
//                    throw new BadRequestException("Cover option 3, allow only 5 (FIVE) children max only.");
//                }
//
//                for (int i = 0; i < policyChildDependentsInfo.size(); i++) {
//                    PolicyDependentsInfo childDep = policyChildDependentsInfo.get(i);
//                    CalculateFamilyProtectionPremiumRequest.Child child = new CalculateFamilyProtectionPremiumRequest.Child();
//                    child.setFullName(childDep.getFullName());
//                    child.setBirthDate(childDep.getDOB().toString());
//
//                    switch (i) {
//                        case 0:
//                            request.setChild1(child);
//                            break;
//                        case 1:
//                            request.setChild2(child);
//                            break;
//                        case 2:
//                            request.setChild3(child);
//                            break;
//                        case 3:
//                            request.setChild4(child);
//                            break;
//                        case 4:
//                            request.setChild5(child);
//                            break;
//                    }
//                }
//            }
//
//        } else if(Integer.parseInt(policyMiscInfo.getCoverOption()) == 4){
//            //Policyholder, Spouse (max of 1) plus Children (max of 5)
//
//            List<PolicyDependentsInfo> policySpouseDependentsInfo = policyDependentsRepo.findByPolicyIdAndDeptType(policyTrans.getPolicyId(),"spouse");
//            List<PolicyDependentsInfo> policyChildDependentsInfo = policyDependentsRepo.findByPolicyIdAndDeptType(policyTrans.getPolicyId(), "child");
//
//            if(!policySpouseDependentsInfo.isEmpty()) {
//                if (policySpouseDependentsInfo.size() > 1 || (totalCount > policySpouseDependentsInfo.size() +1)) {
//                    throw new BadRequestException("Cover option 4, allows only 1 (ONE) spouse only.");
//                }
//                spouseName = policySpouseDependentsInfo.get(0).getFullName();
//            }
//
//            //check if child is empty
//            if(!policyChildDependentsInfo.isEmpty()) {
//                if (policyChildDependentsInfo.size() > 5 || (totalCount > policyChildDependentsInfo.size()+1) ) {
//                    throw new BadRequestException("Cover option 4, allow only 5 (FIVE) children max only.");
//                }
//
//                for (int i = 0; i < policyChildDependentsInfo.size(); i++) {
//                    PolicyDependentsInfo childDep = policyChildDependentsInfo.get(i);
//                    CalculateFamilyProtectionPremiumRequest.Child child = new CalculateFamilyProtectionPremiumRequest.Child();
//                    child.setFullName(childDep.getFullName());
//                    child.setBirthDate(childDep.getDOB().toString());
//
//                    switch (i) {
//                        case 0:
//                            request.setChild1(child);
//                            break;
//                        case 1:
//                            request.setChild2(child);
//                            break;
//                        case 2:
//                            request.setChild3(child);
//                            break;
//                        case 3:
//                            request.setChild4(child);
//                            break;
//                        case 4:
//                            request.setChild5(child);
//                            break;
//                    }
//                }
//            }
//
//        } else if(Integer.parseInt(policyMiscInfo.getCoverOption()) == 5){
//            //Stand-alone Parents (max of 4) /Extended Family (max of 8)
//            List<PolicyDependentsInfo> policyParentDependentsInfo = policyDependentsRepo.findByPolicyIdAndDeptType(policyTrans.getPolicyId(),"parent");
//            List<PolicyDependentsInfo> policyEfmDependentsInfo = policyDependentsRepo.findByPolicyIdAndDeptType(policyTrans.getPolicyId(),"efm");
//
//            if(policyParentDependentsInfo.size() > 5 || policyEfmDependentsInfo.size() > 8 || (totalCount > 13)){
//                throw new BadRequestException("Cover Option 5, allows only 4 (FOUR) Stand-alone parents max or Extended family of 8 (EIGHT) max people only.");
//            }
//            //parents info
//            if(!policyParentDependentsInfo.isEmpty()) {
//                for (int i = 0; i < policyParentDependentsInfo.size(); i++) {
//                    PolicyDependentsInfo parentDetailsInfo = policyParentDependentsInfo.get(i);
//                    CalculateFamilyProtectionPremiumRequest.Parent parentDetails = new CalculateFamilyProtectionPremiumRequest.Parent();
//                    parentDetails.setFullName(parentDetailsInfo.getFullName());
//                    parentDetails.setBirthDate(parentDetailsInfo.getDOB().toString());
//                    parentDetails.setSumInsured(Double.parseDouble(String.valueOf(parentDetailsInfo.getSumInsured())));
//
//                    switch (i) {
//                        case 0:
//                            request.setParent1(parentDetails);
//                            break;
//                        case 1:
//                            request.setParent2(parentDetails);
//                            break;
//                        case 2:
//                            request.setParent3(parentDetails);
//                            break;
//                        case 3:
//                            request.setParent4(parentDetails);
//                            break;
//                    }
//                }
//            }
//            //check if efm is empty
//            if(!policyEfmDependentsInfo.isEmpty()) {
//                for (int i = 0; i < policyEfmDependentsInfo.size(); i++) {
//                    PolicyDependentsInfo efmDetailsInfo = policyEfmDependentsInfo.get(i);
//                    CalculateFamilyProtectionPremiumRequest.EFM efmDetails = new CalculateFamilyProtectionPremiumRequest.EFM();
//                    efmDetails.setFullName(efmDetailsInfo.getFullName());
//                    efmDetails.setBirthDate(efmDetailsInfo.getDOB().toString());
//
//                    switch (i) {
//                        case 0:
//                            request.setEFM1(efmDetails);
//                            break;
//                        case 1:
//                            request.setEFM2(efmDetails);
//                            break;
//                        case 2:
//                            request.setEFM3(efmDetails);
//                            break;
//                        case 3:
//                            request.setEFM4(efmDetails);
//                            break;
//                        case 4:
//                            request.setEFM5(efmDetails);
//                            break;
//                        case 5:
//                            request.setEFM6(efmDetails);
//                            break;
//                        case 6:
//                            request.setEFM7(efmDetails);
//                            break;
//                        case 7:
//                            request.setEFM8(efmDetails);
//                            break;
//                    }
//                }
//            }
//        }
//        request.setSpouseFullName(spouseName);
        request.setSpouseFullName("");

        premiumResultBean.setSumInsured(sumAssured.doubleValue());
        SectionTransDTO section = new SectionTransDTO();
        for (PremiumItemsBean premiumItemsBean : itemsBeans) {
            if (premiumItemsBean.getPremiumId().equalsIgnoreCase("Main Benefit")) {
                List<Object[]> sectionArr = sectionRepo.findSectionTransById(premiumItemsBean.getSectId());
                if (!sectionArr.isEmpty()) {
                    final BigDecimal rate = (BigDecimal) sectionArr.get(0)[7];
                    final BigDecimal freeLimit = (BigDecimal) sectionArr.get(0)[4];
                    final BigDecimal divFactor = (BigDecimal) sectionArr.get(0)[3];
                    final Long sectId = ((BigInteger) sectionArr.get(0)[0]).longValue();
                    final Long sectSectionId = ((BigInteger) sectionArr.get(0)[8]).longValue();
                    final Long premRateId = ((BigInteger) sectionArr.get(0)[9]).longValue();
                    section.setPremRatesId(premRateId);
                    section.setRate(rate);
                    section.setFreeLimit(freeLimit);
                    section.setDivFactor(divFactor);
                    section.setSectId(sectId);
                    section.setSectionSectId(sectSectionId);
                }
            }

            if (premiumItemsBean.getPremiumId().equalsIgnoreCase("Accidental Hospitalization Benefit") || (premiumItemsBean.getPremiumId().equalsIgnoreCase("INCOME Benefit"))) {
                List<Object[]> sectionArr = sectionRepo.findSectionTransById(premiumItemsBean.getSectId());
                if (!sectionArr.isEmpty()) {
                    final BigDecimal rate = (BigDecimal) sectionArr.get(0)[7];
                    final BigDecimal freeLimit = (BigDecimal) sectionArr.get(0)[4];
                    final BigDecimal divFactor = (BigDecimal) sectionArr.get(0)[3];
                    final Long sectId = ((BigInteger) sectionArr.get(0)[0]).longValue();
                    final Long sectSectionId = ((BigInteger) sectionArr.get(0)[8]).longValue();
                    final Long premRateId = ((BigInteger) sectionArr.get(0)[9]).longValue();
                    section.setPremRatesId(premRateId);
                    section.setRate(rate);
                    section.setFreeLimit(freeLimit);
                    section.setDivFactor(divFactor);
                    section.setSectId(sectId);
                    section.setSectionSectId(sectSectionId);
                }
                ahb = true;
            }
        }

            request.setIncomeBenefit(ahb); // Accidental hospitalization benefit flag

            System.out.println("ALAK FPP PAYLOAD: " + new Gson().toJson(request));
            try {
                String paramValue = paramService.getParameterString("API_INTEGRATION_URL") + "/alak/calculateFamilyProtectionPremium";
                HttpHeaders headers = new HttpHeaders();
                headers.set("Content-Type", "application/json");
                HttpEntity<?> requestEntity = new HttpEntity<>(request, headers);
                ResponseEntity<String> responseEntity = restTemplate.postForEntity(paramValue, requestEntity, String.class);
                log.info("Getting a respose for alak fpp {}",responseEntity.getBody());
                if (responseEntity.getStatusCode() != HttpStatus.OK) {
                    // bad request handling
                    JsonNode error = objectMapper.readTree(responseEntity.getBody());
                    String message = Optional.ofNullable(error.get("Message")).map(JsonNode::asText)
                            .orElse("Confirm your input parameters");
                    throw new BadRequestException(message);
                }

                JsonNode rootNode = objectMapper.readTree(responseEntity.getBody());

                BigDecimal premium = BigDecimal.ZERO;
                String currency = rootNode.get("Currency").asText();

                double totalAnnualPremium = rootNode.get("TotalAnnualPremium").asDouble(); // basic prem = installment
                double totalMonthlyPremium = rootNode.get("TotalMonthlyPremium").asDouble();// // prem


                premiumResultBean.setQuarterlypremium(totalMonthlyPremium*4);
                premiumResultBean.setSemiAnnualpremium(totalMonthlyPremium*6);
                premiumResultBean.setSinglepremium(totalMonthlyPremium);
                premiumResultBean.setAnnualpremium(totalAnnualPremium);

                premiumResultBean.setPremium(totalAnnualPremium);

                // Extracting main member information
                JsonNode mainMember = rootNode.get("MainMember");
                if(mainMember != null && !mainMember.isNull()) {
                    String mainMemberName = mainMember.get("Name").asText();
                    double mainMemberSumInsured = mainMember.get("SumInsured").asDouble();
                    premiumResultBean.setSumInsured(mainMemberSumInsured);
                    double mainMemberAnnualPremium = mainMember.get("AnnualPremium").asDouble();
                    double mainMemberMonthlyPremium = mainMember.get("MonthlyPremium").asDouble();
                    double accidentalHospitalisationPremium = mainMember.get("IncomeBenefitPremium").asDouble();

                    if (ahb) {
                        section.setPrem(BigDecimal.valueOf(accidentalHospitalisationPremium));

                        section.setCalcprem(section.getPrem());
                        // sectionRepo.updateSectionSumAssuredDetails(
                        //         (section.getPrem() != null) ? section.getPrem() : BigDecimal.ZERO,
                        //         (section.getCalcprem() != null) ? section.getCalcprem() : BigDecimal.ZERO,
                        //         (section.getAmount() != null) ? section.getAmount() : BigDecimal.ZERO,
                        //         section.getSectId());
                    }

                    updateAlakDependeciesPrem(mainMemberName, policyTrans.getPolicyId(), "main_member", mainMemberMonthlyPremium, mainMemberAnnualPremium, mainMemberSumInsured, accidentalHospitalisationPremium);
                }

                //spouse
                JsonNode mainMemberSpouse = rootNode.get("Spouse");
                if(mainMemberSpouse != null && !mainMemberSpouse.isNull()) {
                    String mainMemberSpouseName = mainMemberSpouse.get("Name").asText();
                    double mainMemberSpouseSumInsured = mainMemberSpouse.get("SumInsured").asDouble();

                    updateAlakDependeciesPrem(mainMemberSpouseName, policyTrans.getPolicyId(), "spouse", null, null, mainMemberSpouseSumInsured, null);
                }

                List<String> childrenKeys = Arrays.asList("Child1", "Child2", "Child3", "Child4", "Child5");
                List<String> parentKeys = Arrays.asList("Parent1", "Parent2", "Parent3", "Parent4");
                List<String> efmKeys = Arrays.asList("EFM1", "EFM2", "EFM3", "EFM4", "EFM5", "EFM6", "EFM7", "EFM8");

                for (String childKey : childrenKeys) {
                    JsonNode childNode = rootNode.get(childKey);
                    if (childNode != null && !childNode.isNull()) {
                        String childName = childNode.get("Name").asText();
                        double childSumInsured = childNode.get("SumInsured").asDouble();

                        // Optional: log or store per-dependent premiums
                        log.info("child {} premium: {} name {}", childKey,childSumInsured, childName);
                        updateAlakDependeciesPrem(childName, policyTrans.getPolicyId(),"child",null,null,childSumInsured,null);
                    }
                }

                for (String parentKey : parentKeys) {
                    JsonNode parentNode = rootNode.get(parentKey);
                    if (parentNode != null && !parentNode.isNull()) {
                        String parentName = parentNode.get("Name").asText();
                        double parentSumInsured = parentNode.get("SumInsured").asDouble();
                        double parentMonthlyPrem = parentNode.get("MonthlyPremium").asDouble();
                        double parentAnnualPremium = parentNode.get("AnnualPremium").asDouble();


                        // Optional: log or store per-dependent premiums
                        log.info("Parent {} premium: {}", parentKey, parentAnnualPremium);
                        updateAlakDependeciesPrem(parentName, policyTrans.getPolicyId(),"parent",parentMonthlyPrem,parentAnnualPremium,parentSumInsured,null);
                    }
                }

                for (String efmKey : efmKeys) {
                    JsonNode efmNode = rootNode.get(efmKey);
                    if (efmNode != null && !efmNode.isNull()) {
                        String efmName = efmNode.get("Name").asText();
                        double efmSumInsured = efmNode.get("SumInsured").asDouble();
                        double efmMonthlyPrem = efmNode.get("MonthlyPremium").asDouble();
                        double efmAnnualPremium = efmNode.get("AnnualPremium").asDouble();

                        log.info("EFM {} premium: {}", efmKey, efmAnnualPremium);
                        updateAlakDependeciesPrem(efmName, policyTrans.getPolicyId(),"efm",efmMonthlyPrem,efmAnnualPremium,efmSumInsured,null);
                    }
                }


                // List<PolicyBenefitsDistribution> benefitsDistributions = new ArrayList<>();
                // List<PolicySurrenderValues> surrenderValuesList = new ArrayList<>();
                // BigDecimal premium = BigDecimal.ZERO;

                // for (JsonNode projectionDetail : projectionDetails) {
                // PolicySurrenderValues surrenderValues = new PolicySurrenderValues();
                // surrenderValues.setPolicyId(policyTrans);
                // surrenderValues.setSurrenderYear(String.format("Year %d",
                // projectionDetail.get("Year").asInt()));
                // surrenderValues.setSurrenderValue(projectionDetail.get("SurrenderValue").decimalValue());
                // surrenderValuesList.add(surrenderValues);
                // premium = projectionDetail.get("Premium").decimalValue();

                // premiumResultBean.setPremium(premium.doubleValue());

                // if (premiumItemsBean.getPremiumId().equalsIgnoreCase("Accidental
                // Hospitalization Benefit")) {
                // section.setPrem(projectionDetail.get("HospitalisationBenefitPremium").decimalValue());
                // }
                // section.setCalcprem(section.getPrem());
                // sectionRepo.updateSectionSumAssuredDetails((section.getPrem() != null) ?
                // section.getPrem() : BigDecimal.ZERO,
                // (section.getCalcprem() != null) ? section.getCalcprem() : BigDecimal.ZERO,
                // (section.getAmount() != null) ? section.getAmount() : BigDecimal.ZERO,
                // section.getSectId());

                // // PolicyInstallments installment = new PolicyInstallments();
                // // installment.setInstallPaid("N");
                // //
                // installment.setInstallPrem(projectionDetail.get("Premium").decimalValue());
                // // installment.setInstallmentNo((long) installmentsList.size() + 1);
                // // installment.setPolicyTrans(policyTrans);
                // //
                // installment.setDueDate(Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                // // installmentsList.add(installment);
                // }
                // JsonNode annualMaturityBenefits = rootNode.get("AnnualMaturityBenefit");

                // if (annualMaturityBenefits == null || !annualMaturityBenefits.isArray()) {
                // throw new BadRequestException("Invalid or missing ProjectionDetails in API
                // response.");
                // }

                // for (JsonNode maturityBenefit : annualMaturityBenefits) {
                // PolicyBenefitsDistribution benefitsDistribution = new
                // PolicyBenefitsDistribution();
                // benefitsDistribution.setPolicyId(policyTrans);
                // Pattern pattern = Pattern.compile("year (\\d+)");
                // Matcher matcher = pattern.matcher(maturityBenefit.get("strText").asText());

                // if (matcher.find()) {
                // int year = Integer.parseInt(matcher.group(1));
                // benefitsDistribution.setMaturityYear(String.format("Year %s", year));
                // }
                // benefitsDistribution.setEstBenefit(maturityBenefit.get("AnnualMaturityBenefit").asDouble());
                // benefitsDistribution.setMaturityExpDate(Date.from(
                // LocalDateTime.parse(maturityBenefit.get("MaturityDate").asText(),
                // DateTimeFormatter.ISO_DATE_TIME)
                // .atZone(ZoneId.systemDefault()).toInstant()
                // ));
                // benefitsDistributions.add(benefitsDistribution);
                // }

                // policyBenefitsDistributionRepo.save(benefitsDistributions);
                // policySurrenderValuesRepo.save(surrenderValuesList);
            }catch (JsonProcessingException e) {
                throw new BadRequestException("Error computing premium: " + e.getMessage());
            }
            catch (HttpServerErrorException ex) {
                throw new BadRequestException("ELAK Integration service has issues...Please contact Support for assistance");

            } catch (RestClientException ex) {
                throw new BadRequestException("ELAK Integration service has issues..."+ex.getMessage());
            }
        return premiumResultBean;
    }

    @Transactional
    public PremiumResultBean computeAlakUPPremium(PolicyTrans policyTrans, List<PremiumItemsBean> itemsBeans)
            throws BadRequestException {
        CalculateUltimateProtectorPremiumRequest request = new CalculateUltimateProtectorPremiumRequest();
        PolicyMiscInfo policyMiscInfo = policyMiscInfoRepo.findByPolicyId(policyTrans.getPolicyId());

        boolean ahb = false;
        PremiumResultBean premiumResultBean = new PremiumResultBean();
        BigDecimal sumAssured = itemsBeans.stream().filter(PremiumItemsBean::isMainSection)
                .map(PremiumItemsBean::getSumAssured).findAny().isPresent()
                        ? itemsBeans.stream().filter(PremiumItemsBean::isMainSection)
                                .map(PremiumItemsBean::getSumAssured).findAny().get()
                        : BigDecimal.ZERO;
        BigDecimal min = new BigDecimal("500000");
        BigDecimal max = new BigDecimal("100000000");
        if (sumAssured.compareTo(min) < 0 || sumAssured.compareTo(max) > 0) {
            throw new BadRequestException("Sum Assured at entry should be between KES 500,000 and KES 10,000,000");
        }

        int frequency = 0;
        String polFrequency = policyTrans.getFrequency();
        if (polFrequency.equalsIgnoreCase("M")) {
            frequency = 1;
        } else if (polFrequency.equalsIgnoreCase("Q")) {
            frequency = 3;
        } else if (polFrequency.equalsIgnoreCase("S")) {
            frequency = 6;
        } else if (polFrequency.equalsIgnoreCase("A")) {
            frequency = 12;
        } else if (polFrequency.equalsIgnoreCase("SG")) {
            frequency = 1;
        }

        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        request.setFrequency(frequency);
        request.setInceptionDate(outputFormat.format(policyTrans.getCoverFrom()));
        //request.setCoverOptionId(Integer.parseInt(policyMiscInfo.getCoverOption()));
        request.setCoverOptionId(1);
        request.setSumInsured(sumAssured.intValue());
        request.setAccidentalOnlyPlan(false);
        request.setMainMemberFullName(policyTrans.getClient().getFname() + " " + policyTrans.getClient().getOtherNames());
        request.setMainMemberBirthDate(policyTrans.getClient().getDob().toString());
        // 1 Female,2 male
        int gender;
        String clientGender = policyTrans.getClient().getGender().toUpperCase();
        if ("F".equals(clientGender)) {
            gender = 1;
        } else if ("M".equals(clientGender)) {
            gender = 2;
        } else {
            gender = 0;
        }
        request.setGender(gender);

        // 1 True, 2 False
        request.setMedicalUWQ1(parseSafeInt(policyMiscInfo.getMedicalUWQ1(), 2));
        request.setMedicalUWQ2(parseSafeInt(policyMiscInfo.getMedicalUWQ2(), 2));
        request.setMedicalUWQ3(parseSafeInt(policyMiscInfo.getMedicalUWQ3(), 2));
        String spouseName = "";

        List<PolicyDependentsInfo> policyTotalDependents = policyDependentsRepo.findByPolicyId(policyTrans.getPolicyId());
        int totalCount = policyTotalDependents.size();

//        if(Integer.parseInt(policyMiscInfo.getCoverOption()) == 1){
//            //only policy holder info is accepted
//            List<PolicyDependentsInfo> policyDependentsInfo = policyDependentsRepo.findByPolicyIdAndDeptType(policyTrans.getPolicyId(),"main_member");
//            if(!policyDependentsInfo.isEmpty() || totalCount > 1){
//                throw  new BadRequestException("Cover option 1, allows only policy holder.");
//            }
//
//        } else if(Integer.parseInt(policyMiscInfo.getCoverOption()) == 2){
//            // Policyholder plus Spouse only (max of 1)
//            List<PolicyDependentsInfo> policySpouseDependentsInfo = policyDependentsRepo.findByPolicyIdAndDeptType(policyTrans.getPolicyId(),"spouse");
//            if(!policySpouseDependentsInfo.isEmpty()) {
//                if (policySpouseDependentsInfo.size() > 1 || (totalCount > policySpouseDependentsInfo.size()+1)) {
//                    throw new BadRequestException("Cover option 2, allows only policy holder and 1 (ONE) spouse only.");
//                }
//                spouseName = policySpouseDependentsInfo.get(0).getFullName();
//            }
//        } else if(Integer.parseInt(policyMiscInfo.getCoverOption()) == 3){
//            //Policyholder plus Children (max of 5)
//
//            List<PolicyDependentsInfo> policyChildDependentsInfo = policyDependentsRepo.findByPolicyIdAndDeptType(policyTrans.getPolicyId(), "child");
//
//            //check if child is empty
//            if(!policyChildDependentsInfo.isEmpty()) {
//                if (policyChildDependentsInfo.size() > 5 || (totalCount > policyChildDependentsInfo.size()+1)) {
//                    throw new BadRequestException("Cover option 3, allow only 5 (FIVE) children max only.");
//                }
//
//                for (int i = 0; i < policyChildDependentsInfo.size(); i++) {
//                    PolicyDependentsInfo childDep = policyChildDependentsInfo.get(i);
//                    CalculateUltimateProtectorPremiumRequest.Child child = new CalculateUltimateProtectorPremiumRequest.Child();
//                    child.setFullName(childDep.getFullName());
//                    child.setBirthDate(childDep.getDOB().toString());
//                    child.setStillBorn(childDep.getStillBorn());
//                    child.setStudent(childDep.getIsStudent());
//
//                    switch (i) {
//                        case 0:
//                            request.setChild1(child);
//                            break;
//                        case 1:
//                            request.setChild2(child);
//                            break;
//                        case 2:
//                            request.setChild3(child);
//                            break;
//                        case 3:
//                            request.setChild4(child);
//                            break;
//                        case 4:
//                            request.setChild5(child);
//                            break;
//                    }
//                }
//            }
//
//        } else if(Integer.parseInt(policyMiscInfo.getCoverOption()) == 4){
//            //Policyholder, Spouse (max of 1) plus Children (max of 5)
//
//            List<PolicyDependentsInfo> policySpouseDependentsInfo = policyDependentsRepo.findByPolicyIdAndDeptType(policyTrans.getPolicyId(),"spouse");
//            List<PolicyDependentsInfo> policyChildDependentsInfo = policyDependentsRepo.findByPolicyIdAndDeptType(policyTrans.getPolicyId(), "child");
//
//            if(!policySpouseDependentsInfo.isEmpty()) {
//                if (policySpouseDependentsInfo.size() > 1 || (totalCount > policySpouseDependentsInfo.size() +1)) {
//                    throw new BadRequestException("Cover option 4, allows only 1 (ONE) spouse only.");
//                }
//                spouseName = policySpouseDependentsInfo.get(0).getFullName();
//            }
//
//            //check if child is empty
//            if(!policyChildDependentsInfo.isEmpty()) {
//                if (policyChildDependentsInfo.size() > 5 || (totalCount > policyChildDependentsInfo.size()+1) ) {
//                    throw new BadRequestException("Cover option 4, allow only 5 (FIVE) children max only.");
//                }
//
//                for (int i = 0; i < policyChildDependentsInfo.size(); i++) {
//                    PolicyDependentsInfo childDep = policyChildDependentsInfo.get(i);
//                    CalculateUltimateProtectorPremiumRequest.Child child = new CalculateUltimateProtectorPremiumRequest.Child();
//                    child.setFullName(childDep.getFullName());
//                    child.setBirthDate(childDep.getDOB().toString());
//                    child.setStillBorn(childDep.getStillBorn());
//                    child.setStudent(childDep.getIsStudent());
//
//                    switch (i) {
//                        case 0:
//                            request.setChild1(child);
//                            break;
//                        case 1:
//                            request.setChild2(child);
//                            break;
//                        case 2:
//                            request.setChild3(child);
//                            break;
//                        case 3:
//                            request.setChild4(child);
//                            break;
//                        case 4:
//                            request.setChild5(child);
//                            break;
//                    }
//                }
//            }
//
//        }
//        request.setSpouseFullName(spouseName);
        request.setSpouseFullName("");

        premiumResultBean.setSumInsured(sumAssured.doubleValue());
        SectionTransDTO section = new SectionTransDTO();
        for (PremiumItemsBean premiumItemsBean : itemsBeans) {
            if (premiumItemsBean.getPremiumId().equalsIgnoreCase("Main Benefit")) {
                List<Object[]> sectionArr = sectionRepo.findSectionTransById(premiumItemsBean.getSectId());
                if (!sectionArr.isEmpty()) {
                    final BigDecimal rate = (BigDecimal) sectionArr.get(0)[7];
                    final BigDecimal freeLimit = (BigDecimal) sectionArr.get(0)[4];
                    final BigDecimal divFactor = (BigDecimal) sectionArr.get(0)[3];
                    final Long sectId = ((BigInteger) sectionArr.get(0)[0]).longValue();
                    final Long sectSectionId = ((BigInteger) sectionArr.get(0)[8]).longValue();
                    final Long premRateId = ((BigInteger) sectionArr.get(0)[9]).longValue();
                    section.setPremRatesId(premRateId);
                    section.setRate(rate);
                    section.setFreeLimit(freeLimit);
                    section.setDivFactor(divFactor);
                    section.setSectId(sectId);
                    section.setSectionSectId(sectSectionId);
                }
            }

            if (premiumItemsBean.getPremiumId().equalsIgnoreCase("Accidental Hospitalization Benefit")) {
                List<Object[]> sectionArr = sectionRepo.findSectionTransById(premiumItemsBean.getSectId());
                if (!sectionArr.isEmpty()) {
                    final BigDecimal rate = (BigDecimal) sectionArr.get(0)[7];
                    final BigDecimal freeLimit = (BigDecimal) sectionArr.get(0)[4];
                    final BigDecimal divFactor = (BigDecimal) sectionArr.get(0)[3];
                    final Long sectId = ((BigInteger) sectionArr.get(0)[0]).longValue();
                    final Long sectSectionId = ((BigInteger) sectionArr.get(0)[8]).longValue();
                    final Long premRateId = ((BigInteger) sectionArr.get(0)[9]).longValue();
                    section.setPremRatesId(premRateId);
                    section.setRate(rate);
                    section.setFreeLimit(freeLimit);
                    section.setDivFactor(divFactor);
                    section.setSectId(sectId);
                    section.setSectionSectId(sectSectionId);
                }
                ahb = true;
            }
        }
            request.setAccidentalHospitalisationBenefit(ahb);

            System.out.println("ALAK UP PAYLOAD: " + new Gson().toJson(request));
            try {
                String paramValue = paramService.getParameterString("API_INTEGRATION_URL")
                        + "/alak/calculateUltimateProtectorPremium";
                HttpHeaders headers = new HttpHeaders();
                headers.set("Content-Type", "application/json");
                HttpEntity<?> requestEntity = new HttpEntity<>(request, headers);
                ResponseEntity<String> responseEntity = restTemplate.postForEntity(paramValue, requestEntity,
                        String.class);
                log.info("Getting a response for alak UP {}",responseEntity.getBody());
                if (responseEntity.getStatusCode() != HttpStatus.OK) {
                    // bad request handling
                    JsonNode error = objectMapper.readTree(responseEntity.getBody());
                    String message = Optional.ofNullable(error.get("Message")).map(JsonNode::asText)
                            .orElse("Confirm your input parameters");
                    throw new BadRequestException(message);
                }

                JsonNode rootNode = objectMapper.readTree(responseEntity.getBody());

                BigDecimal premium = BigDecimal.ZERO;
                String currency = rootNode.get("Currency").asText();

                double totalAnnualPremium = rootNode.get("TotalAnnualPremium").asDouble(); // basic prem = instllament
                double totalMonthlyPremium = rootNode.get("TotalMonthlyPremium").asDouble(); // prem

                premiumResultBean.setQuarterlypremium(totalMonthlyPremium*4);
                premiumResultBean.setSemiAnnualpremium(totalMonthlyPremium*6);
                premiumResultBean.setSinglepremium(totalMonthlyPremium);
                premiumResultBean.setAnnualpremium(totalAnnualPremium);

                premiumResultBean.setPremium(totalAnnualPremium);

                // Extracting main member information
                JsonNode mainMember = rootNode.get("MainMember");
                if(mainMember != null && !mainMember.isNull()) {
                    String mainMemberName = mainMember.get("Name").asText();
                    double mainMemberSumInsured = mainMember.get("SumInsured").asDouble();
                    //premiumResultBean.setSumInsured(mainMemberSumInsured);
                    double mainMemberAnnualPremium = mainMember.get("AnnualPremium").asDouble();
                    double mainMemberMonthlyPremium = mainMember.get("MonthlyPremium").asDouble();
                    double accidentalHospitalisationPremium = mainMember.get("AccidentalHospitalisationPremium").asDouble();
//                    if (premiumItemsBean.getPremiumId().equalsIgnoreCase("Accidental Hospitalization Benefit")) {
//                        section.setPrem(BigDecimal.valueOf(accidentalHospitalisationPremium));
//                    }
//
//                    section.setCalcprem(section.getPrem());
                    // if(ahb) {
                    //          sectionRepo.updateSectionSumAssuredDetails(
                    //         (section.getPrem() != null) ? section.getPrem() : BigDecimal.ZERO,
                    //         (section.getCalcprem() != null) ? section.getCalcprem() : BigDecimal.ZERO,
                    //         (section.getAmount() != null) ? section.getAmount() : BigDecimal.ZERO,
                    //         section.getSectId());
                    //  }
                }
                //spouse
                JsonNode mainMemberSpouse = rootNode.get("Spouse");
                if(mainMemberSpouse != null && !mainMemberSpouse.isNull()) {
                    String mainMemberSpouseName = mainMemberSpouse.get("Name").asText();
                    double mainMemberSpouseSumInsured = mainMemberSpouse.get("SumInsured").asDouble();

                    updateAlakDependeciesPrem(mainMemberSpouseName, policyTrans.getPolicyId(), "spouse", null, null, mainMemberSpouseSumInsured, null);
                }
                List<String> childrenKeys = Arrays.asList("Child1", "Child2", "Child3", "Child4", "Child5");
                for (String childKey : childrenKeys) {
                    JsonNode childNode = rootNode.get(childKey);
                    if (childNode != null && !childNode.isNull()) {
                        String childName = childNode.get("Name").asText();
                        double childSumInsured = childNode.get("SumInsured").asDouble();

                        // Optional: log or store per-dependent premiums
                        log.info("child {} premium: {} name {}", childKey,childSumInsured, childName);
                        updateAlakDependeciesPrem(childName, policyTrans.getPolicyId(),"child",null,null,childSumInsured,null);
                    }
                }
            } catch (JsonProcessingException e) {
                throw new BadRequestException("Error computing premium: " + e.getMessage());
            }
            catch (HttpServerErrorException ex) {
                throw new BadRequestException("ELAK Integration service has issues...Please contact Support for assistance");

            } catch (RestClientException ex) {
                throw new BadRequestException("ELAK Integration service has issues..."+ex.getMessage());
            }
        return premiumResultBean;
    }

    /**
     * Computes the premium for an ALAK education policy using the premium value.
     *
     * @param policyTrans The policy transaction details.
     * @param itemsBeans  The list of premium items.
     * @return A PremiumResultBean containing the computed premium and related details.
     * @throws BadRequestException If the premium value is invalid or API call fails.
     */
    @Transactional
    public PremiumResultBean computeAlakEducationPremiumUsingPremiumValue(PolicyTrans policyTrans, List<PremiumItemsBean> itemsBeans)
            throws BadRequestException {
        GenerateQuoteForEducationPolicyRequest request = new GenerateQuoteForEducationPolicyRequest();
        PremiumResultBean premiumResultBean = new PremiumResultBean();
        BigDecimal premiumAtEntry = itemsBeans.stream().filter(PremiumItemsBean::isMainSection)
                .map(PremiumItemsBean::getPremium)
                .findAny().orElse(BigDecimal.ZERO);

        if (premiumAtEntry.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Premium at entry cannot be zero for this calculator...");
        }
        premiumResultBean.setPremium(premiumAtEntry.doubleValue());

        int frequency = 0;
        String polFrequency = policyTrans.getFrequency();
        if (polFrequency.equalsIgnoreCase("M")) {
            frequency = 1;
        } else if (polFrequency.equalsIgnoreCase("Q")) {
            frequency = 3;
        } else if (polFrequency.equalsIgnoreCase("S")) {
            frequency = 6;
        } else if (polFrequency.equalsIgnoreCase("A")) {
            frequency = 12;
        } else if (polFrequency.equalsIgnoreCase("SG")) {
            frequency = 1;
        }

        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        request.setFrequency(frequency);
//        request.setInceptionDate(LocalDateTime.parse(
//                outputFormat.format(policyTrans.getCoverFrom()),
//                DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        request.setInceptionDate(outputFormat.format(policyTrans.getCoverFrom()));
        request.setPremiumAtEntry(premiumAtEntry.doubleValue());
        request.setInflationPercentage(0);
        request.setPremiumPaymentTerms(policyTrans.getPolTerm());
        request.setMaturityPeriod(4); // 4/8

        request.setMedicalUWQ1(false);
        request.setMedicalUWQ2(false);
        request.setMedicalUWQ3(0);
        request.setMedicalUWQ4(false);
        request.setMedicalUWQ5(null);
        request.setMainMemberFullName(policyTrans.getClient().getFname() + " " + policyTrans.getClient().getOtherNames());
//        request.setMainMemberBirthDate(LocalDateTime.parse(
//                outputFormat.format(policyTrans.getClient().getDob()),
//                DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        request.setMainMemberBirthDate(outputFormat.format(policyTrans.getClient().getDob()));
        boolean staffDiscount = policyTrans.getClient().getTenantType().getTypeDesc().equalsIgnoreCase("STAFF");
        request.setStaffDiscount(staffDiscount);

        boolean ahb = false;
        SectionTransDTO mainSection = null;
        SectionTransDTO ahbSection = null;

        for (PremiumItemsBean premiumItemsBean : itemsBeans) {
            log.info("prem item {}", premiumItemsBean);
            if (premiumItemsBean.getPremiumId().equalsIgnoreCase("MAIN BENEFIT")) {
                List<Object[]> sectionArr = sectionRepo.findSectionTransById(premiumItemsBean.getSectId());
                if (!sectionArr.isEmpty()) {
                    mainSection = new SectionTransDTO();
                    final BigDecimal rate = (BigDecimal) sectionArr.get(0)[7];
                    final BigDecimal freeLimit = (BigDecimal) sectionArr.get(0)[4];
                    final BigDecimal divFactor = (BigDecimal) sectionArr.get(0)[3];
                    final Long sectId = ((BigInteger) sectionArr.get(0)[0]).longValue();
                    final Long sectSectionId = ((BigInteger) sectionArr.get(0)[8]).longValue();
                    final Long premRateId = ((BigInteger) sectionArr.get(0)[9]).longValue();
                    mainSection.setPremRatesId(premRateId);
                    mainSection.setRate(rate);
                    mainSection.setFreeLimit(freeLimit);
                    mainSection.setDivFactor(divFactor);
                    mainSection.setSectId(sectId);
                    mainSection.setSectionSectId(sectSectionId);
                }
            } else if (premiumItemsBean.getPremiumId().equalsIgnoreCase("Accidental Hospitalization Benefit")) {
                List<Object[]> sectionArr = sectionRepo.findSectionTransById(premiumItemsBean.getSectId());
                if (!sectionArr.isEmpty()) {
                    ahbSection = new SectionTransDTO();
                    final BigDecimal rate = (BigDecimal) sectionArr.get(0)[7];
                    final BigDecimal freeLimit = (BigDecimal) sectionArr.get(0)[4];
                    final BigDecimal divFactor = (BigDecimal) sectionArr.get(0)[3];
                    final Long sectId = ((BigInteger) sectionArr.get(0)[0]).longValue();
                    final Long sectSectionId = ((BigInteger) sectionArr.get(0)[8]).longValue();
                    final Long premRateId = ((BigInteger) sectionArr.get(0)[9]).longValue();
                    ahbSection.setPremRatesId(premRateId);
                    ahbSection.setRate(rate);
                    ahbSection.setFreeLimit(freeLimit);
                    ahbSection.setDivFactor(divFactor);
                    ahbSection.setSectId(sectId);
                    ahbSection.setSectionSectId(sectSectionId);
                }
                ahb = true;
            }
        }

        // request.setAccidentalHospitalisationBenefit(ahb);

        System.out.println("ALAK education PAYLOAD: " + new Gson().toJson(request));
        try {
            String paramValue = paramService.getParameterString("API_INTEGRATION_URL") + "/kit/api/v1/education/quote/calculate-sum-assured-from-premium";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<GenerateQuoteForEducationPolicyRequest> requestEntity = new HttpEntity<>(request, headers);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(paramValue, requestEntity, String.class);
            String responseBody = responseEntity.getBody();
            JsonNode rootNode = objectMapper.readTree(responseBody);

            if (!rootNode.isArray() || rootNode.size() == 0) {
                throw new BadRequestException("Invalid or missing API response.");
            }

            JsonNode firstItem = rootNode.get(0);
            JsonNode projectionDetails = firstItem.get("Projections");

            System.out.println("full response: " + responseBody);
            System.out.println("firstItem: " + firstItem.toString());
            System.out.println("projection details: " + projectionDetails.toString());

            List<PolicyBenefitsDistribution> benefitsDistributions = new ArrayList<>();
            List<PolicySurrenderValues> surrenderValuesList = new ArrayList<>();

            BigDecimal premiumAmount = BigDecimal.ZERO;
            boolean premiumSet = false;

            BigDecimal sumAssuredAmount = BigDecimal.ZERO;
            boolean sumAssuredSet = false;

            for (JsonNode projectionDetail : projectionDetails) {
                log.info("Projection details is " + projectionDetail);
                // Set the premium
                if (!premiumSet) {
                    BigDecimal currentPremium = projectionDetail.get("Premium").decimalValue();
                    if (currentPremium.compareTo(BigDecimal.ZERO) > 0) {
                        premiumAmount = currentPremium;
                        premiumSet = true;
                        log.info("prem set to {} ", premiumAmount);
                    }
                }

                if (!sumAssuredSet) {
                    BigDecimal currentSumAssured = projectionDetail.get("SumAssured").decimalValue();
                    if (currentSumAssured.compareTo(BigDecimal.ZERO) > 0) {
                        sumAssuredAmount = currentSumAssured;
                        sumAssuredSet = true;
                        log.info("Sum assured set to {} ", currentSumAssured);
                    }
                }

                PolicySurrenderValues surrenderValues = new PolicySurrenderValues();
                surrenderValues.setPolicyId(policyTrans);
                surrenderValues.setSurrenderYear(String.format("Year %d", projectionDetail.get("Year").asInt()));
                surrenderValues.setSurrenderValue(projectionDetail.get("SurrenderValue").decimalValue());
                surrenderValuesList.add(surrenderValues);

                PolicyBenefitsDistribution benefitsDistribution = new PolicyBenefitsDistribution();
                benefitsDistribution.setPolicyId(policyTrans);
                double year = projectionDetail.get("Year").asDouble();
                benefitsDistribution.setMaturityYear(String.format("Year %.0f", Math.floor(year)));
                benefitsDistribution.setEstBenefit(projectionDetail.get("PaidupAnnualMaturityBenefit").asDouble());

                String projectionDateStr = projectionDetail.get("ProjectionDate").asText();
                LocalDateTime projectionDateTime = LocalDateTime.parse(projectionDateStr, DateTimeFormatter.ISO_DATE_TIME);
                benefitsDistribution.setMaturityExpDate(Date.from(projectionDateTime.atZone(ZoneId.systemDefault()).toInstant()));
                benefitsDistributions.add(benefitsDistribution);
            }

            //SET THE SUM ASSURED THAT WAS CALCULATED FROM THE PREMIUM
            premiumResultBean.setSumInsured(sumAssuredAmount.doubleValue());

            if (premiumAmount.compareTo(BigDecimal.ZERO) > 0) {
                premiumResultBean.setPremium(premiumAmount.doubleValue());

                String frequency1 = policyTrans.getFrequency();
                if (frequency1.equalsIgnoreCase("Q")) {
                    premiumResultBean.setQuarterlypremium(premiumAmount.doubleValue());
                } else if (frequency1.equalsIgnoreCase("S")) {
                    premiumResultBean.setSemiAnnualpremium(premiumAmount.doubleValue());
                } else if (frequency1.equalsIgnoreCase("A")) {
                    premiumResultBean.setAnnualpremium(premiumAmount.doubleValue());
                } else if (frequency1.equalsIgnoreCase("M")) {
                    premiumResultBean.setSinglepremium(premiumAmount.doubleValue());
                }
            }

            if (mainSection != null) {
                mainSection.setPrem(premiumAmount);
                mainSection.setCalcprem(premiumAmount);

                sectionRepo.updateSectionSumAssuredDetails(
                        (mainSection.getPrem() != null) ? mainSection.getPrem() : BigDecimal.ZERO,
                        (mainSection.getCalcprem() != null) ? mainSection.getCalcprem() : BigDecimal.ZERO,
                        (mainSection.getAmount() != null) ? mainSection.getAmount() : BigDecimal.ZERO,
                        mainSection.getSectId()
                );
            }
            if (ahbSection != null && ahb) {
                BigDecimal ahbPremium = BigDecimal.ZERO;

                if (projectionDetails.size() > 0) {
                    JsonNode firstProjection = projectionDetails.get(0);
                    if (firstProjection.has("AccidentalBenefit")) {
                        ahbPremium = firstProjection.get("AccidentalBenefit").decimalValue();
                    }
                }

                ahbSection.setPrem(ahbPremium);
                ahbSection.setCalcprem(ahbPremium);

                sectionRepo.updateSectionSumAssuredDetails(
                        (ahbSection.getPrem() != null) ? ahbSection.getPrem() : BigDecimal.ZERO,
                        (ahbSection.getCalcprem() != null) ? ahbSection.getCalcprem() : BigDecimal.ZERO,
                        (ahbSection.getAmount() != null) ? ahbSection.getAmount() : BigDecimal.ZERO,
                        ahbSection.getSectId()
                );
            }
            if (!benefitsDistributions.isEmpty()) {
                policyBenefitsDistributionRepo.save(benefitsDistributions);
            }
            if (!surrenderValuesList.isEmpty()) {
                policySurrenderValuesRepo.save(surrenderValuesList);
            }
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Error computing premium: " + e.getMessage());
        } catch (HttpServerErrorException ex) {
            throw new BadRequestException("ALAK Integration service has issues...Please contact Support for assistance");
        } catch (RestClientException ex) {
            throw new BadRequestException("ALAK Integration service has issues..." + ex.getMessage());
        }

        System.out.println("result to saved " + premiumResultBean.toString());
        log.info("{}", premiumResultBean);
        return premiumResultBean;
    }


    @Transactional
    public PremiumResultBean computeAlakEdPremium(PolicyTrans policyTrans, List<PremiumItemsBean> itemsBeans)
            throws BadRequestException {
        CalculateEducationPremiumRequest request = new CalculateEducationPremiumRequest();

        PremiumResultBean premiumResultBean = new PremiumResultBean();
        BigDecimal sumAssured = itemsBeans.stream().filter(PremiumItemsBean::isMainSection)
                .map(PremiumItemsBean::getSumAssured).findAny().isPresent()
                ? itemsBeans.stream().filter(PremiumItemsBean::isMainSection)
                .map(PremiumItemsBean::getSumAssured).findAny().get()
                : BigDecimal.ZERO;

        if (sumAssured.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Sum Assured cannot be zero for this calculator...");
        }
        premiumResultBean.setSumInsured(sumAssured.doubleValue());

//        BigDecimal min = new BigDecimal("1000000");
//        BigDecimal max = new BigDecimal("100000000");
//        if (sumAssured.compareTo(min) < 0 || sumAssured.compareTo(max) > 0) {
//            throw new BadRequestException("Sum Assured at entry should be between KES 1,000,000 and KES 100,000,000");
//        }

        int frequency = 0;
        String polFrequency = policyTrans.getFrequency();
        if (polFrequency.equalsIgnoreCase("M")) {
            frequency = 1;
        } else if (polFrequency.equalsIgnoreCase("Q")) {
            frequency = 3;
        } else if (polFrequency.equalsIgnoreCase("S")) {
            frequency = 6;
        } else if (polFrequency.equalsIgnoreCase("A")) {
            frequency = 12;
        } else if (polFrequency.equalsIgnoreCase("SG")) {
            frequency = 1;
        }

        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        request.setFrequency(frequency);
        request.setInceptionDate(outputFormat.format(policyTrans.getCoverFrom()));
        request.setSumAssuredAtEntry(sumAssured.intValue());
        request.setInflationPercentage(0);
        request.setPremiumPaymentTerms(policyTrans.getPolTerm());
        request.setMaturityPeriod(4); // 4/8

        request.setMedicalUWQ1(false);
        request.setMedicalUWQ2(false);
        request.setMedicalUWQ3(false);
        request.setMedicalUWQ4(false);
        request.setMedicalUWQ5(false);

        request.setMainMemberFullName(policyTrans.getClient().getFname() + " " + policyTrans.getClient().getOtherNames());
        request.setMainMemberBirthDate(outputFormat.format(policyTrans.getClient().getDob()));
        String gender = policyTrans.getClient().getGender();
        int mainMemberGender = gender.equalsIgnoreCase("F") ? 1 : 2;
        request.setMainMemberGender(mainMemberGender);
        boolean staffDiscount = policyTrans.getClient().getTenantType().getTypeDesc().equalsIgnoreCase("STAFF");
        request.setStaffDiscount(staffDiscount);

        boolean ahb = false;
        SectionTransDTO mainSection = null;
        SectionTransDTO ahbSection = null;

        for (PremiumItemsBean premiumItemsBean : itemsBeans) {
            log.info("prem item {}", premiumItemsBean);
            if (premiumItemsBean.getPremiumId().equalsIgnoreCase("MAIN BENEFIT")) {
                List<Object[]> sectionArr = sectionRepo.findSectionTransById(premiumItemsBean.getSectId());
                if (!sectionArr.isEmpty()) {
                    mainSection = new SectionTransDTO();
                    final BigDecimal rate = (BigDecimal) sectionArr.get(0)[7];
                    final BigDecimal freeLimit = (BigDecimal) sectionArr.get(0)[4];
                    final BigDecimal divFactor = (BigDecimal) sectionArr.get(0)[3];
                    final Long sectId = ((BigInteger) sectionArr.get(0)[0]).longValue();
                    final Long sectSectionId = ((BigInteger) sectionArr.get(0)[8]).longValue();
                    final Long premRateId = ((BigInteger) sectionArr.get(0)[9]).longValue();
                    mainSection.setPremRatesId(premRateId);
                    mainSection.setRate(rate);
                    mainSection.setFreeLimit(freeLimit);
                    mainSection.setDivFactor(divFactor);
                    mainSection.setSectId(sectId);
                    mainSection.setSectionSectId(sectSectionId);
                }
            } else if (premiumItemsBean.getPremiumId().equalsIgnoreCase("Accidental Hospitalization Benefit")) {
                List<Object[]> sectionArr = sectionRepo.findSectionTransById(premiumItemsBean.getSectId());
                if (!sectionArr.isEmpty()) {
                    ahbSection = new SectionTransDTO();
                    final BigDecimal rate = (BigDecimal) sectionArr.get(0)[7];
                    final BigDecimal freeLimit = (BigDecimal) sectionArr.get(0)[4];
                    final BigDecimal divFactor = (BigDecimal) sectionArr.get(0)[3];
                    final Long sectId = ((BigInteger) sectionArr.get(0)[0]).longValue();
                    final Long sectSectionId = ((BigInteger) sectionArr.get(0)[8]).longValue();
                    final Long premRateId = ((BigInteger) sectionArr.get(0)[9]).longValue();
                    ahbSection.setPremRatesId(premRateId);
                    ahbSection.setRate(rate);
                    ahbSection.setFreeLimit(freeLimit);
                    ahbSection.setDivFactor(divFactor);
                    ahbSection.setSectId(sectId);
                    ahbSection.setSectionSectId(sectSectionId);
                }
                ahb = true;
            }
        }

            request.setAccidentalHospitalisationBenefit(ahb);

            System.out.println("ALAK education PAYLOAD: " + new Gson().toJson(request));
            try {
                String paramValue = paramService.getParameterString("API_INTEGRATION_URL") + "/alak/calculateEducationPremium";
                HttpHeaders headers = new HttpHeaders();
                headers.set("Content-Type", "application/json");
                HttpEntity<?> requestEntity = new HttpEntity<>(request, headers);
                ResponseEntity<String> responseEntity = restTemplate.postForEntity(paramValue, requestEntity,
                        String.class);
                String responseBody = responseEntity.getBody();
                JsonNode rootNode = objectMapper.readTree(responseBody);

                if (!rootNode.isArray() || rootNode.size() == 0 ) {
                    throw new BadRequestException("Invalid or missing API response.");
                }

                JsonNode firstItem = rootNode.get(0);
                JsonNode projectionDetails = firstItem.get("Projections");

                System.out.println("full response"+responseBody);
                System.out.println("firstItem"+firstItem.toString());
                System.out.println("projection detAILS "+ projectionDetails.toString());

                List<PolicyBenefitsDistribution> benefitsDistributions = new ArrayList<>();
                List<PolicySurrenderValues> surrenderValuesList = new ArrayList<>();

                BigDecimal premiumAmount = BigDecimal.ZERO;
                boolean premiumSet = false;

                for (JsonNode projectionDetail : projectionDetails) {
                    log.info("Projection details is " + projectionDetail);
                    //set the premium
                    if (!premiumSet) {
                        BigDecimal currentPremium = projectionDetail.get("Premium").decimalValue();
                        if (currentPremium.compareTo(BigDecimal.ZERO) > 0) {
                            premiumAmount = currentPremium;
                            premiumSet = true;
                            log.info("prem set to {} ", premiumAmount);
                        }
                    }

                    PolicySurrenderValues surrenderValues = new PolicySurrenderValues();
                    surrenderValues.setPolicyId(policyTrans);
                    surrenderValues.setSurrenderYear(String.format("Year %d", projectionDetail.get("Year").asInt()));
                    surrenderValues.setSurrenderValue(projectionDetail.get("SurrenderValue").decimalValue());
                    surrenderValuesList.add(surrenderValues);

                    PolicyBenefitsDistribution benefitsDistribution = new PolicyBenefitsDistribution();
                    benefitsDistribution.setPolicyId(policyTrans);
                    double year = projectionDetail.get("Year").asDouble();
                    benefitsDistribution.setMaturityYear(String.format("Year %.0f", Math.floor(year)));
                    benefitsDistribution.setEstBenefit(projectionDetail.get("PaidupAnnualMaturityBenefit").asDouble());

                    String projectionDateStr = projectionDetail.get("ProjectionDate").asText();
                    LocalDateTime projectionDateTime = LocalDateTime.parse(projectionDateStr, DateTimeFormatter.ISO_DATE_TIME);
                    benefitsDistribution.setMaturityExpDate(Date.from(projectionDateTime.atZone(ZoneId.systemDefault()).toInstant()));
                    benefitsDistributions.add(benefitsDistribution);
                }

                if(premiumAmount.compareTo(BigDecimal.ZERO) > 0) {
                    premiumResultBean.setPremium(premiumAmount.doubleValue());

                    String frequency1 = policyTrans.getFrequency();
                    if (frequency1.equalsIgnoreCase("Q")) {
                        premiumResultBean.setQuarterlypremium(premiumAmount.doubleValue());
                    } else if (frequency1.equalsIgnoreCase("S")) {
                        premiumResultBean.setSemiAnnualpremium(premiumAmount.doubleValue());
                    } else if (frequency1.equalsIgnoreCase("A")) {
                        premiumResultBean.setAnnualpremium(premiumAmount.doubleValue());
                    } else if (frequency1.equalsIgnoreCase("M")) {
                        premiumResultBean.setSinglepremium(premiumAmount.doubleValue());
                    }
                }

                if(mainSection != null){
                    mainSection.setPrem(premiumAmount);
                    mainSection.setCalcprem(premiumAmount);

                    sectionRepo.updateSectionSumAssuredDetails(
                            (mainSection.getPrem() != null) ? mainSection.getPrem() : BigDecimal.ZERO,
                            (mainSection.getCalcprem() != null) ? mainSection.getCalcprem() : BigDecimal.ZERO,
                            (mainSection.getAmount() != null) ? mainSection.getAmount() : BigDecimal.ZERO,
                            mainSection.getSectId()
                    );
                }
                if (ahbSection != null && ahb) {
                    BigDecimal ahbPremium = BigDecimal.ZERO;

                    if (projectionDetails.size() > 0) {
                        JsonNode firstProjection = projectionDetails.get(0);
                        if (firstProjection.has("AccidentalBenefit")) {
                            ahbPremium = firstProjection.get("AccidentalBenefit").decimalValue();
                        }
                    }

                    ahbSection.setPrem(ahbPremium);
                    ahbSection.setCalcprem(ahbPremium);

                    sectionRepo.updateSectionSumAssuredDetails(
                            (ahbSection.getPrem() != null) ? ahbSection.getPrem() : BigDecimal.ZERO,
                            (ahbSection.getCalcprem() != null) ? ahbSection.getCalcprem() : BigDecimal.ZERO,
                            (ahbSection.getAmount() != null) ? ahbSection.getAmount() : BigDecimal.ZERO,
                            ahbSection.getSectId()
                    );
                }
                if(!benefitsDistributions.isEmpty()) {
                    policyBenefitsDistributionRepo.save(benefitsDistributions);
                }
                if(!surrenderValuesList.isEmpty()) {
                    policySurrenderValuesRepo.save(surrenderValuesList);
                }
            } catch (JsonProcessingException e) {
                throw new BadRequestException("Error computing premium: " + e.getMessage());
            }
            catch (HttpServerErrorException ex) {
                throw new BadRequestException("ELAK Integration service has issues...Please contact Support for assistance");

            } catch (RestClientException ex) {
                throw new BadRequestException("ELAK Integration service has issues..."+ex.getMessage());
            }

        System.out.println("result to saved "+premiumResultBean.toString());
        log.info("{}", premiumResultBean);
        return premiumResultBean;
    }

    private void updateAlakDependeciesPrem(String name, Long polId, String depType, Double monthlyPremium, Double annualPremium, double sumInsured, Double incomeBenefit) {
        PolicyDependentsInfo updatePrem = policyDependentsRepo.findByPolicyIdNameDepType(polId, name, depType);
        if (updatePrem != null) {
            updatePrem.setSumInsured(BigDecimal.valueOf(sumInsured));

            if (monthlyPremium != null) {
                updatePrem.setMonthlyPremium(BigDecimal.valueOf(monthlyPremium));
            }

            if (annualPremium != null) {
                updatePrem.setAnnualPremium(BigDecimal.valueOf(annualPremium));
            }

            if (incomeBenefit != null) {
                updatePrem.setIncomeBenefitPremium(BigDecimal.valueOf(incomeBenefit));
            }

            policyDependentsRepo.save(updatePrem);
        } else {
            log.warn("No dependent found for policyId {}, name {}, type {}", polId, name, depType);
        }
    }

    private int parseSafeInt(String value, int defaultValue) {
        try {
            return (value != null) ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    private void  validateAlakClientDetails (ClientDef clientDef){
        if (clientDef.getPinNo() == null) {
            throw new RuntimeException("Client's pin no. is missing.");
        }
        if (clientDef.getTown() == null || clientDef.getTown().getCtName() == null) {
            throw new RuntimeException("Client's town is required.");
        }
        if (clientDef.getDob() == null) {
            throw new RuntimeException("Client's DOB is required.");
        }
        if (clientDef.getOtherNames() == null) {
            throw new RuntimeException("Client's Surname is required.");
        }
        if (clientDef.getClientTitle() == null || clientDef.getClientTitle().getTitleName() == null) {
            throw new RuntimeException("Client's title is required.");
        }
        if (clientDef.getFname() == null) {
            throw new RuntimeException("Client's First name is required.");
        }
        if (clientDef.getPhonePrefix() == null || clientDef.getPhonePrefix().getPrefixName() == null || clientDef.getPhoneNo() == null) {
            throw new RuntimeException("Client's Tel/cell is missing.");
        }
        if (clientDef.getIdNo() == null) {
            throw new RuntimeException("Client's Identification Number is not defined.");
        }
        if (clientDef.getGender() == null) {
            throw new RuntimeException("Client's  gender is not defined.");
        }
        if (clientDef.getPostalCodesDef() == null || clientDef.getPostalCodesDef().getZipCode() == null || clientDef.getTown() == null || clientDef.getTown().getCtName() == null) {
            throw new RuntimeException("Client's postal/zip code is missing.");
        }
    }
}
