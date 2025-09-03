package com.brokersystems.brokerapp.integrations.serviceimpl;

import com.brokersystems.brokerapp.dms.model.SybrinCases;
import com.brokersystems.brokerapp.dms.dto.*;
import com.brokersystems.brokerapp.dms.repo.SybrinCasesRepo;
import com.brokersystems.brokerapp.integrations.service.SybrinDocumentService;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.retry.annotation.Backoff;
//import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

/**
 * Created by Dancan Angwenyi on 19/06/2025.
 * <p>
 * Implementation of the SybrinDocumentService for interacting with the Sybrin document management system.
 */
@Service
@Slf4j
public class SybrinDocumentServiceImpl implements SybrinDocumentService {

    @Autowired
    private SybrinCasesRepo sybrinCasesRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${sybrin.base.url:https://uat-bss.intra.absaafrica/SybrinCMS-KE/SybrinCMS.svc}")
    private String baseUrl;

    @Value("${sybrin.token.store:DMS}")
    private String tokenStore;

    @Value("${sybrin.case.type:463}")
    private String caseType;

    private static final String SOAP_ENVELOPE_NS = "http://schemas.xmlsoap.org/soap/envelope/";
    private static final String TEMPURI_NS = "http://tempuri.org/";
    private static final String SYBRIN_NS = "http://schemas.datacontract.org/2004/07/SybrinCMS";

    /**
     * Creates a new case in the Sybrin system and associates it with the provided SybrinCases entity.
     *
     * @param caseCreation the case creation data transfer object containing case details
     * @param sybrinCases  the SybrinCases entity to be updated with case details
     * @return the updated SybrinCases entity with case number and document GUID
     * @throws IOException         if there is an error communicating with the Sybrin API
     * @throws BadRequestException if the case creation fails or response is invalid
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
//    @Retryable(value = IOException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public SybrinCases createCase(CaseCreation caseCreation, SybrinCases sybrinCases) throws IOException, BadRequestException {
        log.info("Creating Sybrin case for transaction ID: {}", caseCreation.getTransactionId());

        int transactionId = getRandomNumberInRange();
        StringBuilder soapPayload = new StringBuilder();
        soapPayload.append(String.format(
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                        "<soapenv:Envelope xmlns:soapenv=\"%s\" xmlns:tem=\"%s\" xmlns:syb=\"%s\">" +
                        "<soapenv:Header/>" +
                        "<soapenv:Body>" +
                        "<tem:CreateCase>" +
                        "<tem:TransactionID>%d</tem:TransactionID>" +
                        "<tem:TokenStore>%s</tem:TokenStore>" +
                        "<tem:CaseType>%s</tem:CaseType>" +
                        "<tem:CaseNumber></tem:CaseNumber>" +
                        "</tem:CreateCase>" +
                        "</soapenv:Body>" +
                        "</soapenv:Envelope>",
                SOAP_ENVELOPE_NS, TEMPURI_NS, SYBRIN_NS, transactionId, tokenStore, caseType));

        String response = postSoapXml(baseUrl, soapPayload.toString(), "http://tempuri.org/ISybrinCMS/CreateCase");
        String caseNumber = extractCaseNumber(response);
        if (caseNumber == null) {
            throw new BadRequestException("Failed to extract case number from Sybrin response");
        }

        sybrinCases.setCaseNumber(caseNumber);
        sybrinCases.setCaseType(caseType);
        addDocumentsToCase(caseCreation, sybrinCases, transactionId);
        return sybrinCasesRepo.save(sybrinCases);
    }

    /**
     * Adds documents to an existing Sybrin case.
     *
     * @param caseCreation  the case creation data containing document details
     * @param sybrinCases   the SybrinCases entity to be updated with document GUID
     * @param transactionId the transaction ID for the SOAP request
     * @throws IOException if there is an error communicating with the Sybrin API
     */
    private void addDocumentsToCase(CaseCreation caseCreation, SybrinCases sybrinCases, int transactionId) throws IOException {
        log.info("Adding documents to Sybrin case: {}", sybrinCases.getCaseNumber());

        StringBuilder soapPayload = new StringBuilder();
        soapPayload.append(String.format(
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                        "<soapenv:Envelope xmlns:soapenv=\"%s\" xmlns:tem=\"%s\" xmlns:syb=\"%s\">" +
                        "<soapenv:Header/>" +
                        "<soapenv:Body>" +
                        "<tem:AddDocumentsToCase>" +
                        "<tem:TransactionID>%d</tem:TransactionID>" +
                        "<tem:TokenStore>%s</tem:TokenStore>" +
                        "<tem:CaseType>%s</tem:CaseType>" +
                        "<tem:CaseNumber>%s</tem:CaseNumber>" +
                        "<tem:Documents>", SOAP_ENVELOPE_NS, TEMPURI_NS, SYBRIN_NS, transactionId, tokenStore, caseType, sybrinCases.getCaseNumber()));

        for (Documents doc : caseCreation.getDocuments()) {
            soapPayload.append("<syb:Document>" + "<syb:DocumentGUID></syb:DocumentGUID>").append(String.format("<syb:DocumentType>%s</syb:DocumentType>", doc.getDocumentTypeValue())).append("<syb:Fields>");
            for (CaseFields field : doc.getFields()) {
                soapPayload.append(String.format(
                        "<syb:Field>" +
                                "<syb:Description>%s</syb:Description>" +
                                "<syb:Name>%s</syb:Name>" +
                                "<syb:Required>%b</syb:Required>" +
                                "<syb:Value>%s</syb:Value>" +
                                "</syb:Field>", field.getDescription(), field.getName(), field.isRequired(), field.getValue()));
            }
            soapPayload.append("</syb:Fields>").append(String.format("<syb:Name>%s</syb:Name>", caseCreation.getDocumentSubjectReference().getCaseType())).append("<syb:NativeContent>");
            for (DocumentContent content : doc.getDocumentContents()) {
                soapPayload.append("<syb:NativeContent>")
                        .append(String.format("<syb:Base64Data>%s</syb:Base64Data>", content.getBase64Data()))
                        .append(String.format("<syb:ContentFileExtension>%s</syb:ContentFileExtension>", content.getDocumentFormat().getContentFileExtension()))
                        .append(String.format("<syb:ContentFileName>%s</syb:ContentFileName>", content.getDocumentFormat().getContentFileName()))
                        .append("</syb:NativeContent>");
            }
            soapPayload.append("</syb:NativeContent>" +
                    "<syb:required>false</syb:required>" +
                    "</syb:Document>");
        }
        soapPayload.append("</tem:Documents>" +
                "</tem:AddDocumentsToCase>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>");

        String response = postSoapXml(baseUrl, soapPayload.toString(), "http://tempuri.org/ISybrinCMS/AddDocumentsToCase");
        JSONObject jsonResponse = XML.toJSONObject(response);
        String documentGuid = extractDocumentGuid(jsonResponse);
        sybrinCases.setDocumentGuid(documentGuid);
    }

    /**
     * Updates an existing case in the Sybrin system with additional documents.
     *
     * @param caseUpdate  the case update data transfer object containing update details
     * @param sybrinCases the SybrinCases entity to be updated with new document details
     * @return the updated SybrinCases entity with updated document GUID
     * @throws IOException         if there is an error communicating with the Sybrin API
     * @throws BadRequestException if the case update fails or response is invalid
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
//    @Retryable(value = IOException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public SybrinCases updateCase(CaseUpdate caseUpdate, SybrinCases sybrinCases) throws IOException, BadRequestException {
        log.info("Updating Sybrin case: {}", sybrinCases.getCaseNumber());
        addDocumentsToCase(convertUpdateToCreation(caseUpdate), sybrinCases, getRandomNumberInRange());
        return sybrinCasesRepo.save(sybrinCases);
    }

    /**
     * Retrieves document content from the Sybrin system based on the provided document details.
     *
     * @param documentDetails the document details data transfer object containing document identifiers
     * @return the document content as a byte array
     * @throws IOException         if there is an error communicating with the Sybrin API
     * @throws BadRequestException if no document content is found or response is invalid
     */
    @Override
//    @Retryable(value = IOException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public byte[] retrieveDocumentDetails(DocumentDetails documentDetails) throws IOException, BadRequestException {
        log.info("Retrieving document details for GUID: {}", documentDetails.getDocuments().get(0).getDocumentName().getDocumentGuid());

        int transactionId = getRandomNumberInRange();
        StringBuilder soapPayload = new StringBuilder();
        soapPayload.append(String.format(
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                        "<soapenv:Envelope xmlns:soapenv=\"%s\" xmlns:tem=\"%s\" xmlns:syb=\"%s\">" +
                        "<soapenv:Header/>" +
                        "<soapenv:Body>" +
                        "<tem:GetDocumentContent>" +
                        "<tem:TransactionID>%d</tem:TransactionID>" +
                        "<tem:TokenStore>%s</tem:TokenStore>" +
                        "<tem:Documents>", SOAP_ENVELOPE_NS, TEMPURI_NS, SYBRIN_NS, transactionId, tokenStore));

        for (DocDetails doc : documentDetails.getDocuments()) {
            soapPayload.append("<syb:Document>")
                    .append(String.format("<syb:DocumentGUID>%s</syb:DocumentGUID>", doc.getDocumentName().getDocumentGuid()))
                    .append(String.format("<syb:DocumentType>%s</syb:DocumentType>", doc.getDocumentTypeValue())).append("<syb:Fields></syb:Fields>")
                    .append(String.format("<syb:required>%b</syb:required>", doc.isRequired())).append("</syb:Document>");
        }
        soapPayload.append("</tem:Documents>" +
                "</tem:GetDocumentContent>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>");

        String response = postSoapXml(baseUrl, soapPayload.toString(), "http://tempuri.org/ISybrinCMS/GetDocumentContent");
        JSONObject jsonResponse = XML.toJSONObject(response);
        String base64Data = extractBase64Data(jsonResponse);
        if (base64Data.isEmpty()) {
            throw new BadRequestException("No document content found in Sybrin response");
        }
        return Base64.decodeBase64(base64Data);
    }

