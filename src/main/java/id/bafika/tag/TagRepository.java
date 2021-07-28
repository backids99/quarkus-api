package id.bafika.tag;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import id.bafika.post.Post;
import id.bafika.post.PostRepository;
import id.bafika.relation.Relation;
import id.bafika.relation.RelationRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class TagRepository implements PanacheRepository<Tag> {

    public List<TagForm> listAllWithPost() {
        List<Tag> tags = Tag.listAll();
        List<TagForm> tagForms = new ArrayList<>();

        for (Tag tag: tags) {
            TagForm tagForm = findTagPostById(tag.id);
            if (tagForm != null) tagForms.add(findTagPostById(tag.id));
        }

        return tagForms;
    }

    public TagForm findTagPostById(long id) {
        Tag tag = findById(id);
        if (tag != null) {
            RelationRepository relationRepository = new RelationRepository();
            List<Relation> relations = relationRepository.find("tagid", tag.id).list();
            List<Post> posts = new ArrayList<>();
            for (Relation relation: relations) {
                Post post = Post.findById(relation.postId);
                posts.add(post);
            }

            TagForm tagForm = new TagForm();
            tagForm.id = tag.id;
            tagForm.label = tag.label;
            tagForm.posts = posts;

            return tagForm;
        } else {
            return null;
        }
    }

    public Tag create(Tag tag) {
        Tag tagFind = Tag.findByLabel(tag.label);
        if (tagFind != null) {
            return tag;
        } else {
            Tag newTag = new Tag();
            newTag.label = tag.label;
            persist(newTag);
            return isPersistent(newTag) ? newTag : null;
        }
    }

    public TagForm create(TagForm tagForm) {
        Tag tag = new Tag();
        tag.label = tagForm.label;
        persist(tag);

        PostRepository postRepository = new PostRepository();
        RelationRepository relationRepository = new RelationRepository();

        for (Post post: tagForm.posts) {
            Post postSaved = postRepository.create(post);
            if (postSaved != null) {
                relationRepository.create(postSaved.id, tag.id);
            }
        }


        return isPersistent(tag) ? tagForm : null;
    }
    
}
