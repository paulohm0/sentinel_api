package paulodev.sentinel_api.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import paulodev.sentinel_api.exception.CustomAuthenticationEntryPoint;
import paulodev.sentinel_api.modules.user.entity.UserRole;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityFilter securityFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.GET, "/user/list").hasAuthority(UserRole.ADMIN.getRole())
                        .anyRequest().authenticated())
                        .exceptionHandling(customizer -> customizer
                            .authenticationEntryPoint(customAuthenticationEntryPoint)
                            .accessDeniedHandler((request, response, accessDeniedException) -> {
                                handlerExceptionResolver.resolveException(request, response, null, accessDeniedException);
                            })
                        )
                        .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                        .build();
    }

    // Injeta o AuthenticationManager no AuthService
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Criptografar a senha antes de salvar no banco de dados
    @Bean
    public PasswordEncoder passwordEncoder() { // Criptografar a senha
        return new BCryptPasswordEncoder();
    }
}
