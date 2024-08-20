package ru.manannikov.filesharingservice.securityservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public enum Permission implements GrantedAuthority {
    ADMIN_CREATE("admin:create"),
    ADMIN_READ("admin:read"),
    ADMIN_DELETE("admin:delete"),
    ADMIN_UPDATE("admin:update")
    ;

    private final String permission;


    @Override
    public String getAuthority() {
        return permission;
    }
}
