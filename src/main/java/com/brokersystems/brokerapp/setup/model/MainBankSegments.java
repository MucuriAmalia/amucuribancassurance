package com.brokersystems.brokerapp.setup.model;

import javax.persistence.*;

@Entity
@Table(name="sys_brk_main_bank_segments")
public class MainBankSegments {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "main_bank_seg_id")
    private Long mainBankSegId;

    @Column(name = "main_bank_seg_name")
    private String mainBankSegName;


    public Long getMainBankSegId() {
        return mainBankSegId;
    }

    public void setMainBankSegId(Long mainBankSegId) {
        this.mainBankSegId = mainBankSegId;
    }

    public String getMainBankSegName() {
        return mainBankSegName;
    }

    public void setMainBankSegName(String mainBankSegName) {
        this.mainBankSegName = mainBankSegName;
    }

}
