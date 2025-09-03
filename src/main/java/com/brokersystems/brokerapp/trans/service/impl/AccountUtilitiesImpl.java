package com.brokersystems.brokerapp.trans.service.impl;

import com.brokersystems.brokerapp.accounts.model.CoaSubAccounts;
import com.brokersystems.brokerapp.accounts.repository.CoaSubAccountsRepo;
import com.brokersystems.brokerapp.enums.RevenueItems;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.ProductsDef;
import com.brokersystems.brokerapp.setup.model.QRevenueItemsDef;
import com.brokersystems.brokerapp.setup.model.RevenueItemsDef;
import com.brokersystems.brokerapp.setup.repository.ProductsRepo;
import com.brokersystems.brokerapp.setup.repository.RevenueItemsRepo;
import com.brokersystems.brokerapp.trans.service.AccountsUtilities;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.model.RiskTrans;
import com.brokersystems.brokerapp.uw.repository.RiskTransRepo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 8/9/2017.
 */
@Service
public class AccountUtilitiesImpl implements AccountsUtilities {

    @Autowired
    private RevenueItemsRepo revenueItemsRepo;


    @Autowired
    private RiskTransRepo riskTransRepo;

    @Override
    public CoaSubAccounts getGlDebitAccount(RevenueItems item) throws BadRequestException {
        long count = revenueItemsRepo.count(QRevenueItemsDef.revenueItemsDef.item.eq(item));
        if(count ==0) throw new BadRequestException("Revenue Item not defined for "+item.getValue());
        RevenueItemsDef revenueItemsDef = null;
        if(count >= 1) {
            Iterable<RevenueItemsDef> revenueItemsDefs = revenueItemsRepo.findAll(QRevenueItemsDef.revenueItemsDef.item.eq(item));
            for(RevenueItemsDef itemsDef:revenueItemsDefs){
                 revenueItemsDef = itemsDef;
                  break;
            }
        }
        if(revenueItemsDef==null)throw new BadRequestException("Revenue Item not defined for "+item.getValue());
        if(revenueItemsDef.getDrAccount()==null) throw new BadRequestException("An Account has not been mapped for "+item.getValue());
        return revenueItemsDef.getDrAccount();
    }

    @Override
    public CoaSubAccounts getGlDebitAccount(RevenueItems item, Long  subCode) throws BadRequestException {
        long count = revenueItemsRepo.count(QRevenueItemsDef.revenueItemsDef.item.eq(item).and(QRevenueItemsDef.revenueItemsDef.subClassDef.isNull()));
        //if (count > 1) throw new BadRequestException("Duplicate Accounts mapping for " + item.getValue());
        System.out.println("Count...1."+count+" Sub class "+subCode+" Revenue Item "+item.getValue());
        RevenueItemsDef revenueItemsDef = null;
        String level = "P";
        if(count==1) {
            level = "P";
            revenueItemsDef =  revenueItemsRepo.findOne(QRevenueItemsDef.revenueItemsDef.item.eq(item).and(QRevenueItemsDef.revenueItemsDef.subClassDef.isNull()));
           if (revenueItemsDef != null) {
               CoaSubAccounts drAccount = revenueItemsDef.getDrAccount();
               drAccount.setApplLevel(level);
               return drAccount;
           }
       }
        if(count!=1 && subCode!=null) {
//            if (subCode == null) throw new BadRequestException("Sub Class Cannot be null...");
            count = revenueItemsRepo.count(QRevenueItemsDef.revenueItemsDef.item.eq(item).and(QRevenueItemsDef.revenueItemsDef.prodGroup.subId.eq(subCode)));
            System.out.println("Count...2."+count+" Sub class "+subCode);
            revenueItemsDef = revenueItemsRepo.findOne(QRevenueItemsDef.revenueItemsDef.item.eq(item).and(QRevenueItemsDef.revenueItemsDef.prodGroup.subId.eq(subCode)));
            level = "R";
        }
        if (count > 1) throw new BadRequestException("Duplicate Accounts mapping for " + item.getValue()+" COUNT ..."+count);
        if (revenueItemsDef!=null && revenueItemsDef.getDrAccount() == null)
            throw new BadRequestException("An Account has not been mapped for " + item.getValue());

        if(subCode!=null && revenueItemsDef!=null) {
            if (count == 0) throw new BadRequestException("Revenue Item not defined for " + item.getValue());
            CoaSubAccounts drAccount = revenueItemsDef.getDrAccount();
            drAccount.setApplLevel(level);
            return drAccount;
        }
        else return  null;
    }

