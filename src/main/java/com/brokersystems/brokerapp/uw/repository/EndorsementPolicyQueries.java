package com.brokersystems.brokerapp.uw.repository;

public interface EndorsementPolicyQueries {

    String countPolicyQuery = "WITH polEnquiry AS (\n" +
            "    SELECT DISTINCT\n" +
            "        p.pol_no,\n" +
            "        p.pol_client_pol_no,\n" +
            "        p.pol_rev_no,\n" +
            "        p.pol_id,\n" +
            "        CONCAT(c.client_fname, ' ', c.client_onames) AS client,\n" +
            "        COALESCE(a.acct_name, a2.acct_name) AS acct_name,\n" +
            "        p.pol_uw_yr,\n" +
            "        CASE\n" +
            "            WHEN a.acct_name IS NOT NULL THEN sbb.bin_name\n" +
            "            ELSE sbb2.bin_name\n" +
            "        END AS bin_name,\n" +
            "        p.pol_renewable AS renewable,\n" +
            "        sbp.pr_desc AS product,\n" +
            "        CAST(p.pol_wef_date AS DATE) AS wefDate,\n" +
            "        CAST(p.pol_wet_date AS DATE) AS wetDate,\n" +
            "        curr.cur_iso_code AS currency,\n" +
            "        u.user_username AS username,\n" +
            "        c.client_idno As idNo,\n" +
            "        c.client_pin As KRA_Pin,\n" +
            "        c.client_cif As CIF\n" +
            "    FROM sys_brk_policies p\n" +
            "    INNER JOIN sys_brk_risks sbr ON p.pol_id = sbr.risk_pol_id\n" +
            "    INNER JOIN sys_brk_clients c ON p.pol_client_id = c.client_id\n" +
            "    INNER JOIN sys_brk_products sbp ON p.pol_prod_id = sbp.pr_code\n" +
            "    INNER JOIN sys_brk_product_grp sbpg ON sbp.pr_bpg_code = sbpg.bpg_code\n" +
            "    INNER JOIN sys_brk_currencies curr ON p.pol_curr_id = curr.cur_code\n" +
            "    INNER JOIN sys_brk_users u ON p.pol_created_user = u.user_id\n" +
            "    LEFT OUTER JOIN sys_brk_accounts a ON p.pol_agent_id = a.acct_id\n" +
            "    LEFT OUTER JOIN sys_brk_binders sbb ON p.pol_binder_id = sbb.bin_id\n" +
            "    LEFT OUTER JOIN sys_brk_policy_binders sbpb ON p.pol_id = sbpb.pol_bind_policy_id\n" +
            "    LEFT OUTER JOIN sys_brk_binders sbb2 ON sbpb.pol_bind_bind_id = sbb2.bin_id\n" +
            "    LEFT OUTER JOIN sys_brk_accounts a2 ON a2.acct_id = sbb2.bin_acct_code\n" +
            "    WHERE sbpg.bpg_type NOT IN ('MD')\n" +
            "    AND (\n" +
            "        LOWER(p.pol_no) LIKE LOWER(CONCAT('%', ?, '%')) OR\n" +
            "        LOWER(sbr.risk_sht_desc) LIKE LOWER(CONCAT('%', ?, '%')) OR\n" +
            "        LOWER(p.pol_ref_no) LIKE LOWER(CONCAT('%', ?, '%')) OR\n" +
            "        LOWER(a.acct_name) LIKE LOWER(CONCAT('%',?,'%')) OR\n" +
            "        LOWER(a2.acct_name) LIKE LOWER(CONCAT('%',?,'%')) OR \n"+
            "        LOWER(sbp.pr_desc) LIKE LOWER(CONCAT('%',?,'%')) OR\n" +
            "        LOWER(c.client_idno) LIKE LOWER(CONCAT('%',?,'%')) OR\n" +
            "        LOWER(c.client_pin) LIKE LOWER(CONCAT('%',?,'%')) OR\n" +
            "        LOWER(c.client_cif) LIKE LOWER(CONCAT('%',?,'%')) OR\n" +
            "        LOWER(CONCAT(c.client_fname,' ',c.client_onames)) LIKE LOWER(CONCAT('%',?,'%'))\n" +
            "    )\n" +
            ")\n" +
            "SELECT COUNT(1) AS policies\n" +
            "FROM polEnquiry pe;\n";


