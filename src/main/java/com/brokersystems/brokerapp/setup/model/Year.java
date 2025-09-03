package com.brokersystems.brokerapp.setup.model;

public class Year {

    String year;
    int date;

    public Year(String year, int date) {
        this.year = year;
        this.date = date;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }
}
