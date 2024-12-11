package project.Common;

import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;

import java.net.URI;

public class HttpGetWithEntity extends HttpUriRequestBase {
    public static final String METHOD_NAME = "GET";

    public HttpGetWithEntity(final String uri) {
        super(METHOD_NAME, URI.create(uri));
    }

    public HttpGetWithEntity(final URI uri) {
        super(METHOD_NAME, uri);
    }
}