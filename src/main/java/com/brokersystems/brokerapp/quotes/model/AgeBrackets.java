package com.brokersystems.brokerapp.quotes.model;

/**
 * Created by waititu on 23/09/2017.
 */
public class AgeBrackets {
    String ageBracket;
    String ageBracketDisp;

    public  AgeBrackets( String ageBracket, String ageBracketDisp){
        this.ageBracket=ageBracket;
        this.ageBracketDisp=ageBracketDisp;

    }
    public String getAgeBracket() {
        return ageBracket;
    }

    public void setAgeBracket(String ageBracket) {
        this.ageBracket = ageBracket;
    }

    public String getAgeBracketDisp() {
        return ageBracketDisp;
    }

    public void setAgeBracketDisp(String ageBracketDisp) {
        this.ageBracketDisp = ageBracketDisp;
    }
}
