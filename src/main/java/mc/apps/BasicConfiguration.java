package mc.apps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class BasicConfiguration extends WebSecurityConfigurerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(BasicConfiguration.class);

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        auth
                .inMemoryAuthentication()
                .withUser("mc")
                .password(encoder.encode("123"))
                .roles("USER")
                .and()
                .withUser("mc2")
                .password(encoder.encode("admin"))
                .roles("USER", "ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/css/**","/webjars/**","/images/**", "/").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                // login
                .formLogin()
                .loginProcessingUrl("/login")
                .usernameParameter("login")
                .passwordParameter("password")
                // logout
                .and()
                .logout()
                .logoutUrl("/logout") //the URL on which the clients should post if they want to logout
                .logoutSuccessHandler(this::logoutSuccessHandler)
                .invalidateHttpSession(true);

                // .httpBasic();
    }

    private void logoutSuccessHandler(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Authentication authentication) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        logger.info("Bye!");
        response.sendRedirect("/");
    }
}
