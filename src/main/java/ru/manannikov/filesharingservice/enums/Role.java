package ru.manannikov.filesharingservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.manannikov.filesharingservice.enums.Permission.*;

@RequiredArgsConstructor
@Getter
public enum Role {
    USER(Collections.emptySet()),
    ADMIN(Set.of(
        ADMIN_CREATE,
        ADMIN_READ,
        ADMIN_UPDATE,
        ADMIN_DELETE
    ))
    ;

    private final Set<Permission> permissions;

    @Override
    public String toString() {
        return "ROLE_" + super.toString();
    }
    /**
     * Все привилегии пользователя должны быть экземплярами реализации GrantedAuthority. В противном случае бины фреймворка не смогут установить привилегии пользователя. Роли (roles) и привилегии (authorities) в Spring Security по сути одно и тоже, только роль идет без префикса ROLE_, а authority должна состоять из названия роли и префикса ROLE_.
     * @return Множество экз. SimpleGrantedAuthority
     */
    public Set<SimpleGrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = permissions.stream()
            .map(permission ->
                    new SimpleGrantedAuthority(permission.getAuthority())
            )
            .collect(Collectors.toSet());
        authorities.add(
            new SimpleGrantedAuthority(this.toString())
        );
        return authorities;
    }
}
