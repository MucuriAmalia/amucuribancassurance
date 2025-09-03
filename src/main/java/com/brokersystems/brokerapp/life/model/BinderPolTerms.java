package com.brokersystems.brokerapp.life.model;

/**
 * Created by waititu on 24/11/2017.
 */
public class BinderPolTerms {
    Integer term;
    Integer termDisplay;
    public BinderPolTerms(Integer term,Integer termDisplay){
        this.term=term;
        this.termDisplay=termDisplay;
    }

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public Integer getTermDisplay() {
        return termDisplay;
    }

    public void setTermDisplay(Integer termDisplay) {
        this.termDisplay = termDisplay;
    }
}