    String getPolicyEnquiryQuery = "WITH polEnquiry AS (\n" +
            "    SELECT DISTINCT\n" +
            "        p.pol_no,\n" +
            "        p.pol_client_pol_no,\n" +
            "        p.pol_rev_no,\n" +
            "        p.pol_id,\n" +
            "        CONCAT(c.client_fname, ' ', c.client_onames) AS client,\n" +
            "        COALESCE(a.acct_name, a2.acct_name) AS acct_name,\n" +
            "        p.pol_uw_yr,\n" +
            "        CASE\n" +
            "            WHEN a.acct_name IS NOT NULL THEN sbb.bin_name\n" +
            "            ELSE sbb2.bin_name\n" +
            "        END AS bin_name,\n" +
            "        p.pol_renewable AS renewable,\n" +
            "        sbp.pr_desc AS product,\n" +
            "        CAST(p.pol_wef_date AS DATE) AS wefDate,\n" +
            "        CAST(p.pol_wet_date AS DATE) AS wetDate,\n" +
            "        curr.cur_iso_code AS currency,\n" +
            "        u.user_username AS username,\n" +
            "        c.client_idno As idNo,\n" +
            "        c.client_pin As KRA_Pin,\n" +
            "        c.client_cif As CIF,\n" +
            "        p.auth_comments AS auth_comments,\n" +
            "        p.pol_trans_type AS transType\n" +
            "    FROM sys_brk_policies p\n" +
            "    INNER JOIN sys_brk_risks sbr ON p.pol_id = sbr.risk_pol_id\n" +
            "    INNER JOIN sys_brk_clients c ON p.pol_client_id = c.client_id\n" +
            "    INNER JOIN sys_brk_products sbp ON p.pol_prod_id = sbp.pr_code\n" +
            "    INNER JOIN sys_brk_product_grp sbpg ON sbp.pr_bpg_code = sbpg.bpg_code\n" +
            "    INNER JOIN sys_brk_currencies curr ON p.pol_curr_id = curr.cur_code\n" +
            "    INNER JOIN sys_brk_users u ON p.pol_created_user = u.user_id\n" +
            "    LEFT OUTER JOIN sys_brk_accounts a ON p.pol_agent_id = a.acct_id\n" +
            "    LEFT OUTER JOIN sys_brk_binders sbb ON p.pol_binder_id = sbb.bin_id\n" +
            "    LEFT OUTER JOIN sys_brk_policy_binders sbpb ON p.pol_id = sbpb.pol_bind_policy_id\n" +
            "    LEFT OUTER JOIN sys_brk_binders sbb2 ON sbpb.pol_bind_bind_id = sbb2.bin_id\n" +
            "    LEFT OUTER JOIN sys_brk_accounts a2 ON a2.acct_id = sbb2.bin_acct_code\n" +
            "    WHERE sbpg.bpg_type NOT IN ('MD')\n" +
            "    AND (\n" +
            "        LOWER(p.pol_no) LIKE LOWER(CONCAT('%', ?, '%')) OR\n" +
            "        LOWER(sbr.risk_sht_desc) LIKE LOWER(CONCAT('%', ?, '%')) OR\n" +
            "        LOWER(p.pol_ref_no) LIKE LOWER(CONCAT('%', ?, '%')) OR\n" +
            "        LOWER(a.acct_name) LIKE LOWER(CONCAT('%',?,'%')) OR\n" +
            "        LOWER(a2.acct_name) LIKE LOWER(CONCAT('%',?,'%')) OR \n"+
            "        LOWER(sbp.pr_desc) LIKE LOWER(CONCAT('%',?,'%')) OR\n" +
            "        LOWER(c.client_idno) LIKE LOWER(CONCAT('%',?,'%')) OR\n" +
            "        LOWER(c.client_pin) LIKE LOWER(CONCAT('%',?,'%')) OR\n" +
            "        LOWER(c.client_cif) LIKE LOWER(CONCAT('%',?,'%')) OR\n" +
            "        LOWER(CONCAT(c.client_fname,' ',c.client_onames)) LIKE LOWER(CONCAT('%',?,'%'))\n" +
            "    )\n" +
            "),\n" +
            "RowConstrainedResult AS (\n" +
            "    SELECT ROW_NUMBER() OVER (ORDER BY pol_no) AS RowNum, pe.*,\n" +
            "    COUNT(*) OVER() AS total_rows\n" +
            "    FROM polEnquiry pe\n" +
            ")\n" +
            "SELECT rcr.*\n" +
            "FROM RowConstrainedResult rcr\n" +
            "WHERE rcr.RowNum >= ((?) * ?) + 1\n" +
            "AND rcr.RowNum < ((? + 1) * ?) + 1\n" +
            "ORDER BY rcr.RowNum;";



