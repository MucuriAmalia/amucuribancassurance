package com.brokersystems.brokerapp.customscreens.model;

import java.util.List;

public class ResultsetRowData {

    private final List<String> row;

    public static ResultsetRowData create(final List<String> rowValues) {
        return new ResultsetRowData(rowValues);
    }

    private ResultsetRowData(final List<String> rowValues) {
        this.row = rowValues;
    }

    public List<String> getRow() {
        return this.row;
    }

    @Override
    public String toString() {
        return "ResultsetRowData{" +
                "row=" + row +
                '}';
    }
}
