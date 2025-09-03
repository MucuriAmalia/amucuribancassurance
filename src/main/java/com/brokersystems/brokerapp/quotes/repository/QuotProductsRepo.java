package com.brokersystems.brokerapp.quotes.repository;

import com.brokersystems.brokerapp.quotes.model.QuoteProTrans;
import com.brokersystems.brokerapp.uw.model.PolicyActiveRisks;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by peter on 3/12/2017.
 */
public interface QuotProductsRepo extends PagingAndSortingRepository<QuoteProTrans, Long>, QueryDslPredicateExecutor<QuoteProTrans> {

    @Query(value = "       select x.*,COUNT(*) OVER() AS total_rows  from (\n" +
            "       select  sbq.quot_pr_id,  sbq2.quot_id,sbq2.quot_no,sbp.pr_desc,sbq.quot_pr_wef,sbq.quot_pr_wet,sbp2.prs_fname,sbp2.prs_onames,sbq2.quot_prs_id,prs_sht_desc,\n" +
            "                        sbq2.quot_clnt_type,sbc.client_fname, sbc.client_onames,sbq2.quot_client_id,sbc2.cur_iso_code,sbq.quot_pr_converted,sbp3.pol_id,sbp3.pol_no,sba.acct_name\n" +
            "                         \n" +
            "                        from sys_brk_quot_products sbq \n" +
            "                        join sys_brk_products sbp  on quot_pr_pro_id  = pr_code \n" +
            "                        join sys_brk_accounts sba on sba.acct_id  = sbq.quot_pr_agent_id \n" +
            "                        join sys_brk_quotations sbq2 on sbq2.quot_id  = sbq.quot_pr_quot_id  \n" +
            "                        left join sys_brk_clients sbc on sbc.client_id  = sbq2.quot_client_id \n" +
            "                        left join sys_brk_prospects sbp2 on sbp2.prs_id = sbq2.quot_prs_id \n" +
            "                        join sys_brk_currencies sbc2 on sbc2.cur_code  = sbq2.quot_curr_id \n" +
            "                        left join sys_brk_policies sbp3 on sbp3.pol_id  = sbq.quot_policy \n" +
            "                        where coalesce(sbq2.quot_prs_id,-2000) = case when :prsId=-2000 then coalesce(sbq2.quot_prs_id,-2000) else :prsId end\n" +
            "                        and coalesce(sbq2.quot_client_id ,-2000) = case when :clientId=-2000 then coalesce(sbq2.quot_client_id,-2000) else :clientId end\n" +
            "                        and sbq.quot_pr_pro_id  = case when :productCode=-2000 then coalesce(quot_pr_pro_id,-2000) else :productCode end\n" +
            "                        and sbq.quot_pr_agent_id  = case when :agentCode=-2000 then coalesce(quot_pr_agent_id,-2000) else :agentCode end\n" +
            "                        and sbq2.quot_status = 'C'\n" +
            "                        and coalesce(sbq2.quot_type,'combined') = 'combined'\n" +
            "                        union all\n" +
            "                        select sbq.quot_pr_id,  sbq2.quot_id,sbq2.quot_no,sbp.pr_desc,sbq.quot_pr_wef,sbq.quot_pr_wet,sbp2.prs_fname,sbp2.prs_onames,sbq2.quot_prs_id,prs_sht_desc,\n" +
            "                        sbq2.quot_clnt_type,sbc.client_fname, sbc.client_onames,sbq2.quot_client_id,sbc2.cur_iso_code,sbq.quot_pr_converted,sbp3.pol_id,sbp3.pol_no,sba.acct_name\n" +
            "                       \n" +
            "                        from sys_brk_quot_products sbq \n" +
            "                        join sys_brk_products sbp  on quot_pr_pro_id  = pr_code \n" +
            "                        join sys_brk_accounts sba on sba.acct_id  = sbq.quot_pr_agent_id \n" +
            "                        join sys_brk_quotations sbq2 on sbq2.quot_id  = sbq.quot_pr_quot_id  \n" +
            "                        left join sys_brk_clients sbc on sbc.client_id  = sbq2.quot_client_id \n" +
            "                        left join sys_brk_prospects sbp2 on sbp2.prs_id = sbq2.quot_prs_id \n" +
            "                        join sys_brk_currencies sbc2 on sbc2.cur_code  = sbq2.quot_curr_id \n" +
            "                        left join sys_brk_policies sbp3 on sbp3.pol_id  = sbq.quot_policy \n" +
            "                        where coalesce(sbq2.quot_prs_id,-2000) = case when :prsId=-2000 then coalesce(sbq2.quot_prs_id,-2000) else :prsId end\n" +
            "                        and coalesce(sbq2.quot_client_id ,-2000) = case when :clientId=-2000 then coalesce(sbq2.quot_client_id,-2000) else :clientId end\n" +
            "                        and sbq.quot_pr_pro_id  = case when :productCode=-2000 then coalesce(quot_pr_pro_id,-2000) else :productCode end\n" +
            "                        and sbq.quot_pr_agent_id  = case when :agentCode=-2000 then coalesce(quot_pr_agent_id,-2000) else :agentCode end\n" +
            "                        and sbq2.quot_status = 'C'\n" +
            "                        and coalesce(sbq2.quot_type,'combined') = 'comparison'\n" +
            "                        and coalesce(sbq.quot_pr_selected_comp,'N') ='Y' )x\n" +
            "                        where x.quot_no like :quotNo OFFSET :pageNo*:limit LIMIT :limit\n" +
            "                     ",nativeQuery = true)
    List<Object[]> enquireQuotes(@Param("quotNo") String quotNo,
                                 @Param("clientId") Long clientId,
                                 @Param("prsId") Long prsId,
                                 @Param("productCode") Long productCode,
                                 @Param("agentCode") Long agentCode,
                                 @Param("pageNo") int pageNo,
                                 @Param("limit") int limit);



