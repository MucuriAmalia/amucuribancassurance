package com.brokersystems.brokerapp.mail.service.impl;

import com.brokersystems.brokerapp.claims.model.ClaimBookings;
import com.brokersystems.brokerapp.claims.repository.ClaimsBookingRepo;
import com.brokersystems.brokerapp.mail.dto.*;
import com.brokersystems.brokerapp.mail.model.*;
import com.brokersystems.brokerapp.mail.service.Mailer;
import com.brokersystems.brokerapp.medical.model.MedicalParTrans;
import com.brokersystems.brokerapp.medical.repository.MedicalParRepo;
import com.brokersystems.brokerapp.quotes.model.QQuoteTrans;
import com.brokersystems.brokerapp.quotes.model.QuoteTrans;
import com.brokersystems.brokerapp.quotes.repository.QuotTransRepo;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.TemplateMerger;
import com.brokersystems.brokerapp.setup.service.OrganizationService;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import de.siegmar.fastcsv.writer.CsvWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by HP on 8/9/2017.
 */
@Service
public class MailerImpl implements Mailer {


    @Autowired
    private TemplateMerger templateMerger;

    @Autowired
    private Environment env;

    @Autowired
    private PolicyTransRepo policyTransRepo;

    @Autowired
    private QuotTransRepo quotTransRepo;

    @Autowired
    private DataSource datasource;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private MedicalParRepo medicalParRepo;

    @Autowired
    private ClaimsBookingRepo claimsBookingRepo;

    @Autowired
    private SendEmailPublisherBean sendEmailPublisherBean;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ParamService paramService;


    @Override
    public void sendEmail(MailMessageBean messageBean) throws BadRequestException {
//        MimeMessagePreparator mimeMessagePreparator = mimeMessage -> {
//            String emailMessage = messageBean.getMessage();
//            String emailReceivers = messageBean.getSendTo();
//            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
//            messageHelper.setTo(StringUtils.split(emailReceivers, ","));
//            messageHelper.setFrom(env.getProperty("sender.username"),organizationService.getOrganizationDetails().getOrgName());
//            messageHelper.setSubject(messageBean.getSubject());
//            messageHelper.setText(emailMessage, true);
//        };
        sendEmailPublisherBean.sendEmail(messageBean);
    }


    @Override
    public void sendSmsAttachments(MailMessageBean message, Long transCode, String transType, HttpServletRequest request) throws BadRequestException {
        if (transType == null)
            throw new BadRequestException("Unable to determine Transaction Type To Merge the Message");


        String emailMessage = null;
        if ("P".equalsIgnoreCase(transType)) {
            PolicyTrans policyTrans = policyTransRepo.findOne(transCode);
            emailMessage = templateMerger.mergePolicyDetails(policyTrans, message.getMessage());
        } else if ("Q".equalsIgnoreCase(transType)) {
            QuoteTrans quoteTrans = quotTransRepo.findOne(transCode);
            emailMessage = templateMerger.mergeQuoteDetails(quoteTrans, message.getMessage());
        } else if ("M".equalsIgnoreCase(transType)) {
            MedicalParTrans medicalParTrans = medicalParRepo.findOne(transCode);
            emailMessage = templateMerger.mergeMedClaims(medicalParTrans, message.getMessage());
        }
        try {
            File dir = new File("sms");
            if (!dir.exists()) dir.mkdir();
            File file = new File("sms/FIA" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".csv");
            if (!file.exists()) file.createNewFile();
            CsvWriter csvWriter = new CsvWriter();

            Collection<String[]> data = new ArrayList<>();
            data.add(new String[]{message.getSendTo(), emailMessage.replace("<div>", "").replace("</div>", "")});


            csvWriter.write(file, StandardCharsets.UTF_8, data);

            try (FileWriter writer = new FileWriter("sms/sms.bat")) {
                writer.write("move *.csv  Z:/");
            }


//            Commandline commandLine = new Commandline();
//
//
//            WriterStreamConsumer systemOut = new WriterStreamConsumer(
//                    new OutputStreamWriter(System.out));
//
//            WriterStreamConsumer systemErr = new WriterStreamConsumer(
//                    new OutputStreamWriter(System.out));
//
//            int returnCode = CommandLineUtils.executeCommandLine(commandLine, systemOut, systemErr,0);
//            if (returnCode != 0) {
//                System.out.println("Something Bad Happened!");
//            } else {
//                System.out.println("Taaa!! ddaaaaa!!");
//            };

        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }


    }

