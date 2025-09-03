package com.brokersystems.brokerapp.users.repository;

import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.users.model.MakerChecker;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface MakerCheckerRepo extends PagingAndSortingRepository<MakerChecker, Long>, QueryDslPredicateExecutor<MakerChecker> {

    User findByMakerId(MakerChecker makerChecker);
    @Query(value = "select distinct mck.mck_id, mck.task_name, mck.made_on_date, sbu.user_name, mck.check_status, mck.maker_task_type, mck.task_pol_id, mck.task_acct_id, " +
            "mck.initiator_id, iu.user_name as initiator_name, CONCAT(c.client_fname, ' ', c.client_onames) as client, " +
            "CASE " +
            "  WHEN p.pol_trans_type = 'CN' THEN 'CANCELLATION' " +
            "  WHEN p.pol_trans_type = 'EX' THEN 'EXTENSION' " +
            "  WHEN p.pol_trans_type = 'EN' THEN 'ENDORSEMENT' " +
            "  WHEN p.pol_trans_type = 'CF' THEN 'CLAIM FEE' " +
            "  WHEN p.pol_trans_type = 'RN' THEN 'RENEWAL' " +
            "  WHEN p.pol_trans_type = 'SP' THEN 'SHORT PERIOD' " +
            "  WHEN p.pol_trans_type = 'BU' THEN 'BULK UPLOADS' " +
            "  WHEN p.pol_trans_type = 'NB' THEN 'NEW BUSINESS' " +
            "  WHEN p.pol_trans_type = 'CO' THEN 'CONTRA' " +
            "  WHEN p.pol_trans_type = 'RF' THEN 'REFUND' " +
            "  ELSE p.pol_trans_type " +
            "END as transType, mck.resubmission_comment, COUNT(*) OVER() as total_rows " +
            "from sys_brk_maker_checker mck " +
            "join sys_brk_users sbu on sbu.user_id = mck.maker_id " +
            "LEFT JOIN sys_brk_policies p ON mck.task_pol_id = p.pol_id " +
            "LEFT JOIN sys_brk_clients c ON p.pol_client_id = c.client_id " +
            "LEFT JOIN LATERAL json_array_elements_text(CAST(mck.checkers AS json)) AS checker_id_text ON TRUE " +
            "LEFT JOIN sys_brk_users iu on iu.user_id = mck.initiator_id " +
            "WHERE (" +
            "  CAST(mck.mck_id AS TEXT) LIKE :search " +
            "  OR LOWER(COALESCE(mck.task_name, '')) LIKE :search " +
            "  OR LOWER(COALESCE(mck.check_status, '')) LIKE :search " +
            "  OR TO_CHAR(mck.made_on_date, 'YYYY-MM-DD HH24:MI:SS') LIKE :search " +
            "  OR LOWER(COALESCE(iu.user_name, '')) LIKE :search " +
            "  OR LOWER(COALESCE(CONCAT(c.client_fname, ' ', c.client_onames), '')) LIKE :search " +
            ") " +
            "AND COALESCE(mck.check_status, 'N') = 'N' " +
            "AND (mck.checkers IS NULL OR json_array_length(CAST(mck.checkers AS json)) = 0 OR :checkerId = checker_id_text) " +
            "AND mck.maker_task_type NOT IN ('RC', 'CL') " +
            "ORDER BY mck.made_on_date DESC " +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
//    @Query(value = "select distinct mck.mck_id, mck.task_name, mck.made_on_date, sbu.user_name, mck.check_status, mck.maker_task_type, mck.task_pol_id, mck.task_acct_id, " +
//            "mck.initiator_id, iu.user_name as initiator_name, COUNT(*) OVER() as total_rows " +
//            "from sys_brk_maker_checker mck " +
//            "join sys_brk_users sbu on sbu.user_id = mck.maker_id " +
//            "LEFT JOIN LATERAL json_array_elements_text(CAST(mck.checkers AS json)) AS checker_id_text ON TRUE " +
//            "LEFT JOIN sys_brk_users iu on iu.user_id = mck.initiator_id " +
//            "WHERE (" +
//            "  CAST(mck.mck_id AS TEXT) LIKE :search " +
//            "  OR LOWER(COALESCE(mck.task_name, '')) LIKE :search " +
//            "  OR LOWER(COALESCE(mck.check_status, '')) LIKE :search " +
//            "  OR TO_CHAR(mck.made_on_date, 'YYYY-MM-DD HH24:MI:SS') LIKE :search " +
//            "  OR LOWER(COALESCE(iu.user_name, '')) LIKE :search " +
//            ") " +
//            "AND COALESCE(mck.check_status, 'N') = 'N' " +
//            "AND (mck.checkers IS NULL OR json_array_length(CAST(mck.checkers AS json)) = 0 OR :checkerId = checker_id_text) " +
//            "AND mck.maker_task_type NOT IN ('RC', 'CL') " +
//            "ORDER BY mck.made_on_date DESC " +
//            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findAllMakerCheckerDetails(@Param("checkerId") String checkerId,
                                              @Param("search") String search,
                                              @Param("pageNo") int pageNo,
                                              @Param("limit") int limit);

    @Query(value = "select count(1) from sys_brk_maker_checker where maker_task_code = :taskCode and maker_task_type = :taskType" +
            " and coalesce(check_status,'N') = 'N'", nativeQuery = true)
    Long countPendingRequest(@Param("taskCode") Long taskCode, @Param("taskType") String taskType);

    @Modifying
    @Transactional
    @Query("UPDATE MakerChecker mc SET mc.rejectionReason.reasonId = :reasonId, mc.rejectedReason = :reason, mc.checkDate = current_timestamp, mc.status = :status, mc.checkerId = :checkerId WHERE mc.id = :taskId")
    void updateRejectedTask(@Param("taskId") Long taskId, @Param("reasonId") Long reasonId, @Param("reason") String reason, @Param("checkerId") User checkerId, @Param("status") String status);

    @Query(value = "SELECT mck_id, task_name, made_on_date, checker_id, check_status, maker_task_type, " +
            "check_on_date, rejected_reason, task_pol_id, task_acct_id, srr.reason_desc, " +
            "maker_task_code, " +
            "COUNT(*) OVER() AS total_rows " +
            "FROM sys_brk_maker_checker " +
            "JOIN sys_brk_users sbu ON sbu.user_id = sys_brk_maker_checker.maker_id " +
            "LEFT JOIN sys_brk_rejected_reasons srr ON srr.reason_id = sys_brk_maker_checker.reason_id " +
            "LEFT JOIN sys_brk_users checker ON checker.user_id = sys_brk_maker_checker.checker_id " +
            "WHERE (" +
            "  LOWER(CAST(mck_id AS TEXT)) LIKE :search " +
            "  OR LOWER(task_name) LIKE :search " +
            "  OR TO_CHAR(made_on_date, 'YYYY-MM-DD HH24:MI:SS') LIKE :search " +
            "  OR LOWER(CAST(checker_id AS TEXT)) LIKE :search " +
            "  OR LOWER(check_status) LIKE :search " +
            "  OR LOWER(maker_task_type) LIKE :search " +
            "  OR TO_CHAR(check_on_date, 'YYYY-MM-DD HH24:MI:SS') LIKE :search " +
            "  OR LOWER(rejected_reason) LIKE :search " +
            "  OR CAST(task_pol_id AS TEXT) LIKE :search " +
            "  OR CAST(task_acct_id AS TEXT) LIKE :search " +
            "  OR LOWER(srr.reason_desc) LIKE :search " +
            "  OR LOWER(COALESCE(checker.user_name, '')) LIKE :search" +
            ") " +
            "AND maker_id = :makerId " +
            "AND (check_status = 'N' OR check_status = 'R') " +
            "ORDER BY made_on_date DESC " +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findMakerTasks(@Param("makerId") Long makerId,
                                  @Param("search") String search,
                                  @Param("pageNo") int pageNo,
                                  @Param("limit") int limit);

    @Query(value = "SELECT DISTINCT maker_task_type," +
            "    CASE " +
            "        WHEN maker_task_type = 'RC'  THEN 'Rejected Receipts'" +
            "        WHEN maker_task_type = 'IM'  THEN 'Rejected Intermediary'" +
            "        WHEN maker_task_type = 'ANP' THEN 'Rejected General Policy'" +
            "        WHEN maker_task_type = 'ALP' THEN 'Rejected Life Policy'" +
            "    END AS task_type," +
            " COUNT(*) OVER() AS total_rows " +
            "FROM sys_brk_maker_checker " +
            "WHERE LOWER(CASE " +
            "        WHEN maker_task_type = 'RC'  THEN 'Rejected Receipts'" +
            "        WHEN maker_task_type = 'IM'  THEN 'Rejected Intermediary'" +
            "        WHEN maker_task_type = 'ANP' THEN 'Rejected General Policy'" +
            "        WHEN maker_task_type = 'ALP' THEN 'Rejected Life Policy'" +
            "    END" +
            ") LIKE LOWER(:search)" +
            "ORDER BY task_type DESC OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findAllTasks(@Param("search") String search,
                                @Param("pageNo") int pageNo,
                                @Param("limit") int limit);

    @Query(value = "SELECT DISTINCT sbmc.checker_id , sbu.user_absa_no , sbu.user_name , " +
            "sbu.user_username , sbu.user_email,COUNT(*) OVER() AS total_rows " +
            "FROM sys_brk_maker_checker sbmc " +
            "INNER JOIN sys_brk_users sbu on sbu.user_id = sbmc.checker_id " +
            "WHERE (LOWER(user_absa_no) LIKE LOWER(:search) " +
            "OR LOWER(user_name) LIKE LOWER(:search) " +
            "OR LOWER(user_username) LIKE LOWER(:search) " +
            "OR LOWER(user_email) LIKE LOWER(:search) )" +
            "And user_status = '1' " +
            "ORDER BY user_name DESC OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findAllCheckers(@Param("search") String search,
                                   @Param("pageNo") int pageNo,
                                   @Param("limit") int limit);

    @Query(value = "select distinct mck_id ,task_name ,made_on_date,sbu.user_name,check_status,maker_task_type,task_pol_id,task_acct_id,COUNT(*) OVER() as total_rows " +
            "from sys_brk_maker_checker " +
            "join sys_brk_users sbu on sbu.user_id  = sys_brk_maker_checker.maker_id " +
            "LEFT JOIN LATERAL json_array_elements_text(CAST(sys_brk_maker_checker.checkers AS json)) AS checker_id_text ON TRUE " +
            "where coalesce(check_status,'N') = 'N' " +
            "AND (checkers IS NULL OR json_array_length(CAST(checkers AS json)) = 0 OR :checkerId = checker_id_text) " +
            "order by made_on_date desc ", nativeQuery = true)
    List<Object[]> findAllPendingTasks(@Param("checkerId") String checkerId);

    @Query(value = "SELECT DISTINCT mck.mck_id, mck.task_name, mck.made_on_date, sbu.user_name, mck.check_status, " +
            "mck.maker_task_type, mck.task_pol_id, mck.task_acct_id, mck.initiator_id, iu.user_name AS initiator_name, " +
            "p.pol_no AS policy_number, COUNT(*) OVER() AS total_rows " +
            "FROM sys_brk_maker_checker mck " +
            "JOIN sys_brk_users sbu ON sbu.user_id = mck.maker_id " +
            "LEFT JOIN LATERAL json_array_elements_text(CAST(mck.checkers AS json)) AS checker_id_text ON TRUE " +
            "LEFT JOIN sys_brk_users iu ON iu.user_id = mck.initiator_id " +
            "LEFT JOIN sys_brk_policies p ON p.pol_id = mck.task_pol_id " +
            "WHERE (" +
            "  CAST(mck.mck_id AS TEXT) LIKE :search " +
            "  OR LOWER(COALESCE(mck.task_name, '')) LIKE :search " +
            "  OR LOWER(COALESCE(iu.user_name, '')) LIKE :search " +
            "  OR LOWER(COALESCE(p.pol_no, '')) LIKE :search " +
            ") " +
            "AND COALESCE(mck.check_status, 'N') = 'N' " +
            "AND mck.maker_task_type = 'RC' " +
            "AND (mck.checkers IS NULL OR json_array_length(CAST(mck.checkers AS json)) = 0 OR :checkerId = checker_id_text) " +
            "ORDER BY mck.made_on_date DESC " +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findAllReceiptTasks(@Param("checkerId") String checkerId,
                                       @Param("search") String search,
                                       @Param("pageNo") int pageNo,
                                       @Param("limit") int limit);

    @Query(value = "SELECT * FROM sys_brk_maker_checker WHERE task_pol_id = :polid AND maker_task_type = 'RC' LIMIT 1", nativeQuery = true)
    MakerChecker findByPolAndReceipt(@Param("polid") Long polid);

    @Query(value = "SELECT DISTINCT mck.mck_id, mck.task_name, mck.made_on_date, sbu.user_name, mck.check_status, " +
            "mck.maker_task_type, mck.task_pol_id AS claim_id, r.risk_id, mck.initiator_id, " +
            "iu.user_name AS initiator_name, c.clm_no AS claim_number, c.clm_next_rvw_dt AS next_review_date, " +
            "COUNT(*) OVER() AS total_rows " +
            "FROM sys_brk_maker_checker mck " +
            "JOIN sys_brk_users sbu ON sbu.user_id = mck.maker_id " +
            "LEFT JOIN LATERAL json_array_elements_text(CAST(mck.checkers AS json)) AS checker_id_text ON TRUE " +
            "LEFT JOIN sys_brk_users iu ON iu.user_id = mck.initiator_id " +
            "LEFT JOIN sys_brk_clm_bookings c ON c.clm_id = mck.maker_task_code " +
            "LEFT JOIN sys_brk_risks r ON r.risk_id = c.clm_risk_id " +
            "WHERE (" +
            "  CAST(mck.mck_id AS TEXT) LIKE :search " +
            "  OR LOWER(COALESCE(mck.task_name, '')) LIKE :search " +
            "  OR LOWER(COALESCE(iu.user_name, '')) LIKE :search " +
            "  OR LOWER(COALESCE(c.clm_no, '')) LIKE :search " +
            ") " +
            "AND COALESCE(mck.check_status, 'N') = 'N' " +
            "AND mck.maker_task_type = 'CL' " +
            "AND (mck.checkers IS NULL OR json_array_length(CAST(mck.checkers AS json)) = 0 OR :checkerId = checker_id_text) " +
            "ORDER BY mck.made_on_date DESC " +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findAllClaimTasks(@Param("checkerId") String checkerId,
                                     @Param("search") String search,
                                     @Param("pageNo") int pageNo,
                                     @Param("limit") int limit);

    // Updated JPQL query for findbytaskpolId
    @Query("SELECT mc FROM MakerChecker mc WHERE mc.policyId = :polId")
    List<MakerChecker> findbytaskpolId(@Param("polId") Long polId);

    @Query(value = "SELECT DISTINCT mck.mck_id, mck.task_name, mck.made_on_date, sbu.user_name, mck.check_status, " +
            "mck.maker_task_type, mck.task_pol_id, mck.task_acct_id, mck.initiator_id, iu.user_name AS initiator_name, " +
            "p.pol_no AS policy_number, COUNT(*) OVER() AS total_rows " +
            "FROM sys_brk_maker_checker mck " +
            "JOIN sys_brk_bulk_receipt sbbr ON sbbr.bulk_receipt_policy = mck.task_pol_id " +
            "JOIN sys_brk_users sbu ON sbu.user_id = mck.maker_id " +
            "LEFT JOIN LATERAL json_array_elements_text(CAST(mck.checkers AS json)) AS checker_id_text ON TRUE " +
            "LEFT JOIN sys_brk_users iu ON iu.user_id = mck.initiator_id " +
            "LEFT JOIN sys_brk_policies p ON p.pol_id = mck.task_pol_id " +
            "WHERE (" +
            "  CAST(mck.mck_id AS TEXT) LIKE :search " +
            "  OR LOWER(COALESCE(mck.task_name, '')) LIKE :search " +
            "  OR LOWER(COALESCE(iu.user_name, '')) LIKE :search " +
            "  OR LOWER(COALESCE(p.pol_no, '')) LIKE :search " +
            ") " +
            "AND COALESCE(mck.check_status, 'N') = 'N' " +
            //"AND p.pol_trans_type = 'BU' " +
            "AND mck.maker_task_type = 'RC' " +
            "AND (mck.checkers IS NULL OR json_array_length(CAST(mck.checkers AS json)) = 0 OR :checkerId = checker_id_text) " +
            "ORDER BY mck.made_on_date DESC " +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findAllBulkReceiptTasks(@Param("checkerId") String checkerId,
                                       @Param("search") String search,
                                       @Param("pageNo") int pageNo,
                                       @Param("limit") int limit);
}