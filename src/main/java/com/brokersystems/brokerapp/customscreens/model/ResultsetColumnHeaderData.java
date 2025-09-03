package com.brokersystems.brokerapp.customscreens.model;

import com.brokersystems.brokerapp.server.exception.BadRequestException;

import java.util.ArrayList;
import java.util.List;

public class ResultsetColumnHeaderData {

    private final String columnName;
    private String columnType;
    private final Long columnLength;
    private final String columnDisplayType;
    private final boolean isColumnNullable;
    @SuppressWarnings("unused")
    private final boolean isColumnPrimaryKey;

    private final List<ResultsetColumnValueData> columnValues;
    private final String columnCode;

    public static ResultsetColumnHeaderData basic(final String columnName, final String columnType) throws BadRequestException {

        final Long columnLength = null;
        final boolean columnNullable = false;
        final boolean columnIsPrimaryKey = false;
        final List<ResultsetColumnValueData> columnValues = new ArrayList<>();
        final String columnCode = null;
        return new ResultsetColumnHeaderData(columnName, columnType, columnLength, columnNullable, columnIsPrimaryKey, columnValues, columnCode);
    }

    public static ResultsetColumnHeaderData detailed(final String columnName, final String columnType, final Long columnLength,
                                                     final boolean columnNullable, final boolean columnIsPrimaryKey, final List<ResultsetColumnValueData> columnValues,
                                                     final String columnCode) throws BadRequestException {
        return new ResultsetColumnHeaderData(columnName, columnType, columnLength, columnNullable, columnIsPrimaryKey, columnValues, columnCode);
    }

    private ResultsetColumnHeaderData(final String columnName, final String columnType, final Long columnLength,
                                      final boolean columnNullable, final boolean columnIsPrimaryKey, final List<ResultsetColumnValueData> columnValues,
                                      final String columnCode) throws BadRequestException {
        this.columnName = columnName;
        this.columnType = columnType;
        this.columnLength = columnLength;
        this.isColumnNullable = columnNullable;
        this.isColumnPrimaryKey = columnIsPrimaryKey;
        this.columnValues = columnValues;
        this.columnCode = columnCode;

        // Adjust column types based on known mappings
        adjustColumnTypes();

        String displayType = null;
        if (this.columnCode == null) {
            if (isString()) {
                displayType = "STRING";
            } else if (isAnyInteger()) {
                displayType = "INTEGER";
            } else if (isDate()) {
                displayType = "DATE";
            } else if (isDateTime() || "timestamp".equalsIgnoreCase(this.columnType)) {
                displayType = "DATETIME";
            } else if (isDecimal()) {
                displayType = "DECIMAL";
            } else if (isAnyText()) {
                displayType = "TEXT";
            } else if (isBit() || isBoolean()) {  // Handle boolean and bit types
                displayType = "BOOLEAN";
            } else if (isNumeric()) {
                displayType = "NUMERIC";
            } else {
                throw new BadRequestException("Invalid Lookup Type: " + this.columnType + " - Column Name: " + this.columnName);
            }
        } else {
            if (isInt()) {
                displayType = "CODELOOKUP";
            } else if (isVarchar()) {
                displayType = "CODEVALUE";
            } else {
                throw new BadRequestException("Invalid Lookup Type: " + this.columnType + " - Column Name: " + this.columnName);
            }
        }

        this.columnDisplayType = displayType;
    }

    private void adjustColumnTypes() {
        switch (this.columnType) {
            case "NEWDECIMAL":
                this.columnType = "DECIMAL";
                break;
            case "CLOB":
            case "ENUM":
            case "SET":
                this.columnType = "varchar";
                break;
            case "LONGLONG":
            case "int8":
                this.columnType = "bigint";
                break;
            case "SHORT":
                this.columnType = "smallint";
                break;
            case "TINY":
                this.columnType = "tinyint";
                break;
            case "INT24":
            case "int4":
                this.columnType = "int";
                break;
            case "TIMESTAMP":
                this.columnType = "DATETIME";
                break;
            case "numeric":
                this.columnType = "NUMERIC";
                break;
            case "bool":  // Adjust bool type to BOOLEAN
                this.columnType = "BOOLEAN";
                break;
            default:
                break;
        }
    }

    public boolean isNamed(final String columnName) {
        return this.columnName.equalsIgnoreCase(columnName);
    }

    private boolean isAnyText() {
        return isText() || isTinyText() || isMediumText() || isLongText();
    }

    private boolean isText() {
        return "text".equalsIgnoreCase(this.columnType);
    }

    private boolean isTinyText() {
        return "tinytext".equalsIgnoreCase(this.columnType);
    }

    private boolean isMediumText() {
        return "mediumtext".equalsIgnoreCase(this.columnType);
    }

    private boolean isLongText() {
        return "longtext".equalsIgnoreCase(this.columnType);
    }

