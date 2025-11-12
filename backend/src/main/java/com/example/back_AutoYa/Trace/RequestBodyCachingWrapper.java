package com.example.back_AutoYa.Trace;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.*;

public class RequestBodyCachingWrapper extends HttpServletRequestWrapper {

    private final byte[] cachedBody;

    public RequestBodyCachingWrapper(HttpServletRequest request) throws IOException {
        super(request);
        try (InputStream is = request.getInputStream()) {
            this.cachedBody = is.readAllBytes(); // Guarda el contenido del body
        }
    }

    @Override
    public ServletInputStream getInputStream() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cachedBody);
        return new ServletInputStream() {
            @Override
            public int read() {
                return byteArrayInputStream.read();
            }

            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // No necesario para este caso
            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    public byte[] getCached() {
        return cachedBody;
    }
}
