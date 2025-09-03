package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.MedicalCards;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by peter on 5/28/2017.
 */
public interface MedicalCardsRepo extends PagingAndSortingRepository<MedicalCards, Long>, QueryDslPredicateExecutor<MedicalCards> {

    @Query(value = "select s from MedicalCards s where s.member.category.policy.polNo like %:polNo%" +
            " and lower(coalesce(s.cardNo,'')) like %:cardNumber% " +
            "and (lower(s.member.client.fname) like  %:memberName% or lower(s.member.client.otherNames) like  %:memberName%)" +
            " and lower(s.member.memberShipNo) like %:memberNumber%  " +
            " and (lower(s.member.category.policy.client.fname) like  %:clientName% or lower(s.member.category.policy.client.otherNames) like  %:clientName%) ")
    public Page<MedicalCards> searchMedicalCards(@Param("memberNumber")String memberNumber,
                                                 @Param("memberName")String memberName,
                                                 @Param("clientName")String clientName,
                                                 @Param("polNo")String polNo,
                                                 @Param("cardNumber")String cardNumber,
                                                 Pageable pageable);

    @Query(value = "select s from MedicalCards s ,CategoryMembers m where  " +
            "  s.member.client.tenId = m.client.tenId " +
            "and m.category.policy.policyId = :policyId " +
            "and (s.status='Dispatched'  or (s.status='Draft' and s.member.category.policy.policyId = :policyId )  ) ")
           public Page<MedicalCards> searchPolicyMedicalCards(
                                                 @Param("policyId")Long policyId,Pageable pageable);

    @Query(value = "select count(s) from MedicalCards s where  " +
            "  s.member.category.policy.policyId =:policyId and s.status='Draft'")
           public Long countPolicyMedicalCards(@Param("policyId")Long policyId);

}
