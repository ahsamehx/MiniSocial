package MiniSocial.Repository;

import MiniSocial.Entity.Comment;
import MiniSocial.Entity.Post;
import MiniSocial.Entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends BaseRepository<Comment> {
    List<Comment> findByPost(Post post);
    List<Comment> findByAuthor(User author);
}

