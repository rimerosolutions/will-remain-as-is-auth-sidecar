package sidecarapp.boundary;

import java.util.Collection;
import java.util.Objects;

import sidecarapp.entity.UserExporter;

final class UserHTTPRequestHeadersExporter implements UserExporter {

    private final AddHeadersServletRequestWrapper request;

    UserHTTPRequestHeadersExporter(final AddHeadersServletRequestWrapper request) {
        Objects.requireNonNull(request, "The request is required.");

        this.request = request;
    }

    @Override
    public void exportId(final String id) {
        request.injectHeader("X-id", id);
    }

    @Override
    public void exportName(final String name) {
        request.injectHeader("X-name", name);
    }

    @Override
    public void exportGroups(final Collection<String> groups) {
        request.injectHeader("X-groups", String.join(",", groups));
    }

}
