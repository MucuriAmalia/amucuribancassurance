package com.brokersystems.brokerapp.developermodule.dto;

import com.brokersystems.brokerapp.developermodule.model.StretchyParameter;
import com.brokersystems.brokerapp.developermodule.model.StretchyReport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StretchyReportParameterDTO {
        private Long strpId;
        private Long reportId;
        private Long oldStpId;
        private Long newStpId;

}
