package coLaon.ClaonBack.application.validator;

import java.util.Optional;

public abstract class Validator {

    protected Optional<Validator> next = Optional.empty();

    public Optional<Validator> linkWith(Optional<Validator> nextValidator) {
        this.next = nextValidator;
        return this.next;
    }

    abstract public void validate();
}
