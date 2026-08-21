package net.engineeringdigest.journalApp.SecurityConfig;
import net.engineeringdigest.journalApp.Utils.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig{

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtAuthFilter jwtAuthFilter;
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(request -> request
                        .antMatchers("/login", "/signup", "/clearUsers", "/greetings/{cityName}").permitAll()
                        .antMatchers("/admin/**", "/audio/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//        return http.authorizeHttpRequests(request -> request
//                        .antMatchers("/api/signup", "/api/login", "/api/clearUsers", "/api/greetings/{cityName}").permitAll()
//                        .antMatchers("/admin/**", "/audio/**").hasRole("ADMIN")
//                        .anyRequest().authenticated())
////                .httpBasic(Customizer.withDefaults())                                         // for normal login
//                .csrf(AbstractHttpConfigurer::disable)
//                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)     // for jwt based login
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))   // ADD THIS
//                .build();
//    }

    // below is old approach newer one the DaoAuth and AuthManager approach
//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//
//        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
//    }
}

/*

        return http.authorizeHttpRequests(request -> request
                        .requestMatchers("/api/addUser").permitAll()
                        .requestMatchers("/journal/**", "/user/**").authenticated()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .build();

        .requestMatchers("/api/addUser")        // exact match only
        .requestMatchers("/api/admin/**")       // matches /api/admin/ANYTHING (any depth)
        .requestMatchers("/api/journal/*")      // matches /api/journal/ONE-segment only
                        .requestMatchers("/api/reports/**").hasAnyRole("ADMIN", "MANAGER")

NOTE: antMatchers() vs requestMatchers()
------------------------------------------

Both are used inside authorizeHttpRequests() / authorizeRequests() to define
which URL patterns need what kind of access (permitAll, authenticated,
hasRole, etc). They do the SAME job -- the difference is which Spring
Security VERSION supports which method.

antMatchers("/api/addUser")
  - Used in Spring Security 5.x (comes with Spring Boot 2.x, e.g. 2.7.16)
  - Takes plain String URL patterns directly -- always worked without issues
  - DEPRECATED in Spring Security 6.x, and REMOVED entirely in newer versions
  - This is what I'm using now, since I'm intentionally on Spring Boot 2.7.16

requestMatchers("/api/addUser")
  - The NEWER replacement, standard in Spring Security 6.x (comes with
    Spring Boot 3.x)
  - Same String... syntax on the surface, BUT it has overloaded versions:
      requestMatchers(String... patterns)          <- needs Spring MVC detected
      requestMatchers(RequestMatcher... matchers)  <- generic matcher objects
  - On Spring Boot 2.7.16 (Spring Security 5.x), the String overload isn't
    reliably resolved -- caused this error for me:

    "method requestMatchers cannot be applied to given types;
     required: RequestMatcher[]
     found: String
     reason: varargs mismatch"

  - Fix at the time: switched every requestMatchers(...) -> antMatchers(...)
    and it compiled immediately.

TAKEAWAY / FUTURE MIGRATION NOTE:
When I eventually upgrade this project from Spring Boot 2.7.x -> 3.x
(as planned, to practice migration), antMatchers() will need to be
swapped BACK to requestMatchers() -- because antMatchers() is removed
in Spring Security 6.x. So:

  Spring Boot 2.x / Spring Security 5.x  -->  use antMatchers()
  Spring Boot 3.x / Spring Security 6.x  -->  use requestMatchers()

Same job, different method name depending on framework version -- pick
based on what pom.xml's spring-boot-starter-parent version actually is.

 */