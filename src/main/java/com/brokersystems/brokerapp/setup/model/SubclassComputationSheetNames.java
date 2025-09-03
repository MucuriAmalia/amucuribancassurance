package com.brokersystems.brokerapp.setup.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="sys_brk_sub_sheet_names")
public class SubclassComputationSheetNames {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="sub_sht_id")
    private Long shtId;

    @Column(name = "sub_sht_name", length = 100)
    private String sheetName;

    @ManyToOne
    @JoinColumn(name="sub_det_code",nullable=false)
    private BinderDetails binderDetails;

    @Column(name = "sub_compute_type", length = 1)
    private String computeType;


}
