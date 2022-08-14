package antifraud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(getEncoder());
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().and()
                .authorizeRequests()
                .mvcMatchers("/api/auth/role").hasRole("ADMINISTRATOR")
                .mvcMatchers("/api/auth/access").hasRole("ADMINISTRATOR")
                .mvcMatchers("/api/antifraud/transaction").hasRole("MERCHANT")
                .mvcMatchers("/api/auth/user/*").hasRole("ADMINISTRATOR")
                .mvcMatchers("/api/auth/list").hasAnyRole("ADMINISTRATOR", "SUPPORT")
                .mvcMatchers("api/antifraud/suspicious-ip").hasRole("SUPPORT")
                .mvcMatchers("api/antifraud/stolencard").hasRole("SUPPORT")
                .mvcMatchers("/api/auth/user").permitAll()
                .mvcMatchers("/actuator/shutdown").permitAll()
                .and().csrf().disable().headers().frameOptions().disable()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    public PasswordEncoder getEncoder () {
        return new BCryptPasswordEncoder();
    }
}