    @Query(value = "select sbq.quot_pr_id, sbq.quot_pr_wef,sbq.quot_pr_wet, sbb.bin_name,sbb.bin_id ,sba.acct_id ,sba.acct_name,quot_pr_sum_insured,\n" +
            "quot_pr_basic_prem,quot_pr_comm_amt,sbq2.quot_status,sbp.pr_code,sbp.pr_desc,sbb.bin_type, sbq.quot_pr_converted, sbq2.quot_type,  COUNT(*) OVER() as total_rows \n" +
            "            from sys_brk_quot_products sbq \n" +
            "            join sys_brk_products sbp  on quot_pr_pro_id  = pr_code \n" +
            "            join sys_brk_quotations sbq2 on sbq2.quot_id  = sbq.quot_pr_quot_id \n" +
            "            join sys_brk_binders sbb on sbb.bin_id =quot_pr_bind_id \n" +
            "            join sys_brk_accounts sba on sba.acct_id  = sbq.quot_pr_agent_id \n" +
            "            where quot_pr_quot_id =:quotId and (acct_name like :search or bin_name like :search )\n" +
            "            order by quot_pr_id desc\n" +
            "            OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> enquireQuoteProducts( @Param("quotId") Long quotId,
                                 @Param("search") String search,
                                 @Param("pageNo") int pageNo,
                                 @Param("limit") int limit);

    @Query(value = "select sbq.quot_pr_id, sbq.quot_pr_wef,sbq.quot_pr_wet, sbb.bin_name,sbb.bin_id ,sba.acct_id ,sba.acct_name,quot_pr_sum_insured,\n" +
            "quot_pr_basic_prem,quot_pr_comm_amt,sbq2.quot_status,sbp.pr_code,sbp.pr_desc,sbb.bin_type, sbq.quot_pr_converted, sbq2.quot_type, COUNT(*) OVER() as total_rows \n" +
            "            from sys_brk_quot_products sbq \n" +
            "            join sys_brk_products sbp  on quot_pr_pro_id  = pr_code \n" +
            "            join sys_brk_quotations sbq2 on sbq2.quot_id  = sbq.quot_pr_quot_id \n" +
            "            join sys_brk_binders sbb on sbb.bin_id =quot_pr_bind_id \n" +
            "            join sys_brk_accounts sba on sba.acct_id  = sbq.quot_pr_agent_id \n" +
            "            where quot_pr_quot_id =:quotId and (acct_name like :search or bin_name like :search )\n" +
            "            and sbq.quot_pr_selected_comp = 'Y'\n" +
            "            order by quot_pr_id desc\n" +
            "            OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> enquireQuoteCompProducts( @Param("quotId") Long quotId,
                                         @Param("search") String search,
                                         @Param("pageNo") int pageNo,
                                         @Param("limit") int limit);



