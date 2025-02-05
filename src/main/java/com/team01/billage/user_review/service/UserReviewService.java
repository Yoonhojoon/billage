package com.team01.billage.user_review.service;

import static com.team01.billage.exception.ErrorCode.RENTAL_RECORD_NOT_FOUND;
import static com.team01.billage.exception.ErrorCode.REVIEW_ALREADY_EXISTS;
import static com.team01.billage.exception.ErrorCode.USER_NOT_FOUND;
import static com.team01.billage.exception.ErrorCode.WRITE_ACCESS_FORBIDDEN;

import com.team01.billage.common.CustomSlice;
import com.team01.billage.exception.CustomException;
import com.team01.billage.product_review.dto.ReviewSubjectResponseDto;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserReviewService {

    private final UserReviewRepository userReviewRepository;
    private final UserRepository userRepository;
    private final RentalRecordRepository rentalRecordRepository;

    public void createUserReview(WriteReviewRequestDto writeReviewRequestDto, long rentalRecordId,
        long userId) {

        Users author = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        RentalRecord rentalRecord = rentalRecordRepository.findById(rentalRecordId)
            .orElseThrow(() -> new CustomException(RENTAL_RECORD_NOT_FOUND));
        Users target;

        if (!rentalRecord.getUserReviews().isEmpty()) {
            rentalRecord.getUserReviews().stream().filter(ur -> ur.getAuthor().equals(author))
                .findAny().ifPresent(ur -> {
                    throw new CustomException(REVIEW_ALREADY_EXISTS);
                });
        }

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
            .rentalRecord(rentalRecord)
            .build();

        userReviewRepository.save(userReview);
    }

    public Slice<ShowReviewResponseDto> readUserReviews(long userId, Long lastStandard,
        Pageable pageable) {

        List<ShowReviewResponseDto> content = userReviewRepository.findByAuthor(userId,
            lastStandard, pageable);

        boolean hasNext = content.size() > pageable.getPageSize();

        if (hasNext) {
            content.remove(content.size() - 1);
        }

        Long nextLastStandard = null;

        if (!content.isEmpty()) {
            nextLastStandard = content.get(content.size() - 1).getReviewId();
        }

        return new CustomSlice<>(content, pageable, hasNext, nextLastStandard);
    }

    public Slice<ShowReviewResponseDto> readTargetReviews(long userId, Long lastStandard,
        Pageable pageable) {

        List<ShowReviewResponseDto> content = userReviewRepository.findByTarget(userId,
            lastStandard, pageable);

        boolean hasNext = content.size() > pageable.getPageSize();

        if (hasNext) {
            content.remove(content.size() - 1);
        }

        Long nextLastStandard = null;

        if (!content.isEmpty()) {
            nextLastStandard = content.get(content.size() - 1).getReviewId();
        }

        return new CustomSlice<>(content, pageable, hasNext, nextLastStandard);
    }

    public ReviewSubjectResponseDto getReviewSubject(long rentalRecordId, long userId) {

        RentalRecord rentalRecord = rentalRecordRepository.findById(rentalRecordId)
            .orElseThrow(() -> new CustomException(RENTAL_RECORD_NOT_FOUND));

        Users subject;

        if (rentalRecord.getSeller().getId() == userId) {
            subject = rentalRecord.getBuyer();
        } else {
            subject = rentalRecord.getSeller();
        }

        return ReviewSubjectResponseDto.builder()
            .imageUrl(subject.getImageUrl())
            .subject(subject.getNickname())
            .build();
    }
}
