package com.brokersystems.brokerapp.uw.dtos;

import com.brokersystems.brokerapp.uw.model.QuestionnaireBean;

import java.util.List;

/**
 * Created by waititu on 03/06/2019.
 */
public class QuestionnaireDTO {

    private Long quizPolicyCode;
    private List<QuestionnaireBean> quizandAnswers;

    public Long getQuizPolicyCode() {
        return quizPolicyCode;
    }

    public void setQuizPolicyCode(Long quizPolicyCode) {
        this.quizPolicyCode = quizPolicyCode;
    }

    public List<QuestionnaireBean> getQuizandAnswers() {
        return quizandAnswers;
    }

    public void setQuizandAnswers(List<QuestionnaireBean> quizandAnswers) {
        this.quizandAnswers = quizandAnswers;
    }
}
