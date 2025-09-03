package com.brokersystems.brokerapp.security;

import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.setup.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class TokenSimpleUrlAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static final Logger logger = LoggerFactory.getLogger(TokenSimpleUrlAuthenticationFailureHandler.class);
    private static final int MAX_ATTEMPTS = 3;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        logger.debug("Processing authentication failure for request: {}", request.getRequestURI());
        String username = request.getParameter("j_username");
        logger.info("Attempted login with username: [masked]");

        setUseForward(true);
        saveException(request, exception);

        if (exception instanceof CredentialsExpiredException) {
            logger.warn("Credentials expired for username: [masked]");
            if (username != null) {
                request.setAttribute("username", username);
            }
            setDefaultFailureUrl("/changepass");
        } else if (exception instanceof LockedException) {
            logger.warn("Account locked for username: [masked]");
            setDefaultFailureUrl("/login?error=true&locked=true");
        } else {
            updateFailedAttempts(username);
            setDefaultFailureUrl("/login?error=true");
        }
        super.onAuthenticationFailure(request, response, exception);
    }

    private void updateFailedAttempts(String username) {
        if (username == null || username.trim().isEmpty()) {
            logger.warn("Username is null or empty, skipping failed attempt update");
            return;
        }

        logger.debug("Querying user with username: [masked]");
        User user = userRepository.findByUsername(username);
        if (user == null) {
            logger.warn("No user found for username: [masked]");
            return;
        }

        if (user.getAccountLocked()) {
            logger.info("User account is already locked: [masked]");
            return;
        }

        int attempts = user.getFailedAttempts() + 1;
        logger.info("Incrementing failed attempts for user: [masked], new count: {}", attempts);
        user.setFailedAttempts(attempts);

        if (attempts >= MAX_ATTEMPTS) {
            logger.warn("Max failed attempts reached for user: [masked], locking account");
            user.setAccountLocked(true);
        }

        // Validate fields to prevent constraint violations
        if (user.getSignature() != null && user.getSignature().length() > 160) {
            logger.error("Signature length exceeds 160 characters for user: [masked]");
            throw new IllegalArgumentException("Signature exceeds 160 characters");
        }
        if (user.getSignatureContentType() != null && user.getSignatureContentType().length() > 20) {
            logger.error("Signature content type length exceeds 20 characters for user: [masked]");
            throw new IllegalArgumentException("Signature content type exceeds 20 characters");
        }

        try {
            logger.debug("Saving user: [masked]");
            userRepository.save(user);
            logger.info("Successfully saved user: [masked] with failed_attempts: {}, account_locked: {}",
                    user.getFailedAttempts(), user.getAccountLocked());
        } catch (Exception e) {
            logger.error("Failed to save user: [masked], redirecting to error page", e);
            setDefaultFailureUrl("/error");
        }
    }
}