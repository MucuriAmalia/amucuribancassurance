package com.brokersystems.brokerapp.claims.repository;

import com.brokersystems.brokerapp.claims.model.ClaimClaimants;
import com.brokersystems.brokerapp.claims.model.ClaimPerils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by peter on 3/8/2017.
 */
public interface ClaimPerilsRepo extends PagingAndSortingRepository<ClaimPerils, Long>, QueryDslPredicateExecutor<ClaimPerils> {


    @Query(value = "select sbp.p_desc,clm_prl_type,clm_prl_limit_amt,clm_prl_excess,clm_prl_remark,clm_prl_id,COUNT(*) OVER() AS total_rows  from sys_brk_clm_perils sbcp \n" +
            "join sys_brk_perils sbp on sbp.p_code =clm_prl_peril_id \n" +
            "where clm_prl_clm_id =:clmId order by sbp.p_desc " +
            " OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findClmPerils(@Param("clmId") Long clmId,
                                    @Param("pageNo") int pageNo,
                                    @Param("limit") int limit);
    @Query(value = "SELECT * FROM sys_brk_clm_perils WHERE clm_prl_clm_id = :claimId", nativeQuery = true)
    List<ClaimPerils> findByClaimIdNative(@Param("claimId") Long claimId);





}
