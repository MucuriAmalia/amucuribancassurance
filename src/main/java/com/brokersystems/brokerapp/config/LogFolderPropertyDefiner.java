package com.brokersystems.brokerapp.config;

import ch.qos.logback.core.PropertyDefinerBase;

import java.io.IOException;
import java.util.Properties;

public class LogFolderPropertyDefiner extends PropertyDefinerBase {


    @Override
    public String getPropertyValue() {
        try {
            Properties prop = new Properties();
            prop.load(this.getClass().getClassLoader().getResourceAsStream("config.properties"));
            return prop.getProperty("log.path");
        }
        catch (IOException e){
            return "/var/logs";
        }
    }

}
