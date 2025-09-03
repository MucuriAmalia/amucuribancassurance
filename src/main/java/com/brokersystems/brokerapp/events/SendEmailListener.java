package com.brokersystems.brokerapp.events;

import com.brokersystems.brokerapp.claims.model.ClaimBookings;
import com.brokersystems.brokerapp.claims.model.QClaimBookings;
import com.brokersystems.brokerapp.claims.repository.ClaimsBookingRepo;
import com.brokersystems.brokerapp.dms.model.IntegrationUw;
import com.brokersystems.brokerapp.dms.model.QIntegrationUw;
import com.brokersystems.brokerapp.dms.repo.IntegrationUwRepo;
import com.brokersystems.brokerapp.dms.service.UploadService;
import com.brokersystems.brokerapp.integrations.apa.APAIntergrationService;
import com.brokersystems.brokerapp.mail.dto.*;
import com.brokersystems.brokerapp.mail.events.SendEmailEvents;
import com.brokersystems.brokerapp.mail.model.MailMessageBean;
import com.brokersystems.brokerapp.medical.model.MedicalParTrans;
import com.brokersystems.brokerapp.medical.model.QMedicalParTrans;
import com.brokersystems.brokerapp.medical.repository.MedicalParRepo;
import com.brokersystems.brokerapp.quotes.model.QQuoteTrans;
import com.brokersystems.brokerapp.quotes.model.QuoteTrans;
import com.brokersystems.brokerapp.quotes.repository.QuotTransRepo;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.dto.OrganizationDTO;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.ClientDocsRepo;
import com.brokersystems.brokerapp.setup.repository.ClientRepository;
import com.brokersystems.brokerapp.setup.repository.RequiredDocsRepo;
import com.brokersystems.brokerapp.setup.repository.UserRepository;
import com.brokersystems.brokerapp.setup.service.OrganizationService;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.brokersystems.brokerapp.trans.model.QReceiptTrans;
import com.brokersystems.brokerapp.trans.model.QReceiptTransDtls;
import com.brokersystems.brokerapp.trans.model.ReceiptTrans;
import com.brokersystems.brokerapp.trans.model.ReceiptTransDtls;
import com.brokersystems.brokerapp.trans.repository.ReceiptDetailsRepository;
import com.brokersystems.brokerapp.trans.repository.ReceiptRepository;
import com.brokersystems.brokerapp.uw.dtos.alak.UploadAlakDocumentRequest;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import com.brokersystems.brokerapp.uw.repository.RiskDocsRepo;
import com.brokersystems.brokerapp.uw.repository.RiskTransRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sun.mail.util.MailSSLSocketFactory;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.core.io.InputStreamResource;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.sql.DataSource;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

@Component
@Async
public class SendEmailListener implements ApplicationListener<SendEmailEvents> {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private DataSource datasource;

    @Autowired
    private PolicyTransRepo policyTransRepo;

    @Autowired
    Environment env;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ParamService paramService;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private QuotTransRepo quotTransRepo;
    @Autowired
    private MedicalParRepo medicalParRepo;
    @Autowired
    private ClaimsBookingRepo claimsBookingRepo;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UploadService uploadService;
    @Autowired
    private ClientDocsRepo clientDocsRepo;
    @Autowired
    private RequiredDocsRepo requiredDocsRepo;
    @Autowired
    private ReceiptDetailsRepository receiptDetailsRepository;
    @Autowired
    private ReceiptRepository receiptRepository;
    @Autowired
    private RiskTransRepo riskRepo;

    @Autowired
    private RiskDocsRepo riskDocsRepo;
    @Autowired
    private IntegrationUwRepo integrationUwRepo;
    @Autowired
    private APAIntergrationService apaIntergrationService;


