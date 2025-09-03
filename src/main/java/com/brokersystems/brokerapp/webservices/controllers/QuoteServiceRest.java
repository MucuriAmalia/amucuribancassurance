
package com.brokersystems.brokerapp.webservices.controllers;

import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.dto.OrganizationDTO;
import com.brokersystems.brokerapp.setup.model.Organization;
import com.brokersystems.brokerapp.setup.model.SystemSequence;
import com.brokersystems.brokerapp.setup.service.OrganizationService;
import com.brokersystems.brokerapp.webservices.model.*;
import com.brokersystems.brokerapp.webservices.portalmodel.Agent;
import com.brokersystems.brokerapp.webservices.portalmodel.IntProducts;
import com.brokersystems.brokerapp.webservices.portalmodel.PolicyData;
import com.brokersystems.brokerapp.webservices.portalmodel.ResponseMessage;
import com.brokersystems.brokerapp.webservices.service.PortalService;
import com.brokersystems.brokerapp.webservices.service.QuotationWebService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by HP on 3/28/2018.
 */

@Controller
@RequestMapping({ "/services" })
public class QuoteServiceRest {

    @Autowired
    private QuotationWebService quotationWebService;

    @Autowired
    private OrganizationService orgService;

    @Autowired
    private DataSource datasource;

    @Autowired
    private PortalService portalService;

