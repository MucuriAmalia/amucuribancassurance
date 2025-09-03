package com.brokersystems.brokerapp.certs.controller;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.brokersystems.brokerapp.certs.model.*;
import com.brokersystems.brokerapp.reports.model.ReportData;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.model.SubClassDef;
import com.brokersystems.brokerapp.uw.model.BatchRenewalForm;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import com.brokersystems.brokerapp.certs.service.CertService;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.ClassesDef;
import com.brokersystems.brokerapp.setup.model.SubclassSections;
import com.brokersystems.brokerapp.trans.model.ReceiptTrans;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

@Controller
@RequestMapping({ "/protected/certs" })
public class CertController {

	@Autowired
	private CertService certService;

	@Autowired
	private DataSource datasource;

	@Autowired
	private UserUtils userUtils;

	@Autowired
	private AuditTrailLogger auditTrailLogger;


	@RequestMapping(value = "certtype", method = RequestMethod.GET)
	public String certtypes(Model model, HttpServletRequest request) {
		auditTrailLogger.log("Accessed Certificate Types Screen ",request, "Certificate");
		return "certtypes";
	}

	@RequestMapping(value = "certlots", method = RequestMethod.GET)
	public String certLots(Model model) {
		return "certlotsreg";
	}

	@RequestMapping(value = "brncerts", method = RequestMethod.GET)
	public String branchCerts(Model model, HttpServletRequest request) {
		auditTrailLogger.log("Accessed Certificate Administration Screen ",request, "Certificate Administration");
		return "brncerts";
	}

	@RequestMapping(value = "prntcerts", method = RequestMethod.GET)
	public String printCerts(Model model, HttpServletRequest request) {
		auditTrailLogger.log("Accessed Print Certificate Screen ",request, "Print Certificates");
		return "printcerts";
	}

	@RequestMapping(value = {"allcerttypes"}, method = {RequestMethod.GET})
	@ResponseBody
	public DataTablesResult<CertTypes> getAllCertTypes(@DataTable DataTablesRequest pageable)
			throws IllegalAccessException {
		return certService.findAllCertTypes(pageable);
	}


	@RequestMapping(value = {"certTypesSubclasses/{certTypeCode}"}, method = {RequestMethod.GET})
	@ResponseBody
	public DataTablesResult<SubclassCertTypes> certTypesSubclasses(@DataTable DataTablesRequest pageable,@PathVariable Long certTypeCode, HttpServletRequest request)
			throws IllegalAccessException {
		auditTrailLogger.log("Accessed Certificates Subclasses Screen ",request, "Certificates Subclasses");
		return certService.findAllCertTypesSubclass(pageable,certTypeCode);
	}

