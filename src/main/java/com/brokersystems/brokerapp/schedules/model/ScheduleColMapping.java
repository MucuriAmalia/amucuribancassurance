package com.brokersystems.brokerapp.schedules.model;

import java.util.List;

/**
 * Created by peter on 2/25/2017.
 */
public class ScheduleColMapping {

    private final String key;
    private final String column;
    private final String colType;
    private final String options;
    private final String premItem;
    private final Long premCode;
    private  Integer order;

    public ScheduleColMapping(String key, String column, String colType, String options,String premItem, Long premCode) {
        this.key = key;
        this.column = column;
        this.colType = colType;
        this.options = options;
        this.premItem = premItem;
        this.premCode = premCode;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getKey() {
        return key;
    }


    public String getColumn() {
        return column;
    }


    public String getColType() {
        return colType;
    }


    public String getOptions() {
        return options;
    }


    public String getPremItem() {
        return premItem;
    }

    public Long getPremCode() {
        return premCode;
    }

    @Override
    public String toString() {
        return "ScheduleColMapping{" +
                "key='" + key + '\'' +
                ", column='" + column + '\'' +
                ", colType='" + colType + '\'' +
                ", options='" + options + '\'' +
                ", premItem='" + premItem + '\'' +
                ", premCode=" + premCode +
                ", order=" + order +
                '}';
    }
}
