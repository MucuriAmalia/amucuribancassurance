package com.brokersystems.brokerapp.trans.service;

import com.brokersystems.brokerapp.accounts.model.CoaSubAccounts;
import com.brokersystems.brokerapp.enums.RevenueItems;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.ProductsDef;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;

import java.util.List;

/**
 * Created by HP on 8/9/2017.
 */
public interface AccountsUtilities {


     CoaSubAccounts getGlDebitAccount(RevenueItems item,Long  subCode) throws BadRequestException;

     CoaSubAccounts getGlDebitAccountAddError(RevenueItems item,Long  subCode, List<String> errors) throws BadRequestException;

     CoaSubAccounts getGlCreditAccount(RevenueItems item,Long  subCode) throws BadRequestException;

     CoaSubAccounts getGlCreditAccountAddError(RevenueItems item,Long  subCode, List<String> errors) throws BadRequestException;

     void validatePolicyAccounts(PolicyTrans policyTrans) throws BadRequestException;

     CoaSubAccounts getGlDebitAccount(RevenueItems item) throws BadRequestException;
}
