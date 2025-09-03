package com.brokersystems.brokerapp.quotes.repository;

import com.brokersystems.brokerapp.quotes.model.QuoteTrans;
import com.brokersystems.brokerapp.uw.model.PolicyActiveRisks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by peter on 3/12/2017.
 */
public interface QuotTransRepo extends PagingAndSortingRepository<QuoteTrans, Long>, QueryDslPredicateExecutor<QuoteTrans> {

     String query = "select count(1) from sys_brk_quotations a\n" +
             "inner join sys_brk_quot_products b on a.quot_id = b.quot_pr_quot_id\n" +
             " where a.quot_status not in ('CL')  \n" +
             " and b.quot_policy is null";

    @Query(value = query,nativeQuery = true)
    public Long getPendingQuote();

    @Query(value = "select a from QuoteTrans a")
    public Page<QuoteTrans> getSubAgentTrans(Pageable pageable);


    @Query(value = "select quot_id , quot_no,quot_rev_no,quot_wef_date,quot_wet_date, case when quot_clnt_type='P' then concat(sbp.prs_fname,' ',sbp.prs_onames)\n" +
            "            else concat(sbc.client_fname,' ',sbc.client_onames) end client_names,sbp2.pr_desc,sbc2.cur_iso_code,quot_status,sbu.user_username,COUNT(*) OVER() as total_rows  from sys_brk_quotations sbq \n" +
            "            left join sys_brk_clients sbc on sbc.client_id =sbq.quot_client_id \n" +
            "            left join sys_brk_quot_products sbqp on sbqp.quot_pr_quot_id =sbq.quot_id \n" +
            "            left join sys_brk_products sbp2 on sbp2.pr_code =sbqp.quot_pr_pro_id \n" +
            "            left join sys_brk_prospects sbp on sbp.prs_id =sbq.quot_prs_id \n" +
            "            join sys_brk_currencies sbc2 on sbc2.cur_code =sbq.quot_curr_id \n" +
            "            join sys_brk_users sbu ON sbu.user_id =sbq.quot_prep_user \n" +
            "            where quot_no like :quotNo\n" +
            "             and (coalesce(sbc.client_id,-2000) =case when :clientId=-2000 then coalesce(sbc.client_id,-2000) else :clientId end\n" +
            "             and coalesce(sbp.prs_id,-2000) =case when :prsId=-2000 then coalesce(sbp.prs_id,-2000) else :prsId end)\n" +
            "                   order by quot_date desc \n" +
            "                   OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> enquireQuotes(@Param("quotNo") String quotNo,
                               @Param("clientId") Long clientId,
                               @Param("prsId") Long prsId,
                               @Param("pageNo") int pageNo,
                               @Param("limit") int limit);

    @Query(value = "select quot_id , quot_status, sbp.prs_fname,sbp.prs_onames,quot_clnt_type,sbc.client_fname,sbc.client_onames,sbq.quot_prs_id,sbc.client_id,\n" +
            "quot_pmode_id,sbpm.pm_desc,sbbs.bs_id,sbbs.bs_desc,sbbsg.bsg_id,sbbsg.bsg_desc,sbb.ob_id,sbb.ob_name,sbc2.cur_code,sbc2.cur_name, \n" +
            "quot_no,quot_rev_no,quot_sum_insured,quot_premium,quot_basic_premium,quot_net_premium,quot_comm_amt,quot_tl,quot_phcf,quot_sd,quot_whtx,\n" +
            "quot_extras,quot_wef_date,quot_wet_date,quot_expiry_date,quot_type, sbc.client_idno,  \n" +
            "quot_sub_agent_id, sba.acct_name as sub_agent,quot_introducer_agent_id,sba2.acct_name as introducer_agent,\n" +
            "quot_marketer_agent_id, sba3.acct_name as marketer_agent,quot_leadsman_id,sbu.user_name, quot_introducer_absa_no,\n" +
            "quot_leadsman_absa_no, quot_subagent_absa_no, quot_marketer_absa_no \n" +
            "from sys_brk_quotations sbq \n" +
            "left join sys_brk_prospects sbp on sbp.prs_id  = sbq.quot_prs_id \n" +
            "left join sys_brk_clients sbc on sbc.client_id  = sbq.quot_client_id \n" +
            "left join sys_brk_payment_modes sbpm on sbpm.pm_id  = quot_pmode_id\n" +
            "left join sys_brk_business_sources sbbs on sbbs.bs_id  = sbq.quot_source_id \n" +
            "left join sys_brk_business_src_grp sbbsg on sbbsg.bsg_id  =sbbs.bs_bsg_id \n" +
            "left join sys_brk_branches sbb on sbb.ob_id  = sbq.quot_branch_id \n" +
            "left join sys_brk_currencies sbc2 on sbc2.cur_code  = sbq.quot_curr_id \n" +
            "left join sys_brk_accounts sba  on sba.acct_id  = sbq.quot_sub_agent_id \n" +
            "left join sys_brk_accounts sba2 on sba2.acct_id = sbq.quot_introducer_agent_id \n" +
            "left join sys_brk_accounts sba3 on sba3.acct_id = sbq.quot_marketer_agent_id \n" +
            "left join sys_brk_users sbu on  sbu.user_id = sbq.quot_leadsman_id \n" +
            "where quot_id =:quoteId ",nativeQuery = true)
    List<Object[]> getQuoteDetails(@Param("quoteId") Long quoteId);

    @Query(value = "select quot_no,quot_premium,quot_expiry_date,quot_status,quot_date,sbp.pr_desc,quot_id,COUNT(*) OVER() AS total_rows  from sys_brk_quotations a\n" +
            " inner join sys_brk_quot_products b on a.quot_id = b.quot_pr_quot_id\n" +
            " join sys_brk_products sbp on sbp.pr_code =b.quot_pr_pro_id \n" +
            "  where a.quot_status not in ('CL')  \n" +
            " and b.quot_policy is null and quot_no like :search order by quot_date desc " +
            "  OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> getPendingQuotes(@Param("search") String search,
                                    @Param("pageNo") int pageNo,
                                    @Param("limit") int limit);

    @Query("select q from QuoteTrans q where q.quoteId = :quotCode")
    QuoteTrans findByQuoteId(@Param("quotCode") Long quotCode);
}
