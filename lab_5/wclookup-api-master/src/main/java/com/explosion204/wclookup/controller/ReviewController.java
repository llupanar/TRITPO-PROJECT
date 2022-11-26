package com.explosion204.wclookup.controller;

import com.explosion204.wclookup.service.ReviewService;
import com.explosion204.wclookup.service.dto.ReviewFilterDto;
import com.explosion204.wclookup.service.dto.identifiable.ReviewDto;
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

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or #filterDto.toiletId ne null and #filterDto.hours eq null")
    public ResponseEntity<PaginationModel<ReviewDto>> getReviews(
            @ModelAttribute ReviewFilterDto filterDto,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize
    ) {
        PageContext pageContext = PageContext.of(page, pageSize);
        PaginationModel<ReviewDto> reviews = reviewService.find(filterDto, pageContext);

        return new ResponseEntity<>(reviews, OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDto> getReview(@PathVariable("id") long id) {
        ReviewDto reviewDto = reviewService.findById(id);
        return new ResponseEntity<>(reviewDto, OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or authentication.name eq T(String).valueOf(#reviewDto.userId)")
    public ResponseEntity<ReviewDto> createReview(@RequestBody ReviewDto reviewDto) {
        reviewDto.setId(null); // new entity cannot have id
        ReviewDto createdReviewDto = reviewService.create(reviewDto);

        return new ResponseEntity<>(createdReviewDto, CREATED);
    }

    // security logic for this endpoint belongs to service layer
    // main reason is to reduce amount of db calls
    @PatchMapping("/{id}")
    public ResponseEntity<ReviewDto> updateReview(@PathVariable("id") long id, @RequestBody ReviewDto reviewDto) {
        reviewDto.setId(id);
        ReviewDto updatedReviewDto = reviewService.update(reviewDto);

        return new ResponseEntity<>(updatedReviewDto, OK);
    }

    // security logic for this endpoint belongs to service layer
    // main reason is to reduce amount of db calls
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable("id") long id) {
        reviewService.delete(id);
        return new ResponseEntity<>(NO_CONTENT);
    }
}