	@RequestMapping(value = {"createCertType"}, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST})
	@ResponseBody
	public void createCertType(CertTypes cert) throws IllegalAccessException, IOException, BadRequestException {
		certService.createCertType(cert);
	}

	@RequestMapping(value = {"deleteCertType/{certId}"}, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCertType(@PathVariable Long certId) {
		certService.deleteCertType(certId);
	}

	@RequestMapping(value = {"deleteSubclassCertType/{subclasscertId}"}, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteSubclassCertType(@PathVariable Long subclasscertId) {
		certService.deleteSubclassCertType(subclasscertId);
	}

	@RequestMapping(value = { "getCertUnassignedSubclasses" }, method = { RequestMethod.GET })
	@ResponseBody
	public List<SubClassDef> getCertUnassignedSubclasses()
			throws IllegalAccessException {
		return certService.findCertUnassignedSubclasses();
	}

	@RequestMapping(value = { "createSubclassCertTypes" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	public ResponseEntity<String> createSubclassCertTypes(@RequestBody CertTypeSubclassBean subclassCertType) throws IllegalAccessException, IOException, BadRequestException {
		certService.createSubClassCertType(subclassCertType);
		return new ResponseEntity<String>("OK",HttpStatus.OK);
	}

	@RequestMapping(value = {"insafecerts"}, method = {RequestMethod.GET})
	@ResponseBody
	public DataTablesResult<CertLots> getUnderwriterCerts(@DataTable DataTablesRequest pageable)
			throws IllegalAccessException {
		return certService.findAllCertLots(pageable);
	}

	@RequestMapping(value = {"createCertLots"}, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST})
	@ResponseBody
	public void createCertLots(CertLots cert) throws IllegalAccessException, IOException, BadRequestException {
		certService.createCertLots(cert);
	}

	@RequestMapping(value = {"selCertTypes"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
	@ResponseBody
	public Page<CertTypes> selCertTypes(@RequestParam(value = "term", required = false) String term, Pageable pageable)
			throws IllegalAccessException {
		return certService.selectCertTypes(term, pageable);
	}


	@RequestMapping(value = {"deleteCertLot/{certId}"}, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCertLot(@PathVariable Long certId) {
		certService.deleteCertLot(certId);
	}

	@RequestMapping(value = {"createBranchCerts"}, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST})
	@ResponseBody
	public void createBranchCerts(BranchCerts cert) throws IllegalAccessException, IOException, BadRequestException {
		certService.createBranchCerts(cert);
	}

	@RequestMapping(value = {"reassignBranchCerts"}, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST})
	@ResponseBody
	public void reassignBranchCerts(BranchCerts cert,HttpServletRequest request) throws IllegalAccessException, IOException, BadRequestException {
		certService.reassignBranchCerts(cert,request);
	}

	@RequestMapping(value = {"outsafebranchcerts/{lotCode}"}, method = {RequestMethod.GET})
	@ResponseBody
	public DataTablesResult<BranchCerts> getBranchCertificates(@DataTable DataTablesRequest pageable,@PathVariable Long lotCode)
			throws IllegalAccessException {
		return certService.findAllBranchCerts(pageable,lotCode);
	}

	@RequestMapping(value = {"selCertLots"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
	@ResponseBody
	public Page<CertLots> selCertLots(@RequestParam(value = "term", required = false) String term, Pageable pageable)
			throws IllegalAccessException {
		return certService.selectCertLots(term, pageable);
	}

	@RequestMapping(value = {"selectSubclassCertTypes"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
	@ResponseBody
	public Page<SubclassCertTypes> selectSubclassCertTypes(@RequestParam(value = "term", required = false) String term, Pageable pageable)
			throws IllegalAccessException {
		return certService.selectSubclassCertTypes(term, pageable);
	}

	@RequestMapping(value = {"deallocateLot/{brnCertId}"}, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deallocateLot(@PathVariable Long brnCertId, @RequestParam(value = "remarks", required = false) String remarks,HttpServletRequest request) throws BadRequestException {
		certService.deallocateBranchCert(brnCertId, remarks,request);
	}

	@RequestMapping(value = {"allocateForPrint/{brnCertId}"}, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void allocateForPrint(@PathVariable Long brnCertId, @RequestParam(value = "allocated", required = false) String allocated,
								 @RequestParam(value = "lastPrintedCert", required = false) Long lastPrintedCert,HttpServletRequest request) throws BadRequestException {
		certService.allocateForPrint(brnCertId, allocated, lastPrintedCert,request);
	}

	@RequestMapping(value = {"acknowledgeCert/{brnCertId}"}, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void acknowledgeCert(@PathVariable Long brnCertId) throws BadRequestException {
		certService.acknowledgeCert(brnCertId);
	}

	@RequestMapping(value = {"selbranchcerttypes"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
	@ResponseBody
	public Page<BranchCerts> SelBranchCertTypes(@RequestParam(value = "term", required = false) String term, Pageable pageable,
												@RequestParam(value = "certId", required = false) Long certId,
												@RequestParam(value = "brnCode", required = false) Long brnCode,
												@RequestParam(value = "acctCode", required = false) Long acctCode)
			throws IllegalAccessException {
		return certService.findAllLots(term, pageable, certId, brnCode, acctCode);
	}

	@RequestMapping(value = {"getprintcerts"}, method = {RequestMethod.GET})
	@ResponseBody
	public DataTablesResult<PrintCertificateQueue> getPrintCerts(@DataTable DataTablesRequest pageable,
																 @RequestParam(value = "certCode", required = false) Long certCode,
																 @RequestParam(value = "brnCode", required = false) Long brnCode,
																 @RequestParam(value = "acctCode", required = false) Long acctCode,
																 @RequestParam(value = "certStatus", required = false) String certStatus,
																 @RequestParam(value = "polNo", required = false) String polNo,
																 @RequestParam(value = "riskId", required = false) String riskId) throws IllegalAccessException {

		return certService.findCertToPrint(pageable, certCode, brnCode, acctCode,certStatus,polNo,riskId);
	}

	@RequestMapping(value = {"allocateCerts"}, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST})
	public ResponseEntity<String> allocateCerts(@RequestBody PrintCertBean certBean) throws  BadRequestException {
		certService.allocateCerts(certBean);
		return new ResponseEntity<String>("OK",HttpStatus.OK);
	}



	@RequestMapping(value = {"deallocateCerts"}, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST})
	public ResponseEntity<String> deallocateCerts(@RequestBody PrintCertBean certBean) throws  BadRequestException {
		certService.deallocateCerts(certBean);
		return new ResponseEntity<String>("OK",HttpStatus.OK);
	}

	@RequestMapping(value = "printCertificate", method = RequestMethod.POST)
	public ResponseEntity<String> printReport(@RequestBody PrintCerts printCerts)
			throws BadRequestException, IOException {
		certService.createBatchCerts(printCerts.getCertCodes());
		return new ResponseEntity<String>("OK",HttpStatus.OK);
	}

	@RequestMapping(value = "markPrintedCerts", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void markPrintedCerts()
			throws BadRequestException, IOException {
		certService.markCertPrinted();
	}

	@RequestMapping(value = "printcert", method = RequestMethod.GET)
	public ModelAndView printCertificate(ModelMap modelMap, ModelAndView modelAndView) throws BadRequestException {
		modelMap.put("datasource", datasource);
		modelMap.put("format", "pdf");
		modelMap.put("user_code", userUtils.getCurrentUser().getId());
		modelAndView = new ModelAndView("rpt_cert_prnP", modelMap);
		return modelAndView;
	}
}