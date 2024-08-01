package uz.pdp.lesson.repo;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import uz.pdp.lesson.dto.User;

import java.util.List;

@Component
public class UserRepo {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> rowMapper = (rs, rowNum) -> {
        int id = rs.getInt("id");
        String fullName = rs.getString("full_name");
        String phoneNumber = rs.getString("phone_number");
        String password = rs.getString("password");
        return User.builder().id(id).fullName(fullName).phoneNumber(phoneNumber).password(password).build();
    };

    public UserRepo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(User user) {
        String query = "INSERT INTO users (full_name, phone_number, password) VALUES (?, ?, ?)";
        jdbcTemplate.update(query, user.getFullName(), user.getPhoneNumber(), user.getPassword());
    }

    public List<User> getAll() {
        String query = "SELECT * FROM users";
        return jdbcTemplate.query(query, rowMapper);
    }

    public User findByFullName(String fullName) {
        String query = "SELECT * FROM users WHERE full_name = ?";
        try {
            return jdbcTemplate.queryForObject(query, rowMapper, fullName);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<User> findAll() {
        String query = "SELECT * FROM users";
        return jdbcTemplate.query(query, rowMapper);
    }
    public User findById(int id) {
        String query = "SELECT * FROM users WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(query, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    public List<User> findByFullNameContaining(String searchQuery) {
        String query = "SELECT * FROM users WHERE full_name LIKE ?";
        String searchPattern = "%" + searchQuery + "%";
        return jdbcTemplate.query(query, rowMapper, searchPattern);
    }

}
