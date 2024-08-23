package ru.manannikov.filesharingservice.securityservice.config;

//import com.nimbusds.jose.jwk.source.ImmutableSecret;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.security.oauth2.jwt.JwtEncoder;
//import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
//import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
//import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import ru.manannikov.filesharingservice.securityservice.services.UserService;

//import static org.springframework.http.HttpMethod.*;
//
//import static ru.manannikov.filesharingservice.securityservice.enums.Role.*;
//import static ru.manannikov.filesharingservice.securityservice.enums.Permission.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtProperties jwtProperties;
    /**
     * WHILE_LIST_URL -> список роутов, доступных любым пользователям.
     */
    private static final String[] WHITE_LIST_URL = {
        "/v*/auth/**", "/swagger-ui/**", "/configuration/ui", "/actuator/health",
        "/configuration/security", "swagger-resources", "/swagger-resources/**", "/v3/api-docs", "/v3/api-docs/**", "/swagger-ui.html"
    };
    /**
     * Выбираем шифровщик паролей.
     * @return бин для шифрования паролей перед их сохранением в БД.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    /**
     * <p>Конфигурация аутентификации.<p/>
     * <p>Интерфейс AuthenticationManager используется в общем случае, так как в приложении может быть зарегистрировано быть несколько AuthenticationProvider, конструктор его реализации -> ProviderManager принимает множество экземпляров реализаций AuthenticationProvider.<p/>
     * <p>DaoAuthenticationProvider используется т.к. пользователи хранятся в БД<p/>
     */
    @Bean
    public AuthenticationProvider authenticationProvider(final UserService userService) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userService);

        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Конфигурация авторизации
     * Бин, создает и настраивает цепочку фильтров безопасности.
     * @param http порождающий бин, соотв. паттерну builder, предназначенный для пошаговой настройки цепочки фильтров SecurityFilterChain;
     * @return настроенную цепочку фильтров -- экземпляр SecurityFilterChain;
     * @throws Exception -- общее исключение.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Защиту от csrf -> cross-site resource forgery = межсайтовой подделки запросов на этапе разработки и тестирования можно отключить;
            .csrf(AbstractHttpConfigurer::disable)
            // Я не делаю запросы к другим доменам
            .cors(Customizer.withDefaults())
        ;
        // Настройка авторизации
        http
            .authorizeHttpRequests((request) -> request

                .requestMatchers(WHITE_LIST_URL).permitAll()

                // Выбросит IllegalArgumentException если у пользователя нет ни одной из перечисленных ролей.

//                .requestMatchers("/users/**").hasRole(ADMIN.name())
//
//                .requestMatchers(POST, "/users/", "/users")
//                    .hasAuthority(ADMIN_CREATE.getAuthority())
//
//                .requestMatchers(GET, "/users/", "/users")
//                    .hasAuthority(ADMIN_READ.getAuthority())
//
//                .requestMatchers(PUT, "/users/**")
//                    .hasAuthority(ADMIN_UPDATE.getAuthority())
//
//                .requestMatchers(DELETE, "/users/**")
//                    .hasAuthority(ADMIN_DELETE.getAuthority())
            )
        ;
        // Настройка oauth2 resource server :: под сервером ресурсов понимают: API, к которому пользователь хочет получить доступ. Сервер, на котором хранятся защищенные ресурсы;
        // На сервере надо настроить дешифратор подписи полученного токена.
        // Используем jwt чтобы не вводить пароль при каждом запросе на сервер.
//        http
//            .oauth2ResourceServer((oauth2ResourceServer) -> oauth2ResourceServer
//                .jwt((jwt) -> jwt
//                    .decoder(jwtDecoder())
//                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
//                )
//            )
//            ;
        http
            .anonymous(AbstractHttpConfigurer::disable)
            .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        ;

        return http.build();
    }
    /**
     * JWT decoder -> дешифратор
     */
//    @Bean
//    public JwtDecoder jwtDecoder() {
//        // Требует экземпляр реализации SecretKey, например SecretKeySpec
//        return NimbusJwtDecoder
//            .withSecretKey(
//                jwtProperties.getKey()
//            )
//            .macAlgorithm(
//                MacAlgorithm.from(jwtProperties.getAlgorithm().getName())
//            )
//            .build();
//    }
    /**
     * JWT encoder -> шифратор
     */
//    @Bean
//    public JwtEncoder jwtEncoder() {
//        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtProperties.getKey()));
//    }
    /**
     * Настраиваем преобразование строки с полномочиями пользователя (утверждения "authorities" (из claim set токена), которое представляет собой строку из названий полномочий, разделенных пробелами) в коллекцию экз. реализации GrantedAuthority
     * Для преобразования используются методы convert(Jwt jwt), getAuthorities(Jwt jwt), getAuthoritiesClaimName(jwt), которые используют с-ва authorityPrefix, authoritiesClaimDelimiter и authoritiesClaimName -> соответственно. Значения перечисленных с-в должны соответствовать значениям, используемым при создании токена в классе JwtService.
     */
//    public JwtAuthenticationConverter jwtAuthenticationConverter() {
//        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
//
//        authoritiesConverter.setAuthoritiesClaimName("authorities");
//        authoritiesConverter.setAuthorityPrefix("");
//        // На всякий случай
//        authoritiesConverter.setAuthoritiesClaimDelimiter(" ");
//
//        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
//        jwtConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
//
//        return jwtConverter;
//    }

}
