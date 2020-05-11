package org.asynchttpclient;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static org.asynchttpclient.Dsl.asyncHttpClient;
import static org.testng.Assert.assertEquals;

public class RequestCharsetTest extends AbstractBasicTest {

    @Override
    public AbstractHandler configureHandler() throws Exception {
        return new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
                response.setContentType(request.getContentType());
                response.getOutputStream().close();
            }
        };
    }

    @Test
    public void defaultTextCharsetIsUtf8() throws Exception {
        String contentType = "text/plain";

        String sentType = executeWithContentType(contentType);

        assertEquals(sentType, "text/plain;charset=utf-8");
    }

    @Test
    public void canSetDifferentTextCharsetInHeaderValue() throws Exception {
        String contentType = "text/plain;charset=iso-8859-1";

        String sentType = executeWithContentType(contentType);

        assertEquals(sentType, contentType);
    }

    @Test
    public void canSetDifferentTextCharsetOnRequestBuilder() throws Exception {
        String contentType = "text/plain";

        String sentType;
        try (AsyncHttpClient client = asyncHttpClient()) {
            sentType = client.prepareGet(getTargetUrl())
                    .addHeader(CONTENT_TYPE, contentType)
                    .setCharset(StandardCharsets.ISO_8859_1)
                    .execute()
                    .get(2, TimeUnit.SECONDS)
                    .getContentType();
        }

        assertEquals(sentType, "text/plain;charset=iso-8859-1");
    }

    @Test
    public void canOmitDefaultTextCharset() throws Exception {
        String contentType = "text/plain";

        String sentType;
        try (AsyncHttpClient client = asyncHttpClient()) {
            sentType = client.prepareGet(getTargetUrl())
                    .addHeader(CONTENT_TYPE, contentType)
                    .allowNoCharset()
                    .execute()
                    .get(2, TimeUnit.SECONDS)
                    .getContentType();
        }

        assertEquals(sentType, "text/plain");
    }

    private String executeWithContentType(String contentType) throws Exception {
        try (AsyncHttpClient client = asyncHttpClient()) {
            return client.prepareGet(getTargetUrl())
                    .addHeader(CONTENT_TYPE, contentType)
                    .execute()
                    .get(2, TimeUnit.SECONDS)
                    .getContentType();
        }
    }
}
