package sidecarapp.boundary;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OncePerRequestFilter implements Filter {

    private static final String FILTERED_ATTR_SUFFIX = ".FILTERED";

    @Override
    public void init(final FilterConfig filterConfig) {}

    @Override
    public final void doFilter(final ServletRequest servletRequest,
                               final ServletResponse servletResponse,
                               final FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        if (requestNotAlreadyFiltered(httpServletRequest)) {
            applyDoFilter(httpServletRequest, httpServletResponse, filterChain);
            markRequestAsFiltered(httpServletRequest);
        }
    }

    @Override
    public void destroy() {}

    protected void applyDoFilter(final HttpServletRequest request,
                                 final HttpServletResponse response,
                                 final FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
    }

    private boolean requestNotAlreadyFiltered(final HttpServletRequest request) {
        return request.getAttribute(filteredRequestAttrName(request)) == null;
    }

    private void markRequestAsFiltered(final HttpServletRequest request) {
        request.setAttribute(filteredRequestAttrName(request), Boolean.TRUE);
    }

    private String filteredRequestAttrName(final HttpServletRequest request) {
        return request.getRequestURI() + FILTERED_ATTR_SUFFIX;
    }

}
