package com.brokersystems.brokerapp.uw.repository;

import com.brokersystems.brokerapp.uw.model.MotorVehicleDetails;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MotorVehicleDetailsRepo  extends PagingAndSortingRepository<MotorVehicleDetails, Long>, QueryDslPredicateExecutor<MotorVehicleDetails> {


    @Query(value = "select vd_color,vd_body_type,vd_make,vd_model,vd_carry_capacity,vd_chassis_no,vd_cc," +
            "vd_engine_no,vd_yom,vd_log_book,vd_risk_id,vd_id,COUNT(*) OVER() as total_rows \n" +
            "from sys_brk_vehicle_details\n" +
            "where vd_risk_id =:riskId\t\n" +
            "order by vd_make \n" +
            "OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> getRiskVehicleDetails(@Param("riskId") Long riskId,
                                         @Param("pageNo") int pageNo,
                                         @Param("limit") int limit);

    @Query(value = "select vd_color,vd_body_type,vd_make,vd_model,vd_carry_capacity,vd_chassis_no,vd_cc,vd_engine_no,vd_log_book,vd_yom \n" +
            "from sys_brk_vehicle_details\n" +
            "where vd_risk_id =:riskId\t\n" , nativeQuery = true)
    List<Object[]> getVehicleDetails(@Param("riskId") Long riskId);

}
