package me.suisui.integration.spring.jackson;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomObjectMapperInjector {
    @Autowired(required=false)
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        if(requestMappingHandlerAdapter != null){
            List<HttpMessageConverter<?>> messageConverters = requestMappingHandlerAdapter.getMessageConverters();
            for (HttpMessageConverter<?> messageConverter : messageConverters) {
                if (messageConverter instanceof MappingJackson2HttpMessageConverter) {
                	MappingJackson2HttpMessageConverter m = (MappingJackson2HttpMessageConverter) messageConverter;
                	//{}&& 和angular不兼容
//                	m.setPrefixJson(true);
                    m.setObjectMapper(objectMapper);
                }
            }
        }
    }

}
