package com.brokersystems.brokerapp.setup.controllers;

import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.setup.model.ProductCodes;

import com.brokersystems.brokerapp.setup.service.ProductCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping({ "/protected/sap/productcodes" })
public class productCodesController {

    @Autowired
    private ProductCodeService productCodeService;

    @Autowired
    private AuditTrailLogger auditTrailLogger;
    @RequestMapping(value = { "allProductCodes" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<ProductCodes> getAllProductCodes(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return productCodeService.findAllProductCodes(pageable);
    }
    @RequestMapping(value = "productCodeHome", method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String classHome(Model model, HttpServletRequest request) {
        auditTrailLogger.log("Accessed product codes screen", request, "Product Codes");
        return "productCodehome";
    }
}
