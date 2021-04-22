package jwt.course.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.File;

import static jwt.course.backend.constant.FileCostant.USER_FOLDER;

@SpringBootApplication
@Configuration
public class JwtCourseApplication {

    public static void main(String[] args) {
        SpringApplication.run(JwtCourseApplication.class, args);
        new File(USER_FOLDER).mkdirs();
    }

    @Bean
    public BCryptPasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }
}
