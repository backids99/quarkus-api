package id.bafika.post;

import javax.persistence.Column;
import javax.persistence.Entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Post extends PanacheEntity {

    @Column(length = 100)
    public String title;

    @Column(length = 1000)
    public String content;

    public static Post findByTitle(String title) {
        return find("title", title).firstResult();
    }
}
