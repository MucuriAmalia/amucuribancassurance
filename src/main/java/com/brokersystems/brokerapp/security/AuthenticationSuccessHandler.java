package com.brokersystems.brokerapp.security;

import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.setup.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Component
public class AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Autowired
	private UserRepository userRepository;

	public AuthenticationSuccessHandler() {
		super();
		this.setAlwaysUseDefaultTargetUrl(true);
		this.setDefaultTargetUrl("/protected/home");
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
										HttpServletResponse response, Authentication authentication)
			throws IOException {
		updateLastLoginAndResetAttempts(authentication, request);
		redirectStrategy.sendRedirect(request, response, "/protected/home");
	}

	private void updateLastLoginAndResetAttempts(Authentication authentication, HttpServletRequest request) {
		if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			User user = userRepository.findByUsername(userDetails.getUsername());
			if (user != null) {
				System.out.println("User: " + user.getUsername() + " Last Login: " + user.getLastLogin());
				user.setLastLogin(new Date());
				user.setLastIP(request.getRemoteAddr());
				user.setFailedAttempts(0); // Reset failed attempts on successful login
				System.out.println("User: " + user.getUsername() + " Failed Attempts: " + user.getFailedAttempts());
				user.setAccountLocked(false); // Optionally unlock account on successful login
				userRepository.save(user);
			}
		}
	}
}



//package com.brokersystems.brokerapp.security;
//
//
//import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
//import com.brokersystems.brokerapp.setup.model.User;
//import com.brokersystems.brokerapp.setup.repository.UserRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.slf4j.MDC;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.DefaultRedirectStrategy;
//import org.springframework.security.web.RedirectStrategy;
//import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import java.io.IOException;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//
//@Component
//public class AuthenticationSuccessHandler extends
//		SavedRequestAwareAuthenticationSuccessHandler {
//
//
//
//	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
//    @Autowired
//    private UserRepository userRepository;
//
//	public AuthenticationSuccessHandler() {
//		super();
//		this.setAlwaysUseDefaultTargetUrl(true);
//		this.setDefaultTargetUrl("/protected/home");
//	}
//
//
// @Override
//	public void onAuthenticationSuccess(HttpServletRequest request,
//			HttpServletResponse response, Authentication authentication)
//			throws IOException {
//		updateLastlogin(authentication, request);
//		redirectStrategy.sendRedirect(request, response, "/protected/home");
//	}
//	private void updateLastlogin(Authentication authentication, HttpServletRequest request) {
//		if(authentication != null && authentication.getPrincipal() instanceof UserDetails) {
//			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//			User user = userRepository.findByUsername(userDetails.getUsername());
//			if(user != null) {
//				user.setLastLogin(new Date());
//				user.setLastIP(request.getRemoteAddr());
//				userRepository.save(user);
//			}
//		}
//	}
//
//
//
//}