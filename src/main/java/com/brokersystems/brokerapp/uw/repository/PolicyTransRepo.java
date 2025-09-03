package com.brokersystems.brokerapp.uw.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;


public interface PolicyTransRepo extends  PagingAndSortingRepository<PolicyTrans, Long>, QueryDslPredicateExecutor<PolicyTrans>{

    String postgres_query = "select  to_char(trans_date,'MM') as mont,to_char(trans_date,'Mon') as mon, "+
            " extract(year from trans_date) as yyyy,sum(case when trans_dc='C' then -trans_amount else trans_amount "+
            " end )as amount "+
            " from sys_brk_main_transactions where trans_clnt_type='C' and trans_fund_id is null "+
            " group by 1,2,3 "+
            " order by 1";

    String oracle_query = "select  to_char(trans_date,'MM') as mont,to_char(trans_date,'Mon') as mon, \n" +
            "             extract(year from trans_date) as yeard,sum(case when trans_dc='C' then -trans_amount else trans_amount \n" +
            "             end )as amount \n" +
            "             from sys_brk_main_transactions where trans_clnt_type='C' and trans_fund_id is null and trans_date > TRUNC(SysDate,'YEAR')\n" +
            "            group by to_char(trans_date,'MM'),to_char(trans_date,'Mon'),extract(year from trans_date)\n" +
            "             order by 1";

    String sqlServerQuery = "   select  FORMAT(trans_date,'MM') as mont,FORMAT(trans_date,'MMM') as mon, \n" +
            "                    FORMAT(trans_date, 'yyyy') as yeard,sum(case when trans_dc='C' then -trans_amount else trans_amount \n" +
            "                         end )as amount\n" +
            "                        from sys_brk_main_transactions where trans_user_auth=? \n" +
            "                        group by FORMAT(trans_date,'MM'),FORMAT(trans_date,'MMM'),FORMAT(trans_date, 'yyyy')\n" +
            "                         order by yeard,mont";

    String prodQuery = "select sum(pol_net_premium_amt)amt,pr_desc from sys_brk_policies\n" +
            " inner join sys_brk_products on pol_prod_id = pr_code where pol_auth_status='A' and pol_auth_user =:userId and pol_current_status not in ('CO') \n" +
            " group by pr_desc\n" +
            " order by 1 desc";

    String branchQuery="select sum(pol_net_premium_amt)amt,ob_name from sys_brk_policies\n" +
            "             inner join sys_brk_branches on pol_branch_id = ob_id\n" +
            "            where pol_auth_status='A' and pol_current_status not in ('CO')\n" +
            "            and pol_auth_user =:userId\n" +
            "            group by ob_name\n" +
            "             order by 1 desc";


    @Query(value = postgres_query,nativeQuery = true)
    public List<Object[]> getPostgresPremiumProduction();

    @Query(value = " select  sum(case when extract(year from trans_auth_date) = extract(year from current_date) then trans_amount else 0 end)\n" +
            "            from sys_brk_main_transactions where trans_clnt_type ='C'\n" +
            "            and trans_authorised ='Y'", nativeQuery = true)
    BigDecimal ytdPremium();

    @Query(value = "select sum(case when extract(year from pol_auth_date) = extract(year from current_date) then pol_total_sum_insured  else 0 end)   from sys_brk_policies  lp \n" +
            "where lp.pol_auth_status ='A'\n" +
            "and lp.pol_current_status  = 'A'", nativeQuery = true)
    BigDecimal ytdSumAssured();

    @Query(value = "select COUNT(1)  " +
            "from sys_brk_policies sbp where pol_no=:policyNo " +
            "and pol_trans_type = 'RN'", nativeQuery = true)
    Long countRenewals(@Param("policyNo") String policyNo);

    @Query(value = "select  count(case when extract(year from pol_auth_date) = extract(year from current_date) then 1 else 0 end)   from sys_brk_policies  lp \n" +
            "            where lp.pol_auth_status ='A'\n" +
            "            and lp.pol_current_status  = 'A'", nativeQuery = true)
    Long countYtpPolicies();
    // user specific aggregates
    @Query(value = "select sum(case when extract(year from trans_auth_date) = extract(year from current_date) then trans_amount else 0 end)\n" +
            "from sys_brk_main_transactions where trans_clnt_type ='C'\n" +
            "and trans_authorised ='Y' and trans_posted_by = :userId", nativeQuery = true)
    BigDecimal ytdPremiumByUser(@Param("userId") Long userId);

