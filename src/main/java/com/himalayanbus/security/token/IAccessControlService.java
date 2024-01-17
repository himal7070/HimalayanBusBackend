package com.himalayanbus.security.token;

public interface IAccessControlService {
    void checkUserAccess(AccessToken accessToken, String userEmail);

    AccessToken extractAccessToken(String authorizationHeader);
}
