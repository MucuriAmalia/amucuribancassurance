package com.brokersystems.brokerapp.setup.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.brokersystems.brokerapp.setup.model.BinderClauses;
import com.brokersystems.brokerapp.setup.model.SubclassClauses;

public interface BinderClauseRepo extends PagingAndSortingRepository<BinderClauses, Long>, QueryDslPredicateExecutor<BinderClauses> {
	
	@Query("select s from SubclassClauses s where s.subclass.subId=:subCode and (lower(s.clause.clauShtDesc) like %:subName% or lower(s.clause.clauHeading) like %:subName%) and NOT EXISTS(select p from s.binderClauses p where p.binderDet.detId=:detId)")
	public List<SubclassClauses> getUnassignedClauses(@Param("detId")Long detId,@Param("subCode")Long subCode,@Param("subName")String subName);




}
