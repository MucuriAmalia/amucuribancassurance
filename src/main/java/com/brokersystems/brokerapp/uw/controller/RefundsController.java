package com.brokersystems.brokerapp.uw.controller;

import com.brokersystems.brokerapp.accounts.model.Refunds;
import com.brokersystems.brokerapp.accounts.service.AccountsService;
import com.brokersystems.brokerapp.dms.model.UploadBean;
import com.brokersystems.brokerapp.dms.service.UploadService;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.setup.dto.ReqDocsDTO;
import com.brokersystems.brokerapp.setup.model.RequiredDocBean;
import com.brokersystems.brokerapp.uw.dtos.ClientsDto;
import com.brokersystems.brokerapp.uw.dtos.EndorsementsDTO;
import com.brokersystems.brokerapp.uw.dtos.RefundClientDTO;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.service.EndorseService;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping({ "/protected/uw/refunds" })
public class RefundsController {
    @Autowired
    private AuditTrailLogger auditTrailLogger;


    @Autowired
    private AccountsService accountsService;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private PolicyTransService policyService;

    @RequestMapping(value = "refundsprocess", method = RequestMethod.GET)
    public String refundProcess(Model model, HttpServletRequest request) {
        String message="Accessed Refunds Screen";
        String resource="Refunds";
        auditTrailLogger.log(message,request, resource);

        return "refunds";
    }



    @RequestMapping(value = { "getClientRefunds" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<RefundClientDTO> getClientRefunds(@DataTable DataTablesRequest pageable,
                                                              @RequestParam(value = "clientCode", required = false) Long clientCode,
                                                              @RequestParam(value = "prodCode", required = false) Long prodCode,
                                                              @RequestParam(value = "policyNo", required = false) String policyNo) throws IllegalAccessException {
        return accountsService.findClientRefunds(pageable, clientCode, prodCode, policyNo);
    }

    @RequestMapping(value = { "processRefunds" }, method = { RequestMethod.POST })
    @ResponseBody
    public ResponseEntity<Map<String, Object>> processRefunds(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Object> transactionNumbersRaw = (List<Object>) request.get("transactionNumbers");
            String refundComments = (String) request.get("refundComments");

            System.out.println(transactionNumbersRaw);

            // Convert to Long, handling both Integer and Long types
            List<Long> transactionNumbers = transactionNumbersRaw.stream()
                    .map(obj -> {
                        if (obj instanceof Integer) {
                            return ((Integer) obj).longValue();
                        } else if (obj instanceof Long) {
                            return (Long) obj;
                        } else {
                            return Long.parseLong(obj.toString());
                        }
                    })
                    .collect(Collectors.toList());

            Map<String, Object> result = accountsService.processRefunds(transactionNumbers, refundComments);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to process refunds: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @RequestMapping(value = { "createRefundRiskDocs" }, method = { RequestMethod.POST })
    public ResponseEntity<String> createRefundDocs(@RequestBody RequiredDocBean requiredDocBean) {
        accountsService.createRefundRequiredDocs(requiredDocBean);
        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }
    @RequestMapping(value = { "uploadRefundDocs" }, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> uploadRefundDocs(UploadBean uploadBean, HttpServletRequest request) throws BadRequestException {
        try {
            uploadService.sybrinCreateCase(uploadBean, "Risk");
            return ResponseEntity.status(HttpStatus.CREATED).body("File Uploaded successfully");
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
