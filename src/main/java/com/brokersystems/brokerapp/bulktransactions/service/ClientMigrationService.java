package com.brokersystems.brokerapp.bulktransactions.service;

import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.bulktransactions.models.ClientMigration;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ClientMigrationService {
    Map<String, Object> uploadAndSaveClientData(MultipartFile file) throws BadRequestException;
    DataTablesResult<ClientMigration> findUnprocessedClients(DataTablesRequest request);
    String processSingleClient(Long clientId) throws BadRequestException;
    String bulkProcessClients(List<Long> clientIds) throws BadRequestException;
    String deleteBulkClients(List<Long> clientIds) throws BadRequestException;
}