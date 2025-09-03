package com.brokersystems.brokerapp.bulktransactions.service;

import com.brokersystems.brokerapp.bulktransactions.dtos.BulkPolicyCreationDTO;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.exception.EndorsementsException;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface BulkNonNBActionService {
    //endorsements
    Map<String, Object> uploadBulkENPolExcel(MultipartFile file) throws BadRequestException;
    DataTablesResult<BulkPolicyCreationDTO> unProcessedBulkEnPol(DataTablesRequest request);
    DataTablesResult<BulkPolicyCreationDTO> viewBulkEnPolicies(DataTablesRequest request);
    List<Long> bulkProcessENPolicies(List<Long> bulkIds, boolean isApproved) throws BadRequestException, EndorsementsException, IOException, IllegalAccessException;
    List<Long> bulkAuthorizeEndorsement(List<Long> bulkIds) throws BadRequestException;
    List<Long> bulkDeleteEndorsements(List<Long> bulkIds) throws BadRequestException;
    List<Long> bulkDelProcessedENPolicies(List<Long> bulkIds) throws BadRequestException;

    //renewals
    Map<String, Object> uploadBulkRNPolExcel(MultipartFile file) throws BadRequestException, IOException, InvalidFormatException;
    DataTablesResult<BulkPolicyCreationDTO> unProcessedBulkRNPol(DataTablesRequest request);
    DataTablesResult<BulkPolicyCreationDTO> viewBulkRNPolicies(DataTablesRequest request);
    List<Long> bulkProcessRNPolicies(List<Long> bulkIds, boolean isApproved) throws BadRequestException, EndorsementsException;
    List<Long> bulkDeleteRenewals(List<Long> bulkIds) throws BadRequestException;
    List<Long> bulkDelProcessedRNPolicies(List<Long> bulkIds) throws BadRequestException;
    void bulkAuthorizeRenewals(List<Long> bulkIds) throws BadRequestException;
    void bulkRejectRenewals(List<Long> bulkIds) throws BadRequestException;

    //cancellations
    Map<String, Object> uploadBulkCNPolExcel(MultipartFile file) throws BadRequestException;
    DataTablesResult<BulkPolicyCreationDTO> unProcessedBulkCNPol(DataTablesRequest request);
    DataTablesResult<BulkPolicyCreationDTO> viewBulkCNPolicies(DataTablesRequest request);
    List<Long> bulkProcessCNPolicies(List<Long> bulkIds, boolean isApproved) throws BadRequestException;
    List<Long> bulkDeleteCancellation(List<Long> bulkIds) throws BadRequestException;
    List<Long> bulkDelProcessedCNPolicies(List<Long> bulkIds) throws BadRequestException;
    List<Long> bulkAuthorizeCancellation(List<Long> bulkIds) throws BadRequestException;

    String approveBulkPol(List<Long> policyIds) throws BadRequestException;
    void bulkDelUploadPolicies(List<Long> bulkIds) throws BadRequestException;

    //refunds
    Map<String, Object> uploadBulkRefundPolExcel(MultipartFile file) throws BadRequestException;
    DataTablesResult<BulkPolicyCreationDTO> unProcessedBulkRefundPol(DataTablesRequest request);
    DataTablesResult<BulkPolicyCreationDTO> viewBulkRefundPolicies(DataTablesRequest request);
    List<Long> bulkProcessRefundPolicies(List<Long> bulkIds, boolean isApproved) throws BadRequestException, IllegalAccessException;
    List<Long> bulkDeleteRefunds(List<Long> bulkIds) throws BadRequestException;
    String bulkARejectRefunds(List<Long> taskIds, Long reason, String reasonDesc) throws BadRequestException;
    List<Long> bulkAuthorizeRefunds(List<Long> bulkIds) throws BadRequestException;

}
