package MiniSocial.Rest;

import MiniSocial.Entity.User;
import MiniSocial.Service.UserService;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @EJB
    private UserService userService;

    @POST
    @Path("/register")
    public Response register(User user) {
        try {
            User registeredUser = userService.register(user);
            return Response.ok(registeredUser).build(); //return Json
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Registration failed: " + e.getMessage()).build();
        }
    }

    @POST
    @Path("/login")
    public Response login(User loginRequest) {
        if (userService.authenticate(loginRequest.getEmail(), loginRequest.getPassword())) {
            return Response.ok().entity("Login successful").build();
        }
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity("Invalid credentials").build();
    }

    @PUT
    @Path("/{id}")
    public Response updateProfile(@PathParam("id") Long id, User user) {     //user is taken as a JSON
        try {
            User updatedUser = userService.updateProfile(user);
            return Response.ok(updatedUser).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Update failed: " + e.getMessage()).build();
        }
    }
}