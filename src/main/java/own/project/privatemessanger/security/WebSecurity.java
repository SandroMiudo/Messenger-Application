package own.project.privatemessanger.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    PasswordEncoder passwordEncoder;


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/css/*", "/js/*", "/error", "/stats","/img/*","/registry"
                ,"/registry/user","/registry/user/changeMail","/registry/user/verification").permitAll()
                .and().authorizeRequests().anyRequest().authenticated().and().formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/messaging/homescreen",true).failureUrl("/login?error=true").permitAll()
                .and().logout().clearAuthentication(true);
    }
}
