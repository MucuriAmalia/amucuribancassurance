package com.brokersystems.brokerapp.nav.checkerdatamappings;

import com.brokersystems.brokerapp.accounts.model.CollectionAccounts;
import com.brokersystems.brokerapp.accounts.repository.CollectionAcctsRepo;
import com.brokersystems.brokerapp.setup.model.OrgBranch;
import com.brokersystems.brokerapp.setup.repository.OrgBranchRepository;
import com.brokersystems.brokerapp.trans.model.*;
import com.brokersystems.brokerapp.trans.repository.SystemTransactionsRepo;
import com.brokersystems.brokerapp.trans.repository.SystemTransactionsTempRepo;
import com.brokersystems.brokerapp.uw.dtos.PolicyCreateDTO;
import com.brokersystems.brokerapp.uw.dtos.ReceiptsDTO;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.json.JSONArray;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class ReceiptCheckerMapperService {
    @Autowired
    private SystemTransactionsRepo systemTransactionsRepo;
    @Autowired
    private CollectionAcctsRepo acctsRepo;
    @Autowired
    private OrgBranchRepository orgBranchRepository;
    @Autowired
    private PolicyTransRepo policyTransRepo;
    @Autowired
    private SystemTransactionsTempRepo systemTransactionsTempRepo;

    public ReceiptsDTO mapReceiptData(JSONObject jsonObject) {
        ReceiptsDTO receiptsDTO = new ReceiptsDTO();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");// For date parsing
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("MMM dd, yyyy, hh:mm:ss a");// For date parsing

            receiptsDTO.setReceiptNo(jsonObject.getString("receiptNo"));

            // Parse the receiptDate
            String receiptDateStr = jsonObject.getString("receiptDate");
            try {
                Date receiptDate = dateFormat.parse(receiptDateStr);
                receiptsDTO.setReceiptDate(receiptDate);
            }
            catch (ParseException e) {

            }
            try {
                Date receiptDate = dateFormat2.parse(receiptDateStr);
                receiptsDTO.setReceiptDate(receiptDate);
            }
            catch (ParseException e) {

            }

            // Parse the receiptTransDate
            String receiptTransDateStr = jsonObject.getString("receiptTransDate");
            try {
                Date receiptTransDate = dateFormat.parse(receiptTransDateStr);
                receiptsDTO.setReceiptTransDate(receiptTransDate);
            }
            catch (ParseException ex){

            }

            try {
                Date receiptTransDate = dateFormat2.parse(receiptTransDateStr);
                receiptsDTO.setReceiptTransDate(receiptTransDate);
            }
            catch (ParseException ex){

            }

            receiptsDTO.setReceiptAmount(BigDecimal.valueOf(jsonObject.getDouble("receiptAmount")));

            // Handling nested collectionAccount object
            if(jsonObject.has("collectionAccount")) {
                JSONObject collectionAccountJson = jsonObject.getJSONObject("collectionAccount");
                CollectionAccounts collectionAccounts = acctsRepo.findOne((long) collectionAccountJson.optInt("caId"));
                receiptsDTO.setCollectionAccount(collectionAccounts);
            }

            JSONObject branch = jsonObject.getJSONObject("branch");
            OrgBranch orgBranch =orgBranchRepository.findOne((long) branch.optInt("obId"));
            receiptsDTO.setBranch(orgBranch);
            // Map other fields
            receiptsDTO.setPaidBy(jsonObject.getString("paidBy"));
            receiptsDTO.setPaymentRef(jsonObject.getString("paymentRef"));
            receiptsDTO.setManualRef(jsonObject.getString("manualRef"));

            // Parse documentDate
            if(jsonObject.has("documentDate")){
            String documentDateStr = jsonObject.getString("documentDate");
                try {
                    Date documentDate = dateFormat.parse(documentDateStr);
                    receiptsDTO.setDocumentDate(documentDate);
                }
                catch (ParseException ex){

                }

                try {
                    Date documentDate = dateFormat2.parse(documentDateStr);
                    receiptsDTO.setDocumentDate(documentDate);
                }
                catch (ParseException ex){

                }
            }

            receiptsDTO.setReceiptDesc(jsonObject.getString("receiptDesc"));
            receiptsDTO.setReceiptType(jsonObject.getString("receiptType"));

            // Map details array
            JSONArray detailsArray = jsonObject.getJSONArray("details");
            ArrayList<ReceiptTransDtls> detailsList = new ArrayList<>();
            for (int i = 0; i < detailsArray.length(); i++) {
                JSONObject detailObj = detailsArray.getJSONObject(i);
                ReceiptTransDtls detail = new ReceiptTransDtls();
                detail.setRctAmount(BigDecimal.valueOf(detailObj.getDouble("rctAmount")));
                if(detailObj.has("transNo")) {
                    detail.setTransNo((long) detailObj.getInt("transNo"));
                } else if (detailObj.has("transTempNo")) {
                    detail.setTransTempNo((long) detailObj.getInt("transTempNo"));
                }
                detailsList.add(detail);
            }
            receiptsDTO.setDetails(detailsList);
            ArrayList<SystemTransactions> transactionsList = new ArrayList<>();
            ArrayList<SystemTransactionsTemp> transactionsTempList = new ArrayList<>();
            ArrayList<PolicyTrans> policyTransList = new ArrayList<>();
            for (int i = 0; i < detailsArray.length(); i++) {
                // Get each detail object
                JSONObject detailObject = detailsArray.optJSONObject(i);

                // Extract the transNo from the detail object
                if (detailObject.has("transNo")) {
                    long transNo = detailObject.optInt("transNo");
                    System.out.println(transNo);

                    if (Objects.equals(jsonObject.getString("receiptType"), "L")) {
                        PolicyTrans policyTrans = policyTransRepo.findOne(transNo);
                        if (policyTrans != null) {
                            policyTransList.add(policyTrans);
                        }
                    } else if (
                            Objects.equals(jsonObject.getString("receiptType"), "N") ||
                                    Objects.equals(jsonObject.getString("receiptType"), "COM")
                    ) {
                        // Find the corresponding SystemTransaction from the database
                        SystemTransactions transaction = systemTransactionsRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(transNo));

                        // Add the transaction to the list
                        if (transaction != null) {
                            transactionsList.add(transaction);
                        }
                    }
                } else if (detailObject.has("transTempNo")) {
                    long transTempNo = detailObject.optInt("transTempNo");
                    System.out.println(transTempNo);

                    if (Objects.equals(jsonObject.getString("receiptType"), "L")) {
                        PolicyTrans policyTrans = policyTransRepo.findOne(transTempNo);
                        if (policyTrans != null) {
                            policyTransList.add(policyTrans);
                        }
                    } else if (
                            Objects.equals(jsonObject.getString("receiptType"), "N") ||
                                    Objects.equals(jsonObject.getString("receiptType"), "COM")
                    ) {
                        // Find the corresponding SystemTransaction from the database
                        SystemTransactionsTemp transactionsTemp = systemTransactionsTempRepo.findOne(QSystemTransactionsTemp.systemTransactionsTemp.tempTransno.eq(transTempNo));
                        System.out.println(transactionsTemp.getClient());
                        // Add the transaction to the list
                        if (transactionsTemp != null) {
                            transactionsTempList.add(transactionsTemp);
                        }
                    }
                }
            }
            if(Objects.equals(jsonObject.getString("receiptType"), "L")){
                receiptsDTO.setPolicyTrans(policyTransList);
            } else if (
                    Objects.equals(jsonObject.getString("receiptType"), "N") ||
                    Objects.equals(jsonObject.getString("receiptType"), "COM")
                    ) {
                // Set the transactions list in the ReceiptsDTO
                receiptsDTO.setTransactions(transactionsList);
                receiptsDTO.setTransactionsTemps(transactionsTempList);
            }




        return receiptsDTO;
    }

}
