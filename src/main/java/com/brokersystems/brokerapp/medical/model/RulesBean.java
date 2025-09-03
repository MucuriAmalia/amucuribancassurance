package com.brokersystems.brokerapp.medical.model;

import java.util.List;

/**
 * Created by peter on 5/24/2017.
 */
public class RulesBean {

    private Long catCode;

    private List<Long> rules;

    public Long getCatCode() {
        return catCode;
    }

    public void setCatCode(Long catCode) {
        this.catCode = catCode;
    }

    public List<Long> getRules() {
        return rules;
    }

    public void setRules(List<Long> rules) {
        this.rules = rules;
    }
}
