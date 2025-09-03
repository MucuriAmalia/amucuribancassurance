package com.brokersystems.brokerapp.customscreens.service;

import com.brokersystems.brokerapp.customscreens.model.ColumnForm;
import com.brokersystems.brokerapp.customscreens.model.RegisteredTable;
import com.brokersystems.brokerapp.customscreens.model.RegisteredTableModel;
import com.brokersystems.brokerapp.customscreens.model.TableForm;
import com.brokersystems.brokerapp.server.exception.BadRequestException;

import java.util.List;

public interface ReadWriteNonCoreDataService {

    void createCustomTable(TableForm tableForm) throws BadRequestException;

    public void updateCustomTable(TableForm tableForm) throws BadRequestException;

    List<ColumnForm> getTableColumns(String tableName);

    List<RegisteredTableModel> getSubclassSchedules(Long subId);

    void deleteScheduleTable(RegisteredTable table);


}