    private boolean isDecimal() {
        return "decimal".equalsIgnoreCase(this.columnType) || "NEWDECIMAL".equalsIgnoreCase(this.columnType);
    }

    private boolean isNumeric() {
        return "numeric".equalsIgnoreCase(this.columnType);
    }

    private boolean isDate() {
        return "date".equalsIgnoreCase(this.columnType);
    }

    private boolean isDateTime() {
        return "datetime".equalsIgnoreCase(this.columnType);
    }

    public boolean isString() {
        return isVarchar() || isChar();
    }

    private boolean isChar() {
        return "char".equalsIgnoreCase(this.columnType);
    }

    private boolean isVarchar() {
        return "varchar".equalsIgnoreCase(this.columnType) || "character varying".equalsIgnoreCase(this.columnType);
    }

    private boolean isAnyInteger() {
        return isInt() || isSmallInt() || isTinyInt() || isMediumInt() || isBigInt() || isLong();
    }

    private boolean isInt() {
        return "int".equalsIgnoreCase(this.columnType);
    }

    private boolean isSmallInt() {
        return "smallint".equalsIgnoreCase(this.columnType);
    }

    private boolean isTinyInt() {
        return "tinyint".equalsIgnoreCase(this.columnType);
    }

    private boolean isMediumInt() {
        return "mediumint".equalsIgnoreCase(this.columnType);
    }

    private boolean isBigInt() {
        return "bigint".equalsIgnoreCase(this.columnType);
    }

    private boolean isLong() {
        return "LONG".equalsIgnoreCase(this.columnType) || "LONGLONG".equalsIgnoreCase(this.columnType);
    }

    private boolean isBit() {
        return "bit".equalsIgnoreCase(this.columnType);
    }

    private boolean isBoolean() {
        return "BOOLEAN".equalsIgnoreCase(this.columnType);  // New method to handle boolean type
    }

    public String getColumnName() {
        return this.columnName;
    }

    public String getColumnType() {
        return this.columnType;
    }

    public Long getColumnLength() {
        return this.columnLength;
    }

    public String getColumnDisplayType() {
        return this.columnDisplayType;
    }

    public boolean isDateDisplayType() {
        return "DATE".equalsIgnoreCase(this.columnDisplayType);
    }

    public boolean isDateTimeDisplayType() {
        return "DATETIME".equalsIgnoreCase(this.columnDisplayType);
    }

    public boolean isIntegerDisplayType() {
        return "INTEGER".equalsIgnoreCase(this.columnDisplayType);
    }

    public boolean isDecimalDisplayType() {
        return "DECIMAL".equalsIgnoreCase(this.columnDisplayType);
    }

    public boolean isBooleanDisplayType() {
        return "BOOLEAN".equalsIgnoreCase(this.columnDisplayType);
    }

    public boolean isNumericDisplayType() {
        return "NUMERIC".equalsIgnoreCase(this.columnDisplayType);
    }

    public boolean isCodeValueDisplayType() {
        return "CODEVALUE".equalsIgnoreCase(this.columnDisplayType);
    }

    public boolean isCodeLookupDisplayType() {
        return "CODELOOKUP".equalsIgnoreCase(this.columnDisplayType);
    }

    public boolean isMandatory() {
        return !isOptional();
    }

    public boolean isOptional() {
        return this.isColumnNullable;
    }

    public boolean hasColumnValues() {
        return !this.columnValues.isEmpty();
    }

    public boolean isColumnValueAllowed(final String match) {
        boolean allowed = false;
        for (final ResultsetColumnValueData allowedValue : this.columnValues) {
            if (allowedValue.matches(match)) {
                allowed = true;
            }
        }
        return allowed;
    }

    public boolean isColumnValueNotAllowed(final String match) {
        return !isColumnValueAllowed(match);
    }

    public boolean isColumnCodeNotAllowed(final Integer match) {
        return !isColumnCodeAllowed(match);
    }

    public boolean isColumnCodeAllowed(final Integer match) {
        boolean allowed = false;
        for (final ResultsetColumnValueData allowedValue : this.columnValues) {
            if (allowedValue.codeMatches(match)) {
                allowed = true;
            }
        }
        return allowed;
    }

    public boolean isEmpty() {
        return this.columnValues.isEmpty();
    }

    public String getColumnCode() {
        return this.columnCode;
    }

    public boolean hasColumnCode() {
        return this.columnCode != null;
    }

    public boolean matchesColumnCode(final String match) {
        if (match == null || match.isEmpty()) {
            return false;
        }
        return this.columnCode != null && match.equalsIgnoreCase(this.columnCode);
    }

    public boolean matchesColumnName(final String match) {
        if (match == null || match.isEmpty()) {
            return false;
        }
        return match.equalsIgnoreCase(this.columnName);
    }
}
