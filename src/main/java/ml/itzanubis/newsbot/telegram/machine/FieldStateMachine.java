package ml.itzanubis.newsbot.telegram.machine;

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

    @Getter
    private final Map<String, UserState> STATES = new HashMap<>();

    public void addState(final @NonNull String name, final @NonNull UserState state) {
        STATES.put(name, state);
    }

    public UserState getState(final @NonNull String name) {
        return STATES.get(name);
    }

    public void createState(final @NonNull User user, final @NonNull UserState userState) {
        FIELD_STATE_MACHINE.put(user, userState);
    }

    public void cancelState(final @NonNull User user) {
        if (FIELD_STATE_MACHINE.get(user) == null) {
            System.out.println("No StateMachine for User: " + user.getId());
            return;
        }

        FIELD_STATE_MACHINE.remove(user);
    }

    public UserState getState(final @NonNull User user) {
        return FIELD_STATE_MACHINE.get(user);
    }
}
