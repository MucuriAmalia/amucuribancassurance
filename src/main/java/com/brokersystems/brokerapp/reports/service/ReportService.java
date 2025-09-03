package com.brokersystems.brokerapp.reports.service;

import com.brokersystems.brokerapp.reports.model.ReportDefinition;
import com.brokersystems.brokerapp.reports.model.ReportHeaders;
import com.brokersystems.brokerapp.reports.model.ReportParameters;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.trans.model.SystemTransactions;
import com.brokersystems.brokerapp.users.model.PermissionsDef;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by peter on 2/12/2017.
 */
public interface ReportService {

    DataTablesResult<ReportHeaders> findAllReportHeaders(DataTablesRequest request) throws IllegalAccessException;

    void defineRptHeader(ReportHeaders reportHeader);

    void deleteRptHeader(Long rhId);

    DataTablesResult<ReportDefinition> findReportsByHeader(long rhid, DataTablesRequest request)  throws IllegalAccessException;

    void defineReportDef(ReportDefinition reportDef);

    void deleteReportDef(Long rhId);

    DataTablesResult<ReportParameters> findParametersByReport(long rptId, DataTablesRequest request)  throws IllegalAccessException;

    void definReportParam(ReportParameters reportParam);

    void deleteReportParam(Long rpId);

    Iterable<ReportDefinition> findReportsByModule(String module);

    Iterable<ReportParameters> getParametersByReport(Long rptId);

    public Page<SystemTransactions> findRemittances(String paramString, Pageable paramPageable);

    ReportDefinition getReportByCode(String reportCode);

    Page<PermissionsDef> findPermissionRep(Long moduleId, String term, Pageable pageable);
}
