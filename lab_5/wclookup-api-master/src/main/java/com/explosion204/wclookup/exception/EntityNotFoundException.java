package com.explosion204.wclookup.exception;

public class EntityNotFoundException extends EntityException {
    public EntityNotFoundException(Class<?> causeEntity) {
        super(causeEntity);
    }
}
