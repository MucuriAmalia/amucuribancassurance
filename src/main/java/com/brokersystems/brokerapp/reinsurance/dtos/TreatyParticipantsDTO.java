package com.brokersystems.brokerapp.reinsurance.dtos;

import java.math.BigDecimal;

public class TreatyParticipantsDTO {

    private  Long treatyClassId;
    private  Long participantId;
    private  String participant;
    private  BigDecimal rate;
    private  String brokerType;
    private  String taxChargeable;
    private  Long brokerId;
    private  Long treatyId;
    private  String broker;
    private  Long revenueItemId;
    private  String revenueItemDesc;
    private  BigDecimal taxRate;
    private  String taxRateType;
    private  BigDecimal recoveryPercent;

    public TreatyParticipantsDTO() {
    }

    private TreatyParticipantsDTO(final Long treatyClassId,
                                  final Long participantId,
                                  final String participant,
                                  final BigDecimal rate,
                                  final String brokerType,
                                  final String taxChargeable,
                                  final Long brokerId,
                                  final String broker,
                                  final Long revenueItemId,
                                  final String revenueItemDesc,
                                  final BigDecimal taxRate,
                                  final String taxRateType,
                                  final BigDecimal recoveryPercent,
                                  final Long treatyId) {
        this.treatyClassId = treatyClassId;
        this.participantId = participantId;
        this.participant = participant;
        this.rate = rate;
        this.brokerType = brokerType;
        this.taxChargeable = taxChargeable;
        this.brokerId = brokerId;
        this.broker = broker;
        this.revenueItemId = revenueItemId;
        this.revenueItemDesc = revenueItemDesc;
        this.taxRate = taxRate;
        this.taxRateType = taxRateType;
        this.recoveryPercent = recoveryPercent;
        this.treatyId = treatyId;
    }

    public  static TreatyParticipantsDTO data(final Long treatyClassId,
                                              final Long participantId,
                                              final String participant,
                                              final BigDecimal rate,
                                              final String brokerType,
                                              final String taxChargeable,
                                              final Long brokerId,
                                              final String broker,
                                              final Long revenueItemId,
                                              final String revenueItemDesc,
                                              final BigDecimal taxRate,
                                              final String taxRateType,
                                              final BigDecimal recoveryPercent,
                                              final Long treatyId){
        return new TreatyParticipantsDTO(treatyClassId, participantId, participant, rate, brokerType,
                taxChargeable, brokerId, broker, revenueItemId, revenueItemDesc, taxRate, taxRateType, recoveryPercent,treatyId);
    }

