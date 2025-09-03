package com.brokersystems.brokerapp.setup.controllers;



import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.setup.dto.AccountsDTO;
import com.brokersystems.brokerapp.setup.dto.BranchDTO;
import com.brokersystems.brokerapp.setup.dto.OrganizationDTO;
import com.brokersystems.brokerapp.setup.service.ParamService;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.util.JRLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.validator.FileUploadValidator;
import com.brokersystems.brokerapp.server.validator.OrganizationValidator;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.service.OrganizationService;

@Controller
@RequestMapping({"/protected/organization"})
public class OrganizationController
{
  private static final String SYSTEM_IMAGES = "images";
  private static final String TEMP_FOLDER_PATH = System.getProperty("java.io.tmpdir");
  private static final String IMAGES_SYSTEM_PATH = TEMP_FOLDER_PATH + File.separator + "images";
  private static final File IMAGES_SYSTEM_DIR = new File(IMAGES_SYSTEM_PATH);
  private static final String IMAGES_SYSTEM_DIR_ABSOLUTE_PATH = IMAGES_SYSTEM_DIR.getAbsolutePath() + File.separator;
  @Autowired
  private OrganizationService orgService;

  @Autowired
  FileUploadValidator fileValidator;
  @Autowired
  OrganizationValidator organizationValidator;
  
  @Autowired
  private DataSource datasource;

  @Autowired
  private AuditTrailLogger auditTrailLogger;

  @InitBinder({"organization"})
  protected void initBinder(WebDataBinder binder)
  {
    binder.setValidator(this.fileValidator);
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    dateFormat.setLenient(false);
    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
  }
  
  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String organizationHome(Model model,HttpServletRequest request)
  {
    String message="Accessed Organisation Screen";
    String resource="Organization";
    auditTrailLogger.log(message,request, resource);
    return "orgdefinition";
  }
  
  
  @ModelAttribute(value = "organization")
  public OrganizationDTO setOrganizationForm()
  {
    OrganizationDTO organization = this.orgService.getOrganizationDetails();
    if (organization.getOrgCode() == null) {
      organization.setFormAction("A");
    } else {
      organization.setFormAction("E");
    }
    return organization;
  }
  
