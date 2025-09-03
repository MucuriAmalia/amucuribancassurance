package com.brokersystems.brokerapp.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

public class TokenSimpleUrlAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(TokenSimpleUrlAuthenticationSuccessHandler.class);
	
	@Override
    protected String determineTargetUrl(HttpServletRequest request,
            HttpServletResponse response) {
        String context = request.getContextPath();
        String fullURL = request.getRequestURI();
        String url = fullURL.substring(fullURL.indexOf(context)+context.length());
        return url;
    }
 
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
    	
        String url = determineTargetUrl(request,response);
        request.getRequestDispatcher(url).forward(request, response);
    }
    

    

   

}
