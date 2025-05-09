package MiniSocial.Repository;

import MiniSocial.Entity.Friendship;
import MiniSocial.Entity.FriendshipStatus;
import MiniSocial.Entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

@Stateless
public class FriendshipRepositoryImpl implements FriendshipRepository {
    
    @PersistenceContext(unitName = "MiniSocialPU")
    private EntityManager em;
    
    @Override
    public Friendship save(Friendship friendship) {
        if (friendship.getId() == null) {
            em.persist(friendship);
            return friendship;
        } else {
            return em.merge(friendship);
        }
    }
    
    @Override
    public Optional<Friendship> findById(Long id) {
        Friendship friendship = em.find(Friendship.class, id);
        return Optional.ofNullable(friendship);
    }
    
    @Override
    public List<Friendship> findAll() {
        TypedQuery<Friendship> query = em.createQuery("SELECT f FROM Friendship f", Friendship.class);
        return query.getResultList();
    }
    
    @Override
    public void delete(Friendship friendship) {
        em.remove(em.contains(friendship) ? friendship : em.merge(friendship));
    }
    
    @Override
    public List<Friendship> findPendingRequests(User user) {
        TypedQuery<Friendship> query = em.createQuery(
            "SELECT f FROM Friendship f WHERE f.friend = :user AND f.status = :status",
            Friendship.class
        );
        query.setParameter("user", user);
        query.setParameter("status", FriendshipStatus.PENDING);
        return query.getResultList();
    }
    
    @Override
    public List<User> findFriends(User user) {
        TypedQuery<User> query = em.createQuery(
            "SELECT f.friend FROM Friendship f WHERE f.user = :user AND f.status = :status " +
            "UNION " +
            "SELECT f.user FROM Friendship f WHERE f.friend = :user AND f.status = :status",
            User.class
        );
        query.setParameter("user", user);
        query.setParameter("status", FriendshipStatus.ACCEPTED);
        return query.getResultList();
    }
    
    @Override
    public boolean existsFriendship(User user1, User user2) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(f) FROM Friendship f WHERE " +
            "(f.user = :user1 AND f.friend = :user2) OR " +
            "(f.user = :user2 AND f.friend = :user1)",
            Long.class
        );
        query.setParameter("user1", user1);
        query.setParameter("user2", user2);
        return query.getSingleResult() > 0;
    }
}
