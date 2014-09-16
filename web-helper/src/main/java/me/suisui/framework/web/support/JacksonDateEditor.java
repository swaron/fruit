package me.suisui.framework.web.support;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.util.StdDateFormat;

public class JacksonDateEditor extends PropertyEditorSupport {

    protected StdDateFormat stdDateFormat;
    private final boolean allowEmpty;
    
    public JacksonDateEditor(TimeZone timeZone,boolean allowEmpty) {
        this.stdDateFormat = new StdDateFormat(timeZone);
        this.allowEmpty = allowEmpty;
    }


    /**
     * Parse the Date from the given text, can parse long and 2013-04-28T00:20:21.960+0800 and 2013-04-28T00:20:21.
     */
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (this.allowEmpty && !StringUtils.hasText(text)) {
            // Treat empty String as null value.
            setValue(null);
        } else {
            try {
                setValue(stdDateFormat.parse(text));
            } catch (ParseException ex) {
                throw new IllegalArgumentException(ex);
            }
        }
    }

    /**
     * Format the Date as String, as: 2013-04-28T00:20:21.960+0800.
     */
    @Override
    public String getAsText() {
        Date value = (Date) getValue();
        return (value != null ? stdDateFormat.format(value) : "");
    }
}
