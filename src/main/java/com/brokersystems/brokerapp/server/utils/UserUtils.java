package com.brokersystems.brokerapp.server.utils;

import com.brokersystems.brokerapp.setup.dto.UserDTO;
import com.brokersystems.brokerapp.setup.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import com.brokersystems.brokerapp.setup.model.User;


@Component
public class UserUtils {

	@Autowired
	private UserService userService;

	public User getCurrentUser() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()
				|| authentication instanceof AnonymousAuthenticationToken) {
			return null;
		}
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		UserDTO user = userService.findByUserName(userDetails.getUsername());
		return userService.findById(user.getId());
	}

}
