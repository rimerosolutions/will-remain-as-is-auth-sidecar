package sidecarapp.boundary;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class PublicURIsTest {

    private final Collection<String> publicUrlRegexes;
    private final String requestURI;
    private final boolean expected;

    public PublicURIsTest(Collection<String> publicUrlRegexes, String requestURI, boolean expected) {
        this.publicUrlRegexes = publicUrlRegexes;
        this.requestURI = requestURI;
        this.expected = expected;
    }

    @Parameters
    public static List<Object[]> data() {
        return Arrays.asList(new Object[][] {
            {
                Arrays.asList("/abc", "/bcd"), "/abc", Boolean.FALSE
            },
            {
                Arrays.asList("/abc", "/bcd"), "/efg", Boolean.TRUE
            },
            {
                Collections.emptyList(), "/abc", Boolean.TRUE
            }
        });
    }

    @Test
    public void Given_the_user_navigates_to_a_resource_then_the_uri_is_checked_for_public_access() {
        PublicURIs publicURIs = PublicURIs.from(publicUrlRegexes);

        assertThat("The URI", publicURIs.isProtected(requestURI), equalTo(expected));
    }

}
