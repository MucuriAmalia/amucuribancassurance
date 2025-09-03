package com.brokersystems.brokerapp.reconciliation.sfi.controller;

import com.brokersystems.brokerapp.reconciliation.sfi.service.SFIService;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/protected/reconciliation")
public class SFIController {

    @Autowired
    private AuditTrailLogger auditTrailLogger;
    @Autowired
    private SFIService SFIService;

    @RequestMapping(value = "sfi", method = RequestMethod.GET)
    public String sfiScreens(HttpServletRequest request) {
        String message="Accessed SFI screens";
        String resource = "SFI";
        auditTrailLogger.log(message,request, resource);
        return "sfiscreens";
    }

    @RequestMapping(value = "getSfiSheetData", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getMortgages(String sheetName) {
        try {
            List<Map<String, String>> mortgagesData = SFIService.readMortgagesData(sheetName);
            return new ResponseEntity<>(mortgagesData, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Error reading data: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
