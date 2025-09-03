package com.brokersystems.brokerapp.uw.dtos;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.easybatch.core.record.GenericRecord;
import org.easybatch.core.record.Header;

import com.brokersystems.brokerapp.setup.model.User;

import static com.brokersystems.brokerapp.common.Constants.REF_PATTERN;

public class RenewalDTO  {

	private static final Logger logger = LoggerFactory.getLogger(RenewalDTO.class);
	
	private Long policyId;
	
	private User user;
	
	private String policyNo;
	

	public Long getPolicyId() {
		return policyId;
	}

	public void setPolicyId(Long policyId) {
		this.policyId = policyId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	

	public String getPolicyNo() {
		return policyNo;
	}


	@Override
	public String toString() {
		return "RenewalDTO [policyId=" + policyId + ", user=" + user.getUsername() + "]";
	}

	private void validateAndSanitize(String input, String pattern, String fieldName) {
		if (input == null) {
			return;
		}
		//logger.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
		String trimmedInput = input.trim();
		if (!trimmedInput.isEmpty() && !trimmedInput.matches(pattern)) {
			throw new IllegalArgumentException(
					String.format("Invalid characters in %s: '%s' does not match pattern %s", fieldName, trimmedInput, pattern)
			);
		}
	}

	public void setPolicyNo(String policyNo) {
		validateAndSanitize(policyNo, REF_PATTERN, "Policy Number");
		this.policyNo = policyNo == null ? null : StringEscapeUtils.escapeHtml4(policyNo.trim());
	}

}
