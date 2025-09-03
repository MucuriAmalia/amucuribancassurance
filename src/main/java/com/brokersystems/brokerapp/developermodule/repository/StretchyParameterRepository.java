package com.brokersystems.brokerapp.developermodule.repository;

import com.brokersystems.brokerapp.developermodule.dto.StretchyParameterDTO;
import com.brokersystems.brokerapp.developermodule.model.StretchyParameter;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StretchyParameterRepository extends PagingAndSortingRepository<StretchyParameter, Long>, QueryDslPredicateExecutor<StretchyParameter> {


    @Query(value = "SELECT \n" +
            "    stp_id, \n" +
            "    parameter_name, \n" +
            "    parameter_variable, \n" +
            "    parameter_label, \n" +
            "    parameter_display_type,\n" +
            "    CASE \n" +
            "        WHEN parameter_format_type = 'D' THEN 'Date'\n" +
            "        WHEN parameter_format_type = 'N' THEN 'Number'\n" +
            "        WHEN parameter_format_type = 'T' THEN 'Text'\n" +
            "        WHEN parameter_format_type = 'L' THEN 'List of Values'\n" +
            "        WHEN parameter_format_type = 'O' THEN 'Options'\n" +
            "    END AS parameter_format_type, \n" +
            "    parameter_default, \n" +
            "    parameter_sql, \n" +
            "    rp_lov_name, \n" +
            "    rp_options, COUNT(*) OVER() as total_rows from stretchy_parameter \n" +
            "where (lower(parameter_name) like :search)\n" +
            "order by parameter_name asc \n" +
            "OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findStretchyParameterListing(@Param("search") String search,
                                                @Param("pageNo") int pageNo,
                                                @Param("limit") int limit);


    @Query(value = "SELECT *" +
            "FROM stretchy_parameter p " +
            "INNER JOIN stretchy_report_parameter s ON p.stp_id = s.stretchy_parameter_id " +
            "WHERE s.stretchy_report_id = :reportId",
            nativeQuery = true)
    List<StretchyParameter> findStretchyReportParameters(@Param("reportId") Long reportId);


}

