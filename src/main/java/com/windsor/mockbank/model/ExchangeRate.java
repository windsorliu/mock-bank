package com.windsor.mockbank.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRate {

    @Schema(example = "1")
    private Integer id;

    @Schema(example = "success")
    private String result;

    @Schema(example = "https://www.exchangerate-api.com/docs")
    private String documentation;

    @JsonProperty("terms_of_use")
    @Schema(example = "https://www.exchangerate-api.com/terms")
    private String termsOfUse;

    @Schema(example = "1689811201")
    @JsonProperty("time_last_update_unix")
    private Long timeLastUpdateUnix;

    @Schema(example = "2023-08-09 18:28:20")
    @JsonProperty("time_last_update_utc")
    private String timeLastUpdateUtc;

    @Schema(example = "1689897601")
    @JsonProperty("time_next_update_unix")
    private Long timeNextUpdateUnix;

    @Schema(example = "2023-08-09 19:28:20")
    @JsonProperty("time_next_update_utc")
    private String timeNextUpdateUtc;

    @Schema(example = "USD")
    @JsonProperty("base_code")
    private String baseCode;

    @Schema(example = "{\"USD\":1,\"AED\":3.6725,\"AFN\":85.8361...}")
    @JsonProperty("conversion_rates")
    private Map<String, BigDecimal> conversionRates;
}
