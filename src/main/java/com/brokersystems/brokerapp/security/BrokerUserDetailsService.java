package com.brokersystems.brokerapp.security;

import com.brokersystems.brokerapp.setup.dto.UserDTO;
import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.setup.model.UserAuthority;
import com.brokersystems.brokerapp.setup.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by HP on 11/4/2017.
 */
@Service("brokerUserDetailsService")
public class BrokerUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(BrokerUserDetailsService.class);

    private final UserService userService;

    @Autowired
    public BrokerUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        logger.debug("Loading user details for username: {}", username);

        final UserDTO userDTO = userService.findByUserName(username);
        if (userDTO == null) {
            logger.warn("UserDTO not found for username: {}", username);
            throw new UsernameNotFoundException("Username not found: " + username);
        }

        final User user = userService.findById(userDTO.getId());
        if (user == null) {
            logger.warn("User entity not found for username: {}", username);
            throw new UsernameNotFoundException("Username not found: " + username);
        }

        final boolean enabled = userDTO.getStatus() != null && userDTO.getStatus().equals("1");
        final boolean changePassword = userDTO.getResetPass() != null && userDTO.getResetPass().equals("Y");
        final boolean accountNonLocked =user.getAccountLocked()==null || !user.getAccountLocked();

        logger.info("User: {} - enabled: {}, credentialsNonExpired: {}, accountNonLocked: {}",
                username, enabled, !changePassword, accountNonLocked);

        if (!accountNonLocked) {
            logger.warn("User account is locked: {}", username);
        }

        return new BrokerUser(
                userDTO.getUsername(),
                userDTO.getPassword() != null ? userDTO.getPassword() : "",
                enabled,
                true,
                !changePassword,
                accountNonLocked,
                getGrantedAuthorities(userDTO.getId())
        );
    }

    private Set<UserAuthority> getGrantedAuthorities(final Long uid) {
        logger.debug("Fetching authorities for user ID: {}", uid);
        Set<UserAuthority> authorities = userService.getUserAuthorities(uid);
        logger.debug("Authorities for user ID {}: {}", uid, authorities);
        return authorities;
    }
}