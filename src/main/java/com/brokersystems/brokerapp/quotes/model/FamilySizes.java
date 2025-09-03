package com.brokersystems.brokerapp.quotes.model;

/**
 * Created by waititu on 23/09/2017.
 */
public class FamilySizes {
    private String famSize;
    private String famSizeDisp;

    public String getFamSizeDisp() {
        return famSizeDisp;
    }

    public void setFamSizeDisp(String famSizeDisp) {
        this.famSizeDisp = famSizeDisp;
    }

    public FamilySizes(String famSize, String famSizeDisp ) {
        this.famSize = famSize;
        this.famSizeDisp=famSizeDisp;
    }
    public String getFamSize() {
        return famSize;
    }

    public void setFamSize(String famSize) {
        this.famSize = famSize;
    }
}
