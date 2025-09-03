package com.brokersystems.brokerapp.bulktransactions.service.impl;

import com.brokersystems.brokerapp.bulktransactions.service.BulkPremComputeService;
import com.brokersystems.brokerapp.enums.RevenueItems;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.Currencies;
import com.brokersystems.brokerapp.setup.model.QTaxRates;
import com.brokersystems.brokerapp.setup.model.TaxRates;
import com.brokersystems.brokerapp.setup.repository.TaxRatesRepo;
import com.brokersystems.brokerapp.uw.dtos.RiskTransDTO;
import com.brokersystems.brokerapp.uw.model.PolicyTaxes;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.model.QRiskTrans;
import com.brokersystems.brokerapp.uw.model.RiskTrans;
import com.brokersystems.brokerapp.uw.repository.PolTaxesRepo;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import com.brokersystems.brokerapp.uw.repository.RiskTransRepo;
import com.brokersystems.brokerapp.uw.service.PremComputeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BulkPremComputeServiceImpl implements BulkPremComputeService {

    @Autowired
    private PolicyTransRepo policyRepo;
    @Autowired
    private RiskTransRepo riskRepo;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PolTaxesRepo polTaxesRepo;

    @Autowired
    private PremComputeService premiumService;

    @Autowired
    private TaxRatesRepo taxRatesRepo;

    @Autowired
    private PremComputeService premComputeServiceImpl;

    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void computeUploadLifePrem(Long polCode) throws BadRequestException, IOException {
        PolicyTrans policy = policyRepo.findOne(polCode);
        Long polId = policy.getPolicyId();
        Iterable<RiskTrans> risks = riskRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(polId));
        BigDecimal totalSumInsured = BigDecimal.ZERO;
        BigDecimal totalCommission = BigDecimal.ZERO;
        BigDecimal polstampDuty = BigDecimal.ZERO;
        BigDecimal polphfFund = BigDecimal.ZERO;
        BigDecimal polwhtxAmt = BigDecimal.ZERO;
        BigDecimal polextras = BigDecimal.ZERO;
        BigDecimal polTl = BigDecimal.ZERO;
        BigDecimal totalPrem = BigDecimal.ZERO;

        for (RiskTrans risk : risks) {
            BigDecimal riskPrem = risk.getPremium();
            BigDecimal sumInsured = risk.getSumInsured();
            risk.setPremium(riskPrem);
            risk.setSumInsured(sumInsured);
            BigDecimal comm = BigDecimal.ZERO;
            if (risk.getCommRate() != null) {
                comm = riskPrem.multiply(risk.getCommRate()).divide(BigDecimal.valueOf(100));
            }

            if (riskPrem.compareTo(BigDecimal.ZERO) == 1) {
                comm = comm.negate();
            } else if (riskPrem.compareTo(BigDecimal.ZERO) == -1) {
                comm = comm.abs();
            }
            risk.setCommAmt(comm);
            totalCommission = totalCommission.add(comm);
            BigDecimal riskstampDuty = BigDecimal.ZERO;
            BigDecimal riskphfFund = BigDecimal.ZERO;
            BigDecimal riskwhtxAmt = BigDecimal.ZERO;
            BigDecimal riskextras = BigDecimal.ZERO;
            BigDecimal riskTl = BigDecimal.ZERO;
            risk.setNetpremium(riskPrem);
            risk.setExtras(riskextras);
            risk.setWhtax(riskwhtxAmt);
            risk.setPhfFund(riskphfFund);
            risk.setTrainingLevy(riskTl);
            risk.setStampDuty(riskstampDuty);
            risk.setFuturePrem(riskPrem);
            risk.setCalcPremium(riskPrem);
            risk.setNetpremium(riskPrem);
            totalPrem = totalPrem.add(riskPrem);
            totalSumInsured = totalSumInsured.add(sumInsured);
        }

        if (totalPrem.compareTo(BigDecimal.ZERO) == 1) {
            totalCommission = totalCommission.abs().negate();
        } else if (totalPrem.compareTo(BigDecimal.ZERO) == -1) {
            totalCommission = totalCommission.abs();
        }

        riskRepo.save(risks);

        Currencies currencies = policy.getTransCurrency();
        totalPrem = totalPrem.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN);
        totalCommission = totalCommission.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN);
        totalSumInsured = totalSumInsured.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN);
        policy.setPremium(totalPrem);
        policy.setBasicPrem(totalPrem);
        policy.setEndosbasicPremium(totalPrem);
        policy.setEndosCommissions(totalCommission);
        policy.setCommAmt(totalCommission);
        policy.setExtras(polextras.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setWhtx(polwhtxAmt.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setPhcf(polphfFund.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setTrainingLevy(polTl.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setTotTrainingLevy(polTl.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setTotPhcf(polphfFund.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setStampDuty(polstampDuty.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setSumInsured(totalSumInsured);
        policy.setNetPrem(totalPrem);
        if (policy.isRenewable())
            policy.setFuturePrem((totalPrem.add(polextras).add(polphfFund).add(polTl)).setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        else
            policy.setFuturePrem(BigDecimal.ZERO);
        policyRepo.save(policy);
    }

    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class},propagation = Propagation.REQUIRED)
    public void computeUploadGeneralPrem(Long polCode) throws BadRequestException, IOException {
                PolicyTrans policy = policyRepo.findOne(polCode);
        Long polId = policy.getPolicyId();
        Iterable<RiskTrans> riskss = riskRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(polId));
        System.out.println("risks count "+riskss.spliterator().getExactSizeIfKnown());
        BigDecimal totalSumInsured = BigDecimal.ZERO;
        BigDecimal totalCommission = BigDecimal.ZERO;
        BigDecimal totalPrem = BigDecimal.ZERO;
        BigDecimal totalBasicPrem = BigDecimal.ZERO;

        for (RiskTrans risk : riskss) {
            BigDecimal riskNetPrem = risk.getNetpremium();
            BigDecimal sumInsured = risk.getSumInsured();
            BigDecimal basicPrem = (riskNetPrem.multiply(BigDecimal.valueOf(100))).divide(BigDecimal.valueOf(100.45), 2, BigDecimal.ROUND_HALF_EVEN);
            risk.setPremium(basicPrem);
            risk.setSumInsured(sumInsured);
            BigDecimal comm = BigDecimal.ZERO;
            if (risk.getCommRate() != null) {
                comm = basicPrem.multiply(risk.getCommRate()).divide(BigDecimal.valueOf(100));
            }

            if (basicPrem.compareTo(BigDecimal.ZERO) == 1) {
                comm = comm.negate();
            } else if (basicPrem.compareTo(BigDecimal.ZERO) == -1) {
                comm = comm.abs();
            }
            risk.setCommAmt(comm);
            totalCommission = totalCommission.add(comm);
            BigDecimal riskstampDuty = BigDecimal.ZERO;
            BigDecimal riskphfFund = basicPrem.multiply(BigDecimal.valueOf(0.25));
            BigDecimal riskwhtxAmt = BigDecimal.ZERO;
            BigDecimal riskextras = BigDecimal.ZERO;
            BigDecimal riskTl = basicPrem.multiply(BigDecimal.valueOf(0.2));
            risk.setExtras(riskextras);
            risk.setWhtax(riskwhtxAmt);
            risk.setPhfFund(riskphfFund);
            risk.setTrainingLevy(riskTl);
            risk.setStampDuty(riskstampDuty);
            risk.setFuturePrem(basicPrem);
            risk.setCalcPremium(basicPrem);
            totalPrem = totalPrem.add(riskNetPrem);
            totalSumInsured = totalSumInsured.add(sumInsured);
            totalBasicPrem = totalBasicPrem.add(basicPrem);

        }

        if (totalPrem.compareTo(BigDecimal.ZERO) == 1) {
            totalCommission = totalCommission.abs().negate();
        } else if (totalPrem.compareTo(BigDecimal.ZERO) == -1) {
            totalCommission = totalCommission.abs();
        }

        riskRepo.save(riskss);
        Set<PolicyTaxes> policyTaxes = new HashSet<>();
        for (RiskTrans risk : riskss) {
            Iterable<TaxRates> taxRates = taxRatesRepo.findAll((QTaxRates.taxRates.active.eq(true).and(QTaxRates.taxRates.mandatory.eq(Boolean.TRUE))).and(QTaxRates.taxRates.subclass.subId.eq(risk.getSubclass().getSubId()))
                    .and(QTaxRates.taxRates.productsDef.proCode.eq(policy.getProduct().getProCode())));
            System.out.println("Tax found..."+taxRates.spliterator().getExactSizeIfKnown());
            for (TaxRates tax : taxRates) {
                PolicyTaxes policyTax = new PolicyTaxes();
                policyTax.setPolicy(policy);
                policyTax.setRateType(tax.getRateType());
                policyTax.setRevenueItems(tax.getRevenueItems());
                policyTax.setSubclass(tax.getSubclass());
                policyTax.setTaxLevel(tax.getTaxLevel());
                policyTax.setTaxRate(tax.getTaxRate());
                policyTax.setDivFactor(tax.getDivFactor());
                policyTax.setTaxAmount(premiumService.calculateTax(policy.getBasicPrem(), tax.getTaxRate(), tax.getDivFactor(), tax.getRateType()));
                policyTaxes.add(policyTax);
            }
        }
        polTaxesRepo.save(policyTaxes);
        BigDecimal polstampDuty = BigDecimal.ZERO;
        BigDecimal polphfFund = BigDecimal.ZERO;
        BigDecimal polTl = BigDecimal.ZERO;
        for (PolicyTaxes policyTax : policyTaxes) {
            BigDecimal computedTax = premiumService.calculateTax(policy.getBasicPrem(), policyTax.getTaxRate(), policyTax.getDivFactor(),
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
        Currencies currencies = policy.getTransCurrency();
        policy.setPhcf(polphfFund.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setTrainingLevy(polTl.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setTotTrainingLevy(polTl.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setTotPhcf(polphfFund.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setTotTrainingLevy(polTl.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        policy.setStampDuty(polstampDuty);
        policy.setFuturePrem(BigDecimal.ZERO);
        policyRepo.save(policy);


//
//        Currencies currencies = policy.getTransCurrency();
//        totalPrem = totalPrem.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN);
//        totalBasicPrem = totalBasicPrem.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN);
//        totalCommission = totalCommission.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN);
//        totalSumInsured = totalSumInsured.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN);
//        policy.setPremium(totalBasicPrem);
//        policy.setBasicPrem(totalPrem);
//        policy.setEndosbasicPremium(totalBasicPrem);
//        policy.setEndosgrossPremium(totalPrem);
//        policy.setEndosCommissions(totalCommission);
//        policy.setCommAmt(totalCommission);
//        policy.setExtras(polextras.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
//        policy.setWhtx(polwhtxAmt.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
//        policy.setPhcf(polphfFund.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
//        policy.setTrainingLevy(polTl.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
//        policy.setTotTrainingLevy(polTl.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
//        policy.setTotPhcf(polphfFund.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
//        policy.setStampDuty(polstampDuty.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
//        policy.setSumInsured(totalSumInsured);
//        policy.setNetPrem(totalBasicPrem);
//        if (policy.isRenewable())
//            policy.setFuturePrem((totalPrem.add(polextras).add(polphfFund).add(polTl)).setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
//        else
//            policy.setFuturePrem(BigDecimal.ZERO);
//        policyRepo.save(policy);
    }
}
