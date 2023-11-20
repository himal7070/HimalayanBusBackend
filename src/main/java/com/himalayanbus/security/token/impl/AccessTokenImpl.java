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
    private final Integer entityId;
    private final Set<String> roles;

    public AccessTokenImpl(String subject, Integer entityId, Collection<String> roles) {
        this.subject = subject;
        this.entityId = Math.toIntExact(entityId);
        this.roles = roles != null ? Set.copyOf(roles) : Collections.emptySet();
    }

    @Override
    public boolean hasRole(String roleName) {
        return this.roles.contains(roleName);
    }
}
