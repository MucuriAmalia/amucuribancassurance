package com.brokersystems.brokerapp.setup.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.brokersystems.brokerapp.setup.model.ProductSubclasses;
import com.brokersystems.brokerapp.setup.model.SubClassDef;


public interface ProdSubclassRepo extends  PagingAndSortingRepository<ProductSubclasses, Long>, QueryDslPredicateExecutor<ProductSubclasses> {

	/**
	 * Get unassigned subclasses for a product with revenue mapping validation status
	 */
	@Query("SELECT s.subId, s.subShtDesc, s.subDesc, s.active, " +
			"CASE " +
			"    WHEN NOT EXISTS (" +
			"        SELECT 1 FROM RevenueItemsDef r " +
			"        WHERE r.prodGroup.subId = s.subId " +
			"        AND r.item IS NOT NULL" +
			"    ) THEN false " +
			"    WHEN EXISTS (" +
			"        SELECT 1 FROM RevenueItemsDef r " +
			"        WHERE r.prodGroup.subId = s.subId " +
			"        AND r.item IS NOT NULL " +
			"        AND (r.drAccount IS NOT NULL OR r.crAccount IS NOT NULL)" +
			"    ) THEN true " +
			"    ELSE true " +
			"END as hasRevenueMapping " +
			"FROM SubClassDef s " +
			"WHERE (LOWER(s.subDesc) LIKE LOWER(CONCAT('%', :subName, '%')) " +
			"OR LOWER(s.subShtDesc) LIKE LOWER(CONCAT('%', :subName, '%'))) " +
			"AND NOT EXISTS (" +
			"    SELECT p FROM s.prodSubclasses p WHERE p.product.proCode = :prodCode" +
			")")
	List<Object[]> getUnassignedSubclassesWithMappingStatus(@Param("prodCode") Long prodCode, @Param("subName") String subName);


	@Query("SELECT s FROM SubClassDef s " +
			"WHERE (LOWER(s.subDesc) LIKE LOWER(CONCAT('%', :subName, '%')) " +
			"OR LOWER(s.subShtDesc) LIKE LOWER(CONCAT('%', :subName, '%'))) " +
			"AND NOT EXISTS (" +
			"    SELECT p FROM s.prodSubclasses p WHERE p.product.proCode = :prodCode" +
			")")
	List<SubClassDef> getUnassignedSubclasses(@Param("prodCode") Long prodCode, @Param("subName") String subName);

	/**
	 * Validate specific subclasses for revenue mappings
	 * Used for server-side validation before saving
	 */
	@Query("SELECT s.subId, s.subShtDesc, s.subDesc, " +
			"CASE WHEN EXISTS (" +
			"    SELECT 1 FROM RevenueItemsDef r " +
			"    WHERE r.prodGroup.subId = s.subId " +
			"    AND r.item IS NOT NULL" +  // Removed GL mapping check
			") THEN true ELSE false END as hasRevenueMapping, " +
			"(SELECT COUNT(r) FROM RevenueItemsDef r WHERE r.prodGroup.subId = s.subId) as revenueItemCount " +
			"FROM SubClassDef s " +
			"WHERE s.subId IN :subclassIds")
	List<Object[]> validateSubclassRevenueMapping(@Param("subclassIds") List<Long> subclassIds);

	/**
	 * Check if a single subclass has revenue mappings
	 * Quick validation query
	 */
	@Query("SELECT CASE WHEN EXISTS (" +
			"    SELECT 1 FROM RevenueItemsDef r " +
			"    WHERE r.prodGroup.subId = :subclassId " +
			"    AND r.item IS NOT NULL " +
			"    AND (r.drAccount IS NOT NULL OR r.crAccount IS NOT NULL)" +
			") THEN true ELSE false END " +
			"FROM SubClassDef s WHERE s.subId = :subclassId")
	Boolean hasRevenueMapping(@Param("subclassId") Long subclassId);

	/**
	 * Get detailed revenue mapping information for a subclass
	 * Returns revenue items with their GL account mappings
	 */
	@Query("SELECT r.item, " +
			"dr.code as drAccountCode, dr.name as drAccountName, " +
			"cr.code as crAccountCode, cr.name as crAccountName, " +
			"CASE WHEN (dr.code IS NOT NULL OR cr.code IS NOT NULL) THEN true ELSE false END as hasValidMapping " +
			"FROM RevenueItemsDef r " +
			"LEFT JOIN r.drAccount dr " +
			"LEFT JOIN r.crAccount cr " +
			"WHERE r.prodGroup.subId = :subclassId " +
			"AND r.item IS NOT NULL")
	List<Object[]> getSubclassRevenueDetails(@Param("subclassId") Long subclassId);
}
