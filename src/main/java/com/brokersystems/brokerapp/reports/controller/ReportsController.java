package com.brokersystems.brokerapp.reports.controller;

import com.brokersystems.brokerapp.developermodule.dto.StretchyParameterDTO;
import com.brokersystems.brokerapp.developermodule.model.StretchyReport;
import com.brokersystems.brokerapp.developermodule.service.Impl.ReportStretchyServiceImpl;
import com.brokersystems.brokerapp.developermodule.service.ReportStretchyService;
import com.brokersystems.brokerapp.developermodule.service.StretchyReportService;
import com.brokersystems.brokerapp.reports.model.ReportData;
import com.brokersystems.brokerapp.reports.model.ReportDefinition;
import com.brokersystems.brokerapp.reports.model.ReportParameters;
import com.brokersystems.brokerapp.reports.service.ReportService;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.exception.ReportNotFoundException;
import com.brokersystems.brokerapp.setup.dto.AccountsDTO;
import com.brokersystems.brokerapp.setup.dto.OrganizationDTO;
import com.brokersystems.brokerapp.setup.dto.ProspectsDTO;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.service.BinderSetupService;
import com.brokersystems.brokerapp.setup.service.ClientService;
import com.brokersystems.brokerapp.setup.service.OrganizationService;
import com.brokersystems.brokerapp.trans.model.SystemTransactions;
import com.brokersystems.brokerapp.users.model.PermissionsDef;
import com.brokersystems.brokerapp.uw.dtos.ClientsDto;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;


@Slf4j
@Controller
@RequestMapping({ "/protected/reports" })
public class ReportsController {

	@Autowired
	private ReportService reportService;

	@Autowired
	private OrganizationService orgService;

	@Autowired
	private DataSource datasource;

	@Autowired
	private PolicyTransService policyService;

	@Autowired
	private BinderSetupService service;

	@Autowired
	private ClientService clientService;

	@Autowired
	private StretchyReportService stretchyReportService;
	@Autowired
	private ReportStretchyService stretchyService;
	@Autowired
	private ReportStretchyServiceImpl reportStretchyService;



