package com.brokersystems.brokerapp.reinsurance.enums;

public enum TreatyTypes {
    MAN(11l,"MAN","Mandatory"),
    QUOTA(13l,"QUOTA","Quota Share"),
    FIRSTSUP(14l,"1ST SURP","1st Surplus"),
    SECONDSUP(15l,"2ND SURP","2nd Surplus"),
    FACRE(16l,"FACRE","Facultative"),
    XOL(18l,"XOL","Excess of Loss");

    public final Long treatyCode;
    public final String treatyId;
    public final String treatyDesc;

    private TreatyTypes(Long treatyCode,String treatyId,String treatyDesc) {
        this.treatyCode = treatyCode;
        this.treatyId = treatyId;
        this.treatyDesc = treatyDesc;
    }

    }
