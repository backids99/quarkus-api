package id.bafika.post;

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

import id.bafika.relation.RelationRepository;
import id.bafika.tag.Tag;
import id.bafika.tag.TagRepository;

@Path("/post")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PostResource {

    @Inject
    private PostRepository postRepository;

    @GET
    public Response getAll() {
        return Response.ok(postRepository.listAllWithTag()).build();
    }

    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") Long id) {
        PostForm postForm = postRepository.findPostTagById(id);
        if (postForm != null) {
            return Response.ok(postForm).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Transactional
    public Response create(PostForm postForm) {
        PostForm pForm = postRepository.create(postForm);
        if (pForm != null) {
            return Response.created(URI.create("/post" + pForm.id)).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT
    @Transactional
    @Path("{id}")
    public Response update(@PathParam("id") Long id, PostForm postForm) {
        Post postFind = postRepository.findById(id);
        if (postFind != null) {
            postFind.title = postForm.title;
            postFind.content = postForm.content;

            RelationRepository relationRepository = new RelationRepository();

            List<Tag> tags = new ArrayList<>();
            for(Tag tag: postForm.tags) {
                Tag tagFind = Tag.findByLabel(tag.label);
                if (tagFind != null) {
                    relationRepository.create(postFind.id, tagFind.id);

                    tags.add(tagFind);
                } else {
                    TagRepository tagRepository = new TagRepository();

                    Tag newTag = new Tag();
                    newTag.label = tag.label;

                    tagRepository.persist(newTag);
                    if (tagRepository.isPersistent(newTag)) {
                        relationRepository.create(postFind.id, newTag.id);
                        tags.add(newTag);
                    }
                }
            }

            postForm.id = postFind.id;
            postForm.tags = tags;
            if (postFind.isPersistent()) {
                return Response.ok(postForm).build();
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
        Post post = postRepository.findById(id);
        if (post != null) {
            postRepository.deleteById(id);

            RelationRepository relationRepository = new RelationRepository();
            relationRepository.deleteByPostId(id);

            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}