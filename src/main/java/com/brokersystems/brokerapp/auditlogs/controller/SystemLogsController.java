package com.brokersystems.brokerapp.auditlogs.controller;

import com.brokersystems.brokerapp.auditlogs.model.SystemLogs;
import com.brokersystems.brokerapp.auditlogs.service.SystemLogsService;
import com.brokersystems.brokerapp.certs.model.CertTypes;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping({"/protected/logs"})
public class SystemLogsController {

    @Autowired
    private SystemLogsService systemLogsService;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @RequestMapping(value = "syslogs", method = RequestMethod.GET)
    public String syslogs(Model model) {
        return "syslogs";
    }

    @RequestMapping(value = {"allsyslogs/{startDate}/{endDate}"}, method = {RequestMethod.GET})
    @ResponseBody
    public List<SystemLogs> getAllLogs(@PathVariable(value = "startDate") String wefDate, @PathVariable(value = "endDate") String wetDate) throws BadRequestException {
        Date wef = new Date();
        Date wet = new Date();
        if (wefDate != null && wetDate != null) {
            try {
                wef = new SimpleDateFormat("dd-MM-yyyy").parse(wefDate);
                wet = new SimpleDateFormat("dd-MM-yyyy").parse(wetDate);
            } catch (ParseException e) {

            }
        }
        System.out.println("Wef: " + wefDate + " Wet: " + wetDate);
        return systemLogsService.getSystemLogs(wef, wet);
    }

}
