package com.explosion204.wclookup.service.dto.identifiable;

import com.explosion204.wclookup.model.entity.Review;
import com.explosion204.wclookup.service.validation.annotation.DtoClass;
import com.explosion204.wclookup.service.validation.annotation.IdentifiableDtoConstraint;
import com.explosion204.wclookup.service.validation.annotation.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@IdentifiableDtoConstraint
@DtoClass
public class ReviewDto extends IdentifiableDto {
    private long userId;
    private long toiletId;

    @DecimalMin("1.0")
    @DecimalMax("5.0")
    private Double rating;

    @Size(min = 1, max = 140)
    private String text;

    @Nullable
    private LocalDateTime creationTime;

    public Review toReview() {
        Review review = new Review();

        review.setId(id);
        review.setText(text);
        review.setRating(rating);
        review.setCreationTime(creationTime);

        return review;
    }

    public static ReviewDto fromReview(Review review) {
        ReviewDto reviewDto = new ReviewDto();

        reviewDto.id = review.getId();
        reviewDto.userId = review.getUser().getId();
        reviewDto.toiletId = review.getToilet().getId();
        reviewDto.rating = review.getRating();
        reviewDto.text = review.getText();
        reviewDto.creationTime = review.getCreationTime();

        return reviewDto;
    }
}
