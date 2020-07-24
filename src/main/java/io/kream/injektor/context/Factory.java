package io.kream.injektor.context;

import javax.inject.Provider;

public abstract class Factory<T> implements Provider<T> {
    protected void resolve(Context context) {
    }
}
