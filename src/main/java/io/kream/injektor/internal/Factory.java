package io.kream.injektor.internal;

public abstract class Factory<T> {
    protected void resolve(Context context) {
    }

    public abstract T get();
}
