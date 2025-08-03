package com.climbx.climbx.ranking.service;

import com.climbx.climbx.common.enums.CriteriaType;
import com.climbx.climbx.common.enums.RoleType;
import com.climbx.climbx.ranking.dto.RankingResponseDto;
import com.climbx.climbx.ranking.dto.UserRankingResponseDto;
import com.climbx.climbx.ranking.repository.RankingRepository;
import com.climbx.climbx.user.entity.UserStatEntity;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RankingService {

    private final RankingRepository rankingRepository;

    public RankingResponseDto getRankingPage(
        CriteriaType criteria,
        Pageable pageable
    ) {
        Sort sort = Sort.by(
            Sort.Order.desc(criteria.fieldName()),
            Sort.Order.asc("updatedAt"),
            Sort.Order.asc("userId")
        );

        Pageable sortedPageable = PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            sort
        );

        // 어드민 계정 제외하고 일반 사용자만 조회
        Page<UserStatEntity> rankingPage = rankingRepository.findAllByUserRole(sortedPageable,
            RoleType.USER);

        List<UserRankingResponseDto> rankingList = rankingPage.getContent().stream()
            .map(UserRankingResponseDto::from)
            .toList();

        // 페이징 정보 계산
        Long totalCount = rankingPage.getTotalElements();
        Boolean hasNext = rankingPage.hasNext();
        String nextCursor = hasNext
            ? rankingPage.nextPageable().getPageNumber() + ""
            : null;

        return RankingResponseDto.builder()
            .rankings(rankingList)
            .totalCount(totalCount)
            .hasNext(hasNext)
            .nextCursor(nextCursor)
            .build();
    }

    public List<String> getCriteriaTypeNames() {
        return Stream.of(CriteriaType.values())
            .map(CriteriaType::name)
            .map(String::toLowerCase)
            .toList();
    }
}
