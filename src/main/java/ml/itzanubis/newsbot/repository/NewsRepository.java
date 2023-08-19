package ml.itzanubis.newsbot.repository;

import ml.itzanubis.newsbot.entity.NewsEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NewsRepository extends CrudRepository<NewsEntity, Long> {
    Optional<NewsEntity> findByArticle(final @NotNull String article);
}
