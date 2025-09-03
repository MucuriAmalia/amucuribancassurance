package com.brokersystems.brokerapp.trans.mappers;

import com.brokersystems.brokerapp.trans.dtos.CommissionDTO;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CommDtoMapper  implements RowMapper<CommissionDTO> {

    @Override
    public CommissionDTO mapRow(ResultSet rs, int i) throws SQLException {
        final Long transId = rs.getLong("trans_receipt");
        final String receiptNo = rs.getString("trans_ref_no");
        final Date receiptDate = rs.getDate("trans_date");
        final String insuranceCo = rs.getString("acct_name");
        final BigDecimal totalCommission = rs.getBigDecimal("commission_amount");
        final String status  = rs.getString("trans_status");
        final BigDecimal originalAmount = rs.getBigDecimal("trans_net_amt");
        final BigDecimal balance = rs.getBigDecimal("trans_balance");
        return CommissionDTO.instance(transId,receiptNo,receiptDate,insuranceCo,totalCommission,status,originalAmount,balance);
    }

}
