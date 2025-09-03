package com.brokersystems.brokerapp.setup.service.impl;

import com.brokersystems.brokerapp.life.model.LifeCommissionRates;
import com.brokersystems.brokerapp.life.model.LifeSubAgentCommissionRates;
import com.brokersystems.brokerapp.life.repository.LifeCommissionRatesRepo;
import com.brokersystems.brokerapp.life.repository.LifeSubAgentCommissionRatesRepo;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.*;
import com.brokersystems.brokerapp.setup.service.SetupAudit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@Service
public class SetupAuditClassImpl implements SetupAudit {
    @Autowired
    BindersRepo bindersRepo;

    @Autowired
    AccountRepo accountRepo;

    @Autowired
    BinderDetRepo binderDetRepo;

    @Autowired
    AuditTrailLogger auditTrailLogger;

    @Autowired
    SubClassCoverRepo subClassCoverRepo;

    @Autowired
    PremRatesRepo premRatesRepo;

    @Autowired
    CommRatesRepo commRatesRepo;

    @Autowired
    SubAgentCommRepo subAgentCommRepo;

    @Autowired
    LifeCommissionRatesRepo lifeCommissionRatesRepo;

    @Autowired
    LifeSubAgentCommissionRatesRepo lifeSubAgentCommissionRatesRepo;

    @Autowired
    SubClassPerilsRepo subClassPerilsRepo;

    @Autowired
    SectionRepo sectionRepo;

    @Autowired
    SubclassReqDocRepo subclassReqDocRepo;

    @Autowired
    BinderReqrdDocsRepo binderReqrdDocsRepo;

    @Override
    public String logBinder(BindersDef binder){
        String message="";
        String binderType="";
        boolean newBinder = binder.getBinId()==null?true:false;
        if(newBinder){
            String binderCurr=binder.getCurrency().getCurName()==null?" KSH":binder.getCurrency().getCurName();
            binderType=binder.getBinType().equalsIgnoreCase("S")?"Self-Fund":
                    binder.getBinType().equalsIgnoreCase("B")? "Binder":"Open Market";
            message= "Created a new Binder with the following attributes. Insurance: "+accountRepo.findOne(binder.getAccount().getAcctId()).getShtDesc()+ " Bind Type "+ binderType+" Binder ID: "+binder.getBinShtDesc()+" Bind Name: "+binder.getBinName()+
                    " Min Premium: "+ binder.getMinPrem()+ " Product: "+binder.getProduct().getProDesc()+" Currency: "+binderCurr+
                    " Bind Remarks: "+ binder.getBinRemarks()+ " Active Indicator: "+ binder.isActive()+" Default: "+binder.isBinDefault();
        }
        else{

            BindersDef oldBinder=bindersRepo.findOne(binder.getBinId());
            binderType=binder.getBinType().equalsIgnoreCase("S")?"Self-Fund":
                    binder.getBinType().equalsIgnoreCase("B")? "Binder":"Open Market";
            int bDef= Boolean.compare(oldBinder.isBinDefault(),binder.isBinDefault());
            String defs=bDef==0 ? "" : " Binder Default Changed From: "+oldBinder.isBinDefault()+" To: "+binder.isBinDefault();
            int bAi=Boolean.compare(oldBinder.isActive(),binder.isActive());
            String active=bAi==0 ? "" : " Active Indicator From: " +oldBinder.isActive()+ " To: "+binder.isActive();
            String oRemarks=oldBinder.getBinRemarks()==null?"None":oldBinder.getBinRemarks();
            String nRemarks=binder.getBinRemarks()==null?"None":binder.getBinRemarks();

            String bRemarks=oRemarks.equalsIgnoreCase(nRemarks)? "" : " Changed Remarks From: "+oRemarks+" To: "+nRemarks;
            String bCur=oldBinder.getCurrency().getCurIsoCode().equalsIgnoreCase(binder.getCurrency().getCurIsoCode())?"" : " Changed Currency From: "+oldBinder.getCurrency().getCurIsoCode()+" To: "+binder.getCurrency().getCurIsoCode();
            String bProd=oldBinder.getProduct().getProDesc().equalsIgnoreCase(binder.getProduct().getProDesc())?"" : " Changed Product From: "+oldBinder.getProduct().getProDesc()+" To: "+binder.getProduct().getProDesc();
            BigDecimal safe=oldBinder.getMinPrem()==null?new BigDecimal(0.00):oldBinder.getMinPrem();
            int bPrem=safe.compareTo(binder.getMinPrem());
            String bPrems=bPrem==0? "" : " Changed Premium From: "+oldBinder.getMinPrem()+" To: "+binder.getMinPrem();
            String bType=oldBinder.getBinType().equalsIgnoreCase("S")?"Self-Fund" :
                    oldBinder.getBinType().equalsIgnoreCase("B")?"Binder":"Open Market";
            String bTypes=bType.equalsIgnoreCase(binderType) ? "" : " Changed Binder Type From: "+bType+ " To: "+binderType;
            String bName=oldBinder.getBinName().equalsIgnoreCase(binder.getBinName()) ? "" : " Changed Binder Name From: "+ oldBinder.getBinName()+" To: "+binder.getBinName();
            message="Edited Binder Named:  "+ oldBinder.getBinName()+ "  Insured By:  "+ accountRepo.findOne(oldBinder.getAccount().getAcctId()).getShtDesc()
                    +bName+bTypes+bPrems+bProd+bCur+bRemarks+active+defs;

        }

        return message;
    }

