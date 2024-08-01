package uz.pdp.lesson.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import uz.pdp.lesson.dto.Message;

import java.util.List;

@Component
public class MessageRepo {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Message> rowMapper = (rs, rowNum) -> {
        int id = rs.getInt("id");
        int chatId = rs.getInt("chat_id");
        int sender = rs.getInt("sender");
        String text = rs.getString("text");
        return new Message(id, chatId, sender, text);
    };
    public MessageRepo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Message message) {
        String query = "INSERT INTO message (chat_id, sender, text) VALUES (?, ?, ?)";
        jdbcTemplate.update(query, message.getChatId(), message.getSender(), message.getText());
    }

    public List<Message> findByChatId(int chatId) {
        String query = "SELECT * FROM message WHERE chat_id = ?";
        return jdbcTemplate.query(query, rowMapper, chatId);
    }

    public void deleteById(int id) {
        String query = "DELETE FROM message WHERE id = ?";
        jdbcTemplate.update(query, id);
    }

    public void deleteByChatId(int chatId) {
        String query = "DELETE FROM message WHERE chat_id = ?";
        jdbcTemplate.update(query, chatId);
    }
}