    @Query(value = "select sum(case when extract(year from pol_auth_date) = extract(year from current_date) then pol_total_sum_insured else 0 end)\n" +
            "from sys_brk_policies lp \n" +
            "where lp.pol_auth_status ='A'\n" +
            "and lp.pol_current_status = 'A'\n" +
            "and lp.pol_created_user = :userId", nativeQuery = true)
    BigDecimal ytdSumAssuredByUser(@Param("userId") Long userId);


    @Query(value = "select count(case when extract(year from pol_auth_date) = extract(year from current_date) then 1 else 0 end)\n" +
            "from sys_brk_policies lp \n" +
            "where lp.pol_auth_status ='A'\n" +
            "and lp.pol_current_status = 'A'\n" +
            "and lp.pol_created_user = :userId", nativeQuery = true)
    Long countYtdPoliciesByUser(@Param("userId") Long userId);


    @Query(value = oracle_query,nativeQuery = true)
    public List<Object[]> getOraclePremiumProduction();

    @Query(value = prodQuery,nativeQuery = true)
    public List<Object[]> getProductsPremium(@Param("userId") Long userId);

    @Query(value = branchQuery,nativeQuery = true)
    public List<Object[]> getBranchPremium(@Param("userId") Long userId);
//peters
//    @Query(value = "select s5.pol_sum_insur_amt,\n" +
//            "sum (case when s7.trans_dc = 'D' \n" +
//            "then s7.trans_balance\n" +
//            "end) as Ins_bal,\n" +
//            "sum (case when s7.trans_dc = 'C' \n" +
//            "then s7.trans_balance\n" +
//            "end)*-1 as client_bal\n" +
//            "FROM  sys_brk_risks s2\n" +
//            "inner join sys_brk_policies s5 on s5.pol_id = s2.risk_pol_id\n" +
//            "inner join sys_brk_main_transactions s7 on s5.pol_id = s7.trans_pol_id\n" +
//            "where s5.POL_ID = :clmCode\n" +
//            "group by pol_sum_insur_amt,trans_dc",nativeQuery = true)
//     List<Object[]> getClaimDetails(@Param("clmCode")Long clmCode);
@Query(value = "SELECT " +
        "s5.pol_no, " +
        "SUM(s7.trans_balance) AS total_balance " +
        "FROM sys_brk_policies s5 " +
        "INNER JOIN sys_brk_main_transactions s7 ON s5.pol_id = s7.trans_pol_id " +
        "WHERE s5.pol_no = :polNo " +
        "AND s7.trans_type NOT IN ('RC', 'SAG', 'RF', 'RFC') " +
        "AND s7.trans_clnt_type != 'A' " +
        "GROUP BY s5.pol_no", nativeQuery = true)
List<Object[]> getClaimDetails(@Param("polNo") String polNo);


//    @Query(value = "SELECT " +
//            "s5.pol_no, " +
//            "SUM(s7.trans_balance) AS total_balance " +
//            "FROM sys_brk_policies s5 " +
//            "INNER JOIN sys_brk_main_transactions s7 ON s5.pol_id = s7.trans_pol_id " +
//            "WHERE s5.POL_ID = :clmCode\n" +
//            "AND s7.trans_type NOT IN ('RC', 'SAG', 'RF', 'RFC') " +
//            "AND s7.trans_clnt_type != 'A' " +
//            "GROUP BY s5.pol_no", nativeQuery = true)
//    List<Object[]> getClaimDetails(@Param("clmCode")Long clmCode);

     PolicyTrans findFirstByClient_TenId(Long idNo);

     PolicyTrans findFirstByPolNo(String polNo);
     PolicyTrans findFirstByPolNoAndClient_TenId(String polNo,Long idNo);

    PolicyTrans findFirstByPolicyId(Long polNo);

    @Query(value = "select pol_no,pol_wef_date,pol_wet_date,sbu.user_username,pol_auth_date,pol_basic_premium_amt,pol_id,COUNT(*) OVER() AS total_rows  from sys_brk_policies pol\n" +
            "join sys_brk_users sbu on sbu.user_id = pol.pol_created_user \n" +
            "where pol_current_status ='A' and pol_wet_date < CURRENT_DATE and pol_no like :search" +
            " order by pol_no desc OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> searchExpiredPolicies(@Param("search") String search,
                                         @Param("pageNo") int pageNo,
                                         @Param("limit") int limit);

