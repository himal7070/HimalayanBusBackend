package com.himalayanbus.security.token.impl;

import com.himalayanbus.security.token.AccessToken;
import com.himalayanbus.security.token.AccessTokenDecoder;
import com.himalayanbus.security.token.IAccessControlService;
import com.himalayanbus.security.token.exception.InvalidAccessTokenException;
import org.springframework.stereotype.Service;

@Service
public class AccessControlService implements IAccessControlService {

    private final AccessTokenDecoder accessTokenDecoder;

    public AccessControlService(AccessTokenDecoder accessTokenDecoder) {
        this.accessTokenDecoder = accessTokenDecoder;
    }

    @Override
    public void checkUserAccess(AccessToken accessToken, String userIdOrEmail) {
        boolean isAdmin = accessToken.getRoles().contains("ADMIN");

        if (!isAdmin && !accessToken.getSubject().equals(userIdOrEmail) && !accessToken.getUserID().toString().equals(userIdOrEmail)) {
            throw new InvalidAccessTokenException("Access denied. You do not have permission.");
        }
    }



    @Override
    public AccessToken extractAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String tokenValue = authorizationHeader.substring(7);
            return accessTokenDecoder.decode(tokenValue);
        }
        return null;
    }



}