    public void onApplicationEvent1(SendEmailEvents events) {
        MailMessageBean sendEmailEvent = events.getMailMessageBean();
        MimeMessagePreparator mimeMessagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,true);
            messageHelper.setTo(StringUtils.split(sendEmailEvent.getSendTo(), ","));
            if (sendEmailEvent.getSendBcc()!=null)
                messageHelper.setBcc(StringUtils.split(sendEmailEvent.getSendBcc(),","));
            if (sendEmailEvent.getSendCC()!=null)
                messageHelper.setCc(StringUtils.split(sendEmailEvent.getSendCC(),","));
            messageHelper.setFrom(env.getProperty("sender.username"),organizationService.getOrganizationDetails().getOrgName());
            messageHelper.setSubject(sendEmailEvent.getSubject());
            messageHelper.setText(sendEmailEvent.getMessage(), true);
            final String transType = sendEmailEvent.getTransType();
            final Long transCode = sendEmailEvent.getTransCode();
            if( sendEmailEvent.getReports()!=null &&  !sendEmailEvent.getReports().isEmpty()) {
                sendEmailEvent.getReports().stream().forEach(a -> {
                    byte[] f = new byte[0];
                    if ("P".equalsIgnoreCase(transType)) {
                        try {
                            f = generateUWReport(transCode, a);
                        } catch (BadRequestException e) {
                            throw new RuntimeException(e);
                        }
                    } else if ("Q".equalsIgnoreCase(transType)) {
                        f = generateQuoteReport(transCode, a);
                    } else if ("M".equalsIgnoreCase(transType)) {
                        f = generateMedClaimsReport(transCode, a);
                    } else if ("C".equalsIgnoreCase(transType)) {
                        f = generateClaimsReport(transCode, a);
                    }
                    if (f.length > 0) {
                        try {
                            messageHelper.addAttachment(a + ".pdf", new ByteArrayResource(f));
                        } catch (MessagingException e) {
                            System.err.println(e.getMessage());
                        }
                    }
                });
            }
        };

        mailSender.send(mimeMessagePreparator);
    }



    @Override
    @Transactional
    public void onApplicationEvent(SendEmailEvents events) {
        MailMessageBean sendEmailEvent = events.getMailMessageBean();

        String paramValue;
        try {
            paramValue = paramService.getParameterString("API_INTEGRATION_URL") +  "/ane/sendEmail";
        } catch (BadRequestException e) {
            throw new RuntimeException(e);
        }

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");
        headers.setAll(map);

        CorrespondenceAddressee addressee = new CorrespondenceAddressee();
        addressee.setEmailContactType("7");
        addressee.setEmailContactValue(sendEmailEvent.getSendTo());


        CorrespondenceMedia media = new CorrespondenceMedia();
        media.setDeliveryMode("2");

        CorrespondenceTemplateReference templateReference = new CorrespondenceTemplateReference();
        templateReference.setEmailDestinationType("EMAIL_INTERNAL");
        templateReference.setEmailEventId("BANCA001");

        ProductAndServiceType productAndServiceType = new ProductAndServiceType();
        productAndServiceType.setEventIdentifier("");

        List<DynamicFields> dynamicFields = new ArrayList<>();

        DynamicFields dynamicFields1 = new DynamicFields();
        dynamicFields1.setName("SUBJECT");
        dynamicFields1.setType("STRING");
        dynamicFields1.setValue("[SECURE]"+sendEmailEvent.getSubject());
        dynamicFields.add(dynamicFields1);


        DynamicFields dynamicFields2 = new DynamicFields();
        dynamicFields2.setName("BODY");
        dynamicFields2.setType("CDATA");
        dynamicFields2.setValue(sendEmailEvent.getMessage()); //.replaceAll("<[^>]*>", "").replaceAll("\\s+", " ").trim());
        dynamicFields.add(dynamicFields2);


        DynamicFields dynamicFields3 = new DynamicFields();
        if (sendEmailEvent.getSendCC()!=null) {
            dynamicFields3.setName("EMAILID_CC");
            dynamicFields3.setType("STRING");
            dynamicFields3.setValue(sendEmailEvent.getSendCC());
            dynamicFields.add(dynamicFields3);

        }

        DynamicFields dynamicFields4 = new DynamicFields();
        if (sendEmailEvent.getSendBcc()!=null) {
            dynamicFields4.setName("EMAILID_BCC");
            dynamicFields4.setType("STRING");
            dynamicFields4.setValue(sendEmailEvent.getSendBcc());
            dynamicFields.add(dynamicFields4);
        }



        List<EmailAttachments> attachments = new ArrayList<>();
        EventDetailList eventDetail = new EventDetailList();
        ClientDef clientDef = null;
        final String transType = sendEmailEvent.getTransType();
        final Long transCode = sendEmailEvent.getTransCode();
        if ("P".equalsIgnoreCase(transType)) {
            clientDef = policyTransRepo.findOne(QPolicyTrans.policyTrans.policyId.eq(transCode)).getClient();
        } else if ("Q".equalsIgnoreCase(transType)) {
            clientDef = quotTransRepo.findOne(QQuoteTrans.quoteTrans.quoteId.eq(transCode)).getClient();
        } else if ("M".equalsIgnoreCase(transType)) {
            clientDef = medicalParRepo.findOne(QMedicalParTrans.medicalParTrans.parId.eq(transCode)).getClientDef();
//        } else if ("C".equalsIgnoreCase(transType)) {
//            clientDef = claimsBookingRepo.findOne(QClaimBookings.claimBookings.clmId.eq(transCode)).getRisk().getInsured();
//        }
        } else if ("C".equalsIgnoreCase(transType)) {
            ClaimBookings claimBooking = claimsBookingRepo.findOne(QClaimBookings.claimBookings.clmId.eq(transCode));
            // Force initialization while session is still open
            Hibernate.initialize(claimBooking.getRisk());
            clientDef = claimBooking.getRisk().getInsured();
            Hibernate.initialize(clientDef);
        }
        Map<String, byte[]> reportsMap = new HashMap<>();
        if( sendEmailEvent.getReports()!=null &&  !sendEmailEvent.getReports().isEmpty()) {
            //risk documents get
            List<Map<String, String>> base64s = generateUWRiskDocs(transCode, transType);
            int riskDocCount = 1;
            for (Map<String, String> b64 : base64s) {
                String base64 = b64.get("base64");
                String contentType = b64.get("contentType");
                String fileName = b64.get("fileName");

                EmailAttachments emailRiskAttachments = new EmailAttachments();
                emailRiskAttachments.setName(riskDocCount+"_"+fileName);
                emailRiskAttachments.setType("String-Base64");
                // emailAttachments.setData(Base64.getEncoder().encodeToString(f).replaceAll("\\\\.*", "==").replace("+","%2B"));
                emailRiskAttachments.setData(base64);//Base64.getEncoder().encodeToString(f));
                attachments.add(emailRiskAttachments);
                //store to upload later
                //reportsMap.put(a, f);
                byte[] f = Base64.getDecoder().decode(base64);
                reportsMap.put(riskDocCount+"_"+fileName, f); // If you're generating a PDF
                // reportsMap.put(a + ".jpg", f); // or
                //reportsMap.put(a + ".jpeg", f);
                riskDocCount++;
            }

            //uw reports generate
            sendEmailEvent.getReports().forEach(a -> {
                byte[] f = new byte[0];
                if ("P".equalsIgnoreCase(transType)) {
                    try {
                        f = generateUWReport(transCode, a);
                    } catch (BadRequestException e) {
                        //throw new RuntimeException(e.getMessage());
                        System.err.println("Error generating report for " + a + ": " + e.getMessage());
                        return; // skip failed report only, in case one fails the others are not shown
                    }
                } else if ("Q".equalsIgnoreCase(transType)) {
                    f = generateQuoteReport(transCode, a);
                } else if ("M".equalsIgnoreCase(transType)) {
                    f = generateMedClaimsReport(transCode, a);
                } else if ("C".equalsIgnoreCase(transType)) {
                    f = generateClaimsReport(transCode, a);
                }
                if (f != null && f.length > 0) {
                    EmailAttachments emailAttachments = new EmailAttachments();
                    emailAttachments.setName(a + ".pdf");
                    emailAttachments.setType("String-Base64");
                    // emailAttachments.setData(Base64.getEncoder().encodeToString(f).replaceAll("\\\\.*", "==").replace("+","%2B"));
                    emailAttachments.setData(Base64.getEncoder().encodeToString(f));
                    attachments.add(emailAttachments);
                    //store to upload later
                    //reportsMap.put(a, f);
                    reportsMap.put(a + ".pdf", f); // If you're generating a PDF
                    // reportsMap.put(a + ".jpg", f); // or
                    //reportsMap.put(a + ".jpeg", f);



                }
            });
            eventDetail.setAttachments(attachments);
        }
        String idNo = "";
        String clientName = "";
        if (clientDef != null) {
            System.out.println("CLIENT: " + clientDef.toString());
           clientName = clientDef.getFname().concat(" ").concat(clientDef.getOtherNames());
           idNo = clientDef.getIdNo();
        } else {
            //User user = userRepository.findOne(QUser.user.email.equalsIgnoreCase(sendEmailEvent.getSendTo()));
            User user = userRepository.findUserByEmail(sendEmailEvent.getSendTo());
            if (user != null) {
                idNo = String.valueOf(user.getId());
                clientName = user.getName();
            } else {
                idNo = "12345678";
                clientName = sendEmailEvent.getSendTo();
            }
        }

        CorrespondenceContent content = new CorrespondenceContent();
        content.setCustomerId(idNo);

        DynamicFields dynamicFields5 = new DynamicFields();
        dynamicFields5.setName("CUSTNAME");
        dynamicFields5.setType("STRING");
        dynamicFields5.setValue(clientName);
        dynamicFields.add(dynamicFields5);

        eventDetail.setCorrespondenceAddressee(addressee);
        eventDetail.setCorrespondenceContent(content);
        eventDetail.setCorrespondenceMedia(media);
        eventDetail.setCorrespondenceTemplateReference(templateReference);
        eventDetail.setProductAndServiceType(productAndServiceType);
        eventDetail.setDynamicFields(dynamicFields);

        List<EventDetailList> eventDetailList = new ArrayList<>();
        eventDetailList.add(eventDetail);

        EventDetails details = new EventDetails();
        details.setEventDetailList(eventDetailList);

        //System.out.println("Event Details: " + new Gson().toJson(details));

        HttpEntity<?> requestEntity = new HttpEntity<>(details,headers);
        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(paramValue, requestEntity, String.class);
            String responseBody = responseEntity.getBody();
            //System.out.println("Response body: " + responseBody);
            JsonNode rootNode = objectMapper.readTree(responseBody);
            if (rootNode != null) {
                if (rootNode.get("status").asText().equalsIgnoreCase("Success")) {
                    JsonNode dataNode = rootNode.get("data");
                    JsonNode sourceInfo = rootNode.get("sourceInfo");
                    if (dataNode.get("responseCode").asText().equalsIgnoreCase("0") && dataNode.get("responseDesc").asText().equalsIgnoreCase("success")) {
                        if (!sourceInfo.get("code").asText().equalsIgnoreCase("0")) {
                            throw new RuntimeException(rootNode.get("message").asText());
                        }
                    }
                }
            } else{
                System.out.println("no email response received"+responseBody.toString());
            }
        } catch (JsonProcessingException e) {
            //throw new RuntimeException(e);
            System.err.println("Email sending failed: " + e.getMessage());
        }
        //done sending email then send the documents
        //check if alak integration documents
        if (!reportsMap.isEmpty()){
             uploadAlakDocuments(transCode, transType, reportsMap);
        }
    }

    private byte[] generateMedClaimsReport(Long parCode, String type)  {
        String databaseType = env.getProperty("database_type");
        String resourcePath = "/reports/";
        String fileName = "";
        if(StringUtils.equalsIgnoreCase("oracle",databaseType)){
            resourcePath = "/oracle_reports/";
        }
        else if(StringUtils.equalsIgnoreCase("mssql",databaseType)){
            resourcePath = "/mssql/";
        }
        else if(StringUtils.equalsIgnoreCase("postgres",databaseType))
            resourcePath = "/reports/";
        switch (type){
            case "IL":
                resourcePath = resourcePath+"rpt_inp_undertaking_letter.jasper";
                fileName = "inp_undertaking_letter.pdf";
                break;
            case "OL":
                resourcePath = resourcePath+"rpt_outp_undertaking_letter.jasper";
                fileName = "outp_undertaking_letter.pdf";
                break;
            case "DL":
                resourcePath = resourcePath+"rpt_med_clm_rejection_letter.jasper";
                fileName = "clm_rejection_letter.pdf";
                break;
            default:
        }
        InputStream jasperStream = this.getClass().getResourceAsStream(resourcePath);
        Map<String, Object> params = new HashMap<>();
        OrganizationDTO organization = organizationService.getOrganizationLogoDetails();

        File file = null;
        Connection conn = null;
        try {
            conn = datasource.getConnection();
            InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
            BufferedImage image = ImageIO.read(in);
            params.put("logo", image );
            params.put("parId", parCode);
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, conn);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException | IOException | SQLException e) {
            System.err.println("Error Generating report..."+e.getMessage());
        }
        finally {
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    //Error closing conn....
                }
            }
        }
        return new byte[0];
    }

    private byte[] generateClaimsReport(Long clmId, String type)  {
        String databaseType = env.getProperty("database_type");
        String resourcePath = "/reports/";
        String fileName = "";
        if(StringUtils.equalsIgnoreCase("oracle",databaseType)){
            resourcePath = "/oracle_reports/";
        }
        else if(StringUtils.equalsIgnoreCase("mssql",databaseType)){
            resourcePath = "/mssql/";
        }
        else if(StringUtils.equalsIgnoreCase("postgres",databaseType))
            resourcePath = "/reports/";
        switch (type){
            case "SY":
                resourcePath = resourcePath+"rpt_claims_synopsis.jasper";
                fileName = "rpt_claims_synopsis.pdf";
                break;
            default:
        }
        InputStream jasperStream = this.getClass().getResourceAsStream(resourcePath);
        Map<String, Object> params = new HashMap<>();
        OrganizationDTO organization = organizationService.getOrganizationLogoDetails();

        File file = null;
        Connection conn = null;
        try {
            conn = datasource.getConnection();
            InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
            BufferedImage image = ImageIO.read(in);
            params.put("logo", image );
            params.put("clmId", clmId);
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, conn);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException | IOException | SQLException e) {
            System.err.println("Error Generating report..."+e.getMessage());
        }
        finally {
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    //Error closing conn....
                }
            }
        }
        return new byte[0];
    }


    private byte[] generateQuoteReport(Long quoteCode, String type)  {
        String databaseType = env.getProperty("database_type");
        String resourcePath = "/reports/";
        String fileName = "";
        if(StringUtils.equalsIgnoreCase("oracle",databaseType)){
            resourcePath = "/oracle_reports/";
        }
        else if(StringUtils.equalsIgnoreCase("mssql",databaseType)){
            resourcePath = "/mssql/";
        }
        else if(StringUtils.equalsIgnoreCase("postgres",databaseType))
            resourcePath = "/reports/";
        System.out.println("resourcePath  =="+resourcePath+" ; db =="+ databaseType);
        switch (type){
            case "CQ":
                resourcePath = resourcePath+"rpt_client_quote.jasper";
                fileName = "client_quote.pdf";
                break;
            case "QI":
                resourcePath = resourcePath+"rpt_quote_info.jasper";
                fileName = "quote_info.pdf";
                break;
            default:
        }
        InputStream jasperStream = this.getClass().getResourceAsStream(resourcePath);
        Map<String, Object> params = new HashMap<>();
        OrganizationDTO organization = organizationService.getOrganizationLogoDetails();

        File file = null;
        Connection conn = null;
        try {
            conn = datasource.getConnection();
            InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
            BufferedImage image = ImageIO.read(in);
            params.put("logo", image );
            params.put("quotId", quoteCode);
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, conn);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException | IOException | SQLException e) {
            System.err.println("Error Generating report..."+e.getMessage());
        }
        finally {
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    //Error closing conn....
                }
            }
        }
        return new byte[0];
    }

    private List<Map<String, String>> generateUWRiskDocs(Long polCode, String type) {
        System.out.println("checking  risk docs for polcode"+polCode);
        List<Map<String, String>> base64Documents = new ArrayList<>();

        // Get all risk documents inline
        List<Object[]> risks = riskRepo.findGeneralAndLifePolicyRisks(polCode);
        System.out.println("risks size"+risks.size());
        for (Object[] risk : risks) {
            Long riskId = ((BigInteger) risk[0]).longValue();
            System.out.println("risk id is "+riskId);
            List<Object[]> riskDocs = riskDocsRepo.getAllRiskDocsAttachment(riskId); //.getAllRiskDocs(riskId, 0, 100000000);
            System.out.println("getting risk docs size"+riskDocs.size());
            if (!riskDocs.isEmpty()) {
                for (Object[] doc : riskDocs) {
                        Long docId = (((BigInteger) doc[1]).longValue());
                        RiskDocs riskDoc = riskDocsRepo.getRiskDocsVal(docId);
                        if (riskDoc != null) {
                            Long reqID = riskDoc.getReqdDocs().getRequiredDoc().getReqId();
                            Long riskID = riskDoc.getRisk().getRiskId();
                            System.out.println("Retrieved RiskDoc: fileName=" + riskDoc.getUploadedFileName()
                                    + ", url=" + riskDoc.getUrl() + ", docId=" + docId
                                    + ", reqID=" + reqID + ", riskID=" + riskID);
                            try {
                                byte[] fileBytes = uploadService.sybrinDocumentDetails("Risk", reqID, riskID);
                                System.out.println("file bytes are "+fileBytes.toString());
                                if (fileBytes.length > 0) {
//                                    String base64 = Base64.getEncoder().encodeToString(fileBytes);
//                                    String contentType = uploadService.getDocContentType(docId);
//                                    String fileName = riskDoc.getUploadedFileName();
//                                    base64Documents.add(base64);

                                    String base64 = Base64.getEncoder().encodeToString(fileBytes);
                                    String contentType = uploadService.getDocContentType(docId);
                                    String fileName = riskDoc.getUploadedFileName();

                                    Map<String, String> docMap = new HashMap<>();
                                    docMap.put("base64", base64);
                                    docMap.put("contentType", contentType);
                                    docMap.put("fileName", fileName);
                                    base64Documents.add(docMap);
                                }
                            } catch (Exception e) {
                                System.err.println("Failed to retrieve risk document reqID: " + reqID +
                                        ", riskID: " + riskID + " - " + e.getMessage());
                            }
                        }
                }
            }
        }
        System.out.println("Total base64 documents returned: " + base64Documents.size());
        return base64Documents;
    }

    private byte[] generateUWReport(Long polCode, String type) throws BadRequestException {
        String databaseType = env.getProperty("database_type");
        String resourcePath = "/reports/";
        String fileName = "";
        PolicyTrans policyTrans = policyTransRepo.findOne(polCode);
        String riskNote = policyTrans.getProduct().getRiskNote();
        Long clientId = policyTrans.getClient().getTenId();
        Iterable<ClientDocs> clientDocsList = clientDocsRepo.findAll(QClientDocs.clientDocs.clientDef.tenId.eq(clientId));
        Long IdReqDoc = 0L;
        Long KraReqDoc = 0L;

        if(clientDocsList != null) {
            for (ClientDocs clientDocs : clientDocsList) {
                RequiredDocs nationalId = requiredDocsRepo.findOne(QRequiredDocs.requiredDocs1.reqId.eq(clientDocs.getRequiredDoc().getReqId()).and(QRequiredDocs.requiredDocs1.reqShtDesc.containsIgnoreCase("National ID")));
                if (nationalId != null) {
                    IdReqDoc = nationalId.getReqId();
                }
                RequiredDocs kraPin = requiredDocsRepo.findOne(QRequiredDocs.requiredDocs1.reqId.eq(clientDocs.getRequiredDoc().getReqId()).and(QRequiredDocs.requiredDocs1.reqShtDesc.containsIgnoreCase("KRA PIN")));
                if (kraPin != null) {
                    KraReqDoc = kraPin.getReqId();
                }
            }
        }
        String renewalTemplate = "";
        byte[] bytes = null;
        if(policyTrans.getProduct().isMotorProduct()){
            renewalTemplate = "rpt_renewal_notice_non_motor.jasper";
        }
        else{
            renewalTemplate = "rpt_renewal_notice_motor.jasper";
        }
        if(StringUtils.equalsIgnoreCase("oracle",databaseType)){
            resourcePath = "/oracle_reports/";
        }
        else if(StringUtils.equalsIgnoreCase("mssql",databaseType)){
            resourcePath = "/mssql/";
        }
        else if(StringUtils.equalsIgnoreCase("postgres",databaseType))
            resourcePath = "/reports/";
        switch (type){
            case "RN":
                if (policyTrans.getBusinessType().equalsIgnoreCase("L")) {
                    if (riskNote != null) {
                        resourcePath = resourcePath + String.format("%s.jasper", riskNote);
                    } else {
                        throw new BadRequestException("risk note for this product is not setup");
                    }
                } else {
                    resourcePath = resourcePath+"rpt_risk_note.jasper";
                }
                fileName = "risk_note.pdf";
                break;
            case "DN":
                resourcePath = resourcePath+"rpt_debit_note_client.jasper";
                fileName = "debit_note.pdf";
                break;
            case "PW":
                resourcePath = resourcePath+"rpt_prem_working.jasper";
                fileName = "prem_working.pdf";
                break;
            case "RT":
                resourcePath = resourcePath+renewalTemplate;
                fileName = "ren_notice.pdf";
                break;
            case "VR":
                resourcePath = resourcePath+"rpt_valuation_rpt.jasper";
                fileName = "valuation_rpt.pdf";
                break;
            case "ER":
                resourcePath = resourcePath+"rpt_endorse.jasper";
                fileName = "rpt_endorse.pdf";
                break;
            case "ILL": //ILUSTRATION
                resourcePath = resourcePath+"rpt_default_client_quote.jasper";
                fileName = "rpt_illustration.pdf";
                break;
            case "PSCH"://POLICY SCHEDULE
                resourcePath = resourcePath+"rpt_risk_note_life_install.jasper";
                fileName = "rpt_policy_schedule.pdf";
                break;
            case "ID":
                if (IdReqDoc != 0L) {
                    bytes = uploadService.sybrinDocumentDetails("Client", IdReqDoc, clientId);
                }
                break;
            case "KRA":
                if (KraReqDoc != 0L) {
                    bytes = uploadService.sybrinDocumentDetails("Client", KraReqDoc, clientId);
                }
                break;
            case "RCT":
                resourcePath = resourcePath + "rpt_receipt_printed.jasper";
                break;
            default:
        }
        InputStream jasperStream = this.getClass().getResourceAsStream(resourcePath);
        Map<String, Object> params = new HashMap<>();
        OrganizationDTO organization = organizationService.getOrganizationLogoDetails();

        Connection conn = null;
        try {
            conn = datasource.getConnection();
            InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
            BufferedImage image = ImageIO.read(in);
            params.put("logo", image );
            if (!type.equalsIgnoreCase("RCT")) {
                params.put("polId", polCode);
            }
            if (type.equals("RCT")) {
                List<Object[]> receiptTransDtls = receiptDetailsRepository.findAllPolRcts(polCode);
                List<ReceiptTrans> receiptTransList = new ArrayList<>();
                Long receiptId = 0L;
                for (Object[] transDtls : receiptTransDtls) {
                    ReceiptTrans receiptTrans = receiptRepository.findOne(QReceiptTrans.receiptTrans.receiptId.eq(((BigInteger) transDtls[0]).longValue()));
                    receiptTransList.add(receiptTrans);
                }
                Optional<ReceiptTrans> latestReceipt = receiptTransList.stream()
                        .max(Comparator.comparing(ReceiptTrans::getReceiptDate));

                if (latestReceipt.isPresent()) {
                    receiptId = latestReceipt.get().getReceiptId();
                }
                params.put("receiptId", receiptId);
            }
            //to prevent failure incase one document failes to be found
            if (jasperStream == null) {
                System.err.println("Jasper file not found: " + resourcePath + " - skipping report generation");
                return new byte[0]; // Return empty byte array
            }

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, conn);
            bytes = JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException | IOException | SQLException e) {
            System.err.println("Error Generating report..."+e.getMessage());
        }
        finally {
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    //Error closing conn....
                }
            }
        }
        return bytes;
    }


    //Policy number
    //DocumentType values are 1, 2 or 3  1. Newbusiness NBD 2. Endorsements EN 3. Claims
    // Include all the uploaded file information like file name,file size, content type etc..
    @Async
    public void  uploadAlakDocuments(Long transCode, String transType, Map<String, byte[]> reports) {
        if (reports == null || reports.isEmpty()) {
            System.out.println("No reports to upload to ALAK - skipping upload");
            return;
        }
        boolean isAlak = false;
        boolean isIntegration = false;
        PolicyTrans policyTrans = null;
        ClaimBookings claimBookings = null;
        if ("P".equalsIgnoreCase(transType)) {
            policyTrans = policyTransRepo.findOne(QPolicyTrans.policyTrans.policyId.eq(transCode));
            // Null safety checks
            if (policyTrans.getBinder() != null && policyTrans.getBinder().getBinName() != null) {
                String binName = policyTrans.getBinder().getBinName().toUpperCase();
                isAlak = binName.contains("ALAK");
                isIntegration = policyTrans.getBinder().getCalculatorType().contains("I");

            }
        } else if ("C".equalsIgnoreCase(transType)) {
            claimBookings = claimsBookingRepo.findOne(QClaimBookings.claimBookings.clmId.eq(transCode));
            if(claimBookings != null){
                String binName = claimBookings.getRisk().getPolicy().getBinder().getBinName().toUpperCase();
                isAlak = binName.contains("ALAK");
                isIntegration = claimBookings.getRisk().getPolicy().getBinder().getCalculatorType().contains("I");
            }
        }

        if(isAlak && isIntegration) {
            //items in alak computed via api and received documents via api
            try {

                policyTrans = null;
                claimBookings = null;
                QuoteTrans quoteTrans = null;
                MedicalParTrans medicalParTrans = null;

                int documentType = 0;
                String clientPolNo = "";

                if ("P".equalsIgnoreCase(transType)) {
                    policyTrans = policyTransRepo.findOne(QPolicyTrans.policyTrans.policyId.eq(transCode));
                    if (policyTrans != null) {

                        if (policyTrans.getTransType() != null) {
                            String transTypeUpper = policyTrans.getTransType().toUpperCase();
                            if (transTypeUpper.contains("NB")) {
                                documentType = 1;
                            } else if (transTypeUpper.contains("EN")) {
                                //Endorsements
                                documentType = 2;
                            }
                        }
                        clientPolNo = policyTrans.getClientPolNo();
                    }
                } else if ("Q".equalsIgnoreCase(transType)) {
                    quoteTrans = quotTransRepo.findOne(QQuoteTrans.quoteTrans.quoteId.eq(transCode));
                } else if ("M".equalsIgnoreCase(transType)) {
                    medicalParTrans = medicalParRepo.findOne(QMedicalParTrans.medicalParTrans.parId.eq(transCode));
                } else if ("C".equalsIgnoreCase(transType)) {
                    //claims
                    claimBookings = claimsBookingRepo.findOne(QClaimBookings.claimBookings.clmId.eq(transCode));
                    documentType = 3;
                }

                IntegrationUw integrationUw = integrationUwRepo.findOne(QIntegrationUw.integrationUw.polId.eq(transCode)); //.findByPolicyTransCode(transCode);
                System.out.println("client pol no:" + integrationUw.getClientPolNo());
                if (clientPolNo.isEmpty()) {
                    clientPolNo = integrationUw.getClientPolNo();
                }

                if (clientPolNo.isEmpty() || documentType == 0 || reports.isEmpty()) {
                    System.out.println("Skipping ALAK doc upload due to missing policy number, document type, or no reports, not an alak integration.");
                    return;
                }


                String paramValue = paramService.getParameterString("API_INTEGRATION_URL") + "/alak/uploadAlakDoc";

                // Create timeout RestTemplate
                HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
                factory.setConnectTimeout(5000);
                factory.setReadTimeout(15000);
                RestTemplate timeoutRestTemplate = new RestTemplate(factory);

                System.out.println("Starting ALAK upload for policy: " + clientPolNo + " (" + reports.size() + " documents)");


                for (Map.Entry<String, byte[]> reportEntry : reports.entrySet()) {
                    String fileName = reportEntry.getKey();// + ".pdf";
                    byte[] fileData = reportEntry.getValue();

                  //  System.out.println("file data " + Arrays.toString(fileData) + "base64" + Base64.getEncoder().encodeToString(fileData));
                    if (fileData == null || fileData.length == 0) {
                        System.out.println("Skipping ALAK doc upload due to missing report data" + fileName);
                        continue;
                    }

                    if (!(fileName.toLowerCase().endsWith(".pdf")
                            || fileName.toLowerCase().endsWith(".jpg")
                            || fileName.toLowerCase().endsWith(".jpeg"))) {
                        System.out.println("Skipping unsupported file type: " + fileName);
                        continue;
                    }

                    try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                        headers.set("Connection", "close");

                        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                        body.add("PolicyNumber", clientPolNo);
                        body.add("DocumentType", String.valueOf(documentType));

                        //create the file
//                ByteArrayResource fileResource = new ByteArrayResource(fileData) {
//                    @Override
//                    public String getFilename() {
//                        return fileName;
//                    }
//                };
//                body.add("File", fileResource);

                        //new changes start
                        String mimeType = getMimeType(fileName);

                        InputStreamResource fileResource = new InputStreamResource(new ByteArrayInputStream(fileData)) {
                            @Override
                            public String getFilename() {
                                return fileName;
                            }

                            @Override
                            public long contentLength() {
                                return fileData.length;
                            }
                        };
                        HttpHeaders filePartHeaders = new HttpHeaders();
                        filePartHeaders.setContentDispositionFormData("File", fileName);
                        filePartHeaders.setContentType(MediaType.parseMediaType(mimeType));
                        //filePartHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);


                        HttpEntity<Resource> filePart = new HttpEntity<>(fileResource, filePartHeaders);
                        body.add("File", filePart);

                        System.out.println("Uploading file: " + fileName);
                        System.out.println("File length: " + fileData.length);
                        //System.out.println("Base64 Preview: " + Base64.getEncoder().encodeToString(fileData).substring(0, 30));
                        //end of new

                        //System.out.println("CREATE ALAK UPLOAD DOCUMENT POLICY PAYLOAD: " + new Gson().toJson(body));

                        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
                        System.out.println("Uploading document: " + fileName + " for policy: " + clientPolNo);
                        System.out.println("Uploading: " + fileName + " (MIME: " + mimeType + ")");

                        ResponseEntity<String> responseEntity = timeoutRestTemplate.postForEntity(paramValue, requestEntity, String.class);
                        System.out.println("ALAK upload response for " + fileName + ": " + responseEntity.getBody());

                        if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                            JsonNode rootNode = objectMapper.readTree(responseEntity.getBody());
                            // Handle response based on 'Code'
                            String responseCode = rootNode.get("Code").asText();

                            if ("200".equals(responseCode)) {
                                // Successful response,
                                System.out.println("Successfully uploaded alak document: " + fileName);
                                //return rootNode.get("Message").asText();
                            } else {
                                // Handle missing fields error
                                System.out.println("Failed to upload alak document " + fileName + ": " + rootNode.get("Message").asText());
                                //throw new RuntimeException("Required DOCUMENT fields are missing: " + rootNode.get("Message").asText());
                            }
                        } else {
                            System.out.println("HTTP error uploading " + fileName + ": " + responseEntity.getStatusCode());
                        }
                        //delay between uploads
                        Thread.sleep(200);

                    } catch (Exception e) {
                        System.out.println("Error uploading alak documents " + fileName + " :" + e.getMessage());
                    }
                }
                System.out.println("ALAK upload completed for policy: " + clientPolNo);
            } catch (Exception e) {
                System.out.println("Error uploading alak documents " + e.getMessage());
            }
        }else {
            //items not computed via integration but have api for receiving info
            sendApaInfo(transCode, transType, reports);
        }
    }

    private String getMimeType(String fileName) {
        if (fileName.toLowerCase().endsWith(".pdf")) {
            return "application/pdf";
        } else if (fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg")) {
            return "image/jpeg";
        } else {
            return "application/octet-stream"; // fallback
        }
    }

    @Async
    public void sendApaInfo(Long transCode, String transType, Map<String, byte[]> reports) {
            if (reports == null || reports.isEmpty()) {
                System.out.println("No reports to upload to apa - skipping upload");
                return;
            }

        PolicyTrans policyTrans = policyTransRepo.findOne(QPolicyTrans.policyTrans.policyId.eq(transCode));
        Iterable<RiskTrans> riskTrans = riskRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(transCode));
        //apa motor staff to handle NB
        //use bin_url future to get api url of a policy
        System.out.println("outside api" +policyTrans.getProduct().getProDesc() +  policyTrans.getBinder().getBinName() + policyTrans.getAuthStatus());
        if (
                policyTrans.getProduct() != null && policyTrans.getBinder() != null &&
                        policyTrans.getProduct().getProDesc().toUpperCase().contains("MOTOR PRIVATE") &&
                        policyTrans.getBinder().getBinName().toUpperCase().contains("APA MOTOR PRIVATE")
        ) {
            System.out.println("inside api" + policyTrans.getProduct().getProDesc() + policyTrans.getBinder().getBinName() + policyTrans.getAuthStatus() + policyTrans.getInterfaceType());
            if (policyTrans.getTransType().equalsIgnoreCase("NB") ||
                    policyTrans.getTransType().equalsIgnoreCase("CN") ||
                    policyTrans.getTransType().equalsIgnoreCase("CO") ||
                    policyTrans.getTransType().equalsIgnoreCase("RN") ||
                    policyTrans.getTransType().equalsIgnoreCase("EN")
            ) {
                apaIntergrationService.postPolicy(policyTrans.getTransType(), policyTrans, riskTrans, reports);
            }
        }
    }
}
