package MiniSocial.dto;

public class FriendRequestDTO {
    private Long friendId;
    private String message;

    public FriendRequestDTO() {
    }

    public Long getFriendId() {
        return friendId;
    }

    public void setFriendId(Long friendId) {
        this.friendId = friendId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
} 