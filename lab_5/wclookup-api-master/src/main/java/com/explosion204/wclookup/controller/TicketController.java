package com.explosion204.wclookup.controller;

import com.explosion204.wclookup.service.TicketService;
import com.explosion204.wclookup.service.dto.TicketReplyDto;
import com.explosion204.wclookup.service.dto.identifiable.TicketDto;
import com.explosion204.wclookup.service.pagination.PageContext;
import com.explosion204.wclookup.service.pagination.PaginationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<PaginationModel<TicketDto>> getTickets(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(value = "skipResolved", required = false) boolean skipResolved
    ) {
        PageContext pageContext = PageContext.of(page, pageSize);
        PaginationModel<TicketDto> tickets = ticketService.findAll(pageContext, skipResolved);
        return new ResponseEntity<>(tickets, OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TicketDto> getTicket(@PathVariable("id") long id) {
        TicketDto ticketDto = ticketService.findById(id);
        return new ResponseEntity<>(ticketDto, OK);
    }

    @PostMapping
    public ResponseEntity<TicketDto> createTicket(@RequestBody TicketDto ticketDto) {
        ticketDto.setId(null);
        TicketDto createdTicketDto = ticketService.create(ticketDto);

        return new ResponseEntity<>(createdTicketDto, CREATED);
    }

    @PostMapping("/{id}/resolve")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> resolveTicket(@RequestBody TicketReplyDto replyDto, @PathVariable("id") long id) {
        ticketService.resolve(replyDto, id);
        return new ResponseEntity<>(NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteTicket(@PathVariable("id") long id) {
        ticketService.delete(id);
        return new ResponseEntity<>(NO_CONTENT);
    }
}
