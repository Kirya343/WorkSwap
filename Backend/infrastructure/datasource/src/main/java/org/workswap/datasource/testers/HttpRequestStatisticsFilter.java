package org.workswap.datasource.testers;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class HttpRequestStatisticsFilter extends OncePerRequestFilter {

    private final QueryCounter queryCounter;

    public HttpRequestStatisticsFilter(QueryCounter queryCounter) {
        this.queryCounter = queryCounter;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        long start = System.nanoTime();

        queryCounter.reset();

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.nanoTime() - start;
            double durationMs = duration / 1_000_000.0;

            if (durationMs > 100 || queryCounter.getCount() > 10) {
                log.warn(
                    "SLOW REQUEST: {} {} -> {} | {} ms | {} SQL",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    String.format("%.2f", durationMs),
                    queryCounter.getCount()
                );
            } else {
                log.info(
                    "{} {} -> {} | {} ms | {} SQL queries",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    String.format("%.2f", durationMs),
                    queryCounter.getCount()
                );
            }

            queryCounter.reset();
        }
    }
}