package uz.pdp.lesson.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message {
    private Integer id;
    private Integer chatId;
    private Integer sender;
    private String text;

    public Message(Integer chatId, Integer sender, String text) {
        this.chatId = chatId;
        this.sender = sender;
        this.text = text;
    }
}
