package com.brokersystems.brokerapp.customscreens.service;

import com.brokersystems.brokerapp.claims.dtos.ServiceProviderDTO;
import com.brokersystems.brokerapp.customscreens.model.GenericResultsetData;
import com.brokersystems.brokerapp.customscreens.model.RegisteredTable;
import com.brokersystems.brokerapp.customscreens.model.ResultsetColumnHeaderData;
import com.brokersystems.brokerapp.customscreens.model.ResultsetRowData;
import com.brokersystems.brokerapp.customscreens.repository.RegisteredTableModelRepo;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SchedulesServiceImpl implements SchedulesService {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private GenericDataService genericDataService;

    @Autowired
    private RegisteredTableModelRepo registeredTableModelRepo;


    @Override
    public DataTablesResult<RegisteredTable> getSubclassSchedules(DataTablesRequest request, Long subId) {
        final String search = ( request.getSearch()!=null && request.getSearch().getValue()!=null)?"%"+request.getSearch().getValue()+"%":"%%";
        final List<RegisteredTable> registeredTables = new ArrayList<>();
        List<Object[]> trans = registeredTableModelRepo.findRegTables(search,subId,request.getPageNumber(), request.getPageSize());
        long rowCount = 0L;
        if(!trans.isEmpty()) rowCount = ((BigInteger)trans.get(0)[4]).intValue();
        for(Object[] tran:trans){
            RegisteredTable registeredTable = new RegisteredTable();
            registeredTable.setTableName((String) tran[0]);
            registeredTable.setCategory(((BigInteger) tran[2]).longValue());
            registeredTable.setKeyValue(((Integer) tran[3]).longValue());
            registeredTables.add(registeredTable);
        }
        Page<RegisteredTable> page = new PageImpl<>(registeredTables,request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public GenericResultsetData retrieveDataTableGenericResultSet(Long subId, Long riskId) throws BadRequestException {
        final String appTable = queryForApplicationTableName(subId);
        final List<ResultsetColumnHeaderData> columnHeaders = this.genericDataService.fillResultsetColumnHeaders(appTable);
        List<String > riskColumn = columnHeaders.stream().map(a -> a.getColumnName()).filter( a->a.contains("risks_id")).collect(Collectors.toList());
        if(riskColumn==null || riskColumn.size()==0 || riskColumn.size() > 1){
            throw new BadRequestException("No column mapped to Risk.. Contact System Administrator ");
        }
        final String riskIdColumn = riskColumn.get(0);
        final String sql =  "select * from \"" + appTable + "\" where "+riskIdColumn+" = " + riskId;
        final List<ResultsetRowData> result = fillDatatableResultSetDataRows(sql);
        return new GenericResultsetData(columnHeaders, result);
    }

    @Override
    public GenericResultsetData retrieveSingleDataTableGenericResultSet(Long subId) throws BadRequestException {
        final String appTable = queryForApplicationTableName(subId);
        final List<ResultsetColumnHeaderData> columnHeaders = this.genericDataService.fillResultsetColumnHeaders(appTable);
        return new GenericResultsetData(columnHeaders, new ArrayList<>());
    }

    private List<ResultsetRowData> fillDatatableResultSetDataRows(final String sql) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        final SqlRowSet rs = jdbcTemplate.queryForRowSet(sql);

        final List<ResultsetRowData> resultsetDataRows = new ArrayList<>();

        final SqlRowSetMetaData rsmd = rs.getMetaData();

        while (rs.next()) {
            final List<String> columnValues = new ArrayList<>();
            for (int i = 0; i < rsmd.getColumnCount(); i++) {
                final String columnName = rsmd.getColumnName(i + 1);
                final String columnValue = rs.getString(columnName);
                columnValues.add(columnValue);
            }

            final ResultsetRowData resultsetDataRow = ResultsetRowData.create(columnValues);
            resultsetDataRows.add(resultsetDataRow);
        }

        return resultsetDataRows;
    }

    private String queryForApplicationTableName( final Long subId) throws BadRequestException {
        //System.out.println("Sub class "+subId);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        final String sql = "SELECT registered_table_name FROM x_registered_table where  app_table_key_value='"+subId+"' limit 1 offset 0";

        final SqlRowSet rs = jdbcTemplate.queryForRowSet(sql);

        String applicationTableName = null;
        if (rs.next()) {
            applicationTableName = rs.getString("registered_table_name");
        } else {
            throw new BadRequestException ("No Table configured..");
        }

        return applicationTableName;
    }

    @Override
    @Transactional
    public void saveSchedule(Long subId, JsonNode data) throws BadRequestException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        final String tableName = queryForApplicationTableName(subId);
        final List<ResultsetColumnHeaderData> columnHeaders = this.genericDataService.fillResultsetColumnHeaders(tableName);
        List<String> dataList = new ArrayList<>();
        for(ResultsetColumnHeaderData columnHeaderData:columnHeaders){
            if(columnHeaderData.getColumnName().indexOf("risks_id") > 0){
                dataList.add(data.get("riskId").asText());
            }
            else
                dataList.add(data.get(columnHeaderData.getColumnName()).asText());
        }
        final StringBuilder insertSql = new StringBuilder("INSERT INTO ");
        insertSql.append("\""+tableName+"\"");
        insertSql.append("(");
        insertSql.append(columnHeaders.stream().map(a -> "\""+a.getColumnName()+"\"").collect(Collectors.joining(",")));
        insertSql.append(") VALUES(");
        insertSql.append(dataList.stream().map(a -> "'"+a+"'").collect(Collectors.joining(",")) +")");
        jdbcTemplate.update(insertSql.toString());
    }
}
