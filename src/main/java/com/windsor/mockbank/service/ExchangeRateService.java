package com.windsor.mockbank.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.windsor.mockbank.dao.ExchangeRateDao;
import com.windsor.mockbank.model.ExchangeRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Service
public class ExchangeRateService {

    @Autowired
    private ExchangeRateDao exchangeRateDao;
    private ObjectMapper objectMapper = new ObjectMapper();

    public Integer createData(ExchangeRate exchangeRate) throws JsonProcessingException {
        // 將Unix時間戳轉換為GMT+8時區的日期時間字串
        String lastUpdateGMTPlus8 = convertUnixTimestampToGMTPlus8String(exchangeRate.getTimeLastUpdateUnix());
        String nextUpdateGMTPlus8 = convertUnixTimestampToGMTPlus8String(exchangeRate.getTimeNextUpdateUnix());

        exchangeRate.setTimeLastUpdateUtc(lastUpdateGMTPlus8);
        exchangeRate.setTimeNextUpdateUtc(nextUpdateGMTPlus8);

        // 將conversion_rates轉換成json格式
        String conversionRatesJson = objectMapper.writeValueAsString(exchangeRate.getConversionRates());

        return exchangeRateDao.createData(exchangeRate, conversionRatesJson);
    }

    public ExchangeRate getData(Integer id) {
        return exchangeRateDao.getData(id);
    }

    private String convertUnixTimestampToGMTPlus8String(Long unixTimestamp) {
        Date date = new Date(unixTimestamp * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return sdf.format(date);
    }

    public ExchangeRate getLatestData() {
        return exchangeRateDao.getLatestData();
    }
}
