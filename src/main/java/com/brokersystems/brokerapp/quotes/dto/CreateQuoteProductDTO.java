package com.brokersystems.brokerapp.quotes.dto;

import com.brokersystems.brokerapp.uw.model.RiskSectionBean;
import com.brokersystems.brokerapp.uw.model.RiskTransBean;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateQuoteProductDTO {

    private Long quoteProductId;
    private Long product;
    private Long prodId;
    private Long bindCode;
    private Long agentId;
    private Long quoteId;
    @Transient
    private RiskTransBean riskBean;
    @Transient
    private List<RiskSectionBean> sections;
}