    @Query(value = "select pol_no,pol_wef_date,pol_wet_date,sbu.user_username,pol_date ,pol_basic_premium_amt,pol_id,COUNT(*) OVER() AS total_rows  from sys_brk_policies pol\n" +
            "join sys_brk_users sbu on sbu.user_id = pol.pol_created_user \n" +
            "where (pol_trans_type ='EN' or pol_trans_type ='EX') and pol_current_status not in ('A') and pol_no like :search " +
            " order by pol_no desc OFFSET :pageNo*:limit LIMIT :limit ",nativeQuery = true)
    List<Object[]> searchPendingEndorsements(@Param("search") String search,
                                             @Param("pageNo") int pageNo,
                                             @Param("limit") int limit);

    @Query(value = "select pol_no,pol_wef_date,pol_wet_date,sbu.user_username,pol_date ,pol_basic_premium_amt,pol_id,COUNT(*) OVER() as total_rows  from sys_brk_policies pol\n" +
            "join sys_brk_users sbu on sbu.user_id = pol.pol_created_user \n" +
            "where (pol_trans_type ='RN') and pol_current_status not in ('A') and pol_no like :search " +
            " order by pol_no desc OFFSET :pageNo*:limit LIMIT :limit ",nativeQuery = true)
    List<Object[]> searchPendingRenewals(@Param("search") String search,
                                         @Param("pageNo") int pageNo,
                                         @Param("limit") int limit);

    @Query(value = "select distinct  p.pol_no,\n" +
            "                                           p.pol_client_pol_no,\n" +
            "                                           p.pol_rev_no,\n" +
            "                                           p.pol_id,\n" +
            "                                           concat(c.client_fname ,' ', c.client_onames)                                              client,\n" +
            "                                           coalesce(a.acct_name, a2.acct_name)                                           acct_name,\n" +
            "                                           p.pol_uw_yr,\n" +
            "                                           case when a.acct_name is not null then sbb.bin_name else sbb2.bin_name end as bin_name,\n" +
            "                                           p.pol_renewable                                                               renewable,\n" +
            "                                           sbp.pr_desc                                                                   product,\n" +
            "                                           cast(p.pol_wef_date as date)                                                  wefDate,\n" +
            "                                           cast(p.pol_wet_date as date)                                                  wetDate,\n" +
            "                                           curr.cur_iso_code                                                             currency,\n" +
            "                                           u.user_username                                                               username,\n" +
            "                                           COUNT(*) OVER() as total_rows\n," +
            "                                           c.client_idno                                                               idNo\n" +
            "from sys_brk_policies p\n" +
            "join sys_brk_risks sbr on p.pol_id = sbr.risk_pol_id\n" +
            "join sys_brk_clients c on p.pol_client_id = c.client_id\n" +
            "join sys_brk_products sbp on p.pol_prod_id = sbp.pr_code\n" +
            "join sys_brk_product_grp sbpg on sbp.pr_bpg_code = sbpg.bpg_code\n" +
            "join sys_brk_currencies curr on p.pol_curr_id = curr.cur_code\n" +
            "join sys_brk_users u on p.pol_created_user = u.user_id\n" +
            "join sys_brk_accounts a on p.pol_agent_id = a.acct_id\n" +
            "join sys_brk_binders sbb on p.pol_binder_id = sbb.bin_id\n" +
            "left join sys_brk_policy_binders sbpb on p.pol_id = sbpb.pol_bind_policy_id\n" +
            "left join sys_brk_binders sbb2 on sbpb.pol_bind_bind_id = sbb2.bin_id\n" +
            "left join sys_brk_accounts a2 on a2.acct_id = sbb2.bin_acct_code\n" +
            "where  sbpg.bpg_type not in ('MD')\n" +
            " and p.pol_current_status = 'A'\n" +
            " and p.pol_trans_type NOT IN ('CN', 'CO')\n" +
            " and p.pol_no like :polNo\n" +
            " and coalesce(sbr.risk_sht_desc, '') like :riskId \n" +
            " and coalesce(p.pol_ref_no, '') like :refNo \n" +
            " and (coalesce(a.acct_id  ,-2000) = case when :agentCode=-2000 then coalesce(a.acct_id,-2000) else :agentCode end or" +
            "  coalesce(a2.acct_id  ,-2000) = case when :agentCode=-2000 then coalesce(a2.acct_id,-2000) else :agentCode end) \n" +
            " and coalesce(c.client_id  ,-2000) = case when :clientId=-2000 then coalesce(c.client_id,-2000) else :clientId end\n" +
            " order by pol_no asc\n" +
            " OFFSET :pageNo*:limit limit :limit", nativeQuery = true)
    List<Object[]> getActivePolicies(@Param("polNo") String polNo,
                                     @Param("riskId") String riskId,
                                     @Param("refNo") String refNo,
                                     @Param("agentCode") Long agentCode,
                                     @Param("clientId") Long clientId,
                                     @Param("pageNo") int pageNo,
                                     @Param("limit") int limit);

