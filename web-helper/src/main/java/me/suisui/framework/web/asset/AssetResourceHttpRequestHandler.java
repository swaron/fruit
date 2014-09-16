package me.suisui.framework.web.asset;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.suisui.framework.web.support.WebRequestUtils;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

public class AssetResourceHttpRequestHandler extends ResourceHttpRequestHandler {

    @Override
    protected void setHeaders(HttpServletResponse response, Resource resource, MediaType mediaType) throws IOException {
        super.setHeaders(response, resource, mediaType);
    }

    @Override
    protected Resource getResource(HttpServletRequest request) {
        Resource resource = super.getResource(request);
        if (resource != null) {
            String referer = request.getHeader("referer");

        }
        return resource;
    }

}