    @Override
    public void logBinderDetailsCreate(BinderDetailsBean binderBean, HttpServletRequest request){
        String message="";


        for(Long binderDet:binderBean.getCoverTypes()){
            BindersDef bindersDef=bindersRepo.findOne(binderBean.getBindCode());
            String binder=bindersDef.getBinName();
            String insurance=bindersDef.getAccount().getName();
            SubclassCoverTypes coverTypes=subClassCoverRepo.findOne(binderDet);
            String scv=coverTypes.getCoverTypes().getCovName();
            String cv=coverTypes.getSubclass().getSubDesc();
             message="Added Binder Details For Binder: "+binder+" Insured By: "+insurance +"Cover: "+cv+ " For Subclass: "+scv;

            String resource = "Log Binder Details Create";
            auditTrailLogger.log(message,request,resource);
        }

    }
    @Override
    public String logBinderDetailsUpdate(BinderDetails det){
        String message="";

        System.out.println("Old Binder");
        BinderDetails oldDet=binderDetRepo.findOne(det.getDetId());
        String binder= oldDet.getBinder().getBinName();
        String insurance=oldDet.getBinder().getAccount().getName();
        String distribution=oldDet.getDistribution()==null?"None":oldDet.getDistribution();
        String distributions=det.getDistribution()==null?"None":det.getDistribution();
        String pPerc=distribution.equalsIgnoreCase(distributions)?"":" Changed Percentage From: " + distribution+ " To: " + distributions;

        String iNo=oldDet.getInstallmentsNo()==det.getInstallmentsNo()? "" : " Changed No. of Installments From:"+oldDet.getInstallmentsNo()+" To: "+det.getInstallmentsNo();
        String sCvN=det.getSingleSectionCover()==null || det.getSingleSectionCover().isEmpty()?"off":det.getSingleSectionCover();
        sCvN=sCvN.equalsIgnoreCase("Y")&& !(sCvN.equals(""))?"on" : "off";

        String sCv=oldDet.getSingleSectionCover()==null || oldDet.getSingleSectionCover().isEmpty()?"off":oldDet.getSingleSectionCover();

        sCv=sCv.equalsIgnoreCase("Y")&& !(sCv.equals(""))?"on" : "off";
        String sCvs=sCv.equalsIgnoreCase(sCvN) ? "" : " Single selection From: "+sCv+" To: "+sCvN;
        BigDecimal oldPrem=oldDet.getMinPrem()==null?new BigDecimal(0.00):oldDet.getMinPrem();
        BigDecimal newPrem=det.getMinPrem()==null?new BigDecimal(0.00):det.getMinPrem();
        int bDec=oldPrem.compareTo(newPrem);
        String sAllN=det.getLimitsAllowed()==null || det.getLimitsAllowed().isEmpty()?"off":det.getLimitsAllowed();
        sAllN=sAllN.equalsIgnoreCase("Y")&& !(sAllN.equals(""))?"on" : "off";
        String sAll=oldDet.getLimitsAllowed()==null || oldDet.getLimitsAllowed().isEmpty()?"off":oldDet.getLimitsAllowed();
        sAll=sAll.equalsIgnoreCase("Y")&& !(sAll.equals(""))?"on" : "off";
        String sAlls=sAll.equalsIgnoreCase(sAllN)? "" : " Limit allowed From: "+sAll+" To: "+sAllN;
        String mPrem=bDec==0?"" : " Changed Premium From: "+oldDet.getMinPrem()+" To: "+det.getMinPrem();
        String cSum=oldDet.getRemarks()==null || oldDet.getRemarks().isEmpty()?"No Remarks":oldDet.getRemarks();
        cSum=cSum.equalsIgnoreCase(det.getRemarks()) ? "" : " Changed Remarks From: "+cSum+" To: "+det.getRemarks();
        message="Edited Binder Details for Binder :"+oldDet.getBinder().getBinShtDesc()+" Insured By: "+insurance+
                " For SubClass Cover Name: "+oldDet.getSubCoverTypes().getSubclass().getSubDesc()+
                cSum+mPrem+sAlls+sCvs+iNo+pPerc;

        return message;
    }
      @Override
      public String logPremiumRates(PremRatesDef rates){
          boolean newPremRates = rates.getId()==null?true:false;
          String message="";

          if(newPremRates){

              BinderDetails binderDetails=binderDetRepo.findOne(rates.getBinderDet().getDetId());
              String insurance=binderDetails.getBinder().getAccount().getName();
              String binder= binderDetails.getBinder().getBinName() == null ? rates.getBinderDet().getBinder().getBinName() : binderDetails.getBinder().getBinName();
              String rApplic=rates.getRangeApplicable()==null?"None":rates.getRangeApplicable();
              String pFulls=rates.getProratedFull().equalsIgnoreCase("P")?"Prorated":"Full";
              String mandatoryrates=rates.getMandatory()==null?"":rates.getMandatory();

              String aRPP=rates.getRangeApplicable()==null?"":rates.getRangeApplicable();
              aRPP=aRPP.equalsIgnoreCase("Y")?"True":"False";
              String rType1=rates.getRangeType()==null?"None":rates.getRangeType();
              rType1=rType1.equalsIgnoreCase("AG")?"Age":rType1.equalsIgnoreCase("AM")?"Amount":rType1.equalsIgnoreCase("TN")?"Tonnage":"None";


              message="Created new Rates for Binder: "+binder
                      +" Insured By: "+insurance
                      +" For Premium Item: "+rates.getSection().getDesc()
                      +" Range Applicable: "+rApplic+" Rate Type: "+rType1
                      +" Rate: "+rates.getRate()+" Free Limit: "+rates.getFreeLimit()
                      +" Prorated Full: "+ pFulls
                      +" Premium: "+rates.getMinPremium()
                      +" Mandatory: "+mandatoryrates;

          }else{
              PremRatesDef oldDef=premRatesRepo.findOne(rates.getId());
              BinderDetails binderDetails=binderDetRepo.findOne(oldDef.getBinderDet().getDetId());
              String insurance=binderDetails.getBinder().getAccount().getName();
              String binder=binderDetails.getBinder().getBinName();

              String rType="";
              String rFrom="";
              String rTo="";
              String rAppl=oldDef.getRangeApplicable()==null?"False":oldDef.getRangeApplicable();
              rAppl=rAppl.equalsIgnoreCase("Y")?"True":"False";
              String rA=rAppl;
              String aRPP=rates.getRangeApplicable()==null?"False":rates.getRangeApplicable();
              aRPP=aRPP.equalsIgnoreCase("Y")?"True":"False";
              String rAppls=rAppl.equalsIgnoreCase(aRPP)?"":
                      " Changed Rate Applicable From: " +rAppl+" To: "+
                              aRPP;
              String rType1=rates.getRangeType()==null?"None":rates.getRangeType();
              rType1=rType1.equalsIgnoreCase("AG")?"Age":rType1.equalsIgnoreCase("AM")?"Amount":rType1.equalsIgnoreCase("TN")?"Tonnage":"None";
              rType=oldDef.getRangeType()==null?"None":oldDef.getRangeType();
              rType=rType.equalsIgnoreCase("AG")?"Age":rType.equalsIgnoreCase("AM")?"Amount":rType1.equalsIgnoreCase("TN")?"Tonnage":"None";

              String rTypes=rType.equalsIgnoreCase(rType1)?"" :
                      " Changed Range Type From: "+rType+" To: "+rType1;
              BigDecimal bdcf=rates.getRangeFrom()==null?new BigDecimal(0):rates.getRangeFrom();
              BigDecimal bdc=oldDef.getRangeFrom()==null?new BigDecimal(0):oldDef.getRangeFrom();
              int rF=bdc.compareTo(bdcf);
              rFrom=rF==0?"" : " Changed Range From From: "+oldDef.getRangeFrom()+ " To: "+rates.getRangeFrom();
              BigDecimal bdcl=rates.getRangeTo()==null?new BigDecimal(0):rates.getRangeTo();
              BigDecimal bdcs=oldDef.getRangeTo()==null?new BigDecimal(0):oldDef.getRangeTo();
              int rT=bdcs.compareTo(bdcl);
              rTo=rT==0?"" : " Changed Range To From: " +oldDef.getRangeTo()+" To: "+rates.getRangeTo();

              BigDecimal myRates=oldDef.getDivFactor()==null?new BigDecimal(0):oldDef.getDivFactor();
              int myRate1=myRates.compareTo(new BigDecimal(1000));
              int myRate2=myRates.compareTo(new BigDecimal(100));
              int myRate3=myRates.compareTo(new BigDecimal(1));
              BigDecimal myRatesF=rates.getDivFactor()==null?new BigDecimal(0):rates.getDivFactor();
              int myRates1=myRatesF.compareTo(new BigDecimal(1000));
              int myRates2=myRatesF.compareTo(new BigDecimal(100));
              int myRates3=myRatesF.compareTo(new BigDecimal(1));
              String rateL=myRate1==0?"Per Mile":myRate2==0?"Percentage":myRate3==0?"Ammount":"Undefined";
              String rateP=myRates1==0?"Per Mile":myRates2==0?"Percentage":myRates3==0?"Ammount":"Undefined";

              String rateT=rateL.equalsIgnoreCase(rateP)?"":" Changed Div Factor from: "+rateL+" To: "+rateP;

              int rRate=oldDef.getRate().compareTo(rates.getRate());
              String rRates=rRate==0?"" : " Changed Rates From: "+oldDef.getRate()+" To: "+rates.getRate();
              int fLimits=oldDef.getFreeLimit().compareTo(rates.getFreeLimit());
              String fLimit=fLimits==0?"" : " Changed Free Limit From: "+oldDef.getFreeLimit()+" To: "+rates.getFreeLimit();
              String fpRates=rates.getProratedFull().equals("P")?"Prorated":"Full";
              String pFull=oldDef.getProratedFull().equals("P")?"Prorated":"Full";
              String  p=pFull;
              String pFullP=pFull.equalsIgnoreCase(fpRates)?"": " Prorated Full From: "+
                      pFull+" To: "+fpRates;
              int mPrem=oldDef.getMinPremium().compareTo(rates.getMinPremium());
              String mPrems=mPrem==0?"" : " Changed Premium From: "+oldDef.getMinPremium()+" To: "+rates.getMinPremium();
              String mandatoryrates=rates.getMandatory()==null||rates.getMandatory().isEmpty()?"off":"on";
             // mandatoryrates=mandatoryrates.equals("Y")?"on":mandatoryrates.equalsIgnoreCase("N")?"off":"off";
              String mandatory=oldDef.getMandatory()==null?"":oldDef.getMandatory();
              mandatory=mandatory.equalsIgnoreCase("Y")?"on":"off";
              String mandatorys=mandatory.equalsIgnoreCase(mandatoryrates)?"" : " Changed  Mandatory From: "
                      +mandatory+" To: "+mandatoryrates;
              message="Edited Rates for Binder: "+binder+" Insured By: "+insurance+" For Premium Item: "+oldDef.getSection().getDesc()
                      +rAppls+rTypes+rFrom+rTo+rateT+rRates+fLimit+pFullP+mPrems+mandatorys;

          }
          return message;
      }
      @Override
      public String logCommissionRates(CommissionRates rates){
          Boolean newComm=rates.getCommId()==null?true:false;
          String message="";
          if(newComm){
              //BinderDetails binderDetails=binderDetRepo.findOne(rates.getBinderDet().getDetId());
              //String insurance=binderDetails.getBinder().getAccount().getName();
              String rType=rates.getRateType().equalsIgnoreCase("P")?"Percentage":
                      rates.getRateType().equalsIgnoreCase("M")?"Per Mile":
                              rates.getRateType().equalsIgnoreCase("A")?"Ammount":"Undefined";
              String rItem=rates.getRevenueItems().getProdGroup().getSubDesc()==null?"None":rates.getRevenueItems().getProdGroup().getSubDesc();
              message="Created Commission Rates for Binder:"+rates.getBindersDef().getBinName()+" Insured By: "+rates.getBindersDef().getAccount().getName()+
                      " With Range From: "+rates.getCommRangeFrom()+" To: "+rates.getCommRangeTo()+" Rate Type: "+rType+
                      " Div Factor: "+rates.getCommDivFactor()+" Active: "+rates.isActive()+" For Revenue Item: "+rItem;
          }
          else{
              CommissionRates oldRates=commRatesRepo.findOne(rates.getCommId());
              BigDecimal cDFO=oldRates.getCommDivFactor()==null?new BigDecimal(0):oldRates.getCommDivFactor();
              BigDecimal cDF=rates.getCommDivFactor()==null?new BigDecimal(0):rates.getCommDivFactor();
              int cdf=cDFO.compareTo(cDF);
              String div=cdf==0?"":" Changed Div Factor From: "+cDFO+" To: "+cDF;
              BigDecimal rTO=oldRates.getCommRate()==null?new BigDecimal(0):oldRates.getCommRate();
              BigDecimal rT=rates.getCommRate()==null?new BigDecimal(0):rates.getCommRate();
              int rate=rTO.compareTo(rT);
              String myRate=rate==0?"":" Changed Rate From: "+rTO+" To: "+rT;
              BigDecimal rfR=oldRates.getCommRangeFrom()==null?new BigDecimal(0):oldRates.getCommRangeFrom();
              BigDecimal rF=rates.getCommRangeFrom()==null?new BigDecimal(0):rates.getCommRangeFrom();
              int rateFrom=rfR.compareTo(rF);
              String myFrom=rateFrom==0?"":" Changed Range From: "+rfR+" To: "+rF;
              BigDecimal rtO=oldRates.getCommRangeTo()==null?new BigDecimal(0):oldRates.getCommRangeTo();
              BigDecimal rO=rates.getCommRangeTo()==null?new BigDecimal(0):rates.getCommRangeTo();
              int mTo=rtO.compareTo(rO);
              String myTo=mTo==0?"":" Changed Range To From: "+rtO+" To: "+rO;
              String isActiveN=rates.isActive()?" Active: on":" Active: off";
              String isActive=oldRates.isActive()?" Active: on":" Active: off";
              String finalActive=isActiveN.equalsIgnoreCase(isActive)?"":" Changed "+isActive+" To "+isActiveN;
              String rType=rates.getRateType().equalsIgnoreCase("P")?"Percentage":
                      rates.getRateType().equalsIgnoreCase("M")?"Per Mile":
                              rates.getRateType().equalsIgnoreCase("A")?"Ammount":"Undefined";
              String rTypeO=oldRates.getRateType().equalsIgnoreCase("P")?"Percentage":
                      oldRates.getRateType().equalsIgnoreCase("M")?"Per Mile":
                              oldRates.getRateType().equalsIgnoreCase("A")?"Ammount":"Undefined";
              String rTypes=rTypeO.equalsIgnoreCase(rType)?"":" Changed Rate Type From: "+rTypeO+" To: "+ rType;
              String rItem=oldRates.getRevenueItems().getProdGroup().getSubDesc()==null?"None":oldRates.getRevenueItems().getProdGroup().getSubDesc();
              String rItemsN=rates.getRevenueItems().getProdGroup().getSubDesc()==null?"None":rates.getRevenueItems().getProdGroup().getSubDesc();
              String item=rItem.equalsIgnoreCase(rItemsN)?"":" Changed Revenue item From: "+rItem+" To: "+
                      rItemsN;
              message="Edited Commission Rates for Binder:"+oldRates.getBindersDef().getBinName()+" Insured By: "+oldRates.getBindersDef().getAccount().getName()+
                      " For Revenue Item: "+rItem+myFrom+myTo+rTypes+myRate+div+item+finalActive;

          }
          return message;
      }

