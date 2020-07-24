package io.kream.injektor.context;

public final class SingletonFactory<T> extends Factory<T> {
    private final Factory<T> factory;
    private T singleton;

    private SingletonFactory(Factory<T> factory) {
        this.factory = factory;
    }

    public static <T> Factory<T> of(Factory<T> factory) {
        return new SingletonFactory<>(factory);
    }

    @Override
    public T get() {
        if (singleton == null) {
            singleton = factory.get();
        }

        return singleton;
    }
}