    public void setTreatyClassId(Long treatyClassId) {
        this.treatyClassId = treatyClassId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    public void setParticipant(String participant) {
        this.participant = participant;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public void setBrokerType(String brokerType) {
        this.brokerType = brokerType;
    }

    public void setTaxChargeable(String taxChargeable) {
        this.taxChargeable = taxChargeable;
    }

    public void setBrokerId(Long brokerId) {
        this.brokerId = brokerId;
    }

    public void setTreatyId(Long treatyId) {
        this.treatyId = treatyId;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }

    public void setRevenueItemId(Long revenueItemId) {
        this.revenueItemId = revenueItemId;
    }

    public void setRevenueItemDesc(String revenueItemDesc) {
        this.revenueItemDesc = revenueItemDesc;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public void setTaxRateType(String taxRateType) {
        this.taxRateType = taxRateType;
    }

    public void setRecoveryPercent(BigDecimal recoveryPercent) {
        this.recoveryPercent = recoveryPercent;
    }

    public Long getTreatyId() {
        return treatyId;
    }

    public Long getTreatyClassId() {
        return treatyClassId;
    }

    public Long getParticipantId() {
        return participantId;
    }

    public String getParticipant() {
        return participant;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public String getBrokerType() {
        return brokerType;
    }

    public String getTaxChargeable() {
        return taxChargeable;
    }

    public Long getBrokerId() {
        return brokerId;
    }

    public String getBroker() {
        return broker;
    }

    public Long getRevenueItemId() {
        return revenueItemId;
    }

    public String getRevenueItemDesc() {
        return revenueItemDesc;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public String getTaxRateType() {
        return taxRateType;
    }

    public BigDecimal getRecoveryPercent() {
        return recoveryPercent;
    }
}

//package com.brokersystems.brokerapp.reinsurance.dtos;
//
//import lombok.Getter;
//import lombok.Setter;
//import org.apache.commons.lang3.StringEscapeUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.math.BigDecimal;
//
//import static com.brokersystems.brokerapp.common.Constants.*;
//
//@Getter
//public class TreatyParticipantsDTO {
//
//    private static final Logger logger = LoggerFactory.getLogger(TreatyParticipantsDTO.class);
//
//    @Setter
//    private Long treatyClassId;
//    @Setter
//    private Long participantId;
//    private String participant;
//    @Setter
//    private BigDecimal rate;
//    private String brokerType;
//    private String taxChargeable;
//    @Setter
//    private Long brokerId;
//    @Setter
//    private Long treatyId;
//    private String broker;
//    @Setter
//    private Long revenueItemId;
//    private String revenueItemDesc;
//    @Setter
//    private BigDecimal taxRate;
//    private String taxRateType;
//    @Setter
//    private BigDecimal recoveryPercent;
//
//    public TreatyParticipantsDTO() {
//    }
//
//    private TreatyParticipantsDTO(final Long treatyClassId,
//                                  final Long participantId,
//                                  final String participant,
//                                  final BigDecimal rate,
//                                  final String brokerType,
//                                  final String taxChargeable,
//                                  final Long brokerId,
//                                  final String broker,
//                                  final Long revenueItemId,
//                                  final String revenueItemDesc,
//                                  final BigDecimal taxRate,
//                                  final String taxRateType,
//                                  final BigDecimal recoveryPercent,
//                                  final Long treatyId) {
//        validateAndSanitize(participant, NAME_PATTERN, "Participant");
//        validateAndSanitize(brokerType, REF_PATTERN, "Broker Type");
//        validateAndSanitize(taxChargeable, REF_PATTERN, "Tax Chargeable");
//        validateAndSanitize(broker, NAME_PATTERN, "Broker");
//        validateAndSanitize(revenueItemDesc, DESC_PATTERN, "Revenue Item Description");
//        validateAndSanitize(taxRateType, REF_PATTERN, "Tax Rate Type");
//        this.treatyClassId = treatyClassId;
//        this.participantId = participantId;
//        this.participant = participant == null ? null : StringEscapeUtils.escapeHtml4(participant.trim());
//        this.rate = rate;
//        this.brokerType = brokerType == null ? null : StringEscapeUtils.escapeHtml4(brokerType.trim());
//        this.taxChargeable = taxChargeable == null ? null : StringEscapeUtils.escapeHtml4(taxChargeable.trim());
//        this.brokerId = brokerId;
//        this.broker = broker == null ? null : StringEscapeUtils.escapeHtml4(broker.trim());
//        this.revenueItemId = revenueItemId;
//        this.revenueItemDesc = revenueItemDesc == null ? null : StringEscapeUtils.escapeHtml4(revenueItemDesc.trim());
//        this.taxRate = taxRate;
//        this.taxRateType = taxRateType == null ? null : StringEscapeUtils.escapeHtml4(taxRateType.trim());
//        this.recoveryPercent = recoveryPercent;
//        this.treatyId = treatyId;
//    }
//
//    public static TreatyParticipantsDTO data(final Long treatyClassId,
//                                             final Long participantId,
//                                             final String participant,
//                                             final BigDecimal rate,
//                                             final String brokerType,
//                                             final String taxChargeable,
//                                             final Long brokerId,
//                                             final String broker,
//                                             final Long revenueItemId,
//                                             final String revenueItemDesc,
//                                             final BigDecimal taxRate,
//                                             final String taxRateType,
//                                             final BigDecimal recoveryPercent,
//                                             final Long treatyId) {
//        return new TreatyParticipantsDTO(treatyClassId, participantId, participant, rate, brokerType,
//                taxChargeable, brokerId, broker, revenueItemId, revenueItemDesc, taxRate, taxRateType, recoveryPercent, treatyId);
//    }
//
//    private void validateAndSanitize(String input, String pattern, String fieldName) {
//        if (input == null) {
//            return;
//        }
//        logger.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
//        String trimmedInput = input.trim();
//        if (!trimmedInput.isEmpty() && !trimmedInput.matches(pattern)) {
//            throw new IllegalArgumentException(
//                    String.format("Invalid characters in %s: '%s' does not match pattern %s", fieldName, trimmedInput, pattern)
//            );
//        }
//    }
//
//    public void setParticipant(String participant) {
//        validateAndSanitize(participant, NAME_PATTERN, "Participant");
//        this.participant = participant == null ? null : StringEscapeUtils.escapeHtml4(participant.trim());
//    }
//
//    public void setBrokerType(String brokerType) {
//        validateAndSanitize(brokerType, REF_PATTERN, "Broker Type");
//        this.brokerType = brokerType == null ? null : StringEscapeUtils.escapeHtml4(brokerType.trim());
//    }
//
//    public void setTaxChargeable(String taxChargeable) {
//        validateAndSanitize(taxChargeable, REF_PATTERN, "Tax Chargeable");
//        this.taxChargeable = taxChargeable == null ? null : StringEscapeUtils.escapeHtml4(taxChargeable.trim());
//    }
//
//    public void setBroker(String broker) {
//        validateAndSanitize(broker, NAME_PATTERN, "Broker");
//        this.broker = broker == null ? null : StringEscapeUtils.escapeHtml4(broker.trim());
//    }
//
//    public void setRevenueItemDesc(String revenueItemDesc) {
//        validateAndSanitize(revenueItemDesc, DESC_PATTERN, "Revenue Item Description");
//        this.revenueItemDesc = revenueItemDesc == null ? null : StringEscapeUtils.escapeHtml4(revenueItemDesc.trim());
//    }
//
//    public void setTaxRateType(String taxRateType) {
//        validateAndSanitize(taxRateType, REF_PATTERN, "Tax Rate Type");
//        this.taxRateType = taxRateType == null ? null : StringEscapeUtils.escapeHtml4(taxRateType.trim());
//    }
//}