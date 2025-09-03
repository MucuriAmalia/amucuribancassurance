package com.brokersystems.brokerapp.customscreens.service;

import com.brokersystems.brokerapp.customscreens.model.ResultsetColumnHeaderData;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Service
public class GenericDataServiceImpl implements GenericDataService {

    @Autowired
    DataSource dataSource;

    @Override
    public List<ResultsetColumnHeaderData> fillResultsetColumnHeaders(String datatable) throws BadRequestException {
        final SqlRowSet columnDefinitions = getDatatableMetaData(datatable);

        final List<ResultsetColumnHeaderData> columnHeaders = new ArrayList<>();

        columnDefinitions.beforeFirst();
        while (columnDefinitions.next()) {
            final String columnName = columnDefinitions.getString("COLUMN_NAME");
            final String isNullable = columnDefinitions.getString("IS_NULLABLE");
            final String isPrimaryKey = columnDefinitions.getString("COLUMN_KEY");
            final String columnType = columnDefinitions.getString("DATA_TYPE");
            final Long columnLength = columnDefinitions.getLong("CHARACTER_MAXIMUM_LENGTH");

            final boolean columnNullable = "YES".equalsIgnoreCase(isNullable);
            final boolean columnIsPrimaryKey = "PRI".equalsIgnoreCase(isPrimaryKey);

//            List<ResultsetColumnValueData> columnValues = new ArrayList<>();
//            String codeName = null;
//            if ("varchar".equalsIgnoreCase(columnType)) {
//
//                final int codePosition = columnName.indexOf("_cv");
//                if (codePosition > 0) {
//                    codeName = columnName.substring(0, codePosition);
//
//                    columnValues = retreiveColumnValues(codeName);
//                }
//
//            } else if ("int".equalsIgnoreCase(columnType)) {
//
//                final int codePosition = columnName.indexOf("_cd");
//                if (codePosition > 0) {
//                    codeName = columnName.substring(0, codePosition);
//                    columnValues = retreiveColumnValues(codeName);
//                }
//            }
//            if (codeName == null) {
//                final SqlRowSet rsValues = getDatatableCodeData(datatable, columnName);
//                Integer codeId = null;
//                while (rsValues.next()) {
//                    codeId = rsValues.getInt("id");
//                    codeName = rsValues.getString("code_name");
//                }
//                columnValues = retreiveColumnValues(codeId);
//
//            }

            final ResultsetColumnHeaderData rsch = ResultsetColumnHeaderData.detailed(columnName, columnType, columnLength, columnNullable,
                    columnIsPrimaryKey, null, null);

            columnHeaders.add(rsch);
        }

        return columnHeaders;
    }

    private SqlRowSet getDatatableMetaData(final String datatable) throws BadRequestException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        final String sql ="select c.COLUMN_NAME, c.IS_NULLABLE, c.DATA_TYPE, c.CHARACTER_MAXIMUM_LENGTH, \n" +
                "CASE WHEN EXISTS(SELECT 1 FROM INFORMATION_SCHEMA.constraint_column_usage k WHERE c.table_name = k.table_name and k.column_name = c.column_name) \n" +
                "     THEN 'PRI' ELSE '' END as COLUMN_KEY\n" +
                "                from INFORMATION_SCHEMA.COLUMNS c  where  c.TABLE_NAME = '"+ datatable +"'\n" +
                "                order by c.ORDINAL_POSITION";

        //System.out.println(sql);

        final SqlRowSet columnDefinitions = jdbcTemplate.queryForRowSet(sql);
        if (columnDefinitions.next()) { return columnDefinitions; }

        throw new BadRequestException("Table not found..."+datatable);
    }

}
