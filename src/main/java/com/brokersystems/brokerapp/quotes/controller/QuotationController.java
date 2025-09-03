package com.brokersystems.brokerapp.quotes.controller;


import com.brokersystems.brokerapp.dms.service.UploadService;
import com.brokersystems.brokerapp.dms.model.UploadBean;
import com.brokersystems.brokerapp.kie.rules.ClientRulesExecutor;
import com.brokersystems.brokerapp.medical.model.RulesBean;

import com.brokersystems.brokerapp.mail.model.MailMessageBean;
import com.brokersystems.brokerapp.mail.model.MailTemplates;
import com.brokersystems.brokerapp.mail.service.MailTemplateService;
import com.brokersystems.brokerapp.mail.service.Mailer;

import com.brokersystems.brokerapp.quotes.dto.*;
import com.brokersystems.brokerapp.quotes.model.*;
import com.brokersystems.brokerapp.quotes.repository.QuotTransRepo;
import com.brokersystems.brokerapp.quotes.services.MedComputeQuotPrem;
import com.brokersystems.brokerapp.quotes.services.QuotationService;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.DateUtilities;
import com.brokersystems.brokerapp.server.utils.FormLockManager;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.server.utils.ValidatorUtils;
import com.brokersystems.brokerapp.setup.dto.ClientDTO;
import com.brokersystems.brokerapp.setup.dto.OrganizationDTO;
import com.brokersystems.brokerapp.setup.dto.ProspectsDTO;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.service.ClientService;
import com.brokersystems.brokerapp.setup.service.OrganizationService;
import com.brokersystems.brokerapp.setup.service.SetupsService;
import com.brokersystems.brokerapp.users.repository.MakerCheckerRepo;
import com.brokersystems.brokerapp.uw.dtos.TaxesDTO;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import com.brokersystems.brokerapp.workflow.utils.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
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
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by peter on 3/12/2017.
 */
@Controller
@RequestMapping({"/protected/quotes"})
public class QuotationController {

    @Autowired
    private QuotationService quotationService;

    @Autowired
    private PolicyTransService policyService;

    @Autowired
    private DateUtilities dateUtilities;

    @Autowired
    private OrganizationService orgService;

    @Autowired
    private ClientService tenService;

    @Autowired
    private DataSource datasource;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private SetupsService setupsService;

    @Autowired
    private ValidatorUtils validator;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private Mailer mailer;

    @Autowired
    private MailTemplateService templateService;

    @Autowired
    private MedComputeQuotPrem medComputeQuotPrem;

    @Autowired
    private ClientRulesExecutor clientRulesExecutor;

