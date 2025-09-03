package com.brokersystems.brokerapp.schedules.model;

import java.util.List;

/**
 * Created by peter on 2/25/2017.
 */
public class ScheduleBean {

    private Long colCount;
    private List<ScheduleColMapping> mappings;


    public Long getColCount() {
        return colCount;
    }

    public void setColCount(Long colCount) {
        this.colCount = colCount;
    }

    public List<ScheduleColMapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<ScheduleColMapping> mappings) {
        this.mappings = mappings;
    }

    @Override
    public String toString() {
        return "ScheduleBean{" +
                "colCount=" + colCount +
                ", mappings=" + mappings +
                '}';
    }
}
