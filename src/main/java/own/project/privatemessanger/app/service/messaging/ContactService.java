package own.project.privatemessanger.app.service.messaging;

import org.springframework.stereotype.Service;
import own.project.privatemessanger.app.service.ContactRepository;
import own.project.privatemessanger.app.service.UserRepository;
import own.project.privatemessanger.dto.UserInfo;

import java.util.List;

@Service
public class ContactService {

    private UserRepository userRepository;
    private ContactRepository contactRepository;


    public ContactService(UserRepository userRepository, ContactRepository contactRepository) {
        this.userRepository = userRepository;
        this.contactRepository = contactRepository;
    }

    // returns the username of the added Contact
    public String addContact(String userA, String userB){
        UserInfo usrA = userRepository.searchByName(userA).get(0);
        UserInfo usrB = userRepository.searchByName(userB).get(0);
        if(contactRepository.searchContactByNames(userRepository.getUsersID(userA),
                userRepository.getUsersID(userB))){
            return null;
        }
        contactRepository.addContact(usrA,usrB);
        return usrB.name();
    }

    public String removeContact(String from, String to){
        UserInfo usrA = userRepository.searchByName(from).get(0);
        UserInfo usrB = userRepository.searchByName(to).get(0);
        if(contactRepository.removeContact(usrA,usrB)){
            return usrB.name();
        }
        return null;
    }

    public List<UserInfo> showAllContactsOf(String messengerName){
        UserInfo user = userRepository.getUser(messengerName);
        return contactRepository.getContactsOf(user);
    }

    public String changeNameOfContact(String newName, UserInfo from, UserInfo to){
        if(newName.length() < 5){
            return null;
        }
        return contactRepository.addNameToContact(newName,from,to);
    }

    public String getModifiedName(String principal,String otherUser) {
        String modifiedName = userRepository.getModifiedName(userRepository.getUsersID(principal),
                userRepository.getUsersID(otherUser));
        if(modifiedName == null){
            return null;
        }
        if(modifiedName.equals("unknown")){
            return otherUser;
        }
        return modifiedName;
    }


}
