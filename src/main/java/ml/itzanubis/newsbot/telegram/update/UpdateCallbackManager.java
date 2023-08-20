package ml.itzanubis.newsbot.telegram.update;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public final class UpdateCallbackManager {

    private static final Map<String, UpdateCallbackQueryExecutor> callbacks = new HashMap<>();

    private static final Map<UpdateCallbackQueryExecutor, Object[]> callbacksObjects = new HashMap<>();

    public void addObjects(final @NotNull UpdateCallbackQueryExecutor callbackQueryExecutor,
                           final @NotNull Object[] objects) {

        callbacksObjects.put(callbackQueryExecutor, objects);
    }

    public Object[] getObjects(final @NotNull UpdateCallbackQueryExecutor callbackQueryExecutor) {
        return callbacksObjects.get(callbackQueryExecutor);
    }

    public void create(final @NotNull String query, final @NotNull UpdateCallbackQueryExecutor queryExecutor) {
        callbacks.put(query, queryExecutor);
    }

    public UpdateCallbackQueryExecutor get(final @NotNull String query) {
        return callbacks.get(query);
    }

    public void call(final @NotNull UpdateCallbackQueryExecutor queryExecutor,
                     final @NotNull User user,
                     final @NotNull CallbackQuery callback) {

        queryExecutor.execute(user, callback);
    }

}