    @Override
    public CoaSubAccounts getGlCreditAccount(RevenueItems item, Long subCode) throws BadRequestException {
        long count = revenueItemsRepo.count(QRevenueItemsDef.revenueItemsDef.item.eq(item).and(QRevenueItemsDef.revenueItemsDef.subClassDef.isNull()));
        String level = "P";
        RevenueItemsDef revenueItemsDef = null;
        if (count== 1) {
             level = "P";
             revenueItemsDef = revenueItemsRepo.findOne(QRevenueItemsDef.revenueItemsDef.item.eq(item).and(QRevenueItemsDef.revenueItemsDef.subClassDef.isNull()));
            if (revenueItemsDef != null) {
                CoaSubAccounts drAccount = revenueItemsDef.getCrAccount();
                if(drAccount!=null){
                drAccount.setApplLevel(level);
                }
                return drAccount;
            }
        }
        if(count!=1 && subCode!=null) {
//            if (subCode == null) throw new BadRequestException("Sub Class Cannot be null...");
            count = revenueItemsRepo.count(QRevenueItemsDef.revenueItemsDef.item.eq(item).and(QRevenueItemsDef.revenueItemsDef.prodGroup.subId.eq(subCode)));
            revenueItemsDef = revenueItemsRepo.findOne(QRevenueItemsDef.revenueItemsDef.item.eq(item).and(QRevenueItemsDef.revenueItemsDef.prodGroup.subId.eq(subCode)));
            level = "R";
        }
        if (count > 1) throw new BadRequestException("Duplicate Accounts mapping for " + item.getValue());
        if (revenueItemsDef!=null && (revenueItemsDef.getDrAccount() == null && revenueItemsDef.getCrAccount()==null))
            throw new BadRequestException("An Account has not been mapped for " + item.getValue());

        if(subCode!=null && revenueItemsDef!=null) {
            if (count == 0) throw new BadRequestException("Revenue Item not defined for " + item.getValue());
            CoaSubAccounts drAccount = revenueItemsDef.getCrAccount();
            if(drAccount!=null){
                drAccount.setApplLevel(level);
            }
            return drAccount;
        }
        else return  null;
    }

    @Override
    public CoaSubAccounts getGlDebitAccountAddError(RevenueItems item, Long subCode, List<String> errors) throws BadRequestException {
        long count = revenueItemsRepo.count(QRevenueItemsDef.revenueItemsDef.item.eq(item).and(QRevenueItemsDef.revenueItemsDef.prodGroup.isNull()));
        System.out.println("Account count 1 "+count+" sub code "+subCode);
        RevenueItemsDef revenueItemsDef = null;
        if(count==1)
          revenueItemsDef = revenueItemsRepo.findOne(QRevenueItemsDef.revenueItemsDef.item.eq(item).and(QRevenueItemsDef.revenueItemsDef.prodGroup.isNull()));
        else {
            if (subCode == null) errors.add("Revenue Item not defined for " + item.getValue() + "<br>");
            count = revenueItemsRepo.count(QRevenueItemsDef.revenueItemsDef.item.eq(item).and(QRevenueItemsDef.revenueItemsDef.prodGroup.subId.eq(subCode)));
            revenueItemsDef = revenueItemsRepo.findOne(QRevenueItemsDef.revenueItemsDef.item.eq(item).and(QRevenueItemsDef.revenueItemsDef.prodGroup.subId.eq(subCode)));
            System.out.println("Account count 2 "+count);
        }
        if(count ==0) errors.add("Revenue Item not defined for " + item.getValue() + "<br>");
        if(count > 1) errors.add("Duplicate Accounts mapping for "+item.getValue() + "<br>");

        if(revenueItemsDef==null){
            errors.add("Revenue Item not defined for " + item.getValue() + "<br>");
            return null;
        }
        else
        if(revenueItemsDef.getDrAccount()==null) errors.add("An Account has not been mapped for "+item.getValue() + "<br>");
        return revenueItemsDef.getDrAccount();
    }