    /**
     * Sends a SOAP request to the Sybrin API and returns the response.
     *
     * @param url        the Sybrin API endpoint URL
     * @param payload    the SOAP XML payload
     * @param soapAction the SOAP action header value
     * @return the SOAP response as a string
     * @throws IOException if there is an error communicating with the Sybrin API
     */
    private String postSoapXml(String url, String payload, String soapAction) throws IOException {
        log.debug("Posting SOAP request to: {}", url);
        HttpURLConnection con = null;
        try {
            URL obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "text/xml");
            con.setRequestProperty("SOAPAction", soapAction);
            con.setDoOutput(true);

            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.writeBytes(payload);
                wr.flush();
            }

            int responseCode = con.getResponseCode();
            if (responseCode >= 400) {
                log.error("Sybrin API returned error: {}", con.getResponseMessage());
                throw new IOException("Sybrin API error: " + con.getResponseMessage());
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
            log.debug("Sybrin API response: {}", response);
            return response.toString();
        } catch (IOException e) {
            log.error("Error communicating with Sybrin API: {}", e.getMessage(), e);
            throw e;
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    /**
     * Extracts the case number from the Sybrin SOAP response.
     *
     * @param xmlResponse the SOAP response XML
     * @return the case number, or null if extraction fails
     */
    private String extractCaseNumber(String xmlResponse) {
        try {
            JSONObject json = XML.toJSONObject(xmlResponse);
            return json.getJSONObject("s:Envelope")
                    .getJSONObject("s:Body")
                    .getJSONObject("CreateCaseResponse")
                    .getString("CaseNumber");
        } catch (Exception e) {
            log.error("Failed to extract case number: {}", e.getMessage(), e);
            return null;
        }
    }


    /**
     * Extracts the document GUID from the Sybrin SOAP response.
     *
     * @param jsonResponse the JSON representation of the SOAP response
     * @return the document GUID, or null if extraction fails
     */
    private String extractDocumentGuid(JSONObject jsonResponse) {
        try {
            return jsonResponse.getJSONObject("s:Envelope")
                    .getJSONObject("s:Body")
                    .getJSONObject("AddDocumentsToCaseResponse")
                    .getJSONObject("AddDocumentsToCaseResult")
                    .getJSONObject("Documents")
                    .getJSONObject("Document")
                    .getString("DocumentGUID");
        } catch (Exception e) {
            log.error("Failed to extract document GUID: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Extracts the base64-encoded document content from the Sybrin SOAP response.
     *
     * @param jsonResponse the JSON representation of the SOAP response
     * @return the base64-encoded document content, or empty string if extraction fails
     */
    private String extractBase64Data(JSONObject jsonResponse) {
        try {
            return jsonResponse.getJSONObject("s:Envelope")
                    .getJSONObject("s:Body")
                    .getJSONObject("GetDocumentContentResponse")
                    .getJSONObject("GetDocumentContentResult")
                    .getJSONObject("Documents")
                    .getJSONObject("Document")
                    .getJSONObject("NativeContent")
                    .getJSONObject("NativeContent")
                    .getString("Base64Data");
        } catch (Exception e) {
            log.error("Failed to extract base64 data: {}", e.getMessage(), e);
            return "";
        }
    }

    /**
     * Converts a CaseUpdate object to a CaseCreation object for use in document addition.
     *
     * @param caseUpdate the case update data transfer object
     * @return the converted CaseCreation object
     */
    private CaseCreation convertUpdateToCreation(CaseUpdate caseUpdate) {
        CaseCreation caseCreation = new CaseCreation();
        caseCreation.setTransactionId(caseUpdate.getTransactionId());
        caseCreation.setCaseFields(caseUpdate.getCaseFields());
        caseCreation.setDocumentSubjectReference(caseUpdate.getDocumentSubjectReference());
        caseCreation.setDocuments(caseUpdate.getDocuments());
        return caseCreation;
    }

    /**
     * Generates a random transaction ID for SOAP requests.
     *
     * @return a random integer between 1 and 10,000,000
     */
    private static int getRandomNumberInRange() {
        Random r = new Random();
        return r.nextInt((10000000 - 1) + 1) + 1;
    }
}
