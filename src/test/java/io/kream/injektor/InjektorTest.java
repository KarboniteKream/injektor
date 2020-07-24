package io.kream.injektor;

import io.kream.injektor.context.Context;
import io.kream.injektor.context.Factory;
import io.kream.injektor.context.SingletonFactory;
import io.kream.injektor.context.ValueFactory;
import io.kream.injektor.util.Counter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InjektorTest {
    private Context context;
    private Injektor injektor;

    @BeforeEach
    void setUp() {
        context = Context.create();
        injektor = Injektor.create(context);
    }

    @Test
    void noFactory() {
        assertThatThrownBy(() -> injektor.getInstance(HttpClient.class))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void httpClient() {
        context.register(Client.class, ValueFactory.of(new HttpClient()));

        final Client client = injektor.getInstance(Client.class);
        assertThat(client).isInstanceOf(HttpClient.class);
        assertThat(client.getProtocol()).isEqualTo("http");
    }

    @Test
    void counter() {
        context.register(Counter.class, new Factory<>() {
            @Override
            public Counter get() {
                return new Counter();
            }
        });

        final Counter counter1 = injektor.getInstance(Counter.class);
        assertThat(counter1.get()).isEqualTo(0);
        counter1.increment();
        assertThat(counter1.get()).isEqualTo(1);

        final Counter counter2 = injektor.getInstance(Counter.class);
        assertThat(counter2.get()).isEqualTo(0);
        counter2.increment();
        counter2.increment();
        assertThat(counter2.get()).isEqualTo(2);

        assertThat(counter1.get()).isEqualTo(1);
    }

    @Test
    void counter_singleton() {
        context.register(Counter.class, SingletonFactory.of(new Factory<>() {
            @Override
            public Counter get() {
                return new Counter();
            }
        }));

        final Counter counter1 = injektor.getInstance(Counter.class);
        assertThat(counter1.get()).isEqualTo(0);
        counter1.increment();
        assertThat(counter1.get()).isEqualTo(1);

        final Counter counter2 = injektor.getInstance(Counter.class);
        assertThat(counter2.get()).isEqualTo(1);
        counter2.increment();
        counter2.increment();
        assertThat(counter2.get()).isEqualTo(3);

        assertThat(counter1.get()).isEqualTo(3);
    }

    @Test
    void counterDelegate() {
        context.register(Counter.class, ValueFactory.of(new Counter()));
        context.register(CounterDelegate.class, new Factory<>() {
            private Factory<Counter> factory;

            @Override
            protected void resolve(Context context) {
                factory = context.getFactory(Counter.class);
            }

            @Override
            public CounterDelegate get() {
                return new CounterDelegate(factory.get());
            }
        });

        final CounterDelegate counter = injektor.getInstance(CounterDelegate.class);
        assertThat(counter.get()).isEqualTo(0);
        counter.increment();
        counter.increment();
        counter.increment();
        assertThat(counter.get()).isEqualTo(3);
    }

    @Test
    void counterDelegate_inject() {
        context.register(Counter.class, ValueFactory.of(new Counter()));

        final CounterDelegate counter = injektor.getInstance(CounterDelegate.class);
        assertThat(counter.get()).isEqualTo(0);
        counter.increment();
        counter.increment();
        counter.increment();
        assertThat(counter.get()).isEqualTo(3);
    }

    @FunctionalInterface
    private interface Client {
        String getProtocol();
    }

    private static class HttpClient implements Client {
        @Override
        public String getProtocol() {
            return "http";
        }
    }

    private static class CounterDelegate {
        private final Counter delegate;

        @Inject
        CounterDelegate(Counter delegate) {
            this.delegate = delegate;
        }

        public int get() {
            return delegate.get();
        }

        public void increment() {
            delegate.increment();
        }
    }
}
