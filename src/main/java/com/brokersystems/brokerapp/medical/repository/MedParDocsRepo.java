package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.MedParReqDocs;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by HP on 8/20/2017.
 */
public interface MedParDocsRepo extends PagingAndSortingRepository<MedParReqDocs, Long>, QueryDslPredicateExecutor<MedParReqDocs> {
}