    @Override
    public String logSubAgentCommissionRates(SubAgentCommissionRates rates){
        System.out.println("Rates now" +rates);
        Boolean newComm=rates.getCommId()==null?true:false;
        String message="";
        if(newComm){
            //BinderDetails binderDetails=binderDetRepo.findOne(rates.getBinderDet().getDetId());
            //String insurance=binderDetails.getBinder().getAccount().getName();
            String rType=rates.getRateType().equalsIgnoreCase("P")?"Percentage":
                    rates.getRateType().equalsIgnoreCase("M")?"Per Mile":
                            rates.getRateType().equalsIgnoreCase("A")?"Ammount":"Undefined";
            String rItem=rates.getAccountTypes().getAccId().toString()==null?"None":rates.getAccountTypes().getAccId().toString();
            message="Created Sub Agent Commission Rates for Binder:"+rates.getBindersDef().getBinName()+" Insured By: "+rates.getBindersDef().getAccount().getName()+
                    " With Range From: "+rates.getCommRangeFrom()+" To: "+rates.getCommRangeTo()+" Rate Type: "+rType+
                    " Div Factor: "+rates.getCommDivFactor()+" Active: "+rates.isActive()+" For Account Type: "+rItem;
        }
        else{
            SubAgentCommissionRates oldRates=subAgentCommRepo.findOne(rates.getCommId());
            BigDecimal cDFO=oldRates.getCommDivFactor()==null?new BigDecimal(0):oldRates.getCommDivFactor();
            BigDecimal cDF=rates.getCommDivFactor()==null?new BigDecimal(0):rates.getCommDivFactor();
            int cdf=cDFO.compareTo(cDF);
            String div=cdf==0?"":" Changed Div Factor From: "+cDFO+" To: "+cDF;
            BigDecimal rTO=oldRates.getCommRate()==null?new BigDecimal(0):oldRates.getCommRate();
            BigDecimal rT=rates.getCommRate()==null?new BigDecimal(0):rates.getCommRate();
            int rate=rTO.compareTo(rT);
            String myRate=rate==0?"":" Changed Rate From: "+rTO+" To: "+rT;
            BigDecimal rfR=oldRates.getCommRangeFrom()==null?new BigDecimal(0):oldRates.getCommRangeFrom();
            BigDecimal rF=rates.getCommRangeFrom()==null?new BigDecimal(0):rates.getCommRangeFrom();
            int rateFrom=rfR.compareTo(rF);
            String myFrom=rateFrom==0?"":" Changed Range From: "+rfR+" To: "+rF;
            BigDecimal rtO=oldRates.getCommRangeTo()==null?new BigDecimal(0):oldRates.getCommRangeTo();
            BigDecimal rO=rates.getCommRangeTo()==null?new BigDecimal(0):rates.getCommRangeTo();
            int mTo=rtO.compareTo(rO);
            String myTo=mTo==0?"":" Changed Range To From: "+rtO+" To: "+rO;
            String isActiveN=rates.isActive()?" Active: on":" Active: off";
            String isActive=oldRates.isActive()?" Active: on":" Active: off";
            String finalActive=isActiveN.equalsIgnoreCase(isActive)?"":" Changed "+isActive+" To "+isActiveN;
            String rType=rates.getRateType().equalsIgnoreCase("P")?"Percentage":
                    rates.getRateType().equalsIgnoreCase("M")?"Per Mile":
                            rates.getRateType().equalsIgnoreCase("A")?"Ammount":"Undefined";
            String rTypeO=oldRates.getRateType().equalsIgnoreCase("P")?"Percentage":
                    oldRates.getRateType().equalsIgnoreCase("M")?"Per Mile":
                            oldRates.getRateType().equalsIgnoreCase("A")?"Ammount":"Undefined";
            String rTypes=rTypeO.equalsIgnoreCase(rType)?"":" Changed Rate Type From: "+rTypeO+" To: "+ rType;
            String rItem=oldRates.getAccountTypes().getAccId()==null?"None":oldRates.getAccountTypes().getAccId().toString();
            String rItemsN=rates.getAccountTypes().getAccId()==null?"None":rates.getAccountTypes().getAccId().toString();
            String item=rItem.equalsIgnoreCase(rItemsN)?"":" Changed Revenue item From: "+rItem+" To: "+
                    rItemsN;
            message="Edited Sub Agent Commission Rates for Binder:"+oldRates.getBindersDef().getBinName()+" Insured By: "+oldRates.getBindersDef().getAccount().getName()+
                    " For Account Type: "+rItem+myFrom+myTo+rTypes+myRate+div+item+finalActive;

        }
        return message;
    }
      @Override
      public String logLifeCommissionRates(LifeCommissionRates rates){
          Boolean newComm=rates.getCommId()==null?true:false;
          String message="";
          if(newComm){
              String frequency=rates.getFrequency().equalsIgnoreCase("M")?"Monthly":
                      rates.getFrequency().equalsIgnoreCase("S")?"Semi Quartely":
                              rates.getFrequency().equalsIgnoreCase("A")?"Annually":
                                      rates.getFrequency().equalsIgnoreCase("Q")?"Quartely":
                                              "Undefined";
              message="Created Commission rates for Binder: "+rates.getBinderDef().getBinName()+" Insured By: "+rates.getBinderDef().getAccount().getName()+
                      " With Term From: "+rates.getCommTermFrom()+" To: "+rates.getCommTermTo()+" Year From: "+
                      rates.getCommYearFrom()+" Year To: "+rates.getCommYearTo()+" Rate: "+rates.getCommRate()+" Frequency: "+
                      frequency;
          }else{
              LifeCommissionRates oldRates=lifeCommissionRatesRepo.findOne(rates.getCommId());
              BigDecimal cDFO=oldRates.getCommDivFactor()==null?new BigDecimal(0):oldRates.getCommDivFactor();
              BigDecimal cDF=rates.getCommDivFactor()==null?new BigDecimal(0):rates.getCommDivFactor();
              int cdf=cDFO.compareTo(cDF);
              String div=cdf==0?"":" Changed Div Factor From: "+cDFO+" To: "+cDF;
              BigDecimal rTO=oldRates.getCommRate()==null?new BigDecimal(0):oldRates.getCommRate();
              BigDecimal rT=rates.getCommRate()==null?new BigDecimal(0):rates.getCommRate();
              int rate=rTO.compareTo(rT);
              String myRate=rate==0?"":" Changed Rate From: "+rTO+" To: "+rT;
              String tFrom=oldRates.getCommTermFrom()==rates.getCommTermFrom()?"":" Changed Term From: "+oldRates.getCommTermFrom()+
                      " To: "+rates.getCommTermFrom();
              String tTo=oldRates.getCommTermTo()==rates.getCommTermTo()?"":" Changed Term To From: "+oldRates.getCommTermTo()+
                      " To: "+rates.getCommTermTo();
              String yFrom=oldRates.getCommYearFrom()==rates.getCommYearFrom()?"":" Changed Year From: "+oldRates.getCommYearFrom()+
                      " To: "+rates.getCommYearFrom();
              String yTo=oldRates.getCommYearTo()==rates.getCommYearTo()?"":" Changed Year To From: "+oldRates.getCommYearTo()+
                      " To: "+rates.getCommYearTo();
              String dateFromo=oldRates.getWefDate().toString();
              String dateFrom=rates.getWefDate().toString();
              String date=dateFromo.equalsIgnoreCase(dateFrom)?"":" Changed Date From: "+dateFromo+" To: "+
                      dateFrom;
              String frequencyo=oldRates.getFrequency().equalsIgnoreCase("M")?"Monthly":
                      oldRates.getFrequency().equalsIgnoreCase("S")?"Semi Quartely":
                              oldRates.getFrequency().equalsIgnoreCase("A")?"Annually":
                                      oldRates.getFrequency().equalsIgnoreCase("Q")?"Quartely":
                                              "Undefined";
              String frequency=rates.getFrequency().equalsIgnoreCase("M")?"Monthly":
                      rates.getFrequency().equalsIgnoreCase("S")?"Semi Quartely":
                              rates.getFrequency().equalsIgnoreCase("A")?"Annually":
                                      rates.getFrequency().equalsIgnoreCase("Q")?"Quartely":
                                              "Undefined";
              String pFreq=frequencyo.equalsIgnoreCase(frequency)?"":" Changed Payment Frequency From: "+frequencyo+
                      " To: "+frequency;

              message="Edited Commission rates for Binder: "+oldRates.getBinderDef().getBinName()+" Insured By: "+oldRates.getBinderDef().getAccount().getName()+
                      tFrom+tTo+yFrom+yTo+myRate+date+pFreq;

          }
          return message;
      }

