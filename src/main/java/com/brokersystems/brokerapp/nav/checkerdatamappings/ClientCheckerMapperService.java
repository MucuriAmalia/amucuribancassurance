package com.brokersystems.brokerapp.nav.checkerdatamappings;

import com.brokersystems.brokerapp.setup.dto.ClientDTO;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class ClientCheckerMapperService {
    @Autowired
    private ClientTypeRepo clientTypeRepo;

    @Autowired
    private TownRepository townRepo;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private MobPrefixRepo mobileRepo;

    @Autowired
    private SectorRepo sectorRepo;

    @Autowired
    private ClientTitleRepo clientTitleRepo;

    @Autowired
    private OrgBranchRepository branchRepository;

    @Autowired
    private PostalCodeRepo postalCodeRepo;

    @Autowired
    private OccupationRepo occupationRepo;

    public ClientDTO mapClientData(JSONObject jsonObject) {
        ClientDTO clientDTO = new ClientDTO();

        ClientTypes clientTypes = clientTypeRepo.findOne((long) jsonObject.optInt("clientTypeId"));
        clientDTO.setClientTypeDesc(clientTypes.getTypeDesc());
        clientDTO.setClientTypeId(clientTypes.getTypeId());
        clientDTO.setClientType(clientTypes.getClientType());
        System.out.println(clientTypes.getClientType());

        Town town = townRepo.findOne((long) jsonObject.optInt("ctCode"));
        clientDTO.setCtName(town.getCtName());
        clientDTO.setCtCode(town.getCtCode());

        Country country = countryRepository.findOne((long) jsonObject.optInt("couCode"));
        clientDTO.setCouCode(country.getCouCode());
        clientDTO.setCouName(country.getCouName());

        MobilePrefixDef mobilePrefixDef = mobileRepo.findOne((long) jsonObject.optInt("phonePrefixId"));
        clientDTO.setSmsPrefixName(mobilePrefixDef.getPrefixName());
        clientDTO.setPhonePrefixName(mobilePrefixDef.getPrefixName());
        if ("I".equals(clientTypes.getClientType())) {
            SectorDef sectorDef = sectorRepo.findOne((long) jsonObject.optInt("sectCode"));
            clientDTO.setSectCode(sectorDef.getCode());
            clientDTO.setSectName(sectorDef.getName());
        }

        if ("I".equals(clientTypes.getClientType())) {
            ClientTitle clientTitle = clientTitleRepo.findOne((long) jsonObject.optInt("titleId"));
            clientDTO.setTitleId(clientTitle.getTitleId());
            clientDTO.setTitleName(clientTitle.getTitleName());
        }

        OrgBranch orgBranch = branchRepository.findOne((long) jsonObject.optInt("obId"));
        clientDTO.setObId(orgBranch.getObId());
        clientDTO.setObName(orgBranch.getObName());

        PostalCodesDef postalCodesDef = postalCodeRepo.findOne((long) jsonObject.optInt("pcode"));
        clientDTO.setPcode(postalCodesDef.getPcode());
        clientDTO.setPostalName(postalCodesDef.getPostalName());

        if ("I".equals(clientTypes.getClientType())) {
            Occupation occupation = occupationRepo.findOne((long) jsonObject.optInt("occCode"));
            clientDTO.setOccCode(occupation.getCode());
            clientDTO.setOccName(occupation.getName());
        }

        clientDTO.setGender(jsonObject.optString("gender"));
        clientDTO.setClientRef(jsonObject.optString("clientRef"));
        clientDTO.setSmsNumber(jsonObject.optString("smsNumber"));
        clientDTO.setIdNo(jsonObject.optString("idNo"));
        clientDTO.setPhoneNo(jsonObject.optString("phoneNo"));
        clientDTO.setOfficeTel(jsonObject.optString("officeTel"));
        clientDTO.setEmailAddress(jsonObject.optString("emailAddress"));
        clientDTO.setTenantNumber(jsonObject.optString("tenantNumber"));
        clientDTO.setPassportNo(jsonObject.optString("passportNo"));
        clientDTO.setFname(jsonObject.optString("fname"));
        clientDTO.setAddress(jsonObject.optString("address"));
        clientDTO.setResidentStatus(jsonObject.optString("residentStatus"));
        clientDTO.setPinNo(jsonObject.optString("pinNo"));
        clientDTO.setOtherNames(jsonObject.optString("otherNames"));

        // Parse the date strings to Date objects
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");
        try {
            Date dob = dateFormat.parse(jsonObject.optString("dob"));
            clientDTO.setDob(dob);

            Date dateRegistered = dateFormat.parse(jsonObject.optString("dateregistered"));
            clientDTO.setDateregistered(dateRegistered);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        clientDTO.setComment(jsonObject.optString("comment"));

        System.out.println(clientDTO);

        return clientDTO;
    }
}