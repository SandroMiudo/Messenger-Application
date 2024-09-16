package own.project.privatemessanger.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import own.project.privatemessanger.app.service.MailProvider;
import own.project.privatemessanger.app.service.messaging.ContactService;
import own.project.privatemessanger.app.service.messaging.MessengerService;
import own.project.privatemessanger.app.service.messaging.SettingService;
import own.project.privatemessanger.app.service.user.UserService;
import own.project.privatemessanger.app.service.verification.VerificationService;
import own.project.privatemessanger.domain.settings.MODE;
import own.project.privatemessanger.dto.MessageInfo;
import own.project.privatemessanger.dto.UserInfo;
import own.project.privatemessanger.spring.NumberGenerator;
import javax.servlet.http.HttpServletRequest;
import java.nio.file.Path;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
public class MessengerController {

    private SettingService settingService;
    private VerificationService verificationService;
    private MessengerService messengerService;
    private ContactService contactService;
    private MailProvider mailProvider;
    private UserService userService;

    public MessengerController(SettingService settingService, VerificationService verificationService, MessengerService messengerService,
                               MailProvider mailProvider, ContactService contactService, UserService userService) {
        this.settingService = settingService;
        this.verificationService = verificationService;
        this.messengerService = messengerService;
        this.mailProvider = mailProvider;
        this.contactService = contactService;
        this.userService = userService;
    }

    @Secured("ROLE_USER")
    @GetMapping("/messaging/homescreen")
    public String index(HttpServletRequest request, Principal principal, Model model){
        String email = verificationService.securingUser(request, principal);
        if(email != null){
            String randomNumber = NumberGenerator.generateRandomNumber();
            Path path = Path.of("src/main/resources/templates/loginverificationmail.html");
            mailProvider.setPath(path);
            mailProvider.sendEmail(email,principal.getName(),randomNumber);
        }
        MODE mode = settingService.loadSettings(principal.getName());
        model.addAttribute("mode",mode.name());
        if(mode.name().equals(MODE.DARK_MODE.name())){
            model.addAttribute("modeItem","white");
        }
        List<own.project.privatemessanger.spring.MessageUserMapper> allMessagesFromUser =
                messengerService.getAllMessagesFromUser(principal.getName());
        model.addAttribute("messages",allMessagesFromUser);
        return "index";
    }

    @Secured("ROLE_USER")
    @GetMapping("/messaging/chat/contacts")
    public String addChatRoom(Principal principal, Model model){
        List<UserInfo> contacts = contactService.showAllContactsOf(principal.getName());
        MODE mode = settingService.loadSettings(principal.getName());
        model.addAttribute("mode",mode);
        model.addAttribute("contacts",contacts);
        return "chats";
    }

    @Secured("ROLE_USER")
    @GetMapping("/messaging/chat/room/{otherUser}")
    public String openChat(Principal principal,@PathVariable String otherUser, Model model,
                           RedirectAttributes redirectAttributes){
        List<MessageInfo> conversation = messengerService.getConversation(principal.getName(), otherUser);
        if(!conversation.isEmpty()){
            model.addAttribute("userIn",userService.getUsersID(principal.getName()));
            model.addAttribute("userOut",userService.getUsersID(otherUser));
        }
        String modifiedName = contactService.getModifiedName(principal.getName(), otherUser);
        if(modifiedName == null){
            redirectAttributes.addAttribute("error","exit1");
            return "redirect:/messaging/homescreen";
        }
        MODE mode = settingService.loadSettings(principal.getName());
        model.addAttribute("mode",mode.name());
        if(mode.equals(MODE.DARK_MODE)){
            model.addAttribute("modeItem","item");
        }
        Map<LocalDateTime, List<MessageInfo>> listOfConversation = messengerService.seperateMessagePerDay(conversation);
        model.addAttribute("modifiedName",modifiedName);
        model.addAttribute("conversation",listOfConversation);
        model.addAttribute("otherUser",otherUser);
        return "chatroom";
    }