	@InitBinder({"reportData"})
	protected void initBinder(WebDataBinder binder)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	}

	@ModelAttribute
	public ReportData getReportData()
	{
		ReportData reportData = new ReportData();
		return reportData;
	}

	@RequestMapping(value = "claimsrep",method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String claimsrep(Model model)
	  {
		  model.addAttribute("reportName","Claims Reports");
		  model.addAttribute("modules", reportService.findReportsByModule("C"));
		  Iterable<StretchyReport> stretchyReports = stretchyReportService.findStretchyReportsByModule("C");
		  model.addAttribute("stretchyModules", stretchyReports);
		  return "reports";
	  }

	@RequestMapping(value = "customrep",method={org.springframework.web.bind.annotation.RequestMethod.GET})
	public String customrep(Model model)
	{
		Iterable<StretchyReport> stretchyReports = stretchyReportService.findStretchyReportsByModule("C");
		model.addAttribute("stretchyModules", stretchyReports);
		return "reports";
	}


	@RequestMapping(value = "uwrep",method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String uwrep(Model model)
	  {
		  model.addAttribute("reportName","Underwriting Reports");
		  Iterable<ReportDefinition> headers = reportService.findReportsByModule("U");
		  Iterable<StretchyReport> stretchyReports = stretchyReportService.findStretchyReportsByModule("U");
		  model.addAttribute("modules", headers);
		  model.addAttribute("stretchyModules", stretchyReports);
		  return "reports";
	  }

	@RequestMapping(value = "medreports",method={org.springframework.web.bind.annotation.RequestMethod.GET})
	public String medreports(Model model)
	{
		model.addAttribute("reportName","Medical Reports");
		Iterable<ReportDefinition> headers = reportService.findReportsByModule("M");
		Iterable<StretchyReport> stretchyReports = stretchyReportService.findStretchyReportsByModule("M");
		model.addAttribute("modules", headers);
		model.addAttribute("stretchyModules", stretchyReports);
		return "reports";
	}

	@RequestMapping(value = "accountrep",method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String classHome(Model model)
	  {
		  model.addAttribute("reportName","Accounts Reports");
		  model.addAttribute("modules", reportService.findReportsByModule("A"));
		  Iterable<StretchyReport> stretchyReports = stretchyReportService.findStretchyReportsByModule("A");
		  model.addAttribute("stretchyModules", stretchyReports);
		  return "reports";
	  }


	@RequestMapping(value = "report/{reportName}", method = RequestMethod.GET)
	public ModelAndView endorsementReport(ModelMap modelMap, HttpServletRequest request, ModelAndView modelAndView, @PathVariable String reportName)
			throws BadRequestException, IOException {
		OrganizationDTO organization = orgService.getOrganizationLogoDetails();
		InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
		BufferedImage image = ImageIO.read(in);
		modelMap.put("logo", image );
		modelMap.put("datasource", datasource);
		modelMap.put("format", "pdf");
		modelAndView = new ModelAndView(reportName, modelMap);
		return modelAndView;
	}

	@RequestMapping(value = "{reportCode}", method = RequestMethod.POST)
	public ModelAndView printReport(@PathVariable String reportCode,@ModelAttribute ReportData reportData, HttpServletRequest request, ModelAndView modelAndView,ModelMap modelMap)
			throws BadRequestException, IOException {
		log.info("Received report data: {}", reportData);

		// Add organization logo to modelMap
		OrganizationDTO organization = orgService.getOrganizationLogoDetails();
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo()))));
		modelMap.put("logo", image);
		modelMap.put("datasource", datasource);

		// Process parameters (paramName1 to paramName17)
		processParameter(reportData.getParamName1(), reportData.getParamValue1(), reportData.getParamType1(), modelMap);
		processParameter(reportData.getParamName2(), reportData.getParamValue2(), reportData.getParamType2(), modelMap);
		processParameter(reportData.getParamName3(), reportData.getParamValue3(), reportData.getParamType3(), modelMap);
		processParameter(reportData.getParamName4(), reportData.getParamValue4(), reportData.getParamType4(), modelMap);
		processParameter(reportData.getParamName5(), reportData.getParamValue5(), reportData.getParamType5(), modelMap);
		processParameter(reportData.getParamName6(), reportData.getParamValue6(), reportData.getParamType6(), modelMap);
		processParameter(reportData.getParamName7(), reportData.getParamValue7(), reportData.getParamType7(), modelMap);
		processParameter(reportData.getParamName8(), reportData.getParamValue8(), reportData.getParamType8(), modelMap);
		processParameter(reportData.getParamName9(), reportData.getParamValue9(), reportData.getParamType9(), modelMap);
		processParameter(reportData.getParamName10(), reportData.getParamValue10(), reportData.getParamType10(), modelMap);
		processParameter(reportData.getParamName11(), reportData.getParamValue11(), reportData.getParamType11(), modelMap);
		processParameter(reportData.getParamName12(), reportData.getParamValue12(), reportData.getParamType12(), modelMap);
		processParameter(reportData.getParamName13(), reportData.getParamValue13(), reportData.getParamType13(), modelMap);
		processParameter(reportData.getParamName14(), reportData.getParamValue14(), reportData.getParamType14(), modelMap);
		processParameter(reportData.getParamName15(), reportData.getParamValue15(), reportData.getParamType15(), modelMap);
		processParameter(reportData.getParamName16(), reportData.getParamValue16(), reportData.getParamType16(), modelMap);
		processParameter(reportData.getParamName17(), reportData.getParamValue17(), reportData.getParamType17(), modelMap);

		// Ensure format is set to 'pdf' if not provided
		if (!modelMap.containsAttribute("format")) {
			modelMap.put("format", "pdf");
		}

		// Log modelMap contents for debugging
		modelMap.forEach((key, value) -> log.info("modelMap entry: {} = {}", key, value));

		// Return ModelAndView with reportCode and modelMap
		return new ModelAndView(reportData.getReportCode(), modelMap);
	}

	private void processParameter(String paramName, String paramValue, String paramType, ModelMap modelMap) {
		if (paramName != null && !StringUtils.isEmpty(paramName) && !StringUtils.isEmpty(paramValue)) {
			// Special handling for selectActivity (ACTIVITY parameter with lovName='ACTIVITY')
			if ("selectActivity".equals(paramName)) {
				try {
					// Store as Long for caId (numeric ID)
					modelMap.put("causation", paramValue);
					log.info("Processed selectActivity as causation: {}", paramValue);
				} catch (NumberFormatException e) {
					log.warn("Invalid caId for selectActivity: {}", paramValue);
					modelMap.put(paramName, paramValue); // Fallback to string
				}
			} else {
				// Try parsing as date for other parameters
				try {
					Date dt = new SimpleDateFormat("dd/MM/yyyy").parse(paramValue);
					modelMap.put(paramName, dt);
					log.info("Processed {} as date: {}", paramName, dt);
				} catch (Exception e) {
					modelMap.put(paramName, paramValue);
					log.info("Processed {} as string: {}", paramName, paramValue);
				}
			}
		}
	}

