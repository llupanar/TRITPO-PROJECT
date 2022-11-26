package com.explosion204.wclookup.service.dto;

import com.explosion204.wclookup.service.validation.annotation.DtoClass;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@DtoClass
public class TicketReplyDto {
    @Size(min = 1, max = 2048)
    private String reply;
}
