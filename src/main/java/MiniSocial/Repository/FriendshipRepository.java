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

public interface FriendshipRepository extends BaseRepository<Friendship> {
    List<Friendship> findPendingRequests(User user);
    List<User> findFriends(User user);
    boolean existsFriendship(User user1, User user2);
}

