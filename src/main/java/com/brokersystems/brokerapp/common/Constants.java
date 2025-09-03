package com.brokersystems.brokerapp.common;

/**
 * Centralized constants for regex patterns used in input validation.
 */
public class Constants {

    //public static final String NAME_PATTERN = "^([a-zA-Z0-9\\s\\-'_\\,\\.\\&\\(\\)\\:]*|[a-zA-Z0-9._%+\\-]*@[a-zA-Z0-9.\\-]*\\.[a-zA-Z]{2,})$|^$";
    public static final String NAME_PATTERN = "^[^<>]*$";
    public static final String PHONE_PATTERN = "^[0-9\\+\\-\\s\\(\\)]*$"; // Digits, +, -, spaces, parentheses
    public static final String MOBILE_PATTERN = "^[0-9\\+\\-\\s\\(\\)]*$"; // Digits, +, -, spaces, parentheses
    public static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+\\-]*@[a-zA-Z0-9.\\-]*\\.[a-zA-Z]{2,}$|^$";
    //public static final String REF_PATTERN = "^[a-zA-Z0-9\\-\\/\\s\\,\\.\\&\\(\\)\\:]*$";
    public static final String REF_PATTERN =  "^[^<>]*$"; // Allows any character except < and >
    //public static final String DESC_PATTERN = "^[a-zA-Z0-9\\s\\,\\-\\.\\#\\(\\)\\&\\:]*$";
    public static final String DESC_PATTERN = "^[^<>]*$";
    //public static final String FILENAME_PATTERN = "^[a-zA-Z0-9_\\-\\.\\s\\(\\)]*$";
    public static final String FILENAME_PATTERN = "^[^<>]*$";
    //public static final String CHECKSUM_PATTERN = "^[a-zA-Z0-9]*$";
    public static final String CHECKSUM_PATTERN = "^[^<>]*$";
    //public static final String CONTENT_TYPE_PATTERN = "^[a-zA-Z0-9\\/\\-\\+]*$";
    public static final String CONTENT_TYPE_PATTERN = "^[^<>]*$";
    //public static final String ADDRESS_PATTERN = "^[a-zA-Z0-9\\s\\,\\-\\.\\#\\&\\(\\)\\:]*$"; // Alphanumeric, spaces, commas, hyphens, periods, hashes, ampersands, parentheses, colons
    public static final String ADDRESS_PATTERN = "^[^<>]*$";
    public static final String ID_NUMBER_PATTERN = "^[a-zA-Z0-9\\-\\s]*$"; // Alphanumeric, hyphens, spaces
    public static final String TIME_PATTERN = "^[^<>]*$";
    //public static final String CREATED_BY_PATTERN = "^([a-zA-Z0-9\\s\\-'_\\,\\.\\&\\(\\)\\:]*|[a-zA-Z0-9._%+\\-]*@[a-zA-Z0-9.\\-]*\\.[a-zA-Z]{2,})$|^$";
    public static final String CREATED_BY_PATTERN = "^[^<>]*$";
    public static final String PASSWORD_PATTERN = "^[^<>]*$";

}