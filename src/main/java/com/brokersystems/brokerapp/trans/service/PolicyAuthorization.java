package com.brokersystems.brokerapp.trans.service;

import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.trans.model.SystemTrans;
import com.brokersystems.brokerapp.trans.model.SystemTransactions;
import com.brokersystems.brokerapp.uw.dtos.PolicyCancellationDTO;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;

import java.math.BigDecimal;
import java.util.List;

public interface PolicyAuthorization {

	void authorizePolicy(Long polCode, BigDecimal refundAmount, boolean isUpload) throws BadRequestException;

	 //void authorizePolicy(Long polCode, BigDecimal refundAmount) throws BadRequestException;

	 void saveAuthorizationComment(Long polCode, String authComments) throws BadRequestException;

	 void authorizeMedicalPolicy(Long polCode ) throws BadRequestException;
	
	 void authorizePolicy(Long polCode,User user) throws BadRequestException;

	void authorizeLifePolicy(Long polCode) throws BadRequestException;

	void submitPolicyCancellation(PolicyCancellationDTO dto, boolean isApproved) throws BadRequestException;

    void submitBulkPolicyCancellation(PolicyCancellationDTO dto, boolean isApproved) throws BadRequestException;

	void updateRefundAmount(PolicyCancellationDTO dto) throws BadRequestException;
//	void submitPolicyCancellation(PolicyCancellationDTO dto, boolean isApproved) throws BadRequestException;


	 void authorizeBulkUploadPolicies(Long polCode) throws BadRequestException;

	 void generateCert(Long polCode) throws BadRequestException;

	void saveRiskDocComment(Long rdId, String comments) throws BadRequestException;

	void verifyRiskDoc(Long rdId) throws BadRequestException;

    BigDecimal cashBasisBalance(Long polCode);

	void postCommissions(PolicyTrans policy, SystemTrans transaction,
						 BigDecimal polComm, BigDecimal polSubAgentComm, BigDecimal polMarketerAgentComm, BigDecimal adminFee,
						 SystemTransactions transactions) throws BadRequestException ;

	void postUwTransactions(PolicyTrans policy, SystemTrans transaction, BigDecimal commamt,
							BigDecimal subagentComm, BigDecimal marketComm, BigDecimal adminFee,
							SystemTransactions transactions) throws BadRequestException;
}
