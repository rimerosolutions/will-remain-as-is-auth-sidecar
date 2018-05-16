package sidecarapp.control;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import sidecarapp.test.TestServletUtils;

public class JWTHTTPHeadersTokenSourceTest {

    @Test(expected=IllegalArgumentException.class)
    public void Given_the_access_token_is_missing_then_the_source_is_not_constructed() {
        new JWTHTTPHeadersTokenSource(TestServletUtils.newServletRequest("/hello"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void Given_the_access_token_header_value_is_ill_formed_then_the_source_is_not_constructed() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearerx valid");
        new JWTHTTPHeadersTokenSource(TestServletUtils.newServletRequest("/hello", headers));
    }

    @Test
    public void Given_the_access_token_is_well_formed_then_the_source_is_constructed() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer valid");
        new JWTHTTPHeadersTokenSource(TestServletUtils.newServletRequest("/hello", headers));
    }

}
