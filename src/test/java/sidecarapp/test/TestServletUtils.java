package sidecarapp.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class TestServletUtils {

    public static FilterConfig newFilterConfig(Map<String, String> initParametersByName) {
        return (FilterConfig) Proxy.newProxyInstance(FilterConfig.class.getClassLoader(),
                                                     new Class<?>[] { FilterConfig.class },
                                                     new FilterConfigProxy(initParametersByName));
    }

    public static FilterChain newFilterChain() {
        return (FilterChain) Proxy.newProxyInstance(FilterChain.class.getClassLoader(),
                                                    new Class<?>[] { FilterChain.class },
                                                    new FilterChainProxy());
    }

    public static HttpServletRequest newServletRequest(String uri) {
        return (HttpServletRequest) Proxy.newProxyInstance(HttpServletRequest.class.getClassLoader(),
                                                           new Class<?>[] { HttpServletRequest.class },
                                                           new HttpServletRequestProxy(uri));
    }

    public static HttpServletRequest newServletRequest(String uri, Map<String, String> headers) {
        return (HttpServletRequest) Proxy.newProxyInstance(HttpServletRequest.class.getClassLoader(),
                                                           new Class<?>[] { HttpServletRequest.class },
                                                           new HttpServletRequestProxy(uri, headers));
    }

    public static HttpServletResponse newServletResponse() {
        return (HttpServletResponse) Proxy.newProxyInstance(HttpServletResponse.class.getClassLoader(),
                                                            new Class<?>[] { HttpServletResponse.class },
                                                            new HttpServletResponseProxy());
    }

    public static final class FilterChainProxy implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return null;
        }

    }

    private static final class FilterConfigProxy implements InvocationHandler {

        private final Map<String, String> initParametersByName;

        public FilterConfigProxy(Map<String, String> initParametersByName) {
            this.initParametersByName = initParametersByName;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("getInitParameter")) {
                Method m = this.getClass().getMethod("getInitParameter", String.class);
                return m.invoke(this, args);
            }

            return null;
        }

        @SuppressWarnings("unused")
        public String getInitParameter(String name) {
            return initParametersByName.get(name);
        }

    }

    private static final class HttpServletRequestProxy implements InvocationHandler {

        private Map<String, Object> requestAttributesMap = new HashMap<>();
        private Map<String, String> requestHeadersMap = new HashMap<>();
        private final String uri;

        public HttpServletRequestProxy(String uri) {
            this(uri, new HashMap<>());
        }

        public HttpServletRequestProxy(String uri, Map<String, String> requestHeadersMap) {
            this.uri = uri;
            this.requestHeadersMap = requestHeadersMap;
        }

        @SuppressWarnings("unused")
        public String getRequestURI() {
            return uri;
        }

        @SuppressWarnings("unused")
        public void setAttribute(String name, Object value) {
            requestAttributesMap.put(name, value);
        }

        @SuppressWarnings("unused")
        public Object getAttribute(String name) {
            return requestAttributesMap.get(name);
        }

        @SuppressWarnings("unused")
        public Enumeration<String> getAttributeNames() {
            return Collections.enumeration(requestAttributesMap.keySet());
        }

        @SuppressWarnings("unused")
        public Enumeration<String> getHeaderNames() {
            return Collections.enumeration(requestHeadersMap.keySet());
        }

        @SuppressWarnings("unused")
        public String getHeader(String name) {
            return requestHeadersMap.get(name);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("setAttribute")) {
                Method m = this.getClass().getMethod("setAttribute", String.class, Object.class);
                return m.invoke(this, args);
            } else if (method.getName().equals("getAttribute")) {
                Method m = this.getClass().getMethod("getAttribute", String.class);
                return m.invoke(this, args);
            } else if (method.getName().equals("getRequestURI")) {
                Method m = this.getClass().getMethod("getRequestURI");
                return m.invoke(this, args);
            } else if (method.getName().equals("getAttributeNames")) {
                Method m = this.getClass().getMethod("getAttributeNames");
                return m.invoke(this, args);
            } else if (method.getName().equals("getHeader")) {
                Method m = this.getClass().getMethod("getHeader", String.class);
                return m.invoke(this, args);
            } else if (method.getName().equals("getHeaderNames")) {
                Method m = this.getClass().getMethod("getHeaderNames");
                return m.invoke(this, args);
            }

            return null;
        }

    }

    private static final class HttpServletResponseProxy implements InvocationHandler {

        private int status = HttpServletResponse.SC_OK;

        @SuppressWarnings("unused")
        public void setStatus(int status) {
            this.status = status;
        }

        @SuppressWarnings("unused")
        public int getStatus() {
            return status;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("setStatus")) {
                Method m = this.getClass().getMethod("setStatus", int.class);
                return m.invoke(this, args);
            } else if (method.getName().equals("getStatus")) {
                Method m = this.getClass().getMethod("getStatus");
                return m.invoke(this, args);
            }

            return null;
        }

    }

}
