package coLaon.ClaonBack.application.validator;

import org.jetbrains.annotations.NotNull;

public abstract class Validator {

    protected Validator next = null;

    public Validator linkWith(@NotNull Validator nextValidator) {
        this.next = nextValidator;
        return this.next;
    }

    abstract public void validate();
}
