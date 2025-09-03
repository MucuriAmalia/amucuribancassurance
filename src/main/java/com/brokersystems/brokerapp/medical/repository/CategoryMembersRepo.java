package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.CategoryMembers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by peter on 4/27/2017.
 */
public interface CategoryMembersRepo extends PagingAndSortingRepository<CategoryMembers, Long>, QueryDslPredicateExecutor<CategoryMembers> {


    @Query(value = "select s from CategoryMembers s where lower(s.memberShipNo) like %:cardNo% " +
            "and (s.client.gender) like  %:gender% " +
            " and CAST( s.category.policy.policyId as string ) like %:polId%  " +
            " and CAST( s.category.policy.client.tenId as string ) like %:clientId%  " +
            " and (lower(s.client.fname) like  %:memberName% or lower(s.client.otherNames) like  %:memberName%) " +
            " and s.category.policy.authStatus = 'A' and s.category.policy.currentStatus='A' ")
    public Page<CategoryMembers> searchCategoryMembers( @Param("polId")Long polId,
                                                        @Param("clientId")Long clientId,
                                                        @Param("memberName")String memberName,
                                                        @Param("gender")String gender,
                                                        @Param("cardNo")String cardNo,
                                                        Pageable pageable);

    @Query(value = "select s from CategoryMembers s where lower(s.memberShipNo) like %:cardNo% " +
            "and (s.client.gender) like  %:gender% " +
            "and (s.client.gender) like  %:search% " +
            " and CAST( s.category.policy.client.tenId as string ) like %:clientId%  " +
            " and (lower(s.client.fname) like  %:memberName% or lower(s.client.otherNames) like  %:memberName%)" +
            " and s.category.policy.authStatus = 'A' and s.category.policy.currentStatus='A' ")
    public Page<CategoryMembers> searchCategoryMembers( @Param("clientId")Long clientId,
                                                        @Param("memberName")String memberName,
                                                        @Param("gender")String gender,
                                                        @Param("cardNo")String cardNo,
                                                        @Param("search")String clientSearch,
                                                        Pageable pageable);

    @Query(value = "select s from CategoryMembers s where lower(s.memberShipNo) like %:cardNo% " +
            "and (s.client.gender) like  %:gender% " +
            " and CAST( s.category.policy.policyId as string ) like %:polId%  " +
            " and (lower(s.client.fname) like  %:memberName% or lower(s.client.otherNames) like  %:memberName%) " +
            " and s.category.policy.authStatus = 'A' and s.category.policy.currentStatus='A' ")
    public Page<CategoryMembers> searchCategoryMembers(@Param("polId")Long polId,
                                                       @Param("memberName")String memberName,
                                                       @Param("gender")String gender,
                                                       @Param("cardNo")String cardNo,
                                                       Pageable pageable);

    @Query(value = "select s from CategoryMembers s where lower(s.memberShipNo) like %:cardNo% " +
            "and (s.client.gender) like  %:gender% " +
            " and (lower(s.client.fname) like  %:memberName% or lower(s.client.otherNames) like  %:memberName%) " +
            " and s.category.policy.authStatus = 'A' and s.category.policy.currentStatus='A' ")
    public Page<CategoryMembers> searchCategoryMembers(@Param("memberName")String memberName,
                                                       @Param("gender")String gender,
                                                       @Param("cardNo")String cardNo,
                                                       Pageable pageable);


}
