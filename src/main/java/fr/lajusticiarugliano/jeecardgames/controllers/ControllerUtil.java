package fr.lajusticiarugliano.jeecardgames.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import fr.lajusticiarugliano.jeecardgames.security.SecurityUtil;

public class ControllerUtil {

    public static final String getMailFromAuthorizationHeader(String header) {
        String token = header.substring(7);
        Algorithm algorithm = SecurityUtil.ALGORITHM;
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        String mail = decodedJWT.getSubject();

        return mail;
    }

    private ControllerUtil() {}
}
