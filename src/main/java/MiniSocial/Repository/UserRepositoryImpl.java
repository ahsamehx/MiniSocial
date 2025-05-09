package MiniSocial.Repository;

import MiniSocial.Entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

@Stateless
public class    UserRepositoryImpl implements UserRepository {
    
    @PersistenceContext(unitName = "MiniSocialPU")
    private EntityManager em;
    
    @Override
    public User save(User user) {
        if (user.getId() == null) {
            em.persist(user);
            return user;
        } else {
            return em.merge(user);
        }
    }
    
    @Override
    public Optional<User> findById(Long id) {
        User user = em.find(User.class, id);
        return Optional.ofNullable(user);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        TypedQuery<User> query = em.createNamedQuery("User.findByEmail", User.class);
        query.setParameter("email", email);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    @Override
    public List<User> findAll() {
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u", User.class);
        return query.getResultList();
    }
    
    @Override
    public void delete(User user) {
        em.remove(em.contains(user) ? user : em.merge(user));
    }
}
