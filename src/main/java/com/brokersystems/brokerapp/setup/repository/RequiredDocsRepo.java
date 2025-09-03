package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.RequiredDocs;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by peter on 3/5/2017.
 */
public interface RequiredDocsRepo extends PagingAndSortingRepository<RequiredDocs, Long>, QueryDslPredicateExecutor<RequiredDocs> {

    @Query("select s from RequiredDocs s where (lower(s.reqDesc) like %:reqName% or lower(s.reqShtDesc) like %:reqName%) and s.appliesClient='Y' and NOT EXISTS(select p from s.clientDocs p where p.clientDef.tenId=:clientCode)")
    public List<RequiredDocs> getUnassignedClientDocs(@Param("clientCode")Long clientCode, @Param("reqName")String reqName);

    @Query("select s from RequiredDocs s where (lower(s.reqDesc) like %:reqName% or lower(s.reqShtDesc) like %:reqName%) and s.appliesAccount='Y' and NOT EXISTS(select p from s.accountsDocs p where p.accountDef.acctId=:acctCode)")
    public List<RequiredDocs> getUnassignedAccountDocs(@Param("acctCode")Long acctCode, @Param("reqName")String reqName);


    @Query("select s from RequiredDocs s where (lower(s.reqDesc) like %:reqName% or lower(s.reqShtDesc) like %:reqName%) and s.appliesSubAgent='Y' and NOT EXISTS(select p from s.accountsDocs p where p.accountDef.acctId=:acctCode)")
    public List<RequiredDocs> getUnassignedSubAgentAccountDocs(@Param("acctCode")Long acctCode, @Param("reqName")String reqName);

    @Query("select s from RequiredDocs s where (lower(s.reqDesc) like %:reqName% or lower(s.reqShtDesc) like %:reqName%) and s.appliesCorpClient='Y' and NOT EXISTS(select p from s.clientDocs p where p.clientDef.tenId=:clientCode)")
    public List<RequiredDocs> getUnassignedCorporateClientDocs(@Param("clientCode")Long clientCode, @Param("reqName")String reqName);

    @Query("select s from RequiredDocs s where (lower(s.reqDesc) like %:reqName% or lower(s.reqShtDesc) like %:reqName%) and s.appliesClient='Y' and NOT EXISTS(select p from s.clientDocs p where p.prospectDef.tenId=:clientCode)")
    public List<RequiredDocs> getUnassignedProspectDocs(@Param("clientCode")Long clientCode, @Param("reqName")String reqName);

    @Query("select s from RequiredDocs s where (lower(s.reqDesc) like %:reqName% or lower(s.reqShtDesc) like %:reqName%) and s.appliesCorpClient='Y' and NOT EXISTS(select p from s.clientDocs p where p.prospectDef.tenId=:clientCode)")
    public List<RequiredDocs> getUnassignedCorporateProspectDocs(@Param("clientCode")Long clientCode, @Param("reqName")String reqName);
}
