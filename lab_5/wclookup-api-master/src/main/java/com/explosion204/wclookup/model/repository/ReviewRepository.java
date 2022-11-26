package com.explosion204.wclookup.model.repository;

import com.explosion204.wclookup.model.entity.Review;
import com.explosion204.wclookup.model.entity.Toilet;
import com.explosion204.wclookup.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {
    Optional<Review> findByUserAndToilet(User user, Toilet toilet);
}
