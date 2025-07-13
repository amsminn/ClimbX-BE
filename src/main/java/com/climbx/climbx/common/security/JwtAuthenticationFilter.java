package com.climbx.climbx.common.security;

import com.climbx.climbx.common.comcode.ComcodeService;
import com.climbx.climbx.common.security.dto.JwtTokenInfo;
import com.climbx.climbx.common.security.exception.InvalidTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtContext jwtContext;
    private final ComcodeService comcodeService;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            // Bearer 토큰 추출
            String token = jwtContext.extractTokenFromRequest(request);
            JwtTokenInfo tokenInfo = jwtContext.parseToken(token);
            
            // ACCESS 토큰인지 확인
            String accessTokenType = comcodeService.getCodeValue("ACCESS");
            if (!accessTokenType.equals(tokenInfo.tokenType())) {
                log.debug("Invalid token type: expected={}, actual={}", accessTokenType, tokenInfo.tokenType());
                throw new InvalidTokenException();
            }
            
            // Spring Security 인증 정보 설정
            setAuthentication(tokenInfo.userId(), comcodeService.getCodeValue(tokenInfo.role()));
            
            log.debug("JWT authentication successful for user: {}", tokenInfo.userId());
            
        } catch (Exception e) {
            log.error("JWT authentication error", e);
            // 예상치 못한 오류 발생 시에도 인증 없이 계속 진행
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Spring Security 인증 정보를 설정합니다.
     */
    private void setAuthentication(Long userId, String role) {
        List<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority(role)
        );
        
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            userId, // principal로 userId 사용
            null,   // credentials는 null (JWT는 이미 검증됨)
            authorities
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
} 