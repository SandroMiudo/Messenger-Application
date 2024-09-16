package own.project.privatemessanger.app.service.verification;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import own.project.privatemessanger.app.service.MailProvider;
import own.project.privatemessanger.app.service.UserRepository;
import own.project.privatemessanger.app.service.UserVerificationRepository;
import own.project.privatemessanger.domain.verification.Verification;
import own.project.privatemessanger.dto.UserInfo;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

@Service
public class VerificationService {

    private UserRepository userRepository;
    private UserVerificationRepository userVerificationRepository;

    public VerificationService(UserRepository userRepository,
                               UserVerificationRepository userVerificationRepository) {
        this.userRepository = userRepository;
        this.userVerificationRepository = userVerificationRepository;
    }

    public boolean verifyUserRegistry(List<Integer> inputs, String code,String username){
        Verification verification = new Verification();
        boolean status = verification.registryVerification(inputs, code);
        if(status){
            List<UserInfo> userInfos = userRepository.searchByName(username);
            assert userInfos.get(0) != null;
            UserInfo user = userInfos.get(0);
            String s = userRepository.verifyUser(user.name());
            return true;
        }
        return false;
    }

    public String securingUser(HttpServletRequest request, Principal principal) {
        String clientIP = request.getRemoteAddr();
        List<String> iPs = userVerificationRepository.getIPs(principal.getName());

        Verification verification = new Verification();
        if(!verification.containsIP(iPs,clientIP)){
            // if ip is not in db than return the email of the user to send a mail
            return userRepository.getUser(principal.getName()).email();
        }
        return null;    //indicates that the ip is stored in db
    }
}
