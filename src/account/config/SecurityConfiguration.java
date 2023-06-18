package account.config;

import account.service.AuditorServiceImpl;
import account.service.util_services.LoginAttemptService;
import account.service.util_services.UserDetailsServiceImpl;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

import java.io.IOException;


@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfiguration {
    private static final String REGISTER_PATH = "/api/auth/signup";
    private static final String PASS_CHANGE_PATH = "/api/auth/changepass";
    private static final String PAYMENT_PATH = "/api/empl/payment**";
    private static final String ADD_PAYMENT_PATH = "/api/acct/payments";
    private static final String UPDATE_PAYMENT_PATH = "/api/acct/payments";
    private static final String ADMINISTRATIVE_PATH = "/api/admin/**";
    private static final String AUDITOR_PATH = "/api/security/**";
    private final PasswordEncoder encoder;
    private final UserDetailsServiceImpl userDetailsService;
    AuditorServiceImpl auditorService;
    LoginAttemptService loginAttemptService;

    @Autowired
    public SecurityConfiguration(PasswordEncoder encoder, UserDetailsServiceImpl userDetailsService,
                                 AuditorServiceImpl auditorService, LoginAttemptService loginAttemptService) {
        this.encoder = encoder;
        this.userDetailsService = userDetailsService;
        this.auditorService = auditorService;
        this.loginAttemptService = loginAttemptService;
    }
//.and().httpBasic()

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.httpBasic()
                .authenticationEntryPoint(endpointAuthenticationEntryPoint()) // Handle auth error
                .and()
                .csrf().disable().headers().frameOptions().disable() // for Postman, the H2 console

                .and()
                .authorizeRequests() // manage access
                .requestMatchers(HttpMethod.POST, REGISTER_PATH).permitAll()
                .requestMatchers("/actuator/shutdown").permitAll() // needs to run test
                .requestMatchers(HttpMethod.POST, PASS_CHANGE_PATH).hasAnyRole("ADMINISTRATOR", "USER", "ACCOUNTANT")
                .requestMatchers(HttpMethod.GET, PAYMENT_PATH).hasAnyRole("USER", "ACCOUNTANT")
                .requestMatchers(HttpMethod.POST, ADD_PAYMENT_PATH).hasRole("ACCOUNTANT")
                .requestMatchers(HttpMethod.PUT, UPDATE_PAYMENT_PATH).hasRole("ACCOUNTANT")
                .requestMatchers(ADMINISTRATIVE_PATH).hasRole("ADMINISTRATOR")
                .requestMatchers(AUDITOR_PATH).hasRole("AUDITOR")
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler()).and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // no session


        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http)
            throws Exception {


        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(encoder)
                .and()
                .build();
    }

    @Bean
    public AuthenticationEntryPoint endpointAuthenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }

    private static class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
        private final RequestMatcher forbiddenEntryPoint = new AntPathRequestMatcher(PAYMENT_PATH);

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
            if (forbiddenEntryPoint.matches(request)) {
                handleLockedAccount(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            }
        }

        private void handleLockedAccount(HttpServletRequest request, HttpServletResponse response) throws IOException {
            String errorMessage = "User account is locked";
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, errorMessage);
        }
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {

        return new CustomAccessDeniedHandler();
    }

}
