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

@Stateless
public class CommentRepositoryImpl implements CommentRepository {
    
    @PersistenceContext(unitName = "MiniSocialPU")
    private EntityManager em;
    
    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == null) {
            em.persist(comment);
            return comment;
        } else {
            return em.merge(comment);
        }
    }
    
    @Override
    public Optional<Comment> findById(Long id) {
        Comment comment = em.find(Comment.class, id);
        return Optional.ofNullable(comment);
    }
    
    @Override
    public List<Comment> findAll() {
        TypedQuery<Comment> query = em.createQuery("SELECT c FROM Comment c", Comment.class);
        return query.getResultList();
    }
    
    @Override
    public void delete(Comment comment) {
        em.remove(em.contains(comment) ? comment : em.merge(comment));
    }
    
    @Override
    public List<Comment> findByPost(Post post) {
        TypedQuery<Comment> query = em.createQuery(
            "SELECT c FROM Comment c WHERE c.post = :post ORDER BY c.createdAt DESC",
            Comment.class
        );
        query.setParameter("post", post);
        return query.getResultList();
    }
    
    @Override
    public List<Comment> findByAuthor(User author) {
        TypedQuery<Comment> query = em.createQuery(
            "SELECT c FROM Comment c WHERE c.author = :author ORDER BY c.createdAt DESC",
            Comment.class
        );
        query.setParameter("author", author);
        return query.getResultList();
    }
}