    String countEndorsementsQuery="with endorsement as (\n" +
            "                Select distinct p.pol_no,\n" +
            "                                p.pol_client_pol_no,\n" +
            "                                p.pol_rev_no,\n" +
            "                                p.pol_id,\n" +
            "                                concat(c.client_fname ,' ', c.client_onames)                                              client,\n" +
            "                                coalesce(a.acct_name, a2.acct_name)                                           acct_name,\n" +
            "                                p.pol_uw_yr,\n" +
            "                                case when a.acct_name is not null then sbb.bin_name else sbb2.bin_name end as bin_name,\n" +
            "                                p.pol_renewable                                                               renewable,\n" +
            "                                sbp.pr_desc                                                                   product,\n" +
            "                                cast(p.pol_wef_date as date)                                                  wefDate,\n" +
            "                                cast(p.pol_wet_date as date)                                                  wetDate,\n" +
            "                                curr.cur_iso_code                                                             currency,\n" +
            "                                u.user_username                                                               username\n" +
            "                from sys_brk_policies p\n" +
            "                         inner join sys_brk_risks sbr on p.pol_id = sbr.risk_pol_id\n" +
            "                         inner join sys_brk_clients c on p.pol_client_id = c.client_id\n" +
            "                         inner join sys_brk_products sbp on p.pol_prod_id = sbp.pr_code\n" +
            "                         inner join sys_brk_product_grp sbpg on sbp.pr_bpg_code = sbpg.bpg_code\n" +
            "                         inner join sys_brk_currencies curr on p.pol_curr_id = curr.cur_code\n" +
            "                         inner join sys_brk_users u on p.pol_created_user = u.user_id\n" +
            "                         left outer join sys_brk_accounts a on p.pol_agent_id = a.acct_id\n" +
            "                         left outer join sys_brk_binders sbb on p.pol_binder_id = sbb.bin_id\n" +
            "                         left outer join sys_brk_policy_binders sbpb on p.pol_id = sbpb.pol_bind_policy_id\n" +
            "                         left outer join sys_brk_binders sbb2 on sbpb.pol_bind_bind_id = sbb2.bin_id\n" +
            "                         left outer join sys_brk_accounts a2 on a2.acct_id = sbb2.bin_acct_code\n" +
            "                where sbpg.bpg_type not in ('MD','L')\n" +
            "                  and p.pol_current_status = 'A'\n" +
            "                  and p.pol_no = coalesce(?, p.pol_no)\n" +
            "                  and sbr.risk_sht_desc = coalesce(?, sbr.risk_sht_desc)\n" +
            "                  and p.pol_ref_no = coalesce(?, p.pol_ref_no)\n" +
            "                  and (a.acct_id  = coalesce(?, a.acct_id) or a2.acct_id = coalesce(?, a2.acct_id))\n" +
            "                  and  c.client_id = coalesce(?, c.client_id)\n" +
            "                ) select count(1) policies from endorsement en\n" +
            "            where en.renewable =coalesce(?,en.renewable);";

