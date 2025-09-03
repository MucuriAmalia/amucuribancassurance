package com.brokersystems.brokerapp.server.utils;

import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.service.ParamService;

@Component
public class ValidatorUtils {

	@Autowired
	private ParamService paramService;

	public boolean validateIdNo(String idNo) throws BadRequestException {
		boolean matchesPass = false;
		String idNoValidator = paramService.getParameterString("ID_NO_FORMAT");
		StringTokenizer token = new StringTokenizer(idNoValidator, ";");
		while (token.hasMoreTokens()) {
			if (idNo.matches(token.nextToken())) {
				matchesPass = true;
			}
		}
		if (!matchesPass) {
			throw new BadRequestException("Enter Valid ID Number");
		}
		return matchesPass;
	}

	public boolean validatePhoneNo(String phoneNo) throws BadRequestException {
		boolean matchesPass = false;
		String phoneNoValidator = paramService.getParameterString("PHONE_NO_FORMAT");
		StringTokenizer token = new StringTokenizer(phoneNoValidator, ";");
		while (token.hasMoreTokens()) {
			if (phoneNo.matches(token.nextToken())) {
				matchesPass = true;
			}
		}
		if (!matchesPass) {
			throw new BadRequestException("Enter Valid Phone Number");
		}
		return matchesPass;
	}

	public boolean validatePassport(String passportNo) throws BadRequestException {
		boolean matchesPass = false;
		if (!"".equals(passportNo) || StringUtils.isNotBlank(passportNo)) {
			String passportValidator = paramService.getParameterString("PASSPORT_NO_FORMAT");
			StringTokenizer token = new StringTokenizer(passportValidator, ";");
			while (token.hasMoreTokens()) {
				if (passportNo.matches(token.nextToken())) {
					matchesPass = true;
				}
			}
			if (!matchesPass) {
				throw new BadRequestException("Enter Valid Passport Number");
			}
		}
		return matchesPass;
	}

	public boolean validatePinNo(String pinNo) throws BadRequestException {
		boolean matchesPass = false;
		String passportValidator = paramService.getParameterString("PIN_NO_FORMAT");
		StringTokenizer token = new StringTokenizer(passportValidator, ";");
		while (token.hasMoreTokens()) {
			if (pinNo.matches(token.nextToken())) {
				matchesPass = true;

			}
		}
		if (!matchesPass) {
			throw new BadRequestException("Enter Valid Pin Number");
		}

		return matchesPass;
	}
	public boolean validatePin(String pinNo) {
		boolean matchesPass = false;
		try {
			String passportValidator = paramService.getParameterString("PIN_NO_FORMAT");
			StringTokenizer token = new StringTokenizer(passportValidator, ";");
			while (token.hasMoreTokens()) {
				if (pinNo.matches(token.nextToken())) {
					matchesPass = true;
				}
			}
		}
		catch(BadRequestException ex){
			matchesPass = false;
		}

		return matchesPass;
	}


	public boolean validateId(String idNo){
		boolean matchesPass = false;
		try {
			String idNoValidator = paramService.getParameterString("ID_NO_FORMAT");
			StringTokenizer token = new StringTokenizer(idNoValidator, ";");
			while (token.hasMoreTokens()) {
				if (idNo.matches(token.nextToken())) {
					matchesPass = true;
				}
			}
		}catch(BadRequestException ex){
			matchesPass = false;
		}
		return matchesPass;
	}

	public boolean validatePasspNo(String passportNo){
		boolean matchesPass = false;
		try {
			String passportValidator = paramService.getParameterString("PASSPORT_NO_FORMAT");
			StringTokenizer token = new StringTokenizer(passportValidator, ";");
			while (token.hasMoreTokens()) {
				if (passportNo.matches(token.nextToken())) {
					matchesPass = true;
				}
			}
		}catch(BadRequestException ex){
			 matchesPass = false;
			}
		return matchesPass;
	}

	public boolean validatePhonePrefix(String prefix) throws BadRequestException {
		boolean matchesPass = false;
		String phonePrefixValidator = paramService.getParameterString("PHONE_NO_PREFIX_FORMAT");
		StringTokenizer token = new StringTokenizer(phonePrefixValidator, ";");
		while (token.hasMoreTokens()) {
			if (prefix.matches(token.nextToken())) {
				matchesPass = true;

			}
		}
		if (!matchesPass) {
			throw new BadRequestException("Enter Valid Prefix");
		}

		return matchesPass;
	}

	public boolean validate(String dataToValidate, String pattern) throws BadRequestException {
		if (pattern == null || StringUtils.isBlank(pattern))
			return true;
		boolean matchesPass = false;
		StringTokenizer token = new StringTokenizer(pattern, ";");
		System.out.println("data to validate "+dataToValidate);
		while (token.hasMoreTokens()) {
			final String nextToken = token.nextToken();
			System.out.println("Next token..."+nextToken);
			if (dataToValidate.matches(nextToken)) {
				matchesPass = true;
			}
		}
		return matchesPass;
	}
}
