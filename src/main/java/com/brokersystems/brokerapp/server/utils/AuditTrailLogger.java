package com.brokersystems.brokerapp.server.utils;

import com.brokersystems.brokerapp.accounts.controllers.AccountsController;
import com.brokersystems.brokerapp.auditlogs.model.AuditLog;
import com.brokersystems.brokerapp.auditlogs.repositories.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

@Component
public class AuditTrailLogger {

    @Autowired
    private AuditLogRepository auditLogRepository;

    private final Logger logger = LoggerFactory.getLogger(AuditTrailLogger.class);


    @Autowired
    private UserUtils userUtils;

    public void log(String message, HttpServletRequest request, String resource) {
        InetAddress addr = null;
        String ipAddress = "";
        String hostname = "localhost";
//        String ipAddress =  request.getSession().getAttribute("ipAddress").toString();
        try {
            ipAddress =  StringUtils.isBlank(userUtils.getCurrentUser().getLastIP())? request.getRemoteAddr(): userUtils.getCurrentUser().getLastIP();
            addr = InetAddress.getByName(ipAddress);
            hostname = addr.getHostName();
        }
        catch (UnknownHostException ex){

        }
        AuditLog auditLog = new AuditLog();
        auditLog.setUsername(userUtils.getCurrentUser().getUsername());
        if(message!=null && message.length() > 255){
            auditLog.setAction(message.substring(0,254));
        }
        else {
            auditLog.setAction(message);
        }
        auditLog.setResource(resource);
        auditLog.setTimestamp(new Date());
        auditLog.setDetails(ipAddress + " " + hostname);
        auditLogRepository.save(auditLog);
//        MDC.put("username", userUtils.getCurrentUser().getUsername());
//        // client's IP address
//        MDC.put("address", ipAddress);
//        MDC.put("machinename", hostname);
//        logger.info(message);
//        MDC.remove("username");
//        MDC.remove("address");
//        MDC.remove("machinename");
    }

//    public void log(String message) {
//        MDC.put("username", userUtils.getCurrentUser().getUsername());
//        logger.info(message);
//        MDC.remove("username");
//    }
}