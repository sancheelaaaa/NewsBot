package ml.itzanubis.newsbot.telegram.machine;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class FieldStateMachine {
    @Getter
    private final Map<User, UserState> FIELD_STATE_MACHINE = new HashMap<>();

    private final Map<UserState, Object[]> callbacks = new HashMap<>();

    @Getter
    private final Map<String, UserState> STATES = new HashMap<>();

    public void addCallback(final @NotNull UserState state, final @NotNull Object[] callback) {
        callbacks.put(state, callback);
    }

    public Object[] getCallback(final @NotNull UserState state) {
        return callbacks.get(state);
    }

    public void clearCallback(final @NotNull UserState state) {
        callbacks.remove(state);
    }

    public void addState(final @NonNull String name, final @NonNull UserState state) {
        STATES.put(name, state);
    }

    public void createState(final @NonNull User user, final @NonNull UserState userState) {
        FIELD_STATE_MACHINE.put(user, userState);
    }

    public void cancelState(final @NonNull User user) {
        if (FIELD_STATE_MACHINE.get(user) == null) {
            return;
        }

        FIELD_STATE_MACHINE.remove(user);
    }

    public UserState getState(final @NonNull User user) {
        return FIELD_STATE_MACHINE.get(user);
    }
}
