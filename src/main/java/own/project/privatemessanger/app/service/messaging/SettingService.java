package own.project.privatemessanger.app.service.messaging;

import org.springframework.stereotype.Service;
import own.project.privatemessanger.app.service.UserRepository;
import own.project.privatemessanger.domain.settings.MODE;
import own.project.privatemessanger.domain.settings.Settings;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SettingService {

    private final UserRepository userRepository;

    public SettingService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public MODE loadSettings(String username){
        return userRepository.loadSettings(username);
    }

    // -2 Settings are already active
    // -1 invalid Settings
    // 0 Settings are ok
    public int changeSettings(String username,Boolean dark, Boolean light){
        Settings settings = new Settings();
        if(settings.isValidChange(dark,light)) {
            MODE mode = settings.getMode(dark);
            MODE currentMode = userRepository.loadSettings(username);
            if (currentMode.equals(mode)) {
                return -2;
            }
            userRepository.changeSettings(username, mode);
            return 0;
        }
        return -1;
    }
}
