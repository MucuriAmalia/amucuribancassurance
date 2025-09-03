package com.brokersystems.brokerapp.setup.service;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface SubAgentMarketerImportService {
    Map<String, Object> importSubAgentMarketer (MultipartFile file) throws BadRequestException;

}
