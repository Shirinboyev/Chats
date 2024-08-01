package uz.pdp.lesson;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import uz.pdp.lesson.dto.User;
import uz.pdp.lesson.jdbcConfig.JdbcConfig;
import uz.pdp.lesson.repo.UserRepo;

public class Main {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JdbcConfig.class);
        UserRepo repo = context.getBean(UserRepo.class);
        repo.save(User.builder().fullName("gayratbek").phoneNumber("+998 97 395 67 07").password("1111").build());
    }
}
