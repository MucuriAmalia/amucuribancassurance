package com.brokersystems.brokerapp.aki.service;

import com.brokersystems.brokerapp.aki.dto.DigitalObject;
import com.brokersystems.brokerapp.aki.dto.TypeCIssueDTO;
import com.brokersystems.brokerapp.aki.dto.TypeCertificateIssueDTO;
import com.brokersystems.brokerapp.server.exception.BadRequestException;

public interface AkiAuthenticationService {

    String issueTypeCerts(TypeCertificateIssueDTO typeCIssueDTO, Long ipuCode) throws BadRequestException;

    DigitalObject printCert(Long riskId) throws BadRequestException;

}
