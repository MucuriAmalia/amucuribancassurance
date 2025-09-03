package com.brokersystems.brokerapp.nav.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ErrorController {

    @RequestMapping(value = "errors", method = RequestMethod.GET)
    public ModelAndView renderErrorPage(HttpServletRequest httpRequest) {

        ModelAndView errorPage = new ModelAndView("errorPage");
//        String errorMsg = "";
//        String userFriendlyMessage = "";
//
//        int httpErrorCode = getErrorCode(httpRequest);
//        Throwable exception = getException(httpRequest);
//
//        switch (httpErrorCode) {
//            case 400: {
//                errorMsg = "Bad Request ";
//                userFriendlyMessage = "The request could not be processed. Please contact support.";
//                break;
//            }
//            case 401: {
//                errorMsg = "Unauthorized ";
//                userFriendlyMessage = "You are not authorized to access this resource. Please login and try again.";
//                break;
//            }
//            case 403: {
//                errorMsg = "Access Forbidden ";
//                userFriendlyMessage = "Access to this resource is forbidden. Please contact support.";
//                break;
//            }
//            case 404: {
//                errorMsg = "Page Not Found  ";
//                userFriendlyMessage = "The requested page was not found. Please contact support.";
//                break;
//            }
//            case 500: {
//                errorMsg = "Internal Server Error ";
//                userFriendlyMessage = "An internal server error occurred. Please contact support.";
//
//                // Handle specific internal server errors
//                if (exception != null) {
//                    String exceptionType = exception.getClass().getSimpleName();
//                    String exceptionMessage = exception.getMessage();
//
//                    if (exceptionType.contains("NullPointer")) {
//                        errorMsg = "System Error ";
//                        userFriendlyMessage = "A system error occurred while processing your request. Please contact support.";
//                    } else if (exceptionMessage != null && exceptionMessage.contains("UnknownHostException")) {
//                        errorMsg = "Connection Error ";
//                        userFriendlyMessage = "Unable to connect to external service. Please contact support.";
//                    } else if (exceptionMessage != null && exceptionMessage.contains("TransactionSystemException")) {
//                        errorMsg = "Transaction Error ";
//                        userFriendlyMessage = "Transaction could not be completed. Please contact support.";
//                    } else if (exceptionMessage != null && exceptionMessage.contains("rollbackOnly")) {
//                        errorMsg = "Data Error ";
//                        userFriendlyMessage = "The operation could not be completed due to data issues. Please check your data and try again.";
//                    }
//                }
//                break;
//            }
//            case 502: {
//                errorMsg = "Bad Gateway ";
//                userFriendlyMessage = "Server communication error. Please contact support.";
//                break;
//            }
//            case 503: {
//                errorMsg = "Service Unavailable ";
//                userFriendlyMessage = "The service is temporarily unavailable. Please try again later.";
//                break;
//            }
//            case 504: {
//                errorMsg = "Gateway Timeout ";
//                userFriendlyMessage = "The server took too long to respond. Please try again later.";
//                break;
//            }
//            default: {
//                errorMsg = "Unknown Error ";
//                userFriendlyMessage = "An unexpected error occurred. Please contact support if the problem persists.";
//                break;
//            }
//        }
//        if (!errorMsg.trim().equalsIgnoreCase("Page Not Found")) {
//            errorPage.addObject("errorMsg", errorMsg);
//            errorPage.addObject("userFriendlyMessage", userFriendlyMessage);
//        }


        return errorPage;
    }

    private int getErrorCode(HttpServletRequest httpRequest) {
        Integer statusCode = (Integer) httpRequest.getAttribute("javax.servlet.error.status_code");
        return statusCode != null ? statusCode : 500;
    }

    private Throwable getException(HttpServletRequest httpRequest) {
        return (Throwable) httpRequest.getAttribute("javax.servlet.error.exception");
    }
}