package io.kream.injektor.context;

import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class Context {
    private final Map<Class<?>, Factory<?>> registeredFactories = new HashMap<>();
    private final Map<Class<?>, Factory<?>> resolvedFactories = new HashMap<>();

    public static Context create() {
        return new Context();
    }

    public <T> void register(Class<T> type, Factory<T> factory) {
        if (registeredFactories.containsKey(type)) {
            throw new IllegalArgumentException("Duplicate factory for " + type);
        }

        registeredFactories.put(type, factory);
    }

    @SuppressWarnings("unchecked")
    public <T> Factory<T> getFactory(Class<T> type) {
        Factory<?> factory = resolvedFactories.get(type);

        if (factory == null) {
            factory = loadFactory(type);
            factory.resolve(this);
            resolvedFactories.put(type, factory);
        }

        return (Factory<T>) factory;
    }

    private <T> Factory<?> loadFactory(Class<T> type) {
        Factory<?> factory = registeredFactories.get(type);

        if (factory != null) {
            return factory;
        }

        final Constructor<?> constructor = findInjectableConstructor(type);

        if (constructor != null) {
            factory = ReflectiveFactory.of(constructor);

            if (type.isAnnotationPresent(Singleton.class)) {
                factory = SingletonFactory.of(factory);
            }

            return factory;
        }

        throw new IllegalArgumentException("No factory for " + type);
    }

    @Nullable
    private static <T> Constructor<?> findInjectableConstructor(Class<T> type) {
        Constructor<?> injectableConstructor = null;

        for (Constructor<?> constructor : type.getDeclaredConstructors()) {
            if (!constructor.isAnnotationPresent(Inject.class)) {
                continue;
            }

            if (injectableConstructor != null) {
                throw new IllegalArgumentException("More than one injectable constructor on " + type);
            }

            injectableConstructor = constructor;
        }

        return injectableConstructor;
    }
}
