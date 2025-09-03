package com.brokersystems.brokerapp.security;

import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.setup.repository.UserRepository;
import com.brokersystems.brokerapp.setup.service.UserService;
import com.brokersystems.brokerapp.setup.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;

/**
 * Created by peter on 4/18/2017.
 */
@Component
public class ReloadUserPerRequestSecurity extends HttpSessionSecurityContextRepository {

    @Autowired
    private BrokerUserDetailsService userDetailsService;

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        final SecurityContext context = super.loadContext(requestResponseHolder);
        final Authentication authentication = context.getAuthentication();
        if(authentication==null) return context;
        if(authentication.getPrincipal()!=null) {
            final UserDetails userDetails = this.createNewUserDetailsFromPrincipal(authentication.getPrincipal());
            final UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            context.setAuthentication(newAuthentication);
        }
        return context;
    }

    private UserDetails createNewUserDetailsFromPrincipal(Object principal) {
        final UserDetails userDetails = (UserDetails) principal;
        final UserDetails user = this.userDetailsService.loadUserByUsername(userDetails.getUsername());
        return user;
    }
}
