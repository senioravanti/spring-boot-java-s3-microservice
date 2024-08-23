package ru.manannikov.filesharingservice.gatewayserver.filter;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.manannikov.filesharingservice.gatewayserver.util.JwtUtil;

import org.springframework.http.HttpHeaders;

import java.util.List;

/**
 * Класс Должен реализовывать именно интерфейс AbstractGatewayFilterFactory, а не GatewayFilter, чтобы создать свой фильтр и иметь возможность применять его в .yml файле конфигурации
 * В этом классе выполняется авторизация, проверка прав пользователя, прошедшего аутентификацию на доступ к ресурсам приложения
 */
@Component
@Slf4j
public class AuthorizationFilter extends AbstractGatewayFilterFactory<AuthorizationFilter.Config> {

    public AuthorizationFilter(JwtUtil util, RouteValidator validator) {
        super(Config.class);
        this.util = util;
        this.validator = validator;
    }

    private final JwtUtil util;
    private final RouteValidator validator;

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (validator.isSecured.test(exchange.getRequest())) {
                HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
                if (!requestHeaders.containsKey(HttpHeaders.AUTHORIZATION)) {
                    log.info("Отсутствует заголовок Authorization.\nheaders: {}", requestHeaders);
                    return onError(exchange, HttpStatus.UNAUTHORIZED);
                }

                ServerHttpRequest request = exchange.getRequest();
                String authHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).getFirst();

                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    log.info("В заголовке Authorization отсутствует токен доступа");
                    return onError(exchange, HttpStatus.UNAUTHORIZED);
                }

                String token = authHeader.substring(7);
                String authorities;
                try {
                   authorities = util.getAuthorities(token);
                } catch (Exception e) {
                    log.info("Передан недопустимый или просроченный токен доступа");
                    return onError(exchange, HttpStatus.UNAUTHORIZED);
                }

                final String role = config.getRole();
                if (!authorities.contains(role)) {
                    log.debug("role: {}\nauthorities: {}", role, authorities);
                    return onError(exchange, HttpStatus.FORBIDDEN);
                }

                if (role.equals("MANAGE_OBJECTS")) {
                    String path = request.getURI().getRawPath();
                    String username = util.getUsername(token);

                    if (StringUtils.countOccurrencesOf(path, "/") == 2 && !path.contains(username)) {
                        log.debug("(FORBIDDEN) path: {}\nusername: {}", role, username);
                        return onError(exchange, HttpStatus.FORBIDDEN);
                    }

                    request = request.mutate().path("/" + username + path).build();
                    exchange = exchange.mutate().request(request).build();
                    log.debug("(OK) path: {}", request.getURI().getRawPath());
                }

            }
            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return List.of("role");
    }

    @Getter @Setter
    public static class Config {
        private String role;
    }

}
