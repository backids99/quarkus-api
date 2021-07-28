package id.bafika.relation;

import javax.persistence.Entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Relation extends PanacheEntity {
    public long postId;
    public long tagId;
}