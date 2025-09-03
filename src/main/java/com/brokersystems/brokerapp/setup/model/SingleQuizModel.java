package com.brokersystems.brokerapp.setup.model;

public class SingleQuizModel {


    private String type;
    private String name;
    private Long quizCode;
    private String[] choices;
    private String isRequired;
    private String startWithNewLine;
    private String visibleIf;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getChoices() {
        return choices;
    }

    public void setChoices(String[] choices) {
        this.choices = choices;
    }

    public Long getQuizCode() {
        return quizCode;
    }

    public void setQuizCode(Long quizCode) {
        this.quizCode = quizCode;
    }

    public String getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(String isRequired) {
        this.isRequired = isRequired;
    }

    public String getStartWithNewLine() {
        return startWithNewLine;
    }

    public void setStartWithNewLine(String startWithNewLine) {
        this.startWithNewLine = startWithNewLine;
    }

    public String getVisibleIf() {
        return visibleIf;
    }

    public void setVisibleIf(String visibleIf) {
        this.visibleIf = visibleIf;
    }
}
