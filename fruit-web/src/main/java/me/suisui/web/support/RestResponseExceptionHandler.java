package me.suisui.web.support;

import java.util.Locale;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;

import me.suisui.framework.exception.AppException;
import me.suisui.framework.web.result.ActionResult;
import me.suisui.framework.web.support.WebRequestUtils;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.StaleStateException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.hibernate.exception.GenericJDBCException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.support.RequestContextUtils;

@ControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {
	@Autowired
	private MessageSource messageSource;

	@ExceptionHandler(value = { RuntimeException.class })
	protected ResponseEntity<Object> handleRuntimeException(RuntimeException ex, ServletWebRequest request) {
		Locale locale = RequestContextUtils.getLocale(request.getRequest());
		String exceptionMessage = ex.toString();
		String message = "系统错误，若你操作无误，请联系管理员，联系管理员前请记录错误出现的时间，截图和操作步骤。这些信息有助于系统管理员找到问题的原因。";
		ActionResult result = ActionResult.errorResult(message, exceptionMessage);
		return handleExceptionInternal(ex, result, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	@ExceptionHandler(value = { AppException.class })
	protected ResponseEntity<Object> handleConflict(AppException ex, ServletWebRequest request) {
		Locale locale = RequestContextUtils.getLocale(request.getRequest());
		String message = messageSource.getMessage(ex, locale);
		ActionResult result = ActionResult.errorResult(message);
		return handleExceptionInternal(ex, result, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	@ExceptionHandler(value = { PersistenceException.class, DataAccessException.class })
	protected ResponseEntity<Object> handleConflict(RuntimeException ex, ServletWebRequest request) {
		Locale locale = RequestContextUtils.getLocale(request.getRequest());
		String exceptionMessage = ExceptionUtils.getStackTrace(ex);
		String message = "数据库错误，你可以重试或者联系管理员，联系管理员前请记录错误出现的时间，截图和操作步骤。这些信息有助于系统管理员找到问题的原因。";
		if (ExceptionUtils.indexOfType(ex, ConstraintViolationException.class) != -1
				&& exceptionMessage.contains("Duplicate entry")) {
			message = "数据唯一性校验出错，系统已经存在相同类型的值，请您检查输入的值，然后重试。";
		}
		if (ExceptionUtils.indexOfType(ex, ConstraintViolationException.class) != -1
				&& exceptionMessage.contains("a foreign key constraint fails")) {
			message = "数据完整性校验出错，你可能在删除一个被依赖的数据，或者在手动增加一个不存在的类型。";
		}
		if (ExceptionUtils.indexOfType(ex, DataException.class) != -1
				&& exceptionMessage.contains("Data truncation: Data too long")) {
			message = "你输入的数据长度超出了系统允许的最大长度，请您检查输入的值，然后重试。";
		}
		if (ExceptionUtils.indexOfType(ex, StaleStateException.class) != -1
				&& exceptionMessage.contains("Row was updated or deleted by another transaction")) {
			message = "数据过时，你正在使用或者编辑的数据已经被别人修改了，操作无法继续，你可以尝试重试来获取最新有效数据。";
		}
		if (ExceptionUtils.indexOfType(ex, GenericJDBCException.class) != -1
				&& exceptionMessage.contains("Incorrect string value")) {
			message = "你输入的数据包含非法字符，请您检查输入的值，然后重试。";
		}

		ActionResult result = ActionResult.errorResult(message, exceptionMessage);
		return handleExceptionInternal(ex, result, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	//
	// @ExceptionHandler(value = { AccessDeniedException.class })
	// protected ResponseEntity<Object> handleAccessDenied(AccessDeniedException
	// ex, ServletWebRequest request) {
	// String exceptionMessage = ExceptionUtils.getStackTrace(ex);
	// Locale locale = RequestContextUtils.getLocale(request.getRequest());
	// String message = "授权失败，你没有权限访问对应的数据，如果您应该有这部分权限，请联系管理员帮忙开通这部分的权限。";
	// ActionResult result =
	// ActionResult.errorResult(message,ExceptionUtils.getStackTrace(ex));
	// return handleExceptionInternal(ex, result, new HttpHeaders(),
	// HttpStatus.INTERNAL_SERVER_ERROR, request);
	// }

	@Override
	protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status,
			WebRequest request) {
		ActionResult result = ActionResult.errorResult("输入参数不正确，请检查提交的参数。");
		return handleExceptionInternal(ex, result, headers, status, request);
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		if (request instanceof ServletWebRequest) {
			logException(ex, (ServletWebRequest) request);
		}
		return super.handleExceptionInternal(ex, body, headers, status, request);
	}

	protected void logException(Exception ex, ServletWebRequest webRequest) {
		String username = "anonymous";
		if (webRequest.getUserPrincipal() != null) {
			username = webRequest.getUserPrincipal().getName();
			// username = principal.getUsername();
		}
		HttpServletRequest request = webRequest.getRequest();
		StringBuilder message = new StringBuilder("exception on handle httprequest.\n");
		message.append("username:").append(username).append("\n");
		message.append("address:").append(request.getRemoteAddr()).append("\n");
		message.append("request uri:").append(request.getRequestURI()).append("\n");
		message.append("request parameters:").append(WebRequestUtils.getParametersMap(request)).append("\n");
		logger.warn(message, ex);
	}
}
