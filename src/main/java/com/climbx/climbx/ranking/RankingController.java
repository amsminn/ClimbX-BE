package com.climbx.climbx.ranking;

import com.climbx.climbx.common.annotation.SuccessStatus;
import com.climbx.climbx.common.enums.SortOrderType;
import com.climbx.climbx.ranking.dto.RankingResponseDto;
import com.climbx.climbx.ranking.service.RankingService;
import com.climbx.climbx.user.enums.CriteriaType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ranking")
@RequiredArgsConstructor
public class RankingController implements RankingApiDocumentation {

    private final RankingService rankingService;

    @Override
    @GetMapping("/users")
    @SuccessStatus(value = HttpStatus.OK)
    public RankingResponseDto getRanking(
        @RequestParam(name = "criteria", required = true)
        CriteriaType criteria,

        @RequestParam(name = "order", required = false, defaultValue = "desc")
        SortOrderType order,

        @RequestParam(name = "page", required = false, defaultValue = "0")
        Integer page,

        @RequestParam(name = "perPage", required = false, defaultValue = "20")
        Integer perPage
    ) {
        return rankingService.getRankingPage(criteria, order, page, perPage);
    }
}
