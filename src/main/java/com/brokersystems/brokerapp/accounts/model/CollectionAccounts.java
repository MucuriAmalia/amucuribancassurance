package com.brokersystems.brokerapp.accounts.model;

import com.brokersystems.brokerapp.setup.model.Currencies;
import com.brokersystems.brokerapp.setup.model.PaymentModes;
import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import javax.persistence.*;
import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;

/**
 * Created by HP on 8/20/2017.
 */
@Entity
@Table(name="sys_brk_collect_accts")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CollectionAccounts {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="ca_Id")
    @Setter
    private Long caId;

    @ManyToOne
    @JoinColumn(name="ca_bb_id")
    @Setter
    private BankBranches bankBranches;

    @Column(name="ca_name",nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name="ca_acc_id")
    @Setter
    private CoaSubAccounts accounts;

    @ManyToOne
    @JoinColumn(name="ca_pm_id",nullable = false)
    @Setter
    private PaymentModes paymentModes;

    @ManyToOne
    @JoinColumn(name="ca_cur_id",nullable = false)
    @Setter
    private Currencies currencies;

    @Column(name="ca_status")
    @Setter
    private boolean status;

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input != null && !input.trim().isEmpty() && !input.matches(pattern)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setName(String name) {
        validateAndSanitize(name, NAME_PATTERN, "Name");
        this.name = name == null ? null : StringEscapeUtils.escapeHtml4(name.trim());
    }
}