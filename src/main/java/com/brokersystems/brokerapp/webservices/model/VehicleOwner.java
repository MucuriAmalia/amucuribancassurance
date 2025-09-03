
package com.brokersystems.brokerapp.webservices.model;

public class VehicleOwner {
	
	private String CODE;
	private String MIDDLENAME;
	private String PIN;
	private String LASTNAME;
	private String TOWN;
	private String ADDRESS;
	private String FIRSTNAME;
	private String TELNO;
        private Integer lossRatio;
        
	public String getCODE() {
		return CODE;
	}
	public void setCODE(String cODE) {
		CODE = cODE;
	}
	public String getMIDDLENAME() {
		return MIDDLENAME;
	}
	public void setMIDDLENAME(String mIDDLENAME) {
		MIDDLENAME = mIDDLENAME;
	}
	public String getPIN() {
		return PIN;
	}
	public void setPIN(String pIN) {
		PIN = pIN;
	}
	public String getLASTNAME() {
		return LASTNAME;
	}
	public void setLASTNAME(String lASTNAME) {
		LASTNAME = lASTNAME;
	}
	public String getTOWN() {
		return TOWN;
	}
	public void setTOWN(String tOWN) {
		TOWN = tOWN;
	}
	public String getADDRESS() {
		return ADDRESS;
	}
	public void setADDRESS(String aDDRESS) {
		ADDRESS = aDDRESS;
	}
	public String getFIRSTNAME() {
		return FIRSTNAME;
	}
	public void setFIRSTNAME(String fIRSTNAME) {
		FIRSTNAME = fIRSTNAME;
	}
	public String getTELNO() {
		return TELNO;
	}
	public void setTELNO(String tELNO) {
		TELNO = tELNO;
	}


    public void setLossRatio(Integer lossRatio) {
        this.lossRatio = lossRatio;
    }

    public Integer getLossRatio() {
        return lossRatio;
    }
}

