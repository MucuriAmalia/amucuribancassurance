package com.brokersystems.brokerapp.setup.model;

import javax.persistence.*;

@Entity
@Table(name="sys_brk_questions_def_opts")
public class LifeQuestionsChoices {


    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="qc_code")
    private Long qcId;

    @ManyToOne
    @JoinColumn(name="qc_bdb_id",nullable=false)
    private LifeQuestions questions;


    @Column(name = "qc_choice", length = 300, nullable = false)
    private String choice;


    public Long getQcId() {
        return qcId;
    }

    public void setQcId(Long qcId) {
        this.qcId = qcId;
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }

    public LifeQuestions getQuestions() {
        return questions;
    }

    public void setQuestions(LifeQuestions questions) {
        this.questions = questions;
    }
}
