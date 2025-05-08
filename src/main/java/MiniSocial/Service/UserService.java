package MiniSocial.Service;

import MiniSocial.Entity.User;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.*;

@Stateless
public class UserService {

    @PersistenceContext
    private EntityManager em;

    @EJB
    private GroupService groupService;

    public User register(User user) {
        em.persist(user);
        return user;
    }

    public User findByEmail(String email) {
        TypedQuery<User> query = em.createNamedQuery("User.findByEmail", User.class);
        query.setParameter("email", email);
        return query.getSingleResult();
    }

    public User updateProfile(User user) {
        return em.merge(user);
    }

    public boolean authenticate(String email, String password) {
        try {
            User user = findByEmail(email);
            return user != null && user.getPassword().equals(password);
        } catch (Exception e) {
            return false;
        }
    }

    // ========== DELEGATED GROUP ACTIONS ==========

    public void createGroup(String name, String description, Long creatorId) {
        groupService.createGroup(name, description, creatorId);
    }

    public void requestToJoinGroup(Long userId, Long groupId) {
        groupService.handleJoinRequest(userId, groupId);
    }

    public void leaveGroup(Long userId, Long groupId) {
        groupService.leaveGroup(userId, groupId);
    }
}
