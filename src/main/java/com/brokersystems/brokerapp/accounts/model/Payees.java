package com.brokersystems.brokerapp.accounts.model;

import com.brokersystems.brokerapp.setup.model.User;
import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="sys_brk_payees")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Payees {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="pay_Id")
    @Setter
    private Long payId;

    @Column(name="pay_full_name")
    private String fullName;

    @Column(name="pay_tel_no",length = 20)
    private String telNo;

    @Column(name="pay_email",length = 50)
    private String email;

    @Column(name="pay_mobile",length = 20)
    private String mobileNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="pay_created_by")
    @Setter
    private User createdBy;

    @Column(name="pay_created_dt")
    @Setter
    private Date createdDate;

    @Column(name="pay_status", length = 1)
    private String status;

    private void validateAndSanitize(String input, String fieldName) {
        if (input != null && !input.trim().isEmpty() && !input.matches(com.brokersystems.brokerapp.common.Constants.NAME_PATTERN)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setFullName(String fullName) {
        validateAndSanitize(fullName, "Full Name");
        this.fullName = fullName == null ? null : StringEscapeUtils.escapeHtml4(fullName.trim());
    }

    public void setTelNo(String telNo) {
        validateAndSanitize(telNo, "Telephone Number");
        this.telNo = telNo == null ? null : StringEscapeUtils.escapeHtml4(telNo.trim());
    }

    public void setEmail(String email) {
        validateAndSanitize(email, "Email");
        this.email = email == null ? null : StringEscapeUtils.escapeHtml4(email.trim());
    }

    public void setMobileNo(String mobileNo) {
        validateAndSanitize(mobileNo, "Mobile Number");
        this.mobileNo = mobileNo == null ? null : StringEscapeUtils.escapeHtml4(mobileNo.trim());
    }

    public void setStatus(String status) {
        validateAndSanitize(status, "Status");
        this.status = status == null ? null : StringEscapeUtils.escapeHtml4(status.trim());
    }
}