    @Override
    public CoaSubAccounts getGlCreditAccountAddError(RevenueItems item, Long subCode, List<String> errors) throws BadRequestException {
        long count = revenueItemsRepo.count(QRevenueItemsDef.revenueItemsDef.item.eq(item).and(QRevenueItemsDef.revenueItemsDef.prodGroup.isNull()));
        RevenueItemsDef revenueItemsDef = null;
        if(count==1)
            revenueItemsDef = revenueItemsRepo.findOne(QRevenueItemsDef.revenueItemsDef.item.eq(item).and(QRevenueItemsDef.revenueItemsDef.prodGroup.isNull()));
        else
        if(count==0) {
            if (subCode == null) errors.add("Revenue Item not defined for " + item.getValue() + "<br>");
            count = revenueItemsRepo.count(QRevenueItemsDef.revenueItemsDef.item.eq(item).and(QRevenueItemsDef.revenueItemsDef.prodGroup.subId.eq(subCode)));
            revenueItemsDef = revenueItemsRepo.findOne(QRevenueItemsDef.revenueItemsDef.item.eq(item).and(QRevenueItemsDef.revenueItemsDef.prodGroup.subId.eq(subCode)));
        }
        if(count ==0) errors.add("Revenue Item not defined for " + item.getValue() + "<br>");
        if(count > 1) errors.add("Duplicate Accounts mapping for "+item.getValue() + "<br>");

        if(revenueItemsDef==null){
            errors.add("Revenue Item not defined for " + item.getValue() + "<br>");
            return null;
        }
        else
        if(revenueItemsDef.getCrAccount()==null) errors.add("An Account has not been mapped for "+item.getValue() + "<br>");
        return revenueItemsDef.getCrAccount();
    }

    @Override
    public void validatePolicyAccounts(PolicyTrans policyTrans) throws BadRequestException {
        StringBuilder errorMessage = new StringBuilder();
        List<RiskTrans> riskTransList = riskTransRepo.getRiskDetails(policyTrans.getPolicyId());
        List<String> errors = new ArrayList<>();
        if(policyTrans.getWhtx()!=null && policyTrans.getWhtx().compareTo(BigDecimal.ZERO)!=0){
            RevenueItems item = RevenueItems.WHTX;
            for(RiskTrans riskTrans:riskTransList) {
               getGlDebitAccountAddError(item,riskTrans.getSubclass().getSubId(),errors);
            }
        }
        if(policyTrans.getCommAmt()!=null && policyTrans.getCommAmt().compareTo(BigDecimal.ZERO)!=0){
            RevenueItems item = RevenueItems.UC;
            for(RiskTrans riskTrans:riskTransList) {
                getGlDebitAccountAddError(item,riskTrans.getSubclass().getSubId(),errors);
            }
        }
        if(policyTrans.getAdminFeeAmt()!=null && policyTrans.getAdminFeeAmt().compareTo(BigDecimal.ZERO)!=0){
            RevenueItems item = RevenueItems.AF;
            for(RiskTrans riskTrans:riskTransList) {
                getGlDebitAccountAddError(item,riskTrans.getSubclass().getSubId(),errors);
            }
        }
        if(policyTrans.getSubAgentComm()!=null && policyTrans.getSubAgentComm().compareTo(BigDecimal.ZERO)!=0){
            RevenueItems item = RevenueItems.SAC;
            for(RiskTrans riskTrans:riskTransList) {
                getGlDebitAccountAddError(item,riskTrans.getSubclass().getSubId(),errors);
            }
        }
        errors.stream().forEach(a -> errorMessage.append(a));
        String error = errorMessage.toString();
//        if(!StringUtils.isBlank(error))
//            throw new BadRequestException("Correct the following mapping errors <br> "+error);
    }
}
