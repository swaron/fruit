package me.suisui.framework.web.result;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ActionResult implements Serializable{

	private static final long serialVersionUID = 1L;
	protected boolean success = true;
	protected String message;
	protected Object result;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public static ActionResult successResult() {
		ActionResult actionResult = new ActionResult();
		actionResult.setSuccess(true);
		return actionResult;
	}
	
	public static ActionResult successResult(Object result) {
		ActionResult actionResult = new ActionResult();
		actionResult.setSuccess(true);
		actionResult.setResult(result);
		return actionResult;
	}

	public static ActionResult errorResult(String message) {
		ActionResult actionResult = new ActionResult();
		actionResult.setSuccess(false);
		actionResult.setMessage(message);
		return actionResult;
	}

	public static ActionResult errorResult(String message, Object result) {
		ActionResult actionResult = new ActionResult();
		actionResult.setSuccess(false);
		actionResult.setMessage(message);
		actionResult.setResult(result);
		return actionResult;
	}

}
