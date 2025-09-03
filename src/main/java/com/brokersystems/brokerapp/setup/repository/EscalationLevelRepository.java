package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.EscalationLevel;
import com.brokersystems.brokerapp.setup.model.OrgBranch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EscalationLevelRepository extends JpaRepository<EscalationLevel, Long> {

    // Find by hierarchy level
    EscalationLevel findByHierarchyLevel(Integer level);

//    EscalationLevel findFirstByHierarchyLevelGreaterThanAndBranchOrderByHierarchyLevelAsc(Integer currentHierarchyLevel, OrgBranch branch);
    EscalationLevel findFirstByHierarchyLevelGreaterThanOrderByHierarchyLevelAsc(Integer hierarchyLevel);

//    EscalationLevel findFirstByHierarchyLevelGreaterThanAndBranchOrderByHierarchyLevelAsc(Integer hierarchyLevel, OrgBranch branch);
}

