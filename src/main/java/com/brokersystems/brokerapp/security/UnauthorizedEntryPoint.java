package com.brokersystems.brokerapp.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * {@link AuthenticationEntryPoint} that rejects all requests. Login-like function is featured
 * in {@link TokenLoginAuthenticationFilter} and this does not perform or suggests any redirection.
 * This object is hit whenever user is not authorized (anonymous) and secured resource is requested.
 */
@Component
public final class UnauthorizedEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}
}