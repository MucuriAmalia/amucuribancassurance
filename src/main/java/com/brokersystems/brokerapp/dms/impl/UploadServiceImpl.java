package com.brokersystems.brokerapp.dms.impl;

import com.brokersystems.brokerapp.claims.model.*;
import com.brokersystems.brokerapp.claims.repository.ClaimRequiredDocsRepo;
import com.brokersystems.brokerapp.claims.repository.ClaimUploadRepo;
import com.brokersystems.brokerapp.claims.repository.ClaimsBookingRepo;
import com.brokersystems.brokerapp.dms.model.QSybrinCases;
import com.brokersystems.brokerapp.dms.model.SybrinCases;
import com.brokersystems.brokerapp.dms.repo.SybrinCasesRepo;
import com.brokersystems.brokerapp.dms.service.UploadService;
import com.brokersystems.brokerapp.dms.dto.*;
import com.brokersystems.brokerapp.dms.model.UploadBean;
import com.brokersystems.brokerapp.medical.model.MedParReqDocs;
import com.brokersystems.brokerapp.medical.model.MedicalParTrans;
import com.brokersystems.brokerapp.medical.repository.CategoryMembersRepo;
import com.brokersystems.brokerapp.medical.repository.MedParDocsRepo;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.dto.SubClassReqdDocsDTO;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.*;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.brokersystems.brokerapp.uw.model.QRiskDocs;
import com.brokersystems.brokerapp.uw.model.RiskDocs;
import com.brokersystems.brokerapp.uw.model.RiskTrans;
import com.brokersystems.brokerapp.uw.repository.RiskDocsRepo;
import com.brokersystems.brokerapp.uw.repository.RiskTransRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysema.query.types.expr.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by HP on 8/14/2017.
 */
@Service
@Slf4j
public class UploadServiceImpl implements UploadService {

    @Autowired
    private ParamService paramService;

    @Autowired
    private RiskDocsRepo riskDocsRepo;

    @Autowired
    private ClaimUploadRepo uploadRepo;

    @Autowired
    private MedParDocsRepo parDocsRepo;

    @Autowired
    private ClientDocsRepo clientDocsRepo;

    @Autowired
    private AccountDocsRepo accountDocsRepo;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private BinderReqrdDocsRepo reqrdDocsRepo;

    @Autowired
    private CategoryMembersRepo categoryMembersRepo;

    @Autowired
    private RiskTransRepo riskTransRepo;

    @Autowired
    private ClaimRequiredDocsRepo claimRequiredDocsRepo;

    @Autowired
    private ClaimsBookingRepo claimsBookingRepo;

    @Autowired
    private SubclassReqDocRepo requiredDocsRepo;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SybrinCasesRepo sybrinCasesRepo;