    @Query(value = "select distinct  p.pol_no,\n" +
            "                                           p.pol_client_pol_no,\n" +
            "                                           p.pol_rev_no,\n" +
            "                                           p.pol_id,\n" +
            "                                           concat(c.client_fname ,' ', c.client_onames)                                              client,\n" +
            "                                           coalesce(a.acct_name, a2.acct_name)                                           acct_name,\n" +
            "                                           p.pol_uw_yr,\n" +
            "                                           case when a.acct_name is not null then sbb.bin_name else sbb2.bin_name end as bin_name,\n" +
            "                                           p.pol_renewable                                                               renewable,\n" +
            "                                           sbp.pr_desc                                                                   product,\n" +
            "                                           cast(p.pol_wef_date as date)                                                  wefDate,\n" +
            "                                           cast(p.pol_wet_date as date)                                                  wetDate,\n" +
            "                                           curr.cur_iso_code                                                             currency,\n" +
            "                                           u.user_username                                                               username,\n" +
            "                                           COUNT(*) OVER() as total_rows\n," +
            "                                           c.client_idno                                                               idNo\n" +
            "from sys_brk_policies p\n" +
            "join sys_brk_risks sbr on p.pol_id = sbr.risk_pol_id\n" +
            "join sys_brk_clients c on p.pol_client_id = c.client_id\n" +
            "join sys_brk_products sbp on p.pol_prod_id = sbp.pr_code\n" +
            "join sys_brk_product_grp sbpg on sbp.pr_bpg_code = sbpg.bpg_code\n" +
            "join sys_brk_currencies curr on p.pol_curr_id = curr.cur_code\n" +
            "join sys_brk_users u on p.pol_created_user = u.user_id\n" +
            "join sys_brk_accounts a on p.pol_agent_id = a.acct_id\n" +
            "join sys_brk_binders sbb on p.pol_binder_id = sbb.bin_id\n" +
            "left join sys_brk_policy_binders sbpb on p.pol_id = sbpb.pol_bind_policy_id\n" +
            "left join sys_brk_binders sbb2 on sbpb.pol_bind_bind_id = sbb2.bin_id\n" +
            "left join sys_brk_accounts a2 on a2.acct_id = sbb2.bin_acct_code\n" +
            "where  sbpg.bpg_type not in ('MD')\n" +
            " and p.pol_current_status = 'CN'\n" +
            " and p.pol_no like :polNo\n" +
            " and coalesce(sbr.risk_sht_desc, '') like :riskId \n" +
            " and p.pol_ref_no like :refNo \n" +
            " and (coalesce(a.acct_id  ,-2000) = case when :agentCode=-2000 then coalesce(a.acct_id,-2000) else :agentCode end or" +
            "  coalesce(a2.acct_id  ,-2000) = case when :agentCode=-2000 then coalesce(a2.acct_id,-2000) else :agentCode end) \n" +
            " and coalesce(c.client_id  ,-2000) = case when :clientId=-2000 then coalesce(c.client_id,-2000) else :clientId end\n" +
            " order by pol_no asc\n" +
            " OFFSET :pageNo*:limit limit :limit", nativeQuery = true)
    List<Object[]> getCancelledPolicies(@Param("polNo") String polNo,
                                     @Param("riskId") String riskId,
                                     @Param("refNo") String refNo,
                                     @Param("agentCode") Long agentCode,
                                     @Param("clientId") Long clientId,
                                     @Param("pageNo") int pageNo,
                                     @Param("limit") int limit);

