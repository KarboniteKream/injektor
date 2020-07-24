package io.kream.injektor;

import io.kream.injektor.context.Context;

public final class Injektor {
    private final Context context;

    private Injektor(Context context) {
        this.context = context;
    }

    public static Injektor create(Context context) {
        return new Injektor(context);
    }

    public <T> T getInstance(Class<T> type) {
        return context.getFactory(type).get();
    }
}
