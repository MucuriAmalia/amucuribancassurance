package com.brokersystems.brokerapp.medical.service;

import com.brokersystems.brokerapp.server.exception.BadRequestException;

/**
 * Created by peter on 4/28/2017.
 */
public interface RulesService {

    public void validateMinimumAge(Long catId) throws BadRequestException;

    public void validateMaximumAge(Long catId) throws BadRequestException;

    public void validateNoDependents(Long catId) throws BadRequestException;

}
