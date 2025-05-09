package MiniSocial.resource;

import MiniSocial.dto.CreatePostDTO;
import MiniSocial.Entity.Post;
import MiniSocial.Entity.User;
import MiniSocial.Service.PostService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.util.List;

@Path("/posts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PostResource {
    @EJB
    private PostService postService;

    @Context
    private SecurityContext securityContext;

    @POST
    @RolesAllowed({"user", "admin"})
    public Response createPost(CreatePostDTO postDTO) {
        User currentUser = (User) securityContext.getUserPrincipal();
        Post post = postService.createPost(currentUser, postDTO);
        return Response.status(Response.Status.CREATED).entity(post).build();
    }

    @GET
    @Path("/{postId}")
    @RolesAllowed({"user", "admin"})
    public Response getPost(@PathParam("postId") Long postId) {
        Post post = postService.getPost(postId);
        if (post == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(post).build();
    }

    @GET
    @Path("/user/{userId}")
    @RolesAllowed({"user", "admin"})
    public Response getUserPosts(@PathParam("userId") Long userId) {
        User user = new User(); // You'll need to fetch the actual user from your user service
        user.setId(userId);
        List<Post> posts = postService.getUserPosts(user);
        return Response.ok(posts).build();
    }

    @GET
    @Path("/feed")
    @RolesAllowed({"user", "admin"})
    public Response getFeed() {
        User currentUser = (User) securityContext.getUserPrincipal();
        List<Post> posts = postService.getFeedPosts(currentUser);
        return Response.ok(posts).build();
    }

    @PUT
    @Path("/{postId}")
    @RolesAllowed({"user", "admin"})
    public Response updatePost(@PathParam("postId") Long postId, CreatePostDTO postDTO) {
        Post existingPost = postService.getPost(postId);
        if (existingPost == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        User currentUser = (User) securityContext.getUserPrincipal();
        if (!existingPost.getAuthor().getId().equals(currentUser.getId()) && 
            !securityContext.isUserInRole("admin")) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        Post updatedPost = postService.updatePost(postId, postDTO);
        return Response.ok(updatedPost).build();
    }

    @DELETE
    @Path("/{postId}")
    @RolesAllowed({"user", "admin"})
    public Response deletePost(@PathParam("postId") Long postId) {
        Post existingPost = postService.getPost(postId);
        if (existingPost == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        User currentUser = (User) securityContext.getUserPrincipal();
        if (!existingPost.getAuthor().getId().equals(currentUser.getId()) && 
            !securityContext.isUserInRole("admin")) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        postService.deletePost(postId);
        return Response.noContent().build();
    }
} 