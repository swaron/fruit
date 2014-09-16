package me.suisui.web.support;

import java.util.Date;
import java.util.TimeZone;

import me.suisui.framework.web.support.JacksonDateEditor;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AppInitBinder {
    /**
     * controller form 的日期类型转换
     * @param binder
     * @param request
     */
    @InitBinder
    public void registerCustomEditors(WebDataBinder binder, WebRequest request) {
        binder.registerCustomEditor(Date.class, new JacksonDateEditor(TimeZone.getDefault(), true));
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }
}
