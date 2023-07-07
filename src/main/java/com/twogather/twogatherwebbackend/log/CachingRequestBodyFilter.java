package com.twogather.twogatherwebbackend.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

@Slf4j
public class CachingRequestBodyFilter extends OncePerRequestFilter {

    private static final ThreadLocal<ContentCachingRequestWrapper> requestHolder = new ThreadLocal<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper wrapper = new ContentCachingRequestWrapper(request);
        try {
            requestHolder.set(wrapper);
            filterChain.doFilter(wrapper, response);
        } finally {
            requestHolder.remove();
        }
    }

    public static Optional<String> getRequestBody() {
        ContentCachingRequestWrapper wrapper = requestHolder.get();
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                try {
                    return Optional.of(new String(buf, 0, buf.length, wrapper.getCharacterEncoding()));
                } catch (UnsupportedEncodingException ex) {
                    log.error(ex.getMessage());
                }
            }
        }
        return Optional.empty();
    }
}