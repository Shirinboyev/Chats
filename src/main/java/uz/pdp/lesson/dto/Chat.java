package uz.pdp.lesson.dto;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class Chat {
    private Integer id;
    private String name;
    private int user1Id;
    private int user2Id;

    public Chat(String chatName, Integer user1Id, Integer user2Id) {
        this.name = chatName;
        this.user1Id = user1Id;
        this.user2Id = user2Id;
    }

    public Chat(Integer id, String name, int user1Id, int user2Id) {
        this.id = id;
        this.name = name;
        this.user1Id = user1Id;
        this.user2Id = user2Id;
    }

    public Chat(String chatName) {
        this.name = chatName;
    }

}