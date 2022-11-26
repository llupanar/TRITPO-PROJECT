package com.explosion204.wclookup.service.dto;

import com.explosion204.wclookup.service.validation.annotation.DtoClass;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@DtoClass
public class ToiletFilterDto {
    private Double latitude;
    private Double longitude;
    private Double radius;

    public boolean hasNoNullAttributes() {
        return latitude != null && longitude != null && radius != null;
    }
}