    @Query(value = "select sbq.quot_pr_id, sbq.quot_pr_wef, sbq.quot_pr_wet, sbq.quot_pr_bind_id, sbb.bin_pr_code\n" +
            "from sys_brk_quot_products sbq \n" +
            "left join public.sys_brk_binders sbb on sbb.bin_id = sbq.quot_pr_bind_id\n" +
            "left join public.sys_brk_products sbp on sbp.pr_code = sbb.bin_pr_code\n" +
            "where sbq.quot_pr_quot_id = :quotId", nativeQuery = true)
    List<Object[]> enquireQuoteProductsByQuoteId(@Param("quotId") Long quotId);

    @Query(value = "select  sbqp.quot_pr_id, sbb.bin_name,sbqp.quot_pr_basic_prem, sbq.quot_type from sys_brk_quot_products sbqp \n" +
            "join sys_brk_binders sbb on sbqp.quot_pr_bind_id = sbb.bin_id\n" +
            "join sys_brk_quotations sbq on sbqp.quot_pr_quot_id =sbq.quot_id\n" +
            "where sbqp.quot_pr_quot_id =:quotId", nativeQuery = true)
    List<Object[]> getComparisonQuotProd(@Param("quotId") Long quotId);

    @Query(value = "SELECT quot_pr_id, quot_pr_basic_prem, quot_pr_comm_amt, quot_pr_extras, quot_pr_net_prem, \n" +
            "quot_pr_phf, quot_calc_basic_prem, quot_pr_sd, quot_pr_sum_insured, quot_pr_tl, quot_pr_bind_id, quot_pr_quot_id,\n" +
            "quot_pr_pro_id, quot_pr_agent_id, quot_pr_free_limit, quot_pr_cal_premium, quot_pr_comm_rate,quot_pr_converted, quot_pr_whtx \n" +
            "FROM public.sys_brk_quot_products sbqp \n" +
            "where sbqp.quot_pr_quot_id =:quotId", nativeQuery = true)
    List<Object[]> getUndoCompQuotProd(@Param("quotId") Long quotId);

    @Query("select q from QuoteProTrans q where q.quoteProductId = :quoteProductId")
    QuoteProTrans findByQuoteProductId(@Param("quoteProductId") Long quoteProductId);

    @Query(value = "select quot_pr_id,quot_pr_converted,quot_pr_quot_id,quot_pr_basic_prem,quot_pr_bind_id,quot_pr_agent_id,\n" +
            "quot_pr_pro_id,sbp.pr_renewable,quot_pr_wet,quot_pr_wef  from sys_brk_quot_products\n" +
            "join sys_brk_products sbp on sbp.pr_code  = sys_brk_quot_products.quot_pr_pro_id where quot_pr_id = :quotProdId", nativeQuery = true)
    List<Object[]> getQuotProductDetails(@Param("quotProdId") Long quotProdId);


    @Modifying
    @Query(value = "update sys_brk_quot_products set quot_pr_converted = :converted, quot_policy = :policyId where quot_pr_id = :quotProdId", nativeQuery = true)
    void updateQuotStatus(  @Param("converted") String converted,
                            @Param("policyId") Long policyId,
                            @Param("quotProdId") Long quotProdId);

