package com.claon.post.common.validator;

public abstract class Validator {
    protected Validator next = null;

    public Validator linkWith(Validator nextValidator) {
        this.next = nextValidator;
        return this.next;
    }

    abstract public void validate();
}