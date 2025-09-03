package com.brokersystems.brokerapp.trans.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brokersystems.brokerapp.trans.model.ReceiptTransDtls;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ReceiptDetailsRepository extends  PagingAndSortingRepository<ReceiptTransDtls, Long>, QueryDslPredicateExecutor<ReceiptTransDtls> {


    @Query(value = "select * from sys_brk_receipt_dtls where rect_receipt_no=:receiptNo",nativeQuery = true)
    List<ReceiptTransDtls> findAllReceipts(@Param("receiptNo") Long receiptNo);

    @Query(value = "select rect_amount,rect_temp_trans_no,rect_trans_no,rect_pol_id,rect_narration  from sys_brk_receipt_dtls where rect_receipt_no=:rectNo",nativeQuery = true)
    List<Object[]> getAllReceiptDtls(@Param("rectNo") Long rectNo);

    @Query(value = "select sbr.receipt_no, sbr.receipt_date, sbrd.rect_amount, COUNT(*) OVER() as total_rows from sys_brk_receipt_dtls sbrd \n" +
            "join sys_brk_policies sbp on sbp.pol_id = sbrd.rect_pol_id \n" +
            "join sys_brk_receipts sbr on sbr.receipt_id = sbrd.rect_receipt_no \n" +
            "where sbp.pol_id = :polId\n" +
            "OFFSET :pageNo*:limit limit :limit ",nativeQuery = true)
    List<Object[]> findPolRcpts(@Param("polId") Long PolicyId,
                                        @Param("pageNo") int pageNo,
                                        @Param("limit") int limit);

    @Query(value = "select sbr.receipt_id,sbr.receipt_no, sbr.receipt_date, sbrd.rect_amount, COUNT(*) OVER() as total_rows from sys_brk_receipt_dtls sbrd \n" +
            "join sys_brk_policies sbp on sbp.pol_id = sbrd.rect_pol_id \n" +
            "join sys_brk_receipts sbr on sbr.receipt_id = sbrd.rect_receipt_no \n" +
            "where sbp.pol_id = :polId\n" ,nativeQuery = true)
    List<Object[]> findAllPolRcts(@Param("polId") Long polId);
}
