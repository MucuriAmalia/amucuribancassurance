package com.brokersystems.brokerapp.integrations.apa;

import com.brokersystems.brokerapp.customscreens.model.ColumnForm;
import com.brokersystems.brokerapp.customscreens.model.TableForm;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.PremiumItemsBean;
import com.brokersystems.brokerapp.setup.model.ClientDef;
import com.brokersystems.brokerapp.setup.model.Currencies;
import com.brokersystems.brokerapp.setup.model.ProductsDef;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.model.RiskTrans;
import com.brokersystems.brokerapp.uw.model.SectionTrans;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import com.brokersystems.brokerapp.uw.service.impl.PolicyTransServiceImpl;
import com.brokersystems.brokerapp.webservices.portalmodel.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class APAIntergrationService {

    @Autowired
    private ParamService paramService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PolicyTransService policyTransService;

    public void postPolicy(String transType, PolicyTrans policyTrans, Iterable<RiskTrans> risks, Map<String, byte[]> reports)  {
        try {
            APAMotorRequest apaMotorRequest = new APAMotorRequest();
            if (transType != null && policyTrans != null && risks != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                final ClientDef clientDef = policyTrans.getClient();
                final ProductsDef product = policyTrans.getProduct();
                final Currencies currencies = policyTrans.getTransCurrency();
                String freq = policyTrans.getFrequency();

                apaMotorRequest.setTransactionCode("String");
                apaMotorRequest.setPolicyCode(0);
                apaMotorRequest.setPolicyNo(policyTrans.getPolNo());
                apaMotorRequest.setBinderPolicyNo(policyTrans.getPolNo());
                apaMotorRequest.setEndorsementNo(policyTrans.getPolRevNo());
                apaMotorRequest.setCoverFromDate(sdf.format(policyTrans.getWefDate()));
                apaMotorRequest.setCoverToDate(sdf.format(policyTrans.getWetDate()));
                apaMotorRequest.setPolicyDate(sdf.format(new Date()));
                apaMotorRequest.setProductShortDescription(product.getProShtDesc());
                apaMotorRequest.setPremiumAmount(policyTrans.getBasicPrem().doubleValue());
                apaMotorRequest.setFap(0);
                apaMotorRequest.setTransactionLevy(0);
                apaMotorRequest.setPhcf(policyTrans.getPhcf().doubleValue());
                apaMotorRequest.setBranchShortDescription("string");
                apaMotorRequest.setAgentShortDescription("string");
                apaMotorRequest.setCurrencyShortDescription(currencies.getCurIsoCode());
                apaMotorRequest.setPaymentFrequency(freq);
                apaMotorRequest.setTransType(policyTrans.getTransType());
                apaMotorRequest.setRiskNoteNumber("string");
                apaMotorRequest.setStampDuty(0);
                apaMotorRequest.setCommissionAmount(0);
                apaMotorRequest.setOtherDuties(0);
                apaMotorRequest.setWithholdingTax(0);
                apaMotorRequest.setTransactionRemarks("string");
                apaMotorRequest.setEffectiveDate("string");
                apaMotorRequest.setNettPremium(0);

                //limits of lability
                List<APAMotorRequest.LimitOfLiability> limitOfLiabilities = new ArrayList<>();
                APAMotorRequest.LimitOfLiability limitOfLiability = new APAMotorRequest.LimitOfLiability();
                limitOfLiability.setValue("string");
                limitOfLiability.setNarration("string");
                limitOfLiabilities.add(limitOfLiability);
                apaMotorRequest.setLimitsOfLiability(limitOfLiabilities);

                //clauses
                List<String> clausesList = new ArrayList<>();
                clausesList.add("string");
                apaMotorRequest.setClauses(clausesList);

                //exclusions
                List<APAMotorRequest.NarratedValue> exclusionsList = new ArrayList<>();
                APAMotorRequest.NarratedValue exclusions = new APAMotorRequest.NarratedValue();
                exclusions.setValue("string");
                exclusions.setNarration("string");
                exclusionsList.add(exclusions);
                apaMotorRequest.setExclusions(exclusionsList);

                //excess
                List<APAMotorRequest.NarratedValue> excessList = new ArrayList<>();
                APAMotorRequest.NarratedValue excess = new APAMotorRequest.NarratedValue();
                excess.setNarration("string");
                excess.setValue("string");
                excessList.add(excess);
                apaMotorRequest.setExcesses(excessList);

                //documents
                //integrate with dms to get the documents
                List<APAMotorRequest.Document> documentList = new ArrayList<>();
                for (Map.Entry<String, byte[]> reportEntry : reports.entrySet()) {
                    String fileName = reportEntry.getKey();// + ".pdf";
                    byte[] fileData = reportEntry.getValue();
                    String mimeType = getMimeTypeFromExtension(fileName);

                    APAMotorRequest.Document document = new APAMotorRequest.Document();
                    document.setDocumentName(fileName);
                    document.setDocument(Base64.getEncoder().encodeToString(fileData));
                    document.setFileType(mimeType);
                    document.setDocumentType("RISK_DOC");
                    documentList.add(document);
                }

                apaMotorRequest.setDocuments(documentList);

                //client info
                APAMotorRequest.ClientInfo clientInfos = new APAMotorRequest.ClientInfo();
                clientInfos.setCode(0);
                clientInfos.setShortDescription("string");
                clientInfos.setName(clientDef.getFname());
                clientInfos.setOtherNames(clientDef.getOtherNames());
                clientInfos.setIdRegistrationNumber("string");
                clientInfos.setDateOfBirth(sdf.format(clientDef.getDob()));
                clientInfos.setPin(clientDef.getPinNo());
                clientInfos.setPhysicalAddress("string");
                clientInfos.setPostalAddress(clientDef.getPostalCodesDef().getPostalName());
                clientInfos.setEmailAddress(clientDef.getEmailAddress());
                clientInfos.setTelephone1(clientDef.getPhoneNo());
                clientInfos.setTelephone2(clientDef.getPhoneNo());
                clientInfos.setFax("string");
                clientInfos.setStatus(clientDef.getStatus());
                clientInfos.setBranchCode(policyTrans.getBranch().getObName());
                clientInfos.setRequestId("string");
                clientInfos.setType(clientDef.getTenantType().getTypeDesc());
                clientInfos.setPassportNo(clientDef.getPassportNo());
                clientInfos.setOccupation(clientDef.getOccupation().getName());
                clientInfos.setSmsTel(clientDef.getPhoneNo());
                clientInfos.setSmsTel2(clientDef.getPhoneNo());
                clientInfos.setClientType(clientDef.getTenantType().getTypeDesc());
                clientInfos.setGender(clientDef.getGender());
                clientInfos.setCountry(clientDef.getCountry().getCouName());
                clientInfos.setTown(clientDef.getTown().getCtName());
                apaMotorRequest.setClientInfo(clientInfos);

                //risk info
                List<APAMotorRequest.RiskInfo> riskInfosList = new ArrayList<>();
                for (RiskTrans riskTrans : risks) {
                    APAMotorRequest.RiskInfo riskInfos = new APAMotorRequest.RiskInfo();
                    List<SectionTrans> sectionTransList = riskTrans.getSectionTrans();

                    riskInfos.setRiskId(riskTrans.getRiskId().toString());
                    riskInfos.setRiskDescription(riskTrans.getRiskShtDesc());
                    riskInfos.setCoverFromDate(sdf.format(riskTrans.getWefDate()));
                    riskInfos.setCoverToDate(sdf.format(riskTrans.getWetDate()));
                    riskInfos.setRiskValue(riskTrans.getSumInsured().doubleValue());
                    riskInfos.setBinderShortDescription("string");
                    riskInfos.setCoverTypeShortDescription("string");
                    riskInfos.setCoverTypeDescription("string");
                    riskInfos.setSubClassShortDescription("string");

                    int randomSixDigit = ThreadLocalRandom.current().nextInt(100000, 1000000);
                    riskInfos.setCertificateNumber(String.valueOf(randomSixDigit)); //if not null provide certfrom and certto date // random 6 digits
                    riskInfos.setCertFromDate(riskTrans.getWefDate().toString()); //certfrom from  risk
                    riskInfos.setCertToDate(riskTrans.getWetDate().toString()); //certto from risk

                    riskInfos.setRiskPremium(riskTrans.getPremium().doubleValue());
                    riskInfos.setRiskIpuCode(1001); //check what is this

                    //sections
                    List<APAMotorRequest.RiskSection> sectionsList = new ArrayList<>();
                    for (SectionTrans sectionTrans : sectionTransList) {
                        APAMotorRequest.RiskSection sections = new APAMotorRequest.RiskSection();
                        sections.setDescription(sectionTrans.getDesc());
                        sections.setLimitAmount(sectionTrans.getAmount().doubleValue());//sectionTrans.getFreeLimit().doubleValue()); //read limit amt
                        sections.setPremRate(sectionTrans.getRate().doubleValue());
                        sections.setShortDescription(sectionTrans.getSection().getShtDesc());
                        sectionsList.add(sections);
                    }
                    riskInfos.setRiskSections(sectionsList);

                    //client info
                    riskInfos.setClientInfo(clientInfos);

                    //additioninfo riskinfo
                    APAMotorRequest.GinMotorPrivateSchLevel1 ginMotorPrivateSchLevel1 = new APAMotorRequest.GinMotorPrivateSchLevel1();
                    ginMotorPrivateSchLevel1.setMpsValue(riskTrans.getSumInsured().toString()); //sum insured

                    Map<String, Object> scheduleResponse = policyTransService.getRiskSchedules(riskTrans.getRiskId());
                    TableForm tableForm = (TableForm) scheduleResponse.get("tableForm");
                    List<ColumnForm> columns = tableForm.getColumnFormList();

                    List<Map<String, Object>> data = (List<Map<String, Object>>) scheduleResponse.get("data");

                    for (Map<String, Object> row : data) {
                        for (ColumnForm column : columns) {
                            String columnName = column.getName();
                            Object value = row.get(columnName);
                            if(columnName.toUpperCase().contains("YEAR OF MANUFACTURE") || columnName.toUpperCase().contains("YOM")) {
                                ginMotorPrivateSchLevel1.setMpsYrManft(value.toString()); //excedd 15 yrs not allow
                            }
                            if(columnName.toUpperCase().contains("LOGBOOK NO")) {
                                ginMotorPrivateSchLevel1.setMpsLogbook(value.toString());
                            }
                            if(columnName.toUpperCase().contains("REG.MARK")) {
                                ginMotorPrivateSchLevel1.setMpsCoverType(value.toString());
                            }
                            if(columnName.toUpperCase().contains("TONNAGE")) {
                                ginMotorPrivateSchLevel1.setMpsTonnage(value.toString());
                            }

                            if(columnName.toUpperCase().contains("CHASIS NUMBER")) {
                                ginMotorPrivateSchLevel1.setMpsChasisNo(value.toString());
                            }
                            if(columnName.toUpperCase().contains("ENGINE NUMBER")) {
                                ginMotorPrivateSchLevel1.setMpsEngineNo(value.toString());
                            }

                            ginMotorPrivateSchLevel1.setMpsColor("string");
                            ginMotorPrivateSchLevel1.setMpsCarryCapacity("0");

                            ginMotorPrivateSchLevel1.setMpsBodyType("SALOON"); //specific offered ?
                            ginMotorPrivateSchLevel1.setMpsRegNo(riskTrans.getRiskShtDesc());

                            ginMotorPrivateSchLevel1.setMpsCoverType(riskTrans.getCovertype().getCovName());
                            if(columnName.toUpperCase().contains("MAKE/MODEL")) {
                                ginMotorPrivateSchLevel1.setMpsMake(value.toString());
                            }

                            System.out.println(columnName + ": " + value);
                        }

                    }

                    APAMotorRequest.RiskAdditionalInfo addInfo = new APAMotorRequest.RiskAdditionalInfo();
                    addInfo.setGinMotorPrivateSch_level1(ginMotorPrivateSchLevel1);
                    List<APAMotorRequest.RiskAdditionalInfo> riskAddInfo = new ArrayList<>();
                    riskAddInfo.add(addInfo);

                    riskInfos.setRiskAdditionalInfo(riskAddInfo);
                    riskInfosList.add(riskInfos);
                }
                apaMotorRequest.setRiskInfo(riskInfosList);

                //call the endpoint responsible for handling the payload
                apaNBCreatePolicy(apaMotorRequest, transType);

            }
        }
        catch (BadRequestException ex){
            log.error("Error Posting APA Transactions...{}", ex.getMessage());
        }
    }


   public void apaNBCreatePolicy(APAMotorRequest apaMotorRequest, String transType) throws BadRequestException {
        System.out.println("CREATE APA NB POLICY PAYLOAD: " + new Gson().toJson(apaMotorRequest));

       // Validate transaction type
       TransactionType transactionType;
       try {
           transactionType = TransactionType.valueOf(transType);
       } catch (IllegalArgumentException e) {
           log.error("Invalid transaction type: {}", transType);
           throw new BadRequestException("Invalid transaction type: " + transType);
       }

        try {
            // Determine API endpoint based on transaction type
            String endpoint = getEndpointForTransactionType(transactionType);
            String baseUrl = paramService.getParameterString("API_INTEGRATION_URL");
            String fullUrl = baseUrl + endpoint;
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<?> requestEntity = new HttpEntity<>(apaMotorRequest, headers);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(fullUrl, requestEntity, String.class);
            JsonNode rootNode = objectMapper.readTree(responseEntity.getBody());

            log.info("Received UP creation response: {}", responseEntity.getBody());
            // Handle response based on 'Code'
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // Handle API errors related to client/server response issues
            throw new BadRequestException("Integration service has issues: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }  catch (JsonProcessingException e) {
            throw new BadRequestException(e.getMessage());
        }
         catch (RestClientException ex) {
            throw new BadRequestException("Integration service has issues..."+ex.getMessage());
        }
    }

    /**
     * Determines the API endpoint based on the transaction type.
     *
     * @param transactionType The transaction type enum.
     * @return The corresponding API endpoint path.
     * @throws BadRequestException If the transaction type is not supported.
     */
    private String getEndpointForTransactionType(TransactionType transactionType) throws BadRequestException {
        switch (transactionType) {
            case NB:
                return "/apa/api/v1/motor/private/new";
            case RN:
                return "/apa/api/v1/motor/private/renew";
            case EN:
                return "/apa/api/v1/motor/private/endorsement";
            case CN:
                return "/apa/api/v1/motor/private/cancel";
            case CO:
                return "/apa/api/v1/motor/private/reverse";
            default:
                log.error("Unsupported transaction type: {}", transactionType);
                throw new BadRequestException("Unsupported transaction type: " + transactionType);
        }
    }
    public static String getMimeTypeFromExtension(String fileName) {
        if (fileName == null || !fileName.contains(".") || fileName.lastIndexOf('.') == fileName.length() - 1) {
            return "application/octet-stream"; // No extension or invalid format
        }

        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

        switch (extension) {
            case "pdf":
                return "application/pdf";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls":
                return "application/vnd.ms-excel";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "txt":
                return "text/plain";
            case "csv":
                return "text/csv";
            case "zip":
                return "application/zip";
            default:
                return "application/octet-stream"; // fallback for unknown types
        }
    }



}
