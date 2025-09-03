package com.brokersystems.brokerapp.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SecurityHeaderFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Hide server version information
        httpResponse.setHeader("Server", "WebServer");

        // Remove X-Powered-By header
        httpResponse.setHeader("X-Powered-By", "");

        // Additional security headers
        httpResponse.setHeader("X-Frame-Options", "DENY");
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");
        httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
        httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

        // HSTS Header - check for HTTPS properly in AWS environment
        if (httpRequest.isSecure() || isHttpsFromLoadBalancer(httpRequest)) {
            httpResponse.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        }

        // Cache control for sensitive pages
        if (httpRequest.getRequestURI().contains("/login") ||
                httpRequest.getRequestURI().contains("/protected")) {
            httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            httpResponse.setHeader("Pragma", "no-cache");
            httpResponse.setHeader("Expires", "0");
        }

        chain.doFilter(request, response);
    }

    /**
     * Check if request came through HTTPS via AWS Load Balancer
     * ALB/ELB terminate SSL and forward as HTTP with special headers
     */
    private boolean isHttpsFromLoadBalancer(HttpServletRequest request) {
        // Check AWS ALB/ELB forwarded headers
        String forwardedProto = request.getHeader("X-Forwarded-Proto");
        String forwardedProtocol = request.getHeader("X-Forwarded-Protocol");
        String forwardedSSL = request.getHeader("X-Forwarded-Ssl");

        // CloudFront header
        String cloudfrontProto = request.getHeader("CloudFront-Forwarded-Proto");

        return "https".equalsIgnoreCase(forwardedProto) ||
                "https".equalsIgnoreCase(forwardedProtocol) ||
                "on".equalsIgnoreCase(forwardedSSL) ||
                "https".equalsIgnoreCase(cloudfrontProto);
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}