    @Secured("ROLE_USER")
    @PostMapping("/messaging/chat/room/message")
    public String createMessage(Principal principal, String otherUser, String message){
        messengerService.sendMessage(principal.getName(),otherUser,message);
        return "redirect:/messaging/chat/room/"+otherUser;
    }

    @Secured("ROLE_USER")
    @GetMapping("/messaging/addContact")
    public String addContact(Principal principal, Model model){
        List<UserInfo> allUsers = userService.getUsers("", principal.getName());
        MODE mode = settingService.loadSettings(principal.getName());
        model.addAttribute("allUsers",allUsers);
        model.addAttribute("username",principal.getName());
        model.addAttribute("contacts",contactService.showAllContactsOf(principal.getName()));
        model.addAttribute("mode",mode.name());
        if(mode.equals(MODE.DARK_MODE)){
            model.addAttribute("modeItem","mode");
        }
        return "contacts";
    }

    @Secured("ROLE_USER")
    @PostMapping("/messaging/addContact/{user}")
    public String addContactforUser(@PathVariable String user, String contact){
        String s = contactService.addContact(user, contact);
        return "redirect:/messaging/chat/room/"+s;
    }

    @Secured("ROLE_USER")
    @GetMapping("/messaging/removeContact/{user}")
    public String removeUser(@PathVariable String user, Principal principal, RedirectAttributes redirectAttributes){
        String removedContact = contactService.removeContact(principal.getName(), user);
        redirectAttributes.addFlashAttribute("removedContact",removedContact);
        return "redirect:/messaging/addContact";
    }

    @Secured("ROLE_USER")
    @GetMapping("/messaging/modifyName/{otherUser}")
    public String modifyContactName(@PathVariable String otherUser, Principal principal, Model model){
        model.addAttribute("otherUser",otherUser);
        model.addAttribute("user",principal.getName());
        MODE mode = settingService.loadSettings(principal.getName());
        model.addAttribute("mode",mode.name());
        if(mode.equals(MODE.DARK_MODE)){
            model.addAttribute("modeItem","mode");
        }
        return "modifyContactName";
    }

    @Secured("ROLE_USER")
    @PostMapping("/messaging/modifyName/{otherUser}")
    public String modifyContactNameForm(@PathVariable String otherUser, Principal principal, String modifiedName){
        UserInfo userIn = new UserInfo(otherUser,null,null,null,null,true);
        UserInfo userOut = new UserInfo(principal.getName(),null,null,null,null,true);
        String s = contactService.changeNameOfContact(modifiedName, userIn, userOut);
        if(s == null){
            return "redirect:/messaging/modifyName/"+otherUser;
        }
        return "redirect:/messaging/homescreen";
    }

    @Secured("ROLE_USER")
    @GetMapping("/messaging/settings")
    public String settings(String errorInvalid, String errorInUse,Model model, Principal principal){
        MODE mode = settingService.loadSettings(principal.getName());
        if(mode.equals(MODE.DARK_MODE)){
            model.addAttribute("modeItem","mode");
        }
        model.addAttribute("mode",mode.name());
        model.addAttribute("errorInvalid",errorInvalid);
        model.addAttribute("errorInUse",errorInUse);
        return "settings";
    }


    @Secured("ROLE_USER")
    @PostMapping("/messaging/settings/form")
    public String settings(Boolean dark, Boolean light, Principal principal, RedirectAttributes redirectAttributes){
        int exitValue = settingService.changeSettings(principal.getName(), dark, light);
        if(exitValue == -1){
            redirectAttributes.addAttribute("errorInvalid","exit1");
            return "redirect:/messaging/settings";
        }
        if(exitValue == -2){
            redirectAttributes.addAttribute("errorInUse","exit2");
            return "redirect:/messaging/settings";
        }
        return "redirect:/messaging/homescreen";
    }


}