    String getEndorsementsQuery="with endorsement as (\n" +
            "                Select distinct p.pol_no,\n" +
            "                                p.pol_client_pol_no,\n" +
            "                                p.pol_rev_no,\n" +
            "                                p.pol_id,\n" +
            "                                concat(c.client_fname ,' ', c.client_onames)                                              client,\n" +
            "                                coalesce(a.acct_name, a2.acct_name)                                           acct_name,\n" +
            "                                p.pol_uw_yr,\n" +
            "                                case when a.acct_name is not null then sbb.bin_name else sbb2.bin_name end as bin_name,\n" +
            "                                p.pol_renewable                                                               renewable,\n" +
            "                                sbp.pr_desc                                                                   product,\n" +
            "                                cast(p.pol_wef_date as date)                                                  wefDate,\n" +
            "                                cast(p.pol_wet_date as date)                                                  wetDate,\n" +
            "                                curr.cur_iso_code                                                             currency,\n" +
            "                                u.user_username                                                               username\n" +
            "                from sys_brk_policies p\n" +
            "                         inner join sys_brk_risks sbr on p.pol_id = sbr.risk_pol_id\n" +
            "                         inner join sys_brk_clients c on p.pol_client_id = c.client_id\n" +
            "                         inner join sys_brk_products sbp on p.pol_prod_id = sbp.pr_code\n" +
            "                         inner join sys_brk_product_grp sbpg on sbp.pr_bpg_code = sbpg.bpg_code\n" +
            "                         inner join sys_brk_currencies curr on p.pol_curr_id = curr.cur_code\n" +
            "                         inner join sys_brk_users u on p.pol_created_user = u.user_id\n" +
            "                         left outer join sys_brk_accounts a on p.pol_agent_id = a.acct_id\n" +
            "                         left outer join sys_brk_binders sbb on p.pol_binder_id = sbb.bin_id\n" +
            "                         left outer join sys_brk_policy_binders sbpb on p.pol_id = sbpb.pol_bind_policy_id\n" +
            "                         left outer join sys_brk_binders sbb2 on sbpb.pol_bind_bind_id = sbb2.bin_id\n" +
            "                         left outer join sys_brk_accounts a2 on a2.acct_id = sbb2.bin_acct_code\n" +
            "                where sbpg.bpg_type not in ('MD','L')\n" +
            "                  and p.pol_current_status = 'A'\n" +
            "                  and p.pol_no = coalesce(?, p.pol_no)\n" +
            "                  and sbr.risk_sht_desc = coalesce(?, sbr.risk_sht_desc)\n" +
            "                  and p.pol_ref_no = coalesce(?, p.pol_ref_no)\n" +
            "                  and (a.acct_id  = coalesce(?, a.acct_id) or a2.acct_id = coalesce(?, a2.acct_id))\n" +
            "                  and  c.client_id = coalesce(?, c.client_id)\n" +
            "                ),RowConstrainedResult as (\n" +
            "                SELECT  ROW_NUMBER() OVER (ORDER BY pol_no) AS RowNum,en.*\n" +
            "                from endorsement en\n" +
            "                where en.renewable =coalesce(?,en.renewable)\n" +
            "            )\n" +
            "            SELECT  rcr.*\n" +
            "                 from RowConstrainedResult rcr\n" +
            "            where  rcr.RowNum >= ((?) * ?) + 1\n" +
            "                  AND rcr.RowNum < ((? + 1) * ?) + 1\n" +
            "                ORDER BY rcr.RowNum;";



    String countMedEndorsementsQuery="with medEndorse as (\n" +
            "                Select distinct p.pol_no,\n" +
            "                                p.pol_client_pol_no,\n" +
            "                                p.pol_rev_no,\n" +
            "                                p.pol_id,\n" +
            "                                c.client_fname + c.client_onames client,\n" +
            "                                a.acct_name                      acct_name,\n" +
            "                                p.pol_uw_yr,\n" +
            "                                sbb.bin_name                     bin_name,\n" +
            "                                p.pol_renewable                                                               renewable,\n" +
            "                                sbp.pr_desc                                                                   product,\n" +
            "                                cast(p.pol_wef_date as date)                                                  wefDate,\n" +
            "                                cast(p.pol_wet_date as date)                                                  wetDate,\n" +
            "                                curr.cur_iso_code                                                             currency,\n" +
            "                                u.user_username                                                               username\n" +
            "                from sys_brk_policies p\n" +
            "                         inner join sys_brk_clients c on p.pol_client_id = c.client_id\n" +
            "                         inner join sys_brk_products sbp on p.pol_prod_id = sbp.pr_code\n" +
            "                         inner join sys_brk_product_grp sbpg on sbp.pr_bpg_code = sbpg.bpg_code\n" +
            "                         inner join sys_brk_currencies curr on p.pol_curr_id = curr.cur_code\n" +
            "                         inner join sys_brk_users u on p.pol_created_user = u.user_id\n" +
            "                         inner join sys_brk_accounts a on p.pol_agent_id = a.acct_id\n" +
            "                         inner join sys_brk_binders sbb on p.pol_binder_id = sbb.bin_id\n" +
            "                where sbpg.bpg_type in ('MD')\n" +
            "                  and p.pol_current_status = 'A'\n" +
            "                  and p.pol_no = coalesce(?, p.pol_no)\n" +
            "                  and p.pol_ref_no = coalesce(?, p.pol_ref_no)\n" +
            "                  and  a.acct_id = coalesce(?, a.acct_id)\n" +
            "                  and  c.client_id = coalesce(?, c.client_id)\n" +
            "            )\n" +
            "            select count(1) policies\n" +
            "            from medEndorse md;";

