package io.kream.injektor.internal;

public final class SingletonFactory<T> extends Factory<T> {
    private final Factory<T> factory;
    private T singleton;

    public static <T> Factory<T> of(Factory<T> factory) {
        return new SingletonFactory<>(factory);
    }

    private SingletonFactory(Factory<T> factory) {
        this.factory = factory;
    }

    @Override
    public T get() {
        if (singleton == null) {
            singleton = factory.get();
        }

        return singleton;
    }
}
