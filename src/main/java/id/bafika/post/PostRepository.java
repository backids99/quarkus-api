package id.bafika.post;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import id.bafika.relation.Relation;
import id.bafika.relation.RelationRepository;
import id.bafika.tag.Tag;
import id.bafika.tag.TagRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class PostRepository implements PanacheRepository<Post> {

    public List<PostForm> listAllWithTag() {
        List<Post> posts = Post.listAll();
        List<PostForm> postForms = new ArrayList<>();

        for (Post post: posts) {
            PostForm postForm = findPostTagById(post.id);
            if (postForm != null) postForms.add(findPostTagById(post.id));
        }

        return postForms;
    }

    public PostForm findPostTagById(long id) {
        Post post = findById(id);
        if (post != null) {
            RelationRepository relationRepository = new RelationRepository();
            List<Relation> relations = relationRepository.find("postid", post.id).list();
            List<Tag> tags = new ArrayList<>();
            for (Relation relation: relations) {
                Tag tag = Tag.findById(relation.tagId);
                tags.add(tag);
            }

            PostForm postForm = new PostForm();
            postForm.id = post.id;
            postForm.title = post.title;
            postForm.content = post.content;
            postForm.tags = tags;

            return postForm;
        } else {
            return null;
        }
    }

    public Post create(Post post) {
        Post postFind = Post.findByTitle(post.title);
        if (postFind != null) {
            return postFind;
        } else {
            Post newPost = new Post();
            newPost.title = post.title;
            newPost.content = post.content;
            persist(newPost);
            return isPersistent(newPost) ? newPost : null;
        }
    }

    public PostForm create(PostForm postForm) {
        Post post = new Post();
        post.title = postForm.title;
        post.content = postForm.content;
        persist(post);

        TagRepository tagRepository = new TagRepository();
        RelationRepository relationRepository = new RelationRepository();

        for (Tag tag: postForm.tags) {
            Tag tagSaved = tagRepository.create(tag);
            if (tagSaved != null) {
                relationRepository.create(post.id, tagSaved.id);
            }
        }


        return isPersistent(post) ? postForm : null;
    }
}
