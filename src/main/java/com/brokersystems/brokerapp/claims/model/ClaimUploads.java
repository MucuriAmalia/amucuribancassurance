package com.brokersystems.brokerapp.claims.model;

import com.brokersystems.brokerapp.common.Constants;
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
 * Created by peter on 3/11/2017.
 */
@Getter
@Entity
@Slf4j
@Table(name = "sys_brk_clm_Uploads")
public class ClaimUploads {

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "cu_id")
    private Long uploadId;

    @Setter
    @ManyToOne
    @JoinColumn(name="cu_clm_id",nullable=false)
    private ClaimBookings claimBookings;

    @Column(name = "cu_file_id")
    private String fileId;

    @Column(name = "cu_file_name")
    private String fileName;

    @Setter
    @ManyToOne
    @JoinColumn(name="cu_uploaded_by",nullable=false)
    private User uploadedBy;

    @Setter
    @Column(name = "cu_uploaded_dt")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date dateUploaded;

    @Column(name = "cu_comment",length = 2000)
    private String uploadedComment;

    @Column(name = "cu_verifier")
    private String checkSum;

    @Column(name = "rd_content_type")
    private String contentType;

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

    public void setFileId(String fileId) {
        validateAndSanitize(fileId, Constants.REF_PATTERN, "File ID");
        this.fileId = fileId == null ? null : StringEscapeUtils.escapeHtml4(fileId.trim());
    }

    public void setFileName(String fileName) {
        validateAndSanitize(fileName, Constants.FILENAME_PATTERN, "File Name");
        this.fileName = fileName == null ? null : StringEscapeUtils.escapeHtml4(fileName.trim());
    }

    public void setUploadedComment(String uploadedComment) {
        validateAndSanitize(uploadedComment, Constants.DESC_PATTERN, "Uploaded Comment");
        this.uploadedComment = uploadedComment == null ? null : StringEscapeUtils.escapeHtml4(uploadedComment.trim());
    }

    public void setCheckSum(String checkSum) {
        validateAndSanitize(checkSum, Constants.CHECKSUM_PATTERN, "Checksum");
        this.checkSum = checkSum == null ? null : StringEscapeUtils.escapeHtml4(checkSum.trim());
    }

    public void setContentType(String contentType) {
        validateAndSanitize(contentType, Constants.CONTENT_TYPE_PATTERN, "Content Type");
        this.contentType = contentType == null ? null : StringEscapeUtils.escapeHtml4(contentType.trim());
    }

}