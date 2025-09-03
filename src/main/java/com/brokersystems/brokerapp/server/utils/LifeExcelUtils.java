package com.brokersystems.brokerapp.server.utils;

import com.brokersystems.brokerapp.life.model.PolicyBenefitsDistribution;
import com.brokersystems.brokerapp.life.model.QPolicyBenefitsDistribution;
import com.brokersystems.brokerapp.life.repository.PolicyBenefitsDistributionRepo;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by waititu on 04/12/2017.
 */

@Component
public class LifeExcelUtils {



    @Autowired
    private PolicyTransRepo policyRepo;

    @Autowired
    private DateUtilities dateUtils;

    @Autowired
    private PolicyBenefitsDistributionRepo maturityRepo;



}
