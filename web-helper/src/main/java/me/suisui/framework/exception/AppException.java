package me.suisui.framework.exception;

import org.springframework.context.MessageSourceResolvable;

public class AppException extends RuntimeException implements MessageSourceResolvable {
    private static final long serialVersionUID = -2437681352739322217L;

    private final String[] codes;

    private final Object[] arguments;

    private final String defaultMessage;

    public AppException(String code) {
        this.codes = new String[] { code };
        this.arguments = null;
        this.defaultMessage = null;
    }

//    public AppException(String codes, String defaultMessage) {
//        this.codes = new String[] { codes };
//        this.arguments = null;
//        this.defaultMessage = defaultMessage;
//    }

    public AppException(String codes, Object[] arguments) {
        this.codes = new String[] { codes };
        this.arguments = arguments;
        this.defaultMessage = null;
    }

//    public AppException(String codes, Object[] arguments, String defaultMessage) {
//        this.codes = new String[] { codes };
//        this.arguments = arguments;
//        this.defaultMessage = defaultMessage;
//    }

    public AppException(String code, Throwable cause) {
        super(cause);
        this.codes = new String[] { code };
        this.arguments = null;
        this.defaultMessage = null;
    }

    public AppException(String codes, String defaultMessage, Throwable cause) {
        super(defaultMessage, cause);
        this.codes = new String[] { codes };
        this.arguments = null;
        this.defaultMessage = defaultMessage;
    }

    public AppException(String codes, Object[] arguments, Throwable cause) {
        super(cause);
        this.codes = new String[] { codes };
        this.arguments = arguments;
        this.defaultMessage = null;
    }

    public AppException(String codes, Object[] arguments, String defaultMessage, Throwable cause) {
        super(defaultMessage, cause);
        this.codes = new String[] { codes };
        this.arguments = arguments;
        this.defaultMessage = defaultMessage;
    }

    @Override
    public String[] getCodes() {
        return codes;
    }

    @Override
    public Object[] getArguments() {
        return arguments;
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }
	@Override
	public String getMessage() {
		String message = super.getMessage();
		if(message == null){
			if(getCodes() != null && getCodes().length >= 0){
				return String.format(getCodes()[0], getArguments());
			}
		}
		return message;
	}
}
