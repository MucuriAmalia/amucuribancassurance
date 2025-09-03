package com.brokersystems.brokerapp.enums;

/**
 * Created by HP on 9/24/2017.
 */
public enum  AccountTypeEnum {

    INS("INS","Agents"),
    IA("IA","Inhouse Agents"),
    DR("DR","Direct"),
    SF("SF","Self Fund"),
    RN("RN","Reinsurance Company"),
    BRK("BRK","Broker"),
    SUB("SUB","Sub Agents"),
    MRK("MRK", "Marketer"),
    INT("INT", "Introducer");

    private String code;
    private String value;

    private AccountTypeEnum(String code,String value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
