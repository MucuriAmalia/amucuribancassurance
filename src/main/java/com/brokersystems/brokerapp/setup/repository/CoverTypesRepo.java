package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.SubClassDef;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.brokersystems.brokerapp.setup.model.CoverTypesDef;

import java.util.List;


public interface CoverTypesRepo  extends  PagingAndSortingRepository<CoverTypesDef, Long>, QueryDslPredicateExecutor<CoverTypesDef> {
	
	@Query("select c from CoverTypesDef c where c.covName like %:coverName% and c.classesDef.clId =:classId and NOT EXISTS(select s from c.coverTypes s where s.subclass.subId=:subId)")
	public Page<CoverTypesDef> getUnassignedCoverTypes(@Param("subId") Long subId,@Param("classId") Long classId, @Param("coverName")String coverName,Pageable paramPageable);

	List<CoverTypesDef> findCoverTypesDefByCovShtDesc(String coverType);

//	@Query("SELECT p FROM CoverTypesDef p WHERE LOWER(REPLACE(p.covName, ' ', '')) = LOWER(REPLACE(:name, ' ', ''))")
//	CoverTypesDef findByCoverType(@Param("name") String name);

	@Query(value = "SELECT * \n" +
			"FROM sys_brk_covertypes sbc \n" +
			"INNER JOIN sys_brk_classes sbc2 on sbc2.cl_id = sbc.cov_class_id\n" +
			"INNER JOIN sys_brk_sub_covertypes sbsc on sbsc.sc_cov_code = sbc.cov_id \n" +
			"INNER JOIN sys_brk_binder_det sbbd on sbbd.bdet_sub_covt_code = sbsc.sc_id\n" +
			"INNER JOIN sys_brk_binders sbb  on sbb.bin_id = sbbd.bdet_bin_code  \n" +
			"WHERE UPPER(sbb.bin_type) = 'B'\n" +
			"AND sbb.bin_id = :binId\n" +
			"AND LOWER(REPLACE(sbc.cov_desc, ' ', '')) = LOWER(REPLACE(:covName, ' ', ''))", nativeQuery = true)
	CoverTypesDef findCoverTypesByBindId(@Param("binId") Long bindId, @Param("covName") String covName);

}
