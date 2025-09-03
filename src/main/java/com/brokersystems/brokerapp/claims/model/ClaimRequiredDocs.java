package com.brokersystems.brokerapp.claims.model;

import com.brokersystems.brokerapp.common.Constants;
import com.brokersystems.brokerapp.setup.model.RequiredDocs;
import com.brokersystems.brokerapp.setup.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by peter on 3/8/2017.
 */
@Getter
@Entity
@Slf4j
@Table(name = "sys_brk_clm_req_docs")
public class ClaimRequiredDocs {

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "clm_reqd_id")
    private Long clmRequiredId;

    @Setter
    @ManyToOne
    @JoinColumn(name="clm_reqrd_req_id",nullable=false)
    private RequiredDocs requiredDoc;

    @Setter
    @ManyToOne
    @JoinColumn(name="clm_reqrd_clm_id",nullable=false)
    private ClaimBookings claimBookings;

    @Column(name = "clm_reqd_submitted")
    private String submitted;

    @Setter
    @ManyToOne
    @JoinColumn(name="clm_reqrd_user_id")
    private User userReceived;

    @Setter
    @Column(name = "clm_reqd_dt_received")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date dateReceived;

    @Column(name = "clm_reqd_doc_ref")
    private String docRefNo;

    @Setter
    @Column(name = "clm_reqd_dt_submit")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date dateSubmitted;

    @Column(name = "clm_reqd_remarks")
    private String remarks;

    @Column(name = "clm_reqd_file_name")
    private String fileName;

    @Column(name = "clm_reqd_verifier")
    private String checkSum;

    @Column(name = "clm_reqd_content_type")
    private String contentType;

    @Transient
    private String transType;

    @Setter
    @Transient
    private MultipartFile file;

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input != null) {
            System.out.println("Validating and sanitizing " + fieldName + ": Raw input = '" + input + "'");
            log.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        }
        if (input != null && !input.trim().isEmpty() && !input.matches(pattern)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setSubmitted(String submitted) {
        validateAndSanitize(submitted, Constants.NAME_PATTERN, "Submitted");
        this.submitted = submitted == null ? null : StringEscapeUtils.escapeHtml4(submitted.trim());
    }

    public void setDocRefNo(String docRefNo) {
        validateAndSanitize(docRefNo, Constants.REF_PATTERN, "Document Reference Number");
        this.docRefNo = docRefNo == null ? null : StringEscapeUtils.escapeHtml4(docRefNo.trim());
    }

    public void setRemarks(String remarks) {
        validateAndSanitize(remarks, Constants.DESC_PATTERN, "Remarks");
        this.remarks = remarks == null ? null : StringEscapeUtils.escapeHtml4(remarks.trim());
    }

    public void setFileName(String fileName) {
        validateAndSanitize(fileName, Constants.FILENAME_PATTERN, "File Name");
        this.fileName = fileName == null ? null : StringEscapeUtils.escapeHtml4(fileName.trim());
    }

    public void setCheckSum(String checkSum) {
        validateAndSanitize(checkSum, Constants.CHECKSUM_PATTERN, "Checksum");
        this.checkSum = checkSum == null ? null : StringEscapeUtils.escapeHtml4(checkSum.trim());
    }

    public void setContentType(String contentType) {
        validateAndSanitize(contentType, Constants.CONTENT_TYPE_PATTERN, "Content Type");
        this.contentType = contentType == null ? null : StringEscapeUtils.escapeHtml4(contentType.trim());
    }

    public void setTransType(String transType) {
        validateAndSanitize(transType, Constants.NAME_PATTERN, "Transaction Type");
        this.transType = transType == null ? null : StringEscapeUtils.escapeHtml4(transType.trim());
    }
}