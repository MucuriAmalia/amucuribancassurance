package com.brokersystems.brokerapp.uw.mappers;

import com.brokersystems.brokerapp.uw.dtos.EndorsementsDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EndorsementsDtoMapper implements RowMapper<EndorsementsDTO> {

    @Override
    public EndorsementsDTO mapRow(ResultSet rs, int i) throws SQLException {

        final Long policyId = rs.getLong("pol_id");
        final String policyNo = rs.getString("pol_no");
        final String clientPolNo = rs.getString("pol_client_pol_no");
        final String polRevNo = rs.getString("pol_rev_no");
        final String clientName = rs.getString("client");
        final String agentName = rs.getString("acct_name");
        final String binderName = rs.getString("bin_name");
        final Long polUwYr = rs.getLong("pol_uw_yr");
        final Boolean renewable = rs.getBoolean("renewable");
        final String product = rs.getString("product");
        final Date wefDate = rs.getDate("wefDate");
        final Date wetDate = rs.getDate("wetDate");
        final String currency = rs.getString("currency");
        final String username = rs.getString("username");
        final Long count = rs.getLong("total_rows");
        final String authComments = rs.getString("auth_comments");
        final String transType = rs.getString("transType");

        // final String idNo = rs.getString("idNo");

        return EndorsementsDTO.instance(policyId,policyNo,clientPolNo,polRevNo,clientName,agentName,binderName,polUwYr,renewable,product,wefDate,wetDate,currency,username,count,null, authComments,transType);
    }
}