    String getMedEndorsementsQuery="\n" +
            "with medEndorse as (\n" +
            "    Select distinct p.pol_no,\n" +
            "                    p.pol_client_pol_no,\n" +
            "                    p.pol_rev_no,\n" +
            "                    p.pol_id,\n" +
            "                    c.client_fname + c.client_onames client,\n" +
            "                    a.acct_name                      acct_name,\n" +
            "                    p.pol_uw_yr,\n" +
            "                    sbb.bin_name                     bin_name,\n" +
            "                    p.pol_renewable                  renewable,\n" +
            "                    sbp.pr_desc                      product,\n" +
            "                    cast(p.pol_wef_date as date)     wefDate,\n" +
            "                    cast(p.pol_wet_date as date)     wetDate,\n" +
            "                    curr.cur_iso_code                currency,\n" +
            "                    u.user_username                  username\n" +
            "    from sys_brk_policies p\n" +
            "             inner join sys_brk_clients c on p.pol_client_id = c.client_id\n" +
            "             inner join sys_brk_products sbp on p.pol_prod_id = sbp.pr_code\n" +
            "             inner join sys_brk_product_grp sbpg on sbp.pr_bpg_code = sbpg.bpg_code\n" +
            "             inner join sys_brk_currencies curr on p.pol_curr_id = curr.cur_code\n" +
            "             inner join sys_brk_users u on p.pol_created_user = u.user_id\n" +
            "             inner join sys_brk_accounts a on p.pol_agent_id = a.acct_id\n" +
            "             inner join sys_brk_binders sbb on p.pol_binder_id = sbb.bin_id\n" +
            "    where sbpg.bpg_type in ('MD')\n" +
            "      and p.pol_current_status = 'A'\n" +
            "      and p.pol_no = coalesce(?, p.pol_no)\n" +
            "      and p.pol_ref_no = coalesce(?, p.pol_ref_no)\n" +
            "      and a.acct_id = coalesce(?, a.acct_id)\n" +
            "      and c.client_id = coalesce(?, c.client_id)\n" +
            "),\n" +
            "     RowConstrainedResult as (\n" +
            "         SELECT ROW_NUMBER() OVER (ORDER BY pol_no) AS RowNum, me.*\n" +
            "         from medEndorse me\n" +
            "     )\n" +
            "SELECT rcr.*\n" +
            "from RowConstrainedResult rcr\n" +
            "where rcr.RowNum >= ((?) * ?) + 1\n" +
            "  AND rcr.RowNum < ((? + 1) * ?) + 1\n" +
            "ORDER BY rcr.RowNum;";


