package com.brokersystems.brokerapp.customscreens.controllers;

import com.brokersystems.brokerapp.customscreens.model.ColumnForm;
import com.brokersystems.brokerapp.customscreens.model.RegisteredTable;
import com.brokersystems.brokerapp.customscreens.model.ScheduleResponseForm;
import com.brokersystems.brokerapp.customscreens.model.TableForm;
import com.brokersystems.brokerapp.customscreens.service.ReadWriteNonCoreDataService;
import com.brokersystems.brokerapp.customscreens.service.SchedulesService;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping({ "/protected/schedules" })
public class CustomTableController {

    @Autowired
    SchedulesService schedulesService;

    @Autowired
    ReadWriteNonCoreDataService readWriteNonCoreDataService;

    @Autowired
    private AuditTrailLogger auditTrailLogger;

    @RequestMapping(value = "access",method={RequestMethod.GET})
    public String newClaim(Model model, HttpServletRequest request)
    {
        auditTrailLogger.log("Accessed Schedule Set up Screen ",request,"Schedule Set Up Screen");
        return "customscreen";
    }

    @RequestMapping(method = RequestMethod.GET, value = "{subId}/schedules")
    @ResponseBody
    public DataTablesResult<RegisteredTable> getSubclassSchedules(@DataTable DataTablesRequest request, @PathVariable Long subId) {
        return schedulesService.getSubclassSchedules(request, subId);
    }

    @RequestMapping(value = { "createSchedule" }, method = {
            RequestMethod.POST })
    @ResponseBody
    public ScheduleResponseForm createSchedule(@RequestBody @Validated TableForm tableForm) throws BadRequestException {
        readWriteNonCoreDataService.createCustomTable(tableForm);
        ScheduleResponseForm responseForm = new ScheduleResponseForm();
        responseForm.setStatus("Success");
        return responseForm;
    }
    @RequestMapping(value = { "updateSchedule" }, method = { RequestMethod.POST })
    @ResponseBody
    public ScheduleResponseForm updateSchedule(@RequestBody @Validated TableForm tableForm) throws BadRequestException {
        // Call the service method to handle the update
        readWriteNonCoreDataService.updateCustomTable(tableForm);
        ScheduleResponseForm responseForm = new ScheduleResponseForm();
        responseForm.setStatus("Success");
        return responseForm;
    }


    @RequestMapping(method = RequestMethod.GET, value = "getTableColumns")
    public ResponseEntity<List<ColumnForm>> getTableColumns(@RequestParam String tableName) {
        List<ColumnForm> columns = readWriteNonCoreDataService.getTableColumns(tableName);
        return ResponseEntity.ok(columns);
    }
    @RequestMapping(value = "deleteScheduleTable", method = RequestMethod.DELETE)
    public Map<String,String> deleteScheduleTable(@RequestParam(value = "tableName", required = true) String tableName,
                                                  @RequestParam(value = "category", required = true) Long category,
                                                  @RequestParam(value = "keyValue", required = true) Long keyValue
    ){
        RegisteredTable table = new RegisteredTable();
        table.setTableName(tableName);
        table.setKeyValue(keyValue);
        table.setCategory(category);
        Map<String,String> map = new HashMap<>();
        map.put("status","success");
        readWriteNonCoreDataService.deleteScheduleTable(table);
        return map;
    }
}