    @Override
    public String logLifeSubAgentCommissionRates(LifeSubAgentCommissionRates rates){
        Boolean newComm=rates.getCommId()==null?true:false;
        String message="";
        if(newComm){
            String frequency=rates.getFrequency().equalsIgnoreCase("M")?"Monthly":
                    rates.getFrequency().equalsIgnoreCase("S")?"Semi Quartely":
                            rates.getFrequency().equalsIgnoreCase("A")?"Annually":
                                    rates.getFrequency().equalsIgnoreCase("Q")?"Quartely":
                                            "Undefined";
            message="Created Commission rates for Binder: "+rates.getBinderDef().getBinName()+" Insured By: "+rates.getBinderDef().getAccount().getName()+
                    " With Term From: "+rates.getCommTermFrom()+" To: "+rates.getCommTermTo()+" Rate: "+rates.getCommRate()+" Frequency: "+
                    frequency;
        }else{
            LifeSubAgentCommissionRates oldRates=lifeSubAgentCommissionRatesRepo.findOne(rates.getCommId());
            BigDecimal cDFO=oldRates.getCommDivFactor()==null?new BigDecimal(0):oldRates.getCommDivFactor();
            BigDecimal cDF=rates.getCommDivFactor()==null?new BigDecimal(0):rates.getCommDivFactor();
            int cdf=cDFO.compareTo(cDF);
            String div=cdf==0?"":" Changed Div Factor From: "+cDFO+" To: "+cDF;
            BigDecimal rTO=oldRates.getCommRate()==null?new BigDecimal(0):oldRates.getCommRate();
            BigDecimal rT=rates.getCommRate()==null?new BigDecimal(0):rates.getCommRate();
            int rate=rTO.compareTo(rT);
            String myRate=rate==0?"":" Changed Rate From: "+rTO+" To: "+rT;
            String tFrom=oldRates.getCommTermFrom()==rates.getCommTermFrom()?"":" Changed Term From: "+oldRates.getCommTermFrom()+
                    " To: "+rates.getCommTermFrom();
            String tTo=oldRates.getCommTermTo()==rates.getCommTermTo()?"":" Changed Term To From: "+oldRates.getCommTermTo()+
                    " To: "+rates.getCommTermTo();
            String dateFromo=oldRates.getWefDate().toString();
            String dateFrom=rates.getWefDate().toString();
            String date=dateFromo.equalsIgnoreCase(dateFrom)?"":" Changed Date From: "+dateFromo+" To: "+
                    dateFrom;
            String frequencyo=oldRates.getFrequency().equalsIgnoreCase("M")?"Monthly":
                    oldRates.getFrequency().equalsIgnoreCase("S")?"Semi Quartely":
                            oldRates.getFrequency().equalsIgnoreCase("A")?"Annually":
                                    oldRates.getFrequency().equalsIgnoreCase("Q")?"Quartely":
                                            "Undefined";
            String frequency=rates.getFrequency().equalsIgnoreCase("M")?"Monthly":
                    rates.getFrequency().equalsIgnoreCase("S")?"Semi Quartely":
                            rates.getFrequency().equalsIgnoreCase("A")?"Annually":
                                    rates.getFrequency().equalsIgnoreCase("Q")?"Quartely":
                                            "Undefined";
            String pFreq=frequencyo.equalsIgnoreCase(frequency)?"":" Changed Payment Frequency From: "+frequencyo+
                    " To: "+frequency;

            message="Edited Commission rates for Binder: "+oldRates.getBinderDef().getBinName()+" Insured By: "+oldRates.getBinderDef().getAccount().getName()+
                    tFrom+tTo+myRate+date+pFreq;

        }
        return message;
    }

