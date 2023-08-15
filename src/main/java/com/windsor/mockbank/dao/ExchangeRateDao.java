package com.windsor.mockbank.dao;

import com.windsor.mockbank.model.ExchangeRate;
import com.windsor.mockbank.rowmapper.ExchangeRateRowMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ExchangeRateDao {

    private final static Logger log = LoggerFactory.getLogger(ExchangeRateDao.class);
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Integer createData(ExchangeRate exchangeRate, String conversionRatesJson) {
        String sql = "INSERT INTO exchange_rate(result, documentation, terms_of_use, " +
                "time_last_update_unix, time_last_update_utc, " +
                "time_next_update_unix, time_next_update_utc, " +
                "base_code, conversion_rates) " +
                "VALUES (:result, :documentation, :terms_of_use, " +
                ":time_last_update_unix, :time_last_update_utc, " +
                ":time_next_update_unix, :time_next_update_utc, :base_code, :conversion_rates)";

        Map<String, Object> map = new HashMap<>();
        map.put("result", exchangeRate.getResult());
        map.put("documentation", exchangeRate.getDocumentation());
        map.put("terms_of_use", exchangeRate.getTermsOfUse());
        map.put("time_last_update_unix", exchangeRate.getTimeLastUpdateUnix());
        map.put("time_last_update_utc", exchangeRate.getTimeLastUpdateUtc());
        map.put("time_next_update_unix", exchangeRate.getTimeNextUpdateUnix());
        map.put("time_next_update_utc", exchangeRate.getTimeNextUpdateUtc());
        map.put("base_code", exchangeRate.getBaseCode());
        map.put("conversion_rates", conversionRatesJson);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

        Integer id = keyHolder.getKey().intValue();
        log.info("ExchangeRate written successfully");

        return id;
    }

    public ExchangeRate getData(Integer id) {
        String sql = "SELECT id, result, documentation, terms_of_use, " +
                "time_last_update_unix, time_last_update_utc, " +
                "time_next_update_unix, time_next_update_utc, " +
                "base_code, conversion_rates " +
                "FROM exchange_rate WHERE id = :id";

        Map<String, Object> map = new HashMap<>();
        map.put("id", id);

        List<ExchangeRate> exchangeRateList = namedParameterJdbcTemplate.query(sql, map, new ExchangeRateRowMapper());

        if (exchangeRateList.isEmpty()) {
            return null;
        } else {
            return exchangeRateList.get(0);
        }
    }

    public ExchangeRate getLatestData() {
        String sql = "SELECT id, result, documentation, terms_of_use, " +
                "time_last_update_unix, time_last_update_utc, " +
                "time_next_update_unix, time_next_update_utc, " +
                "base_code, conversion_rates " +
                "FROM exchange_rate ORDER BY time_last_update_unix DESC LIMIT 1";

        Map<String, Object> map = new HashMap<>();

        List<ExchangeRate> exchangeRateList = namedParameterJdbcTemplate.query(sql, map, new ExchangeRateRowMapper());

        if (exchangeRateList.isEmpty()) {
            return null;
        } else {
            return exchangeRateList.get(0);
        }
    }
}
