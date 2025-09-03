package com.brokersystems.brokerapp.bulktransactions.repositories;

import com.brokersystems.brokerapp.bulktransactions.models.BulkReceipt;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;

public interface BulkReceiptRepository extends PagingAndSortingRepository<BulkReceipt, Long>, QueryDslPredicateExecutor<BulkReceipt> {

    @Query(value = "SELECT sbbr.bulk_receipt_id, " +
            "CASE " +
            "    WHEN sbbr.bulk_receipt_type = 'N' THEN 'General Insurance' \n" +
            "    WHEN sbbr.bulk_receipt_type = 'L' THEN 'Life Insurance' \n" +
            "END AS bulk_receipt_type, sba.acct_name , sbb.ob_name , sbbr.bulk_receipt_amount , \n" +
            "sbbr.bulk_receipt_paid_by , sbbr.bulk_receipt_doc_date ,sbbr.bulk_receipt_payment_ref, \n" +
            "sbbr.bulk_receipt_manual_ref  , sbbr.bulk_receipt_desc, \n" +
            "COUNT(*) OVER() as total_rows \n" +
            "FROM sys_brk_bulk_receipt sbbr \n" +
            "INNER JOIN sys_brk_accounts sba on sba.acct_id = sbbr.bulk_receipt_acct_id \n" +
            "INNER JOIN sys_brk_branches sbb on sbb.ob_id = sbbr.bulk_receipt_brn_code \n" +
            "WHERE sbbr.bulk_receipt_status = 'N' \n" +
            "AND (\n" +
            "    LOWER(sba.acct_name) LIKE LOWER(:search) \n" +
            "    OR LOWER(sbb.ob_name) LIKE LOWER(:search) \n" +
            "    OR LOWER(bulk_receipt_paid_by) LIKE LOWER(:search) \n" +
            "    OR LOWER(bulk_receipt_payment_ref) LIKE LOWER(:search) \n" +
            "    OR LOWER(bulk_receipt_manual_ref) LIKE LOWER(:search) \n" +
            "    OR LOWER(bulk_receipt_desc) LIKE LOWER(:search) \n" +
            "    OR CAST(bulk_receipt_doc_date AS TEXT) LIKE :search \n" +
            "    OR LOWER(\n" +
            "        CASE  \n" +
            "            WHEN sbbr.bulk_receipt_type = 'N' THEN 'General Insurance'  \n" +
            "            WHEN sbbr.bulk_receipt_type = 'L' THEN 'Life Insurance'  \n" +
            "        END\n" +
            "    ) LIKE LOWER(:search)\n" +
            ")" +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findUnprocessedBulkReceipt(@Param("search") String search,
                                              @Param("pageNo") int pageNo,
                                              @Param("limit") int limit);


    @Query(value = "SELECT sbbr.bulk_receipt_id, " +
            "CASE " +
            "    WHEN sbbr.bulk_receipt_type = 'N' THEN 'General Insurance' \n" +
            "    WHEN sbbr.bulk_receipt_type = 'L' THEN 'Life Insurance' \n" +
            "END AS bulk_receipt_type, sba.acct_name , sbb.ob_name , sbbr.bulk_receipt_amount , \n" +
            "sbbr.bulk_receipt_paid_by , sbbr.bulk_receipt_doc_date ,sbbr.bulk_receipt_payment_ref, \n" +
            "sbbr.bulk_receipt_manual_ref  , sbbr.bulk_receipt_desc, \n" +
            "COUNT(*) OVER() as total_rows \n" +
            "FROM sys_brk_bulk_receipt sbbr \n" +
            "INNER JOIN sys_brk_accounts sba on sba.acct_id = sbbr.bulk_receipt_acct_id \n" +
            "INNER JOIN sys_brk_branches sbb on sbb.ob_id = sbbr.bulk_receipt_brn_code \n" +
            "WHERE sbbr.bulk_receipt_status = 'N' \n" +
            "AND (\n" +
            "    LOWER(sba.acct_name) LIKE LOWER(:search) \n" +
            "    OR LOWER(sbb.ob_name) LIKE LOWER(:search) \n" +
            "    OR LOWER(bulk_receipt_paid_by) LIKE LOWER(:search) \n" +
            "    OR LOWER(bulk_receipt_payment_ref) LIKE LOWER(:search) \n" +
            "    OR LOWER(bulk_receipt_manual_ref) LIKE LOWER(:search) \n" +
            "    OR LOWER(bulk_receipt_desc) LIKE LOWER(:search) \n" +
            "    OR CAST(bulk_receipt_doc_date AS TEXT) LIKE :search \n" +
            "    OR LOWER(\n" +
            "        CASE  \n" +
            "            WHEN sbbr.bulk_receipt_type = 'N' THEN 'General Insurance'  \n" +
            "            WHEN sbbr.bulk_receipt_type = 'L' THEN 'Life Insurance'  \n" +
            "        END\n" +
            "    ) LIKE LOWER(:search)\n" +
            ")" +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findProcessedBulkReceipt(@Param("search") String search,
                                              @Param("pageNo") int pageNo,
                                              @Param("limit") int limit);

    @Query(value = "select  count(*) from sys_brk_bulk_receipt where bulk_receipt_payment_ref = :ref and bulk_receipt_pol_no = :polNo", nativeQuery = true)
    int countBulk(@Param("ref") String ref, @Param("polNo") String polNo);

    @Query(value = "select  * from sys_brk_bulk_receipt where bulk_receipt_payment_ref = :ref and bulk_receipt_pol_no = :polNo", nativeQuery = true)
    BulkReceipt findBypolicyandpaymentref(@Param("ref") String ref, @Param("polNo") String polNo);
}
