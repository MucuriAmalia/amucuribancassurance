package com.brokersystems.brokerapp.auditlogs.service;

import com.brokersystems.brokerapp.auditlogs.model.SystemLogs;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface SystemLogsService {

    List<SystemLogs> getSystemLogs(Date startDate, Date endDate) throws BadRequestException;

}
