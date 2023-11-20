package com.himalayanbus.security.token;

public interface AccessTokenDecoder {
    AccessToken decode(String accessTokenEncoded);
}
