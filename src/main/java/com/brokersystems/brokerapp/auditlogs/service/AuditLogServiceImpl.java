package com.brokersystems.brokerapp.auditlogs.service;

import com.brokersystems.brokerapp.auditlogs.model.AuditLog;
import com.brokersystems.brokerapp.auditlogs.model.QAuditLog;
import com.brokersystems.brokerapp.auditlogs.repositories.AuditLogRepository;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.mysema.query.types.expr.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepo;

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<AuditLog> findAllAuditLogs(DataTablesRequest request, Long dateFrom, Long dateTo) {
        BooleanExpression pred = QAuditLog.auditLog.id.isNotNull();

        if (dateFrom != null) {
            pred = QAuditLog.auditLog.timestamp.goe(new Date(dateFrom)).and(pred);
        }
        if (dateTo != null) {
            pred = QAuditLog.auditLog.timestamp.loe(new Date(dateTo)).and(pred);
        }

        if (request.getSearch() != null && !request.getSearch().getValue().isEmpty()) {
            String term = request.getSearch().getValue();
            pred = pred.and(QAuditLog.auditLog.username.containsIgnoreCase(term)
                    .or(QAuditLog.auditLog.resource.containsIgnoreCase(term))
                    .or(QAuditLog.auditLog.details.containsIgnoreCase(term))
                    .or(QAuditLog.auditLog.timestamp.stringValue().containsIgnoreCase(term)));
        }

        Page<AuditLog> page = auditLogRepo.findAll(pred, request);
        return new DataTablesResult<>(request, page);
    }
}
