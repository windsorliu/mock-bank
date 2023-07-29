package com.windsor.mockbank.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.windsor.mockbank.dao.ExchangeRateDao;
import com.windsor.mockbank.model.ExchangeRate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final static Logger log = LoggerFactory.getLogger(ExchangeRateDao.class);


    public Integer createData(ExchangeRate exchangeRate) {

        // 將Unix時間戳轉換為GMT+8時區的日期時間字串
        String lastUpdateGMTPlus8 = convertUnixTimestampToGMTPlus8String(exchangeRate.getTimeLastUpdateUnix());
        String nextUpdateGMTPlus8 = convertUnixTimestampToGMTPlus8String(exchangeRate.getTimeNextUpdateUnix());

        exchangeRate.setTimeLastUpdateUtc(lastUpdateGMTPlus8);
        exchangeRate.setTimeNextUpdateUtc(nextUpdateGMTPlus8);

        // 將conversion_rates轉換成json格式
        String conversionRatesJson;
        try {
            conversionRatesJson = objectMapper.writeValueAsString(exchangeRate.getConversionRates());
        } catch (JsonProcessingException e) {
            log.warn("Failed to store the conversion_rates of ExchangeRate.");
            throw new RuntimeException(e);
        }

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
}
