package com.brokersystems.brokerapp.users.repository;

import com.brokersystems.brokerapp.users.model.RolesDef;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by peter on 4/17/2017.
 */
public interface RolesRepo extends PagingAndSortingRepository<RolesDef, Long>, QueryDslPredicateExecutor<RolesDef> {

    @Query("select c from RolesDef c where  c.roleId NOT IN(select s.roles.roleId from c.userRoles s where s.user.id=:userId)")
    public Page<RolesDef> getUnassignedRoles(@Param("userId") Long userId, Pageable paramPageable);

    @Query("select c from RolesDef c where  c.roleId  IN(select s.roles.roleId from c.userRoles s where s.user.id=:userId)")
    public Page<RolesDef> getAssignedRoles(@Param("userId") Long userId, Pageable paramPageable);
}
