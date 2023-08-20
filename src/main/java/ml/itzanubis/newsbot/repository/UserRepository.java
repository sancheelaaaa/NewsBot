package ml.itzanubis.newsbot.repository;

import ml.itzanubis.newsbot.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> getUserById(final int id);

}
