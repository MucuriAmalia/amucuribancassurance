package com.brokersystems.brokerapp.customscreens.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "x_registered_table")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisteredTableModel {

    @Id
    @Column(name = "registered_table_name", length = 100, nullable = false)
    private String tableName;

    @Column(name = "application_table_name", length = 100, nullable = false)
    private String appTableName;

    @Column(name = "category", nullable = false)
    private Long category;

    @Column(name = "app_table_key_value", nullable = false)
    private Long keyValue;



}
