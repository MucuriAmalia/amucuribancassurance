package com.brokersystems.brokerapp.server.utils;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by HP on 11/1/2017.
 */
@Component
public class CopyUtils {
    public Object copy(Object dest, Object origin) {
        try {
            BeanUtils.copyProperties(dest, origin);
        } catch (IllegalAccessException e) {
            dest = null;
        } catch (InvocationTargetException e) {
            dest = null;
        }
        return dest;
    }
}
