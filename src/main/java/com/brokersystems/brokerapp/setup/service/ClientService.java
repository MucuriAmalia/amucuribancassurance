package com.brokersystems.brokerapp.setup.service;

import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.dto.ClientDTO;
import com.brokersystems.brokerapp.setup.dto.ClientDocsDTO;
import com.brokersystems.brokerapp.setup.dto.ClientTransactionDTO;
import com.brokersystems.brokerapp.setup.dto.ProspectsDTO;
import com.brokersystems.brokerapp.setup.model.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Tenant Service
 * Used for crud services for tenant operations
 * @author Peter
 *
 */
public interface ClientService {
	
	
	DataTablesResult<ClientDTO> findAllClients(DataTablesRequest request, String searchValue, String searchType) throws BadRequestException;

    ClientDTO scvidPersonalChecker(String customerIdentifier) throws BadRequestException;

	@Transactional
	ClientDTO scvidNonPersonalChecker(String customerIdentifier) throws BadRequestException;

	String cifPersonalChecker(String masterCifId) throws BadRequestException;

    DataTablesResult<ClientTransactionDTO> findClientTransactions(DataTablesRequest request, Long clientId);

    ClientDef defineClient(ClientDef tenant);
	
	void deleteClient(Long tenId);
	
	ClientDef getClientDetails(Long tenId);

	DataTablesResult<ProspectsDTO> findAllProspects(DataTablesRequest request) throws IllegalAccessException;

	ProspectsDTO findOneProspect(long id) throws BadRequestException;

	void deleteProspect(Long prosId);

	ProspectDef defineProspect(ProspectsDTO prospect) throws BadRequestException;

	ProspectDef defineQuotProspect(ProspectDef prospect) throws BadRequestException;

	public DataTablesResult<ClientDocsDTO> findClientDOcs(DataTablesRequest request, Long clientId);

	public DataTablesResult<ClientDocs> findProspectDOcs(DataTablesRequest request, Long clientId) throws IllegalAccessException;

	List<RequiredDocs> findUnassignedClientDocs(Long clientCode, String docName)  throws IllegalAccessException;

	public List<RequiredDocs> findUnassignedProspectDocs(Long clientCode, String docName) throws IllegalAccessException;

	void createClientRequiredDocs(RequiredDocBean requiredDocBean);
	public void createProspectRequiredDocs(RequiredDocBean requiredDocBean);

	public void authorizeClient(Long clientCode) throws BadRequestException;
	public void authorizeEditedClient(Long clientCode) throws BadRequestException;


}
