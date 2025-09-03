package com.brokersystems.brokerapp.uw.model;

import com.brokersystems.brokerapp.setup.model.BinderQuestionnaire;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Created by waititu on 07/06/2019.
 */
@Entity
@Table(name="sys_brk_pol_questionnaire")
public class PolicyQuestionnaire {
    @Id
    @SequenceGenerator(name = "myPolQuizSeq",sequenceName = "pol_quiz_seq",allocationSize=1)
    @GeneratedValue(generator = "myPolQuizSeq")
    @Column(name="quiz_id")
    private Long quizId;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="quiz_pol_id", nullable = false)
    private PolicyTrans policy;


    @XmlTransient
    @ManyToOne
    @JoinColumn(name="quiz_bqd_code", nullable = false)
    private BinderQuestionnaire question;

    @Column(name = "quiz_choice", length = 600)
    private String choice;

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public PolicyTrans getPolicy() {
        return policy;
    }

    public void setPolicy(PolicyTrans policy) {
        this.policy = policy;
    }

    public BinderQuestionnaire getQuestion() {
        return question;
    }

    public void setQuestion(BinderQuestionnaire question) {
        this.question = question;
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }
}