//	@RequestMapping(value = "{reportCode}", method = RequestMethod.POST)
//	public ModelAndView printReport(@PathVariable String reportCode,@ModelAttribute ReportData reportData, HttpServletRequest request, ModelAndView modelAndView,ModelMap modelMap)
//			throws BadRequestException, IOException {
//		reportData.setReportCode(reportCode);
//		System.out.println(reportData);
//		log.info("The report data received by print report endpoint is {}", reportData);
//		OrganizationDTO organization = orgService.getOrganizationLogoDetails();
//		InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
//		BufferedImage image = ImageIO.read(in);
//		modelMap.put("logo", image );
//		modelMap.put("datasource", datasource);
//
////		boolean passwordProtected = reportService.getReportByCode(reportData.getReportCode()).getPasswordProtect()!=null
////				                    && "Y".equalsIgnoreCase(reportService.getReportByCode(reportData.getReportCode()).getPasswordProtect());
//		//modelMap.put("JRPdfExporterParameter.USER_PASSWORD", "user_pwd");
//		if(reportData.getParamName1()!=null && !StringUtils.isBlank(reportData.getParamName1())){
//			String value=reportData.getParamValue1();
//			if(StringUtils.isBlank(value)) value=null;
//			try{
//				Date dt = new SimpleDateFormat("dd/MM/yyyy").parse(value);
//				modelMap.put(reportData.getParamName1(),dt);
//			}
//			catch (Exception e){
//				modelMap.put(reportData.getParamName1(),value);
////				if(reportData.getParamType1()!=null && "C".equalsIgnoreCase(reportData.getParamType1())){
////					 ClientDef clientDef = clientService.getClientDetails(Long.valueOf(value));
////
////				}
//			}
//
//		}
//
//		if(reportData.getParamName2()!=null && !StringUtils.isBlank(reportData.getParamName2())){
//			String value=reportData.getParamValue2();
//			if(StringUtils.isBlank(value)) value=null;
//			try{
//				Date dt = new SimpleDateFormat("dd/MM/yyyy").parse(value);
//				modelMap.put(reportData.getParamName2(),dt);
//			}
//			catch (Exception e){
//				modelMap.put(reportData.getParamName2(),value);
//			}
//
//		}
//
//		if(reportData.getParamName3()!=null && !StringUtils.isBlank(reportData.getParamName3())){
//			String value=reportData.getParamValue3();
//			if(StringUtils.isBlank(value)) value=null;
//			try{
//				Date dt = new SimpleDateFormat("dd/MM/yyyy").parse(value);
//				modelMap.put(reportData.getParamName3(),dt);
//			}
//			catch (Exception e){
//				modelMap.put(reportData.getParamName3(),value);
//			}
//
//		}
//
//		if(reportData.getParamName4()!=null && !StringUtils.isBlank(reportData.getParamName4())){
//			String value=reportData.getParamValue4();
//			System.out.println("Parameter Name == "+reportData.getParamName4()+" ; "+"Parameter Value == "+value);
//			if(StringUtils.isBlank(value)) value=null;
//			try{
//				Date dt = new SimpleDateFormat("dd/MM/yyyy").parse(value);
//				modelMap.put(reportData.getParamName4(),dt);
//			}
//			catch (Exception e){
//				modelMap.put(reportData.getParamName4(),value);
//			}
//
//		}
//
//		if(reportData.getParamName5()!=null && !StringUtils.isBlank(reportData.getParamName5())){
//			String value=reportData.getParamValue5();
//			if(StringUtils.isBlank(value)) value=null;
//			try{
//				Date dt = new SimpleDateFormat("dd/MM/yyyy").parse(value);
//				modelMap.put(reportData.getParamName5(),dt);
//			}
//			catch (Exception e){
//				modelMap.put(reportData.getParamName5(),value);
//			}
//
//		}
//
//		if(reportData.getParamName6()!=null && !StringUtils.isBlank(reportData.getParamName6())){
//			String value=reportData.getParamValue6();
//			if(StringUtils.isBlank(value)) value=null;
//			try{
//				Date dt = new SimpleDateFormat("dd/MM/yyyy").parse(value);
//				modelMap.put(reportData.getParamName6(),dt);
//			}
//			catch (Exception e){
//				modelMap.put(reportData.getParamName6(),value);
//			}
//
//		}
//
//		if(reportData.getParamName7()!=null && !StringUtils.isBlank(reportData.getParamName7())){
//			String value=reportData.getParamValue7();
//			if(StringUtils.isBlank(value)) value=null;
//			try{
//				Date dt = new SimpleDateFormat("dd/MM/yyyy").parse(value);
//				modelMap.put(reportData.getParamName7(),dt);
//			}
//			catch (Exception e){
//				modelMap.put(reportData.getParamName7(),value);
//			}
//
//		}
//
//		if(reportData.getParamName8()!=null && !StringUtils.isBlank(reportData.getParamName8())){
//			String value=reportData.getParamValue8();
//			if(StringUtils.isBlank(value)) value=null;
//			try{
//				Date dt = new SimpleDateFormat("dd/MM/yyyy").parse(value);
//				modelMap.put(reportData.getParamName8(),dt);
//			}
//			catch (Exception e){
//				modelMap.put(reportData.getParamName8(),value);
//			}
//
//		}
//
//		if(reportData.getParamName9()!=null && !StringUtils.isBlank(reportData.getParamName9())){
//			String value=reportData.getParamValue9();
//			if(StringUtils.isBlank(value)) value=null;
//			try{
//				Date dt = new SimpleDateFormat("dd/MM/yyyy").parse(value);
//				modelMap.put(reportData.getParamName9(),dt);
//			}
//			catch (Exception e){
//				modelMap.put(reportData.getParamName9(),value);
//			}
//
//		}
//
//		if(reportData.getParamName10()!=null && !StringUtils.isBlank(reportData.getParamName10())){
//			String value=reportData.getParamValue10();
//			if(StringUtils.isBlank(value)) value=null;
//			try{
//				Date dt = new SimpleDateFormat("dd/MM/yyyy").parse(value);
//				modelMap.put(reportData.getParamName10(),dt);
//
//			}
//			catch (Exception e){
//				modelMap.put(reportData.getParamName10(),value);
//			}
//
//		}
//
//		if(reportData.getParamName11()!=null && !StringUtils.isBlank(reportData.getParamName11())){
//			String value=reportData.getParamValue11();
//			if(StringUtils.isBlank(value)) value=null;
//			try{
//				Date dt = new SimpleDateFormat("dd/MM/yyyy").parse(value);
//				modelMap.put(reportData.getParamName11(),dt);
//			}
//			catch (Exception e){
//				modelMap.put(reportData.getParamName11(),value);
//			}
//
//		}
//
//		if(reportData.getParamName12()!=null && !StringUtils.isBlank(reportData.getParamName12())){
//			String value=reportData.getParamValue12();
//			if(StringUtils.isBlank(value)) value=null;
//			try{
//				Date dt = new SimpleDateFormat("dd/MM/yyyy").parse(value);
//				modelMap.put(reportData.getParamName12(),dt);
//			}
//			catch (Exception e){
//				modelMap.put(reportData.getParamName12(),value);
//			}
//
//		}
//
//		if(reportData.getParamName13()!=null && !StringUtils.isBlank(reportData.getParamName13())){
//			String value=reportData.getParamValue13();
//			if(StringUtils.isBlank(value)) value=null;
//			try{
//				Date dt = new SimpleDateFormat("dd/MM/yyyy").parse(value);
//				modelMap.put(reportData.getParamName13(),dt);
//			}
//			catch (Exception e){
//				modelMap.put(reportData.getParamName13(),value);
//			}
//		}
//
//		if(reportData.getParamName14()!=null && !StringUtils.isBlank(reportData.getParamName14())){
//			String value=reportData.getParamValue14();
//			if(StringUtils.isBlank(value)) value=null;
//			try{
//				Date dt = new SimpleDateFormat("dd/MM/yyyy").parse(value);
//				modelMap.put(reportData.getParamName14(),dt);
//			}
//			catch (Exception e){
//				modelMap.put(reportData.getParamName14(),value);
//			}
//		}
//
//		if(reportData.getParamName15()!=null && !StringUtils.isBlank(reportData.getParamName15())){
//			String value=reportData.getParamValue15();
//			if(StringUtils.isBlank(value)) value=null;
//			try{
//				Date dt = new SimpleDateFormat("dd/MM/yyyy").parse(value);
//				modelMap.put(reportData.getParamName15(),dt);
//			}
//			catch (Exception e){
//				modelMap.put(reportData.getParamName15(),value);
//			}
//		}
//
//		if(reportData.getParamName16()!=null && !StringUtils.isBlank(reportData.getParamName16())){
//			String value=reportData.getParamValue16();
//			if(StringUtils.isBlank(value)) value=null;
//			try{
//				Date dt = new SimpleDateFormat("dd/MM/yyyy").parse(value);
//				modelMap.put(reportData.getParamName16(),dt);
//			}
//			catch (Exception e){
//				modelMap.put(reportData.getParamName16(),value);
//			}
//		}
//
//		if(reportData.getParamName17()!=null && !StringUtils.isBlank(reportData.getParamName17())){
//			String value=reportData.getParamValue17();
//			if(StringUtils.isBlank(value)) value=null;
//			try{
//				Date dt = new SimpleDateFormat("dd/MM/yyyy").parse(value);
//				modelMap.put(reportData.getParamName17(),dt);
//			}
//			catch (Exception e){
//				modelMap.put(reportData.getParamName17(),value);
//			}
//		}
//		if(!modelMap.containsAttribute("format")){
//			modelMap.addAttribute("format","pdf");
//		}
//         modelMap.entrySet().forEach(System.out::println);
//		modelAndView = new ModelAndView(reportData.getReportCode(), modelMap);
//		return modelAndView;
//	}

