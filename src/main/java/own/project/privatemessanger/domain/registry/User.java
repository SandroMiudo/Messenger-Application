package own.project.privatemessanger.domain.registry;

import java.util.Optional;

class User {

    Email mail;
    Password password;
    String name;

    User(String name, Email mail, Password password){
        this.name = name;
        this.mail = mail;
        this.password = password;
    }

    void changeProps(Optional<String> mail, Optional<String> password){
        mail.ifPresent(x -> this.mail.changeEmail(x));
        password.ifPresent(x -> this.password.changePassword(x));
    }

    public String getName() {
        return name;
    }
}
