package com.explosion204.wclookup.exception;

public abstract class EntityException extends RuntimeException {
    private final Class<?> causeEntity;

    protected EntityException(Class<?> causeEntity) {
        this.causeEntity = causeEntity;
    }

    public Class<?> getCauseEntity() {
        return causeEntity;
    }
}