    @Query(value = "select  sbq.quot_pr_id,  sbq2.quot_id,sbq2.quot_no,sbp.pr_desc,sbq.quot_pr_wef,sbq.quot_pr_wet,sbp2.prs_fname,sbp2.prs_onames,sbq2.quot_prs_id,prs_sht_desc,\n" +
            "                                    sbq2.quot_clnt_type,sbc.client_fname, sbc.client_onames,sbq2.quot_client_id,sbc2.cur_iso_code,sbq.quot_pr_converted,sbp3.pol_id,sbp3.pol_no,sba.acct_name, sbq2.quot_type, sbq.quot_pr_selected_comp  \n" +
            "                                    from sys_brk_quot_products sbq \n" +
            "                                    join sys_brk_products sbp  on quot_pr_pro_id  = pr_code \n" +
            "                                    join sys_brk_accounts sba on sba.acct_id  = sbq.quot_pr_agent_id \n" +
            "                                    join sys_brk_quotations sbq2 on sbq2.quot_id  = sbq.quot_pr_quot_id  \n" +
            "                                    left join sys_brk_clients sbc on sbc.client_id  = sbq2.quot_client_id \n" +
            "                                    left join sys_brk_prospects sbp2 on sbp2.prs_id = sbq2.quot_prs_id \n" +
            "                                    join sys_brk_currencies sbc2 on sbc2.cur_code  = sbq2.quot_curr_id \n" +
            "                                    left join sys_brk_policies sbp3 on sbp3.pol_id  = sbq.quot_policy \n" +
            "                                    where sbq2.quot_id = :quotNo\n" +
            "                                    and sbq2.quot_status = 'C'", nativeQuery = true)
    List<Object[]> getProductDetails(@Param("quotNo") Long quotCode);

    @Query(value = "select sbqp.quot_pr_id,sba.acct_name, sbp.pr_desc, sbqr.quote_rsk_sht_desc, sbqp.quot_pr_basic_prem \n" +
            "            from sys_brk_quot_products sbqp \n" +
            "            join sys_brk_binders sbb on sbb.bin_id = sbqp.quot_pr_bind_id \n" +
            "            left join sys_brk_accounts sba on sba.acct_id = sbb.bin_acct_code \n" +
            "            left join sys_brk_products sbp on sbp.pr_code = sbb.bin_pr_code \n" +
            "            left join sys_brk_quot_risks sbqr on sbqr.quot_rsk_pr_id = sbqp.quot_pr_id\n" +
            "            left join sys_brk_quotations sbq on sbq.quot_id = sbqp.quot_pr_quot_id \n" +
            "            where sbqp.quot_pr_quot_id = :quotId\n" +
            "            and (sbqp.quot_pr_converted not in ('Y') or sbqp.quot_pr_converted is null)\n" +
            "            and sbq.quot_type = 'combined'", nativeQuery = true)
    List<Object[]> getQuoteProductsConvert(@Param("quotId") Long quotId);
    @Modifying
    @Query(value = "update sys_brk_quot_products set quot_pr_converted = :converted, quot_policy = null where quot_policy = :policyId", nativeQuery = true)
    void updatePolicyStatusToNull(  @Param("converted") String converted,
                            @Param("policyId") Long policyId);

    @Query(value = "select quot_pr_id,quot_policy from sys_brk_quot_products where quot_pr_quot_id = :quoteId", nativeQuery = true)
    List<Object[]> getQuotProducts(@Param("quoteId") Long quoteId);

    @Modifying
    @Query(value = "delete from sys_brk_quot_limits where quot_sect_rsk_id in (select quot_rsk_id from sys_brk_quot_risks where quot_rsk_pr_id=:quotProdId)",nativeQuery = true)
    void deleteQuoteRiskLimits(@Param("quotProdId") Long quotProdId);

    @Modifying
    @Query(value = "delete from sys_brk_quot_risks where quot_rsk_pr_id=:quotProdId",nativeQuery = true)
    void deleteQuoteRisks(@Param("quotProdId") Long quotProdId);

    @Modifying
    @Query(value = "delete from sys_brk_quot_clauses where qp_clau_pr_id=:quotProdId",nativeQuery = true)
    void deleteQuoteClauses(@Param("quotProdId") Long quotProdId);

