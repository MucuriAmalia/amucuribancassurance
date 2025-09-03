package com.brokersystems.brokerapp.medical.controllers;

import com.brokersystems.brokerapp.dms.service.UploadService;
import com.brokersystems.brokerapp.dms.model.UploadBean;
import com.brokersystems.brokerapp.mail.model.MailTemplates;
import com.brokersystems.brokerapp.mail.service.MailTemplateService;
import com.brokersystems.brokerapp.mail.service.Mailer;
import com.brokersystems.brokerapp.medical.exceptions.MedicalPolicyException;
import com.brokersystems.brokerapp.medical.model.*;
import com.brokersystems.brokerapp.medical.service.MedicalCardsService;
import com.brokersystems.brokerapp.medical.service.MedicalComputePrem;
import com.brokersystems.brokerapp.medical.service.MedicalEndorseService;
import com.brokersystems.brokerapp.medical.service.MedicalTransService;
import com.brokersystems.brokerapp.quotes.services.QuotationService;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.server.utils.DateUtilities;
import com.brokersystems.brokerapp.setup.dto.ClientDTO;
import com.brokersystems.brokerapp.setup.dto.OrganizationDTO;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.service.OrganizationService;
import com.brokersystems.brokerapp.setup.service.SetupsService;
import com.brokersystems.brokerapp.trans.model.SystemTransactions;
import com.brokersystems.brokerapp.trans.model.TransChecks;
import com.brokersystems.brokerapp.trans.service.PolicyAuthorization;
import com.brokersystems.brokerapp.uw.dtos.BinderDTO;
import com.brokersystems.brokerapp.uw.dtos.PolicyCreateDTO;
import com.brokersystems.brokerapp.uw.dtos.TaxesDTO;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by peter on 4/26/2017.
 */

@Controller
@RequestMapping({ "/protected/medical/policies" })
public class MedicalController {

    @Autowired
    private MedicalTransService transService;

    @Autowired
    private PolicyTransService policyTransService;


    @Autowired
    private DateUtilities dateUtils;

    @Autowired
    private PolicyAuthorization authService;

    @Autowired
    private MedicalComputePrem computePrem;

    @Autowired
    private OrganizationService orgService;

    @Autowired
    private DataSource datasource;

    @Autowired
    private SetupsService setupsService;

    @Autowired
    private MedicalCardsService medicalCardsService;

    @Autowired
    private MedicalEndorseService endorseService;

    @Autowired
    private Mailer mailer;

    @Autowired
    private MailTemplateService templateService;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private QuotationService quotationService;

