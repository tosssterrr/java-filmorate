package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "User_friendship", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "friend_id"})
})
public class Friendship {
    @EmbeddedId
    private FriendshipId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("friendId")
    @JoinColumn(name = "friend_id")
    private User friend;

    @Embeddable
    @Data
    public static class FriendshipId implements Serializable {
        private Integer userId;
        private Integer friendId;
    }
}
