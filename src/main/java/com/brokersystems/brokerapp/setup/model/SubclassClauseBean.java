package com.brokersystems.brokerapp.setup.model;

import java.util.List;

public class SubclassClauseBean {
	
	    private Long subCode;
	    
	    private Long detCode;
		
		private List<Long> clauses;

		public Long getSubCode() {
			return subCode;
		}

		public void setSubCode(Long subCode) {
			this.subCode = subCode;
		}

		public List<Long> getClauses() {
			return clauses;
		}

		public void setClauses(List<Long> clauses) {
			this.clauses = clauses;
		}

		public Long getDetCode() {
			return detCode;
		}

		public void setDetCode(Long detCode) {
			this.detCode = detCode;
		}
		
		
		
		

}
