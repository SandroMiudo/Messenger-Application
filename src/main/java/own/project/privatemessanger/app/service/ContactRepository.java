package own.project.privatemessanger.app.service;

import org.springframework.security.core.userdetails.User;
import own.project.privatemessanger.dto.UserInfo;

import java.util.List;

public interface ContactRepository {
    public void addContact(UserInfo from, UserInfo to);
    public List<UserInfo> getContactsOf(UserInfo user);
    public boolean removeContact(UserInfo user, UserInfo removeUser);
    public String addNameToContact(String givenName, UserInfo from, UserInfo to);
    public boolean searchContactByNames(Integer userIn, Integer userOut);
}
