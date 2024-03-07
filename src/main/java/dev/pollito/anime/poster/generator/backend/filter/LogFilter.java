package dev.pollito.anime.poster.generator.backend.filter;

import static dev.pollito.anime.poster.generator.backend.util.Constants.SLF4J_MDC_SESSION_ID_KEY;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
public class LogFilter implements Filter {

  @Override
  public void doFilter(
      ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
      throws IOException, ServletException {
    MDC.put(SLF4J_MDC_SESSION_ID_KEY, UUID.randomUUID().toString());
    try {
      logRequestDetails((HttpServletRequest) servletRequest);
      filterChain.doFilter(servletRequest, servletResponse);
      logResponseDetails((HttpServletResponse) servletResponse);
    } finally {
      MDC.clear();
    }
  }

  private void logRequestDetails(HttpServletRequest request) {
    log.info(
        ">>>> Method: {}; URI: {}; QueryString: {}; Headers: {}",
        request.getMethod(),
        request.getRequestURI(),
        request.getQueryString(),
        headersToString(request));
  }

  public String headersToString(HttpServletRequest request) {
    Enumeration<String> headerNames = request.getHeaderNames();
    StringBuilder stringBuilder = new StringBuilder("{");

    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      String headerValue = request.getHeader(headerName);

      stringBuilder.append(headerName).append(": ").append(headerValue);

      if (headerNames.hasMoreElements()) {
        stringBuilder.append(", ");
      }
    }

    stringBuilder.append("}");
    return stringBuilder.toString();
  }

  private void logResponseDetails(HttpServletResponse response) {
    log.info("<<<< Response Status: {}", response.getStatus());
  }
}
