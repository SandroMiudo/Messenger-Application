package own.project.privatemessanger.app.service.user;

import org.springframework.stereotype.Service;
import own.project.privatemessanger.app.service.UserRepository;
import own.project.privatemessanger.dto.UserInfo;

import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserInfo> getUsers(String regex, String username){
        return userRepository.getByRegEX(regex,username);
    }

    public Integer getUsersID(String username){
        return userRepository.getUsersID(username);
    }
}
