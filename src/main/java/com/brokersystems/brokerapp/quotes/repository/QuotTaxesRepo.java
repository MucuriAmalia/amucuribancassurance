package com.brokersystems.brokerapp.quotes.repository;

import com.brokersystems.brokerapp.quotes.model.QuotClauses;
import com.brokersystems.brokerapp.quotes.model.QuotTaxes;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by peter on 3/15/2017.
 */
public interface QuotTaxesRepo extends PagingAndSortingRepository<QuotTaxes, Long>, QueryDslPredicateExecutor<QuotTaxes> {

    @Query(value = "select rev_code,qt_tax_rate,qt_tax_div_fact,qt_tax_rate_type,qt_tax_amount,qt_tax_level,qt_tax_id,sbq2.quot_status,COUNT(*) OVER() AS total_rows  from sys_brk_quot_taxes\n" +
            "join sys_brk_revenue_items sbri on sbri.rev_id =qt_tax_rev_code\n" +
            "join sys_brk_quot_products sbq on sbq.quot_pr_id =qt_qpt_id\n" +
            "join sys_brk_quotations sbq2 on sbq2.quot_id  = sbq.quot_pr_quot_id \n" +
            "where qt_qpt_id = :prodId\n" +
            "order by qt_tax_id\n" +
            "OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> enquireQuoteTaxes(@Param("prodId") Long prodId,
                                            @Param("pageNo") int pageNo,
                                            @Param("limit") int limit);

    @Query("select q.proTrans.quoteTrans.quoteId from QuotTaxes q where q.polTaxId = :polTaxId")
    Long getQuotId(@Param("polTaxId")Long polTaxId);

    @Modifying
    @Query(value = "delete from sys_brk_quot_taxes where qt_tax_id = :polTaxId", nativeQuery = true)
    void deleteQuotTaxes(@Param("polTaxId") Long polTaxId);

}
