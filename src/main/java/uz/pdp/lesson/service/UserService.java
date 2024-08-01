package uz.pdp.lesson.service;

import org.springframework.stereotype.Service;
import uz.pdp.lesson.dto.User;

import java.util.List;

@Service
public interface UserService {
        List<User> findByName(String name);
}
