package com.brokersystems.brokerapp.setup.service.impl;


import com.brokersystems.brokerapp.accounts.dtos.PayeesDTO;
import com.brokersystems.brokerapp.accounts.model.BankBranches;
import com.brokersystems.brokerapp.accounts.model.CoaSubAccounts;
import com.brokersystems.brokerapp.accounts.repository.BankBranchRepo;
import com.brokersystems.brokerapp.accounts.repository.CoaSubAccountsRepo;
import com.brokersystems.brokerapp.claims.dtos.ClaimantsDTO;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.dto.BankAccountDTO;
import com.brokersystems.brokerapp.setup.model.BankAccounts;
import com.brokersystems.brokerapp.setup.model.OrgBranch;
import com.brokersystems.brokerapp.setup.repository.BankAccountsRepo;
import com.brokersystems.brokerapp.setup.repository.OrgBranchRepository;
import com.brokersystems.brokerapp.setup.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class BankServiceImpl implements BankService {

    @Autowired
    private BankAccountsRepo accountsRepo;
    @Autowired
    private CoaSubAccountsRepo subAccountsRepo;
    @Autowired
    private BankBranchRepo bankBranchRepo;

    @Autowired
    private OrgBranchRepository branchRepository;
    @Override
    public DataTablesResult<BankAccountDTO> findAllBankAccounts(DataTablesRequest request) {
        final String search = ( request.getSearch()!=null && request.getSearch().getValue()!=null)?"%"+request.getSearch().getValue()+"%":"%%";
        List<Object[]> accnts = accountsRepo.findBankAccnts(search.toLowerCase(),request.getPageNumber(), request.getPageSize());
        final List<BankAccountDTO> bankAccountDTOS = new ArrayList<>();
        long rowCount = 0l;
        if(!accnts.isEmpty()) rowCount =((BigInteger)accnts.get(0)[14]).intValue();
        for(Object[] acct:accnts){
            BankAccountDTO accountDTO = new BankAccountDTO();
            accountDTO.setBaId(((BigInteger)acct[0]).longValue());
            accountDTO.setBankAcctName((String) acct[1]);
            accountDTO.setBankAcctNumber((String) acct[2]);
            accountDTO.setCurrentChequeNo((String) acct[3]);
            accountDTO.setMaximumAmt(((BigDecimal)acct[4]));
            accountDTO.setMinimumAmt(((BigDecimal)acct[5]));
            accountDTO.setStatus((String) acct[6]);
            accountDTO.setBranchName((String) acct[7]);
            accountDTO.setBranchId(((BigInteger)acct[8]).longValue());
            accountDTO.setBankBranchName((String) acct[9]);
            accountDTO.setBankBranchId(((BigInteger)acct[10]).longValue());
            accountDTO.setGlId(((BigInteger)acct[11]).longValue());
            accountDTO.setGlCode((String) acct[12]);
            accountDTO.setGlName((String) acct[13]);
            bankAccountDTOS.add(accountDTO);
        }
        Page<BankAccountDTO> page = new PageImpl<>(bankAccountDTOS,request, rowCount);
        return new DataTablesResult<>(request, page);
    }


    @Override
    public void createBankAccount(BankAccountDTO bankAccountDTO) throws BadRequestException {
        if(bankAccountDTO.getBankBranchId()==null){
            throw new BadRequestException("Please Select Bank Branch to continue");
        }
        if(bankAccountDTO.getBranchId()==null){
            throw new BadRequestException("Select Branch of Operation to continue..");
        }
        if(bankAccountDTO.getGlId()==null){
            throw new BadRequestException("Select GL Account to continue...");
        }
        final CoaSubAccounts subAccounts = subAccountsRepo.findOne(bankAccountDTO.getGlId());
        if(subAccounts==null){
            throw new BadRequestException("Invalid GL Account..Select A valid GL Account");
        }
        final BankBranches bankBranches = bankBranchRepo.findOne(bankAccountDTO.getBankBranchId());
        if(bankBranches==null){
            throw new BadRequestException("Select A Valid Bank Branch to continue...");
        }
        final OrgBranch orgBranch = branchRepository.findOne(bankAccountDTO.getBranchId());
        if(orgBranch==null){
            throw new BadRequestException("Select a Valid Branch of Operation...");
        }
        final BankAccounts bankAccounts = new BankAccounts();
        if(bankAccountDTO.getBaId()==null){
            bankAccounts.setStatus("A");
        }
        else{
            bankAccounts.setBaId(bankAccountDTO.getBaId());
            bankAccounts.setStatus(bankAccountDTO.getStatus());
        }
        if(bankAccountDTO.getMinimumAmt()!=null && bankAccountDTO.getMaximumAmt()!=null){
            if(bankAccountDTO.getMinimumAmt().compareTo(bankAccountDTO.getMaximumAmt()) > 0){
                throw new BadRequestException("Account Minimum cannot be greater than maximum...");
            }
        }
        bankAccounts.setMinAmount(bankAccountDTO.getMinimumAmt());
        bankAccounts.setMaximumAmount(bankAccountDTO.getMaximumAmt());
        bankAccounts.setBankBranches(bankBranches);
        bankAccounts.setGlAcc(subAccounts);
        bankAccounts.setBranch(orgBranch);
        bankAccounts.setCurrentCheckNo(bankAccountDTO.getCurrentChequeNo());
        bankAccounts.setBankAcctName(bankAccountDTO.getBankAcctName());
        bankAccounts.setBankAcctNumber(bankAccountDTO.getBankAcctNumber());
        bankAccounts.setTranstype("B");
        accountsRepo.save(bankAccounts);
    }

    @Override
    public void deleteBankAccount(long accountId) throws BadRequestException{
        final BankAccounts bankAccounts = accountsRepo.findOne(accountId);
        if(bankAccounts==null){
            throw new BadRequestException("Cannot delete Bank Account..Does not exist..");
        }
        try{
            accountsRepo.delete(accountId);
        }
        catch (Exception ex){
            throw new BadRequestException("Cannot delete Bank Account..Can only deactivate");
        }
    }
}