    @RequestMapping(value = { "subagentquotes/{page}/{size}/{key}" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public DataResponse getQuotes(@PathVariable("page") int page, @PathVariable("size") int size, @PathVariable("key") String key)
            throws IllegalAccessException {
        return quotationWebService.findSubAgentQuotes(page,size,key);
    }

    @RequestMapping(value = { "policyenquiry" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public DataResponse getPolicies(@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("pin") String pin)
            throws IllegalAccessException {
        return quotationWebService.findSubAgentPolicies(page,size,pin);
    }


    @RequestMapping(value = { "prembalance" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public BalanceObject getClientBalance(@RequestParam("idNumber") String idNumber, @RequestParam("pin") String pin)
            throws IllegalAccessException {
        return quotationWebService.findClientBalance(idNumber,pin);
    }

    @RequestMapping(value = { "policycount" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public CountObject getPolicyCount(@RequestParam("idNumber") String idNumber, @RequestParam("pin") String pin)
            throws IllegalAccessException {
        return quotationWebService.findClientCountPolicies(idNumber,pin);
    }

    @RequestMapping(value = { "claimCount" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public CountObject getClaimCount(@RequestParam("idNumber") String idNumber, @RequestParam("pin") String pin)
            throws IllegalAccessException {
        return quotationWebService.findClientCountClaims(idNumber,pin);
    }

    @RequestMapping(value = { "policyenquiryemail" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public DataResponse getPoliciesByEmail(@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("email") String email)
            throws IllegalAccessException {
        return quotationWebService.findPoliciesByEmail(page,size,email);
    }

    @RequestMapping(value = { "quotebenefits" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public List<WebBenefitsDTO> getPoliciesByEmail() {
        return quotationWebService.getSwitchBenefits();
    }

    @RequestMapping(value = { "subagentclaims/{page}/{size}/{key}" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public DataResponse getClaims(@PathVariable("page") int page, @PathVariable("size") int size, @PathVariable("key") String key)
            throws IllegalAccessException {
        return quotationWebService.findSubAgentClaims(page,size,key);
    }

    @RequestMapping(value = { "policydetails/{polid}" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public PolicyWebInfo getPolicyDetails(@PathVariable("polid") String polid)
            throws IllegalAccessException {
        return quotationWebService.getPolicyInfo(polid);
    }


    @RequestMapping(value = { "policyrisks/{polid}/{key}" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public DataResponse getPolicyRisks(@PathVariable("polid") String page, @PathVariable("key") String key)
            throws IllegalAccessException {
        return quotationWebService.findPolicyRisks(page,key);
    }

    @RequestMapping(value = "rpt_debit_note/{polid}", method = RequestMethod.GET)
    public ModelAndView invoiceRpt(ModelMap modelMap, @PathVariable("polid") String polid, ModelAndView modelAndView)
            throws BadRequestException, IOException {
        Long polCode = Long.valueOf(polid);
        OrganizationDTO organization = orgService.getOrganizationLogoDetails();
        InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
        BufferedImage image = ImageIO.read(in);
        modelMap.put("logo", image );
        modelMap.put("datasource", datasource);
        modelMap.put("format", "pdf");
        modelMap.put("polId", polCode);
        modelAndView = new ModelAndView("rpt_risk_note", modelMap);
        return modelAndView;
    }

    @RequestMapping(value = { "risknote/{polid}/{key}" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public DataResponse printRiskNote(@PathVariable("polid") String page, @PathVariable("key") String key)
            throws IllegalAccessException {
        return quotationWebService.findPolicyRisks(page,key);
    }

    @RequestMapping(value = { "premworking/{polid}/{key}" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public DataResponse premWorking(@PathVariable("polid") String page, @PathVariable("key") String key)
            throws IllegalAccessException {
        return quotationWebService.findPolicyRisks(page,key);
    }

    @RequestMapping(value = { "quotdetails/{quotid}/{key}" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public QuoteWebInfo getQuotDetails(@PathVariable("quotid") String quotid, @PathVariable("key") String key)
            throws IllegalAccessException {
        return quotationWebService.getQuotInfo(quotid,key);
    }

    @RequestMapping(value = { "quotproducts/{quotid}/{key}" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public DataResponse getQuotProducts(@PathVariable("quotid") String quotid, @PathVariable("key") String key)
            throws IllegalAccessException {
        return quotationWebService.findQuotProducts(quotid,key);
    }


//    @RequestMapping(value = { "validateUrl" }, method = { RequestMethod.POST }, consumes = {MediaType.APPLICATION_XML_VALUE})
//    public ResponseEntity<MpesaResponseCode> validateUrl(@RequestBody String transaction)
//            throws IllegalAccessException {
//        MpesaResponseCode mpesaResponseCode = new MpesaResponseCode();
//        mpesaResponseCode.setResultCode(1);
//        mpesaResponseCode.setResultDesc("failed");
//        mpesaResponseCode.setThirdPartyTransID(0);
//        System.out.println("Validation url.... "+transaction);
//        return new ResponseEntity<MpesaResponseCode>(mpesaResponseCode, HttpStatus.OK);
//    }


//    @RequestMapping(value = { "confirmUrl" }, method = { RequestMethod.POST })
//    public ResponseEntity<String> confirmUrl(@RequestBody C2BPaymentConfirmationRequest transaction)
//            throws IllegalAccessException {
//        return new ResponseEntity<String>("success", HttpStatus.OK);
//    }

    @RequestMapping(value = { "clients/{page}/{size}/{key}/{search}" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public DataResponse getQuotProducts(@PathVariable("page") int page,@PathVariable("size") int size, @PathVariable("key") String key, @PathVariable("search") String search)
            throws IllegalAccessException {
        return quotationWebService.getClientDetails(-2000l,page,size,key,search);
    }

    @RequestMapping(value = { "createPolicy" }, method = {
        org.springframework.web.bind.annotation.RequestMethod.POST })
    public ResponseEntity<ResponseObject> createPolicy(@RequestBody PolicyData policyData) {

        try {
           final ResponseObject ret = portalService.createPolicy(policyData);
            return new ResponseEntity<>(ret, HttpStatus.OK);
        } catch (BadRequestException e) {
            ResponseObject ret = new ResponseObject();
            ret.setSuccess(false);
            ret.setMessage(e.getMessage());
            ret.setRefcode(null);
            return new ResponseEntity<>(ret, HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping(value = { "createClaim" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    public ResponseEntity<ClaimResponseObject> createClaim(@RequestBody CreateClaimModel claimModel) {

        try {
            final ClaimResponseObject ret = portalService.createClaim(claimModel);
            return new ResponseEntity<>(ret, HttpStatus.OK);
        } catch (BadRequestException e) {
            ClaimResponseObject ret = new ClaimResponseObject();
            ret.setSuccess(false);
            ret.setMessage(e.getMessage());
            ret.setRefcode(null);
            return new ResponseEntity<>(ret, HttpStatus.BAD_REQUEST);
        }

    }


    @RequestMapping(value = { "downgradePolicy" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    public ResponseEntity<ResponseObject> downgradePolicy(@RequestBody DowngradePolicyObject policyData) {

        try {
            final ResponseObject ret = portalService.downgradePolicy(policyData);
            return new ResponseEntity<>(ret, HttpStatus.OK);
        } catch (BadRequestException e) {
            ResponseObject ret = new ResponseObject();
            ret.setSuccess(false);
            ret.setMessage(e.getMessage());
            ret.setRefcode(null);
            return new ResponseEntity<>(ret, HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping(value = { "policyDoc/{polId}" }, method = {
            RequestMethod.GET })
    public ResponseEntity<DocResponseObject> policyDocument(@PathVariable Long polId) {

        try {
            final DocResponseObject ret = portalService.getPolicyDocument(polId);
            return new ResponseEntity<>(ret, HttpStatus.OK);
        } catch (BadRequestException e) {
            DocResponseObject ret = new DocResponseObject();
            ret.setSuccess(false);
            ret.setFileurl(null);
            return new ResponseEntity<>(ret, HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping(value = { "createPortalClient" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST },
            consumes = "application/json"
    )
    public ResponseEntity<ResponseMessage> createPortalClient(@RequestBody Agent agent)  {
        ResponseMessage message = new ResponseMessage();
        try {
            String ret = portalService.createClient(agent);
            message.setMessage(ret);
        }
        catch (BadRequestException ex){
            message.setMessage("Error");
        }
        return new ResponseEntity<ResponseMessage>(message, HttpStatus.OK);
    }



//    @RequestMapping(value = { "createIntermediary" }, method = {
//            org.springframework.web.bind.annotation.RequestMethod.POST },
//            consumes = "application/json"
//    )
//    public ResponseEntity<ResponseMessage> createAgent(@RequestBody Agent agent)  {
//        ResponseMessage message = new ResponseMessage();
//       try {
//           String ret = portalService.createAgent(agent);
//           message.setMessage(ret);
//       }
//       catch (Exception ex){
//           ex.printStackTrace();
//           message.setMessage(ex.getMessage());
//
//       }
//        return new ResponseEntity<ResponseMessage>(message, HttpStatus.OK);
//    }

    @RequestMapping(value = { "findProducts" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    public ResponseEntity<IntProducts> findProducts()  {
        IntProducts taxes = portalService.findProducts();
        return new ResponseEntity<IntProducts>(taxes, HttpStatus.OK);
    }


}