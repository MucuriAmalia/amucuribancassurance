package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.AccountDef;
import com.brokersystems.brokerapp.setup.model.CommRecv;
import com.brokersystems.brokerapp.setup.model.SubClassDef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommRecvRepo extends JpaRepository<CommRecv, Long> {

    @Query(value = "select count(*) from sys_brk_uw_com_rev_items where uw_acc_id = :accDef and rev_sub_id = :prodGroup", nativeQuery = true)
    int countByAccountDefAndProdGroup(@Param("accDef") Long accDef, @Param("prodGroup") Long prodGroup);
}
