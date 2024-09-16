package own.project.privatemessanger.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import own.project.privatemessanger.app.service.verification.VerificationService;

import java.util.List;

@Controller
public class VerificationController {


    private VerificationService verificationService;

    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping("/registry/user/verification")
    public String verification(int a, int b, int c, int d, int e, int f, String verificationcode,
                               String username, Model model, RedirectAttributes redirectAttributes){
        List<Integer> inputValues = List.of(a,b,c,d,e,f);
        boolean verified = verificationService.verifyUserRegistry(inputValues, verificationcode, username);
        if(verified){
            redirectAttributes.addAttribute("verify","success");
            return "redirect:/login";
        }
        model.addAttribute("errorVerify",true);
        model.addAttribute("generatedNumber",verificationcode);
        model.addAttribute("username",username);
        return "verification";
    }
}
