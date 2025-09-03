package com.brokersystems.brokerapp.accounts.utils;

import com.brokersystems.brokerapp.accounts.model.*;
import com.brokersystems.brokerapp.accounts.repository.*;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.DateUtilities;
import com.brokersystems.brokerapp.server.utils.Streamable;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.model.OrgBranch;
import com.brokersystems.brokerapp.setup.repository.OrgBranchRepository;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccountsPostingUtilities {

    @Autowired
    private FinalReportFormatsRepo finalReportFormatsRepo;
    @Autowired
    private AccountsBusinessClassesRepo accountsBusinessClassesRepo;
    @Autowired
    private AccountYearPeriodsRepo accountYearPeriodsRepo;
    @Autowired
    private OrgBranchRepository branchRepository;
    @Autowired
    private TrialBalancesRepo trialBalancesRepo;
    @Autowired
    private ReportProcessedValuesRepo reportProcessedValuesRepo;
    @Autowired
    private OpeningBalancesRepo openingBalancesRepo;

    @Autowired
    private TrialBalanceTransRepo trialBalanceTransRepo;
    @Autowired
    private UserUtils userUtils;

    public Date getDefaultDate(Date transDate, Long branchCode, Long year) throws BadRequestException {
        if(transDate==null){
            throw new BadRequestException("Please provide Transaction Date...");
        }
        final String period = new SimpleDateFormat("MMMM").format(transDate);
        List<Object[]> periods = accountYearPeriodsRepo.findMonthlyAcctPeriod(period,branchCode,year);
        if(periods.isEmpty()) throw new BadRequestException("Unable to get Active Accounting period for the selected Date...");
        for(Object[] per: periods){
            Date wefDate = (Date) per[0];
            Date wetDate = (Date) per[1];
            Date currDate = new Date();
            if(currDate.after(wefDate) && currDate.before(wetDate)){
                return currDate;
            }
            else {
                if(wetDate.before(currDate)){
                    return wetDate;
                }
                else{
                    return wefDate;
                }
            }
        }
        return null;
    }

    public void processFinalReports(Date fromDate,
                                    Date toDate,
                                    String quarterType,
                                    String showClasses,
                                    String acctPeriod,
                                    String rptName) throws BadRequestException {

        if(rptName==null){
            throw new BadRequestException("Select a Valid Report....");
        }
        FinalReportTypes reportTypes = FinalReportTypes.valueOf(rptName);
        if(fromDate==null && toDate==null){
            throw new BadRequestException("Select Date Range to Generate final Report");
        }
        Calendar calendarDate = Calendar.getInstance();
        calendarDate.setTime(toDate);
        final Long headOfficeId = branchRepository.findHeadOffice();
        final Integer year = Integer.valueOf( new SimpleDateFormat("yyyy").format(fromDate));
        String firstPeriod = accountYearPeriodsRepo.firstPeriodOfYr(headOfficeId, Long.valueOf(year));
        final String datePeriod = new SimpleDateFormat("MMMM").format(toDate);
        calendarDate.set(Calendar.DAY_OF_MONTH, calendarDate.getActualMaximum(Calendar.DAY_OF_MONTH));
        final Date endPeriod = calendarDate.getTime();

        if(quarterType!=null) {
            final QuarterDates quarterDates = getQuarterDates(quarterType, calendarDate.getWeekYear());
            if(quarterDates==null){
                throw new BadRequestException("Invalid Quarter Type");
            }
            fromDate = quarterDates.fromDate;
            toDate = quarterDates.toDate;
        }
        if(reportTypes.isTb()){
            List<Long> recordsToDelete = trialBalancesRepo.findRecordsToDelete(userUtils.getCurrentUser().getId());
            List<TrialBalances> trialBalancesList = new ArrayList<>();
            for(Long record:recordsToDelete){
                trialBalancesList.add(trialBalancesRepo.findOne(record));
            }
            List<Long> recordsToDeletes = trialBalanceTransRepo.findRecordsToDelete(userUtils.getCurrentUser().getId());
            List<TrialBalanceTrans> trialBalancesLists = new ArrayList<>();
            for(Long record:recordsToDeletes){
                trialBalancesLists.add(trialBalanceTransRepo.findOne(record));
            }
            trialBalancesRepo.delete(trialBalancesList);
            trialBalanceTransRepo.delete(trialBalancesLists);
            final OrgBranch headOffice = branchRepository.findOne(headOfficeId);
            if(headOffice==null){
                throw new BadRequestException("Unable to get Head Office. Cannot continue...");
            }
            Date dateFrom = null;
            if(firstPeriod!=null){
                try {
                    dateFrom = new SimpleDateFormat("dd/MMM/yyyy").parse("01/"+firstPeriod+"/"+year);
                } catch (ParseException e) {
                    throw new BadRequestException("Unable to get valid Date from....");
                }
            }
             

        }
        final String cummulative = reportTypes.getCummulative();
        final Iterable<AccountsBusinessClasses> businessClassesList = accountsBusinessClassesRepo.findAll();
        final Iterable<FinalReportFormats> rptRows = finalReportFormatsRepo.findAll(QFinalReportFormats.finalReportFormats.reportTypes.eq(reportTypes)
                .and(QFinalReportFormats.finalReportFormats.type.notIn("TOTAL","FORMULAE")));
        List<FinalReportFormats> orderedRptRows = Streamable.streamOf(rptRows).sorted(Comparator.comparingInt(FinalReportFormats::getOrder))
                .collect(Collectors.toList());

        final Iterable<FinalReportFormats> formularRows = finalReportFormatsRepo.findAll(QFinalReportFormats.finalReportFormats.reportTypes.eq(reportTypes)
                .and(QFinalReportFormats.finalReportFormats.type.in("FORMULAE")));
        List<FinalReportFormats> orderedFormularRows = Streamable.streamOf(formularRows).sorted(Comparator.comparingInt(FinalReportFormats::getOrder))
                .collect(Collectors.toList());
        final String showAllMonths = reportTypes.getShowAllmonths();
        final String showEquity = reportTypes.getShowEquity();
        final Integer showPrevYears = reportTypes.getPrevYearsToShow();
        final String showMonthTotal = reportTypes.getShowMonthlyTotal();
        final String prevShowMonthTotal = reportTypes.getShowPrevMonthlyTotal();
        final String month = new SimpleDateFormat("MMMM").format(toDate);

        final Integer toYear = Integer.valueOf( new SimpleDateFormat("yyyy").format(toDate));
        final Date startOfYear = getStartOfYear(year);
        final Date endOfYear = getEndOfYear(year);
        if(showClasses!=null && showClasses.equalsIgnoreCase("Y")){
            Date finalFromDate = fromDate;
            Date finalToDate = toDate;
            orderedRptRows.forEach(a -> {
                Streamable.streamOf(businessClassesList).forEach(x -> {
                    try {
                        final BigDecimal rowTotal = getPrereportValue(x.getBacId(),year.longValue(), finalFromDate, finalToDate,a.getRfId(),reportTypes);
                        final BigDecimal prevrowTotal = getPrereportValue(x.getBacId(),year.longValue()-1, DateUtils.addMonths(finalFromDate,-12),
                               DateUtils.addMonths(finalToDate,-12),a.getRfId(),reportTypes);
                        final ReportProcessedValues values = new ReportProcessedValues();
                        values.setRefAmount(rowTotal);
                        values.setRefPrevAmount(prevrowTotal);
                        values.setFinancialClasses(x);
                        values.setUser(userUtils.getCurrentUser());
                        reportProcessedValuesRepo.save(values);
                    } catch (BadRequestException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
        }

        

    }

    private BigDecimal getPrereportValue(Long claCode, Long year, Date dateFrom,Date dateTo, final Long rowId,
                                         FinalReportTypes reportTypes) throws BadRequestException {
        final Long headOfficeId = branchRepository.findHeadOffice();
        if(headOfficeId==null){
            throw new BadRequestException("Unable to get Head Office. Cannot continue...");
        }
        final OrgBranch headOffice = branchRepository.findOne(headOfficeId);
        if(headOffice==null){
            throw new BadRequestException("Unable to get Head Office. Cannot continue...");
        }
        String firstPeriod = accountYearPeriodsRepo.firstPeriodOfYr(headOfficeId,year);
        final String datePeriod = new SimpleDateFormat("MMMM").format(dateTo);
        final boolean isFirstMonth = (firstPeriod.equalsIgnoreCase(datePeriod));
        final FinalReportFormats reportFormats = finalReportFormatsRepo.findOne(rowId);
        if(reportFormats==null){
            throw new BadRequestException("Unable to get Final Report for the selected Row Id"+rowId+" Contact Admininstrator");
        }
        final String rowColum = reportFormats.getPickedFrom();
        BigDecimal opBal = BigDecimal.ZERO;
        if(rowColum.equalsIgnoreCase("OP-BAL")){
            opBal = getPrereportValueOpBal(claCode,year,dateFrom,dateTo,rowId,reportTypes);
        }
        else{

        }
        return opBal;
    }

    private BigDecimal getPrereportValueOpBal(Long claCode,Long year, Date dateFrom,Date dateTo, final Long rowId,
                                              FinalReportTypes reportTypes) throws BadRequestException {
        final Long headOfficeId = branchRepository.findHeadOffice();
        if(headOfficeId==null){
            throw new BadRequestException("Unable to get Head Office. Cannot continue...");
        }
        final OrgBranch headOffice = branchRepository.findOne(headOfficeId);
        if(headOffice==null){
            throw new BadRequestException("Unable to get Head Office. Cannot continue...");
        }
        String firstPeriod = accountYearPeriodsRepo.firstPeriodOfYr(headOfficeId,year);
        final String period2 = new SimpleDateFormat("MMMM").format(dateFrom);
        final Date dtFrom = constructDate(year,firstPeriod);
        final Date lastMonthDt = DateUtils.addMonths(dateFrom,-1);
        Calendar cal = Calendar.getInstance();
        cal.setTime(lastMonthDt);
        int res = cal.getActualMaximum(Calendar.DATE);
        Date prdEndDate = null;
        if(firstPeriod!=null){
              prdEndDate = constructDate(res,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH));
        }
        if(dtFrom==null){
            throw new BadRequestException("Unable to parse the Date from...Please Contact Administrator for assistance...");
        }
        final BigDecimal v_fbal  = openingBalancesRepo.findOpBalance(rowId,reportTypes.getCode(),claCode);
        final BigDecimal v_fbal2  = openingBalancesRepo.findGroupOpBalance(rowId,reportTypes.getCode(),claCode);
        if(firstPeriod!=null && !firstPeriod.equalsIgnoreCase(period2)){
            BigDecimal operatingBal = openingBalancesRepo.findOperatingBal(rowId,dtFrom,prdEndDate,claCode);
            return v_fbal.add(v_fbal2).add(operatingBal);
        }
        else
        return v_fbal.add(v_fbal2);
    }

    private Date constructDate(Long year, String month){
        try {
            return new SimpleDateFormat("dd/MMMM/yyyy").parse("01/"+month+"/"+year);
        } catch (ParseException e) {
            return null;
        }
    }

    private Date constructDate(int day, int year, int month){
        try {
            return new SimpleDateFormat("dd/MMMM/yyyy").parse(day+"/"+month+"/"+year);
        } catch (ParseException e) {
            return null;
        }
    }


    private Date getEndOfYear(final Integer year){
        Calendar calendarEnd=Calendar.getInstance();
        calendarEnd.set(Calendar.YEAR,year);
        calendarEnd.set(Calendar.MONTH,11);
        calendarEnd.set(Calendar.DAY_OF_MONTH,31);
        return calendarEnd.getTime();
    }


    private Date getStartOfYear(final Integer year){
        Calendar calendarEnd=Calendar.getInstance();
        calendarEnd.set(Calendar.YEAR,year);
        calendarEnd.set(Calendar.MONTH,0);
        calendarEnd.set(Calendar.DAY_OF_MONTH,1);
        return calendarEnd.getTime();
    }

    private QuarterDates getQuarterDates(String quarterType,Integer year){
        Calendar fromCalendar = Calendar.getInstance();
        Calendar toCalendar = Calendar.getInstance();
        if(quarterType!=null && year!=null){
            if(quarterType.equalsIgnoreCase("FIRST")){
                fromCalendar.set(year, Calendar.JANUARY,1);
                toCalendar.set(year, Calendar.MARCH,31);
                return new QuarterDates(fromCalendar.getTime(),toCalendar.getTime());
            }
            else  if(quarterType.equalsIgnoreCase("SECOND")){
                fromCalendar.set(year, Calendar.APRIL,1);
                toCalendar.set(year, Calendar.JUNE,30);
                return new QuarterDates(fromCalendar.getTime(),toCalendar.getTime());
            }
            else  if(quarterType.equalsIgnoreCase("THIRD")){
                fromCalendar.set(year, Calendar.JULY,1);
                toCalendar.set(year, Calendar.SEPTEMBER,30);
                return new QuarterDates(fromCalendar.getTime(),toCalendar.getTime());
            }
            else  if(quarterType.equalsIgnoreCase("FOURTH")){
                fromCalendar.set(year, Calendar.OCTOBER,1);
                toCalendar.set(year, Calendar.DECEMBER,31);
                return new QuarterDates(fromCalendar.getTime(),toCalendar.getTime());
            }
            else {
                return null;
            }
        }
        return null;
    }



    private static class QuarterDates{

        public QuarterDates(Date fromDate, Date toDate) {
            this.fromDate = fromDate;
            this.toDate = toDate;
        }

        private Date fromDate;
        private Date toDate;

        public Date getFromDate() {
            return fromDate;
        }

        public Date getToDate() {
            return toDate;
        }

    }

}
