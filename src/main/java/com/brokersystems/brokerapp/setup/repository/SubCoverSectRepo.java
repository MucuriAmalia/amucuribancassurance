package com.brokersystems.brokerapp.setup.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.brokersystems.brokerapp.setup.model.SubCoverTypeSections;
import com.brokersystems.brokerapp.setup.model.SubclassSections;

public interface SubCoverSectRepo extends  PagingAndSortingRepository<SubCoverTypeSections, Long>, QueryDslPredicateExecutor<SubCoverTypeSections> {

	@Query("select c from SubclassSections c where c.section.desc like %:sectName% and c.subclass.subId=:subId and NOT EXISTS(select s from c.subcovsections s where s.subcoverType.scId=:secId)")
	public List<SubclassSections> getUnassignedSections(@Param("secId") Long secId,@Param("subId") Long subId,@Param("sectName")String sectName);

	
}
