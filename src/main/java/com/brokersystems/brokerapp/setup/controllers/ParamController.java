package com.brokersystems.brokerapp.setup.controllers;

import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.service.ParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by HP on 10/7/2017.
 */
@Controller
@RequestMapping({ "/protected/setups" })
public class ParamController {

    @Autowired
    private ParamService paramService;


    @RequestMapping(value = { "params" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public String validatePin(@RequestParam(value = "paramName", required = false) String paramName) throws BadRequestException {
        String ret = "N";
        try {
            ret = paramService.getParameterString(paramName);
        } catch (BadRequestException e) {
            ret = "N";
        }
        return ret;
    }

}
