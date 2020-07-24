package io.kream.injektor.context;

import io.kream.injektor.util.Counter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Provider;
import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;

class ReflectiveFactoryTest {
    private Context context;

    @BeforeEach
    void setUp() {
        context = Context.create();
        context.register(Counter.class, ValueFactory.of(new Counter()));
    }

    @Test
    void injectInstance() throws Exception {
        final Constructor<Delegate> constructor =
                Delegate.class.getDeclaredConstructor(Counter.class);
        final Factory<Delegate> factory = ReflectiveFactory.of(constructor);
        factory.resolve(context);

        final Counter counter = factory.get().get();
        assertThat(counter).isNotNull();
    }

    @Test
    void injectFactory() throws Exception {
        final Constructor<FactoryDelegate> constructor =
                FactoryDelegate.class.getDeclaredConstructor(Factory.class);
        final Factory<FactoryDelegate> factory = ReflectiveFactory.of(constructor);
        factory.resolve(context);

        final Counter counter = factory.get().get();
        assertThat(counter).isNotNull();
    }

    @Test
    void injectProvider() throws Exception {
        final Constructor<ProviderDelegate> constructor =
                ProviderDelegate.class.getDeclaredConstructor(Provider.class);
        final Factory<ProviderDelegate> factory = ReflectiveFactory.of(constructor);
        factory.resolve(context);

        final Counter counter = factory.get().get();
        assertThat(counter).isNotNull();
    }

    private static class Delegate {
        private final Counter instance;

        Delegate(Counter instance) {
            this.instance = instance;
        }

        public Counter get() {
            return instance;
        }
    }

    private static class FactoryDelegate {
        private final Counter instance;

        FactoryDelegate(Factory<Counter> factory) {
            instance = factory.get();
        }

        public Counter get() {
            return instance;
        }
    }

    private static class ProviderDelegate {
        private final Counter instance;

        ProviderDelegate(Provider<Counter> factory) {
            instance = factory.get();
        }

        public Counter get() {
            return instance;
        }
    }
}
