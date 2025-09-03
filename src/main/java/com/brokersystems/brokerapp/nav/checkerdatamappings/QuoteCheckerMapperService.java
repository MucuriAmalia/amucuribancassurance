package com.brokersystems.brokerapp.nav.checkerdatamappings;

import com.brokersystems.brokerapp.quotes.dto.CreateQuoteDTO;
import com.brokersystems.brokerapp.quotes.dto.QuoteDetailsDTO;
import com.brokersystems.brokerapp.quotes.model.QuotRiskLimits;
import com.brokersystems.brokerapp.quotes.model.QuoteProTrans;
import com.brokersystems.brokerapp.quotes.model.QuoteProductBean;
import com.brokersystems.brokerapp.quotes.model.QuoteRiskTrans;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.*;
import com.brokersystems.brokerapp.uw.model.RiskSectionBean;
import com.brokersystems.brokerapp.uw.model.RiskTransBean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;


@Service
public class QuoteCheckerMapperService {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private PaymentModeRepo paymentModeRepo;
    @Autowired
    private OrgBranchRepository orgBranchRepository;
    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private BusinessSourcesRepo businessSourcesRepo;
    @Autowired
    private BindersRepo bindersRepo;
    @Autowired
    private BinderDetRepo binderDetRepo;
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private ProductsRepo productsRepo;
    @Autowired
    private SubClassRepo subclassRepo;
    @Autowired
    private CoverTypesRepo coverTypesRepo;
    @Autowired
    private ProspectsRepo prospectsRepo;
    @Autowired
    private SectionRepo sectionRepo;


