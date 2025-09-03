package com.brokersystems.brokerapp.quotes.services.impl;

import com.brokersystems.brokerapp.enums.RevenueItems;
import com.brokersystems.brokerapp.medical.model.BinderRatesTable;
import com.brokersystems.brokerapp.medical.model.QBinderRatesTable;
import com.brokersystems.brokerapp.medical.repository.BinderRatesTblRepo;
import com.brokersystems.brokerapp.quotes.model.*;
import com.brokersystems.brokerapp.quotes.repository.*;
import com.brokersystems.brokerapp.quotes.services.MedComputeQuotPrem;
import com.brokersystems.brokerapp.quotes.services.QuotationService;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.DateUtilities;
import com.brokersystems.brokerapp.server.utils.MedicalExcelUtils;
import com.brokersystems.brokerapp.setup.model.AccountTypes;
import com.brokersystems.brokerapp.setup.model.Currencies;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.brokersystems.brokerapp.uw.model.QPolicyTaxes;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by waititu on 17/08/2017.
 */
@Service
public class MedComputeQuotPremImpl implements MedComputeQuotPrem {
    @Autowired
    private QuotTransRepo quotTransRepo;

    @Autowired
    private BinderRatesTblRepo ratesTblRepo;

    @Autowired
    private MedicalQuoteCategoryRepo medicalCategoryRepo;

    @Autowired
    private MedQuotCategoryBenefitsRepo benefitRepo;

    @Autowired
    private MedicalExcelUtils excelUtils;

    @Autowired
    private DateUtilities dateUtils;

    @Autowired
    private MedicalQuoteTaxesRepo quotTaxesRepo;

    @Autowired
    private QuotationService quotationService;

    @Autowired
    private MedQuotCatFamilyDetailsRepo familyDetailsRepo;

    @Autowired
    private PolicyTransService policyService;

    //@Autowired
   // private MedicalTransService transService;


