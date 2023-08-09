package com.windsor.mockbank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class Page<T> {

    @Schema(example = "50")
    private Integer limit;

    @Schema(description = "Number of records to skip", example = "50")
    private Integer offset;

    @Schema(example = "100")
    private Integer total;
    private List<T> results;
}
