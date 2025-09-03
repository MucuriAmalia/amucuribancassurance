package com.brokersystems.brokerapp.dataloading.service;

import com.brokersystems.brokerapp.dataloading.model.PolicyData;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface DataLoadService {


    void loadPolicies() throws BadRequestException;

    public void removeUnloadedPolicies() throws BadRequestException;

    public DataTablesResult<PolicyData> getPoliciesToLoad(DataTablesRequest request, String status) throws IllegalAccessException;
    public void importPolicies(MultipartFile file) throws IOException;
}
