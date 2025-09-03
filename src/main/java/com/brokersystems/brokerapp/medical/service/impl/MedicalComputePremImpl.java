package com.brokersystems.brokerapp.medical.service.impl;

import com.brokersystems.brokerapp.enums.RevenueItems;
import com.brokersystems.brokerapp.medical.model.*;
import com.brokersystems.brokerapp.medical.repository.*;
import com.brokersystems.brokerapp.medical.service.MedicalComputePrem;
import com.brokersystems.brokerapp.medical.service.MedicalTransService;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.DateUtilities;
import com.brokersystems.brokerapp.server.utils.MedicalExcelUtils;
import com.brokersystems.brokerapp.setup.model.AccountTypes;
import com.brokersystems.brokerapp.setup.model.Currencies;
import com.brokersystems.brokerapp.setup.model.PremRatesTable;
import com.brokersystems.brokerapp.setup.model.QPremRatesTable;
import com.brokersystems.brokerapp.setup.repository.PremRatesTableRepo;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.brokersystems.brokerapp.uw.model.PolicyTaxes;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.model.QPolicyTaxes;
import com.brokersystems.brokerapp.uw.repository.PolTaxesRepo;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateUtils;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by peter on 4/27/2017.
 */
@Service
public class MedicalComputePremImpl implements MedicalComputePrem {

    @Autowired
    private PolicyTransRepo policyTransRepo;

    @Autowired
    private PremRatesTableRepo ratesTblRepo;

    @Autowired
    private MedicalCategoryRepo medicalCategoryRepo;

    @Autowired
    private CategoryMembersRepo membersRepo;

    @Autowired
    private CategoryBenefitRepo benefitRepo;

    @Autowired
    private MedicalExcelUtils excelUtils;

    @Autowired
    private DateUtilities dateUtils;

    @Autowired
    private PolTaxesRepo polTaxesRepo;

    @Autowired
    private PolicyTransService policyService;

    @Autowired
    private MedicalTransService transService;

    @Autowired
    private CatLoadingRepo loadingRepo;

    @Autowired
    private SelfFundParamsRepo selfFundParamsRepo;

    @Autowired
    private ParamService paramService;

