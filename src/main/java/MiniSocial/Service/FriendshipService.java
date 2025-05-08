package MiniSocial.Service;

import MiniSocial.Dto.FriendRequestDTO;
import MiniSocial.Entity.Friendship;
import MiniSocial.Entity.FriendshipStatus;
import MiniSocial.Entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Stateless
public class FriendshipService {
    @PersistenceContext
    private EntityManager entityManager;

    public Friendship sendFriendRequest(User sender, Long receiverId) {
        User receiver = entityManager.find(User.class, receiverId);
        if (receiver == null) {
            throw new IllegalArgumentException("User not found");
        }

        // Check if friendship already exists
        TypedQuery<Friendship> query = entityManager.createQuery(
            "SELECT f FROM Friendship f WHERE (f.user = :user1 AND f.friend = :user2) " +
            "OR (f.user = :user2 AND f.friend = :user1)",
            Friendship.class
        );
        query.setParameter("user1", sender);
        query.setParameter("user2", receiver);

        List<Friendship> existingFriendships = query.getResultList();
        if (!existingFriendships.isEmpty()) {
            throw new IllegalStateException("Friendship already exists");
        }

        Friendship friendship = new Friendship();
        friendship.setUser(sender);
        friendship.setFriend(receiver);
        friendship.setStatus(FriendshipStatus.PENDING);

        entityManager.persist(friendship);
        return friendship;
    }

    public Friendship acceptFriendRequest(Long friendshipId, User user) {
        Friendship friendship = entityManager.find(Friendship.class, friendshipId);
        if (friendship == null || !friendship.getFriend().equals(user)) {
            throw new IllegalArgumentException("Friend request not found");
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        entityManager.merge(friendship);
        return friendship;
    }

    public Friendship rejectFriendRequest(Long friendshipId, User user) {
        Friendship friendship = entityManager.find(Friendship.class, friendshipId);
        if (friendship == null || !friendship.getFriend().equals(user)) {
            throw new IllegalArgumentException("Friend request not found");
        }

        friendship.setStatus(FriendshipStatus.REJECTED);
        entityManager.merge(friendship);
        return friendship;
    }

    public List<Friendship> getPendingFriendRequests(User user) {
        TypedQuery<Friendship> query = entityManager.createQuery(
            "SELECT f FROM Friendship f WHERE f.friend = :user AND f.status = :status",
            Friendship.class
        );
        query.setParameter("user", user);
        query.setParameter("status", FriendshipStatus.PENDING);
        return query.getResultList();
    }

    public List<User> getFriends(User user) {
        TypedQuery<User> query = entityManager.createQuery(
            "SELECT f.friend FROM Friendship f WHERE f.user = :user AND f.status = :status " +
            "UNION " +
            "SELECT f.user FROM Friendship f WHERE f.friend = :user AND f.status = :status",
            User.class
        );
        query.setParameter("user", user);
        query.setParameter("status", FriendshipStatus.ACCEPTED);
        return query.getResultList();
    }

    public void removeFriend(Long friendshipId, User user) {
        Friendship friendship = entityManager.find(Friendship.class, friendshipId);
        if (friendship == null || 
            (!friendship.getUser().equals(user) && !friendship.getFriend().equals(user))) {
            throw new IllegalArgumentException("Friendship not found");
        }

        entityManager.remove(friendship);
    }
} 