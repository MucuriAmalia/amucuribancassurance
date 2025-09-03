package com.brokersystems.brokerapp.developermodule.repository;

import com.brokersystems.brokerapp.developermodule.model.StretchyParameter;
import com.brokersystems.brokerapp.developermodule.model.StretchyReport;
import com.brokersystems.brokerapp.developermodule.model.StretchyReportParameter;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface StretchyReportParameterRepo extends PagingAndSortingRepository<StretchyReportParameter, Long>, QueryDslPredicateExecutor<StretchyReportParameter> {
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM stretchy_report_parameter srp " +
            "WHERE srp.stretchy_report_id = :reportId AND srp.stretchy_parameter_id = :stpId",
            nativeQuery = true)
    void deleteByReportIdAndStpId(@Param("reportId") Long reportId, @Param("stpId") Long stpId);

    @Query(value = "SELECT COUNT(*) FROM stretchy_report_parameter srp " +
            "WHERE srp.stretchy_report_id = :reportId " +
            "AND srp.stretchy_parameter_id = :oldStpId", nativeQuery = true)
    Long countByReportIdAndStpId(@Param("reportId") Long reportId, @Param("oldStpId") Long oldStpId);
    @Query(value = "SELECT * FROM stretchy_report_parameter srp " +
            "WHERE srp.stretchy_report_id = :reportId " +
            "AND srp.stretchy_parameter_id = :oldStpId",
            nativeQuery = true)
    Optional<StretchyReportParameter> findByReportIdAndStpId(@Param("reportId") Long reportId, @Param("oldStpId") Long oldStpId);
}
