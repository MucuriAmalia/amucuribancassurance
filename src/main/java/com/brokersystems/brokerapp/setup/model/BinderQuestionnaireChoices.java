package com.brokersystems.brokerapp.setup.model;

import javax.persistence.*;

/**
 * Created by waititu on 01/06/2019.
 */
@Entity
@Table(name="sys_brk_binder_quiz_def_opts")
public class BinderQuestionnaireChoices {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="bqc_code")
    private Long bqcId;

    @ManyToOne
    @JoinColumn(name="bqc_bdb_id",nullable=false)
    private BinderQuestionnaire questions;


    @Column(name = "bqc_choice", length = 600, nullable = false)
    private String choice;

    public Long getBqcId() {
        return bqcId;
    }

    public void setBqcId(Long bqcId) {
        this.bqcId = bqcId;
    }

    public BinderQuestionnaire getQuestions() {
        return questions;
    }

    public void setQuestions(BinderQuestionnaire questions) {
        this.questions = questions;
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }
}
