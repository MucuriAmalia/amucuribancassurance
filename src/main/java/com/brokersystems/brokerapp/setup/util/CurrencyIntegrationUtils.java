package com.brokersystems.brokerapp.setup.util;

import com.brokersystems.brokerapp.setup.dto.ExchangeRatesDTO;
import com.brokersystems.brokerapp.setup.repository.OrganizationRepository;
import com.brokersystems.brokerapp.uw.repository.EndorsementPolicyQueries;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.io.IOException;

@Component
public class CurrencyIntegrationUtils {

    @Autowired
    private DataSource dataSource;

    static final String apiURl = "http://api.exchangeratesapi.io/v1/latest?access_key=63f35f19b513d744a2c79277fbe454ed";

    public void updateExchangeRates() throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String baseCurrency = jdbcTemplate.queryForObject("select sbc.cur_iso_code  from sys_brk_organization sbo join sys_brk_currencies sbc on sbo.org_cur_code  = sbc.cur_code", String.class);
        String userJson = restTemplate.getForObject(apiURl, String.class);
        ObjectMapper mapper = new ObjectMapper();
        ExchangeRatesDTO ratesDTO = mapper.readValue(userJson,ExchangeRatesDTO.class);

    }


}
