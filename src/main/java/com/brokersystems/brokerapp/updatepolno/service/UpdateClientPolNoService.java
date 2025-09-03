package com.brokersystems.brokerapp.updatepolno.service;

import com.brokersystems.brokerapp.bulktransactions.dtos.WezeshaStockDTO;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.updatepolno.dto.UpdateClientPolNoDTO;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface UpdateClientPolNoService {

    Map<String, Object> uploadClientPol(MultipartFile file) throws BadRequestException;

    DataTablesResult<UpdateClientPolNoDTO> findUnprocessedUpdates(DataTablesRequest request);

    DataTablesResult<UpdateClientPolNoDTO> viewProcessedUpdates(DataTablesRequest request, Long agentCode, Boolean status, Date wefDate, Date wetDate);

    public String processPolNoUpdates(List<Long> updateIds) throws BadRequestException;

    String deleteUploadedPolNoUpdates(List<Long> policyIds) throws BadRequestException;
//
//    PolicyTrans processSinglePolUpdates(Long updateId, boolean isApproved) throws BadRequestException;
//
//    List<Long> bulkProcessPolUpdates(List<Long> updateIds, boolean isApproved) throws BadRequestException;
}
