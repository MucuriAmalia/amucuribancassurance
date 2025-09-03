package com.brokersystems.brokerapp.bulktransactions.repositories;

import com.brokersystems.brokerapp.bulktransactions.models.TransactionProcessing;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransProcessingRepository extends PagingAndSortingRepository<TransactionProcessing, Long>, QueryDslPredicateExecutor<TransactionProcessing> {

    @Query(value = "select sbtp.trans_processing_id, sbtp.trans_processing_serial_no, sbtp.trans_processing_pol_no, " +
            "sbtp.trans_processing_pol_code, sbtp.trans_processing_wef, sbtp.trans_processing_wet, " +
            "sbtp.trans_processing_currency, sbtp.trans_processing_inception_date, sbtp.trans_processing_proposer_code, " +
            "sbtp.trans_processing_agent_code, sbtp.trans_processing_processed, sbtp.trans_processing_is_renewable, " +
            "sbtp.trans_processing_renew_date, sbtp.trans_processing_clnt_fname, sbtp.trans_processing_clnt_other_names, " +
            "sbtp.trans_processing_risk_id, sbtp.trans_processing_subclass_code, sbtp.trans_processing_covertype_code, " +
            "sbtp.trans_processing_risk_sum_assured, sbtp.trans_processing_authorised_by, sbtp.trans_processing_authorised_date," +
            "sbtp.trans_processing_coinsurance_flag, sbtp.trans_processing_coinsurance_percentage, sbtp.trans_processing_coinsurance_leader_flag," +
            "sbtp.trans_processing_section_code, sbtp.trans_processing_policy_insured_code, sbtp.trans_processing_risk_code, " +
            " COUNT(*) OVER() as total_rows \n" +
            "from sys_brk_trans_processing sbtp \n" +
            "where sbtp.trans_processing_processed = 'N'\n" +
            "and (sbtp.trans_processing_pol_no like :search or sbtp.trans_processing_cover_type like :search) \n" +
            "order by sbtp.trans_processing_id desc \n" +
            "OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findUnprocessedTrans(@Param("search") String search,
                                        @Param("pageNo") int pageNo,
                                        @Param("limit") int limit);

    @Query(value = "select sbtp.trans_processing_id, sbtp.trans_processing_serial_no, sbtp.trans_processing_pol_no, " +
            "sbtp.trans_processing_pol_code, sbtp.trans_processing_wef, sbtp.trans_processing_wet, " +
            "sbtp.trans_processing_currency, sbtp.trans_processing_inception_date, sbtp.trans_processing_proposer_code, " +
            "sbtp.trans_processing_agent_code, sbtp.trans_processing_processed, sbtp.trans_processing_is_renewable, " +
            "sbtp.trans_processing_renew_date, sbtp.trans_processing_clnt_fname, sbtp.trans_processing_clnt_other_names, " +
            "sbtp.trans_processing_risk_id, sbtp.trans_processing_subclass_code, sbtp.trans_processing_covertype_code, " +
            "sbtp.trans_processing_authorised_by, sbtp.trans_processing_authorised_date, sbtp.trans_processing_coinsurance_flag, " +
            "sbtp.trans_processing_coinsurance_percentage, sbtp.trans_processing_coinsurance_leader_flag, sbtp.trans_processing_section_code, " +
            "sbtp.trans_processing_policy_insured_code, sbtp.trans_processing_risk_code, " +
            "sbtp.trans_processing_risk_sum_assured, COUNT(*) OVER() as total_rows \n" +
            "from sys_brk_trans_processing sbtp \n" +
            "where sbtp.trans_processing_processed = 'Y'\n" +
            "and (sbtp.trans_processing_pol_no like :search or sbtp.trans_processing_cover_type like :search) \n" +
            "order by sbtp.trans_processing_id desc \n" +
            "OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findprocessedTrans(@Param("search") String search,
                                      @Param("pageNo") int pageNo,
                                      @Param("limit") int limit);

}
