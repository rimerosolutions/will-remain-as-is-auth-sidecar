package sidecarapp.control;

import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;

public final class JWTHTTPHeadersTokenSource implements Supplier<String> {

    private final String token;

    public JWTHTTPHeadersTokenSource(final HttpServletRequest request) {
        final String authHeaderValue = request.getHeader("Authorization");

        if (authHeaderValue == null || authHeaderValue.trim().length() == 0) {
            throw new IllegalArgumentException("The access token is required.");
        }

        final String[] authHeaderData = authHeaderValue.split("Bearer ");

        if (authHeaderData.length != 2) {
            throw new IllegalArgumentException("The access token is invalid=" + authHeaderValue + ".");
        }

        this.token = authHeaderData[1];
    }

    @Override
    public String get() {
        return token;
    }

}
