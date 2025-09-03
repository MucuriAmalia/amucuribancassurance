package com.brokersystems.brokerapp.setup.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.brokersystems.brokerapp.common.Constants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountsDTO {

    private static final Logger logger = LoggerFactory.getLogger(AccountsDTO.class);

    private Long acctId;                    // Keep - you're setting this


    private Long acctTypeId;

    private String name;                    // Keep - you're setting this
    private String shtDesc;                 // Keep - you're setting this

    @JsonIgnore
    private String address;                 // Hide
    @JsonIgnore
    private String physaddress;             // Hide
    @JsonIgnore
    private String contactTitle;            // Hide
    @JsonIgnore
    private String contactPerson;           // Hide

    private String pinNo;                   // Hide
    @JsonIgnore
    private String licenseNumber;           // Hide
    @JsonIgnore
    private String email;                   // Hide

    private String phoneNo;                 // Hide

    @JsonIgnore
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date dob;                       // Hide

    @JsonIgnore
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wef;                       // Hide

    @JsonIgnore
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wet;                       // Hide

    private String status;                  // Hide
    @JsonIgnore
    private String bankAccount;             // Hide
    @JsonIgnore
    private String paybillNumber;           // Hide
    @JsonIgnore
    private String payTelNo;                // Hide
    @JsonIgnore
    private String createdBy;               // Hide

    private String updatedBy;
    @JsonIgnore
    private String logo;                    // Hide
    @JsonIgnore
    private MultipartFile file;             // Hide
    @JsonIgnore
    private Long bankId;                    // Hide
    @JsonIgnore
    private Long bankBranchId;              // Hide
    @JsonIgnore
    private Long branchId;                  // Hide
    @JsonIgnore
    private Long parentAcctId;              // Hide
    @JsonIgnore
    private String parentAcctName;          // Hide
    @JsonIgnore
    private Long paymentModeId;             // Hide
    @JsonIgnore
    private String bankBranchName;          // Hide
    @JsonIgnore
    private String bankName;                // Hide
    @JsonIgnore
    private String paymentMode;             // Hide
    @JsonIgnore
    private String branchName;              // Hide

    private String accountTypeName;         // Hide

    private String accountTypeId;           // Hide
    @JsonIgnore
    private String underwriterApiUrl;       // Hide
    @JsonIgnore
    private String integrationType;         // Hide
    @JsonIgnore
    private String insuranceType;           // Hide
    @JsonIgnore
    private String underwriterApiUsername;  // Hide
    @JsonIgnore
    private String underwriterApiPassword;  // Hide
    @JsonIgnore
    private String payableAccountName;      // Hide
    @JsonIgnore
    private String receivableAccountName;   // Hide
    @JsonIgnore
    private Long payableAccountId;          // Hide
    @JsonIgnore
    private Long receivableAccountId;       // Hide
    @JsonIgnore
    private String payableAccount;          // Hide
    @JsonIgnore
    private String receivableAccount;       // Hide
    @JsonIgnore
    private String whtxAccountName;         // Hide
    @JsonIgnore
    private String adminWhtxAccountName;    // Hide
    @JsonIgnore
    private String commAccountName;         // Hide
    @JsonIgnore
    private String adminAccountName;        // Hide
    @JsonIgnore
    private Long whtxAccountId;             // Hide
    @JsonIgnore
    private Long adminWhtxAccountId;        // Hide
    @JsonIgnore
    private Long commAccountId;             // Hide
    @JsonIgnore
    private Long adminAccountId;            // Hide
    @JsonIgnore
    private String whtxAccount;             // Hide
    @JsonIgnore
    private String commAccount;             // Hide
    @JsonIgnore
    private String adminAccount;            // Hide
    @JsonIgnore
    private String commissionEarning;       // Hide

    @JsonIgnore
    private String absaNo;                  // Keep - you're setting this

    @JsonIgnore
    private String idNumber;                // Hide
    @JsonIgnore
    private List<String> checkerIds;        // Hide


    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input == null) {
            return;
        }
        String trimmedInput = input.trim();
        if (!trimmedInput.isEmpty() && !trimmedInput.matches(pattern)) {
            throw new IllegalArgumentException(
                    String.format("Invalid characters in %s:", fieldName)
            );
        }
    }

    public void setName(String name) {
        validateAndSanitize(name, NAME_PATTERN, "Name");
        this.name = name == null ? null : StringEscapeUtils.escapeHtml4(name.trim());
    }

    public void setShtDesc(String shtDesc) {
        validateAndSanitize(shtDesc, DESC_PATTERN, "Short Description");
        this.shtDesc = shtDesc == null ? null : StringEscapeUtils.escapeHtml4(shtDesc.trim());
    }

    public void setAddress(String address) {
        validateAndSanitize(address, ADDRESS_PATTERN, "Address");
        this.address = address == null ? null : StringEscapeUtils.escapeHtml4(address.trim());
    }

    public void setPhysaddress(String physaddress) {
        validateAndSanitize(physaddress, ADDRESS_PATTERN, "Physical Address");
        this.physaddress = physaddress == null ? null : StringEscapeUtils.escapeHtml4(physaddress.trim());
    }

    public void setContactTitle(String contactTitle) {
        validateAndSanitize(contactTitle, DESC_PATTERN, "Contact Title");
        this.contactTitle = contactTitle == null ? null : StringEscapeUtils.escapeHtml4(contactTitle.trim());
    }

    public void setContactPerson(String contactPerson) {
        validateAndSanitize(contactPerson, NAME_PATTERN, "Contact Person");
        this.contactPerson = contactPerson == null ? null : StringEscapeUtils.escapeHtml4(contactPerson.trim());
    }

    public void setPinNo(String pinNo) {
        validateAndSanitize(pinNo, REF_PATTERN, "PIN Number");
        this.pinNo = pinNo == null ? null : StringEscapeUtils.escapeHtml4(pinNo.trim());
    }

    public void setLicenseNumber(String licenseNumber) {
        validateAndSanitize(licenseNumber, REF_PATTERN, "License Number");
        this.licenseNumber = licenseNumber == null ? null : StringEscapeUtils.escapeHtml4(licenseNumber.trim());
    }

    public void setEmail(String email) {
        validateAndSanitize(email, EMAIL_PATTERN, "Email");
        this.email = email == null ? null : StringEscapeUtils.escapeHtml4(email.trim());
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo == null ? null : StringEscapeUtils.escapeHtml4(phoneNo.trim());
    }

    public void setStatus(String status) {
        validateAndSanitize(status, REF_PATTERN, "Status");
        this.status = status == null ? null : StringEscapeUtils.escapeHtml4(status.trim());
    }

    public void setBankAccount(String bankAccount) {
        validateAndSanitize(bankAccount, REF_PATTERN, "Bank Account");
        this.bankAccount = bankAccount == null ? null : StringEscapeUtils.escapeHtml4(bankAccount.trim());
    }

    public void setPaybillNumber(String paybillNumber) {
        validateAndSanitize(paybillNumber, REF_PATTERN, "Paybill Number");
        this.paybillNumber = paybillNumber == null ? null : StringEscapeUtils.escapeHtml4(paybillNumber.trim());
    }

    public void setPayTelNo(String payTelNo) {
        validateAndSanitize(payTelNo, PHONE_PATTERN, "Pay Telephone Number");
        this.payTelNo = payTelNo == null ? null : StringEscapeUtils.escapeHtml4(payTelNo.trim());
    }

    public void setCreatedBy(String createdBy) {
        validateAndSanitize(createdBy, CREATED_BY_PATTERN, "Created By");
        this.createdBy = createdBy == null ? null : StringEscapeUtils.escapeHtml4(createdBy.trim());
    }

    public void setUpdatedBy(String updatedBy) {
        validateAndSanitize(updatedBy, CREATED_BY_PATTERN, "Updated By");
        this.updatedBy = updatedBy == null ? null : StringEscapeUtils.escapeHtml4(updatedBy.trim());
    }

    public void setLogo(String logo) {
        validateAndSanitize(logo, FILENAME_PATTERN, "Logo");
        this.logo = logo == null ? null : StringEscapeUtils.escapeHtml4(logo.trim());
    }

    public void setParentAcctName(String parentAcctName) {
        validateAndSanitize(parentAcctName, NAME_PATTERN, "Parent Account Name");
        this.parentAcctName = parentAcctName == null ? null : StringEscapeUtils.escapeHtml4(parentAcctName.trim());
    }

    public void setBankBranchName(String bankBranchName) {
        validateAndSanitize(bankBranchName, NAME_PATTERN, "Bank Branch Name");
        this.bankBranchName = bankBranchName == null ? null : StringEscapeUtils.escapeHtml4(bankBranchName.trim());
    }

    public void setBankName(String bankName) {
        validateAndSanitize(bankName, NAME_PATTERN, "Bank Name");
        this.bankName = bankName == null ? null : StringEscapeUtils.escapeHtml4(bankName.trim());
    }

    public void setPaymentMode(String paymentMode) {
        validateAndSanitize(paymentMode, DESC_PATTERN, "Payment Mode");
        this.paymentMode = paymentMode == null ? null : StringEscapeUtils.escapeHtml4(paymentMode.trim());
    }

    public void setBranchName(String branchName) {
        validateAndSanitize(branchName, NAME_PATTERN, "Branch Name");
        this.branchName = branchName == null ? null : StringEscapeUtils.escapeHtml4(branchName.trim());
    }

    public void setAccountTypeName(String accountTypeName) {
        validateAndSanitize(accountTypeName, NAME_PATTERN, "Account Type Name");
        this.accountTypeName = accountTypeName == null ? null : StringEscapeUtils.escapeHtml4(accountTypeName.trim());
    }

    public void setAccountTypeId(String accountTypeId) {
        validateAndSanitize(accountTypeId, REF_PATTERN, "Account Type ID");
        this.accountTypeId = accountTypeId == null ? null : StringEscapeUtils.escapeHtml4(accountTypeId.trim());
    }

    public void setUnderwriterApiUrl(String underwriterApiUrl) {
        validateAndSanitize(underwriterApiUrl, DESC_PATTERN, "Underwriter API URL");
        this.underwriterApiUrl = underwriterApiUrl == null ? null : StringEscapeUtils.escapeHtml4(underwriterApiUrl.trim());
    }

    public void setIntegrationType(String integrationType) {
        validateAndSanitize(integrationType, DESC_PATTERN, "Integration Type");
        this.integrationType = integrationType == null ? null : StringEscapeUtils.escapeHtml4(integrationType.trim());
    }

    public void setInsuranceType(String insuranceType) {
        validateAndSanitize(insuranceType, DESC_PATTERN, "Insurance Type");
        this.insuranceType = insuranceType == null ? null : StringEscapeUtils.escapeHtml4(insuranceType.trim());
    }

    public void setUnderwriterApiUsername(String underwriterApiUsername) {
        validateAndSanitize(underwriterApiUsername, DESC_PATTERN, "Underwriter API Username");
        this.underwriterApiUsername = underwriterApiUsername == null ? null : StringEscapeUtils.escapeHtml4(underwriterApiUsername.trim());
    }

    public void setUnderwriterApiPassword(String underwriterApiPassword) {
        validateAndSanitize(underwriterApiPassword, DESC_PATTERN, "Underwriter API Password");
        this.underwriterApiPassword = underwriterApiPassword == null ? null : StringEscapeUtils.escapeHtml4(underwriterApiPassword.trim());
    }

    public void setPayableAccountName(String payableAccountName) {
        validateAndSanitize(payableAccountName, NAME_PATTERN, "Payable Account Name");
        this.payableAccountName = payableAccountName == null ? null : StringEscapeUtils.escapeHtml4(payableAccountName.trim());
    }

    public void setReceivableAccountName(String receivableAccountName) {
        validateAndSanitize(receivableAccountName, NAME_PATTERN, "Receivable Account Name");
        this.receivableAccountName = receivableAccountName == null ? null : StringEscapeUtils.escapeHtml4(receivableAccountName.trim());
    }

    public void setPayableAccount(String payableAccount) {
        validateAndSanitize(payableAccount, REF_PATTERN, "Payable Account");
        this.payableAccount = payableAccount == null ? null : StringEscapeUtils.escapeHtml4(payableAccount.trim());
    }

    public void setReceivableAccount(String receivableAccount) {
        validateAndSanitize(receivableAccount, REF_PATTERN, "Receivable Account");
        this.receivableAccount = receivableAccount == null ? null : StringEscapeUtils.escapeHtml4(receivableAccount.trim());
    }

    public void setWhtxAccountName(String whtxAccountName) {
        validateAndSanitize(whtxAccountName, NAME_PATTERN, "WHTX Account Name");
        this.whtxAccountName = whtxAccountName == null ? null : StringEscapeUtils.escapeHtml4(whtxAccountName.trim());
    }

    public void setAdminWhtxAccountName(String adminWhtxAccountName) {
        validateAndSanitize(adminWhtxAccountName, NAME_PATTERN, "Admin WHTX Account Name");
        this.adminWhtxAccountName = adminWhtxAccountName == null ? null : StringEscapeUtils.escapeHtml4(adminWhtxAccountName.trim());
    }

    public void setCommAccountName(String commAccountName) {
        validateAndSanitize(commAccountName, NAME_PATTERN, "Commission Account Name");
        this.commAccountName = commAccountName == null ? null : StringEscapeUtils.escapeHtml4(commAccountName.trim());
    }

    public void setAdminAccountName(String adminAccountName) {
        validateAndSanitize(adminAccountName, NAME_PATTERN, "Admin Account Name");
        this.adminAccountName = adminAccountName == null ? null : StringEscapeUtils.escapeHtml4(adminAccountName.trim());
    }

    public void setWhtxAccount(String whtxAccount) {
        validateAndSanitize(whtxAccount, REF_PATTERN, "WHTX Account");
        this.whtxAccount = whtxAccount == null ? null : StringEscapeUtils.escapeHtml4(whtxAccount.trim());
    }

    public void setCommAccount(String commAccount) {
        validateAndSanitize(commAccount, REF_PATTERN, "Commission Account");
        this.commAccount = commAccount == null ? null : StringEscapeUtils.escapeHtml4(commAccount.trim());
    }

    public void setAdminAccount(String adminAccount) {
        validateAndSanitize(adminAccount, REF_PATTERN, "Admin Account");
        this.adminAccount = adminAccount == null ? null : StringEscapeUtils.escapeHtml4(adminAccount.trim());
    }

    public void setCommissionEarning(String commissionEarning) {
        validateAndSanitize(commissionEarning, DESC_PATTERN, "Commission Earning");
        this.commissionEarning = commissionEarning == null ? null : StringEscapeUtils.escapeHtml4(commissionEarning.trim());
    }

    public void setAbsaNo(String absaNo) {
        validateAndSanitize(absaNo, REF_PATTERN, "ABSA Number");
        this.absaNo = absaNo == null ? null : StringEscapeUtils.escapeHtml4(absaNo.trim());
    }

    public void setIdNumber(String idNumber) {
        validateAndSanitize(idNumber, ID_NUMBER_PATTERN, "ID Number");
        this.idNumber = idNumber == null ? null : StringEscapeUtils.escapeHtml4(idNumber.trim());
    }

    public void setCheckerIds(List<String> checkerIds) {
        if (checkerIds != null) {
            this.checkerIds = checkerIds.stream()
                    .map(id -> {
                        validateAndSanitize(id, ID_NUMBER_PATTERN, "Checker ID");
                        return id == null ? null : StringEscapeUtils.escapeHtml4(id.trim());
                    })
                    .collect(Collectors.toList());
        } else {
            this.checkerIds = null;
        }
    }

}
