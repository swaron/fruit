package me.suisui.web.shiro;

import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;

public class ShiroWebSessionManager extends DefaultWebSessionManager {
	
	/**
	 * don't need to distinct EIS-tier Session object and client-tier object,
	 * create a delegate session is a waste here.
	 */
	public Session start(SessionContext context) {
		Session session = createSession(context);
		applyGlobalSessionTimeout(session);
		onStart(session, context);
		notifyStart(session);
		return session;
//		// Don't expose the EIS-tier Session object to the client-tier:
//		return createExposedSession(session, context);
	}
	@Override
	public Session getSession(SessionKey key) throws SessionException {
		if (key == null) {
            throw new NullPointerException("SessionKey argument cannot be null.");
        }
		return doGetSession(key);
	}
	@Override
	protected void validate(Session session, SessionKey key) throws InvalidSessionException {
		//检查X-XSRF-TOKEN,angular/docs/api/ng/service/$http
		// TODO Auto-generated method stub
		super.validate(session, key);
	}
	@Override
	protected Session createExposedSession(Session session, SessionContext context) {
		throw new IllegalStateException("no delegate session is expected to created.");
	}
	@Override
	protected Session createExposedSession(Session session, SessionKey key) {
		throw new IllegalStateException("no delegate session is expected to created.");
	}

}
