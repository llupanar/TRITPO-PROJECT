package com.explosion204.wclookup.controller;

import com.explosion204.wclookup.security.util.AuthUtil;
import com.explosion204.wclookup.service.ToiletService;
import com.explosion204.wclookup.service.dto.ToiletFilterDto;
import com.explosion204.wclookup.service.dto.identifiable.ToiletDto;
import com.explosion204.wclookup.service.pagination.PageContext;
import com.explosion204.wclookup.service.pagination.PaginationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.explosion204.wclookup.security.ApplicationAuthority.ADMIN;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/toilets")
public class ToiletController {
    private final ToiletService toiletService;
    private final AuthUtil authUtil;

    public ToiletController(ToiletService toiletService, AuthUtil authUtil) {
        this.toiletService = toiletService;
        this.authUtil = authUtil;
    }

    @GetMapping
    public ResponseEntity<PaginationModel<ToiletDto>> getToilets(
            @ModelAttribute ToiletFilterDto toiletFilterDto,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize
    ) {
        PageContext pageContext = PageContext.of(page, pageSize);
        // admin can load get all toilets, even not confirmed
        boolean onlyConfirmed = !authUtil.hasAuthority(ADMIN.getAuthority());
        PaginationModel<ToiletDto> toilets = toiletService.find(toiletFilterDto, onlyConfirmed, pageContext);
        return new ResponseEntity<>(toilets, OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ToiletDto> getToilet(@PathVariable("id") long id) {
        // admin can view not confirmed toilets
        boolean onlyConfirmed = !authUtil.hasAuthority(ADMIN.getAuthority());
        ToiletDto toiletDto = toiletService.findById(id, onlyConfirmed);

        return new ResponseEntity<>(toiletDto, OK);
    }

    @GetMapping("/coordinates")
    public ResponseEntity<PaginationModel<ToiletDto>> getToiletByCoordinates(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize
    ) {
        PageContext pageContext = PageContext.of(page, pageSize);
        PaginationModel<ToiletDto> toilets = toiletService.find(latitude, longitude, pageContext);
        return new ResponseEntity<>(toilets, OK);
    }

    @PostMapping
    public ResponseEntity<ToiletDto> createToilet(@RequestBody ToiletDto toiletDto) {
        toiletDto.setId(null); // new entity cannot have id
        // user can only propose toilet (isConfirmed = false)
        boolean isProposal = !authUtil.hasAuthority(ADMIN.getAuthority());
        ToiletDto createdToiletDto = toiletService.create(toiletDto, isProposal);

        return new ResponseEntity<>(createdToiletDto, CREATED);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ToiletDto> updateToilet(@PathVariable("id") long id, @RequestBody ToiletDto toiletDto) {
        toiletDto.setId(id); // existing entity must have id
        ToiletDto updatedToiletDto = toiletService.update(toiletDto);

        return new ResponseEntity<>(updatedToiletDto, OK);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteToilet(@PathVariable("id") long id) {
        toiletService.delete(id);
        return new ResponseEntity<>(NO_CONTENT);
    }
}
