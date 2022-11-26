package com.explosion204.wclookup.service;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageSourceService {
    private final ResourceBundleMessageSource messageSource;

    public MessageSourceService(ResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getString(String errorMessageName) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(errorMessageName, null, locale);
    }
}
