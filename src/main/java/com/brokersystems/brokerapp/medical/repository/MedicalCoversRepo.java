package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.MedicalCovers;
import com.brokersystems.brokerapp.setup.model.CoverTypesDef;
import com.brokersystems.brokerapp.setup.model.SectionsDef;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by peter on 4/24/2017.
 */
public interface MedicalCoversRepo extends PagingAndSortingRepository<MedicalCovers, Long>, QueryDslPredicateExecutor<MedicalCovers> {

    @Query(value = "select a.med_id,b.sc_desc,b.sc_sht_desc from sys_brk_med_covers a\n" +
            "inner join sys_brk_sections b on a.med_sec_code=b.sc_id where med_id\n" +
            "not in (select cb_cov_id from sys_brk_cat_benefits where cb_cat_id=:catId) and a.med_bd_code=:bindCode",nativeQuery = true)
    public List<Object[]> getUnassignedCovers(@Param("catId") Long catId, @Param("bindCode") Long bindCode);


    @Query("select DISTINCT s from SectionsDef s where (lower(s.desc) like %:secName% or lower(s.shtDesc) like %:secName%) and EXISTS(select p from s.medicalCovers p)")
    public Page<SectionsDef> getMedicalSections(@Param("secName")String secName, Pageable pageable);


}
