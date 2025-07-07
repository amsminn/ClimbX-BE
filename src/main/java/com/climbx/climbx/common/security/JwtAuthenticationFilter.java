package com.climbx.climbx.common.security;

import com.climbx.climbx.common.enums.RoleType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        Optional<String> tokenOptional = jwtContext.extractTokenFromRequest(request);

        if (tokenOptional.isPresent()) {
            String token = tokenOptional.get();
            jwtContext.validateToken(token); // 유효하지 않으면 InvalidToken 또는 TokenExpired 예외 throw

            extractAuthenticationInfo(token)
                .ifPresent(this::setAuthentication);
        }

        filterChain.doFilter(request, response);
    }

    private Optional<AuthenticationInfo> extractAuthenticationInfo(String validToken) {
        return jwtContext.extractSubject(validToken)
            .map(userId -> new AuthenticationInfo(
                userId,
                jwtContext.getRole(validToken).orElse(RoleType.USER)
            ));
    }

    private void setAuthentication(AuthenticationInfo authInfo) {
        List<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + authInfo.role.name())
        );

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                authInfo.userId,
                null,
                authorities
            );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug("사용자 인증 완료: userId={}, role={}", authInfo.userId, authInfo.role);
    }

    private static class AuthenticationInfo {

        final Long userId;
        final RoleType role;

        AuthenticationInfo(Long userId, RoleType role) {
            this.userId = userId;
            this.role = role;
        }
    }
} 