    // #TODO: Updated method to support comprehensive file types for bancassurance, including multiple Excel extensions, CSV, email formats, and a default type "11" for unrecognized extensions, using Map for Java 8 compatibility
    private String getDocumentTypeValue(String fileExtension) throws BadRequestException {
        if (fileExtension == null || fileExtension.trim().isEmpty()) {
            throw new BadRequestException("File extension cannot be null or empty");
        }
        String ext = fileExtension.toLowerCase();

        Map<String, String> extensionToType = new HashMap<>();
        extensionToType.put("pdf", "2");     // PDF documents (e.g., insurance forms, policies)
        extensionToType.put("jpg", "1");     // Image files (e.g., scanned IDs, photos)
        extensionToType.put("jpeg", "1");
        extensionToType.put("png", "1");
        extensionToType.put("gif", "1");
        extensionToType.put("bmp", "1");
        extensionToType.put("xls", "3");     // Excel files (e.g., financial statements, data exports)
        extensionToType.put("xlsx", "3");
        extensionToType.put("xlsm", "3");
        extensionToType.put("xlsb", "3");
        extensionToType.put("doc", "4");     // Word documents (e.g., contracts, letters)
        extensionToType.put("docx", "4");
        extensionToType.put("txt", "5");     // Plain text files (e.g., notes, logs)
        extensionToType.put("rtf", "6");     // Rich text format (e.g., formatted documents)
        extensionToType.put("tiff", "7");    // TIFF images (e.g., high-quality scans)
        extensionToType.put("tif", "7");
        extensionToType.put("csv", "10");    // CSV files (e.g., data exports, member lists)
        extensionToType.put("msg", "12");    // Email files (e.g., Outlook messages, email archives)
        extensionToType.put("eml", "12");
        extensionToType.put("odt", "13");    // OpenDocument Text (e.g., open-source documents)
        extensionToType.put("ods", "14");    // OpenDocument Spreadsheet (e.g., open-source spreadsheets)
        extensionToType.put("odp", "15");    // OpenDocument Presentation (e.g., presentations)
        extensionToType.put("ppt", "16");    // PowerPoint presentations (e.g., training materials)
        extensionToType.put("pptx", "16");
        extensionToType.put("zip", "17");    // ZIP archives (e.g., compressed document sets)
        extensionToType.put("xml", "18");    // XML files (e.g., data interchange)

        return extensionToType.getOrDefault(ext, "11"); // Default type for unrecognized extensions
    }

    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void uploadRiskDocument(UploadBean uploadBean) throws BadRequestException {
        if (uploadBean.getFile().isEmpty())
            throw new BadRequestException("Upload File is Empty...");
        if (uploadBean.getDocId() == null)
            throw new BadRequestException("Risk Document ID cannot be null");
        String uploadFolder = paramService.getParameterString("APP_UPLOAD_FOLDER");
        RiskDocs riskDoc = riskDocsRepo.getRiskDocsVal(uploadBean.getDocId());
        String batchNo = "";
        if (riskDoc.getRisk().getRiskId() != null)
            batchNo = "BATCH_" + String.valueOf(riskDoc.getRisk().getPolicy().getPolNo());
        else if (riskDoc.getMember() != null)
            batchNo = "BATCH_" + String.valueOf(riskDoc.getPolId());
        String riskId = "";
        if (riskDoc.getRisk().getRiskId() != null)
            riskId = "RSK_" + String.valueOf(riskDoc.getRisk().getRiskId());
        else if (riskDoc.getMember() != null)
            riskId = "MEM_" + String.valueOf(riskDoc.getMember().getMemberShipNo());
        riskDoc.setUploadedFileName(uploadBean.getFile().getOriginalFilename());
        try {
            byte[] bytes = uploadBean.getFile().getBytes();
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(bytes);
            String hashString = new BigInteger(1, digest).toString(16);
            riskDoc.setCheckSum(hashString);
            riskDoc.setContentType(uploadBean.getFile().getContentType());
            if (riskDoc.getMember() != null) {
                riskDoc.setMember(riskDoc.getMember());
            }
            if (riskDoc.getRiskId() != null) {
                riskDoc.setRisk(riskTransRepo.QueryRiskTrans(riskDoc.getRiskId()));
            }
            riskDocsRepo.save(riskDoc);
            String folderName = uploadFolder + "/" + batchNo + "/" + riskId;
            File file = new File(folderName);
            if (!file.exists())
                FileUtils.forceMkdir(file);
            Path path = Paths.get(uploadFolder + "/" + batchNo + "/" + riskId + "/" + uploadBean.getFile().getOriginalFilename());
            Files.write(path, bytes);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void uploadClientDocument(UploadBean uploadBean) throws BadRequestException {
        if (uploadBean.getFile().isEmpty())
            throw new BadRequestException("Upload File is Empty...");
        if (uploadBean.getDocId() == null)
            throw new BadRequestException("Client Document ID cannot be null");
        String uploadFolder = paramService.getParameterString("APP_UPLOAD_FOLDER");
        ClientDocs clientDocs = clientDocsRepo.findOne(uploadBean.getDocId());
        ClientDef clientDef = clientDocs.getClientDef();
        String folderName = clientDef.getTenantNumber() + "_" + clientDef.getTenId();
        clientDocs.setUploadedFileName(uploadBean.getFile().getOriginalFilename());
        try {
            byte[] bytes = uploadBean.getFile().getBytes();
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(bytes);
            String hashString = new BigInteger(1, digest).toString(16);
            clientDocs.setCheckSum(hashString);
            clientDocs.setContentType(uploadBean.getFile().getContentType());
            clientDocs.setFileId(uploadBean.getFileId());
            clientDocs.setClientDef(clientDef);
            clientDocsRepo.save(clientDocs);
            String folderPath = uploadFolder + "/" + folderName;
            File file = new File(folderPath);
            if (!file.exists())
                FileUtils.forceMkdir(file);
            Path path = Paths.get(folderPath + "/" + uploadBean.getFile().getOriginalFilename());
            Files.write(path, bytes);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public void sybrinCreateCase(UploadBean uploadBean, String docType) throws BadRequestException {
        if (uploadBean.getFile().isEmpty())
            throw new BadRequestException("Uploaded File is Empty...");
        if (uploadBean.getDocId() == null)
            throw new BadRequestException("Document ID cannot be null");
        try {
            byte[] bytes = uploadBean.getFile().getBytes();
            String base64Data = Base64.getEncoder().encodeToString(bytes);
            CaseCreation caseCreation = new CaseCreation();
            List<CaseFields> caseFields = new ArrayList<>();
            DocumentSubjectReference documentSubjectReference = new DocumentSubjectReference();
            String transactionId = "";
            List<DocumentContent> documentContent = new ArrayList<>();
            List<Documents> documents = new ArrayList<>();
            DocumentFormat documentFormat = new DocumentFormat();
            DocumentContent docContent = new DocumentContent();
            Documents docs = new Documents();
            CaseFields caseField = new CaseFields();
            SybrinCases sybrinCases = new SybrinCases();
            Long caseRequiredDocId;
            SybrinCases caseExists;
            SybrinCases created = new SybrinCases();
            ClientDocs clientDocs = null;
            ClientDocs prospectDocs = null;
            RiskDocs riskDoc = null;
            AccountsDocs accountsDocs = null;

            if (docType.equalsIgnoreCase("Client")) {
                clientDocs = clientDocsRepo.findOne(uploadBean.getDocId());
                ClientDef clientDef = clientDocs.getClientDef();
                caseRequiredDocId = clientDocs.getRequiredDoc().getReqId();
                caseExists = sybrinCasesRepo.findOne(QSybrinCases.sybrinCases.caseClientId.eq(clientDef.getTenId()).and(QSybrinCases.sybrinCases.caseRequiredDocId.eq(caseRequiredDocId)));
                caseField.setDescription("Client Id");
                caseField.setName("clientId");
                caseField.setRequired(true);
                caseField.setValue(clientDef.getIdNo());
                sybrinCases.setCaseClientId(clientDef.getTenId());
                transactionId = "BANCA-CLIENT";
                clientDocs.setClientDef(clientDef);

            } else if (docType.equalsIgnoreCase("Prospect")) {
                prospectDocs = clientDocsRepo.findOne(uploadBean.getDocId());
                ProspectDef prospectDef = prospectDocs.getProspectDef();
                caseRequiredDocId = prospectDocs.getRequiredDoc().getReqId();
                caseExists = sybrinCasesRepo.findOne(QSybrinCases.sybrinCases.caseProspectId.eq(prospectDef.getTenId()).and(QSybrinCases.sybrinCases.caseRequiredDocId.eq(caseRequiredDocId)));
                caseField.setDescription("Prospect Id");
                caseField.setName("prospectId");
                caseField.setRequired(true);
                caseField.setValue(prospectDef.getIdNo());
                sybrinCases.setCaseProspectId(prospectDef.getTenId());
                transactionId = "BANCA-PROSPECT";
                prospectDocs.setProspectDef(prospectDef);
            } else if (docType.equalsIgnoreCase("Risk")) {
                riskDoc = riskDocsRepo.findOne(QRiskDocs.riskDocs.rdId.eq(uploadBean.getDocId()));
                RiskTrans riskTrans = riskDoc.getRisk();
                caseRequiredDocId = riskDoc.getReqdDocs().getRequiredDoc().getReqId();
                caseExists = sybrinCasesRepo.findOne(QSybrinCases.sybrinCases.caseRiskId.eq(riskTrans.getRiskId()).and(QSybrinCases.sybrinCases.caseRequiredDocId.eq(caseRequiredDocId)));
                caseField.setDescription("Risk Id");
                caseField.setName("riskId");
                caseField.setRequired(true);
                caseField.setValue("RISK " + riskTrans.getRiskId().toString());
                sybrinCases.setCaseRiskId(riskTrans.getRiskId());
                transactionId = "BANCA-RISK";

            } else if (docType.equalsIgnoreCase("Account")) {
                accountsDocs = accountDocsRepo.findOne(uploadBean.getDocId());
                AccountDef accountDef = accountsDocs.getAccountDef();
                caseRequiredDocId = accountsDocs.getRequiredDoc().getReqId();

                caseExists = sybrinCasesRepo.findOne(QSybrinCases.sybrinCases.caseAcctId.eq(accountDef.getAcctId()).and(QSybrinCases.sybrinCases.caseRequiredDocId.eq(caseRequiredDocId)));
                caseField.setDescription("Account Id");
                caseField.setName("accountId");
                caseField.setRequired(true);
                caseField.setValue(accountDef.getPinNo());
                sybrinCases.setCaseAcctId(accountDef.getAcctId());
                transactionId = "BANCA-ACCOUNT";
                accountsDocs.setAccountDef(accountDef);
            } else {
                throw new BadRequestException("Invalid document type");
            }

            if (caseExists != null) {
                throw new BadRequestException("Document already exists");
            } else {
                sybrinCases.setCaseRequiredDocId(caseRequiredDocId);
                sybrinCases.setCaseFileName(uploadBean.getFile().getOriginalFilename());
                caseFields.add(caseField);

                docContent.setBase64Data(base64Data);
                documentFormat.setContentFileName(uploadBean.getFile().getOriginalFilename());
                documentFormat.setContentFileExtension(FilenameUtils.getExtension(uploadBean.getFile().getOriginalFilename()));
                docContent.setDocumentFormat(documentFormat);
                documentContent.add(docContent);

                // #TODO: Replaced hardcoded document type value with getDocumentTypeValue method
                String fileExtension = FilenameUtils.getExtension(uploadBean.getFile().getOriginalFilename());
                String documentTypeValue = getDocumentTypeValue(fileExtension);

                docs.setFields(caseFields);
                docs.setDocumentContents(documentContent);
                docs.setDocumentTypeValue(documentTypeValue);
                documents.add(docs);

                documentSubjectReference.setCaseType("280");
                caseCreation.setCaseFields(caseFields);
                caseCreation.setDocumentSubjectReference(documentSubjectReference);
                caseCreation.setTransactionId(transactionId);
                caseCreation.setDocuments(documents);

                created = createCase(caseCreation, sybrinCases);
            }

            if (created.getCaseNumber() != null) {
                if (clientDocs != null) {
                    clientDocs.setUploadedFileName(uploadBean.getFile().getOriginalFilename());
                    clientDocs.setContentType(uploadBean.getFile().getContentType());
                    clientDocs.setFileId(uploadBean.getFileId());
                    clientDocs.setCreationDate(new Date());
                    clientDocs.setInitiator(userUtils.getCurrentUser().getUsername());

                    clientDocsRepo.save(clientDocs);
                } else if (prospectDocs != null) {
                    prospectDocs.setUploadedFileName(uploadBean.getFile().getOriginalFilename());
                    prospectDocs.setContentType(uploadBean.getFile().getContentType());
                    prospectDocs.setFileId(uploadBean.getFileId());
                    prospectDocs.setCreationDate(new Date());
                    prospectDocs.setInitiator(userUtils.getCurrentUser().getUsername());
                    clientDocsRepo.save(prospectDocs);
                } else if (riskDoc != null) {
                    riskDoc.setUploadedFileName(uploadBean.getFile().getOriginalFilename());
                    riskDoc.setContentType(uploadBean.getFile().getContentType());
                    if (riskDoc.getMember() != null) {
                        riskDoc.setMember(riskDoc.getMember());
                    }
                    if (riskDoc.getRiskId() != null) {
                        riskDoc.setRisk(riskTransRepo.QueryRiskTrans(riskDoc.getRiskId()));
                    }
                    riskDoc.setCreationDate(new Date());
                    riskDoc.setInitiator(userUtils.getCurrentUser().getUsername());
                    riskDocsRepo.save(riskDoc);
                } else {
                    accountsDocs.setUploadedFileName(uploadBean.getFile().getOriginalFilename());
                    accountsDocs.setContentType(uploadBean.getFile().getContentType());
                    accountsDocs.setCreationDate(new Date());
                    accountsDocs.setInitiator(userUtils.getCurrentUser().getUsername());
                    accountDocsRepo.save(accountsDocs);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private SybrinCases createCase(CaseCreation caseCreation, SybrinCases sybrinCases) throws IOException {
        try {
            String paramValue = paramService.getParameterString("API_INTEGRATION_URL") + "/api/v1/main/sybrin/sybrin/createCase";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<?> requestEntity = new HttpEntity<>(caseCreation, headers);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(paramValue, requestEntity, String.class);
            String responseBody = responseEntity.getBody();
            System.out.println("SYBRIN RESPONSE: " + responseBody);
            JsonNode rootNode = objectMapper.readTree(responseBody);
            if (rootNode.get("code").asText().equals("200") && rootNode.get("status").asText().equalsIgnoreCase("Success")) {
                JsonNode dataNode = rootNode.get("data");
                String caseNumber = dataNode.get("documentSubjectReference").get("caseNumber").asText();
                JsonNode document = dataNode.get("documents");
                if (document.isArray()) {
                    for (JsonNode doc : document) {
                        String documentGuid = doc.get("documentName").get("documentGuid").asText();
                        sybrinCases.setDocumentGuid(documentGuid);
                    }
                }
                sybrinCases.setCaseNumber(caseNumber);
                return sybrinCasesRepo.save(sybrinCases);
            } else {
                throw new BadRequestException(rootNode.get("message").asText());
            }
        } catch (BadRequestException | JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private SybrinCases updateCase(CaseUpdate caseUpdate, SybrinCases sybrinCases) {
        try {
            String paramValue = paramService.getParameterString("API_INTEGRATION_URL") + "/sybrin/updateCase";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<?> requestEntity = new HttpEntity<>(caseUpdate, headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(paramValue, HttpMethod.PUT, requestEntity, String.class);
            String responseBody = responseEntity.getBody();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            if (rootNode.get("code").asText().equals("200") && rootNode.get("status").asText().equalsIgnoreCase("Success")) {
                JsonNode dataNode = rootNode.get("data");
                String caseNumber = dataNode.get("documentSubjectReference").get("caseNumber").asText();
                JsonNode document = dataNode.get("documents");
                if (document.isArray()) {
                    for (JsonNode doc : document) {
                        String documentGuid = doc.get("documentName").get("documentGuid").asText();
                        sybrinCases.setDocumentGuid(documentGuid);
                    }
                }
                sybrinCases.setCaseNumber(caseNumber);
                return sybrinCasesRepo.save(sybrinCases);
            } else {
                throw new BadRequestException(rootNode.get("message").asText());
            }
        } catch (BadRequestException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] sybrinDocumentDetails(String docType, Long reqDocId, Long caseTypeId) throws BadRequestException {
        byte[] arr = new byte[0];
        DocumentDetails documentDetails = new DocumentDetails();
        List<DocDetails> docDetailsList = new ArrayList<>();
        DocDetails docDetails = new DocDetails();
        DocumentName documentName = new DocumentName();
        String documentGuid = "";
        String caseFileName = "";
        SybrinCases sybrinCases;
        switch (docType) {
            case "Client":
                sybrinCases = sybrinCasesRepo.findOne(QSybrinCases.sybrinCases.caseRequiredDocId.eq(reqDocId).and(QSybrinCases.sybrinCases.caseClientId.eq(caseTypeId)));
                documentGuid = sybrinCases.getDocumentGuid();
                caseFileName = sybrinCases.getCaseFileName();
                break;
            case "Prospect":
                sybrinCases = sybrinCasesRepo.findOne(QSybrinCases.sybrinCases.caseRequiredDocId.eq(reqDocId).and(QSybrinCases.sybrinCases.caseProspectId.eq(caseTypeId)));
                documentGuid = sybrinCases.getDocumentGuid();
                caseFileName = sybrinCases.getCaseFileName();
                break;
            case "Risk":
                log.debug("Fetching Risk document case with reqDocId: {} and caseTypeId: {}", reqDocId, caseTypeId);
                sybrinCases = sybrinCasesRepo.findOne(QSybrinCases.sybrinCases.caseRequiredDocId.eq(reqDocId)
                        .and(QSybrinCases.sybrinCases.caseRiskId.eq(caseTypeId)));
                if (sybrinCases == null) {
                    log.error("Risk document case not found for reqDocId: {} and caseTypeId: {}", reqDocId, caseTypeId);
                    throw new BadRequestException("Risk document case not found");
                }
                log.debug("Found SybrinCase: {}", sybrinCases.getCaseNumber());

                documentGuid = sybrinCases.getDocumentGuid();
                if (documentGuid == null || documentGuid.isEmpty()) {
                    log.error("Document GUID not found for risk document. Case number: {}", sybrinCases.getCaseNumber());
                    throw new BadRequestException("Document GUID not found for risk document");
                }
                log.debug("Retrieved documentGuid: {}", documentGuid);

                caseFileName = sybrinCases.getCaseFileName();
                if (caseFileName == null || caseFileName.isEmpty()) {
                    log.error("Case file name not found for risk document. Case number: {}", sybrinCases.getCaseNumber());
                    throw new BadRequestException("Case file name not found for risk document");
                }
                log.debug("Retrieved caseFileName: {}", caseFileName);
                break;
            case "Account":
                sybrinCases = sybrinCasesRepo.findOne(QSybrinCases.sybrinCases.caseRequiredDocId.eq(reqDocId).and(QSybrinCases.sybrinCases.caseAcctId.eq(caseTypeId)));
                documentGuid = sybrinCases.getDocumentGuid();
                caseFileName = sybrinCases.getCaseFileName();
                break;
            case "Claim Gen Doc":
                sybrinCases = sybrinCasesRepo.findOne(QSybrinCases.sybrinCases.caseClaimId.eq(caseTypeId).and(QSybrinCases.sybrinCases.caseRequiredDocId.isNull()));
                documentGuid = sybrinCases.getDocumentGuid();
                caseFileName = sybrinCases.getCaseFileName();
                break;
            case "Claim Req Doc":
                ClaimRequiredDocs reqDoc = claimRequiredDocsRepo.findOne(reqDocId);
                if (reqDoc == null) {
                    throw new BadRequestException("Document not found with ID: " + reqDocId);
                }
                sybrinCases = sybrinCasesRepo.findOne(
                        QSybrinCases.sybrinCases.caseClaimId.eq(caseTypeId)
                                .and(QSybrinCases.sybrinCases.caseRequiredDocId.eq(reqDoc.getRequiredDoc().getReqId()))
                );
                if (sybrinCases == null) {
                    throw new BadRequestException("Case not found for claim ID: " + caseTypeId + " and document ID: " + reqDocId);
                }
                documentGuid = sybrinCases.getDocumentGuid();
                caseFileName = sybrinCases.getCaseFileName();
                break;
            default:
                throw new BadRequestException("Invalid document type");
        }
        documentName.setDocumentGuid(documentGuid);
        docDetails.setDocumentName(documentName);
        docDetails.setRequired(false);
        // #TODO: Replaced hardcoded document type value with getDocumentTypeValue method
        String fileExtension = FilenameUtils.getExtension(caseFileName);
        String documentTypeValue = getDocumentTypeValue(fileExtension);
        docDetails.setDocumentTypeValue(documentTypeValue);
        docDetailsList.add(docDetails);
        documentDetails.setDocuments(docDetailsList);
        try {
            String paramValue = paramService.getParameterString("API_INTEGRATION_URL") + "/api/v1/main/sybrin/sybrin/retrieveDocumentDetails";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<?> requestEntity = new HttpEntity<>(documentDetails, headers);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(paramValue, requestEntity, String.class);
            String responseBody = responseEntity.getBody();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            String base64Data = "";
            String extension = "";
            if (rootNode.get("status").asText().equalsIgnoreCase("Success")) {
                if (rootNode.get("message").asText().equalsIgnoreCase("Data Found")) {
                    JsonNode documentsNode = rootNode.get("data").get("documents");
                    if (documentsNode.isArray()) {
                        for (JsonNode document : documentsNode) {
                            JsonNode docContents = document.get("documentContents");
                            if (docContents.isArray()) {
                                for (JsonNode docContent : docContents) {
                                    base64Data = docContent.get("base64Data").asText();
                                    System.out.println("base64Data: " + base64Data);
                                }
                            }
                        }
                    }
                    arr = Base64.getDecoder().decode(base64Data);
                    System.out.println("Content Length: " + arr.length);
                } else if (rootNode.get("message").asText().equalsIgnoreCase("Data Not Found")) {
                    throw new BadRequestException(rootNode.get("message").asText());
                } else {
                    throw new BadRequestException(rootNode.get("message").asText());
                }
            }
            return arr;

        } catch (BadRequestException | RestClientException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void uploadProspectDocument(UploadBean uploadBean) throws BadRequestException {
        if (uploadBean.getFile().isEmpty())
            throw new BadRequestException("Upload File is Empty...");
        if (uploadBean.getDocId() == null)
            throw new BadRequestException("Client Document ID cannot be null");
        String uploadFolder = paramService.getParameterString("APP_UPLOAD_FOLDER");
        ClientDocs clientDocs = clientDocsRepo.findOne(uploadBean.getDocId());
        ProspectDef clientDef = clientDocs.getProspectDef();
        String folderName = clientDef.getProspShtDesc() + "_" + clientDef.getTenId();
        clientDocs.setUploadedFileName(uploadBean.getFile().getOriginalFilename());
        try {
            byte[] bytes = uploadBean.getFile().getBytes();
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(bytes);
            String hashString = new BigInteger(1, digest).toString(16);
            clientDocs.setCheckSum(hashString);
            clientDocs.setContentType(uploadBean.getFile().getContentType());
            clientDocs.setProspectDef(clientDef);
            clientDocsRepo.save(clientDocs);
            String folderPath = uploadFolder + "/" + folderName;
            File file = new File(folderPath);
            if (!file.exists())
                FileUtils.forceMkdir(file);
            Path path = Paths.get(folderPath + "/" + uploadBean.getFile().getOriginalFilename());
            Files.write(path, bytes);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void uploadAccountDocument(UploadBean uploadBean) throws BadRequestException {
        if (uploadBean.getFile().isEmpty())
            throw new BadRequestException("Upload File is Empty...");
        if (uploadBean.getDocId() == null)
            throw new BadRequestException("Account Document ID cannot be null");
        String uploadFolder = paramService.getParameterString("APP_UPLOAD_FOLDER");
        AccountsDocs accountsDocs = accountDocsRepo.findOne(uploadBean.getDocId());
        AccountDef accountDef = accountsDocs.getAccountDef();
        String folderName = accountDef.getShtDesc() + "_" + accountDef.getAcctId();
        accountsDocs.setUploadedFileName(uploadBean.getFile().getOriginalFilename());
        try {
            byte[] bytes = uploadBean.getFile().getBytes();
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(bytes);
            String hashString = new BigInteger(1, digest).toString(16);
            accountsDocs.setCheckSum(hashString);
            accountsDocs.setContentType(uploadBean.getFile().getContentType());
            accountsDocs.setAccountDef(accountDef);
            accountDocsRepo.save(accountsDocs);
            String folderPath = uploadFolder + "/" + folderName;
            File file = new File(folderPath);
            if (!file.exists())
                FileUtils.forceMkdir(file);
            Path path = Paths.get(folderPath + "/" + uploadBean.getFile().getOriginalFilename());
            Files.write(path, bytes);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new BadRequestException("Error uploading document: " + e.getMessage());
        }
    }

    @Override
    @Modifying
    @Transactional(readOnly = true)
    public byte[] getRiskDocFileDetails(Long rdocId) throws BadRequestException {
        byte[] arr = new byte[0];
        if (rdocId == null)
            throw new BadRequestException("Document does not exist...");
        RiskDocs riskDoc = riskDocsRepo.getRiskDocsVal(rdocId);
        String uploadFolder = paramService.getParameterString("APP_UPLOAD_FOLDER");
        String batchNo = "";
        if (riskDoc.getRiskId() != null)
            batchNo = "BATCH_" + String.valueOf(riskDoc.getPolId());
        else if (riskDoc.getMember() != null)
            batchNo = "BATCH_" + String.valueOf(riskDoc.getPolId());
        String riskId = "";
        if (riskDoc.getRiskId() != null)
            riskId = "RSK_" + String.valueOf(riskDoc.getRiskId());
        else if (riskDoc.getMember() != null)
            riskId = "MEM_" + String.valueOf(riskDoc.getMember().getMemberShipNo());
        String uploadedName = riskDoc.getUploadedFileName();
        Path path = Paths.get(uploadFolder + "/" + batchNo + "/" + riskId + "/" + uploadedName);
        if (path.toFile().exists()) {
            try {
                arr = Files.readAllBytes(path);
            } catch (IOException e) {
                throw new BadRequestException(e.getMessage());
            }
        }
        return arr;
    }

    @Override
    public byte[] getAccountDocument(Long adId) throws BadRequestException {
        byte[] arr = new byte[0];
        if (adId == null)
            throw new BadRequestException("Document does not exist...");
        AccountsDocs accountsDoc = accountDocsRepo.findOne(adId);
        String uploadFolder = paramService.getParameterString("APP_UPLOAD_FOLDER");
        String uploadedName = accountsDoc.getUploadedFileName();
        AccountDef accountDef = accountsDoc.getAccountDef();
        String folderName = accountDef.getShtDesc() + "_" + accountDef.getAcctId();
        Path path = Paths.get(uploadFolder + "/" + folderName + "/" + uploadedName);
        if (path.toFile().exists()) {
            try {
                arr = Files.readAllBytes(path);
            } catch (IOException e) {
                throw new BadRequestException(e.getMessage());
            }
        }
        return arr;
    }

    @Override
    public String getAcctDocumentType(Long adId) throws BadRequestException {
        if (adId == null)
            throw new BadRequestException("Document does not exist...");
        AccountsDocs accountsDoc = accountDocsRepo.findOne(adId);
        return accountsDoc.getContentType();
    }

    @Override
    public byte[] getClientDocument(Long adId) throws BadRequestException {
        byte[] arr = new byte[0];
        if (adId == null)
            throw new BadRequestException("Document does not exist...");
        ClientDocs clientDocs = clientDocsRepo.findOne(adId);
        String uploadFolder = paramService.getParameterString("APP_UPLOAD_FOLDER");
        String uploadedName = clientDocs.getUploadedFileName();
        String folderName = "";
        if (clientDocs.getProspectDef() != null) {
            ProspectDef clientDef = clientDocs.getProspectDef();
            folderName = clientDef.getProspShtDesc() + "_" + clientDef.getTenId();
        } else {
            ClientDef clientDef = clientDocs.getClientDef();
            folderName = clientDef.getTenantNumber() + "_" + clientDef.getTenId();
        }
        Path path = Paths.get(uploadFolder + "/" + folderName + "/" + uploadedName);
        if (path.toFile().exists()) {
            try {
                arr = Files.readAllBytes(path);
            } catch (IOException e) {
                throw new BadRequestException(e.getMessage());
            }
        }
        return arr;
    }

    @Override
    public byte[] getProspectDocument(Long adId) throws BadRequestException {
        byte[] arr = new byte[0];
        if (adId == null)
            throw new BadRequestException("Document does not exist...");
        ClientDocs clientDocs = clientDocsRepo.findOne(adId);
        String uploadFolder = paramService.getParameterString("APP_UPLOAD_FOLDER");
        String uploadedName = clientDocs.getUploadedFileName();
        ProspectDef clientDef = clientDocs.getProspectDef();
        String folderName = clientDef.getProspShtDesc() + "_" + clientDef.getTenId();
        Path path = Paths.get(uploadFolder + "/" + folderName + "/" + uploadedName);
        if (path.toFile().exists()) {
            try {
                arr = Files.readAllBytes(path);
            } catch (IOException e) {
                throw new BadRequestException(e.getMessage());
            }
        }
        return arr;
    }

    @Override
    public String getClientDocumentType(Long adId) throws BadRequestException {
        if (adId == null)
            throw new BadRequestException("Document does not exist...");
        ClientDocs clientDocs = clientDocsRepo.findOne(adId);
        return clientDocs.getContentType();
    }

    @Override
    public byte[] getMedClmDocFileDetails(Long rdocId) throws BadRequestException {
        byte[] arr = new byte[0];
        if (rdocId == null)
            throw new BadRequestException("Document does not exist...");
        MedParReqDocs parReqDocs = parDocsRepo.findOne(rdocId);
        String uploadFolder = paramService.getParameterString("APP_UPLOAD_FOLDER");
        String folderName = uploadFolder + "/MED_" + parReqDocs.getParTrans().getParId();
        Path path = Paths.get(folderName + "/" + parReqDocs.getUploadedFileName());
        if (path.toFile().exists()) {
            try {
                arr = Files.readAllBytes(path);
            } catch (IOException e) {
                throw new BadRequestException(e.getMessage());
            }
        }
        return arr;
    }

    @Override
    public String getClmDocContentTyoe(Long docId) throws BadRequestException {
        if (docId == null)
            throw new BadRequestException("Document does not exist...");
        MedParReqDocs parReqDocs = parDocsRepo.findOne(docId);
        return parReqDocs.getContentType();
    }

    @Override
    public byte[] getGeneralClaimDoc(Long uploadId) throws BadRequestException {
        byte[] arr = new byte[0];
        if (uploadId == null)
            throw new BadRequestException("Document does not exist...");
        ClaimUploads upload = uploadRepo.findOne(uploadId);
        String uploadFolder = paramService.getParameterString("APP_UPLOAD_FOLDER");
        String folderName = uploadFolder + "/" + upload.getClaimBookings().getClaimNo();
        Path path = Paths.get(folderName + "/" + upload.getFileName());
        if (path.toFile().exists()) {
            try {
                arr = Files.readAllBytes(path);
            } catch (IOException e) {
                throw new BadRequestException(e.getMessage());
            }
        }
        return arr;
    }

    @Override
    public byte[] getRequiredClaimDoc(Long clmRequiredId) throws BadRequestException {
        byte[] arr = new byte[0];
        if (clmRequiredId == null)
            throw new BadRequestException("Document does not exist...");
        ClaimRequiredDocs upload = claimRequiredDocsRepo.findOne(clmRequiredId);
        String uploadFolder = paramService.getParameterString("APP_UPLOAD_FOLDER");
        String folderName = uploadFolder + "/" + upload.getClaimBookings().getClaimNo();
        Path path = Paths.get(folderName + "/" + upload.getFileName());
        if (path.toFile().exists()) {
            try {
                arr = Files.readAllBytes(path);
            } catch (IOException e) {
                throw new BadRequestException(e.getMessage());
            }
        }
        return arr;
    }

    @Override
    @Transactional(readOnly = true)
    public String getDocContentType(Long docId) throws BadRequestException {
        if (docId == null)
            throw new BadRequestException("Document does not exist...");
        RiskDocs riskDoc = riskDocsRepo.getRiskDocsVal(docId);
        return riskDoc.getContentType();
    }

    @Override
    public String getGeneralClmContentType(Long docId) throws BadRequestException {
        if (docId == null)
            throw new BadRequestException("Document does not exist...");
        ClaimUploads upload = uploadRepo.findOne(docId);
        return upload.getContentType();
    }

    @Override
    public String getReqClmContentType(Long docId) throws BadRequestException {
        if (docId == null)
            throw new BadRequestException("Document does not exist...");
        ClaimRequiredDocs upload = claimRequiredDocsRepo.findOne(docId);
        return upload.getContentType();
    }

    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void deleteRiskDoc(Long docId) throws BadRequestException {
        if (docId == null)
            throw new BadRequestException("Document does not exist...");
        RiskDocs riskDoc = riskDocsRepo.findOne(QRiskDocs.riskDocs.rdId.eq(docId));

        BooleanExpression binderDetCondition = riskDoc.getBinderDetId() != null
                ? QBinderReqrdDocs.binderReqrdDocs.binderDetail.detId.eq(riskDoc.getBinderDetId())
                : null;

        long count = reqrdDocsRepo.count(QBinderReqrdDocs.binderReqrdDocs.requiredDocs.sclReqrdId.eq(riskDoc.getReqdDocs().getSclReqrdId())
                .and(binderDetCondition)
                .and(QBinderReqrdDocs.binderReqrdDocs.mandatory.eq(true)));
        if (count > 0)
            throw new BadRequestException("Cannot delete Binder mandatory document...");
        String uploadFolder = paramService.getParameterString("APP_UPLOAD_FOLDER");
        String batchNo = "";
        if (riskDoc.getRiskId() != null)
            batchNo = "BATCH_" + String.valueOf(riskDoc.getPolId());
        else if (riskDoc.getMember() != null)
            batchNo = "BATCH_" + String.valueOf(riskDoc.getPolId());
        String riskId = "";
        if (riskDoc.getRisk() != null)
            riskId = "RSK_" + String.valueOf(riskDoc.getRiskId());
        else if (riskDoc.getMember() != null)
            riskId = "MEM_" + String.valueOf(riskDoc.getMember().getMemberShipNo());
        String uploadedName = riskDoc.getUploadedFileName();
        Path path = Paths.get(uploadFolder + "/" + batchNo + "/" + riskId + "/" + uploadedName);
        if (path.toFile().exists()) {
            try {
                Files.delete(path);
            } catch (IOException e) {
                throw new BadRequestException(e.getMessage());
            }
        }
        SybrinCases sybrinCases = sybrinCasesRepo.findOne(QSybrinCases.sybrinCases.caseRequiredDocId.eq(riskDoc.getReqdDocs().getRequiredDoc().getReqId()).and(QSybrinCases.sybrinCases.caseRiskId.eq(riskDoc.getRisk().getRiskId())));
        if (sybrinCases != null) {
            sybrinCasesRepo.delete(sybrinCases);
        }
        riskDocsRepo.deleteRiskDocs(docId);
    }

    @Override
    public void deleteAcctDocument(Long adId) throws BadRequestException {
        if (adId == null)
            throw new BadRequestException("Document does not exist...");
        AccountsDocs accountsDoc = accountDocsRepo.findOne(adId);
        String uploadFolder = paramService.getParameterString("APP_UPLOAD_FOLDER");
        String uploadedName = accountsDoc.getUploadedFileName();
        AccountDef accountDef = accountsDoc.getAccountDef();
        String folderName = accountDef.getShtDesc() + "_" + accountDef.getAcctId();
        Path path = Paths.get(uploadFolder + "/" + folderName + "/" + uploadedName);
        if (path.toFile().exists()) {
            try {
                Files.delete(path);
            } catch (IOException e) {
                throw new BadRequestException(e.getMessage());
            }
        }
        SybrinCases sybrinCases = sybrinCasesRepo.findOne(QSybrinCases.sybrinCases.caseRequiredDocId.eq(accountsDoc.getRequiredDoc().getReqId()).and(QSybrinCases.sybrinCases.caseAcctId.eq(accountsDoc.getAccountDef().getAcctId())));
        if (sybrinCases != null) {
            sybrinCasesRepo.delete(sybrinCases);
        }
        accountDocsRepo.delete(adId);
    }

    @Override
    public void deleteClntDocument(Long adId) throws BadRequestException {
        if (adId == null)
            throw new BadRequestException("Document does not exist...");
        ClientDocs clientDocs = clientDocsRepo.findOne(adId);
        String uploadFolder = paramService.getParameterString("APP_UPLOAD_FOLDER");
        String uploadedName = clientDocs.getUploadedFileName();
        ClientDef clientDef = clientDocs.getClientDef();
        String folderName = clientDef.getTenantNumber() + "_" + clientDef.getTenId();
        Path path = Paths.get(uploadFolder + "/" + folderName + "/" + uploadedName);
        if (path.toFile().exists()) {
            try {
                Files.delete(path);
            } catch (IOException e) {
                throw new BadRequestException(e.getMessage());
            }
        }
        SybrinCases sybrinCases = sybrinCasesRepo.findOne(QSybrinCases.sybrinCases.caseRequiredDocId.eq(clientDocs.getRequiredDoc().getReqId()).and(QSybrinCases.sybrinCases.caseClientId.eq(clientDef.getTenId())));
        if (sybrinCases != null) {
            sybrinCasesRepo.delete(sybrinCases);
        }
        clientDocsRepo.delete(adId);
    }

    @Override
    public void deletePrspctDocument(Long adId) throws BadRequestException {
        if (adId == null)
            throw new BadRequestException("Document does not exist...");
        ClientDocs clientDocs = clientDocsRepo.findOne(adId);
        String uploadFolder = paramService.getParameterString("APP_UPLOAD_FOLDER");
        String uploadedName = clientDocs.getUploadedFileName();
        ProspectDef clientDef = clientDocs.getProspectDef();
        String folderName = clientDef.getProspShtDesc() + "_" + clientDef.getTenId();
        Path path = Paths.get(uploadFolder + "/" + folderName + "/" + uploadedName);
        if (path.toFile().exists()) {
            try {
                Files.delete(path);
            } catch (IOException e) {
                throw new BadRequestException(e.getMessage());
            }
        }
        SybrinCases sybrinCases = sybrinCasesRepo.findOne(QSybrinCases.sybrinCases.caseRequiredDocId.eq(clientDocs.getRequiredDoc().getReqId()).and(QSybrinCases.sybrinCases.caseProspectId.eq(clientDef.getTenId())));
        if (sybrinCases != null) {
            sybrinCasesRepo.delete(sybrinCases);
        }
        clientDocsRepo.delete(adId);
    }

    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void deleteClmDoc(Long docId) throws BadRequestException {
        if (docId == null)
            throw new BadRequestException("Document does not exist...");
        MedParReqDocs parReqDocs = parDocsRepo.findOne(docId);
        if (parReqDocs.getReqdDocs().isMandatory())
            throw new BadRequestException("Cannot delete mandatory document...");
        String uploadFolder = paramService.getParameterString("APP_UPLOAD_FOLDER");
        String folderName = uploadFolder + "/MED_" + parReqDocs.getParTrans().getParId();
        String uploadedName = parReqDocs.getUploadedFileName();
        Path path = Paths.get(folderName + "/" + uploadedName);
        if (path.toFile().exists()) {
            try {
                Files.delete(path);
            } catch (IOException e) {
                throw new BadRequestException(e.getMessage());
            }
        }
        parDocsRepo.delete(docId);
    }

    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void deleteClmReqDoc(Long docId) throws BadRequestException {
        if (docId == null)
            throw new BadRequestException("Document does not exist...");
        ClaimRequiredDocs reqDocs = claimRequiredDocsRepo.findOne(docId);
        if (requiredDocsRepo.count(QSubClassReqdDocs.subClassReqdDocs.requiredDoc.reqId.eq(reqDocs.getRequiredDoc().getReqId())
                .and(QSubClassReqdDocs.subClassReqdDocs.requiredDoc.appliesLossOpening.eq(true))
                .and(QSubClassReqdDocs.subClassReqdDocs.mandatory.eq(true))) != 0)
            throw new BadRequestException("Cannot delete mandatory document...");

        String uploadFolder = paramService.getParameterString("APP_UPLOAD_FOLDER");
        String folderName = uploadFolder + "/" + reqDocs.getClaimBookings().getClaimNo();
        String uploadedName = reqDocs.getFileName();
        Path path = Paths.get(folderName + "/" + uploadedName);
        if (path.toFile().exists()) {
            try {
                Files.delete(path);
            } catch (IOException e) {
                throw new BadRequestException(e.getMessage());
            }
        }
        SybrinCases sybrinCases = sybrinCasesRepo.findOne(QSybrinCases.sybrinCases.caseRequiredDocId.eq(reqDocs.getRequiredDoc().getReqId()).and(QSybrinCases.sybrinCases.caseClaimId.eq(reqDocs.getClaimBookings().getClmId())));
        if (sybrinCases != null) {
            sybrinCasesRepo.delete(sybrinCases);
        }
        claimRequiredDocsRepo.delete(docId);
    }

    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void deleteClmUploadDoc(Long docId) throws BadRequestException {
        if (docId == null)
            throw new BadRequestException("Document does not exist...");
        ClaimUploads uploadDocs = uploadRepo.findOne(docId);

        String uploadFolder = paramService.getParameterString("APP_UPLOAD_FOLDER");
        String folderName = uploadFolder + "/" + uploadDocs.getClaimBookings().getClaimNo();
        String uploadedName = uploadDocs.getFileName();
        Path path = Paths.get(folderName + "/" + uploadedName);
        if (path.toFile().exists()) {
            try {
                Files.delete(path);
            } catch (IOException e) {
                throw new BadRequestException(e.getMessage());
            }
        }
        SybrinCases sybrinCases = sybrinCasesRepo.findOne(QSybrinCases.sybrinCases.caseRequiredDocId.isNull().and(QSybrinCases.sybrinCases.caseClaimId.eq(uploadDocs.getClaimBookings().getClmId())));
        if (sybrinCases != null) {
            sybrinCasesRepo.delete(sybrinCases);
        }
        uploadRepo.delete(docId);
    }

    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void uploadGeneralClaimDoc(ClaimUploads upload) throws BadRequestException {
        if (upload.getFile().isEmpty())
            throw new BadRequestException("Upload File is Empty...");
        if (upload.getClaimBookings() == null)
            throw new BadRequestException("Claim File cannot be null");
        ClaimBookings bookings = upload.getClaimBookings();
        try {
            byte[] bytes = upload.getFile().getBytes();
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(bytes);
            String hashString = new BigInteger(1, digest).toString(16);
            upload.setCheckSum(hashString);
            upload.setFileName(upload.getFile().getOriginalFilename());
            upload.setDateUploaded(new Date());
            upload.setUploadedBy(userUtils.getCurrentUser());
            upload.setContentType(upload.getFile().getContentType());

            String base64Data = Base64.getEncoder().encodeToString(bytes);
            CaseCreation caseCreation = new CaseCreation();
            List<CaseFields> caseFields = new ArrayList<>();
            DocumentSubjectReference documentSubjectReference = new DocumentSubjectReference();
            String transactionId = "";
            List<DocumentContent> documentContent = new ArrayList<>();
            List<Documents> documents = new ArrayList<>();
            DocumentFormat documentFormat = new DocumentFormat();
            DocumentContent docContent = new DocumentContent();
            Documents docs = new Documents();
            CaseFields caseField = new CaseFields();
            SybrinCases sybrinCases = new SybrinCases();

            SybrinCases caseExists = sybrinCasesRepo.findOne(QSybrinCases.sybrinCases.caseClaimId.eq(bookings.getClmId()).and(QSybrinCases.sybrinCases.caseRequiredDocId.isNull()));
            caseField.setDescription("Claim Booking Id");
            caseField.setName("claimId");
            caseField.setRequired(false);
            caseField.setValue(bookings.getClaimNo());
            caseFields.add(caseField);

            sybrinCases.setCaseClaimId(bookings.getClmId());
            sybrinCases.setCaseFileName(upload.getFile().getOriginalFilename());
            transactionId = "BANCA-CLIENT";

            docContent.setBase64Data(base64Data);
            documentFormat.setContentFileName(upload.getFile().getOriginalFilename());
            documentFormat.setContentFileExtension(FilenameUtils.getExtension(upload.getFile().getOriginalFilename()));
            docContent.setDocumentFormat(documentFormat);
            documentContent.add(docContent);

            docs.setFields(caseFields);
            docs.setDocumentContents(documentContent);
            // #TODO: Replaced hardcoded document type value with getDocumentTypeValue method
            String fileExtension = FilenameUtils.getExtension(upload.getFile().getOriginalFilename());
            String documentTypeValue = getDocumentTypeValue(fileExtension);
            docs.setDocumentTypeValue(documentTypeValue);
            documents.add(docs);

            documentSubjectReference.setCaseType("280");
            caseCreation.setCaseFields(caseFields);
            caseCreation.setDocumentSubjectReference(documentSubjectReference);
            caseCreation.setTransactionId(transactionId);
            caseCreation.setDocuments(documents);

            if (caseExists != null) {
                CaseUpdate caseUpdate = new CaseUpdate();
                caseUpdate.setAction("AddDocumentsToCase");
                caseUpdate.setTransactionId(transactionId);
                documentSubjectReference.setCaseNumber(caseExists.getCaseNumber());
                caseUpdate.setDocumentSubjectReference(documentSubjectReference);
                caseUpdate.setCaseFields(caseFields);
                caseUpdate.setDocuments(documents);
                updateCase(caseUpdate, sybrinCases);
            } else {
                createCase(caseCreation, sybrinCases);
            }
            uploadRepo.save(upload);
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void uploadClaimReqDoc(ClaimRequiredDocs newUpload) throws BadRequestException {
        if (("N").equalsIgnoreCase(newUpload.getTransType())) // N- upload OPTION , E - EDIT OPTION
        {
            if (newUpload.getFile().isEmpty())
                throw new BadRequestException("Upload File is Empty...");
        }
        if (newUpload.getClmRequiredId() == null) {
            throw new BadRequestException("No document record to update...");
        }
        String uploadFolder = paramService.getParameterString("APP_UPLOAD_FOLDER");
        ClaimRequiredDocs upload = claimRequiredDocsRepo.findOne(QClaimRequiredDocs.claimRequiredDocs.clmRequiredId.eq(newUpload.getClmRequiredId()));
        ClaimBookings bookings = upload.getClaimBookings();
        try {
            if (newUpload.getTransType().equalsIgnoreCase("N")) // N- upload OPTION , E - EDIT OPTION
            {
                if (!(newUpload.getFile().isEmpty())) {
                    byte[] bytes = newUpload.getFile().getBytes();
                    MessageDigest md5 = MessageDigest.getInstance("MD5");
                    byte[] digest = md5.digest(bytes);

                    String base64Data = Base64.getEncoder().encodeToString(bytes);
                    CaseCreation caseCreation = new CaseCreation();
                    List<CaseFields> caseFields = new ArrayList<>();
                    DocumentSubjectReference documentSubjectReference = new DocumentSubjectReference();
                    String transactionId = "";
                    List<DocumentContent> documentContent = new ArrayList<>();
                    List<Documents> documents = new ArrayList<>();
                    DocumentFormat documentFormat = new DocumentFormat();
                    DocumentContent docContent = new DocumentContent();
                    Documents docs = new Documents();
                    CaseFields caseField = new CaseFields();
                    SybrinCases sybrinCases = new SybrinCases();
                    SybrinCases created = new SybrinCases();

                    SybrinCases caseExists = sybrinCasesRepo.findOne(
                            QSybrinCases.sybrinCases.caseRequiredDocId.eq(upload.getRequiredDoc().getReqId())
                                    .and(QSybrinCases.sybrinCases.caseClaimId.eq(bookings.getClmId()))
                    );
                    caseField.setDescription("Claim Booking Id");
                    caseField.setName("claimNo");
                    caseField.setRequired(false);
                    caseField.setValue(bookings.getClaimNo());
                    caseFields.add(caseField);

                    sybrinCases.setCaseClaimId(bookings.getClmId());
                    sybrinCases.setCaseFileName(newUpload.getFile().getOriginalFilename());
                    sybrinCases.setCaseRequiredDocId(upload.getRequiredDoc().getReqId());
                    transactionId = "BANCA-CLAIMREQDOC";

                    docContent.setBase64Data(base64Data);
                    documentFormat.setContentFileName(newUpload.getFile().getOriginalFilename());
                    documentFormat.setContentFileExtension(FilenameUtils.getExtension(newUpload.getFile().getOriginalFilename()));
                    docContent.setDocumentFormat(documentFormat);
                    documentContent.add(docContent);

                    docs.setFields(caseFields);
                    docs.setDocumentContents(documentContent);
                    // #TODO: Replaced hardcoded document type value with getDocumentTypeValue method
                    String fileExtension = FilenameUtils.getExtension(newUpload.getFile().getOriginalFilename());
                    String documentTypeValue = getDocumentTypeValue(fileExtension);
                    docs.setDocumentTypeValue(documentTypeValue);
                    documents.add(docs);

                    documentSubjectReference.setCaseType("280");
                    caseCreation.setCaseFields(caseFields);
                    caseCreation.setDocumentSubjectReference(documentSubjectReference);
                    caseCreation.setTransactionId(transactionId);
                    caseCreation.setDocuments(documents);

                    if (caseExists != null) {
                        throw new BadRequestException("Document already exists");
                    } else {
                        created = createCase(caseCreation, sybrinCases);
                    }
                    if (created.getCaseNumber() != null) {
                        String hashString = new BigInteger(1, digest).toString(16);
                        upload.setCheckSum(hashString);
                        upload.setFileName(newUpload.getFile().getOriginalFilename());
                        upload.setDateReceived(new Date());
                        upload.setDateSubmitted(new Date());
                        upload.setContentType(newUpload.getFile().getContentType());
                        upload.setRemarks(newUpload.getRemarks());
                        upload.setDocRefNo(newUpload.getDocRefNo());
                        claimRequiredDocsRepo.save(upload);
                    }
                }
            } else {
                upload.setRemarks(newUpload.getRemarks());
                upload.setDocRefNo(newUpload.getDocRefNo());
                claimRequiredDocsRepo.save(upload);
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new BadRequestException("Error uploading document: " + e.getMessage());
        }
    }

    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void uploadClaimDocument(MedParReqDocs parReqDocs) throws BadRequestException {
        if (parReqDocs.getFile().isEmpty()) {
            throw new BadRequestException("Upload File is Empty..");
        }
        if (parReqDocs.getReqdDocs() == null)
            throw new BadRequestException("Select Document Type to be uploaded");
        String uploadFolder = paramService.getParameterString("APP_UPLOAD_FOLDER");
        MedicalParTrans parTrans = parReqDocs.getParTrans();
        try {
            byte[] bytes = parReqDocs.getFile().getBytes();
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(bytes);
            String hashString = new BigInteger(1, digest).toString(16);
            parReqDocs.setContentType(parReqDocs.getFile().getContentType());
            parReqDocs.setCheckSum(hashString);
            parReqDocs.setUploadedFileName(parReqDocs.getFile().getOriginalFilename());
            parReqDocs.setDateUploaded(new Date());
            parDocsRepo.save(parReqDocs);
            String folderName = uploadFolder + "/MED_" + parTrans.getParId();
            File file = new File(folderName);
            if (!file.exists())
                FileUtils.forceMkdir(file);
            Path path = Paths.get(folderName + "/" + parReqDocs.getFile().getOriginalFilename());
            Files.write(path, bytes);
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public List<SubClassReqdDocsDTO> findUnassignedRiskDocs(Long clmId, String docName) {
        List<Object[]> clmReqdDocs = requiredDocsRepo.getUnassignedClaimsReqDocs(clmId, (docName != null) ? "%" + docName + "%" : "%%");
        final List<SubClassReqdDocsDTO> reqdDocsDTOList = new ArrayList<>();
        for (Object[] tran : clmReqdDocs) {
            SubClassReqdDocsDTO reqdDocsDTO = new SubClassReqdDocsDTO();
            reqdDocsDTO.setSclReqrdId(((BigInteger) tran[0]).longValue());
            reqdDocsDTO.setReqShtDesc((String) tran[1]);
            reqdDocsDTO.setReqDesc((String) tran[2]);
            reqdDocsDTOList.add(reqdDocsDTO);
        }
        return reqdDocsDTOList;
    }

    @Override
    public File getMedMembersTemplate() throws BadRequestException {
        File file;
        try {
            file = ResourceUtils.getFile("classpath:templates/medical_upload_template.xls");
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }
        return file;
    }
}
