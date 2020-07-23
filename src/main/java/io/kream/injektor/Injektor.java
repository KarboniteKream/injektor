package io.kream.injektor;

import io.kream.injektor.internal.Context;

public final class Injektor {
    private final Context context;

    public static Injektor create(Context context) {
        return new Injektor(context);
    }

    private Injektor(Context context) {
        this.context = context;
    }

    public <T> T getInstance(Class<T> type) {
        return context.getFactory(type).get();
    }
}
