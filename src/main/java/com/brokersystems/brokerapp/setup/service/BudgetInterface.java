package com.brokersystems.brokerapp.setup.service;

import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.Budgets;
import com.brokersystems.brokerapp.setup.model.UnloadedBudgets;

import java.io.FileNotFoundException;
import java.util.List;

public interface BudgetInterface {


    public void deleteBudgets(Long id);

    void saveBudget(Budgets budget,String product,String agent,String branch) throws BadRequestException, FileNotFoundException;

    DataTablesResult<Budgets> findBudgets(DataTablesRequest request, Long product) throws IllegalAccessException;


    DataTablesResult<Budgets> findBudgetsLife(DataTablesRequest pageable, Long product) throws IllegalAccessException;

    Budgets editBudget(Long id);

    void editBudgets(Budgets budget);

    void delBudgets(Long id);

    DataTablesResult<Budgets> searchBudgets(DataTablesRequest pageable, String sbr) throws IllegalAccessException;

    void saveBudgets(Budgets budget);

    DataTablesResult<UnloadedBudgets> findUnloaded(DataTablesRequest pageable) throws IllegalAccessException;

    List<UnloadedBudgets> findAll();
}
