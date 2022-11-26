package com.explosion204.wclookup.controller.debug;

import com.explosion204.wclookup.service.MailService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@Profile("debug")
public class DebugMailLogger {
    private static final String BUG_REPORT_SUBJECT = "wclookup [DEBUG]";
    private static final String BUG_REPORT_BODY = "Request: %s %s?%s, stacktrace: %s";
    private final MailService mailService;

    @Value("${debug.mail.receiver}")
    private String debugMailReceiver;

    public DebugMailLogger(MailService mailService) {
        this.mailService = mailService;
    }

    public void log(HttpServletRequest request, Throwable e) {
        String requestMethod = request.getMethod();
        String requestUri = request.getRequestURI();
        String queryParams = request.getQueryString() != null ? request.getQueryString() : StringUtils.EMPTY;
        String stackTrace = ExceptionUtils.getStackTrace(e);
        String body = String.format(BUG_REPORT_BODY, requestMethod, requestUri, queryParams, stackTrace);

        mailService.sendEmail(debugMailReceiver, BUG_REPORT_SUBJECT, body);
    }
}