    @Autowired
    private ParamService paramService;

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class,IOException.class })
    public void computePrem(Long quotCode) throws BadRequestException,FileNotFoundException,IOException {
        if(quotCode==null) throw new BadRequestException("No policy Details to compute premium");
        QuoteTrans quoteTrans = quotTransRepo.findOne(quotCode);
        boolean fundPolicy = quoteTrans.getBinder().getFundBinder()!=null && "Y".equalsIgnoreCase(quoteTrans.getBinder().getFundBinder());
        if(!fundPolicy)
            quotationService.populateCategoryTaxes(quoteTrans);
        String tableRateType = "";
        if(quoteTrans.getMedicalCoverType()!=null){
            tableRateType = quoteTrans.getMedicalCoverType();
        }
        Iterable<MedicalQuoteTaxes> quoteTaxes = quotTaxesRepo.findAll(QMedicalQuoteTaxes.medicalQuoteTaxes.quotation.quoteId.eq(quoteTrans.getQuoteId()).and(QMedicalQuoteTaxes.medicalQuoteTaxes.taxLevel.eq("P")));
        Iterable<MedicalQuoteTaxes> memberTaxes = quotTaxesRepo.findAll(QMedicalQuoteTaxes.medicalQuoteTaxes.quotation.quoteId.eq(quoteTrans.getQuoteId()).
                and(QMedicalQuoteTaxes.medicalQuoteTaxes.taxLevel.eq("R")));
        Iterable<BinderRatesTable> ratesTables = ratesTblRepo.findAll(QBinderRatesTable.binderRatesTable.binder.binId.eq(quoteTrans.getBinder().getBinId())
                .and(QBinderRatesTable.binderRatesTable.tableType.eq(tableRateType)) );
        if(!fundPolicy)
            if(ratesTables.spliterator().getExactSizeIfKnown()==0) throw new BadRequestException("Rates Table for the Binder has not been setup");
        Calendar cal = Calendar.getInstance();
        cal.set(1970,01,01);
        Date maxDate = cal.getTime();
        for (BinderRatesTable ratesTable : ratesTables) {
            if(ratesTable.getEffDate().after(maxDate)){
                maxDate = ratesTable.getEffDate();
            }
        }

        BinderRatesTable rateTable = ratesTblRepo.findOne(QBinderRatesTable.binderRatesTable.binder.binId.eq(quoteTrans.getBinder().getBinId())
                .and(QBinderRatesTable.binderRatesTable.effDate.eq(maxDate))
                .and(QBinderRatesTable.binderRatesTable.tableType.eq(tableRateType)));
        if(!fundPolicy) {
            if (rateTable.getRate_table().length == 0)
                throw new BadRequestException("Rates Table for the Binder has not been setup");
            try (
                    ByteArrayInputStream in = new ByteArrayInputStream(rateTable.getRate_table());
                    FileOutputStream out = new FileOutputStream(rateTable.getFileName())) {
                IOUtils.copy(in, out);
            }
        }
        Iterable<MedicalQuoteCategory> categories = medicalCategoryRepo.findAll(QMedicalQuoteCategory.medicalQuoteCategory.binderDetails.binder.binId.eq(quoteTrans.getBinder().getBinId())
                .and(QMedicalQuoteCategory.medicalQuoteCategory.quotation.quoteId.eq(quotCode)));
        BigDecimal totalPrem = BigDecimal.ZERO;
        BigDecimal totalCommission =BigDecimal.ZERO;
        BigDecimal polstampDuty = BigDecimal.ZERO;
        BigDecimal polphfFund = BigDecimal.ZERO;
        BigDecimal polwhtxAmt = BigDecimal.ZERO;
        BigDecimal polextras = BigDecimal.ZERO;
        BigDecimal polTl = BigDecimal.ZERO;
        BigDecimal polIssueCard = BigDecimal.ZERO;
        BigDecimal polServiceCharge = BigDecimal.ZERO;
        BigDecimal polVatAmt = BigDecimal.ZERO;
        long memberCount = 0;
        long memberCount4Srvchrg= 0;
        for(MedicalQuoteCategory category:categories){
            BigDecimal rates = policyService.getCommissionRate(category.getBinderDetails().getDetId());
            BigDecimal categoryPrem = BigDecimal.ZERO;
           Iterable<MedQuotCategoryBenefits> benefits = benefitRepo.findAll(QMedQuotCategoryBenefits.medQuotCategoryBenefits.category.id.eq(category.getId()));
            for(MedQuotCategoryBenefits benefit:benefits){
                benefit.setPremium(BigDecimal.ZERO);
            }

            benefitRepo.save(benefits);
            if (quoteTrans.getMedicalCoverType()!=null ){
            if("I".equalsIgnoreCase(quoteTrans.getMedicalCoverType())) {
                String principalGender = category.getPrincipalGender().toString();
                String spouseGender=                    "";
                if(!principalGender.isEmpty()) {
                    if (principalGender.equals("M"))
                        spouseGender = "F";
                    else
                        spouseGender = "M";
                }
                if(category.getPrincipalAge()!=null){
                    int age = category.getPrincipalAge().intValue();
                    BigDecimal prem = BigDecimal.ZERO;
                    memberCount = 1;
                    for(MedQuotCategoryBenefits benefit:benefits){
                        double limitAmount =0;
                        if(benefit.getLimit()!=null){
                            if(benefit.getLimit().getLimitAmount()!=null)
                                limitAmount = benefit.getLimit().getLimitAmount().doubleValue();
                        }
                        String dependentType = "";
                        if(quoteTrans.getMedicalCoverType()!=null && "I".equalsIgnoreCase(quoteTrans.getMedicalCoverType())) {
                            dependentType = "Principal";
                        }
                        double premium = 0;
                        boolean gender = benefit.getCover().isDependsOnGender();
                        if(gender){
                            if(!benefit.getCover().getGender().equalsIgnoreCase(principalGender)){
                                    continue;
                                }
                        }

                         if(!fundPolicy) {
                                premium = excelUtils.getPremium(age, limitAmount,
                                     dependentType, benefit.getCover().getSection().getShtDesc(),
                                     rateTable.getFileName());
                            }
                        if(premium==0){
                            if(!fundPolicy) {
                                premium = excelUtils.getPremium(age, limitAmount,
                                        "P", benefit.getCover().getSection().getShtDesc(),
                                        rateTable.getFileName());
                            }
                        }
                        benefit.setPremium(benefit.getPremium().add(new BigDecimal(premium)));
                        prem = prem.add(new BigDecimal(premium));

                    }
                    benefitRepo.save(benefits);
                    if (category.getSpouseAge()!=null) {
                         memberCount = memberCount+1;
                        Iterable<MedQuotCategoryBenefits> benefits1 = benefitRepo.findAll(QMedQuotCategoryBenefits.medQuotCategoryBenefits.category.id.eq(category.getId()));

                        age = category.getSpouseAge().intValue();
                       // prem = BigDecimal.ZERO;
                        for(MedQuotCategoryBenefits benefit:benefits1){
                            double limitAmount =0;
                            if(benefit.getLimit()!=null){
                                if(benefit.getLimit().getLimitAmount()!=null)
                                    limitAmount = benefit.getLimit().getLimitAmount().doubleValue();
                            }
                            String dependentType = "";
                            if(quoteTrans.getMedicalCoverType()!=null && "I".equalsIgnoreCase(quoteTrans.getMedicalCoverType())) {
                                dependentType = "Spouse";
                            }
                            double premium = 0;
                            boolean gender = benefit.getCover().isDependsOnGender();
                            if(gender) {
                                if (!benefit.getCover().getGender().equalsIgnoreCase(spouseGender)) {
                                    continue;
                                }
                            }
                            if(!fundPolicy) {
                                premium = excelUtils.getPremium(age, limitAmount,
                                        dependentType, benefit.getCover().getSection().getShtDesc(),
                                        rateTable.getFileName());
                            }
                            if(premium==0){
                                if(!fundPolicy) {
                                    premium = excelUtils.getPremium(age, limitAmount,
                                            "S", benefit.getCover().getSection().getShtDesc(),
                                            rateTable.getFileName());
                                }
                            }
                            benefit.setPremium(benefit.getPremium().add(new BigDecimal(premium)));
                            prem = prem.add(new BigDecimal(premium));

                        }
                        benefitRepo.save(benefits1);
                    }
                    if (category.getChildrenCount()!=null) {
                        age = 16;
                        //prem = BigDecimal.ZERO;
                        memberCount = memberCount+category.getChildrenCount().intValue();
                        Iterable<MedQuotCategoryBenefits> benefits1 = benefitRepo.findAll(QMedQuotCategoryBenefits.medQuotCategoryBenefits.category.id.eq(category.getId()));
                        for(MedQuotCategoryBenefits benefit:benefits1){
                            double limitAmount =0;
                            if(benefit.getLimit()!=null){
                                if(benefit.getLimit().getLimitAmount()!=null)
                                    limitAmount = benefit.getLimit().getLimitAmount().doubleValue();
                            }
                            String dependentType = "";
                            if(quoteTrans.getMedicalCoverType()!=null && "I".equalsIgnoreCase(quoteTrans.getMedicalCoverType())) {
                                dependentType = "Child";
                            }
                            double premium = 0;
                            boolean gender = benefit.getCover().isDependsOnGender();
                            if(gender) {
                                    continue;
                            }
                            if(!fundPolicy) {
                                premium = excelUtils.getPremium(age, limitAmount,
                                        dependentType, benefit.getCover().getSection().getShtDesc(),
                                        rateTable.getFileName());
                                premium=premium*category.getChildrenCount().intValue();
                            }
                            if(premium==0){
                                if(!fundPolicy) {
                                    premium = excelUtils.getPremium(age, limitAmount,
                                            "C", benefit.getCover().getSection().getShtDesc(),
                                            rateTable.getFileName());
                                    premium=premium*category.getChildrenCount().intValue();
                                }
                            }
                            benefit.setPremium(benefit.getPremium().add(new BigDecimal(premium)));
                            prem = prem.add(new BigDecimal(premium));

                        }
                        benefitRepo.save(benefits1);
                    }
                    categoryPrem = categoryPrem.add(prem);


                }
                memberCount4Srvchrg =memberCount;
            }else {
                BigDecimal prem = BigDecimal.ZERO;
                Iterable<MedQuotCatFamilyDetails> famDtls = familyDetailsRepo.findAll(QMedQuotCatFamilyDetails.medQuotCatFamilyDetails.category.id.eq(category.getId()));
                for (MedQuotCatFamilyDetails famSize:famDtls) {
                    BigDecimal famPrem = BigDecimal.ZERO;
                    int famsize =famSize.getFamilies().intValue();
                    int oldestage = Integer.MIN_VALUE;
                    String value = "";
                    String value2 = "";
                    String minage = "";
                    String dependantCount = "";
                    String maxage = "";
                    value=famSize.getAgebracket().toString();
                    if(value.indexOf("-") > 0){
                        String[] arr = value.split("-");
                        minage = arr[0].trim();
                        maxage = arr[1].trim();
                    }
                    value2 =famSize.getFamilySize().toString();
                    if(value2.indexOf("+") > 0){
                        String[] arr = value2.split("\\+");
                        dependantCount = arr[1].trim();
                        int dependantCount1 = Integer.parseInt(dependantCount) ;
                        memberCount4Srvchrg = memberCount4Srvchrg + ((dependantCount1+1)*famsize);
                    } else {
                        memberCount4Srvchrg = memberCount4Srvchrg + (1*famsize);
                    }
                    int calcAge = Integer.parseInt(maxage) ;
                        if(calcAge > oldestage)
                            oldestage = calcAge;
                    String dependentType = famSize.getFamilySize();
                    if (famSize.getRequireCards()!=null){
                        memberCount = memberCount+famSize.getRequireCards();
                    }

                    Iterable<MedQuotCategoryBenefits> benefits1 = benefitRepo.findAll(QMedQuotCategoryBenefits.medQuotCategoryBenefits.category.id.eq(category.getId()));
                    for (MedQuotCategoryBenefits benefit : benefits1) {
                        double premium = 0;
                        double limitAmount =0;
                        if(benefit.getLimit()!=null){
                            if(benefit.getLimit().getLimitAmount()!=null)
                                limitAmount = benefit.getLimit().getLimitAmount().doubleValue();
                        }
                        if(!fundPolicy) {
                            premium = excelUtils.getPremium(oldestage, limitAmount,
                                    dependentType, benefit.getCover().getSection().getShtDesc(),
                                    rateTable.getFileName());
                            premium =premium*famsize;
                        }
                        if(premium==0){
                            if(!fundPolicy) {
                                premium = excelUtils.getPremium(oldestage, limitAmount,
                                        "P", benefit.getCover().getSection().getShtDesc(),
                                        rateTable.getFileName());
                                premium =premium*famsize;
                            }
                        }
                        benefit.setPremium(benefit.getPremium().add(new BigDecimal(premium)));
                        prem = prem.add(new BigDecimal(premium));
                        famPrem=famPrem.add(new BigDecimal(premium));
                    }
                    benefitRepo.save(benefits1);
                   famSize.setPremium(famPrem);
                }

                familyDetailsRepo.save(famDtls);
                categoryPrem = categoryPrem.add(prem);
            }

            }
            category.setPremium(categoryPrem);
            totalPrem = totalPrem.add(categoryPrem);
            BigDecimal comm = totalPrem.multiply(rates).divide(new BigDecimal(100));
            if(totalPrem.compareTo(BigDecimal.ZERO)==1){
                comm = comm.negate();
            }
            else if(totalPrem.compareTo(BigDecimal.ZERO)==-1){
                comm = comm.abs();
            }
            totalCommission = totalCommission.add(comm);
        }
        medicalCategoryRepo.save(categories);
        for(MedicalQuoteTaxes quotTax:quoteTaxes){
            BigDecimal computedTax = calculateTax(totalPrem, quotTax.getTaxRate(), quotTax.getDivFactor(),quotTax.getRateType());
//            if(quotTax.getRevenueItems().getItem()== RevenueItems.EX){
//                polextras = polextras.add(computedTax);
//                quotTax.setTaxAmount(polextras);
//            }
           // else
                if(quotTax.getRevenueItems().getItem() == RevenueItems.SD){
                polstampDuty = polstampDuty.add(computedTax);
                quotTax.setTaxAmount(polstampDuty);
            }
            else if(quotTax.getRevenueItems().getItem() == RevenueItems.PHCF){
                polphfFund = polphfFund.add(computedTax);
                quotTax.setTaxAmount(polphfFund);
            }
            else if(quotTax.getRevenueItems().getItem()==RevenueItems.TL){
                polTl = polTl.add(computedTax);
                quotTax.setTaxAmount(polTl);
            }
        }

        BigDecimal whtxAmt = BigDecimal.ZERO;
        AccountTypes accType = quoteTrans.getBinder().getAccount().getAccountType();
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
        boolean issueCard = true;
        if (quoteTrans.getBinCardType()==null)
            issueCard = false;
        BigDecimal issueCardFee = BigDecimal.ZERO;
        BigDecimal serviceCharge = BigDecimal.ZERO;
        if(issueCard ) {
//            if("P".equalsIgnoreCase(quoteTrans.getCardType())){
//                issueCardFee = paramService.getParamValue("PHOTO_CARD_ISSUE_FEE");
//            }
//            else  if("E".equalsIgnoreCase(quoteTrans.getCardType())){
//                issueCardFee = paramService.getParamValue("ELECTRONIC_CARD_ISSUE_FEE");
//                serviceCharge = paramService.getParamValue("SERVICE_CHARGE");
//            }
            issueCardFee=quoteTrans.getBinCardType().getCardFee();
            serviceCharge=quoteTrans.getBinCardType().getServiceCharge();
            polIssueCard = issueCardFee.multiply(BigDecimal.valueOf(memberCount));
            polServiceCharge = serviceCharge.multiply(BigDecimal.valueOf(memberCount4Srvchrg));
//            for (MedicalQuoteTaxes polTaxes : memberTaxes) {
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
            quotTaxesRepo.save(quoteTaxes);
            if(quoteTrans.getBinCardType().getVatApplicable()!=null) {
                if ("Y".equalsIgnoreCase(quoteTrans.getBinCardType().getVatApplicable())) {
                    polVatAmt = polVatAmt.add(polServiceCharge.multiply(quoteTrans.getBinder().getAccount().getAccountType().getVatRate().divide(BigDecimal.valueOf(100))));
                    polVatAmt = polVatAmt.add(polIssueCard.multiply(quoteTrans.getBinder().getAccount().getAccountType().getVatRate().divide(BigDecimal.valueOf(100))));
                }
            }
            quoteTrans.setServiceCharge(polServiceCharge);
            quoteTrans.setIssueCardFee(polIssueCard);
            quoteTrans.setVatAmount(polVatAmt);
        }

        Currencies currencies = quoteTrans.getTransCurrency();
        totalPrem = totalPrem.setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN);

        quoteTrans.setPremium(totalPrem);
        quoteTrans.setBasicPrem((totalPrem.add(polextras).add(polstampDuty).add(polphfFund).add(polTl).add(polIssueCard).add(polServiceCharge).add(polVatAmt)).setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN));
        quoteTrans.setCommAmt(totalCommission.setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN));
        quoteTrans.setExtras(polextras);
        quoteTrans.setWhtx(polwhtxAmt);
        quoteTrans.setPhcf(polphfFund);
        quoteTrans.setTrainingLevy(polTl);
        quoteTrans.setStampDuty(polstampDuty);
        quoteTrans.setSumInsured(BigDecimal.ZERO);
        quoteTrans.setNetPrem((totalPrem.add(polextras).add(polstampDuty).add(polphfFund).add(polTl).add(polwhtxAmt).add(totalCommission).add(polServiceCharge).add(polIssueCard).add(polVatAmt)).setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN));
        QuoteTrans savedPolicy =  quotTransRepo.save(quoteTrans);
    }

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
