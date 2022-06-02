package coLaon.ClaonBack.application.validator;

import java.util.Optional;

public abstract class Validator {

    protected Validator next = null;

    public Validator linkWith(Validator nextValidator) {
        this.next = nextValidator;
        return this.next;
    }

    abstract public void validate();
}