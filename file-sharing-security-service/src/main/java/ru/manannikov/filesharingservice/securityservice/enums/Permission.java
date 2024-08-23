package ru.manannikov.filesharingservice.securityservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public enum Permission implements GrantedAuthority {
    MANAGE_OBJECTS,
    MANAGE_BUCKETS,
    MANAGE_USERS
    ;

    @Override
    public String getAuthority() {
        return toString();
    }
}
