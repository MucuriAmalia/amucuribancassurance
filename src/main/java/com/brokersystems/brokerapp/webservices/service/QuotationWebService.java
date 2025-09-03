package com.brokersystems.brokerapp.webservices.service;

import com.brokersystems.brokerapp.webservices.model.*;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by HP on 3/28/2018.
 */
public interface QuotationWebService {

    DataResponse findSubAgentQuotes(int page, int size,String key);

    DataResponse findSubAgentPolicies(int page, int size, String pinNo);
    BalanceObject findClientBalance(String idNumber, String pinNo);
    CountObject findClientCountPolicies(String idNumber, String pinNo);
    CountObject findClientCountClaims(String idNumber, String pinNo);
    DataResponse findPoliciesByEmail(int page, int size, String email);

    List<WebBenefitsDTO> getSwitchBenefits();

    DataResponse findSubAgentClaims(int page, int size,String key);

    PolicyWebInfo getPolicyInfo(String polId);

    DataResponse findPolicyRisks(String polId,String key);

    QuoteWebInfo getQuotInfo(String quotId,String key);

    DataResponse findQuotProducts(String quotId,String key);

    DataResponse getClientDetails(Long subAgentId, int page, int size,String key,String search);

}
