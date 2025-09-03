package com.brokersystems.brokerapp.medical.model;

import java.util.List;

/**
 * Created by peter on 5/29/2017.
 */
public class MedicalBatchDTO {

    private List<Long> cards;

    public List<Long> getCards() {
        return cards;
    }

    public void setCards(List<Long> cards) {
        this.cards = cards;
    }
}
