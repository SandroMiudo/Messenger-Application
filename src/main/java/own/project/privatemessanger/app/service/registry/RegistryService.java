package own.project.privatemessanger.app.service.registry;

import org.springframework.security.crypto.password.PasswordEncoder;
import own.project.privatemessanger.app.service.UserRepository;
import own.project.privatemessanger.dto.UserInfo;
import org.springframework.stereotype.Service;
import own.project.privatemessanger.domain.registry.AggregateRoot;

@Service
public class RegistryService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public RegistryService(UserRepository userRepository,PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserInfo createUser(String name, String password, String email){
        long sameName = userRepository.searchByName(name).size();
        if(sameName > 0){
            return new UserInfo("None",null,null,null,null,false);
        }
        UserInfo userInfo = new AggregateRoot.UserBuilder().name(name).email(email).password(password)
                .withEncoder(passwordEncoder).create();

        // userInfo is not set correct !
        if(userInfo.email() == null || userInfo.password() == null){
            return userInfo;
        }
        userInfo = userRepository.addUser(userInfo);
        userRepository.addIP(name);
        return userInfo;
    }

    public UserInfo changeEmail(String email, String username){
        boolean validMail = new AggregateRoot.UserBuilder().checkEmail(email);
        if(validMail){
            String newEmail = userRepository.setNewEmail(email, username);
            return new UserInfo(username,newEmail,null,null,null,false);
        }
        return null;
    }
}
