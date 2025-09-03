package com.brokersystems.brokerapp.certs.service.impl;

import com.brokersystems.brokerapp.certs.dto.CertTypesDTO;
import com.brokersystems.brokerapp.certs.dto.PolicyCertificateDTO;
import com.brokersystems.brokerapp.certs.model.*;
import com.brokersystems.brokerapp.certs.repository.*;
import com.brokersystems.brokerapp.claims.dtos.ClaimEnquiryDTO;
import com.brokersystems.brokerapp.claims.dtos.ClaimantsDTO;
import com.brokersystems.brokerapp.medical.model.SubLimits;
import com.brokersystems.brokerapp.server.utils.DateUtilities;
import com.brokersystems.brokerapp.server.utils.TemplateMerger;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.dto.UserDTO;
import com.brokersystems.brokerapp.setup.model.OrgBranch;
import com.brokersystems.brokerapp.setup.model.SubClassDef;
import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.setup.repository.OrgBranchRepository;
import com.brokersystems.brokerapp.setup.repository.SubClassRepo;
import com.brokersystems.brokerapp.setup.repository.UserRepository;
import com.brokersystems.brokerapp.setup.service.UserService;
import com.brokersystems.brokerapp.trans.model.QSystemTransactionsTemp;
import com.brokersystems.brokerapp.trans.model.SystemTransactionsTemp;
import com.brokersystems.brokerapp.trans.repository.SystemTransactionsTempRepo;
import com.brokersystems.brokerapp.uw.dtos.WIBABeneficiariesDTO;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import com.brokersystems.brokerapp.uw.repository.RiskTransRepo;
import com.brokersystems.brokerapp.uw.repository.WIBABeneficiariesRepo;
import com.google.gson.Gson;
import de.siegmar.fastcsv.reader.CsvParser;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.brokersystems.brokerapp.certs.service.CertService;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.QSubclassSections;
import com.mysema.query.types.Predicate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Service
public class CertServiceImpl implements CertService {
	
	@Autowired
	private CertTypeRepo certRepo;
	
	@Autowired
	private CertLotsRepo certLotsRepo;

	@Autowired
	private BranchCertsRepo branchCertsRepo;

	@Autowired
	private RiskTransRepo riskRepo;

	@Autowired
	private PolicyCertsRepo policyCertsRepo;

	@Autowired
	private UserService userRepository;

	@Autowired
	private UserUtils userUtils;

	@Autowired
	private PrintQueueRepo printQueueRepo;

	@Autowired
	private DateUtilities dateUtilities;

	@Autowired
	private CertPrintRepo printRepo;

	@Autowired
	private PolicyTransRepo policyTransRepo;

	@Autowired
	private OrgBranchRepository branchRepository;

	@Autowired
	private TemplateMerger templateMerger;

	@Autowired
	private DataSource datasource;

	@Autowired
	private SubclassCertTypesRepo subclassCertTypesRepo;

	@Autowired
	private SubClassRepo subclassRepo;

	@Autowired
	private SystemTransactionsTempRepo systemTransactionsTempRepo;
	@Autowired
	private WIBABeneficiariesRepo beneficiariesRepo;
	@Autowired
	private RiskTransRepo riskTransRepo;

	@Autowired
	private CsvReader csvReader;

	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<CertTypes> findAllCertTypes(DataTablesRequest request) throws IllegalAccessException {
		Page<CertTypes> page = certRepo.findAll(request.searchPredicate(QCertTypes.certTypes), request);
		return new DataTablesResult(request, page);
	}


	@Override
	@Transactional(readOnly = false)
	public void createCertType(CertTypes cert) throws BadRequestException {
		if(cert.getMinCapacity()!=null && cert.getMaxCapacity()!=null){
		      if(cert.getMinCapacity() > cert.getMaxCapacity()) throw new BadRequestException("Min Capacity Cannot be greater than Max Capacity");
		}
		if(cert.getReorderLevel() < 0) throw new BadRequestException("Reorder Level cannot be less than zero");
		certRepo.save(cert);
		
	}


	@Override
	@Transactional(readOnly = false)
	public void deleteCertType(Long certId) {
		certRepo.delete(certId);
		
	}


	@Override
	@Transactional(readOnly = false)
	public void deleteSubclassCertType(Long subclasscertId) {

//		Predicate pred = QSubclassCertTypes.branchCerts.branch.obId.eq(branchCerts.getBranch().getObId())
//				.and(QBranchCerts.branchCerts.deallocated.isNull().or(QBranchCerts.branchCerts.deallocated.eq("N")))
//				.and(QBranchCerts.branchCerts.user.id.eq(branchCerts.getUser().getId()))
//				.and(QBranchCerts.branchCerts.certLots.cerId.eq(branchCerts.getCertLots().getCerId()))
//				.and(QBranchCerts.branchCerts.noFrom.between(branchCerts.getNoFrom(), branchCerts.getNoTo())
//						.or(QBranchCerts.branchCerts.noTo.between(branchCerts.getNoFrom(), branchCerts.getNoTo())));
//
//		Long count = branchCertsRepo.count(pred);
		subclassCertTypesRepo.delete(subclasscertId);

	}



	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<CertLots> findAllCertLots(DataTablesRequest request) throws IllegalAccessException {
		Page<CertLots> page = certLotsRepo.findAll(request.searchPredicate(QCertLots.certLots), request);
		return new DataTablesResult(request, page);
	}


	@Override
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public void createCertLots(CertLots certLot) throws BadRequestException {
		if(certLot.getNoFrom()==null)
			throw new BadRequestException("Provide Cert No From");
		if(certLot.getNoTo()==null)
			throw new BadRequestException("Provide Cert No To");
		if(certLot.getNoTo() < certLot.getNoFrom()){
			throw new BadRequestException("Cert No From Cannot be greater than Cert No To");
		}
		
		if(certLot.getUnderwriter()==null)
			throw new BadRequestException("Select the Underwriter the cert is assigned to");
		
		if(certLot.getSubclass()==null)
			throw new BadRequestException("Select Sub class");
		
		if(certLot.getCertTypes()==null)
			throw new BadRequestException("Select Certificate Type");
		
	    Predicate pred = QCertLots.certLots.certTypes.certId.eq(certLot.getCertTypes().getCertId())
	    		         .and(QCertLots.certLots.noFrom.between(certLot.getNoFrom(), certLot.getNoTo())
	    		        	  .or(QCertLots.certLots.noTo.between(certLot.getNoFrom(), certLot.getNoTo())));
	    
		Long count = certLotsRepo.count(pred);
		if(certLot.getCerId()==null)
		if(count > 0) throw new BadRequestException("Certificate Lot Range already exists");
		else 
			if(count > 1) throw new BadRequestException("Certificate Lot Range already exists");
		certLot.setCertCount(certLot.getNoTo()-certLot.getNoFrom()+1);
		if(certLot.getCerId()==null){
			certLot.setAvailableNoFrom(certLot.getNoFrom());
		}
		else{
			CertLots existingLot = certLotsRepo.findOne(certLot.getCerId());
			Predicate predicate = QBranchCerts.branchCerts.certLots.cerId.eq(existingLot.getCerId());
			if(branchCertsRepo.count(predicate) > 0) {
				if (existingLot.getAvailableNoFrom() > certLot.getNoFrom()) {
					throw new BadRequestException("Cannot Update A used Lot..Consult Admin");
				}

				if (existingLot.getAvailableNoFrom() < certLot.getNoFrom())
					throw new BadRequestException("Error Updating Certificate Lot...Consult Admin");
			}
			certLot.setAvailableNoFrom(certLot.getNoFrom());
		}

		certLotsRepo.save(certLot);
		
	}


	@Override
	@Transactional(readOnly = true)
	public Page<CertTypes> selectCertTypes(String paramString, Pageable paramPageable) {
		Predicate pred = null;
		if (paramString == null || StringUtils.isBlank(paramString)) {
			pred = QCertTypes.certTypes.isNotNull();
		} else {
			pred = QCertTypes.certTypes.certDesc.containsIgnoreCase(paramString);
		}
		return certRepo.findAll(pred, paramPageable);
	}


	@Override
	@Transactional(readOnly = false)
	public void deleteCertLot(Long cerId) {
		certLotsRepo.delete(cerId);
		
	}

	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<BranchCerts> findAllBranchCerts(DataTablesRequest request,Long lotCode) throws IllegalAccessException {
		Predicate pred = (QBranchCerts.branchCerts.deallocated.isNull().or(QBranchCerts.branchCerts.deallocated.equalsIgnoreCase("N"))).and(QBranchCerts.branchCerts.certLots.cerId.eq(lotCode)).and(request.searchPredicate(QBranchCerts.branchCerts));
		Page<BranchCerts> page = branchCertsRepo.findAll(pred, request);
		List<BranchCerts> branchCertses = page.getContent();
		branchCertses.stream().forEach(a -> {
           if(a.getUser().getId()==userUtils.getCurrentUser().getId()){
			   a.setShowUser(true);
		   }
			else a.setShowUser(false);
		});

		Page<BranchCerts> branchCertsPage =new PageImpl<BranchCerts>(branchCertses);
		return new DataTablesResult(request, branchCertsPage);
	}


