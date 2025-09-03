package com.brokersystems.brokerapp.server.utils;

import com.brokersystems.brokerapp.kie.rules.ClientRulesExecutor;
import com.brokersystems.brokerapp.medical.model.MemberBeanHolder;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.*;
import com.mysema.query.types.Predicate;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by peter on 5/26/2017.
 */
@Component
public class MemberUploadUtils {

    @Autowired
    private ClientRulesExecutor clientRulesExecutor;

    @Autowired
    private ClientRepository clientRepo;

    @Autowired
    private ClientTypeRepo clientTypeRepo;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private TownRepository townRepository;

    @Autowired
    private OccupationRepo occupationRepo;

    @Autowired
    private OrgBranchRepository branchRepository;

    @Autowired
    private SequenceRepository sequenceRepo;

    @Autowired
    private PostalCodeRepo postalCodeRepo;

    @Autowired
    private MobPrefixRepo mobPrefixRepo;


    @Autowired
    private ClientTitleRepo clientTitleRepo;

//    private File convert(MultipartFile file) throws IOException
//    {
//        File convFile = new File(file.getOriginalFilename());
//        convFile.createNewFile();
//        FileOutputStream fos = new FileOutputStream(convFile);
//        fos.write(file.getBytes());
//        fos.close();
//        return convFile;
//    }

    public File multipartToFile(MultipartFile multipart) throws IllegalStateException, IOException
    {
        File convFile = new File( multipart.getOriginalFilename());
        if(convFile.exists()){
            convFile.delete();
        }
        multipart.transferTo(convFile);
        return convFile;
    }

    private String createClientFromMember(MemberBean memberBean) {
        long count =-2000;
        long titlecount =-2000;
        if(memberBean.getIdno()==null){
            count = 0;
        }else {
            count = clientRepo.count(QClientDef.clientDef.idNo.equalsIgnoreCase(memberBean.getIdno()));
        }



        if(count==1){
            return null;
        }
        else if(count > 1) return "Client Exists more than once";
        else if(count ==0){
            ClientDef clientDef = new ClientDef();
            ClientTitle clientTitle = new ClientTitle();
            String  phoneNo="";
            String  smsNo="";
            if (memberBean.getClientTitle()==null){
                titlecount = 0;
            }else {
                titlecount = clientTitleRepo.count(QClientTitle.clientTitle.titleName.equalsIgnoreCase(memberBean.getClientTitle()));
                if (titlecount==0){
                    clientTitle.setTitleName(memberBean.getClientTitle());
                    ClientTitle savedClientTitle = clientTitleRepo.save(clientTitle);
                    clientTitle = savedClientTitle;
                }else if (titlecount>0){
                    clientTitle = clientTitleRepo.findOne(QClientTitle.clientTitle.titleName.equalsIgnoreCase(memberBean.getClientTitle()));
                }
            }
            clientDef.setFname(memberBean.getFname());
            if (memberBean.getPhoneNumber()==null && memberBean.getSmsNo()!=null){
                phoneNo=memberBean.getSmsNo();
            } else if (memberBean.getPhoneNumber()!=null){
                phoneNo=memberBean.getPhoneNumber();
            }
            if (memberBean.getSmsNo()==null && memberBean.getPhoneNumber()!=null){
                smsNo=memberBean.getPhoneNumber();
            } else if (memberBean.getSmsNo()!=null){
                smsNo=memberBean.getSmsNo();
            }

            if (!phoneNo.isEmpty()){
                String phonePrefix =phoneNo.substring(0,3);
                 phoneNo =phoneNo.substring(3,phoneNo.length());
                clientDef.setPhoneNo(phoneNo);
                clientDef.setPhonePrefix(mobPrefixRepo.findOne(QMobilePrefixDef.mobilePrefixDef.prefixName.equalsIgnoreCase(phonePrefix)) );
            }
            if (!smsNo.isEmpty()){
                System.out.println("sms no. "+ smsNo);
                String smsPrefix =smsNo.substring(0,3);
                String smNo =smsNo.substring(3,smsNo.length());
                clientDef.setSmsNumber(smNo);
                clientDef.setSmsPrefix(mobPrefixRepo.findOne(QMobilePrefixDef.mobilePrefixDef.prefixName.equalsIgnoreCase(smsPrefix)) );
            }
            //clientDef.setPhoneNo(memberBean.getPhoneNumber());
            clientDef.setOtherNames(memberBean.getOtherNames());
            clientDef.setIdNo(memberBean.getIdno());
            clientDef.setGender(memberBean.getGender());
            clientDef.setAddress(memberBean.getAddress());
            clientDef.setClientTitle(clientTitle);
            if (memberBean.getPostalCode()!=null){
                System.out.println("code  "+memberBean.getPostalCode()+" "+postalCodeRepo.count(QPostalCodesDef.postalCodesDef.zipCode.eq(memberBean.getPostalCode())));
                clientDef.setPostalCodesDef(postalCodeRepo.findOne(QPostalCodesDef.postalCodesDef.zipCode.eq(memberBean.getPostalCode())));
            }
            clientDef.setEmailAddress(memberBean.getEmail());
            if(memberBean.getOccupation()!=null)
                clientDef.setOccupation(occupationRepo.findOne(QOccupation.occupation.shortDesc.eq(memberBean.getOccupation())));
            clientDef.setStatus("A");
            clientDef.setTenantType(clientTypeRepo.findOne(QClientTypes.clientTypes.typeDesc.equalsIgnoreCase("INDIVIDUAL")));
            Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("C");
            SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
            Long seqNumber = sequence.getNextNumber();
            final String clientNumber = sequence.getSeqPrefix() + String.format("%06d", seqNumber);
            sequence.setLastNumber(seqNumber);
            sequence.setNextNumber(seqNumber + 1);
            sequenceRepo.save(sequence);
            clientDef.setTenantNumber(clientNumber);
            if(memberBean.getCountry()!=null){
                clientDef.setCountry(countryRepository.findOne(QCountry.country.couShtDesc.eq(memberBean.getCountry())));
            }
            if(memberBean.getTown()!=null){
                clientDef.setTown(townRepository.findOne(QTown.town.ctShtDesc.eq(memberBean.getTown())));
            }
            clientDef.setDob(memberBean.getDob());
            clientDef.setDateregistered(new Date());
            if(memberBean.getOccupation()!=null)
                clientDef.setOccupation(occupationRepo.findOne(QOccupation.occupation.shortDesc.eq(memberBean.getOccupation())));
            clientDef.setPinNo(memberBean.getPinNumber());
            OrgBranch defaultBranch = null;
            for(OrgBranch branch:branchRepository.findAll()){
                defaultBranch = branch;
                break;
            }
            clientDef.setRegisteredbrn(defaultBranch);
             String errors =  clientRulesExecutor.validateClientChecks(clientDef);
            return errors;
        }
        return "Error validating client..consult admin";
    }

