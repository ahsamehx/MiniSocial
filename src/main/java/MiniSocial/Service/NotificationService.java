package MiniSocial.Service;

import MiniSocial.Entity.Notification;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Date;

@Stateless
public class NotificationService {

    // Injecting EntityManager
    @PersistenceContext
    private EntityManager em;

    // Notify a user
    public void notifyUser(Long userId, String message) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setTimestamp(new Date()); // Using java.util.Date
        em.persist(notification); // Persisting the notification to the database
    }

    // Notify the admin
    public void notifyAdmin(Long adminId, String message) {
        notifyUser(adminId, "[ADMIN] " + message); // Add [ADMIN] tag to admin notifications
    }
}
