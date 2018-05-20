package sidecarapp.boundary;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

final class AddHeadersServletRequestWrapper extends HttpServletRequestWrapper {

    private final Map<String, String> headersByName = new HashMap<>();

    public AddHeadersServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    void injectHeader(String name, String value) {
        headersByName.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        String header = super.getHeader(name);

        return (header != null) ? header : headersByName.get(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        List<String> names = Collections.list(super.getHeaderNames());
        names.addAll(headersByName.keySet());

        return Collections.enumeration(names);
    }
}