    @Modifying
    @Query(value = "delete from sys_brk_quot_taxes where qt_qpt_id=:quotProdId",nativeQuery = true)
    void deleteQuotTaxes(@Param("quotProdId") Long quotProdId);

    @Modifying
    @Query(value = "delete from sys_brk_quot_products where quot_pr_id=:quotProdId",nativeQuery = true)
    void deleteQuoteProduct(@Param("quotProdId") Long quotProdId);

    @Modifying
    @Query(value = "delete from sys_brk_wf_docs where bwd_quot_id=:quotId",nativeQuery = true)
    void deleteQuoteWorkFlows(@Param("quotId") Long quotProdId);

    @Modifying
    @Query(value = "delete from sys_brk_quotations where quot_id=:quotId",nativeQuery = true)
    void deleteQuote(@Param("quotId") Long quotProdId);


    @Query(value = "select  sbq.quot_pr_id,  sbq2.quot_id,sbq2.quot_no,sbp.pr_desc,sbq.quot_pr_wef,sbq.quot_pr_wet,sbp2.prs_fname,sbp2.prs_onames,sbq2.quot_prs_id,prs_sht_desc,\n" +
            "                                    sbq2.quot_clnt_type,sbc.client_fname, sbc.client_onames,sbq2.quot_client_id,sbc2.cur_iso_code,sbq.quot_pr_converted,sbp3.pol_id,sbp3.pol_no,sba.acct_name, sbq2.quot_type \n" +
            "                                    from sys_brk_quot_products sbq \n" +
            "                                    join sys_brk_products sbp  on quot_pr_pro_id  = pr_code \n" +
            "                                    join sys_brk_accounts sba on sba.acct_id  = sbq.quot_pr_agent_id \n" +
            "                                    join sys_brk_quotations sbq2 on sbq2.quot_id  = sbq.quot_pr_quot_id  \n" +
            "                                    left join sys_brk_clients sbc on sbc.client_id  = sbq2.quot_client_id \n" +
            "                                    left join sys_brk_prospects sbp2 on sbp2.prs_id = sbq2.quot_prs_id \n" +
            "                                    join sys_brk_currencies sbc2 on sbc2.cur_code  = sbq2.quot_curr_id \n" +
            "                                    left join sys_brk_policies sbp3 on sbp3.pol_id  = sbq.quot_policy \n" +
            "                                    where sbq2.quot_id = :quotNo\n" +
            "                                    and sbq.quot_pr_selected_comp = 'Y'\n" +
            "                                    and sbq2.quot_status = 'C'", nativeQuery = true)
    List<Object[]> getCompConvertProductDetails(@Param("quotNo") Long quotCode);

    @Query(value = "select quot_pr_id, quot_pr_basic_prem, quot_pr_cal_premium, quot_pr_comm_amt, quot_pr_comm_rate, \n" +
            "    quot_pr_converted, quot_pr_extras, quot_pr_free_limit, quot_pr_net_prem, quot_pr_phf, \n" +
            "    quot_calc_basic_prem, quot_pr_sd, quot_pr_sum_insured, quot_pr_tl, quot_pr_wef, quot_pr_wet, \n" +
            "    quot_pr_whtx, sbqp.quot_pr_agent_id, sbqp.quot_pr_bind_id, sbqp.quot_policy, sbqp.quot_pr_pro_id, sbqp.quot_pr_quot_id, \n" +
            "    quot_pr_selected_comp from sys_brk_quot_products sbqp \n" +
            "left join sys_brk_policies sbp on sbp.pol_id = sbqp.quot_policy \n" +
            "left join sys_brk_quotations sbq on sbq.quot_id = sbqp.quot_pr_quot_id \n" +
            "left join sys_brk_products sbp2 on sbp2.pr_code = sbqp.quot_pr_pro_id \n" +
            "left join sys_brk_accounts sba on sba.acct_id = sbqp.quot_pr_agent_id \n" +
            "where quot_pr_id = :quotProdId", nativeQuery = true)
    List<Object[]> getProductDetailsByPrdId(@Param("quotProdId") Long quotProdId);
}
