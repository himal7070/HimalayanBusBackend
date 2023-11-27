package com.himalayanbus.security.token.impl;

import com.himalayanbus.security.token.AccessToken;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@EqualsAndHashCode
@Getter
public class AccessTokenImpl implements AccessToken {
    private final String subject;
    private final Long passengerId;
    private final Set<String> roles;

    public AccessTokenImpl(String subject, Long passengerId, Collection<String> roles) {
        this.subject = subject;
        this.passengerId = passengerId;
        this.roles = roles != null ? Set.copyOf(roles) : Collections.emptySet();
    }

    @Override
    public boolean hasRole(String roleName) {
        return this.roles.contains(roleName);
    }


}
