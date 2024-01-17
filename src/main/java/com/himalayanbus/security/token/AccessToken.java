package com.himalayanbus.security.token;

import java.util.Set;

public interface AccessToken {
    String getSubject();

    Long getUserID();

    Set<String> getRoles();

}
