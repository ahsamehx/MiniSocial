package MiniSocial.Rest;

import MiniSocial.Entity.Group;
import MiniSocial.Service.GroupService;
import jakarta.ejb.EJB;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

@Path("/groups")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GroupResource {

    @EJB
    private GroupService groupService;

    // --- Group Creation ---
    @POST
    public Response createGroup(
            @HeaderParam("X-User-ID") Long creatorId,
            @Valid GroupForm form
    ) {
        if (creatorId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing X-User-ID header").build();
        }

        try {
            Group group = groupService.createGroup(form.getName(), form.getDescription(), creatorId);
            return Response.status(Response.Status.CREATED).entity(group).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    // --- User Request to Join ---
    @POST
    @Path("/{groupId}/requests")
    public Response requestToJoin(
            @PathParam("groupId") Long groupId,
            @HeaderParam("X-User-ID") Long userId
    ) {
        if (userId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing X-User-ID header").build();
        }

        try {
            groupService.requestToJoinGroup(userId, groupId);
            return Response.ok("Join request sent to admin").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    // --- Admin Approves/Rejects Join Request ---
    @PUT
    @Path("/{groupId}/requests/{requestId}")
    public Response processJoinRequest(
            @PathParam("groupId") Long groupId,
            @PathParam("requestId") Long requestId,
            @QueryParam("approve") boolean approve,
            @HeaderParam("X-Admin-ID") Long adminId
    ) {
        if (adminId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing X-Admin-ID header").build();
        }

        try {
            groupService.processJoinRequest(adminId, requestId, approve);
            return Response.ok("Request " + (approve ? "approved" : "rejected")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    // --- Admin Adds a User to Group ---
    @POST
    @Path("/{groupId}/members/{userId}")
    public Response addUserToGroup(
            @HeaderParam("X-Admin-ID") Long adminId,
            @PathParam("groupId") Long groupId,
            @PathParam("userId") Long userId
    ) {
        if (adminId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing X-Admin-ID header").build();
        }

        try {
            groupService.addUserToGroup(adminId, userId, groupId);
            return Response.ok("User added to group").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    // --- Admin Removes a User from Group ---
    @DELETE
    @Path("/{groupId}/members/{userId}")
    public Response removeUserFromGroup(
            @HeaderParam("X-Admin-ID") Long adminId,
            @PathParam("groupId") Long groupId,
            @PathParam("userId") Long userId
    ) {
        if (adminId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing X-Admin-ID header").build();
        }

        try {
            groupService.removeUserFromGroup(adminId, userId, groupId);
            return Response.ok("User removed from group").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    // --- Get Group Info ---
    @GET
    @Path("/{groupId}")
    public Response getGroup(@PathParam("groupId") Long groupId) {
        Group group = groupService.findById(groupId);
        return group != null
                ? Response.ok(group).build()
                : Response.status(Response.Status.NOT_FOUND).entity("Group not found").build();
    }

    // --- DTO: Group Creation Form ---
    public static class GroupForm {
        @NotBlank
        private String name;

        private String description;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
