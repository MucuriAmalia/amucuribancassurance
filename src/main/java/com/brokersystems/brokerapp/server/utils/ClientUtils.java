package com.brokersystems.brokerapp.server.utils;

import com.brokersystems.brokerapp.setup.model.QClientDef;
import com.brokersystems.brokerapp.setup.model.QProspectDef;
import com.brokersystems.brokerapp.setup.repository.ClientRepository;
import com.brokersystems.brokerapp.setup.repository.ProspectsRepo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ClientUtils {


    private static ClientRepository clientRepository;

    private static ProspectsRepo prospectsRepository;

    private static ValidatorUtils validatorUtils;

    @Autowired
    private ClientRepository clientRepo;

    @Autowired
    private ProspectsRepo prospectsRepo;

    @Autowired
    private ValidatorUtils utils;

    @PostConstruct
    public void init() {
        ClientUtils.clientRepository = this.clientRepo;
        ClientUtils.validatorUtils = this.utils;
        ClientUtils.prospectsRepository = this.prospectsRepo;
    }

    public static boolean validatePin(String pinNo) {
        return validatorUtils.validatePin(pinNo);
    }

    public static boolean validatePassport (String passport) {
        return validatorUtils.validatePasspNo(passport);
    }

    public static boolean validateId(String idNo) {
        return validatorUtils.validateId(idNo);
    }

    public static boolean validatePhone(String phoneNo) {
        if (phoneNo != null && phoneNo.matches("^[0-9]{7}$")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean validateAddress(String address) {
        if (StringUtils.startsWith(StringUtils.upperCase(address),"P.O. Box".toUpperCase())){
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkPinNew(String pin){
        return clientRepository.count(QClientDef.clientDef.pinNo.equalsIgnoreCase(pin))>0;
    }

    public static boolean checkPinExists(String pin){
        return clientRepository.count(QClientDef.clientDef.pinNo.equalsIgnoreCase(pin))>1;
    }

    public static boolean checkIdNew(String id){
        return clientRepository.count(QClientDef.clientDef.idNo.equalsIgnoreCase(id))>0;
    }

    public static boolean checkIdExists(String id){
        return clientRepository.count(QClientDef.clientDef.idNo.equalsIgnoreCase(id))>1;
    }

    public static boolean checkPassportNoNew(String passportNo){
        return clientRepository.count(QClientDef.clientDef.passportNo.equalsIgnoreCase(passportNo))>0;
    }

    public static boolean checkPassportNoExist(String passportNo){
        return clientRepository.count(QClientDef.clientDef.passportNo.equalsIgnoreCase(passportNo))>1;
    }

    public static boolean checkProspectPinNew(String pin){
        return prospectsRepository.count(QProspectDef.prospectDef.pinNo.equalsIgnoreCase(pin))>0;
    }

    public static boolean checkProspectPinExists(String pin){
        return prospectsRepository.count(QProspectDef.prospectDef.pinNo.equalsIgnoreCase(pin))>1;
    }

    public static boolean checkProspectIdNew(String id){
        return prospectsRepository.count(QProspectDef.prospectDef.idNo.equalsIgnoreCase(id))>0;
    }

    public static boolean checkProspectIdExists(String id){
        return prospectsRepository.count(QProspectDef.prospectDef.idNo.equalsIgnoreCase(id))>1;
    }
}
