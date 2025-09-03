package com.brokersystems.brokerapp.claims.model;

import com.brokersystems.brokerapp.accounts.model.Payees;
import com.brokersystems.brokerapp.setup.model.BankAccounts;
import com.brokersystems.brokerapp.setup.model.Currencies;
import com.brokersystems.brokerapp.setup.model.PaymentModes;
import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.trans.model.ChequeTrans;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.REF_PATTERN;


@Getter
@Entity
@Slf4j
@Table(name = "sys_brk_clm_pymnts")
public class ClaimPayments {

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "clm_pymnt_id")
    private Long clmPymntId;

    @Setter
    @Column(name = "clm_pymt_created_dt", nullable = false)
    private Date transDate;

    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clm_pymt_created_by", nullable = false)
    private User user;

    @Column(name = "clm_pymt_auth")
    private String authorised;

    @Setter
    @Column(name = "clm_pymt_amount")
    private BigDecimal clmPymntAmount;

    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clm_pymt_auth_by")
    private User authBy;

    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clm_pymt_clmnt_id")
    private ClaimClaimants claimClaimants;

    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clm_pymt_spd_id")
    private ServiceProviderDef serviceProviderDef;

    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clm_pymt_clm_id", nullable = false)
    private ClaimBookings claimBookings;

    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clm_pymt_ba_acc")
    private BankAccounts bankAccounts;

    @Column(name = "clm_pymt_trans_type", length = 10)
    private String tranType;

    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clm_pymt_pm_id")
    private PaymentModes paymentModes;

    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clm_pymt_payee")
    private Payees payee;

    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clm_pymt_cur_id")
    private Currencies currencies;

    @Setter
    @Column(name = "clm_pymt_bcur_amount")
    private BigDecimal clmPymntBcurAmount;

    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clm_pymt_chq_id")
    private ChequeTrans chequeTrans;

    @Column(name = "clm_pymt_invoice_no", length = 20)
    private String invoiceNo;

    @Setter
    @Column(name = "clm_pymt_inv_date")
    private Date invoiceDate;

    @Setter
    @Column(name = "clm_pymt_auth_date")
    private Date authDate;

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input != null) {
            System.out.println("Validating and sanitizing " + fieldName + ": Raw input = '" + input + "'");
            log.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        }
        if (input != null && !input.trim().isEmpty() && !input.matches(pattern)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setInvoiceNo(String invoiceNo) {
        validateAndSanitize(invoiceNo, REF_PATTERN, "Invoice Number");
        this.invoiceNo = invoiceNo == null ? null : StringEscapeUtils.escapeHtml4(invoiceNo.trim());
    }

    public void setAuthorised(String authorised) {
        validateAndSanitize(authorised, NAME_PATTERN, "Authorised");
        this.authorised = authorised == null ? null : StringEscapeUtils.escapeHtml4(authorised.trim());
    }

    public void setTranType(String tranType) {
        validateAndSanitize(tranType, NAME_PATTERN, "Transaction Type");
        this.tranType = tranType == null ? null : StringEscapeUtils.escapeHtml4(tranType.trim());
    }

}