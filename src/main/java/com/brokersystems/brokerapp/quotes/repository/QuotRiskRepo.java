package com.brokersystems.brokerapp.quotes.repository;

import com.brokersystems.brokerapp.quotes.model.QuoteRiskTrans;
import com.brokersystems.brokerapp.uw.model.PolicyActiveRisks;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by peter on 3/12/2017.
 */
public interface QuotRiskRepo extends PagingAndSortingRepository<QuoteRiskTrans, Long>, QueryDslPredicateExecutor<QuoteRiskTrans> {


    @Query(value = "select quot_rsk_id,quote_rsk_sht_desc,quot_rsk_desc,quot_rsk_wef,quot_rsk_wet,sbs.sub_id,sbs.sub_desc,\n" +
            "sbc.cov_id,sbc.cov_desc,quot_rsk_value, quot_rsk_premium,sbq.quot_status,risk_prorata,quot_rsk_com_rate,quot_risk_but_charge,sbcp.client_id,sbcp.client_fname,sbcp.client_onames,\n" +
            "sbp.prs_id,sbp.prs_fname,sbp.prs_onames,quot_rsk_bin_det, sbcp.client_idno ,COUNT(*) OVER() as total_rows   \n" +
            "from sys_brk_quot_risks sbqr\n" +
            "join sys_brk_subclasses sbs on sbs.sub_id =sbqr.quot_rsk_sub_id \n" +
            "join sys_brk_covertypes sbc on sbc.cov_id =sbqr.quot_rsk_cov_id \n" +
            "join sys_brk_quot_products sbqp ON sbqp.quot_pr_id =sbqr.quot_rsk_pr_id \n" +
            "join sys_brk_quotations sbq on sbq.quot_id =sbqp.quot_pr_quot_id " +
            "left join sys_brk_clients sbcp on sbcp.client_id  = quot_rsk_insured_id\n" +
            "left join sys_brk_prospects sbp on sbp.prs_id =quot_risk_prs_id\n" +
            "where sbqr.quot_rsk_pr_id  =:prodId\n" +
            "and (quote_rsk_sht_desc like :search or quot_rsk_desc like :search)\n" +
            "order by quote_rsk_sht_desc\n" +
            "OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> enquireQuoteProducts(@Param("prodId") Long prodId,
                                        @Param("search") String search,
                                        @Param("pageNo") int pageNo,
                                        @Param("limit") int limit);

    @Query(value = "SELECT sbqr.quot_rsk_id, sbqr.quot_risk_but_charge, sbqr.quot_risk_cal_prem, sbqr.quot_rsk_com_amt, sbqr.quot_rsk_com_rate, sbqr.quot_risk_extras,  \n" +
            "               sbqr.quot_free_limit, sbqr.quot_rsk_clnt_type, sbqr.quot_risk_net_prem, sbqr.quot_risk_phf, sbqr.quot_rsk_premium, sbqr.risk_prorata, sbqr.quot_rsk_desc,  \n" +
            "               sbqr.quote_rsk_sht_desc, sbqr.quot_risk_sd, sbqr.quot_rsk_value, sbqr.quot_risk_tl, sbqr.quot_rsk_wef, sbqr.quot_rsk_wet, sbqr.quot_risk_whtx,  \n" +
            "               sbqr.quot_rsk_bind_id, sbqr.quot_rsk_bin_det, sbqr.quot_rsk_cov_id, sbqr.quot_rsk_insured_id, sbqr.quot_risk_prs_id, sbqr.quot_rsk_sub_id, sbqr.quot_rsk_pr_id\n" +
            "               FROM sys_brk_quot_risks sbqr  \n" +
            "               JOIN sys_brk_binders binders ON sbqr.quot_rsk_bind_id = binders.bin_id  \n" +
            "               JOIN sys_brk_subclasses subclasses ON sbqr.quot_rsk_sub_id = subclasses.sub_id  \n" +
            "               JOIN sys_brk_binder_det binderdet ON sbqr.quot_rsk_bin_det = binderdet.bdet_id  \n" +
            "               JOIN sys_brk_covertypes covertype ON sbqr.quot_rsk_cov_id = covertype.cov_id  \n" +
            "               LEFT JOIN sys_brk_clients clients ON sbqr.quot_rsk_insured_id = clients.client_id  \n" +
            "               LEFT JOIN sys_brk_prospects prospects ON sbqr.quot_risk_prs_id = prospects.prs_id  \n" +
            "               WHERE sbqr.quot_rsk_pr_id = :quoteProductId", nativeQuery = true)
    Iterable<Object[]> findQuoteRiskTrans(@Param("quoteProductId") Long quoteProductId);

}
