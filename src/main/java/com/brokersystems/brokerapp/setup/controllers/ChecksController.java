package com.brokersystems.brokerapp.setup.controllers;

import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.UserRepository;
import com.brokersystems.brokerapp.setup.service.SetupsService;
import com.brokersystems.brokerapp.setup.service.UserService;
import com.brokersystems.brokerapp.users.model.ModulesDef;
import com.brokersystems.brokerapp.users.model.PermissionsDef;
import com.brokersystems.brokerapp.users.repository.ModulesRepo;
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

/**
 * Created by HP on 10/4/2017.
 */
@Controller
@RequestMapping({ "/protected/setups/checks" })
public class ChecksController {

    @Autowired
    private SetupsService setupsService;

    @Autowired
    private UserService userService;

    @Autowired
    private ModulesRepo modulesRepo;

    @Autowired
    AuditTrailLogger auditTrailLogger;

    @RequestMapping(value = "checksHome",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String checksHome(Model model, HttpServletRequest request)
    {
        String message= "Accessed Checks Home Screen";
        String resource="Checks";
        auditTrailLogger.log(message,request, resource);
        return "checks";

    }
    @RequestMapping(value = { "prodChecksList" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<ProductChecks> getProductChecks(@DataTable DataTablesRequest pageable, @RequestParam(value = "prodId", required = false) Long prodId)
            throws IllegalAccessException {
        return setupsService.findProductChecks(pageable,prodId);
    }

    @RequestMapping(value = { "checksList" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<Checks> getChecks(@DataTable DataTablesRequest pageable, @RequestParam(value = "prodId", required = false) Long prodId)
            throws IllegalAccessException {
        return setupsService.findAllChecks(pageable);
    }

    @RequestMapping(value = { "unselectedChecks" }, method = { RequestMethod.GET })
    @ResponseBody
    public Iterable<Checks> findUnselectedChecks( @RequestParam(value = "prodId", required = false) Long prodId)
            throws IllegalAccessException {
        return setupsService.findUnassginedChecks(prodId);
    }

    @RequestMapping(value = { "createChecks" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void createChecks(Checks checks) throws IllegalAccessException, IOException, BadRequestException {
        setupsService.defineChecks(checks);
    }

    @RequestMapping(value = { "deleteChecks/{checkCode}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChecks(@PathVariable Long checkCode) {
        setupsService.deleteChecks(checkCode);
    }

    @RequestMapping(value = { "deleteProdChecks/{checkCode}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProdChecks(@PathVariable Long checkCode) {
        setupsService.deleteProdChecks(checkCode);
    }

    @RequestMapping(value = { "selPerms" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public Page<PermissionsDef> selPerms(@RequestParam(value = "term", required = false) String term, Pageable pageable)
            throws IllegalAccessException {
        return setupsService.findSelPermissions(term, pageable);
    }

    @RequestMapping(value = { "exceptions"}, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public Page<PermissionsDef> selPermssion(@RequestParam(value = "term", required = false) String term,Pageable pageable)
            throws IllegalAccessException {
        String exception="Exceptions";
        //Long id=setupsService.getId(exception);
        return setupsService.findSelPerm(term,null,pageable);
    }

    @RequestMapping(value = { "selproducts" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public Page<ProductsDef> selproducts(@RequestParam(value = "term", required = false) String term, Pageable pageable)
            throws IllegalAccessException {
        return setupsService.findSelProducts(term, pageable);
    }

    @RequestMapping(value = { "createProductChecks" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    public ResponseEntity<String> createProductChecks(@RequestBody ChecksBean checksBean) throws IllegalAccessException, IOException, BadRequestException {
        setupsService.createProdChecks(checksBean);
        return new ResponseEntity<String>("OK",HttpStatus.OK);
    }

    @RequestMapping(value = { "moduleName" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public ModulesDef selproduct(@RequestParam String exception)
            throws IllegalAccessException {
        return userService.findException(exception);
    }
    @RequestMapping(value = { "addPermission" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void selproducts(PermissionsDef permissionsDef)
            throws IllegalAccessException, BadRequestException {
        userService.setPermissionSetups(permissionsDef);
    }

}
