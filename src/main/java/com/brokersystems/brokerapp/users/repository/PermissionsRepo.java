package com.brokersystems.brokerapp.users.repository;

import com.brokersystems.brokerapp.users.model.PermissionsDef;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by peter on 4/17/2017.
 */
public interface PermissionsRepo extends PagingAndSortingRepository<PermissionsDef, Long>, QueryDslPredicateExecutor<PermissionsDef> {


    List<PermissionsDef> findByModule_ModuleId(Long exception);

    @Query(value = "select x.* from (\n" +
            "select   perm_id,perm_acc_type,perm_desc,perm_name,perm_min_amount,perm_max_amount,1 count   from sys_brk_permissions sbp\n" +
            "            left outer join  sys_brk_role_permissions sbrp  on sbp.perm_id =sbrp.rp_perm \n" +
            "            where sbp.perm_module_id  =:moduleId and sbrp.rp_role=:roleId and sbp.perm_name like :search\n" +
            "            union ALL\n" +
            "            select   perm_id,perm_acc_type,perm_desc,perm_name,null perm_min_amount,null perm_max_amount,0 count  from sys_brk_permissions sbp\n" +
            "            where sbp.perm_module_id  =:moduleId and sbp.perm_name like :search\n" +
            "            and perm_id not in (select sbrp.rp_perm  from sys_brk_role_permissions sbrp where sbrp.rp_role=:roleId))x\n" +
            "            order by perm_name desc OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> findRolePermissions(@Param("moduleId") Long moduleId,
                                       @Param("roleId") Long roleId,
                                       @Param("search") String search,
                                       @Param("pageNo") int pageNo,
                                       @Param("limit") int limit);

    @Query(value = "select count(1) from (\n" +
            "select   perm_id,perm_acc_type,perm_desc,perm_name,perm_min_amount,perm_max_amount  from sys_brk_permissions sbp\n" +
            "            left outer join  sys_brk_role_permissions sbrp  on sbp.perm_id =sbrp.rp_perm \n" +
            "            where sbp.perm_module_id  =:moduleId and sbrp.rp_role=:roleId and sbp.perm_name like :search\n" +
            "            union ALL\n" +
            "            select   perm_id,perm_acc_type,perm_desc,perm_name,null perm_min_amount,null perm_max_amount  from sys_brk_permissions sbp\n" +
            "            where sbp.perm_module_id  =:moduleId and sbp.perm_name like :search\n" +
            "            and perm_id not in (select sbrp.rp_perm  from sys_brk_role_permissions sbrp where sbrp.rp_role=:roleId))x",nativeQuery = true)
    Long countRolePermissions(@Param("moduleId") Long moduleId,
                              @Param("roleId") Long roleId,
                              @Param("search") String search);

    @Query(value = "select sbp.perm_name, sbp.perm_desc, sbp.perm_acc_type, md.module_name, sbp.perm_enable_maker_checker, COUNT(*) OVER() AS total_rows  " +
            "from sys_brk_permissions sbp " +
            "left outer join sys_brk_role_permissions sbrp on sbp.perm_id = sbrp.rp_perm " +
            "left outer join sys_brk_sys_modules md on sbp.perm_module_id = md.module_id " +
            "where lower(sbp.perm_name) like :search or lower(md.module_name) like :search " +
            "order by sbp.perm_name desc OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findAllPermissions(@Param("search") String search,
                                      @Param("pageNo") int pageNo,
                                      @Param("limit") int limit);


}
