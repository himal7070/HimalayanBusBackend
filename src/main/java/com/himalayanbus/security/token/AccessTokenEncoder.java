package com.himalayanbus.security.token;

public interface AccessTokenEncoder {
    String encode(AccessToken accessToken);
}
