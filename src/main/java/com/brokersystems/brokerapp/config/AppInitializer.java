package com.brokersystems.brokerapp.config;

import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SessionTrackingMode;
import java.util.EnumSet;

public class AppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext container) throws ServletException {
        container.setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE));
    }
}
