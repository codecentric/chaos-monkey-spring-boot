package com.example.chaos.monkey.chaosdemo.bean;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.lang3.StringEscapeUtils;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class CrossSiteScriptingRequestWrapper extends HttpServletRequestWrapper {
    public CrossSiteScriptingRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getRequestURI() {
        return sanitizedInput(super.getRequestURI());
    }

    @Override
    public String[] getParameterValues(String name) {
        return super.getParameterValues(name);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final String requestBody = new String(super.getInputStream().readAllBytes());
        final String sanitizedBody = sanitizedInput(requestBody);

        return new ServletInputStream() {
            private final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(sanitizedBody.getBytes());
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

            }

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
    }

    private String sanitizedInput(String requestData){
        final PolicyFactory policyFactory = Sanitizers.FORMATTING;
        return StringEscapeUtils.unescapeHtml3(policyFactory.sanitize(requestData));
    }
}
