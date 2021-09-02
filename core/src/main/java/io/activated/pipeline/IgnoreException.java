package io.activated.pipeline;

public class IgnoreException extends RuntimeException implements Ignore {

    private final Object returnInstead;

    public IgnoreException(Object returnInstead) {
        this.returnInstead = returnInstead;
    }

    public <S> S returnInstead() {
        return (S) returnInstead;
    }
}
