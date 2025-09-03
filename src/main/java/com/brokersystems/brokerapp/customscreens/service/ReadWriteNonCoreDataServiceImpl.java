package com.brokersystems.brokerapp.customscreens.service;

import com.brokersystems.brokerapp.customscreens.model.ColumnForm;
import com.brokersystems.brokerapp.customscreens.model.RegisteredTable;
import com.brokersystems.brokerapp.customscreens.model.RegisteredTableModel;
import com.brokersystems.brokerapp.customscreens.model.TableForm;
import com.brokersystems.brokerapp.customscreens.repository.RegisteredTableModelRepo;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReadWriteNonCoreDataServiceImpl implements ReadWriteNonCoreDataService {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private RegisteredTableModelRepo registeredTableModelRepo;

    @Autowired
    private GenericDataService genericDataService;

    private final static String DATATABLE_NAME_REGEX_PATTERN = "^[a-zA-Z][a-zA-Z0-9\\-_\\s]{0,48}[a-zA-Z0-9]$";

    private final static HashMap<String, String> apiTypeToPostgres = new HashMap<String, String>() {

        {
            put("string", "VARCHAR");
            put("number", "INT8");
            put("boolean", "BOOL");
            put("decimal", "DECIMAL");
            put("date", "DATE");
            put("datetime", "DATETIME");
            put("text", "TEXT");
            put("dropdown", "VARCHAR");
        }
    };

    @Override
    @Transactional
    public void createCustomTable(TableForm tableForm) throws BadRequestException {
        if(tableForm.getColumnFormList().isEmpty()){
            throw new BadRequestException("The Table must have at least one column");
        }
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        final String apptableName = tableForm.getApptableName();
        final String datatableName = tableForm.getDatatableName();
        validateDatatableName(tableForm.getDatatableName(),tableForm.getAppTableNameKey());
        validateAppTable(tableForm.getApptableName());
        final String fkColumnName = apptableName.substring(2) + "_id";
        final String dataTableNameAlias = datatableName.toLowerCase().replaceAll("\\s", "_");
        final String fkName = dataTableNameAlias + "_" + fkColumnName;
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder = sqlBuilder.append("CREATE TABLE \"" + datatableName + "\" (");
        sqlBuilder = sqlBuilder.append("" + fkColumnName + " int8 NOT NULL, ");
        for (final ColumnForm column : tableForm.getColumnFormList()) {
            if(column.getName()==null || column.getName().isEmpty()){
                throw new BadRequestException("The column name must not be null or empty");
            }
            parseDatatableColumnObjectForCreate(column, sqlBuilder);
            if(column.getType()!=null && column.getType().equals("dropdown")){
                if(!StringUtils.isBlank(column.getOptions())){
                    if(!column.getOptions().contains(",")){
                        throw new BadRequestException("Drop down options must be separated with commas");
                    }
                    this._registerDataTableOptions(apptableName,column.getName(),column.getOptions());
                }
            }
        }
        sqlBuilder = sqlBuilder.delete(sqlBuilder.length() - 2, sqlBuilder.length());
        sqlBuilder = sqlBuilder.append(", pri_code int8 NOT NULL");
        sqlBuilder = sqlBuilder.append(",CONSTRAINT "+datatableName.replaceAll(" ", "_")+"_pkey PRIMARY KEY (pri_code)");
        sqlBuilder = sqlBuilder.append(", CONSTRAINT fk_" + fkName + " ")
                .append(" FOREIGN KEY (" + fkColumnName + ") ").append("REFERENCES " + apptableName + " (risk_id))");
        System.out.println(sqlBuilder.toString());
        jdbcTemplate.execute(sqlBuilder.toString());
        Integer category = 100;
        this._registerDataTable(apptableName, datatableName, category, tableForm.getAppTableNameKey());

    }

    @Override
    @Transactional
    public void updateCustomTable(TableForm tableForm) throws BadRequestException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        final String datatableName = tableForm.getDatatableName();

        // Get existing columns in the table
        List<ColumnForm> existingColumns = getTableColumns(datatableName);
        List<String> existingColumnNames = existingColumns.stream()
                .map(ColumnForm::getName)
                .collect(Collectors.toList());

        // Loop through the columns in tableForm and determine the update action
        for (ColumnForm column : tableForm.getColumnFormList()) {
            if (column.getName() == null || column.getName().isEmpty()) {
                throw new BadRequestException("The column name must not be null or empty");
            }

            // Ensure pri_code maintains its primary key attribute
            if ("pri_code".equals(column.getName())) {
                // If pri_code column is not present, add it as primary key
                if (!existingColumnNames.contains("pri_code")) {
                    String addPrimaryKeySql = "ALTER TABLE \"" + datatableName + "\" ADD COLUMN \"pri_code\" SERIAL PRIMARY KEY";
                    jdbcTemplate.execute(addPrimaryKeySql);
                }
                // Skip further processing for pri_code since it's already set as primary key
                continue;
            }

            boolean columnExists = existingColumnNames.contains(column.getName());

            if (columnExists) {
                // Update the existing column's attributes
                String alterColumnSql = buildModifyColumnSQL(datatableName, column);
                jdbcTemplate.execute(alterColumnSql);

                // Update NOT NULL constraint based on the mandatory attribute
                if ("true".equalsIgnoreCase(column.getMandatory())) {
                    String notNullSql = "ALTER TABLE \"" + datatableName + "\" ALTER COLUMN \"" + column.getName() + "\" SET NOT NULL";
                    jdbcTemplate.execute(notNullSql);
                } else {
                    String dropNotNullSql = "ALTER TABLE \"" + datatableName + "\" ALTER COLUMN \"" + column.getName() + "\" DROP NOT NULL";
                    jdbcTemplate.execute(dropNotNullSql);
                }

                // Update column length if applicable
                if (column.getLength() != null && column.getLength() > 0) {
                    String modifyLengthSql = "ALTER TABLE \"" + datatableName + "\" ALTER COLUMN \"" + column.getName() + "\" TYPE " + getColumnTypeWithLength(column);
                    jdbcTemplate.execute(modifyLengthSql);
                }

                existingColumnNames.remove(column.getName()); // Track columns that are not dropped
            } else {
                // Add a new column
                String addColumnSql = buildAddColumnSQL(datatableName, column);
                jdbcTemplate.execute(addColumnSql);

                // Set NOT NULL constraint based on the mandatory attribute
                if ("true".equalsIgnoreCase(column.getMandatory())) {
                    String notNullSql = "ALTER TABLE \"" + datatableName + "\" ALTER COLUMN \"" + column.getName() + "\" SET NOT NULL";
                    jdbcTemplate.execute(notNullSql);
                }
            }

            // Handle dropdown options if type is 'dropdown'
            if ("dropdown".equals(column.getType()) && !StringUtils.isBlank(column.getOptions())) {
                if (!column.getOptions().contains(",")) {
                    throw new BadRequestException("Dropdown options must be separated with commas");
                }
                this._registerDataTableOptions(tableForm.getApptableName(), column.getName(), column.getOptions());
            }
        }

        // Drop columns that are no longer present in the update request, excluding pri_code
        for (String columnToDrop : existingColumnNames) {
            if (!"pri_code".equals(columnToDrop)) {
                String dropColumnSql = buildDropColumnSQL(datatableName, columnToDrop);
                jdbcTemplate.execute(dropColumnSql);
            }
        }
    }

    private String getColumnTypeWithLength(ColumnForm column) throws BadRequestException {
        String columnType = getColumnType(column.getType());
        if ("VARCHAR".equals(columnType) || "DECIMAL".equals(columnType)) {
            return columnType + "(" + column.getLength() + ")";
        }
        return columnType;
    }

    // Utility method to check if a column exists in the table
    private boolean checkIfColumnExists(String tableName, String columnName) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String sql = "SELECT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = ? AND column_name = ?)";
        return jdbcTemplate.queryForObject(sql, new Object[]{tableName, columnName}, Boolean.class);
    }


    private String buildAddColumnSQL(String tableName,ColumnForm columnForm) throws BadRequestException {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("ALTER TABLE \"")
                .append(tableName)
                .append("\" ADD COLUMN \"")
                .append(columnForm.getName())
                .append("\" ")
                .append(getColumnType(columnForm.getType()));

        // Append length only for types that require it
        if (("VARCHAR".equals(getColumnType(columnForm.getType()))
                || "DECIMAL".equals(getColumnType(columnForm.getType())))
                && columnForm.getLength() != null) {
            sqlBuilder.append("(")
                    .append(columnForm.getLength())
                    .append(")");
        }

        if ("NOT NULL".equalsIgnoreCase(columnForm.getMandatory())) {
            sqlBuilder.append(" NOT NULL");
        }

        return sqlBuilder.toString();
    }

    private String buildDropColumnSQL(String datatableName, String columnName) {
        return "ALTER TABLE \"" + datatableName + "\" DROP COLUMN \"" + columnName + "\"";
    }


    // Utility method to get the column type SQL representation
    private String getColumnType(String type) throws BadRequestException{
        switch (type.toLowerCase()) {
            case "string":
                return "VARCHAR";
            case "number":
                return "BIGINT";
            case "decimal":
                return "DECIMAL";
            case "boolean":
                return "BOOLEAN";
            case "date":
                return "DATE";
            case "datetime":
                return "TIMESTAMP";
            case "text":
                return "TEXT";
            default:
                throw new BadRequestException("Unsupported column type: " + type);
        }
    }

    private String buildModifyColumnSQL(String tableName, ColumnForm columnForm) throws BadRequestException {
        StringBuilder sqlBuilder = new StringBuilder();
        String columnType = getColumnType(columnForm.getType());

        sqlBuilder.append("ALTER TABLE \"")
                .append(tableName)
                .append("\" ALTER COLUMN \"")
                .append(columnForm.getName())
                .append("\" SET DATA TYPE ")
                .append(columnType);

        // Add the USING clause if the new type is incompatible
        if (!columnType.equalsIgnoreCase("VARCHAR")) {
            sqlBuilder.append(" USING \"")
                    .append(columnForm.getName())
                    .append("\"::")
                    .append(columnType);
        }

        // Handle NOT NULL constraint if specified
        if ("true".equalsIgnoreCase(columnForm.getMandatory())) {
            sqlBuilder.append(", ALTER COLUMN \"")
                    .append(columnForm.getName())
                    .append("\" SET NOT NULL");
        } else {
            sqlBuilder.append(", ALTER COLUMN \"")
                    .append(columnForm.getName())
                    .append("\" DROP NOT NULL");
        }

        return sqlBuilder.toString();
    }


    public List<ColumnForm> getTableColumns(String tableName) {
        String sql = "SELECT column_name, data_type, character_maximum_length, is_nullable " +
                "FROM information_schema.columns " +
                "WHERE table_name = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<ColumnForm> columns = jdbcTemplate.query(sql, new Object[]{tableName}, (rs, rowNum) -> {
            ColumnForm column = new ColumnForm();
            String dataType = rs.getString("data_type");
            String isNullable = rs.getString("is_nullable");
            String columnType;
            switch (dataType) {
                case "bigint":
                    columnType = "Number";
                    break;
                case "character varying":
                    columnType = "String";
                    break;
                case "numeric":
                    columnType = "Decimal";
                    break;
                case "boolean":
                    columnType = "Boolean";
                    break;
                case "date":
                    columnType = "Date";
                    break;
                case "timestamp without time zone":
                    columnType = "Datetime";
                    break;
                case "text":
                    columnType = "Text";
                    break;
                default:
                    columnType = "Unknown"; // Handle other data types accordingly
                    break;
            }
            String mandatory = "NO".equals(isNullable) ? "true" : "false";
            column.setName(rs.getString("column_name"));
            column.setType(columnType);
            column.setLength(rs.getInt("character_maximum_length"));
            column.setMandatory(mandatory);


            return column;
        });
        return columns;
    }



    private void validateDatatableName(final String name, Long key) throws BadRequestException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        if (name == null || name.isEmpty()) {
            throw new BadRequestException("Custom table name must not be blank.");
        } else if (!name.matches(DATATABLE_NAME_REGEX_PATTERN)) { throw new BadRequestException( "Invalid Custom table name."); }
        int count  = jdbcTemplate.queryForObject("select count(1) from x_registered_table where app_table_key_value=?",new Object[] {key},Integer.class);
        if(count > 0){
            throw new BadRequestException("Mapping has already been done for this sub class. Please select another one");
        }
        SQLInjectionValidator.validateSQLInput(name);
    }

    private void validateAppTable(final String appTable) throws BadRequestException {

        if (appTable.equalsIgnoreCase("sys_brk_risks")) { return; }

        throw new BadRequestException( "Invalid Application Table: " + appTable);
    }

    private void parseDatatableColumnObjectForCreate(final ColumnForm column, StringBuilder sqlBuilder) {

        String name = column.getName();
        final String type = column.getType();
        final Integer length = column.getLength();
        final Boolean mandatory = column.getMandatory()!=null && "on".equalsIgnoreCase(column.getMandatory());


        final String mysqlType = apiTypeToPostgres.get(type);
        sqlBuilder = sqlBuilder.append("\"" + name + "\" " + mysqlType);

        if (type != null) {
            if (type.equalsIgnoreCase("String")) {
                sqlBuilder = sqlBuilder.append("(" + length + ")");
            } else if (type.equalsIgnoreCase("Decimal")) {
                sqlBuilder = sqlBuilder.append("(19,6)");
            }
        }

        if (mandatory) {
            sqlBuilder = sqlBuilder.append(" NOT NULL");
        } else {
            sqlBuilder = sqlBuilder.append(" NULL");
        }

        sqlBuilder = sqlBuilder.append(", ");
    }

    private void _registerDataTableOptions(final String dataTableName, final String tableColumn, final String options) {
        final NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, Object> paramMap = new HashMap<>(3);
        final String registerDatatableSql = "insert into x_registered_table_options (registered_table_name, registered_option_column,registered_options) values ( :dataTableName, :optionColumn,:optionValue)";
        paramMap.put("dataTableName", dataTableName);
        paramMap.put("optionColumn", tableColumn);
        paramMap.put("optionValue", options);
        namedParameterJdbcTemplate.update(registerDatatableSql, paramMap);


    }

    private void _registerDataTable(final String applicationTableName, final String dataTableName, final Integer category, final Long appTablePk) throws BadRequestException {
        final NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        validateAppTable(applicationTableName);
        validateDatatableName(dataTableName,appTablePk);
        assertDataTableExists(dataTableName);

        Map<String, Object> paramMap = new HashMap<>(3);
        final String registerDatatableSql = "insert into x_registered_table (registered_table_name, application_table_name,category,app_table_key_value) values ( :dataTableName, :applicationTableName, :category,:appTablePk)";
        paramMap.put("dataTableName", dataTableName);
        paramMap.put("applicationTableName", applicationTableName);
        paramMap.put("category", category);
        paramMap.put("appTablePk", appTablePk);
        namedParameterJdbcTemplate.update(registerDatatableSql, paramMap);


    }

    private void assertDataTableExists(final String datatableName) throws BadRequestException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        final String sql = "select case when (select 1 from information_schema.tables where table_name = ?) = 1 then 'true' else  'false' end";
        final String dataTableExistsString = jdbcTemplate.queryForObject(sql, String.class, new Object[] { datatableName });
        final boolean dataTableExists = new Boolean(dataTableExistsString);
        if (!dataTableExists) { throw new BadRequestException( "Invalid Data Table: " + datatableName); }
    }

    @Override
    public List<RegisteredTableModel> getSubclassSchedules(Long subId) {
          return registeredTableModelRepo.getAllSchedules(subId);
    }

    @Override
    public void deleteScheduleTable(RegisteredTable table) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(" DELETE FROM  x_registered_table WHERE category =? AND app_table_key_value = ?",
                table.getCategory(),table.getKeyValue());
        jdbcTemplate.update("delete from x_registered_table_options where registered_table_name =?",table.getTableName());
    }
}
