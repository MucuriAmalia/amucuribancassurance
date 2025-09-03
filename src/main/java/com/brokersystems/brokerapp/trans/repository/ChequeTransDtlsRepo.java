package com.brokersystems.brokerapp.trans.repository;

import com.brokersystems.brokerapp.trans.model.ChequeTransDtls;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChequeTransDtlsRepo extends PagingAndSortingRepository<ChequeTransDtls, Long>, QueryDslPredicateExecutor<ChequeTransDtls> {



    @Query(value = "select ctd_no,ctd_dc,ctd_narrative,ctd_amount,acc.co_code,acc.co_name,sbb.ob_name,COUNT(*) OVER() AS total_rows  from sys_chq_trans_dtls \n" +
            "join sys_brk_coa_sub acc on acc.co_id =ctd_gl_acc \n" +
            "join sys_brk_branches sbb on sbb.ob_id =ctd_br_code\n" +
            "where ctd_ct_no = :reqNo\n" +
            "and (lower(ctd_narrative) like :search or lower(co_code) like :search or lower(co_name) like :search)\n" +
            "order by ctd_no desc OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> searchAllRequistionDetails(@Param("reqNo") Long reqNo,
                                          @Param("search") String search,
                                          @Param("pageNo") int pageNo,
                                          @Param("limit") int limit);

    @Query(value = "select ctd_no,ctd_dc,ctd_narrative,ctd_amount,acc.co_id,acc.co_name,sbb.ob_id  from sys_chq_trans_dtls \n" +
            "join sys_brk_coa_sub acc on acc.co_id =ctd_gl_acc \n" +
            "join sys_brk_branches sbb on sbb.ob_id =ctd_br_code\n" +
            "where ctd_ct_no = :reqNo",nativeQuery = true)
    List<Object[]> searchRequistionDetails(@Param("reqNo") Long reqNo);

}
