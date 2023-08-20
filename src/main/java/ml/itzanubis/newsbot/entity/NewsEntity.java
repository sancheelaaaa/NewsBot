package ml.itzanubis.newsbot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@SuppressWarnings("ALL")
@NoArgsConstructor
public class NewsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String article;

    private String content;

    private Date date;

    public NewsEntity(String article, String content, Date date) {
        this.article = article;
        this.content = content;
        this.date = date;
    }

}