    String countContraPoliciesQuery = " select COUNT(1) count\n" +
            "  from (Select distinct p.pol_no,\n" +
            "                        p.pol_client_pol_no,\n" +
            "                        p.pol_rev_no,\n" +
            "                        p.pol_id,\n" +
            "                        concat(c.client_fname,' ', c.client_onames) client,\n" +
            "                        case\n" +
            "                          when a.acct_name is null then 'Multiple Insurers'\n" +
            "                          else a.acct_name end  as acct_name,\n" +
            "                        p.pol_uw_yr,\n" +
            "                        case\n" +
            "                          when sbb.bin_name is null then 'Multiple Binders'\n" +
            "                          else sbb.bin_name end as bin_name,\n" +
            "                    p.pol_renewable                                                               renewable,\n" +
            "                    sbp.pr_desc                                                                   product,\n" +
            "                    cast(p.pol_wef_date as date)                                                  wefDate,\n" +
            "                    cast(p.pol_wet_date as date)                                                  wetDate,\n" +
            "                    curr.cur_iso_code                                                             currency,\n" +
            "                    u.user_username                                                               username" +
            "        from sys_brk_policies p\n" +
            "               inner join sys_brk_risks sbr on p.pol_id = sbr.risk_pol_id\n" +
            "               inner join sys_brk_clients c on p.pol_client_id = c.client_id\n" +
            "               inner join sys_brk_products sbp on p.pol_prod_id = sbp.pr_code\n" +
            "               inner join sys_brk_product_grp sbpg on sbp.pr_bpg_code = sbpg.bpg_code\n" +
            "               inner join sys_brk_currencies curr on p.pol_curr_id = curr.cur_code\n" +
            "               inner join sys_brk_users u on p.pol_created_user = u.user_id\n" +
            "               left outer join sys_brk_accounts a on p.pol_agent_id = a.acct_id\n" +
            "               left outer join sys_brk_binders sbb on p.pol_binder_id = sbb.bin_id\n" +
            "        where (p.pol_current_status = 'A' OR p.pol_current_status = 'CN')\n" +
            "          and p.pol_no = coalesce(?, p.pol_no)\n" +
            "          and sbr.risk_sht_desc = coalesce(?, sbr.risk_sht_desc)\n" +
            "          and p.pol_ref_no = coalesce(?, p.pol_ref_no)\n" +
            "          and (a.acct_name is null or a.acct_name like '%?%' or a.acct_name = coalesce(?, a.acct_name))\n" +
            "          and (concat(c.client_fname ,' ', c.client_onames) = coalesce(?, concat(c.client_fname ,' ', c.client_onames)) or\n" +
            "               concat(c.client_fname ,' ', c.client_onames) like ? or concat(c.client_fname ,' ', c.client_onames) like ? or\n" +
            "               c.client_sht_desc like ? or\n" +
            "               c.client_cust_ref like ?)) u;";





//    String getContraPoliciesQuery = "SELECT pol_no,\n" +
//            "       pol_client_pol_no,\n" +
//            "       pol_rev_no,\n" +
//            "       pol_id,\n" +
//            "       client,\n" +
//            "       acct_name,\n" +
//            "       pol_uw_yr,\n" +
//            "       bin_name,\n" +
//            "       renewable,\n" +
//            "       product,\n" +
//            "       wefDate,\n" +
//            "       wetDate,\n" +
//            "       currency,\n" +
//            "       username,\n" +
//            "       auth_comments,\n" +
//            "       transtype,\n" +
//            "       total_rows\n" +
//            "from (SELECT ROW_NUMBER() OVER (ORDER BY pol_no) AS RowNum,\n" +
//            "             pol_no,\n" +
//            "             pol_client_pol_no,\n" +
//            "             pol_rev_no,\n" +
//            "             pol_id,\n" +
//            "             client,\n" +
//            "             acct_name,\n" +
//            "             pol_uw_yr,\n" +
//            "             bin_name,\n" +
//            "             renewable,\n" +
//            "             product,\n" +
//            "             wefDate,\n" +
//            "             wetDate,\n" +
//            "             currency,\n" +
//            "             username,\n" +
//            "             auth_comments,\n" +
//            "             transtype,\n" +
//            "             COUNT(*) OVER() AS total_rows\n" +
//            "      FROM (Select distinct p.pol_no,\n" +
//            "                            p.pol_client_pol_no,\n" +
//            "                            p.pol_rev_no,\n" +
//            "                            p.pol_id,\n" +
//            "                            concat(c.client_fname ,' ', c.client_onames) client,\n" +
//            "                            case\n" +
//            "                              when a.acct_name is null then 'Multiple Insurers'\n" +
//            "                              else a.acct_name end  as       acct_name,\n" +
//            "                            p.pol_uw_yr,\n" +
//            "                            case\n" +
//            "                              when sbb.bin_name is null then 'Multiple Binders'\n" +
//            "                              else sbb.bin_name end as       bin_name,\n" +
//            "                            p.pol_renewable                                                               renewable,\n" +
//            "                            sbp.pr_desc                                                                   product,\n" +
//            "                            cast(p.pol_wef_date as date)                                                  wefDate,\n" +
//            "                            cast(p.pol_wet_date as date)                                                  wetDate,\n" +
//            "                            curr.cur_iso_code                                                             currency,\n" +
//            "                            u.user_username                                                               username,\n" +
//            "                            p.auth_comments                                                               auth_comments,\n" +
//            "                            p.pol_trans_type                                                             transtype\n" +
//            "            from sys_brk_policies p\n" +
//            "                   inner join sys_brk_risks sbr on p.pol_id = sbr.risk_pol_id\n" +
//            "                   inner join sys_brk_clients c on p.pol_client_id = c.client_id\n" +
//            "                   inner join sys_brk_products sbp on p.pol_prod_id = sbp.pr_code\n" +
//            "                   inner join sys_brk_product_grp sbpg on sbp.pr_bpg_code = sbpg.bpg_code\n" +
//            "                   inner join sys_brk_currencies curr on p.pol_curr_id = curr.cur_code\n" +
//            "                   inner join sys_brk_users u on p.pol_created_user = u.user_id\n" +
//            "                   left outer join sys_brk_accounts a on p.pol_agent_id = a.acct_id\n" +
//            "                   left outer join sys_brk_binders sbb on p.pol_binder_id = sbb.bin_id\n" +
//            "            where (p.pol_current_status = 'A' OR p.pol_current_status = 'CN')\n" +
//            "              and p.pol_no = coalesce(?, p.pol_no)\n" +
//            "              and sbr.risk_sht_desc = coalesce(?, sbr.risk_sht_desc)\n" +
//            "              and p.pol_ref_no = coalesce(?, p.pol_ref_no)\n" +
//            "              and (a.acct_name is null or a.acct_name like '%?%' or a.acct_name = coalesce(?, a.acct_name))\n" +
//            "              and (concat(c.client_fname ,' ', c.client_onames) = coalesce(?, concat(c.client_fname ,' ', c.client_onames)) or\n" +
//            "                   concat(c.client_fname ,' ', c.client_onames) like ? or concat(c.client_fname ,' ', c.client_onames) like ? or\n" +
//            "                   c.client_sht_desc like ? or\n" +
//            "                   c.client_cust_ref like ?)) u) AS RowConstrainedResult\n" +
//            "WHERE RowNum >= ((?) * ?) + 1\n" +
//            "  AND RowNum < ((? + 1) * ?) + 1\n" +
//            "ORDER BY RowNum;";
String getContraPoliciesQuery = "SELECT pol_no,\n" +
        "pol_client_pol_no,\n" +
        "pol_rev_no,\n" +
        "pol_id,\n" +
        "client,\n" +
        "acct_name,\n" +
        "pol_uw_yr,\n" +
        "bin_name,\n" +
        "renewable,\n" +
        "product,\n" +
        "wefDate,\n" +
        "wetDate,\n" +
        "currency,\n" +
        "username,\n" +
        "auth_comments,\n" +
        "transtype,\n" +
        "total_rows\n" +
        "from (SELECT ROW_NUMBER() OVER (ORDER BY pol_no) AS RowNum,\n" +
        "pol_no,\n" +
        "pol_client_pol_no,\n" +
        "pol_rev_no,\n" +
        "pol_id,\n" +
        "client,\n" +
        "acct_name,\n" +
        "pol_uw_yr,\n" +
        "bin_name,\n" +
        "renewable,\n" +
        "product,\n" +
        "wefDate,\n" +
        "wetDate,\n" +
        "currency,\n" +
        "username,\n" +
        "auth_comments,\n" +
        "transtype,\n" +
        "COUNT(*) OVER() AS total_rows\n" +
        "FROM (Select distinct p.pol_no,\n" +
        "p.pol_client_pol_no,\n" +
        "p.pol_rev_no,\n" +
        "p.pol_id,\n" +
        "concat(c.client_fname ,' ', c.client_onames) client,\n" +
        "case\n" +
        "  when a.acct_name is null then 'Multiple Insurers'\n" +
        "  else a.acct_name end as acct_name,\n" +
        "p.pol_uw_yr,\n" +
        "case\n" +
        "  when sbb.bin_name is null then 'Multiple Binders'\n" +
        "  else sbb.bin_name end as bin_name,\n" +
        "p.pol_renewable renewable,\n" +
        "sbp.pr_desc product,\n" +
        "cast(p.pol_wef_date as date) wefDate,\n" +
        "cast(p.pol_wet_date as date) wetDate,\n" +
        "curr.cur_iso_code currency,\n" +
        "u.user_username username,\n" +
        "p.auth_comments auth_comments,\n" +
        "p.pol_trans_type transtype\n" +
        "from sys_brk_policies p\n" +
        "left join sys_brk_risks sbr on p.pol_id = sbr.risk_pol_id\n" +
        "inner join sys_brk_clients c on p.pol_client_id = c.client_id\n" +
        "inner join sys_brk_products sbp on p.pol_prod_id = sbp.pr_code\n" +
        "inner join sys_brk_product_grp sbpg on sbp.pr_bpg_code = sbpg.bpg_code\n" +
        "inner join sys_brk_currencies curr on p.pol_curr_id = curr.cur_code\n" +
        "inner join sys_brk_users u on p.pol_created_user = u.user_id\n" +
        "left outer join sys_brk_accounts a on p.pol_agent_id = a.acct_id\n" +
        "left outer join sys_brk_binders sbb on p.pol_binder_id = sbb.bin_id\n" +
        "where (p.pol_current_status = 'A' OR p.pol_current_status = 'CN')\n" +
        "and sbpg.bpg_type <> 'L'\n" +  // ADD THIS LINE TO EXCLUDE LIFE POLICIES
        "and p.pol_no = coalesce(?, p.pol_no)\n" +
        "and (sbr.risk_sht_desc = coalesce(?, sbr.risk_sht_desc) OR sbr.risk_sht_desc IS NULL)\n" +
        "and p.pol_ref_no = coalesce(?, p.pol_ref_no)\n" +
        "and (a.acct_name is null or a.acct_name like '%?%' or a.acct_name = coalesce(?, a.acct_name))\n" +
        "and (concat(c.client_fname ,' ', c.client_onames) = coalesce(?, concat(c.client_fname ,' ', c.client_onames)) or\n" +
        "     concat(c.client_fname ,' ', c.client_onames) like ? or concat(c.client_fname ,' ', c.client_onames) like ? or\n" +
        "     c.client_sht_desc like ? or\n" +
        "     c.client_cust_ref like ?)) u) AS RowConstrainedResult\n" +
        "WHERE RowNum >= ((?) * ?) + 1\n" +
        "  AND RowNum < ((? + 1) * ?) + 1\n" +
        "ORDER BY RowNum;";



