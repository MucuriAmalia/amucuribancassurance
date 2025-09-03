package com.brokersystems.brokerapp.claims.service;

import com.brokersystems.brokerapp.claims.dtos.*;
import com.brokersystems.brokerapp.claims.exception.ClaimException;
import com.brokersystems.brokerapp.claims.model.*;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.uw.dtos.WIBABeneficiariesDTO;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.model.RiskTrans;
import com.brokersystems.brokerapp.uw.model.SectionTrans;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by peter on 3/5/2017.
 */
public interface ClaimService {

    public Page<ClaimRisksDTO> findRisksToClaim(String searchValue, Date lossDate, Pageable pageable);

    public Page<ClmCausations> findClaimStatuses(String searchValue, Pageable pageable);

     DataTablesResult<ClaimantsDTO> findAllClaimants(DataTablesRequest request);

     Page<ClaimantsDTO> findAllClaimants(String searchValue, Pageable pageable);

     void defineClaimant(ClaimantsDef claimantsDef) throws BadRequestException;

    List<Map<String, Object>> getClaimPerilsForEdit(Long claimId);


    public void capturePerilPayment(ClaimPerilPayments perilPayments) throws BadRequestException;

    public void captureClaimPerils(ClaimPerils claimPeril) throws BadRequestException;

    void deleteClaimant(Long clmntId);

    void deleteClaimantPeril(Long clmPerilId);

    void deletePerilPayment(Long clmPymntId);

    void deleteClaimClaimant(Long clmntId);

    public Page<Occupation> findOccupations(String paramString, Pageable paramPageable);

    public Page<ClaimantsDef> findClaimants(String searchValue, Pageable pageable);

    Page<ClaimPerilDTO> findSubclassPerils(String searchValue,Pageable pageable,Long riskId);

    Long createClaim(ClaimForm claimForm, boolean isApproved, boolean skipMakerChecker,String claimStatus) throws ClaimException,BadRequestException;

    void createMakerCheckerForDraftClaim(Long claimId) throws BadRequestException;

    void createClaimantPeril(PerilBean perilBean) throws ClaimException,BadRequestException;

    ClaimBookings getOne(Long clmId);

     ClaimDetailsDTO getClaimInformation(Long clmId) throws BadRequestException;

    DataTablesResult<ClaimEnquiryDTO> enquireClaims(DataTablesRequest request,
                                                    Long clientCode, String polNo, String riskId,  String claimNo) throws IllegalAccessException;

     DataTablesResult<ClaimPerilReserveDTO> getClaimPerils(DataTablesRequest request,Long clmId);
     DataTablesResult<ClaimPaymentsDTO> getClaimPayments(DataTablesRequest request,Long clmId, Long sprId);

    public DataTablesResult<ClaimPerilPayments> getPerilPayments(DataTablesRequest request,Long claimantId) throws IllegalAccessException;

    DataTablesResult<ClaimClaimantsDTO> getClaimClaimants(DataTablesRequest request, Long clmId);

    DataTablesResult<ClaimRequiredDocsDTO> getRequiredDocs(DataTablesRequest request,Long clmId) throws IllegalAccessException ;

    void createclaimsRequiredDocs(RequiredDocBean requiredDocBean,Long clmId);

    public DataTablesResult<ClaimBookings> getClaimBookings(DataTablesRequest request,Long clmId) throws IllegalAccessException;

    DataTablesResult<ClaimUploadsDTO> getClaimUploads(DataTablesRequest request,Long clmId) throws IllegalAccessException;
    DataTablesResult<ClaimActivityDTO> getClaimAcitivities(DataTablesRequest request,Long clmId) throws IllegalAccessException;
    DataTablesResult<ClaimAuditLogDTO> getClaimAuditLogs(DataTablesRequest request, Long clmId) throws IllegalAccessException;
    public DataTablesResult<ClaimStatuses> getClaimStatuses(DataTablesRequest request, Long clmId) throws IllegalAccessException;

    public void addActivity(ClaimActivities activity) throws BadRequestException;

    public void addClaimStatus(ClaimStatuses claimStatuses, Long clmId) throws BadRequestException;

    public void saveDvProgress(ClaimStatuses claimStatuses, Long clmId) throws BadRequestException;

    public ClaimStatuses getLatestDvData(Long claimId);

    public ClaimBalanceBean getBalance(String polNo);

    void validateCoverPeriod(Long policyId, Date lossDate) throws BadRequestException;

    public Set<SectionTransBean> getExpireSections(Long perilId, Long riskId);

    DataTablesResult<ClaimsTransDto> getClaimTransactions(DataTablesRequest request, Long clmId) ;

    void makeReady(Long transId);

    void makeUndo(Long transId);

    void authoriseTransaction(Long transId) throws BadRequestException;

    Page<ServiceProviderTypesDTO> findServiceProviderTypes(String searchValue, Pageable pageable);
    Page<ServiceProviderDTO> findServiceProviders(String searchValue, Pageable pageable);

    void createServiceProviderTypes(ServiceProviderTypesDTO serviceProviderTypes) throws BadRequestException;

    void createServiceProviders(ServiceProviderDTO serviceProviderTypes) throws BadRequestException;

    DataTablesResult<ServiceProviderDTO> getServiceProviders(Long id, DataTablesRequest request);

    void deleteServiceProvider(Long providerId) throws BadRequestException;

    void deleteServiceProviderType(Long providerId) throws BadRequestException;

    void updateRejectedClaim(Long claimId, ClaimForm claimForm, String resubmissionComment) throws ClaimException, BadRequestException;

    void cleanupOldDrafts();


}
