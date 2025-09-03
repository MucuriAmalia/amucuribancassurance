package com.brokersystems.brokerapp.setup.service.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import com.brokersystems.brokerapp.setup.util.AESUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.ParametersDef;
import com.brokersystems.brokerapp.setup.model.QParametersDef;
import com.brokersystems.brokerapp.setup.repository.ParametersRepo;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.mysema.query.types.expr.BooleanExpression;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

@Service
public class ParamServiceImpl implements ParamService{
	
	@Autowired
	private ParametersRepo paramRepo;

	@Autowired
	private AESUtil aesUtil;

	@Override
	@Transactional(readOnly=true)
	public DataTablesResult<ParametersDef> findAllParameters(DataTablesRequest request) throws IllegalAccessException {
		Page<ParametersDef> page = paramRepo.findAll(request.searchPredicate(QParametersDef.parametersDef), request);
		return new DataTablesResult<>(request, page);
	}

	@Override
	@Modifying
	@Transactional(readOnly = false)
	public void createParameter(ParametersDef paramDef) throws BadRequestException {
		if(paramDef.getParamName().contains("AKI_") || paramDef.getParamName().contains("MPESA_")) {
			try {
				String cipherText = aesUtil.encrypt(paramDef.getParamValue());
				paramDef.setParamValue(cipherText);
			} catch (NoSuchAlgorithmException | BadPaddingException | InvalidKeyException | IllegalBlockSizeException |
					 NoSuchPaddingException  e) {
				throw new BadRequestException(e.getMessage());
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}

		}
		paramRepo.save(paramDef);
		
	}




	@Override
	@Transactional(readOnly = false)
	public void deleteParameter(Long paramCode) {
		paramRepo.delete(paramCode);
		
	}

	@Override
	@Transactional(readOnly=true)
	public String getParameterString(String paramName) throws BadRequestException {
		BooleanExpression pred = QParametersDef.parametersDef.paramName.eq(paramName);
		if(paramRepo.count(pred)==0) throw new BadRequestException(String.format("Parameter %s is not defined", paramName));
		if(paramRepo.count(pred)>1) throw new BadRequestException(String.format("Parameter %s has been defined more than once", paramName));
		return paramRepo.findOne(pred).getParamValue();
	}

	@Override
	@Transactional(readOnly=true)
	public BigDecimal getParamValue(String paramName) throws BadRequestException{
		BooleanExpression pred = QParametersDef.parametersDef.paramName.eq(paramName);
		if(paramRepo.count(pred)==0) throw new BadRequestException(String.format("Parameter %s is not defined", paramName));
		if(paramRepo.count(pred)>1) throw new BadRequestException(String.format("Parameter %s has been defined more than once", paramName));
		String paramValue = paramRepo.findOne(pred).getParamValue();
		BigDecimal val=null;
		try{
			val = new BigDecimal(paramValue);
		}
		catch(Exception ex){
			throw ex;
		}
		return val;
	}

	@Override
	@Transactional(readOnly=true)
	public Boolean getParamBoolean(String paramName) throws BadRequestException {
		BooleanExpression pred = QParametersDef.parametersDef.paramName.eq(paramName);
		if(paramRepo.count(pred)==0) throw new BadRequestException(String.format("Parameter %s is not defined", paramName));
		if(paramRepo.count(pred)>1) throw new BadRequestException(String.format("Parameter %s has been defined more than once", paramName));
		String paramValue = paramRepo.findOne(pred).getParamValue();
		Boolean val=null;
		try{
			val =  Boolean.valueOf(paramValue);
		}
		catch(Exception ex){
			throw ex;
		}
		return val;
	}


	@Override
	@Transactional(readOnly=true)
	public Integer getParamInt(String paramName) throws BadRequestException {
		BooleanExpression pred = QParametersDef.parametersDef.paramName.eq(paramName);
		if(paramRepo.count(pred)==0) throw new BadRequestException(String.format("Parameter %s is not defined", paramName));
		if(paramRepo.count(pred)>1) throw new BadRequestException(String.format("Parameter %s has been defined more than once", paramName));
		String paramValue = paramRepo.findOne(pred).getParamValue();
		Integer val=null;
		try{
			val = Integer.valueOf(paramValue);
		}
		catch(Exception ex){
			throw ex;
		}
		return val;
	}
}
