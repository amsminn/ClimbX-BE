package com.climbx.climbx.common.config;

import com.climbx.climbx.common.dto.TierDefinitionDto;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class TierConfig {

    @Bean
    public List<TierDefinitionDto> tierList() {
        List<TierDefinitionDto> tierList = new ArrayList<>();
        List<String> normalTierNames = List.of(
            "브론즈", "실버", "골드", "플래티넘", "다이아몬드"
        );
        for (int i = 0; i < normalTierNames.size(); i++) {
            String name = normalTierNames.get(i);
            for (int level = 3; level >= 1; level--) {
                int minRating = i * 450 + (3 - level) * 150;
                int maxRating = minRating + 150 - 1;
                int score = i * 6 + 2 * (3 - level) + 1; // 1, 3, 5, 7, 9
                tierList.add(new TierDefinitionDto(name, level, minRating, maxRating, score));
            }
        }
        tierList.add(new TierDefinitionDto(
            "마스터", null, 2250, 3000, 30
        ));
        log.info("티어 목록 초기화 완료: {}", tierList);
        return tierList;
    }
}
