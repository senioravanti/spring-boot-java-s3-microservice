package ru.manannikov.filesharingservice.gatewayserver.filter;

import
        org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    // Список не защищенных эндпоинтов
    public static final List<String> openApiEndpoints = List.of(
        "/auth/signup",
        "/auth/login",
        "/auth/refresh"
    );

    public Predicate<ServerHttpRequest> isSecured = (request) -> {
        // Возвращает true если ни один из элементов uri списка openApiEndpoints не соотв. указанному предикату
        return openApiEndpoints.stream().noneMatch(uri -> request.getURI().getPath().contains(uri));
    };
}
