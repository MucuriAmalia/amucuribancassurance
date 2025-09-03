package com.brokersystems.brokerapp.auditlogs.service;

import com.brokersystems.brokerapp.auditlogs.model.AuditLog;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {
        DataTablesResult<AuditLog> findAllAuditLogs(DataTablesRequest request, Long dateFrom, Long dateTo);
}
