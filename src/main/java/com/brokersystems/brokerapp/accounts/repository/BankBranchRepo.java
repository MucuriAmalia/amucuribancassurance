package com.brokersystems.brokerapp.accounts.repository;

import com.brokersystems.brokerapp.accounts.model.BankBranches;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by peter on 4/10/2017.
 */
public interface BankBranchRepo extends PagingAndSortingRepository<BankBranches, Long>, QueryDslPredicateExecutor<BankBranches> {

    @Query(value = "select bb_id,concat(sbb.bn_name,'-',bb_name) bb_name,COUNT(*) OVER()  as total_rows from sys_brk_bank_branches\n" +
            "join sys_brk_banks sbb on sbb.bn_id =bb_bn_id \n" +
            "where (lower(sbb.bn_name) like :search or lower(bb_name) like :search)\n" +
            "order by bb_name desc OFFSET :pageNo*:limit LIMIT :limit ",nativeQuery = true)
    List<Object[]> findBnkBranches(@Param("search") String search,
                                @Param("pageNo") int pageNo,
                                @Param("limit") int limit);

    @Query(value = "select bb_id,concat(sbb.bn_name,'-',bb_name) bb_name,COUNT(*) OVER() as total_rows  from sys_brk_bank_branches\n" +
            "join sys_brk_banks sbb on sbb.bn_id =bb_bn_id \n" +
            "where sbb.bn_id=:bankId and  (lower(sbb.bn_name) like :search or lower(bb_name) like :search)\n" +
            "order by bb_name desc OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> findBnkBranchesByBank(@Param("search") String search,
                                   @Param("bankId") Long bankId,
                                   @Param("pageNo") int pageNo,
                                   @Param("limit") int limit);

}