    @Autowired
    private ClientService clientService;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private QuotTransRepo quotTransRepo;
    @Autowired
    private MakerCheckerRepo makerCheckerRepo;
    @Autowired
    private FormLockManager formLockManager;


//    @InitBinder
//    protected void initBinder(WebDataBinder binder) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//        dateFormat.setLenient(false);
//        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
//    }


    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }


    @RequestMapping(value = "/editquottrans", method = RequestMethod.POST)
    public String editQuoteForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model, HttpServletRequest request) {
        request.getSession().setAttribute("policyCode", helperForm.getId());
        model.addAttribute("quotId", helperForm.getId());
        model.addAttribute("polId", helperForm.getId());
        request.getSession().setAttribute("quotCode", helperForm.getId());
        long count = quotationService.medCount(helperForm.getId());
        if (count > 0) {
            return "quotform";
        } else {
            return "medquotform";
        }

    }

    @RequestMapping(value = "/editquote/{quoteCode}", method = RequestMethod.GET)
    public String redirectQuote(@PathVariable Long quoteCode, HttpServletRequest request) throws BadRequestException {
        request.getSession().setAttribute("quotCode", quoteCode);
        formLockManager.openForm("QUOTE", String.valueOf(quoteCode));
        return "redirect:/protected/quotes/editquote";
    }

    @RequestMapping(value = "/editquote", method = RequestMethod.GET)
    public String editQuoteTrans(Model model, HttpServletRequest request) {
        Long quotCode = (Long) request.getSession().getAttribute("quotCode");
        model.addAttribute("quotId", quotCode);
        long count = quotationService.medCount(quotCode);
        if (count > 0) {
            return "quotform";
        } else {
            return "medquotform";
        }
    }


    @RequestMapping(value = "/editClientQuote", method = RequestMethod.GET)
    public String editClientQuote(Model model, HttpServletRequest request) {
        Long quotCode = (Long) request.getSession().getAttribute("quotCode");
        if (quotCode == null) {
            model.addAttribute("quotId", -2000);
        } else
            model.addAttribute("quotId", quotCode);
        return "quotform";
    }

    @RequestMapping(value = "/editMedQuote", method = RequestMethod.GET)
    public String editMedQuote(Model model, HttpServletRequest request) {
        Long quotCode = (Long) request.getSession().getAttribute("quotCode");
        if (quotCode == null) {
            model.addAttribute("quotId", -2000);
        } else
            model.addAttribute("quotId", quotCode);
        return "medquotform";
    }


    @RequestMapping(value = "medquotform", method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String medQuotesHome(Model model) {
        model.addAttribute("quotId", -2000);
        return "medquotform";
    }


    @RequestMapping(value = "enquiryquotes", method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String enquiryquotes(Model model) {
        return "quoteenquiry";
    }

    @RequestMapping(value = "convertquote", method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String convertQuotes(Model model) {
        return "convertqt";
    }


    @RequestMapping(value = {"quotTrans"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<QuoteTrans> getUserQuotTrans(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return quotationService.findUserQuotes(pageable);
    }

    @RequestMapping(value = {"businesssources"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Page<BusinessSources> selectBusinessSources(@RequestParam(value = "term", required = false) String term, @RequestParam("srcGroupId") Long srcGroupId, Pageable pageable)
            throws IllegalAccessException {
        return quotationService.findBusinessSources(term, pageable, srcGroupId);
    }

    @RequestMapping(value = "quotform", method = RequestMethod.GET)
    public String tenantForm(Model model) {
        model.addAttribute("quotId", -2000);
        return "quotform";
    }

    @RequestMapping(value = {"getQuotationDetails/{quotCode}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    public ResponseEntity<QuoteDetailsDTO> getPolicyDetails(@PathVariable long quotCode) throws BadRequestException {
        QuoteDetailsDTO created = quotationService.getQuotationDetails(quotCode);
        return new ResponseEntity<>(created, HttpStatus.OK);
    }

    @RequestMapping(value = {"getMedQuotationDetails/{quotCode}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    public ResponseEntity<QuoteTrans> getMedQuoteDetails(@PathVariable long quotCode) throws BadRequestException {
        QuoteTrans created = quotationService.getMedQuotationDetails(quotCode);
        if (created.getWefDate() instanceof Date) {
            System.out.println("Wef Date instanceof Date....");
        } else
            System.out.println("date from=" + created.getWefDate() + " cover from=" + created.getWetDate());
        return new ResponseEntity<QuoteTrans>(created, HttpStatus.OK);
    }

    @RequestMapping(value = {"getBinderPremRates"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    public ResponseEntity<Set<RiskSectionBean>> getBinderPremRates(
            @RequestParam(value = "detId", required = false) Long detId) throws BadRequestException {
        Set<RiskSectionBean> rates = policyService.getBinderPremRates(detId);
        return new ResponseEntity<Set<RiskSectionBean>>(rates, HttpStatus.OK);
    }

    @RequestMapping(value = {"getWetDate"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Date getPolicyWetDate(@RequestParam(value = "wefDate", required = false) Date wef) {
        return dateUtilities.getWetDate(wef);
    }

    @RequestMapping(value = {"createQuotation"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public ResponseEntity<QuoteTrans> createQuoteTrans(@RequestBody CreateQuoteDTO quote,
                                                       HttpServletRequest request) throws BadRequestException {
        QuoteTrans created = quotationService.createQuotation(quote);
        Long quotCode = created.getQuoteId();
        try {
            quotationService.computeQuotPrem(quotCode);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
        request.getSession().setAttribute("quotCode", quotCode);
        return new ResponseEntity<QuoteTrans>(created, HttpStatus.OK);
    }

    @RequestMapping(value = {"compareQuote"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public ResponseEntity<ComparisonQuotDTO> compareQuote(@RequestBody ComparisonQuotDTO comparisonQuotDTO, HttpServletRequest request) throws BadRequestException {
        ComparisonQuotDTO compare = quotationService.compareQuot(comparisonQuotDTO);
        Long quotId = comparisonQuotDTO.getQuotId();
        try {
            quotationService.computeQuotPrem(quotId);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
        request.getSession().setAttribute("quotId", quotId);
        return new ResponseEntity<ComparisonQuotDTO>(compare, HttpStatus.OK);
    }

    @RequestMapping(value = {"makeMedQuoteReady"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void makeMedQuoteReady(HttpServletRequest request) throws BadRequestException {
        Long quotCode = (Long) request.getSession().getAttribute("quotCode");

        try {
            medComputeQuotPrem.computePrem(quotCode);
        } catch (IOException ex) {
            throw new BadRequestException(ex.getMessage());
        }

        quotationService.MakeQuotReady(quotCode);

    }

    @RequestMapping(value = {"createMedQuotation"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public ResponseEntity<QuoteTrans> createMedQuoteTrans(@RequestBody CreateQuoteDTO quote,
                                                          HttpServletRequest request) throws BadRequestException {
        QuoteTrans created = quotationService.createMedQuotation(quote);
        Long quotCode = created.getQuoteId();


        request.getSession().setAttribute("quotCode", quotCode);
        request.getSession().setAttribute("quotId", quotCode);

        return new ResponseEntity<QuoteTrans>(created, HttpStatus.OK);
    }

    @RequestMapping(value = {"undoQuotMakeReady"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void undoQuotMakeReady(HttpServletRequest request) throws BadRequestException {
        Long quotcode = (Long) request.getSession().getAttribute("quotCode");
        quotationService.undoMakeReady(quotcode);

    }

    @RequestMapping(value = {"quotProducts"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<QuoteProductDTO> getQuotProducts(@DataTable DataTablesRequest pageable, HttpServletRequest request) {
        Long quotCode = (Long) request.getSession().getAttribute("quotCode");
        return quotationService.findQuotProducts(pageable, quotCode);
    }

    @RequestMapping(value = {"quotProductRisks/{qrCode}"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<QuoteRiskDTO> getQuotProductRisks(@DataTable DataTablesRequest pageable, @PathVariable Long qrCode)
            throws IllegalAccessException {
        return quotationService.findProdRisks(pageable, qrCode);
    }

    @RequestMapping(value = {"quotRiskLimits/{riskId}"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<QuotRiskPremItemsDTO> getQuotRiskLimits(@DataTable DataTablesRequest pageable, @PathVariable Long riskId)
            throws IllegalAccessException {
        return quotationService.findQuotRiskLimits(pageable, riskId);
    }

    @RequestMapping(value = {"saveRiskSections"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void createQuotProductSection(QuotRiskPremItemsDTO riskLimits, HttpServletRequest request) throws BadRequestException {
        quotationService.createQuotRiskSection(riskLimits);
        Long quotCode = (Long) request.getSession().getAttribute("quotCode");
        try {
            quotationService.computeQuotPrem(quotCode);
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @RequestMapping(value = {"deleteProductRisk/{riskId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductRisk(@PathVariable Long riskId, HttpServletRequest request) throws BadRequestException {
        quotationService.deleteProductRisks(riskId);
        Long quotCode = (Long) request.getSession().getAttribute("quotCode");
        try {
            quotationService.computeQuotPrem(quotCode);
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @RequestMapping(value = {"deleteRiskSection/{sectId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRiskSection(@PathVariable Long sectId, HttpServletRequest request) throws BadRequestException {
        quotationService.deleteQuoteRiskSections(sectId);
        Long quotCode = (Long) request.getSession().getAttribute("quotCode");
        try {
            quotationService.computeQuotPrem(quotCode);
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @RequestMapping(value = {"updateQuotCatbenefits"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public void updateQuotCatbenefits(MedQuotCategoryBenefits benefits) throws IllegalAccessException, IOException, BadRequestException {
        quotationService.updateCategoryBenefits(benefits);
    }

    @RequestMapping(value = {"updateQuotFundBenefit"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public void updateQuotFundBenefit(MedQuotCategoryBenefits benefits) throws IllegalAccessException, IOException, BadRequestException {
        quotationService.updateFundBenefits(benefits);
    }

    @RequestMapping(value = {"deleteQuoteProduct/{qpId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteQuoteProduct(@PathVariable Long qpId, HttpServletRequest request) throws BadRequestException {
        quotationService.deleteQuotProducts(qpId);
        Long quotCode = (Long) request.getSession().getAttribute("quotCode");
        try {
            quotationService.computeQuotPrem(quotCode);
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @RequestMapping(value = {"deleteQuote/{quoteId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteQuote(@PathVariable Long quoteId, HttpServletRequest request) throws BadRequestException {
        quotationService.deleteQuote(quoteId);

    }

    @RequestMapping(value = {"quotProTaxes/{qrCode}"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<QuotTaxesDTO> getProductTaxes(@DataTable DataTablesRequest pageable, @PathVariable Long qrCode) throws IllegalAccessException {
        return quotationService.getProductTaxes(pageable, qrCode);
    }

    @RequestMapping(value = {"quotProClauses/{qrCode}"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<QuoteClauseDTO> getProductClauses(@DataTable DataTablesRequest pageable, @PathVariable Long qrCode) throws IllegalAccessException {
        return quotationService.getProductClauses(pageable, qrCode);
    }

    @RequestMapping(value = {"getNewPremiumItems"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    public ResponseEntity<List<PremRatesDef>> getNewPremItems(
            @RequestParam(value = "detId", required = false) Long detId, @RequestParam(value = "riskId", required = false) Long riskId,
            @RequestParam(value = "secName", required = false) String secName) throws BadRequestException {
        List<PremRatesDef> rates = quotationService.getNewPremiumItems(detId, riskId, secName);
        return new ResponseEntity<List<PremRatesDef>>(rates, HttpStatus.OK);
    }

    @RequestMapping(value = {"createPremiumItems"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public ResponseEntity<String> createPremiumItems(@RequestBody RiskBean sections,
                                                     HttpServletRequest request) throws BadRequestException {
        quotationService.createRiskSections(sections);
        return new ResponseEntity<String>("Created", HttpStatus.OK);
    }

    @RequestMapping(value = {"createRisk"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public ResponseEntity<String> createRiskTrans(@RequestBody QuoteRiskTrans riskTrans,
                                                  HttpServletRequest request) throws BadRequestException, IOException {
        quotationService.createProductRisk(riskTrans);
        return new ResponseEntity<String>("Ok", HttpStatus.OK);
    }

    @RequestMapping(value = {"sourcesgroups"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Page<BusinessSourceGroups> selectSourcesGroups(@RequestParam(value = "term", required = false) String term, Pageable pageable)
            throws IllegalAccessException {
        return quotationService.findSourcesGroups(term, pageable);
    }

    @RequestMapping(value = {"createQuoteProduct"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public ResponseEntity<String> createQuoteProduct(@RequestBody CreateQuoteProductDTO proTrans,
                                                     HttpServletRequest request) throws BadRequestException {
        Long quotCode = (Long) request.getSession().getAttribute("quotCode");
        QuoteTrans quoteTrans = quotTransRepo.findOne(QQuoteTrans.quoteTrans.quoteId.eq(quotCode));
        proTrans.setQuoteId(quotCode);
        quotationService.createQuotProduct(proTrans);
        try {
            quotationService.computeQuotPrem(quotCode);
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }
        return new ResponseEntity<String>("Ok", HttpStatus.OK);
    }

    @RequestMapping(value = {"makeReadyCompQuote"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void makeReadyCompQuote(HttpServletRequest request, @RequestParam("quoteProductId") Long quoteProductId) throws BadRequestException {
        Long quotCode = (Long) request.getSession().getAttribute("quotCode");
        System.out.println("Quote Product Id: " + quoteProductId);
        quotationService.MakeCompQuotReady(quotCode, quoteProductId);
        quotationService.authorizeQuote(quotCode);
        quotationService.confirmQuoteWF(quotCode);
    }

    @RequestMapping(value = {"makeReadyQuote"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void makeReadyQuote(HttpServletRequest request) throws BadRequestException {
        Long quotCode = (Long) request.getSession().getAttribute("quotCode");
        quotationService.MakeQuotReady(quotCode);
        quotationService.authorizeQuote(quotCode);
        quotationService.confirmQuoteWF(quotCode);

    }

    @RequestMapping(value = {"undoMakeReadyQuote"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void undoMakeReadyQuote(HttpServletRequest request) throws BadRequestException {
        Long quotCode = (Long) request.getSession().getAttribute("quotCode");
        quotationService.undoMakeReady(quotCode);


    }

    @RequestMapping(value = {"confirmQuote"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void confirmQuote(HttpServletRequest request) throws BadRequestException {
        Long quotCode = (Long) request.getSession().getAttribute("quotCode");
        System.out.println("confirm quote=" + quotCode);
        quotationService.confirmQuoteWF(quotCode);

    }

    @RequestMapping(value = {"authorizeQuote"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void authorizeQuote(HttpServletRequest request) throws BadRequestException {
        Long quotCode = (Long) request.getSession().getAttribute("quotCode");
        quotationService.authorizeQuote(quotCode);

    }


    @RequestMapping(value = {"cancelQuote"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelQuote(HttpServletRequest request, @RequestParam(value = "reason", required = false) String reason) throws BadRequestException {
        Long quotCode = (Long) request.getSession().getAttribute("quotCode");
        quotationService.cancelQuoteWF(quotCode, reason);

    }

    @RequestMapping(value = {"getNewClauses/{prodId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    public ResponseEntity<Set<QuotClausesDTO>> getNewClauses(@PathVariable Long prodId) throws BadRequestException {
        Set<QuotClausesDTO> clauses = quotationService.getNewClauses(prodId);
        return new ResponseEntity<>(clauses, HttpStatus.OK);
    }

    @RequestMapping(value = {"createQuoteClause"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public void createPolicyClause(QuoteClausesDTO clause) throws IllegalAccessException, IOException, BadRequestException {
        quotationService.createQuotClause(clause);
    }

    @RequestMapping(value = {"createNewClause"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    public ResponseEntity<String> createNewClause(@RequestBody QuoteClausesBean clause,
                                                  HttpServletRequest request) throws BadRequestException {
        quotationService.createClause(clause);
        return new ResponseEntity<String>("Created", HttpStatus.OK);
    }

    @RequestMapping(value = {"quotenquiry"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<QuoteEnquireDTO> quoteEnquiry(@DataTable DataTablesRequest pageable,
                                                          @RequestParam(value = "quoteNo", required = false) String quoteNo,
                                                          @RequestParam(value = "clientCode", required = false) Long clientCode,
                                                          @RequestParam(value = "prsCode", required = false) Long prsCode)
            throws IllegalAccessException {
        return quotationService.quoteEnquiry(pageable, clientCode, quoteNo, prsCode);
    }


    @RequestMapping(value = {"convertedquotes"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<QuoteProductDTO> convertedQuotesEnquiry(@DataTable DataTablesRequest pageable,
                                                                    @RequestParam(value = "quoteNo", required = false) String quoteNo,
                                                                    @RequestParam(value = "clientCode", required = false) Long clientCode,
                                                                    @RequestParam(value = "agentCode", required = false) Long agentCode,
                                                                    @RequestParam(value = "proCode", required = false) Long proCode,
                                                                    @RequestParam(value = "prsCode", required = false) Long prsCode)
            throws IllegalAccessException {
        return quotationService.quoteProductEnquiry(pageable, clientCode, quoteNo, proCode, agentCode, prsCode);
    }

    @RequestMapping(value = {"getProductDetails/{quotCode}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    public ResponseEntity<QuoteProductDTO> getProductDetails(@PathVariable Long quotCode) throws BadRequestException {
        QuoteProductDTO created = quotationService.getProductDetails(quotCode);
        return new ResponseEntity<>(created, HttpStatus.OK);
    }

    @RequestMapping(value = {"convertedMedquotes"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<QuoteTrans> convertMedQuotesEnquiry(@DataTable DataTablesRequest pageable,
                                                                @RequestParam(value = "quoteNo", required = false) String quoteNo,
                                                                @RequestParam(value = "clientCode", required = false) Long clientCode,
                                                                @RequestParam(value = "agentCode", required = false) Long agentCode,
                                                                @RequestParam(value = "proCode", required = false) Long proCode,
                                                                @RequestParam(value = "prsCode", required = false) Long prsCode)
            throws IllegalAccessException {
        return quotationService.medQuoteEnquiry(pageable, clientCode, quoteNo, proCode, agentCode, prsCode);
    }


    @RequestMapping(value = {"convertQuote"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public QuoteProspectResult convertQuote(@RequestBody ConvertQuotDTO convertQuotDTO, HttpServletRequest request) throws BadRequestException, IOException {
        QuoteProspectResult result = quotationService.convertQuoteProduct(convertQuotDTO);
        request.getSession().setAttribute("policyCode", result.getPolId());
        System.out.println("Result policy id>>>" + result.getPolId());
        return result;
    }

    @RequestMapping(value = {"reuseQuote/{quoteCode}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Long reuseQuote(@PathVariable Long quoteCode, HttpServletRequest request) throws BadRequestException {
        Long ret = quotationService.reuseQuote(quoteCode);
        return ret;
    }

    @RequestMapping(value = {"reviseQuote/{quoteCode}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Long reviseQuote(@PathVariable Long quoteCode, HttpServletRequest request) throws BadRequestException {
        Long ret = quotationService.reviseQuote(quoteCode);
        return ret;
    }


    @RequestMapping(value = {"convertMedQuote/{quoteCode}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public QuoteProspectResult convertMedQuote(@PathVariable Long quoteCode, HttpServletRequest request) throws BadRequestException {

        return quotationService.convertMedQuote(quoteCode);
    }

    @RequestMapping(value = {"categoryQuotBenefits/{catId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<MedQuotCategoryBenefits> getCategoryBenefits(@DataTable DataTablesRequest pageable, @PathVariable Long catId)
            throws IllegalAccessException {
        return quotationService.getCategoryBenefits(pageable, catId);
    }

    @RequestMapping(value = {"quotCategoryRules/{catId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<QuoteCategoryRules> getCategoryRules(@DataTable DataTablesRequest pageable, @PathVariable Long catId)
            throws IllegalAccessException {
        return quotationService.getCategoryRules(pageable, catId);
    }

    @RequestMapping(value = {"quotCategoryFamDetails/{catId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<MedQuotCatFamilyDetails> getCategoryFamDetails(@DataTable DataTablesRequest pageable, @PathVariable Long catId)
            throws IllegalAccessException {
        return quotationService.getCategoryFamDetails(pageable, catId);
    }


    @RequestMapping(value = {"familySize/{quotCode}"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public List<FamilySizes> selectfamilySize(@PathVariable long quotCode)
            throws IllegalAccessException, BadRequestException, FileNotFoundException, IOException {
        return quotationService.familySize(quotCode);
    }


    @RequestMapping(value = {"ageBracket/{quotCode}"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public List<AgeBrackets> selectageBracket(@PathVariable long quotCode)
            throws IllegalAccessException, BadRequestException, FileNotFoundException, IOException {
        return quotationService.getAgeBrackets(quotCode);
    }

    @RequestMapping(value = {"getQuotCategories"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<MedicalQuoteCategory> getMedicalCategories(@DataTable DataTablesRequest pageable, HttpServletRequest request)
            throws IllegalAccessException {
        Long quotCode = (Long) request.getSession().getAttribute("quotCode");
        return quotationService.findQuotCategories(pageable, quotCode);
    }

    @RequestMapping(value = {"quotClauses"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<MedQuoteClauses> getQuoteClauses(@DataTable DataTablesRequest pageable, HttpServletRequest request)
            throws IllegalAccessException {
        Long quotCode = (Long) request.getSession().getAttribute("quotCode");
        return quotationService.findQuotClauses(pageable, quotCode);
    }

    @RequestMapping(value = {"quotTaxes"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<MedicalQuoteTaxes> getPolicyTaxes(@DataTable DataTablesRequest pageable, HttpServletRequest request)
            throws IllegalAccessException {
        Long quotCode = (Long) request.getSession().getAttribute("quotCode");
        if (quotCode == null) {
            return null;
        } else
            return quotationService.findQuotTaxes(pageable, quotCode);
    }

    @RequestMapping(value = {"createQuotCategories"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public void createQuotCategories(MedicalQuoteCategory category) throws IllegalAccessException, IOException, BadRequestException {
        quotationService.defineMedicalCategories(category);
    }

    @RequestMapping(value = {"saveQuotTaxes"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public void saveQuotTaxes(MedicalQuoteTaxes tax) throws IllegalAccessException, IOException, BadRequestException {
        quotationService.saveQuotTaxes(tax);
    }

    @RequestMapping(value = {"unassignQuotTaxes"}, method = {RequestMethod.GET})
    @ResponseBody
    public List<Object[]> getUnassignQuotTaxes(@RequestParam(value = "quotId", required = false) Long quotId)
            throws IllegalAccessException {

        return quotationService.getUnassignQuotTaxes(quotId);
    }

    @RequestMapping(value = {"createNewQuotTax"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    public ResponseEntity<String> createNewQuotTax(@RequestBody TaxesDTO taxesDTO) throws IllegalAccessException, IOException, BadRequestException {
        quotationService.createNewQuotTax(taxesDTO);
        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }

    @RequestMapping(value = {"deleteCategoryRule/{ruleId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCatRule(@PathVariable Long ruleId) {
        quotationService.deleteCategoryRules(ruleId);
    }

    @RequestMapping(value = {"deleteQuotTax/{quotTaxId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteQuotTax(@PathVariable Long quotTaxId) {
        quotationService.deleteQuotTax(quotTaxId);
    }

    @RequestMapping(value = {"deleteQuotGenTax/{quotTaxId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGenQuotTax(@PathVariable Long quotTaxId) throws BadRequestException, IOException {
        quotationService.deleteQuotGenTax(quotTaxId);
    }

    @RequestMapping(value = {"editQuotGenTax"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editGenQuotTax(@RequestParam Long polTaxId, @RequestParam BigDecimal taxRate,
                               @RequestParam BigDecimal divFactor) throws BadRequestException, IOException {
        quotationService.editGenTax(polTaxId, divFactor, taxRate);
    }

    @RequestMapping(value = {"deleteQuotCategory/{categoryId}"}, method = {
            RequestMethod.GET
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePolCategory(@PathVariable Long categoryId) {
        quotationService.deleteQuotCategory(categoryId);
    }


    @RequestMapping(value = {"createQuotFamDetails"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public void createQuotFamDetails(MedQuotCatFamilyDetails familyDetails) throws IllegalAccessException, IOException, BadRequestException {
        quotationService.defineMedQuotFamDetails(familyDetails);
    }

    @RequestMapping(value = {"createCategoryRule"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public void createCategoryRule(QuoteCategoryRules categoryRule) throws IllegalAccessException, IOException, BadRequestException {
        quotationService.defineCategoryRules(categoryRule);
    }

    @RequestMapping(value = {"createCategoryRules"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    public ResponseEntity<String> createCategoryRules(@RequestBody RulesBean rulesBean) throws IllegalAccessException, IOException, BadRequestException {
        quotationService.createBinderRules(rulesBean);
        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }

    @RequestMapping(value = {"unassignBinderRules"}, method = {RequestMethod.GET})
    @ResponseBody
    public List<Object[]> getUnassignedBinderRules(@RequestParam(value = "catId", required = false) Long catId)
            throws IllegalAccessException {
        return quotationService.getUnassignedBinderRules(catId);
    }

    @RequestMapping(value = {"createProspectConvert"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    //@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    @ResponseBody
    public Long createProspectConvert(ProspectsDTO prospectsDTO, HttpServletRequest request) throws BadRequestException, IOException {
        if (prospectsDTO.getTenId() == null)
            throw new BadRequestException("Invalid Prospect to convert...");
        if (prospectsDTO.getPolStartDate() == null) {
            throw new BadRequestException("Please Provide Policy Start Date");
        }
        //System.out.println("Tenant... ..."+prospectsDTO.getClientType());
        //System.out.println("Tenant... ..."+new Gson().toJson(prospectsDTO));
        tenService.defineProspect(prospectsDTO);
        QuoteProspectResult convert = new QuoteProspectResult();
        ConvertQuotDTO convertQuotDTO = new ConvertQuotDTO();
        convertQuotDTO.setQuotProdId(prospectsDTO.getProdId());
        convertQuotDTO.setStartDate(dateUtilities.removeTime(prospectsDTO.getPolStartDate()));

        if (prospectsDTO.getProdId() != null) {
            convert = quotationService.convertQuoteProduct(convertQuotDTO);
        } else if (prospectsDTO.getQuotationId() != null) {
            convert = quotationService.convertMedQuote(prospectsDTO.getQuotationId());
        }
        if (!convert.getResult().equalsIgnoreCase("Y")) {
            throw new BadRequestException(convert.getResult());
        }
        request.getSession().setAttribute("policyCode", convert.getPolId());
        return convert.getPolId();
    }

    @RequestMapping(value = "/edituwtrans", method = RequestMethod.POST)
    public String editPolicyForm(@Valid @ModelAttribute ModelHelperForm helperForm, RedirectAttributes redirectAttrs, Model model, HttpServletRequest request) throws BadRequestException {
        request.getSession().setAttribute("policyCode", helperForm.getId());
        System.out.println("Policy attribute>>>>>: " + helperForm.getId());
        PolicyTrans policyTrans = policyService.getPolicyDetails(helperForm.getId());
        System.out.println("helper id = " + helperForm.getId());
        if ("MD".equalsIgnoreCase(policyTrans.getProduct().getProGroup().getPrgType()))
            return "redirect:/protected/medical/policies/edituwpolicy";
        else {
            return "redirect:/protected/uw/policies/edituwpolicy";
        }
    }

    @RequestMapping(value = "rpt_client_quote", method = RequestMethod.GET)
    public ModelAndView clientQuote(ModelMap modelMap, HttpServletRequest request, ModelAndView modelAndView)
            throws BadRequestException, IOException {
        Long quotCode = (Long) request.getSession().getAttribute("quotCode");
        OrganizationDTO organization = orgService.getOrganizationLogoDetails();
        InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
        BufferedImage image = ImageIO.read(in);
        modelMap.put("logo", image);
        modelMap.put("datasource", datasource);
        modelMap.put("format", "pdf");
        modelMap.put("quotId", quotCode);
        modelAndView = new ModelAndView("rpt_client_quote", modelMap);
        return modelAndView;
    }
    @RequestMapping(value = "rpt_client_quote_summary", method = RequestMethod.GET)
    public ModelAndView clientQuoteSummary(ModelMap modelMap, HttpServletRequest request, ModelAndView modelAndView)
            throws BadRequestException, IOException {
        Long quotCode = (Long) request.getSession().getAttribute("quotCode");
        OrganizationDTO organization = orgService.getOrganizationLogoDetails();
        InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
        BufferedImage image = ImageIO.read(in);
        modelMap.put("logo", image);
        modelMap.put("datasource", datasource);
        modelMap.put("format", "pdf");
        modelMap.put("quotId", quotCode);
        modelAndView = new ModelAndView("rpt_comp_client_quote_summary", modelMap);
        return modelAndView;
    }

    @RequestMapping(value = "rpt_comp_client_quote", method = RequestMethod.GET)
    public ModelAndView clientCompQuote(ModelMap modelMap, HttpServletRequest request, ModelAndView modelAndView)
            throws BadRequestException, IOException {
        Long quotCode = (Long) request.getSession().getAttribute("quotCode");
        OrganizationDTO organization = orgService.getOrganizationLogoDetails();
        InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
        BufferedImage image = ImageIO.read(in);
        modelMap.put("logo", image);
        modelMap.put("datasource", datasource);
        modelMap.put("format", "pdf");
        modelMap.put("quotId", quotCode);
        modelAndView = new ModelAndView("rpt_comp_client_quote", modelMap);
        return modelAndView;
    }

    @RequestMapping(value = "rpt_comb_client_quote", method = RequestMethod.GET)
    public ModelAndView clientCombQuote(ModelMap modelMap, HttpServletRequest request, ModelAndView modelAndView)
            throws BadRequestException, IOException {
        Long quotCode = (Long) request.getSession().getAttribute("quotCode");
        OrganizationDTO organization = orgService.getOrganizationLogoDetails();
        InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
        BufferedImage image = ImageIO.read(in);
        modelMap.put("logo", image);
        modelMap.put("datasource", datasource);
        modelMap.put("format", "pdf");
        modelMap.put("quotId", quotCode);
        modelAndView = new ModelAndView("rpt_comb_client_quote", modelMap);
        return modelAndView;
    }

    @RequestMapping(value = "rpt_med_client_quote", method = RequestMethod.GET)
    public ModelAndView medClientQuote(ModelMap modelMap, HttpServletRequest request, ModelAndView modelAndView)
            throws BadRequestException, IOException {
        Long quotCode = (Long) request.getSession().getAttribute("quotCode");
        OrganizationDTO organization = orgService.getOrganizationLogoDetails();
        InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
        BufferedImage image = ImageIO.read(in);
        modelMap.put("logo", image);
        modelMap.put("datasource", datasource);
        modelMap.put("format", "pdf");
        modelMap.put("quotId", quotCode);
        modelAndView = new ModelAndView("rpt_med_client_quote", modelMap);
        return modelAndView;
    }

    @RequestMapping(value = "rpt_med_quote_summ", method = RequestMethod.GET)
    public ModelAndView medQuoteSummary(ModelMap modelMap, HttpServletRequest request, ModelAndView modelAndView)
            throws BadRequestException, IOException {
        Long quotCode = (Long) request.getSession().getAttribute("quotCode");
        OrganizationDTO organization = orgService.getOrganizationLogoDetails();
        InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
        BufferedImage image = ImageIO.read(in);
        modelMap.put("logo", image);
        modelMap.put("datasource", datasource);
        modelMap.put("format", "pdf");
        modelMap.put("quotId", quotCode);
        modelAndView = new ModelAndView("rpt_med_quote_summ", modelMap);
        return modelAndView;
    }

    @RequestMapping(value = "rpt_quote_info", method = RequestMethod.GET)
    public ModelAndView quoteInfo(ModelMap modelMap, HttpServletRequest request, ModelAndView modelAndView)
            throws BadRequestException, IOException {
        Long quotCode = (Long) request.getSession().getAttribute("quotCode");
        OrganizationDTO organization = orgService.getOrganizationLogoDetails();
        InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
        BufferedImage image = ImageIO.read(in);
        modelMap.put("logo", image);
        modelMap.put("datasource", datasource);
        modelMap.put("format", "pdf");
        modelMap.put("quotId", quotCode);
        modelAndView = new ModelAndView("rpt_client_info", modelMap);
        return modelAndView;
    }

    @RequestMapping(value = {"selprospects"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Page<ProspectsDTO> selectProspects(@RequestParam(value = "term", required = false) String term, Pageable pageable)
            throws IllegalAccessException {
        return quotationService.findActiveProspects(term, pageable);
    }

    @RequestMapping(value = {"getProspectDetails/{prospectId}"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public ProspectsDTO getProspectDetails(@PathVariable Long prospectId) throws BadRequestException {
        return clientService.findOneProspect(prospectId);
    }

    @RequestMapping(value = {"createClient"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public String createClient(ClientDTO tenDef) throws IllegalAccessException, BadRequestException, ParseException {
        tenDef.setDateregistered(new Date());
        return setupsService.defineClient(tenDef, false);
    }

    @RequestMapping(value = {"deleteCategoryBenefit/{benefitId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryBenefit(@PathVariable Long benefitId, HttpServletRequest request) throws BadRequestException {
        Long quotCode = (Long) request.getSession().getAttribute("quotCode");
        quotationService.deleteCategoryBenefit(benefitId, quotCode);

    }

    @RequestMapping(value = {"deleteCategoryFamDetails/{familyId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryFamDetails(@PathVariable Long familyId, HttpServletRequest request) throws BadRequestException {
        Long quotCode = (Long) request.getSession().getAttribute("quotCode");
        quotationService.deleteCategoryFamDetails(familyId, quotCode);

    }

    @RequestMapping(value = {"sendEmail"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    public ResponseEntity<String> sendEmail(@RequestBody MailMessageBean messageBean, HttpServletRequest request) throws BadRequestException {
        Long quotCode = (Long) request.getSession().getAttribute("quotCode");
        mailer.sendEmailAttachments(messageBean, quotCode, "Q", request);
        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }

    @RequestMapping(value = {"sendSms"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    public ResponseEntity<String> sendSms(@RequestBody MailMessageBean messageBean, HttpServletRequest request) throws BadRequestException {
        Long quotCode = (Long) request.getSession().getAttribute("quotCode");
        mailer.sendSmsAttachmentsANE(messageBean, quotCode, "Q", request);
        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }


    @ResponseBody
    @RequestMapping(value = "mailtemplate")
    public String getMailTemplate(HttpServletResponse response, HttpServletRequest request, @RequestParam(required = false) String templateType) throws BadRequestException {
        return templateService.getMailTemplate(MailTemplates.QUOTE_TEMPLATE, request);
    }

    @ResponseBody
    @RequestMapping(value = "smstemplate")
    public String getSmsTemplate(HttpServletResponse response, HttpServletRequest request) throws BadRequestException {
        return templateService.getMailTemplate(MailTemplates.QUOTE_SMS_TEMPLATE, request);
    }

    @RequestMapping(value = {"getReceiverEmail"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    public ResponseEntity<String> getReceiverEmail(@RequestParam(value = "receiver", required = false) String receiver, HttpServletRequest request) throws BadRequestException {
        Long quotCode = (Long) request.getSession().getAttribute("quotCode");
        String email = mailer.getEmailReceivers(quotCode, "Q", receiver);
        return new ResponseEntity<String>(email, HttpStatus.OK);
    }

    @RequestMapping(value = {"getReceiverSmsNumber"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    public ResponseEntity<String> getReceiverSmsNumber(@RequestParam(value = "receiver", required = false) String receiver, HttpServletRequest request) throws BadRequestException {
        Long quotCode = (Long) request.getSession().getAttribute("quotCode");
        String email = mailer.getSMSReceivers(quotCode, "Q", receiver);
        return new ResponseEntity<String>(email, HttpStatus.OK);
    }

    @RequestMapping(value = {"getInhouseEmail"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    public ResponseEntity<String> getInhouseEmail() throws BadRequestException {
        String email = quotationService.getInhouseEmail();
        return new ResponseEntity<String>(email, HttpStatus.OK);
    }


    @RequestMapping(value = {"getNewTaxes"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    public ResponseEntity<Set<QuotTaxes>> getNewTaxes(@RequestParam(value = "quoteProdId", required = false) Long quoteProdId, HttpServletRequest request) throws BadRequestException {
        Set<QuotTaxes> taxes = quotationService.getNewTaxes(quoteProdId);
        return new ResponseEntity<Set<QuotTaxes>>(taxes, HttpStatus.OK);
    }

    @RequestMapping(value = {"createNewTax"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    public ResponseEntity<String> createNewTax(@RequestBody PolicyTaxBean tax,
                                               HttpServletRequest request) throws BadRequestException {
        Long quotCode = (Long) request.getSession().getAttribute("quotCode");
        quotationService.createQuoteTaxes(tax);
        try {
            quotationService.computeQuotPrem(quotCode);
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }
        return new ResponseEntity<String>("Created", HttpStatus.OK);
    }

    @RequestMapping(value = {"prospectreqdocs"}, method = {RequestMethod.GET})
    @ResponseBody
    public List<RequiredDocs> getUnassignedProspectDocs(@RequestParam(value = "clientCode", required = false) Long clientCode, @RequestParam(value = "docName", required = false) String docName)
            throws IllegalAccessException {
        return clientService.findUnassignedProspectDocs(clientCode, docName);
    }

    @RequestMapping(value = {"uploadProspectDocs"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<String> uploadProspectDocs(UploadBean uploadBean) throws BadRequestException {
        try {
            uploadService.sybrinCreateCase(uploadBean, "Prospect");
            return ResponseEntity.status(HttpStatus.CREATED).body("File Uploaded successfully");
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @RequestMapping(value = {"createProspectDocs"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    public ResponseEntity<String> createProspectDocs(@RequestBody RequiredDocBean requiredDocBean) throws IllegalAccessException, IOException, BadRequestException {
        clientService.createProspectRequiredDocs(requiredDocBean);
        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }


    @RequestMapping(value = {"prospectDocs/{clientId}"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<ClientDocs> getProspectDocs(@DataTable DataTablesRequest pageable, @PathVariable Long clientId)
            throws IllegalAccessException {
        return clientService.findProspectDOcs(pageable, clientId);
    }


    @RequestMapping(value = {"deleteProspectDoc/{adId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProspectDoc(@PathVariable Long adId) throws BadRequestException {
        uploadService.deletePrspctDocument(adId);
    }

    @RequestMapping(value = {"selectQuoteForConvert"}, method = {RequestMethod.GET})
    @ResponseBody
    public List<Object[]> getCompQuotForSelect(HttpServletRequest request) throws IllegalAccessException {
        Long quotId = (Long) request.getSession().getAttribute("quotCode");
        return quotationService.compQuoteConvertSelect(quotId);
    }

    @RequestMapping(value = {"selectQuoteProductForConvert"}, method = {RequestMethod.GET})
    @ResponseBody
    public List<Object[]> getCombQuotProducts(HttpServletRequest request) throws IllegalAccessException {
        Long quotId = (Long) request.getSession().getAttribute("quotCode");
        return quotationService.getCombineQuoteProduct(quotId);
    }
}
