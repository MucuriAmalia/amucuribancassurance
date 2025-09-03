package com.brokersystems.brokerapp.setup.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.brokersystems.brokerapp.setup.model.ClausesDef;
import com.brokersystems.brokerapp.setup.model.SubClassDef;
import com.brokersystems.brokerapp.setup.model.SubclassClauses;

public interface SubClausesRepo extends  PagingAndSortingRepository<SubclassClauses, Long>, QueryDslPredicateExecutor<SubclassClauses> {

	
	@Query("select s from ClausesDef s where (lower(s.clauShtDesc) like %:subName% or lower(s.clauHeading) like %:subName% or lower(s.clauShtDesc) like %:subName% ) and NOT EXISTS(select p from s.subClauses p where p.subclass.subId=:subCode)")
	public List<ClausesDef> getUnassignedClauses(@Param("subCode")Long subCode,@Param("subName")String subName);
	
}
