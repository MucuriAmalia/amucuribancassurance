package com.brokersystems.brokerapp.developermodule.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
public class StretchyReportParameter implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long strpId;

    @ManyToOne
    @JoinColumn(name = "stretchy_parameter_id")
    @JsonIgnore
    private StretchyParameter stretchyParameter;

    @ManyToOne
    @JoinColumn(name = "stretchy_report_id")
    @JsonIgnore
    private StretchyReport stretchyReport;

    public StretchyReportParameter() {
    }

    public StretchyReportParameter(StretchyParameter stretchyParameter, StretchyReport stretchyReport) {
        this.stretchyParameter = stretchyParameter;
        this.stretchyReport = stretchyReport;
    }
}

