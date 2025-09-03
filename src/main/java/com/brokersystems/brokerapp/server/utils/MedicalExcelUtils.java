package com.brokersystems.brokerapp.server.utils;

import com.brokersystems.brokerapp.quotes.model.AgeBrackets;
import com.brokersystems.brokerapp.quotes.model.FamilySizes;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by peter on 4/27/2017.
 */
@Component
public class MedicalExcelUtils {



    public  double getPremium(int age, double amt,String types,String sheetName,String path) throws IOException,BadRequestException {
        double premium = 0 ;
        Workbook workbook = new HSSFWorkbook(Files.newInputStream(Paths.get(path)));
        sheetName = sheetName.toUpperCase();
        Sheet sheet = workbook.getSheet(sheetName);
         if(sheet==null) throw new BadRequestException("Error getting premium from Rate table...Error on Sheet "+sheetName);
        Iterator<Row> iterator = sheet.iterator();
        List<MedicalBean> data = new ArrayList<MedicalBean>();
        double coveredamt = 0;
        double coveredamt2 = 0;
        double coveredamt3 = 0;
        double coveredamt4 = 0;
        double coveredamt5 = 0;
        double coveredamt6 = 0;
        double coveredamt7 = 0;
        double coveredamt8 = 0;
        double coveredamt9 = 0;
        double coveredamt10 = 0;
        double coveredamt11 = 0;
        double coveredamt12 = 0;
        double coveredamt13 = 0;
        double coveredamt14 = 0;
        double coveredamt15 = 0;
        double coveredamt16 = 0;
        double coveredamt17 = 0;
        double coveredamt18 = 0;
        double coveredamt19 = 0;
        double coveredamt20 = 0;
        double coveredamt21 = 0;
        double coveredamt22 = 0;
        double coveredamt23 = 0;
        double coveredamt24 = 0;
        double coveredamt25 = 0;
        double coveredamt26 = 0;
        double coveredamt27 = 0;
        double coveredamt28 = 0;
        double coveredamt29 = 0;
        double coveredamt30 = 0;
        String minage = "";
        String maxage = "";
        String type = "";
        while (iterator.hasNext()) {
            Row currentRow = iterator.next();
            Iterator<Cell> cellIterator = currentRow.iterator();
            while (cellIterator.hasNext()) {
                Cell currentCell = cellIterator.next();
                int columnIndex = currentCell.getColumnIndex();
                switch(columnIndex){
                    case 0:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                            MedicalBean medical = new MedicalBean();
                            String value = currentCell.getStringCellValue();
                            if(value.indexOf("-") > 0){
                                String[] arr = value.split("-");
                                minage = arr[0];
                                maxage = arr[1];
                                medical.setMinAge(arr[0]);
                                medical.setMaxAge(arr[1]);
                                data.add(medical);
                            }
                            else {
                                type = value;
                                medical.setType(type);
                                data.add(medical);
                            }
                        }
                        break;
                    case 1:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 2:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt2 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt2);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 3:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt3 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt3);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 4:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt4 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt4);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 5:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt5 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt5);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 6:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt6 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt6);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 7:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt7 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt7);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 8:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt8 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt8);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 9:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt9 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt9);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 10:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt10 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt10);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 11:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt11 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt11);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 12:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt12 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt12);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 13:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt13 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt13);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 14:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt14 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt14);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 15:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt15 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt15);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 16:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt16 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt16);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 17:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt17 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt17);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 18:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt18 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt18);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 19:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt19 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt19);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 20:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt20 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt20);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 21:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt21 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt21);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 22:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt22 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt22);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 23:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt23 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt23);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 24:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt24 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt24);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 25:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt25 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt25);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 26:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt26 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt26);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 27:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt27 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt27);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 28:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt28 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt28);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 29:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt29 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt29);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 30:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt30 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt30);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;

                }
            }



            for(MedicalBean bean:data){
                if(bean.getCoveredAmt() > 0 && bean.getPremium() > 0)
                    if((age >= Integer.parseInt(bean.getMinAge().trim())) && age <= Integer.parseInt(bean.getMaxAge().trim()) && amt==bean.getCoveredAmt() && types.equalsIgnoreCase(bean.getType())){
                        premium = bean.getPremium();
                        break;
                    }
            }


        }
        return premium;
    }
    public  List<FamilySizes> getSetFamilysize(String sheetName, InputStream excelFile) throws IOException,BadRequestException {
        double premium = 0 ;
        //FileInputStream excelFile = new FileInputStream(new File(FILE_NAME));
        Workbook workbook = new HSSFWorkbook(excelFile);
        sheetName = sheetName.toUpperCase();
        Sheet sheet = workbook.getSheet(sheetName);
        if(sheet==null) throw new BadRequestException("Error getting premium from Rate table...Error on Sheet "+sheetName);
        Iterator<Row> iterator = sheet.iterator();
        List<MedicalBean> data = new ArrayList<MedicalBean>();
        double coveredamt = 0;
        double coveredamt2 = 0;
        double coveredamt3 = 0;
        double coveredamt4 = 0;
        double coveredamt5 = 0;
        double coveredamt6 = 0;
        double coveredamt7 = 0;
        double coveredamt8 = 0;
        double coveredamt9 = 0;
        double coveredamt10 = 0;
        double coveredamt11 = 0;
        double coveredamt12 = 0;
        double coveredamt13 = 0;
        double coveredamt14 = 0;
        double coveredamt15 = 0;
        double coveredamt16 = 0;
        double coveredamt17 = 0;
        double coveredamt18 = 0;
        double coveredamt19 = 0;
        double coveredamt20 = 0;
        double coveredamt21 = 0;
        double coveredamt22 = 0;
        double coveredamt23 = 0;
        double coveredamt24 = 0;
        double coveredamt25 = 0;
        double coveredamt26 = 0;
        double coveredamt27 = 0;
        double coveredamt28 = 0;
        double coveredamt29 = 0;
        double coveredamt30 = 0;
        String minage = "";
        String maxage = "";
        String type = "";
        while (iterator.hasNext()) {
            Row currentRow = iterator.next();
            Iterator<Cell> cellIterator = currentRow.iterator();
            while (cellIterator.hasNext()) {
                Cell currentCell = cellIterator.next();
                int columnIndex = currentCell.getColumnIndex();
                switch(columnIndex){
                    case 0:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                            MedicalBean medical = new MedicalBean();
                            String value = currentCell.getStringCellValue();
                            if(value.indexOf("-") > 0){
                                String[] arr = value.split("-");
                                minage = arr[0];
                                maxage = arr[1];
                                medical.setMinAge(arr[0]);
                                medical.setMaxAge(arr[1]);
                                data.add(medical);
                            }
                            else {
                                type = value;
                                medical.setType(type);
                                data.add(medical);
                            }
                        }
                        break;
                    case 1:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 2:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt2 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt2);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 3:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt3 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt3);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 4:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt4 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt4);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 5:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt5 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt5);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 6:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt6 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt6);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 7:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt7 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt7);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 8:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt8 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt8);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 9:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt9 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt9);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 10:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt10 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt10);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 11:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt11 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt11);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 12:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt12 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt12);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 13:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt13 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt13);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 14:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt14 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt14);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 15:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt15 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt15);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 16:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt16 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt16);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 17:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt17 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt17);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 18:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt18 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt18);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 19:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt19 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt19);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 20:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt20 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt20);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 21:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt21 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt21);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 22:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt22 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt22);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 23:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt23 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt23);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 24:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt24 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt24);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 25:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt25 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt25);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 26:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt26 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt26);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 27:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt27 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt27);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 28:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt28 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt28);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 29:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt29 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt29);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 30:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt30 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt30);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;

                }
            }
        }
        List<FamilySizes> familySizes = new ArrayList<>();

            List<String> datas = data.stream().filter(a -> a.getType() != null && !(a.getType().equalsIgnoreCase("")) && !(a.getType().equalsIgnoreCase("Extra"))).map(a -> a.getType()).distinct().collect(Collectors.toList());

        for (String data1:datas){
            familySizes.add(new FamilySizes(data1,data1));
        }
        return familySizes;
    }

    public  List<AgeBrackets> getSetAgeBrackets(String sheetName, InputStream excelFile) throws IOException,BadRequestException {
        double premium = 0 ;
        //FileInputStream excelFile = new FileInputStream(new File(FILE_NAME));
        Workbook workbook = new HSSFWorkbook(excelFile);
        sheetName = sheetName.toUpperCase();
        Sheet sheet = workbook.getSheet(sheetName);
        if(sheet==null) throw new BadRequestException("Error getting premium from Rate table...Error on Sheet "+sheetName);
        Iterator<Row> iterator = sheet.iterator();
        List<MedicalBean> data = new ArrayList<MedicalBean>();
        double coveredamt = 0;
        double coveredamt2 = 0;
        double coveredamt3 = 0;
        double coveredamt4 = 0;
        double coveredamt5 = 0;
        double coveredamt6 = 0;
        double coveredamt7 = 0;
        double coveredamt8 = 0;
        double coveredamt9 = 0;
        double coveredamt10 = 0;
        double coveredamt11 = 0;
        double coveredamt12 = 0;
        double coveredamt13 = 0;
        double coveredamt14 = 0;
        double coveredamt15 = 0;
        double coveredamt16 = 0;
        double coveredamt17 = 0;
        double coveredamt18 = 0;
        double coveredamt19 = 0;
        double coveredamt20 = 0;
        double coveredamt21 = 0;
        double coveredamt22 = 0;
        double coveredamt23 = 0;
        double coveredamt24 = 0;
        double coveredamt25 = 0;
        double coveredamt26 = 0;
        double coveredamt27 = 0;
        double coveredamt28 = 0;
        double coveredamt29 = 0;
        double coveredamt30 = 0;
        String minage = "";
        String maxage = "";
        String type = "";
        String agebracket = "";
        while (iterator.hasNext()) {
            Row currentRow = iterator.next();
            Iterator<Cell> cellIterator = currentRow.iterator();
            while (cellIterator.hasNext()) {
                Cell currentCell = cellIterator.next();
                int columnIndex = currentCell.getColumnIndex();
                switch(columnIndex){
                    case 0:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                            MedicalBean medical = new MedicalBean();
                            String value = currentCell.getStringCellValue();
                            if(value.indexOf("-") > 0){
                                String[] arr = value.split("-");
                                minage = arr[0];
                                maxage = arr[1];
                                agebracket = value;
                                medical.setMinAge(arr[0]);
                                medical.setMaxAge(arr[1]);
                                medical.setAgeBracket(agebracket);
                                data.add(medical);
                            }
                            else {
                                type = value;
                                medical.setType(type);
                                data.add(medical);
                            }
                        }
                        break;
                    case 1:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                medical.setAgeBracket(agebracket);
                                coveredamt = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 2:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setAgeBracket(agebracket);
                                medical.setMaxAge(maxage);
                                medical.setType(type);
                                coveredamt2 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt2);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 3:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                coveredamt3 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt3);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 4:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                coveredamt4 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt4);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 5:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                coveredamt5 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt5);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 6:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                coveredamt6 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt6);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 7:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                coveredamt7 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt7);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 8:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                coveredamt8 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt8);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 9:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                coveredamt9 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt9);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 10:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                coveredamt10 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt10);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 11:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                coveredamt11 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt11);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 12:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                coveredamt12 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt12);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 13:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                coveredamt13 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt13);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 14:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                coveredamt14 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt14);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 15:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                coveredamt15 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt15);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 16:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                coveredamt16 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt16);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 17:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                coveredamt17 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt17);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 18:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                coveredamt18 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt18);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 19:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                coveredamt19 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt19);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 20:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                coveredamt20 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt20);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 21:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                coveredamt21 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt21);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 22:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                coveredamt22 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt22);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 23:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                coveredamt23 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt23);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 24:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                coveredamt24 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt24);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 25:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                coveredamt25 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt25);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 26:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                coveredamt26 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt26);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 27:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                coveredamt27 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt27);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 28:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                coveredamt28 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt28);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 29:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                coveredamt29 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt29);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;
                    case 30:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            MedicalBean medical = new MedicalBean();
                            if(currentCell.getRowIndex()==1){
                                medical.setCoveredAmt(currentCell.getNumericCellValue());
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                coveredamt30 = currentCell.getNumericCellValue();
                                data.add(medical);
                            }
                            else{
                                medical.setPremium(currentCell.getNumericCellValue());
                                medical.setCoveredAmt(coveredamt30);
                                medical.setMinAge(minage);
                                medical.setMaxAge(maxage);
                                medical.setAgeBracket(agebracket);
                                medical.setType(type);
                                data.add(medical);
                            }

                        }
                        break;

                }
            }
        }
        List<AgeBrackets> agebrackets = new ArrayList<>();
            List<String> datas = data.stream().filter(a -> a.getAgeBracket() != null && (a.getAgeBracket().contains("-")) && !(a.getAgeBracket().equalsIgnoreCase("")) && !(a.getAgeBracket().equalsIgnoreCase("Extra"))).map(a -> a.getAgeBracket()).distinct().collect(Collectors.toList());

            for (String data1:datas){
                agebrackets.add(new AgeBrackets(data1,data1));
            }

        return agebrackets;
    }

}