      @Override
      public void logBinderSectPerils(BinderPerilsBean perilsBean,HttpServletRequest request){
          for(Long peril:perilsBean.getPerils()) {
              String message = "";
              Long bDetails = perilsBean.getBinderDetail();
              BinderDetails binderDetails = binderDetRepo.findOne(bDetails);
              SubclassPerils subclassPerils=subClassPerilsRepo.findOne(peril);
              String binder = binderDetails.getBinder().getBinName();
              Long sectId = perilsBean.getSectId();
              SectionsDef sectionsDef = sectionRepo.findOne(sectId);
              String sect = sectionsDef.getDesc();
              message = "Created Peril: "+subclassPerils.getPeril().getPerilDesc()+ " For Binder: " + binder +"Insured By: "+binderDetails.getBinder().getAccount().getName()+ " With Binder Details: " + binderDetails.getSubCoverTypes().getCoverTypes().getCovName() + " For Section: " + sect;

              String resource = "Log Binder Sect Perils";
              auditTrailLogger.log(message, request, resource);
          }
      }
      @Override
      public void logBinderRequiredDocs(RequiredDocsBean docsBean,HttpServletRequest request){
          for(Long docs:docsBean.getRequiredDocs()){
              SubClassReqdDocs subClassReqdDocs=subclassReqDocRepo.findOne(docs);
              BinderDetails binderDet=binderDetRepo.findOne(docsBean.getBinderDetail());
              String binder=binderDet.getBinder().getBinName();
              String insurance=binderDet.getBinder().getAccount().getName();

              String message="";

              String details =subClassReqdDocs.getRequiredDoc().getReqDesc();
              String cover = binderDet.getSubCoverTypes().getCoverTypes().getCovName();
              message = "Added required documents : "+details+" For Binder: "+binder+" Insured By: "+insurance+" For Subclass: " + cover;

              String resource = "Log Binder Required Docs";

              auditTrailLogger.log(message,request, resource);

          }
      }
      @Override
      public String logUpdateBinderRequiredDocs(BinderReqrdDocs reqrdDocs){
          BinderReqrdDocs binderReqrdDocs=binderReqrdDocsRepo.findOne(reqrdDocs.getBrdId());
          String message="";
          int mandatory=Boolean.compare(reqrdDocs.isMandatory(),binderReqrdDocs.isMandatory());
          String man=mandatory==0?" No Changes made":" Set mandatory From "+binderReqrdDocs.isMandatory()+" To:"+reqrdDocs.isMandatory();
          message="Edited required document: "+reqrdDocs.getRequiredDocs().getRequiredDoc().getReqDesc()+" For Binder: "+binderReqrdDocs.getBinderDetail().getBinder().getBinShtDesc()+" Insured By: "+binderReqrdDocs.getBinderDetail().getBinder().getAccount().getName()+man;
          return message;
      }
      @Override
      public String logDeleteBinder(Long binCode){
          BindersDef bindersDef=bindersRepo.findOne(binCode);
          String insurance=accountRepo.findOne(bindersDef.getAccount().getAcctId()).getShtDesc();
          String binderName=bindersDef.getBinName();
          String message="Deleted Binder: "+binderName+" Insured By: "+insurance;
          return message;
      }
      @Override
      public String logDelBinderDetails(Long detId){
          BinderDetails binder=binderDetRepo.findOne(detId);
          String bDetail=binder.getBinder().getBinName();
          String insurance=binder.getBinder().getAccount().getName();
          String cover=binder.getSubCoverTypes().getCoverTypes().getCovName();
          String subClass=binder.getSubCoverTypes().getSubclass().getSubDesc();
          String message="Deleted Binder Details For: "+bDetail+" Insured by: "+insurance+" For Cover: "+cover+" For Subclass: "+
                  subClass;
          return message;
      }
      @Override
      public String logDelPremRates(Long detId){
          PremRatesDef premRatesDef=premRatesRepo.findOne(detId);
          String premiumItem=premRatesDef.getSection().getDesc();
          String binder=premRatesDef.getBinderDet().getBinder().getBinName();
          String insurance=premRatesDef.getBinderDet().getBinder().getAccount().getName();
          String message="Deleted Rates For Premium Item: "+ premiumItem+" For Binder: "+binder+" Insured By: "+insurance;
          return message;
      }
      @Override
      public String logDelCommissionRates(Long commCode){
          CommissionRates commissionRates=commRatesRepo.findOne(commCode);
          String binder=commissionRates.getBindersDef().getBinName();
          String message="Deleted Commission Rates For Binder: "+binder+" Insured By: "+commissionRates.getBindersDef().getAccount().getName();
          return message;
      }

