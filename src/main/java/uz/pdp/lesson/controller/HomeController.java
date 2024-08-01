package uz.pdp.lesson.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import uz.pdp.lesson.dto.Chat;
import uz.pdp.lesson.dto.ChatDTO;
import uz.pdp.lesson.dto.Message;
import uz.pdp.lesson.dto.User;
import uz.pdp.lesson.repo.ChatRepo;
import uz.pdp.lesson.repo.MessageRepo;
import uz.pdp.lesson.repo.UserRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ChatRepo chatRepo;

    @Autowired
    private MessageRepo messageRepo;

    private static List<ChatDTO> getChatDTOS(List<User> users, Integer userId) {
        if (users == null) {
            users = new ArrayList<>();
        }
        return users.stream()
                .filter(u -> !Objects.equals(u.getId(), userId))
                .map(ChatDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/profile")
    public String profile(@RequestParam(required = false) Integer userId, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        List<User> users = userRepo.findAll().stream()
                .filter(u -> !u.getId().equals(loggedInUser.getId()))
                .collect(Collectors.toList());
        model.addAttribute("users", users);

        if (userId == null && !users.isEmpty()) {
            userId = users.get(0).getId();
        }

        if (userId == null) {
            model.addAttribute("error", "No user selected and no users available");
            model.addAttribute("selectedUserName", "No User Selected");
            model.addAttribute("messages", List.of());
            return "profile";
        }

        Optional<Chat> chatOpt = chatRepo.findByUserIdAndOtherUserId(loggedInUser.getId(), userId);
        if (chatOpt.isPresent()) {
            Chat chat = chatOpt.get();
            List<Message> selectedMessages = messageRepo.findByChatId(chat.getId());
            model.addAttribute("selectedUserId", userId);
            model.addAttribute("selectedUserName", userRepo.findById(userId).getFullName());
            model.addAttribute("messages", selectedMessages);
        } else {
            model.addAttribute("error", "Chat not found");
            model.addAttribute("selectedUserName", "No User Selected");
            model.addAttribute("messages", List.of());
        }

        return "profile";
    }

    @PostMapping("/send-message")
    public String sendMessage(@RequestParam Integer recipientId, @RequestParam String message, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        int senderId = loggedInUser.getId();

        Optional<Chat> chatOpt = chatRepo.findByUserIdAndOtherUserId(senderId, recipientId);
        if (chatOpt.isEmpty()) {
            Chat newChat = new Chat("Chat between " + senderId + " and " + recipientId, senderId, recipientId);
            chatRepo.save(newChat);
            chatOpt = Optional.of(newChat);
        }

        Chat chat = chatOpt.get();
        Message newMessage = new Message(chat.getId(), senderId, message);
        messageRepo.save(newMessage);

        return "redirect:/profile?userId=" + recipientId;
    }

    @PostMapping("/create-chat")
    public String createChat(@RequestParam String chatName, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        int userId = loggedInUser != null ? loggedInUser.getId() : -1;

        if (chatRepo.findByName(chatName).isPresent()) {
            return "redirect:/profile?error=Chat+already+exists";
        }

        Chat newChat = new Chat(chatName, userId, userId);
        chatRepo.save(newChat);

        Message introMessage = new Message(newChat.getId(), userId, "Chat created successfully.");
        messageRepo.save(introMessage);

        return "redirect:/profile?chatId=" + newChat.getId();
    }

    @GetMapping("/delete-chat")
    public String deleteChat(@RequestParam Integer chatId) {
        chatRepo.deleteById(chatId);
        messageRepo.deleteByChatId(chatId);
        return "redirect:/profile";
    }

    @GetMapping("/delete-message")
    public String deleteMessage(@RequestParam Integer messageId) {
        messageRepo.deleteById(messageId);
        return "redirect:/profile";
    }

    @PostMapping("/logout")
    public ModelAndView logout(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView modelAndView = new ModelAndView();
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
            Cookie cookie = new Cookie("JSESSIONID", null);
            cookie.setMaxAge(0);
            resp.addCookie(cookie);
        }
        modelAndView.setViewName("redirect:/home");
        return modelAndView;
    }

    @PostMapping("/search")
    public ModelAndView search(@RequestParam("search") String search, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (search == null || search.isBlank()) {
            search = "";
        }
        List<User> users = (List<User>) userRepo.findByFullName(search);
        List<ChatDTO> chats = getChatDTOS(users, userId);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("chats", chats);
        modelAndView.setViewName("home");
        return modelAndView;
    }

    @GetMapping("/chat/{id}")
    public String getChat(@PathVariable("id") int userId, Model model) {
        User selectedUser = userRepo.findById(userId);
        if (selectedUser == null) {
            return "redirect:/";
        }

        List<Message> messages = messageRepo.findByChatId(userId);

        model.addAttribute("selectedUserName", selectedUser.getFullName());
        model.addAttribute("selectedUserId", selectedUser.getId());
        model.addAttribute("users", userRepo.findAll());
        model.addAttribute("messages", messages);

        return "profile";
    }
}