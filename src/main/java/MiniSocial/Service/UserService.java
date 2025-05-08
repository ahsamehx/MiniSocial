package MiniSocial.Service;

import MiniSocial.Entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Stateless
public class UserService {

    @PersistenceContext
    private EntityManager em;

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
}