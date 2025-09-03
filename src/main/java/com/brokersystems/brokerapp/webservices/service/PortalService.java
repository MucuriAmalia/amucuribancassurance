package com.brokersystems.brokerapp.webservices.service;

import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.webservices.model.*;
import com.brokersystems.brokerapp.webservices.portalmodel.Agent;
import com.brokersystems.brokerapp.webservices.portalmodel.IntProducts;
import com.brokersystems.brokerapp.webservices.portalmodel.PolicyData;
import com.brokersystems.brokerapp.webservices.portalmodel.ResponseMessage;

public interface PortalService {

    String createClient(Agent agent) throws BadRequestException;

    String createAgent(Agent agent);

    ResponseObject createPolicy(PolicyData policyData) throws BadRequestException;
    ClaimResponseObject createClaim(CreateClaimModel claimModel) throws BadRequestException;

    DocResponseObject getPolicyDocument(Long policyId) throws BadRequestException;

    IntProducts findProducts();

    ResponseObject downgradePolicy(DowngradePolicyObject policyData) throws BadRequestException;


}
