package io.kream.injektor.context;

public class NoSuchFactoryException extends RuntimeException {
    private static final long serialVersionUID = 6143392445881742611L;

    public NoSuchFactoryException(Class<?> type) {
        super("Factory for " + type + " does not exist");
    }
}
