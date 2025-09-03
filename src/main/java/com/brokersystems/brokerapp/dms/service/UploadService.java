package com.brokersystems.brokerapp.dms.service;

import com.brokersystems.brokerapp.claims.model.ClaimRequiredDocs;
import com.brokersystems.brokerapp.claims.model.ClaimUploads;
import com.brokersystems.brokerapp.dms.model.UploadBean;
import com.brokersystems.brokerapp.medical.model.MedParReqDocs;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.dto.SubClassReqdDocsDTO;

import java.io.File;
import java.util.List;

/**
 * Created by HP on 8/14/2017.
 */
public interface UploadService {


    void uploadRiskDocument(UploadBean uploadBean) throws BadRequestException;

    byte[] getRiskDocFileDetails(Long rdocId) throws BadRequestException;

    String getDocContentType(Long docId) throws BadRequestException;

    void deleteRiskDoc(Long docId) throws BadRequestException;

    void uploadGeneralClaimDoc(ClaimUploads upload) throws BadRequestException;

    public void uploadClaimReqDoc(ClaimRequiredDocs upload) throws BadRequestException;

    byte[] getGeneralClaimDoc(Long uploadId) throws BadRequestException;

    public byte[] getRequiredClaimDoc(Long clmRequiredId) throws BadRequestException;

    String getGeneralClmContentType(Long docId) throws BadRequestException;

    public String getReqClmContentType(Long docId) throws BadRequestException;

    void uploadClaimDocument(MedParReqDocs parReqDocs) throws BadRequestException;

    byte[] getMedClmDocFileDetails(Long rdocId) throws BadRequestException;

    String getClmDocContentTyoe(Long docId) throws BadRequestException;

    void deleteClmDoc(Long docId) throws BadRequestException;

    public void deleteClmReqDoc(Long docId) throws BadRequestException;

    public void deleteClmUploadDoc(Long docId) throws BadRequestException;

    void uploadClientDocument(UploadBean uploadBean) throws BadRequestException;

    void sybrinCreateCase(UploadBean uploadBean, String docType) throws BadRequestException;

    byte[] sybrinDocumentDetails(String docType, Long reqDocId, Long caseTypeId) throws BadRequestException;

    public void uploadProspectDocument(UploadBean uploadBean) throws BadRequestException;

    void uploadAccountDocument(UploadBean uploadBean) throws BadRequestException;

    byte[] getAccountDocument(Long adId) throws BadRequestException;

    String getAcctDocumentType(Long adId) throws BadRequestException;

    void deleteAcctDocument(Long adId) throws BadRequestException;

    byte[] getClientDocument(Long adId) throws BadRequestException;

    byte[] getProspectDocument(Long adId) throws BadRequestException;

    String getClientDocumentType(Long adId) throws BadRequestException;

    void deleteClntDocument(Long adId) throws BadRequestException;

    void deletePrspctDocument(Long adId) throws BadRequestException;

    List<SubClassReqdDocsDTO> findUnassignedRiskDocs(Long clmId, String docName)  throws IllegalAccessException;

    public File getMedMembersTemplate() throws BadRequestException;

}
