package com.brokersystems.brokerapp.jobs;

import com.brokersystems.brokerapp.integrations.service.ClientIntegrationService;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.dto.netreveal.NetrevealRequest;
import com.brokersystems.brokerapp.setup.model.ClientDef;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.brokersystems.brokerapp.users.dto.netreveal.*;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.model.QPolicyTrans;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class BackgroundNetrevealJob extends AbstractJob {
    @Autowired
    private ParamService paramService;
    @Autowired
    private PolicyTransRepo policyTransRepo;
    @Autowired
    private ClientIntegrationService clientIntegrationService;

    @Override
    public String getCronExpression() {
        String param = "0 0 0 * * ?";
        try {
            param = paramService.getParameterString("BACKGROUND_NETREVEAL_JOB_CRON_EXPRESSION");
        } catch (BadRequestException ignored) {
        }

        return param;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        String screeningMonths = "12";
        try {
            screeningMonths = paramService.getParameterString("BACKGROUND_NETREVEAL_SCREENING_MONTHS");
        } catch (BadRequestException ignored) {
        }
        Calendar screeningCal = Calendar.getInstance();
        screeningCal.setTime(new Date());
        screeningCal.add(Calendar.MONTH, -(Integer.parseInt(screeningMonths)));
        Date screeningDueDate = screeningCal.getTime();
        int pageLimit = 200;
        int page = 0;
        int totalRecords = 0;
        do {
            PageRequest pageRequest = new PageRequest(page, pageLimit);
            Page<PolicyTrans> activePolicies = policyTransRepo.findAll(QPolicyTrans.policyTrans.authStatus.eq("A"), pageRequest);
            List<ClientDef> clients = new ArrayList<>();
            if (activePolicies.getTotalElements() > 0) {
                for (PolicyTrans policyTrans : activePolicies) {
                    ClientDef clientDef = policyTrans.getClient();

                    if (clientDef != null && clientDef.getScreeningDate() != null) {
                        if (!clientDef.getScreeningDate().after(screeningDueDate)) {
                            clients.add(clientDef);
                        }
                    }
                }
                if (!clients.isEmpty()) {
                    for (ClientDef clientDef : clients) {
                        String clientType = clientDef.getTenantType().getTypeDesc();
                        NetrevealRequest netrevealRequest = new NetrevealRequest();
                        BackgroundNetrevealRequest backgroundNetrevealRequest = new BackgroundNetrevealRequest();
                        RegulatoryAssessmentWorkProducts regulatoryAssessmentWorkProducts = new RegulatoryAssessmentWorkProducts();
                        CommonDetails commonDetails = new CommonDetails();
                        IndividualDetails individualDetails = new IndividualDetails();
                        EntityDetails entityDetails = new EntityDetails();

                        String identificationType = clientDef.getIdNo() != null ? "905" : "906";
                        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

                        if (clientType.equalsIgnoreCase("CORPORATE")) {
                            commonDetails.setCustomerType("E_PRIMARY");
                            commonDetails.setCustomerId("custid");
                            commonDetails.setControlId("cntrlid");
                            commonDetails.setSbu("RBB");
                            commonDetails.setTeller("AB031TA");
                            commonDetails.setBranch("632005");
                            commonDetails.setOrgUnit("KE");

                            entityDetails.setCompanyName(clientDef.getFname());
                            entityDetails.setCompanyTradingAsName(clientDef.getFname());
                            entityDetails.setDateOfRegistration(outputFormat.format(clientDef.getDob()));
                            entityDetails.setCompanyRegistrationNumber(clientDef.getIdNo());
                            entityDetails.setCountryOfOperation(Collections.singletonList("KE"));
                            entityDetails.setCountriesTradedWith(Collections.singletonList("KE"));
                            entityDetails.setIncorporationCountryCode("KE");
                            regulatoryAssessmentWorkProducts.setEntityDetails(entityDetails);
                        } else {
                            commonDetails.setCustomerType("I_PRIMARY");
                            commonDetails.setIdentificationType(identificationType);
                            commonDetails.setSbu("RBB");
                            commonDetails.setTeller("AB031TA");
                            commonDetails.setBranch("632005");
                            commonDetails.setOrgUnit("KE");

                            individualDetails.setFirstName(clientDef.getFname());
                            individualDetails.setMiddleName(clientDef.getOtherNames().contains(" ") ? clientDef.getOtherNames().split(" ")[0] : clientDef.getOtherNames());
                            individualDetails.setSurName(clientDef.getOtherNames().contains(" ") ? clientDef.getOtherNames().split(" ")[1] : clientDef.getOtherNames());
                            individualDetails.setIdentificationNumber(clientDef.getIdNo());
                            individualDetails.setDateOfBirth(outputFormat.format(clientDef.getDob()));
                            individualDetails.setNationality("KE");
                            individualDetails.setCountryOfResidence("KE");
                            regulatoryAssessmentWorkProducts.setIndividualDetails(individualDetails);
                        }
                        regulatoryAssessmentWorkProducts.setCommonDetails(commonDetails);
                        backgroundNetrevealRequest.setRegulatoryAssessmentWorkProducts(regulatoryAssessmentWorkProducts);
                        netrevealRequest.setRequest(backgroundNetrevealRequest);
                        netrevealRequest.setClientType(clientType);
                        try {
                            clientIntegrationService.backgroundNetrevealScreening(clientDef, netrevealRequest);
                        } catch (BadRequestException e) {
                            throw new RuntimeException(e.getMessage());
                        }
                    }
                }
                page++;
                totalRecords = activePolicies.getTotalPages();
            }
        } while (page < totalRecords);
    }
}
