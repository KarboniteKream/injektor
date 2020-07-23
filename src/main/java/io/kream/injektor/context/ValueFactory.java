package io.kream.injektor.context;

public final class ValueFactory<T> extends Factory<T> {
    private final T value;

    public static <T> Factory<T> of(T value) {
        return new ValueFactory<>(value);
    }

    private ValueFactory(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }
}
