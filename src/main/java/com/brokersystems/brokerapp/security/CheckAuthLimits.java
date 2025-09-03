package com.brokersystems.brokerapp.security;

import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.setup.model.UserAuthority;
import com.brokersystems.brokerapp.setup.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Created by peter on 4/19/2017.
 */
@Component
public class CheckAuthLimits {


    @Autowired
    private BrokerUserDetailsService userDetailsService;


    public boolean checkAuthorizationLimits(String accessArea, BigDecimal limitAmount){
        final String username = SecurityContextHolder.getContext().getAuthentication().getName();
        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        final Collection<UserAuthority> authorities = (Collection<UserAuthority>) userDetails.getAuthorities();
        for(UserAuthority authority:authorities){
            if(StringUtils.equals(authority.getAuthority(),accessArea)){
                if(authority.getMinAmount()==null || authority.getMaxAmount()==null) return false;
                if(limitAmount==null){
                    limitAmount = BigDecimal.ZERO;
                }else
                limitAmount = limitAmount.abs();

                if((limitAmount.compareTo(authority.getMinAmount()) ==1 || limitAmount.compareTo(authority.getMinAmount()) ==0)
                && (limitAmount.compareTo(authority.getMaxAmount())==-1 || limitAmount.compareTo(authority.getMaxAmount())==0)){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkAuthorizationLimits(String accessArea){
        final String username = SecurityContextHolder.getContext().getAuthentication().getName();
        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        final Collection<UserAuthority> authorities = (Collection<UserAuthority>) userDetails.getAuthorities();
        final long count = authorities.stream().filter(a -> StringUtils.equals(a.getAuthority(), accessArea)).count();
        return count > 0;
    }


}

