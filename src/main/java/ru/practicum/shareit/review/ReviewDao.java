package ru.practicum.shareit.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.review.model.Review;

@Repository
public interface ReviewDao extends JpaRepository<Review, Long> {
}
