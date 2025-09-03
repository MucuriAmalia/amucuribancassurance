package com.brokersystems.brokerapp.customscreens.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisteredTable {

    private String tableName;
    private Long category;
    private Long keyValue;
    private Long subId;
    private String subName;
    private List<ResultsetColumnHeaderData> columnHeaderDataList;
}
