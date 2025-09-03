package com.brokersystems.brokerapp.customscreens.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "x_registered_table_options")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisteredTableModelOptions implements Serializable {

    @Id
    @Column(name = "registered_table_name", length = 100, nullable = false)
    private String tableName;

    @Id
    @Column(name = "registered_option_column", length = 100, nullable = false)
    private String optionColumn;


    @Column(name = "registered_options", length = 500, nullable = false)
    private String registeredOptions;



}