    @Query(value = "select  p.pol_no,\n" +
            "                                           p.pol_client_pol_no,\n" +
            "                                           p.pol_rev_no,\n" +
            "                                           p.pol_id,\n" +
            "                                           concat(c.client_fname ,' ', c.client_onames)                                              client,\n" +
            "                                           coalesce(a.acct_name, a2.acct_name)                                           acct_name,\n" +
            "                                           p.pol_uw_yr,\n" +
            "                                           case when a.acct_name is not null then sbb.bin_name else sbb2.bin_name end as bin_name,\n" +
            "                                           p.pol_renewable                                                               renewable,\n" +
            "                                           sbp.pr_desc                                                                   product,\n" +
            "                                           cast(p.pol_wef_date as date)                                                  wefDate,\n" +
            "                                           cast(p.pol_wet_date as date)                                                  wetDate,\n" +
            "                                           curr.cur_iso_code                                                             currency,\n" +
            "                                           u.user_username                                                               username,\n" +
            "                                           c.client_idno                                                               idNo\n," +
            "                                           COUNT(*) OVER() as total_rows\n" +
            "from sys_brk_policies p\n" +
            "join sys_brk_risks sbr on p.pol_id = sbr.risk_pol_id\n" +
            "join sys_brk_clients c on p.pol_client_id = c.client_id\n" +
            "join sys_brk_products sbp on p.pol_prod_id = sbp.pr_code\n" +
            "join sys_brk_currencies curr on p.pol_curr_id = curr.cur_code\n" +
            "join sys_brk_users u on p.pol_created_user = u.user_id\n" +
            "join sys_brk_accounts a on p.pol_agent_id = a.acct_id\n" +
            "join sys_brk_binders sbb on p.pol_binder_id = sbb.bin_id\n" +
            "left join sys_brk_policy_binders sbpb on p.pol_id = sbpb.pol_bind_policy_id\n" +
            "left join sys_brk_binders sbb2 on sbpb.pol_bind_bind_id = sbb2.bin_id\n" +
            "left join sys_brk_accounts a2 on a2.acct_id = sbb2.bin_acct_code\n" +
            "where  p.pol_current_status = 'CO' and p.pol_trans_type ='CO'\n" +
            " and p.pol_no like :polNo\n" +
            " and sbr.risk_sht_desc like :riskId\n" +
            " and p.pol_ref_no like :refNo\n" +
            " and  concat(c.client_fname ,' ', c.client_onames)  like :clientName\n" +
            " and  coalesce(a.acct_name, a2.acct_name) like :acct\n" +
            " order by pol_no asc\n" +
            " OFFSET :pageNo*:limit limit :limit", nativeQuery = true)
    List<Object[]> getContraPolicies(@Param("polNo") String polNo,
                                     @Param("riskId") String riskId,
                                     @Param("refNo") String refNo,
                                     @Param("clientName") String clientName,
                                     @Param("acct") String acct,
                                     @Param("pageNo") int pageNo,
                                     @Param("limit") int limit);

    @Query(value = "SELECT pol_no FROM sys_brk_policies p " +
            "JOIN sys_brk_main_transactions sbmt ON p.pol_id = sbmt.trans_pol_id " +
            "WHERE sbmt.trans_ref_no = :riskNoteNumber " , nativeQuery = true)
    String findPolicyNumberWithRiskNote(@Param("riskNoteNumber") String riskNoteNumber);

