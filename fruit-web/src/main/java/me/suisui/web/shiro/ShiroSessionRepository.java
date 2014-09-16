package me.suisui.web.shiro;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import me.suisui.data.domain.user.ShiroPrincipal;
import me.suisui.data.jdbc.po.pub.UsrSession;
import me.suisui.domain.login.LoginService;
import me.suisui.integration.shiro.cache.SpringCacheManager.DelegateCache;
import me.suisui.repo.jdbc.dao.pub.UserSessionDao;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

@Service
public class ShiroSessionRepository extends CachingSessionDAO {
	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired(required = false)
	UserSessionDao userSessionDao;

	@PreDestroy
	private void destroy() {
		Cache<Serializable, Session> cache = getActiveSessionsCache();
		cache.clear();
	}

	RemovalListener<Serializable, Session> listener = new RemovalListener<Serializable, Session>() {

		@Override
		public void onRemoval(RemovalNotification<Serializable, Session> notification) {
			Serializable key = notification.getKey();
			Session session = notification.getValue();
			if (notification.getCause() == RemovalCause.EXPIRED) {
				// time out cause session expired.
				logger.info("session for {} expired.", session.getId());
			} else {
				// logout cause session be removed.
				logger.info("session for {} stoped.", session.getId());
			}
			Object attribute = session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
			if (attribute instanceof PrincipalCollection) {
				PrincipalCollection collection = (PrincipalCollection) attribute;
				for (Object object : collection) {
					if (object instanceof ShiroPrincipal) {
						ShiroPrincipal shiroPrincipal = (ShiroPrincipal) object;
						UsrSession userSession = shiroPrincipal.getSession();
						userSession.setLastAccessTime(new Timestamp(session.getLastAccessTime().getTime()));
						userSession.setStopTime(new Timestamp(System.currentTimeMillis()));
						UsrSession merge = userSessionDao.update(userSession);
						shiroPrincipal.setSession(merge);
					}
				}
			}
		}
	};

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PostConstruct
	private void init() {
		String cacheName = getActiveSessionsCacheName();
		CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
		com.google.common.cache.Cache graphs = builder.expireAfterAccess(30, TimeUnit.MINUTES).initialCapacity(10000)
				.removalListener(listener).build();
		org.springframework.cache.Cache guavaCache = new GuavaCache(cacheName, graphs);
		Cache cache = new DelegateCache(guavaCache);
		setActiveSessionsCache(cache);
	}

	/**
	 * touch,setTime etc cause session updated. we don't want a re-cache in such
	 * situation
	 */
	public void update(Session session) throws UnknownSessionException {
		logger.info("session {} was updated.", session.getId());
		doUpdate(session);
		if (session instanceof ValidatingSession) {
			if (((ValidatingSession) session).isValid()) {
				// do nothing if valid. a not valid session will never became
				// valid.
				// cache(session, session.getId());
			} else {
				uncache(session);
			}
		} else {
			// do nothing if update a normal session
			// cache(session, session.getId());
		}
	}

	protected Serializable doCreate(Session session) {
		Serializable sessionId = generateSessionId(session);
		assignSessionId(session, sessionId);
		return sessionId;
	}

	protected Session doReadSession(Serializable sessionId) {
		// 1. read from cache
		return null; // should never execute because this implementation relies
						// on parent class to access cache, which
		// is where all sessions reside - it is the cache implementation that
		// determines if the
		// cache is memory only or disk-persistent, etc.
	}

	protected void doUpdate(Session session) {
		// does nothing - parent class persists to cache.
	}

	protected void doDelete(Session session) {
		logger.info("remove expired session {} from cache.", session.getId());
		// does nothing - parent class remove from cache.
	}

}
