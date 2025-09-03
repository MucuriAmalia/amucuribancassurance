package com.brokersystems.brokerapp.medical.service;

import com.brokersystems.brokerapp.medical.model.*;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by peter on 5/12/2017.
 */
public interface MedicalClmService {

    public Page<MedicalEvents> findEvents(String paramString, Pageable paramPageable);

    public DataTablesResult<CategoryMembers> findClaimMembers(DataTablesRequest request, Long policyId, String cardNo,
                                                              String memberName, String gender,
                                                              Long clientId) throws IllegalAccessException;

    public DataTablesResult<CategoryMembers> findClmMembers(DataTablesRequest request, String policyNo, String memberNo, String memberName,
                                                              Long age, String gender, Date dob, String clientName) throws IllegalAccessException;

    public Long createPreauthTrans(MedicalParTrans parTrans) throws BadRequestException;

    public void createRequests(MedicalParRequest parRequest) throws BadRequestException;

    public void createClmRequests(MedicalParRequest parRequest) throws BadRequestException;

    public void deleteRequests(Long requestId) throws BadRequestException;

    public void deleteClmRequests(Long requestId) throws BadRequestException;

    public void createServices(MedicalRequestServices requestServices) throws BadRequestException;

    public void createClmServices(MedicalRequestServices requestServices) throws BadRequestException;

    public void deleteServices(Long serviceId) throws BadRequestException;

    public void deleteClmServices(Long serviceId) throws BadRequestException;

    public DataTablesResult<MedicalParTrans> enquirePreuathTrans(DataTablesRequest request) throws IllegalAccessException;

    public DataTablesResult<MedicalParTrans> batchTrans(DataTablesRequest request, Long contractId) throws IllegalAccessException;

    public DataTablesResult<MedicalParRequest> enquireParRequests(DataTablesRequest request,Long parId) throws IllegalAccessException;

    public DataTablesResult<MedicalRequestServices> getRequestServices(DataTablesRequest request, Long reqId) throws IllegalAccessException;

    public MedicalParTrans getMedicalClaimDetails(Long parId) throws BadRequestException;

    public Page<MedCategoryBenefits> findBenefits(String paramString, Pageable paramPageable,Long catId);

    public void authPreauth(Long parId) throws BadRequestException;

    public Long createClmTrans(MedicalParTrans parTrans) throws BadRequestException;

    public BigDecimal calculateLimit(Long benefitId);

    public void authMedClaims(Long parId) throws BadRequestException;

    public DataTablesResult<MedicalParTrans> smartClaims(DataTablesRequest request) throws IllegalAccessException;

    public Long convertPreauthTrans(Long parId) throws BadRequestException;

    public Page<PolicyTrans> findMedicalPolicies(String paramString, Pageable paramPageable);

    public void makeReadyPreauth(Long parId) throws BadRequestException;

    public void makeReadyMedClaims(Long parId) throws BadRequestException;

    public DataTablesResult<RequestServiceLog> getRequestServiceLog(DataTablesRequest request, Long serviceId) throws IllegalAccessException;

    public DataTablesResult<MedParReqDocs> getParDocs(DataTablesRequest request, Long parId) throws IllegalAccessException;

}
