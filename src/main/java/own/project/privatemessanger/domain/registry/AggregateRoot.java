package own.project.privatemessanger.domain.registry;

import org.springframework.security.crypto.password.PasswordEncoder;
import own.project.privatemessanger.dto.UserInfo;

public class AggregateRoot {

    User user;

    AggregateRoot(String name, Email email, Password password) {
        user = new User(name,email,password);
    }

    public static class UserBuilder{
        String name;
        String email;
        String password;
        PasswordEncoder passwordEncoder;

        public UserBuilder name(String name){
            this.name = name;
            return this;
        }

        public UserBuilder email(String email){
            this.email = email;
            return this;
        }

        public UserBuilder password(String password){
            this.password = password;
            return this;
        }

        public UserBuilder withEncoder(PasswordEncoder passwordEncoder){
            this.passwordEncoder = passwordEncoder;
            return this;
        }

        public UserInfo create(){
            Email email = new Email(this.email);
            Password password = new Password(this.password);
            if(checkProperties(email,password)){
                password = new Password(passwordEncoder.encode(password.characters));
                AggregateRoot aggregateRoot = new AggregateRoot(name,email,password);
                return new UserInfo(aggregateRoot.user.getName(),email.characters,
                        password.characters,null,null,false);
            }
            return checkErrors(email,password);
        }

        private UserInfo checkErrors(Email email, Password password){
            String validateEmail = "None";
            String validatePassword = "None";
            if(!email.valid()){
                validateEmail = null;
            }
            if(!password.exclude_Characters() || !password.min_Length()){
                validatePassword = null;
            }
            return new UserInfo(name,validateEmail,validatePassword,null,null,false);
        }

        private boolean checkProperties(Email email , Password password){
            return email.valid() && password.exclude_Characters() && password.min_Length();
        }

        public boolean checkEmail(String email){
            Email mail = new Email(email);
            return mail.valid();
        }
    }
}
