package uz.pdp.lesson.repo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import uz.pdp.lesson.dto.Chat;

import java.util.List;
import java.util.Optional;

@Component
public class ChatRepo {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Chat> rowMapper = (rs, rowNum) -> {
        Integer id = rs.getInt("id");
        String name = rs.getString("name");
        int user1Id = rs.getInt("user1_id");
        int user2Id = rs.getInt("user2_id");
        return new Chat(id, name, user1Id, user2Id);
    };

    public ChatRepo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Chat chat) {
        String query = "INSERT INTO chats (name, user1_id, user2_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(query, chat.getName(), chat.getUser1Id(), chat.getUser2Id());
    }

    public List<Chat> findAll() {
        String query = "SELECT * FROM chats";
        return jdbcTemplate.query(query, rowMapper);
    }

    public void deleteById(int id) {
        String query = "DELETE FROM chats WHERE id = ?";
        jdbcTemplate.update(query, id);
    }

    public Optional<Chat> findByName(String chatName) {
        if (chatName == null || chatName.trim().isEmpty()) {
            return Optional.empty();
        }
        String query = "SELECT * FROM chats WHERE name = ?";
        try {
            Chat chat = jdbcTemplate.queryForObject(query, new Object[]{chatName}, rowMapper);
            return Optional.ofNullable(chat);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    public Optional<Chat> findByUserIdAndOtherUserId(int userId1, int userId2) {
        String query = "SELECT * FROM chats WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";
        try {
            Chat chat = jdbcTemplate.queryForObject(
                    query,
                    new Object[]{userId1, userId2, userId2, userId1},
                    rowMapper
            );
            return Optional.ofNullable(chat);
        } catch (Exception e) {
            // Log the exception here
            System.err.println("Error finding chat by user IDs: " + e.getMessage());
            return Optional.empty();
        }
    }

}
