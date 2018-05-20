package sidecarapp.boundary;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import com.nimbusds.jwt.proc.BadJWTException;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Tested;
import sidecarapp.control.JWTAuthenticationService;
import sidecarapp.entity.UserImporter;
import sidecarapp.test.TestServletUtils;

public class AuthenticationFilterTest {

    @Tested
    AuthenticationFilter authenticationFilter;

    @Mocked
    JWTAuthenticationService authenticationService;

    private Map<String, String> initParametersByName;
    private FilterConfig filterConfig;
    private FilterChain filterChain;
    private HttpServletRequest servletRequest;
    private HttpServletResponse servletResponse;

    @Before
    public void setup() {
        initParametersByName = new HashMap<>();
        filterChain = TestServletUtils.newFilterChain();
        servletResponse = TestServletUtils.newServletResponse();
    }

    @Test
    public void Given_that_the_uri_is_public_then_access_is_granted() throws IOException, ServletException {
        initParametersByName.put("PUBLIC_URL_REGEXES", "/uri");
        servletRequest = TestServletUtils.newServletRequest("/uri");
        filterConfig = TestServletUtils.newFilterConfig(initParametersByName);
        authenticationFilter.init(filterConfig);
        authenticationFilter.doFilter(servletRequest, servletResponse, filterChain);

        assertThat("Access is granted", servletResponse.getStatus(), equalTo(HttpServletResponse.SC_OK));
    }

    @Test
    public void Given_that_the_uri_is_private_without_a_bearer_token_then_the_request_is_unauthorized() throws IOException, ServletException {
        initParametersByName.put("PUBLIC_URL_REGEXES", "/uri");
        mockLogger();
        servletRequest = TestServletUtils.newServletRequest("/uriprivate");
        filterConfig = TestServletUtils.newFilterConfig(initParametersByName);
        authenticationFilter.init(filterConfig);
        authenticationFilter.doFilter(servletRequest, servletResponse, filterChain);
        assertThat("Access unauthorized", servletResponse.getStatus(), equalTo(HttpServletResponse.SC_UNAUTHORIZED));

    }

    @SuppressWarnings("unchecked")
    @Test
    public void Given_that_the_uri_is_private_with_an_invalid_bearer_token_then_the_request_is_unauthorized() throws IOException, ServletException {
        mockLogger();
        initParametersByName.put("PUBLIC_URL_REGEXES", "/uri");
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer invalid");
        servletRequest = TestServletUtils.newServletRequest("/uriprivate", headers);
        filterConfig = TestServletUtils.newFilterConfig(initParametersByName);
        authenticationFilter.init(filterConfig);

        try {
            new Expectations() {
                {
                    authenticationService.authenticate( (Supplier<String>) any);
                    result = new BadJWTException("Invalid token");
                }
            };
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        authenticationFilter.doFilter(servletRequest, servletResponse, filterChain);

        assertThat("Access is unauthorized", servletResponse.getStatus(), equalTo(HttpServletResponse.SC_UNAUTHORIZED));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void Given_that_the_uri_is_private_with_a_valid_bearer_token_then_the_request_is_authorized() throws IOException, ServletException {
        initParametersByName.put("PUBLIC_URL_REGEXES", "/uri");
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer valid");
        servletRequest = TestServletUtils.newServletRequest("/uriprivate", headers);
        filterConfig = TestServletUtils.newFilterConfig(initParametersByName);
        authenticationFilter.init(filterConfig);
        FilterChainWithRequestHandle filterChainWithRequestHandle = new FilterChainWithRequestHandle();

        try {
            new Expectations() {
                {
                    authenticationService.authenticate( (Supplier<String>) any);
                    result = new UserImporter() {

                        @Override
                        public String provideName() {
                            return "name";
                        }

                        @Override
                        public String provideId() {
                            return "logged-in-user";
                        }

                        @Override
                        public Collection<String> provideGroups() {
                            return Arrays.asList("GROUPS");
                        }
                    };
                }
            };
        } catch (Exception e) {
            fail(e.getMessage());
        }

        authenticationFilter.doFilter(servletRequest, servletResponse, filterChainWithRequestHandle);

        servletRequest = (HttpServletRequest) filterChainWithRequestHandle.request;
        assertThat("Access is granted", servletResponse.getStatus(), equalTo(HttpServletResponse.SC_OK));
        assertThat("The user id header is set", servletRequest.getHeader("X-id"), equalTo("logged-in-user"));
        assertThat("The name header is set", servletRequest.getHeader("X-name"), equalTo("name"));
        assertThat("The groups header is set", servletRequest.getHeader("X-groups"), equalTo("GROUPS"));
    }

    private static class FilterChainWithRequestHandle implements FilterChain {

        ServletRequest request;

        @Override
        public void doFilter(ServletRequest request, ServletResponse response) throws java.io.IOException, ServletException {
            this.request = request;
        }
    }

    private void mockLogger() {
        new MockUp<Logger>() {
            @Mock
            public boolean isLoggable(Level level) { return false;}
        };
    }

}
