package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.CategoryMembers;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by waititu on 09/06/2017.
 */
public interface getMedicalMembersRepo extends PagingAndSortingRepository<CategoryMembers , Long> ,QueryDslPredicateExecutor<CategoryMembers>{
    @Query(" select cm from CategoryMembers cm where cm.memberShipNo like %:memberNo% ")
    public DataTablesResult<CategoryMembers> findClaimMembers(@Param("memberNo") String memberNo);

}