//	@RequestMapping(value = "printCustomReport", method = RequestMethod.POST)
//	public ResponseEntity<StreamingResponseBody> printCustomReport(
//			@RequestParam(required = false) String reportCode,
//			HttpServletRequest request,
//			HttpServletResponse response) throws IOException, BadRequestException, ReportNotFoundException, IllegalAccessException {
//
//		// Extract parameters from the request
//		String csvFileName = "customReport.csv";
//		Map<String, String> parameters = extractParameters(request);
//
//		// Generate the CSV file
//		try {
//			reportStretchyService.generateCSVFile(reportCode, parameters);
//		} catch (Exception e) {
//			throw new BadRequestException("Failed to generate CSV file");
//		}
//
//		// Set headers for CSV download
//		response.setContentType("text/plain");
//		response.setHeader("Content-Disposition", "attachment; filename=\"" + csvFileName + "\"");
//
//		return new ResponseEntity<>(outputStream -> {
//			try (InputStream inputStream = new FileInputStream(new File(csvFileName))) {
//				IOUtils.copy(inputStream, outputStream);
//			}
//		}, HttpStatus.OK);
//	}

	private Map<String, String> extractParameters(HttpServletRequest request) {
		Map<String, String> modelMap = new HashMap<>();
		// Define the input date format (dd/MM/yyyy)
		SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		// Define the output date format (yyyy-MM-dd) for the SQL query
		SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");

		for (int i = 1; i <= 16; i++) {
			String paramName = request.getParameter("paramName" + i);
			String paramValue = request.getParameter("paramValue" + i);

			if (paramName != null && !StringUtils.isBlank(paramName)) {
				try {
					if (StringUtils.isBlank(paramValue)) {
						modelMap.put(paramName, null);
					} else {
						// Try parsing the date in dd/MM/yyyy format and convert to yyyy-MM-dd
						Date parsedDate = inputDateFormat.parse(paramValue);
						modelMap.put(paramName, outputDateFormat.format(parsedDate));  // Convert to the output format
					}
				} catch (Exception e) {
					// If parsing fails, treat it as a normal string (non-date value)
					modelMap.put(paramName, paramValue);
				}
			}
		}

		return modelMap;
	}

	@RequestMapping(value = { "getReportParams" }, method = RequestMethod.GET)
	public ResponseEntity<Iterable<ReportParameters>> getReportParams(
			@RequestParam(value = "rptId", required = false) Long rptId) throws BadRequestException {

		List<ReportParameters> params = new ArrayList<>();
		reportService.getParametersByReport(rptId).forEach(params::add);

		// Check if Format parameter is already present
		boolean hasFormat = params.stream()
				.anyMatch(p -> "format".equalsIgnoreCase(p.getParamActualName()));

		if (!hasFormat) {
			ReportParameters formatParam = new ReportParameters();
			formatParam.setParamName("Format");
			formatParam.setParamActualName("format");
			formatParam.setParamType("O");
			formatParam.setOptions("pdf,csv,xlsx");
			formatParam.setPasswordField(null);

			params.add(formatParam);
		}

		return new ResponseEntity<>(params, HttpStatus.OK);
	}

	@RequestMapping(value = { "getStretchyReportParams" }, method = RequestMethod.GET)
	public ResponseEntity<List<StretchyParameterDTO>> getParametersByStretchyReport(@RequestParam(value = "strId", required = false) Long strId) throws BadRequestException, IllegalAccessException {
		List<StretchyParameterDTO> params = stretchyReportService.findStretchyReportParameters(strId);
		return new ResponseEntity<>(params, HttpStatus.OK);
	}


