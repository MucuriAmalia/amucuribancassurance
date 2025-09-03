package com.brokersystems.brokerapp.developermodule.service.Impl;

import com.brokersystems.brokerapp.developermodule.dto.ReportParameterJoinData;
import com.brokersystems.brokerapp.developermodule.dto.StretchyParameterDTO;
import com.brokersystems.brokerapp.developermodule.dto.StretchyReportDTO;
import com.brokersystems.brokerapp.developermodule.dto.StretchyReportParameterDTO;
import com.brokersystems.brokerapp.developermodule.model.*;
import com.brokersystems.brokerapp.developermodule.repository.StretchyParameterRepository;
import com.brokersystems.brokerapp.developermodule.repository.StretchyReportParameterRepo;
import com.brokersystems.brokerapp.developermodule.repository.StretchyReportRepository;
import com.brokersystems.brokerapp.developermodule.service.StretchyReportService;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.exception.ReportNotFoundException;
import com.brokersystems.brokerapp.users.model.PermissionsDef;
import com.brokersystems.brokerapp.users.model.QPermissionsDef;
import com.brokersystems.brokerapp.users.repository.PermissionsRepo;
import com.mysema.query.types.expr.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StretchyReportServiceImpl implements StretchyReportService {
    @Autowired
    private StretchyReportRepository stretchyReportRepository;

    @Autowired
    private StretchyParameterRepository stretchyParameterRepository;

    @Autowired
    private StretchyReportParameterRepo stretchyReportParameterRepo;

    @Autowired
    private StretchyReportParameterRepo reportParameterRepo;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PermissionsRepo permissionsRepo;

    @Autowired
    private StretchyReportParameterRepo stretchyReportParameterRepository;

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public StretchyReport createStretchyReport(StretchyReportDTO stretchyReport) throws BadRequestException {
        StretchyReport stretchyRpt = new StretchyReport();
        //validateSql(stretchyReport.getReportSql());
        stretchyRpt.setStrId(null);
        stretchyRpt.setStrRptName(stretchyReport.getStrRptName());
        stretchyRpt.setStrTemplateName(stretchyReport.getStrTemplateName());
        stretchyRpt.setReportType(stretchyReport.getReportType());
        if ("C".equals(stretchyReport.getReportType())) {
            stretchyRpt.setReportSubtype(stretchyReport.getReportSubtype());
        } else {
            stretchyRpt.setReportSubtype(null);
        }
        stretchyRpt.setReportCategory(stretchyReport.getReportCategory());
        stretchyRpt.setDescription(stretchyReport.getDescription());
        stretchyRpt.setReportSql(stretchyReport.getReportSql());
        StretchyReport savedStretchyRpt = stretchyReportRepository.save(stretchyRpt);

        if (stretchyReport.getParameters() != null) {
            for (String parameterId : stretchyReport.getParameters()) {
                StretchyParameter parameter = stretchyParameterRepository.findOne(Long.valueOf(parameterId));
                StretchyReportParameter reportParameter = new StretchyReportParameter();
                reportParameter.setStretchyReport(savedStretchyRpt);
                reportParameter.setStretchyParameter(parameter);
                reportParameterRepo.save(reportParameter);
            }
        }

        return savedStretchyRpt;
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<StretchyReportDTO> findAllStretchyReports(DataTablesRequest request) throws IllegalAccessException {
        final String search = (request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%%";
        List<Object[]> reports = stretchyReportRepository.findStretchyReportListing(search.toLowerCase(), request.getPageNumber(), request.getPageSize());
        final List<StretchyReportDTO> stretchyReportDTOList = new ArrayList<>();
        long rowCount = 0L;
        if (!reports.isEmpty() && reports.get(0).length > 8) {
            rowCount = Long.parseLong(reports.get(0)[8].toString());
        }

        for (Object[] stretchyReports : reports) {
            StretchyReportDTO stretchyReportDTO = new StretchyReportDTO();
            stretchyReportDTO.setStrId(Long.valueOf(String.valueOf(((BigInteger) stretchyReports[0]).longValue())));
            stretchyReportDTO.setStrRptName((String) stretchyReports[1]);

            String reportType = (String) stretchyReports[2];
            stretchyReportDTO.setReportType(reportType);
//            stretchyReportDTO.setReportType(reportType.equals("T") ? "Table" : "Charts");

            String reportSubtype = (String) stretchyReports[3];
            if ("C".equals(reportType)) {
                if ("BG".equals(reportSubtype)) {
                    stretchyReportDTO.setReportSubtype("Bar Graph");
                } else if ("PC".equals(reportSubtype)) {
                    stretchyReportDTO.setReportSubtype("Pie Chart");
                }
            } else {
                stretchyReportDTO.setReportSubtype(null);
            }

            stretchyReportDTO.setReportCategory((String) stretchyReports[4]);
            stretchyReportDTO.setReportSql((String) stretchyReports[5]);
            stretchyReportDTO.setDescription((String) stretchyReports[6]);
            stretchyReportDTO.setStrTemplateName((String) stretchyReports[7]);
            stretchyReportDTOList.add(stretchyReportDTO);
        }

        Page<StretchyReportDTO> page = new PageImpl<>(stretchyReportDTOList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }


    @Override
    public void deleteStretchyReport(Long strId) {
        stretchyReportRepository.delete(strId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StretchyParameterDTO> selectStretchyParameter(String searchValue, Pageable pageable) {
        if(searchValue==null) searchValue="%%";
        else searchValue = "%" +searchValue+ "%";
        List<Object[]> selectParams = stretchyParameterRepository.findStretchyParameterListing(searchValue, pageable.getPageNumber(), pageable.getPageSize());
        long rowCount = 0L;
        if (!selectParams.isEmpty()) rowCount = ((BigInteger)selectParams.get(0)[10]).longValue();
        List<StretchyParameterDTO> parameterDTOList = new ArrayList<>();
        for (Object[] parameterList:selectParams){
            StretchyParameterDTO parameterListDTO = new StretchyParameterDTO();
            parameterListDTO.setStpId(((BigInteger) parameterList[0]).longValue());
            parameterListDTO.setParamName((String) parameterList[1]);
            parameterListDTO.setParamType((String) parameterList[2]);
            parameterListDTO.setParameterVariable((String) parameterList[3]);
            parameterListDTO.setParameterDisplayType((String) parameterList[4]);
            parameterListDTO.setParameterSql((String) parameterList[5]);
            parameterListDTO.setParamActualName((String) parameterList[6]);
            parameterListDTO.setParameterDefault((String) parameterList[7]);
            parameterListDTO.setLovName((String) parameterList[8]);
            parameterListDTO.setOptions((String) parameterList[9]);
            parameterDTOList.add(parameterListDTO);
        }
        return new PageImpl<>(parameterDTOList, pageable, rowCount);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<StretchyReport> findStretchyReportsByModule(String module) {
        Iterable<StretchyReport> stretchyReportIterable = stretchyReportRepository.findAll(QStretchyReport.stretchyReport.reportCategory.eq(module));
        return stretchyReportIterable;
    }


    @Override
    public Page<PermissionsDef> findStretchyPermissionRep(Long moduleId, String paramString, Pageable pageable) {
        BooleanExpression pred = QPermissionsDef.permissionsDef.module.moduleId.eq(moduleId).and(
                QPermissionsDef.permissionsDef.permName.containsIgnoreCase(paramString)
        );
        return permissionsRepo.findAll(pred, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StretchyParameterDTO> findStretchyReportParameters(Long strId) throws IllegalAccessException {
        List<Object[]> results = stretchyReportRepository.findStretchyReportParameters(strId);
        final List<StretchyParameterDTO> stretchyParameterDTOList = new ArrayList<>();

        for (Object[] result : results) {
            StretchyParameterDTO stretchyParameterDTO = new StretchyParameterDTO();
            stretchyParameterDTO.setStpId(((BigInteger) result[0]).longValue());
            stretchyParameterDTO.setParamName((String) result[1]);
            stretchyParameterDTO.setParameterVariable((String) result[2]);
            stretchyParameterDTO.setParamActualName((String) result[3]);
            stretchyParameterDTO.setParameterDisplayType((String) result[4]);
            stretchyParameterDTO.setParamType((String) result[5]);
            stretchyParameterDTO.setParameterDefault((String) result[6]);
            stretchyParameterDTO.setParameterSql((String) result[7]);
            stretchyParameterDTO.setLovName((String) result[8]);
            stretchyParameterDTO.setOptions((String) result[9]);
            stretchyParameterDTOList.add(stretchyParameterDTO);
        }

        return stretchyParameterDTOList;
    }



    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public StretchyReport updateStretchyReport(StretchyReportDTO stretchyReport) throws BadRequestException {
        // Fetch the existing report using findById
        Optional<StretchyReport> existingReportOptional = stretchyReportRepository.findById(stretchyReport.getStrId());

        // If the report is not found, throw an exception
        StretchyReport existingReport = existingReportOptional.orElseThrow(() -> new BadRequestException("Report not found"));
        //validateSql(stretchyReport.getReportSql());
        // Update the fields
        existingReport.setStrRptName(stretchyReport.getStrRptName());
        existingReport.setReportType(stretchyReport.getReportType());
        existingReport.setReportSubtype("C".equals(stretchyReport.getReportType()) ? stretchyReport.getReportSubtype() : null);
        existingReport.setReportCategory(stretchyReport.getReportCategory());
        existingReport.setDescription(stretchyReport.getDescription());
        existingReport.setReportSql(stretchyReport.getReportSql());

        // Save the updated entity
        StretchyReport savedStretchyRpt = stretchyReportRepository.save(existingReport);

        // Update parameters if necessary
        if (stretchyReport.getParameters() != null) {
            // Update parameters logic here
        }

        return savedStretchyRpt;
    }

    @Override
    public StretchyReport findStretchyReportDetails(Long reportCode) throws BadRequestException {
        return  stretchyReportRepository.findOne(reportCode);
    }

    public List<StretchyParameter> getStretchyReportParameters(Long reportId) throws BadRequestException {

        List<StretchyParameter> parameters = stretchyParameterRepository.findStretchyReportParameters(reportId);

        return parameters;
    }

    public void deleteStretchyReportParameter(Long reportId, Long stpId) {
        stretchyReportParameterRepository.deleteByReportIdAndStpId(reportId, stpId);
    }

    public void updateStretchyReportParameter(StretchyReportParameterDTO stretchyReportParameterDTO) throws BadRequestException {
        Long reportId = stretchyReportParameterDTO.getReportId();
        Long oldStpId = stretchyReportParameterDTO.getOldStpId();
        Long newStpId = stretchyReportParameterDTO.getNewStpId();

        // Check if there's exactly one row with the given reportId and oldStpId
        Long rowCount = stretchyReportParameterRepository.countByReportIdAndStpId(reportId, oldStpId);
        if (rowCount == 1) {
            // Fetch the existing parameter to update
            StretchyReportParameter existingParameter = stretchyReportParameterRepository.findByReportIdAndStpId(reportId, oldStpId)
                    .orElseThrow(() -> new BadRequestException("Parameter not found"));
            // Update the stretchyParameter with the newStpId
            StretchyParameter newParameter = stretchyParameterRepository.findOne(newStpId);
            existingParameter.setStretchyParameter(newParameter);
            stretchyReportParameterRepo.save(existingParameter);
        } else {
            throw new BadRequestException("Expected exactly one matching parameter, but found " + rowCount);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportParameterJoinData> findStretchyReportQuery(String strTemplateName) throws IllegalAccessException, ReportNotFoundException {
        List<Object[]> results = stretchyReportRepository.reportQuery(strTemplateName);
        if (results.isEmpty()) {
            throw new ReportNotFoundException("No reports found with the name: " + strTemplateName);
        }

        final List<ReportParameterJoinData> reportParameterJoinDataList = new ArrayList<>();

        for (Object[] result : results) {
            ReportParameterJoinData reportData = new ReportParameterJoinData();

            reportData.setStrId(((BigInteger) result[0]).longValue());
            reportData.setStrRptName((String) result[1]);
            reportData.setReportType((String) result[2]);
            reportData.setReportSubtype((String) result[3]);
            reportData.setReportCategory((String) result[4]);
            reportData.setReportSql((String) result[5]);
            reportData.setDescription((String) result[6]);

            reportData.setStpId(((BigInteger) result[7]).longValue());
            reportData.setParamName((String) result[8]);
            reportData.setParameterVariable((String) result[9]);
            reportData.setParamActualName((String) result[10]);
            reportData.setParameterDisplayType((String) result[11]);
            reportData.setParamType((String) result[12]);
            reportData.setParameterDefault((String) result[13]);
            reportData.setParameterSql((String) result[14]);
            reportData.setLovName((String) result[15]);
            reportData.setOptions((String) result[16]);

            reportData.setStrTemplateName((String) result[17]);

            reportParameterJoinDataList.add(reportData);
        }

        return reportParameterJoinDataList;
    }


}
