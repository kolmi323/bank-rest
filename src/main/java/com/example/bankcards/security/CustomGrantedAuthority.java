package com.example.bankcards.security;

import com.example.bankcards.util.UserRole;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomGrantedAuthority implements GrantedAuthority {
    private final String prefix = "ROLE_";
    private final UserRole userRole;

    @Override
    public String getAuthority() {
        return prefix + userRole.toString();
    }
}
