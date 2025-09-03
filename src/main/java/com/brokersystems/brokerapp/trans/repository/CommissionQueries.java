package com.brokersystems.brokerapp.trans.repository;

public interface CommissionQueries {

    String countProcessedCommissions = "select COUNT(*) count\n" +
            "from sys_brk_comm_process_trans t\n" +
            "where t.trans_status = 'P'\n" +
            "  and t.trans_insurance = ? ";


    String processedCommQuery = " SELECT trans_receipt,trans_ref_no, trans_date, acct_name, commission_amount, trans_status, trans_net_amt, trans_balance\n" +
            "FROM (select ROW_NUMBER() OVER (ORDER BY trans_receipt) AS RowNum,\n" +
            "             t.trans_receipt,\n" +
            "             sbmt.trans_ref_no,\n" +
            "             cast(sbmt.trans_date as date)                trans_date,\n" +
            "             a.acct_name,\n" +
            "             sum(t.trans_amount)                          commission_amount,\n" +
            "             t.trans_status,\n" +
            "             sbmt.trans_net_amt,\n" +
            "             sbmt.trans_balance\n" +
            "      from sys_brk_comm_process_trans t\n" +
            "             inner join sys_brk_accounts a on a.acct_id = t.trans_insurance\n" +
            "             inner join sys_brk_main_transactions sbmt on t.trans_receipt = sbmt.trans_no\n" +
            "      where t.trans_status = 'P'\n" +
            "        and t.trans_insurance = ? \n" +
            "      group by t.trans_receipt,sbmt.trans_ref_no, cast(sbmt.trans_date as date), a.acct_name, t.trans_status, sbmt.trans_net_amt,\n" +
            "               sbmt.trans_balance) AS RowConstrainedResult\n" +
            "WHERE RowNum >= ((?) * ?) + 1\n" +
            "  AND RowNum < ((? + 1) * ?) + 1\n" +
            "ORDER BY RowNum;";
}