    @Override
    public void sendSmsAttachmentsANE(MailMessageBean message, Long transCode, String transType, HttpServletRequest request) throws BadRequestException {
        if (transType == null)
            throw new BadRequestException("Unable to determine Transaction Type To Merge the Message");



        String smsMessage = null;
        if ("P".equalsIgnoreCase(transType)) {
            PolicyTrans policyTrans = policyTransRepo.findOne(transCode);
            smsMessage = templateMerger.mergePolicyDetails(policyTrans, message.getMessage());
        } else if ("Q".equalsIgnoreCase(transType)) {
            QuoteTrans quoteTrans = quotTransRepo.findOne(QQuoteTrans.quoteTrans.quoteId.eq(transCode));
            smsMessage = templateMerger.mergeQuoteDetails(quoteTrans, message.getMessage());
        } else if ("M".equalsIgnoreCase(transType)) {
            MedicalParTrans medicalParTrans = medicalParRepo.findOne(transCode);
            smsMessage = templateMerger.mergeMedClaims(medicalParTrans, message.getMessage());
        } else {
            smsMessage = message.getMessage();
        }
        String aneUrl = paramService.getParameterString("API_INTEGRATION_URL") +  "/ane/sendSms";

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");
        headers.setAll(map);

        DynamicFields dynamicField = new DynamicFields();
        dynamicField.setName("ALERTTEXT");
        dynamicField.setType("String");
        dynamicField.setValue(smsMessage.replaceAll("<[^>]*>", "").replaceAll("\\s+", " ").trim());

        DynamicFields dynamicField2 = new DynamicFields();
        dynamicField2.setName("TRXREFNUMBER");
        dynamicField2.setType("String");
        dynamicField2.setValue("1234567");

        List<DynamicFields> dynamicFields = new ArrayList<>();
        dynamicFields.add(dynamicField);
        dynamicFields.add(dynamicField2);

        CorrespondenceAddressee addressee = new CorrespondenceAddressee();
        addressee.setMobileContactType("8");
        //254723398160
        addressee.setMobileContactValue("254712955066");

        CorrespondenceContent content = new CorrespondenceContent();
        content.setCustomerId("44556677");

        CorrespondenceMedia media = new CorrespondenceMedia();
        media.setDeliveryMode("1");

        CorrespondenceTemplateReference templateReference = new CorrespondenceTemplateReference();
        templateReference.setSmsDestinationType("SMS");
        templateReference.setSmsEventId("BANCA001");

        ProductAndServiceType productAndServiceType = new ProductAndServiceType();
        productAndServiceType.setEventIdentifier("");

        EventDetailList eventDetail = new EventDetailList();
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
        System.out.println("SMS DETAILS" + details);
        HttpEntity<?> requestEntity = new HttpEntity<>(details,headers);
        try {
            //ResponseEntity<String> responseEntity = restTemplate.postForEntity(aneUrl, requestEntity, String.class);
            ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(aneUrl, requestEntity, JsonNode.class);
            String responseBody = responseEntity.getBody().toString();
            System.out.println("Response body: " + responseBody);
            Gson gson = new Gson();
            Map<String,Object> responseMap = gson.fromJson(responseBody, Map.class);
            Map<String,Object> dataMap = (Map<String, Object>) responseMap.get("data");
            if (dataMap.get("responseCode").equals("0")) {
                System.out.println("SMS RESPONSE: " + responseMap);
            }
        } catch (RestClientException e) {
            //throw new RuntimeException(e);
        }
//        try {
//            File dir = new File("sms");
//            if (!dir.exists()) dir.mkdir();
//            File file = new File("sms/FIA" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".csv");
//            if (!file.exists()) file.createNewFile();
//            CsvWriter csvWriter = new CsvWriter();
//
//            Collection<String[]> data = new ArrayList<>();
//            data.add(new String[]{message.getSendTo(), emailMessage.replace("<div>", "").replace("</div>", "")});
//
//
//            csvWriter.write(file, StandardCharsets.UTF_8, data);
//
//            try (FileWriter writer = new FileWriter("sms/sms.bat")) {
//                writer.write("move *.csv  Z:/");
//            }
//
//
////            Commandline commandLine = new Commandline();
////
////            File executable = new File("sms/sms.bat");
////            commandLine.setExecutable(executable.getAbsolutePath());
////
////            WriterStreamConsumer systemOut = new WriterStreamConsumer(
////                    new OutputStreamWriter(System.out));
////
////            WriterStreamConsumer systemErr = new WriterStreamConsumer(
////                    new OutputStreamWriter(System.out));
////
////            int returnCode = CommandLineUtils.executeCommandLine(commandLine, systemOut, systemErr,0);
////            if (returnCode != 0) {
////                System.out.println("Something Bad Happened!");
////            } else {
////                System.out.println("Taaa!! ddaaaaa!!");
////            };
//
//        } catch (IOException e) {
//            throw new BadRequestException(e.getMessage());
//        }


    }


