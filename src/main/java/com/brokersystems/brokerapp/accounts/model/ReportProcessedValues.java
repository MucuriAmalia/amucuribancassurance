package com.brokersystems.brokerapp.accounts.model;

import com.brokersystems.brokerapp.setup.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="sys_brk_rpt_processed_vals")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReportProcessedValues {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="rpv_Id")
    @Setter
    private Long refId;

    @Column(name="rpv_row_Id")
    @Setter
    private Long refRowId;

    @Column(name="rpv_amount")
    @Setter
    private BigDecimal refAmount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="rpv_user_Id",nullable=false)
    @Setter
    private User user;

    @Column(name="rpv_bgt_amount")
    @Setter
    private BigDecimal budgetAmt;

    @Column(name="rpv_var_amount")
    @Setter
    private BigDecimal varianceAmt;

    @Column(name="rpv_var_perc")
    @Setter
    private BigDecimal variancePercent;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="rpv_clas_id")
    @Setter
    private AccountsBusinessClasses financialClasses;

    @Column(name="rpv_prev_amount")
    @Setter
    private BigDecimal refPrevAmount;

    @Column(name="rpv_prev_bgt_amount")
    @Setter
    private BigDecimal prevBudgetAmt;

    @Column(name="rpv_month_amount")
    @Setter
    private BigDecimal monthAmt;

    @Column(name="rpv_prev_month_amount")
    @Setter
    private BigDecimal prevMonthAmt;

    @Column(name="rpv_prev_mon_bgt")
    @Setter
    private BigDecimal prevMonthBudgetAmt;
}