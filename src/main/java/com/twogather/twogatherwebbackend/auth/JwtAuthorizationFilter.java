package com.twogather.twogatherwebbackend.auth;
import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.dto.member.CustomUser;
import com.twogather.twogatherwebbackend.exception.CustomAuthenticationException;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import static com.twogather.twogatherwebbackend.exception.CustomAuthenticationException.AuthenticationExceptionErrorCode.INVALID_TOKEN;
import static com.twogather.twogatherwebbackend.exception.MemberException.MemberErrorCode.NO_SUCH_EMAIL;

// 인가
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private MemberRepository memberRepository;
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;


    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, MemberRepository memberRepository,
                                  JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        super(authenticationManager);
        this.memberRepository = memberRepository;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String header = request.getHeader(PrivateConstants.HEADER_STRING);
        if (header == null || !header.startsWith(PrivateConstants.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }
        System.out.println("header : " + header);
        String token = request.getHeader(PrivateConstants.HEADER_STRING)
                .replace(PrivateConstants.TOKEN_PREFIX, "");

        // 토큰 검증 (이게 인증이기 때문에 AuthenticationManager도 필요 없음)
        // 내가 SecurityContext에 집적접근해서 세션을 만들때 자동으로 UserDetailsService에 있는
        // loadByUsername이 호출됨.
        String username="";
        try {
            username = JWT.require(Algorithm.HMAC512(PrivateConstants.JWT_SECRET)).build().verify(token)
                    .getClaim("username").asString();
        }catch(RuntimeException e){
            e.printStackTrace();
            jwtAuthenticationEntryPoint.commence(request, response, new CustomAuthenticationException(INVALID_TOKEN));
            return;
        }
        if (username != null) {
            Member member = memberRepository.findByEmail(username).orElseThrow(
                    ()-> new MemberException(NO_SUCH_EMAIL)
            );

            // 인증은 토큰 검증시 끝. 인증을 하기 위해서가 아닌 스프링 시큐리티가 수행해주는 권한 처리를 위해
            // 아래와 같이 토큰을 만들어서 Authentication 객체를 강제로 만들고 그걸 세션에 저장!
            CustomUser principalDetails = new CustomUser(member);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    principalDetails, // 나중에 컨트롤러에서 DI해서 쓸 때 사용하기 편함.
                    null, // 패스워드는 모르니까 null 처리, 어차피 지금 인증하는게 아니니까!!
                    principalDetails.getAuthorities());

            // 강제로 시큐리티의 세션에 접근하여 값 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }

}