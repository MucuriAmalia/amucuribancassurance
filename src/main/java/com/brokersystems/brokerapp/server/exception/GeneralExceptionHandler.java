package com.brokersystems.brokerapp.server.exception;

import javax.mail.MessagingException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GeneralExceptionHandler {
	
	
	@ExceptionHandler(Exception.class)
	    ResponseEntity handle(Exception   e) {
		  e.printStackTrace();
	        return new ResponseEntity(e.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
	    }
	
	
	@SuppressWarnings("unchecked")
	@ExceptionHandler(MessagingException.class)
    ResponseEntity messagingError(MessagingException   e) {
		e.printStackTrace();
        return new ResponseEntity(e.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
	
	@SuppressWarnings("unchecked")
	@ExceptionHandler(BadRequestException.class)
    ResponseEntity handleError(BadRequestException   e) {
		e.printStackTrace();
        return new ResponseEntity(e.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
	
	
	@ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity handle(IllegalArgumentException   e) {
		e.printStackTrace();
        return new ResponseEntity(e.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
	 

    @ExceptionHandler({DataAccessException.class})
    ResponseEntity databaseError(DataAccessException   e) {
    	       e.printStackTrace();
    		  return new ResponseEntity(e.getMostSpecificCause().getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    	
    }
    

}
