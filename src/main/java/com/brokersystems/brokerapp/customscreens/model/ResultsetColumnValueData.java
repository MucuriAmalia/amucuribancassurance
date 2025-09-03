package com.brokersystems.brokerapp.customscreens.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResultsetColumnValueData {

    private  int id;
    private String value;
    @SuppressWarnings("unused")
    private Integer score;

    public boolean matches(final String match) {
        return match.equalsIgnoreCase(this.value);
    }

    public boolean codeMatches(final Integer match) {
        return match.intValue() == this.id;
    }
}
