package com.brokersystems.brokerapp.claims.repository;

import com.brokersystems.brokerapp.claims.model.ClaimClaimants;
import com.brokersystems.brokerapp.claims.model.ClaimRequiredDocs;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by peter on 3/8/2017.
 */
public interface ClaimRequiredDocsRepo extends PagingAndSortingRepository<ClaimRequiredDocs, Long>, QueryDslPredicateExecutor<ClaimRequiredDocs> {


    @Query(value = "select clm_reqd_id,clm_reqd_doc_ref,clm_reqd_file_name,clm_reqd_dt_received,sbu.user_name,clm_reqd_remarks,\n" +
            "sbrd.req_desc ,sbcb.clm_status,  COUNT(*) OVER() AS total_rows\n" +
            "from sys_brk_clm_req_docs \n" +
            "left join sys_brk_users sbu on sbu.user_id =clm_reqrd_user_id\n" +
            "join sys_brk_req_docs sbrd on sbrd.req_id  = clm_reqrd_req_id\n" +
            "join sys_brk_clm_bookings sbcb on sbcb.clm_id =clm_reqrd_clm_id\n" +
            "where clm_reqrd_clm_id = :clmId\n" +
            "and ( coalesce(clm_reqd_file_name,'%%') like :search or coalesce(clm_reqd_doc_ref,'%%') like :search)\n" +
            " order by clm_reqd_id ASC \n" +
            " OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> findClaimDocs(@Param("clmId") Long clmId,
                                 @Param("search") String search,
                                 @Param("pageNo") int pageNo,
                                 @Param("limit") int limit);


}
