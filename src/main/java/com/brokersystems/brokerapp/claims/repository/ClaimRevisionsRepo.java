package com.brokersystems.brokerapp.claims.repository;

import com.brokersystems.brokerapp.claims.model.ClaimBookings;
import com.brokersystems.brokerapp.claims.model.ClaimPerils;
import com.brokersystems.brokerapp.claims.model.ClaimRevisions;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by peter on 3/8/2017.
 */
public interface ClaimRevisionsRepo  extends PagingAndSortingRepository<ClaimRevisions, Long>, QueryDslPredicateExecutor<ClaimRevisions> {

    @Query(value = "select x.*,COUNT(*) OVER() as total_rows from(\n" +
            "           select clm_pymnt_id,clm_pymt_amount,clm_pymt_created_dt,clm_pymt_trans_type,clm_pymt_auth_date,sbu.user_username created_by,\n" +
            "           sbu2.user_username auth_by,coalesce(clm_pymt_auth,'N')clm_pymnt_auth_status \n" +
            "           from sys_brk_clm_pymnts\n" +
            "         join sys_brk_users sbu on sbu.user_id =clm_pymt_created_by\n" +
            "         left join sys_brk_users sbu2 on sbu2.user_id  = clm_pymt_auth_by  \n" +
            "           where clm_pymt_clm_id = :clmId)x order by 1 desc  OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
     List<Object[]> getClaimTransactions(@Param("clmId") Long clmId, @Param("pageNo") int pageNo,@Param("limit") int limit);


    @Query(value = "select  coalesce(sum(clm_rev_amt),0) \n" +
            "from sys_brk_clm_revisions \n" +
            "JOIN sys_brk_clm_rev_trans sbcrt on clm_rev_id =  sbcrt.clm_rv_rev_id \n" +
            "where clm_rev_clm_id  = :clmId\n" +
            "and  sbcrt.clm_rv_prl_id =:perilId\n" +
            "and clm_rev_auth_status ='A'",nativeQuery = true)
    BigDecimal getTotalRevisions(@Param("clmId") Long clmId,@Param("perilId") Long perilId);

    @Query(value = "select  coalesce(sum(clm_rev_amt),0) \n" +
            "from sys_brk_clm_revisions \n" +
            "JOIN sys_brk_clm_rev_trans sbcrt on clm_rev_id =  sbcrt.clm_rv_rev_id \n" +
            "where clm_rev_clm_id  = :clmId\n" +
            "and  sbcrt.clm_rv_prl_id =:perilId",nativeQuery = true)
    BigDecimal getTotalRevisionsUnauth(@Param("clmId") Long clmId,@Param("perilId") Long perilId);

    @Query(value = "select  coalesce(sum(clm_pymt_amount),0) \n" +
            "from sys_brk_clm_pymnts \n" +
            "where clm_pymt_clm_id  = :clmId \n" +
            "and isnull(clm_pymt_auth,'N') ='A' ",nativeQuery = true)
    BigDecimal getClaimTotalRevisions(@Param("clmId") Long clmId);

    @Query(value = "select coalesce(sum(clm_pymt_amount),0) \n" +
            "from sys_brk_clm_pymnts " +
            "where clm_pymt_clm_id = :clmId " +
            "and isnull(clm_pymt_auth,'N') IN ('A','P') ",nativeQuery = true)
    BigDecimal getTotalPayments(@Param("clmId") Long clmId);

    @Query(value = "select coalesce(sum(clm_pymnt_amount),0) \n" +
            "from sys_brk_clm_peril_pymnts\n" +
            "join sys_brk_clm_perils sbcp on clm_pymnt_peril_id = sbcp.clm_prl_id \n" +
            "where clm_prl_clm_id = :clmId\n" +
            "and clm_pymnt_auth_status='A'",nativeQuery = true)
    BigDecimal getClaimTotalPayments(@Param("clmId") Long clmId);

}
