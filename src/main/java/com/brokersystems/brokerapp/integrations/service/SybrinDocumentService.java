package com.brokersystems.brokerapp.integrations.service;

import com.brokersystems.brokerapp.dms.dto.CaseCreation;
import com.brokersystems.brokerapp.dms.dto.CaseUpdate;
import com.brokersystems.brokerapp.dms.dto.DocumentDetails;
import com.brokersystems.brokerapp.dms.model.SybrinCases;
import com.brokersystems.brokerapp.server.exception.BadRequestException;

import java.io.IOException;

/**
 * Service interface for managing interactions with the Sybrin document management system.
 */
public interface SybrinDocumentService {

    /**
     * Creates a new case in the Sybrin system and associates it with the provided SybrinCases entity.
     *
     * @param caseCreation the case creation data transfer object containing case details
     * @param sybrinCases  the SybrinCases entity to be updated with case details
     * @return the updated SybrinCases entity with case number and document GUID
     * @throws IOException         if there is an error communicating with the Sybrin API
     * @throws BadRequestException if the case creation fails or response is invalid
     */
    SybrinCases createCase(CaseCreation caseCreation, SybrinCases sybrinCases) throws IOException, BadRequestException;

    /**
     * Updates an existing case in the Sybrin system with additional documents.
     *
     * @param caseUpdate   the case update data transfer object containing update details
     * @param sybrinCases  the SybrinCases entity to be updated with new document details
     * @return the updated SybrinCases entity with updated document GUID
     * @throws IOException         if there is an error communicating with the Sybrin API
     * @throws BadRequestException if the case update fails or response is invalid
     */
    SybrinCases updateCase(CaseUpdate caseUpdate, SybrinCases sybrinCases) throws IOException, BadRequestException;

    /**
     * Retrieves document content from the Sybrin system based on the provided document details.
     *
     * @param documentDetails the document details data transfer object containing document identifiers
     * @return the document content as a byte array
     * @throws IOException         if there is an error communicating with the Sybrin API
     * @throws BadRequestException if no document content is found or response is invalid
     */
    byte[] retrieveDocumentDetails(DocumentDetails documentDetails) throws IOException, BadRequestException;
}
