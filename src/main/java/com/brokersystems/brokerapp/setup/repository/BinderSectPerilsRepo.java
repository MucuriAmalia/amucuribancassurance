package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.BinderSectionPerils;
import com.brokersystems.brokerapp.setup.model.SubClassDef;
import com.brokersystems.brokerapp.setup.model.SubclassPerils;
import com.brokersystems.brokerapp.setup.model.SubclassSections;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by peter on 3/3/2017.
 */
public interface BinderSectPerilsRepo extends PagingAndSortingRepository<BinderSectionPerils, Long>, QueryDslPredicateExecutor<BinderSectionPerils> {

    @Query("select s from SubclassPerils s where s.subclass.subId=:subCode and (lower(s.peril.perilDesc) like %:perilName% or lower(s.peril.perilShtDesc) like %:perilName%) and NOT EXISTS(select p from s.binderSectionPerils p where p.binderDetail.detId=:detCode and p.sectionsDef.id=:sectCode)")
    List<SubclassPerils> getUnassignedPerils(@Param("detCode")Long detCode,@Param("subCode")Long subCode,@Param("sectCode")Long sectCode, @Param("perilName")String perilName);


    @Query(value = "select bsp_id,perils.p_desc,COUNT(*) OVER() as total_rows  from sys_brk_bind_sect_prls \n" +
            "join sys_brk_subclass_perils pr on pr.sper_id =bsp_sper_code \n" +
            "join sys_brk_perils perils on perils.p_code =pr.sper_per_code \n" +
            "where pr.sper_sub_code\n" +
            "in (select risk_subclass_id  from sys_brk_rsk_limits\n" +
            "join sys_brk_risks sbr on sbr.risk_id =sect_risk_id\n" +
            "where sect_risk_id =:riskId and coalesce(sect_expired,'N') not in ('Y')\n" +
            ")\n" +
            "and bsp_det_code in (select sbr.risk_binder_det_id  from sys_brk_rsk_limits\n" +
            "join sys_brk_risks sbr on sbr.risk_id =sect_risk_id\n" +
            "where sect_risk_id =:riskId and coalesce(sect_expired,'N') not in ('Y')\n" +
            ")\n" +
            "and bsp_sect_code in (select sect_sec_id  from sys_brk_rsk_limits\n" +
            "where sect_risk_id =:riskId and coalesce(sect_expired,'N') not in ('Y'))\n" +
            "and perils.p_desc like :search order by perils.p_desc OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> searchBinderPerils(@Param("riskId") Long riskId,
                                      @Param("search") String search,
                                      @Param("pageNo") int pageNo,
                                      @Param("limit") int limit);


}
