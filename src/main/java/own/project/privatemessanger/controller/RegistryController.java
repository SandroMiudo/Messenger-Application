package own.project.privatemessanger.controller;

import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import own.project.privatemessanger.app.service.LoginAttemptService;
import own.project.privatemessanger.app.service.MailProvider;
import own.project.privatemessanger.app.service.registry.RegistryService;
import own.project.privatemessanger.dto.UserInfo;
import own.project.privatemessanger.spring.NumberGenerator;

import java.nio.file.Path;

@Controller
public class RegistryController {

    private RegistryService registryService;
    private LoginAttemptService loginAttemptService;
    private MailProvider mailProvider;
    private UserDetailsService userDetailsService;

    public RegistryController(RegistryService registryService, LoginAttemptService loginAttemptService,
                              MailProvider mailProvider, UserDetailsService userDetailsService){
        this.registryService = registryService;
        this.loginAttemptService = loginAttemptService;
        this.mailProvider = mailProvider;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/registry")
    public String index(Model model, String errorEmail, String errorPassword, String errorUsername){
        validate(errorEmail,model);
        validate(errorPassword,model);
        validate(errorUsername,model);
        return "registry";
    }

    @PostMapping("/registry/user")
    public String userReg(String username, String password, String email, RedirectAttributes redirectAttributes,Model model){
        UserInfo user = registryService.createUser(username, password, email);
        if(!user.validateUser()){
            if(user.email() == null){
                redirectAttributes.addAttribute("errorEmail","exit1");
            }
            if(user.password() == null){
                redirectAttributes.addAttribute("errorPassword","exit2");
            }
            if(user.name().equals("None")){
                redirectAttributes.addAttribute("errorUsername","exit3");
            }
            return "redirect:/registry";
        }
        mailing(model,user);
        return "verification";
    }

    @PostMapping("/registry/user/changeMail")
    public String changeMail(String email,String username,Model model){
        UserInfo user = registryService.changeEmail(email, username);
        if(user != null){
            mailing(model,user);
            return "verification";
        }
        return "redirect:/login";
    }

    private void mailing(Model model, UserInfo user){
        String randomNumber_asString = NumberGenerator.generateRandomNumber();
        Path path = Path.of("src/main/resources/templates/mail.html");
        mailProvider.setPath(path);
        mailProvider.sendEmail(user.email(),user.name(),randomNumber_asString);
        model.addAttribute("generatedNumber",randomNumber_asString);
        model.addAttribute("username",user.name());
        model.addAttribute("email",user.email());
    }

    private void validate(String s, Model model){
        if(s == null){
            return;
        }
        if(s.equals("exit1")){
            model.addAttribute("errorEmail","true");
        }
        if(s.equals("exit2")){
            model.addAttribute("errorPassword","true");
        }
        if(s.equals("exit3")){
            model.addAttribute("errorUsername","true");
        }
    }
}
