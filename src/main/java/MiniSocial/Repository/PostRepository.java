package MiniSocial.Repository;

import MiniSocial.Entity.Post;
import MiniSocial.Entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends BaseRepository<Post> {
    List<Post> findByAuthor(User author);
    List<Post> findFeedPosts(User user);
}

