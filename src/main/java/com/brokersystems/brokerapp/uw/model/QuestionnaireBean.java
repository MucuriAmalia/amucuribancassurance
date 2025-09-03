package com.brokersystems.brokerapp.uw.model;

import java.util.List;

/**
 * Created by waititu on 04/06/2019.
 */
public class QuestionnaireBean {

    private String question;
    private List<String> answer;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getAnswer() {
        return answer;
    }

    public void setAnswer(List<String> answer) {
        this.answer = answer;
    }
}
