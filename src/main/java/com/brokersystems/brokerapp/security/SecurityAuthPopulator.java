package com.brokersystems.brokerapp.security;

import com.brokersystems.brokerapp.setup.dto.UserDTO;
import com.brokersystems.brokerapp.setup.model.UserAuthority;
import com.brokersystems.brokerapp.setup.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;

@Service
public class SecurityAuthPopulator implements UserDetailsContextMapper {

    @Autowired
    private UserService userService;


    @Override
    public UserDetails mapUserFromContext(DirContextOperations dirContextOperations, String username, Collection<? extends GrantedAuthority> collection) {
        UserDTO user = userService.findByUserName(username);
        Set<UserAuthority> authorities = userService.getUserAuthorities(user.getId());
        return new User(username, "", true, true, true, true, authorities);
    }

    @Override
    public void mapUserToContext(UserDetails userDetails, DirContextAdapter dirContextAdapter) {

    }
}

