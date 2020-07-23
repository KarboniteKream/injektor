package io.kream.injektor.context;

public abstract class Factory<T> {
    protected void resolve(Context context) {
    }

    public abstract T get();
}
