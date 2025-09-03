package com.brokersystems.brokerapp.integrations.serviceimpl;

import com.brokersystems.brokerapp.integrations.service.ClientIntegrationService;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.dto.netreveal.NetrevealRequest;
import com.brokersystems.brokerapp.setup.model.ClientDef;
import com.brokersystems.brokerapp.setup.repository.ClientRepository;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class ClientIntegrationServiceImpl implements ClientIntegrationService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ParamService paramService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ClientRepository clientRepository;

    @Override
    public Map<String,Object> validateID (String documentType, String documentId) throws BadRequestException {
        System.out.println("=== INTEGRATION SERVICE validateID called ===");
        System.out.println("documentType: " + documentType);
        System.out.println("documentId: " + documentId);

        try {
            // Step 1: Get base URL
            System.out.println("Step 1: Getting API_INTEGRATION_URL...");
            String baseUrl = paramService.getParameterString("API_INTEGRATION_URL");
            System.out.println("Base URL: " + baseUrl);

            if (baseUrl == null || baseUrl.trim().isEmpty()) {
                System.out.println("ERROR: API_INTEGRATION_URL is null or empty");
                throw new BadRequestException("API_INTEGRATION_URL configuration missing");
            }

            // Step 2: Build full URL
            String paramValue = baseUrl + "/iprs/get-information";
            System.out.println("Full URL: " + paramValue);

            // Step 3: Build URI
            URI uri = UriComponentsBuilder.fromHttpUrl(paramValue)
                    .queryParam("documentType", documentType)
                    .queryParam("documentId", documentId)
                    .build()
                    .toUri();
            System.out.println("Final URI: " + uri.toString());

            // Step 4: Prepare request
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);

            // Step 5: Make internal HTTP call
            System.out.println("Making internal HTTP call to IPRS controller...");
            long startTime = System.currentTimeMillis();

            ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, String.class);

            long duration = System.currentTimeMillis() - startTime;
            System.out.println("Internal HTTP call completed in " + duration + "ms");
            System.out.println("Response status: " + responseEntity.getStatusCode());

            String responseBody = responseEntity.getBody();
            System.out.println("Response body length: " + (responseBody != null ? responseBody.length() : 0));
            System.out.println("Response body: " + responseBody);

            // Step 6: Parse response
            if (responseBody == null || responseBody.trim().isEmpty()) {
                System.out.println("ERROR: Empty response body");
                throw new RuntimeException("Empty response from internal IPRS service");
            }

            System.out.println("Parsing JSON response...");
            Gson gson = new Gson();
            Map<String, Object> result = gson.fromJson(responseBody, Map.class);

            System.out.println("=== INTEGRATION SERVICE - Success ===");
            System.out.println("Parsed result: " + result);
            return result;

        } catch (RestClientException e) {
            System.out.println("=== INTEGRATION SERVICE - RestClientException ===");
            System.out.println("Error calling internal IPRS endpoint: " + e.getMessage());
            if (e.getCause() != null) {
                System.out.println("Root cause: " + e.getCause().getMessage());
            }
            e.printStackTrace();
            throw new RuntimeException("Internal IPRS service call failed: " + e.getMessage());

        } catch (Exception e) {
            System.out.println("=== INTEGRATION SERVICE - Exception ===");
            System.out.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Unexpected error in validateID: " + e.getMessage());
        }
    }

    @Override
    public String validateKRA (Object authentication) throws BadRequestException {
        String paramValue = paramService.getParameterString("API_INTEGRATION_URL") +  "/kra/validate-pin";
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");
        headers.setAll(map);
        HttpEntity<?> requestEntity = new HttpEntity<>(authentication,headers);
        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(paramValue, requestEntity, String.class);
            String responseBody = responseEntity.getBody();
            System.out.println("Response body: " + responseBody);
            Gson gson = new Gson();
            Map<String,Object> responseMap = gson.fromJson(responseBody, Map.class);
            return responseMap.get("pin").toString();
        } catch (RestClientException e) {
            throw new RuntimeException(e.getMessage());
        }

    }
    @Override
    public Map<String, Object> individualScreening(Object request) throws BadRequestException {
        Map<String,Object> responseMap = new HashMap<>();
        String paramValue = paramService.getParameterString("API_INTEGRATION_URL") +  "/netreveal/individual";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<?> requestEntity = new HttpEntity<>(request,headers);
        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(paramValue, requestEntity, String.class);
            String responseBody = responseEntity.getBody();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            if (rootNode.get("message").asText().equalsIgnoreCase("Data Validated Successfully") && rootNode.get("status").asText().equalsIgnoreCase("Success")) {
                JsonNode resultNode = rootNode.get("data").get("regulatoryAssessmentResult");
                Long ecasaRefNo = resultNode.get("ecasaRefNo").asLong();
                responseMap = referralScreening(ecasaRefNo,request);
            }
        } catch (RestClientException | JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
        return responseMap;
    }
    @Override
    public Map<String, Object> entityScreening(Object request) throws BadRequestException {
        Map<String,Object> responseMap = new HashMap<>();
        String paramValue = paramService.getParameterString("API_INTEGRATION_URL") +  "/netreveal/entity";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<?> requestEntity = new HttpEntity<>(request,headers);
        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(paramValue, requestEntity, String.class);
            String responseBody = responseEntity.getBody();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            if (rootNode.get("message").asText().equalsIgnoreCase("Data Validated Successfully") && rootNode.get("status").asText().equalsIgnoreCase("Success")) {
                JsonNode resultNode = rootNode.get("data").get("regulatoryAssessmentResult");
                Long ecasaRefNo = resultNode.get("ecasaRefNo").asLong();
                responseMap = referralScreening(ecasaRefNo,request);
            }
        } catch (RestClientException | JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
        return responseMap;
    }

    // Removed entityScreening method - Corporate entities skip screening

    public Map<String, Object> referralScreening(Long ecasaRefNo,Object request) throws BadRequestException {
        Map<String,Object> response = new HashMap<>();
        String paramValue = paramService.getParameterString("API_INTEGRATION_URL") +  "/netreveal/referral";
        URI uri = UriComponentsBuilder.fromHttpUrl(paramValue)
                .queryParam("ecasaReferenceNumber", ecasaRefNo)
                .build()
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<?> requestEntity = new HttpEntity<>(request,headers);
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, String.class);
            String responseBody = responseEntity.getBody();
            System.out.println("Response body: " + responseBody);
            JsonNode rootNode = objectMapper.readTree(responseBody);
            if (rootNode.get("message").asText().equalsIgnoreCase("Data Found") && rootNode.get("status").asText().equalsIgnoreCase("Success")) {
                JsonNode resultNode = rootNode.get("data").get("regulatoryAssessmentResults");
                for (JsonNode node : resultNode) {
                    String pepType = node.get("pepType").asText();
                    String screeningStatus = node.get("screeningStatus").asText();
                    response.put("pepType", pepType);
                    response.put("screeningStatus", screeningStatus);
                    response.put("ecasaRefNo", ecasaRefNo);
                }
            }
        } catch (RestClientException | JsonSyntaxException | JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
        return response;
    }

    @Transactional
    @Override
    public void backgroundNetrevealScreening(ClientDef clientDef, NetrevealRequest request) throws BadRequestException {
        String clientType = request.getClientType();

        if (clientType.equalsIgnoreCase("Individual")) {
            // Keep full screening for individuals
            Map<String,Object> response = individualScreening(request.getRequest());
            String pepType = response.get("pepType").toString();
            String screeningStatus = response.get("screeningStatus").toString();
            String ecaseRefNo = response.get("ecasaRefNo").toString();

            clientDef.setEcasaRefNo(ecaseRefNo);
            clientDef.setPepType(pepType);
            clientDef.setScreeningStatus(screeningStatus);
            clientDef.setScreeningDate(new Date());

        } else if (clientType.equalsIgnoreCase("Corporate")) {
            // Skip screening for corporate entities
            String ecaseRefNo = "CORP_NO_SCREENING_" + System.currentTimeMillis();
            clientDef.setEcasaRefNo(ecaseRefNo);
            clientDef.setScreeningDate(new Date());
            // No pepType or screeningStatus set for corporate entities
        }

        clientRepository.save(clientDef);
    }
}