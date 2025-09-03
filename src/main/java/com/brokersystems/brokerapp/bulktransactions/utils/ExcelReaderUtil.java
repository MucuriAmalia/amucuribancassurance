package com.brokersystems.brokerapp.bulktransactions.utils;

import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.Currencies;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.poi.ss.usermodel.Sheet;

public class ExcelReaderUtil {
    public static String getCellValue(Row row, int columnIndex) {
        if (row == null) return "";
        return getSmartCellValue(row.getCell(columnIndex));
    }

    public static String getSmartCellValue(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue().trim().replaceAll("\\s+", " ");

            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
                } else {
                    // Convert numeric value to BigDecimal and return as string
                    double d = cell.getNumericCellValue();
                    BigDecimal bigDecimal = BigDecimal.valueOf(d).stripTrailingZeros();
                    return bigDecimal.toPlainString();  // Clean string, no .0 or scientific notation
                }

            case Cell.CELL_TYPE_BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());

            case Cell.CELL_TYPE_FORMULA:
                try {
                    return cell.getStringCellValue().trim().replaceAll("\\s+", " ");
                } catch (IllegalStateException e) {
                    try {
                        double d = cell.getNumericCellValue();
                        BigDecimal bigDecimal = BigDecimal.valueOf(d).stripTrailingZeros();
                        return bigDecimal.toPlainString();  // Clean string, no .0 or scientific notation
                    } catch (Exception ex) {
                        return "";
                    }
                }
            default:
                return "";
        }
    }

    public static Date parseFlexibleDate(String rawDate) {
        if (rawDate == null || rawDate.trim().isEmpty()) return null;

        String[] formats = {
                "yyyy-MM-dd", "dd-MM-yyyy", "dd/MM/yyyy",
                "MM/dd/yyyy", "yyyy/MM/dd", "dd.MM.yyyy",
                "dd MMM yyyy", "dd MMMM yyyy" , "DD/MM/YYYY"
        };

        for (String format : formats) {
            try {
                return new SimpleDateFormat(format).parse(rawDate.trim());
            } catch (Exception ignored) {
            }
        }

        throw new IllegalArgumentException("Unrecognized date format: " + rawDate);
    }

    public static BigDecimal parseBigDecimalSafe(String input) {
        try {
            return new BigDecimal(input.trim());
        } catch (Exception e) {
            return BigDecimal.ZERO; // or null, or throw a custom exception
        }
    }

    public static List<String> validateBulkPolicyTemplate(Workbook workbook) {
        List<String> errors = new ArrayList<String>();
        String[] sheet1Headers = {
                "Serial No", "Client ID No.", "Client CIF", "Sales Agent", "Sales Code",
                "Underwriter Code", "Product Group",
                "Product Name", "Contract", "Cover Type", "Branch Code", "Payment Frequency",
                "CoverDateFrom", "Currency ISO code", "Risk/Property ID", "Risk Description", "sum_insured","premium",
                "Accrual or Cash", "Accrual Inst Date", "Accrual Payment Type"
        };

        String[] sheet2Headers = {"Serial No", "Sections", "Amount"};

        try {
            // Check minimum sheets
            if (workbook.getNumberOfSheets() < 2) {
                errors.add("Template must contain at least 2 sheets.");
                return errors;
            }
            // Validate Sheet 1
            Sheet sheet1 = workbook.getSheetAt(0);
            Row headerRow1 = sheet1.getRow(0);
            if (headerRow1 == null) {
                errors.add("Sheet 1: Header row is missing.");
            } else {
                if (headerRow1.getLastCellNum() != sheet1Headers.length) {
                    errors.add("Sheet 1: Expected " + sheet1Headers.length + " columns, found " + headerRow1.getLastCellNum());
                }

                for (int i = 0; i < sheet1Headers.length; i++) {
                    Cell cell = headerRow1.getCell(i);
                    String actualHeader = getSmartCellValue(cell); // (cell != null) ? cell.toString().trim() : "";
                    if (!sheet1Headers[i].trim().equalsIgnoreCase(actualHeader)) {
                        errors.add("Sheet 1 Column " + (i + 1) + ": Expected '" + sheet1Headers[i] + "', found '" + actualHeader + "'");
                    }
                }
            }
            // Validate Sheet 2
            Sheet sheet2 = workbook.getSheetAt(1);
            Row headerRow2 = sheet2.getRow(0);
            if (headerRow2 == null) {
                errors.add("Sheet 2: Header row is missing.");
            } else {
                if (headerRow2.getLastCellNum() != sheet2Headers.length) {
                    errors.add("Sheet 2: Expected " + sheet2Headers.length + " columns, found " + headerRow2.getLastCellNum());
                }

                for (int i = 0; i < sheet2Headers.length; i++) {
                    Cell cell = headerRow2.getCell(i);
                    String actualHeader = getSmartCellValue(cell); // (cell != null) ? cell.toString().trim() : "";
                    if (!sheet2Headers[i].trim().equalsIgnoreCase(actualHeader)) {
                        errors.add("Sheet 2 Column " + (i + 1) + ": Expected '" + sheet2Headers[i] + "',but found '" + actualHeader + " ");
                    }
                }
            }

        } catch (Exception e) {
            errors.add("Validation error: " + e.getMessage());
        }
            return errors;
    }

    public static List<String> validateEmbedPackageInsuranceTemplate(Workbook workbook) {
        List<String> errors = new ArrayList<String>();
        String[] sheet1Headers = {
                "Serial No","Reg/ID No","Client CIF", "Branch Code",
                "Transaction Date", "Insurer Code", "Product Group", "Product Name", "Cover Type",
                "Frequency", "Currency ISO Code", "Start Date", "End Date",
                "Accrual or Cash", "Accrual Inst Date", "Accrual Payment Type"
        };

        String[] sheet2Headers = {  "Serial No", "Insured ID No", "Insured CIF",
                "Account No.", "Acct Open Date", "Branch Code", "Category", "Month", "Premium"
        };

        try {
            // Check minimum sheets
            if (workbook.getNumberOfSheets() < 2) {
                errors.add("Template must contain at least 2 sheets.");
                return errors;
            }
            // Validate Sheet 1
            Sheet sheet1 = workbook.getSheetAt(0);
            Row headerRow1 = sheet1.getRow(0);
            if (headerRow1 == null) {
                errors.add("Sheet 1: Header row is missing.");
            } else {
                if (headerRow1.getLastCellNum() != sheet1Headers.length) {
                    errors.add("Sheet 1: Expected " + sheet1Headers.length + " columns, found " + headerRow1.getLastCellNum());
                }

                for (int i = 0; i < sheet1Headers.length; i++) {
                    Cell cell = headerRow1.getCell(i);
                    String actualHeader = getSmartCellValue(cell); // (cell != null) ? cell.toString().trim() : "";
                    if (!sheet1Headers[i].trim().equalsIgnoreCase(actualHeader)) {
                        errors.add("Sheet 1 Column " + (i + 1) + ": Expected '" + sheet1Headers[i] + "', found '" + actualHeader + "'");
                    }
                }
            }
            // Validate Sheet 2
            Sheet sheet2 = workbook.getSheetAt(1);
            Row headerRow2 = sheet2.getRow(0);
            if (headerRow2 == null) {
                errors.add("Sheet 2: Header row is missing.");
            } else {
                if (headerRow2.getLastCellNum() != sheet2Headers.length) {
                    errors.add("Sheet 2: Expected " + sheet2Headers.length + " columns, found " + headerRow2.getLastCellNum());
                }

                for (int i = 0; i < sheet2Headers.length; i++) {
                    Cell cell = headerRow2.getCell(i);
                    String actualHeader = getSmartCellValue(cell); // (cell != null) ? cell.toString().trim() : "";
                    if (!sheet2Headers[i].trim().equalsIgnoreCase(actualHeader)) {
                        errors.add("Sheet 2 Column " + (i + 1) + ": Expected '" + sheet2Headers[i] + "',but found '" + actualHeader + " ");
                    }
                }
            }

        } catch (Exception e) {
            errors.add("Validation error: " + e.getMessage());
        }
        return errors;
    }

    public static List<String> validateAbsaStaffMotorsTemplate(Workbook workbook) {
        List<String> errors = new ArrayList<String>();
        String[] sheet1Headers = {
                "Cover Id","Insured Name","AB Number","ID Number","KRA PIN","Account No","Email Address","Mobile Number",
                "Body Type","Color","Load Capacity","Tare","No of Passengers","Registration No","Make/Model","YOM",
                "Engine Rating","Sum Insured","Cover Start Date","Chassis No","Engine Number","Underwriter","Cover Type",
                "Cover Option","Excess Buy Back Option","Excess Buy Back","Courtesy Cars Option","Loss of Use","AA Service Option",
                "AA Service","AMREF Option","AMREF","Basic Premium","Excess Protector","PVT","Tax","PLL", "Total Premium",
                "Cover Status","Application Type","Date Submitted","branch","frequency","currency","Product Name", "Product Group", "Contract",
                "Accrual or Cash", "Accrual Inst Date", "Accrual Payment Type"
        };

        try {
            Sheet sheet1 = workbook.getSheetAt(0);
            Row headerRow1 = sheet1.getRow(0);
            if (headerRow1 == null) {
                errors.add("Sheet 1: Header row is missing.");
            } else {
                if (headerRow1.getLastCellNum() != sheet1Headers.length) {
                    errors.add("Sheet 1: Expected " + sheet1Headers.length + " columns, found " + headerRow1.getLastCellNum());
                }
                for (int i = 0; i < sheet1Headers.length; i++) {
                    Cell cell = headerRow1.getCell(i);
                    String actualHeader = getSmartCellValue(cell); // (cell != null) ? cell.toString().trim() : "";
                    if (!sheet1Headers[i].trim().equalsIgnoreCase(actualHeader)) {
                        errors.add("Sheet 1 Column " + (i + 1) + ": Expected '" + sheet1Headers[i] + "', but found '" + actualHeader + " ");
                    }
                }
            }
        } catch (Exception e) {
            errors.add("Validation error: " + e.getMessage());
        }
        return errors;
    }

    public static List<String> validateCreditShieldPolicyTemplate(Workbook workbook) {
        List<String> errors = new ArrayList<String>();

        String[] sheet1Headers = {
                "ID No", "Client CIF", "Sales Agent",
                "Sales Manager", "Sales Code", "Card Type", "Card Account", "Insurer Code",
                "Product Group", "Product Name", "Cover Type", "Payment Frequency", "Currency ISO Code",
                "Branch", "Premium Amount", "Date Booked", "Start Date", "End Date"
        };

        try {
            Sheet sheet1 = workbook.getSheetAt(0);
            Row headerRow1 = sheet1.getRow(0);
            if (headerRow1 == null) {
                errors.add("Sheet 1: Header row is missing.");
            } else {
                if (headerRow1.getLastCellNum() != sheet1Headers.length) {
                    errors.add("Sheet 1: Expected " + sheet1Headers.length + " columns, found " + headerRow1.getLastCellNum());
                }
                for (int i = 0; i < sheet1Headers.length; i++) {
                    Cell cell = headerRow1.getCell(i);
                    String actualHeader = getSmartCellValue(cell); // (cell != null) ? cell.toString().trim() : "";
                    if (!sheet1Headers[i].trim().equalsIgnoreCase(actualHeader)) {
                        errors.add("Sheet 1 Column " + (i + 1) + ": Expected '" + sheet1Headers[i] + "', but found '" + actualHeader + " ");
                    }
                }
            }
        } catch (Exception e) {
            errors.add("Validation error: " + e.getMessage());
        }
        return errors;
    }


    public static List<String> validateTimizaPolicyTemplate(Workbook workbook) {
        List<String> errors = new ArrayList<String>();
        String[] sheet1Headers = {
                "ID No", "Client CIF","Transaction Date",
                "Transaction ID", "Payment Mode", "Branch Code", "Insurer Code", "Product Group",
                "Product Name", "Cover Type", "Currency ISO Code", "Frequency", "Premium",
                "Start Date", "End Date", "Term"
        };

        try {
            Sheet sheet1 = workbook.getSheetAt(0);
            Row headerRow1 = sheet1.getRow(0);
            if (headerRow1 == null) {
                errors.add("Sheet 1: Header row is missing.");
            } else {
                if (headerRow1.getLastCellNum() != sheet1Headers.length) {
                    errors.add("Sheet 1: Expected " + sheet1Headers.length + " columns, found " + headerRow1.getLastCellNum());
                }
                for (int i = 0; i < sheet1Headers.length; i++) {
                    Cell cell = headerRow1.getCell(i);
                    String actualHeader = getSmartCellValue(cell); // (cell != null) ? cell.toString().trim() : "";
                    if (!sheet1Headers[i].trim().equalsIgnoreCase(actualHeader)) {
                        errors.add("Sheet 1 Column " + (i + 1) + ": Expected '" + sheet1Headers[i] + "', but found '" + actualHeader + " ");
                    }
                }
            }
        } catch (Exception e) {
            errors.add("Validation error: " + e.getMessage());
        }
        return errors;
    }

    public static List<String> validateMortageLifePolicyTemplate(Workbook workbook) {
        List<String> errors = new ArrayList<String>();

        String[] sheet1Headers = {
                "ID No", "Client CIF", "Insurer Code",
                "Product Group", "Product Name", "Cover Type", "Payment Frequency", "Currency ISO Code",
                "Policy Branch", "Sum Insured", "Premium Amount", "Transaction Date",
                "Start Date", "End Date","Loan Account", "Casa", "Contract"
        };
        try {
            Sheet sheet1 = workbook.getSheetAt(0);
            Row headerRow1 = sheet1.getRow(0);
            if (headerRow1 == null) {
                errors.add("Sheet 1: Header row is missing.");
            } else {
                if (headerRow1.getLastCellNum() != sheet1Headers.length) {
                    errors.add("Sheet 1: Expected " + sheet1Headers.length + " columns, found " + headerRow1.getLastCellNum());
                }
                for (int i = 0; i < sheet1Headers.length; i++) {
                    Cell cell = headerRow1.getCell(i);
                    String actualHeader = getSmartCellValue(cell); // (cell != null) ? cell.toString().trim() : "";
                    if (!sheet1Headers[i].trim().equalsIgnoreCase(actualHeader)) {
                        errors.add("Sheet 1 Column " + (i + 1) + ": Expected '" + sheet1Headers[i] + "', but found '" + actualHeader + " ");
                    }
                }
            }
        } catch (Exception e) {
            errors.add("Validation error: " + e.getMessage());
        }
        return errors;
    }


    public static List<String> validateWezeshaStockPolicyTemplate(Workbook workbook) {
        List<String> errors = new ArrayList<String>();

        String[] sheet1Headers = {
                "Loan Id", "ID No", "Client CIF",
                "Business Location", "Type of Business", "Transaction Date", "Branch Code",
                "Insurer Code", "Product Group", "Product Name", "Cover Type", "Currency ISO Code",
                "Frequency", "Value of Stock to Insured", "Premium", "Start Date", "End Date",
                "Accrual or Cash", "Accrual Inst Date", "Accrual Payment Type"
        };
        try {
            Sheet sheet1 = workbook.getSheetAt(0);
            Row headerRow1 = sheet1.getRow(0);
            if (headerRow1 == null) {
                errors.add("Sheet 1: Header row is missing.");
            } else {
                if (headerRow1.getLastCellNum() != sheet1Headers.length) {
                    errors.add("Sheet 1: Expected " + sheet1Headers.length + " columns, found " + headerRow1.getLastCellNum());
                }
                for (int i = 0; i < sheet1Headers.length; i++) {
                    Cell cell = headerRow1.getCell(i);
                    String actualHeader = getSmartCellValue(cell); // (cell != null) ? cell.toString().trim() : "";
                    if (!sheet1Headers[i].trim().equalsIgnoreCase(actualHeader)) {
                        errors.add("Sheet 1 Column " + (i + 1) + ": Expected '" + sheet1Headers[i] + "', but found '" + actualHeader + " ");
                    }
                }
            }
        } catch (Exception e) {
            errors.add("Validation error: " + e.getMessage());
        }
        return errors;
    }


    public static List<String> validateCreditCardPolicyTemplate(Workbook workbook) {
        List<String> errors = new ArrayList<String>();

        String[] sheet1Headers = {
                "ID No", "Client CIF",
                "Sales Agent", "Sales Manager", "Sales Code", "Card Type", "Card Account",
                "Insurer Code", "Product Group", "Product Name", "Cover Type",
                "Payment Frequency", "Currency ISO Code", "Branch",
                "Premium Amount", "Date Booked", "Start Date", "End Date","Card Number","Cycles",
                "Accrual or Cash", "Accrual Inst Date", "Accrual Payment Type", "Seq"
        };

        try {
            Sheet sheet1 = workbook.getSheetAt(0);
            Row headerRow1 = sheet1.getRow(0);
            if (headerRow1 == null) {
                errors.add("Sheet 1: Header row is missing.");
            } else {
                if (headerRow1.getLastCellNum() != sheet1Headers.length) {
                    errors.add("Sheet 1: Expected " + sheet1Headers.length + " columns, found " + headerRow1.getLastCellNum());
                }
                for (int i = 0; i < sheet1Headers.length; i++) {
                    Cell cell = headerRow1.getCell(i);
                    String actualHeader = getSmartCellValue(cell); // (cell != null) ? cell.toString().trim() : "";
                    if (!sheet1Headers[i].trim().equalsIgnoreCase(actualHeader)) {
                        errors.add("Sheet 1 Column " + (i + 1) + ": Expected '" + sheet1Headers[i] + "', but found '" + actualHeader + " ");
                    }
                }
            }
        } catch (Exception e) {
            errors.add("Validation error: " + e.getMessage());
        }
        return errors;
    }


    public static List<String> validateCreditLifePolicyTemplate(Workbook workbook) {
        List<String> errors = new ArrayList<String>();

        String[] sheet1Headers = {
                "ID No","Client CIF","Insurer Code",
                "Product Group", "Product Name","Contract", "Cover Type", "Term", "Payment Frequency",
                "Currency ISO Code", "Policy Branch", "Sum Insured", "Premium Amount",
                "Transaction Date", "Start Date", "Loan Id"
        };

        try {
            Sheet sheet1 = workbook.getSheetAt(0);
            Row headerRow1 = sheet1.getRow(0);
            if (headerRow1 == null) {
                errors.add("Sheet 1: Header row is missing.");
            } else {
                if (headerRow1.getLastCellNum() != sheet1Headers.length) {
                    errors.add("Sheet 1: Expected " + sheet1Headers.length + " columns, found " + headerRow1.getLastCellNum());
                }
                for (int i = 0; i < sheet1Headers.length; i++) {
                    Cell cell = headerRow1.getCell(i);
                    String actualHeader = getSmartCellValue(cell); // (cell != null) ? cell.toString().trim() : "";
                    if (!sheet1Headers[i].trim().equalsIgnoreCase(actualHeader)) {
                        errors.add("Sheet 1 Column " + (i + 1) + ": Expected '" + sheet1Headers[i] + "', but found '" + actualHeader + " ");
                    }
                }
            }
        } catch (Exception e) {
            errors.add("Validation error: " + e.getMessage());
        }
        return errors;
    }

    public static List<String> validateEmbRetrPolicyTemplate(Workbook workbook) {
        List<String> errors = new ArrayList<String>();

        String[] sheet1Headers = {
                "Serial No", "Reg/ID No", "Client CIF", "Branch Code", "Insurer Code", "Product Group", "Product Name",
                "Cover Type", "Frequency", "Currency ISO Code", "Start Date", "End Date"
        };

        String[] sheet2Headers = {
                "Serial No", "Insured ID No", "Insured CIF", "Insured Account No", "Open Date",
                "Transaction Code", "Transaction Date", "Insured Branch Code", "Premium"
        };


        try {
            // Check minimum sheets
            if (workbook.getNumberOfSheets() < 2) {
                errors.add("Template must contain at least 2 sheets.");
                return errors;
            }
            // Validate Sheet 1
            Sheet sheet1 = workbook.getSheetAt(0);
            Row headerRow1 = sheet1.getRow(0);
            if (headerRow1 == null) {
                errors.add("Sheet 1: Header row is missing.");
            } else {
                if (headerRow1.getLastCellNum() != sheet1Headers.length) {
                    errors.add("Sheet 1: Expected " + sheet1Headers.length + " columns, found " + headerRow1.getLastCellNum());
                }

                for (int i = 0; i < sheet1Headers.length; i++) {
                    Cell cell = headerRow1.getCell(i);
                    String actualHeader = getSmartCellValue(cell); // (cell != null) ? cell.toString().trim() : "";
                    if (!sheet1Headers[i].trim().equalsIgnoreCase(actualHeader)) {
                        errors.add("Sheet 1 Column " + (i + 1) + ": Expected '" + sheet1Headers[i] + "', found '" + actualHeader + "'");
                    }
                }
            }
            // Validate Sheet 2
            Sheet sheet2 = workbook.getSheetAt(1);
            Row headerRow2 = sheet2.getRow(0);
            if (headerRow2 == null) {
                errors.add("Sheet 2: Header row is missing.");
            } else {
                if (headerRow2.getLastCellNum() != sheet2Headers.length) {
                    errors.add("Sheet 2: Expected " + sheet2Headers.length + " columns, found " + headerRow2.getLastCellNum());
                }

                for (int i = 0; i < sheet2Headers.length; i++) {
                    Cell cell = headerRow2.getCell(i);
                    String actualHeader = getSmartCellValue(cell); // (cell != null) ? cell.toString().trim() : "";
                    if (!sheet2Headers[i].trim().equalsIgnoreCase(actualHeader)) {
                        errors.add("Sheet 2 Column " + (i + 1) + ": Expected '" + sheet2Headers[i] + "',but found '" + actualHeader + " ");
                    }
                }
            }

        } catch (Exception e) {
            errors.add("Validation error: " + e.getMessage());
        }
        return errors;
    }

    public static List<String> validateGroupLifePolicyTemplate(Workbook workbook) {
        List<String> errors = new ArrayList<String>();

        String[] sheet1Headers = {
                "Serial No", "Reg/ID No", "Client CIF", "Branch Code", "Insurer Code", "Product Group",
                "Product Name", "Cover Type", "Term", "Frequency", "Currency ISO Code",
                "Start Date", "End Date", "Contract"
        };

        String[] sheet2Headers = {
                "Serial No", "ID No", "Insured CIF", "Employee Code",
                "Insured Branch Code", "Insurer Code", "Sum Insured",
                "Premium", "Transaction Date"
        };

        try {
            // Check minimum sheets
            if (workbook.getNumberOfSheets() < 2) {
                errors.add("Template must contain at least 2 sheets.");
                return errors;
            }
            // Validate Sheet 1
            Sheet sheet1 = workbook.getSheetAt(0);
            Row headerRow1 = sheet1.getRow(0);
            if (headerRow1 == null) {
                errors.add("Sheet 1: Header row is missing.");
            } else {
                if (headerRow1.getLastCellNum() != sheet1Headers.length) {
                    errors.add("Sheet 1: Expected " + sheet1Headers.length + " columns, found " + headerRow1.getLastCellNum());
                }

                for (int i = 0; i < sheet1Headers.length; i++) {
                    Cell cell = headerRow1.getCell(i);
                    String actualHeader = getSmartCellValue(cell); // (cell != null) ? cell.toString().trim() : "";
                    if (!sheet1Headers[i].trim().equalsIgnoreCase(actualHeader)) {
                        errors.add("Sheet 1 Column " + (i + 1) + ": Expected '" + sheet1Headers[i] + "', found '" + actualHeader + "'");
                    }
                }
            }
            // Validate Sheet 2
            Sheet sheet2 = workbook.getSheetAt(1);
            Row headerRow2 = sheet2.getRow(0);
            if (headerRow2 == null) {
                errors.add("Sheet 2: Header row is missing.");
            } else {
                if (headerRow2.getLastCellNum() != sheet2Headers.length) {
                    errors.add("Sheet 2: Expected " + sheet2Headers.length + " columns, found " + headerRow2.getLastCellNum());
                }

                for (int i = 0; i < sheet2Headers.length; i++) {
                    Cell cell = headerRow2.getCell(i);
                    String actualHeader = getSmartCellValue(cell); // (cell != null) ? cell.toString().trim() : "";
                    if (!sheet2Headers[i].trim().equalsIgnoreCase(actualHeader)) {
                        errors.add("Sheet 2 Column " + (i + 1) + ": Expected '" + sheet2Headers[i] + "',but found '" + actualHeader + " ");
                    }
                }
            }

        } catch (Exception e) {
            errors.add("Validation error: " + e.getMessage());
        }
        return errors;
    }
    public  static void validateCurrency(String currency, BigDecimal amount) throws BadRequestException {
        // if kes allow multiples of 5 on the decimal denotation that is 0.00, 0.20, 0.05
        if (Objects.equals(currency, "KES")) {
            BigDecimal fractionalPart = amount.remainder(BigDecimal.ONE).setScale(2, RoundingMode.HALF_UP);

            // Multiply by 100 to convert to cents
            int cents = fractionalPart.multiply(BigDecimal.valueOf(100)).intValue();

            if (cents % 5 != 0) {
                throw new BadRequestException("Amount must be in multiples of 0.05 for KES currency.");
            }
        }
    }
    private static final List<String> VALID_FREQUENCIES = Arrays.asList(
            "Daily", "Weekly", "Monthly", "Quarterly",
            "Semi-Annually", "Annually", "Single"
    );

    public static String validateFrequency(String frequency, int rowIndex) throws BadRequestException {
        if (frequency == null || frequency.trim().isEmpty()) {
            throw new BadRequestException("Payment frequency is required at Row " + (rowIndex + 1) + " in Payment frequency column");
        }

        // frequency = frequency.trim();
        //        if (!VALID_FREQUENCIES.contains(frequency)) {
        //            throw new BadRequestException("Invalid frequency: '" + frequency + "' at Row " + (rowIndex + 1) + " in Payment frequency column"+ VALID_FREQUENCIES);
        //        }
        //      return frequency;
        final String normalizedFrequency = frequency.trim();
        Optional<String> matchedFrequency = VALID_FREQUENCIES.stream()
                .filter(valid -> valid.equalsIgnoreCase(normalizedFrequency))
                .findFirst();

        if (!matchedFrequency.isPresent()) {
            String options = String.join(", ", VALID_FREQUENCIES);
            throw new BadRequestException(
                    "Invalid frequency: '" + frequency + "' at Row " + (rowIndex + 1)
                            + " in Payment frequency column. Valid values are: " + options
            );
        }

        return matchedFrequency.get();
    }

    public static List<String> validateBulkCommRemp(Workbook workbook) {
        List<String> errors = new ArrayList<String>();

        String[] sheet1Headers = {
                "POLICY", "CLIENT", "DR NO", "CR NO", "PAYMENT", "COMMISSION", "WITHHOLDING TAX", "PAYABLE AMOUNT"
        };

        try {
            // Validate Sheet 1
            Sheet sheet1 = workbook.getSheetAt(0);
            Row headerRow1 = sheet1.getRow(0);
            if (headerRow1 == null) {
                errors.add("Sheet 1: Header row is missing.");
            } else {
                if (headerRow1.getLastCellNum() != sheet1Headers.length) {
                    errors.add("Sheet 1: Expected " + sheet1Headers.length + " columns, found " + headerRow1.getLastCellNum());
                }

                for (int i = 0; i < sheet1Headers.length; i++) {
                    Cell cell = headerRow1.getCell(i);
                    String actualHeader = getSmartCellValue(cell); // (cell != null) ? cell.toString().trim() : "";
                    if (!sheet1Headers[i].trim().equalsIgnoreCase(actualHeader)) {
                        errors.add("Sheet 1 Column " + (i + 1) + ": Expected '" + sheet1Headers[i] + "', found '" + actualHeader + "'");
                    }
                }
            }

        } catch (Exception e) {
            errors.add("Validation error: " + e.getMessage());
        }
        return errors;
    }

    public static int findDataStartRow(Sheet sheet) {
        for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (row != null && !isHeaderRow(row)) {
                return rowNum;
            }
        }
        return -1;
    }

    public static boolean isHeaderRow(Row row) {
        if (row == null) return false;

        for (int cellNum = 0; cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell == null) continue;
            String cellValue = getSmartCellValue(cell);
            if (cellValue != null) {
                cellValue = cellValue.toLowerCase().trim();
                if (cellValue.contains("policy") ||
                        cellValue.contains("number") ||
                        cellValue.contains("name") ||
                        cellValue.contains("code") ||
                        cellValue.contains("amount") ||
                        cellValue.contains("serial no") ||
                        cellValue.contains("sections") ||
                        cellValue.contains("type")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isBlankRow(Row row) {
        if (row == null) return true;

        for (int cellNum = 0; cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                return false;
            }
        }
        return true;
    }

    public static List<String> validateBulkReceiptTemplate(Workbook workbook) {
        List<String> errors = new ArrayList<String>();

        String[] sheet1Headers = {
                "Policy No", "Policy ref/Proposal No", "Document Date",
                "Payment Mode", "Insurer Code", "Branch Code",
                "Receipt Amount", "Paid By",
                "Receipt Ref", "Manual Ref","Narration"
        };

        try {
            // Validate Sheet 1
            Sheet sheet1 = workbook.getSheetAt(0);
            Row headerRow1 = sheet1.getRow(0);
            if (headerRow1 == null) {
                errors.add("Sheet 1: Header row is missing.");
            } else {
                if (headerRow1.getLastCellNum() != sheet1Headers.length) {
                    errors.add("Sheet 1: Expected " + sheet1Headers.length + " columns, found " + headerRow1.getLastCellNum());
                }

                for (int i = 0; i < sheet1Headers.length; i++) {
                    Cell cell = headerRow1.getCell(i);
                    String actualHeader = getSmartCellValue(cell); // (cell != null) ? cell.toString().trim() : "";
                    if (!sheet1Headers[i].trim().equalsIgnoreCase(actualHeader)) {
                        errors.add("Sheet 1 Column " + (i + 1) + ": Expected '" + sheet1Headers[i] + "', found '" + actualHeader + "'");
                    }
                }
            }

        } catch (Exception e) {
            errors.add("Validation error: " + e.getMessage());
        }
        return errors;
    }

}