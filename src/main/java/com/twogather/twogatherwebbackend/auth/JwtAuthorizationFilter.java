package com.twogather.twogatherwebbackend.auth;
import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.dto.member.CustomUser;
import com.twogather.twogatherwebbackend.exception.CustomAuthenticationException;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import static com.twogather.twogatherwebbackend.exception.CustomAuthenticationException.AuthenticationExceptionErrorCode.INVALID_TOKEN;
import static com.twogather.twogatherwebbackend.exception.MemberException.MemberErrorCode.NO_SUCH_MEMBER_ID;
import static com.twogather.twogatherwebbackend.exception.MemberException.MemberErrorCode.NO_SUCH_USERNAME;

// 인가
@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private MemberRepository memberRepository;
    private PrivateConstants constants;
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;


    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, MemberRepository memberRepository,
                                  JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                                  PrivateConstants constants) {
        super(authenticationManager);
        this.memberRepository = memberRepository;
        this.constants = constants;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String header = request.getHeader(constants.ACCESS_TOKEN_HEADER);
        if (header == null || !header.startsWith(constants.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.replace(constants.TOKEN_PREFIX, "");

        try {
            CustomUser customUser = parseToken(token);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    customUser, null, customUser.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (TokenExpiredException e) {
            handleTokenExpiration(request, response);
            return;
        } catch (CustomAuthenticationException e) {
            jwtAuthenticationEntryPoint.commence(request, response, e);
            return;
        }

       chain.doFilter(request, response);


    }

    private CustomUser parseToken(String token) {
        Long memberId = JWT
                .require(Algorithm.HMAC512(constants.JWT_SECRET))
                .build()
                .verify(token)
                .getClaim("id").asLong();

        if (memberId != null) {
            // Retrieve member details from the database (you may need to modify this part)
            Member member = memberRepository.findById(memberId).orElseThrow(
                    ()->new MemberException(NO_SUCH_MEMBER_ID)
            );
            if (member == null) {
                throw new CustomAuthenticationException(INVALID_TOKEN);
            }

            return new CustomUser(member);
        } else {
            throw new CustomAuthenticationException(INVALID_TOKEN);
        }
    }

    private void handleTokenExpiration(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String refreshToken = request.getHeader(constants.REFRESH_TOKEN_HEADER);
        if (refreshToken == null) {
            jwtAuthenticationEntryPoint.commence(request, response, new CustomAuthenticationException(INVALID_TOKEN));
            return;
        }

        try {
            CustomUser customUser = parseToken(refreshToken);
            String newAccessToken = generateAccessToken(customUser);

            response.setHeader(constants.ACCESS_TOKEN_HEADER, constants.TOKEN_PREFIX + newAccessToken);
        } catch (CustomAuthenticationException e) {
            jwtAuthenticationEntryPoint.commence(request, response, e);
        }
    }

    private String generateAccessToken(CustomUser customUser) {
        return JWT.create()
                .withSubject(customUser.getMemberId().toString())
                        .withExpiresAt(new Date(System.currentTimeMillis() + constants.ACCESS_TOKEN_EXPIRATION_TIME))
                        .withClaim("id", customUser.getMemberId())
                        .withClaim("role", customUser.getRole())
                        .sign(Algorithm.HMAC512(constants.JWT_SECRET));
    }

}