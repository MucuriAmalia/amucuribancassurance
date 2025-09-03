package com.brokersystems.brokerapp.aki.service;

import com.brokersystems.brokerapp.aki.dto.*;
import com.brokersystems.brokerapp.certs.model.PolicyCerts;
import com.brokersystems.brokerapp.certs.model.PrintCertificateQueue;
import com.brokersystems.brokerapp.certs.model.QPrintCertificateQueue;
import com.brokersystems.brokerapp.certs.repository.PolicyCertsRepo;
import com.brokersystems.brokerapp.certs.repository.PrintQueueRepo;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.brokersystems.brokerapp.setup.util.AESUtil;
import com.brokersystems.brokerapp.uw.repository.RiskTransRepo;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AkiAuthenticationServiceImpl implements AkiAuthenticationService{

    @Autowired
    Environment env;
    @Autowired
    private  RestTemplate restTemplate;
    @Autowired
    private PrintQueueRepo printQueueRepo;
    @Autowired
    private PolicyCertsRepo policyCertsRepo;
    @Autowired
    private ParamService paramService;

    @Autowired
    private AESUtil aesUtil;

    @Autowired
    private RiskTransRepo riskTransRepo;

    private String getParameterValue(String param) throws BadRequestException {

            try {
                final String paramValue = paramService.getParameterString(param);
                return aesUtil.decryptPasswordBased(paramValue);
            } catch (NoSuchAlgorithmException | BadPaddingException | InvalidKeyException | IllegalBlockSizeException |
                     NoSuchPaddingException   |
                     BadRequestException e) {
                throw new BadRequestException(e.getMessage()+" Param "+param);
            }
    }

    private LoginAuthDTO login() throws BadRequestException {
        final String uthUrl = env.getProperty("aki.loginUrl");
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        Map map = new HashMap<String, String>();
        map.put("Content-Type", "application/json");
        headers.setAll(map);
        LoginBean loginBean = new LoginBean();
        loginBean.setUsername(getParameterValue("AKI_USERNAME"));
        System.out.println("Username "+getParameterValue("AKI_USERNAME"));
        System.out.println("Password "+getParameterValue("AKI_PASSWORD"));
        loginBean.setPassword(getParameterValue("AKI_PASSWORD"));
        HttpEntity<?> request = new HttpEntity<>(loginBean, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(uthUrl, request, String.class);
        String jsonString = response.getBody();
        System.out.println("Auth token "+jsonString);
        Gson g = new Gson();
        return g.fromJson(jsonString, LoginAuthDTO.class);
    }

    @Override
    public String issueTypeCerts(TypeCertificateIssueDTO issueDTO, Long ipuCode) throws BadRequestException {
     //   try {
            final LoginAuthDTO authDTO = login();
            if (authDTO.getToken() == null) {
                throw new BadRequestException("Unable to get Aki Credentials");
            }
            issueDTO.setMembercompanyid(15);
            System.out.println(new Gson().toJson(issueDTO));
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
            Map map = new HashMap<String, String>();
            map.put("Content-Type", "application/json");
            map.put("ClientID", getParameterValue("AKI_CLIENT_ID"));
            map.put("Authorization", "Bearer " + authDTO.getToken());
            headers.setAll(map);
//             JSONObject certRequest = new JSONObject();
//            certRequest.put("IntermediaryIRANumber",issueDTO.getIntermediaryIRANumber());
//            certRequest.put("Typeofcover",issueDTO.getTypeofcover());
//            certRequest.put("Policyholder",issueDTO.getPolicyholder());
//            certRequest.put("policynumber",issueDTO.getPolicynumber());
//            certRequest.put("Commencingdate",issueDTO.getCommencingdate());
//            certRequest.put("Expiringdate",issueDTO.getExpiringdate());
//            certRequest.put("Registrationnumber",issueDTO.getRegistrationnumber());
//            certRequest.put("Chassisnumber",issueDTO.getChassisnumber());
//            certRequest.put("Phonenumber",issueDTO.getPhonenumber());
//            certRequest.put("Bodytype",issueDTO.getBodytype());
//            certRequest.put("Vehiclemake",issueDTO.getVehiclemake());
//            certRequest.put("Vehiclemodel",issueDTO.getVehiclemodel());
//            certRequest.put("Enginenumber",issueDTO.getEnginenumber());
//            certRequest.put("Email",issueDTO.getEmail());
//            certRequest.put("SumInsured",issueDTO.getSumInsured());
//            certRequest.put("InsuredPIN",issueDTO.getInsuredPIN());
//            certRequest.put("Yearofmanufacture",issueDTO.getYearofmanufacture());
//            certRequest.put("HudumaNumber",issueDTO.getHudumaNumber());
//            certRequest.put("TypeOfCertificate",3);
//            HttpEntity<?> request = new HttpEntity<>(certRequest.toString(), headers);
            HttpEntity<?> request = new HttpEntity<>(issueDTO,headers);
            System.out.println(env.getProperty("aki.issueTypeC"));
            ResponseEntity<String> response = restTemplate.postForEntity(env.getProperty("aki.issueTypeC"), request, String.class);
            String jsonString = response.getBody();
            System.out.println(jsonString);
            IssueObjectResponse remoteObject = new Gson().fromJson(jsonString, IssueObjectResponse.class);
            List<AkiErrorLog> errors = remoteObject.getError();
            if ((remoteObject.getSuccess() != null && "true".equals(remoteObject.getSuccess())) || (remoteObject.getCallbackObj() != null && remoteObject.getCallbackObj().getIssueCertificate() != null &&
                    !isBlank(remoteObject.getCallbackObj().getIssueCertificate().getActualCNo()))) {
                final List<PrintCertificateQueue> printCertificateQueueList = printQueueRepo.findAllByRisk(riskTransRepo.findOne(ipuCode));
                if (printCertificateQueueList.size()==1) {
                    final PrintCertificateQueue printCertificateQueue = printCertificateQueueList.get(0);
                    printCertificateQueue.setAkiCertNo(remoteObject.getCallbackObj().getIssueCertificate().getActualCNo());
                    printCertificateQueue.setAkiRequestNo(remoteObject.getCallbackObj().getIssueCertificate().getTransactionNo());
                    printCertificateQueue.setCertNo(Long.parseLong(remoteObject.getCallbackObj().getIssueCertificate().getActualCNo().substring(1)));
                    printCertificateQueue.setIssued("Y");
                    printQueueRepo.save(printCertificateQueue);
                    final PolicyCerts policyCerts = printCertificateQueue.getPolicyCerts();
                    policyCerts.setCertNo(Long.parseLong(remoteObject.getCallbackObj().getIssueCertificate().getActualCNo().substring(1)));
                    policyCertsRepo.save(policyCerts);
                }
            } else {
                List<AkiErrorLog> checkError = errors.stream().filter(a -> a.getErrorCode().equalsIgnoreCase("ER001")
                        || a.getErrorCode().equalsIgnoreCase("ER002")
                        || a.getErrorCode().equalsIgnoreCase("ER003")
                        || a.getErrorCode().equalsIgnoreCase("ER004")
                        || a.getErrorCode().equalsIgnoreCase("ER005")
                        || a.getErrorCode().equalsIgnoreCase("ER006")).collect(Collectors.toList());
                if (checkError.size() > 0) {
                        final String errorString = remoteObject.getError().stream().map(a -> a.getErrorText()).collect(Collectors.joining(" , "));
                        throw new BadRequestException(errorString);
                    } else {
                       System.out.println("Resissue certificate....");
                        VerifyErrorCertificateDTO certDto = new VerifyErrorCertificateDTO();
                        certDto.setApproved(true);
                        certDto.setLogBookVerified(true);
                        certDto.setVehicleInspected(true);
                        certDto.setAdditionalComments("");
                        certDto.setIpucode(ipuCode);
                        certDto.setIssuanceRequestID(remoteObject.getCallbackObj().getIssuanceRequestID());
                        verifyCert2(certDto);
                    }
            }
            return "Y";
      //  }
//        catch (Exception ex){
//            throw new BadRequestException(ex.getMessage());
//        }
    }

    private Map<String,String> verifyCert2(VerifyErrorCertificateDTO certificate) throws BadRequestException {
        try {
            LoginAuthDTO authDTO = login();
            long start = System.currentTimeMillis();
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
            Map map = new HashMap<String, String>();
            map.put("Content-Type", "application/json");
            map.put("ClientID", getParameterValue("AKI_CLIENT_ID"));
            map.put("Authorization", "Bearer " + authDTO.getToken());
            headers.setAll(map);
            VerifyErrorCertificate issueDTO = new VerifyErrorCertificate();
            issueDTO.setIsApproved(certificate.isApproved());
            issueDTO.setIsVehicleInspected(certificate.isVehicleInspected());
            issueDTO.setIsLogBookVerified(certificate.isLogBookVerified());
            issueDTO.setIssuanceRequestID(certificate.getIssuanceRequestID());
            issueDTO.setUserName(certificate.getUserName());
            issueDTO.setAdditionalComments(certificate.getAdditionalComments());
            HttpEntity<?> request = new HttpEntity<>(issueDTO, headers);
            System.out.println(env.getProperty("aki.verifyCert"));
            ResponseEntity<String> response = restTemplate.postForEntity(env.getProperty("aki.verifyCert"), request, String.class);
            String jsonString = response.getBody();
            System.out.println(jsonString);
            Gson g = new Gson();
            Map<String, String> rmap = new HashMap<>();
            ReIssueObjectResponse remoteObject = g.fromJson(jsonString, ReIssueObjectResponse.class);
            if (remoteObject.getSuccess() != null && "true".equals(remoteObject.getSuccess())) {
                if (remoteObject.getCallbackObj().getIssueCertificate() != null || remoteObject.getCallbackObj().getIssueCertificate().getActualCNo() != null) {
                    long finish2 = System.currentTimeMillis();
                    long timeElapsed2 = (finish2 - start) / 1000;
                    System.out.println("It took " + timeElapsed2 + " seconds to get response from dmvic api...cert no." + remoteObject.getCallbackObj().getIssueCertificate().getActualCNo());
                    if (certificate.getIpucode() != null) {
                        final List<PrintCertificateQueue> printCertificateQueueList = printQueueRepo.findAllByRisk(riskTransRepo.findOne(certificate.getIpucode()));
                        if (printCertificateQueueList.size()==1) {
                            final PrintCertificateQueue printCertificateQueue = printCertificateQueueList.get(0);
                            printCertificateQueue.setAkiCertNo(remoteObject.getCallbackObj().getIssueCertificate().getActualCNo());
                            printCertificateQueue.setAkiRequestNo(remoteObject.getCallbackObj().getIssueCertificate().getTransactionNo());
                            printCertificateQueue.setCertNo(Long.parseLong(remoteObject.getCallbackObj().getIssueCertificate().getActualCNo().substring(1)));
                            printCertificateQueue.setIssued("Y");
                            printQueueRepo.save(printCertificateQueue);
                            final PolicyCerts policyCerts = printCertificateQueue.getPolicyCerts();
                            policyCerts.setCertNo(Long.parseLong(remoteObject.getCallbackObj().getIssueCertificate().getActualCNo().substring(1)));
                            policyCertsRepo.save(policyCerts);
                        }
                    }
                    rmap.put("certNo", remoteObject.getCallbackObj().getIssueCertificate().getActualCNo());
                    rmap.put("status", "success");
                    return rmap;
                } else {
                    if (remoteObject.getCallbackObj().getIssuanceMessage() != null) {
                        rmap.put("error", remoteObject.getCallbackObj().getIssuanceMessage());
                        rmap.put("status", "failure");
                        return rmap;
                    }
                }
            }
//		else{
            rmap.put("error", remoteObject.getError().stream().map(a -> a.getErrorText()).collect(Collectors.joining(" , ")));
            rmap.put("status", "failure");
            return rmap;
//		}
        }
            catch (Exception ex){
                throw new BadRequestException(ex.getMessage());
            }

    }

    private  boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs != null && (strLen = cs.length()) != 0) {
            for(int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    public DigitalObject printCert(Long riskId) throws BadRequestException{
        if(riskId==null){
            throw new BadRequestException("Please select a valid risk to get a certificate....");
        }
        final String certNo = printQueueRepo.getCertNo(riskId);
        if(certNo==null){
            throw new BadRequestException("Cannot print the cert...Please allocate first....");
        }
        LoginAuthDTO authDTO =  login();
        String jsonInput = " {\"CertificateNumber\": \""+ certNo+"\"}";
        System.out.println(jsonInput);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        Map map = new HashMap<String, String>();
        map.put("Content-Type", "application/json");
        map.put("ClientID",getParameterValue("AKI_CLIENT_ID"));
        map.put("Authorization","Bearer "+authDTO.getToken());
        headers.setAll(map);
        HttpEntity<String> request = new HttpEntity<>(jsonInput, headers);
        String validatePaymentUrl =  env.getProperty("aki.printCertUrl");
        ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(validatePaymentUrl, request, String.class);
        String jsonString = stringResponseEntity.getBody();
        System.out.println(jsonString);
        final Gson gson = new Gson();
        final CertPrintResponse certUrl = gson.fromJson(jsonString, CertPrintResponse.class);
        DigitalObject digitalObject = new DigitalObject();
        digitalObject.setCerturl(certUrl.getCallbackObj().getURL());
        return digitalObject;
    }
}