    @Override
    public void sendEmailAttachments(MailMessageBean message, Long transCode, String transType, HttpServletRequest request) throws BadRequestException {
        if (transType == null)
            throw new BadRequestException("Unable to determine Transaction Type To Merge the Message");

        String emailMessage = "";
        String emailReceivers = "";
        if ("P".equalsIgnoreCase(transType)) {
            PolicyTrans policyTrans = policyTransRepo.findOne(transCode);
            emailMessage = templateMerger.mergePolicyDetails(policyTrans, message.getMessage());
            emailReceivers = getEmailReceivers(transCode, "P", message.getReceiverType());
        } else if ("Q".equalsIgnoreCase(transType)) {
            QuoteTrans quoteTrans = quotTransRepo.findOne(QQuoteTrans.quoteTrans.quoteId.eq(transCode));
            emailMessage = templateMerger.mergeQuoteDetails(quoteTrans, message.getMessage());
            emailReceivers = getEmailReceivers(transCode, "Q", message.getReceiverType());
        } else if ("M".equalsIgnoreCase(transType)) {
            MedicalParTrans medicalParTrans = medicalParRepo.findOne(transCode);
            emailMessage = templateMerger.mergeMedClaims(medicalParTrans, message.getMessage());
            emailReceivers = getEmailReceivers(transCode, "M", message.getReceiverType());
        } else if ("C".equalsIgnoreCase(transType)) {
            ClaimBookings booking = claimsBookingRepo.findOne(transCode);
            emailMessage = templateMerger.mergeClaims(booking, message.getMessage());
            emailReceivers = getEmailReceivers(transCode, "C", message.getReceiverType());
        }

        emailReceivers += "," + message.getSendTo();
        MailMessageBean messageHelper = new MailMessageBean();
        messageHelper.setSendTo(emailReceivers);
        if (!message.getSendBcc().isEmpty())
            messageHelper.setSendTo(message.getSendBcc());
        if (!message.getSendCC().isEmpty())
            messageHelper.setSendCC(message.getSendCC());
        messageHelper.setSubject(message.getSubject());
        messageHelper.setMessage(emailMessage);
        messageHelper.setTransCode(transCode);
        messageHelper.setTransType(transType);
        messageHelper.setReports(message.getReports());
        sendEmailPublisherBean.sendEmail(messageHelper);
    }

    @Override
    public String getSMSReceivers(Long transCode, String transType, String receiver) throws BadRequestException {
        if (transType == null)
            throw new BadRequestException("Unable to determine Transaction Type To Merge the Message");
        if (receiver == null)
            throw new BadRequestException("Receiver Type Cannot be null...");
        if ("P".equalsIgnoreCase(transType)) {
            PolicyTrans policyTrans = policyTransRepo.findOne(transCode);
            if ("B".equalsIgnoreCase(receiver)) {
                if (policyTrans.getClient().getPhonePrefix() != null) {
                    String smsNumber = policyTrans.getClient().getPhoneNo();
                    if (smsNumber.startsWith("0")) {
                        smsNumber = smsNumber.substring(1);
                    }
                    return policyTrans.getClient().getPhonePrefix().getPrefixName() + smsNumber;
                }
                if (policyTrans.getAgent().getPhoneNo() != null) {
                    return policyTrans.getAgent().getPhoneNo();
                }
            } else if ("C".equalsIgnoreCase(receiver)) {
                if (policyTrans.getClient().getPhonePrefix() != null) {
                    String smsNumber = policyTrans.getClient().getPhoneNo();
                    if (smsNumber.startsWith("0")) {
                        smsNumber = smsNumber.substring(1);
                    }
                    return policyTrans.getClient().getPhonePrefix().getPrefixName() + smsNumber;
                }
            } else if ("A".equalsIgnoreCase(receiver)) {
                if (policyTrans.getAgent().getPhoneNo() != null) {
                    return policyTrans.getAgent().getPhoneNo();
                }
            }

        } else if ("Q".equalsIgnoreCase(transType)) {
            QuoteTrans policyTrans = quotTransRepo.findOne(QQuoteTrans.quoteTrans.quoteId.eq(transCode));
            System.out.println("Receiver " + receiver + " prefix " + policyTrans.getClient());
            if ("C".equalsIgnoreCase(receiver)) {
                if ("C".equalsIgnoreCase(policyTrans.getClientType())) {
                    if (policyTrans.getClient().getPhonePrefix() != null) {
                        String smsNumber = policyTrans.getClient().getPhoneNo();
                        if (smsNumber.startsWith("0")) {
                            smsNumber = smsNumber.substring(1);
                        }
                        return policyTrans.getClient().getPhonePrefix().getPrefixName() + smsNumber;
                    }
                } else if ("P".equalsIgnoreCase(policyTrans.getClientType())) {
                    if (policyTrans.getProspect().getSmsPrefix() != null) {
                        String smsNumber = policyTrans.getProspect().getSmsNumber();
                        if (smsNumber.startsWith("0")) {
                            smsNumber = smsNumber.substring(1);
                        }
                        return policyTrans.getProspect().getSmsPrefix().getPrefixName() + smsNumber;
                    }
                }
            }

        } else if ("M".equalsIgnoreCase(transType)) {
            MedicalParTrans parTrans = medicalParRepo.findOne(transCode);
            if ("S".equalsIgnoreCase(receiver)) {
                if (parTrans.getProviderContracts().getServiceProviders().getEmail() != null) {
                    return "0" + parTrans.getProviderContracts().getServiceProviders().getTelNumber();
                }
            }

        }
        return null;
    }

