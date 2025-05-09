package MiniSocial.Repository;

import MiniSocial.Entity.Post;
import MiniSocial.Entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

@Stateless
public class PostRepositoryImpl implements PostRepository {
    
    @PersistenceContext(unitName = "MiniSocialPU")
    private EntityManager em;
    
    @Override
    public Post save(Post post) {
        if (post.getId() == null) {
            em.persist(post);
            return post;
        } else {
            return em.merge(post);
        }
    }
    
    @Override
    public Optional<Post> findById(Long id) {
        Post post = em.find(Post.class, id);
        return Optional.ofNullable(post);
    }
    
    @Override
    public List<Post> findAll() {
        TypedQuery<Post> query = em.createQuery("SELECT p FROM Post p", Post.class);
        return query.getResultList();
    }
    
    @Override
    public void delete(Post post) {
        em.remove(em.contains(post) ? post : em.merge(post));
    }
    
    @Override
    public List<Post> findByAuthor(User author) {
        TypedQuery<Post> query = em.createQuery(
            "SELECT p FROM Post p WHERE p.author = :author ORDER BY p.createdAt DESC",
            Post.class
        );
        query.setParameter("author", author);
        return query.getResultList();
    }
    
    @Override
    public List<Post> findFeedPosts(User user) {
        TypedQuery<Post> query = em.createQuery(
            "SELECT p FROM Post p WHERE p.author IN " +
            "(SELECT f.friend FROM Friendship f WHERE f.user = :user AND f.status = 'ACCEPTED') " +
            "OR p.author = :user " +
            "ORDER BY p.createdAt DESC",
            Post.class
        );
        query.setParameter("user", user);
        return query.getResultList();
    }
}
