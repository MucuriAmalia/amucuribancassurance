package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.ProductsDef;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ProductsRepo extends  PagingAndSortingRepository<ProductsDef, Long>, QueryDslPredicateExecutor<ProductsDef> {

    List<ProductsDef> findProductsDefByProShtDesc(String shtDesc);

    @Query(value = "select sbp.pr_code, sbp.pr_desc, COUNT(*) OVER() as total_rows from sys_brk_products sbp\n" +
            "join sys_brk_product_grp sbpg on sbpg.bpg_code = sbp.pr_bpg_code\n" +
            "where sbpg.bpg_type not in ('L')\n" +
            "and LOWER(sbp.pr_desc) like LOWER(:search)\n" +
            "order by sbp.pr_desc OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> searchProducts(@Param("search") String search,
                                  @Param("pageNo") int pageNo,
                                  @Param("limit") int limit);

//    @Query(value = "select sbp.pr_code, sbp.pr_desc, COUNT(*) OVER() as total_rows from sys_brk_products sbp\n" +
//            "join sys_brk_product_grp sbpg on sbpg.bpg_code = sbp.pr_bpg_code\n" +
//            "where sbpg.bpg_type not in ('MD','L')\n" +
//            "and LOWER(sbp.pr_desc) like LOWER(:search)\n" +
//            "order by sbp.pr_desc OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
//    List<Object[]> searchProducts(@Param("search") String search,
//                                  @Param("pageNo") int pageNo,
//                                  @Param("limit") int limit);

    @Query("SELECT p FROM ProductsDef p WHERE LOWER(REPLACE(p.proDesc, ' ', '')) = LOWER(REPLACE(:name, ' ', ''))")
    ProductsDef findByNormalizedPName(@Param("name") String name);

    @Query("SELECT p FROM ProductsDef p WHERE LOWER(REPLACE(p.proDesc, ' ', '')) = LOWER(REPLACE(:name, ' ', '')) \n" +
            "AND LOWER(p.proPolPrefix) = LOWER(:searchCode)")
    ProductsDef findByNormalizedName(@Param("name") String name, @Param("searchCode") String searchCode);
}
