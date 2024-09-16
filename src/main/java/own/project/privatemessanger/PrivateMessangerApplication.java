package own.project.privatemessanger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import own.project.privatemessanger.spring.TextParser;
import own.project.privatemessanger.spring.TextParserProperties;

import java.util.Map;

@SpringBootApplication
public class PrivateMessangerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrivateMessangerApplication.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
