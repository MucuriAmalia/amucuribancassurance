package com.brokersystems.brokerapp.developermodule.service;


import com.brokersystems.brokerapp.developermodule.dto.ReportParameterJoinData;
import com.brokersystems.brokerapp.developermodule.dto.StretchyParameterDTO;
import com.brokersystems.brokerapp.developermodule.dto.StretchyReportDTO;
import com.brokersystems.brokerapp.developermodule.dto.StretchyReportParameterDTO;
import com.brokersystems.brokerapp.developermodule.model.StretchyParameter;
import com.brokersystems.brokerapp.developermodule.model.StretchyReport;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.exception.ReportNotFoundException;
import com.brokersystems.brokerapp.users.model.PermissionsDef;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;




public interface StretchyReportService {

    StretchyReport createStretchyReport(StretchyReportDTO stretchyReport) throws BadRequestException;

    DataTablesResult<StretchyReportDTO> findAllStretchyReports(DataTablesRequest request) throws  IllegalAccessException;

    void deleteStretchyReport(Long strId);

    public Page<StretchyParameterDTO> selectStretchyParameter(String searchValue, Pageable pageable);
    Iterable<StretchyReport> findStretchyReportsByModule(String module);

    Page<PermissionsDef> findStretchyPermissionRep(Long moduleId, String term, Pageable pageable);


    StretchyReport updateStretchyReport(StretchyReportDTO stretchyReport) throws BadRequestException;

    public StretchyReport findStretchyReportDetails(Long reportCode) throws BadRequestException;

    public List<StretchyParameter> getStretchyReportParameters(Long reportId) throws BadRequestException;


    void deleteStretchyReportParameter(Long reportId, Long stpId);

    @Transactional(readOnly = true)
    List<StretchyParameterDTO> findStretchyReportParameters(Long strId) throws IllegalAccessException;

    void updateStretchyReportParameter(StretchyReportParameterDTO stretchyReportParameterDTO) throws BadRequestException;

    @Transactional(readOnly = true)
    List<ReportParameterJoinData> findStretchyReportQuery(String strTemplateName) throws IllegalAccessException, ReportNotFoundException;
}
