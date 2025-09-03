package com.brokersystems.brokerapp.medical.model;

import com.brokersystems.brokerapp.setup.model.SubClassReqdDocs;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;

/**
 * Created by HP on 8/20/2017.
 */
@Entity
@Table(name = "sys_brk_par_req_docs")
public class MedParReqDocs {

    @Id
    @SequenceGenerator(name = "parDocSeq",sequenceName = "par_docs_seq",allocationSize=1)
    @GeneratedValue(generator = "parDocSeq")
    @Column(name="prd_id")
    private Long prdId;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="prd_par_id")
    private MedicalParTrans parTrans;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="prd_req_id")
    private SubClassReqdDocs reqdDocs;

    @Column(name = "prd_loc_name")
    private String uploadedFileName;

    @Column(name = "prd_verifier")
    private String checkSum;

    @Column(name = "prd_content_type")
    private String contentType;

    @Column(name = "prd_uploaded_dt")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date dateUploaded;

    @Transient
    private MultipartFile file;

    public Long getPrdId() {
        return prdId;
    }

    public void setPrdId(Long prdId) {
        this.prdId = prdId;
    }

    public MedicalParTrans getParTrans() {
        return parTrans;
    }

    public void setParTrans(MedicalParTrans parTrans) {
        this.parTrans = parTrans;
    }

    public SubClassReqdDocs getReqdDocs() {
        return reqdDocs;
    }

    public void setReqdDocs(SubClassReqdDocs reqdDocs) {
        this.reqdDocs = reqdDocs;
    }

    public String getUploadedFileName() {
        return uploadedFileName;
    }

    public void setUploadedFileName(String uploadedFileName) {
        this.uploadedFileName = uploadedFileName;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public Date getDateUploaded() {
        return dateUploaded;
    }

    public void setDateUploaded(Date dateUploaded) {
        this.dateUploaded = dateUploaded;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MedParReqDocs that = (MedParReqDocs) o;

        if (parTrans.getParId() != null ? !parTrans.getParId().equals(that.parTrans.getParId()) : that.parTrans.getParId() != null) return false;
        return reqdDocs.getSclReqrdId() != null ? reqdDocs.getSclReqrdId().equals(that.reqdDocs.getSclReqrdId()) : that.reqdDocs.getSclReqrdId() == null;

    }

    @Override
    public int hashCode() {
        int result = parTrans.getParId() != null ? parTrans.getParId().hashCode() : 0;
        result = 31 * result + (reqdDocs.getSclReqrdId() != null ? reqdDocs.getSclReqrdId().hashCode() : 0);
        return result;
    }
}
