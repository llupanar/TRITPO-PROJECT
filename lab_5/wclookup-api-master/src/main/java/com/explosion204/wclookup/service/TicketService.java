package com.explosion204.wclookup.service;

import com.explosion204.wclookup.exception.EntityNotFoundException;
import com.explosion204.wclookup.model.entity.Ticket;
import com.explosion204.wclookup.model.repository.TicketRepository;
import com.explosion204.wclookup.service.dto.TicketReplyDto;
import com.explosion204.wclookup.service.dto.identifiable.TicketDto;
import com.explosion204.wclookup.service.pagination.PageContext;
import com.explosion204.wclookup.service.pagination.PaginationModel;
import com.explosion204.wclookup.service.validation.annotation.ValidateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static java.time.ZoneOffset.UTC;

@Service
public class TicketService {
    private static final String TICKET_REPLY_SUBJECT_MESSAGE = "ticket_reply_subject";

    private final TicketRepository ticketRepository;
    private final MailService mailService;
    private final MessageSourceService messageSourceService;

    public TicketService(
            TicketRepository ticketRepository,
            MailService mailService,
            MessageSourceService messageSourceService
    ) {
        this.ticketRepository = ticketRepository;
        this.mailService = mailService;
        this.messageSourceService = messageSourceService;
    }

    public PaginationModel<TicketDto> findAll(PageContext pageContext, boolean skipResolved) {
        PageRequest pageRequest = pageContext.toPageRequest();
        Page<Ticket> ticketPage = skipResolved
                ? ticketRepository.findAllByResolvedFalse(pageRequest)
                : ticketRepository.findAll(pageRequest);
        Page<TicketDto> ticketDtoPage = ticketPage.map(TicketDto::fromTicket);

        return PaginationModel.fromPage(ticketDtoPage);
    }

    public TicketDto findById(long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Ticket.class));
        return TicketDto.fromTicket(ticket);
    }

    @ValidateDto
    public TicketDto create(TicketDto ticketDto) {
        Ticket ticket = ticketDto.toTicket();

        LocalDateTime creationTime = LocalDateTime.now(UTC);
        ticket.setCreationTime(creationTime);
        ticket.setResolved(false); // just in case when this flag is accidentally true

        Ticket savedTicket = ticketRepository.save(ticket);
        return TicketDto.fromTicket(savedTicket);
    }

    @ValidateDto
    public void resolve(TicketReplyDto replyDto, long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException(Ticket.class));

        String to = ticket.getEmail();
        String subject = messageSourceService.getString(TICKET_REPLY_SUBJECT_MESSAGE);
        mailService.sendEmail(to, subject, replyDto.getReply());

        ticket.setResolved(true);
        ticketRepository.save(ticket);
    }

    public void delete(long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Ticket.class));
        ticketRepository.delete(ticket);
    }
}