    @Autowired
    private CategoryMemberBenefitsRepo memberBenefitsRepo;


    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class,IOException.class })
    public void computePrem(Long polCode) throws BadRequestException,FileNotFoundException,IOException {
        if(polCode==null) throw new BadRequestException("No policy Details to compute premium");
        PolicyTrans policyTrans = policyTransRepo.findOne(polCode);


        if("CR".equalsIgnoreCase(policyTrans.getTransType())){
            return;
        }
        // transService.populateCategoryClauses(policyTrans);

        // transService.populateCategoryExclusions(policyTrans);
        // transService.populateCategoryLoadings(policyTrans);
        //  transService.populateCategoryProviders(policyTrans);
        boolean fundPolicy = policyTrans.getBinder().getFundBinder()!=null && "Y".equalsIgnoreCase(policyTrans.getBinder().getFundBinder());
        if(!fundPolicy)
            transService.populateCategoryTaxes(policyTrans);
        String tableRateType = "";
        if(policyTrans.getMedicalCoverType()!=null){
            tableRateType = policyTrans.getMedicalCoverType();
        }
        int remainingPeriod = dateUtils.daysBetweenUsingJoda(policyTrans.getWefDate(),policyTrans.getWetDate())+1;
        int yeardays = dateUtils.daysBetweenUsingJoda(policyTrans.getCoverFrom(),policyTrans.getCoverTo())+1;
        System.out.println(tableRateType+"=remainingPeriod="+remainingPeriod);
        Iterable<PolicyTaxes> policyTaxes = polTaxesRepo.findAll(QPolicyTaxes.policyTaxes.policy.policyId.eq(policyTrans.getPolicyId()));
        //Iterable<PolicyTaxes> memberTaxes = polTaxesRepo.findAll(QPolicyTaxes.policyTaxes.policy.policyId.eq(policyTrans.getPolicyId()).and(QPolicyTaxes.policyTaxes.taxLevel.eq("R")));

        Iterable<MedicalCategory> categories = medicalCategoryRepo.findAll(QMedicalCategory.medicalCategory.binderDetails.binder.binId.eq(policyTrans.getBinder().getBinId())
                .and(QMedicalCategory.medicalCategory.policy.policyId.eq(polCode)));
        BigDecimal totalPrem = BigDecimal.ZERO;
        BigDecimal totalCommission =BigDecimal.ZERO;
        BigDecimal polstampDuty = BigDecimal.ZERO;
        BigDecimal polAnnualstampDuty = BigDecimal.ZERO;
        BigDecimal polphfFund = BigDecimal.ZERO;
        BigDecimal polwhtxAmt = BigDecimal.ZERO;
        BigDecimal polextras = BigDecimal.ZERO;
        BigDecimal polTl = BigDecimal.ZERO;
        BigDecimal polAnnualphfFund = BigDecimal.ZERO;
        BigDecimal polAnnualextras = BigDecimal.ZERO;
        BigDecimal polAnnualTl = BigDecimal.ZERO;
        BigDecimal polIssueCard = BigDecimal.ZERO;
        BigDecimal polServiceCharge = BigDecimal.ZERO;
        BigDecimal polVatAmt = BigDecimal.ZERO;
        BigDecimal polAnnualPrem = BigDecimal.ZERO;
        for(MedicalCategory category:categories){
            category.setComputedPremium(BigDecimal.ZERO);
        }
        for(MedicalCategory category:categories){
            BigDecimal rates = policyService.getCommissionRate(category.getBinderDetails().getDetId());
            BigDecimal categoryPrem = BigDecimal.ZERO;
            Iterable<CategoryMembers> members = membersRepo.findAll(QCategoryMembers.categoryMembers.category.id.eq(category.getId()));
            Iterable<MedCategoryBenefits> benefits = benefitRepo.findAll(QMedCategoryBenefits.medCategoryBenefits.category.id.eq(category.getId()));
            Iterable<CategoryLoadings> loadings = loadingRepo.findAll(QCategoryLoadings.categoryLoadings.category.id.eq(category.getId()));
            for(MedCategoryBenefits benefit:benefits){
                benefit.setPremium(BigDecimal.ZERO);
                benefit.setComputedPremium(BigDecimal.ZERO);
                benefit.setUsedPremium(BigDecimal.ZERO);
            }
            int oldestage = Integer.MIN_VALUE;
            for(CategoryMembers catMembers:members){
                int calcAge = dateUtils.getAge(catMembers.getClient().getDob());
                if(calcAge > oldestage)
                    oldestage = calcAge;
            }
            benefitRepo.save(benefits);

            final String ratesTable = ratesTblRepo.getRatesLocation(category.getBinderDetails().getDetId());
            if (ratesTable == null)
                throw new BadRequestException("Rates Table for the Binder has not been setup");

            for(CategoryMembers member:members){
                int age = dateUtils.getAge(member.getClient().getDob());
                BigDecimal prem = BigDecimal.ZERO;
                BigDecimal computedPrem= BigDecimal.ZERO;
                BigDecimal riskstampDuty = BigDecimal.ZERO;
                BigDecimal riskphfFund = BigDecimal.ZERO;
                BigDecimal riskwhtxAmt = BigDecimal.ZERO;
                BigDecimal riskextras = BigDecimal.ZERO;
                BigDecimal riskTl = BigDecimal.ZERO;
                for(MedCategoryBenefits benefit:benefits){
                    CategoryMemberBenefits memBenefits = memberBenefitsRepo.findOne(QCategoryMemberBenefits.categoryMemberBenefits.benefit.sectId.eq(benefit.getSectId())
                            .and(QCategoryMemberBenefits.categoryMemberBenefits.member.sectId.eq(member.getSectId())));
                    boolean computeprem= true;
                    double limitAmount =0;
                    if(benefit.getLimit()!=null){
                        if(benefit.getLimit().getLimitAmount()!=null)
                            limitAmount = benefit.getLimit().getLimitAmount().doubleValue();
                    }
                    String dependentType = "";
                    if(policyTrans.getMedicalCoverType()!=null && "I".equalsIgnoreCase(policyTrans.getMedicalCoverType())) {
                        if ("P".equalsIgnoreCase(member.getDependentTypes())) dependentType = "Principal";
                        else if ("S".equalsIgnoreCase(member.getDependentTypes())) dependentType = "Spouse";
                        else if ("C".equalsIgnoreCase(member.getDependentTypes())) dependentType = "Child";
                        else throw new BadRequestException("Unknown Member Type..Error Computing premium");
                    }
                    else if("G".equalsIgnoreCase(policyTrans.getMedicalCoverType())){
                        long memberCount = membersRepo.count(QCategoryMembers.categoryMembers.category.eq(category)
                                .and(QCategoryMembers.categoryMembers.memberStatus.notEqualsIgnoreCase("D"))
                                .and(QCategoryMembers.categoryMembers.mainClient.eq(member.getMainClient())));
                        if(memberCount==1){
                            dependentType = "M";
                        }else if (memberCount==0 ){
                            dependentType = "";
                            computeprem=false;
                        } else
                            dependentType = "M+"+(memberCount-1);
                        age = oldestage;
                    }
                    double premium = 0;
                    BigDecimal newpremium = BigDecimal.ZERO;
                    BigDecimal annualPrem= BigDecimal.ZERO;
                    BigDecimal unusedPrem= BigDecimal.ZERO;
                    BigDecimal consumedPrevPrem= BigDecimal.ZERO;
                    BigDecimal consumedPrevPrem1= BigDecimal.ZERO;
                    BigDecimal computedPremg= BigDecimal.ZERO;
                    boolean gender = benefit.getCover().isDependsOnGender();
                    if(gender && "C".equalsIgnoreCase(member.getDependentTypes())) continue;
                    else
                    if(gender && ("P".equalsIgnoreCase(member.getDependentTypes()) || "S".equalsIgnoreCase(member.getDependentTypes()))){
                        if(!benefit.getCover().getGender().equalsIgnoreCase(member.getClient().getGender())){
                            continue;
                        }
                    }

                    if(!fundPolicy && computeprem) {
                        premium = excelUtils.getPremium(age, limitAmount,
                                dependentType, benefit.getCover().getSection().getShtDesc(),
                                ratesTable);
                    }


                    if(premium==0 && computeprem){
                        if(!fundPolicy) {
                            premium = excelUtils.getPremium(age, limitAmount,
                                    "P", benefit.getCover().getSection().getShtDesc(),
                                    ratesTable);
                        }
                    }
                    annualPrem = new BigDecimal(premium).setScale(2, BigDecimal.ROUND_HALF_EVEN);
                    benefit.setUnitPremium(annualPrem);
                    premium= (new BigDecimal((new BigDecimal(premium).setScale(2, BigDecimal.ROUND_HALF_EVEN).multiply(new BigDecimal(remainingPeriod).setScale(2, BigDecimal.ROUND_HALF_EVEN)).divide(new BigDecimal(yeardays),2,BigDecimal.ROUND_HALF_EVEN)).toPlainString())).doubleValue();
//                                newpremium= (new BigDecimal(premium).setScale(2, BigDecimal.ROUND_HALF_EVEN).multiply(new BigDecimal(remainingPeriod).setScale(2, BigDecimal.ROUND_HALF_EVEN)).divide(new BigDecimal(yeardays)));

                    if ( memBenefits!=null && memBenefits.getUnusedPremium()!= null){
                        unusedPrem = memBenefits.getUnusedPremium();
                    }else
                        unusedPrem = BigDecimal.ZERO;


                    newpremium= new BigDecimal(premium).subtract(unusedPrem);
                    if ("CN".equalsIgnoreCase(policyTrans.getTransType())){
                        premium = 0;
                        newpremium=newpremium.negate();
                    }
                        if(policyTrans.getMedicalCoverType()!=null && "I".equalsIgnoreCase(policyTrans.getMedicalCoverType())) {
                            benefit.setPremium(benefit.getPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN).add(newpremium.setScale(2, BigDecimal.ROUND_HALF_EVEN)));
                            memBenefits.setPremium(newpremium.setScale(2, BigDecimal.ROUND_HALF_EVEN));
                            memBenefits.setUnitPremium(annualPrem);
                            memBenefits.setComputedPremium(new BigDecimal(premium).setScale(2, BigDecimal.ROUND_HALF_EVEN));
                            if (benefit.getComputedPremium() == null)
                                benefit.setComputedPremium(new BigDecimal(premium).setScale(2, BigDecimal.ROUND_HALF_EVEN));
                            else
                                benefit.setComputedPremium(benefit.getComputedPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN).add(memBenefits.getComputedPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN)));

                        }
                        else if("G".equalsIgnoreCase(policyTrans.getMedicalCoverType())){
                            if("P".equalsIgnoreCase(member.getDependentTypes()) && memBenefits!=null) {

                                benefit.setPremium(benefit.getPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN).add(newpremium.setScale(2, BigDecimal.ROUND_HALF_EVEN)));
                                memBenefits.setPremium(newpremium.setScale(2, BigDecimal.ROUND_HALF_EVEN));
                                memBenefits.setComputedPremium(new BigDecimal(premium).setScale(2, BigDecimal.ROUND_HALF_EVEN));
                                memBenefits.setUnitPremium(annualPrem);
                                if (benefit.getComputedPremium() == null)
                                    benefit.setComputedPremium(new BigDecimal(premium).setScale(2, BigDecimal.ROUND_HALF_EVEN));
                                else
                                    benefit.setComputedPremium(benefit.getComputedPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN).add(memBenefits.getComputedPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN)));

                            }else {
                                premium = 0;
                                newpremium = BigDecimal.ZERO;
                            }
                        }





                    prem = prem.add(newpremium.setScale(2, BigDecimal.ROUND_HALF_EVEN));
                    if(policyTrans.getMedicalCoverType()!=null && "I".equalsIgnoreCase(policyTrans.getMedicalCoverType())) {
                        if (!(prem.compareTo(BigDecimal.ZERO) == 0)) {

                            polAnnualPrem = polAnnualPrem.setScale(2, BigDecimal.ROUND_HALF_EVEN).add(annualPrem.setScale(2, BigDecimal.ROUND_HALF_EVEN));
//                            if (prem.compareTo(BigDecimal.ZERO) == -1) {
//                                polAnnualPrem = polAnnualPrem.negate();
//                            }
                        }
                    }
                    else if("G".equalsIgnoreCase(policyTrans.getMedicalCoverType())){
                        if("P".equalsIgnoreCase(member.getDependentTypes())) {
                            polAnnualPrem = polAnnualPrem.setScale(2, BigDecimal.ROUND_HALF_EVEN).add(annualPrem.setScale(2, BigDecimal.ROUND_HALF_EVEN));
                            //if (prem.compareTo(BigDecimal.ZERO) == -1) {
                            // polAnnualPrem = polAnnualPrem.negate();
                            //}
                        }
                    }
                     if(memBenefits!=null) {
                         computedPrem = computedPrem.add(memBenefits.getComputedPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN));
                         memberBenefitsRepo.save(memBenefits);
                     }

                }

                benefitRepo.save(benefits);
                age = dateUtils.getAge(member.getClient().getDob());
                member.setAge((long)age);
                System.out.println("premium="+prem);

                if(policyTrans.getMedicalCoverType()!=null && "I".equalsIgnoreCase(policyTrans.getMedicalCoverType())) {
                    for(PolicyTaxes policyTax:policyTaxes){
                        BigDecimal computedTax = calculateTax(prem, policyTax.getTaxRate(), policyTax.getDivFactor(),policyTax.getRateType());
                        policyTax.setTaxAmount(computedTax);
//                        if(policyTax.getRevenueItems().getItem()== RevenueItems.EX){
//                            if("R".equalsIgnoreCase(policyTax.getTaxLevel())){
//                                riskextras = riskextras.add(computedTax);
//                            }
//                        }
//                        else
                            if(policyTax.getRevenueItems().getItem() == RevenueItems.SD){
                            if("R".equalsIgnoreCase(policyTax.getTaxLevel())){
                                riskstampDuty = riskstampDuty.add(computedTax);
                            }
                        }
                        else if(policyTax.getRevenueItems().getItem() == RevenueItems.PHCF){
                            if("R".equalsIgnoreCase(policyTax.getTaxLevel())){
                                riskphfFund = riskphfFund.add(computedTax);
                            }
                        }
                        else if(policyTax.getRevenueItems().getItem() ==RevenueItems.WHTX){
                            if("R".equalsIgnoreCase(policyTax.getTaxLevel())){
                                riskwhtxAmt = riskwhtxAmt.add(computedTax);
                            }
                        }
                        else if(policyTax.getRevenueItems().getItem()==RevenueItems.TL){
                            if("R".equalsIgnoreCase(policyTax.getTaxLevel())){
                                riskTl = riskTl.add(computedTax);
                            }
                        }
                    }
                    member.setPremium(prem);
                    member.setComputedPremium(computedPrem);


                    member.setExtras(riskextras);
                    member.setPhfFund(riskphfFund);
                    member.setTrainingLevy(riskTl);
                    member.setStampDuty(riskstampDuty);
                }
                else if("G".equalsIgnoreCase(policyTrans.getMedicalCoverType())){
                    if("P".equalsIgnoreCase(member.getDependentTypes())) {

                        for(PolicyTaxes policyTax:policyTaxes){
                            BigDecimal computedTax = calculateTax(prem, policyTax.getTaxRate(), policyTax.getDivFactor(),policyTax.getRateType());
                            policyTax.setTaxAmount(computedTax);
//                            if(policyTax.getRevenueItems().getItem()== RevenueItems.EX){
//                                if("R".equalsIgnoreCase(policyTax.getTaxLevel())){
//                                    riskextras = riskextras.add(computedTax);
//                                }
//                            }
//                            else
                                if(policyTax.getRevenueItems().getItem() == RevenueItems.SD){
                                if("R".equalsIgnoreCase(policyTax.getTaxLevel())){
                                    riskstampDuty = riskstampDuty.add(computedTax);
                                }
                            }
                            else if(policyTax.getRevenueItems().getItem() == RevenueItems.PHCF){
                                if("R".equalsIgnoreCase(policyTax.getTaxLevel())){
                                    riskphfFund = riskphfFund.add(computedTax);
                                }
                            }
                            else if(policyTax.getRevenueItems().getItem() ==RevenueItems.WHTX){
                                if("R".equalsIgnoreCase(policyTax.getTaxLevel())){
                                    riskwhtxAmt = riskwhtxAmt.add(computedTax);
                                }
                            }
                            else if(policyTax.getRevenueItems().getItem()==RevenueItems.TL){
                                if("R".equalsIgnoreCase(policyTax.getTaxLevel())){
                                    riskTl = riskTl.add(computedTax);
                                }
                            }
                        }

                        member.setPremium(prem);
                        member.setComputedPremium(computedPrem);



                        member.setExtras(riskextras);
                        member.setPhfFund(riskphfFund);
                        member.setTrainingLevy(riskTl);
                        member.setStampDuty(riskstampDuty);
                    }
                }
