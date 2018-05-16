package sidecarapp;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.mitre.dsmiley.httpproxy.ProxyServlet;

import sidecarapp.boundary.AuthenticationFilter;

public final class App {

    private static final String DEFAULT_PUBLIC_URL_REGEXES = "/change_me_index.html";
    private static final String DEFAULT_MICROSERVICE_URI = "http://change_me_host_microservice";
    private static final String DEFAULT_JWKS_URL = "https://change_me_jwkuri/keys.json";
    private static final int DEFAULT_SERVER_PORT = 8080;

    public static void main(final String[] args) throws Exception {
        final int serverPort = Integer.getInteger("APP_SERVER_PORT", DEFAULT_SERVER_PORT);
        final String proxyTargetUri = System.getProperty("MICROSERVICE_URI", DEFAULT_MICROSERVICE_URI);
        final String publicUrlRegexes = System.getProperty("PUBLIC_URL_REGEXES", DEFAULT_PUBLIC_URL_REGEXES);
        final String jwksUrl = System.getProperty("JWKS_URL", DEFAULT_JWKS_URL);

        new App().run(serverPort, proxyTargetUri, publicUrlRegexes, jwksUrl);
    }

    private void run(final int serverPort,
                     final String proxyTargetUri,
                     final String publicUrlRegexes,
                     final String jwksUrl) throws Exception, InterruptedException {
        final Server server = new Server(serverPort);

        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        servletContextHandler.setContextPath("/");

        servletContextHandler.addFilter(authFilter(publicUrlRegexes, jwksUrl),
                                        "/*",
                                        EnumSet.of(DispatcherType.INCLUDE, DispatcherType.REQUEST));
        servletContextHandler.addServlet(proxyServlet(proxyTargetUri), "/*");

        server.setHandler(servletContextHandler);
        server.start();
        server.join();
    }

    private ServletHolder proxyServlet(final String proxyTargetUri) {
        final ServletHolder servletHolder = new ServletHolder(ProxyServlet.class);

        servletHolder.setInitParameter("targetUri", proxyTargetUri);
        servletHolder.setInitParameter("log", "true");

        return servletHolder;
    }

    private FilterHolder authFilter(final String publicUrlRegexes, final String jwksUrl) {
        final FilterHolder filterHolder = new FilterHolder(AuthenticationFilter.class);

        filterHolder.setInitParameter("PUBLIC_URL_REGEXES", publicUrlRegexes);
        filterHolder.setInitParameter("JWKS_URL", jwksUrl);

        return filterHolder;
    }

}

