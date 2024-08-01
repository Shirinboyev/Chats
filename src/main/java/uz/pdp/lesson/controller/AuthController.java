package uz.pdp.lesson.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import uz.pdp.lesson.dto.Chat;
import uz.pdp.lesson.dto.ChatDTO;
import uz.pdp.lesson.dto.Message;
import uz.pdp.lesson.dto.User;
import uz.pdp.lesson.repo.ChatRepo;
import uz.pdp.lesson.repo.MessageRepo;
import uz.pdp.lesson.repo.UserRepo;
import uz.pdp.lesson.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class AuthController {
    private Message message;
    private Chat chat;
    private static List<ChatDTO> getChatDTOS(List<User> users, Integer userId) {
        if (users == null) {
            users = new ArrayList<>();
        }
        return users.stream()
                .filter(u -> !Objects.equals(u.getId(), userId))
                .map(ChatDTO::new)
                .collect(Collectors.toList());
    }

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ChatRepo chatRepo;

    @Autowired
    private MessageRepo messageRepo;

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String authenticateUser(@RequestParam String username, @RequestParam String password, Model model, HttpSession session) {
        User user = userRepo.findByFullName(username);
        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("loggedInUser", user);
            return "redirect:/profile";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @PostMapping("/signup")
    public String registerUser(@RequestParam String username, @RequestParam String phoneNumber, @RequestParam String password, Model model) {
        if (userRepo.findByFullName(username) != null) {
            model.addAttribute("error", "User already exists");
            return "signup";
        }

        User newUser = new User(username, phoneNumber, password);
        userRepo.save(newUser);

        Chat defaultChat = new Chat("Default Chat");
        chatRepo.save(defaultChat);

        Message welcomeMessage = new Message(defaultChat.getId(), message.getSender(), "Welcome to the default chat!");
        messageRepo.save(welcomeMessage);

        return "redirect:/login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }


}
