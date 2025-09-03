package com.brokersystems.brokerapp.users.service;

import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.dto.UserDTO;
import com.brokersystems.brokerapp.users.dto.MakerCheckDTO;
import com.brokersystems.brokerapp.users.model.MakerChecker;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

public interface MakerCheckerService {

    void createBulkMakerChecker(MakerCheckDTO makerCheckDTO) throws BadRequestException;

  void createMakerChecker(MakerCheckDTO makerCheckDTO) throws BadRequestException;

  void checkExists(MakerCheckDTO makerCheckDTO) throws BadRequestException;
  DataTablesResult<MakerCheckDTO> findPendingReceipts(DataTablesRequest request) throws BadRequestException;

    DataTablesResult<MakerCheckDTO> findPendingClaims(DataTablesRequest request) throws BadRequestException;

  DataTablesResult<MakerCheckDTO> findPendingTasks(DataTablesRequest request) throws BadRequestException;
 @Transactional
  void approveTask(final Long taskId) throws BadRequestException;

    void notifyClient(PolicyTrans policyTrans, String insuranceType, String clientPhone, String clientEmail, String eventType, String days) throws BadRequestException;

    void  notifyPolicyMaker(PolicyTrans policyTrans, String eventType, String Day, BigDecimal balance, BigDecimal paidAmount);

    void notifyMaker(MakerChecker makerChecker);

    void rejectTask(final Long taskId, Long reasonId, final String reason) throws BadRequestException;

  Object findMakerCheckerTask(Long taskId);

    Object findMakerTaskDetail(Long taskId);

    List<UserDTO> findEligibleCheckers(String permissionName, String searchParam);

    @javax.transaction.Transactional
    void resubmitTask(Long taskId, String taskType, Object updatedData, List<Long> checkerIds) throws BadRequestException;

    DataTablesResult<MakerCheckDTO> getMakerTasks(DataTablesRequest request);
}
