package sidecarapp.boundary;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sidecarapp.control.JWTAuthenticationService;
import sidecarapp.control.JWTHTTPHeadersTokenSource;
import sidecarapp.entity.User;
import sidecarapp.entity.UserImporter;

public final class AuthenticationFilter extends OncePerRequestFilter {

    private JWTAuthenticationService authService;
    private PublicURIs publicURIs;

    private static final Logger LOG = Logger.getLogger(AuthenticationFilter.class.getName());

    @Override
    public void init(final FilterConfig filterConfig) {
        publicURIs = PublicURIs.from(Arrays.asList(filterConfig.getInitParameter("PUBLIC_URL_REGEXES").split(",")));
        authService = new JWTAuthenticationService(filterConfig.getInitParameter("JWKS_URL"));
    }

    @Override
    protected void applyDoFilter(final HttpServletRequest request,
                                 final HttpServletResponse response,
                                 final FilterChain chain) throws IOException, ServletException {
        LOG.info("Filtering request to =" + request.getRequestURI());
        User user = User.ANONYMOUS;

        AddHeadersServletRequestWrapper requestWrapper = new AddHeadersServletRequestWrapper(request);

        if (publicURIs.isProtected(request.getRequestURI())) {
            try {
                UserImporter importer = authService.authenticate(new JWTHTTPHeadersTokenSource(requestWrapper));
                user = User.from(importer);
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Cannot authenticate user.", e);
                markResponseAsUnauthorized(response);
                return;
            }

            user.export(new UserHTTPRequestHeadersExporter(requestWrapper));

            LOG.info("Logged in as " + user + ".");
        }

        chain.doFilter(requestWrapper, response);
    }

    private void markResponseAsUnauthorized(final HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

}
