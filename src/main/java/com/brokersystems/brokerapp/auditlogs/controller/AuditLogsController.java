package com.brokersystems.brokerapp.auditlogs.controller;


import com.brokersystems.brokerapp.auditlogs.model.AuditLog;
import com.brokersystems.brokerapp.auditlogs.service.AuditLogService;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.setup.dto.OrganizationDTO;
import com.brokersystems.brokerapp.setup.service.OrganizationService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;

@Controller
@RequestMapping({"/protected/audittrail"})
public class AuditLogsController {

    @Autowired
    AuditTrailLogger auditTrailLogger;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private OrganizationService orgService;

    @Autowired
    private DataSource datasource;


    @RequestMapping(value = "auditlogs", method = RequestMethod.GET)
    public String auditlogs(Model model, HttpServletRequest request) {
        String message = "Accessed Audit Trail Log Screen";
        String resource = "Audit Trail Log";
        auditTrailLogger.log(message, request, resource);
        return "auditlogs";
    }

    @RequestMapping(value = "auditLogsList", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesResult<AuditLog> getAuditLogs(
            @DataTable DataTablesRequest pageable,
            @RequestParam(required = false) Long dateFrom,
            @RequestParam(required = false) Long dateTo) {
        return auditLogService.findAllAuditLogs(pageable, dateFrom, dateTo);
    }

    @RequestMapping(value = "rpt_auditlogs", method = RequestMethod.POST)
    public ModelAndView auditLogsReport(ModelMap modelMap,
                                        HttpServletRequest request,
                                        ModelAndView modelAndView,
                                        @RequestParam("dateFrom") String dateFromString,
                                        @RequestParam("dateTo") String dateToString,
                                        @RequestParam(value = "format", defaultValue = "pdf") String format)
            throws BadRequestException, IOException, ParseException {

        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        Date dateFrom = isoFormat.parse(dateFromString);
        Date dateTo = isoFormat.parse(dateToString);
        Timestamp timestampFrom = new Timestamp(dateFrom.getTime());
        Timestamp timestampTo = new Timestamp(dateTo.getTime());

        OrganizationDTO organization = orgService.getOrganizationLogoDetails();
        InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
        BufferedImage image = ImageIO.read(in);

        modelMap.put("logo", image);
        modelMap.put("datasource", datasource);
        modelMap.put("format", format.toLowerCase()); // pdf, csv, or xlsx
        modelMap.put("dateFrom", timestampFrom);
        modelMap.put("dateTo", timestampTo);

        modelAndView = new ModelAndView("rpt_audit_trail", modelMap);
        return modelAndView;
    }

}
