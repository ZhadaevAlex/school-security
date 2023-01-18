package ru.zhadaev.schoolsecurity.config;

import lombok.extern.slf4j.Slf4j;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;

@Slf4j
public class HttpRequestHeadersLoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request =
                (HttpServletRequest) servletRequest;
        String out = String.format("URL: %s | Remote Addr: %s | HTTP Method: %s | Headers: %s | QueryStringParams: %s ",
                request.getRequestURL(), request.getRemoteAddr(),
                request.getMethod(), getRequestHeaders(request),
                request.getQueryString());

        log.debug(out);

        chain.doFilter(servletRequest, servletResponse);
    }

    private Map<String, String> getRequestHeaders(HttpServletRequest request) {
        Map<String, String> headersMap = new HashMap<>();
        Enumeration<String> headerEnumeration = request.getHeaderNames();
        while (headerEnumeration.hasMoreElements()) {
            String header = headerEnumeration.nextElement();
            headersMap.put(header, request.getHeader(header));
        }

        return headersMap;
    }
}
