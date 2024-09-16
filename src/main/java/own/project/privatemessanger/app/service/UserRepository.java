package own.project.privatemessanger.app.service;

import own.project.privatemessanger.domain.settings.MODE;
import own.project.privatemessanger.dto.UserInfo;

import java.util.List;

public interface UserRepository {
    List<UserInfo> searchByName(String name);

    UserInfo addUser(UserInfo userInfo);

    String verifyUser(String name);

    String setNewEmail(String email, String username);

    void addIP(String username);

    UserInfo getUser(String username);

    Integer getUsersID(String username);

    List<UserInfo> getByRegEX(String regex, String username);

    String getUsersName(Integer ID);

    String getModifiedName(Integer userIn, Integer userOut);

    void changeSettings(String username, MODE mode);

    MODE loadSettings(String username);
}