//	@RequestMapping(value = { "clients" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
//	@ResponseBody
//	public Page<ClientDef> selectClients(@RequestParam(value = "term", required = false) String term, Pageable pageable)
//			throws IllegalAccessException {
//		return policyService.findActiveClients(term, pageable);
//	}

	@RequestMapping(value = { "clients" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<ClientsDto> selectClients(@RequestParam(value = "term", required = false) String term, Pageable pageable)
			throws IllegalAccessException {
		return policyService.findActiveClients(term, pageable);
	}

	@RequestMapping(value = { "prospect" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<ProspectsDTO> selectProspect(@RequestParam(value = "term", required = false) String term, Pageable pageable)
			throws IllegalAccessException {
		return policyService.findActiveProspect(term, pageable);
	}

	@RequestMapping(value = { "accounts" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<AccountDef> selAccounts(@RequestParam(value = "term", required = false) String term, Pageable pageable)
			throws IllegalAccessException {
		return service.findInsuranceAccountsReport(term, pageable);
	}

	@RequestMapping(value={"users"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	@ResponseBody
	public Page<AccountsDTO> branchManagers(@RequestParam(value="term", required=false) String term, Pageable pageable)
			throws IllegalAccessException
	{
		return this.orgService.findUsersForSelect(term, pageable);
	}
	@RequestMapping(value={"permissionRep"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	@ResponseBody
	public Page<PermissionsDef> selPerms(@RequestParam(value="term", required=false) String term, Pageable pageable)
			throws IllegalAccessException
	{
		return this.orgService.findPerm(term, pageable);
	}
	@RequestMapping(value = { "policies" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<PolicyTrans> selectPolicies(@RequestParam(value = "term", required = false) String term, Pageable pageable)
			throws IllegalAccessException {
		return policyService.findAllPolicies(term, pageable);
	}

	@RequestMapping(value = { "uwBinders" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<BindersDef> selectBinders(@RequestParam(value = "term", required = false) String term, Pageable pageable)
			throws IllegalAccessException {
		return policyService.findAllBinders(term, pageable);
	}


	@RequestMapping(value = { "remmittances" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<SystemTransactions> selectRemmitances(@RequestParam(value = "term", required = false) String term, Pageable pageable)
			throws IllegalAccessException {
		return reportService.findRemittances(term, pageable);
	}

	@RequestMapping(value = "printCustomReport", method = RequestMethod.POST)
	public ResponseEntity<StreamingResponseBody> printCustomReport(
			@ModelAttribute ReportData reportData,
			ModelMap modelMap,
			@RequestParam(required = false) String reportCode,
			HttpServletRequest request,
			HttpServletResponse response) throws IOException, BadRequestException, ReportNotFoundException, IllegalAccessException {

		// Extract parameters from the request
		Map<String, String> parameters = extractParameters(request);

		// Use reflection to iterate through all param fields dynamically
		List<String> paramValues = new ArrayList<>();
		for (Field field : reportData.getClass().getDeclaredFields()) {
			if (field.getName().startsWith("paramValue")) {
				field.setAccessible(true);
				String value = (String) field.get(reportData);
				if (value != null) {
					paramValues.add(value);
				}
			}
		}

		try {
			// Check if any paramValue contains "pdf" or "csv"
			boolean isCSV = paramValues.stream().anyMatch(val -> val.equalsIgnoreCase("csv"));
			boolean isPDF = paramValues.stream().anyMatch(val -> val.equalsIgnoreCase("pdf"));

			// Default to CSV if neither PDF nor CSV is chosen
			if (!isPDF && !isCSV) {
				isCSV = true;
			}

			if (isCSV) {
				String csvFileName = "customReport.csv";
				reportStretchyService.generateCSVFile(reportCode, parameters);

				// Set headers for CSV download
				response.setContentType("text/csv");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + csvFileName + "\"");

				return new ResponseEntity<>(outputStream -> {
					try (InputStream inputStream = new FileInputStream(new File(csvFileName))) {
						IOUtils.copy(inputStream, outputStream);
					}
				}, HttpStatus.OK);
			}

			if (isPDF) {
				String pdfFileName = "customReport.pdf";
				reportStretchyService.generatePDFFile(reportCode, parameters, response);

				// Set headers for PDF download
				response.setContentType("application/pdf");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + pdfFileName + "\"");

				return new ResponseEntity<>(outputStream -> {
					try (InputStream inputStream = new FileInputStream(new File(pdfFileName))) {
						IOUtils.copy(inputStream, outputStream);
					}
				}, HttpStatus.OK);
			}

			throw new BadRequestException("Invalid format requested");

		} catch (Exception e) {
			throw new BadRequestException("Failed to generate report: " + e.getMessage());
		}
	}
}
