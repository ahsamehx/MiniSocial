package MiniSocial.Rest;

import MiniSocial.Entity.User;
import MiniSocial.Service.UserService;
import MiniSocial.Repository.UserRepository;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @EJB
    private UserService userService;

    @EJB
    private UserRepository userRepository;

    @POST
    @Path("/register")
    public Response register(User user) {
        try {
            User registeredUser = userService.register(user);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User registered successfully.");
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Registration failed: " + e.getMessage()).build();
        }
    }

    @POST
    @Path("/login")
    public Response login(User loginRequest) {
        if (userService.authenticate(loginRequest.getEmail(), loginRequest.getPassword())) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Login successful.");
            response.put("token", "JWT-TOKEN"); // TODO: Implement actual JWT token generation
            return Response.ok(response).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity("Invalid credentials").build();
    }

    @PUT
    @Path("/{user_id}/update")
    public Response updateProfile(@PathParam("user_id") Long userId, User user) {
        try {
            user.setId(userId); // Ensure the ID is set from the path parameter
            User updatedUser = userService.updateProfile(user);
            return Response.ok(updatedUser).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Update failed: " + e.getMessage()).build();
        }
    }
}