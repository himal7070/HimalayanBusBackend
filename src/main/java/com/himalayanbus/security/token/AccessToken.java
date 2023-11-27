package com.himalayanbus.security.token;

import java.util.Set;

public interface AccessToken {
    String getSubject();

    Long getPassengerId();

    Set<String> getRoles();

    boolean hasRole(String roleName);
}