    @Override
    public String logDelSubAgentCommissionRates(Long commCode){
        SubAgentCommissionRates subAgentCommissionRates=subAgentCommRepo.findOne(commCode);
        String binder=subAgentCommissionRates.getBindersDef().getBinName();
        String message="Deleted Sub Agent Commission Rates For Binder: "+binder+" Insured By: "+subAgentCommissionRates.getBindersDef().getAccount().getName();
        return message;
    }

      @Override
      public String logDelLifeCommissionRates(Long commCode){
          LifeCommissionRates lifeCommissionRates=lifeCommissionRatesRepo.findOne(commCode);
          String binder=lifeCommissionRates.getBinderDef().getBinName();
          String message="Deleted Life Commission Rates For Binder: "+binder+" Insured By: "+lifeCommissionRates.getBinderDef().getAccount().getName();;
          return message;
      }

    @Override
    public String logDelSubAgentLifeCommissionRates(Long commCode){
        LifeSubAgentCommissionRates lifeSubAgentCommissionRates=lifeSubAgentCommissionRatesRepo.findOne(commCode);
        String binder=lifeSubAgentCommissionRates.getBinderDef().getBinName();
        String message="Deleted Sub Agent Life Commission Rates For Binder: "+binder+" Insured By: "+lifeSubAgentCommissionRates.getBinderDef().getAccount().getName();;
        return message;
    }

