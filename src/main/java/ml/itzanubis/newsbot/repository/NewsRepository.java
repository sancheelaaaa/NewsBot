package ml.itzanubis.newsbot.repository;

import ml.itzanubis.newsbot.entity.News;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NewsRepository extends CrudRepository<News, Long> {
    Optional<News> findByArticle(final @NotNull String article);
}
