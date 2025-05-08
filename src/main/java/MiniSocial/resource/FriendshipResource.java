package MiniSocial.resource;

import MiniSocial.Dto.FriendRequestDTO;
import MiniSocial.Entity.Friendship;
import MiniSocial.Entity.User;
import MiniSocial.Service.FriendshipService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.util.List;

@Path("/friends")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FriendshipResource {
    @EJB
    private FriendshipService friendshipService;

    @Context
    private SecurityContext securityContext;

    @POST
    @Path("/request")
    @RolesAllowed({"user", "admin"})
    public Response sendFriendRequest(FriendRequestDTO requestDTO) {
        try {
            User currentUser = (User) securityContext.getUserPrincipal();
            Friendship friendship = friendshipService.sendFriendRequest(currentUser, requestDTO.getFriendId());
            return Response.status(Response.Status.CREATED).entity(friendship).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{friendshipId}/accept")
    @RolesAllowed({"user", "admin"})
    public Response acceptFriendRequest(@PathParam("friendshipId") Long friendshipId) {
        try {
            User currentUser = (User) securityContext.getUserPrincipal();
            Friendship friendship = friendshipService.acceptFriendRequest(friendshipId, currentUser);
            return Response.ok(friendship).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{friendshipId}/reject")
    @RolesAllowed({"user", "admin"})
    public Response rejectFriendRequest(@PathParam("friendshipId") Long friendshipId) {
        try {
            User currentUser = (User) securityContext.getUserPrincipal();
            Friendship friendship = friendshipService.rejectFriendRequest(friendshipId, currentUser);
            return Response.ok(friendship).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/pending")
    @RolesAllowed({"user", "admin"})
    public Response getPendingFriendRequests() {
        User currentUser = (User) securityContext.getUserPrincipal();
        List<Friendship> pendingRequests = friendshipService.getPendingFriendRequests(currentUser);
        return Response.ok(pendingRequests).build();
    }

    @GET
    @Path("/list")
    @RolesAllowed({"user", "admin"})
    public Response getFriends() {
        User currentUser = (User) securityContext.getUserPrincipal();
        List<User> friends = friendshipService.getFriends(currentUser);
        return Response.ok(friends).build();
    }

    @DELETE
    @Path("/{friendshipId}")
    @RolesAllowed({"user", "admin"})
    public Response removeFriend(@PathParam("friendshipId") Long friendshipId) {
        try {
            User currentUser = (User) securityContext.getUserPrincipal();
            friendshipService.removeFriend(friendshipId, currentUser);
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }
} 