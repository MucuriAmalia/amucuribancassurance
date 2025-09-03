package com.brokersystems.brokerapp.server.utils;

import com.brokersystems.brokerapp.server.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by HP on 10/3/2017.
 */
@Component
public class ErrorUtils {

    List<String> errors = new ArrayList<String>();

    public void addError(String message) {
        errors.add(message);
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }


    public void clearErrors(){
        errors.clear();
    }


}
