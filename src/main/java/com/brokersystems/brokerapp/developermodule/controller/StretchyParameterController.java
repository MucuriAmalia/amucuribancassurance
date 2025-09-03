package com.brokersystems.brokerapp.developermodule.controller;

import com.brokersystems.brokerapp.developermodule.dto.StretchyParameterDTO;
import com.brokersystems.brokerapp.developermodule.service.StretchyParameterService;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.setup.model.ModelHelperForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping({"/protected/stretchyparameters"})
public class StretchyParameterController {

    @Autowired
    private StretchyParameterService stretchyParameterService;

    @Autowired
    AuditTrailLogger auditTrailLogger;

    @ModelAttribute
    public ModelHelperForm createHelperForm(){
        return new ModelHelperForm();
    }

    @RequestMapping(value = "params", method = RequestMethod.GET)
    public String parameterHome(Model model, HttpServletRequest request) {
        String message = "Accessed Stretchy Parameters Screen";
        String resource = "Stretchy Parameters";
        auditTrailLogger.log(message, request, resource);
        return "stretchyparams";
    }

    @RequestMapping(value = {"createStretchyParam"}, method = {RequestMethod.POST})
    @ResponseStatus(HttpStatus.CREATED)
    public void createStretchParameter(StretchyParameterDTO stretchyParameter) throws IllegalAccessException, BadRequestException {
        stretchyParameterService.createStretchyParam(stretchyParameter);
    }

    @RequestMapping(value = "stretchyParametersForm", method = RequestMethod.GET)
    public String stretchyparameterForm(Model model) {
        model.addAttribute("stpId", -2000);
        return "stretchyparametersform";
    }

    @RequestMapping (value = {"stretchyparams"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<StretchyParameterDTO> getStretchyParameters(@DataTable DataTablesRequest pageable)
        throws IllegalAccessException{
        return stretchyParameterService.findAllStretchyParameters(pageable);
    }
    @RequestMapping (value = "deleteStretchyParameter/{stpId}", method = {RequestMethod.DELETE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStretchyParameter(@PathVariable Long stpId){
        stretchyParameterService.deleteStretchyParameter(stpId);
    }
    @RequestMapping (value = "/viewStretchyParameter/{stpId}", method = RequestMethod.GET)
    public String viewStretchyParameterForm(@PathVariable Long stpId) {
        return "viewstretchyparameterform";
    }
}
