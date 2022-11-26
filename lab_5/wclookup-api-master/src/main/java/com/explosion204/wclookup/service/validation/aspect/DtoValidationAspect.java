package com.explosion204.wclookup.service.validation.aspect;

import com.explosion204.wclookup.service.validation.annotation.DtoClass;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Arrays;
import java.util.Set;

@Aspect
@Component
public class DtoValidationAspect {
    private final Validator validator;

    public DtoValidationAspect(Validator validator) {
        this.validator = validator;
    }

    @Before("@annotation(com.explosion204.wclookup.service.validation.annotation.ValidateDto)")
    public void validationAdvice(JoinPoint joinPoint) {
        Arrays.stream(joinPoint.getArgs())
                .filter(arg -> arg.getClass().isAnnotationPresent(DtoClass.class))
                .forEach(arg -> {
                    Set<ConstraintViolation<Object>> errors = validator.validate(arg);

                    if (!errors.isEmpty()) {
                        throw new ConstraintViolationException(errors);
                    }
                });
    }
}
