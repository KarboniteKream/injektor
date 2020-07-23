package io.kream.injektor.internal;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public final class ReflectiveFactory<T> extends Factory<T> {
    private final Constructor<T> constructor;
    private final List<Factory<?>> factories = new ArrayList<>();

    public static <T> Factory<T> of(Constructor<T> constructor) {
        return new ReflectiveFactory<>(constructor);
    }

    private ReflectiveFactory(Constructor<T> constructor) {
        this.constructor = constructor;
    }

    @Override
    protected void resolve(Context context) {
        for (Class<?> parameterType : constructor.getParameterTypes()) {
            factories.add(context.getFactory(parameterType));
        }
    }

    @Override
    public T get() {
        final Object[] parameters = factories.stream().map(Factory::get).toArray();

        try {
            return constructor.newInstance(parameters);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
