package com.brokersystems.brokerapp.mail.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDetailList {
    private CorrespondenceAddressee correspondenceAddressee;
    private CorrespondenceContent correspondenceContent;
    private CorrespondenceMedia correspondenceMedia;
    private CorrespondenceTemplateReference correspondenceTemplateReference;
    private ProductAndServiceType productAndServiceType;
    private List<DynamicFields> dynamicFields;
    private List<EmailAttachments> attachments;
}
