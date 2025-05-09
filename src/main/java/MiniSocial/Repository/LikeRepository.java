package MiniSocial.Repository;

import MiniSocial.Entity.Like;
import MiniSocial.Entity.Post;
import MiniSocial.Entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Stateless
public class LikeRepository implements BaseRepository<Like> {
    
    @PersistenceContext(unitName = "MiniSocialPU")
    private EntityManager em;
    
    @Override
    public Like save(Like like) {
        if (like.getId() == null) {
            em.persist(like);
            return like;
        } else {
            return em.merge(like);
        }
    }
    
    @Override
    public Optional<Like> findById(Long id) {
        Like like = em.find(Like.class, id);
        return Optional.ofNullable(like);
    }
    
    @Override
    public List<Like> findAll() {
        TypedQuery<Like> query = em.createQuery("SELECT l FROM Like l", Like.class);
        return query.getResultList();
    }
    
    @Override
    public void delete(Like like) {
        em.remove(em.contains(like) ? like : em.merge(like));
    }
    
    public List<Like> findByPost(Post post) {
        TypedQuery<Like> query = em.createQuery(
            "SELECT l FROM Like l WHERE l.post = :post",
            Like.class
        );
        query.setParameter("post", post);
        return query.getResultList();
    }
    
    public List<Like> findByUser(User user) {
        TypedQuery<Like> query = em.createQuery(
            "SELECT l FROM Like l WHERE l.user = :user",
            Like.class
        );
        query.setParameter("user", user);
        return query.getResultList();
    }
    
    public boolean existsLike(User user, Post post) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(l) FROM Like l WHERE l.user = :user AND l.post = :post",
            Long.class
        );
        query.setParameter("user", user);
        query.setParameter("post", post);
        return query.getSingleResult() > 0;
    }
} 