	@Override
	@Transactional(readOnly = true)
	public List<SubClassDef> findCertUnassignedSubclasses() throws IllegalAccessException {

		return subclassCertTypesRepo.getCertUnassignedSubclasses();
	}

	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<SubclassCertTypes> findAllCertTypesSubclass(DataTablesRequest request,Long certTypeCode) throws IllegalAccessException {
		Predicate pred = (QSubclassCertTypes.subclassCertTypes.certType.certId.eq(certTypeCode));
		Page<SubclassCertTypes> page = subclassCertTypesRepo.findAll(pred, request);
		return new DataTablesResult(request, page);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public void createBranchCerts(BranchCerts branchCerts) throws BadRequestException {
		if(branchCerts.getBranch()==null) throw  new BadRequestException("Select Branch");
		if(branchCerts.getUser()==null) throw new BadRequestException("Select User to Allocate to");
		if(branchCerts.getCertLots()==null) throw new BadRequestException("Select Certificate Lot");
		Predicate pred = QBranchCerts.branchCerts.branch.obId.eq(branchCerts.getBranch().getObId())
				.and(QBranchCerts.branchCerts.deallocated.isNull().or(QBranchCerts.branchCerts.deallocated.eq("N")))
				.and(QBranchCerts.branchCerts.user.id.eq(branchCerts.getUser().getId()))
				.and(QBranchCerts.branchCerts.certLots.cerId.eq(branchCerts.getCertLots().getCerId()))
				.and(QBranchCerts.branchCerts.noFrom.between(branchCerts.getNoFrom(), branchCerts.getNoTo())
						.or(QBranchCerts.branchCerts.noTo.between(branchCerts.getNoFrom(), branchCerts.getNoTo())));

		Long count = branchCertsRepo.count(pred);
		if(branchCerts.getNoFrom() > branchCerts.getNoTo()){
			throw new BadRequestException("Cert No from cannot be greater than Cert No to");
		}
		if(branchCerts.getBrnCertId()==null)
			if(count > 0) throw new BadRequestException("Certificate Lot Range already exists");
			else
			if(count > 1) throw new BadRequestException("Certificate Lot Range already exists");

		CertLots lot = branchCerts.getCertLots();
		if(lot.getAvailableNoFrom()==lot.getNoTo()){
			throw new BadRequestException("Certificate Lot Selected has been fully utilized");
		}

		long newNoto = 0l;
		if(branchCerts.getNoTo()==lot.getNoTo()){
			newNoto = branchCerts.getNoTo();
		}
		else{
			newNoto = branchCerts.getNoTo()+1;
		}

		long certCount = branchCerts.getNoTo()-branchCerts.getNoFrom()+1;
		long remainingCerts = lot.getNoTo() - lot.getAvailableNoFrom()+1;

		if(certCount > remainingCerts) throw new BadRequestException("The Number of Certs to Allocate "+certCount+" Cannot be greater than Number of Remaining Certs "+remainingCerts);

		lot.setAvailableNoFrom(newNoto);

		branchCerts.setCountCerts(certCount);
		branchCerts.setAllocatedBy(userUtils.getCurrentUser());
		branchCerts.setAllocatedDate(new Date());
		branchCertsRepo.save(branchCerts);
		certLotsRepo.save(lot);

	}


	@Override
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public void reassignBranchCerts(BranchCerts branchCerts,HttpServletRequest request) throws BadRequestException {
		if(branchCerts.getBranch()==null) throw  new BadRequestException("Select Branch");
		if(branchCerts.getUser()==null) throw new BadRequestException("Select User to Allocate to");
		if(branchCerts.getCertLots()==null) throw new BadRequestException("Select Certificate Lot");
		if(branchCerts.getReassignedBrnCert()==null) throw new BadRequestException("Select Certificate Lot to reassign");
		BranchCerts fromCert = branchCertsRepo.findOne(branchCerts.getReassignedBrnCert().getBrnCertId());
		if (!(fromCert.getLastPrintedNo()==null))
		if (branchCerts.getNoFrom()< fromCert.getLastPrintedNo()) throw new BadRequestException("Certificate No from cannot be less than the last printed certificate No.");
		if (branchCerts.getNoFrom()> fromCert.getNoTo()) throw new BadRequestException("Cert No from cannot be greater than Cert No to");
		if(branchCerts.getNoFrom()==fromCert.getNoTo()){
			throw new BadRequestException("Certificate Selected has been fully utilized");
		}

		Long currentLot = branchCertsRepo.count(QBranchCerts.branchCerts.user.id.eq(branchCerts.getUser().getId())
				.and(QBranchCerts.branchCerts.branch.obId.eq(branchCerts.getBranch().getObId()))
				.and(QBranchCerts.branchCerts.certLots.cerId.eq(branchCerts.getCertLots().getCerId()))
				.and(QBranchCerts.branchCerts.currentLot.equalsIgnoreCase("Y")));
		if(currentLot > 0){
				throw new BadRequestException("Cannot Have more than one Active Lot for printing");
		}

		long certCount = branchCerts.getNoTo()-branchCerts.getNoFrom()+1;


		branchCerts.setCountCerts(certCount);
		if (branchCerts.getNoFrom().equals(fromCert.getNoFrom()) && branchCerts.getNoTo().equals(fromCert.getNoTo())){
			fromCert.setDeallocated("Y");
			fromCert.setRemarks("reassigned to another user");
			fromCert.setCurrentLot("N");
			fromCert.setDeallocatedDate(new Date());
			fromCert.setDeallocatedBy(userUtils.getCurrentUser());
		}else {
			fromCert.setNoTo(branchCerts.getNoFrom() - 1);
			fromCert.setCountCerts(branchCerts.getNoFrom() - fromCert.getNoFrom() + 1);
		}
		branchCerts.setAllocatedBy(userUtils.getCurrentUser());
		branchCerts.setAllocatedDate(new Date());
		branchCerts.setStatus("D");
		branchCerts.setCurrentLot("Y");
		branchCertsRepo.save(fromCert);
		branchCertsRepo.save(branchCerts);

		String ret = templateMerger.sendCertAllocEmail(branchCerts.getUser().getId(), branchCerts.getNoFrom(), branchCerts.getNoTo(),
				branchCerts.getCertLots().getUnderwriter().getName()
				, branchCerts.getCertLots().getCertTypes().getCertDesc(), "Y",request);

	}

	@Override
	@Transactional(readOnly = true)
	public Page<CertLots> selectCertLots(String paramString, Pageable paramPageable) {
		Predicate pred = null;
		if (paramString == null || StringUtils.isBlank(paramString)) {
			pred = QCertLots.certLots.certTypes.certDesc.isNotNull();
		} else {
			pred = QCertLots.certLots.certTypes.certDesc.containsIgnoreCase(paramString);
		}
		List<CertLots> certs = certLotsRepo.findAll(pred,paramPageable).getContent();
		List<CertLots> newCerts = new ArrayList<>();
		for(CertLots cert:certs){
			 cert.setCertLotDesc(cert.getCertTypes().getCertDesc()+"("+cert.getNoFrom()+"-"+cert.getNoTo()+")");
			 newCerts.add(cert);
		}
		Page<CertLots> returnedPage = new PageImpl<CertLots>(newCerts);
		return returnedPage;
	}


	@Override
	@Transactional(readOnly = true)
	public Page<SubclassCertTypes> selectSubclassCertTypes(String paramString, Pageable paramPageable) {
		Predicate pred = null;
		if (paramString == null || StringUtils.isBlank(paramString)) {
			pred = QSubclassCertTypes.subclassCertTypes.subclass.subDesc.isNotNull();
		} else {
			pred = QSubclassCertTypes.subclassCertTypes.subclass.subDesc.containsIgnoreCase(paramString);
		}
		List<SubclassCertTypes> certs = subclassCertTypesRepo.findAll(pred,paramPageable).getContent();
		List<SubclassCertTypes> newCerts = new ArrayList<>();
		for(SubclassCertTypes cert:certs){
			cert.setSubclassDesc(cert.getSubclass().getSubDesc());
			newCerts.add(cert);
		}
		Page<SubclassCertTypes> returnedPage = new PageImpl<SubclassCertTypes>(newCerts);
		return returnedPage;
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public void deallocateBranchCert(Long brnCertId,String remarks,HttpServletRequest request) throws BadRequestException {
		BranchCerts branchCerts = branchCertsRepo.findOne(brnCertId);
		if(branchCerts==null) throw new BadRequestException("Branch Certificate to Deallocate cannot be null");
		if(branchCerts.getDeallocated()!=null){
			if("Y".equalsIgnoreCase(branchCerts.getDeallocated()))
				throw new BadRequestException("Cannot Deallocate already deallocated lot");

		}

		if(branchCerts.getCurrentLot()!=null){
			if("Y".equalsIgnoreCase(branchCerts.getCurrentLot()) && "A".equalsIgnoreCase(branchCerts.getStatus()))
				throw new BadRequestException("Cannot deallocate and active print lot");
		}

		if(remarks==null || StringUtils.isBlank(remarks)){
			throw new BadRequestException("Provide Deallocation Remarks to continue");
		}
		Long certTo = branchCerts.getNoTo();
		Iterable<BranchCerts> relatedCerts = branchCertsRepo.findAll(QBranchCerts.branchCerts.user.id.eq(branchCerts.getUser().getId())
		                                    .and(QBranchCerts.branchCerts.branch.obId.eq(branchCerts.getBranch().getObId()))
		                                    .and(QBranchCerts.branchCerts.certLots.cerId.eq(branchCerts.getCertLots().getCerId())));
		boolean maximumExists = false;
		for(BranchCerts cert:relatedCerts){
			String deallocated = (cert.getDeallocated()==null || "N".equalsIgnoreCase(cert.getDeallocated()))?"N":"Y";
			if("N".equalsIgnoreCase(deallocated))
			  if(certTo < cert.getNoTo()) {
			  	maximumExists = true;
			  	break;
			  }
		}

		if(maximumExists){
			throw new BadRequestException("You Cannot deallocate a lower Certificate Lot When a Higher Lot exists");
		}


		branchCerts.setDeallocated("Y");
		branchCerts.setRemarks(remarks);
		branchCerts.setCurrentLot("N");
		branchCerts.setDeallocatedDate(new Date());
		branchCerts.setDeallocatedBy(userUtils.getCurrentUser());
		CertLots lot = branchCerts.getCertLots();
		lot.setAvailableNoFrom(branchCerts.getNoFrom());
		certLotsRepo.save(lot);

		String ret = templateMerger.sendCertAllocEmail(branchCerts.getUser().getId(), branchCerts.getNoFrom(), branchCerts.getNoTo(),
				branchCerts.getCertLots().getUnderwriter().getName()
				, branchCerts.getCertLots().getCertTypes().getCertDesc(), "N",request);


	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public void allocateForPrint(Long brnCertId, String allocated, Long lastPrintedCert ,HttpServletRequest request) throws BadRequestException {
		BranchCerts branchCerts = branchCertsRepo.findOne(brnCertId);
		if(branchCerts==null) throw new BadRequestException("Branch Certificate to Deallocate cannot be null");
		if(branchCerts.getDeallocated()!=null){
			if("Y".equalsIgnoreCase(branchCerts.getDeallocated()))
				throw new BadRequestException("Cannot Allocate for Print an already deallocated lot");

		}
		if(branchCerts.getStatus()!=null){
			if("A".equalsIgnoreCase(branchCerts.getStatus()) )
				throw new BadRequestException("Cannot Allocate for Print an already acknowledged lot");

		}
		if(branchCerts.getCurrentLot()==null || "N".equalsIgnoreCase(branchCerts.getCurrentLot())){
			Long currentLot = branchCertsRepo.count(QBranchCerts.branchCerts.user.id.eq(branchCerts.getUser().getId())
					.and(QBranchCerts.branchCerts.branch.obId.eq(branchCerts.getBranch().getObId()))
					.and(QBranchCerts.branchCerts.certLots.cerId.eq(branchCerts.getCertLots().getCerId()))
					.and(QBranchCerts.branchCerts.currentLot.equalsIgnoreCase("Y")));
			if(currentLot > 0){
				if(allocated==null) allocated="N";
				if("Y".equalsIgnoreCase(allocated))
				throw new BadRequestException("Cannot Have more than one Active Lot for printing");
			}
		}

		if(lastPrintedCert!=null){
			if(lastPrintedCert > branchCerts.getNoTo() || lastPrintedCert < branchCerts.getNoFrom()){
				throw new BadRequestException("Last Printed Certificate No is not within Range");
			}
		}

		branchCerts.setLastPrintedNo(lastPrintedCert);
		branchCerts.setCurrentLot(allocated);
		if("Y".equalsIgnoreCase(allocated)){
			branchCerts.setStatus("D"); // D- DISPATCHED A - ACKNOWLEDGED
	}
		branchCertsRepo.save(branchCerts);

//		String ret = templateMerger.sendCertAllocEmail(branchCerts.getUser().getId(), branchCerts.getNoFrom(), branchCerts.getNoTo(),
//					branchCerts.getCertLots().getUnderwriter().getName()
//					, branchCerts.getCertLots().getCertTypes().getCertDesc(), "Y",request);
	}


	@Override
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public void acknowledgeCert(Long brnCertId) throws BadRequestException {
		BranchCerts branchCerts = branchCertsRepo.findOne(brnCertId);
		if(branchCerts==null) throw new BadRequestException("Branch Certificate to acknowledge cannot be null");
		if (userUtils.getCurrentUser().getId()!=branchCerts.getUser().getId()){
			throw new BadRequestException("Can only acknowledge certificate lot allocated to you");
		}
		if(branchCerts.getDeallocated()!=null){
			if("Y".equalsIgnoreCase(branchCerts.getDeallocated()))
				throw new BadRequestException("Cannot Acknowledge an already deallocated lot");

		}
		if(branchCerts.getStatus()!=null){
			if("A".equalsIgnoreCase(branchCerts.getStatus()))
				throw new BadRequestException("Cannot Acknowledge an already acknowledged lot");
			else {
				if(!("D".equalsIgnoreCase(branchCerts.getStatus())))
					throw new BadRequestException("Cannot Acknowledge a certificate lot that is not dispatched");
			}

		}else throw new BadRequestException("Cannot Acknowledge a certificate lot that is not dispatched");
		branchCerts.setStatus("A"); // D- DISPATCHED A - ACKNOWLEDGED

		branchCertsRepo.save(branchCerts);
	}

	@Override
	public void defineWibaBeneficiary(WIBABeneficiariesDTO beneficiary) throws BadRequestException {
		if(beneficiary.getRiskId()==null){
			throw new BadRequestException("Select Risk to add Beneficiary...");
		}
		final RiskTrans riskTrans = riskTransRepo.findOne(beneficiary.getRiskId());
		if(riskTrans==null){
			throw new BadRequestException("Select Risk to add Beneficiary...");
		}
		final WIBABeneficiaries beneficiaries = new WIBABeneficiaries();
		beneficiaries.setBwbId(beneficiary.getBwbId());
		beneficiaries.setFullName(beneficiary.getFullName());
		beneficiaries.setRiskTrans(riskTrans);
		beneficiaries.setSalary(beneficiary.getSalary());
		beneficiaries.setOccupation(beneficiary.getOccupation());
		beneficiariesRepo.save(beneficiaries);
	}
	@Override
	public DataTablesResult<WIBABeneficiariesDTO> findBeneficiries(DataTablesRequest request, Long ipuCode) {
		final String search = ( request.getSearch()!=null && request.getSearch().getValue()!=null)?"%"+request.getSearch().getValue().toLowerCase()+"%":"%%";
		List<Object[]> beneficiaries = beneficiariesRepo.findBeneficiaries(search.toLowerCase(),ipuCode, request.getPageNumber(), request.getPageSize());
		final List<WIBABeneficiariesDTO> beneficiariesDTOList = new ArrayList<>();
		long rowCount = 0l;
		if(!beneficiaries.isEmpty()) rowCount = ((BigInteger)beneficiaries.get(0)[4]).intValue();
		for(Object[] beneficiary:beneficiaries){
			WIBABeneficiariesDTO beneficiariesDTO = new WIBABeneficiariesDTO();
			beneficiariesDTO.setBwbId(((BigInteger)beneficiary[0]).longValue());
			beneficiariesDTO.setFullName((String) beneficiary[1]);
			beneficiariesDTO.setOccupation((String) beneficiary[2]);
			beneficiariesDTO.setSalary((BigDecimal) beneficiary[3]);
			beneficiariesDTO.setRiskId(ipuCode);
			beneficiariesDTOList.add(beneficiariesDTO);
		}
		Page<WIBABeneficiariesDTO>  page = new PageImpl<>(beneficiariesDTOList,request, rowCount);
		return new DataTablesResult<>(request, page);
	}

	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<PolicyCerts> findPrintQueue(DataTablesRequest request, Long ipuCode) throws IllegalAccessException {
//		Predicate pred = QRiskTrans.riskTrans.riskId.eq(ipuCode);
//         if(riskRepo.count(pred)==0) throw new IllegalAccessException("Invalid Risk ID");
//		Page<PolicyCerts> page = policyCertsRepo.findAll(QPolicyCerts.policyCerts.risk.riskId.eq(ipuCode).and(request.searchPredicate(QPolicyCerts.policyCerts)), request);

		List<Object[]> polcerts = policyCertsRepo.QueryPolicyCert(ipuCode);
		List<PolicyCerts> policyCertificates = new ArrayList<>();
		for (Object[] cert:polcerts){
			PolicyCerts policyCerts =new PolicyCerts();
			if (cert[0] instanceof BigInteger){
				policyCerts.setPcId(((BigInteger)cert[0]).longValue());
				policyCerts.setSubclassCertTypes(subclassCertTypesRepo.findOne(((BigInteger)cert[1]).longValue()));
				if (cert[6]!=null)
				policyCerts.setCertNo(((BigInteger) cert[6]).longValue());
				else
					policyCerts.setCertNo(null);

			} else if (cert[1] instanceof BigDecimal){
				policyCerts.setPcId(((BigDecimal)cert[0]).longValue());
				policyCerts.setSubclassCertTypes(subclassCertTypesRepo.findOne(((BigDecimal)cert[1]).longValue()));
				if (cert[6]!=null)
				policyCerts.setCertNo(((BigDecimal) cert[6]).longValue());
				else
					policyCerts.setCertNo(null);

			}
			policyCerts.setStatus((String)cert[2]);
			policyCerts.setPrintStatus((String)cert[3]);
			policyCerts.setCertWef((Date) cert[4]);
			policyCerts.setCertWet((Date) cert[5]);
			policyCerts.setReasonCancelled((String) cert[7]);
			policyCertificates.add(policyCerts);

		}
		Page<PolicyCerts> page = new PageImpl<PolicyCerts>(policyCertificates);

				//policyCertsRepo.QueryPolicyCert(ipuCode, request);

		return new DataTablesResult(request, page);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public void autoGenerateCert(Long riskId) throws BadRequestException {
		if(riskRepo.count(QRiskTrans.riskTrans.riskId.eq(riskId))==0) return;
		RiskTrans risk = riskRepo.findOne(riskId);
		long branchCertCount = branchCertsRepo.count(QBranchCerts.branchCerts.branch.obId.eq(risk.getPolicy().getBranch().getObId())
		                      .and(QBranchCerts.branchCerts.user.id.eq(userUtils.getCurrentUser().getId()))
		                       .and(QBranchCerts.branchCerts.certLots.subclass.subId.eq(risk.getSubclass().getSubId()))
		                       .and(QBranchCerts.branchCerts.currentLot.equalsIgnoreCase("Y")));

		if(branchCertCount!=1) return;
		BranchCerts branchCerts = branchCertsRepo.findOne(QBranchCerts.branchCerts.branch.obId.eq(risk.getPolicy().getBranch().getObId())
				.and(QBranchCerts.branchCerts.user.id.eq(userUtils.getCurrentUser().getId()))
				.and(QBranchCerts.branchCerts.certLots.subclass.subId.eq(risk.getSubclass().getSubId()))
				.and(QBranchCerts.branchCerts.currentLot.equalsIgnoreCase("Y")));
		  long lastPrintedCert = 0l;
		 if(branchCerts.getLastPrintedNo()==null){
		 	lastPrintedCert = branchCerts.getNoFrom();
		 }
		 else{
		 	lastPrintedCert = branchCerts.getLastPrintedNo();
		 }
		long remainingCerts = branchCerts.getNoTo() - lastPrintedCert;
		 if(remainingCerts ==0) return;
		 Date wefDate = risk.getWefDate();
		 if(wefDate.before(new Date())){
		 	wefDate = new Date();
		 }
		 if(risk.getWetDate().before(new Date())) return;
		long existingCertCount = policyCertsRepo.count(QPolicyCerts.policyCerts.riskId.eq(risk.getRiskIdentifier())
		                         .and(QPolicyCerts.policyCerts.certWet.goe(wefDate))
		                         .and(QPolicyCerts.policyCerts.status.notEqualsIgnoreCase("C"))
		                         .and(QPolicyCerts.policyCerts.certWef.between(risk.getWefDate(),risk.getWetDate())
								 .or(QPolicyCerts.policyCerts.certWet.between(risk.getWefDate(),risk.getWetDate()))));
		 if(existingCertCount > 0) return;
		 PolicyCerts policyCert = new PolicyCerts();
		 policyCert.setCert(branchCerts);
		 policyCert.setCertWef(wefDate);
		 policyCert.setCertWet(risk.getWetDate());
		 policyCert.setRiskId(risk.getRiskIdentifier());
		 policyCert.setRisk(risk);
		 policyCert.setStatus("A");
		 policyCert.setPrintStatus("R");

		 PrintCertificateQueue certificateQueue = new PrintCertificateQueue();
		 certificateQueue.setCertWef(wefDate);
		 certificateQueue.setCert(branchCerts);
		 certificateQueue.setStatus("N");
		 certificateQueue.setRisk(risk);
		 certificateQueue.setCertYear((new Long(dateUtilities.getUwYear(wefDate))));
		 certificateQueue.setClientName(risk.getInsured().getFname()+" "+risk.getInsured().getOtherNames());
		 certificateQueue.setPolicyCerts(policyCert);
		 certificateQueue.setDoneBy(userUtils.getCurrentUser());

		 policyCertsRepo.save(policyCert);
		 printQueueRepo.save(certificateQueue);

	}

	@Override
	@Transactional(readOnly = true)
	public Page<BranchCerts> findActiveLots(String paramString, Pageable paramPageable, Long riskId) throws IllegalAccessException {
		if(riskId==null) throw new IllegalAccessException("Select Risk to continue");
		RiskTrans risk = riskRepo.findOne(riskId);
		Predicate pred = null;
		if (paramString == null || StringUtils.isBlank(paramString)) {
			pred = QBranchCerts.branchCerts.branch.obId.eq(risk.getPolicy().getBranch().getObId())
					.and(QBranchCerts.branchCerts.user.id.eq(userUtils.getCurrentUser().getId()))
					.and(QBranchCerts.branchCerts.certLots.underwriter.acctId.eq(risk.getPolicy().getAgent().getAcctId()))
					.and(QBranchCerts.branchCerts.certLots.subclass.subId.eq(risk.getSubclass().getSubId()))
					.and(QBranchCerts.branchCerts.currentLot.equalsIgnoreCase("Y"));
		} else {
			pred = QBranchCerts.branchCerts.branch.obId.eq(risk.getPolicy().getBranch().getObId())
					.and(QBranchCerts.branchCerts.user.id.eq(userUtils.getCurrentUser().getId()))
					.and(QBranchCerts.branchCerts.certLots.subclass.subId.eq(risk.getSubclass().getSubId()))
					.and(QBranchCerts.branchCerts.certLots.underwriter.acctId.eq(risk.getPolicy().getAgent().getAcctId()))
					.and(QBranchCerts.branchCerts.currentLot.equalsIgnoreCase("Y"))
			        .and(QBranchCerts.branchCerts.certLots.certTypes.certDesc.containsIgnoreCase(paramString));
		}
		return branchCertsRepo.findAll(pred,paramPageable);
	}


	@Override
	@Transactional(readOnly = true)
	public Page<CertTypesDTO> findSubclassCertTypes(String searchValue, Pageable paramPageable, Long riskId) throws IllegalAccessException {
		if(riskId==null) throw new IllegalAccessException("Select Risk to continue");
		if(searchValue==null) searchValue="%%";
		else searchValue = "%"+searchValue+"%";
		List<Object[]> certTypesList = subclassCertTypesRepo.searchCertificateTypes(riskId, searchValue, paramPageable.getPageNumber(), paramPageable.getPageSize());
		long rowCount = 0L;
		if(!certTypesList.isEmpty()) rowCount = ((BigInteger)certTypesList.get(0)[2]).intValue();
		final List<CertTypesDTO> certTypesDTOList = new ArrayList<>();
		for(Object[] certType:certTypesList){
			final CertTypesDTO certTypesDTO = new CertTypesDTO();
			certTypesDTO.setSubclasscertId(((BigInteger)certType[0]).longValue());
			certTypesDTO.setCertDesc((String)certType[1]);
			certTypesDTOList.add(certTypesDTO);
		}
		return new PageImpl<>(certTypesDTOList,paramPageable, rowCount);
	}

	@Override
	@Transactional(readOnly = false,propagation = Propagation.NESTED)
	public void createRiskCert(RiskCertForm riskCertForm) throws BadRequestException {
		if(riskRepo.count(QRiskTrans.riskTrans.riskId.eq(riskCertForm.getRiskId()))==0)
			throw new BadRequestException("Error Getting Risk Details..Risk details are missing");
		RiskTrans riskTrans = riskRepo.QueryRiskTrans(riskCertForm.getRiskId());
		if(riskCertForm.getSubclasscertId()==null) throw new BadRequestException("Select Certificate Type to Add to the Risk");
		if(riskCertForm.getWefDate().after(riskCertForm.getWetDate())){
			throw new BadRequestException("Certificate Wef Date cannot be greater than Cert Wet Date");
		}

		if(riskCertForm.getWefDate().before(riskTrans.getWefDate())||
				riskCertForm.getWefDate().after(riskTrans.getWetDate())||
				riskCertForm.getWetDate().before(riskTrans.getWefDate()) ||
				riskCertForm.getWetDate().after(riskTrans.getWetDate())){
			throw new BadRequestException("Certificate Dates cannot be outside Risk Dates");
		}
		long count = policyCertsRepo.count(QPolicyCerts.policyCerts.risk.riskId.eq(riskTrans.getRiskId()));
		if(count > 0){
			Iterable<PolicyCerts> policyCerts = policyCertsRepo.findAll(QPolicyCerts.policyCerts.risk.riskId.eq(riskTrans.getRiskId()));
			for(PolicyCerts certs: policyCerts){

				if(certs.getCertNo()!=null && certs.getStatus()!=null && !certs.getStatus().equalsIgnoreCase("C")){
					throw new BadRequestException("Cannot delete an active certifcate");
				}
			}
			policyCertsRepo.delete(policyCerts);
			Iterable<PrintCertificateQueue> printCertificateQueues = printQueueRepo.findAllByRisk(riskTrans);
			printQueueRepo.delete(printCertificateQueues);
		}
		Date wefDate = riskCertForm.getWefDate();
		if(wefDate.before(new Date())){
			wefDate = new Date();
		}
		if(riskCertForm.getWetDate().before(new Date())) {
			throw new BadRequestException("Cannot generate Certificate for the Expired Risk");
		}
		PolicyCerts policyCert = new PolicyCerts();
		//policyCert.setCert(branchCerts);
		policyCert.setCertWef(wefDate);
		policyCert.setCertWet(riskCertForm.getWetDate());
		policyCert.setRiskId(riskTrans.getRiskIdentifier());
		policyCert.setRisk(riskTrans);
		policyCert.setStatus("A");
		policyCert.setPrintStatus("R");
//		policyCert.setRiskId(riskTrans.getRiskId());
		policyCert.setSubclassCertTypes(subclassCertTypesRepo.findOne(riskCertForm.getSubclasscertId()));
		PolicyCerts saved = policyCertsRepo.save(policyCert);
		PrintCertificateQueue certificateQueue = new PrintCertificateQueue();
		certificateQueue.setCertWef(wefDate);
		//certificateQueue.setCert(branchCerts);
		certificateQueue.setStatus("N");
		certificateQueue.setRisk(riskTrans);
		certificateQueue.setCertYear((new Long(dateUtilities.getUwYear(wefDate))));
		certificateQueue.setClientName(riskTrans.getInsured().getFname()+" "+riskTrans.getInsured().getOtherNames());
		certificateQueue.setPolicyCerts(saved);

		if(userUtils.getCurrentUser()==null){
			final UserDTO user = userRepository.findByUserName("Admin");
			certificateQueue.setDoneBy(userRepository.findById(user.getId()));
		}
		else{
			certificateQueue.setDoneBy(userUtils.getCurrentUser());
		}
		certificateQueue.setSubclassCertTypes(subclassCertTypesRepo.findOne(riskCertForm.getSubclasscertId()));
		printQueueRepo.save(certificateQueue);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public void updateRiskCertificate(EditRiskCertForm certForm) throws BadRequestException {
		if(certForm.getWefDate()==null) throw new BadRequestException("Wef Date cannot be null");
		if(certForm.getWetDate()==null) throw new BadRequestException("Wet Date cannot be null");
		if("C".equalsIgnoreCase(certForm.getStatus())){
			if(certForm.getReasonCancelled()==null || StringUtils.isBlank(certForm.getReasonCancelled())){
				throw new BadRequestException("Provide Reason to cancel the certificate");
			}
			if(certForm.getDateCancelled()==null){
				throw new BadRequestException("Provide Certificate Cancellation Date");
			}
		}
		if(certForm.getPcId()==null){
			throw new BadRequestException("Error Updating Risk Certificate. Contact System Administrator");
		}
		PolicyCerts policyCerts  = policyCertsRepo.findOne(certForm.getPcId());
		if(policyCerts==null) throw new BadRequestException("Error getting Certificate Details...");
		String printStatus = "";
		if(policyCerts.getPrintStatus()!=null) printStatus = policyCerts.getPrintStatus();
		if(!"P".equalsIgnoreCase(printStatus) && "C".equalsIgnoreCase(certForm.getStatus())){
			throw new BadRequestException("You cannot cancel a certificate that has not been printed");
		}

		if("P".equalsIgnoreCase(printStatus) && policyCerts.getCertNo()!=null ){
			System.out.println(certForm.getWefDate()+"=="+policyCerts.getCertWef());
			if(dateUtilities.removeTime(certForm.getWefDate()).compareTo(dateUtilities.removeTime(policyCerts.getCertWef()))!=0){
				throw  new BadRequestException("You cannot change wef Date of a printed certificate");
			}
			if(dateUtilities.removeTime(certForm.getWetDate()).compareTo(dateUtilities.removeTime(policyCerts.getCertWet()))!=0){
				throw  new BadRequestException("You cannot change Wet Date of a printed certificate");
			}
		}

		RiskTrans riskTrans = riskRepo.findOne(policyCerts.getRisk().getRiskId());

		if(certForm.getWefDate().before(riskTrans.getWefDate())||
				certForm.getWefDate().after(riskTrans.getWetDate())||
				certForm.getWetDate().before(riskTrans.getWefDate()) ||
				certForm.getWetDate().after(riskTrans.getWetDate())){
			throw new BadRequestException("Certificate Dates cannot be outside Risk Dates");
		}

		policyCerts.setCertWef(certForm.getWefDate());
		policyCerts.setCertWet(certForm.getWetDate());
		if("C".equalsIgnoreCase(certForm.getStatus())) {
			policyCerts.setReasonCancelled(certForm.getReasonCancelled());
			policyCerts.setCancelDate(certForm.getDateCancelled());
		}
		policyCerts.setStatus(certForm.getStatus());
		policyCertsRepo.save(policyCerts);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public void deleteRiskCertificate(Long pcId) throws BadRequestException {
		if(pcId==null){
			throw new BadRequestException("Error Deleting Risk Certificate. Contact System Administrator");
		}
		PolicyCerts policyCerts  = policyCertsRepo.findOne(pcId);
		String printStatus = "";
		if(policyCerts.getPrintStatus()!=null) printStatus = policyCerts.getPrintStatus();
		if("P".equalsIgnoreCase(printStatus) && policyCerts.getCertNo()!=null){
			throw  new BadRequestException("You cannot delete a printed certificate");
		}
		if (policyCerts.getCertNo()!=null){
			throw  new BadRequestException("Deallocate the certificate before deleting");
		}
		PrintCertificateQueue queue = printQueueRepo.findOne(QPrintCertificateQueue.printCertificateQueue.policyCerts.pcId.eq(pcId));
		printQueueRepo.delete(queue.getCqId());
		policyCertsRepo.delete(pcId);

	}

	@Override
	public void deleteRiskBeneficiary(Long benId) throws BadRequestException {
		if(benId==null){
			throw new BadRequestException("Unable to get beneficiary details to delete");
		}
		beneficiariesRepo.delete(benId);
	}

	@Override
	public void importRiskBeneficiaries(RiskBeneficiariesImport beneficiariesImport) throws BadRequestException {
		if (beneficiariesImport.getFile() == null) throw new BadRequestException("No File to import...");
		if (beneficiariesImport.getRiskId() == null)
			throw new BadRequestException("Select Risk to import Beneficiaries");
		if (!beneficiariesImport.getFile().getOriginalFilename().endsWith(".csv"))
			throw new BadRequestException("Can only Upload CSV File");
		try (CsvParser csvParser = csvReader.parse(multipartToFile(beneficiariesImport.getFile()), StandardCharsets.UTF_8)) {
			CsvRow row;
			List<WIBABeneficiaries> beneficiariesList = new ArrayList<>();
			while ((row = csvParser.nextRow()) != null) {
				WIBABeneficiaries beneficiaries = new WIBABeneficiaries();
				beneficiaries.setFullName(row.getField(0));
				beneficiaries.setOccupation(row.getField(1));
				beneficiaries.setRiskTrans(riskTransRepo.findOne(beneficiariesImport.getRiskId()));
				if (row.getField(2) == null || StringUtils.isBlank(row.getField(2))) {

				} else {
					try {
						double val = Double.parseDouble(row.getField(2));
						beneficiaries.setSalary(BigDecimal.valueOf(val));
					} catch (NumberFormatException ex) {
						throw new BadRequestException(ex.getMessage());
					}
				}
				beneficiariesList.add(beneficiaries);
			}
			beneficiariesRepo.save(beneficiariesList);
		} catch (IOException e) {
			throw new BadRequestException(e.getMessage());
		}
	}

	private File multipartToFile(MultipartFile multipart) throws IllegalStateException, IOException
	{
		File convFile = new File( multipart.getOriginalFilename());
		if(!convFile.exists()) convFile.delete();
		multipart.transferTo(convFile);
		return convFile;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<BranchCerts> findAllLots(String paramString, Pageable paramPageable, Long certCode,Long brnCode,Long acctCode) {
		Predicate pred = null;
		if (paramString == null || StringUtils.isBlank(paramString)) {
			pred = QBranchCerts.branchCerts.certLots.subclassCertType.subclasscertId.eq(certCode)
					.and(QBranchCerts.branchCerts.branch.obId.eq(brnCode))
					.and(QBranchCerts.branchCerts.certLots.underwriter.acctId.eq(acctCode))
					.and(QBranchCerts.branchCerts.isNotNull())
					.and(QBranchCerts.branchCerts.status.equalsIgnoreCase("A"));
		}
		else{
			pred = QBranchCerts.branchCerts.certLots.subclassCertType.subclasscertId.eq(certCode)
					.and(QBranchCerts.branchCerts.branch.obId.eq(brnCode))
					.and(QBranchCerts.branchCerts.certLots.underwriter.acctId.eq(acctCode))
					.and(QBranchCerts.branchCerts.certLots.certTypes.certDesc.containsIgnoreCase(paramString))
					.and(QBranchCerts.branchCerts.status.equalsIgnoreCase("A"));
		}
		return branchCertsRepo.findAll(pred, paramPageable);
	}

	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<PrintCertificateQueue> findCertToPrint(DataTablesRequest request, Long certCode, Long brnCode, Long acctCode ,String certStatus, String polNo, String riskId) throws IllegalAccessException {
      Page<PrintCertificateQueue> page = printQueueRepo.getPrintCertificateQueue(brnCode,certCode,acctCode, certStatus,  polNo,  riskId,request);


        return new DataTablesResult(request, page);
	}


	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<PolicyCertificateDTO> findPolCertToPrint(DataTablesRequest request,  Long polId) {
		List<Object[]> certToPrint = printQueueRepo.getPolCertToPrint(polId,request.getPageNumber(), request.getPageSize());
		long rowCount = 0L;
		if(!certToPrint.isEmpty()) rowCount = ((BigInteger)certToPrint.get(0)[8]).intValue();
		final List<PolicyCertificateDTO> certificateDTOList = new ArrayList<>();
		for(Object[] obj:certToPrint){
			PolicyCertificateDTO certificateDTO = new PolicyCertificateDTO();
			certificateDTO.setCqId(((BigInteger)obj[0]).longValue());
			certificateDTO.setCertWef((Date) obj[1]);
			certificateDTO.setRiskId((String)obj[2]);
			certificateDTO.setPolicyWef((Date) obj[3]);
			certificateDTO.setPolicyWet((Date) obj[4]);
			certificateDTO.setStatus((String)obj[5]);
			certificateDTO.setUsername((String)obj[6]);
			if(obj[7]!=null){
				certificateDTO.setCertNo(((BigDecimal)obj[7]).longValue());
			}
			certificateDTOList.add(certificateDTO);
		}
		Page<PolicyCertificateDTO>  page = new PageImpl<>(certificateDTOList,request, rowCount);
		return new DataTablesResult<>(request, page);
	}
	@Override
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public void allocatePolCerts(PrintCertBean certBean) throws BadRequestException {

		List<PrintCertificateQueue> allCerts = new ArrayList<>();
		List<PolicyCerts> polCerts = new ArrayList<>();
		for(Long printCode:certBean.getCerts()){


			PrintCertificateQueue printCertificateQueue = printQueueRepo.findOne(printCode);

			if(cashBasisBalance(printCertificateQueue.getPolicyCerts().getRisk().getPolicy().getPolicyId()).compareTo(BigDecimal.ZERO) > 0){
				throw new BadRequestException("Cannot Allocate Certificate to A Policy with pending balance ");
			}

			if(!printCertificateQueue.getPolicyCerts().getRisk().getPolicy().getAuthStatus().equalsIgnoreCase("A")){
				throw new BadRequestException("Cannot Allocate Certificate to An UnAuthorized Policy!! ");
			}

			Predicate pred = QBranchCerts.branchCerts.branch.obId.eq(printCertificateQueue.getPolicyCerts().getRisk().getPolicy().getBranch().getObId())
					.and(QBranchCerts.branchCerts.user.id.eq(userUtils.getCurrentUser().getId()))
					.and(QBranchCerts.branchCerts.certLots.underwriter.acctId.eq(printCertificateQueue.getPolicyCerts().getRisk().getPolicy().getAgent().getAcctId()))
					.and(QBranchCerts.branchCerts.certLots.subclass.subId.eq(printCertificateQueue.getPolicyCerts().getRisk().getSubclass().getSubId()))
					.and(QBranchCerts.branchCerts.currentLot.equalsIgnoreCase("Y"))
					.and(QBranchCerts.branchCerts.status.equalsIgnoreCase("D"));
			//BranchCerts  branchCert = branchCertsRepo.findOne(pred);
			BranchCerts branchCerts = branchCertsRepo.findOne(pred);
			if(branchCerts==null) throw new BadRequestException("The logged in user does not have certificate to allocate");
			if(printCertificateQueue.getCertNo()==null) {
				Long certNo = branchCerts.getLastPrintedNo();
				if (branchCerts.getLastPrintedNo() == null)
					certNo = branchCerts.getNoFrom();
				else
					certNo = branchCerts.getLastPrintedNo() + 1;

				Long lastCertNo = certNo;
				Long certTo = branchCerts.getNoTo();

				long count = printQueueRepo.count(QPrintCertificateQueue.printCertificateQueue.cert.brnCertId.eq(branchCerts.getBrnCertId())
						.and(QPrintCertificateQueue.printCertificateQueue.certNo.between(certNo, certTo)));
				if (count > 0) {
					throw new BadRequestException("Some Certificates are duplicated on this lot. Cannot Allocate certNo" + certNo + " certTo " + certTo);
				}

				if (certNo > certTo) {
					throw new BadRequestException("Certificate lot already fully allocated");
				}
				if (printCode == null) throw new BadRequestException("Error getting Certificate Details");


				Date printTime = printCertificateQueue.getCertWef();
				printCertificateQueue.setCertNo(certNo);
				printCertificateQueue.setStatus("R");
				printCertificateQueue.setGoodForPrint("Y");
				printCertificateQueue.setCert(branchCerts);
				printCertificateQueue.setDoneBy(userUtils.getCurrentUser());
				printCertificateQueue.setAllocBy(userUtils.getCurrentUser());
				printCertificateQueue.setCertYear(new Long(dateUtilities.getUwYear(printCertificateQueue.getPolicyCerts().getCertWef())));
				if (printTime == null) {
					printCertificateQueue.setCertWef(new Date());
				} else if (printTime.before(new Date()))
					printCertificateQueue.setCertWef(new Date());
				else
					printCertificateQueue.setCertWef(printTime);

				PolicyCerts policyCerts = printCertificateQueue.getPolicyCerts();
				policyCerts.setCertNo(certNo);
				policyCerts.setCert(branchCerts);
				polCerts.add(policyCerts);

				allCerts.add(printCertificateQueue);
				branchCerts.setLastPrintedNo(lastCertNo);
				System.out.println(lastCertNo+" == "+certTo);
				if (lastCertNo.equals(certTo)) {
					branchCerts.setCurrentLot("N");
				}
				branchCertsRepo.save(branchCerts);
			}
			else throw new BadRequestException("Certificate already allocated");

		}
		printQueueRepo.save(allCerts);
		policyCertsRepo.save(polCerts);

	}
	@Override
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public void allocateCerts(PrintCertBean certBean) throws BadRequestException {
		if(certBean.getBranchCert()==null) throw new BadRequestException("Select Certificate to Allocate");
		List<PrintCertificateQueue> allCerts = new ArrayList<>();
		List<PolicyCerts> polCerts = new ArrayList<>();
		for(Long printCode:certBean.getCerts()){
			BranchCerts branchCerts = branchCertsRepo.findOne(certBean.getBranchCert());
			PrintCertificateQueue printCertificateQueue = printQueueRepo.findOne(printCode);
			if(cashBasisBalance(printCertificateQueue.getPolicyCerts().getRisk().getPolicy().getPolicyId()).compareTo(BigDecimal.ZERO) > 0){
				throw new BadRequestException("Cannot Allocate Certificate to A Policy with pending balance ");
			}

			if(printCertificateQueue.getCertNo()==null) {
				Long certNo = branchCerts.getLastPrintedNo();
				if (branchCerts.getLastPrintedNo() == null)
					certNo = branchCerts.getNoFrom();
				else
					certNo = branchCerts.getLastPrintedNo() + 1;

				Long lastCertNo = certNo;
				Long certTo = branchCerts.getNoTo();

				Long count = printQueueRepo.count(QPrintCertificateQueue.printCertificateQueue.cert.brnCertId.eq(certBean.getBranchCert())
						.and(QPrintCertificateQueue.printCertificateQueue.certNo.between(certNo, certTo)));
				if (count > 0) {
					throw new BadRequestException("Some Certificates are duplicated on this lot. Cannot Allocate certNo" + certNo + " certTo " + certTo);
				}

				if (certNo > certTo) {
					throw new BadRequestException("Certificate lot already fully allocated");
				}
				if (printCode == null) throw new BadRequestException("Error getting Certificate Details");


				Date printTime = printCertificateQueue.getCertWef();
				printCertificateQueue.setCertNo(certNo);
				printCertificateQueue.setStatus("R");
				printCertificateQueue.setGoodForPrint("Y");
				printCertificateQueue.setCert(branchCerts);
				printCertificateQueue.setDoneBy(userUtils.getCurrentUser());
				printCertificateQueue.setAllocBy(userUtils.getCurrentUser());
				printCertificateQueue.setCertYear(new Long(dateUtilities.getUwYear(printCertificateQueue.getPolicyCerts().getCertWef())));
				if (printTime == null) {
					printCertificateQueue.setCertWef(new Date());
				} else if (printTime.before(new Date()))
					printCertificateQueue.setCertWef(new Date());
				else
					printCertificateQueue.setCertWef(printTime);

				PolicyCerts policyCerts = printCertificateQueue.getPolicyCerts();
				policyCerts.setCertNo(certNo);
				policyCerts.setCert(branchCerts);
				polCerts.add(policyCerts);

				allCerts.add(printCertificateQueue);
				branchCerts.setLastPrintedNo(lastCertNo);
				System.out.println(lastCertNo+" == "+certTo);
				if (lastCertNo.equals(certTo)) {
					branchCerts.setCurrentLot("N");
				}
				branchCertsRepo.save(branchCerts);
			}

		}
		printQueueRepo.save(allCerts);
		policyCertsRepo.save(polCerts);

	}
	@Override
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public void deallocatePolCerts(PrintCertBean certBean) throws BadRequestException {
		if(certBean.getCerts()==null) throw new BadRequestException("Select Certificate to Deallocate");
		Long minCertNo=0l;
		Long maxCertNo=0l;
		int count = 0;
		for(Long printCode:certBean.getCerts()){
			PrintCertificateQueue printCertificateQueue = printQueueRepo.findOne(printCode);
			Predicate pred = QBranchCerts.branchCerts.branch.obId.eq(printCertificateQueue.getPolicyCerts().getRisk().getPolicy().getBranch().getObId())
					.and(QBranchCerts.branchCerts.user.id.eq(userUtils.getCurrentUser().getId()))
					.and(QBranchCerts.branchCerts.certLots.underwriter.acctId.eq(printCertificateQueue.getPolicyCerts().getRisk().getPolicy().getAgent().getAcctId()))
					.and(QBranchCerts.branchCerts.certLots.subclass.subId.eq(printCertificateQueue.getPolicyCerts().getRisk().getSubclass().getSubId()))
					.and(QBranchCerts.branchCerts.currentLot.equalsIgnoreCase("Y"));
			//BranchCerts  branchCert = branchCertsRepo.findOne(pred);
			BranchCerts branchCert = branchCertsRepo.findOne(pred);
			if(printCertificateQueue.getCertNo()!=null){
				count++;
				if(count==1){
					minCertNo = printCertificateQueue.getCertNo();
					maxCertNo = printCertificateQueue.getCertNo();
				}
				else{
					if(minCertNo > printCertificateQueue.getCertNo()){
						minCertNo = printCertificateQueue.getCertNo();
					}
					else if(maxCertNo < printCertificateQueue.getCertNo()){
						maxCertNo = printCertificateQueue.getCertNo();
					}
				}
			}
			if(count > 0){

				if(minCertNo + (count-1) != maxCertNo){
					throw new BadRequestException("The current certificate allocation cannot be undone. Some certificates are missing in the sequence");
				}

				Long printedCount = printQueueRepo.count(QPrintCertificateQueue.printCertificateQueue.cert.brnCertId.eq(branchCert.getBrnCertId())
						.and(QPrintCertificateQueue.printCertificateQueue.status.eq("P"))
						.and(QPrintCertificateQueue.printCertificateQueue.certNo.between(minCertNo,maxCertNo)));

				if(printedCount > 0) throw new BadRequestException("Some of the certificates in the series to deallocated have been printed");

				List<PrintCertificateQueue> allCerts = new ArrayList<>();
				List<PolicyCerts> polCerts = new ArrayList<>();
				if (printCertificateQueue.getCertNo() != null) {
					printCertificateQueue.setCertNo(null);
					printCertificateQueue.setCert(null);
					printCertificateQueue.setStatus("N");
					printCertificateQueue.setGoodForPrint("N");
					//printCertificateQueue.setCertWef(null);
					printCertificateQueue.setCertYear(null);
					//printCertificateQueue.setDoneBy(null);
					//printCertificateQueue.setAllocBy(null);
					PolicyCerts policyCert = printCertificateQueue.getPolicyCerts();
					policyCert.setCertNo(null);
					policyCert.setCert(null);
					//policyCert.setAllocBy(null);
					policyCert.setStatus("R");
					allCerts.add(printCertificateQueue);
					polCerts.add(policyCert);
				}
				if ("N".equalsIgnoreCase(branchCert.getCurrentLot())){
					Long currLotCount  = branchCertsRepo.count(QBranchCerts.branchCerts.currentLot.eq("Y")
							.and(QBranchCerts.branchCerts.user.id.eq(branchCert.getUser().getId()))
							.and(QBranchCerts.branchCerts.branch.obId.eq(branchCert.getBranch().getObId()))
							.and(QBranchCerts.branchCerts.certLots.underwriter.acctId.eq(branchCert.getCertLots().getUnderwriter().getAcctId())))  ;
					if (currLotCount==0){
						branchCert.setCurrentLot("Y");
					}
				}
				branchCert.setLastPrintedNo(branchCert.getLastPrintedNo()-count);
				branchCertsRepo.save(branchCert);
				printQueueRepo.save(allCerts);
				policyCertsRepo.save(polCerts);

			}
		}


	}
	@Override
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public void deallocateCerts(PrintCertBean certBean) throws BadRequestException {
		if(certBean.getBranchCert()==null) throw new BadRequestException("Select Certificate to Deallocate");
		Long minCertNo=0l;
		Long maxCertNo=0l;
		int count = 0;
		for(Long printCode:certBean.getCerts()){
			PrintCertificateQueue printCertificateQueue = printQueueRepo.findOne(printCode);
			if(printCertificateQueue.getCertNo()!=null){
				count++;
				if(count==1){
					minCertNo = printCertificateQueue.getCertNo();
					maxCertNo = printCertificateQueue.getCertNo();
				}
				else{
					if(minCertNo > printCertificateQueue.getCertNo()){
						minCertNo = printCertificateQueue.getCertNo();
					}
					else if(maxCertNo < printCertificateQueue.getCertNo()){
						maxCertNo = printCertificateQueue.getCertNo();
					}
				}
			}

		}

		if(count > 0){
			if(minCertNo + (count-1) != maxCertNo){
				throw new BadRequestException("The current certificate allocation cannot be undone. Some certificates missing in the sequence");
			}

			Long printedCount = printQueueRepo.count(QPrintCertificateQueue.printCertificateQueue.cert.brnCertId.eq(certBean.getBranchCert())
			                                         .and(QPrintCertificateQueue.printCertificateQueue.status.eq("P"))
			                                         .and(QPrintCertificateQueue.printCertificateQueue.certNo.between(minCertNo,maxCertNo)));

			if(printedCount > 0) throw new BadRequestException("Some of the certificates in the series to deallocated have been printed");

			List<PrintCertificateQueue> allCerts = new ArrayList<>();
			List<PolicyCerts> polCerts = new ArrayList<>();
			for(Long printCode:certBean.getCerts()) {
				PrintCertificateQueue printCertificateQueue = printQueueRepo.findOne(printCode);
				if (printCertificateQueue.getCertNo() != null) {
					printCertificateQueue.setCertNo(null);
					printCertificateQueue.setStatus("N");
					printCertificateQueue.setGoodForPrint("N");
					//printCertificateQueue.setCertWef(null);
					printCertificateQueue.setCertYear(null);
					printCertificateQueue.setCert(null);
					//printCertificateQueue.setDoneBy(null);
					//printCertificateQueue.setAllocBy(null);
					PolicyCerts policyCert = printCertificateQueue.getPolicyCerts();
					policyCert.setCertNo(null);
					policyCert.setCert(null);
					//policyCert.setAllocBy(null);
					policyCert.setStatus("R");
					allCerts.add(printCertificateQueue);
					polCerts.add(policyCert);
				}
			}
			BranchCerts branchCert = branchCertsRepo.findOne(certBean.getBranchCert());
			if ("N".equalsIgnoreCase(branchCert.getCurrentLot())){
				Long currLotCount  = branchCertsRepo.count(QBranchCerts.branchCerts.currentLot.eq("Y")
						.and(QBranchCerts.branchCerts.user.id.eq(branchCert.getUser().getId()))
						.and(QBranchCerts.branchCerts.branch.obId.eq(branchCert.getBranch().getObId()))
						.and(QBranchCerts.branchCerts.certLots.underwriter.acctId.eq(branchCert.getCertLots().getUnderwriter().getAcctId())))  ;
			if (currLotCount==0){
				branchCert.setCurrentLot("Y");
				}
			}
			branchCert.setLastPrintedNo(branchCert.getLastPrintedNo()-count);
			branchCertsRepo.save(branchCert);
			printQueueRepo.save(allCerts);
			policyCertsRepo.save(polCerts);

		}

	}




	@Override
	@Transactional(readOnly = false)
	public void createBatchCerts(List<Long> certCodes) {
		final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.datasource);
		jdbcTemplate.update("delete from sys_brk_certificate_print where cerp_user_code = ?", new Object[]{userUtils.getCurrentUser().getId()});
		String sql = "insert into sys_brk_certificate_print(cerp_user_code,cerp_cq_id) values(?,?)";
		for(Long certCode:certCodes){
			List<Object[]> certs = printQueueRepo.SearchCertsById(certCode);
			for(Object[] cert: certs){
				final String status = (String)cert[0];
				final BigDecimal certNo = (BigDecimal)cert[1];
				if(certNo!=null && status.equalsIgnoreCase("R")){
					jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
						@Override
						public void setValues(PreparedStatement ps, int i) throws SQLException {
							ps.setLong(1, userUtils.getCurrentUser().getId());
							ps.setLong(2, certCode );
						}
						@Override
						public int getBatchSize() {
							return certCodes.size();
						}

					});
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void markCertPrinted() {
		List<PrintCertificateQueue> printcerts = new ArrayList<>();
		List<PolicyCerts> polCerts = new ArrayList<>();
		Iterable<CertPrint> prints = printRepo.findAll(QCertPrint.certPrint.user.id.eq(userUtils.getCurrentUser().getId()));
		for(CertPrint certCode:prints) {
			PrintCertificateQueue certificateQueue = certCode.getCertQueue();
			certificateQueue.setStatus("P");
			PolicyCerts policyCerts = certificateQueue.getPolicyCerts();
			policyCerts.setPrintDt(new Date());
			policyCerts.setPrintStatus("P");
			printcerts.add(certificateQueue);
			polCerts.add(policyCerts);
		}
		printQueueRepo.save(printcerts);
		policyCertsRepo.save(polCerts);
	}
	@Override
	@Transactional(readOnly = false)
	public void createSubClassCertType(CertTypeSubclassBean subclassCertType) {
		List<SubclassCertTypes> subclassCertTypes = new ArrayList<>();
		for(Long subCode:subclassCertType.getSubclasses()){
			SubclassCertTypes certtype = new SubclassCertTypes();
			certtype.setSubclass(subclassRepo.findOne(subCode));
			certtype.setCertType(certRepo.findOne(subclassCertType.getCertTypeId()));
			subclassCertTypes.add(certtype);
		}
		subclassCertTypesRepo.save(subclassCertTypes);

	}
	@Override
	@Transactional(readOnly = false)
	public void batchPolicyCerts(Long polCode) throws BadRequestException {
		PolicyTrans policyTrans = policyTransRepo.findOne(polCode);
		if(policyTrans==null)
			throw new BadRequestException("Error getting Policy Details...");
		if(policyTrans.getAuthStatus()!=null && !"A".equalsIgnoreCase(policyTrans.getAuthStatus())){
			throw new BadRequestException("Cannot Print Certificate for Unauthorised Transactions...");
		}
		Iterable<RiskTrans> riskTrans = riskRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(polCode));
		if(riskTrans.spliterator().getExactSizeIfKnown()==0){
			throw new BadRequestException("No Risks to print Certificates...");
		}
		List<PrintCertificateQueue> certificateQueues = new ArrayList<>();
		List<PolicyCerts> policyCertss = new ArrayList<>();
		Iterable<CertPrint> prints = printRepo.findAll(QCertPrint.certPrint.user.id.eq(userUtils.getCurrentUser().getId()));
		for(CertPrint certPrint:prints){
			printRepo.delete(certPrint.getCerpId());
		}
		List<Long> certPrints = new ArrayList<>();
		for(RiskTrans riskTran:riskTrans){
			long count = policyCertsRepo.count(QPolicyCerts.policyCerts.risk.riskId.eq(riskTran.getRiskId())
					.and(QPolicyCerts.policyCerts.printStatus.isNull().or(QPolicyCerts.policyCerts.printStatus.ne("P"))));
			if(count==1){
				PolicyCerts policyCerts = policyCertsRepo.findOne(QPolicyCerts.policyCerts.risk.riskId.eq(riskTran.getRiskId())
						.and(QPolicyCerts.policyCerts.printStatus.isNull().or(QPolicyCerts.policyCerts.printStatus.ne("P"))));
				OrgBranch branch = branchRepository.findOne(policyTrans.getBranch().getObId());
				CertTypes certTypes = policyCerts.getCert().getCertLots().getCertTypes();
				Predicate pred = QBranchCerts.branchCerts.certLots.certTypes.certId.eq(certTypes.getCertId())
						.and(QBranchCerts.branchCerts.branch.obId.eq(branch.getObId()))
						.and(QBranchCerts.branchCerts.noTo.gt(QBranchCerts.branchCerts.lastPrintedNo.coalesce(0l)))
						.and(QBranchCerts.branchCerts.user.id.eq(userUtils.getCurrentUser().getId()))
						.and(QBranchCerts.branchCerts.certLots.underwriter.acctId.eq(policyTrans.getAgent().getAcctId()))
						.and(QBranchCerts.branchCerts.isNotNull());
//				System.out.println("cert id "+certTypes.getCertId()+" branch id "+branch.getObId()+" acct id "+policyTrans.getAgent().getAcctId());
				if(branchCertsRepo.count(pred)> 1){
					throw new BadRequestException("More than one lot available for allocation to print certificate for risk "+riskTran.getRiskShtDesc());
				}
				if(branchCertsRepo.count(pred)==0){
					throw new BadRequestException("No lot available for allocation to print certificate for risk "+riskTran.getRiskShtDesc());
				}
				BranchCerts branchCerts = branchCertsRepo.findOne(pred);
				PrintCertificateQueue certificateQueue = printQueueRepo.findOne(QPrintCertificateQueue.printCertificateQueue.policyCerts.pcId.eq(policyCerts.getPcId()));
				certPrints.add(certificateQueue.getCqId());
				if(certificateQueue.getCertNo()==null) {
					Long certNo =0l;
					if (branchCerts.getLastPrintedNo() == null)
						certNo = branchCerts.getNoFrom();
					else
						certNo = branchCerts.getLastPrintedNo() + 1;

					Long lastCertNo = certNo;
					Long certTo = branchCerts.getNoTo();

					Long countCert = printQueueRepo.count(QPrintCertificateQueue.printCertificateQueue.cert.brnCertId.eq(branchCerts.getBrnCertId())
							.and(QPrintCertificateQueue.printCertificateQueue.certNo.between(certNo, certTo)));
					if (countCert > 0) {
						throw new BadRequestException("Some Certificates are duplicated on this lot. Cannot Allocate certNo" + certNo + " certTo " + certTo);
					}

					if (certNo > certTo) {
						throw new BadRequestException("Certificate lot already fully allocated");
					}
					policyCerts.setCertNo(certNo);
					policyCerts.setAllocBy(userUtils.getCurrentUser());
					//policyCerts.setStatus("A");
					Date printTime = certificateQueue.getCertWef();
					certificateQueue.setCertNo(certNo);
					certificateQueue.setStatus("R");
					certificateQueue.setGoodForPrint("Y");
					certificateQueue.setDoneBy(userUtils.getCurrentUser());
					certificateQueue.setAllocBy(userUtils.getCurrentUser());
					if (printTime == null) {
						certificateQueue.setCertWef(new Date());
					} else if (printTime.before(new Date()))
						certificateQueue.setCertWef(new Date());
					else
						certificateQueue.setCertWef(printTime);
					certificateQueues.add(certificateQueue);
					policyCertss.add(policyCerts);
					branchCerts.setLastPrintedNo(lastCertNo);
					branchCertsRepo.save(branchCerts);
				}

			}
		}
		if(certPrints.size()==0)
			throw new BadRequestException("No Certificate Available to printed...");
		printQueueRepo.save(certificateQueues);
		policyCertsRepo.save(policyCertss);
		createBatchCerts(certPrints);
	}

	private BigDecimal cashBasisBalance(Long polCode){
		SystemTransactionsTemp systemTransactionsTemp = systemTransactionsTempRepo.findOne(QSystemTransactionsTemp.systemTransactionsTemp.policy.policyId.eq(polCode));
		if(systemTransactionsTemp!=null && systemTransactionsTemp.getBalance().compareTo(BigDecimal.ZERO)==1){
			return systemTransactionsTemp.getBalance();
		}
		PolicyTrans policy = policyTransRepo.findOne(polCode);
		boolean cashBasis = policy.getInterfaceType()!=null && "C".equalsIgnoreCase(policy.getInterfaceType());
		BigDecimal prems = (policy.getPremium()==null)?BigDecimal.ZERO:policy.getPremium();
		if(cashBasis && prems.compareTo(BigDecimal.ZERO)==1) {
			return prems;
		}
		return BigDecimal.ZERO;
	}


}
