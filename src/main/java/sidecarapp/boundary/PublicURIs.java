package sidecarapp.boundary;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

final class PublicURIs {

    private final Collection<String> regexes;

    private PublicURIs(final Collection<String> regexes) {
        Objects.requireNonNull(regexes, "The regexes are required.");
        this.regexes = new HashSet<>(regexes);
    }

    static PublicURIs from(final Collection<String> regexes) {
        return new PublicURIs(regexes);
    }

    public boolean isPublic(final String requestURI) {
        Objects.requireNonNull(requestURI, "The request URI is required.");

        for (String regex : regexes) {
            if (requestURI.matches(regex)) {
                return true;
            }
        }

        return false;
    }

}