    @Autowired
    private AuditTrailLogger auditTrailLogger;




    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @RequestMapping(value = "policyList",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String uwHome(Model model)
    {
        return "uwmeduserlist";
    }

    @RequestMapping(value = { "policyTrans" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<PolicyTrans> getUserPolTrans(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return policyTransService.findUserMedicalTrans(pageable);
    }

    @RequestMapping(value = "editpolicy", method = RequestMethod.GET)
    public String editpolicy(Model model,HttpServletRequest request) throws BadRequestException {
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        if(polCode==null){
            model.addAttribute("policyId", -2000);
            model.addAttribute("policyStatus", 0);
        }else
            model.addAttribute("policyId", polCode);
        request.getSession().setAttribute("filename","success");
        PolicyTrans poltrans =policyTransService.getPolicyDetails(polCode);
        if ( poltrans.getAuthStatus()!=null && poltrans.getAuthStatus().equalsIgnoreCase("A")){
            model.addAttribute("policyStatus", 2);
        }else if (poltrans.getAuthStatus()!=null  && poltrans.getAuthStatus().equalsIgnoreCase("R"))
            model.addAttribute("policyStatus", 1);
        else if (poltrans.getAuthStatus()!=null  && poltrans.getAuthStatus().equalsIgnoreCase("CV"))
            model.addAttribute("policyStatus", 3);
        else model.addAttribute("policyStatus", 0);
        return "schcategories";
    }

//    @ModelAttribute
//    public PolicyTrans getPolicyTrans(){
//        return new PolicyTrans();
//    }

    /**
     * Initialize the PolicyTrans object in the controller to avoid NullPointerException
     * @return
     */
    @ModelAttribute
    public PolicyTrans getPolicyTrans() {
        PolicyTrans policyTrans = new PolicyTrans();
        policyTrans.setClient(new ClientDef());
        policyTrans.setBinder(new BindersDef());
        policyTrans.setProduct(new ProductsDef());
        policyTrans.setAgent(new AccountDef());
        policyTrans.setPaymentMode(new PaymentModes());
        policyTrans.setBranch(new OrgBranch());
        policyTrans.setTransCurrency(new Currencies());
        policyTrans.setBinCardType(new BinderMedicalCards());
        return policyTrans;
    }

    @RequestMapping(value = "uwform", method = RequestMethod.GET)
    public String tenantForm(Model model,HttpServletRequest request) {
        request.getSession().removeAttribute("policyCode");
        model.addAttribute("policyId", -2000);
        model.addAttribute("policyStatus", 0);
        return "medicaluw";
    }

//    @RequestMapping(value = { "uwBinders" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
//    @ResponseBody
//    public Page<BindersDef> selectBinders(@RequestParam(value = "term", required = false) String term, @RequestParam("bindType") String bindType, Pageable pageable)
//            throws IllegalAccessException {
//        return transService.findMedicalBinders(term, pageable,bindType);
//    }


//    @RequestMapping(value = { "uwBinders" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
//    @ResponseBody
//    public Page<BinderDTO> selectMedicalInsuranceBinders(@RequestParam(value = "term", required = false) String term, @RequestParam("bindType") String bindType, Pageable pageable)
//    {
//        return transService.findMedicalInsuranceBinder(term, pageable, bindType);
//    }

    @RequestMapping(value = { "uwBinders" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public Page<BinderDTO> selectMedicalInsuranceBinders(@RequestParam(value = "term", required = false) String term, Pageable pageable,@RequestParam("bindType") String binType)
    {
        return transService.findMedicalInsuranceBinder(term, pageable, binType);
    }




    @RequestMapping(value = { "binderCardTypes" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public Page<BinderCardTypes> getBinderCardTypes(@RequestParam(value = "term", required = false) String term, Pageable pageable,@RequestParam("bindCode")Long bindCode, @RequestParam(value = "wefDate", required = false) Date wefDate)
            throws IllegalAccessException {
        return transService.getBinderCardTypes(term, pageable,bindCode,wefDate);
    }

    @RequestMapping(value = { "getWetDate" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public Date getPolicyWetDate(@RequestParam(value = "wefDate", required = false) Date wef){
        return dateUtils.getWetDate(wef);
    }

    @RequestMapping(value={"/createPolicy"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public String createPolicy(@Valid @ModelAttribute PolicyCreateDTO policyTrans, BindingResult result,
                               RedirectAttributes redirectAttrs, Model model, HttpServletRequest request
    )
            throws IOException
    {
        try {
            PolicyTrans policy = policyTransService.createPolicy(policyTrans, false);
            model.addAttribute("policyId", policy.getPolicyId());
            request.getSession().setAttribute("policyCode",policy.getPolicyId());
            if ( policy.getAuthStatus()!=null && policy.getAuthStatus().equalsIgnoreCase("A")){
                model.addAttribute("policyStatus", 2);
            }else if (policy.getAuthStatus()!=null  && policy.getAuthStatus().equalsIgnoreCase("R"))
                model.addAttribute("policyStatus", 1);
            else if (policy.getAuthStatus()!=null  && policy.getAuthStatus().equalsIgnoreCase("CV"))
                model.addAttribute("policyStatus", 3);
            else model.addAttribute("policyStatus", 0);
        }
        catch(BadRequestException ex ){
            redirectAttrs.addFlashAttribute("error", ex.getMessage());
            redirectAttrs.addFlashAttribute("policyTrans", policyTrans);
            return "redirect:/protected/medical/policies/uwform";
        }
        return "schcategories";
    }


    @ExceptionHandler(MedicalPolicyException.class)
    public ModelAndView getSuperheroesUnavailable(MedicalPolicyException ex) {
        ModelAndView mv = new ModelAndView("uwform", "error", ex.getMessage());
        mv.addObject("policyTrans", getPolicyTrans());
        return mv;
    }

    @RequestMapping(value = "edituwpolicy", method = RequestMethod.GET)
    public String edituwpolicy(Model model,HttpServletRequest request) throws BadRequestException {
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        model.addAttribute("policyId", polCode);
        
        PolicyTrans poltrans =policyTransService.getPolicyDetails(polCode);

        if ( poltrans.getAuthStatus()!=null && poltrans.getAuthStatus().equalsIgnoreCase("A")){
            model.addAttribute("policyStatus", 2);
        }else if (poltrans.getAuthStatus()!=null  && poltrans.getAuthStatus().equalsIgnoreCase("R"))
            model.addAttribute("policyStatus", 1);
        else if (poltrans.getAuthStatus()!=null  && poltrans.getAuthStatus().equalsIgnoreCase("CV"))
            model.addAttribute("policyStatus", 3);
        else model.addAttribute("policyStatus", 0);
        return "schcategories";
    }


    @RequestMapping(value = "/edituwtrans", method = RequestMethod.POST)
    public String editPolicyForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model, HttpServletRequest request) throws BadRequestException {
        model.addAttribute("policyId", helperForm.getId());
        request.getSession().setAttribute("policyCode", helperForm.getId());
        PolicyTrans poltrans =policyTransService.getPolicyDetails(helperForm.getId());
        if ( poltrans.getAuthStatus()!=null && poltrans.getAuthStatus().equalsIgnoreCase("A")){
            model.addAttribute("policyStatus", 2);
        }else if (poltrans.getAuthStatus()!=null  && poltrans.getAuthStatus().equalsIgnoreCase("R"))
            model.addAttribute("policyStatus", 1);
        else if (poltrans.getAuthStatus()!=null  && poltrans.getAuthStatus().equalsIgnoreCase("CV"))
            model.addAttribute("policyStatus", 3);
        else model.addAttribute("policyStatus", 0);
        return "schcategories";
    }

    @RequestMapping(value = { "getPolicyDetails" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    public ResponseEntity<PolicyTrans> getPolicyDetails(HttpServletRequest request) throws BadRequestException {
        Long policyCode = (Long) request.getSession().getAttribute("policyCode");
        PolicyTrans created = policyTransService.getPolicyDetails(policyCode);
        return new ResponseEntity<PolicyTrans>(created, HttpStatus.OK);
    }

    @RequestMapping(value = { "getPolicyCategories" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<MedicalCategory> getMedicalCategories(@DataTable DataTablesRequest pageable,HttpServletRequest request)
            throws IllegalAccessException{
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        return transService.findPolicyCategories(pageable,polCode);
    }

    @RequestMapping(value = { "getCategoryBedTypes/{catId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<MedicalCategory> getMedicalCategoryBedType(@DataTable DataTablesRequest pageable,@PathVariable Long catId,HttpServletRequest request)
            throws IllegalAccessException{
        return transService.findCategoryBedType(pageable,catId);
    }

    @RequestMapping(value = { "createPolCategories" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void createPolCategories(MedicalCategory category) throws IllegalAccessException, IOException, BadRequestException {
        transService.defineMedicalCategories(category);
    }

    @RequestMapping(value = { "createCategoriesBedType" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void createCategoriesBedType(MedicalCategory category) throws IllegalAccessException, IOException, BadRequestException {
        transService.defineMedicalCategoryBedType(category);
    }

    @RequestMapping(value = { "deletePolCategories/{catId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePolCategories(@PathVariable Long catId) {
        transService.deleteMedicalCategory(catId);
    }

    @RequestMapping(value = { "getFamilySize/{catId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<CategoryFamilySize> getMedicalCategories(@DataTable DataTablesRequest pageable, @PathVariable Long catId)
            throws IllegalAccessException{
        return transService.getCategoryFamilySize(pageable,catId);
    }

    @RequestMapping(value = { "dependenttypes" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public Page<DependentTypes> selectDependentTypes(@RequestParam(value = "term", required = false) String term, Pageable pageable)
            throws IllegalAccessException {
        return transService.findDependentTypes(term, pageable);
    }

    @RequestMapping(value = { "createFamilySize" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void createFamilySize(CategoryFamilySize familySize) throws IllegalAccessException, IOException, BadRequestException {
        transService.defineCategoryFamilySize(familySize);
    }

    @RequestMapping(value = { "deleteFamilySize/{sizeId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFamilySize(@PathVariable Long sizeId) {
        transService.deleteFamSize(sizeId);
    }

    @RequestMapping(value = { "binderDetails" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public Page<BinderDetails> selectBinderDetails(@RequestParam(value = "term", required = false) String term, @RequestParam("bindCode") Long bindCode, Pageable pageable)
            throws IllegalAccessException {
        return transService.findBinderDetails(term, pageable, bindCode);
    }

    @RequestMapping(value = { "categoryBenefits/{catId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<MedCategoryBenefits> getCategoryBenefits(@DataTable DataTablesRequest pageable, @PathVariable Long catId)
            throws IllegalAccessException{
        return transService.getCategoryBenefits(pageable,catId);
    }

    @RequestMapping(value = { "deleteCatBenefits/{benId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCatBenefits(@PathVariable Long benId) {
        transService.deleteCatBenefits(benId);
    }


    @RequestMapping(value = { "categoryMembers/{catId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<CategoryMembers> getCategoryMembers(@DataTable DataTablesRequest pageable, @PathVariable Long catId)
            throws IllegalAccessException{
        return transService.getCategoryMembers(pageable,catId);
    }

    @RequestMapping(value = { "createCategoryMember" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void createCategoryMember(CategoryMembers members,HttpServletRequest request) throws IllegalAccessException, IOException, BadRequestException {
        transService.defineMember(members);

    }

    @RequestMapping(value = { "getmemberreqdocs" }, method = { RequestMethod.GET })
    @ResponseBody
    public List<SubClassReqdDocs> getRiskUnassignedDocs(@RequestParam(value = "riskId", required = false) Long riskId, @RequestParam(value = "docName", required = false) String docName )
            throws IllegalAccessException {
        return transService.findUnassignedMemberDocs(riskId,docName);
    }

    @RequestMapping(value = { "categoryRules/{catId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<CategoryRules> getCategoryRules(@DataTable DataTablesRequest pageable, @PathVariable Long catId)
            throws IllegalAccessException{
        return transService.getCategoryRules(pageable,catId);
    }


    @RequestMapping(value = { "createMemberDocs" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    public ResponseEntity<String> createClientDocs(@RequestBody RequiredDocBean requiredDocBean) throws IllegalAccessException, IOException, BadRequestException {
        transService.createMemberRequiredDocs(requiredDocBean);
        return new ResponseEntity<String>("OK",HttpStatus.OK);
    }

    @RequestMapping(value = { "categoryBedType/{catId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<MedicalCategory> getCategoryBedTypes(@DataTable DataTablesRequest pageable, @PathVariable Long catId)
            throws IllegalAccessException{
        return transService.getCategoryBedTypes(pageable,catId);
    }

    @RequestMapping(value = { "createCategoryRule" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void createCategoryRule(CategoryRules categoryRule) throws IllegalAccessException, IOException, BadRequestException {
        transService.defineCategoryRules(categoryRule);
    }


    @RequestMapping(value = { "deleteCategoryRule/{ruleId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCatRule(@PathVariable Long ruleId) {
        transService.deleteCategoryRules(ruleId);
    }

    @RequestMapping(value = {"deletePolCategory/{categoryId}"}, method = {
            RequestMethod.GET
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePolCategory(@PathVariable Long categoryId )  throws BadRequestException {
        transService.deletePolCategory(categoryId);
    }

    @RequestMapping(value ={ "deletePolTaxes/{polTaxId}"} , method = {
            RequestMethod.GET
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePolTaxes(@PathVariable Long polTaxId) throws BadRequestException{
        transService.deletePolTaxes(polTaxId);
    }

    @RequestMapping(value = { "savePolTaxes" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void saveQuotTaxes(PolicyTaxes tax) throws IllegalAccessException, IOException, BadRequestException {
        transService.savePolTaxes(tax);
    }

    @RequestMapping(value = { "principals" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public Page<ClientDef> selectClients(@RequestParam(value = "term", required = false) String term,@RequestParam("catId") Long catId, Pageable pageable)
            throws IllegalAccessException {
        return transService.findCategoryPrincipals(term, pageable,catId);
    }

    @RequestMapping(value = { "policyClauses" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<PolicyClauses> getPolicyClauses(@DataTable DataTablesRequest pageable,HttpServletRequest request)
            throws IllegalAccessException {
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        return policyTransService.findPolicyClauses(pageable,polCode);
    }

    @RequestMapping(value = { "deleteCatMember/{memberId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCatMember(@PathVariable Long memberId) throws BadRequestException {
        transService.deleteMember(memberId);
    }

    @RequestMapping(value = { "undoDeleteCatMember/{memberId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void undoDeleteCatMember(@PathVariable Long memberId) throws BadRequestException {
        transService.undoDeleteMember(memberId);
    }

    @RequestMapping(value = { "policyTaxes" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<PolicyTaxes> getPolicyTaxes(@DataTable DataTablesRequest pageable,HttpServletRequest request)
            throws IllegalAccessException {
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        return policyTransService.findPolicyTaxes(pageable,polCode);
    }

    @RequestMapping(value = { "categoryLoadings/{catId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<CategoryLoadings> getCategoryLoadings(@DataTable DataTablesRequest pageable, @PathVariable Long catId)
            throws IllegalAccessException{
        return transService.getCategoryLoadings(pageable,catId);
    }

    @RequestMapping(value = { "categoryExclusions/{catId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<CategoryExclusions> getCategoryExclusions(@DataTable DataTablesRequest pageable, @PathVariable Long catId)
            throws IllegalAccessException{
        return transService.getCategoryExclusions(pageable,catId);
    }

    @RequestMapping(value = { "saveCatLoadings" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void saveCatLoadings(CategoryLoadings loadings) throws IllegalAccessException, IOException, BadRequestException {
        transService.saveCategoryLoadings(loadings);
    }

    @RequestMapping(value = {"editPolicyDetails"},method = {
            RequestMethod.POST

    })
    @ResponseBody
    public void editPolicyDetails(PolicyCreateDTO policyTrans) throws IllegalAccessException,IOException,BadRequestException{
        transService.savePolicyDetails(policyTrans);
    }

    @RequestMapping(value = { "categoryProviders/{catId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<CatalogueProviders> getCategoryProviders(@DataTable DataTablesRequest pageable, @PathVariable Long catId)
            throws IllegalAccessException{
        return transService.getCategoryProviders(pageable,catId);
    }

    @RequestMapping(value = { "makePolicyReady" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void makePolicyReady(HttpServletRequest request) throws BadRequestException {
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        try{
            computePrem.computePrem(polCode);
        }
        catch(IOException ex){
            throw new BadRequestException(ex.getMessage());
        }

        policyTransService.makeMedicalReady(polCode);

    }

    @RequestMapping(value = { "undoMakeReady" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void undoMakePolicyReady(HttpServletRequest request,
                                    @RequestParam Long reasonId,
                                    @RequestParam String reason) throws BadRequestException {
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        policyTransService.undoMakeReady(polCode, reasonId, reason);

    }

    @RequestMapping(value = { "authorizePolicy" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void authorizePolicy(HttpServletRequest request) throws BadRequestException {
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        authService.authorizeMedicalPolicy(polCode);

    }

    @RequestMapping(value = { "selCoverLimits" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public Page<CoverLimits> selCoverLimits(@RequestParam(value = "term", required = false) String term, @RequestParam("covId") Long covId, Pageable pageable)
            throws IllegalAccessException,BadRequestException {
        return transService.findCoverLimits(term, pageable, covId);
    }


    @RequestMapping(value = { "updateCatbenfits" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void updateCatbenfits(MedCategoryBenefits benefits) throws IllegalAccessException, IOException, BadRequestException {
        transService.updateCategoryBenefits(benefits);
    }

    @RequestMapping(value = { "updateFundBenefit"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void updateFundBenefit(MedCategoryBenefits benefits) throws IllegalAccessException, IOException, BadRequestException{
        transService.updateFundBenefits(benefits);
    }

    @RequestMapping(value = "rpt_risk_note", method = RequestMethod.GET)
    public ModelAndView riskNote(ModelMap modelMap, HttpServletRequest request, ModelAndView modelAndView)
            throws BadRequestException, IOException {
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        OrganizationDTO organization = orgService.getOrganizationLogoDetails();
        InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
        BufferedImage image = ImageIO.read(in);
        modelMap.put("logo", image );
        modelMap.put("datasource", datasource);
        modelMap.put("format", "pdf");
        modelMap.put("polId", polCode);
        modelAndView = new ModelAndView("rpt_medical_risk_note", modelMap);
        return modelAndView;
    }

    @RequestMapping(value = { "selffundparams" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<SelfFundParams> getSelfFundParams(@DataTable DataTablesRequest pageable,HttpServletRequest request)
            throws IllegalAccessException{
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        return transService.findSelfFundParams(pageable,polCode);
    }

    @RequestMapping(value = { "createSelfFundParams" }, method = {
            RequestMethod.POST })
    @ResponseBody
    public  void defineSelfFundParams(SelfFundParams selfFundParams,HttpServletRequest request) throws BadRequestException{
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        PolicyTrans policyTrans = policyTransService.getPolicyDetails(polCode);
        selfFundParams.setPolicyTrans(policyTrans);
        transService.defineSelfFundParams(selfFundParams);
    }


    @RequestMapping(value = { "deleteSelfFundParams/{paramId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSelfFundParams(HttpServletRequest request,@PathVariable Long paramId) throws BadRequestException {
        transService.deleteSelfFundParams(paramId);

    }



    @RequestMapping(value = { "unassignBinderRules" }, method = { RequestMethod.GET })
    @ResponseBody
    public List<Object[]> getUnassignedBinderRules(@RequestParam(value = "catId", required = false) Long catId )
            throws IllegalAccessException {
        return transService.getUnassignedBinderRules(catId);
    }

    @RequestMapping(value = { "createCategoryRules" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    public ResponseEntity<String> createCategoryRules(@RequestBody RulesBean rulesBean) throws IllegalAccessException, IOException, BadRequestException {
        transService.createBinderRules(rulesBean);
        return new ResponseEntity<String>("OK",HttpStatus.OK);
    }

    @RequestMapping(value = { "schemeMembers" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<CategoryMembers> getPolicyMembers(@DataTable DataTablesRequest pageable,HttpServletRequest request)
            throws IllegalAccessException{
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        return transService.getSchemeMembers(pageable,polCode);
    }


    @RequestMapping(value = { "principalDetails/{memberId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<CategoryMembers> getPrincipalDetails(@DataTable DataTablesRequest pageable,@PathVariable Long memberId)
            throws IllegalAccessException{
        return transService.getPrincipalInfo(pageable,memberId);
    }


    @RequestMapping(value = { "dependentDetails/{memberId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<CategoryMembers> getDependentDetails(@DataTable DataTablesRequest pageable,@PathVariable Long memberId)
            throws IllegalAccessException{
        return transService.getDependantsInfo(pageable,memberId);
    }

    @RequestMapping(value = "excelUpload/{file_name}", method = RequestMethod.GET)
    public void getFile(
            @PathVariable("file_name") String fileName,
            HttpServletResponse response,HttpServletRequest request) {
        try {
            String name =  request.getServletContext().getRealPath(fileName+".xls");
            File file = new File(name);
            // get your file as InputStream
            InputStream is =  new FileInputStream(name);
            // copy it to response's OutputStream
            org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
            response.setContentType("application/vnd.ms-excel");
            response.flushBuffer();
        } catch (IOException ex) {
            ex.printStackTrace();
            //log.info("Error writing file to output stream. Filename was '{}'", fileName, ex);
            throw new RuntimeException("IOError writing file to output stream");
        }

    }

    @RequestMapping(value = { "uploadMembers" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public String uploadMembers(MemberUploadForm uploadForm,HttpServletRequest request) throws BadRequestException, IOException {
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        String file =  transService.uploadMembers(uploadForm,polCode,request);
         if(file!=null && !"success".equalsIgnoreCase(file)){
             return file;
         }
         return "";
    }

    @RequestMapping(value = { "unassignMedCovers" }, method = { RequestMethod.GET })
    @ResponseBody
    public List<Object[]> getUnassignMedCovers(@RequestParam(value = "catId", required = false) Long catId,@RequestParam(value = "binCode", required = false) Long binCode )
            throws IllegalAccessException {
        return transService.getUnassignedCovers(catId,binCode);
    }

    @RequestMapping(value = { "unassignPolTaxes" }, method = { RequestMethod.GET })
    @ResponseBody
    public List<Object[]> getUnassignPolTaxes(@RequestParam(value = "polId", required = false) Long polId)
            throws IllegalAccessException {

        return transService.getUnassignPolTaxes(polId);
    }


    @RequestMapping(value = { "createCategoryBenefits" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    public ResponseEntity<String> createCategoryBenefits(@RequestBody MedicalCoverDTO coverDTO) throws IllegalAccessException, IOException, BadRequestException {
        transService.createCategoryBenefits(coverDTO);
        return new ResponseEntity<String>("OK",HttpStatus.OK);
    }

    @RequestMapping(value = { "createNewPolTax" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    public ResponseEntity<String> createNewPolTax(@RequestBody TaxesDTO taxesDTO) throws IllegalAccessException, IOException, BadRequestException {
        transService.createNewPolTax(taxesDTO);
        return new ResponseEntity<String>("OK",HttpStatus.OK);
    }


    @RequestMapping(value = { "deleteCategoryBenefit/{benefitId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryBenefit(@PathVariable Long benefitId,HttpServletRequest request) throws BadRequestException {
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        transService.deleteCategoryBenefit(benefitId,polCode);

    }

    @RequestMapping(value = { "unassignCatProviders" }, method = { RequestMethod.GET })
    @ResponseBody
    public List<Object[]> getUnassignCatProviders(@RequestParam(value = "catId", required = false) Long catId,@RequestParam(value = "binCode", required = false) Long binCode )
            throws IllegalAccessException {
        return transService.getUnassignedBinderProviders(catId,binCode);
    }

    @RequestMapping(value = { "createCategoryProviders" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    public ResponseEntity<String> createCategoryProviders(@RequestBody ProviderBean providerBean) throws IllegalAccessException, IOException, BadRequestException {
        transService.saveCategoryProviders(providerBean);
        return new ResponseEntity<String>("OK",HttpStatus.OK);
    }

    @RequestMapping(value = { "deleteCategoryProvider/{providerId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryProvider(@PathVariable Long providerId,HttpServletRequest request) throws BadRequestException {
        transService.deleteCategoryProviders(providerId);

    }

    @RequestMapping(value = { "getNewClauses" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    public ResponseEntity<Set<PolicyClauses>> getNewClauses(HttpServletRequest request) throws BadRequestException {
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        Set<PolicyClauses> clauses = transService.getNewClauses(polCode);
        return new ResponseEntity<Set<PolicyClauses>>(clauses, HttpStatus.OK);
    }

    @RequestMapping(value = { "createNewClause" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    public ResponseEntity<String> createNewClause(@RequestBody PolicyClausesBean clause,
                                                  HttpServletRequest request) throws BadRequestException {
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        clause.setPolId(polCode);
        transService.createClause(clause);
        return new ResponseEntity<String>("Created", HttpStatus.OK);
    }

    @RequestMapping(value = { "createPolicyClause" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void createPolicyClause(PolicyClauses clause) throws IllegalAccessException, IOException, BadRequestException {
        policyTransService.createPolicyClause(clause);
    }

    @RequestMapping(value = { "deletePolClause/{clauseId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePolClause(@PathVariable Long clauseId) throws BadRequestException {
        policyTransService.deletePolicyClause(clauseId);
    }

    @RequestMapping(value = { "selBedtypes" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public Page<BedTypes> selBedtypes(@RequestParam(value = "term", required = false) String term, Pageable pageable)
            throws IllegalAccessException {
        return transService.findBedTypes(term, pageable);
    }

    @RequestMapping(value = { "createClient" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public String createClient(ClientDTO tenDef) throws IllegalAccessException, BadRequestException, ParseException {
        tenDef.setDateregistered(new Date());
        return setupsService.defineClient(tenDef,false);
    }

    @RequestMapping(value = { "fundreceipts" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<SystemTransactions> getFundReceipts(@DataTable DataTablesRequest pageable, HttpServletRequest request)
            throws IllegalAccessException{
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        return transService.getFundReceipts(pageable,polCode);
    }


    @RequestMapping(value = { "getReceiptedAmt" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    public ResponseEntity<BigDecimal> getReceiptedAmt(HttpServletRequest request)
            throws IllegalAccessException{
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        return new ResponseEntity<BigDecimal>(transService.getTotalReceiptedAmt(polCode),HttpStatus.OK);
    }

    @RequestMapping(value = { "medicalCards" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<MedicalCards> getMemberCards(@DataTable DataTablesRequest pageable,HttpServletRequest request) throws IllegalAccessException {
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        return medicalCardsService.findPolicyMedicalCards(pageable,polCode);
    }

    @RequestMapping(value = { "cancelCard/{cardId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelCard(@PathVariable Long cardId,HttpServletRequest request) throws BadRequestException {
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
       endorseService.replaceCard(cardId,polCode);
    }

    @RequestMapping(value = { "dispatchDocs" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void dispatchDocs(HttpServletRequest request) throws BadRequestException {
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        policyTransService.dispatchDocuments(polCode);

    }


    @ResponseBody
    @RequestMapping(value ="mailtemplate" )
    public String getMailTemplate(HttpServletResponse response, HttpServletRequest request) throws BadRequestException{
        return templateService.getMailTemplate(MailTemplates.POLICY_TEMPLATE,request);
    }

    @RequestMapping(value = { "getReceiverEmail" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    public ResponseEntity<String> getReceiverEmail(@RequestParam(value = "receiver", required = false) String receiver, HttpServletRequest request) throws BadRequestException {
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        String email = mailer.getEmailReceivers(polCode,"P",receiver);
        return new ResponseEntity<String>(email, HttpStatus.OK);
    }

    @RequestMapping(value = { "riskDocs/{memberId}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<RiskDocs> getRiskDocs(@DataTable DataTablesRequest pageable, @PathVariable Long memberId)
            throws IllegalAccessException {
        return transService.findMemberDocs(pageable,memberId);
    }

    @RequestMapping(value = { "uploadRequiredDocs" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadRequiredDocs(UploadBean uploadBean) throws BadRequestException {
        uploadService.uploadRiskDocument(uploadBean);
    }

    @RequestMapping(value = "/riskdocument/{docId}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> thumbnail(@PathVariable Long docId ) throws BadRequestException {
        byte[] content = uploadService.getRiskDocFileDetails(docId);
        if (content.length>0) {
            String contentType = uploadService.getDocContentType(docId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentLength(content.length);
            return new ResponseEntity<byte[]>(content, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<byte[]>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = { "deleteRiskDoc/{docId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRiskDoc(@PathVariable Long docId) throws BadRequestException {
        uploadService.deleteRiskDoc(docId);
    }


    @RequestMapping(value = { "getInhouseEmail" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    public ResponseEntity<String> getInhouseEmail() throws BadRequestException {
        String email = quotationService.getInhouseEmail();
        return new ResponseEntity<String>(email, HttpStatus.OK);
    }

    @RequestMapping(value = { "policyChecks" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<TransChecks> getPolicyChecks(@DataTable DataTablesRequest pageable, HttpServletRequest request)
            throws IllegalAccessException {
        Long policyCode = (Long) request.getSession().getAttribute("policyCode");
        if(policyCode==null) policyCode=-2000l;
        return policyTransService.findPolicyChecks(pageable,policyCode);
    }

    @RequestMapping(value = "medMembersTemplate",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public void  commissionsTemplate(HttpServletRequest request, HttpServletResponse response) throws BadRequestException,IOException
    {
        String name ="members_upload_template";
        try {
            File myFile = uploadService.getMedMembersTemplate();
            // get your file as InputStream
            InputStream is =  new FileInputStream(myFile);

            // Audit User Actions
            auditTrailLogger.log("Downloaded The Medical Members Template: "+ name+ ".xls", request, "Med Members Template");

            // copy it to response's OutputStream
            org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
            response.setHeader("Content-Disposition", "attachment; filename=\"" + name + ".xls\"");
            response.setContentType("application/vnd.ms-excel");
            response.flushBuffer();
        } catch (IOException ex) {
            ex.printStackTrace();
            //log.info("Error writing file to output stream. Filename was '{}'", fileName, ex);
            throw new RuntimeException("IOError writing file to output stream");
        }
    }

}
