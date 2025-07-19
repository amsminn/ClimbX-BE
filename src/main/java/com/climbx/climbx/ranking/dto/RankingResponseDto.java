package com.climbx.climbx.ranking.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record RankingResponseDto(

    Long totalCount, // 전체 유저 수
    Integer page, // 현재 페이지
    Integer perPage, // 페이지당 유저 수
    Integer totalPage, // 전체 페이지 수
    List<UserRankingResponseDto> rankingList // 랭킹 리스트
) {

}
