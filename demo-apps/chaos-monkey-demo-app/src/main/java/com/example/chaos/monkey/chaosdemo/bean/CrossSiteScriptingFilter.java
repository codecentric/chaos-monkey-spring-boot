package com.example.chaos.monkey.chaosdemo.bean;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

@WebFilter("/*")
public class CrossSiteScriptingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            filterChain.doFilter(new CrossSiteScriptingRequestWrapper((HttpServletRequest) servletRequest),servletResponse);
    }
}
