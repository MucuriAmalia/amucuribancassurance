package com.brokersystems.brokerapp.schedules.service;

import com.brokersystems.brokerapp.schedules.model.ScheduleBean;
import com.brokersystems.brokerapp.schedules.model.ScheduleMapping;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.BindersDef;
import com.brokersystems.brokerapp.setup.model.SectionsDef;
import com.sun.mail.iap.BadCommandException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by peter on 2/20/2017.
 */
public interface ScheduleService {

    DataTablesResult<ScheduleMapping> findSubclassMapping(DataTablesRequest request, Long subCode)
            throws IllegalAccessException;

    void createScheduleMapping(ScheduleMapping scheduleMapping) throws BadRequestException;

    void deleteScheduleMapping(Long schedId);

    ScheduleBean generateScheduleColumns(Long riskId) throws BadRequestException;

    public Page<SectionsDef> findSectionsForSel(String term, Pageable pageable, Long subId);
}
