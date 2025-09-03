package com.brokersystems.brokerapp.server.validator;

import com.brokersystems.brokerapp.setup.dto.OrganizationDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.brokersystems.brokerapp.setup.model.Organization;


@Component
public class FileUploadValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		 return OrganizationDTO.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		 
		OrganizationDTO holder = (OrganizationDTO)obj;
		if(holder.getFile()!=null){
			if(holder.getFile().getSize()> 600000){
				errors.rejectValue("file", "file.toobig");
			}
		}
		
			ValidationUtils.rejectIfEmptyOrWhitespace(errors,"currency.curCode", "missing.currency");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors,"orgName", "missing.orgname");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors,"orgShtDesc", "missing.orgshtdesc");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors,"country.couCode", "missing.orgcountry");
			//ValidationUtils.rejectIfEmptyOrWhitespace(errors,"address.county.countyId", "missing.orgcounty");
			//ValidationUtils.rejectIfEmptyOrWhitespace(errors,"address.town.ctCode", "missing.orgtown");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors,"address.addAddress", "missing.address");
	}

}
