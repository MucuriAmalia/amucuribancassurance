package com.brokersystems.brokerapp.setup.model;

import com.brokersystems.brokerapp.accounts.model.CoaSubAccounts;
import lombok.Data;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Data
@Table(
        name = "sys_brk_uw_com_rev_items",
        uniqueConstraints = @UniqueConstraint(columnNames = {"uw_acc_id", "rev_sub_id"})
)
public class CommRecv {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="com_rev_id")
    private Long revenueId;

    @ManyToOne
    @JoinColumn(name = "uw_acc_id")
    private AccountDef accountDef;


    @ManyToOne
    @JoinColumn(name="rev_sub_id",nullable=true)
    private SubClassDef prodGroup;

    @ManyToOne
    @JoinColumn(name="rev_dr_account")
    private CoaSubAccounts drAccount;

    @ManyToOne
    @JoinColumn(name="rev_cr_account")
    private CoaSubAccounts crAccount;
}

