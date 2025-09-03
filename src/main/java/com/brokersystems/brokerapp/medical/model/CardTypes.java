package com.brokersystems.brokerapp.medical.model;

import javax.persistence.*;

/**
 * Created by waititu on 13/04/2018.
 */
@Entity
@Table(name = "sys_brk_card_types")
public class CardTypes {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "card_id")
    private Long cardId;

    @Column(name = "card_sht_desc",nullable = false,unique = true)
    private String cardShtDesc;

    @Column(name = "card_desc",nullable = false)
    private String cardDesc;

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public String getCardShtDesc() {
        return cardShtDesc;
    }

    public void setCardShtDesc(String cardShtDesc) {
        this.cardShtDesc = cardShtDesc;
    }

    public String getCardDesc() {
        return cardDesc;
    }

    public void setCardDesc(String cardDesc) {
        this.cardDesc = cardDesc;
    }
}
