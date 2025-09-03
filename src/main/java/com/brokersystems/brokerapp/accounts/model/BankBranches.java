package com.brokersystems.brokerapp.accounts.model;

import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import javax.persistence.*;
import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.DESC_PATTERN;

/**
 * Created by peter on 4/10/2017.
 */
@Entity
@Table(name="sys_brk_bank_branches")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BankBranches {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="bb_Id")
    @Setter
    private Long bbId;

    @ManyToOne
    @JoinColumn(name="bb_bn_id")
    @Setter
    private Banks bank;

    @Column(name="bb_name",nullable = false,unique = true)
    private String branchName;

    @Column(name="bb_sht_desc",nullable = false,unique = true)
    private String branchShtDesc;

    @Column(name="bb_ref_code",nullable = false,unique = true)
    private String branchRefCode;

    @Column(name="bb_remarks",length = 1000)
    private String branchRemarks;

    @Column(name="bb_eft_support")
    @Setter
    private boolean eftSupported;

    @Column(name="bb_physical_address")
    private String physicalAddress;

    @Column(name="bb_postal_address")
    private String postalAddress;

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input != null && !input.trim().isEmpty() && !input.matches(pattern)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setBranchName(String branchName) {
        validateAndSanitize(branchName, NAME_PATTERN, "Branch Name");
        this.branchName = branchName == null ? null : StringEscapeUtils.escapeHtml4(branchName.trim());
    }

    public void setBranchShtDesc(String branchShtDesc) {
        validateAndSanitize(branchShtDesc, NAME_PATTERN, "Branch Short Description");
        this.branchShtDesc = branchShtDesc == null ? null : StringEscapeUtils.escapeHtml4(branchShtDesc.trim());
    }

    public void setBranchRefCode(String branchRefCode) {
        validateAndSanitize(branchRefCode, NAME_PATTERN, "Branch Reference Code");
        this.branchRefCode = branchRefCode == null ? null : StringEscapeUtils.escapeHtml4(branchRefCode.trim());
    }

    public void setBranchRemarks(String branchRemarks) {
        validateAndSanitize(branchRemarks, DESC_PATTERN, "Branch Remarks");
        this.branchRemarks = branchRemarks == null ? null : StringEscapeUtils.escapeHtml4(branchRemarks.trim());
    }

    public void setPhysicalAddress(String physicalAddress) {
        validateAndSanitize(physicalAddress, DESC_PATTERN, "Physical Address");
        this.physicalAddress = physicalAddress == null ? null : StringEscapeUtils.escapeHtml4(physicalAddress.trim());
    }

    public void setPostalAddress(String postalAddress) {
        validateAndSanitize(postalAddress, DESC_PATTERN, "Postal Address");
        this.postalAddress = postalAddress == null ? null : StringEscapeUtils.escapeHtml4(postalAddress.trim());
    }
}