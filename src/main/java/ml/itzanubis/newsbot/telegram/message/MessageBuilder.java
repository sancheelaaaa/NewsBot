package ml.itzanubis.newsbot.telegram.message;

import org.jetbrains.annotations.NotNull;

public final class MessageBuilder {
    private final StringBuilder builder;

    public MessageBuilder() {
        builder = new StringBuilder();
    }

    public StringBuilder add(final @NotNull String row) {
        builder.append(row);

        return this.builder;
    }

    public String build() {
        return builder.toString();
    }
}
