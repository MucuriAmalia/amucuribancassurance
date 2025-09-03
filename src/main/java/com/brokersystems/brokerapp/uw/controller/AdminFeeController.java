package com.brokersystems.brokerapp.uw.controller;

import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.setup.dto.OrganizationDTO;
import com.brokersystems.brokerapp.setup.model.ModelHelperForm;
import com.brokersystems.brokerapp.setup.model.Organization;
import com.brokersystems.brokerapp.setup.service.OrganizationService;
import com.brokersystems.brokerapp.trans.model.IntegrationDtls;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by peter on 6/6/2017.
 */
@Controller
@RequestMapping({ "/protected/trans/adminfee" })
public class AdminFeeController {

    @Autowired
    private PolicyTransService policyTransService;

    @Autowired
    private OrganizationService orgService;

    @Autowired
    private DataSource datasource;

    @Autowired
    private AuditTrailLogger auditTrailLogger;

    @InitBinder({"adminFee"})
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @ModelAttribute
    private AdminFeeForm getAdminFeeForm() {
        return new AdminFeeForm();
    }

    @RequestMapping(value = "newtrans", method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String endorsementTrans(Model model,HttpServletRequest request) {
        String message="Accessed Admin Fees Screen";
        String resource="Admin Fees";
        auditTrailLogger.log(message,request, resource);
        return "admintrans";
    }

    @RequestMapping(value = "createAdminFee", method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String createAdminFee(Model model) {
        return "newadminfee";
    }

    @RequestMapping(value = {"adminfeeTrans"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<AdminFee> unAuthTrans(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return policyTransService.findUnauthTrans(pageable);
    }

    @RequestMapping(value = {"authadminfeeTrans"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<AdminFee> authorisedTrans(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return policyTransService.findAuthorisedTrans(pageable);
    }

    @RequestMapping(value = {"startAdminFee"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    public String startRenewals(@Valid @ModelAttribute AdminFeeForm adminFeeForm, BindingResult result, RedirectAttributes redirectAttrs, Model model, HttpServletRequest request) {
        if(adminFeeForm.getBrnCode()==null){
            redirectAttrs.addFlashAttribute("error", "Select Branch");
            redirectAttrs.addFlashAttribute("adminFeeForm", adminFeeForm);
            return "redirect:/protected/trans/adminfee/createAdminFee";
        }
        if(adminFeeForm.getClientId()==null){
            redirectAttrs.addFlashAttribute("error", "Select Client");
            redirectAttrs.addFlashAttribute("adminFeeForm", adminFeeForm);
            return "redirect:/protected/trans/adminfee/createAdminFee";
        }
        if(adminFeeForm.getCurrencyId()==null){
            redirectAttrs.addFlashAttribute("error", "Select Currency");
            redirectAttrs.addFlashAttribute("adminFeeForm", adminFeeForm);
            return "redirect:/protected/trans/adminfee/createAdminFee";
        }
        Long adminFeeId = policyTransService.createAdminFeeTrans(adminFeeForm);
        request.getSession().setAttribute("adminFeeCode", adminFeeId);
        return "adminfeeuwform";
    }


    @RequestMapping(value = "/editadminfee", method = RequestMethod.POST)
    public String editPolicyForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model, HttpServletRequest request) throws BadRequestException {
        request.getSession().setAttribute("adminFeeCode", helperForm.getId());
        return "adminfeeuwform";
    }

    @RequestMapping(value = { "getAdminFeeDetails/{adminFeeId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    public ResponseEntity<AdminFee> getAdminFeeDetails(@PathVariable Long adminFeeId) throws BadRequestException {
        AdminFee created = policyTransService.getAdminFeeDetails(adminFeeId);
        return new ResponseEntity<AdminFee>(created, HttpStatus.OK);
    }


    @RequestMapping(value = { "adminFeePolicies" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<AdminFeePolicies> getAdminFeePolicies(@DataTable DataTablesRequest pageable, HttpServletRequest request)
            throws IllegalAccessException {
        Long adminFeeId = (Long) request.getSession().getAttribute("adminFeeCode");
        return policyTransService.findAdminFeePolicies(pageable, adminFeeId);
    }

    @RequestMapping(value = { "createAdminFeePolicies" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    public ResponseEntity<String> createAdminFeePolicies(@RequestBody AdminFeePolicyBean adminFeePolicyBean) throws IllegalAccessException, IOException, BadRequestException {
        policyTransService.addAdminFeePolicies(adminFeePolicyBean);
        return new ResponseEntity<String>("OK",HttpStatus.OK);
    }

    @RequestMapping(value = { "getAdminFeePolicies" }, method = { RequestMethod.GET })
    @ResponseBody
    public List<Object[]> getAdminFeePolicies(@RequestParam(value = "clientId", required = false) Long clientId,@RequestParam(value = "adminFeeId", required = false) Long adminFeeId )
            throws IllegalAccessException {
        return policyTransService.getAdminFeePolicies(clientId,adminFeeId);
    }

    @RequestMapping(value = { "authoriseAdminFee" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void authoriseAdminFee(HttpServletRequest request) throws BadRequestException {
        Long adminFeeId = (Long) request.getSession().getAttribute("adminFeeCode");
        policyTransService.authorizeAdminFee(adminFeeId);
    }

    @RequestMapping(value = "rpt_admin_fee", method = RequestMethod.GET)
    public ModelAndView outpatientRpt(ModelMap modelMap, HttpServletRequest request, ModelAndView modelAndView)
            throws BadRequestException, IOException {
        Long adminFeeId = (Long) request.getSession().getAttribute("adminFeeCode");
        OrganizationDTO organization = orgService.getOrganizationLogoDetails();
        InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
        BufferedImage image = ImageIO.read(in);
        modelMap.put("logo", image );
        modelMap.put("datasource", datasource);
        modelMap.put("format", "pdf");
        modelMap.put("afId", adminFeeId);
        modelAndView = new ModelAndView("rpt_admin_fee", modelMap);
        return modelAndView;
    }

}