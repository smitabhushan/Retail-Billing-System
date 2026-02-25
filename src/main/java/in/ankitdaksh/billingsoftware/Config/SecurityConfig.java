package in.ankitdaksh.billingsoftware.Config;

import in.ankitdaksh.billingsoftware.filter.JwtRequestFilter;
import in.ankitdaksh.billingsoftware.service.impl.AppUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AppUserDetailsService appUserDetailsService;
    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/encode","/uploads/**").permitAll()
                        .requestMatchers("/categories", "/items","/orders","/payments","/dashboard").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter(corsConfigurationSource());
    }
    //    यह method तुम्हारे Spring Boot backend को React frontend (localhost:5173)
    //    से आने वाले cross‑origin requests accept करने के लिए configure कर रहा है
    private UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization","Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}




/**
 private UrlBasedCorsConfigurationSource corsConfigurationSource() {
 CorsConfiguration config = new CorsConfiguration();

 // 1. Allowed Origins
 config.setAllowedOrigins(List.of("http://localhost:5173"));
 // मतलब सिर्फ इस origin (तुम्हारा React/Vite frontend जो 5173 port पर चल रहा है) से आने वाले requests को allow करेगा।

 // 2. Allowed Methods
 config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIOND"));
 // मतलब backend इन HTTP methods को accept करेगा। (Note: "OPTIOND" typo है, सही "OPTIONS" होना चाहिए)

 // 3. Allowed Headers
 config.setAllowedHeaders(List.of("Authorization","Content-Type"));
 // मतलब request में ये headers आने की अनुमति है।

 // 4. Allow Credentials
 config.setAllowCredentials(true);
 // मतलब cookies, authorization headers, या TLS client certificates cross‑origin requests में भेजे जा सकते हैं।

 // 5. Register Configuration
 UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
 source.registerCorsConfiguration("/**", config);
 // मतलब ये CORS rules पूरे application के सभी endpoints (`/**`) पर लागू होंगे।

 return source;
 }
 */