    public QuoteDetailsDTO mapQuoteData(JSONObject jsonObject) {
        QuoteDetailsDTO quoteDetailsDTO = new QuoteDetailsDTO();

        final Date startDate = new Date();
        final Date endDate = DateUtils.addDays(DateUtils.addMonths(startDate,12),-1);

        //  Extracting data from the JSON object and setting it on the DTO
        if (jsonObject.has("clientType")) {
            quoteDetailsDTO.setClientType(jsonObject.optString("clientType"));
        }
        if (jsonObject.has("clientId")) {
            ClientDef clientDef = clientRepository.findOne((long) jsonObject.optInt("clientId"));
            quoteDetailsDTO.setTenId(clientDef.getTenId());
            quoteDetailsDTO.setFname(clientDef.getFname());
            quoteDetailsDTO.setOtherNames(clientDef.getOtherNames());
        }
        if (jsonObject.has("paymentId")) {
            PaymentModes paymentModes = paymentModeRepo.findOne((long) jsonObject.optInt("paymentId"));
            quoteDetailsDTO.setPmId(paymentModes.getPmId());
            quoteDetailsDTO.setPaymentMode(paymentModes.getPmDesc());
        }
        if (jsonObject.has("branchId")) {
            OrgBranch orgBranch = orgBranchRepository.findOne((long) jsonObject.optInt("branchId"));
            quoteDetailsDTO.setBranch(orgBranch.getObName());
        }
        if (jsonObject.has("currencyId")) {
            Currencies currencies = currencyRepository.findOne((long) jsonObject.optInt("currencyId"));
            quoteDetailsDTO.setCurCode(currencies.getCurCode());
            quoteDetailsDTO.setCurrency(currencies.getCurName());
        }
        if (jsonObject.has("sourceId")) {
            BusinessSources businessSources = businessSourcesRepo.findOne((long) jsonObject.optInt("sourceId"));
            quoteDetailsDTO.setSourceId(businessSources.getSrcId());
            quoteDetailsDTO.setSourceName(businessSources.getDesc());
            quoteDetailsDTO.setSourceGroupId(businessSources.getBusinessSourceGroup().getSrcGroupId());
            quoteDetailsDTO.setSourceGroupName(businessSources.getBusinessSourceGroup().getDesc());
        }
        quoteDetailsDTO.setQuoteType(jsonObject.optString("quoteType"));
        QuoteProTrans quoteProTrans = new QuoteProTrans();
        QuoteRiskTrans risk = new QuoteRiskTrans();
        if (jsonObject.has("riskBean")) {
            JSONObject riskBean = jsonObject.getJSONObject("riskBean");
            System.out.println(riskBean);
            risk.setBinder(bindersRepo.findOne((long) riskBean.optInt("bindCode")));
            risk.setBinderDetails(binderDetRepo.findOne((long) riskBean.optInt("binderDet")));
            risk.setCovertype(coverTypesRepo.findOne((long) riskBean.optInt("coverCode")));
            risk.setSubclass(subclassRepo.findOne((long) riskBean.optInt("sclCode")));
            if (quoteDetailsDTO.getClientType() == null || "C".equalsIgnoreCase(quoteDetailsDTO.getClientType())){
                risk.setInsured(clientRepository.findOne((long) riskBean.optInt("insuredCode")));
                String insuredClient = risk.getInsured().getFname() + " " + risk.getInsured().getOtherNames();
                quoteDetailsDTO.setInsured(insuredClient);
                System.out.println(insuredClient);
            }
            else{
                risk.setProspect(prospectsRepo.findOne((long) riskBean.optInt("insuredCode")));
                String insuredClient = risk.getInsured().getFname() + " " + risk.getInsured().getOtherNames();
                quoteDetailsDTO.setInsured(insuredClient);
            }
            quoteDetailsDTO.setCommAmt(BigDecimal.valueOf( riskBean.optInt("commRate")));
            quoteDetailsDTO.setRiskDesc(riskBean.optString("riskDesc"));
            quoteDetailsDTO.setRiskShtDesc(riskBean.optString("riskShtDesc"));
            quoteDetailsDTO.setQuoteWef(startDate);
            quoteDetailsDTO.setQuoteWet(endDate);
//            quoteDetailsDTO.setQuoteRiskTrans(risk);
            quoteDetailsDTO.setContract(risk.getBinder().getBinName());
            quoteDetailsDTO.setInsuranceCompany(risk.getBinder().getAccount().getName());
            quoteDetailsDTO.setProduct(risk.getBinder().getProduct().getProDesc());
            quoteDetailsDTO.setClassification(risk.getSubclass().getSubDesc());
            quoteDetailsDTO.setCovType(risk.getCovertype().getCovName());
            System.out.println("from riskbean>>>>>>"+risk);




        }
        if (jsonObject.has("quoteProductBean")) {
            JSONObject productBean = jsonObject.getJSONObject("quoteProductBean");
            System.out.println(productBean);
            quoteProTrans.setAgent(accountRepo.findOne((long) productBean.optInt("agentId")));
            quoteProTrans.setBinder(bindersRepo.findOne((long) productBean.optInt("bindCode")));
            quoteProTrans.setProduct(productsRepo.findOne((long) productBean.optInt("prodId")));
            risk.setProductTrans(quoteProTrans);
//            if(quoteDetailsDTO.getQuoteRiskTrans() == null){
//                quoteDetailsDTO.setQuoteRiskTrans(risk);
//                System.out.println("from productbean>>>>>>"+risk);
//            }

        }

        risk.setProductTrans(quoteProTrans);
        JSONArray sectionsArray = jsonObject.getJSONArray("sections");
        if (sectionsArray.length() > 0) {
            JSONObject firstSection = sectionsArray.getJSONObject(0);
            quoteDetailsDTO.setSumInsured(BigDecimal.valueOf(firstSection.optInt("amount")));
        }

        List<QuotRiskLimits> sectionsTrans =  new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(String.valueOf(jsonObject));

            JsonNode sectionsNode = rootNode.get("sections");
            if (sectionsNode.isArray()) {
                for (JsonNode sectionNode : sectionsNode) {
                    // Create a new QuotRiskLimits object
                    QuotRiskLimits section = new QuotRiskLimits();

                    // Set values from the JSON object
                    int sectionId = sectionNode.get("section").asInt();
                    SectionsDef sectionDef = sectionRepo.findOne((long) sectionId);
                    section.setSection(sectionDef);

                    BigDecimal amount = sectionNode.has("amount") ? sectionNode.get("amount").decimalValue() : BigDecimal.ZERO;
                    section.setAmount(amount);

                    BigDecimal rate = sectionNode.get("rate").decimalValue();
                    section.setRate(rate);

                    int divFactor = sectionNode.get("divFactor").asInt();
                    section.setDivFactor(BigDecimal.valueOf(divFactor));

                    BigDecimal freeLimit = sectionNode.get("freeLimit").decimalValue();
                    section.setFreeLimit(freeLimit);

                    boolean compute = sectionNode.get("compute").asBoolean();
                    section.setCompute(compute);

                    // Add to sectionTransactions list
                    sectionsTrans.add(section);
                }
                quoteDetailsDTO.setQuotRiskLimitsList(sectionsTrans);
            }
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        // Set other fields as needed from the JSON object

        return quoteDetailsDTO;
        }

        private RiskTransBean mapRiskBean(JSONObject riskBean) {
            RiskTransBean riskTransBean = new RiskTransBean();

            if (riskBean.has("bindCode")) {
                riskTransBean.setBindCode(riskBean.getLong("bindCode"));
            }
            if (riskBean.has("sclCode")) {
                riskTransBean.setSclCode(riskBean.getLong("sclCode"));
            }
            System.out.println(riskTransBean);
            return riskTransBean;
        }

        private QuoteProductBean mapProductBean(JSONObject productBean) {
            QuoteProductBean quoteProductBean = new QuoteProductBean();
            // Map individual fields of productBean to the QuoteProductBean
            if (productBean.has("prodId")) {
                quoteProductBean.setProdId(productBean.getLong("prodId"));
            }
            if (productBean.has("agentId")) {
                quoteProductBean.setAgentId(productBean.getLong("agentId"));
            }
            // Continue mapping other fields
            System.out.println(quoteProductBean);
            return quoteProductBean;
        }
    }

