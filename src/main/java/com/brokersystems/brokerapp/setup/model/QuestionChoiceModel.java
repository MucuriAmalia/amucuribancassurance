package com.brokersystems.brokerapp.setup.model;

import java.io.Serializable;
import java.util.List;

public class QuestionChoiceModel implements Serializable {

    private List<SingleQuizModel> questions;

    public List<SingleQuizModel> getQuestions() {
        return questions;
    }

    public void setQuestions(List<SingleQuizModel> questions) {
        this.questions = questions;
    }
}
