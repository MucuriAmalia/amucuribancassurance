package com.brokersystems.brokerapp.developermodule.repository;

import com.brokersystems.brokerapp.developermodule.model.StretchyReport;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StretchyReportRepository extends PagingAndSortingRepository<StretchyReport, Long>, QueryDslPredicateExecutor<StretchyReport> {
    @Query(value = "SELECT \n" +
            "    str_id, \n" +
            "    report_name, \n" +
            "    report_type, \n" +
            "    report_subtype, \n" +
            "    CASE \n" +
            "        WHEN report_category = 'M' THEN 'Medical' \n" +
            "        WHEN report_category = 'A' THEN 'Accounts'\n" +
            "        WHEN report_category = 'C' THEN 'Claims'\n" +
            "        WHEN report_category = 'U' THEN 'Underwriting' \n" +
            "    END AS report_category, \n" +
            "    report_sql, \n" +
            "    description, \n" +
            "    report_template, COUNT(*) OVER() as total_rows from stretchy_report \n" +
            "where (lower(report_name) like :search or lower(report_category) like :search)\n" +
            "order by report_name asc \n" +
            "OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findStretchyReportListing(@Param("search") String search,
                                                @Param("pageNo") int pageNo,
                                                @Param("limit") int limit);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END FROM StretchyReport r WHERE lower(r.strRptName) = lower(:strRptName)")
    boolean existsByReportName(@Param("strRptName") String strRptName);

    @Query(value = "SELECT stp_id, parameter_name, parameter_variable, parameter_label, parameter_display_type, \n" +
            "parameter_format_type, parameter_default, parameter_sql, rp_lov_name, rp_options \n" +
            "FROM stretchy_parameter AS p \n" +
            "INNER JOIN stretchy_report_parameter s ON p.stp_id = s.stretchy_parameter_id \n" +
            "WHERE s.stretchy_report_id = :strId",
            nativeQuery = true)
    List<Object[]> findStretchyReportParameters(@Param("strId") Long strId);

    @Query(value = "SELECT r.str_id, r.report_name, r.report_type, r.report_subtype, r.report_category, r.report_sql, " +
            "r.description, " +
            "p.stp_id, p.parameter_name, p.parameter_variable, p.parameter_label, p.parameter_display_type, " +
            "p.parameter_format_type, p.parameter_default, p.parameter_sql, p.rp_lov_name, p.rp_options, " +
            "r.report_template " +
            "FROM stretchy_report r " +
            "INNER JOIN stretchy_report_parameter rp ON rp.stretchy_report_id = r.str_id " +
            "INNER JOIN stretchy_parameter p ON p.stp_id = rp.stretchy_parameter_id " +
            "WHERE r.report_template = :strTemplateName",
            nativeQuery = true)
    List<Object[]> reportQuery(@Param("strTemplateName") String strTemplateName);


    @Query(value = "select str_id, description, report_category, report_name, report_sql, report_subtype, report_type, rpt_permissions, report_template\n" +
            "from stretchy_report\n" +
            "where str_id=:strId",nativeQuery = true)
    Optional<StretchyReport> findById(@Param("strId")Long strId);



}
