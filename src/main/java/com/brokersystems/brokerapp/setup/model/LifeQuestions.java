package com.brokersystems.brokerapp.setup.model;

import javax.persistence.*;

@Entity
@Table(name="sys_brk_questions_def")
public class LifeQuestions {


    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="bqd_code")
    private Long bqdid;

    @Column(name = "bdq_quiz_type",length = 30)
    private String questiontype;

    @Column(name = "bdq_quiz_name", length = 600)
    private String questionname;


    public Long getBqdid() {
        return bqdid;
    }

    public void setBqdid(Long bqdid) {
        this.bqdid = bqdid;
    }

    public String getQuestiontype() {
        return questiontype;
    }

    public void setQuestiontype(String questiontype) {
        this.questiontype = questiontype;
    }

    public String getQuestionname() {
        return questionname;
    }

    public void setQuestionname(String questionname) {
        this.questionname = questionname;
    }

}
