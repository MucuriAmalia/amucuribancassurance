package com.brokersystems.brokerapp.setup.service;

import com.brokersystems.brokerapp.life.model.LifeCommissionRates;
import com.brokersystems.brokerapp.life.model.LifeSubAgentCommissionRates;
import com.brokersystems.brokerapp.setup.model.*;

import javax.servlet.http.HttpServletRequest;

public interface SetupAudit {
    String logBinder(BindersDef binder);
     void logBinderDetailsCreate(BinderDetailsBean binderBean, HttpServletRequest request);
     String logBinderDetailsUpdate(BinderDetails det);
     String logPremiumRates(PremRatesDef rates);
     String logCommissionRates(CommissionRates rates);
     String logSubAgentCommissionRates(SubAgentCommissionRates rates);
     String logLifeCommissionRates(LifeCommissionRates rates);
     String logLifeSubAgentCommissionRates(LifeSubAgentCommissionRates rates);
     void logBinderSectPerils(BinderPerilsBean perilsBean,HttpServletRequest request);
     void logBinderRequiredDocs(RequiredDocsBean docsBean,HttpServletRequest request);
     String logUpdateBinderRequiredDocs(BinderReqrdDocs reqrdDocs);
     String logDeleteBinder(Long binCode);
     String logDelBinderDetails(Long detId);
     String logDelPremRates(Long detId);
     String logDelCommissionRates(Long commCode);
     String logDelSubAgentCommissionRates(Long commCode);
     String logDelLifeCommissionRates(Long commCode);
     String logDelSubAgentLifeCommissionRates(Long commCode);
     String logDelBinderRequiredDocs(Long docId);
     String logMakeReady(Long id);
     String logAuthorise(Long id);
     String logUndoBinder(Long id);
     String logUnAuthorise(Long id);
}
