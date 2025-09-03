package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.BinderReqrdDocs;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by HP on 9/15/2017.
 */
public interface BinderReqrdDocsRepo extends PagingAndSortingRepository<BinderReqrdDocs, Long>, QueryDslPredicateExecutor<BinderReqrdDocs> {

    @Query(value = "select a.SRQ_ID,b.REQ_DESC, b.REQ_SHT_DESC from SYS_BRK_SUBCLASS_REQ_DOCS a join SYS_BRK_REQ_DOCS b on A.SRQ_REQ_CODE = b.REQ_ID \n" +
            "where SRQ_SUB_CODE =:subCode and SRQ_ID not in (select BRD_SRQD_CODE from SYS_BRK_BIND_REQ_DOCS where BRD_DET_CODE=:detCode)",nativeQuery = true)
    public List<Object[]> getUnassignedReqDocs(@Param("subCode") Long subCode,@Param("detCode") Long detCode);

}
