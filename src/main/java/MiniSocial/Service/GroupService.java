package MiniSocial.Service;

import MiniSocial.Entity.*;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.*;
import java.util.List;

@Stateless
public class GroupService {

    @PersistenceContext
    private EntityManager em;

    @EJB
    private NotificationService notificationService;

    // Create a new group
    public void createGroup(String name, String description, Long creatorId) {
        User creator = em.find(User.class, creatorId);
        if (creator == null) {
            throw new IllegalArgumentException("Creator not found");
        }

        Group group = new Group();
        group.setName(name);
        group.setDescription(description);
        group.setCreator(creator);
        group.getMembers().add(creator);

        em.persist(group);
        creator.getJoinedGroups().add(group);
        em.merge(creator);
    }

    // Handle the request for a user to join a group
    public void handleJoinRequest(Long userId, Long groupId) {
        User user = em.find(User.class, userId);
        Group group = em.find(Group.class, groupId);

        if (user == null || group == null) {
            throw new IllegalArgumentException("User or group not found");
        }

        if (group.getMembers().contains(user)) {
            throw new IllegalStateException("User already in group");
        }

        GroupJoinRequest request = new GroupJoinRequest();
        request.setUser(user);
        request.setGroup(group);
        request.setStatus(RequestStatus.PENDING);
        em.persist(request);

        notificationService.notifyAdmin(
                group.getCreator().getId(),
                "Join request from " + user.getName()
        );
    }

    // Admin processes join request (approve or reject)
    public void processJoinRequest(Long adminId, Long requestId, boolean approve) {
        GroupJoinRequest request = em.find(GroupJoinRequest.class, requestId);
        if (request == null) {
            throw new IllegalArgumentException("Request not found");
        }

        if (!request.getGroup().getCreator().getId().equals(adminId)) {
            throw new SecurityException("Only group admin can process requests");
        }

        if (approve) {
            Group group = request.getGroup();
            User user = request.getUser();

            group.getMembers().add(user);
            user.getJoinedGroups().add(group);

            em.merge(group);
            em.merge(user);

            notificationService.notifyUser(
                    user.getId(),
                    "Approved to join " + group.getName()
            );
        }

        request.setStatus(approve ? RequestStatus.APPROVED : RequestStatus.REJECTED);
        em.merge(request);
    }

    // Admin adds a user to the group
    public void addUserToGroup(Long adminId, Long userId, Long groupId) {
        Group group = em.find(Group.class, groupId);
        User user = em.find(User.class, userId);

        if (group == null || user == null) {
            throw new IllegalArgumentException("User or group not found");
        }

        if (!group.getCreator().getId().equals(adminId)) {
            throw new SecurityException("Only the group admin can add users");
        }

        if (group.getMembers().contains(user)) {
            throw new IllegalStateException("User is already a member of the group");
        }

        group.getMembers().add(user);
        user.getJoinedGroups().add(group);

        em.merge(group);
        em.merge(user);

        notificationService.notifyUser(
                user.getId(),
                "You have been added to the group: " + group.getName()
        );
    }

    // Admin removes a user from the group
    public void removeUserFromGroup(Long adminId, Long userId, Long groupId) {
        Group group = em.find(Group.class, groupId);
        User user = em.find(User.class, userId);

        if (group == null || user == null) {
            throw new IllegalArgumentException("User or group not found");
        }

        if (!group.getCreator().getId().equals(adminId)) {
            throw new SecurityException("Only the group admin can remove users");
        }

        if (!group.getMembers().contains(user)) {
            throw new IllegalStateException("User is not a member of the group");
        }

        group.getMembers().remove(user);
        user.getJoinedGroups().remove(group);

        em.merge(group);
        em.merge(user);

        notificationService.notifyUser(
                user.getId(),
                "You have been removed from the group: " + group.getName()
        );
    }

    // Admin leaves the group (optional)
    public void leaveGroup(Long userId, Long groupId) {
        Group group = em.find(Group.class, groupId);
        User user = em.find(User.class, userId);

        if (group == null || user == null || !group.getMembers().contains(user)) {
            throw new IllegalArgumentException("Invalid leave request");
        }

        group.getMembers().remove(user);
        user.getJoinedGroups().remove(group);

        em.merge(group);
        em.merge(user);
    }

    // Get pending join requests for a group
    public List<GroupJoinRequest> getPendingRequests(Long groupId) {
        return em.createQuery(
                "SELECT r FROM GroupJoinRequest r WHERE r.group.id = :groupId AND r.status = 'PENDING'",
                GroupJoinRequest.class
        ).setParameter("groupId", groupId).getResultList();
    }
}
