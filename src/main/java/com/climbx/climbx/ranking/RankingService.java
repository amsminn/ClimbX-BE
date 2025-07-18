package com.climbx.climbx.ranking;

import com.climbx.climbx.common.comcode.ComcodeService;
import com.climbx.climbx.common.util.OptionalUtils;
import com.climbx.climbx.ranking.dto.RankingResponseDto;
import com.climbx.climbx.ranking.dto.UserRankingResponseDto;
import com.climbx.climbx.ranking.enums.RankingCriteria;
import com.climbx.climbx.ranking.repository.RankingRepository;
import com.climbx.climbx.user.entity.UserStatEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RankingService {

    private final RankingRepository rankingRepository;
    private final ComcodeService comcodeService;

    public RankingResponseDto getRankingPage(
        String criteria,
        String order,
        Integer page,
        Integer perPage
    ) {
        String validatedCriteria = RankingCriteria.fromCode(criteria);
        Direction direction = OptionalUtils.tryOf(
            () -> Direction.valueOf(comcodeService.getCodeValue(order))
        ).orElse(Direction.DESC);

        Sort sort = Sort.by(direction, validatedCriteria);
        Pageable pageable = PageRequest.of(page, perPage, sort);

        // 어드민 계정 제외하고 일반 사용자만 조회
        Page<UserStatEntity> rankingPage = rankingRepository.findAllByUserRole(pageable,
            comcodeService.getCodeValue("USER"));

        Long totalCount = rankingPage.getTotalElements();
        Integer totalPage = rankingPage.getTotalPages();
        List<UserRankingResponseDto> rankingList = rankingPage.getContent().stream()
            .map(UserRankingResponseDto::from)
            .toList();

        return RankingResponseDto.builder()
            .totalCount(totalCount)
            .page(page)
            .perPage(perPage)
            .totalPage(totalPage)
            .rankingList(rankingList)
            .build();
    }
}
