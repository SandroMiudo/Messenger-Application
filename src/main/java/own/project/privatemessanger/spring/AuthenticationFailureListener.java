package own.project.privatemessanger.spring;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;
import own.project.privatemessanger.app.service.LoginAttemptService;
import javax.servlet.http.HttpServletRequest;

@Component
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    private HttpServletRequest request;

    private LoginAttemptService loginAttemptService;

    public AuthenticationFailureListener(HttpServletRequest request, LoginAttemptService loginAttemptService) {
        this.request = request;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            loginAttemptService.loginFailed(request.getRemoteAddr());
        } else {
            loginAttemptService.loginFailed(xfHeader.split(",")[0]);
        }
    }
}
