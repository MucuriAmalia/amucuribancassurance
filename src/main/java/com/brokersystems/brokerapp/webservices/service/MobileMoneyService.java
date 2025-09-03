package com.brokersystems.brokerapp.webservices.service;

import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.webservices.model.MobMoneydto;

/**
 * Created by HP on 5/27/2018.
 */
public interface MobileMoneyService {

    String createMobileReceipt(MobMoneydto mobMoneydto);

    void autoReceipt(String refNo) throws BadRequestException;


}
