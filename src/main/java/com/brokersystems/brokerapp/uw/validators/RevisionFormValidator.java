package com.brokersystems.brokerapp.uw.validators;


import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.brokersystems.brokerapp.uw.model.RevisionForm;

@Component
public class RevisionFormValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		 return RevisionForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		//ValidationUtils.rejectIfEmptyOrWhitespace(errors, "effectiveDate", "missing.revisiondate");
		
	}

}
