package MiniSocial.Service;

import MiniSocial.dto.CreatePostDTO;
import MiniSocial.Entity.Post;
import MiniSocial.Entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Stateless
public class PostService {
    @PersistenceContext
    private EntityManager entityManager;

    public Post createPost(User author, CreatePostDTO postDTO) {
        Post post = new Post();
        post.setAuthor(author);
        post.setContent(postDTO.getContent());
        post.setImageUrl(postDTO.getImageUrl());
        post.setLinkUrl(postDTO.getLinkUrl());
        
        entityManager.persist(post);
        return post;
    }

    public Post getPost(Long postId) {
        return entityManager.find(Post.class, postId);
    }

    public List<Post> getUserPosts(User user) {
        TypedQuery<Post> query = entityManager.createQuery(
            "SELECT p FROM Post p WHERE p.author = :user ORDER BY p.createdAt DESC",
            Post.class
        );
        query.setParameter("user", user);
        return query.getResultList();
    }

    public List<Post> getFeedPosts(User user) {
        TypedQuery<Post> query = entityManager.createQuery(
            "SELECT p FROM Post p WHERE p.author IN " +
            "(SELECT f.friend FROM Friendship f WHERE f.user = :user AND f.status = 'ACCEPTED') " +
            "OR p.author = :user " +
            "ORDER BY p.createdAt DESC",
            Post.class
        );
        query.setParameter("user", user);
        return query.getResultList();
    }

    public Post updatePost(Long postId, CreatePostDTO postDTO) {
        Post post = getPost(postId);
        if (post != null) {
            post.setContent(postDTO.getContent());
            post.setImageUrl(postDTO.getImageUrl());
            post.setLinkUrl(postDTO.getLinkUrl());
            entityManager.merge(post);
        }
        return post;
    }

    public void deletePost(Long postId) {
        Post post = getPost(postId);
        if (post != null) {
            entityManager.remove(post);
        }
    }
} 