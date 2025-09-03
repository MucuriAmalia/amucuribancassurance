package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.BinderMedicalCards;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by waititu on 16/04/2018.
 */
public interface BinderMedicalCardsRepo extends PagingAndSortingRepository<BinderMedicalCards,Long>, QueryDslPredicateExecutor<BinderMedicalCards> {

    @Query(value = "      select crd_id,card_desc\n" +
            "      from sys_brk_card_types, sys_brk_binder_med_cards\n" +
            "      where card_id = crd_card_code\n" +
            "        and crd_card_code = :binCode\n" +
            "        and ( :wefDate   BETWEEN crd_wef_date AND crd_wet_date Or \n" +
            "          ( crd_wef_date >= :wefDate  \n" +
            "        AND crd_wet_date is null))",nativeQuery = true)
    public List<Object[]> getBinderCardTypes(@Param("binCode") Long binCode,  @Param("wefDate")Date wefDate);
}
