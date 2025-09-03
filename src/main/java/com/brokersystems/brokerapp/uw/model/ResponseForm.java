package com.brokersystems.brokerapp.uw.model;

public class ResponseForm {

     public ResponseForm(final String message) {
          this.message = message;
     }

     private final String message;

     public String getMessage() {
          return message;
     }
}
