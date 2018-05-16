package sidecarapp.control;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import com.nimbusds.jwt.JWTClaimsSet;

import sidecarapp.entity.UserImporter;

final class JWTUserClaimsImporter implements UserImporter {

    private final String id;
    private final String username;
    private final Collection<String> groups;

    JWTUserClaimsImporter(final JWTClaimsSet claims) throws ParseException {
        Objects.requireNonNull(claims, "The claims are required.");

        this.id = claims.getStringClaim(JWTTokenFields.ID);
        this.username = claims.getStringClaim(JWTTokenFields.NAME);
        this.groups = Arrays.asList("USER"); // TODO hard-coded, this is a bug or WIP
    }

    @Override
    public String provideId() {
        return id;
    }

    @Override
    public String provideName() {
        return username;
    }

    @Override
    public Collection<String> provideGroups() {
        return groups;
    }

}
