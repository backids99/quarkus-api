package id.bafika.tag;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import id.bafika.post.Post;
import id.bafika.post.PostRepository;
import id.bafika.relation.RelationRepository;

@Path("/tag")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TagResource {

    @Inject
    private TagRepository tagRepository;

    @GET
    public Response getAll() {
        return Response.ok(tagRepository.listAllWithPost()).build();
    }

    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") Long id) {
        TagForm tagForm = tagRepository.findTagPostById(id);
        if (tagForm != null) {
            return Response.ok(tagForm).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Transactional
    public Response create(TagForm tagForm) {
        TagForm tForm = tagRepository.create(tagForm);
        if (tForm != null) {
            return Response.created(URI.create("/tag" + tForm.id)).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT
    @Transactional
    @Path("{id}")
    public Response update(@PathParam("id") Long id, TagForm tagForm) {
        Tag tagFind = tagRepository.findById(id);
        if (tagFind != null) {
            tagFind.label = tagForm.label;

            RelationRepository relationRepository = new RelationRepository();

            List<Post> posts = new ArrayList<>();
            for(Post post: tagForm.posts) {
                Post postFind = Post.findByTitle(post.title);
                if (postFind != null) {
                    relationRepository.create(postFind.id, tagFind.id);

                    posts.add(postFind);
                } else {
                    PostRepository postRepository = new PostRepository();

                    Post newPost = new Post();
                    newPost.title = post.title;
                    newPost.content = post.content;

                    postRepository.persist(newPost);
                    if (postRepository.isPersistent(newPost)) {
                        relationRepository.create(newPost.id, tagFind.id);
                        posts.add(newPost);
                    }
                }
            }

            tagForm.id = tagFind.id;
            tagForm.posts = posts;
            if (tagFind.isPersistent()) {
                return Response.ok(tagForm).build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Transactional
    @Path("{id}")
    public Response deleteById(@PathParam("id") Long id) {
        Tag tag = tagRepository.findById(id);
        if (tag != null) {
            tagRepository.deleteById(id);

            RelationRepository relationRepository = new RelationRepository();
            relationRepository.deleteByTagId(id);

            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}