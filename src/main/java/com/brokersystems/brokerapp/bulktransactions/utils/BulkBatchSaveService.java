package com.brokersystems.brokerapp.bulktransactions.utils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.brokersystems.brokerapp.bulktransactions.models.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.springframework.transaction.annotation.Propagation;

@Service
public class BulkBatchSaveService {

    @PersistenceContext
    private EntityManager entityManager;

    private static final int BATCH_SIZE = 1000;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveBulkDSCBatch(List<BulkPolicyCreation> entities) {
        int i = 0;
        for (BulkPolicyCreation entity : entities) {
            entityManager.persist(entity);

            if (++i % BATCH_SIZE == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        entityManager.flush();
        entityManager.clear();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveBulkDSCRisksBatch(List<BulkPolicyRisk> entities) {
        int i = 0;
        for (BulkPolicyRisk entity : entities) {
            entityManager.persist(entity);

            if (++i % BATCH_SIZE == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        entityManager.flush();
        entityManager.clear();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveBulkRenewalBatch(List<BulkRenewal> entities) {
        int i = 0;
        for (BulkRenewal entity : entities) {
            entityManager.persist(entity);

            if (++i % BATCH_SIZE == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        entityManager.flush();
        entityManager.clear();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveBulkRenewalErrorsBatch(List<BulkRenewalError> entities) {
        int i = 0;
        for (BulkRenewalError entity : entities) {
            entityManager.persist(entity);

            if (++i % BATCH_SIZE == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        entityManager.flush();
        entityManager.clear();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveEmbedPackageInsuranceBatch(List<EmbedPackageInsurance> policies) {
        int i = 0;
        for (EmbedPackageInsurance policy : policies) {
            entityManager.persist(policy);

            if (++i % BATCH_SIZE == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        entityManager.flush();
        entityManager.clear();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveEmbedPackageInsuranceRisksBatch(List<EmbedPackageInsuranceRisks> risks) {
        int i = 0;
        for (EmbedPackageInsuranceRisks risk : risks) {
            entityManager.persist(risk);

            if (++i % BATCH_SIZE == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        entityManager.flush();
        entityManager.clear();
    }


}
