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

    // ✅ Create a new group and return it
    public Group createGroup(String name, String description, Long creatorId) {
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

        return group;
    }

    // ✅ Handles user request to join a group
    public void requestToJoinGroup(Long userId, Long groupId) {
        User user = em.find(User.class, userId);
        Group group = em.find(Group.class, groupId);

        if (user == null || group == null) {
            throw new IllegalArgumentException("User or group not found");
        }

        if (group.getMembers().contains(user)) {
            throw new IllegalStateException("User is already in the group");
        }

        // Prevent duplicate requests
        TypedQuery<Long> existing = em.createQuery(
                "SELECT COUNT(r) FROM GroupJoinRequest r WHERE r.user.id = :uid AND r.group.id = :gid AND r.status = 'PENDING'",
                Long.class
        );
        existing.setParameter("uid", userId);
        existing.setParameter("gid", groupId);
        if (existing.getSingleResult() > 0) {
            throw new IllegalStateException("A pending join request already exists");
        }

        GroupJoinRequest request = new GroupJoinRequest();
        request.setUser(user);
        request.setGroup(group);
        request.setStatus(RequestStatus.PENDING);
        em.persist(request);

        notificationService.notifyAdmin(group.getCreator().getId(), "New join request from " + user.getName());
    }

    // ✅ Admin approves or rejects request
    public void processJoinRequest(Long adminId, Long requestId, boolean approve) {
        GroupJoinRequest request = em.find(GroupJoinRequest.class, requestId);
        if (request == null) {
            throw new IllegalArgumentException("Join request not found");
        }

        Group group = request.getGroup();
        if (!group.getCreator().getId().equals(adminId)) {
            throw new SecurityException("Only the group admin can process this request");
        }

        if (approve) {
            User user = request.getUser();
            group.getMembers().add(user);
            user.getJoinedGroups().add(group);
            em.merge(group);
            em.merge(user);

            notificationService.notifyUser(user.getId(), "Your request to join " + group.getName() + " was approved.");
        }

        request.setStatus(approve ? RequestStatus.APPROVED : RequestStatus.REJECTED);
        em.merge(request);
    }

    // ✅ Admin adds user directly
    public void addUserToGroup(Long adminId, Long userId, Long groupId) {
        Group group = em.find(Group.class, groupId);
        User user = em.find(User.class, userId);

        validateGroupAndAdmin(group, user, adminId);

        if (group.getMembers().contains(user)) {
            throw new IllegalStateException("User is already in the group");
        }

        group.getMembers().add(user);
        user.getJoinedGroups().add(group);

        em.merge(group);
        em.merge(user);

        notificationService.notifyUser(user.getId(), "You were added to " + group.getName());
    }

    // ✅ Admin removes a user
    public void removeUserFromGroup(Long adminId, Long userId, Long groupId) {
        Group group = em.find(Group.class, groupId);
        User user = em.find(User.class, userId);

        validateGroupAndAdmin(group, user, adminId);

        if (!group.getMembers().contains(user)) {
            throw new IllegalStateException("User is not a member of the group");
        }

        group.getMembers().remove(user);
        user.getJoinedGroups().remove(group);

        em.merge(group);
        em.merge(user);

        notificationService.notifyUser(user.getId(), "You were removed from " + group.getName());
    }

    // ✅ Optional: user leaves group
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

    // ✅ Get pending join requests
    public List<GroupJoinRequest> getPendingRequests(Long groupId) {
        return em.createQuery(
                        "SELECT r FROM GroupJoinRequest r WHERE r.group.id = :groupId AND r.status = 'PENDING'",
                        GroupJoinRequest.class
                ).setParameter("groupId", groupId)
                .getResultList();
    }

    // ✅ Get group by ID
    public Group findById(Long groupId) {
        return em.find(Group.class, groupId);
    }

    // ✅ Utility: validate admin rights and entity existence
    private void validateGroupAndAdmin(Group group, User user, Long adminId) {
        if (group == null || user == null) {
            throw new IllegalArgumentException("User or group not found");
        }

        if (!group.getCreator().getId().equals(adminId)) {
            throw new SecurityException("Only the group admin can perform this action");
        }
    }
    public void handleJoinRequest(Long userId, Long groupId) {
        requestToJoinGroup(userId, groupId); // or implement logic here
    }

}
