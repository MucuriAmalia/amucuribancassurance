package com.brokersystems.brokerapp.webservices.utils;

import com.brokersystems.brokerapp.medical.model.MedicalCovers;
import com.brokersystems.brokerapp.medical.model.QMedicalCovers;
import com.brokersystems.brokerapp.medical.repository.MedicalCoversRepo;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.MedicalBean;
import com.brokersystems.brokerapp.server.utils.PremExcelUtils;
import com.brokersystems.brokerapp.server.utils.Streamable;
import com.brokersystems.brokerapp.setup.model.PremRatesTable;
import com.brokersystems.brokerapp.setup.model.QPremRatesTable;
import com.brokersystems.brokerapp.setup.repository.PremRatesRepo;
import com.brokersystems.brokerapp.setup.repository.PremRatesTableRepo;
import com.brokersystems.brokerapp.webservices.portalmodel.MedicalDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class BinderExcelUtils {

    @Autowired
    private PremRatesTableRepo premRatesTableRepo;

    @Autowired
    private MedicalCoversRepo medicalCoversRepo;

   public List<MedicalDTO> getMedicalBinders(Long detCode){
       final String ratesTable = premRatesTableRepo.getRatesLocation(detCode);
       if (ratesTable == null)
           return new ArrayList<>();
       List<MedicalDTO> medicalDTOS = new ArrayList<>();
       Iterable<MedicalCovers> medicalCovers = medicalCoversRepo.findAll(QMedicalCovers.medicalCovers.binderDet.detId.eq(detCode));
       try {
           for(MedicalCovers covers:medicalCovers){
//                       System.out.println(covers.getSection().getShtDesc());
                      List<MedicalBean> medicalBeans=getPremium(covers.getSection().getShtDesc(),ratesTable);
                      List<MedicalBean> filtMedicalBeans = medicalBeans.stream().filter(a -> a.getPremium() >0).collect(Collectors.toList());
                      Map<Double,List<MedicalBean>> medicalMap = filtMedicalBeans.stream().collect(Collectors.groupingBy(MedicalBean::getCoveredAmt));


                      medicalMap.keySet().forEach(a ->{
                          List<MedicalBean> medicalBeanList = medicalMap.get(a);
                              double maxAge =medicalBeanList.stream().filter(h -> !h.getType().equalsIgnoreCase("child")).mapToDouble(s -> Double.valueOf(s.getMaxAge())).max().getAsDouble();
                              double minAge =medicalBeanList.stream().filter(h -> !h.getType().equalsIgnoreCase("child")).mapToDouble(s -> Double.valueOf(s.getMinAge())).min().getAsDouble();
                        //  System.out.println("Max age "+maxAge+" min age "+minAge);
                          Map<String,List<MedicalBean>> mapsType = medicalBeanList.stream().collect(Collectors.groupingBy(MedicalBean::getType));

                          mapsType.keySet().forEach(y ->{
//


                              List<MedicalBean> medicalBeanLst = mapsType.get(y);
                              for(MedicalBean medicalBean:medicalBeanLst){
                                  MedicalDTO medicalDTO = new MedicalDTO();
                                  if("Spouse".equalsIgnoreCase(y)){
                                      medicalDTO.setSpouse(true);
                                  }
                                  medicalDTO.setCoverLimit(BigDecimal.valueOf(a));
                                  if(!y.equalsIgnoreCase("child")) {
                                      medicalDTO.setMaxAgeYears(Double.valueOf(medicalBean.getMaxAge()));
                                      medicalDTO.setMinAgeYears(Double.valueOf(medicalBean.getMinAge()));
                                      medicalDTO.setMinAgeMonths(Double.valueOf(medicalBean.getMinAge())*12);
                                      medicalDTO.setMaxAgeMonths(Double.valueOf(medicalBean.getMaxAge())*12);
                                  }
                                  else {
                                      medicalDTO.setMinAgeMonths(Double.valueOf(medicalBean.getMinAge()));
                                      medicalDTO.setMaxAgeMonths(Double.valueOf(medicalBean.getMaxAge()));
                                  }
                                  medicalDTO.setName(covers.getSection().getShtDesc()+" "+a);
                                  medicalDTO.setPremium(medicalBean.getPremium());
                                  medicalDTO.setCover(covers.getMainSection()!=null?covers.getMainSection().getDesc():covers.getSection().getDesc());
                                  if(covers.getSection().getShtDesc().toLowerCase().indexOf("outpatient")!=-1){
                                      medicalDTO.setPrimary(false);
                                  }
                                  else
                                      medicalDTO.setPrimary(true);
                                  medicalDTO.setCoverId(covers.getSection().getId());

                                  medicalDTO.setMaxJoinAgeYears(maxAge);
                                  medicalDTOS.add(medicalDTO);
                              }

                          });


                      });

           }
//           System.out.println(medicalDTOS);
       } catch (IOException|BadRequestException e) {
           e.printStackTrace();
           return new ArrayList<>();
       }
       return medicalDTOS;
   }

    public  List<MedicalBean> getPremium(String sheetName,final String path) throws IOException,BadRequestException {
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


        }
        return data;
    }


}
