package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.OrgBranch;
import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.setup.model.UserBranches;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserBranchesRepository extends PagingAndSortingRepository<UserBranches, Long>, QueryDslPredicateExecutor<UserBranches> {

    UserBranches findByUser(User user);
    List<UserBranches> findByBranch(OrgBranch branch);
    List<UserBranches> findByBranchAndUser_EscalationLevel(OrgBranch branch, Integer escalationLevel);


    @Query(value = "select bub_id , bub_date_assigned,sbb.ob_name,sbu.user_username,sbu2.user_username user_assigned,\n" +
            "COUNT(*) OVER() AS total_rows from sys_brk_user_branches  \n" +
            "join sys_brk_branches sbb on sbb.ob_id  = bub_ob_id\n" +
            "join sys_brk_users sbu on sbu.user_id =bub_user_id\n" +
            "join sys_brk_users sbu2 ON sbu2.user_id =bub_user_assigned\n" +
            "where (lower(sbb.ob_name) like :search)\n" +
            "and bub_user_id = :userId\n" +
            "order by sbb.ob_name OFFSET :pageNo*:limit LIMIT :limit ",nativeQuery = true)
    List<Object[]> findUsrBranches(@Param("search") String search,
                               @Param("userId") Long userId,
                               @Param("pageNo") int pageNo,
                               @Param("limit") int limit);

    @Query(value = "select count(1) from sys_brk_user_branches where bub_user_id=:userId and bub_ob_id=:branchId",nativeQuery = true)
    long countBranchesUser(@Param("userId") Long userId, @Param("branchId") Long branchId);

    @Query(value = "select ob_id,ob_name  from sys_brk_branches \n" +
            "where ob_id not in (select bub_ob_id from sys_brk_user_branches where bub_user_id=:userId)\n" +
            "and lower(ob_name) like :search",nativeQuery = true)
    List<Object[]> findunassignedUsrBranches(@Param("search") String search,
                                   @Param("userId") Long userId);

}
