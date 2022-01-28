package fr.lajusticiarugliano.jeecardgames.security;

import com.auth0.jwt.algorithms.Algorithm;

import java.util.Arrays;

public class SecurityUtil {
    private static final String SECRET_KEY = "super_secret_lajusticia_rugliano_distruggere";

    public static final Algorithm ALGORITHM = Algorithm.HMAC256(SecurityUtil.SECRET_KEY.getBytes());

    public static final String[] UNRESTRICTED_PATHS = {
            "/api/authenticate",
            "/api/users/save",
            "/api/users/refreshtoken",
    };

    public static final boolean pathIsUnrestricted(String path) {
        return Arrays.stream(UNRESTRICTED_PATHS).anyMatch(path::equals);
    }

    private SecurityUtil() {}
}
