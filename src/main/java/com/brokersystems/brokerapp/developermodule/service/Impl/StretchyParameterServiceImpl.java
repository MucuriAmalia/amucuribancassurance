package com.brokersystems.brokerapp.developermodule.service.Impl;

import com.brokersystems.brokerapp.developermodule.dto.StretchyParameterDTO;
import com.brokersystems.brokerapp.developermodule.model.StretchyParameter;
import com.brokersystems.brokerapp.developermodule.repository.StretchyParameterRepository;
import com.brokersystems.brokerapp.developermodule.service.StretchyParameterService;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class StretchyParameterServiceImpl implements StretchyParameterService {
    @Autowired
    private StretchyParameterRepository stretchyParameterRepository;

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public StretchyParameter createStretchyParam(StretchyParameterDTO stretchyParameter) throws BadRequestException {
        StretchyParameter stretchyParam = new StretchyParameter();
        stretchyParam.setStpId(stretchyParameter.getStpId());
        stretchyParam.setParamName(stretchyParameter.getParamName());
        stretchyParam.setParameterVariable(stretchyParameter.getParameterVariable());
        stretchyParam.setParamActualName(stretchyParameter.getParamActualName());
        stretchyParam.setParameterDisplayType(stretchyParameter.getParameterDisplayType());
        stretchyParam.setParamType(stretchyParameter.getParamType());
        stretchyParam.setParameterDefault(stretchyParameter.getParameterDefault());
        stretchyParam.setParameterSql(stretchyParameter.getParameterSql());
        stretchyParam.setLovName(stretchyParameter.getLovName());
        stretchyParam.setOptions(stretchyParameter.getOptions());
        return stretchyParameterRepository.save(stretchyParam);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<StretchyParameterDTO> findAllStretchyParameters(DataTablesRequest request) throws IllegalAccessException {
        final String search = (request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%%";
        List<Object[]> parameters = stretchyParameterRepository.findStretchyParameterListing(search.toLowerCase(), request.getPageNumber(), request.getPageSize());
        final List<StretchyParameterDTO> stretchyParameterDTOList = new ArrayList<>();
        long rowCount = 0L;
        if (!parameters.isEmpty() && parameters.get(0).length > 10) {
            rowCount = Long.parseLong(parameters.get(0)[10].toString());
        }

        for (Object[] stretchyParameters : parameters) {
            StretchyParameterDTO stretchyParameterDTO = new StretchyParameterDTO();
            stretchyParameterDTO.setStpId(((BigInteger) stretchyParameters[0]).longValue());
            stretchyParameterDTO.setParamName((String) stretchyParameters[1]);
            stretchyParameterDTO.setParameterVariable((String) stretchyParameters[2]);
            stretchyParameterDTO.setParamActualName((String) stretchyParameters[3]);
            stretchyParameterDTO.setParameterDisplayType((String) stretchyParameters[4]);
            stretchyParameterDTO.setParamType((String) stretchyParameters[5]);
            stretchyParameterDTO.setParameterDefault((String) stretchyParameters[6]);
            stretchyParameterDTO.setParameterSql((String) stretchyParameters[7]);
            stretchyParameterDTO.setLovName((String) stretchyParameters[8]);
            stretchyParameterDTO.setOptions((String) stretchyParameters[9]);
            stretchyParameterDTOList.add(stretchyParameterDTO);
        }

        Page<StretchyParameterDTO> page = new PageImpl<>(stretchyParameterDTOList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteStretchyParameter(Long stpId) {
        stretchyParameterRepository.delete(stpId);
    }

}
