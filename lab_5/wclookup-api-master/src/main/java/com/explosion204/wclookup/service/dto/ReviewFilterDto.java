package com.explosion204.wclookup.service.dto;

import com.explosion204.wclookup.service.validation.annotation.DtoClass;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@NoArgsConstructor
@DtoClass
public class ReviewFilterDto {
    private Long toiletId;

    @Min(1)
    @Max(168)
    private Integer hours;
}
