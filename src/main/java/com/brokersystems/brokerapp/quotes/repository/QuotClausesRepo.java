package com.brokersystems.brokerapp.quotes.repository;

import com.brokersystems.brokerapp.quotes.model.QuotClauses;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by peter on 3/15/2017.
 */
public interface QuotClausesRepo extends PagingAndSortingRepository<QuotClauses, Long>, QueryDslPredicateExecutor<QuotClauses> {


    @Query(value = "select subcl_cl_id, clau_id,clau_header ,clau_sht_desc ,clau_wording,clau_editable  from sys_brk_sub_clauses \n" +
            "join sys_brk_clauses sbc on sbc.clau_id =subcl_cl_code\n" +
            "where subcl_sub_code in (select quot_rsk_sub_id from sys_brk_quot_risks where quot_rsk_pr_id = :prodId )\n" +
            "and subcl_cl_id not in (select qp_clau_sub_code from sys_brk_quot_clauses where qp_clau_pr_id =:prodId )",nativeQuery = true)
    List<Object[]> getQuotProdClauses(@Param("prodId") Long prodId);

    @Query(value = "select bc.qp_clau_id,qp_clau_header,case when cast(qp_clau_editable as int)=1 then 'Yes' else 'No' end qp_clau_editable \n" +
            ",convert_from(lo_get(cast(qp_clau_wording as oid)), 'UTF8') qp_clau_wording,sbc2.clau_type,sbc.subcl_cl_id,sbc2.clau_sht_desc,sbq.quot_status, \n" +
            " COUNT(*) OVER() as total_rows  from sys_brk_quot_clauses bc\n" +
            "join sys_brk_sub_clauses  sbc on sbc.subcl_cl_id  = bc.qp_clau_sub_code   \n" +
            "join sys_brk_clauses sbc2 on sbc2.clau_id  = sbc.subcl_cl_code\n" +
            "join sys_brk_quot_products sbqp on sbqp.quot_pr_id  = bc.qp_clau_pr_id \n" +
            "join sys_brk_quotations sbq on sbq.quot_id  = sbqp.quot_pr_quot_id  where qp_clau_pr_id = :quotProdId" +
            " order by bc.qp_clau_id desc OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> getQuoteProdClauses(@Param("quotProdId") Long prodId,
                                       @Param("pageNo") int pageNo,
                                       @Param("limit") int limit);

}
