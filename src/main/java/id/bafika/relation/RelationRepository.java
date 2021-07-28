package id.bafika.relation;

import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;

public class RelationRepository implements PanacheRepository<Relation> {
    public List<Relation> findByPostId(long id) {
        return find("postid", id).list();
    }

    public List<Relation> findByTagId(long id) {
        return find("tagid", id).list();
    }

    public Relation create(long postId, long tagId) {
        Relation relation = find("postid = :postid and tagid = :tagid", Parameters.with("postid", postId).and("tagid", tagId)).firstResult();
        if (relation != null) {
            return relation;
        } else {
            Relation saveRelation = new Relation();
            saveRelation.postId = postId;
            saveRelation.tagId = tagId;

            persist(saveRelation);
            return isPersistent(saveRelation) ? saveRelation : null;
        }
    }

    public void deleteByPostId(long id) {
        List<Relation> relations = findByPostId(id);
        for (Relation relation: relations) {
            deleteById(relation.id);
        }
    }

    public void deleteByTagId(long id) {
        List<Relation> relations = findByTagId(id);
        for (Relation relation: relations) {
            deleteById(relation.id);
        }
    }
}