    PolicyTrans findByPolNo(String polNo);
    //    @Query(value = "select distinct p.pol_no, p.pol_client_pol_no,\n" +
//            "    case when p.pol_current_status = 'A' then 'Active'\n" +
//            "         when p.pol_current_status = 'D' then 'Draft'\n" +
//            "         when p.pol_current_status = 'CN' then 'Cancelled'\n" +
//            "         when p.pol_current_status = 'CO' then 'Converted'\n" +
//            "         when p.pol_current_status = 'R' then 'Ready'\n" +
//            "         when p.pol_current_status = 'PL' then 'Pre-Loaded'\n" +
//            "         when p.pol_current_status = 'PD' then 'Loaded Policy'\n" +
//            "         when p.pol_current_status = 'LD' then 'Loaded Data'\n" +
//            "         else p.pol_current_status end as pol_current_status,\n" +
//            "    p.pol_rev_no, p.pol_id, concat(c.client_fname, ' ', c.client_onames) client,\n" +
//            "    coalesce(a.acct_name, a2.acct_name) acct_name, \n" +
//            "    p.pol_uw_yr, case when a.acct_name is not null then sbb.bin_name else sbb2.bin_name end as bin_name, \n" +
//            "    p.pol_renewable, sbp.pr_desc product, \n" +
//            "    cast(p.pol_wef_date as date) wefDate, cast(p.pol_wet_date as date) wetDate, \n" +
//            "    curr.cur_iso_code currency, u.user_username username, \n" +
//            "    c.client_idno idNo, p.auth_comments, COUNT(*) OVER() as total_rows\n" +
//            "from sys_brk_policies p\n" +
//            "join sys_brk_risks sbr on p.pol_id = sbr.risk_pol_id\n" +
//            "join sys_brk_clients c on p.pol_client_id = c.client_id\n" +
//            "join sys_brk_products sbp on p.pol_prod_id = sbp.pr_code\n" +
//            "join sys_brk_product_grp sbpg on sbp.pr_bpg_code = sbpg.bpg_code\n" +
//            "join sys_brk_currencies curr on p.pol_curr_id = curr.cur_code\n" +
//            "join sys_brk_users u on p.pol_created_user = u.user_id\n" +
//            "join sys_brk_accounts a on p.pol_agent_id = a.acct_id\n" +
//            "join sys_brk_binders sbb on p.pol_binder_id = sbb.bin_id\n" +
//            "left join sys_brk_policy_binders sbpb on p.pol_id = sbpb.pol_bind_policy_id\n" +
//            "left join sys_brk_binders sbb2 on sbpb.pol_bind_bind_id = sbb2.bin_id\n" +
//            "left join sys_brk_accounts a2 on a2.acct_id = sbb2.bin_acct_code\n" +
//            "where (lower(c.client_fname) like :search \n" +
//            "       or lower(sbp.pr_desc) like :search \n" +
//            "       or lower(p.pol_no) like :search \n" +
//            "       or lower(p.pol_rev_no) like :search \n" +
//            "       or lower(a.acct_name) like :search)\n" +
//            "and (:status is null or p.pol_current_status = cast(:status as text)) \n" +
//            "and (:hasPermission = true or u.user_id = :userId) \n" +
//            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
//    List<Object[]> findUserPolicyPortfolio(
//            @Param("search") String search,
//            @Param("pageNo") int pageNo,
//            @Param("limit") int limit,
//            @Param("hasPermission") boolean hasPermission,
//            @Param("userId") Long userId,
//            @Param("status") String status);

    @Query(value = "select distinct p.pol_no, p.pol_client_pol_no,\n" +
            "    case when p.pol_current_status = 'A' then 'Active'\n" +
            "         when p.pol_current_status = 'D' then 'Draft'\n" +
            "         when p.pol_current_status = 'CN' then 'Cancelled'\n" +
            "         when p.pol_current_status = 'CO' then 'Converted'\n" +
            "         when p.pol_current_status = 'R' then 'Ready'\n" +
            "         when p.pol_current_status = 'PL' then 'Pre-Loaded'\n" +
            "         when p.pol_current_status = 'PD' then 'Loaded Policy'\n" +
            "         when p.pol_current_status = 'LD' then 'Loaded Data'\n" +
            "         else p.pol_current_status end as pol_current_status,\n" +
            "    p.pol_rev_no, p.pol_id, concat(c.client_fname, ' ', c.client_onames) client,\n" +
            "    coalesce(a.acct_name, a2.acct_name) acct_name, \n" +
            "    p.pol_uw_yr, case when a.acct_name is not null then sbb.bin_name else sbb2.bin_name end as bin_name, \n" +
            "    p.pol_renewable, sbp.pr_desc product, \n" +
            "    cast(p.pol_wef_date as date) wefDate, cast(p.pol_wet_date as date) wetDate, \n" +
            "    curr.cur_iso_code currency, u.user_username username, \n" +
            "    c.client_idno idNo, p.auth_comments as authComments, COUNT(*) OVER() as total_rows\n" +
            "from sys_brk_policies p\n" +
            "join sys_brk_risks sbr on p.pol_id = sbr.risk_pol_id\n" +
            "join sys_brk_clients c on p.pol_client_id = c.client_id\n" +
            "join sys_brk_products sbp on p.pol_prod_id = sbp.pr_code\n" +
            "join sys_brk_product_grp sbpg on sbp.pr_bpg_code = sbpg.bpg_code\n" +
            "join sys_brk_currencies curr on p.pol_curr_id = curr.cur_code\n" +
            "join sys_brk_users u on p.pol_created_user = u.user_id\n" +
            "join sys_brk_accounts a on p.pol_agent_id = a.acct_id\n" +
            "join sys_brk_binders sbb on p.pol_binder_id = sbb.bin_id\n" +
            "left join sys_brk_policy_binders sbpb on p.pol_id = sbpb.pol_bind_policy_id\n" +
            "left join sys_brk_binders sbb2 on sbpb.pol_bind_bind_id = sbb2.bin_id\n" +
            "left join sys_brk_accounts a2 on a2.acct_id = sbb2.bin_acct_code\n" +
            "where (lower(c.client_fname) like :search \n" +
            "       or lower(sbp.pr_desc) like :search \n" +
            "       or lower(p.pol_no) like :search \n" +
            "       or lower(p.pol_rev_no) like :search \n" +
            "       or lower(a.acct_name) like :search)\n" +
            "and (:status is null or p.pol_current_status = cast(:status as text)) \n" +
            "and (:hasPermission = true or u.user_id = :userId) \n" +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)

