package com.explosion204.wclookup.exception;

public class EntityAlreadyExistsException extends EntityException {
    public EntityAlreadyExistsException(Class<?> causeEntity) {
        super(causeEntity);
    }
}
