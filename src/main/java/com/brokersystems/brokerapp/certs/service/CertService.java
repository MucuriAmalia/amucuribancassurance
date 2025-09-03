package com.brokersystems.brokerapp.certs.service;

import com.brokersystems.brokerapp.certs.dto.CertTypesDTO;
import com.brokersystems.brokerapp.certs.dto.PolicyCertificateDTO;
import com.brokersystems.brokerapp.certs.model.*;
import com.brokersystems.brokerapp.setup.model.SubClassDef;
import com.brokersystems.brokerapp.uw.dtos.WIBABeneficiariesDTO;
import com.brokersystems.brokerapp.uw.model.RiskBeneficiariesImport;
import com.brokersystems.brokerapp.uw.model.WIBABeneficiaries;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.SubclassSections;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CertService {
	
	DataTablesResult<CertTypes> findAllCertTypes(DataTablesRequest request) throws IllegalAccessException;

	void createCertType(CertTypes cert) throws BadRequestException;

	void deleteCertType(Long certId);
	void deleteSubclassCertType(Long subclasscertId);
	
	DataTablesResult<CertLots> findAllCertLots(DataTablesRequest request) throws IllegalAccessException;
	
	void createCertLots(CertLots certLot) throws BadRequestException;
	
	Page<CertTypes> selectCertTypes(String paramString, Pageable paramPageable);
	
	void deleteCertLot(Long cerId);

	DataTablesResult<BranchCerts> findAllBranchCerts(DataTablesRequest request, Long lotCode) throws IllegalAccessException;

	List<SubClassDef> findCertUnassignedSubclasses() throws IllegalAccessException;

	DataTablesResult<SubclassCertTypes> findAllCertTypesSubclass(DataTablesRequest request,Long certTypeCode) throws IllegalAccessException;

	void createBranchCerts(BranchCerts branchCerts) throws BadRequestException;

	void reassignBranchCerts(BranchCerts branchCerts,HttpServletRequest request) throws BadRequestException;

	Page<CertLots> selectCertLots(String paramString, Pageable paramPageable);

	Page<SubclassCertTypes> selectSubclassCertTypes(String paramString, Pageable paramPageable);

	void deallocateBranchCert(Long brnCertId,String remarks,HttpServletRequest request) throws BadRequestException;

	void allocateForPrint(Long brnCertId,String allocated,Long lastPrintedCert,HttpServletRequest request) throws BadRequestException;
	void acknowledgeCert(Long brnCertId) throws BadRequestException;

	DataTablesResult<PolicyCerts> findPrintQueue(DataTablesRequest request,Long ipuCode) throws IllegalAccessException;
	DataTablesResult<WIBABeneficiariesDTO> findBeneficiries(DataTablesRequest request, Long ipuCode) throws IllegalAccessException;

	void defineWibaBeneficiary(WIBABeneficiariesDTO beneficiary) throws BadRequestException;
	void autoGenerateCert(Long riskId) throws BadRequestException;

	Page<BranchCerts> findActiveLots(String paramString, Pageable paramPageable,Long riskId) throws  IllegalAccessException;

	Page<CertTypesDTO> findSubclassCertTypes(String paramString, Pageable paramPageable, Long riskId) throws IllegalAccessException;

	void createRiskCert(RiskCertForm riskCertForm) throws BadRequestException;

	void updateRiskCertificate(EditRiskCertForm certForm) throws BadRequestException;

	void deleteRiskCertificate(Long pcId) throws  BadRequestException;
	void deleteRiskBeneficiary(Long benId) throws  BadRequestException;

	void importRiskBeneficiaries(RiskBeneficiariesImport beneficiariesImport) throws BadRequestException;

	Page<BranchCerts> findAllLots(String paramString, Pageable paramPageable,Long certCode,Long brnCode,Long acctCode);

	DataTablesResult<PrintCertificateQueue> findCertToPrint(DataTablesRequest request,Long certCode,Long brnCode,Long acctCode,String certStatus, String polNo, String riskId) throws IllegalAccessException;

	DataTablesResult<PolicyCertificateDTO> findPolCertToPrint(DataTablesRequest request, Long polId);
	void allocatePolCerts(PrintCertBean certBean) throws BadRequestException;

	void allocateCerts(PrintCertBean certBean) throws BadRequestException;
	void deallocatePolCerts(PrintCertBean certBean) throws BadRequestException;

	void deallocateCerts(PrintCertBean certBean) throws BadRequestException;

	void createBatchCerts(List<Long> certCodes);

	void markCertPrinted();

	void createSubClassCertType(CertTypeSubclassBean subclassCertType);

	void batchPolicyCerts(Long polCode) throws BadRequestException;
}
