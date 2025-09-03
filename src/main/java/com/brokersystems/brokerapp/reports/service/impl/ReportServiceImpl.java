package com.brokersystems.brokerapp.reports.service.impl;

import com.brokersystems.brokerapp.reports.model.*;
import com.brokersystems.brokerapp.reports.repository.ReportDefRepo;
import com.brokersystems.brokerapp.reports.repository.ReportHeaderRepo;
import com.brokersystems.brokerapp.reports.repository.ReportParamRepo;
import com.brokersystems.brokerapp.reports.service.ReportService;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.trans.model.QSystemTransactions;
import com.brokersystems.brokerapp.trans.model.SystemTransactions;
import com.brokersystems.brokerapp.trans.repository.SystemTransactionsRepo;
import com.brokersystems.brokerapp.users.model.PermissionsDef;
import com.brokersystems.brokerapp.users.model.QPermissionsDef;
import com.brokersystems.brokerapp.users.repository.PermissionsRepo;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.BooleanExpression;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by peter on 2/12/2017.
 */
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportHeaderRepo rptHeaderRepo;

    @Autowired
    private ReportDefRepo reportDefRepo;

    @Autowired
    private ReportParamRepo reportParamRepo;

    @Autowired
    private SystemTransactionsRepo transactionsRepo;

    @Autowired
    private PermissionsRepo permissionsRepo;

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<ReportHeaders> findAllReportHeaders(DataTablesRequest request) throws IllegalAccessException {
        Page<ReportHeaders> page = rptHeaderRepo.findAll(request.searchPredicate(QReportHeaders.reportHeaders), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = false)
    public void defineRptHeader(ReportHeaders reportHeader) {
        rptHeaderRepo.save(reportHeader);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteRptHeader(Long rhId) {
        rptHeaderRepo.delete(rhId);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<ReportDefinition> findReportsByHeader(long rhid, DataTablesRequest request) throws IllegalAccessException {
        QReportHeaders header = QReportDefinition.reportDefinition.reportHeader;
        BooleanExpression pred = header.rhId.eq(rhid);
        Page<ReportDefinition> page = reportDefRepo.findAll(pred.and(request.searchPredicate(QReportDefinition.reportDefinition)), request);
        return new DataTablesResult(request, page);
    }

    @Override
    @Transactional(readOnly = false)
    public void defineReportDef(ReportDefinition reportDef) {
        if(reportDef.getPasswordProtect()!=null && "on".equalsIgnoreCase(reportDef.getPasswordProtect()))
            reportDef.setPasswordProtect("Y");
        else reportDef.setPasswordProtect("N");
        reportDefRepo.save(reportDef);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteReportDef(Long rhId) {
        reportDefRepo.delete(rhId);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<ReportParameters> findParametersByReport(long rptId, DataTablesRequest request) throws IllegalAccessException {
        QReportDefinition reportDefinition = QReportParameters.reportParameters.reportDef;
        BooleanExpression pred = reportDefinition.rptId.eq(rptId);
        Page<ReportParameters> page = reportParamRepo.findAll(pred.and(request.searchPredicate(QReportParameters.reportParameters)), request);
        return new DataTablesResult(request, page);
    }

    @Override
    @Transactional(readOnly = false)
    public void definReportParam(ReportParameters reportParam) {
        reportParamRepo.save(reportParam);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteReportParam(Long rpId) {
        reportParamRepo.delete(rpId);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<ReportDefinition> findReportsByModule(String module) {
        Iterable<ReportDefinition> reportDefinitions = reportDefRepo.findAll(QReportDefinition.reportDefinition.reportHeader.moduleCode.eq(module));

        return reportDefinitions;
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<ReportParameters> getParametersByReport(Long rptId) {
        OrderSpecifier<Long> specifier = QReportParameters.reportParameters.rpId.asc();
        Iterable<ReportParameters> params = reportParamRepo.findAll(QReportParameters.reportParameters.reportDef.rptId.eq(rptId),specifier);
        return params;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SystemTransactions> findRemittances(String paramString, Pageable paramPageable) {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = QSystemTransactions.systemTransactions.isNotNull();
        } else {
            pred =  QSystemTransactions.systemTransactions.refNo.containsIgnoreCase(paramString)
                    .and(QSystemTransactions.systemTransactions.transdc.eq("D"))
                    .and(QSystemTransactions.systemTransactions.narrations.eq("Creditor Payment"))
                    .and(QSystemTransactions.systemTransactions.transType.eq("PM"))
                    .and(QSystemTransactions.systemTransactions.clientType.eq("I"))
                    .and(QSystemTransactions.systemTransactions.authorised.eq("Y"));
        }
        return transactionsRepo.findAll(pred, paramPageable);
    }

    @Override
    @Transactional(readOnly = true)
    public ReportDefinition getReportByCode(String reportCode) {
        return reportDefRepo.findOne(QReportDefinition.reportDefinition.rptTemplateName.eq(reportCode));
    }

    @Override
    public Page<PermissionsDef> findPermissionRep(Long moduleId, String paramString, Pageable pageable) {
        BooleanExpression pred =QPermissionsDef.permissionsDef.module.moduleId.eq(moduleId).and(
                QPermissionsDef.permissionsDef.permName.containsIgnoreCase(paramString)
        );
        return permissionsRepo.findAll(pred, pageable);    }
}
