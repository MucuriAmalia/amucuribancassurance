package com.brokersystems.brokerapp.certs.repository;

import com.brokersystems.brokerapp.certs.model.SubclassCertTypes;
import com.brokersystems.brokerapp.setup.model.SubClassDef;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by waititu on 27/10/2018.
 */
public interface SubclassCertTypesRepo extends PagingAndSortingRepository<SubclassCertTypes ,Long> ,QueryDslPredicateExecutor<SubclassCertTypes> {

    //@Query("select sub_id,sub_sht_desc,sub_desc from sys_brk_subclasses s where sub_id NOT IN (select sbccert_sub_id from sys_brk_subclass_cert_types )")
    @Query("select s from SubClassDef s where  NOT EXISTS(select p from s.subclassCertTypes p )")
    public List<SubClassDef> getCertUnassignedSubclasses();


    @Query(value = "select distinct sbccert_id,sbct.cert_desc,COUNT(*) OVER() as total_rows from sys_brk_subclass_cert_types sbsct \n" +
            "join  sys_brk_cert_types sbct on sbsct.sbccert_cert_id  =sbct.cert_id \n" +
            "join sys_brk_subclasses sbs on sbs.sub_id  =  sbsct.sbccert_sub_id \n" +
            "join sys_brk_risks sbr on sbr.risk_subclass_id  = sbs.sub_id \n" +
            "where sbr.risk_id = :riskId\n" +
            "and (lower(sbct.cert_desc) like :search)\n" +
            "ORDER BY sbct.cert_desc ASC \n" +
            "OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> searchCertificateTypes( @Param("riskId") Long riskId,
                                           @Param("search") String search,
                                           @Param("pageNo") int pageNo,
                                           @Param("limit") int limit);

    @Query(value = "select  sbccert_id  from sys_brk_subclass_cert_types sbsct \n" +
            "where sbsct.sbccert_sub_id  = ?",nativeQuery = true)
    List<BigInteger> getCertificateCode(@Param("subCode") Long subCode);
}
