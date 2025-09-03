package com.brokersystems.brokerapp.users.repository;

import com.brokersystems.brokerapp.users.model.RolePermissions;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by peter on 4/17/2017.
 */
public interface RolePermissionsRepo extends PagingAndSortingRepository<RolePermissions, Long>, QueryDslPredicateExecutor<RolePermissions> {

    @Query(value = " select\n" +
            "        permission0_.perm_name as perm_name4_95_0_,\n" +
            "         perm.perm_max_amount as perm_max_amount2_126_,\n" +
            "        perm.perm_min_amount as perm_min_amount3_126_\n" +
            "    from\n" +
            "        sys_brk_permissions permission0_ \n" +
            "    left outer join\n" +
            "        sys_brk_sys_modules modulesdef1_ \n" +
            "            on permission0_.perm_module_id=modulesdef1_.module_id \n" +
            "    left outer join\n" +
            "          sys_brk_role_permissions perm\n" +
            "             on RP_PERM = permission0_.PERM_ID\n" +
            "     left outer join\n" +
            "         sys_brk_user_roles user_role\n" +
            "             on user_role.ROLE_NAME = perm.RP_ROLE\n" +
            "      left outer join\n" +
            "        sys_brk_users user5_ \n" +
            "            on user_role.USERID=user5_.user_id \n" +
            "    where\n" +
            "        user5_.user_id= :userId",nativeQuery = true)
    public List<Object[]> getUserPerms(@Param("userId")Long userId);

}
