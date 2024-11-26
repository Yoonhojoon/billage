package com.team01.billage.user_review.service;

import static com.team01.billage.exception.ErrorCode.RENTAL_RECORD_NOT_FOUND;
import static com.team01.billage.exception.ErrorCode.USER_NOT_FOUND;
import static com.team01.billage.exception.ErrorCode.WRITE_ACCESS_FORBIDDEN;

import com.team01.billage.exception.CustomException;
import com.team01.billage.product_review.dto.ShowReviewResponseDto;
import com.team01.billage.product_review.dto.WriteReviewRequestDto;
import com.team01.billage.rental_record.domain.RentalRecord;
import com.team01.billage.rental_record.repository.RentalRecordRepository;
import com.team01.billage.user.domain.Users;
import com.team01.billage.user.repository.UserRepository;
import com.team01.billage.user_review.domain.UserReview;
import com.team01.billage.user_review.repository.UserReviewRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserReviewService {

    private final UserReviewRepository userReviewRepository;
    private final UserRepository userRepository;
    private final RentalRecordRepository rentalRecordRepository;

    public void createUserReview(WriteReviewRequestDto writeReviewRequestDto, long rentalRecordId,
        String email) {

        Users author = userRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        RentalRecord rentalRecord = rentalRecordRepository.findById(rentalRecordId)
            .orElseThrow(() -> new CustomException(RENTAL_RECORD_NOT_FOUND));
        Users target;

        if (rentalRecord.getBuyer().equals(author)) {
            target = rentalRecord.getSeller();
        } else if (rentalRecord.getSeller().equals(author)) {
            target = rentalRecord.getBuyer();
        } else {
            throw new CustomException(WRITE_ACCESS_FORBIDDEN);
        }

        UserReview userReview = UserReview.builder()
            .score(writeReviewRequestDto.getScore())
            .content(writeReviewRequestDto.getContent())
            .author(author)
            .target(target)
            .build();

        userReviewRepository.save(userReview);
    }

    public List<ShowReviewResponseDto> readUserReviews(String email) {

        return userReviewRepository.findByAuthor_email(email);
    }
}
