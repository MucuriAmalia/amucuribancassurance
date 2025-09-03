package com.brokersystems.brokerapp.setup.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by waititu on 31/05/2019.
 */
@Entity
@Table(name="sys_brk_binder_quiz_def")
public class BinderQuestionnaire {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="bqd_code")
    private Long bqdid;


    @Column(name = "bdq_id",nullable=false)
    private String questionShtDesc;

    @Column(name = "bdq_order",nullable=false)
    private BigDecimal questionOrder;

    @Column(name = "bdq_mandatory",nullable=false  )
    private String questionMandatory;


    @Column(name = "bdq_quiz_type",length = 30 ,nullable=false)
    private String questiontype;



    @Column(name = "bdq_quiz_name", length = 600 ,nullable=false)
    private String questionname;

    @ManyToOne
    @JoinColumn(name="bdq_bdq_code")
    private BinderQuestionnaire triggerByQuiz;

    @ManyToOne
    @JoinColumn(name="bdq_bqc_code")
    private BinderQuestionnaireChoices triggerByQuizAnswer;

    @ManyToOne
    @JoinColumn(name="bdq_bin_code",nullable=false)
    private BindersDef binder;

    @Column(name="bdq_wef")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wefDate;

    @Column(name="bdq_wet")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wetDate;

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

    public BindersDef getBinder() {
        return binder;
    }

    public void setBinder(BindersDef binder) {
        this.binder = binder;
    }

    public Date getWefDate() {
        return wefDate;
    }

    public void setWefDate(Date wefDate) {
        this.wefDate = wefDate;
    }

    public Date getWetDate() {
        return wetDate;
    }

    public void setWetDate(Date wetDate) {
        this.wetDate = wetDate;
    }

    public String getQuestionShtDesc() {
        return questionShtDesc;
    }

    public void setQuestionShtDesc(String questionShtDesc) {
        this.questionShtDesc = questionShtDesc;
    }

    public String getQuestionMandatory() {
        return questionMandatory;
    }

    public void setQuestionMandatory(String questionMandatory) {
        this.questionMandatory = questionMandatory;
    }

    public BinderQuestionnaire getTriggerByQuiz() {
        return triggerByQuiz;
    }

    public void setTriggerByQuiz(BinderQuestionnaire triggerByQuiz) {
        this.triggerByQuiz = triggerByQuiz;
    }

    public BinderQuestionnaireChoices getTriggerByQuizAnswer() {
        return triggerByQuizAnswer;
    }

    public void setTriggerByQuizAnswer(BinderQuestionnaireChoices triggerByQuizAnswer) {
        this.triggerByQuizAnswer = triggerByQuizAnswer;
    }

    public BigDecimal getQuestionOrder() {
        return questionOrder;
    }

    public void setQuestionOrder(BigDecimal questionOrder) {
        this.questionOrder = questionOrder;
    }
}
