package own.project.privatemessanger.spring;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import own.project.privatemessanger.app.service.LoginAttemptService;

import javax.servlet.http.HttpServletRequest;

@Component
public class AuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private HttpServletRequest request;
    private LoginAttemptService loginAttemptService;

    public AuthenticationSuccessEventListener(HttpServletRequest request, LoginAttemptService loginAttemptService) {
        this.request = request;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            loginAttemptService.loginSucceeded(request.getRemoteAddr());
        } else {
            loginAttemptService.loginSucceeded(xfHeader.split(",")[0]);
        }
    }
}
