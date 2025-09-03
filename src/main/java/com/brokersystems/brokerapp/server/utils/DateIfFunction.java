package com.brokersystems.brokerapp.server.utils;

import org.apache.poi.ss.formula.eval.*;
import org.apache.poi.ss.formula.functions.Function;
import org.apache.poi.ss.usermodel.DateUtil;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Years;

import java.util.Date;

public class DateIfFunction implements Function {

    @Override
    public ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
//                System.out.println("DateDiff Call --> "+args+ " "+ srcRowIndex+ "  "+srcColumnIndex);


        ValueEval v_a=null;
        ValueEval v_b=null;
        try{

            v_a = OperandResolver.getSingleValue(args[0], srcRowIndex,srcColumnIndex);
            Date d1 = DateUtil.getJavaDate(OperandResolver.coerceValueToDouble(v_a));

            v_b = OperandResolver.getSingleValue(args[1], srcRowIndex,srcColumnIndex);
            Date d2 = DateUtil.getJavaDate(OperandResolver.coerceValueToDouble(v_b));

            switch( OperandResolver.coerceValueToString(args[2]).charAt(0)){
                case 'm':
                    int m=  Months.monthsBetween( new DateTime(d1).withDayOfMonth(1), new DateTime(d2).withDayOfMonth(1)).getMonths();
                    return new NumberEval(m);
                case 'M':
                    int M=  Months.monthsBetween( new DateTime(d1).withDayOfMonth(1), new DateTime(d2).withDayOfMonth(1)).getMonths();
                    return new NumberEval(M);
                case 'Y':
                    int Y=  Years.yearsBetween( new DateTime(d1).withDayOfMonth(1), new DateTime(d2).withDayOfMonth(1)).getYears();
                    return new NumberEval(Y);
                case 'D':
                    int D= Days.daysBetween(new DateTime(d1), new DateTime(d2)).getDays();
                    return new NumberEval(D);

//                case 'YM':
//                    int YM= Months.monthsBetween( new DateTime(d1).withDayOfMonth(1), new DateTime(d2).withDayOfMonth(1).withYear(new DateTime(d1).getYear())).getMonths();
//                    return new NumberEval(YM);
//                case 'YD':
//                    int YD= Days.daysBetween( new DateTime(d1), new DateTime(d2).withYear(new DateTime(d1).getYear())).getDays();
//                    return new NumberEval(YD);
                default:
                    System.out.println("DATEDIFF --> Unknown third parameter "+ OperandResolver.coerceValueToString(args[2]).charAt(0) );
                    return ErrorEval.NUM_ERROR;
            }

        } catch (EvaluationException e) {
            System.out.println("DateDiff Error --> "+args+ " "+ srcRowIndex+ "  "+srcColumnIndex);
            e.printStackTrace();
        }
        return ErrorEval.NA;
    }
}
