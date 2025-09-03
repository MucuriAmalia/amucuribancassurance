package com.brokersystems.brokerapp.customscreens.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@ToString
public class TableForm {


   private String datatableName;

   private String  apptableName;

   private List<ColumnForm> columnFormList = new ArrayList<>();

   private Long appTableNameKey;


}
