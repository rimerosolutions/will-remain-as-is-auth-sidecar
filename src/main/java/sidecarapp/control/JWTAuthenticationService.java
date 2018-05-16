package sidecarapp.control;

import java.util.Date;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Logger;

import com.nimbusds.jwt.JWTClaimsSet;

import sidecarapp.entity.UserImporter;

public final class JWTAuthenticationService {

    private static final Logger LOG = Logger.getLogger(JWTAuthenticationService.class.getName());

    private final String jwkProviderUrl;

    public JWTAuthenticationService(final String jwkProviderUrl) {
        Objects.requireNonNull(jwkProviderUrl, "The JWK provider URL is required.");

        this.jwkProviderUrl = jwkProviderUrl;
    }

    public UserImporter authenticate(final Supplier<String> tokenSource) throws Exception {
        JWTClaimsSet claims = extractClaims(tokenSource.get());

        return new JWTUserClaimsImporter(claims);
    }

    private JWTClaimsSet extractClaims(final String token) throws Exception {
        // TODO Real logic omitted for verifying the signature form the jwkProviderUrl and extracting claims
        // FIXME Hypothetically, update in next Sprint when there are clarifications around requirements and infrastructure issues :-)
        // What is going on, what is inside the JWT token? Who is the real issuer for user? I don't understand :-)
        LOG.info("Using jwkProviderUrl=" + jwkProviderUrl + " for checking JWT public keys");

        return new JWTClaimsSet.Builder()
            .subject("joe")
            .expirationTime(new Date(1300819380 * 1000l))
            .claim("http://example.com/is_root", true)
            .claim("name", "John Doe")
            .claim("id", "john.doe")
            .build();
    }

}