    @Override
    public String getEmailReceivers(Long transCode, String transType, String receiver) throws BadRequestException {
        if (transType == null)
            throw new BadRequestException("Unable to determine Transaction Type To Merge the Message");
        if (receiver == null)
            throw new BadRequestException("Receiver Type Cannot be null...");
        StringBuilder emailHolder = new StringBuilder();
        if ("P".equalsIgnoreCase(transType)) {
            PolicyTrans policyTrans = policyTransRepo.findOne(transCode);
            if ("B".equalsIgnoreCase(receiver)) {
                if (policyTrans.getClient().getEmailAddress() != null) {
                    emailHolder.append(policyTrans.getClient().getEmailAddress());
                    emailHolder.append(",");
                }
                if (policyTrans.getAgent().getEmail() != null) {
                    emailHolder.append(policyTrans.getAgent().getEmail());
                }
            } else if ("C".equalsIgnoreCase(receiver)) {
                if (policyTrans.getClient().getEmailAddress() != null) {
                    emailHolder.append(policyTrans.getClient().getEmailAddress());
                }
            } else if ("A".equalsIgnoreCase(receiver)) {
                if (policyTrans.getAgent().getEmail() != null) {
                    emailHolder.append(policyTrans.getAgent().getEmail());
                }
            }

        } else if ("Q".equalsIgnoreCase(transType)) {
            QuoteTrans policyTrans = quotTransRepo.findOne(QQuoteTrans.quoteTrans.quoteId.eq(transCode));
            if ("C".equalsIgnoreCase(receiver)) {
                if ("C".equalsIgnoreCase(policyTrans.getClientType())) {
                    if (policyTrans.getClient().getEmailAddress() != null) {
                        emailHolder.append(policyTrans.getClient().getEmailAddress());
                    }
                } else if ("P".equalsIgnoreCase(policyTrans.getClientType())) {
                    if (policyTrans.getProspect().getEmailAddress() != null) {
                        emailHolder.append(policyTrans.getProspect().getEmailAddress());
                    }
                }
            }

        } else if ("M".equalsIgnoreCase(transType)) {
            MedicalParTrans parTrans = medicalParRepo.findOne(transCode);
            if ("S".equalsIgnoreCase(receiver)) {
                if (parTrans.getProviderContracts().getServiceProviders().getEmail() != null) {
                    emailHolder.append(parTrans.getProviderContracts().getServiceProviders().getEmail());
                }
            }

        } else if ("C".equalsIgnoreCase(transType)) {
            ClaimBookings booking = claimsBookingRepo.findOne(transCode);
            PolicyTrans policyTrans = booking.getRisk().getPolicy();
            if ("B".equalsIgnoreCase(receiver)) {
                if (policyTrans.getClient().getEmailAddress() != null) {
                    emailHolder.append(policyTrans.getClient().getEmailAddress());
                    emailHolder.append(",");
                }
                if (policyTrans.getAgent().getEmail() != null) {
                    emailHolder.append(policyTrans.getAgent().getEmail());
                }
            } else if ("C".equalsIgnoreCase(receiver)) {
                if (policyTrans.getClient().getEmailAddress() != null) {
                    emailHolder.append(policyTrans.getClient().getEmailAddress());
                }
            } else if ("A".equalsIgnoreCase(receiver)) {
                if (policyTrans.getAgent().getEmail() != null) {
                    emailHolder.append(policyTrans.getAgent().getEmail());
                }
            }

        }

        return emailHolder.toString();
    }


}