  public MemberBeanHolder uploadMembers(MultipartFile file, String sheetNam, HttpServletRequest request) throws IOException {
      MemberBeanHolder memberBeanHolder = new MemberBeanHolder();
        List<MemberBean> members = new ArrayList<MemberBean>();
        byte [] byteArr=file.getBytes();
        InputStream excelFile = new ByteArrayInputStream(byteArr);
//        File uploadFile = convert(file);
//        FileInputStream excelFile = new FileInputStream(uploadFile);
        Workbook workbook = new HSSFWorkbook(excelFile);
        Sheet sheet = workbook.getSheet(sheetNam);
        Iterator<Row> iterator = sheet.iterator();
        String key = null;
        int rowCount = 0;
        outerloop: while (iterator.hasNext()) {
            Row currentRow = iterator.next();
            if(currentRow.getRowNum()==0) continue;
            MemberBean memberBean = new MemberBean();
             boolean validRecord = false;
            Iterator<Cell> cellIterator = currentRow.iterator();
            while (cellIterator.hasNext()) {
                Cell currentCell = cellIterator.next();
                int columnIndex = currentCell.getColumnIndex();
               // boolean update = false;

                switch(columnIndex){
                    case 0:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                            if(currentCell.getStringCellValue()!=null && "Principal".equalsIgnoreCase(currentCell.getStringCellValue())){
                                UUID uuid = UUID.randomUUID();
                                key = uuid.toString();
                            }
                            validRecord = true;
                            memberBean.setType(currentCell.getStringCellValue());
                        }
                        break;
                    case 1:

                        if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                            memberBean.setClientTitle(currentCell.getStringCellValue());

                        }
                        break;
                    case 2:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                            memberBean.setFname(currentCell.getStringCellValue());

                        }
                        break;
                    case 3:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                            memberBean.setOtherNames(currentCell.getStringCellValue());

                        }
                        break;
                    case 4:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                            memberBean.setGender(currentCell.getStringCellValue());

                        }
                        break;
                    case 5:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            if(currentCell.getNumericCellValue()==0){
                                memberBean.setIdno(null);
                            }else
                                memberBean.setIdno(String.valueOf(new BigDecimal(currentCell.getNumericCellValue()).setScale(0, BigDecimal.ROUND_HALF_EVEN)));
                        }
                        break;
                    case 6:

                        if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                            memberBean.setPinNumber(currentCell.getStringCellValue());

                        }
                        break;
                    case 7:

                        if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            if(HSSFDateUtil.isCellDateFormatted(currentCell)){
                                memberBean.setDob(currentCell.getDateCellValue());
                            }
                        }
                        break;
                    case 8:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_STRING ){
                            memberBean.setPhoneNumber(currentCell.getStringCellValue());
                        }
                        else if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC ){
                            //memberBean.setPhoneNumber((currentCell.getNumericCellValue()!=0)?String.valueOf(currentCell.getNumericCellValue()):null);
                            memberBean.setPhoneNumber((currentCell.getNumericCellValue()!=0)?String.valueOf(new BigDecimal(currentCell.getNumericCellValue()).setScale(0, BigDecimal.ROUND_HALF_EVEN)):null);
                        }
                        break;
                    case 9:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_STRING ){
                            memberBean.setSmsNo(currentCell.getStringCellValue());
                        }
                        else if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC ){
                            memberBean.setSmsNo((currentCell.getNumericCellValue()!=0)?String.valueOf(new BigDecimal(currentCell.getNumericCellValue()).setScale(0, BigDecimal.ROUND_HALF_EVEN)):null);

                            // memberBean.setSmsNo((currentCell.getNumericCellValue()!=0)?String.valueOf(currentCell.getNumericCellValue()):null);
                        }
                        break;
                    case 10:

                        if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                            memberBean.setEmail(currentCell.getStringCellValue());

                        }
                        break;
                    case 11:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                            memberBean.setAddress(currentCell.getStringCellValue());

                        }
                        break;
                    case 12:

                        if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                            memberBean.setPostalCode(currentCell.getStringCellValue());

                        }
                        break;
                    case 13:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                            memberBean.setCountry(currentCell.getStringCellValue());

                        }
                        break;
                    case 14:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                            memberBean.setTown(currentCell.getStringCellValue());

                        }
                        break;

                    case 15:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                            memberBean.setOccupation(currentCell.getStringCellValue());

                        }
                        break;


                    case 16:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                            memberBean.setType(currentCell.getStringCellValue());

                        }
                        break;
                    case 17:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                            memberBean.setSubType(currentCell.getStringCellValue());

                        }
                        break;


                    case 18:

                        if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                            memberBean.setMemberHasCard(currentCell.getStringCellValue());

                        }
                        break;
                    case 19:
                        if(currentCell.getCellType() == Cell.CELL_TYPE_STRING ){
                            memberBean.setCardNo(currentCell.getStringCellValue());
                        }
                        else if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC ){
                            memberBean.setCardNo((currentCell.getNumericCellValue()!=0)?String.valueOf(new BigDecimal(currentCell.getNumericCellValue()).setScale(0, BigDecimal.ROUND_HALF_EVEN)):null);
                        }
                        break;
                    case 20:

                        if(currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                            memberBean.setAutoGenerateCardNo(currentCell.getStringCellValue());

                        }
                        break;




                }
                //if(validRecord)



            }
            if(memberBean.getFname()!=null) {
                System.out.println(memberBean.getFname()+" "+memberBean.getOtherNames());
                System.out.println(memberBean.toString());
                String errors = createClientFromMember(memberBean);
                if (errors != null) {
                    memberBean.setError(errors);
                    int start = 21;

                    String error = errors.replace("<br>","\"&CHAR(10)&\"");

                        Font headerFont = workbook.createFont();
                        headerFont.setBoldweight((short) 0);
                        headerFont.setFontHeightInPoints((short) 14);
                        headerFont.setColor(IndexedColors.RED.getIndex());
                        CellStyle headerCellStyle = workbook.createCellStyle();
                        headerCellStyle.setWrapText(true);
                        headerCellStyle.setFont(headerFont);
                        Cell cell = currentRow.createCell(start);
                        cell.setCellFormula("CONCATENATE(\""+error+"\")");
                        cell.setCellStyle(headerCellStyle);
                    rowCount++;

                }
                else{
                    memberBean.setError(null);
                }
            }
            else{
                memberBean.setError(null);
            }
            memberBean.setKey(key);
            if(memberBean.getFname()==null && memberBean.getOtherNames()==null) continue outerloop;
            members.add(memberBean);
            final String ifkey = key;
            if(memberBean.getError()!=null) {
                members.removeIf(a -> a.getKey().equalsIgnoreCase(ifkey));
            }

        }
     // System.out.println("Member count "+members.size()+" row count "+rowCount);
        if(rowCount > 0) {
            String fileName = "error_log_"+new SimpleDateFormat("ddMMyyss").format(new Date())+".xls";
            String name =  request.getServletContext().getRealPath("/"+fileName);
            memberBeanHolder.setExcelFile(fileName);
            memberBeanHolder.setMemberBeans(new ArrayList<>());
            FileOutputStream fileOut = new FileOutputStream(name);
            workbook.write(fileOut);
            fileOut.close();
        }

        else{
            memberBeanHolder.setMemberBeans(members);
        }
        return memberBeanHolder;
    }


    public static class MemberBean{

        private String type;
        private String fname;
        private  String otherNames;
        private Date dob;
        private String gender;
        private  String country;
        private  String town;
        private  String occupation;
        private  String address;
        private  String phoneNumber;
        private  String subType;
        private String key;
        private  String idno;
        private String pinNumber;
        private String memberHasCard;
        private String cardNo;
        private String error;
        private String autoGenerateCardNo;
        private String email;
        private String smsNo;
        private String clientTitle;
        private String postalCode;

        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }
        public String getFname() {
            return fname;
        }
        public void setFname(String fname) {
            this.fname = fname;
        }
        public String getOtherNames() {
            return otherNames;
        }
        public void setOtherNames(String otherNames) {
            this.otherNames = otherNames;
        }
        public Date getDob() {
            return dob;
        }
        public void setDob(Date dob) {
            this.dob = dob;
        }
        public String getGender() {
            return gender;
        }
        public void setGender(String gender) {
            this.gender = gender;
        }
        public String getCountry() {
            return country;
        }
        public void setCountry(String country) {
            this.country = country;
        }
        public String getOccupation() {
            return occupation;
        }
        public void setOccupation(String occupation) {
            this.occupation = occupation;
        }
        public String getAddress() {
            return address;
        }
        public void setAddress(String address) {
            this.address = address;
        }
        public String getPhoneNumber() {
            return phoneNumber;
        }
        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
        public String getSubType() {
            return subType;
        }
        public void setSubType(String subType) {
            this.subType = subType;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getIdno() {
            return idno;
        }

        public void setIdno(String idno) {
            this.idno = idno;
        }

        public String getKey() {
            return key;
        }
        public void setKey(String key) {
            this.key = key;
        }

        public String getPinNumber() {
            return pinNumber;
        }

        public void setPinNumber(String pinNumber) {
            this.pinNumber = pinNumber;
        }

        public String getMemberHasCard() {
            return memberHasCard;
        }

        public void setMemberHasCard(String memberHasCard) {
            this.memberHasCard = memberHasCard;
        }

        public String getCardNo() {
            return cardNo;
        }

        public void setCardNo(String cardNo) {
            this.cardNo = cardNo;
        }

        public String getAutoGenerateCardNo() {
            return autoGenerateCardNo;
        }

        public void setAutoGenerateCardNo(String autoGenerateCardNo) {
            this.autoGenerateCardNo = autoGenerateCardNo;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getSmsNo() {
            return smsNo;
        }

        public void setSmsNo(String smsNo) {
            this.smsNo = smsNo;
        }

        public String getClientTitle() {
            return clientTitle;
        }

        public void setClientTitle(String clientTitle) {
            this.clientTitle = clientTitle;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }

        public String getTown() {
            return town;
        }

        public void setTown(String town) {
            this.town = town;
        }

        @Override
        public String toString() {
            return "MemberBean{" +
                    "type='" + type + '\'' +
                    ", fname='" + fname + '\'' +
                    ", otherNames='" + otherNames + '\'' +
                    ", dob=" + dob +
                    ", gender='" + gender + '\'' +
                    ", country='" + country + '\'' +
                    ", occupation='" + occupation + '\'' +
                    ", address='" + address + '\'' +
                    ", phoneNumber='" + phoneNumber + '\'' +
                    ", subType='" + subType + '\'' +
                    ", key='" + key + '\'' +
                    ", idno='" + idno + '\'' +
                    ", pinNumber='" + pinNumber + '\'' +
                    ", memberHasCard='" + memberHasCard + '\'' +
                    ", cardNo='" + cardNo + '\'' +
                    ", autoGenerateCardNo='" + autoGenerateCardNo + '\'' +
                    ", email='" + email + '\'' +
                    ", smsNo='" + smsNo + '\'' +
                    ", clientTitle='" + clientTitle + '\'' +
                    ", postalCode='" + postalCode + '\'' +
                    ", town='" + town + '\'' +
                    '}';
        }
    }




}
