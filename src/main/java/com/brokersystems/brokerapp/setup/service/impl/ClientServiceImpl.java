package com.brokersystems.brokerapp.setup.service.impl;

import com.brokersystems.brokerapp.dms.model.QSybrinCases;
import com.brokersystems.brokerapp.dms.model.SybrinCases;
import com.brokersystems.brokerapp.dms.repo.SybrinCasesRepo;
import com.brokersystems.brokerapp.kie.rules.ClientRulesExecutor;
import com.brokersystems.brokerapp.security.DefaultPlatformObjectEncoder;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.server.utils.ValidatorUtils;
import com.brokersystems.brokerapp.setup.dto.ClientDTO;
import com.brokersystems.brokerapp.setup.dto.ClientDocsDTO;
import com.brokersystems.brokerapp.setup.dto.ClientTransactionDTO;
import com.brokersystems.brokerapp.setup.dto.ProspectsDTO;
import com.brokersystems.brokerapp.setup.repository.*;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.brokersystems.brokerapp.uw.model.QRiskDocs;
import com.brokersystems.brokerapp.uw.model.RiskDocs;
import com.brokersystems.brokerapp.workflow.docs.DocType;
import com.brokersystems.brokerapp.workflow.utils.WorkflowService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.service.ClientService;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientRepository clientRepo;
    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private ClientTitleRepo clientTitleRepo;
    @Autowired
    private ProspectsRepo prospectsRepo;
    @Autowired
    private ValidatorUtils validator;
    @Autowired
    private ClientDocsRepo clientDocsRepo;
    @Autowired
    private RequiredDocsRepo requiredDocsRepo;
    @Autowired
    private UserUtils userUtils;
    @Autowired
    private ClientRulesExecutor clientRulesExecutor;
    @Autowired
    private AuditTrailLogger auditTrailLogger;
    @Autowired
    private ParamService paramService;
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private ClientTypeRepo clientTypeRepo;
    @Autowired
    private OrgBranchRepository branchRepository;
    @Autowired
    private MobPrefixRepo mobPrefixRepo;
    @Autowired
    private SectorRepo sectorRepo;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private OccupationRepo occupationRepo;
    @Autowired
    private TownRepository townRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SequenceRepository sequenceRepository;
    @Autowired
    private OrgBranchRepository orgBranchRepository;
    @Autowired
    private DefaultPlatformObjectEncoder defaultPlatformObjectEncoder;
    @Autowired
    private SybrinCasesRepo sybrinCasesRepo;
    @Autowired
    private SegmentRepo segmentRepo;


    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<ClientDTO> findAllClients(DataTablesRequest request, String searchType, String searchValue) throws BadRequestException {
        String search = "%" + searchValue + "%";
        final List<ClientDTO> clientDTOList = new ArrayList<>();
        List<Object[]> clientsList = clientRepo.searchClientsList(search.toLowerCase(),
                request.getPageNumber(), request.getPageSize());
        long rowCount = 0l;
        if (!clientsList.isEmpty()) rowCount = ((BigInteger) clientsList.get(0)[14]).intValue();
        ClientDTO clientDTO = new ClientDTO();
        for (Object[] client : clientsList) {
            clientDTO.setTenantNumber((String) client[0]);
            clientDTO.setClientName(client[1] + " " + client[2]);
            clientDTO.setIdNo((String) client[3]);
            clientDTO.setEmailAddress((String) client[4]);
            clientDTO.setPhoneNo((String) client[5]);
            clientDTO.setClientType((String) client[6]);
            clientDTO.setStatus((String) client[7]);
            clientDTO.setDateCreated((Date) client[8]);
            clientDTO.setTenId(((BigInteger) client[9]).longValue());
            clientDTO.setUsername((String) client[10]);
            clientDTO.setAuthStatus((String) client[11]);
            clientDTO.setHashCode((String) client[12]);
            clientDTO.setClientCIF((String) client[13]);
            clientDTOList.add(clientDTO);
        }
        if(clientDTOList.isEmpty()) {
            try {
                if (searchType.equalsIgnoreCase("IND")) {
                    clientDTO = scvidPersonalChecker(searchValue);
                    clientDTOList.add(clientDTO);
                } else if (searchType.equalsIgnoreCase("CORP")) {
                    clientDTO = scvidNonPersonalChecker(searchValue);
                    clientDTOList.add(clientDTO);
                }
            } catch (BadRequestException e) {
                throw new RuntimeException(e);
            }
        }

        Page<ClientDTO> page = new PageImpl<>(clientDTOList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional
    public ClientDTO scvidPersonalChecker(String customerIdentifier) throws BadRequestException {
        String paramValue = paramService.getParameterString("API_INTEGRATION_URL") +  "/mce/scvidPersonalChecker";
        URI uri = UriComponentsBuilder.fromHttpUrl(paramValue)
                .queryParam("searchType", "ALL")
                .queryParam("customerIdentifierType", "I")
                .queryParam("customerIdentifier",customerIdentifier)
                .build()
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ClientDTO clientDTO = new ClientDTO();
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, String.class);
            String responseBody = responseEntity.getBody();
            System.out.println("Response body: " + responseBody);
            JsonNode rootNode = objectMapper.readTree(responseBody);
            if (rootNode.get("code").asText().equals("200") && rootNode.get("status").asText().equalsIgnoreCase("Success")) {
                if (rootNode.get("message").asText().equalsIgnoreCase("Data Found")) {
                    JsonNode dataNode = rootNode.get("data");
                    JsonNode partyList = dataNode.get("partyRefDataDirectoryDomainResList");

                    if (partyList.isArray()) {
                        for (JsonNode party : partyList) {
                            String cellNumber = party.get("cellNumber").asText();
                            String[] cellParts = cellNumber.split("-");
                            String countryCode = cellParts.length > 0 ? cellParts[0] : "254";
                            String localNumber = cellParts.length > 1 ? cellParts[1] : "712345678";
                            String scvId = party.get("customerReference").get("scvId").asText();
                            String dateOfBirthString = party.get("dateOfBirth").asText();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date dateOfBirth = null;
                            try {
                                dateOfBirth = dateFormat.parse(dateOfBirthString);
                            } catch (ParseException e) {
                                System.out.println("Error parsing dateOfBirth: " + e.getMessage());
                            }
                            String emailAddress = party.get("emailAddress").asText();
                            String firstName = party.get("partyName").get("firstName").asText();
                            String fullName = party.get("partyName").get("fullName").asText();
                            //String lastName = party.get("partyName").get("lastName").asText();
                            String lastName = null;

                            JsonNode partyNameNode = party.get("partyName");
                            if (partyNameNode != null && partyNameNode.has("lastName")) {
                                JsonNode lastNameNode = partyNameNode.get("lastName");
                                if (!lastNameNode.isNull() && !lastNameNode.asText().isEmpty()) {
                                    lastName = lastNameNode.asText();
                                }
                            }

                            String partySalutation = party.get("partySalutation").asText();

                            ClientDef clientDef = new ClientDef();
                            if (scvId != null) {
                                String cifNo = cifPersonalChecker(scvId);
                                clientDef.setClientCIF(cifNo);
                                String segmentId = cifSegmentGet(cifNo);
                                if (segmentId != null && !segmentId.isEmpty()) {
                                    Segments seg = segmentRepo.findbySegName(segmentId);
                                    if(seg != null) {
                                        clientDef.setSegments(seg);
                                    }else{
                                        log.warn("seg ment not found for id {}", segmentId);
                                    }
                                } else {
                                    log.warn("Segment ID is null or empty for CIF {}", scvId);
                                }
                            }
                            User user = userUtils.getCurrentUser();
                            clientDef.setDob(dateOfBirth);
                            clientDef.setEmailAddress(emailAddress);
                            clientDef.setClientTitle(clientTitleRepo.findOne(QClientTitle.clientTitle.titleName.equalsIgnoreCase(partySalutation)));
                            clientDef.setFname(firstName);
                            //clientDef.setOtherNames(lastName);
                            if (lastName != null && !lastName.isEmpty()) {
                                clientDef.setOtherNames(lastName);
                            } else {
                                clientDef.setOtherNames(fullName);
                            }

                            clientDef.setIdNo(customerIdentifier);
                            clientDef.setPhoneNo(localNumber);
                            clientDef.setResidentStatus("resident");
                            clientDef.setPhonePrefix(mobPrefixRepo.findOne(QMobilePrefixDef.mobilePrefixDef.prefixName.equalsIgnoreCase(countryCode)));
                            clientDef.setStatus("A");
                            clientDef.setAuthStatus("Y");
                            clientDef.setTenantType(clientTypeRepo.findOne(QClientTypes.clientTypes.typeDesc.equalsIgnoreCase("Individual")));
                            clientDef.setDateregistered(new Date());
                            clientDef.setRegisteredbrn(orgBranchRepository.findOne(QOrgBranch.orgBranch.headOffice.equalsIgnoreCase("Y")));
                            clientDef.setCreatedBy(user);
                            clientDef.setDateCreated(new Date());

                            Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("C");
                            if (sequenceRepository.count(seqPredicate) == 0)
                                throw new BadRequestException("Sequence for Client Definition has not been setup");
                            SystemSequence sequence = sequenceRepository.findOne(seqPredicate);
                            Long seqNumber = sequence.getNextNumber();
                            final String clientNumber = sequence.getSeqPrefix() + String.format("%06d", seqNumber);
                            sequence.setLastNumber(seqNumber);
                            sequence.setNextNumber(seqNumber + 1);
                            sequenceRepository.save(sequence);
                            clientDef.setTenantNumber(clientNumber);
                            clientDef.setClientHash(defaultPlatformObjectEncoder.encodeExistingClient(clientDef));
                            ClientDef savedClient = clientRepo.save(clientDef);

//                            workflowService.startNewWorkFlow(DocType.CUSTOMER_ONBOARDING, String.valueOf(savedClient.getPinNo()), null, "N", null, null, null,savedClient);
//                            Map<String, Object> processVariables = Maps.newHashMap();
//                            processVariables.put("isExistingCustomer", false);
//
//                            if(savedClient.getIdNo()!=null){
//                                processVariables.put("isIdValid", true);
//                            }else{
//                                processVariables.put("isIdValid", false);
//                            }
//                            if(savedClient.getClientCIF()!=null){
//                                processVariables.put("isCifCreated", true);
//                            }else{
//                                processVariables.put("isCifCreated", false);
//                            }
//                            processVariables.put("cifCreationRetry", false);
//                            processVariables.put("isScreeninPassed", true);
//
//                            workflowService.completeTask(String.valueOf(savedClient.getPinNo()),processVariables,null, DocType.CUSTOMER_ONBOARDING,null, null,null,null,savedClient);
                            authorizeClient(savedClient.getTenId());

                            clientDTO.setTenantNumber(clientDef.getTenantNumber());
                            clientDTO.setClientName(fullName);
                            clientDTO.setIdNo(customerIdentifier);
                            clientDTO.setEmailAddress(emailAddress);
                            clientDTO.setPhoneNo(localNumber);
                            clientDTO.setClientType("Individual");
                            clientDTO.setStatus(clientDef.getStatus());
                            clientDTO.setDateCreated(new Date());
                            clientDTO.setTenId(clientDef.getTenId());
                            clientDTO.setAuthStatus("Y");
                            clientDTO.setHashCode(clientDef.getClientHash());
                            clientDTO.setClientCIF(clientDef.getClientCIF());

                        }
                    }
                } else {
                    String errorMessage = "No Records Found for this client";
                    throw new BadRequestException(errorMessage);
                }
            }
            return clientDTO;
        } catch (HttpClientErrorException e) {
            System.out.println("HTTP Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw new BadRequestException("Error while fetching data: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + e.getMessage());
        }

    }


    public String cifSegmentGet(String masterCifId) throws BadRequestException {
        String paramValue = paramService.getParameterString("API_INTEGRATION_URL") +  "/mce/GetSegment";
        URI uri = UriComponentsBuilder.fromHttpUrl(paramValue)
                .queryParam("masterCifId", masterCifId)
                .build()
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, String.class);
            if(responseEntity.getStatusCode().is2xxSuccessful()) {
                String segment = responseEntity.getBody();
                System.out.println("Response body: " + segment);
                return segment;
            }else{
                //throw new BadRequestException("no response value found ");
                log.warn("no response value was found.");
                return null;
            }
        } catch (RestClientException e) {
            //throw new RuntimeException(e.getMessage());
            log.warn("Error while fetching segment for masterCifId {}: {}", masterCifId, e.getMessage());
            return null;
        }
    }


    @Transactional
    @Override
    public ClientDTO scvidNonPersonalChecker(String registrationNumber) throws BadRequestException {
        String paramValue = paramService.getParameterString("API_INTEGRATION_URL") +  "/mce/scvidNonPersonalChecker";
        URI uri = UriComponentsBuilder.fromHttpUrl(paramValue)
                .queryParam("searchType", "REG")
                .queryParam("registrationNumber",registrationNumber)
                .build()
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ClientDTO clientDTO = new ClientDTO();
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, String.class);
            String responseBody = responseEntity.getBody();
            System.out.println("Response body: " + responseBody);
            JsonNode rootNode = objectMapper.readTree(responseBody);
            if (rootNode.get("code").asText().equals("200") && rootNode.get("status").asText().equalsIgnoreCase("Success")) {
                if (rootNode.get("message").asText().equalsIgnoreCase("Data Found")) {
                    JsonNode dataNode = rootNode.get("data");
                    JsonNode corporateDetails = dataNode.get("nonIndividualCustomerDomainDetailsList");

                    if (corporateDetails.isArray()) {
                        for (JsonNode party : corporateDetails) {
                            String scvId = party.get("customerReference").get("scvId").asText();
                            String regDateString = party.get("date").get("registrationDate").asText();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date registrationDate = null;
                            try {
                                registrationDate = dateFormat.parse(regDateString);
                            } catch (ParseException e) {
                                System.out.println("Error parsing registrationDate: " + e.getMessage());
                            }

                            String businessName = party.get("corporateCustomerLegalEntityReference").get("businessName").asText();
                            String regNo = party.get("corporateCustomerLegalEntityReference").get("registrationNumber").asText();
                            String cifNo = party.get("productServiceDetails").get("productServiceId").asText();
                            String addressLine1 = party.get("corporateAddress").get("addressLine1").asText();
                            String addressLine2 = party.get("corporateAddress").get("addressLine2").asText();
                            String addressLine3 = party.get("corporateAddress").get("addressLine3").asText();
                            String address = String.format("%s,%s,%s",addressLine1,addressLine2,addressLine3);

                            ClientDef clientDef = new ClientDef();
                            clientDef.setClientCIF(cifNo);
                            clientDef.setDob(registrationDate);
                            clientDef.setFname(businessName);
                            clientDef.setIdNo(regNo);
                            clientDef.setStatus("A");
                            clientDef.setAuthStatus("Y");
                            clientDef.setTenantType(clientTypeRepo.findOne(QClientTypes.clientTypes.typeDesc.equalsIgnoreCase("Corporate")));
                            clientDef.setAddress(address);
                            clientRepo.save(clientDef);

                            clientDTO.setTenantNumber(clientDef.getTenantNumber());
                            clientDTO.setClientName(businessName);
                            clientDTO.setIdNo(registrationNumber);
                            clientDTO.setClientType("Corporate");
                            clientDTO.setStatus(clientDef.getStatus());
                            clientDTO.setDateCreated(new Date());
                            clientDTO.setTenId(clientDef.getTenId());
                            clientDTO.setAuthStatus("Yes");
                            clientDTO.setHashCode(clientDef.getClientHash());

                        }
                    }
                } else {
                    String errorMessage = "No Records Found for this client";
                    throw new BadRequestException(errorMessage);
                }
            }
            return clientDTO;
        } catch (HttpClientErrorException e) {
            System.out.println("HTTP Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw new BadRequestException("Error while fetching data: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + e.getMessage());
        }

    }


    @Override
    public String cifPersonalChecker(String masterCifId) throws BadRequestException {
        String paramValue = paramService.getParameterString("API_INTEGRATION_URL") +  "/mce/cifPersonalChecker";
        URI uri = UriComponentsBuilder.fromHttpUrl(paramValue)
                .queryParam("masterCifId", masterCifId)
                .build()
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String productServiceId = "";
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, String.class);
            String responseBody = responseEntity.getBody();
            System.out.println("Response body: " + responseBody);
            JsonNode rootNode = objectMapper.readTree(responseBody);
            if (rootNode.get("code").asText().equals("200") && rootNode.get("status").asText().equalsIgnoreCase("Success")) {
                if (rootNode.get("message").asText().equalsIgnoreCase("Data Found")) {
                    JsonNode dataNode = rootNode.get("data");
                    JsonNode productServiceDetails = dataNode.get("productServiceDetails");

                    if (productServiceDetails.isArray()) {
                        for (JsonNode serviceDetail : productServiceDetails) {
                            productServiceId = serviceDetail.get("productServiceId").asText();
                            String productServiceType = serviceDetail.get("productServiceType").asText();
                        }
                    }
                }
            }
            return productServiceId;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public DataTablesResult<ClientTransactionDTO> findClientTransactions(DataTablesRequest request, Long clientId) {
        final String search = (request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%%";
        final List<ClientTransactionDTO> transactionList = new ArrayList<>();
        List<Object[]> transList = clientRepo.getClientTransactions(clientId, search,
                request.getPageNumber(), request.getPageSize());
        long rowCount = 0l;
        if (!transList.isEmpty()) rowCount = ((BigInteger) transList.get(0)[10]).intValue();
        for (Object[] trans : transList) {
            ClientTransactionDTO transaction = new ClientTransactionDTO();
            transaction.setGrossAmount((BigDecimal) trans[0]);
            transaction.setTransDate((Date) trans[1]);
            transaction.setNetAmount((BigDecimal) trans[2]);
            transaction.setTransactionBalance((BigDecimal) trans[3]);
            transaction.setTransactionRef((String) trans[4]);
            transaction.setTransactionType((String) trans[5]);
            transaction.setWefDate((Date) trans[6]);
            transaction.setWetDate((Date) trans[7]);
            transaction.setPolicyNo((String) trans[8]);
            transaction.setProduct((String) trans[9]);
            transactionList.add(transaction);
        }
        Page<ClientTransactionDTO> page = new PageImpl<>(transactionList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Modifying
    @Transactional(readOnly = false)
    public ClientDef defineClient(ClientDef tenant) {
        return clientRepo.save(tenant);
    }

    @Override
    @Modifying
    @Transactional(readOnly = false)
    public void deleteClient(Long tenId) {
        clientRepo.delete(tenId);

    }

    @Override
    @Transactional(readOnly = true)
    public ClientDef getClientDetails(Long tenId) {
        return clientRepo.findByTenId(tenId).orElse(new ClientDef());
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<ProspectsDTO> findAllProspects(DataTablesRequest request) throws IllegalAccessException {
        final String search = (request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%%";
        Long createdBy = userUtils.getCurrentUser().getId();
        boolean hasPermission = AuthorityUtils.authorityListToSet(SecurityContextHolder.getContext().getAuthentication().getAuthorities()).contains("VIEW_ALL_PROSPECTS");
        List<Object[]> prospcts = prospectsRepo.findProspctsListing(search.toLowerCase(), request.getPageNumber(), request.getPageSize(), hasPermission, createdBy);
        final List<ProspectsDTO> prospectsDTOList = new ArrayList<>();
        long rowCount = 0l;
        if (!prospcts.isEmpty()) rowCount = ((BigInteger) prospcts.get(0)[19]).intValue();
        for (Object[] prospect : prospcts) {
            ProspectsDTO prospectsDTO = new ProspectsDTO();
            prospectsDTO.setTenId(((BigInteger) prospect[0]).longValue());
            prospectsDTO.setFname((String) prospect[1]);
            prospectsDTO.setOtherNames((String) prospect[2]);
            prospectsDTO.setPhoneNo((String) prospect[3]);
            prospectsDTO.setDob((Date) prospect[4]);
            prospectsDTO.setClientType((String) prospect[5]);
            if (prospect[6] != null)
                prospectsDTO.setClientTypeId(((BigInteger) prospect[6]).longValue());
            prospectsDTO.setStatus((String) prospect[7]);
            prospectsDTO.setCategory((String) prospect[8]);
            if (prospect[9] != null)
                prospectsDTO.setBranchId(((BigInteger) prospect[9]).longValue());
            prospectsDTO.setBranchName((String) prospect[10]);
            if (prospect[11] != null)
                prospectsDTO.setAcctId(((BigInteger) prospect[11]).longValue());
            prospectsDTO.setAcctName((String) prospect[12]);
            prospectsDTO.setComment((String) prospect[13]);
            prospectsDTO.setProspShtDesc((String) prospect[14]);
            prospectsDTO.setEmailAddress((String) prospect[15]);
            prospectsDTO.setGender((String) prospect[16]);
            prospectsDTO.setUsername((String) prospect[17]);
            prospectsDTO.setDateofBirth((String) prospect[18]);
            prospectsDTOList.add(prospectsDTO);
        }
        Page<ProspectsDTO> page = new PageImpl<>(prospectsDTOList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public ProspectsDTO findOneProspect(long id) throws BadRequestException {
        List<Object[]> prospcts = prospectsRepo.findProspctsId(id);
        if (prospcts.size() != 1)
            throw new BadRequestException("Invalid Prospect...");
        for (Object[] prospect : prospcts) {
            ProspectsDTO prospectsDTO = new ProspectsDTO();
            prospectsDTO.setTenId(((BigInteger) prospect[0]).longValue());
            prospectsDTO.setFname((String) prospect[1]);
            prospectsDTO.setOtherNames((String) prospect[2]);
            prospectsDTO.setPhoneNo((String) prospect[3]);
            prospectsDTO.setDob((Date) prospect[4]);
            prospectsDTO.setClientType((String) prospect[5]);
            if (prospect[6] != null)
                prospectsDTO.setClientTypeId(((BigInteger) prospect[6]).longValue());
            prospectsDTO.setStatus((String) prospect[7]);
            prospectsDTO.setCategory((String) prospect[8]);
            if (prospect[9] != null)
                prospectsDTO.setBranchId(((BigInteger) prospect[9]).longValue());
            prospectsDTO.setBranchName((String) prospect[10]);
            if (prospect[11] != null)
                prospectsDTO.setAcctId(((BigInteger) prospect[11]).longValue());
            prospectsDTO.setAcctName((String) prospect[12]);
            prospectsDTO.setComment((String) prospect[13]);
            prospectsDTO.setProspShtDesc((String) prospect[14]);
            prospectsDTO.setEmailAddress((String) prospect[15]);
            prospectsDTO.setGender((String) prospect[16]);
            return prospectsDTO;
        }
        return new ProspectsDTO();
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteProspect(Long prosId) {
        ProspectDef prospectDef = prospectsRepo.findOne(prosId);
        prospectDef.setStatus("T");
        prospectsRepo.save(prospectDef);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public ProspectDef defineProspect(ProspectsDTO prospect) throws BadRequestException {
        ProspectDef prospectDef = new ProspectDef();
        prospectDef.setCategory(prospect.getCategory());
        final AccountDef accountDef = (prospect.getAcctId() != null) ? accountRepo.findOne(prospect.getAcctId()) : null;
        final ClientTypes clientTypes = (prospect.getClientTypeId() != null) ? clientTypeRepo.findOne(prospect.getClientTypeId()) : null;
        if (clientTypes == null) {
            throw new BadRequestException("Client Type cannot be null");
        }
//		if(prospect.getClientTitle()==null && prospect.getClientType().equalsIgnoreCase("I")){
//			throw new BadRequestException("Please Select a Valid Client Title");
//		}
//		if(prospect.getClientTitle()==null){
//			if(clientTitleRepo.findOne(prospect.getClientTitle())==null){
//				throw new BadRequestException("Please Select a Valid Client Title");
//			}
//		}
        prospectDef.setTenantType(clientTypes);
        prospectDef.setCategory(prospect.getCategory());
        if (prospect.getClientTitle() != null) {
            prospectDef.setClientTitle(clientTitleRepo.findOne(prospect.getClientTitle()));
        }
        prospectDef.setComment(prospect.getComment());
        prospectDef.setDateregistered(new Date());
        prospectDef.setBranch((prospect.getBranchId() != null) ? branchRepository.findOne(prospect.getBranchId()) : null);
        prospectDef.setPhoneNo(prospect.getPhoneNo());
        prospectDef.setEmailAddress(prospect.getEmailAddress());
        prospectDef.setPhonePrefix((prospect.getPrefixId() != null) ? mobPrefixRepo.findOne(prospect.getPrefixId()) : null);
        prospectDef.setAcc(accountDef);
        prospectDef.setGender(prospect.getGender());
        prospectDef.setDob(prospect.getDob());
        prospectDef.setFname(prospect.getFname());
        prospectDef.setOtherNames(prospect.getOtherNames());
        prospectDef.setIdNo(prospect.getIdNo());
        prospectDef.setPinNo(prospect.getPinNo());
        prospectDef.setAddress(prospect.getAddress());
        prospectDef.setOfficeTel(prospect.getOfficeTel());
        prospectDef.setPassportNo(prospect.getPassportNo());
        prospectDef.setSmsNumber(prospect.getSmsNumber());
        prospectDef.setClientSector((prospect.getClientSector() != null) ? sectorRepo.findOne(prospect.getClientSector()) : null);
        prospectDef.setCountry((prospect.getCountry() != null) ? countryRepository.findOne(prospect.getCountry()) : null);
        prospectDef.setOccupation((prospect.getOccupation() != null) ? occupationRepo.findOne(prospect.getOccupation()) : null);
        prospectDef.setTown((prospect.getTown() != null) ? townRepository.findOne(prospect.getTown()) : null);
        prospectDef.setSegments((prospect.getSegmentId() != null) ? segmentRepo.findOne(prospect.getSegmentId()) : null);
        prospectDef.setResidentStatus(prospect.getResidentStatus());
        if (prospect.getTenId() == null) {
            prospectDef.setStatus("A");
            prospectDef.setProspShtDesc("PRS/" + String.format("%05d", prospectsRepo.count() + 1)+ "/"+new SimpleDateFormat("ddMMyy").format(new Date()));
        } else {
            prospectDef.setStatus(prospect.getStatus());
            prospectDef.setProspShtDesc(prospect.getProspShtDesc());
            prospectDef.setTenId(prospect.getTenId());
        }
        clientRulesExecutor.handleProspectChecks(prospectDef);
        prospectDef.setCreatedBy(userUtils.getCurrentUser());

        return prospectsRepo.save(prospectDef);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public ProspectDef defineQuotProspect(ProspectDef prospect) throws BadRequestException {
        if (prospect.getTenId() == null) {
            prospect.setProspShtDesc("PRS/" + String.format("%05d", prospectsRepo.count() + 1));
        }
        clientRulesExecutor.handleProspectChecks(prospect);
        return prospectsRepo.save(prospect);
    }

    @Override
    public DataTablesResult<ClientDocsDTO> findClientDOcs(DataTablesRequest request, Long clientId) {
        final String search = (request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue().toLowerCase() + "%" : "%%";
        List<Object[]> docsList = clientDocsRepo.getAllClientDocuments(clientId, search.toLowerCase(), request.getPageNumber(), request.getPageSize());
        final List<ClientDocsDTO> clientDocsList = new ArrayList<>();
        long rowCount = 0l;
        if (!docsList.isEmpty()) rowCount = ((BigInteger) docsList.get(0)[13]).intValue();
        for (Object[] doc : docsList) {
            final ClientDocsDTO docsDTO = new ClientDocsDTO();
            docsDTO.setCdId(((BigInteger) doc[0]).longValue());
            docsDTO.setFileId((String) doc[1]);
            docsDTO.setUploadedFileName((String) doc[2]);
            docsDTO.setCheckSum((String) doc[3]);
            docsDTO.setContentType((String) doc[4]);
            docsDTO.setReqId(((BigInteger) doc[5]).longValue());
            docsDTO.setReqShtDesc((String) doc[7]);
            docsDTO.setReqDesc((String) doc[6]);
            docsDTO.setAuthStatus((String) doc[8]);
            docsDTO.setUploadedBy((String) doc[9]);
            docsDTO.setUploadedDate((Date) doc[12]);
            docsDTO.setVerifiedBy((String) doc[10]);
            docsDTO.setVerifiedDate((Date) doc[11]);
            clientDocsList.add(docsDTO);
        }
        Page<ClientDocsDTO> page = new PageImpl<>(clientDocsList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }


    @Override
    public DataTablesResult<ClientDocs> findProspectDOcs(DataTablesRequest request, Long clientId) throws IllegalAccessException {
        BooleanExpression pred = QClientDocs.clientDocs.prospectDef.tenId.eq(clientId);
        Page<ClientDocs> page = clientDocsRepo.findAll(pred.and(request.searchPredicate(QClientDocs.clientDocs)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public List<RequiredDocs> findUnassignedClientDocs(Long clientCode, String docName) throws IllegalAccessException {
        if (clientCode == null) throw new IllegalArgumentException("No Client Selected...");
        ClientDef clientDef = clientRepo.findOne(clientCode);
        if ("I".equalsIgnoreCase(clientDef.getTenantType().getClientType()))
            return requiredDocsRepo.getUnassignedClientDocs(clientCode, docName);
        else if ("C".equalsIgnoreCase(clientDef.getTenantType().getClientType()))
            return requiredDocsRepo.getUnassignedCorporateClientDocs(clientCode, docName);
        else return new ArrayList<>();
    }

    @Override
    public List<RequiredDocs> findUnassignedProspectDocs(Long clientCode, String docName) throws IllegalAccessException {
        if (clientCode == null) throw new IllegalArgumentException("No Client Selected...");
        ProspectDef clientDef = prospectsRepo.findOne(clientCode);
        if ("I".equalsIgnoreCase(clientDef.getTenantType().getClientType()))
            return requiredDocsRepo.getUnassignedProspectDocs(clientCode, docName);
        else if ("C".equalsIgnoreCase(clientDef.getTenantType().getClientType()))
            return requiredDocsRepo.getUnassignedCorporateProspectDocs(clientCode, docName);
        else return new ArrayList<>();
    }

    @Override
    public void createClientRequiredDocs(RequiredDocBean requiredDocBean) {
        List<ClientDocs> clientDocs = new ArrayList<>();
        for (Long reqId : requiredDocBean.getRequiredDocs()) {
            ClientDocs clientDoc = new ClientDocs();
            clientDoc.setRequiredDoc(requiredDocsRepo.findOne(reqId));
            clientDoc.setClientDef(clientRepo.findOne(requiredDocBean.getSubCode()));
            clientDocs.add(clientDoc);
        }
        clientDocsRepo.save(clientDocs);
    }

    @Override
    public void createProspectRequiredDocs(RequiredDocBean requiredDocBean) {
        List<ClientDocs> clientDocs = new ArrayList<>();
        for (Long reqId : requiredDocBean.getRequiredDocs()) {
            ClientDocs clientDoc = new ClientDocs();
            clientDoc.setRequiredDoc(requiredDocsRepo.findOne(reqId));
            clientDoc.setProspectDef(prospectsRepo.findOne(requiredDocBean.getSubCode()));
            clientDocs.add(clientDoc);
        }
        clientDocsRepo.save(clientDocs);
    }


    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    //Disabled HttpServletRequest request
    public void authorizeClient(Long clientCode) throws BadRequestException {

        if (clientCode == null)
            throw new BadRequestException("Client Code cannot be null....");
        ClientDef clientDef = clientRepo.findOne(clientCode);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>" + clientCode);

        if (clientDef.getAuthStatus() != null && "Y".equalsIgnoreCase(clientDef.getAuthStatus())) {
            clientDef.setAuthStatus("N");
            clientDef.setAuthBy(null);
            clientRepo.save(clientDef);


        } else {
            Iterable<RequiredDocs> requiredDocs = requiredDocsRepo.findAll(QRequiredDocs.requiredDocs1.appliesClient.equalsIgnoreCase("Y"));
            for (RequiredDocs requiredDoc : requiredDocs) {
                long count = clientDocsRepo.count(QClientDocs.clientDocs.clientDef.tenId.eq(clientCode).and(QClientDocs.clientDocs.requiredDoc.reqId.eq(requiredDoc.getReqId())));
                if (count == 0) {
                    throw new BadRequestException(String.format("Cannot Authorize Client.Please Upload %s",requiredDoc.getReqDesc()));
                }
            }
			for(ClientDocs clientDocs:clientDocsRepo.findAll(QClientDocs.clientDocs.clientDef.tenId.eq(clientCode))){
                SybrinCases caseExists = sybrinCasesRepo.findOne(QSybrinCases.sybrinCases.caseClientId.eq(clientCode).and(QSybrinCases.sybrinCases.caseRequiredDocId.eq(clientDocs.getRequiredDoc().getReqId())));
                String requiredDocDesc = clientDocs.getRequiredDoc().getReqDesc();
                if(clientDocs.getRequiredDoc().isMandatory()) {
                    if (caseExists == null)
                        throw new BadRequestException("Cannot Authorize Client..Upload Document for Doc. " + clientDocs.getRequiredDoc().getReqDesc());

                }
                if (requiredDocDesc.equals(clientDocs.getRequiredDoc().getReqDesc()) &&
                        caseExists == null) {
                    throw new BadRequestException(String.format("%s document upload  is initiated but not completed. Upload document to proceed.",requiredDocDesc));
                }
            }
            Iterable<ClientDocs> clientDocs = clientDocsRepo.findAll(QClientDocs.clientDocs.clientDef.tenId.eq(clientCode));
            if (clientDocs != null) {
                for (ClientDocs clientDoc : clientDocs) {
                    clientDoc.setApprover(userUtils.getCurrentUser().getUsername());
                    clientDoc.setApprovalDate(new Date());
                    clientDocsRepo.save(clientDoc);
                }
            }
            clientDef.setAuthStatus("Y");
            clientDef.setAuthBy(userUtils.getCurrentUser());
            clientRepo.save(clientDef);
//            workflowService.completeTask(String.valueOf(clientDef.getPinNo()), null, DocType.CUSTOMER_ONBOARDING, null, null, null, null, clientDef);
			/*
			clientRulesExecutor.handleClientChecks(clientDef);
            String pushProspects = "N";
            int minAge = 18;
            try {
                pushProspects = paramService.getParameterString("PUSH_PROSPECTS");
                minAge = paramService.getParamInt("MIN_PUSH_AGE");
            } catch (BadRequestException e) {
                throw new BadRequestException("Parameters PUSH_PROSPECTS and MIN_PUSH_AGE have not been Defined! Define the parameter to proceed");
            }
			if(StringUtils.isBlank(clientDef.getClientRef())&&"Y".equalsIgnoreCase(pushProspects)&& DateUtilities.computeAge(clientDef.getDob())>=minAge){
				synchronizeTransaction(clientDef,request);
			}

			 */
        }

    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    //Disabled HttpServletRequest request
    public void authorizeEditedClient(Long clientCode) throws BadRequestException {

        if (clientCode == null)
            throw new BadRequestException("Client Code cannot be null....");
        ClientDef clientDef = clientRepo.findOne(clientCode);
        if (clientDef.getAuthStatus() != null && "Y".equalsIgnoreCase(clientDef.getAuthStatus())) {
            clientDef.setAuthStatus("N");
            clientDef.setAuthBy(null);
            clientRepo.save(clientDef);


        } else {
            Iterable<RequiredDocs> requiredDocs = requiredDocsRepo.findAll(QRequiredDocs.requiredDocs1.appliesClient.equalsIgnoreCase("Y"));
            for (RequiredDocs requiredDoc : requiredDocs) {
                long count = clientDocsRepo.count(QClientDocs.clientDocs.clientDef.tenId.eq(clientCode).and(QClientDocs.clientDocs.requiredDoc.reqId.eq(requiredDoc.getReqId())));
                if (count == 0) {
                    throw new BadRequestException(String.format("Cannot Authorize Client.Please Upload %s",requiredDoc.getReqDesc()));
                }
            }
			for(ClientDocs clientDocs:clientDocsRepo.findAll(QClientDocs.clientDocs.clientDef.tenId.eq(clientCode))){
                SybrinCases caseExists = sybrinCasesRepo.findOne(QSybrinCases.sybrinCases.caseClientId.eq(clientCode).and(QSybrinCases.sybrinCases.caseRequiredDocId.eq(clientDocs.getRequiredDoc().getReqId())));
                if(caseExists == null)
					throw new BadRequestException("Cannot Authorize Client..Upload Document for Doc. "+clientDocs.getRequiredDoc().getReqDesc());
			}
            Iterable<ClientDocs> clientDocs = clientDocsRepo.findAll(QClientDocs.clientDocs.clientDef.tenId.eq(clientCode));
            if (clientDocs != null) {
                for (ClientDocs clientDoc : clientDocs) {
                    clientDoc.setApprover(userUtils.getCurrentUser().getUsername());
                    clientDoc.setApprovalDate(new Date());
                    clientDocsRepo.save(clientDoc);
                }
            }
            clientDef.setAuthStatus("Y");
            clientDef.setAuthBy(userUtils.getCurrentUser());
            clientRepo.save(clientDef);
            //workflowService.completeTask(String.valueOf(clientDef.getPinNo()),null, DocType.CUSTOMER_ONBOARDING, null,null,null,null,clientDef);
			/*
			clientRulesExecutor.handleClientChecks(clientDef);
            String pushProspects = "N";
            int minAge = 18;
            try {
                pushProspects = paramService.getParameterString("PUSH_PROSPECTS");
                minAge = paramService.getParamInt("MIN_PUSH_AGE");
            } catch (BadRequestException e) {
                throw new BadRequestException("Parameters PUSH_PROSPECTS and MIN_PUSH_AGE have not been Defined! Define the parameter to proceed");
            }
			if(StringUtils.isBlank(clientDef.getClientRef())&&"Y".equalsIgnoreCase(pushProspects)&& DateUtilities.computeAge(clientDef.getDob())>=minAge){
				synchronizeTransaction(clientDef,request);
			}

			 */
        }

    }

}



