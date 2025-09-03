package com.brokersystems.brokerapp.nav.controllers;

import com.brokersystems.brokerapp.server.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityNotFoundException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.sql.SQLException;

@ControllerAdvice
@Order(1000)
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ModelAndView buildErrorView(String errorMsg, String userFriendlyMessage,
                                        HttpServletRequest request, Exception ex) {
        logger.error("Global Handler - {} at: {}", errorMsg, request.getRequestURI(), ex);

        ModelAndView mv = new ModelAndView("errorPage");
        mv.addObject("errorMsg", errorMsg);
        mv.addObject("userFriendlyMessage", userFriendlyMessage);
        return mv;
    }


    // BUSINESS LOGIC EXCEPTIONS
    @ExceptionHandler(BadRequestException.class)
    public ModelAndView handleBadRequestException(BadRequestException ex, HttpServletRequest request) {
        return buildErrorView("Business Logic Error ",
                ex.getMessage(),
                request, ex);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        return buildErrorView("Access Denied ",
                "You don't have permission to perform this action",
                request, ex);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        return buildErrorView("Invalid Input ",
                "Invalid parameter provided. Please check your input",
                request, ex);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ModelAndView handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
        return buildErrorView("Validation Error ",
                "Data validation failed. Please check your input",
                request, ex);
    }


    // SYSTEM EXCEPTIONS
    @ExceptionHandler(NullPointerException.class)
    public ModelAndView handleNullPointerException(NullPointerException ex, HttpServletRequest request) {
        return buildErrorView("System Error; Null Pointer exception ",
                "A system error occurred. Please contact support",
                request, ex);
    }

    @ExceptionHandler(IOException.class)
    public ModelAndView handleIOException(IOException ex, HttpServletRequest request) {
        return buildErrorView("File Processing Error ",
                "File operation failed. Please contact support",
                request, ex);
    }


    // DATABASE EXCEPTIONS
    @ExceptionHandler(SQLException.class)
    public ModelAndView handleSQLException(SQLException ex, HttpServletRequest request) {
        String errorMsg = "Database Error ";
        String userFriendlyMessage = "A database error occurred. Please contact support";

        String state = ex.getSQLState();
        if ("23505".equals(state)) {
            errorMsg = "Duplicate Record Error ";
            userFriendlyMessage = "This record already exists in the system";
        } else if ("23503".equals(state) || "23502".equals(state)) {
            errorMsg = "Data Integrity Error";
            userFriendlyMessage = "Required data is missing or invalid";
        }

        return buildErrorView(errorMsg, userFriendlyMessage, request, ex);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ModelAndView handleDataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest request) {
        String errorMsg = "Data Integrity Error ";
        String userFriendlyMessage = "Data integrity violation occurred";

        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("unique")) {
            errorMsg = "Duplicate Record Error";
            userFriendlyMessage = "This record already exists in the system";
        }

        return buildErrorView(errorMsg, userFriendlyMessage, request, ex);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ModelAndView handleDuplicateKeyException(DuplicateKeyException ex, HttpServletRequest request) {
        return buildErrorView("Duplicate Record Error ",
                "This record already exists in the system",
                request, ex);
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ModelAndView handleOptimisticLockException(OptimisticLockException ex, HttpServletRequest request) {
        return buildErrorView("Concurrency Error ",
                "Record was modified by another user. Please refresh and try again",
                request, ex);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ModelAndView handleTransactionSystemException(TransactionSystemException ex, HttpServletRequest request) {
        return buildErrorView("Transaction Error",
                "Transaction failed. Please contact support",
                request, ex);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ModelAndView handleEntityNotFoundException(EntityNotFoundException ex, HttpServletRequest request) {
        return buildErrorView("Record Not Found ",
                "The requested record was not found",
                request, ex);
    }

    @ExceptionHandler({DataAccessException.class, PersistenceException.class})
    public ModelAndView handleDataAccessExceptions(Exception ex, HttpServletRequest request) {
        return buildErrorView("Database Error ",
                "Please contact support",
                request, ex);
    }


    // NETWORK EXCEPTIONS
    @ExceptionHandler({UnknownHostException.class, ConnectException.class})
    public ModelAndView handleNetworkExceptions(Exception ex, HttpServletRequest request) {
        return buildErrorView("Network Error ",
                "External service is unreachable. Please contact support",
                request, ex);
    }

    // CATCH-ALL EXCEPTIONS
    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        return buildErrorView("Runtime Error ",
                "A system error occurred. Please contact support",
                request, ex);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception ex, HttpServletRequest request) {
        return buildErrorView("System Error ",
                "An unexpected error occurred. Please contact support",
                request, ex);
    }
}