//                if (policyTrans.getTransType().equalsIgnoreCase("AD")) {
//                    if (category.getComputedPremium() == null)
//                        category.setComputedPremium(member.getComputedPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN));
//                    else
//                    if(member.getComputedPremium()!=null) {
//                        category.setComputedPremium(category.getComputedPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN).add(member.getComputedPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN)));
//                    }
//                }

                categoryPrem = categoryPrem.add(prem);

            }
            membersRepo.save(members);
//            categoryPrem=category.getComputedPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN).subtract(category.getPrevPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN));
            BigDecimal loadingPrem = BigDecimal.ZERO;
            for(CategoryLoadings loading:loadings){
                BigDecimal divideRate = BigDecimal.valueOf(100);
                if(loading.getRateType()==null)
                {}
                else if("P".equalsIgnoreCase(loading.getRateType())){
                    divideRate = BigDecimal.valueOf(100);
                }
                else if("M".equalsIgnoreCase(loading.getRateType())){
                    divideRate = BigDecimal.valueOf(1000);
                }
                else if("A".equalsIgnoreCase(loading.getRateType())){
                    // loadingPrem = loadingPrem.add(loading.getRate());
                    //  continue;
                    divideRate = BigDecimal.ONE;
                }
                if(divideRate.compareTo(BigDecimal.ONE)==0){
                    loadingPrem = loadingPrem.add(loading.getRate());
                }
                else  if(loading.getRate()!=null && loading.getRate().compareTo(BigDecimal.ZERO) ==1){
                    loadingPrem = loadingPrem.add(loading.getRate().divide(divideRate).multiply(categoryPrem));
                }
                loading.setLoadingAmt(loadingPrem);
            }
            loadingRepo.save(loadings);
            category.setLoadingPrem(loadingPrem);
            categoryPrem = categoryPrem.add(loadingPrem);

            category.setPremium(categoryPrem);
            totalPrem = totalPrem.add(categoryPrem);
            BigDecimal comm = totalPrem.multiply(rates).divide(new BigDecimal(100));
            if(totalPrem.compareTo(BigDecimal.ZERO)==1){
                comm = comm.negate();
            }
            else if(totalPrem.compareTo(BigDecimal.ZERO)==-1){
                comm = comm.abs();
            }
            // totalCommission = totalCommission.add(comm);
            totalCommission = comm;
        }
        medicalCategoryRepo.save(categories);
        for(PolicyTaxes policyTax:policyTaxes){
            BigDecimal computedTax = calculateTax(totalPrem, policyTax.getTaxRate(), policyTax.getDivFactor(),policyTax.getRateType());
            BigDecimal computedAnnualTax = calculateTax(polAnnualPrem, policyTax.getTaxRate(), policyTax.getDivFactor(),policyTax.getRateType());
//            if(policyTax.getRevenueItems().getItem()== RevenueItems.EX){
//                polextras = polextras.add(computedTax);
//                polAnnualextras = computedAnnualTax.add(computedAnnualTax);
//                policyTax.setTaxAmount(polextras);
//            }
//            else
                if(policyTax.getRevenueItems().getItem() == RevenueItems.SD){
                polstampDuty = polstampDuty.add(computedTax);
                polAnnualstampDuty = polAnnualstampDuty.add(computedAnnualTax);
                policyTax.setTaxAmount(polstampDuty);
            }
            else if(policyTax.getRevenueItems().getItem() == RevenueItems.PHCF){
                polphfFund = polphfFund.add(computedTax);
                polAnnualphfFund = polAnnualphfFund.add(computedAnnualTax);
                policyTax.setTaxAmount(polphfFund);
            }
            else if(policyTax.getRevenueItems().getItem()==RevenueItems.TL){
                polTl = polTl.add(computedTax);
                polAnnualTl = polAnnualTl.add(computedAnnualTax);
                policyTax.setTaxAmount(polTl);
            }
        }
        long memberCount =0;
        long memberCardCount =0;

