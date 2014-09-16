package me.suisui.web.springsecurity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;

import me.suisui.data.jdbc.po.pub.UsrSession;
import me.suisui.domain.login.LoginService;
import me.suisui.framework.http.WebSession;
import me.suisui.framework.web.support.WebRequestUtils;
import me.suisui.integration.guava.ExpiredCache;
import me.suisui.repo.jdbc.dao.pub.UserSessionDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

@Service
public class UserSessionRepositoryImpl implements UserSessionRepository {
	Logger logger = LoggerFactory.getLogger(getClass());
	private long expireTime = 30 * 60;
	@Autowired
	LoginService loginService;
	@Autowired
	UserSessionDao userSessionDao;

	ExpiredCache<Serializable, WebSession> memoryRepository;
	RemovalListener<Serializable, WebSession> listener = new RemovalListener<Serializable, WebSession>() {

		@Override
		public void onRemoval(RemovalNotification<Serializable, WebSession> notification) {
			Serializable key = notification.getKey();
			WebSession session = notification.getValue();
			if (notification.getCause() == RemovalCause.EXPIRED) {
				// time out cause session expired.
				logger.info("session for {} expired.", session.getId());
			} else {
				// logout cause session be removed.
				logger.info("session for {} stoped.", session.getId());
			}
			session.stop();
			UsrSession userSession = session.getValue();
			userSession.setLastAccessTime(new Timestamp(session.getLastAccessTime().getTime()));
			userSession.setStopTime(new Timestamp(session.getStopTimestamp().getTime()));
			userSessionDao.update(userSession);
		}
	};

	@PostConstruct
	private void init() {
		CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
		Cache<Serializable, WebSession> graphs = builder.expireAfterAccess(expireTime, TimeUnit.SECONDS)
				.initialCapacity(1000).removalListener(listener).build();
		memoryRepository = new ExpiredCache<Serializable, WebSession>(graphs);
	}

	@PreDestroy
	private void destroy() {
		memoryRepository.invalidateAll();
	}
	/**
	 */
	@Override
	public WebSession getUserSession(HttpServletRequest request, boolean create) {
		String accessToken = WebRequestUtils.getAccessToken(request);
		if (accessToken == null) {
			return null;
		}
		WebSession session = memoryRepository.getIfPresent(accessToken);
		if (create && session == null) {
			session = loadUserSession(loginService.createUserSession(accessToken, request));
		}
		if (session != null) {
			session.touch();
		}
		return session;
	}

	private WebSession loadUserSession(me.suisui.data.jdbc.po.pub.UsrSession userSession) {
		if (userSession == null) {
			return null;
		}
		WebSession session = new WebSession();
		session.setValue(userSession);
		session.setId(userSession.getAccessToken());
		session.setHost(userSession.getLoginIp());
		Date now = new Date();
		session.setStartTimestamp(now);
		session.setLastAccessTime(now);
		session.setTimeout(-1);
		if (((UsrSession)session.getValue()).getExpireTime().before(now)) {
			session.setExpired(true);
		}
		memoryRepository.put(userSession.getAccessToken(), session);
		return session;
	}

}
