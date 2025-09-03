package com.brokersystems.brokerapp.accounts.model;

import com.brokersystems.brokerapp.setup.model.OrgBranch;
import com.brokersystems.brokerapp.setup.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import javax.persistence.*;
import java.util.Date;
import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;

@Entity
@Table(name="sys_brk_account_yr_prds")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AccountYearPeriods {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="yp_Id")
    @Setter
    private Long ypId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="yp_bay_id",nullable = false)
    @Setter
    private AccountYears accountYears;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="yp_ob_id",nullable = false)
    @Setter
    private OrgBranch branch;

    @Column(name="yp_wef",nullable=false)
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    @Setter
    private Date monthStart;

    @Column(name="yp_wet",nullable=false)
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    @Setter
    private Date monthEnd;

    @Column(name="yp_state",nullable = false,length = 1)
    private String state;

    @Column(name="yp_transacted",length = 1)
    private String transacted;

    @Column(name = "yp_period_name",length = 20)
    private String periodName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="yp_closed_by")
    @Setter
    private User closedBy;

    @Column(name="yp_closed_date")
    @Setter
    private Date closedDate;

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input != null && !input.trim().isEmpty() && !input.matches(pattern)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setPeriodName(String periodName) {
        validateAndSanitize(periodName, NAME_PATTERN, "Period Name");
        this.periodName = periodName == null ? null : StringEscapeUtils.escapeHtml4(periodName.trim());
    }

    public void setState(String state) {
        validateAndSanitize(state, NAME_PATTERN, "State");
        this.state = state == null ? null : StringEscapeUtils.escapeHtml4(state.trim());
    }

    public void setTransacted(String transacted) {
        validateAndSanitize(transacted, NAME_PATTERN, "Transacted");
        this.transacted = transacted == null ? null : StringEscapeUtils.escapeHtml4(transacted.trim());
    }
}