  @RequestMapping(value={"/editOrganization"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String createOrganization(Model model)
    throws IOException
  {
    if(!model.containsAttribute("org.springframework.validation.BindingResult.organization"))
    {
    	OrganizationDTO organization = this.orgService.getOrganizationDetails();
    	organization.setFormAction("A");
        model.addAttribute("organization", organization);
    }
    return "orgdefinition";
  }

  @RequestMapping(value={"/createOrganization"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String createOrganization(@Valid @ModelAttribute OrganizationDTO organization,BindingResult result,
          RedirectAttributes redirectAttrs
          )
    throws IOException, BadRequestException
  {
	  if(result.hasErrors())
	  {
		  redirectAttrs.addFlashAttribute("org.springframework.validation.BindingResult.organization", result);
		  redirectAttrs.addFlashAttribute("organization", organization);
		  return organization.getOrgCode() == null ? "redirect:/protected/organization/" : "redirect:/protected/organization/editOrganization";
	  }
    if ((organization.getFile() != null) && 
      (!organization.getFile().isEmpty())) {
      String uploadFolder = IMAGES_SYSTEM_DIR_ABSOLUTE_PATH;
      byte[] bytes = organization.getFile().getBytes();
      String folderName = uploadFolder + "/ORGANIZATION";
      File file = new File(folderName);
      if (!file.exists())
        FileUtils.forceMkdir(file);
      Path path = Paths.get(folderName + "/" + organization.getFile().getOriginalFilename());
      Files.write(path, bytes);
      organization.setOrgLogoContentType(organization.getFile().getContentType());
      organization.setOrgLogo(path.toFile().getAbsolutePath());
    }
    else{
      OrganizationDTO dto = orgService.getOrganizationLogoDetails();
      organization.setOrgLogoContentType(dto.getOrgLogoContentType());
      organization.setOrgLogo(dto.getOrgLogo());
    }
    this.orgService.createOrganization(organization);
    organization.setFormAction("E");
    return "redirect:/protected/organization/";
  }
  
  @RequestMapping(value = "/logo")
  public void getImage(HttpServletResponse response) throws IOException,ServletException {
	 OrganizationDTO organization = orgService.getOrganizationLogoDetails();
	 response.setContentType(organization.getOrgLogoContentType());
	 response.getOutputStream().write(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
	 response.getOutputStream().close();
  }
  
  @RequestMapping(value={"countries"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseBody
  public Page<Country> select(@RequestParam(value="term", required=false) String term, Pageable pageable)
    throws IllegalAccessException
  {
    return this.orgService.findCountryForSelect(term, pageable);
  }
  
  @RequestMapping(value={"counties"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseBody
  public Page<County> selectCounties(@RequestParam(value="term", required=false) String term, @RequestParam("couCode") Long couCode, Pageable pageable)
    throws IllegalAccessException, BadRequestException
  {
    this.organizationValidator.validateSelectCountiesForCountry(couCode);
    return this.orgService.findCountyForSelect(term, pageable, couCode.longValue());
  }
  
  @RequestMapping(value={"towns"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseBody
  public Page<Town> towns(@RequestParam(value="term", required=false) String term, @RequestParam("countyId") Long countyId, Pageable pageable)
    throws IllegalAccessException, BadRequestException
  {
    this.organizationValidator.validateSelectTownsForCounty(countyId);
    return this.orgService.findTownForSelect(term, pageable, countyId.longValue());
  }
  
  @RequestMapping(value={"currencies"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseBody
  public Page<Currencies> currencies(@RequestParam(value="term", required=false) String term, Pageable pageable)
    throws IllegalAccessException
  {
    return this.orgService.findCurrenciesForSelect(term, pageable);
  }
  
  
  @RequestMapping(value={"managers"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseBody
  public Page<AccountsDTO> branchManagers(@RequestParam(value="term", required=false) String term, Pageable pageable)
    throws IllegalAccessException
  {
    return this.orgService.findUsersForSelect(term, pageable);
  }
  
  @RequestMapping(value={"branches/{regCode}"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseBody
  public DataTablesResult<BranchDTO> orgBranch(@DataTable DataTablesRequest pageable, @PathVariable Long regCode)
  {
    return this.orgService.findOrgBranches(regCode, pageable);
  }
  

  @RequestMapping(value={"regions/{orgCode}"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseBody
  public DataTablesResult<OrgRegions> orgRegions(@DataTable DataTablesRequest pageable, @PathVariable Long orgCode)
    throws IllegalAccessException
  {
    return this.orgService.findOrgRegions(orgCode, pageable);
  }
  
  
 
  
  @RequestMapping(value={"createRegionBranch"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseStatus(HttpStatus.CREATED)
  public void saveOrUpdateBranch(BranchDTO branch)
          throws  BadRequestException {
    this.orgService.createRegionBranch(branch);
  }
  
  
  @RequestMapping(value={"createRegion"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseStatus(HttpStatus.CREATED)
  public void saveOrUpdateRegion(OrgRegions region)
    throws IllegalAccessException
  {
    if (region.getOrganization() == null) {
      throw new IllegalArgumentException("Cannot create Region without Organization");
    }
    this.orgService.createOrgRegion(region);
  }
  
  @RequestMapping(value={"deleteBranch/{branchCode}"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteRegionBranch(@PathVariable Long branchCode)
  {
    this.orgService.deleteOrgBranch(branchCode);
  }
  
  @RequestMapping(value={"deleteRegion/{regCode}"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteOrgRegion(@PathVariable Long regCode)
  {
    this.orgService.deleteOrgRegion(regCode);
  }
  
  
 
  
  
}
