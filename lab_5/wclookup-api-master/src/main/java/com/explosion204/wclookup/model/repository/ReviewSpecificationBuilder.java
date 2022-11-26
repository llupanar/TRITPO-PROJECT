package com.explosion204.wclookup.model.repository;

import com.explosion204.wclookup.model.entity.Review;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

import static com.explosion204.wclookup.model.repository.FieldName.CREATION_TIME;
import static com.explosion204.wclookup.model.repository.FieldName.ID;
import static com.explosion204.wclookup.model.repository.FieldName.TOILET;

public class ReviewSpecificationBuilder {
    private Specification<Review> composedSpecification;

    public ReviewSpecificationBuilder() {
        composedSpecification = Specification.where(null);
    }

    public ReviewSpecificationBuilder byToiletId(Long toiletId) {
        if (toiletId != null) {
            Specification<Review> specification = (reviewRoot, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.equal(reviewRoot.get(TOILET).get(ID), toiletId);
            composedSpecification = composedSpecification.and(specification);
        }

        return this;
    }

    public ReviewSpecificationBuilder byCreationTimeAfter(LocalDateTime targetTime) {
        if (targetTime != null) {
            Specification<Review> specification = (reviewRoot, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(reviewRoot.get(CREATION_TIME), targetTime);
            composedSpecification = composedSpecification.and(specification);
        }

        return this;
    }

    public Specification<Review> build() {
        return composedSpecification;
    }
}
