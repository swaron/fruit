package me.suisui.framework.web.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.springframework.context.NoSuchMessageException;
import org.springframework.web.servlet.tags.MessageTag;

public class JsTag extends MessageTag {

    private static final long serialVersionUID = -3899088734036915408L;

    @Override
    protected String resolveMessage() throws JspException, NoSuchMessageException {
        //set JavaScriptEscape to false to make it more convenient to use this.
        setJavaScriptEscape(true);
        setHtmlEscape(false);
        return super.resolveMessage();
    }
    @Override
    protected void writeMessage(String msg) throws IOException {
        super.writeMessage(escapeAdditionChars(msg));
    }

    public static String escapeAdditionChars(String input) {
        if (input == null) {
            return input;
        }
        return input.replace("</script>", "<\\/script>");
    }
}
