package com.explosion204.wclookup.service;

import com.explosion204.wclookup.exception.EntityAlreadyExistsException;
import com.explosion204.wclookup.exception.EntityNotFoundException;
import com.explosion204.wclookup.model.entity.Review;
import com.explosion204.wclookup.model.entity.Toilet;
import com.explosion204.wclookup.model.entity.User;
import com.explosion204.wclookup.model.repository.ReviewRepository;
import com.explosion204.wclookup.model.repository.ReviewSpecificationBuilder;
import com.explosion204.wclookup.model.repository.ToiletRepository;
import com.explosion204.wclookup.model.repository.UserRepository;
import com.explosion204.wclookup.security.util.AuthUtil;
import com.explosion204.wclookup.service.dto.ReviewFilterDto;
import com.explosion204.wclookup.service.dto.identifiable.ReviewDto;
import com.explosion204.wclookup.service.pagination.PageContext;
import com.explosion204.wclookup.service.pagination.PaginationModel;
import com.explosion204.wclookup.service.validation.annotation.ValidateDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.explosion204.wclookup.model.repository.FieldName.CREATION_TIME;
import static com.explosion204.wclookup.security.ApplicationAuthority.ADMIN;
import static java.time.ZoneOffset.UTC;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ToiletRepository toiletRepository;
    private final UserRepository userRepository;
    private final AuthUtil authUtil;

    public ReviewService(
            ReviewRepository reviewRepository,
            ToiletRepository toiletRepository,
            UserRepository userRepository,
            AuthUtil authUtil
    ) {
        this.reviewRepository = reviewRepository;
        this.toiletRepository = toiletRepository;
        this.userRepository = userRepository;
        this.authUtil = authUtil;
    }

    @ValidateDto
    public PaginationModel<ReviewDto> find(ReviewFilterDto filterDto, PageContext pageContext) {
        LocalDateTime targetTime = filterDto.getHours() != null
                ? LocalDateTime.now(UTC).minusHours(filterDto.getHours())
                : null;
        Specification<Review> specification = new ReviewSpecificationBuilder()
                .byToiletId(filterDto.getToiletId())
                .byCreationTimeAfter(targetTime)
                .build();
        Sort creationTimeAscSort = Sort.by(Sort.Direction.DESC, CREATION_TIME);

        PageRequest pageRequest = pageContext.toPageRequest(creationTimeAscSort);
        Page<ReviewDto> page = reviewRepository.findAll(specification, pageRequest)
                .map(ReviewDto::fromReview);
        return PaginationModel.fromPage(page);
    }

    public ReviewDto findById(long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Review.class));
        return ReviewDto.fromReview(review);
    }

    @ValidateDto
    public ReviewDto create(ReviewDto reviewDto) {
        Review review = reviewDto.toReview();

        Toilet toilet = toiletRepository.findById(reviewDto.getToiletId())
                .orElseThrow(() -> new EntityNotFoundException(Toilet.class));
        User user = userRepository.findById(reviewDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(User.class));

        if (reviewRepository.findByUserAndToilet(user, toilet).isPresent()) {
            throw new EntityAlreadyExistsException(Review.class);
        }

        LocalDateTime creationTime = LocalDateTime.now(UTC);
        review.setCreationTime(creationTime);
        review.setToilet(toilet);
        review.setUser(user);
        Review savedReview = reviewRepository.save(review);

        return ReviewDto.fromReview(savedReview);
    }

    @ValidateDto
    public ReviewDto update(ReviewDto reviewDto) {
        Review review = reviewRepository.findById(reviewDto.getId())
                .orElseThrow(() -> new EntityNotFoundException(Review.class));
        User ownerUser = review.getUser();
        checkAuthority(ownerUser.getId());

        if (reviewDto.getText() != null) {
            review.setText(reviewDto.getText());
        }

        if (reviewDto.getRating() != null) {
            review.setRating(reviewDto.getRating());
        }

        Review updatedReview = reviewRepository.save(review);
        return ReviewDto.fromReview(updatedReview);
    }

    public void delete(long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Review.class));
        User ownerUser = review.getUser();
        checkAuthority(ownerUser.getId());

        reviewRepository.delete(review);
    }

    private void checkAuthority(long ownerUserId) {
        long currentUserId = authUtil.getAuthenticatedUserId();

         // only review owner or admin can update/delete
        if (!authUtil.hasAuthority(ADMIN.getAuthority()) && currentUserId != ownerUserId) {
            throw new AccessDeniedException(StringUtils.EMPTY);

        }

    }
}
