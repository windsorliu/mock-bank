package com.windsor.mockbank.rowmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.windsor.mockbank.model.ExchangeRate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ExchangeRateRowMapper implements RowMapper<ExchangeRate> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ExchangeRate mapRow(ResultSet resultSet, int i) throws SQLException {
        ExchangeRate exchangeRate = new ExchangeRate();

        exchangeRate.setId(resultSet.getInt("id"));
        exchangeRate.setResult(resultSet.getString("result"));
        exchangeRate.setDocumentation(resultSet.getString("documentation"));
        exchangeRate.setTermsOfUse(resultSet.getString("terms_of_use"));
        exchangeRate.setTimeLastUpdateUnix(resultSet.getObject("time_last_update_unix", Long.class));
        exchangeRate.setTimeLastUpdateUtc(resultSet.getString("time_last_update_utc"));
        exchangeRate.setTimeNextUpdateUnix(resultSet.getObject("time_next_update_unix", Long.class));
        exchangeRate.setTimeNextUpdateUtc(resultSet.getString("time_next_update_utc"));
        exchangeRate.setBaseCode(resultSet.getString("base_code"));

        Map<String, Object> conversionRates;

        try {
            conversionRates = objectMapper.readValue(
                    resultSet.getString("conversion_rates"),
                    HashMap.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        exchangeRate.setConversionRates(conversionRates);

        return exchangeRate;
    }
}

