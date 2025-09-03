package com.brokersystems.brokerapp.server.utils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.brokersystems.brokerapp.server.exception.BadRequestException;
import org.apache.commons.lang.time.DateUtils;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class DateUtilities {


	public Date getWetMonthDate(Date wefDate ,Integer term){
		return DateUtils.addDays(DateUtils.addMonths(wefDate, term), -1);
	}

	public Date getWetDate(Date wefDate) {
		return DateUtils.addDays(DateUtils.addYears(wefDate, 1), -1);
	}

	public Date getRiskWetDate(Date wefDate, String frequency){
		if (wefDate == null || frequency == null || frequency.isEmpty()) {
			throw new IllegalArgumentException("Invalid input: wefDate or frequency cannot be null/empty.");
		}
		switch (frequency) {
			case "W":
				return DateUtils.addDays(DateUtils.addWeeks(wefDate, 1), -1);
			case "M":
				return DateUtils.addDays(DateUtils.addMonths(wefDate, 1),-1);
			case "Q":
				return DateUtils.addDays(DateUtils.addMonths(wefDate, 3),-1);
			case "S":
				return DateUtils.addDays(DateUtils.addMonths(wefDate, 6),-1);
			case "A":
            case "SG":
                return DateUtils.addDays(DateUtils.addYears(wefDate, 1),-1);
            default:
				throw new IllegalArgumentException("Unsupported frequency: " + frequency);
		}
	}


	public Date getMaturityDate(Date wefDate ,Integer term){
		return DateUtils.addDays(DateUtils.addYears(wefDate, term), -1);
	}

	public Date getPaidToDate(Date wefDate ,Integer inst){
		return DateUtils.addDays(DateUtils.addMonths(wefDate, inst), -1);
	}
	
	public int getUwYear(Date wefDate){
		Calendar cal =  Calendar.getInstance();
		cal.setTime(wefDate);
		return cal.get(Calendar.YEAR);
    }

	public String getMonth(Date date){
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTime(date);
		String month = mCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
		return month;
	}
	
	 public  Date removeTime(Date date) {
	        Calendar cal = Calendar.getInstance();
	        cal.setTime(date);
	        cal.set(Calendar.HOUR_OF_DAY, 0);
	        cal.set(Calendar.MINUTE, 0);
	        cal.set(Calendar.SECOND, 0);
	        cal.set(Calendar.MILLISECOND, 0);
	        return cal.getTime();
	    }
	 
	 public String formatDate(Date date){
		 return new SimpleDateFormat("dd/MM/yyyy").format(date);
	 }


	public int getNumberOfMonths(final Date from,final Date to){
		if(from.after(to))
			throw new IllegalArgumentException("Date from cannot be greater than Date To");
		Calendar dateFrom = Calendar.getInstance();
		dateFrom.setTime(from);

		Calendar dateTo  = Calendar.getInstance();
		dateTo.setTime(to);

		int months = 0;
		months = dateTo.get(Calendar.MONTH) - dateFrom.get(Calendar.MONTH);
		return months;
	}


	public static int computeAge( final Date dateOfBirth) {

		Calendar today = Calendar.getInstance();
		Calendar birthDate = Calendar.getInstance();
		int age = 0;
		birthDate.setTime(dateOfBirth);
		age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

		// If birth date is greater than todays date (after 2 days adjustment of leap year) then decrement age one year
		if ( (birthDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) > 3) ||
				(birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH ))){
			age--;

			// If birth date and todays date are of same month and birth day of month is greater than todays day of month then decrement age
		}else if ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH )) &&
				(birthDate.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH ))){
			age--;
		}

		return age;
	}

	public static BigDecimal convert(String val){
		try{
			return new BigDecimal(val);
		}
		catch (Exception e){

		}
		return BigDecimal.ZERO;
	}

	public int daysBetweenUsingJoda(Date d1, Date d2){
		return Days.daysBetween(
				new LocalDate(d1.getTime()),
				new LocalDate(d2.getTime())).getDays();
	}

	public int getAge( final Date dateOfBirth) {
		if(dateOfBirth==null)
			throw new IllegalArgumentException("Date of Birth cannot be null...");
		Calendar today = Calendar.getInstance();
		Calendar birthDate = Calendar.getInstance();

		int age = 0;

		birthDate.setTime(dateOfBirth);
		if (birthDate.after(today)) {
			throw new IllegalArgumentException("Can't be born in the future");
		}

		age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

		// If birth date is greater than todays date (after 2 days adjustment of leap year) then decrement age one year
		if ( (birthDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) > 3) ||
				(birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH ))){
			age--;

			// If birth date and todays date are of same month and birth day of month is greater than todays day of month then decrement age
		}else if ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH )) &&
				(birthDate.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH ))){
			age--;
		}

		return age;
	}
	public Date getPayoutDate(Date wefDate, int numOfPayouts,int term){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(wefDate);
		calendar.add(Calendar.YEAR, term/3 * numOfPayouts);
		return calendar.getTime();
	}
}