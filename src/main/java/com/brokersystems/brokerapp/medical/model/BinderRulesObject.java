package com.brokersystems.brokerapp.medical.model;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class BinderRulesObject {

    private List<MedicalBinderRules> binderRules;

    public List<MedicalBinderRules> getBinderRules() {
        return binderRules;
    }

    public void setBinderRules(Iterable<MedicalBinderRules> binderRules) {
        this.binderRules = new ArrayList<>();
        for(MedicalBinderRules rules:binderRules){
            this.binderRules.add(rules);
        }
    }
}
