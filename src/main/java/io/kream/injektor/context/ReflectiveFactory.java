package io.kream.injektor.context;

import javax.inject.Provider;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public final class ReflectiveFactory<T> extends Factory<T> {
    private final Constructor<T> constructor;
    private final List<Descriptor> descriptors = new ArrayList<>();

    private ReflectiveFactory(Constructor<T> constructor) {
        this.constructor = constructor;
    }

    public static <T> Factory<T> of(Constructor<T> constructor) {
        return new ReflectiveFactory<>(constructor);
    }

    @Override
    protected void resolve(Context context) {
        final Class<?>[] parameterTypes = constructor.getParameterTypes();
        final Type[] genericTypes = constructor.getGenericParameterTypes();

        for (int idx = 0; idx < parameterTypes.length; idx++) {
            Class<?> parameterType = parameterTypes[idx];
            boolean instantiate = true;

            if (Provider.class.isAssignableFrom(parameterType)) {
                final ParameterizedType parameterizedType = (ParameterizedType) genericTypes[idx];
                parameterType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                instantiate = false;
            }

            final Factory<?> factory = context.getFactory(parameterType);
            descriptors.add(Descriptor.of(factory, instantiate));
        }
    }

    @Override
    public T get() {
        final Object[] parameters = descriptors.stream().map(Descriptor::get).toArray();

        try {
            constructor.setAccessible(true);
            return constructor.newInstance(parameters);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final class Descriptor {
        private final Factory<?> factory;
        private final boolean instantiate;

        private Descriptor(Factory<?> factory, boolean instantiate) {
            this.factory = factory;
            this.instantiate = instantiate;
        }

        public static Descriptor of(Factory<?> factory, boolean instantiate) {
            return new Descriptor(factory, instantiate);
        }

        public Object get() {
            return instantiate ? factory.get() : factory;
        }
    }
}
