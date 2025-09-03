package com.brokersystems.brokerapp.bulktransactions.repositories;

import com.brokersystems.brokerapp.bulktransactions.models.ClientMigration;
import com.brokersystems.brokerapp.setup.model.ClientDef;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ClientMigrationRepository extends
        JpaRepository<ClientMigration, Long>,
        JpaSpecificationExecutor<ClientMigration>,
        QueryDslPredicateExecutor<ClientMigration> {

    // Custom method to find all by IDs
    List<ClientMigration> findAllByIdIn(List<Long> ids);

    // Custom method for DataTables
    Page<ClientMigration> findAll(Specification<ClientMigration> spec, Pageable pageable);

    @Query("SELECT c.id FROM ClientMigration c WHERE c.processed = false")
    List<Long> findAllUnprocessedClientIds();

}