    String getContraLifePoliciesQuery = "SELECT pol_no,\n" +
            "pol_client_pol_no,\n" +
            "pol_rev_no,\n" +
            "pol_id,\n" +
            "client,\n" +
            "acct_name,\n" +
            "pol_uw_yr,\n" +
            "bin_name,\n" +
            "renewable,\n" +
            "product,\n" +
            "wefDate,\n" +
            "wetDate,\n" +
            "currency,\n" +
            "username,\n" +
            "auth_comments,\n" +
            "transtype,\n" +
            "total_rows\n" +
            "from (SELECT ROW_NUMBER() OVER (ORDER BY pol_no) AS RowNum,\n" +
            "pol_no,\n" +
            "pol_client_pol_no,\n" +
            "pol_rev_no,\n" +
            "pol_id,\n" +
            "client,\n" +
            "acct_name,\n" +
            "pol_uw_yr,\n" +
            "bin_name,\n" +
            "renewable,\n" +
            "product,\n" +
            "wefDate,\n" +
            "wetDate,\n" +
            "currency,\n" +
            "username,\n" +
            "auth_comments,\n" +
            "transtype,\n" +
            "COUNT(*) OVER() AS total_rows\n" +
            "FROM (Select distinct p.pol_no,\n" +
            "p.pol_client_pol_no,\n" +
            "p.pol_rev_no,\n" +
            "p.pol_id,\n" +
            "concat(c.client_fname ,' ', c.client_onames) client,\n" +
            "case\n" +
            "  when a.acct_name is null then 'Multiple Insurers'\n" +
            "  else a.acct_name end as acct_name,\n" +
            "p.pol_uw_yr,\n" +
            "case\n" +
            "  when sbb.bin_name is null then 'Multiple Binders'\n" +
            "  else sbb.bin_name end as bin_name,\n" +
            "p.pol_renewable renewable,\n" +
            "sbp.pr_desc product,\n" +
            "cast(p.pol_wef_date as date) wefDate,\n" +
            "cast(p.pol_wet_date as date) wetDate,\n" +
            "curr.cur_iso_code currency,\n" +
            "u.user_username username,\n" +
            "p.auth_comments auth_comments,\n" +
            "p.pol_trans_type transtype\n" +
            "from sys_brk_policies p\n" +
            "left join sys_brk_risks sbr on p.pol_id = sbr.risk_pol_id\n" +
            "inner join sys_brk_clients c on p.pol_client_id = c.client_id\n" +
            "inner join sys_brk_products sbp on p.pol_prod_id = sbp.pr_code\n" +
            "inner join sys_brk_product_grp sbpg on sbp.pr_bpg_code = sbpg.bpg_code\n" +
            "inner join sys_brk_currencies curr on p.pol_curr_id = curr.cur_code\n" +
            "inner join sys_brk_users u on p.pol_created_user = u.user_id\n" +
            "left outer join sys_brk_accounts a on p.pol_agent_id = a.acct_id\n" +
            "left outer join sys_brk_binders sbb on p.pol_binder_id = sbb.bin_id\n" +
            "where (p.pol_current_status = 'A' OR p.pol_current_status = 'CN')\n" +
            "and sbpg.bpg_type = 'L'\n" +
            "and p.pol_auth_status = 'A'\n" +
            "and p.pol_no = coalesce(?, p.pol_no)\n" +
            "and (sbr.risk_sht_desc = coalesce(?, sbr.risk_sht_desc) OR sbr.risk_sht_desc IS NULL)\n" +
            "and p.pol_ref_no = coalesce(?, p.pol_ref_no)\n" +
            "and (a.acct_name is null or a.acct_name like '%?%' or a.acct_name = coalesce(?, a.acct_name))\n" +
            "and (concat(c.client_fname ,' ', c.client_onames) = coalesce(?, concat(c.client_fname ,' ', c.client_onames)) or\n" +
            "     concat(c.client_fname ,' ', c.client_onames) like ? or concat(c.client_fname ,' ', c.client_onames) like ? or\n" +
            "     c.client_sht_desc like ? or\n" +
            "     c.client_cust_ref like ?)) u) AS RowConstrainedResult\n" +
            "WHERE RowNum >= ((?) * ?) + 1\n" +
            "  AND RowNum < ((? + 1) * ?) + 1\n" +
            "ORDER BY RowNum;";

}