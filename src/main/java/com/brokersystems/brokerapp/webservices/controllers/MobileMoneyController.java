package com.brokersystems.brokerapp.webservices.controllers;

import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.webservices.model.C2BPaymentConfirmationRequest;
import com.brokersystems.brokerapp.webservices.model.MobMoneydto;
import com.brokersystems.brokerapp.webservices.service.MobileMoneyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by HP on 5/27/2018.
 */
@Controller
@RequestMapping({ "/mobileMoney" })
public class MobileMoneyController {

    @Autowired
    private MobileMoneyService mobileMoneyService;

    @RequestMapping(value = { "confirmTrans" }, method = { RequestMethod.POST })
    public ResponseEntity<String> confirmUrl(@RequestBody MobMoneydto mobMoneydto)
            throws IllegalAccessException {
        mobileMoneyService.createMobileReceipt(mobMoneydto);
        try {
            mobileMoneyService.autoReceipt(mobMoneydto.getTransId());
        } catch (BadRequestException e) {
            e.printStackTrace();
            return new ResponseEntity<String>("success", HttpStatus.OK);
        }
        return new ResponseEntity<String>("success", HttpStatus.OK);
    }

}
