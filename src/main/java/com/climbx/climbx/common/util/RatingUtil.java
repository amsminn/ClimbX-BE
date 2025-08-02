package com.climbx.climbx.common.util;

import com.climbx.climbx.common.dto.TierDefinitionDto;
import com.climbx.climbx.common.exception.InvalidRatingValueException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RatingUtil {

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

        int contributionScore = (int) Math.round(50 * (1 - Math.pow(0.9, contributionCount)));

        return topProblemScore + submissionCountScore + solvedCountScore + contributionScore;
    }

    public int calculateCategoryRating(
        List<Integer> topProblemRatings,
        int submissionCount,
        int solvedCount
    ) {
        int topProblemScore = 2 * topProblemRatings.stream()
            .mapToInt(Integer::intValue)
            .map(rating -> getTierDefinition(rating).score())
            .sum();

        int submissionCountScore = 10 * Math.min(submissionCount, 50);

        int solvedCountScore = (int) Math.round(1000 * (1 - Math.pow(0.98, solvedCount)));

        return topProblemScore + submissionCountScore + solvedCountScore;
    }
}
