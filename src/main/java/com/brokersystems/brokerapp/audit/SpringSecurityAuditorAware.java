package com.brokersystems.brokerapp.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class SpringSecurityAuditorAware implements AuditorAware<String> {

    private static final Logger logger = LoggerFactory.getLogger(SpringSecurityAuditorAware.class);

    @Override
    public String getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            logger.debug("No authenticated user found in SecurityContext, returning null for auditor");
            System.out.println("No authenticated user found in SecurityContext, returning null for auditor");
            return "system"; // Return "system" for audit fields when no user is authenticated
        }
        String username = authentication.getName();
        logger.debug("Returning auditor: {}", username);
        System.out.println("Returning auditor: " + username);
        return username;
    }
}