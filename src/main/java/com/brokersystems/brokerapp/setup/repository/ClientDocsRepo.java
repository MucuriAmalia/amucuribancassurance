package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.ClientDocs;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by HP on 10/7/2017.
 */
public interface ClientDocsRepo extends PagingAndSortingRepository<ClientDocs, Long>, QueryDslPredicateExecutor<ClientDocs> {

    @Query(value = "select cd_id,cd_file_id,cd_loc_name,cd_verifier,cd_content_type,cd_req_code,sbrd.req_desc,sbrd.req_sht_desc,\n" +
            "sbc.client_authorized,cd_initiator,cd_approver,cd_approval_date,cd_creation_date \n" +
            ",COUNT(*) OVER() AS total_rows from sys_brk_clnt_docs\n" +
            "join sys_brk_req_docs sbrd on sbrd.req_id  = cd_req_code\n" +
            "join sys_brk_clients sbc on sbc.client_id  = cd_client_id\n" +
            "where cd_client_id = :clientId and sbrd.req_desc like :search\n" +
            "order by sbrd.req_desc\n" +
            "OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> getAllClientDocuments(@Param("clientId") Long clientId,
                                         @Param("search") String search,
                                         @Param("pageNo") int pageNo,
                                         @Param("limit") int limit);

}