//        boolean issueCard = policyTrans.getIssueCard()!=null && "Y".equalsIgnoreCase(policyTrans.getIssueCard());
        boolean issueCard = true;
        if (policyTrans.getBinCardType()==null)
            issueCard = false;
        BigDecimal issueCardFee = BigDecimal.ZERO;
        BigDecimal serviceCharge = BigDecimal.ZERO;
        if(issueCard ) {
            if ( "NB".equalsIgnoreCase(policyTrans.getTransType())){
                memberCount = membersRepo.count(QCategoryMembers.categoryMembers.category.policy.policyId.eq(polCode));
                memberCardCount = membersRepo.count(QCategoryMembers.categoryMembers.category.policy.policyId.eq(polCode)
                        .and(QCategoryMembers.categoryMembers.memberHasCard.eq("N")));
            } else if ( "AD".equalsIgnoreCase(policyTrans.getTransType())) {
                memberCount = membersRepo.count(QCategoryMembers.categoryMembers.category.policy.policyId.eq(polCode)
                        .and(QCategoryMembers.categoryMembers.memberStatus.eq("N")));
                memberCardCount = membersRepo.count(QCategoryMembers.categoryMembers.category.policy.policyId.eq(polCode)
                        .and(QCategoryMembers.categoryMembers.memberHasCard.eq("N"))
                        .and(QCategoryMembers.categoryMembers.memberStatus.eq("N")));
            }
//            if("P".equalsIgnoreCase(policyTrans.getCardType())){
//                issueCardFee = paramService.getParamValue("PHOTO_CARD_ISSUE_FEE");
//            }
//            else  if("E".equalsIgnoreCase(policyTrans.getCardType())){
//                issueCardFee = paramService.getParamValue("ELECTRONIC_CARD_ISSUE_FEE");
//                serviceCharge = paramService.getParamValue("SERVICE_CHARGE");
//            }

            issueCardFee=policyTrans.getBinCardType().getCardFee();
            if ("Y".equalsIgnoreCase(policyTrans.getBinCardType().getServiceProrated()))
                serviceCharge= new BigDecimal((policyTrans.getBinCardType().getServiceCharge().setScale(2, BigDecimal.ROUND_HALF_EVEN).multiply(new BigDecimal(remainingPeriod).setScale(2, BigDecimal.ROUND_HALF_EVEN)).divide(new BigDecimal(yeardays),2,BigDecimal.ROUND_HALF_EVEN)).toPlainString());
            else
                serviceCharge=policyTrans.getBinCardType().getServiceCharge();
            polIssueCard = issueCardFee.multiply(BigDecimal.valueOf(memberCardCount));
            polServiceCharge = serviceCharge.multiply(BigDecimal.valueOf(memberCount));

//            for (PolicyTaxes polTaxes : memberTaxes) {
//                if (polTaxes.getRevenueItems().getItem().equals(RevenueItems.CF)) {
//                    polIssueCard = issueCardFee.multiply(BigDecimal.valueOf(memberCount));
//                    polTaxes.setTaxAmount(polIssueCard);
//                    polTaxes.setTaxRate(issueCardFee);
//                } else if ( polTaxes.getRevenueItems().getItem().equals(RevenueItems.SC)) {
//                    polServiceCharge = serviceCharge.multiply(BigDecimal.valueOf(memberCount));
//                    polTaxes.setTaxAmount(polServiceCharge);
//                    polTaxes.setTaxRate(serviceCharge);
//                }
//            }
            polTaxesRepo.save(policyTaxes);
             if(policyTrans.getBinCardType().getVatApplicable()!=null) {
                 if ("Y".equalsIgnoreCase(policyTrans.getBinCardType().getVatApplicable())) {
                     polVatAmt = polVatAmt.add(polServiceCharge.multiply(policyTrans.getAgent().getAccountType().getVatRate().divide(BigDecimal.valueOf(100))));
                     polVatAmt = polVatAmt.add(polIssueCard.multiply(policyTrans.getAgent().getAccountType().getVatRate().divide(BigDecimal.valueOf(100))));
                 }
             }
                policyTrans.setServiceCharge(polServiceCharge);
            policyTrans.setIssueCardFee(polIssueCard);
            policyTrans.setVatAmount(polVatAmt);
        }
        BigDecimal whtxAmt = BigDecimal.ZERO;
        AccountTypes accType = policyTrans.getAgent().getAccountType();
        if(accType.isWhtxAppl()){
            if(accType.getWhtaxVal().compareTo(BigDecimal.ZERO)==1){
                whtxAmt = accType.getWhtaxVal().divide(new BigDecimal(100)).multiply(totalCommission);
                polwhtxAmt = whtxAmt.negate();
            }
        }

        if(totalPrem.compareTo(BigDecimal.ZERO)==1){
            totalCommission = totalCommission.abs().negate();
        }
        else if(totalPrem.compareTo(BigDecimal.ZERO)==-1){
            totalCommission = totalCommission.abs();
        }
        Currencies currencies = policyTrans.getTransCurrency();
        totalPrem = totalPrem.setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN);
        totalCommission =totalCommission.setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN);
        policyTrans.setPremium(totalPrem);
        policyTrans.setBasicPrem((totalPrem.add(polextras).add(polstampDuty).add(polphfFund).add(polTl).add(polIssueCard).add(polServiceCharge).add(polVatAmt)).setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN));
        policyTrans.setEndosbasicPremium(totalPrem);
        policyTrans.setEndosCommissions(totalCommission);
        policyTrans.setCommAmt(totalCommission);
        policyTrans.setExtras(polextras);
        policyTrans.setWhtx(polwhtxAmt);
        policyTrans.setPhcf(polphfFund);
        policyTrans.setTrainingLevy(polTl);
        policyTrans.setTotTrainingLevy(polTl);
        policyTrans.setTotPhcf(polphfFund);
        policyTrans.setStampDuty(polstampDuty);
        policyTrans.setSumInsured(BigDecimal.ZERO);
        policyTrans.setNetPrem((totalPrem.add(polextras).add(polstampDuty).add(polphfFund).add(polTl).add(polwhtxAmt).add(totalCommission).add(polServiceCharge).add(polIssueCard).add(polVatAmt)).setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN));
        if(policyTrans.isRenewable()) {
            if (policyTrans.getMedicalCoverType() != null && "I".equalsIgnoreCase(policyTrans.getMedicalCoverType())) {
                if (policyTrans.getPrevFuturePrem() == null) {
                    policyTrans.setFuturePrem((totalPrem.add(polextras).add(polphfFund).add(polTl)).setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                } else {
                    policyTrans.setFuturePrem(policyTrans.getPrevFuturePrem().setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN).add(polAnnualPrem.add(polAnnualextras).add(polAnnualphfFund).add(polAnnualTl)).setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                }
            } else if ("G".equalsIgnoreCase(policyTrans.getMedicalCoverType())) {
                policyTrans.setFuturePrem((polAnnualPrem.add(polAnnualextras).add(polAnnualphfFund).add(polAnnualTl)).setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            }
        }
        else
            policyTrans.setFuturePrem(BigDecimal.ZERO);
        PolicyTrans savedPolicy =  policyTransRepo.save(policyTrans);
    }


//    @Override
//    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class,IOException.class })
//    public void computePrem18092018(Long polCode, String calcRefund) throws BadRequestException,FileNotFoundException,IOException {
//        if(polCode==null) throw new BadRequestException("No policy Details to compute premium");
//        PolicyTrans policyTrans = policyTransRepo.findOne(polCode);
//
//
//        if("CR".equalsIgnoreCase(policyTrans.getTransType())){
//            return;
//        }
//       // transService.populateCategoryClauses(policyTrans);
//
//       // transService.populateCategoryExclusions(policyTrans);
//       // transService.populateCategoryLoadings(policyTrans);
//      //  transService.populateCategoryProviders(policyTrans);
//        boolean fundPolicy = policyTrans.getBinder().getFundBinder()!=null && "Y".equalsIgnoreCase(policyTrans.getBinder().getFundBinder());
//        if(!fundPolicy)
//        transService.populateCategoryTaxes(policyTrans);
//        String tableRateType = "";
//        if(policyTrans.getMedicalCoverType()!=null){
//            tableRateType = policyTrans.getMedicalCoverType();
//        }
//        int remainingPeriod = dateUtils.daysBetweenUsingJoda(policyTrans.getWefDate(),policyTrans.getWetDate())+1;
//        System.out.println(tableRateType+"=remainingPeriod="+remainingPeriod);
//        Iterable<PolicyTaxes> policyTaxes = polTaxesRepo.findAll(QPolicyTaxes.policyTaxes.policy.policyId.eq(policyTrans.getPolicyId()).and(QPolicyTaxes.policyTaxes.taxLevel.eq("P")));
//        Iterable<PolicyTaxes> memberTaxes = polTaxesRepo.findAll(QPolicyTaxes.policyTaxes.policy.policyId.eq(policyTrans.getPolicyId()).and(QPolicyTaxes.policyTaxes.taxLevel.eq("R")));
//        Iterable<BinderRatesTable> ratesTables = ratesTblRepo.findAll(QBinderRatesTable.binderRatesTable.binder.binId.eq(policyTrans.getBinder().getBinId())
//                                                              .and(QBinderRatesTable.binderRatesTable.tableType.eq(tableRateType)) );
//        if(!fundPolicy)
//        if(ratesTables.spliterator().getExactSizeIfKnown()==0) throw new BadRequestException("Rates Table for the Binder has not been setup");
//        Calendar cal = Calendar.getInstance();
//        cal.set(1970,01,01);
//        Date maxDate = cal.getTime();
//        for (BinderRatesTable ratesTable : ratesTables) {
//            if(ratesTable.getEffDate().after(maxDate)){
//                maxDate = ratesTable.getEffDate();
//            }
//        }
//
//        BinderRatesTable rateTable = ratesTblRepo.findOne(QBinderRatesTable.binderRatesTable.binder.binId.eq(policyTrans.getBinder().getBinId())
//                                                         .and(QBinderRatesTable.binderRatesTable.effDate.eq(maxDate))
//                                                         .and(QBinderRatesTable.binderRatesTable.tableType.eq(tableRateType)));
//        if(!fundPolicy) {
//            if (rateTable.getRate_table().length == 0)
//                throw new BadRequestException("Rates Table for the Binder has not been setup");
////            File rateFile = new File(rateTable.getId()+"_"+rateTable.getFileName());
////            if(!rateFile.exists()) {
////                try (
////
////                        ByteArrayInputStream in = new ByteArrayInputStream(rateTable.getRate_table());
////                        FileOutputStream out = new FileOutputStream(rateTable.getId() + "_" + rateTable.getFileName())) {
////                    IOUtils.copy(in, out);
////                }
////            }
//        }
//        Iterable<MedicalCategory> categories = medicalCategoryRepo.findAll(QMedicalCategory.medicalCategory.binderDetails.binder.binId.eq(policyTrans.getBinder().getBinId())
//        .and(QMedicalCategory.medicalCategory.policy.policyId.eq(polCode)));
//       BigDecimal totalPrem = BigDecimal.ZERO;
//        BigDecimal totalCommission =BigDecimal.ZERO;
//        BigDecimal polstampDuty = BigDecimal.ZERO;
//        BigDecimal polAnnualstampDuty = BigDecimal.ZERO;
//        BigDecimal polphfFund = BigDecimal.ZERO;
//        BigDecimal polwhtxAmt = BigDecimal.ZERO;
//        BigDecimal polextras = BigDecimal.ZERO;
//        BigDecimal polTl = BigDecimal.ZERO;
//        BigDecimal polAnnualphfFund = BigDecimal.ZERO;
//        BigDecimal polAnnualextras = BigDecimal.ZERO;
//        BigDecimal polAnnualTl = BigDecimal.ZERO;
//        BigDecimal polIssueCard = BigDecimal.ZERO;
//        BigDecimal polServiceCharge = BigDecimal.ZERO;
//        BigDecimal polVatAmt = BigDecimal.ZERO;
//        BigDecimal polAnnualPrem = BigDecimal.ZERO;
//        for(MedicalCategory category:categories){
//            category.setComputedPremium(BigDecimal.ZERO);
//        }
//        for(MedicalCategory category:categories){
//            BigDecimal rates = policyService.getCommissionRate(category.getBinderDetails().getDetId());
//            BigDecimal categoryPrem = BigDecimal.ZERO;
//            Iterable<CategoryMembers> members = membersRepo.findAll(QCategoryMembers.categoryMembers.category.id.eq(category.getId()));
//            Iterable<MedCategoryBenefits> benefits = benefitRepo.findAll(QMedCategoryBenefits.medCategoryBenefits.category.id.eq(category.getId()));
//            Iterable<CategoryLoadings> loadings = loadingRepo.findAll(QCategoryLoadings.categoryLoadings.category.id.eq(category.getId()));
//            for(MedCategoryBenefits benefit:benefits){
//                benefit.setPremium(BigDecimal.ZERO);
//                benefit.setComputedPremium(BigDecimal.ZERO);
//                benefit.setUsedPremium(BigDecimal.ZERO);
//            }
//            int oldestage = Integer.MIN_VALUE;
//            for(CategoryMembers catMembers:members){
//                int calcAge = dateUtils.getAge(catMembers.getClient().getDob());
//                if(calcAge > oldestage)
//                    oldestage = calcAge;
//            }
//            benefitRepo.save(benefits);
//            for(CategoryMembers member:members){
//                int age = dateUtils.getAge(member.getClient().getDob());
//                BigDecimal prem = BigDecimal.ZERO;
//                BigDecimal computedPrem= BigDecimal.ZERO;
//                   for(MedCategoryBenefits benefit:benefits){
//                       CategoryMemberBenefits memBenefits = memberBenefitsRepo.findOne(QCategoryMemberBenefits.categoryMemberBenefits.benefit.sectId.eq(benefit.getSectId())
//                       .and(QCategoryMemberBenefits.categoryMemberBenefits.member.sectId.eq(member.getSectId())));
//                       boolean computeprem= true;
//                       double limitAmount =0;
//                                  if(benefit.getLimit()!=null){
//                                      if(benefit.getLimit().getLimitAmount()!=null)
//                                      limitAmount = benefit.getLimit().getLimitAmount().doubleValue();
//                                  }
//                                String dependentType = "";
//                                if(policyTrans.getMedicalCoverType()!=null && "I".equalsIgnoreCase(policyTrans.getMedicalCoverType())) {
//                                    if ("P".equalsIgnoreCase(member.getDependentTypes())) dependentType = "Principal";
//                                    else if ("S".equalsIgnoreCase(member.getDependentTypes())) dependentType = "Spouse";
//                                    else if ("C".equalsIgnoreCase(member.getDependentTypes())) dependentType = "Child";
//                                    else throw new BadRequestException("Unknown Member Type..Error Computing premium");
//                                }
//                                else if("G".equalsIgnoreCase(policyTrans.getMedicalCoverType())){
//                                    long memberCount = membersRepo.count(QCategoryMembers.categoryMembers.category.eq(category)
//                                            .and(QCategoryMembers.categoryMembers.memberStatus.notEqualsIgnoreCase("D"))
//                                                                         .and(QCategoryMembers.categoryMembers.mainClient.eq(member.getMainClient())));
//                                    if(memberCount==1){
//                                        dependentType = "M";
//                                    }else if (memberCount==0 ){
//                                        dependentType = "";
//                                        computeprem=false;
//                                    } else
//                                    dependentType = "M+"+(memberCount-1);
//                                    age = oldestage;
//                                }
//                                double premium = 0;
//                                BigDecimal newpremium = BigDecimal.ZERO;
//                                BigDecimal annualPrem= BigDecimal.ZERO;
//                                BigDecimal unconsumedPrevPrem= BigDecimal.ZERO;
//                                BigDecimal consumedPrevPrem= BigDecimal.ZERO;
//                                BigDecimal consumedPrevPrem1= BigDecimal.ZERO;
//                                BigDecimal computedPremg= BigDecimal.ZERO;
//                               boolean gender = benefit.getCover().isDependsOnGender();
//                               if(gender && "C".equalsIgnoreCase(member.getDependentTypes())) continue;
//                               else
//                               if(gender && ("P".equalsIgnoreCase(member.getDependentTypes()) || "S".equalsIgnoreCase(member.getDependentTypes()))){
//                                   if(!benefit.getCover().getGender().equalsIgnoreCase(member.getClient().getGender())){
//                                       continue;
//                                   }
//                               }
//
//                                    if(!fundPolicy && computeprem) {
//                                        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(rateTable.getRate_table())) {
//                                            if (inputStream != null) {
//                                                premium = excelUtils.getPremium(age, limitAmount,
//                                                        dependentType, benefit.getCover().getSection().getShtDesc(),
//                                                        inputStream);
//                                            }
//                                        }
//                                    }
//
//
//                                if(premium==0 && computeprem){
//                                    if(!fundPolicy) {
//                                        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(rateTable.getRate_table())) {
//                                            if (inputStream != null) {
//                                                premium = excelUtils.getPremium(age, limitAmount,
//                                                        "P", benefit.getCover().getSection().getShtDesc(),
//                                                        inputStream);
//                                            }
//                                        }
//                                    }
//                                }
//                                annualPrem = new BigDecimal(premium).setScale(2, BigDecimal.ROUND_HALF_EVEN);
//                                benefit.setUnitPremium(annualPrem);
//                                 newpremium= new BigDecimal((new BigDecimal(premium).setScale(2, BigDecimal.ROUND_HALF_EVEN).multiply(new BigDecimal(remainingPeriod).setScale(2, BigDecimal.ROUND_HALF_EVEN)).divide(new BigDecimal(yeardays),2,BigDecimal.ROUND_HALF_EVEN)).toPlainString());
////                                newpremium= (new BigDecimal(premium).setScale(2, BigDecimal.ROUND_HALF_EVEN).multiply(new BigDecimal(remainingPeriod).setScale(2, BigDecimal.ROUND_HALF_EVEN)).divide(new BigDecimal(yeardays)));
//
//                                    premium= newpremium.doubleValue();
//
//                                if (policyTrans.getTransType().equalsIgnoreCase("AD")){
//                                   if ("I".equalsIgnoreCase(policyTrans.getMedicalCoverType())) {
//                                       if (member.getMemberStatus().equalsIgnoreCase("D")) {
//                                           int coverperiod = dateUtils.daysBetweenUsingJoda(memBenefits.getPrevwefDate(),memBenefits.getWetDate())+1;
//                                           newpremium = memBenefits.getPrevPremium().multiply(new BigDecimal(remainingPeriod).setScale(2, BigDecimal.ROUND_HALF_EVEN)).divide(new BigDecimal(coverperiod),2,BigDecimal.ROUND_HALF_EVEN);
//                                           premium= newpremium.doubleValue();
//                                           premium = premium * -1;
//                                       }
//                                       if (member.getMemberStatus().equalsIgnoreCase("E")) {
//                                           premium = 0;
//                                           memBenefits.setComputedPremium(memBenefits.getPrevPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN));
//                                           if (benefit.getComputedPremium() == null)
//                                               benefit.setComputedPremium(memBenefits.getPrevPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN));
//                                           else
//                                               benefit.setComputedPremium(benefit.getComputedPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN).add(memBenefits.getPrevPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN)));
//
//                                           memBenefits.setPremium(new BigDecimal(premium).setScale(2, BigDecimal.ROUND_HALF_EVEN));
//                                       } else {
//
//                                           if (memBenefits.getPrevPremium() == null)
//                                               memBenefits.setPremium(new BigDecimal(premium).setScale(2, BigDecimal.ROUND_HALF_EVEN));
//                                           else {
//                                               memBenefits.setPremium(new BigDecimal(premium).setScale(2, BigDecimal.ROUND_HALF_EVEN).subtract(memBenefits.getPrevPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN)));
//                                           }
//                                           memBenefits.setComputedPremium(new BigDecimal(premium).setScale(2, BigDecimal.ROUND_HALF_EVEN));
//                                           if (benefit.getComputedPremium() == null)
//                                               benefit.setComputedPremium(new BigDecimal(premium).setScale(2, BigDecimal.ROUND_HALF_EVEN));
//                                           else
//                                               benefit.setComputedPremium(benefit.getComputedPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN).add(memBenefits.getComputedPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN)));
//
//                                       }
//                                       if (benefit.getPremium() == null)
//                                           benefit.setPremium(memBenefits.getPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN));
//                                       else
//                                           benefit.setPremium(benefit.getPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN).add(memBenefits.getPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN)));
//                                   }else if("G".equalsIgnoreCase(policyTrans.getMedicalCoverType())){
//
//                                      if("P".equalsIgnoreCase(member.getDependentTypes())) {
//                                          PolicyTrans prevPolicyTrans = policyTrans.getPreviousTrans();
//                                          int lapsedperiod = dateUtils.daysBetweenUsingJoda(memBenefits.getPrevwefDate(),policyTrans.getWefDate());
//                                          int prevlapsedperiod =dateUtils.daysBetweenUsingJoda(policyTrans.getCoverFrom(),prevPolicyTrans.getWefDate());
//                                          int lapsedperioddiff = lapsedperiod-prevlapsedperiod;
//                                          if (memBenefits.getPrevPremium() == null) {
//                                              unconsumedPrevPrem = BigDecimal.ZERO;
//                                              consumedPrevPrem = BigDecimal.ZERO;
//                                          }
//                                          else {
//                                                 consumedPrevPrem = new BigDecimal((benefit.getPrevUnitPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN).multiply(new BigDecimal(lapsedperioddiff).setScale(2, BigDecimal.ROUND_HALF_EVEN)).divide(new BigDecimal(yeardays), 2, BigDecimal.ROUND_HALF_EVEN)).toPlainString());
//                                              unconsumedPrevPrem= (memBenefits.getPrevPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN)).subtract(consumedPrevPrem);
//                                          }
//
//                                          if (benefit.getPrevusedPremium()==null)
//                                              consumedPrevPrem1=BigDecimal.ZERO;
//                                          else
//                                              consumedPrevPrem1=benefit.getPrevusedPremium();
//
//                                          computedPremg=newpremium.add(consumedPrevPrem).add(consumedPrevPrem1);
//                                          if (memBenefits.getPrevPremium()==null){
//                                              premium= computedPremg.doubleValue();
//                                          }else {
//                                              premium= (computedPremg.subtract(memBenefits.getPrevPremium()).doubleValue());
//                                          }
//
//                                          if ((newpremium.add(consumedPrevPrem).add(consumedPrevPrem1)).equals(memBenefits.getPrevPremium()))
//                                              premium=0;
//
//                                          benefit.setPremium(benefit.getPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN).add(new BigDecimal(premium).setScale(2, BigDecimal.ROUND_HALF_EVEN)));
//                                          memBenefits.setPremium(new BigDecimal(premium).setScale(2, BigDecimal.ROUND_HALF_EVEN));
//
//                                          System.out.println("prevlapsedperiod"+ prevlapsedperiod+";lapsedperiod="+lapsedperiod+";computedPremg="+ computedPremg + ";premium="+premium+";newpremium="+newpremium +"consumedPrevPrem="+ consumedPrevPrem +"unconsumedPrevPrem="+ unconsumedPrevPrem +"consumedPrevPrem1="+ consumedPrevPrem1);
////                                          if (memBenefits.getPrevPremium() == null)
//                                              memBenefits.setComputedPremium(computedPremg);
//                                                memBenefits.setUsedPremium(consumedPrevPrem);
////                                              else
////                                          memBenefits.setComputedPremium(memBenefits.getPrevPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN).add(computedPremg));
//                                           if (benefit.getComputedPremium() == null)
//                                               benefit.setComputedPremium( memBenefits.getComputedPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN));
//                                           else
//                                               benefit.setComputedPremium(benefit.getComputedPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN).add(memBenefits.getComputedPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN)));
//
//                                          if (benefit.getUsedPremium() == null)
//                                              benefit.setUsedPremium( memBenefits.getUsedPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN));
//                                          else
//                                              benefit.setUsedPremium(benefit.getUsedPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN).add(memBenefits.getUsedPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN)));
//                                      }else premium=0;
//                                   }
//                                } else if (policyTrans.getTransType().equalsIgnoreCase("AB")){
//                                    if ("I".equalsIgnoreCase(policyTrans.getMedicalCoverType())) {
//                                        if (memBenefits.getPrevPremium()==null)
//                                        unconsumedPrevPrem= BigDecimal.ZERO;
//                                        else
//                                            unconsumedPrevPrem=new BigDecimal((memBenefits.getPrevPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN).multiply(new BigDecimal(remainingPeriod).setScale(2, BigDecimal.ROUND_HALF_EVEN)).divide(new BigDecimal(yeardays),2,BigDecimal.ROUND_HALF_EVEN)).toPlainString());
//                                        premium= (newpremium.subtract(unconsumedPrevPrem)).doubleValue();
//                                        memBenefits.setPremium(new BigDecimal(premium).setScale(2, BigDecimal.ROUND_HALF_EVEN));
//                                        if (memBenefits.getPrevPremium()==null)
//                                            memBenefits.setComputedPremium(new BigDecimal(premium).setScale(2, BigDecimal.ROUND_HALF_EVEN));
//                                        else
//                                            memBenefits.setComputedPremium(memBenefits.getPrevPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN).add(new BigDecimal(premium).setScale(2, BigDecimal.ROUND_HALF_EVEN)));
//
//                                        if (benefit.getComputedPremium() == null)
//                                            benefit.setComputedPremium(new BigDecimal(premium).setScale(2, BigDecimal.ROUND_HALF_EVEN));
//                                        else
//                                            benefit.setComputedPremium(benefit.getComputedPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN).add(memBenefits.getComputedPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN)));
//
//                                        if (benefit.getPremium() == null)
//                                            benefit.setPremium(memBenefits.getPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN));
//                                        else
//                                            benefit.setPremium(benefit.getPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN).add(memBenefits.getPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN)));
//
//                                    }else if("G".equalsIgnoreCase(policyTrans.getMedicalCoverType())){
//
//                                        if("P".equalsIgnoreCase(member.getDependentTypes())) {
//
//                                            if (benefit.getPrevPremium() == null)
//                                                unconsumedPrevPrem=BigDecimal.ZERO;
//                                                else
//                                            unconsumedPrevPrem=new BigDecimal((benefit.getPrevPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN).multiply(new BigDecimal(remainingPeriod).setScale(2, BigDecimal.ROUND_HALF_EVEN)).divide(new BigDecimal(yeardays),2,BigDecimal.ROUND_HALF_EVEN)).toPlainString());
//                                            if (benefit.getStatus().equalsIgnoreCase("D")) {  // deleted(D)
//                                                unconsumedPrevPrem = unconsumedPrevPrem.negate();
//                                                premium= (unconsumedPrevPrem).doubleValue();
//                                            }else if (benefit.getStatus().equalsIgnoreCase("E")){  // existing(E)
//                                                premium=0;
//                                            }else {  // changed(C)
//                                                premium= (newpremium.subtract(unconsumedPrevPrem)).doubleValue();
//                                            }
//
//                                            benefit.setPremium(benefit.getPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN).add(new BigDecimal(premium).setScale(2, BigDecimal.ROUND_HALF_EVEN)));
//                                            memBenefits.setPremium(new BigDecimal(premium).setScale(2, BigDecimal.ROUND_HALF_EVEN));
//                                            if (memBenefits.getPrevPremium()==null)
//                                                memBenefits.setComputedPremium(new BigDecimal(premium).setScale(2, BigDecimal.ROUND_HALF_EVEN));
//                                                else
//                                            memBenefits.setComputedPremium(memBenefits.getPrevPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN).add(new BigDecimal(premium).setScale(2, BigDecimal.ROUND_HALF_EVEN)));
//                                            if (benefit.getComputedPremium() == null)
//                                                benefit.setComputedPremium( memBenefits.getComputedPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN));
//                                            else
//                                                benefit.setComputedPremium(benefit.getComputedPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN).add(memBenefits.getComputedPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN)));
//                                        }else premium=0;
//                                    }
//                                }
//                                else {
//
//                                    if(policyTrans.getMedicalCoverType()!=null && "I".equalsIgnoreCase(policyTrans.getMedicalCoverType())) {
//                                        benefit.setPremium(benefit.getPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN).add(new BigDecimal(premium).setScale(2, BigDecimal.ROUND_HALF_EVEN)));
//                                        memBenefits.setPremium(new BigDecimal(premium).setScale(2, BigDecimal.ROUND_HALF_EVEN));
//
//                                        memBenefits.setComputedPremium(new BigDecimal(premium).setScale(2, BigDecimal.ROUND_HALF_EVEN));
//                                        if (benefit.getComputedPremium() == null)
//                                            benefit.setComputedPremium(new BigDecimal(premium).setScale(2, BigDecimal.ROUND_HALF_EVEN));
//                                        else
//                                            benefit.setComputedPremium(benefit.getComputedPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN).add(memBenefits.getComputedPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN)));
//
//                                    }
//                                    else if("G".equalsIgnoreCase(policyTrans.getMedicalCoverType())){
//                                        if("P".equalsIgnoreCase(member.getDependentTypes())) {
//
//                                            benefit.setPremium(benefit.getPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN).add(new BigDecimal(premium).setScale(2, BigDecimal.ROUND_HALF_EVEN)));
//                                            memBenefits.setPremium(new BigDecimal(premium).setScale(2, BigDecimal.ROUND_HALF_EVEN));
//                                            memBenefits.setComputedPremium(new BigDecimal(premium).setScale(2, BigDecimal.ROUND_HALF_EVEN));
//                                            if (benefit.getComputedPremium() == null)
//                                                benefit.setComputedPremium(new BigDecimal(premium).setScale(2, BigDecimal.ROUND_HALF_EVEN));
//                                            else
//                                                benefit.setComputedPremium(benefit.getComputedPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN).add(memBenefits.getComputedPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN)));
//
//                                        }else
//                                            premium =0;
//                                    }
//
//
//                                }
//
//
//
//                                    prem = prem.add(new BigDecimal(premium).setScale(2, BigDecimal.ROUND_HALF_EVEN));
//                                    if(policyTrans.getMedicalCoverType()!=null && "I".equalsIgnoreCase(policyTrans.getMedicalCoverType())) {
//                                        if (!(prem.compareTo(BigDecimal.ZERO) == 0)) {
//
//                                            polAnnualPrem = polAnnualPrem.setScale(2, BigDecimal.ROUND_HALF_EVEN).add(annualPrem.setScale(2, BigDecimal.ROUND_HALF_EVEN));
//                                            if (prem.compareTo(BigDecimal.ZERO) == -1) {
//                                                polAnnualPrem = polAnnualPrem.negate();
//                                            }
//                                        }
//                                    }
//                                    else if("G".equalsIgnoreCase(policyTrans.getMedicalCoverType())){
//                                        if("P".equalsIgnoreCase(member.getDependentTypes())) {
//                                                polAnnualPrem = polAnnualPrem.setScale(2, BigDecimal.ROUND_HALF_EVEN).add(annualPrem.setScale(2, BigDecimal.ROUND_HALF_EVEN));
//                                                //if (prem.compareTo(BigDecimal.ZERO) == -1) {
//                                                   // polAnnualPrem = polAnnualPrem.negate();
//                                                //}
//                                            }
//                                        }
//
//                                    computedPrem=computedPrem.add(memBenefits.getComputedPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN));
//                                    memberBenefitsRepo.save(memBenefits);
//
//                   }
//
//                benefitRepo.save(benefits);
//                age = dateUtils.getAge(member.getClient().getDob());
//                member.setAge((long)age);
//                if(policyTrans.getMedicalCoverType()!=null && "I".equalsIgnoreCase(policyTrans.getMedicalCoverType())) {
//                    member.setPremium(prem);
//                    member.setComputedPremium(computedPrem);
//                }
//                else if("G".equalsIgnoreCase(policyTrans.getMedicalCoverType())){
//                    if("P".equalsIgnoreCase(member.getDependentTypes())) {
//
//                        member.setPremium(prem);
//                        member.setComputedPremium(computedPrem);
//                    }
//                }
////                if (policyTrans.getTransType().equalsIgnoreCase("AD")) {
////                    if (category.getComputedPremium() == null)
////                        category.setComputedPremium(member.getComputedPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN));
////                    else
////                    if(member.getComputedPremium()!=null) {
////                        category.setComputedPremium(category.getComputedPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN).add(member.getComputedPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN)));
////                    }
////                }
//
//                categoryPrem = categoryPrem.add(prem);
//
//            }
//            membersRepo.save(members);
////            categoryPrem=category.getComputedPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN).subtract(category.getPrevPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN));
//            BigDecimal loadingPrem = BigDecimal.ZERO;
//            for(CategoryLoadings loading:loadings){
//                BigDecimal divideRate = BigDecimal.valueOf(100);
//                    if(loading.getRateType()==null)
//                    {}
//                    else if("P".equalsIgnoreCase(loading.getRateType())){
//                           divideRate = BigDecimal.valueOf(100);
//                    }
//                   else if("M".equalsIgnoreCase(loading.getRateType())){
//                       divideRate = BigDecimal.valueOf(1000);
//                   }
//                   else if("A".equalsIgnoreCase(loading.getRateType())){
//                       // loadingPrem = loadingPrem.add(loading.getRate());
//                      //  continue;
//                        divideRate = BigDecimal.ONE;
//                   }
//                 if(divideRate.compareTo(BigDecimal.ONE)==0){
//                     loadingPrem = loadingPrem.add(loading.getRate());
//                 }
//                else  if(loading.getRate()!=null && loading.getRate().compareTo(BigDecimal.ZERO) ==1){
//                    loadingPrem = loadingPrem.add(loading.getRate().divide(divideRate).multiply(categoryPrem));
//                }
//                loading.setLoadingAmt(loadingPrem);
//            }
//            loadingRepo.save(loadings);
//            category.setLoadingPrem(loadingPrem);
//            categoryPrem = categoryPrem.add(loadingPrem);
//
//            category.setPremium(categoryPrem);
//            totalPrem = totalPrem.add(categoryPrem);
//            BigDecimal comm = totalPrem.multiply(rates).divide(new BigDecimal(100));
//            if(totalPrem.compareTo(BigDecimal.ZERO)==1){
//                comm = comm.negate();
//            }
//            else if(totalPrem.compareTo(BigDecimal.ZERO)==-1){
//                comm = comm.abs();
//            }
//           // totalCommission = totalCommission.add(comm);
//            totalCommission = comm;
//        }
//        medicalCategoryRepo.save(categories);
//        for(PolicyTaxes policyTax:policyTaxes){
//            BigDecimal computedTax = calculateTax(totalPrem, policyTax.getTaxRate(), policyTax.getDivFactor(),policyTax.getRateType());
//            BigDecimal computedAnnualTax = calculateTax(polAnnualPrem, policyTax.getTaxRate(), policyTax.getDivFactor(),policyTax.getRateType());
//            if(policyTax.getRevenueItems().getItem()== RevenueItems.EX){
//                polextras = polextras.add(computedTax);
//                polAnnualextras = computedAnnualTax.add(computedAnnualTax);
//                policyTax.setTaxAmount(polextras);
//            }
//            else if(policyTax.getRevenueItems().getItem() == RevenueItems.SD){
//                polstampDuty = polstampDuty.add(computedTax);
//                polAnnualstampDuty = polAnnualstampDuty.add(computedAnnualTax);
//                policyTax.setTaxAmount(polstampDuty);
//            }
//            else if(policyTax.getRevenueItems().getItem() == RevenueItems.PHCF){
//                polphfFund = polphfFund.add(computedTax);
//                polAnnualphfFund = polAnnualphfFund.add(computedAnnualTax);
//                policyTax.setTaxAmount(polphfFund);
//            }
//            else if(policyTax.getRevenueItems().getItem()==RevenueItems.TL){
//                polTl = polTl.add(computedTax);
//                polAnnualTl = polAnnualTl.add(computedAnnualTax);
//                policyTax.setTaxAmount(polTl);
//            }
//        }
//        long memberCount =0;
//        long memberCardCount =0;
//
////        boolean issueCard = policyTrans.getIssueCard()!=null && "Y".equalsIgnoreCase(policyTrans.getIssueCard());
//        boolean issueCard = true;
//        if (policyTrans.getBinCardType()==null)
//            issueCard = false;
//        BigDecimal issueCardFee = BigDecimal.ZERO;
//        BigDecimal serviceCharge = BigDecimal.ZERO;
//        if(issueCard ) {
//            if ( "NB".equalsIgnoreCase(policyTrans.getTransType())){
//                memberCount = membersRepo.count(QCategoryMembers.categoryMembers.category.policy.policyId.eq(polCode));
//                memberCardCount = membersRepo.count(QCategoryMembers.categoryMembers.category.policy.policyId.eq(polCode)
//                        .and(QCategoryMembers.categoryMembers.memberHasCard.eq("N")));
//            } else if ( "AD".equalsIgnoreCase(policyTrans.getTransType())) {
//                memberCount = membersRepo.count(QCategoryMembers.categoryMembers.category.policy.policyId.eq(polCode)
//                .and(QCategoryMembers.categoryMembers.memberStatus.eq("N")));
//                memberCardCount = membersRepo.count(QCategoryMembers.categoryMembers.category.policy.policyId.eq(polCode)
//                        .and(QCategoryMembers.categoryMembers.memberHasCard.eq("N"))
//                        .and(QCategoryMembers.categoryMembers.memberStatus.eq("N")));
//            }
////            if("P".equalsIgnoreCase(policyTrans.getCardType())){
////                issueCardFee = paramService.getParamValue("PHOTO_CARD_ISSUE_FEE");
////            }
////            else  if("E".equalsIgnoreCase(policyTrans.getCardType())){
////                issueCardFee = paramService.getParamValue("ELECTRONIC_CARD_ISSUE_FEE");
////                serviceCharge = paramService.getParamValue("SERVICE_CHARGE");
////            }
//
//            issueCardFee=policyTrans.getBinCardType().getCardFee();
//            if ("Y".equalsIgnoreCase(policyTrans.getBinCardType().getServiceProrated()))
//                serviceCharge= new BigDecimal((policyTrans.getBinCardType().getServiceCharge().setScale(2, BigDecimal.ROUND_HALF_EVEN).multiply(new BigDecimal(remainingPeriod).setScale(2, BigDecimal.ROUND_HALF_EVEN)).divide(new BigDecimal(yeardays),2,BigDecimal.ROUND_HALF_EVEN)).toPlainString());
//            else
//                serviceCharge=policyTrans.getBinCardType().getServiceCharge();
//            polIssueCard = issueCardFee.multiply(BigDecimal.valueOf(memberCardCount));
//            polServiceCharge = serviceCharge.multiply(BigDecimal.valueOf(memberCount));
//
////            for (PolicyTaxes polTaxes : memberTaxes) {
////                if (polTaxes.getRevenueItems().getItem().equals(RevenueItems.CF)) {
////                    polIssueCard = issueCardFee.multiply(BigDecimal.valueOf(memberCount));
////                    polTaxes.setTaxAmount(polIssueCard);
////                    polTaxes.setTaxRate(issueCardFee);
////                } else if ( polTaxes.getRevenueItems().getItem().equals(RevenueItems.SC)) {
////                    polServiceCharge = serviceCharge.multiply(BigDecimal.valueOf(memberCount));
////                    polTaxes.setTaxAmount(polServiceCharge);
////                    polTaxes.setTaxRate(serviceCharge);
////                }
////            }
//            polTaxesRepo.save(policyTaxes);
//
//
//            polVatAmt = polVatAmt.add(polServiceCharge.multiply(policyTrans.getAgent().getAccountType().getVatRate().divide(BigDecimal.valueOf(100))));
//            polVatAmt = polVatAmt.add(polIssueCard.multiply(policyTrans.getAgent().getAccountType().getVatRate().divide(BigDecimal.valueOf(100))));
//            policyTrans.setServiceCharge(polServiceCharge);
//            policyTrans.setIssueCardFee(polIssueCard);
//            policyTrans.setVatAmount(polVatAmt);
//        }
//        BigDecimal whtxAmt = BigDecimal.ZERO;
//        AccountTypes accType = policyTrans.getAgent().getAccountType();
//        if(accType.isWhtxAppl()){
//            if(accType.getWhtaxVal().compareTo(BigDecimal.ZERO)==1){
//                whtxAmt = accType.getWhtaxVal().divide(new BigDecimal(100)).multiply(totalCommission);
//                polwhtxAmt = whtxAmt.negate();
//            }
//        }
//
//        if(totalPrem.compareTo(BigDecimal.ZERO)==1){
//            totalCommission = totalCommission.abs().negate();
//        }
//        else if(totalPrem.compareTo(BigDecimal.ZERO)==-1){
//            totalCommission = totalCommission.abs();
//        }
//        Currencies currencies = policyTrans.getTransCurrency();
//        totalPrem = totalPrem.setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN);
//        totalCommission =totalCommission.setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN);
//        policyTrans.setPremium(totalPrem);
//        policyTrans.setBasicPrem((totalPrem.add(polextras).add(polstampDuty).add(polphfFund).add(polTl).add(polIssueCard).add(polServiceCharge).add(polVatAmt)).setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN));
//        policyTrans.setEndosbasicPremium(totalPrem);
//        policyTrans.setEndosCommissions(totalCommission);
//        policyTrans.setCommAmt(totalCommission);
//        policyTrans.setExtras(polextras);
//        policyTrans.setWhtx(polwhtxAmt);
//        policyTrans.setPhcf(polphfFund);
//        policyTrans.setTrainingLevy(polTl);
//        policyTrans.setTotTrainingLevy(polTl);
//        policyTrans.setTotPhcf(polphfFund);
//        policyTrans.setStampDuty(polstampDuty);
//        policyTrans.setSumInsured(BigDecimal.ZERO);
//        policyTrans.setNetPrem((totalPrem.add(polextras).add(polstampDuty).add(polphfFund).add(polTl).add(polwhtxAmt).add(totalCommission).add(polServiceCharge).add(polIssueCard).add(polVatAmt)).setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN));
//        if(policyTrans.isRenewable()) {
//            if (policyTrans.getMedicalCoverType() != null && "I".equalsIgnoreCase(policyTrans.getMedicalCoverType())) {
//                if (policyTrans.getPrevFuturePrem() == null) {
//                    policyTrans.setFuturePrem((totalPrem.add(polextras).add(polphfFund).add(polTl)).setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
//                } else {
//                    policyTrans.setFuturePrem(policyTrans.getPrevFuturePrem().setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN).add(polAnnualPrem.add(polAnnualextras).add(polAnnualphfFund).add(polAnnualTl)).setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
//                }
//            } else if ("G".equalsIgnoreCase(policyTrans.getMedicalCoverType())) {
//                policyTrans.setFuturePrem((polAnnualPrem.add(polAnnualextras).add(polAnnualphfFund).add(polAnnualTl)).setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
//            }
//        }
//        else
//            policyTrans.setFuturePrem(BigDecimal.ZERO);
//        PolicyTrans savedPolicy =  policyTransRepo.save(policyTrans);
//    }



    private BigDecimal calculateTax(BigDecimal premAmount, BigDecimal rate,
                                   BigDecimal divFactor,String rateType) {
        if("P".equalsIgnoreCase(rateType)){
            if(premAmount==null || premAmount.compareTo(BigDecimal.ZERO)==0){
                return BigDecimal.ZERO;
            }else
                return premAmount.multiply(rate).divide(divFactor);
        }

        else if("A".equalsIgnoreCase(rateType)){
            if(premAmount==null || premAmount.compareTo(BigDecimal.ZERO)==0){
                return BigDecimal.ZERO;
            }
            else {
                if(premAmount.compareTo(BigDecimal.ZERO)==1) return rate;
                else
                    return rate.negate();
            }

        }
        else return BigDecimal.ZERO;
    }
}