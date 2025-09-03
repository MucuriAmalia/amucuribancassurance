package com.brokersystems.brokerapp.life.repository;

import com.brokersystems.brokerapp.life.model.LifeReceiptAllocations;
import com.brokersystems.brokerapp.life.model.ReceiptAllocationCommissions;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by waititu on 18/03/2019.
 */
public interface ReceiptAllocationCommissionsRepo extends PagingAndSortingRepository<ReceiptAllocationCommissions, Long>, QueryDslPredicateExecutor<ReceiptAllocationCommissions> {

    @Query(value = "select acomm_id,acomm_alloc_id,sect_prem,acomm_cover_comm,acomm_alloc,acomm__cover_prem,acomm_comm_rate," +
            "acomm_div_fact,acomm_prem_id " +
            "from sys_brk_rct_alloc_comms s,sys_brk_rsk_limits r where s.acomm_alloc_id=:allocId and r.sect_id =s.acomm_sect_id ",nativeQuery = true)
    List<Object[]> findAllocCommisions(@Param("allocId") Long allocId);

    @Query(value = "select acomm_id,alloc_install_no,acomm__cover_prem,acomm_cover_comm,\n" +
            "to_char(alloc_paid_to_date, 'DD/MM/YYYY') as alloc_paid_to_date, sbs.sc_desc," +
            "acomm_cover_subagent_comm, acomm_cover_marketer_comm, COUNT(*) OVER() as total_rows from sys_brk_rct_alloc_comms com\n" +
            "join sys_brk_life_rct_allocs alloc on com.acomm_alloc_id = alloc.alloc_id  \n" +
            "left join sys_brk_prem_rts sbpr on com.acomm_prem_id = sbpr.prem_id " +
            "left join sys_brk_sections sbs on sbpr.prem_sec_code = sbs.sc_id " +
            "where com.acomm_lrct_id = :rctId\n" +
            "order by alloc_install_no asc OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> searchReceiptCommissionsAlloc(@Param("rctId") Long rctId,
                                        @Param("pageNo") int pageNo,
                                        @Param("limit") int limit);
}
