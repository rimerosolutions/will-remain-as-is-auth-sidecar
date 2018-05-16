package sidecarapp.boundary;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import sidecarapp.test.TestServletUtils;

@RunWith(Parameterized.class)
public class OncePerRequestFilterTest {

    private int invocationCount;

    Map<String, String> initParametersByName;
    OncePerRequestFilterWithInvocationCounter oncePerRequestFilter;
    FilterConfig filterConfig;
    FilterChain filterChain;
    ServletRequest servletRequest;
    ServletResponse servletResponse;

    @Parameters
    public static List<Object[]> data() {
        return Arrays.asList(new Object[][] {
            {
                1
            },
            {
                ThreadLocalRandom.current().nextInt(3, 10)
            }
        });
    }

    public OncePerRequestFilterTest(int invocationCount) {
        this.invocationCount = invocationCount;
    }

    @Before
    public void setup() {
        initParametersByName = new HashMap<>();
        filterConfig = TestServletUtils.newFilterConfig(initParametersByName);
        filterChain = TestServletUtils.newFilterChain();
        servletRequest = TestServletUtils.newServletRequest("/uri");
        servletResponse = TestServletUtils.newServletResponse();
        oncePerRequestFilter = new OncePerRequestFilterWithInvocationCounter();
        oncePerRequestFilter.init(filterConfig);

        assertThat(oncePerRequestFilter.counter, equalTo(0));
    }

    @Test
    public void Given_that_the_filter_is_called_possibly_multiple_times_in_same_request_then_it_applies_business_logic_once() throws Exception {

        for (int i = 0; i < invocationCount; i++) {
            oncePerRequestFilter.doFilter(servletRequest, servletResponse, filterChain);
        }

        assertThat("Request got filtered once", oncePerRequestFilter.counter, equalTo(1));
    }

    static class OncePerRequestFilterWithInvocationCounter extends OncePerRequestFilter {

        int counter = 0;

        @Override
        protected void applyDoFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
            counter++;
            super.applyDoFilter(request, response, chain);
        }
    }

}
