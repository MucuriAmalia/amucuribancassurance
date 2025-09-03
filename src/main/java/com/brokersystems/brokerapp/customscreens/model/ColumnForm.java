package com.brokersystems.brokerapp.customscreens.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ColumnForm {

    private String name;

    private String type;

    private Integer length;

    private String mandatory;

    private String options;


}
