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

public class AttachmentHttpRequestHandler extends ResourceHttpRequestHandler {

    @Override
    protected void setHeaders(HttpServletResponse response, Resource resource, MediaType mediaType) throws IOException {
        super.setHeaders(response, resource, mediaType);
        // 父类的SetHeader没有提供request参数， 虽然一般情况只要response就可以了，但是有的时候我需要request的值呀。
        HttpServletRequest request = getRequest();
        setHeaders(request, response, resource, mediaType);
    }

    @Override
    protected Resource getResource(HttpServletRequest request) {
        Resource resource = super.getResource(request);
        if (resource != null) {
            String referer = request.getHeader("referer");

        }
        return resource;
    }

    /**
     * 设置下载文件的文件名
     */
    protected void setHeaders(HttpServletRequest request, HttpServletResponse response, Resource resource,
            MediaType mediaType) throws UnsupportedEncodingException {
        // 用ie的人不习惯升级
        String filename = resource.getFilename();
        request.getParameter("filename");
        request.getParameter("attachment");
		WebRequestUtils.setContentDispositionHeader(request, response, filename);
    }

    private HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
            return servletRequestAttributes.getRequest();
        }
        return null;
    }


}
