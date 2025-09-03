package com.brokersystems.brokerapp.users.dto.netreveal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegulatoryAssessmentWorkProducts {
    private CommonDetails commonDetails;
    private IndividualDetails individualDetails;
    private EntityDetails entityDetails;
}
