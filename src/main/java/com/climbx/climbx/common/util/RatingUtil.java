package com.climbx.climbx.common.util;

import com.climbx.climbx.common.dto.TierDefinitionDto;
import com.climbx.climbx.common.exception.InvalidRatingValueException;
import com.climbx.climbx.problem.enums.ProblemType;
import com.climbx.climbx.submission.dto.TagProjectionDto;
import com.climbx.climbx.user.dto.TagRatingResponseDto;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RatingUtil {

    static final int CATEGORY_TYPE_LIMIT = 8;
    private final List<TierDefinitionDto> tierList;

    public TierDefinitionDto getTierDefinition(int rating) {
        return tierList.stream()
            .filter(tier -> tier.minRating() <= rating && rating <= tier.maxRating())
            .findFirst()
            .orElseThrow(() -> new InvalidRatingValueException(rating));
    }

    public String getTier(int rating) {
        TierDefinitionDto tierDefinition = getTierDefinition(rating);
        return tierDefinition.name() + (tierDefinition.level() != null ? tierDefinition.level()
            .toString() : "");
    }

    public int calculateUserRating(
        List<Integer> topProblemRatings,
        int submissionCount,
        int solvedCount,
        int contributionCount
    ) {
        int topProblemScore = topProblemRatings.stream()
            .mapToInt(Integer::intValue)
            .map(rating -> getTierDefinition(rating).score())
            .sum();

        int submissionCountScore = 10 * Math.min(submissionCount, 50);

        int solvedCountScore = (int) Math.round(1000 * (1 - Math.pow(0.98, solvedCount)));

        int contributionScore = (int) Math.round(100 * (1 - Math.pow(0.9, contributionCount)));

        return topProblemScore + submissionCountScore + solvedCountScore + contributionScore;
    }

    public List<TagRatingResponseDto> calculateCategoryRating(
        List<TagProjectionDto> solvedTags,
        List<TagProjectionDto> allTags
    ) {
        // {Problem, tag} 형태로 solvedTags와 allTags를 받음.
        // Problem은 각각 2개씩 있을 수 있음

        log.info("Calculating category ratings for {} solved tags and {} all tags",
            solvedTags.size(), allTags.size());
        solvedTags.forEach(tag -> log.debug("Solved tag: {}, rating: {}",
            tag.getTag(), tag.getRating()));
        allTags.forEach(tag -> log.debug("All tag: {}, rating: {}",
            tag.getTag(), tag.getRating()));

        Map<ProblemType, List<Long>> solvedByTag = solvedTags.stream()
            .collect(Collectors.groupingBy(
                TagProjectionDto::getTag,
                Collectors.mapping(TagProjectionDto::getRating, Collectors.toList())
            ));

        Map<ProblemType, List<Long>> allByTag = allTags.stream()
            .collect(Collectors.groupingBy(
                TagProjectionDto::getTag,
                Collectors.mapping(TagProjectionDto::getRating, Collectors.toList())
            ));

        return allByTag.keySet().stream()
            .map(tag -> {
                List<Long> solvedRatings = solvedByTag.getOrDefault(tag, List.of());
                List<Long> allRatings = allByTag.getOrDefault(tag, List.of());

                int topProblemScore = 2 * solvedRatings.stream()
                    .sorted(Comparator.reverseOrder())
                    .limit(50)
                    .mapToInt(Long::intValue)
                    .sum();
                int allSubmissionScore = 10 * Math.min(allRatings.size(), 50);
                int solvedCountScore = (int) Math.round(
                    1000 * (1 - Math.pow(0.98, solvedRatings.size()))
                );
                return TagRatingResponseDto.builder()
                    .category(tag.displayName())
                    .rating(
                        topProblemScore + allSubmissionScore + solvedCountScore
                    ).build();
            }).sorted(Comparator.comparing(TagRatingResponseDto::rating).reversed())
            .toList()
            .subList(0, Math.min(CATEGORY_TYPE_LIMIT, allByTag.size()));
    }
}
