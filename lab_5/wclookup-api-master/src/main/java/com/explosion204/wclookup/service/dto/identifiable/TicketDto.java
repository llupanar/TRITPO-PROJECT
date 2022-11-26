package com.explosion204.wclookup.service.dto.identifiable;

import com.explosion204.wclookup.model.entity.Ticket;
import com.explosion204.wclookup.service.validation.annotation.DtoClass;
import com.explosion204.wclookup.service.validation.annotation.IdentifiableDtoConstraint;
import com.explosion204.wclookup.service.validation.annotation.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@IdentifiableDtoConstraint
@DtoClass
public class TicketDto extends IdentifiableDto {
    @Size(min = 1, max = 140)
    private String subject;

    @Size(min = 1, max = 1024)
    private String text;

    @Size(min = 3, max = 100)
    @Email
    private String email;

    @Nullable
    private LocalDateTime creationTime;
    private boolean resolved;

    public Ticket toTicket() {
        Ticket ticket = new Ticket();

        ticket.setId(id);
        ticket.setSubject(subject);
        ticket.setText(text);
        ticket.setEmail(email);
        ticket.setResolved(resolved);

        return ticket;
    }

    public static TicketDto fromTicket(Ticket ticket) {
        TicketDto ticketDto = new TicketDto();

        ticketDto.id = ticket.getId();
        ticketDto.subject = ticket.getSubject();
        ticketDto.text = ticket.getText();
        ticketDto.email = ticket.getEmail();
        ticketDto.resolved = ticket.isResolved();
        ticketDto.creationTime = ticket.getCreationTime();

        return ticketDto;
    }
}