    List<Object[]> findUserPolicyPortfolio(
            @Param("search") String search,
            @Param("pageNo") int pageNo,
            @Param("limit") int limit,
            @Param("hasPermission") boolean hasPermission,
            @Param("userId") Long userId,
            @Param("status") String status);




    @Query(value = "select sum(rect_amount) from sys_brk_receipt_dtls where rect_pol_id = :polCode and rect_dc ='C'",nativeQuery = true)
    BigDecimal paidPremium(@Param("polCode") Long polCode);

    @Query(value = "select distinct pol_no,pol_client_pol_no,pol_id,concat(sbc.client_fname ,' ', sbc.client_onames),sba.acct_name, " +
            "pol_uw_yr, sba.acct_acc_code,pol_renewable,sbp.pr_desc,cast(p.pol_wef_date as date)," +
            "cast(p.pol_wet_date as date),sbc2.cur_iso_code,sbu.user_username,pol_ref_no,pol_current_status, p.auth_comments " +
            "from sys_brk_policies p " +
            "left join sys_brk_risks sbr on p.pol_id = sbr.risk_pol_id " +
            "left join sys_brk_clients sbc on p.pol_client_id = sbc.client_id " +
            "left join sys_brk_products sbp on p.pol_prod_id = sbp.pr_code " +
            "left join sys_brk_product_grp sbpg on sbp.pr_bpg_code = sbp.pr_bpg_code " +
            "left join sys_brk_currencies sbc2 on p.pol_curr_id = sbc2.cur_code " +
            "left join sys_brk_users sbu on p.pol_created_user = sbu.user_id " +
            "left join sys_brk_accounts sba on p.pol_agent_id = sba.acct_id " +
            "where coalesce(p.pol_no, '') like :polNo " +
            "and (p.pol_ref_no like :drNumber or :drNumber = '%%')" +
            "and (:riskShtDesc = '%%' or sbr.risk_sht_desc like :riskShtDesc) " +
            "and p.pol_agent_id = case when :agentCode = -2000 then coalesce(pol_agent_id,-2000) else :agentCode end " +
            "and p.pol_prod_id = case when :prodCode= -2000 then coalesce(pol_prod_id,-2000) else :prodCode end " +
            "and p.pol_client_id =case when :clientId=-2000 then coalesce(pol_client_id,-2000) else :clientId end " +
            "and pol_auth_status = 'A'" +
            "order by pol_wef_date desc " +
            "offset :pageNo *:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> findEnquiryActiveorLapsedMedPolicies(@Param("polNo") String polNo,
                                                        @Param("riskShtDesc") String riskShtDesc,
                                                        @Param("agentCode") Long agentCode,
                                                        @Param("prodCode") Long prodCode,
                                                        @Param("clientId") Long clientId,
                                                        @Param("drNumber") String drNumber,
                                                        @Param("pageNo") int pageNo,
                                                        @Param("limit") int limit);

@Query(value = "SELECT p.pol_id, p.pol_basic_premium_amt, CAST(p.pol_cover_from AS DATE) AS pol_cover_from, "
        + "CAST(p.pol_cover_to AS DATE) AS pol_cover_to, p.pol_no, p.pol_sum_insur_amt, sbtp.trans_processing_cover_type, "
        + "CAST(sbtp.trans_processing_date_processed AS DATE) AS pol_processed_date, COUNT(*) OVER() AS total_rows "
        + "FROM sys_brk_policies p "
        + "INNER JOIN sys_brk_trans_processing sbtp on sbtp.trans_processing_pol_id = p.pol_id "
        + "WHERE p.pol_auth_status = 'PL' "
        + "AND (:search IS NULL OR :search = '' "
        + "OR p.pol_no LIKE :search "
        + "OR CAST(p.pol_cover_from AS TEXT) LIKE :search "
        + "OR CAST(p.pol_cover_to AS TEXT) LIKE :search "
        + "OR CAST(sbtp.trans_processing_date_processed AS TEXT) LIKE :search "
        + "OR sbtp.trans_processing_cover_type LIKE :search) "
        + "ORDER BY p.pol_id DESC "
        + "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
List<Object[]> findUnauthorizedPolicies(@Param("search") String search,
                                        @Param("pageNo") int pageNo,
                                        @Param("limit") int limit);

@Query(value = "select pol_prev_policy from sys_brk_policies where pol_id=:polId",nativeQuery = true)
Long getPreviousTrans(@Param("polId") Long polId);



    @Query(value = "SELECT p.pol_proposal_no, p.pol_no, CAST(p.pol_date AS DATE) AS pol_date,\n" +
            "             CAST(p.pol_wef_date AS DATE) AS pol_wef_date,CAST(p.pol_wet_date AS DATE) AS pol_wet_date,\n" +
            "             p.pol_id, p.pol_current_status,sbu.user_username  , COUNT(*) OVER() AS total_rows \n" +
            "           FROM sys_brk_policies p\n" +
            "           join sys_brk_users sbu on p.pol_created_user = sbu.user_id \n" +
            "            WHERE p.pol_current_status = 'D' and p.pol_business_type IN ('N','L')\n" +
            "            AND p.pol_no = :policyNo\n" +
            "            ORDER BY p.pol_id DESC "
            + "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findPendingTransactions(@Param("policyNo") String policyNo,
                                            @Param("pageNo") int pageNo,
                                            @Param("limit") int limit);

    @Query(value = "SELECT * FROM sys_brk_policies sbp WHERE sbp.pol_agent_id = :polAgentId " +
            "AND LOWER(sbp.pol_no) = LOWER(:policyNo) " +
            "AND (LOWER(sbp.pol_ref_no) = LOWER(:policyRefNo) OR LOWER(sbp.pol_proposal_no) = LOWER(:policyProposalNo) OR LOWER(sbp.pol_rev_no) = LOWER(:polRevNo))",
            nativeQuery = true)
    PolicyTrans findBulkProcessPolicytrans(@Param("polAgentId") Long polAgentId, @Param("policyNo") String policyNo, @Param("policyRefNo") String policyRefNo, @Param("policyProposalNo") String policyProposalNo, @Param("polRevNo") String polRevNo  );


    @Query("SELECT pt FROM PolicyTrans pt JOIN FETCH pt.createdUser WHERE pt.policyId = :policyId")
    PolicyTrans findByIdWithCreatedUser(@Param("policyId") Long policyId);

    @Query(value = "SELECT * FROM sys_brk_policies sbp WHERE LOWER(sbp.pol_no) = LOWER(:policyNo) and LOWER(sbp.pol_rev_no) = LOWER(:pol_rev_no)", nativeQuery = true)
    PolicyTrans findBulkProcessPolicytransProposalAndPolNo(@Param("policyNo") String policyNo, @Param("pol_rev_no") String pol_rev_no);

    //@Query(value = "SELECT * FROM sys_brk_policies sbp WHERE LOWER(sbp.pol_no) = LOWER(:policyNo) and pol_current_status = 'A'", nativeQuery = true)
    @Query(value = "SELECT *\n" +
            "FROM sys_brk_policies sbp\n" +
            "WHERE LOWER(sbp.pol_no) = LOWER(:policyNo) \n" +
            "  AND (\n" +
            "        (sbp.pol_trans_type = 'BU' AND sbp.pol_current_status = 'BU')\n" +
            "     OR (sbp.pol_trans_type <> 'BU' AND sbp.pol_current_status = 'A'))", nativeQuery = true)
    PolicyTrans findActiveNonNNNBulkProcess(@Param("policyNo") String policyNo);

    @Query(value = "select pol_id from sys_brk_policies sbp where sbp.pol_interface_type ='A' and sbp.pol_auth_status ='A' and sbp.pol_current_status != 'BU' and sbp.pol_current_status != 'CN'", nativeQuery = true)
    List<BigInteger> findNonBulkProcessPolicytrans();


    @Query(value = "SELECT pol_refund_comments FROM sys_brk_policies WHERE pol_id = :polCode", nativeQuery = true)
    String getRefundCommentsByPolicyId(@Param("polCode") Long polCode);
}
