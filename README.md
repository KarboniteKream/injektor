# injektor

A simple dependency injection container for Java.

## Example

```java
import io.kream.injektor.Injektor;
import io.kream.injektor.context.Context;
import io.kream.injektor.context.ValueFactory;

import javax.inject.Inject;

public class Main {
    public static void main(String[] args) {
        final Context context = Context.create();
        context.register(Counter.class, ValueFactory.of(new Counter()));

        final Injektor injektor = Injektor.create(context);
        final CounterDelegate counter = injektor.getInstance(CounterDelegate.class);
    }

    static class CounterDelegate {
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

    static class Counter {
        private int count;

        public int get() {
            return count;
        }

        public void increment() {
            count++;
        }
    }
}
```
