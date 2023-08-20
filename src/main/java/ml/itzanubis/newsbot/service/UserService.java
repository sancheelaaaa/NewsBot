package ml.itzanubis.newsbot.service;

import jakarta.annotation.PostConstruct;
import lombok.val;
import ml.itzanubis.newsbot.entity.UserEntity;
import ml.itzanubis.newsbot.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final List<UserEntity> userEntities = new ArrayList<>();

    @PostConstruct
    private void init() {
        refresh();
    }

    private void refresh() {
        userEntities.clear();
        userEntities.addAll(userRepository.findAll());
    }

    @Autowired
    public UserService(final @NotNull UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public UserEntity getUser(final @NotNull Long id) {
        return userEntities.stream().filter(userEntity -> userEntity.getId() == id).findAny().orElse(null);
    }

    public UserEntity save(final @NotNull Long id, final @NotNull String language) {
        val userEntity = userRepository.save(new UserEntity(Math.toIntExact(id), language));

        refresh();

        return userEntity;
    }

}
