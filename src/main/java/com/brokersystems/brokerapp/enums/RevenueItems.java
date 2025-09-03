package com.brokersystems.brokerapp.enums;

public enum RevenueItems {

	//UP("UP","Underwriting Premium"),
	UC("UC","Underwriting Commission"),
	AF("AF","Admin Fees"),
	//CP("CP","Creditor Payment"),
	SD("SD","Stamp Duty"),
	PHCF("PHCF","PHF Fund"),
	SAC("SAC","Sub Agent Commission"),
	TL("TL","Training Levy"),
	WHTX("WHTX","Withholding Tax"),
//	WI("WI","Write Ins"),
//	WO("WO","Write Offs"),
	NR("NR"," Commission Receipts");
//	DR("DR","Direct Receipts"),
//	DC("DC","Document Charge"),
//	LTA("LTA","Long Term Agreements"),

//	SAP("SAP","Sub Agent Payments"),
//	EX("EX","Extras"),
//	CF("CF","Issue Card Fee"),
//	SC("SC","Card Service Charge"),
//	RE("RE","Re Issue Card Fee"),
//	LO("LO","Claim Loss Opening"),
//	LR("LR","Claim Loss Revision"),
//	CPR("CP","Claimant Payment"),
//	CPF("CF","Claim Fee Payment"),
//	CPL("CPL","Claims Payment Pool"),
//	VAT("VAT","Value Added Tax");

	private String code;
	
	private String value;

	private RevenueItems(String code,String value) {
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
