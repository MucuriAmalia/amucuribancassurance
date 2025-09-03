package com.brokersystems.brokerapp.trans.model;

import com.brokersystems.brokerapp.setup.model.Currencies;
import com.brokersystems.brokerapp.setup.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@ToString
@Entity
@Table(name="sys_brk_commission_trans")
public class CommissionPayments {

    @Id
    @SequenceGenerator(name = "commissionProcessSeq",sequenceName = "commissionProcessSeq",allocationSize=1)
    @GeneratedValue(generator = "commissionProcessSeq")
    @Column(name="ct_id")
    private Long transId;


    @Column(name = "ct_date")
    private Date date;

    @Column(name = "ct_amount")
    private BigDecimal amount;

    @Column(name = "ct_whtx")
    private BigDecimal whtx;

    @Column(name = "ct_net_amt")
    private BigDecimal netAmount;

    @Column(name = "ct_paid")
    private String processed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ct_processed_by")
    private User processedBy;

    @Column(name = "ct_authorised")
    private String authorised;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ct_authorised_by")
    private User authorisedBy;

    @Column(name = "ct_auth_date")
    private Date authorisedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ct_debit_trans", nullable = true)
    private SystemTransactions debitTransaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ct_credit_trans")
    private SystemTransactions creditTransaction;

    @Column(name = "ct_trans_type", length = 20)
    private String transType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ct_cur_code")
    private Currencies currencies;


    @Column(name = "ct_withdrawn",length = 1)
    private String withDrawn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ct_withdrawn_by")
    private User withDrawnBy;

    @Column(name = "ct_withdrawn_date")
    private Date withDrawnDate;

    @Column(name = "ct_load_amount")
    private BigDecimal loadAmount;

    @Column(name = "ct_load_whtx")
    private BigDecimal loadWhtx;

    @Column(name = "ct_load_net_amt")
    private BigDecimal loadNetAmount;

    @Column(name = "ct_loaded",length = 1)
    private String loaded;


}