      @Override
      public String logDelBinderRequiredDocs(Long docId){
          BinderReqrdDocs binderReqrdDocs=binderReqrdDocsRepo.findOne(docId);
          String details=binderReqrdDocs.getRequiredDocs().getRequiredDoc().getReqDesc();
          String cover=binderReqrdDocs.getBinderDetail().getSubCoverTypes().getCoverTypes().getCovName();
          String message="Deleted Required Document: "+details+" For Cover: "+cover+" For Binder: "+binderReqrdDocs.getBinderDetail().getBinder().getBinName()+
                  " Insured By: "+binderReqrdDocs.getBinderDetail().getBinder().getAccount().getName();
          return message;
      }

    @Override
    public String logMakeReady(Long id) {
        BindersDef binder=bindersRepo.findOne(id);
        String myBinder=binder.getBinName();
        String insurance=binder.getAccount().getName();
        String type=binder.getBinType().equalsIgnoreCase("B")?"Binder":
                binder.getBinType().equalsIgnoreCase("M")?"Open Market":
                        binder.getBinType().equalsIgnoreCase("S")?"Self Fund":"Unknown";

        String message=" Made Ready Binder: "+myBinder+" insured by: "+insurance+" of type: "+type;
        return  message;
    }

    @Override
    public String logAuthorise(Long id) {
        BindersDef binder=bindersRepo.findOne(id);
        String myBinder=binder.getBinName();
        String insurance=binder.getAccount().getName();
        String type=binder.getBinType().equalsIgnoreCase("B")?"Binder":
                binder.getBinType().equalsIgnoreCase("M")?"Open Market":
                        binder.getBinType().equalsIgnoreCase("S")?"Self Fund":"Unknown";

        String message=" Authorised Binder: "+myBinder+" insured by: "+insurance+" of type: "+type;
        return  message;
    }

    @Override
    public String logUndoBinder(Long id) {
        BindersDef binder=bindersRepo.findOne(id);
        String myBinder=binder.getBinName();
        String insurance=binder.getAccount().getName();
        String type=binder.getBinType().equalsIgnoreCase("B")?"Binder":
                binder.getBinType().equalsIgnoreCase("M")?"Open Market":
                binder.getBinType().equalsIgnoreCase("S")?"Self Fund":"Unknown";

        String message=" Made Undo Make-Ready For Binder: "+myBinder+" insured by: "+insurance+" of type: "+type;
        return  message;
    }

    @Override
    public String logUnAuthorise(Long id) {
        BindersDef binder=bindersRepo.findOne(id);
        String myBinder=binder.getBinName();
        String insurance=binder.getAccount().getName();
        String type=binder.getBinType().equalsIgnoreCase("B")?"Binder":
                binder.getBinType().equalsIgnoreCase("M")?"Open Market":
                        binder.getBinType().equalsIgnoreCase("S")?"Self Fund":"Unknown";

        String message="UnAuthorised Binder: "+myBinder+" insured by: "+insurance+" of type: "+type;
        return  message;
    }
}
