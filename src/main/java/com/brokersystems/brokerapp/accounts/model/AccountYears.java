package com.brokersystems.brokerapp.accounts.model;

import com.brokersystems.brokerapp.setup.model.OrgBranch;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="sys_brk_account_years")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AccountYears {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="bay_Id")
    @Setter
    private Long bayId;

    @Column(name = "bay_year")
    @Setter
    private Long year;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="bay_ob_id",nullable = false)
    @Setter
    private OrgBranch branch;

    @Column(name="bay_wef",nullable=false)
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    @Setter
    private Date yearStart;

    @Column(name="bay_wet",nullable=false)
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    @Setter
    private Date yearEnd;

    @Column(name="bay_prds",nullable = false)
    @Setter
    private Integer totalPeriods;

    @Column(name="bay_state",nullable = false,length = 1)
    private String state;

    private void validateAndSanitize(String input) {
        if (input != null && !input.trim().isEmpty() && !input.matches(com.brokersystems.brokerapp.common.Constants.NAME_PATTERN)) {
            throw new IllegalArgumentException("Invalid characters in " + "State");
        }
    }

    public void setState(String state) {
        validateAndSanitize(state);
        this.state = state == null ? null : StringEscapeUtils.escapeHtml4(state.trim());
    }
}